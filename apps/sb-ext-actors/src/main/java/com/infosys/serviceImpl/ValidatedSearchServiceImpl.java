package com.infosys.serviceImpl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.model.CourseRecommendation;
import com.infosys.searchv6.validations.model.ValidatedSearchData;
import com.infosys.service.ValidatedSearchService;
import com.infosys.util.LexJsonKey;
import com.infosys.util.LexProjectUtil;
import com.infosys.util.Util;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.CassandraUtil;
import org.sunbird.common.Constants;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.common.responsecode.ResponseCode;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionMngrFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

@Service
public class ValidatedSearchServiceImpl implements ValidatedSearchService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${infosys.lex.core.ip}")
    String lexCoreIp;

    @Value("${infosys.lex.core.interests.endpoint}")
    private String lexCoreInterestsEndPoint;

    @Value("${infosys.lex.core.continue.learning.endpoint}")
    private String lexCoreContinueLearningEndPoint;

    private static String bodhiKeyspace = LexJsonKey.BODHI_DB_KEYSPACE;
    private static Util.DbInfo userPlaylistDb = Util.dbInfoMap.get(LexJsonKey.USER_PLAYLIST_DB);
    private static Util.DbInfo recentPlaylistDb = Util.dbInfoMap.get(LexJsonKey.RECENT_PLAYLIST_DB);
    private static Util.DbInfo sharedPlaylistDb = Util.dbInfoMap.get(LexJsonKey.SHARED_PLAYLIST_DB);

    private CassandraConnectionManager connectionManager;
    private static PropertiesCache properties = PropertiesCache.getInstance();


    @PostConstruct
    public void init() {
        Util.checkCassandraDbConnections(bodhiKeyspace);
        String cassandraMode = properties.getProperty(JsonKey.SUNBIRD_CASSANDRA_MODE);
        connectionManager = CassandraConnectionMngrFactory.getObject(cassandraMode);
    }


    @Override
    public void buildSearchRequestQuery(ValidatedSearchData validatedSearchData) throws Exception {


        if (validatedSearchData.getSearchType() != null && !validatedSearchData.getSearchType().isEmpty()) {
            List<String> searchKeyWords = getSearchKeywords(validatedSearchData.getSearchType(), validatedSearchData.getRootOrg(), validatedSearchData.getLocale(), validatedSearchData.getUuid().toString(), validatedSearchData.getPageNo(), validatedSearchData.getPageSize());
            String identifiers = searchKeyWords.stream().map(Object::toString).collect(Collectors.joining(","));

            if (identifiers == null || identifiers.isEmpty()) {
                validatedSearchData.setQuery("none");
            } else {
                validatedSearchData.setQuery(identifiers);
            }

        }

    }


    /**
     * This method is to produce the search keywords for a given search type.
     *
     * @param searchTypeStr values are interest, playlist, history, trending
     * @param rootOrg
     * @param locale
     * @param uuid
     * @param pageNumber
     * @param pageSize
     * @return
     */
    private List<String> getSearchKeywords(String searchTypeStr, String rootOrg, List<String> locale, String uuid, int pageNumber, int pageSize) {

        List<String> searchKeyWords = new ArrayList<>();

        LexProjectUtil.SearchType searchType = LexProjectUtil.SearchType.valueOf(searchTypeStr.toUpperCase());

        switch (searchType) {
            case INTEREST: {
                HttpHeaders headers = new HttpHeaders();
                headers.set("rootOrg", rootOrg);
                if (null != locale && locale.size() != 0)
                    locale.forEach(item -> headers.add("langCode", item));
                HttpEntity entity = new HttpEntity<>(headers);
                String x = lexCoreIp + lexCoreInterestsEndPoint.replace("{uuid}", uuid.toString());

                ResponseEntity<Map> interests = null;
                try {
                    interests = restTemplate.exchange(x, HttpMethod.GET, entity, Map.class);

                } catch (HttpStatusCodeException e) {
                    System.out.println(e.getStatusCode());
                    System.out.println(e.getResponseBodyAsString());

                }
                StringBuilder query = new StringBuilder();
                Map<String, Object> responseData = (Map<String, Object>) interests.getBody();
                searchKeyWords = (List<String>) responseData.getOrDefault("user_interest", new ArrayList<>());
                System.out.println("Interest ids :: " + searchKeyWords);


            }
            break;

            case PLAYLIST: {
                Response response = new Response();
                try {
                    Select select = QueryBuilder.select().all().from(userPlaylistDb.getKeySpace(), userPlaylistDb.getTableName());

                    select.where(eq("root_org", rootOrg)).and(eq("user_id", uuid));
                    ProjectLogger.log("Query: " + select, LoggerEnum.DEBUG);
                    ResultSet results = connectionManager.getSession(userPlaylistDb.getKeySpace()).execute(select);
                    response = CassandraUtil.createResponse(results);
                } catch (Exception e) {
                    ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + userPlaylistDb.getTableName() + " : " + e.getMessage(), e);
                    throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
                            ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
                }
                List<Map<String, Object>> userPlaylistData = (List<Map<String, Object>>) response.getResult()
                        .get("response");
                for (Iterator<Map<String, Object>> iterator = userPlaylistData.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> map = iterator.next();
                    searchKeyWords.addAll((List<String>) map.get("resource_ids"));
                }
                System.out.println("Playlist ids :: " + searchKeyWords);

            }
            break;

            case TRENDING: {
                try {
                    RestHighLevelClient client = ConnectionManager.getClient();
                    BoolQueryBuilder trendingBoolQuery = QueryBuilders.boolQuery();
                    trendingBoolQuery.filter(QueryBuilders.termsQuery("locale", locale));
                    trendingBoolQuery.filter(QueryBuilders.termQuery("root_org.keyword", rootOrg));
                    SearchResponse response = client
                            .search(new SearchRequest().indices(Util.TRENDING_SCORE_INDEX).types(Util.TRENDING_SCORE_INDEX_TYPE)
                                            .source(new SearchSourceBuilder().query(trendingBoolQuery)
                                                    .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC)).size(pageSize)
                                                    .from(pageNumber * pageSize)),
                                    RequestOptions.DEFAULT);


                    ArrayList<CourseRecommendation> trendingCourses = new ArrayList<>();
                    for (SearchHit hit : response.getHits()) {
                        Map<String, Object> map = hit.getSourceAsMap();
                        searchKeyWords.add((String) map.get("course_id"));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println("Popular ids :: " + searchKeyWords);


            }
            break;

            case HISTORY: {
                HttpHeaders headers = new HttpHeaders();
                headers.set("rootOrg", rootOrg);
                if (null != locale && locale.size() != 0)
                    locale.forEach(item -> headers.add("langCode", item));
                HttpEntity entity = new HttpEntity<>(headers);
                String x = lexCoreIp + lexCoreContinueLearningEndPoint.replace("{uuid}", uuid.toString());

                ResponseEntity<Map> continueLearning = null;
                try {
                    continueLearning = restTemplate.exchange(x, HttpMethod.GET, entity, Map.class);

                } catch (HttpStatusCodeException e) {
                    System.out.println(e.getStatusCode());
                    System.out.println(e.getResponseBodyAsString());

                }
                Map<String, Object> responseData = (Map<String, Object>) continueLearning.getBody();
                searchKeyWords = (List<String>) responseData.getOrDefault("resource_ids", new ArrayList<>());
                System.out.println("continueLearning ids :: " + searchKeyWords);


            }
            break;

        }


        return searchKeyWords;

    }
}
