///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
//
//*/
package com.infosys.lex.ext.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.ext.Models.Cassandra.AppConfig;
import com.infosys.lex.ext.Models.Cassandra.AppConfigPrimaryKey;
import com.infosys.lex.ext.ServiceRepository.bodhi.AppConfigRepository;
import com.infosys.lex.ext.exception.ApplicationLogicError;
import com.infosys.lex.ext.service.UserUtilityService;
import com.infosys.lex.ext.util.LexConstants;
import com.infosys.lex.ext.util.LexServerProperties;

@Service
public class UserUtilityServiceImpl implements UserUtilityService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	AppConfigRepository appConfigRepo;

	@Autowired
	LexServerProperties serverConfig;

	@Autowired
	RestHighLevelClient client;

	@Override
	public String getUserDataSource(String rootOrg) {
		String dataSource = null;
		AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "user_data_source")).orElse(null);
		if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
			dataSource = config.getValue();
		}
		return dataSource;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean validateUser(String rootOrg, String userId) throws Exception {

		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		pidRequestMap.put("source_fields", Arrays.asList("wid", "root_org"));
		Map<String, String> conditions = new HashMap<String, String>();
		conditions.put("root_org", rootOrg);
		conditions.put("wid", userId);
		pidRequestMap.put("conditions", conditions);

		try {

			List<Map<String, Object>> pidResponse = restTemplate.postForObject(
					"http://" + serverConfig.getPidIp() + ":" + serverConfig.getPidPort().toString() + "/user",
					pidRequestMap, List.class);

			if (pidResponse.isEmpty() || pidResponse == null) {
				return false;
			} else {
				return (pidResponse.get(0).get("wid").equals(userId));
			}

		} catch (Exception e) {
			throw new ApplicationLogicError("PID ERROR: " + e.getMessage());
		}

	}

	@Override
	public Map<String, Object> getUserIdForEmail(String rootOrg, List<String> email, List<String> source) {

		Map<String, Object> result = new HashMap<>();

		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		if (!source.contains("wid"))
			source.add("wid");
		if (!source.contains("email")) {
			source.add("email");
		}
		pidRequestMap.put("source_fields", source);
		pidRequestMap.put("values", email);
		Map<String, String> conditions = new HashMap<String, String>();
		conditions.put("root_org", rootOrg);
		pidRequestMap.put("conditions", conditions);

		try {

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp() + ":"
					+ serverConfig.getPidPort().toString() + "/user/multi-fetch/email", pidRequestMap, List.class);

			if (pidResponse.isEmpty() || pidResponse == null) {
				return null;
			}

			for (Map<String, Object> record : pidResponse) {
				if (record.get("email") != null && email.contains(record.get("email"))) {
					result.put(record.get("email").toString(), record.get("wid"));
				}
			}
			return result;

		} catch (Exception e) {
			throw new ApplicationLogicError("PidResponse.get(0).get(\"wid\")ID ERROR: " + e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUsersDataFromUserIds(String rootOrg, List<String> userIds, List<String> source) {

		Map<String, Object> result = new HashMap<>();

		List<String> sources = new ArrayList<>(source);

		if (!sources.contains("wid")) {
			sources.add("wid");
		}
		Map<String, Object> pidRequestMap = new HashMap<String, Object>();
		pidRequestMap.put("source_fields", sources);
		pidRequestMap.put("values", userIds);
		Map<String, String> conditions = new HashMap<String, String>();
		conditions.put("root_org", rootOrg);
		pidRequestMap.put("conditions", conditions);

		try

		{

			List<Map<String, Object>> pidResponse = restTemplate.postForObject("http://" + serverConfig.getPidIp() + ":"
					+ serverConfig.getPidPort().toString() + "/user/multi-fetch/wid", pidRequestMap, List.class);

			for (Map<String, Object> record : pidResponse) {
				if (record.get("wid") != null && userIds.contains(record.get("wid"))) {
					result.put(record.get("wid").toString(), record);
				}
			}

		} catch (Exception e) {
			throw new ApplicationLogicError("PidResponse.get(0).get(\"wid\")ID ERROR: " + e.getMessage());
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> getContentMeta(List<String> ids, List<String> source, List<String> statusList)
			throws ApplicationLogicError, IOException {

		List<Map<String, Object>> requiredContentMeta = new ArrayList<>();

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(LexConstants.CONTENT_SEARCH_INDEX);
		searchRequest.types(LexConstants.CONTENT_RESOURCE_TYPE);

		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids));

		if (statusList != null && statusList.size() != 0)
			query.must(QueryBuilders.termsQuery("status", statusList));

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (source != null && source.size() > 0)
			searchSourceBuilder.fetchSource(source.toArray(new String[0]), new String[] {});
		searchSourceBuilder.size(ids.size());
		searchRequest.source(searchSourceBuilder);
		SearchHits response = client.search(searchRequest, RequestOptions.DEFAULT).getHits();

		for (SearchHit hit : response) {
			requiredContentMeta.add(hit.getSourceAsMap());
		}

		return requiredContentMeta;
	}

}