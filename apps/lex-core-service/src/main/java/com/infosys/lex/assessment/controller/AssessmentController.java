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
package com.infosys.lex.assessment.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;
import com.infosys.lex.assessment.service.AssessmentService;

@RestController
@CrossOrigin(origins = "*")
public class AssessmentController {

	@Autowired
	AssessmentService assessmentService;

	/**
	 * validates, submits and inserts assessments and quizzes into the db
	 * 
	 * @param requestBody
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v2/user/{userId}/assessment/submit")
	public ResponseEntity<Map<String, Object>> submitAssessmentByCassandra(
			@Valid @RequestBody AssessmentSubmissionDTO requestBody, @PathVariable("userId") String userId,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
			    
		return new ResponseEntity<Map<String, Object>>(assessmentService.submitAssessment(rootOrg, requestBody, userId),
				HttpStatus.CREATED);
	}

	/**
	 * Controller to a get request to Fetch AssessmentData the request requires
	 * user_id and course_id returns a JSON of processesd data and list of
	 * Assessments Given
	 * 
	 * @param courseId
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/content/{courseId}/user/{userId}/assessment")
	public ResponseEntity<Map<String, Object>> getAssessmentByContentUser(@PathVariable String courseId,
			@PathVariable("userId") String userId, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				assessmentService.getAssessmentByContentUser(rootOrg, courseId, userId), HttpStatus.OK);
	}

}
