/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import java.io.IOException;
import java.util.*;

import javax.annotation.PostConstruct;

import com.googlecode.concurrenttrees.radix.RadixTree;
import com.infosys.searchv6.validations.model.*;
import com.infosys.searchv6.GeneralMultiLingualIntegratedSearchServicev6;
import com.infosys.service.TopicService;
import com.infosys.util.SearchConstants;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.models.response.Response;

import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.model.CourseRecommendation;
import com.infosys.model.cassandra.UserPersonalizationRolesModel;
import com.infosys.repository.UserPersonalizationRolesRepository;
import com.infosys.service.AdminAccessControlService;
import com.infosys.service.RecommendationsService;
import com.infosys.service.TrendingService;
import com.infosys.util.LexConstants;
import com.infosys.util.LexProjectUtil;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {

	@Autowired
	private UserPersonalizationRolesRepository userPersonalizationRolesRepository;

	@Autowired
	private TrendingService trendingService;

	@Autowired
	private AdminAccessControlService accessControlService;

	private boolean accessControlEnabled;

	@Autowired
	private Environment environment;

	@Autowired
	TopicService topicService;

	@Autowired
	RestTemplate restTemplate;

	@Value("${infosys.core.ip}")
	String CoreIp;

	@Value("${infosys.core.interests.endpoint}")

	private String CoreInterestsEndPoint;

	@Autowired
	GeneralMultiLingualIntegratedSearchServicev6 generalMultiLingualIntegratedSearchServicev6;

	@Value("${pid.service.getuserinfo.path}")
	private String pidDbGetUsersInfoPath;

	@Value("${pid.service.ip}")
	private String pidDbIp;

	@Value("${pid.service.port}")
	private String pidDbPort;

	@PostConstruct
	private void initialize() {
		accessControlEnabled = environment.getProperty(LexConstants.ENABLE_ACCESS_CONTROL, Boolean.class);
	}

	private List<String> thiru = Arrays.asList("7bf31dd1-5b5d-4411-aa2e-4f264b546ee4",
			"f688c2cf-1131-47d1-9945-290c88565528", "8725901f-0159-42cb-bb6f-ceae3a84b79d",
			"7ff8df1b-60d6-4411-8c1a-4d7c6c492529");

	private RestHighLevelClient elasticClient = ConnectionManager.getClient();

	private ArrayList<String> includedSource = new ArrayList<>(Arrays.asList(
			"isStandAlone",
			"isInIntranet",
			"deliveryLanguages",
			"deliveryCountries",
			"costCenter",
			"exclusiveContent",
			"instanceCatalog",
			"price",
			"isContentEditingDisabled",
			"isMetaEditingDisabled",
			"labels",
			"publishedOn",
			"expiryDate",
			"hasTranslations",
			"isTranslationOf",
			"viewCount",
			"averageRating",
			"uniqueUsersCount",
			"totalRating",
			"collections",
			"unit",
			"status",
			"isExternal",
			"learningMode",
			"uniqueLearners",
			"name",
			"identifier",
			"description",
			"resourceType",
			"contentType",
			"isExternal",
			"appIcon",
			"artifactUrl",
			"children",
			"mimeType",
			"creatorContacts",
			"downloadUrl",
			"duration",
			"me_totalSessionsCount",
			"size",
			"complexityLevel",
			"lastUpdatedOn",
			"resourceCategory",
			"msArtifactDetails",
			"isIframeSupported",
			"contentUrlAtSource",
			"certificationUrl",
			"certificationList",
			"skills",
			"topics",
			"creatorDetails",
			"catalogPaths",
			"learningObjective",
			"preRequisites",
			"softwareRequirements",
			"systemRequirements",
			"track",
			"idealScreenSize",
			"minLexVersion",
			"preContents",
			"postContents",
			"isExternal",
			"certificationStatus",
			"subTitles",
			"publisherDetails",
			"trackContacts",
			"creatorContacts",
			"appIcon",
			"trackContacts",
			"publisherDetails"
	));

	@Override
	public Response getLatestOrg(UUID userId, String learningMode, String rootOrg, String org,
								 int pageNumber, int pageSize, List<String> contentTypefiltersArray, List<String> sourceShortName,
								 Boolean externalContent, List<String> locale, Boolean isInIntranet, Boolean isStandAlone) throws Exception {
		Response resp = new Response();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (null != learningMode) {
			boolQuery.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("learningMode", learningMode)));
		}

		if (externalContent)
			boolQuery.filter(QueryBuilders.boolQuery()
					.mustNot(QueryBuilders.termQuery("isExternal", true)));
		else
			boolQuery.filter(
					QueryBuilders.boolQuery().must(QueryBuilders.termQuery("isExternal", false)));

		if (contentTypefiltersArray.size() > 0) {
			boolQuery.mustNot(
					QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("contentType", contentTypefiltersArray)));
		}

		ArrayList<String> fields = new ArrayList<>(includedSource);
		fields.add("viewCount."+rootOrg);
		fields.add("averageRating."+rootOrg);
		fields.add("uniqueUsersCount."+rootOrg);
		fields.add("totalRating."+rootOrg);
		Object[] fieldsObjs = fields.toArray();
//		boolQuery.mustNot(QueryBuilders.nestedQuery("collections",QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("collections")), ScoreMode.Avg));
		boolQuery.filter(QueryBuilders.termQuery("isStandAlone", isStandAlone));
		boolQuery.filter(QueryBuilders.termQuery("status","Live"));
		boolQuery.filter(QueryBuilders.termQuery("rootOrg", rootOrg));
//		boolQuery.filter(QueryBuilders.nestedQuery("org", QueryBuilders.boolQuery().must(QueryBuilders.termQuery("org.org", org)).must(QueryBuilders.rangeQuery("validTill").gte("now/d")), ScoreMode.Avg));

		if (null != isInIntranet)
			boolQuery.filter(QueryBuilders.termQuery("isInIntranet",isInIntranet.toString()));

		if (accessControlEnabled) {
			Response accessControlResponse = accessControlService.getUserAccess(userId.toString(), rootOrg);
			List<String> accessPaths = (ArrayList<String>) accessControlResponse.get("combinedAccessPaths");
			boolQuery.filter(QueryBuilders.termsQuery("accessPaths", accessPaths));
		}
		if (null == locale)
			locale = Collections.singletonList("en");
		if (locale.isEmpty())
			locale.add("en");
		List<String> indices = new ArrayList<>();
		for (String searchIndexLocale : locale) {
			indices.add(LexProjectUtil.EsIndex.multi_lingual_search_index.getIndexName() + SearchConstants.SEARCH_INDEX_LOCALE_DELIMITER + searchIndexLocale);
		}
		SearchResponse searchResult = elasticClient.search(
				new SearchRequest().indices(indices.toArray(new String[0]))
						.types(LexProjectUtil.EsType.new_lex_search.getTypeName())
						.searchType(SearchType.QUERY_THEN_FETCH)
						.source(new SearchSourceBuilder().query(boolQuery).sort("lastUpdatedOn", SortOrder.DESC)
								.size(pageSize).from(pageNumber).fetchSource(Arrays.copyOf(fieldsObjs,fieldsObjs.length,String[].class), null)),
				RequestOptions.DEFAULT);

		List<Map<String, Object>> responseData = new ArrayList<>();
		searchResult.getHits().forEach(hit -> {
			Map<String, Object> data = hit.getSourceAsMap();
			Object viewCount = ((Map<String, Object>) data.getOrDefault("viewCount", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object averageRating = ((Map<String, Object>) data.getOrDefault("averageRating", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object uniqueUsersCount = ((Map<String, Object>) data.getOrDefault("uniqueUsersCount", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object totalRating = ((Map<String, Object>) data.getOrDefault("totalRating", new HashMap<>())).getOrDefault(rootOrg, 0.0);

			data.put("viewCount",viewCount);
			data.put("averageRating",averageRating);
			data.put("uniqueUsersCount",uniqueUsersCount);
			data.put("totalRating",totalRating);

			responseData.add(data);
		});

		resp.put("greyOut",false);
		resp.put(LexConstants.RESPONSE, responseData);
		return resp;
	}

	@Override
	public Response getLatestForRoles(UUID userId, String learningMode, String rootOrg,
									  String org, int pageNumber, int pageSize, List<String> contentTypefiltersArray,
									  List<String> sourceShortName, Boolean externalContent, List<String> locale, Boolean isInIntranet, Boolean isStandAlone) throws Exception {
		Response resp = new Response();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (thiru.contains(userId.toString())) {
			boolQuery.filter(QueryBuilders.termsQuery("audience", new HashSet<String>(Arrays.asList("Sales"))));
		} else {
			try {
				UserPersonalizationRolesModel userRolesResult = userPersonalizationRolesRepository
						.findByPrimaryKeyRootOrgAndPrimaryKeyOrgAndPrimaryKeyUserId(rootOrg, org, userId);
				if (null == userRolesResult.getRoles() || userRolesResult.getRoles().isEmpty())
					return getLatestOrg(userId, learningMode, rootOrg, org, pageNumber, pageSize,
							contentTypefiltersArray, sourceShortName, externalContent, locale, isInIntranet, isStandAlone);
				boolQuery.filter(QueryBuilders.termsQuery("audience", userRolesResult.getRoles()));
			} catch (Exception e) {
				return getLatestOrg(userId, learningMode, rootOrg, org, pageNumber, pageSize,
						contentTypefiltersArray, sourceShortName, externalContent, locale, isInIntranet, isStandAlone);
			}
		}
		if (contentTypefiltersArray.size() > 0) {
			boolQuery.mustNot(
					QueryBuilders.boolQuery().filter(QueryBuilders.termsQuery("contentType", contentTypefiltersArray)));
		}

		if (externalContent)
			boolQuery.filter(QueryBuilders.boolQuery()
					.mustNot(QueryBuilders.termsQuery("sourceShortName.keyword", sourceShortName)));
		else
			boolQuery.filter(
					QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("sourceShortName.keyword", sourceShortName)));

		if (null != learningMode) {
			boolQuery.filter(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("learningMode", learningMode)));
		}
		ArrayList<String> fields = new ArrayList<>(includedSource);
		fields.add("viewCount."+rootOrg);
		fields.add("averageRating."+rootOrg);
		fields.add("uniqueUsersCount."+rootOrg);
		fields.add("totalRating."+rootOrg);
		Object[] fieldsObjs = fields.toArray();
		boolQuery.mustNot(QueryBuilders.nestedQuery("collections",QueryBuilders.boolQuery().filter(QueryBuilders.existsQuery("collections")), ScoreMode.Avg));
		boolQuery.must(QueryBuilders.termQuery("status","Live"));
//		boolQuery.filter(QueryBuilders.termQuery("rootOrg", rootOrg));
//		boolQuery.filter(QueryBuilders.nestedQuery("org", QueryBuilders.boolQuery().must(QueryBuilders.termQuery("org.org", org)).must(QueryBuilders.rangeQuery("validTill").gte("now/d")), ScoreMode.Avg));
		if (accessControlEnabled) {
			Response accessControlResponse = accessControlService.getUserAccess(userId.toString(), rootOrg);
			List<String> accessPaths = (ArrayList<String>) accessControlResponse.get("combinedAccessPaths");
			boolQuery.filter(QueryBuilders.termsQuery("accessPaths", accessPaths));
		}
		if (null == locale)
			locale = Collections.singletonList("en");
		if (locale.isEmpty())
			locale.add("en");
		List<String> indices = new ArrayList<>();
		for (String searchIndexLocale : locale) {
			indices.add(LexProjectUtil.EsIndex.multi_lingual_search_index.getIndexName() + SearchConstants.SEARCH_INDEX_LOCALE_DELIMITER + searchIndexLocale);
		}
		SearchResponse searchResult = elasticClient.search(
				new SearchRequest().indices(indices.toArray(new String[0]))
						.types(LexProjectUtil.EsType.new_lex_search.getTypeName())
						.searchType(SearchType.QUERY_THEN_FETCH)
						.source(new SearchSourceBuilder().query(boolQuery).sort("lastUpdatedOn", SortOrder.DESC)
								.size(pageSize).from(pageNumber).fetchSource(Arrays.copyOf(fieldsObjs,fieldsObjs.length,String[].class), null)),
				RequestOptions.DEFAULT);

		List<Map<String, Object>> responseData = new ArrayList<>();
		searchResult.getHits().forEach(hit -> {
			Map<String, Object> data = hit.getSourceAsMap();
			Object viewCount = ((Map<String, Object>) data.getOrDefault("viewCount", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object averageRating = ((Map<String, Object>) data.getOrDefault("averageRating", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object uniqueUsersCount = ((Map<String, Object>) data.getOrDefault("uniqueUsersCount", new HashMap<>())).getOrDefault(rootOrg, 0.0);
			Object totalRating = ((Map<String, Object>) data.getOrDefault("totalRating", new HashMap<>())).getOrDefault(rootOrg, 0.0);

			data.put("viewCount",viewCount);
			data.put("averageRating",averageRating);
			data.put("uniqueUsersCount",uniqueUsersCount);
			data.put("totalRating",totalRating);

			responseData.add(data);
		});

		resp.put(LexConstants.RESPONSE, responseData);
		return resp;
	}

	@SuppressWarnings("unchecked")
	private Response getRecommendation(UUID userId, String indexName, String typeName, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		Response resp = new Response();

		try {
			GetResponse data = elasticClient.get(new GetRequest(indexName, typeName, userId.toString()),RequestOptions.DEFAULT);
			if (!data.isExists()) {
				return sendSearchRecommendations(userId,locales,sourceFields, isInIntranet);
			}
			if (filtersArray.size() > 0) {
				ArrayList<Map<String, Object>> responseData = new ArrayList<>();
				ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) ((Map<String, Object>) data
						.getSourceAsMap().get("doc")).get("data");
				results.forEach(item -> {
					if (!filtersArray.contains((String) item.get("contentType"))) {
						responseData.add(item);
					}
				});
				Map<String, Object> formattingMap = new HashMap<>();
				formattingMap.put("result",responseData);
				resp.put(LexConstants.RESPONSE, formattingMap);
			} else {
				Map<String, Object> formattingMap = new HashMap<>();
				formattingMap.put("result",((Map<String, Object>) data.getSourceAsMap().get("doc")).get("data"));
				resp.put(LexConstants.RESPONSE, formattingMap);
			}
		} catch (Exception e) {
			return sendSearchRecommendations(userId, locales,sourceFields, isInIntranet);
		}

		resp.put("greyOut",false);
		return resp;
	}

	private Response sendSearchRecommendations(UUID userId, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		Response resp = new Response();

		Map<String,Object> requestData = new HashMap<>();
		requestData.put("source_fields",Arrays.asList("root_org","residence_country","sub_department_name"));
		Map<String,Object> conditions = new HashMap<>();
		conditions.put("wid",userId.toString());
		requestData.put("conditions",conditions);
		List<Object> userDataResposne = (List<Object>) restTemplate.postForObject("http://"+pidDbIp + ":" + pidDbPort + pidDbGetUsersInfoPath,requestData,List.class);
		if (null != userDataResposne && !userDataResposne.isEmpty()){
			Map<String,Object> userData = (Map<String, Object>) userDataResposne.get(0);
			String query = (String) userData.getOrDefault("sub_department_name","");
			String rootOrg = (String) userData.getOrDefault("root_org","");
			String region = (String) userData.getOrDefault("residence_country","");

			if (null != query && !query.isEmpty()) {
				ValidatedSearchData validatedSearchData = new ValidatedSearchData();
				validatedSearchData.setQuery(query);
				validatedSearchData.setUuid(userId);
				validatedSearchData.setRootOrg(rootOrg);
				validatedSearchData.setPageNo(0);
				validatedSearchData.setPageSize(25);
				validatedSearchData.setLocale(locales);
				if (null != sourceFields && !sourceFields.isEmpty())
					validatedSearchData.setIncludeSourceFields(sourceFields);
				if (null != isInIntranet){
					AndFilters filters = new AndFilters();
					filters.setIsInIntranet(Collections.singletonList(isInIntranet.toString()));
					FiltersGroup filtersGroup = new FiltersGroup();
					filtersGroup.setAndFilters(Collections.singletonList(filters));
					validatedSearchData.setFilters(Collections.singletonList(filtersGroup));
				}

				try {
					Map<String, Object> searchResult = generalMultiLingualIntegratedSearchServicev6.performSearch(validatedSearchData);
					long totalHits = (long) searchResult.getOrDefault("totalHits", 0);
					if (totalHits == 0) {
						if (!region.isEmpty()) {
							validatedSearchData.setQuery("all");
							AndFilters filters = new AndFilters();
							filters.setRegion(Collections.singletonList(region));
							FiltersGroup filtersGroup = new FiltersGroup();
							filtersGroup.setAndFilters(Collections.singletonList(filters));
							validatedSearchData.setFilters(Collections.singletonList(filtersGroup));
							Map<String, Object> searchResponse = generalMultiLingualIntegratedSearchServicev6.performSearch(validatedSearchData);
							Response response = new Response();
							response.put("response", searchResponse);
							return response;
						}
					} else {
						Response response = new Response();
						response.put("response", searchResult);
						return response;
					}
				} catch (Exception e) {
					e.printStackTrace();
					resp.put(LexConstants.RESPONSE, new ArrayList<>());
				}
			}
		}else
			resp.put(LexConstants.RESPONSE, new ArrayList<>());
		resp.put("greyOut",false);
		return resp;
	}

	@Override
	public Response getRecommendationsForJL(UUID userId, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		return getRecommendation(userId, "recommendationsjl", "recommendation", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public Response getRecommendationsForUnit(UUID userId, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		return getRecommendation(userId, "recommendationsunit", "recommendation", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public Response getRecommendationsForAccount(UUID userId, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		return getRecommendation(userId, "recommendationsaccount", "recommendation", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public Response getRecommendationsForOrg(UUID userId, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) {
		return getRecommendation(userId, "recommendationsorg", "recommendation", filtersArray, locales, sourceFields, isInIntranet);
	}

	public Response getRandomRecommendations(UUID userId) throws IOException {
		Response resp = new Response();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("isStandAlone", true));

		Object[] fieldsObjs = includedSource.toArray();

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		if (thiru.contains(userId.toString()))
			sourceBuilder.query(boolQuery).size(10).from(0).fetchSource(Arrays.copyOf(fieldsObjs,fieldsObjs.length,String[].class), null);
		else
			sourceBuilder.query(boolQuery).size(10).from(11).fetchSource(Arrays.copyOf(fieldsObjs,fieldsObjs.length,String[].class), null);
		SearchResponse searchResult = elasticClient
				.search(new SearchRequest().indices(LexProjectUtil.EsIndex.new_lex_search.getIndexName())
						.types(LexProjectUtil.EsType.new_lex_search.getTypeName())
						.searchType(SearchType.QUERY_THEN_FETCH).source(sourceBuilder), RequestOptions.DEFAULT);
		List<Map<String, Object>> responseData = new ArrayList<>();
		searchResult.getHits().forEach(hit -> {
			responseData.add(hit.getSourceAsMap());
		});

		resp.put(LexConstants.RESPONSE, responseData);

		return resp;
	}

	@SuppressWarnings("unchecked")
	private Response getTrending(UUID userId, String rootOrg, String org, int pageNumber, int pageSize,
			String indexName, String typeName, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) throws Exception {
		Response resp = new Response();

		try {
			GetResponse data = elasticClient.get(new GetRequest(indexName, typeName, userId.toString()),
					RequestOptions.DEFAULT);
			if (!data.isExists()) {
				resp.put(LexConstants.RESPONSE,
						getTrendingOrg(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet));
				return resp;
			}
			if (filtersArray.size() > 0) {
				ArrayList<Map<String, Object>> responseData = new ArrayList<>();
				ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) ((Map<String, Object>) data
						.getSourceAsMap().get("doc")).get("data");
				results.forEach(item -> {
					if (!filtersArray.contains((String) item.get("contentType"))) {
						responseData.add(item);
					}
				});
				resp.put(LexConstants.RESPONSE, responseData);
			} else {
				resp.put(LexConstants.RESPONSE, ((Map<String, Object>) data.getSourceAsMap().get("doc")).get("data"));
			}
		} catch (Exception e) {
			resp.put(LexConstants.RESPONSE, getTrendingOrg(userId, rootOrg, org, pageNumber, pageSize, filtersArray, locales, sourceFields, isInIntranet));
			return resp;
		}

		return resp;
	}

	@Override
	public Response getTrendingForUnit(UUID userId, String rootOrg, String org, int pageNumber, int pageSize,
									   List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) throws Exception {
		return getTrending(userId, rootOrg, org, pageNumber, pageSize, "trendingunit", "trending", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public Response getTrendingForAccount(UUID userId, String rootOrg, String org, int pageNumber, int pageSize,
										  List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) throws Exception {
		return getTrending(userId, rootOrg, org, pageNumber, pageSize, "trendingaccount", "trending", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public Response getTrendingForJL(UUID userId, String rootOrg, String org, int pageNumber, int pageSize,
									 List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) throws Exception {
		return getTrending(userId, rootOrg, org, pageNumber, pageSize, "trendingjl", "trending", filtersArray, locales, sourceFields, isInIntranet);
	}

	@Override
	public List<Map<String, Object>> getTrendingOrg(UUID userId, String rootOrg, String org, int pageNumber,
													int pageSize, List<String> filtersArray, List<String> locales, List<String> sourceFields, Boolean isInIntranet) throws Exception {

		List<Map<String, Object>> responseData = new ArrayList<>();
		List<CourseRecommendation> serviceData = trendingService.getTrendingCourses(rootOrg, userId.toString(), String.valueOf(pageNumber),
				String.valueOf(pageSize), locales, sourceFields);

		if (null == serviceData || serviceData.isEmpty()){
			ValidatedSearchData validatedSearchData = new ValidatedSearchData();
			validatedSearchData.setUuid(userId);
			if (null != locales && !locales.isEmpty()) {
				validatedSearchData.setLocale(locales);
			}
			validatedSearchData.setRootOrg(rootOrg);
			validatedSearchData.setPageNo(0);
			validatedSearchData.setPageSize(25);
			if (null != sourceFields && !sourceFields.isEmpty())
				validatedSearchData.setIncludeSourceFields(sourceFields);

			validatedSearchData.setSort(Collections.singletonList(new HashMap<SortableFields, SortOrders>() {{
				put(SortableFields.averageRating, SortOrders.desc);
			}}));

			if (!filtersArray.isEmpty() || null != isInIntranet) {
				AndFilters filters = new AndFilters();
				FiltersGroup filtersGroup = new FiltersGroup();

				if (!filtersArray.isEmpty()) {
					filters.setContentType(filtersArray);
				}

				if (null != isInIntranet) {
					filters.setIsInIntranet(Collections.singletonList(isInIntranet.toString()));
				}

				filtersGroup.setAndFilters(Collections.singletonList(filters));
				validatedSearchData.setFilters(Collections.singletonList(filtersGroup));
			}

			Map<String, Object> searchResult = generalMultiLingualIntegratedSearchServicev6.performSearch(validatedSearchData);
			return (List<Map<String, Object>>) searchResult.getOrDefault("result",new ArrayList<>());
		}

		serviceData.forEach(item -> {
			if (filtersArray.size() > 0) {
				if (filtersArray.contains(item.getCourse().get("contentType"))) {
					responseData.add(item.getCourse());
				}
			} else {
				responseData.add(item.getCourse());
			}
		});
		return responseData;
	}

	@Override
	public Response getInterests(UUID userId, List<String> learningMode, int pageNumber, int pageSize, List<String> includeContentTypes, Boolean externalContentFilter, List<String> locales, String rootOrg, List<String> sourceFields, Boolean isInIntranet, Boolean isStandAlone) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("rootOrg",rootOrg);
		if (null !=locales && !locales.isEmpty())
			locales.forEach(item->headers.add("langCode",item));
		HttpEntity entity = new HttpEntity<>(headers);
		String x = CoreIp + CoreInterestsEndPoint.replace("{uuid}", userId.toString());

		ResponseEntity<Map> interests = null;
		try {
			interests = restTemplate.exchange(x, HttpMethod.GET, entity, Map.class);
		}catch(HttpStatusCodeException e){
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());

		}
		StringBuilder query = new StringBuilder();
		Map<String, Object> responseData = (Map<String, Object>) interests.getBody();
		List<String> interestsList = (List<String>) responseData.getOrDefault("user_interest", new ArrayList<>());
		for (String item : interestsList) {
			query.append(" ").append(item);
		}

		if (!query.toString().isEmpty()) {
			ValidatedSearchData validatedSearchData = new ValidatedSearchData();
			validatedSearchData.setQuery(query.toString());
			validatedSearchData.setUuid(userId);

			if (null != isStandAlone)
				validatedSearchData.setIsStandAlone(isStandAlone);
			if (null != locales && !locales.isEmpty()) {
				validatedSearchData.setLocale(locales);
			}
			validatedSearchData.setRootOrg(rootOrg);
			validatedSearchData.setPageNo(pageNumber);
			validatedSearchData.setPageSize(pageSize);

			if (null != sourceFields && !sourceFields.isEmpty())
				validatedSearchData.setIncludeSourceFields(sourceFields);

			AndFilters filters = new AndFilters();
			filters.setContentType(includeContentTypes);
			filters.setLearningMode(learningMode);
			if (null != externalContentFilter)
				filters.setIsExternal(Collections.singletonList(externalContentFilter.toString()));

			if (null != isInIntranet)
				filters.setIsInIntranet(Collections.singletonList(isInIntranet.toString()));

			FiltersGroup filtersGroup = new FiltersGroup();
			filtersGroup.setAndFilters(Collections.singletonList(filters));
			validatedSearchData.setFilters(Collections.singletonList(filtersGroup));
			Map<String, Object> searchResponse = generalMultiLingualIntegratedSearchServicev6.performSearch(validatedSearchData);
			Response response = new Response();
			response.put("greyOut",false);
			response.put("response", searchResponse);
			return response;
		} else {
			Response resp = new Response();
			resp.put("response",new HashMap<String,List>(){{put("result",Collections.emptyList());}});
			resp.put("greyOut",false);
			return resp;
		}
	}

}
