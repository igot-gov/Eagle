/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.common.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.bodhi.repo.AppConfigRepository;
import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.common.util.ContentMetaConstants;
import com.infosys.lex.common.util.DatabaseProperties;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties props;

	@Autowired
	RestHighLevelClient client;
	
	@Autowired
	DatabaseProperties dbprops;

	@Autowired
	AppConfigRepository appConfigRepo;

	private final SimpleDateFormat expiryDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ContentService#getMetaByIDListandSource(java.util.
	 * List, java.lang.String[])
	 */
	@Override
	public List<Map<String, Object>> getMetaByIDListandSource(List<String> ids, String[] source, String status)
			throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids));

		if (status != null && !status.isEmpty()) {
			query.must(QueryBuilders.termQuery("status", status));
		}

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (source != null && source.length > 0)
			searchSourceBuilder.fetchSource(source, new String[] {});
		searchSourceBuilder.size(ids.size());
		searchRequest.source(searchSourceBuilder);
		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();

		List<Map<String, Object>> ret = new ArrayList<>();
		for (SearchHit hit : response) {
			ret.add(hit.getSourceAsMap());
		}
		return ret;
	}
	@Override
	public List<Map<String, Object>> getMetaByIDListandStatusList(List<String> ids, String[] source, String[] statusList)
			throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids));

		if (statusList != null && statusList.length != 0) {
			query.must(QueryBuilders.termsQuery("status", statusList));
		}

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (source != null && source.length > 0)
			searchSourceBuilder.fetchSource(source, new String[] {});
		searchSourceBuilder.size(ids.size());
		searchRequest.source(searchSourceBuilder);
		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();

		List<Map<String, Object>> ret = new ArrayList<>();
		for (SearchHit hit : response) {
			ret.add(hit.getSourceAsMap());
		}
		return ret;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMetaByIDListandSourceRest(List<String> ids, String[] source, String status)
			throws IOException {
		
		String contentHost = dbprops.getElasticIp();
		String contentPort = dbprops.getElasticPort();
		String query =  "{\r\n" + 
				"  \"size\": @size ,  \r\n" +
				"  \"_source\": [ \"@source\" ],   \r\n" + 
				"  \"query\": {\r\n" + 
				"    \"bool\": {\r\n" + 
				"      \"must\": [\r\n" + 
				"        {\r\n" + 
				"          \"terms\": {\r\n" + 
				"            \"identifier\": [ \"@ids\" ] \r\n" +  
				"          }\r\n" + 
				"        },\r\n" + 
				"		 {\r\n" + 
				"          \"term\": {\r\n" + 
				"            \"status\": {\r\n" + 
				"              \"value\": \"@status\"\r\n" + 
				"            }\r\n" + 
				"          }\r\n" + 
				"        }\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"}";
		
		int si = ids.size() ;
		String size = new Integer(si).toString(); 
		String str = String.join("\",\"", source);
		String idsStr = String.join("\",\"", ids);
		String username = dbprops.getElasticUser();
		String password = dbprops.getElasticPassword();
		query = query.replace("@size", size);
		query = query.replace("@source", str);
		query = query.replace("@ids",idsStr);
		query = query.replace("@status", status.toString());
         
		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> map = mapper.readValue(query,Map.class);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(map, headers);
//        Map<String,Object> response = (Map<String, Object>) restTemplate.exchange("http://elastic:Es$v2atc10@IPaddress:1201/mlsearch_*/_search", HttpMethod.POST,entity,Map.class);
        Map<String, Object> response = restTemplate.postForObject(
				"http://" + username + ":" + password + "@" + contentHost + ":" + contentPort + "/mlsearch_*/_search",
				entity, Map.class);
		Map<String,Object> hits = (Map<String, Object>) response.get("hits");
		List<Map<String,Object>> ret = (List<Map<String, Object>>) hits.get("hits");
//        System.out.println(ret);
//		System.out.println(query.toString());
//		Map<String,Object>hMap = restTemplate.getForObject("http://elastic:Es$v2atc10@IPaddress:1201/mlsearch_*/_search", Map.class);
		List<Map<String,Object>> retMeta = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> hit : ret) {
			retMeta.add((Map<String, Object>) hit.get("_source"));
		}
//		System.out.println(retMeta);
		return retMeta;
//		sample.add(hMap);
		

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ContentService#getMapFromContentStore(java.lang.
	 * String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapFromContentStore(String artifactUrl) {
		return restTemplate.getForObject(artifactUrl, Map.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ContentService#getContentStoreData(java.lang.String)
	 */
	@Override
	public String getContentStoreData(String url) throws Exception {
		try {

			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			return response.getBody();
		} catch (HttpStatusCodeException ex) {
			throw new ApplicationLogicError("Error in fetching solution Json!!", ex);
		}

	}

	@Override
	public String getKeyFromContentStore(String url) throws Exception {
		try {
			String strCompare = "content-store/";
			int index = url.indexOf(strCompare);
			if (index == -1)
				throw new ApplicationLogicError("Invalid exercise kry url");
			int startIndexOfLocation = index + strCompare.length();
			String location = url.substring(startIndexOfLocation);
			String urlEncodedLocation = location.replaceAll("/", "%2F");
			String contentHost = props.getContentServiceHost();
			String contentPort = props.getBodhiContentPort();

			String fetchUrl = "http://" + contentHost + ":" + contentPort + "/contentv3/download-assessment-key/"
					+ urlEncodedLocation;
			URI uri = URI.create(fetchUrl);

			ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

			return response.getBody();
		} catch (HttpStatusCodeException ex) {
			throw new ApplicationLogicError("Error in fetching solution Json!!", ex);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAssessmentKeyFromContentStore(String url) throws Exception {
		try {
//			System.out.println(url);
			String strCompare = "content-store/";
			int index = url.indexOf(strCompare);
			if (index == -1)
				throw new ApplicationLogicError("Invalid assessment key url");
			int startIndexOfLocation = index + strCompare.length();
			String location = url.substring(startIndexOfLocation);
			String urlEncodedLocation = location.replaceAll("/", "%2F");
			String contentHost = props.getContentServiceHost();
			String contentPort = props.getBodhiContentPort();

			String fetchUrl = "http://" + contentHost + ":" + contentPort + "/contentv3/download-assessment-key/"
					+ urlEncodedLocation;
//			System.out.println(fetchUrl);
			URI uri = URI.create(fetchUrl);

			return restTemplate.getForObject(uri, Map.class);

		} catch (HttpStatusCodeException ex) {
			throw new ApplicationLogicError("Error in fetching solution Json!!", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ContentService#insertFileInContentStore(java.io.
	 * File, java.lang.String, java.lang.String)
	 */
	@Override
	public String insertFileInContentStore(File f, String insertionUrl, String type) throws Exception {
		String url = "";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String contentHost = props.getContentServiceHost();
		String contentPort = props.getBodhiContentPort();
		// "http://" + contentHost + ":" + contentPort + "/content/Submissions/" +
		// contentId
		// creates folder or responds with 409(Conflict): Folder Exists
		HttpPost postRequest = new HttpPost("http://" + contentHost + ":" + contentPort + insertionUrl);
		HttpResponse resp = httpClient.execute(postRequest);
		int statuscode = resp.getStatusLine().getStatusCode();
		if (!(statuscode < 300 || statuscode == 409)) {
			throw new ApplicationLogicError(
					"Error creating submission folder. StatusCode :" + String.valueOf(statuscode));
		}
		postRequest.releaseConnection();
		// adds the file
		postRequest = new HttpPost("http://" + contentHost + ":" + contentPort + insertionUrl + "/" + type);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("content", f, ContentType.APPLICATION_OCTET_STREAM, f.getName());
		HttpEntity multipart = builder.build();

		postRequest.setEntity(multipart);

		resp = httpClient.execute(postRequest);
		BufferedReader br = new BufferedReader(new InputStreamReader((resp.getEntity().getContent())));
		statuscode = resp.getStatusLine().getStatusCode();
		if (statuscode >= 300) {

			throw new ApplicationLogicError(
					"Error inserting file in submission folder. StatusCode :" + String.valueOf(statuscode));
		}
		String output;
		// gets the url
		while ((output = br.readLine()) != null) {
			url = new ObjectMapper().readValue(output, Map.class).get("contentUrl").toString();
		}
		postRequest.releaseConnection();
		httpClient.close();
		return url;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public List<Map<String, Object>> searchMatchedData(String index, String type, Map<String, Object> searchData,
			List<String> sourceData, int size) throws IOException {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.size(size);
		if (sourceData != null && !sourceData.isEmpty())
			sourceBuilder.fetchSource(sourceData.toArray(new String[0]), null);
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		Iterator<Map.Entry<String, Object>> itr = searchData.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, Object> entry = itr.next();
			String keyValue = entry.getKey() + ".keyword";
			if (entry.getValue() instanceof List) {
				List<Object> values = (List<Object>) entry.getValue();
				BoolQueryBuilder inQuery = QueryBuilders.boolQuery();
				for (Object value : values) {
					inQuery.should(QueryBuilders.matchQuery(keyValue, value));
				}
				query.must(inQuery);
			} else if (entry.getValue().getClass().isArray()) {
				Object[] values = (Object[]) entry.getValue();
				BoolQueryBuilder inQuery = QueryBuilders.boolQuery();
				for (Object value : values) {
					inQuery.should(QueryBuilders.matchQuery(keyValue, value));
				}
				query.must(inQuery);
			} else {
				query.must(QueryBuilders.matchQuery(keyValue, entry.getValue()));
			}
		}
		sourceBuilder.query(query);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("en_mlsearch_v1");
		searchRequest.types("resource");

		searchRequest.source(sourceBuilder);

		SearchResponse sr = client.search(searchRequest, RequestOptions.DEFAULT);
		Iterator<SearchHit> it = sr.getHits().iterator();
		List<Map<String, Object>> lst = new ArrayList<>();
		while (it.hasNext()) {
			Map<String, Object> item = it.next().getSourceAsMap();
			lst.add(item);
		}
		return lst;
	}

	@Cacheable("mimeTypes")
	@Override
	public Map<String, String> getMimeTypes() {
		Map<String, String> ret = new HashMap<String, String>();
		String defaultRootOrg = JsonKey.DEFAULT_ROOTORG;
		List<Map<String, Object>> properties = appConfigRepo
				.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(defaultRootOrg, Arrays.asList("page", "time", "result"));
		for (Map<String, Object> prop : properties)
			ret.put(prop.get("key").toString().toLowerCase(), prop.get("value").toString().toLowerCase());
		return ret;
	}

	@Cacheable("macConfig")
	@Override
	public Map<String, String> getMACConfiguration(String rootOrg) {
		Map<String, String> ret = new HashMap<String, String>();

		List<Map<String, Object>> properties = appConfigRepo.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(rootOrg,
				Arrays.asList("mac_for_external", "mac_for_instructor_led", "mac_for_source_short_name"));
		for (Map<String, Object> prop : properties)
			ret.put(prop.get("key").toString().toLowerCase(), prop.get("value").toString().toLowerCase());
		return ret;
	}

	/**
	 * Fetch content meta only if fields exists
	 * 
	 * @param ids
	 * @param source
	 * @param status
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<Map<String, Object>> getMetaByIDListandSourceIfSourceFieldsExists(List<String> ids, String[] source,
			String[] fields, String status) throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids));
		// fields in exists query
		for (String field : fields)
			query.must(QueryBuilders.existsQuery(field));

		if (status != null && !status.isEmpty()) {
			query.must(QueryBuilders.termQuery("status", status));
		}

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (source != null && source.length > 0)
			searchSourceBuilder.fetchSource(source, new String[] {});
		searchSourceBuilder.size(ids.size());
		searchRequest.source(searchSourceBuilder);

		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();

		List<Map<String, Object>> ret = new ArrayList<>();
		for (SearchHit hit : response) {
			ret.add(hit.getSourceAsMap());
		}
		return ret;
	}

	/**
	 * Checks whether the content Id exists for given status
	 * 
	 * @param contentId
	 * @param statusList
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@Override
	public boolean validateContentIdToShow(String contentId) throws IOException, ParseException {
		boolean isExists = false;
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");
		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", contentId));

		query.must(QueryBuilders.termsQuery("status", ContentMetaConstants.LIVE_CONTENT_STATUS));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);

		searchSourceBuilder.fetchSource(new String[] { "identifier", "expiryDate" }, new String[] {});
		searchSourceBuilder.size(1);
		searchRequest.source(searchSourceBuilder);
		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();
		Map<String, Object> sourceMap;
		if (response.totalHits > 0) {
			for (SearchHit hit : response) {
				sourceMap = hit.getSourceAsMap();
				if (sourceMap.containsKey("expiryDate") && (sourceMap.get("expiryDate") == null
						|| sourceMap.get("expiryDate").toString().trim().isEmpty())) {
					isExists = true;
				} else if (sourceMap.containsKey("expiryDate") && sourceMap.get("expiryDate") != null
						&& !sourceMap.get("expiryDate").toString().isEmpty()) {
					Date expiryDate = expiryDateFormat.parse(sourceMap.get("expiryDate").toString());
					if (!expiryDate.before(new Date())) {
						isExists = true;
					}

				}
			}
		}

		return isExists;
	}

	/**
	 * This method filters the contents that are to be shown in UI( Live content).
substitute url based on requirement
	 * empty then all the meta is returned
	 */
	@Override
	public Map<String, Map<String, Object>> filterAndFetchContentMetaToShow(List<String> contentIds,
			Set<String> sourceFields) throws IOException, ParseException {
		SearchRequest searchRequest = new SearchRequest();
		Map<String, Map<String, Object>> filteredMetaMap = new HashMap<>();
		String contentId;
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", contentIds));

		query.must(QueryBuilders.termsQuery("status", ContentMetaConstants.LIVE_CONTENT_STATUS));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (sourceFields != null && !sourceFields.isEmpty()) {
			sourceFields.add("identifier");
			sourceFields.add("expiryDate");
			searchSourceBuilder.fetchSource(sourceFields.toArray(new String[] {}), new String[] {});
		}
		searchSourceBuilder.size(contentIds.size());
		searchRequest.source(searchSourceBuilder);
		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();
		Map<String, Object> sourceMap;
		if (response.totalHits > 0) {
			for (SearchHit hit : response) {
				sourceMap = hit.getSourceAsMap();
				contentId = sourceMap.get("identifier").toString();

				if (sourceMap.containsKey("expiryDate") && (sourceMap.get("expiryDate") == null
						|| sourceMap.get("expiryDate").toString().trim().isEmpty())) {
					filteredMetaMap.put(contentId, sourceMap);

				} else if (sourceMap.containsKey("expiryDate") && sourceMap.get("expiryDate") != null
						&& !sourceMap.get("expiryDate").toString().isEmpty()) {
					Date expiryDate = expiryDateFormat.parse(sourceMap.get("expiryDate").toString());
					// expiry date is after todays date
					if (!expiryDate.before(new Date())) {
						filteredMetaMap.put(contentId, sourceMap);
					}

				}
			}
		}

		return filteredMetaMap;
	}

}