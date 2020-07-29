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
package com.infosys.lex.goal.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.goal.dto.ActionDTO;
import com.infosys.lex.goal.dto.GoalDTO;
import com.infosys.lex.goal.service.GoalsService;

@RestController
@CrossOrigin(origins = "*")
public class GoalsController {

	GoalsService goalsService;

	@Autowired
	public GoalsController(GoalsService goalsService) {
		super();
		this.goalsService = goalsService;
	}

// ---------------------------------------------NEW APIS---------------------------------------------------------------------------------------------------------
//									@author: Akhilesh Kumar
//----------------------------------------------------------------------------------------------------------------------
	/*
	 * This API is used to remove a goal from user_learning_goals table.
	 */
	@DeleteMapping("/v3/users/{user_id}/goals/{goal_id}")
	public ResponseEntity<String> removeUserLearningGoal(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestParam(defaultValue = "user", required = true, name = "goal_type") String goalType)
			throws Exception {

		goalsService.removeUserLearningGoal(rootOrg, goalType, userUUID, goalId);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/*
substitute url based on requirement
	 * as to create a common goal from the navigator page.
	 */
	@PostMapping("/v4/users/{user_id}/goals")
	public ResponseEntity<String> createLearningGoal(@Valid @RequestBody GoalDTO goalData,
			@PathVariable("user_id") String userUUID, @RequestHeader("rootOrg") String rootOrg) throws Exception {

		goalsService.createLearningGoal(rootOrg, goalData, userUUID);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * This API is used to share a goal be it common or custom from the goals page.
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/v1/users/{user_id}/goals/{goal_id}/recipients")
	public ResponseEntity<Map<String, Object>> shareGoal_v1(@RequestBody Map<String, Object> recipients,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestParam(name = "type") String type, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		List<String> listOfUsers = (List<String>) recipients.get("users");

		String message = null;
		if (recipients.get("message") != null) {
			message = recipients.get("message").toString();
		}

		return new ResponseEntity<>(goalsService.shareGoal_v1(listOfUsers, userUUID, goalId, type, rootOrg, message),
				HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/v2/users/{user_id}/goals/{goal_id}/recipients")
	public ResponseEntity<Map<String, Object>> shareGoal(@RequestBody Map<String, Object> recipients,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestParam(name = "type") String type, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		List<String> listOfUsers = (List<String>) recipients.get("users");
		String message = null;
		if (recipients.get("message") != null) {
			message = recipients.get("message").toString();
		}

		return new ResponseEntity<>(goalsService.shareGoal(listOfUsers, userUUID, goalId, type, rootOrg, message),
				HttpStatus.OK);
	}

	/*
	 * This API is used to accept or reject a shared goal from the "Action Required"
	 * section on the goals page.
	 */
	@PostMapping("/v1/users/{user_id}/goals/{goal_id}/actions")
	public ResponseEntity<Map<String, Object>> acceptOrRejectSharedGoal(@Valid @RequestBody ActionDTO actionData,
			@RequestParam(name = "action", required = true) String action,
			@RequestParam(defaultValue = "false", name = "confirm") Boolean confirm,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {

		return new ResponseEntity<>(goalsService.takeAction(action, actionData, confirm, userUUID, goalId, rootOrg),
				HttpStatus.OK);
	}

	/*
	 * This API is used to fetch the goals and their details to be shown under
	 * "Action Required" section of the goals page.
	 */
	@GetMapping("/v1/users/{user_id}/goals-For-Action")
	public ResponseEntity<List<Map<String, Object>>> getGoalsForAction_v1(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(
				goalsService.fetchGoalsForAction_v1(userUUID, rootOrg, language, meta), HttpStatus.OK);
	}

	/*
	 * This API is used to fetch the goals and their details to be shown under
	 * "Action Required" section of the goals page.
	 */
	@GetMapping("/v2/users/{user_id}/goals-For-Action")
	public ResponseEntity<List<Map<String, Object>>> getGoalsForAction(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(
				goalsService.fetchGoalsForAction(userUUID, rootOrg, language, meta), HttpStatus.OK);
	}

	/*
	 * This API is used to fetch the common goals visible under "Suggested Goals"
	 * section while creating a goal.
	 */
	@GetMapping("/v1/users/{user_id}/common-goals")
	public ResponseEntity<List<Map<String, Object>>> getSuggestedGoals(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language) throws Exception {
		return new ResponseEntity<>(goalsService.getSuggestedGoals(rootOrg, userUUID, language), HttpStatus.OK);
	}

	/*
	 * This API is used to fetch the common goals visible under "Suggested Goals"
	 * section while creating a goal.
	 */
	@GetMapping("/v1/users/{user_id}/goal-groups")
	public ResponseEntity<List<Map<String, Object>>> getGoalGroups(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language) throws Exception {
		return new ResponseEntity<>(goalsService.getGoalGroups(rootOrg, userUUID, language), HttpStatus.OK);
	}

	/*
	 * This API is used to fetch the common goals visible under "Suggested Goals"
	 * section while creating a goal for a given goal group.
	 */
	@GetMapping("/v1/users/{user_id}/common-goals/{group_id}")
	public ResponseEntity<Map<String, Object>> getSuggestedGoalsByGroup(
			@PathVariable(required = false, name = "group_id") String groupId, @PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		return new ResponseEntity<>(
				goalsService.getSuggestedGoalsByGoalGroup(rootOrg, userUUID, groupId, language, meta), HttpStatus.OK);
	}

	@GetMapping("/v1/common-goals")
	public ResponseEntity<List<Map<String, Object>>> getAllCommonGoals(
			@RequestParam(required = true, name = "rootOrg") String rootOrg) {
		return new ResponseEntity<>(goalsService.getAllCommonGoals(rootOrg), HttpStatus.OK);
	}

	/*
	 * This API is used to update duration of a common goal by user.
	 */
	@PutMapping("/v3/users/{user_id}/common-goals/{goal_id}")
	public ResponseEntity<Map<String, Object>> updateCommonGoal(@RequestParam(name = "goal_type") String goalType,
			@RequestParam(name = "duration") Integer duration, @PathVariable("user_id") String userUUID,
			@PathVariable("goal_id") String goalId, @RequestHeader("rootOrg") String rootOrg) throws Exception {

		return new ResponseEntity<>(
				goalsService.updateCommonGoalDuration(rootOrg, userUUID, goalType, goalId, duration.intValue()),
				HttpStatus.OK);
	}

	/*
	 * This API is used to remove/update goal content when a checkbox is clicked
	 * while adding content to a goal.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/v4/users/goals/resources")
	public ResponseEntity<Map<String, Object>> removeSubsetFromGoals(@RequestBody Map<String, Object> goalsData)
			throws Exception {

		return new ResponseEntity<>(goalsService.updateGoalReosurces((List<String>) goalsData.get("goal_content_id")),
				HttpStatus.OK);
	}

	/*
	 * This API is for deleting a single resource(resource means everything here
	 * like course/program/module etc) from a user goal i.e. a goal that has been
	 * created by user and is not of type common or shared with him. This API is
substitute url based on requirement
	 */
	@DeleteMapping("/v3/users/{user_id}/goals/{goal_id}/contents/{lex_id}")
	public ResponseEntity<Map<String, Object>> deleteSingleContentFromHomePage(@PathVariable("user_id") String userUUID,
			@PathVariable("goal_id") String goalId, @PathVariable("lex_id") String lexId,
			@RequestParam(name = "goal_type") String goalType, @RequestHeader("rootOrg") String rootOrg)
			throws Exception {

		return new ResponseEntity<>(goalsService.deleteResourceFromUserGoal(rootOrg, userUUID, goalId, lexId, goalType),
				HttpStatus.OK);
	}

	/*
	 * This API is for adding a single resource(resource means everything here like
	 * course/program/module etc) from a user goal i.e. a goal that has been created
substitute url based on requirement
	 * home page.
	 */
@PatchMapping("/v3/users/{user_id}/goals/{goal_id}/contents/{lex_id}")
public ResponseEntity<Map<String, Object>> addSingleContentFromHomePage(@PathVariable("user_id") String userUUID,
		@PathVariable("goal_id") String goalId, @PathVariable("lex_id") String lexId,
		@RequestParam(name = "goal_type") String goalType, @RequestHeader("rootOrg") String rootOrg)
		throws Exception {

	return new ResponseEntity<>(goalsService.addContentToUserGoal(userUUID, goalId, lexId, goalType, rootOrg),
			HttpStatus.OK);
}

	/*
	 * This method is responsible for removal of users(targets) from a certain
	 * shared goal. It will also remove the goal from user_shared_goal and add it to
	 * user_learning_goals, in-case the last user is removed.
	 */
	@PostMapping("/v1/users/{user_id}/goals/{goal_id}/recipients/unshare")
	public ResponseEntity<Map<String, Object>> modifySharedGoal_v1(@RequestBody Map<String, Object> targetIds,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestParam(required = true, name = "goal_type") String goalType,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<>(goalsService.removeGoalSharing_v1(targetIds, userUUID, goalId, goalType, rootOrg),
				HttpStatus.OK);
	}

	/*
	 * This method is responsible for removal of users(targets) from a certain
	 * shared goal. It will also remove the goal from user_shared_goal and add it to
	 * user_learning_goals, in-case the last user is removed.
	 */
	@PostMapping("/v2/users/{user_id}/goals/{goal_id}/recipients/unshare")
	public ResponseEntity<Map<String, Object>> modifySharedGoal(@RequestBody Map<String, Object> targetIds,
			@PathVariable("user_id") String userUUID, @PathVariable("goal_id") String goalId,
			@RequestParam(required = true, name = "goal_type") String goalType,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<>(goalsService.removeGoalSharing(targetIds, userUUID, goalId, goalType, rootOrg),
				HttpStatus.OK);
	}

	/*
	 * This method is responsible for fetching all the goals that are displayed
	 * under "MY GOALS" section. It will fetch the goals from both ULG<if you have
	 * created a common or custom goal> and USG<in case a goal has been shared with
	 * you and you've accepted it.> This method is also responsible for displaying
	 * individual resource progress and over all goal completion progress
	 * 
	 * Additional Functionality: 1. This method will also display the goals to which
	 * a common goal can be added directly from homepage. If details is false, fetch
	 * the list of goals depending upon the goalType. GoalType can either be "user"
	 * or "tobeshared".
	 */
	@GetMapping("/v4/users/{user_id}/goals")
	public ResponseEntity<Map<String, Object>> fetchGoalsWithProgress_v1(@PathVariable("user_id") String userUUID,
			@RequestParam(required = false, name = "goal_type", defaultValue = "user") String goalType,
			@RequestParam(required = false, name = "details", defaultValue = "true") Boolean details,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {

		Map<String, Object> myGoalsData = null;

		if (details) {
			myGoalsData = goalsService.fetchMyGoalsWithProgress_v1(userUUID, rootOrg, language, meta);
		} else {
			myGoalsData = goalsService.fetchGoalsByGoalType(userUUID, rootOrg, goalType);
		}

		return new ResponseEntity<>(myGoalsData, HttpStatus.OK);
	}

	@GetMapping("/v5/users/{user_id}/goals")
	public ResponseEntity<Map<String, Object>> fetchGoalsWithProgress(@PathVariable("user_id") String userUUID,
			@RequestParam(required = false, name = "goal_type", defaultValue = "user") String goalType,
			@RequestParam(required = false, name = "details", defaultValue = "true") Boolean details,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {

		Map<String, Object> myGoalsData = null;

		if (details) {
			myGoalsData = goalsService.fetchMyGoalsWithProgress(userUUID, rootOrg, language, meta);
		} else {
			myGoalsData = goalsService.fetchGoalsByGoalType(userUUID, rootOrg, goalType);
		}

		return new ResponseEntity<>(myGoalsData, HttpStatus.OK);
	}

	@GetMapping("/v5/users/{user_id}/goals/content")
	public ResponseEntity<Map<String, Object>> fetchGoalsContentWithProgress(@PathVariable("user_id") String userUUID,
																			 @RequestHeader("rootOrg") String rootOrg,
																			 @RequestParam(required = false, name = "sourceFields") List<String> sourceFields) throws Exception {

		Map<String, Object> myGoalsContents = goalsService.fetchUserGoalsContentWithProgress(userUUID, rootOrg, sourceFields);
		return new ResponseEntity<>(myGoalsContents, HttpStatus.OK);
	}

	/*
	 * This method is responsible for fetching all the goals that are displayed
	 * under "GOALS FOR OTHERS" section. It will fetch the goals from USG<whose
	 * shared_by is userUUID> and from ULG<whose user_id is userUUID and goal_type
	 * is either commonshared or tobeshared>.
	 */
	@GetMapping("/v1/users/{user_id}/goals-for-others")
	public ResponseEntity<List<Map<String, Object>>> fetchGoalsSharedByMe_v1(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		List<Map<String, Object>> sharedGoalsData = goalsService.fetchGoalsSharedByMe_v1(userUUID, rootOrg, language,
				meta);
		return new ResponseEntity<>(sharedGoalsData, HttpStatus.OK);
	}

	@GetMapping("/v2/users/{user_id}/goals-for-others")
	public ResponseEntity<List<Map<String, Object>>> fetchGoalsSharedByMe(@PathVariable("user_id") String userUUID,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader(required = false, value = "langCode") String language,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		List<Map<String, Object>> sharedGoalsData = goalsService.fetchGoalsSharedByMe(userUUID, rootOrg, language,
				meta);
		return new ResponseEntity<>(sharedGoalsData, HttpStatus.OK);
	}

	/*
	 * This end-point is responsible for the tracking feature of the shared goals.
	 * It can determine the number of acceptors, rejectors and number of pending
	 * requests for a shared goal. It will also calculate individual content
	 * progress and total goal progress for a accepted goal for all its acceptors.
	 */
	@GetMapping("/v1/users/{user_id}/goals/{goal_id}")
	public ResponseEntity<Map<String, Object>> trackSharedGoal_v1(@PathVariable("user_id") String userUUID,
			@PathVariable("goal_id") String goalId, @RequestParam(required = true, name = "goal_type") String goalType,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		Map<String, Object> sharedGoalsData = goalsService.trackSharedGoal_v1(rootOrg, userUUID, goalId, goalType,
				meta);
		return new ResponseEntity<>(sharedGoalsData, HttpStatus.OK);
	}

	@GetMapping("/v2/users/{user_id}/goals/{goal_id}")
	public ResponseEntity<Map<String, Object>> trackSharedGoal(@PathVariable("user_id") String userUUID,
			@PathVariable("goal_id") String goalId, @RequestParam(required = true, name = "goal_type") String goalType,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(required = false, name = "sourceFields") List<String> meta) throws Exception {
		Map<String, Object> sharedGoalsData = goalsService.trackSharedGoal(rootOrg, userUUID, goalId, goalType, meta);
		return new ResponseEntity<>(sharedGoalsData, HttpStatus.OK);
	}
}