/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at 
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.response.ResponseParams;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.ExecutionContext;
import org.sunbird.common.request.Request;
import org.sunbird.common.responsecode.ResponseCode;

import com.infosys.service.GoalsService;

@RestController
@CrossOrigin(origins = "*")
public class GoalsController {
	@Autowired
	GoalsService goalsService;

	@DeleteMapping("/v1/users/{userEmail}/goals/{goalId}")
	public Response removeUserLearningGoals(@PathVariable("userEmail") String userEmail,
			@PathVariable("goalId") String goalId,
			@RequestParam(defaultValue = "user", required = true, name = "goalType") String goalType) {
		String url = "/v1/users/" + userEmail + "/goals/" + goalId;
		Map<String, Object> deleteMap = new HashMap<String, Object>();
		deleteMap.put("user_email", userEmail.toLowerCase().trim());
		deleteMap.put("goal_id", goalId);
		deleteMap.put("goal_type", goalType.toLowerCase());
		Long start = System.currentTimeMillis();
		Response resp = goalsService.removeLearningGoals(deleteMap); // calling addLearninggoal to save data into db
		resp.setVer("v1");
		resp.setId("api.users.goals.delete");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this gives the list of all user goals except common learning goals selected
	// by user
	@GetMapping("/v1/users/{userEmail}/goals&type=custom")
	public Response getUserGoalsWithoutCommonGoals_v1(@PathVariable("userEmail") String userEmail) {
		String url = "/v1/users/" + userEmail + "/goals&type=custom";
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getUserGoalsList_v1(userEmail);
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v1");
		resp.setId("api.users.goals.customgoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this gives the list of all user goals except common learning goals selected
	// by user
	@GetMapping("/v2/users/{userEmail}/goals&type=custom")
	public Response getUserGoalsWithoutCommonGoals(@PathVariable("userEmail") String userEmail) throws Exception {
		String url = "/v2/users/" + userEmail + "/goals&type=custom";
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getUserGoalsList(userEmail);
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v1");
		resp.setId("api.users.goals.customgoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	/* this api is used to insert/update user learning goals */

	@PostMapping("/v1/users/{userEmail}/goals")
	public Response upsertUserLearningGoals_v1(@RequestBody Request reqData,
			@PathVariable("userEmail") String userEmail) throws ParseException {
		String url = "/v1/users/" + userEmail + "/goals";
		Map<String, Object> reqMap = reqData.getRequest();
		reqMap.put("user_email", userEmail.trim());
		Long start = System.currentTimeMillis();
		Response resp = goalsService.upsertLearningGoals_v1(reqMap); // calling addLearninggoal to save data into db
		resp.setVer("v1");
		resp.setId("api.users.goals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// resp.put(Constants.RESPONSE, resp);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this api is used to get the list of all common goals except those already
	// chosen by users.
	@GetMapping("/v1/users/{userEmail}/goals&type=common&signup=false")
	public Response getAllCommonLearningGoals_v1(@PathVariable("userEmail") String userEmail) {
		String url = "/v1/users/" + userEmail + "/goals&type=common&signup=false";
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getCommonGoals_v1(userEmail.trim());
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v1");
		resp.setId("api.learninggoals.getcommongoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@GetMapping("/v1/users/{userEmail}/goals")
	public Response getUserLearningGoalsProgress_v1(@PathVariable("userEmail") String userEmail) {
		String url = "/v1/users/" + userEmail + "/goals";
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getGoalsProgress_v1(userEmail.trim());
		resp.setVer("v1");
		resp.setId("api.learninggoals.getgoalsprogress");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@PostMapping("/v2/users/goals/resources")
	public Response removeSubsetFromGoals(@RequestBody Request reqData,
			@RequestParam(defaultValue = "removesubset", required = true, name = "action") String operationType)
			throws ParseException {
		String url = "/v2/users/goals/resources";

		Map<String, Object> reqMap = reqData.getRequest();
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getUpdatedResourceList(reqMap); // calling addLearninggoal to save data into db
		resp.setVer("v2");
		resp.setId("api.users.goals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// resp.put(Constants.RESPONSE, resp);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@GetMapping("/v2/users/{userEmail}/goals")
	public Response getGoals(@PathVariable("userEmail") String userEmail,
			@RequestParam(defaultValue = "user", required = true, name = "type") String goalType) throws Exception {
		Response resp = null;
		String url = "/v2/users/" + userEmail + "/goals";
		Long start = System.currentTimeMillis();
		switch (goalType.toLowerCase()) {
		case "user":
			resp = goalsService.getGoalsProgress(userEmail.trim().toLowerCase());
			break;
		case "sharedby":
		case "sharedwith":
			resp = goalsService.getSharedGoalsList(userEmail.trim().toLowerCase(), goalType.toLowerCase());
			break;
		}
		resp.setVer("v2");
		resp.setId("api.goals.usergoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@PostMapping("/v2/users/{userEmail}/goals")
	public Response upsertGoals(@RequestBody Request reqData, @PathVariable("userEmail") String userEmail,
			@RequestParam(defaultValue = "user", required = false, name = "type") String type) throws Exception {
		Response resp = new Response();
		String url = "/v2/users/" + userEmail + "/goals";
		Map<String, Object> reqMap = reqData.getRequest();
		reqMap.put("user_email", userEmail.trim().toLowerCase());
		// delete particular users from shared goals
		Long start = System.currentTimeMillis();
		if (type.toLowerCase().trim().equals("deleteshared")) {
			resp = goalsService.deleteSharedUserFromSharedGoal(reqMap);
		} else {
			resp = goalsService.upsertLearningGoals(reqMap);
		}
		resp.setVer("v2");
		resp.setId("api.goals.usergoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@GetMapping("/v2/users/{userEmail}/goals/{goalId}")
	public Response goalTracking(@PathVariable("userEmail") String userEmail, @PathVariable("goalId") String goalId,
			@RequestParam(defaultValue = "goalstatus", required = true, name = "type") String type,
			@RequestParam(defaultValue = "custom_shared", required = true, name = "goaltype") String goalType)
			throws Exception {
		Response resp = null;
		String url = "/v2/users/" + userEmail + "/goals/" + goalId;
		Map<String, Object> sharedGoalIdentifier = new HashMap<>();
		sharedGoalIdentifier.put("user_email", userEmail.trim().toLowerCase());
		sharedGoalIdentifier.put("goal_id", goalId);
		sharedGoalIdentifier.put("goal_type", "sharedby");
		sharedGoalIdentifier.put("shared_goal_type", goalType);
		Long start = System.currentTimeMillis();
		if (type.toLowerCase().trim().equals("count")) {
			resp = goalsService.getCountForSharedGoal(sharedGoalIdentifier);
		} else {
			resp = goalsService.getSharedGoalStatus(sharedGoalIdentifier);
		}
		resp.setVer("v2");
		resp.setId("api.goals.sharedgoalstatus");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this api is used to get the list of all common goals except those already
	// chosen by users.
	@GetMapping("/v2/users/{userEmail}/goals&type=common&signup=false")
	public Response getAllCommonLearningGoals(@PathVariable("userEmail") String userEmail) {
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getCommonGoals(userEmail.trim());
		String url = "/v2/users/" + userEmail + "/goals&type=common&signup=false";
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v2");
		resp.setId("api.learninggoals.getcommongoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this api is used to get all programs that are part of onboarding goal group
	@GetMapping("/v1/goals/onboarding")
	public Response getOnboardingPrograms() {
		String url = "/v1/goals/onboarding";
		Long start = System.currentTimeMillis();
		Response resp = goalsService.getOnboardingPrograms();
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v1");
		resp.setId("api.goals.onboardingprograms");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	// this api is used to check if a shared common goal is already part of user
	// goal
	@PostMapping("/v1/users/{userEmail}/goals/commongoalscheck")
	public Response isAlreadyPartOfUserGoal(@RequestBody Request reqData, @PathVariable("userEmail") String userEmail) {
		String url = "/v1/users/" + userEmail + "/goals/commongoalscheck";
		Map<String, Object> reqMap = reqData.getRequest();
		reqMap.put("user_email", userEmail.trim().toLowerCase());
		Long start = System.currentTimeMillis();
		Response resp = goalsService.isAddedAsCommonGoal(reqMap);
		// Setting other attributes of response like version, timestamp, response param
		// etc.
		resp.setVer("v1");
		resp.setId("api.goals.sharedCommongoals");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode code = ResponseCode.getResponse(ResponseCode.success.getErrorCode());
		code.setResponseCode(ResponseCode.OK.getResponseCode());
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(code.getResponseCode()).name());
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	/*
	 * this api is for deleting a single resource(resource means everything here
	 * like course/program/module etc) from a user goal i.e. a goal that has been
	 * created by user and is not of type common or shared with him. this api is
	 * used on  home page.
	 */

	@DeleteMapping("/v1/users/{userEmail}/goals/{goalId}/contents/{}")
	public Response deleteSingleContentFromUserGoal(@PathVariable("userEmail") String userEmail,
			@PathVariable("goalId") String goalId, @PathVariable("lexId") String lexId ) {
		String url = "/v1/users/" + userEmail + "/goals/" + goalId + "/contents/" + lexId;
		Map<String, Object> deleteMap = new HashMap<String, Object>();
		deleteMap.put("user_email", userEmail.toLowerCase().trim());
		deleteMap.put("goal_id", goalId);
		deleteMap.put("content_id", lexId);
		// goal type is fixed for this api i.e. user as
		// he can only delete resource from user goals.
		deleteMap.put("goal_type", "user");
		Long start = System.currentTimeMillis();
		Response resp = goalsService.deleteContentUserGoal(deleteMap);
		resp.setVer("v1");
		resp.setId("api.users.goals.contentdelete");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	/*
	 * this api is for adding a single resource(resource means everything here like
	 * course/program/module etc) from a user goal i.e. a goal that has been created
	 * by user and is not of type common or shared with him. this api is used on 
	 * home page.
	 */

	@PatchMapping("/v1/users/{userEmail}/goals/{goalId}/contents/{}")
	public Response addSingleContentFromUserGoal(@PathVariable("userEmail") String userEmail,
			@PathVariable("goalId") String goalId, @PathVariable("lexId") String lexId ) {
		String url = "/v1/users/" + userEmail + "/goals/" + goalId + "/contents/" + lexId;
		Map<String, Object> updateMap = new HashMap<String, Object>();
		updateMap.put("user_email", userEmail.toLowerCase().trim());
		updateMap.put("goal_id", goalId);
		updateMap.put("content_id", lexId);
		// goal type is fixed for this api i.e. user as
		// he can only add resource from user goals.
		updateMap.put("goal_type", "user");
		Long start = System.currentTimeMillis();
		Response resp = goalsService.addContentUserGoal(updateMap);
		resp.setVer("v1");
		resp.setId("api.users.goals.contentadd");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Internal Server problem");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	/*
	 * this method is used to add a new common goal for a user based on
	 * useremail,common goal id and goal_duration only
	 */
	@PostMapping("/v1/users/{userEmail}/common-goals/{goalId}")
	public Response addCommonGoalByUser(@RequestBody Request reqData, @PathVariable("userEmail") String userEmail,
			@PathVariable("goalId") String goalId) throws ParseException {
		String url = "/v1/users/" + userEmail + "/common-goals/" + goalId;
		Map<String, Object> reqMap = reqData.getRequest();
		reqMap.put("user_email", userEmail.toLowerCase().trim());
		reqMap.put("goal_id", goalId);
		Long start = System.currentTimeMillis();
		Response resp = goalsService.addCommonGoal(reqMap);
		resp.setVer("v1");
		resp.setId("api.users.goals.addcommongoal");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Some error occurred.");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	/*
	 * This method is used to update duration of a common goal by user
	 */
	@PutMapping("/v1/users/{userEmail}/common-goals/{goalId}")
	public Response updateCommonGoalByUser(@RequestBody Request reqData, @PathVariable("userEmail") String userEmail,
			@PathVariable("goalId") String goalId) throws ParseException {
		String url = "/v1/users/" + userEmail + "/common-goals/" + goalId;
		Map<String, Object> reqMap = reqData.getRequest();
		reqMap.put("user_email", userEmail.toLowerCase().trim());
		reqMap.put("goal_id", goalId);
		Long start = System.currentTimeMillis();
		Response resp = goalsService.updateCommonGoal(reqMap);
		resp.setVer("v1");
		resp.setId("api.users.goals.updatecommongoal");
		resp.setTs(ProjectUtil.getFormattedDate());
		ResponseCode responseCode = resp.getResponseCode();
		// Preparing and setting response param
		ResponseParams params = new ResponseParams();
		params.setMsgid(ExecutionContext.getRequestId());
		params.setStatus(ResponseCode.getHeaderResponseCode(responseCode.getResponseCode()).name());
		if (responseCode.getResponseCode() != 200) {
			params.setErr("Some error occurred.");
		}
		resp.setParams(params);
		// ProjectLogger.log(url +":" +
		// LexProjectUtil.getFormattedTime(System.currentTimeMillis() -
		// start),LoggerEnum.INFO);
		return resp;
	}

	@GetMapping("/v1/users/{user_email}/goal/{goal_id}/content")
	public ResponseEntity<List<String>> getContentForGoals(@PathVariable("user_email") String userId,
			@PathVariable("goal_id") String goalId) {
		try {
			return new ResponseEntity<List<String>>(goalsService.getContentIdsForGoal(userId, goalId), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
