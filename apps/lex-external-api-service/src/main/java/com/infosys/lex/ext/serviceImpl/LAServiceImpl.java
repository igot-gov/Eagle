package com.infosys.lex.ext.serviceImpl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.sunbird.common.models.util.ProjectLogger;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.ext.ServiceRepository.bodhi.AssessmentRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.CommonLearningGoalsRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.ContentProgressRepo;
import com.infosys.lex.ext.ServiceRepository.bodhi.LeaderBoardRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.SharedPlayListRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserBadgeRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserLearningGoalsRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserPlayListRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserSharedGoalsRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserTermsAndConditionRepository;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserTopicsMappingRepository;
import com.infosys.lex.ext.exception.ApplicationLogicError;
import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.service.LAService;
import com.infosys.lex.ext.service.UserUtilityService;
import com.infosys.lex.ext.util.LexConstants;
import com.infosys.lex.ext.util.LexServerProperties;
import com.infosys.lex.ext.util.ProjectCommonUtil;

@Service
public class LAServiceImpl implements LAService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	UserUtilityService userUtilityService;

	@Autowired
	SharedPlayListRepository sharedPlayListRepository;

	@Autowired
	UserPlayListRepository userPlayListRepository;

	@Autowired
	AssessmentRepository assessmentRepository;

	@Autowired
	CommonLearningGoalsRepository commonLearningGoalsRepository;

	@Autowired
	UserLearningGoalsRepository userLearningGoalsRepository;

	@Autowired
	UserSharedGoalsRepository userSharedGoalsRepository;

	@Autowired
	UserBadgeRepository userBadgeRepository;

	@Autowired
	UserTermsAndConditionRepository userTermsAndConditionRepository;

	@Autowired
	LeaderBoardRepository leaderBoardRepository;

	@Autowired
	UserTopicsMappingRepository userTopicMappingRepository;

	@Autowired
	ContentProgressRepo contentProgressRepo;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	RestHighLevelClient esClient;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static final int scrollStateTime = 300000;

	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Override
	public Map<String, Object> getTags() throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		ResponseEntity<Object> responseEntity = null;
		try {
			responseEntity = restTemplate.getForEntity(getTagsUrl(), Object.class);
			responseMap.put(LexConstants.RESULT, responseEntity.getBody());
		} catch (HttpServerErrorException httpServerErrorException) {
			ProjectLogger.log("Error : " + httpServerErrorException.getMessage(), httpServerErrorException);
			throw new Exception("Content Service Rest Call Exception");
		} catch (Exception exception) {
			exception.printStackTrace();
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getFeedBack(String rootOrg, String pageSequence, String pageSize, String startDate,
			String endDate) throws Exception {

		ProjectCommonUtil.elasticSearchPageSizeValidations(pageSize);
		Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
		Date eDate = startAndEndDatesMap.get("eDate");
		Date sDate = startAndEndDatesMap.get("sDate");
		if (pageSequence.equals("-1"))
			throw new BadRequestException("recordLink -1 marks the halt of data");

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);
		String url = "http://" + lexServerProps.getLexCoreSerivce() + "/v1/feedback/dump?startDate=" + sDate.getTime()
				+ "&endDate=" + eDate.getTime() + "&size=" + pageSize;

		if (!pageSequence.equals("0")) {
			url += "&scrollId=" + pageSequence;
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		if (!responseMap.containsKey("scrollId")) {
			responseMap.put("scrollId", "-1");
		}

		return responseMap;
	}

	@Override
	public Map<String, Object> getCatalog(String rootOrg, String pageSequence, String pageSize) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.elasticSearchPageSizeValidations(pageSize);
			List<Object> esData = new ArrayList<>();
			SearchResponse response = null;

			if (pageSequence.equals("0")) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(LexConstants.LEX_CONTENT_INDEX).types(LexConstants.LEX_RESOURCE_TYPE)
						.scroll(new TimeValue(scrollStateTime));
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				searchSourceBuilder.query(QueryBuilders.termQuery(LexConstants.ROOT_ORG, rootOrg));
				searchSourceBuilder.size(Integer.parseInt(pageSize));
				searchRequest.source(searchSourceBuilder);
				response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			} else {
				SearchScrollRequest scrollRequest = new SearchScrollRequest(pageSequence);
				scrollRequest.scroll(new TimeValue(scrollStateTime));
				response = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
			}
			if (response.getHits().getHits().length > 0
					&& response.getHits().getHits().length == Integer.parseInt(pageSize)) {
				responseMap.put("nextRecordState", response.getScrollId());
			} else {
				responseMap.put("nextRecordState", "-1");
			}
			for (SearchHit searchHit : response.getHits()) {
				Map<String, Object> source = searchHit.getSourceAsMap();
				esData.add(source);
			}
			responseMap.put(LexConstants.RESULT, esData);
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getCatalogv2(String rootOrg, String pageSequence, String pageSize, String identifier)
			throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.elasticSearchPageSizeValidations(pageSize);
			List<Object> esData = new ArrayList<>();
			SearchResponse response = null;

			/*
			 * Copyright 2020 the original author or authors.
			 *
			 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
			 * use this file except in compliance with the License. You may obtain a copy of
			 * the License at
			 *
			 * http://www.apache.org/licenses/LICENSE-2.0
			 *
			 * Unless required by applicable law or agreed to in writing, software
			 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
			 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
			 * License for the specific language governing permissions and limitations under
			 * the License.
			 */

			if (pageSequence.equals("0")) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(LexConstants.CONTENT_SEARCH_INDEX).types(LexConstants.CONTENT_RESOURCE_TYPE)
						.scroll(new TimeValue(scrollStateTime));
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				searchSourceBuilder.query(QueryBuilders.termQuery(LexConstants.ROOT_ORG, rootOrg));
				if (identifier != null && !identifier.isEmpty()) {
					String[] ids = identifier.split(",");
					searchSourceBuilder.query(QueryBuilders.termsQuery(LexConstants.IDENTIFIER, ids));

				}
				searchSourceBuilder.size(Integer.parseInt(pageSize));
				searchRequest.source(searchSourceBuilder);
				response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			} else {
				SearchScrollRequest scrollRequest = new SearchScrollRequest(pageSequence);
				scrollRequest.scroll(new TimeValue(scrollStateTime));
				response = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
			}
			if (response.getHits().getHits().length > 0
					&& response.getHits().getHits().length == Integer.parseInt(pageSize)) {
				responseMap.put("nextRecordState", response.getScrollId());
			} else {
				responseMap.put("nextRecordState", "-1");
			}
			for (SearchHit searchHit : response.getHits()) {
				Map<String, Object> source = searchHit.getSourceAsMap();
				esData.add(source);
			}
			responseMap.put(LexConstants.RESULT, esData);
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getCourseCatalog(String rootOrg, String pageSequence, String pageSize) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.elasticSearchPageSizeValidations(pageSize);
			List<Object> esData = new ArrayList<>();
			SearchResponse response = null;
			if (pageSequence.equals("0")) {
				SearchRequest searchRequest = new SearchRequest();
				searchRequest.indices(LexConstants.CONTENT_SEARCH_INDEX).types(LexConstants.CONTENT_RESOURCE_TYPE)
						.scroll(new TimeValue(scrollStateTime));

				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				searchSourceBuilder
						.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rootOrg", rootOrg))
								.must(QueryBuilders.termQuery("contentType.keyword", "Course")))
						.size(Integer.parseInt(pageSize));
				response = esClient.search(searchRequest, RequestOptions.DEFAULT);
			} else {
				SearchScrollRequest scrollRequest = new SearchScrollRequest(pageSequence);
				scrollRequest.scroll(pageSequence);
				response = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
			}
			if (response.getHits().getHits().length > 0
					&& response.getHits().getHits().length == Integer.parseInt(pageSize)) {
				responseMap.put("nextRecordState", response.getScrollId());
			} else {
				responseMap.put("nextRecordState", "-1");
			}
			for (SearchHit searchHit : response.getHits()) {
				Map<String, Object> source = searchHit.getSourceAsMap();
				esData.add(source);
			}
			responseMap.put(LexConstants.RESULT, esData);
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getAssessmentDetails(String rootOrg, String pageSize, String pagingSequence,
			String startDate, String endDate) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
			Date eDate = startAndEndDatesMap.get("eDate");
			Date sDate = startAndEndDatesMap.get("sDate");
			List<Date> filterDates = ProjectCommonUtil.getDatesBetweenStartAndEndDate(sDate, eDate);
			Map<String, Object> result = assessmentRepository.fetchAssessments(rootOrg, filterDates,
					Integer.parseInt(pageSize), pagingSequence, sDate, eDate);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("dateCreated", convertDate(row, "date_created"));
				resultMap.put("timeStampCreated", convertDate(row, "ts_created"));
				resultMap.put("parentSourceId", row.getObject("parent_content_id"));
				resultMap.put("resultPercent", row.getObject("result_percent"));
				resultMap.put("id", row.getObject("id"));
				resultMap.put("passPercent", row.getObject("pass_percent"));
				resultMap.put("sourceId", row.getObject("content_id"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getUserProgress(String rootOrg, String pagingSequence, String pageSize, String startDate,
			String endDate) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Date> startAndEndDatesMap = getStartAndEndDateFromInput(startDate, endDate);
			Date eDate = startAndEndDatesMap.get("eDate");
			Date sDate = startAndEndDatesMap.get("sDate");
			List<Date> filterDates = ProjectCommonUtil.getDatesBetweenStartAndEndDate(sDate, eDate);
			Map<String, Object> result = contentProgressRepo.fetchProgress(rootOrg, Integer.valueOf(pageSize),
					pagingSequence, filterDates);

			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("dateUpdated", row.getObject("date_updated"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("contentType", row.getObject("content_type"));
				resultMap.put("contentId", row.getObject("content_id"));
				resultMap.put("firstAccessedOn", convertDate(row, "first_accessed_on"));
				resultMap.put("firstCompletedOn", convertDate(row, "first_completed_on"));
				resultMap.put("lastAccessedOn", convertDate(row, "last_accessed_on"));
				resultMap.put("lastTs", convertDate(row, "last_ts"));
				resultMap.put("progress", row.getObject("progress"));
				resultMap.put("updatedBy", row.getObject("updated_by"));

				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			exception.printStackTrace();
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getSharedPlayLists(String rootOrg, String pageSize, String pagingSequence,
			String startDate, String endDate, String dumpType) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			ProjectCommonUtil.dumpTypeValidations(dumpType, startDate, endDate);
			Date eDate = new Date();
			Date sDate = new Date();
			if (dumpType.equals(LexConstants.DUMP_TYPE_INCREMENTAL)) {
				Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
				eDate = startAndEndDatesMap.get("eDate");
				sDate = startAndEndDatesMap.get("sDate");
			}
			Map<String, Object> result = sharedPlayListRepository.getSharedPlayLists(rootOrg,
					Integer.parseInt(pageSize), pagingSequence, sDate, eDate, dumpType);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("sharedWith", row.getObject("shared_with"));
				resultMap.put("playListId", row.getObject("playlist_id"));
				resultMap.put("playListTitle", row.getObject("playlist_title"));
				resultMap.put("resourceIds", row.getObject("resource_ids"));
				resultMap.put("sharedBy", row.getObject("shared_by"));
				resultMap.put("sharedOn", convertDate(row, "shared_on"));
				resultMap.put("visibility", row.getObject("visibility"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getPlayList(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate, String dumpType) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			ProjectCommonUtil.dumpTypeValidations(dumpType, startDate, endDate);
			Date eDate = new Date();
			Date sDate = new Date();
			if (dumpType.equals(LexConstants.DUMP_TYPE_INCREMENTAL)) {
				Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
				eDate = startAndEndDatesMap.get("eDate");
				sDate = startAndEndDatesMap.get("sDate");
			}
			Map<String, Object> result = userPlayListRepository.getUserPlayList(rootOrg, Integer.parseInt(pageSize),
					pagingSequence, sDate, eDate, dumpType);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("playListId", row.getObject("playlist_id"));
				resultMap.put("createdOn", convertDate(row, "created_on"));
				resultMap.put("isShared", row.getObject("isshared"));
				resultMap.put("lastUpdatedOn", convertDate(row, "last_updated_on"));
				resultMap.put("playListTitle", row.getObject("playlist_title"));
				resultMap.put("resourceIds", row.getObject("resource_ids"));
				resultMap.put("sharedBy", row.getObject("shared_by"));
				resultMap.put("sourcePlayListId", row.getObject("source_playlist_id"));
				resultMap.put("visibility", row.getObject("visibility"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			exception.printStackTrace();
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getUserSharedGoals(String rootOrg, String pageSize, String pagingSequence,
			String startDate, String endDate, String dumpType) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			ProjectCommonUtil.dumpTypeValidations(dumpType, startDate, endDate);
			Date eDate = new Date();
			Date sDate = new Date();
			if (dumpType.equals(LexConstants.DUMP_TYPE_INCREMENTAL)) {
				Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
				eDate = startAndEndDatesMap.get("eDate");
				sDate = startAndEndDatesMap.get("sDate");
			}
			Map<String, Object> result = userSharedGoalsRepository.getUserSharedGoals(rootOrg,
					Integer.parseInt(pageSize), pagingSequence, sDate, eDate, dumpType);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("sharedWith", row.getObject("shared_with"));
				resultMap.put("goalType", row.getObject("goal_type"));
				resultMap.put("goalId", row.getObject("goal_id"));
				resultMap.put("sharedBy", row.getObject("shared_by"));
				resultMap.put("goalContentId", row.getObject("goal_content_id"));
				resultMap.put("goalDescription", row.getObject("goal_desc"));
				resultMap.put("goalDuration", row.getObject("goal_duration"));
				resultMap.put("goalStartDate", convertDate(row, "goal_start_date"));
				resultMap.put("goalEndDate", convertDate(row, "goal_end_date"));
				resultMap.put("goalTitle", row.getObject("goal_title"));
				resultMap.put("lastUpdatedOn", convertDate(row, "last_updated_on"));
				resultMap.put("sharedOn", convertDate(row, "shared_on"));
				resultMap.put("status", row.getObject("status"));
				resultMap.put("statusMessage", row.getObject("status_message"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getUserLearningGoals(String rootOrg, String pageSize, String pagingSequence,
			String startDate, String endDate, String dumpType) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			ProjectCommonUtil.dumpTypeValidations(dumpType, startDate, endDate);
			Date eDate = new Date();
			Date sDate = new Date();
			if (dumpType.equals(LexConstants.DUMP_TYPE_INCREMENTAL)) {
				Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
				eDate = startAndEndDatesMap.get("eDate");
				sDate = startAndEndDatesMap.get("sDate");
			}
			Map<String, Object> result = userLearningGoalsRepository.getUserLearningGoals(rootOrg,
					Integer.parseInt(pageSize), pagingSequence, sDate, eDate, dumpType);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("goalType", row.getObject("goal_type"));
				resultMap.put("goalId", row.getObject("goal_id"));
				resultMap.put("createdOn", convertDate(row, "created_on"));
				resultMap.put("goalContentId", row.getObject("goal_content_id"));
				resultMap.put("goalDuration", row.getObject("goal_duration"));
				resultMap.put("goalStartDate", convertDate(row, "goal_start_date"));
				resultMap.put("goalEndDate", convertDate(row, "goal_end_date"));
				resultMap.put("goalTitle", row.getObject("goal_title"));
				resultMap.put("lastUpdatedOn", convertDate(row, "last_updated_on"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getMasterInterests(String rootOrg) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		String url = "http://" + lexServerProps.getLexCoreSerivce() + "/v1/interests/org";

		HttpHeaders headers = new HttpHeaders();
		headers.set("rootOrg", rootOrg);

		try {
			ResponseEntity<List> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), List.class);
			responseMap.put("result", responseEntity.getBody());
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}

		return responseMap;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getCommonLearningGoals(String rootOrg) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();

		String url = "http://" + lexServerProps.getLexCoreSerivce() + "/v1/common-goals?rootOrg=" + rootOrg;

		try {
			ResponseEntity<List> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, List.class);
			responseMap.put("result", responseEntity.getBody());
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getInterests(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
			Date eDate = startAndEndDatesMap.get("eDate");
			Date sDate = startAndEndDatesMap.get("sDate");
			Map<String, Object> result = userTopicMappingRepository.getInterests(rootOrg, Integer.parseInt(pageSize),
					pagingSequence, sDate, eDate);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				Calendar dateModified = Calendar.getInstance();
				resultMap.put("rootOrg", row.getObject("root_org"));
				dateModified.setTime((Date) (row.getObject("updated_on")));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("dateModified", convertDate(row, "updated_on"));
				resultMap.put("topics", row.getObject("interest"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getBadges(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
			Date eDate = startAndEndDatesMap.get("eDate");
			Date sDate = startAndEndDatesMap.get("sDate");
			Map<String, Object> result = userBadgeRepository.getBadges(rootOrg, Integer.parseInt(pageSize),
					pagingSequence, sDate, eDate);
			ResultSet resultSet = (ResultSet) result.get("result");
			Integer currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("badgeId", row.getObject("badge_id"));
				resultMap.put("badgeType", row.getObject("badge_type"));
				resultMap.put("firstRecievedDate", convertDate(row, "first_received_date"));
				resultMap.put("lastRecievedDate", convertDate(row, "last_received_date"));
				resultMap.put("progress", row.getObject("progress"));
				resultMap.put("recievedCount", row.getObject("received_count"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getLeaderBoardsData(String rootOrg, String pageSize, String pagingSequence,
			int leaderBoardYear, String durationType, int durationValue, String leaderBoardType) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Object> result = leaderBoardRepository.getLeaderBoardsData(rootOrg, Integer.parseInt(pageSize),
					pagingSequence, leaderBoardYear, durationType, durationValue, leaderBoardType);
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			ResultSet resultSet = (ResultSet) result.get("result");
			int currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("points", row.getObject("points"));
				resultMap.put("rank", row.getObject("rank"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getUserTermsAndCondtion(String rootOrg, String pageSize, String pageSequence)
			throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Object> result = userTermsAndConditionRepository.getUserTermsAndCondtion(rootOrg,
					Integer.parseInt(pageSize), pageSequence);
			List<Map<String, Object>> resultMaps = new ArrayList<>();
			int currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			ResultSet resultSet = (ResultSet) result.get("result");
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("rootOrg", row.getObject("root_org"));
				resultMap.put("userId", row.getObject("user_id"));
				resultMap.put("version", row.getObject("version"));
				resultMap.put("dateAccepted", convertDate(row, "accepted_on"));
				resultMap.put("language", row.getObject("language"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getUserTermsAndCondtionV2(String pageSize, String pageSequence, String startDate,
			String endDate) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.cassandraPageSizeValidations(pageSize);
			Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDate, endDate);
			Date eDate = startAndEndDatesMap.get("eDate");
			Date sDate = startAndEndDatesMap.get("sDate");
			Map<String, Object> result = userTermsAndConditionRepository
					.getUserTermsAndCondtionV2(Integer.parseInt(pageSize), pageSequence, sDate, eDate);

			List<Map<String, Object>> resultMaps = new ArrayList<>();
			int currentlyFetched = Integer.parseInt(result.get("currentlyFetched").toString());
			ResultSet resultSet = (ResultSet) result.get("result");
			for (Row row : resultSet) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("emailId", row.getObject("email"));
				resultMap.put("version", row.getObject("version"));
				resultMap.put("dateAccepted", convertDate(row, "date_accepted"));
				resultMaps.add(resultMap);
				if (--currentlyFetched == 0) {
					break;
				}
			}
			responseMap.put(LexConstants.RESULT, resultMaps);
			responseMap.put(LexConstants.NEXT_RECORD_STATE, result.get("nextRecord").toString());
		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	// Util Functions

	private Map<String, Date> getStartAndEndDateTimeFromInput(String startDate, String endDate) throws Exception {
		Map<String, Date> startAndEndDateMap = new HashMap<>();
		Date sDate = new Date();
		Date eDate = new Date();
		if (!startDate.equals("0") && !endDate.equals("0")) {
			sDate = getDateAndTimeFromString(startDate);
			eDate = getDateAndTimeFromString(endDate);
			dateValidations(sDate, eDate);
		} else {
			Map<String, Date> defaultDatesMap = getDefaultStartAndEndDate();
			sDate = defaultDatesMap.get("sDate");
			eDate = defaultDatesMap.get("eDate");
		}
		startAndEndDateMap.put("sDate", sDate);
		startAndEndDateMap.put("eDate", eDate);
		return startAndEndDateMap;
	}

	private Map<String, Date> getStartAndEndDateFromInput(String startDate, String endDate) throws Exception {
		Map<String, Date> startAndEndDateMap = new HashMap<>();
		Date sDate = new Date();
		Date eDate = new Date();
		if (!startDate.equals("0") && !endDate.equals("0")) {
			sDate = getDateFromString(startDate);
			eDate = getDateFromString(endDate);
			dateValidations(sDate, eDate);
		} else {
			Map<String, Date> defaultDatesMap = getDefaultStartAndEndDate();
			sDate = defaultDatesMap.get("sDate");
			eDate = defaultDatesMap.get("eDate");
		}
		startAndEndDateMap.put("sDate", sDate);
		startAndEndDateMap.put("eDate", eDate);
		return startAndEndDateMap;
	}

	private String getTagsUrl() {
		String contentServiceUrl = lexServerProps.getContentServiceUrl();
		return contentServiceUrl + "/content/DEFAULTS/JSONS/tags.json?type=artifacts";
	}

	private Map<String, Date> getDefaultStartAndEndDate() throws ParseException {
		Map<String, Date> defaultDatesMap = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(UserDataUtilImpl.inputFormatterDate.parse(UserDataUtilImpl.inputFormatterDate.format(new Date())));
		cal.add(Calendar.HOUR, 00);
		cal.add(Calendar.MINUTE, 00);
		cal.add(Calendar.SECOND, 00);
		defaultDatesMap.put("eDate", cal.getTime());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		defaultDatesMap.put("sDate", cal.getTime());
		return defaultDatesMap;
	}

	private String convertDate(Row row, String rowParameter) {
		if (row.getObject(rowParameter) == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) (row.getObject(rowParameter)));
		return UserDataUtilImpl.formatterDate.format(calendar.getTime());

	}

	private Date getDateFromString(String dateString) {

		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(UserDataUtilImpl.formatter.parse(dateString));
		} catch (ParseException parseException) {
			throw new BadRequestException("Cannot parse input date. Please Provide Date in the format- YYYY-MM-DD");
		}
		return calendar.getTime();
	}

	private Date getDateAndTimeFromString(String dateString) {

		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(UserDataUtilImpl.inputFormatterDate.parse(dateString));
		} catch (ParseException parseException) {
			throw new BadRequestException(
					"Cannot parse input date. Please Provide Date in the format- YYYY-MM-DD HH:MM:SS");
		}
		return calendar.getTime();
	}

	private void dateValidations(Date startDate, Date endDate) {
		if (startDate.after(endDate)) {
			throw new BadRequestException("The start date should be smaller than the end date.");
		}
		if (endDate.after(Calendar.getInstance().getTime())) {
			throw new BadRequestException("End Date cannot be greater than todays's Date.");
		}
		if ((endDate.getTime() - startDate.getTime()) / (1000f * 60 * 60 * 24) > 5) {
			throw new BadRequestException("The difference in dates should be smaller than 5 days.");
		}
	}

	private void dateValidationsSocial(Date startDate, Date endDate) {
		if (startDate.after(endDate)) {
			throw new BadRequestException("The start date should be smaller than the end date.");
		}
		if ((endDate.getTime() - startDate.getTime()) / (1000f * 60 * 60 * 24) > 5) {
			throw new BadRequestException("The difference in dates should be smaller than 5 days.");
		}
	}

	@Override
	public Map<String, Object> getAllAssignments(String rootOrg, String startDateTimeStr, String endDateTimeStr,
			Integer prevPageNo, Integer pageSize) throws Exception {
		String url = "http://" + lexServerProps.getLexCoreSerivce() + "/v1/content-assignments";
		Map<String, Date> startAndEndDatesMap = getStartAndEndDateTimeFromInput(startDateTimeStr, endDateTimeStr);
		Date endDateTime = startAndEndDatesMap.get("eDate");
		Date startDateTime = startAndEndDatesMap.get("sDate");
		ProjectCommonUtil.postgresPageSizeValidations(pageSize);

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		builder.queryParam("prevPageNo", prevPageNo);
		builder.queryParam("pageSize", pageSize);

		String uriString = builder.toUriString();
		uriString += "&startDateTime=" + UserDataUtilImpl.inputFormatterDate.format(startDateTime);
		uriString += "&endDateTime=" + UserDataUtilImpl.inputFormatterDate.format(endDateTime);

		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(uriString, HttpMethod.GET, entity, String.class);

		Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});
		return responseMap;

	}

	@Override
	public Map<String, Object> getUsersAssignments(String rootOrg, String startDateStr, String endDateStr,
			String pageState, Integer pageSize) throws Exception {
		String url = "http://" + lexServerProps.getLexCoreSerivce() + "/v1/users/content-assignments";
		Map<String, Date> startAndEndDatesMap = getStartAndEndDateFromInput(startDateStr, endDateStr);
		Date endDate = startAndEndDatesMap.get("eDate");
		Date startDate = startAndEndDatesMap.get("sDate");
		ProjectCommonUtil.cassandraPageSizeValidations(pageSize.toString());

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

		builder.queryParam("startDate", UserDataUtilImpl.formatter.format(startDate));
		builder.queryParam("endDate", UserDataUtilImpl.formatter.format(endDate));
		builder.queryParam("pageState", pageState);

		builder.queryParam("pageSize", pageSize);

		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
				String.class);

		Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});
		return responseMap;

	}

	@Override
	public Map<String, Object> getSocialPosts(String rootOrg, String recordLink, String startDateTime,
			String endDateTime, String pageSize, List<String> postStatus) throws Exception {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ProjectCommonUtil.elasticSearchPageSizeValidations(pageSize);
			List<Object> esData = new ArrayList<>();
			SearchResponse response = null;

			if (!startDateTime.equals("0") && !endDateTime.equals("0")) {
				dateValidationsSocial(inputFormatterDateTime.parse(startDateTime),
						inputFormatterDateTime.parse(endDateTime));

				if (recordLink.equals("0")) {
					SearchRequest searchRequest = new SearchRequest();
					searchRequest.indices(LexConstants.SOCIAL_POST_INDEX).types(LexConstants.SOCIAL_POST_TYPE)
							.scroll(new TimeValue(scrollStateTime));

					SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
					searchSourceBuilder
							.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("rootOrg", rootOrg))
									.must(QueryBuilders.termsQuery("status", postStatus))
									.must(QueryBuilders.boolQuery().should(QueryBuilders.nestedQuery("lastEdited",
											QueryBuilders.rangeQuery("lastEdited.dtLastEdited")
													.from(inputFormatterDateTime.parse(startDateTime))
													.to(inputFormatterDateTime.parse(endDateTime)),
											ScoreMode.None))
											.should(QueryBuilders.rangeQuery("dtPublished")
													.from(inputFormatterDateTime.parse(startDateTime))
													.to(inputFormatterDateTime.parse(endDateTime)))
											.should(QueryBuilders.rangeQuery("dtLastModified")
													.from(inputFormatterDateTime.parse(startDateTime))
													.to(inputFormatterDateTime.parse(endDateTime)))

									));

					searchSourceBuilder.size(Integer.parseInt(pageSize));
					searchRequest.source(searchSourceBuilder);
					response = esClient.search(searchRequest, RequestOptions.DEFAULT);
				} else {
					SearchScrollRequest scrollRequest = new SearchScrollRequest(recordLink);
					scrollRequest.scroll(new TimeValue(scrollStateTime));
					response = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
				}
			}
			if (response.getHits().getHits().length >= Integer.parseInt(pageSize)) {
				responseMap.put(LexConstants.NEXT_RECORD_STATE, response.getScrollId());
			} else {
				responseMap.put(LexConstants.NEXT_RECORD_STATE, "-1");
			}

			for (SearchHit searchHit : response.getHits()) {
				Map<String, Object> source = searchHit.getSourceAsMap();
				esData.add(source);
			}
			responseMap.put(LexConstants.RESULT, esData);

		} catch (BadRequestException badRequestException) {
			ProjectLogger.log("Error : " + badRequestException.getMessage(), badRequestException);
			throw badRequestException;
		} catch (Exception exception) {
			ProjectLogger.log("Error : " + exception.getMessage(), exception);
			throw new Exception("Something went wrong");
		}
		return responseMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map<String, Object> getChannelJson(String rootOrg, String contentId)
			throws ApplicationLogicError, IOException {

		List<String> source = Arrays.asList("artifactUrl", "identifier", "contentType");
		List<String> status = Arrays.asList("Live", "Marked For Deletion");
		List<Map<String, Object>> contentMeta = userUtilityService.getContentMeta(Arrays.asList(contentId), source,
				status);
		if (contentMeta.size() < 1) {
			throw new BadRequestException("invalid.contentId");
		}
		Map<String, Object> meta = contentMeta.get(0);
		if (meta.get("contentType") == null || !meta.get("contentType").equals("Channel")) {
			throw new BadRequestException("invalid.contentType");
		}
		if (meta.get("artifactUrl") == null) {
			throw new BadRequestException("invalid.artifactUrl");
		}
		String artifactUrl = meta.get("artifactUrl").toString();

		// taking string after content-store and replacing '/' with '%2F'
		String formatString = artifactUrl.substring(artifactUrl.indexOf("content-store") + 14).replace("/", "%2F");
		String url = lexServerProps.getContentServiceUrl() + "/contentv3/download-live-content/" + formatString;
		ResponseEntity<Map> responseEntity;
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
			return responseEntity.getBody();
		} catch (HttpServerErrorException httpServerErrorException) {
			ProjectLogger.log("Error : " + httpServerErrorException.getMessage(), httpServerErrorException);
			throw new BadRequestException("Content Service Rest Call Exception");
		}
	}

}
