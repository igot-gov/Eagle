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
package com.infosys.lex.progress.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.progress.dto.AssessmentRecalculateDTO;
import com.infosys.lex.progress.dto.ContentProgressDTO;
import com.infosys.lex.progress.dto.ExternalProgressDTO;
import com.infosys.lex.progress.service.ContentProgressService;

@RestController
@CrossOrigin(origins = "*")
public class ContentProgressController {

	@Autowired
	ContentProgressService service;

	/**
	 * updates the progress and access dates of a content id and its hierarchy
	 * 
	 * @param requestBody
	 * @param emailId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/content/{content_id}/progress/update")
	public ResponseEntity<String> calculateProgress(@Valid @RequestBody ContentProgressDTO progressDTO,
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("content_id") String contentId,
			@RequestParam(value="markread",required = false, defaultValue = "false") Boolean markAsComplete) throws Exception {
		progressDTO.setUser_id(userId);
		progressDTO.setRoot_org(rootOrg);
		progressDTO.setResource_id(contentId);
		progressDTO.setMarkAsComplete(markAsComplete);
		return new ResponseEntity<String>(service.updateProgress(progressDTO), HttpStatus.CREATED);
		
	}
	
	/**
	 * get progress meta for mark as complete
	 *  
	 * @param requestBody
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/mark-as-complete")
	public ResponseEntity<?> calculateProgress(@Valid @RequestBody Map<String,Object> requestBody,
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<String> idsList = (List<String>)requestBody.get("contentIds");
		return new ResponseEntity<>(service.metaForProgress(rootOrg, userId, idsList),HttpStatus.OK);
		
	}
	
	/**
	 * get progress meta for mark as complete
	 *  
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{user_id}/content-ids/{content_id}/progress-meta")
	public ResponseEntity<?> getProgressMeta(
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId,
			@PathVariable("content_id") String contentId) throws Exception {
		
		return new ResponseEntity<>(service.metaForProgressForContentId(rootOrg, userId, contentId),HttpStatus.OK);
		
	}

	@GetMapping("/v1/users/{user_id}/content-ids/{content_id}/progress-meta/hierarchy")
	public ResponseEntity<?> getProgressMetaHierarchy(
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId,
			@PathVariable("content_id") String contentId) throws Exception {

		return new ResponseEntity<>(service.metaForProgressHierarchy(rootOrg, userId, contentId),HttpStatus.OK);

	}
	
	@PostMapping("/v1/users/{user_id}/assessment/{content_id}/progress/recalculate")
	public ResponseEntity<String> assessmentRecalculate(@Valid @RequestBody AssessmentRecalculateDTO progressDTO,
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("content_id") String contentId) throws Exception {
		progressDTO.setUser_id(userId);
		progressDTO.setRoot_org(rootOrg);
		progressDTO.setResource_id(contentId);
	
		return new ResponseEntity<String>(service.updateAssessmentRecalculate(progressDTO), HttpStatus.CREATED);
		
	}

	@PostMapping("/v1/progress/external")
	public ResponseEntity<String> externalProgress(@Valid @RequestBody ExternalProgressDTO progressDTO,
			@RequestHeader("rootOrg") String rootOrg
			) throws Exception {
		progressDTO.setRoot_org(rootOrg);
	
		return new ResponseEntity<String>(service.updateExternalProgress(progressDTO ), HttpStatus.CREATED);
		
	}

	/**
	 *
	 * @param rootOrg
	 * @param userId
	 * @return Status of mandatory content
	 * @throws Exception
	 */
	@GetMapping("/v1/check/mandatoryContentStatus/{user_id}")
	public ResponseEntity<?> getMandatoryContentStatus(
			@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org,
			@PathVariable("user_id") String userId) throws Exception {
		return new ResponseEntity<>(service.getMandatoryContentStatusForUser(rootOrg, org, userId),HttpStatus.OK);
	}
}