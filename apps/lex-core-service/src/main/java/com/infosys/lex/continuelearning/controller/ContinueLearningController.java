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
package com.infosys.lex.continuelearning.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.continuelearning.dto.ContinueLearningDTO;
import com.infosys.lex.continuelearning.service.ContinueLearningService;

@RestController
@CrossOrigin(origins = "*")
public class ContinueLearningController {

	@Autowired
	private ContinueLearningService continueLearningSvc;

	@PostMapping("/v1/continue/user/{userId}/putdata")
	public ResponseEntity<Map<String, Object>> upsertLearningData(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("userId") String userId, @Valid @RequestBody ContinueLearningDTO data) throws Exception {
		return new ResponseEntity<Map<String, Object>>(continueLearningSvc.upsertLearningData(rootOrg, userId, data),
				HttpStatus.ACCEPTED);

	}

	@GetMapping("/v1/continue/user/{userId}/getdata")
	public ResponseEntity<Map<String, Object>> getLearningData(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("userId") String userId,
			@RequestParam(value = "sourceFields", required = false) Set<String> sourceFields,
			@RequestParam(defaultValue = "all", required = false, name = "contextPathId") String contextPathId,
			@RequestParam(defaultValue = "5", required = false, name = "pageSize") String pageSize,
			@RequestParam(defaultValue = "0", required = false, name = "pageState") String pageState,
			@RequestParam(defaultValue = "default", required = false, name = "isCompleted") String isCompleted,
			@RequestParam(defaultValue = "default", required = false, name = "isInIntranet") String isInIntranet,
			@RequestParam(defaultValue = "default", required = false, name = "isStandAlone") String isStandAlone,
			@RequestParam(defaultValue = "all", required = false, name = "contentType") List<String> contentType)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(continueLearningSvc.getLearningDataWithFilters(rootOrg, userId,
				sourceFields, contextPathId, pageSize, pageState, isCompleted, isInIntranet, isStandAlone, contentType),
				HttpStatus.OK);
	}

	@GetMapping("/v1/continue/user/{userId}")
	public ResponseEntity<Map<String, Object>> getLearningUser(@RequestHeader("rootOrg") String rootOrg,
															   @PathVariable("userId") String userId,
															   @RequestParam(value = "sourceFields", required = false) Set<String> sourceFields,
															   @RequestParam(defaultValue = "all", required = false, name = "contextPathId") String contextPathId,
															   @RequestParam(defaultValue = "5", required = false, name = "pageSize") String pageSize,
															   @RequestParam(defaultValue = "0", required = false, name = "pageState") String pageState,
															   @RequestParam(defaultValue = "default", required = false, name = "isCompleted") String isCompleted,
															   @RequestParam(defaultValue = "default", required = false, name = "isInIntranet") String isInIntranet,
															   @RequestParam(defaultValue = "default", required = false, name = "isStandAlone") String isStandAlone,
															   @RequestParam(defaultValue = "all", required = false, name = "resourceType") String resourceType) throws Exception {
		return new ResponseEntity<Map<String, Object>>(continueLearningSvc.getLearningContent(rootOrg, userId,
				sourceFields, contextPathId, pageSize, pageState, isCompleted, isInIntranet, isStandAlone, resourceType),
				HttpStatus.OK);
	}
}
