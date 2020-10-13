package com.infosys.serviceImpl;

import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.searchv6.util.SearchConstantsv6;
import com.infosys.service.CompetencySearchService;
import com.infosys.util.LexProjectUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CompetencySearchServiceImp implements CompetencySearchService {

    private RestHighLevelClient elasticClient = ConnectionManager.getClient();

    private final String COMPETENCIES_CONSTANT = "competencies";

    private final String COMPETENCIES_ID_CONSTANT = "competencies.id";

    private final String IDENTIFIER_CONSTANT = "identifier";

    /**
     * Get the list of identifier which contains the competency id's
     *
     * @param rootOrg
     * @param competencyId
     * @return List of identifiers
     * @throws IOException
     */
    @Override
    public List<Object> getLexIdsContainingCompId(String rootOrg, String competencyId) throws IOException {
        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery(COMPETENCIES_CONSTANT, QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(COMPETENCIES_ID_CONSTANT, competencyId)), ScoreMode.None);
        QueryBuilder mainBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(SearchConstantsv6.ROOT_ORG, rootOrg)).filter(nestedQueryBuilder);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String[] includeFields = new String[]{IDENTIFIER_CONSTANT};
        SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
        searchRequest.indices(SearchConstantsv6.SEARCH_INDEX_NAME_PREFIX + SearchConstantsv6.SEARCH_INDEX_LOCALE_DELIMITER + "en");
        searchRequest.types(LexProjectUtil.EsType.new_lex_search.getTypeName());
        sourceBuilder.query(mainBuilder);
        sourceBuilder.fetchSource(includeFields, new String[]{});
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = elasticClient.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse.getHits().totalHits == 0) {
            return Collections.emptyList();
        }
        List<Object> totalRecords = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            totalRecords.add(hit.getSourceAsMap());
        }
        return totalRecords;
    }
}