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
package com.infosys.lex.exercise.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.exercise.dto.NewCodeExerciseDTO;
import com.infosys.lex.exercise.dto.NewExerciseFeedbackDTO;
import com.infosys.lex.exercise.dto.NewLNDExerciseDTO;
import com.infosys.lex.exercise.service.ExerciseService;

@RestController
@CrossOrigin(origins = "*")
public class ExerciseController {

	@Autowired
	ExerciseService exerciseServ;
	


	/**
	 * inserts LND exercises into the database
	 * 
	 * @param requestBody
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{content_id}/submissions")
	public ResponseEntity<Map<String, Object>> submitExercise(@Valid @RequestBody NewLNDExerciseDTO requestBody,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@PathVariable("user_id") String userId, @PathVariable("content_id") String contentId,
			@RequestHeader HttpHeaders headers) throws Exception {
		Map<String, Object> resp = new HashMap<>();
		String rep = exerciseServ.insertLNDSubmission(rootOrg,requestBody, contentId, userId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.CREATED);
	}

	/**
	 * inserts code based exercises into the database
	 * 
	 * @param userId
	 * @param contentId
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{content_id}/code-submissions")
	public ResponseEntity<Map<String, Object>> submitExerciseCode(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@PathVariable("content_id") String contentId, @Valid @RequestBody NewCodeExerciseDTO requestBody)
			throws Exception {
		Map<String, Object> resp = new HashMap<>();
		String rep = exerciseServ.insertCodeSubmission(rootOrg,requestBody, contentId, userId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.CREATED);
	}

	/**
	 * gets all or the latest exercise submissions from the database for a user and
	 * a particular exercise
	 * 
	 * @param userId
	 * @param contentId
	 * @param userIdType
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/exercises/{content_id}/submissions")
	public ResponseEntity<Map<String, Object>> getAllSubmissions(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@PathVariable("content_id") String contentId,
			@RequestParam(defaultValue = "0", required = false, name = "type") String type,
			@RequestHeader HttpHeaders headers) throws Exception {
		Map<String, Object> resp = new HashMap<>();
		List<Map<String, Object>> rep = null;
		if (type.toLowerCase().equals("latest"))
			rep = exerciseServ.getLatestData(rootOrg,userId, contentId);
		else
			rep = exerciseServ.getExerciseSubmissionsByUser(rootOrg,userId, contentId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}

	/**
	 * gets a specific exercise submissions from the database for a user and a
	 * particular exercise
	 * 
	 * @param userId
	 * @param contentId
	 * @param submissionId
	 * @param userIdType
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/exercises/{content_id}/submissions/{submission_id}")
	public ResponseEntity<Map<String, Object>> getOneSubmission(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@PathVariable("content_id") String contentId, @PathVariable("submission_id") String submissionId)
			throws Exception {
		Map<String, Object> resp = new HashMap<>();
		HttpStatus status = HttpStatus.OK;
		List<Map<String, Object>> rep = exerciseServ.getOneData(rootOrg,userId, contentId, submissionId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, status);
	}

	/**
	 * submits the feedback by the educator for a particular exercise
	 * 
	 * @param userId
	 * @param contentId
	 * @param submissionId
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{content_id}/submissions/{submission_id}/feedback")
	public ResponseEntity<Map<String, Object>> submitFeedback(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@PathVariable("content_id") String contentId, @PathVariable("submission_id") String submissionId,
			@Valid @RequestBody NewExerciseFeedbackDTO requestBody) throws Exception {
		Map<String, Object> resp = new HashMap<>();
		String rep = exerciseServ.insertFeedback(rootOrg,requestBody, contentId, userId, submissionId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.CREATED);
	}

	/**
	 * gets all latest exercise submissions from the database for group
	 * 
	 * @param groupId
	 * @param contentId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/groups/{group_id}/exercises/{exercise_id}/submissions")
	public ResponseEntity<Map<String, Object>> getSubmissionsByGroup(@PathVariable("group_id") String groupId,
			@PathVariable("exercise_id") String contentId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg, 
			@RequestParam(defaultValue = "latest", required = false, name = "type") String type,
			@RequestHeader HttpHeaders headers) throws Exception {
		Map<String, Object> resp = new HashMap<>();
		Map<String, Object> rep = exerciseServ.getSubmissionsByGroups(rootOrg,groupId, contentId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}

	/**
	 * gets all the latest feedback for the notifications page(within last 15 days).
	 * 
	 * @param userId
	 * @param userIdType
	 * @return
	 * @throws Exception
	 */
	
	
	@GetMapping("/v1/users/{user_id}/exercises/notification")
	public ResponseEntity<Map<String, Object>> getExerciseNotification(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG) String rootOrg)
			throws Exception {
		Map<String, Object> resp = new HashMap<>();
		List<Map<String, Object>> rep = exerciseServ.getExerciseNotification(rootOrg,userId);
		resp.put("response", rep);
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}

}
