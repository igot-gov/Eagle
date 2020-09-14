package com.infosys.lex.ext.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.ext.exception.ApplicationLogicError;

public interface LAService {

	Map<String, Object> getTags() throws Exception;

	Map<String, Object> getFeedBack(String rootOrg, String pageSequence, String pageSize, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getCatalog(String rootOrg, String pageSequence, String pageSize) throws Exception;

	Map<String, Object> getCatalogv2(String rootOrg, String pageSequence, String pageSize, String identifier)
			throws Exception;

	Map<String, Object> getCourseCatalog(String rootOrg, String pageSequence, String pageSize) throws Exception;

	Map<String, Object> getUserProgress(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getAssessmentDetails(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getSharedPlayLists(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate, String dumpType) throws Exception;

	Map<String, Object> getPlayList(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate, String dumpType) throws Exception;

	Map<String, Object> getUserSharedGoals(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate, String dumpType) throws Exception;

	Map<String, Object> getUserLearningGoals(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate, String dumpType) throws Exception;

	Map<String, Object> getCommonLearningGoals(String rootOrg) throws Exception;

	Map<String, Object> getMasterInterests(String rootOrg) throws Exception;

	Map<String, Object> getInterests(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getBadges(String rootOrg, String pageSize, String pagingSequence, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getLeaderBoardsData(String rootOrg, String pageSize, String pagingSequence, int leaderBoardYear,
			String durationType, int durationValue, String leaderBoardType) throws Exception;

	Map<String, Object> getUserTermsAndCondtion(String rootOrg, String pageSize, String pageSequence) throws Exception;

	Map<String, Object> getUserTermsAndCondtionV2(String pageSize, String pageSequence, String startDate,
			String endDate) throws Exception;

	Map<String, Object> getAllAssignments(String rootOrg, String startDateStr, String endDateStr, Integer prevPageNo,
			Integer pageSize) throws JsonParseException, JsonMappingException, IOException, Exception;

	Map<String, Object> getUsersAssignments(String rootOrg, String startDateStr, String endDateStr, String pageState,
			Integer pageSize) throws JsonParseException, JsonMappingException, IOException, Exception;

	Map<String, Object> getSocialPosts(String rootOrg, String recordLink, String startDateTime, String endDateTime,
			String pageSize, List<String> postStatus) throws Exception;

	Map<String, Object> getChannelJson(String rootOrg, String contentId) throws ApplicationLogicError, IOException;
}
