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
package com.infosys.lex.learninghistory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.infosys.lex.learninghistory.service.LearningHistoryService;

@RestController
@CrossOrigin(origins = "*")
public class LearningHistoryController {

	@Autowired
	LearningHistoryService lhService;

	/**
	 * learning history from cassandra (Real time progress)
	 * 
	 * @param userId
	 * @param pageState
	 * @param pageSize
	 * @param progressStatus
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v3/users/{user_id}/dashboard/courses")
	public ResponseEntity<Map<String, Object>> getContentFromCassandra(@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(defaultValue = "0", required = false, name = "page_state") String pageState,
			@RequestParam(defaultValue = "10", required = false, name = "page_size") Integer pageSize,
			@RequestParam(defaultValue = "inprogress", required = false, name = "status") String progressStatus,
			@RequestParam(defaultValue = "0", required = false, name = "content_type") String contentType)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				lhService.getUserCourseProgress(rootOrg,userId, pageSize, pageState, progressStatus, contentType),
				HttpStatus.OK);
	}

	/**
	 * progress from cassandra for particular ids with details(Real time progress)
	 * 
	 * @param userId
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v3/users/{user_id}/dashboard/courses/details")
	public ResponseEntity<List<Map<String, Object>>> getContentListDataFromCassandar(
			@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestBody List<String> requestBody) throws Exception {
		return new ResponseEntity<>(lhService.getUserContentListProgress(rootOrg,userId, requestBody),
				HttpStatus.OK);
	}

	/**
	 * progress from cassandra without details(Real time progress)
	 * 
	 * @param userId
	 * @param contentIds
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v3/users/{user_id}/contentlist/progress")
	public ResponseEntity<Map<String, Object>> getContentProgressFromCassandra(@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(defaultValue = "", required = false, name = "contentIds") String contentIds)
			throws Exception {
		return new ResponseEntity<>(lhService.getContentListStrProgress(rootOrg,userId, contentIds),
				HttpStatus.OK);
	}

	
	/**
	 * progress from cassandra without details(Real time progress)(POST)
	 * 
	 * @param userId
	 * @param contentIds
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v3/users/{user_id}/contentlist/progress")
	public ResponseEntity<Map<String, Object>> getContentProgressFromCassandra(@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestBody Map<String,Object> contentIdMap)
			throws Exception {
		return new ResponseEntity<>(lhService.getProgressForContentListMap(rootOrg,userId, contentIdMap),
				HttpStatus.OK);
	}

}
