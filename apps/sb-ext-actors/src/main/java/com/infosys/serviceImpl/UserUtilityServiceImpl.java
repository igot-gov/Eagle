/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at 
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.elastic.common.ElasticSearchUtil;
import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.exception.ApplicationLogicError;
import com.infosys.exception.BadRequestException;
import com.infosys.exception.InvalidDataInputException;
import com.infosys.model.CourseProgress;
import com.infosys.service.LearningHistoryService;
import com.infosys.service.UserUtilityService;
import com.infosys.util.ConfigurationsUtil;
import com.infosys.util.LexJsonKey;
import com.infosys.util.LexProjectUtil;
import com.infosys.util.Util;

@Service
public class UserUtilityServiceImpl implements UserUtilityService {

	@Autowired
	LearningHistoryService lhService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	@Autowired
	ConfigurationsUtil configUtil;

	@Value("${content.service.host}")
	private String contentHost;

	@Value("${bodhi_content_port}")
	private String contentPort;

	private PropertiesCache properties = PropertiesCache.getInstance();
	private String bodhiKeyspace = LexJsonKey.BODHI_DB_KEYSPACE;
	private String appPropertiesTable = properties.getProperty(LexJsonKey.APPLICATION_PROPERTIES_TABLE);
	private CassandraConnectionManager connectionManager;
	private Map<String, String> mappingToPID = new HashMap<>();
	private String pidSvcIp = null;
	private String pidSvcPort = null;

	public UserUtilityServiceImpl() {
		Util.checkCassandraDbConnections(bodhiKeyspace);
		String cassandraMode = properties.getProperty(JsonKey.SUNBIRD_CASSANDRA_MODE);
		connectionManager = CassandraConnectionMngrFactory.getObject(cassandraMode);

		mappingToPID.put("firstname", "first_name");
		mappingToPID.put("lastname", "last_name");
		mappingToPID.put("email", "last_name");
		mappingToPID.put("userId", "wid");
	}

	@PostConstruct
	public void initialize() {
		pidSvcIp = env.getProperty("pid.service.ip");
		pidSvcPort = env.getProperty("pid.service.port");
	}

	@Override
	public Map<String, Object> getMetaByIDandSource(String id, String[] source) throws IOException {

		SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
		searchRequest.indices(LexProjectUtil.EsIndex.bodhi.getIndexName());
		searchRequest.types(LexProjectUtil.EsType.resource.getTypeName());

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", id)));
		searchSourceBuilder.fetchSource(source, new String[] {});
		searchSourceBuilder.size(1);
		searchRequest.source(searchSourceBuilder);

		SearchResponse response = ConnectionManager.getClient().search(searchRequest, RequestOptions.DEFAULT);

		if (response.getHits().totalHits == 0) {
			throw new InvalidDataInputException("invalid.resource");
		}
		return response.getHits().getAt(0).getSourceAsMap();
	}

	@Override
	public SearchHits getMetaByIDListandSource(List<String> ids, String[] source) throws IOException {
		RestHighLevelClient requestBuilder = ConnectionManager.getClient();
		SearchRequest searchRequest = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
		searchRequest.indices(LexProjectUtil.EsIndex.bodhi.getIndexName());
		searchRequest.types(LexProjectUtil.EsType.resource.getTypeName());

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids)));
		searchSourceBuilder.fetchSource(source, new String[] {});
		searchSourceBuilder.size(ids.size());
		searchRequest.source(searchSourceBuilder);

		SearchHits response = requestBuilder.search(searchRequest, RequestOptions.DEFAULT).getHits();

		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CourseProgress> getUserCourseProgress(String emailId, int pageSize) {
		List<CourseProgress> historyProgress = new ArrayList<>();
		try {
			if (!emailId.endsWith(".com")) {
				emailId += ".com";
			}

			Map<String, Object> temp = lhService.getUserCourseProgress(emailId, 0, pageSize, "inprogress", 1, "course");
			List<Object> results = (List<Object>) temp.get("results");
			System.out.println("Results " + results.size());
			historyProgress = results.stream().map(meta -> {
				HashMap<String, Object> metaMap = (HashMap<String, Object>) meta;
				return new CourseProgress((String) metaMap.get("identifier"), (String) metaMap.get("name"));
			}).collect(Collectors.toList());
		} catch (Exception ex) {
			ex.printStackTrace();
			ProjectLogger.log("Error : " + ex.getMessage(), ex);
		}

		return historyProgress;
	}

	// elastic
	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getUserDataFromElastic(List<String> users) {
		List<Map<String, Object>> userdata = new ArrayList<Map<String, Object>>();
		try {
			RestHighLevelClient requestBuilder = ConnectionManager.getClient();
			SearchRequest req = new SearchRequest().searchType(SearchType.QUERY_THEN_FETCH);
			req.indices("searchindex");
			req.types("user");

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
					.query(QueryBuilders.termsQuery("loginId.keyword", new ArrayList<String>(users)))
					.fetchSource(new String[] { "firstName", "lastName", "loginId" }, new String[0]).size(users.size());
			req.source(sourceBuilder);
			SearchResponse searchResponse = requestBuilder.search(req, RequestOptions.DEFAULT);
			SearchHits searchHits = searchResponse.getHits();
			for (SearchHit searchHit : searchHits) {
				userdata.add(new ObjectMapper().readValue(searchHit.getSourceAsString(), Map.class));
			}
		} catch (Exception e) {
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return userdata;
	}

	@Override
	public Response getAllRecordsForColumns(String keySpaceName, String tableName, List<String> columnNames) {
		long startTime = System.currentTimeMillis();
		ProjectLogger.log("Cassandra Service getAllRecordsForColumns method started at ==" + startTime,
				LoggerEnum.PERF_LOG);
		Response response = new Response();
		try {
			Select selectQuery = QueryBuilder.select(columnNames.toArray(new String[0])).from(keySpaceName, tableName);
			ResultSet results = connectionManager.getSession(keySpaceName).execute(selectQuery);
			response = CassandraUtil.createResponse(results);
		} catch (Exception exception) {
			ProjectLogger.log("Error fetching from " + tableName + " : " + exception.getMessage(), exception);
			throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
					ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		ProjectLogger.log("Cassandra Service getAllRecordsForColumns method end at ==" + stopTime
				+ " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
		return response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Response getRecordsByProperties(String keyspaceName, String tableName, Map<String, Object> propertyMap) {
		long startTime = System.currentTimeMillis();
		ProjectLogger.log("Cassandra Service getRecordsByProperties method started at ==" + startTime,
				LoggerEnum.PERF_LOG);
		Response response = new Response();
		try {
			Select selectQuery = QueryBuilder.select().all().from(keyspaceName, tableName);
			Where selectWhere = selectQuery.where();
			for (Entry<String, Object> entry : propertyMap.entrySet()) {
				if (entry.getValue() instanceof List) {
					List<?> l = (List<?>) entry.getValue();
					if (!l.isEmpty() && l.get(0) instanceof Integer) {
						Clause clause = QueryBuilder.in(entry.getKey(), (List<Integer>) entry.getValue());
						selectWhere.and(clause);
					} else if (!l.isEmpty() && l.get(0) instanceof String) {
						Clause clause = QueryBuilder.in(entry.getKey(), (List<String>) entry.getValue());
						selectWhere.and(clause);
					} else {
						Clause clause = QueryBuilder.in(entry.getKey(), entry.getValue());
						selectWhere.and(clause);
					}
				} else {
					Clause clause = eq(entry.getKey(), entry.getValue());
					selectWhere.and(clause);
				}
			}
			ResultSet results = connectionManager.getSession(keyspaceName).execute(selectQuery.allowFiltering());
			response = CassandraUtil.createResponse(results);

		} catch (Exception e) {
			ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + tableName + " : " + e.getMessage(), e);
			// e.printStackTrace();
			throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
					ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		ProjectLogger.log("Cassandra Service getRecordsByProperties method end at ==" + stopTime
				+ " ,Total time elapsed = " + elapsedTime, LoggerEnum.PERF_LOG);
		return response;
	}

	@SuppressWarnings({ "unchecked" })
	private List<Map<String, Object>> getUserIdsFromAccessToken(String AccessToken, List<String> mailIds)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<String> queries = new ArrayList<String>();
		if (mailIds == null || mailIds.isEmpty()) {
			throw new BadRequestException("mail id list is null or empty");
		}
		try {
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < mailIds.size(); i++) {
				sb.append("mail%20eq%20'").append(mailIds.get(i)).append("@infosys.com'");
				if ((i + 1) % 10 != 0) {
					sb.append("%20or%20");
					continue;
				}
				queries.add(sb.toString());
				sb = new StringBuilder();
			}
			if (sb != null && sb.toString().endsWith("%20or%20")) {
				sb = new StringBuilder(sb.toString().substring(0, sb.length() - 8));
			}
			if (sb != null && !sb.toString().equals("")) {
				queries.add(sb.toString());
			}

			for (String s : queries) {

				CloseableHttpClient httpClient = HttpClients.createDefault();
				// CloseableHttpClient httpClient = this.getAuthorizedClient();
				HttpGet getRequest = new HttpGet("https://graph.microsoft.com/beta/users?$filter=" + s
						+ "&$select=onPremisesUserPrincipalName,mail");
				getRequest.addHeader("Authorization", "Bearer " + AccessToken);

				HttpResponse response = httpClient.execute(getRequest);

				if (response != null && response.getEntity() != null) {
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader((response.getEntity().getContent())));

						String output;
						List<Map<String, Object>> directoryResult = new ArrayList<Map<String, Object>>();
						while ((output = br.readLine()) != null) {
							directoryResult.add(new ObjectMapper().readValue(output, Map.class));
						}
						if (directoryResult != null && !directoryResult.isEmpty()) {
							result.addAll((List<Map<String, Object>>) directoryResult.get(0).get("value"));
						}
					} catch (JsonParseException jpe) {
						jpe.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info ");
					} catch (JsonMappingException jme) {
						jme.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info");
					} catch (IOException ioex) {
						ioex.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info");
					}
				}
				getRequest.releaseConnection();
				httpClient.close();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			ProjectLogger.log("Error : " + e.getMessage(), e);
			throw new Exception("Access Token Expired");
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	private List<Map<String, Object>> getUserFromAccessToken(String AccessToken, List<String> mailIds)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<String> queries = new ArrayList<String>();
		if (mailIds == null || mailIds.isEmpty()) {
			throw new BadRequestException("mail id list is null or empty");
		}
		try {
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < mailIds.size(); i++) {
				sb.append("mail%20eq%20'").append(mailIds.get(i)).append("@infosys.com'");
				if ((i + 1) % 10 != 0) {
					sb.append("%20or%20");
					continue;
				}
				queries.add(sb.toString());
				sb = new StringBuilder();
			}
			if (sb != null && sb.toString().endsWith("%20or%20")) {
				sb = new StringBuilder(sb.toString().substring(0, sb.length() - 8));
			}
			if (sb != null && !sb.toString().equals("")) {
				queries.add(sb.toString());
			}

			for (String s : queries) {

				CloseableHttpClient httpClient = HttpClients.createDefault();
				// CloseableHttpClient httpClient = this.getAuthorizedClient();
				HttpGet getRequest = new HttpGet("https://graph.microsoft.com/beta/users?$filter=" + s
						+ "&$select=department,givenName,mobilePhone,surname,jobTitle,onPremisesUserPrincipalName,usageLocation,city,companyName,mail");
				getRequest.addHeader("Authorization", "Bearer " + AccessToken);

				HttpResponse response = httpClient.execute(getRequest);

				if (response != null && response.getEntity() != null) {
					try {
						BufferedReader br = new BufferedReader(
								new InputStreamReader((response.getEntity().getContent())));

						String output;
						List<Map<String, Object>> directoryResult = new ArrayList<Map<String, Object>>();
						while ((output = br.readLine()) != null) {
							directoryResult.add(new ObjectMapper().readValue(output, Map.class));
						}
						if (directoryResult != null && !directoryResult.isEmpty()) {
							result.addAll((List<Map<String, Object>>) directoryResult.get(0).get("value"));
						}
					} catch (JsonParseException jpe) {
						jpe.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info ");
					} catch (JsonMappingException jme) {
						jme.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info");
					} catch (IOException ioex) {
						ioex.printStackTrace();
						throw new ApplicationLogicError("Error Processing Info");
					}
				}
				getRequest.releaseConnection();
				httpClient.close();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			ProjectLogger.log("Error : " + e.getMessage(), e);
			throw new Exception("Access Token Expired");
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	private List<Map<String, Object>> getSearchDataFromAccessToken(String AccessToken, String searchString)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (searchString == null || searchString.isEmpty()) {
			throw new BadRequestException("search string is null or empty");
		}
		searchString = searchString.replaceAll(" ", "%20");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// CloseableHttpClient httpClient = this.getAuthorizedClient();
		HttpGet getRequest = new HttpGet("https://graph.microsoft.com/beta/users?$filter=startswith(displayName,'"
				+ searchString + "')%20or%20startswith(givenName,'" + searchString + "')%20or%20startswith(surname,'"
				+ searchString + "')%20or%20startswith(mail,'" + searchString
				+ "')&$select=displayName,mail,companyName&$top=5");
		getRequest.addHeader("Authorization", "Bearer " + AccessToken);

		HttpResponse response = httpClient.execute(getRequest);

		if (response != null && response.getEntity() != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
				String output;
				List<Map<String, Object>> directoryResult = new ArrayList<Map<String, Object>>();
				while ((output = br.readLine()) != null) {
					directoryResult.add(new ObjectMapper().readValue(output, Map.class));
				}
				if (directoryResult != null && !directoryResult.isEmpty()) {
					result.addAll((List<Map<String, Object>>) directoryResult.get(0).get("value"));
				}
			} catch (JsonParseException jpe) {
				jpe.printStackTrace();
				throw new ApplicationLogicError("Error Processing Info ");
			} catch (JsonMappingException jme) {
				jme.printStackTrace();
				throw new ApplicationLogicError("Error Processing Info");
			} catch (IOException ioex) {
				ioex.printStackTrace();
				throw new ApplicationLogicError("Error Processing Info");
			}
		}
		getRequest.releaseConnection();
		httpClient.close();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSearchDataFromActiveDirectory(String searchString) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("key", Arrays.asList("refresh_token", "access_token", "token_expires_on"));
		try {
			result = (List<Map<String, Object>>) this
					.getRecordsByProperties(bodhiKeyspace, "application_properties", propertyMap).getResult()
					.get("response");
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> m : result) {
				try {
					if (m.containsValue("token_expires_on")) {
						map.put("token_expires_on", new Date(Long.parseLong(m.get("value").toString()) * 1000));
					} else {
						map.put(m.get("key").toString(), m.get("value").toString());
					}
				} catch (NullPointerException npe) {
					throw new ApplicationLogicError("Error processing info");
				}
			}

			if (map.get("token_expires_on") == null || map.get("refresh_token") == null
					|| map.get("access_token") == null) {
				ProjectLogger.log("token expires on is null or refresh token is null or access token is null");
				throw new ApplicationLogicError("Error processing info");
			}

			// changed to 1 from 0
			if (((Date) map.get("token_expires_on")).compareTo(Calendar.getInstance().getTime()) < 0) {
				map.put("access_token", this.getAccessToken(map.get("refresh_token").toString()));
			}
			result = this.getSearchDataFromAccessToken(map.get("access_token").toString(), searchString);

			if (result == null || result.isEmpty()) {
				ProjectLogger.log("result from getAccessToken is null");
				throw new ApplicationLogicError("Error Processing info");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			result = new ArrayList<Map<String, Object>>();
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getUserIdsFromActiveDirectory(List<String> mailIds) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("key", Arrays.asList("refresh_token", "access_token", "token_expires_on"));
		try {
			result = (List<Map<String, Object>>) this
					.getRecordsByProperties(bodhiKeyspace, "application_properties", propertyMap).getResult()
					.get("response");
			// System.out.println(result);
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> m : result) {
				try {
					if (m.containsValue("token_expires_on")) {
						map.put("token_expires_on", new Date(Long.parseLong(m.get("value").toString()) * 1000));
					} else {
						map.put(m.get("key").toString(), m.get("value").toString());
					}
				} catch (NullPointerException npe) {
					throw new ApplicationLogicError("Error processing info");
				}
			}

			if (map.get("token_expires_on") == null || map.get("refresh_token") == null
					|| map.get("access_token") == null) {
				ProjectLogger.log("token expires on is null or refresh token is null or access token is null");
				throw new ApplicationLogicError("Error processing info");
			}

			if (((Date) map.get("token_expires_on")).compareTo(Calendar.getInstance().getTime()) < 0) {
				map.put("access_token", this.getAccessToken(map.get("refresh_token").toString()));
			}
			result = this.getUserIdsFromAccessToken(map.get("access_token").toString(), mailIds);

			if (result == null || result.isEmpty()) {
				ProjectLogger.log("result from getAccessToken is null");
				throw new ApplicationLogicError("Error Processing info");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			result = new ArrayList<Map<String, Object>>();
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return result;
	}

	// User Data
	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getUsersFromActiveDirectory(List<String> mailIds) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("key", Arrays.asList("refresh_token", "access_token", "token_expires_on"));
		try {
			result = (List<Map<String, Object>>) this
					.getRecordsByProperties(bodhiKeyspace, "application_properties", propertyMap).getResult()
					.get("response");
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> m : result) {

				try {
					if (m.containsValue("token_expires_on")) {
						map.put("token_expires_on", new Date(Long.parseLong(m.get("value").toString()) * 1000));
					} else {
						map.put(m.get("key").toString(), m.get("value").toString());
					}
				} catch (NullPointerException npe) {
					throw new ApplicationLogicError("Error processing info");
				}
			}

			if (map.get("token_expires_on") == null || map.get("refresh_token") == null
					|| map.get("access_token") == null) {
				ProjectLogger.log("token expires on is null or refresh token is null or access token is null");
				throw new ApplicationLogicError("Error processing info");
			}

			if (((Date) map.get("token_expires_on")).compareTo(Calendar.getInstance().getTime()) < 0) {
				map.put("access_token", this.getAccessToken(map.get("refresh_token").toString()));
			}
			result = this.getUserFromAccessToken(map.get("access_token").toString(), mailIds);

			if (result == null || result.isEmpty()) {
				ProjectLogger.log("result from getAccessToken is null");
				throw new ApplicationLogicError("Error Processing info");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			result = new ArrayList<Map<String, Object>>();
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return result;
	}

	// User Photo
	@Override
	@SuppressWarnings("unchecked")
	public byte[] getUserPhotoFromActiveDirectory(String mailId) {
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("key", Arrays.asList("refresh_token", "access_token", "token_expires_on"));
		byte[] r = null;
		try {
			List<Map<String, Object>> result = (List<Map<String, Object>>) this
					.getRecordsByProperties(bodhiKeyspace, "application_properties", propertyMap).getResult()
					.get("response");
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> m : result) {
				if (m.containsValue("token_expires_on"))
					map.put("token_expires_on", new Date(Long.parseLong(m.get("value").toString()) * 1000));
				else
					map.put(m.get("key").toString(), m.get("value").toString());
			}

			if (((Date) map.get("token_expires_on")).compareTo(Calendar.getInstance().getTime()) < 0) {
				map.put("access_token", this.getAccessToken(map.get("refresh_token").toString()));
			}
			r = this.getUserPhotoFromAccessToken(map.get("access_token").toString(), mailId);

		} catch (Exception e) {
			r = new byte[0];
			propertyMap = new HashMap<String, Object>();
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return r;
	}

	private byte[] getUserPhotoFromAccessToken(String AccessToken, String mailId) throws Exception {
		byte byteArray[] = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			// CloseableHttpClient httpClient = this.getAuthorizedClient();
			HttpGet getRequest = new HttpGet(
					"https://graph.microsoft.com/beta/users/" + mailId + "@ad.infosys.com/photo/$value");
			getRequest.addHeader("Authorization", "Bearer " + AccessToken);
			HttpResponse response = httpClient.execute(getRequest);
			BufferedInputStream bin = new BufferedInputStream(response.getEntity().getContent());
			byteArray = IOUtils.toByteArray(bin);
			bin.close();
			String output = "";
			for (byte b : byteArray) {
				output += (char) b;
				if (output.contains("\"error\"")) {
					byteArray = "".getBytes();
					break;
				}
				if (output.length() > 20) {
					break;
				}
			}
			getRequest.releaseConnection();
			httpClient.close();
		} catch (Exception e) {
			throw new Exception("Access Token Expired");
		}
		return byteArray;
	}

	// Multiple User Photos
	@SuppressWarnings("unchecked")
	public Map<String, String> getMultipleUserPhotoFromActiveDirectory(List<String> mailIds) {
		Map<String, String> retValue = new HashMap<String, String>();
		try {
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			propertyMap.put("key", Arrays.asList("refresh_token", "access_token", "token_expires_on"));
			List<Map<String, Object>> result = (List<Map<String, Object>>) this
					.getRecordsByProperties(bodhiKeyspace, "application_properties", propertyMap).getResult()
					.get("response");
			Map<String, Object> map = new HashMap<String, Object>();
			for (Map<String, Object> m : result) {
				if (m.containsValue("token_expires_on"))
					map.put("token_expires_on", new Date(Long.parseLong(m.get("value").toString()) * 1000));
				else
					map.put(m.get("key").toString(), m.get("value").toString());
			}

			if (((Date) map.get("token_expires_on")).compareTo(Calendar.getInstance().getTime()) < 0) {
				map.put("access_token", this.getAccessToken(map.get("refresh_token").toString()));
			}
			retValue = this.getMultipleUserPhotoFromAccessToken(map.get("access_token").toString(), mailIds);

		} catch (Exception e) {
			retValue = new HashMap<String, String>();
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return retValue;
	}

	private Map<String, String> getMultipleUserPhotoFromAccessToken(String AccessToken, List<String> mailIds)
			throws Exception {
		Map<String, String> retValue = new HashMap<String, String>();
		try {
			mailIds.parallelStream().forEach(mailId -> {
				try {
					CloseableHttpClient httpClient = HttpClients.createDefault();
					// CloseableHttpClient httpClient = this.getAuthorizedClient();
					HttpGet getRequest = new HttpGet(
							"https://graph.microsoft.com/beta/users/" + mailId + "@ad.infosys.com/photo/$value");
					getRequest.addHeader("Authorization", "Bearer " + AccessToken);
					HttpResponse response = httpClient.execute(getRequest);
					BufferedInputStream bin = new BufferedInputStream(response.getEntity().getContent());
					byte[] byteArray = IOUtils.toByteArray(bin);
					bin.close();
					String output = "";
					for (byte b : byteArray) {
						output += (char) b;
						if (output.contains("\"error\"")) {
							byteArray = "".getBytes();
							break;
						}
						if (output.length() > 20) {
							break;
						}
					}
					getRequest.releaseConnection();
					httpClient.close();
					retValue.put(mailId, new String(Base64.encodeBase64(byteArray), "UTF-8"));
				} catch (Exception e) {
					retValue.put(mailId, "");
				}
			});
		} catch (Exception e) {
			throw new Exception("Access Token Expired");
		}
		return retValue;
	}

	// util
	@SuppressWarnings("unchecked")
	private String getAccessToken(String refresh_token) throws Exception {
		String access_token = "";
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			// CloseableHttpClient httpClient = this.getAuthorizedClient();
			HttpPost postRequest = new HttpPost("https://login.microsoftonline.com/common/oauth2/token");

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("client_id", "c6597d0b-c629-4885-a53c-44490643ddda"));
			postParameters.add(new BasicNameValuePair("refresh_token", refresh_token));
			postParameters.add(new BasicNameValuePair("redirect_uri", "url"));
			postParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
			postParameters.add(new BasicNameValuePair("client_secret", "514sYsYWUtkP1ViUvrToJgIJOQfN7Ijnwo4I7pZbxWw="));
			postRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
			HttpResponse response = httpClient.execute(postRequest);

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			Map<String, Object> map = new HashMap<String, Object>();
			while ((output = br.readLine()) != null) {
				// System.out.println(output);
				map.putAll(new ObjectMapper().readValue(output, Map.class));
			}
			if (!map.containsKey("error_description"))
				access_token = map.get("access_token").toString();
			else {
				throw new Exception("Refresh Token Expired.");
			}
			postRequest.releaseConnection();
			httpClient.close();
			insertToken(bodhiKeyspace, appPropertiesTable, map.get("access_token").toString(),
					map.get("refresh_token").toString(), map.get("expires_on").toString());
		} catch (Exception e) {
			// e.printStackTrace();
			ProjectLogger.log("Error : " + e.getMessage(), e);
			throw e;
		}
		return access_token;
	}

	private Response insertToken(String keyspaceName, String tableName, String access_token, String refresh_token,
			String token_expires_on) throws Exception {
		Response response = new Response();
		try {
			String query = "insert into application_properties (key,value) values(?,?);";
			PreparedStatement statement = connectionManager.getSession(keyspaceName).prepare(query);
			BatchStatement batchStatement = new BatchStatement();
			batchStatement.add(statement.bind("resource", "graph.microsoft.com"));
			batchStatement.add(statement.bind("access_token", access_token));
			batchStatement.add(statement.bind("refresh_token", refresh_token));
			batchStatement.add(statement.bind("token_expires_on", token_expires_on));

			connectionManager.getSession(keyspaceName).execute(batchStatement);
			response.put(Constants.RESPONSE, Constants.SUCCESS);
		} catch (Exception e) {
			throw new Exception("Cassandra Insertion failed.");
		}
		return response;
	}

	@SuppressWarnings("unused")
	private CloseableHttpClient getAuthorizedClient() throws Exception {
		final String username = "";
		final String password = "";
		final String proxyUrl = "";
		final int port = 80;
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyUrl, port),
				new UsernamePasswordCredentials(username, password));

		HttpHost myProxy = new HttpHost(proxyUrl, port);
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();

		clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
		return clientBuilder.build();
	}

	/*
	 * this method checks whether the list of users with whom goals/playlist is
	 * being shared is a list of valid users.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> verifyUsers(List<String> emails) {
		List<String> graphApiValidUsers = new ArrayList<>();
		List<String> validUsers = new ArrayList<>();
		List<String> dataForGraphApi = new ArrayList<>();
		if (emails != null && !emails.isEmpty()) {
			{
				Set<String> temp = new HashSet<String>();
				temp.addAll(emails);
				emails = new ArrayList<>(temp);
				emails.replaceAll(String::toLowerCase);
			}
			Map<String, Object> validUserData = userVerificationFromUserDb(emails);
			validUsers = (ArrayList<String>) validUserData.get("valid_users");
			emails.removeAll(validUsers);
			if (validUserData.containsKey("validate_options")
					&& (((List<String>) (validUserData.get("validate_options"))).contains("graph"))) {
				for (String email : emails) {
					if (email.contains("@")) {
						dataForGraphApi.add(email.substring(0, email.indexOf("@")).trim());
					}
				}
				if (!dataForGraphApi.isEmpty()) {
					List<Map<String, Object>> userGraphData = this
							.getUserIdsFromActiveDirectory(new ArrayList<String>(dataForGraphApi));
					if (userGraphData != null && !userGraphData.isEmpty()) {
						for (Map<String, Object> map : userGraphData) {
							if (emails.contains(map.get("onPremisesUserPrincipalName").toString().toLowerCase())) {
								graphApiValidUsers.add(map.get("onPremisesUserPrincipalName").toString().toLowerCase());
							}
						}
					}
					validUsers.addAll(graphApiValidUsers);
					emails.removeAll(graphApiValidUsers);
				}
			}
		}
		Map<String, Object> userValidityMap = new HashMap<String, Object>();
		userValidityMap.put("valid_users", validUsers);
		userValidityMap.put("invalid_users", new ArrayList<String>(emails));
		return userValidityMap;
	}

	/*
	 * this method is used to verify user email from sunbird.mv_user materialized
	 * view in cassandra db.
	 *
	 */
	@Override
	public String getValidationOptions() {
		Select select = QueryBuilder.select().column("value").from(bodhiKeyspace, appPropertiesTable);
		select.where(eq("key", LexJsonKey.EMAIL_VALIDATE_OPTIONS)).and(eq("root_org", "Infosys"));
		ProjectLogger.log("Query: " + select, LoggerEnum.DEBUG);
		ResultSet appResults = connectionManager.getSession(bodhiKeyspace).execute(select);
		String emailValidationOptions = "";
		for (Row row : appResults) {
			emailValidationOptions = row.getString(0);
		}
		return emailValidationOptions;
	}

	private Map<String, Object> userVerificationFromUserDb(List<String> userData) {
		Map<String, Object> output = new HashMap<String, Object>();
		ArrayList<String> validUsers = new ArrayList<>();
		ArrayList<String> validateOptions = new ArrayList<>();
		validateOptions.add("user");
		try {
			String emailValidationOptions = this.getValidationOptions();
			if (emailValidationOptions.toLowerCase().contains("graph")) {
				validateOptions.add("graph");
			}
			Select emailSelect = QueryBuilder.select().column("email").from(JsonKey.SUNBIRD, LexJsonKey.MV_USER);
			emailSelect.where(QueryBuilder.in("email", userData));
			ProjectLogger.log("Query: " + emailSelect, LoggerEnum.DEBUG);
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(emailSelect);
			for (Row row : results) {
				validUsers.add(row.getString(0).toLowerCase().trim());
			}
			output.put("validate_options", validateOptions);
			output.put("valid_users", validUsers);
		} catch (Exception e) {
			ProjectLogger.log(Constants.EXCEPTION_MSG_FETCH + LexJsonKey.MV_USER + " : " + e.getMessage(), e);
			throw new ProjectCommonException(ResponseCode.SERVER_ERROR.getErrorCode(),
					ResponseCode.SERVER_ERROR.getErrorMessage(), ResponseCode.SERVER_ERROR.getResponseCode());
		}
		return output;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAssessmentMap(String artifactUrl) {
		return restTemplate.getForObject(artifactUrl, Map.class);
	}

	@Override
	public HttpResponse getFileForEmail(String url) {
		HttpResponse res = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			StringBuilder goodUri = new StringBuilder();
			String[] urlArray = url.split("/");
			for (int i = 0; i < urlArray.length; i++) {
				if (i < urlArray.length - 1)
					goodUri.append(urlArray[i] + "/");
				else
					goodUri.append(urlArray[i].replaceAll(" ", "%20").replaceAll("\\[", "%5B").replaceAll("]", "%5D"));
			}
			HttpGet getRequest = new HttpGet(goodUri.toString());

			res = httpClient.execute(getRequest);
			getRequest.releaseConnection();
			httpClient.close();
		} catch (Exception e) {
			res = null;
		}
		return res;
	}

	@Override
	public File getImageFromContentStore() throws Exception {
		File tempFile = File.createTempFile("LexHeader", ".png");
		tempFile.deleteOnExit();
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet getRequest = new HttpGet("http://" + contentHost + ":" + contentPort
					+ "/content/Images/AppImages/LexHeader.png?type=artifacts");
			HttpResponse response = httpClient.execute(getRequest);
			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				IOUtils.copy(response.getEntity().getContent(), out);
			}
			getRequest.releaseConnection();
			httpClient.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			ProjectLogger.log("Error : Image fetch failed :" + e.getMessage(), e);
		}
		return tempFile;
	}

	@Override
	public String insertFile(File f, String contentId) {
		String url = "";
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();

			// creates folder or responds with 409(Conflict): Folder Exists
			HttpPost postRequest = new HttpPost(
					"http://" + contentHost + ":" + contentPort + "/content/Submissions/" + contentId);
			httpClient.execute(postRequest);
			postRequest.releaseConnection();
			// adds the file
			postRequest = new HttpPost(
					"http://" + contentHost + ":" + contentPort + "/content/Submissions/" + contentId + "/artifacts");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("content", f, ContentType.APPLICATION_OCTET_STREAM, f.getName());
			HttpEntity multipart = builder.build();

			postRequest.setEntity(multipart);

			HttpResponse resp = httpClient.execute(postRequest);
			BufferedReader br = new BufferedReader(new InputStreamReader((resp.getEntity().getContent())));

			String output;
			// gets the url
			while ((output = br.readLine()) != null) {
				url = new ObjectMapper().readValue(output, Map.class).get("contentURL").toString();
			}
			url = url.split(contentPort)[1];
			postRequest.releaseConnection();
			httpClient.close();
		} catch (Exception e) {
			ProjectLogger.log("Error : " + e.getMessage(), e);
			url = null;
		}
		return url;
	}

	@Cacheable("domainCache")
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getMailData() {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			Map<String, Object> propertyMap = new HashMap<String, Object>();
			// hard coding root_org here because this will be removed soon.
			propertyMap.put("root_org", "Infosys");
			propertyMap.put("key", Arrays.asList("valid_domains", "mail_id", "mail_name"));
			Response result = this.getRecordsByProperties(bodhiKeyspace, appPropertiesTable, propertyMap);

			if (!((List<Map<String, Object>>) result.get("response")).isEmpty()) {
				for (Map<String, Object> map : (List<Map<String, Object>>) result.get("response")) {
					if (map.get("key").toString().equals("valid_domains")) {
						ret.put("domains", map.get("value"));
					} else if (map.get("key").toString().equals("mail_id")) {
						ret.put("senderId", map.get("value"));
					} else if (map.get("key").toString().equals("mail_name")) {
						ret.put("senderName", map.get("value"));
					}
				}
			} else
				throw new Exception("No valid domains");
		} catch (Exception e) {
			System.out.println("No domain in cassandra");
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}
		return ret;
	}

	@Override
	public String checkElasticSearch() {
		try {
			ClusterHealthResponse response = ConnectionManager.getClient().cluster().health(
					new ClusterHealthRequest(LexProjectUtil.EsIndex.bodhi.getIndexName()), RequestOptions.DEFAULT);
			ClusterHealthStatus status = response.getStatus();
			if (status.equals(ClusterHealthStatus.RED)) {
				return "0";
			}
			return "1";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public Boolean checkContentStore(String contentHost, String contentPort) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity("http://" + contentHost + ":" + contentPort
					+ "/content/Images/AppImages/LexHeader.png?type=artifacts", String.class);
			if (response.getStatusCodeValue() >= 300)
				return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<Map<String, Object>> getUserSuggestionsForQuery(String searchString) throws Exception {
		List<Map<String, Object>> userList = new ArrayList<>();
		try {
			SearchRequest req = new SearchRequest().indices(LexProjectUtil.EsIndex.lexUserAutoComplete.getIndexName())
					.types(LexProjectUtil.EsType.autoCompleteType.getTypeName()).searchType(SearchType.QUERY_THEN_FETCH)
					.source(new SearchSourceBuilder()
							.query(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("email", searchString))
									.should(QueryBuilders.matchQuery("name", searchString)))
							.size(5));
			SearchResponse searchResponse = ConnectionManager.getClient().search(req, RequestOptions.DEFAULT);

			SearchHits searchHits = searchResponse.getHits();

			for (SearchHit option : searchHits) {
				Map<String, Object> temp = new HashMap<>();
				{
					Map<String, Object> map = option.getSourceAsMap();
					temp.put("displayName", map.get("name"));
					temp.put("mail", map.get("email"));
					temp.put("id", map.get("id"));
				}
				userList.add(temp);
			}

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		return userList;
	}

	@Override
	public void insertRecordInElasticSearchAutocompleteIndex(Map<String, Object> requestMap) {
		String name = (String) requestMap.get("firstName");
		if (!name.equals(requestMap.get("lastName")))
			name = name + " " + requestMap.get("lastName");
		String id = (String) requestMap.get(JsonKey.ID) == null ? "" : requestMap.get(JsonKey.ID).toString();
		String email = (String) requestMap.get(JsonKey.EMAIL);
		email = email.replace("@infosys.com", "@ad.infosys.com");
		Map<String, Object> source = new HashMap<>();
		source.put(JsonKey.EMAIL, email);
		source.put(JsonKey.ID, id);
		source.put(JsonKey.NAME, name);
		ElasticSearchUtil.upsertData(LexProjectUtil.EsIndex.lexUserAutoComplete.getIndexName(),
				LexProjectUtil.EsType.autoCompleteType.getTypeName(), email, source);
//		Client client = ConnectionManager.getClient();
//		client.prepareIndex(LexProjectUtil.EsIndex.UserAutoComplete.getIndexName(),
//				LexProjectUtil.EsType.autoCompleteType.getTypeName(), email).setSource(source).get();
	}

	@Override
	public boolean validateRootOrgUser(String user, String rootOrg) {

		String userId = "";
		Select.Where select = QueryBuilder.select().all().from(bodhiKeyspace, "user_terms_condition")
				.where(eq("root_org", rootOrg)).and(eq("user_id", user)).and(eq("doc_name", "Generic T&C"))
				.and(QueryBuilder.in("doc_for", Arrays.asList("User", "Author")));

		ResultSet results = connectionManager.getSession(bodhiKeyspace).execute(select);
		userId = results.one().getString("user_id");
		if (userId != null && !userId.isEmpty())
			return true;
		return false;
	}

	// ---------------------------------------------------------------------------------------------------------------------

	@Override
	@SuppressWarnings("unchecked")
	public boolean validateUser(String rootOrg, String userId) {

		boolean isValid = false;
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
//		System.out.println("=======================================");
//		System.out.println("==============Step 1: DATA SOURCE: " + dataSource + "===================");
		if (dataSource.equalsIgnoreCase("su")) {

			Select userIdSelect = QueryBuilder.select().column("id").from(JsonKey.SUNBIRD, "user");
			userIdSelect.where(QueryBuilder.eq("id", userId));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);
			if (results.one() != null) {
				isValid = true;
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("source_fields", Arrays.asList("wid"));
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("wid", userId);
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			List<Map<String, Object>> resultFromPID = new ArrayList<>();
			try {
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				isValid = false;
			}
			if (!resultFromPID.isEmpty()) {
				isValid = true;
			}
		}
		return isValid;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> validateUsers(String rootOrg, List<String> userIds) {
		Map<String, Object> validityMap = new HashMap<>();

		List<String> validUserIds = new ArrayList<>();
		List<String> invalidUserIds = new ArrayList<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select().column("id").from(JsonKey.SUNBIRD, "user");
			userIdSelect.where(QueryBuilder.in("id", userIds));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				validUserIds.add(row.getString(0).trim());
			}
			for (String uId : userIds) {
				if (!validUserIds.contains(uId)) {
					invalidUserIds.add(uId);
				}
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user/multi-fetch/wid";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("source_fields", Arrays.asList("wid"));
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			requestMap.put("values", userIds);
			List<Map<String, Object>> resultFromPID = new ArrayList<>();
			try {
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()) {
						validUserIds.add(data.get("wid").toString());
					}
				}
				for (String uId : userIds) {
					if (!validUserIds.contains(uId)) {
						invalidUserIds.add(uId);
					}
				}
			} else {
				invalidUserIds.addAll(userIds);
			}
		}
		validityMap.put("valid_users", validUserIds);
		validityMap.put("invalid_users", invalidUserIds);
		return validityMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source) {
		Map<String, Object> userWiseData = new HashMap<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select("firstname", "lastname", "email", "id").from(JsonKey.SUNBIRD,
					"user");
			userIdSelect.where(QueryBuilder.in("id", userIds));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("first_name", row.getString("firstname"));
				data.put("last_name", row.getString("lastname"));
				data.put("email", row.getString("email"));

				userWiseData.put(row.getString("id"), data);
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user/multi-fetch/wid";
			Map<String, Object> requestMap = new HashMap<>();
			List<String> sourceFields = new ArrayList<>();
			sourceFields.addAll(source);
			if (!sourceFields.contains("wid")) {
				sourceFields.add("wid");
			}
			requestMap.put("source_fields", sourceFields);
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			requestMap.put("values", userIds);

			List<Map<String, Object>> resultFromPID = new ArrayList<>();

//			System.out.println("============ START =================");

			try {
//				System.out.println("=============================");
//				System.out.println(requestMap);
//				System.out.println("PID service: " + url + " " + requestMap);
//				System.out.println("=============================");
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()) {
						userWiseData.put(data.get("wid").toString(), data);
					}
				}
			}
		}
//		System.out.println("=========== END =================");
		return userWiseData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserDataFromUserId(String rootOrg, String userId, List<String> source) {

		Map<String, Object> userData = new HashMap<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select("firstname", "lastname", "email", "id").from(JsonKey.SUNBIRD,
					"user");
			userIdSelect.where(QueryBuilder.eq("id", userId));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("first_name", row.getString("firstname"));
				data.put("last_name", row.getString("lastname"));
				data.put("email", row.getString("email"));

				userData.put(row.getString("id"), data);
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user";
			Map<String, Object> requestMap = new HashMap<>();
			List<String> sourceFields = new ArrayList<>();
			sourceFields.addAll(source);
			if (!sourceFields.contains("wid")) {
				sourceFields.add("wid");
			}
			requestMap.put("source_fields", sourceFields);
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("wid", userId);
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			List<Map<String, Object>> resultFromPID = new ArrayList<>();
//			System.out.println("============ START =================");
			try {
//				System.out.println("=============================");
//				System.out.println(requestMap);
//				System.out.println("PID service: " + url + " " + requestMap);
//				System.out.println("=============================");
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()) {
						userData.put(data.get("wid").toString(), data);
					}
				}
			}
		}
//		System.out.println("============ END =================");
		return userData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserEmailsFromUserIds(String rootOrg, List<String> userIds) {
		Map<String, Object> userData = new HashMap<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select("email", "id").from(JsonKey.SUNBIRD, "user");
			userIdSelect.where(QueryBuilder.in("id", userIds));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				userData.put(row.getString("id"), row.getString("email"));
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user/multi-fetch/wid";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("source_fields", Arrays.asList("wid", "root_org", "email"));
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("root_org", rootOrg);
			requestMap.put("values", userIds);

			List<Map<String, Object>> resultFromPID = new ArrayList<>();

//			System.out.println("============ START =================");

			try {
//				System.out.println("=============================");
//				System.out.println(requestMap);
//				System.out.println("PID service: " + url + " " + requestMap);
//				System.out.println("=============================");
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()) {
						userData.put(data.get("wid").toString(), data.get("email").toString());
					}
				}
			}
		}
//		System.out.println("============ END =================");
		return userData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getUserEmailFromUserId(String rootOrg, String userId) {
		String email = null;
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select("email", "id").from(JsonKey.SUNBIRD, "user");
			userIdSelect.where(QueryBuilder.eq("id", userId));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);
			for (Row row : results) {
				email = row.getString("email");
			}
			if (email == null) {
				throw new InvalidDataInputException("invalid.user");
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("source_fields", Arrays.asList("wid", "root_org", "email"));
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("wid", userId);
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			List<Map<String, Object>> resultFromPID = new ArrayList<>();
//			System.out.println("============ START =================");
			try {
//				System.out.println("=============================");
//				System.out.println(requestMap);
//				System.out.println("PID service: " + url + " " + requestMap);
//				System.out.println("=============================");
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()) {
						email = data.get("email").toString();
					}
				}
			} else {
				throw new InvalidDataInputException("invalid.user");
			}
		}
//		System.out.println("============ END =================");
		return email;
	}

	public Map<String, Object> getUserIdsFromEmails(String rootOrg, List<String> emails) {
		Map<String, Object> userData = new HashMap<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select("email", "id").from(JsonKey.SUNBIRD, "mv_user");
			userIdSelect.where(QueryBuilder.in("email", emails));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				userData.put(row.getString("email"), row.getString("id"));
			}
		}
		return userData;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> validateAndFetchExistingAndNewUsers(String rootOrg, List<String> userIds) {
		Map<String, Object> validityMap = new HashMap<>();

		List<String> validUserIds = new ArrayList<>();
		List<String> invalidUserIds = new ArrayList<>();
		List<String> newUserIds = new ArrayList<>();
		String dataSource = configUtil.getUserDataSourceConfiguration(rootOrg);
		if (dataSource.equalsIgnoreCase("su")) {
			Select userIdSelect = QueryBuilder.select().column("id").from(JsonKey.SUNBIRD, "user");
			userIdSelect.where(QueryBuilder.in("id", userIds));
			ResultSet results = connectionManager.getSession(JsonKey.SUNBIRD).execute(userIdSelect);

			for (Row row : results) {
				validUserIds.add(row.getString(0).trim());
			}
			for (String uId : userIds) {
				if (!validUserIds.contains(uId)) {
					invalidUserIds.add(uId);
				}
			}
		} else {
			String url = "http://" + this.pidSvcIp + ":" + this.pidSvcPort + "/user/multi-fetch/wid";
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("source_fields", Arrays.asList("wid", "kid"));
			Map<String, Object> conditionMap = new HashMap<>();
			conditionMap.put("root_org", rootOrg);
			requestMap.put("conditions", conditionMap);
			requestMap.put("values", userIds);
			List<Map<String, Object>> resultFromPID = new ArrayList<>();
			try {
				resultFromPID = restTemplate.postForObject(url, requestMap, List.class);
			} catch (Exception e) {
				throw new InvalidDataInputException(e.getMessage());
			}
			if (!resultFromPID.isEmpty()) {
				for (Map<String, Object> data : resultFromPID) {
					if (data.get("wid") != null && !data.get("wid").toString().isEmpty()
							&& (data.get("kid") != null && !data.get("kid").toString().isEmpty())) {
						validUserIds.add(data.get("wid").toString());
					} else if (data.get("wid") != null && !data.get("wid").toString().isEmpty()
							&& (data.get("kid") == null || data.get("kid").toString().isEmpty())) {

						newUserIds.add(data.get("wid").toString());
					}
				}
				for (String uId : userIds) {
					if (!validUserIds.contains(uId) && !newUserIds.contains(uId)) {
						invalidUserIds.add(uId);
					}
				}
			} else {
				invalidUserIds.addAll(userIds);
			}
		}
		validityMap.put("valid_users", validUserIds);
		validityMap.put("invalid_users", invalidUserIds);
		validityMap.put("new_users", newUserIds);
		return validityMap;
	}
}
