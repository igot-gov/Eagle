/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.service.VerificationPreviewService;

@RestController
@CrossOrigin(origins = "*")
public class ExerciseAuthPreviewController {

	private final VerificationPreviewService classDiagramPreviewService;

	private final VerificationPreviewService multiLanguagePreviewService;

	private final VerificationPreviewService pythonVerifyPreviewService;
	
	private final VerificationPreviewService javaVerifyPreviewSerivce;
	
	
	

	@Autowired
	public ExerciseAuthPreviewController(@Qualifier("classDiagramVerificationPreview") VerificationPreviewService classDiagramPreviewService,
			@Qualifier("multiLangVerificationPreview") VerificationPreviewService multiLanguagePreviewService,
			@Qualifier("pythonVerificationPreview") VerificationPreviewService pythonVerifyPreviewService,
			@Qualifier("javaVerificationPreview") VerificationPreviewService javaVerifyPreviewService) {
		this.classDiagramPreviewService = classDiagramPreviewService;
		this.multiLanguagePreviewService = multiLanguagePreviewService;
		this.pythonVerifyPreviewService = pythonVerifyPreviewService;
		this.javaVerifyPreviewSerivce = javaVerifyPreviewService;
	}

	/**
	 * Used in authoring tool to preview class diagram verify
	 * 
	 * @param submittedData
	 * @param userId
	 * @param rootOrg
	 * @param org
	 * @param exerciseId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/classdiagram/verify-preview")
	public ResponseEntity<Map<String, Object>> getClassDiagramVerifyPreview(
			@Valid @RequestBody AssignmentSubmissionDTO submittedData, @PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg",required=false,defaultValue = "Infosys") String rootOrg, @RequestHeader(name = "org",required=false,defaultValue = "Infosys Ltd") String org,
			@PathVariable("exercise_id") String exerciseId) throws Exception {
		submittedData.setResourceId(exerciseId);
		submittedData.setUserId(userId);
		return new ResponseEntity<Map<String, Object>>(
				classDiagramPreviewService.executeVerificationPreview(rootOrg, org, submittedData), HttpStatus.OK);
	}

	/**
	 * This is used to preview multilanguage exercise verify preview in authoring
	 * tool
	 * 
	 * @param submittedData
	 * @param userId
	 * @param exerciseId
	 * @param action
	 * @param rootOrg
	 * @param org
	 * @return
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/multilanguage-code/verify-preview")
	public ResponseEntity<Map<String, Object>> allLangVerificationPreview(
			@Valid @RequestBody AssignmentSubmissionDTO submittedData, @PathVariable("user_id") String userId,
			@PathVariable("exercise_id") String exerciseId,
			@RequestHeader(name = "rootOrg",required=false,defaultValue = "Infosys") String rootOrg,
			@RequestHeader(name = "org",required=false,defaultValue = "Infosys Ltd") String org) throws Exception {
		
		submittedData.setResourceId(exerciseId);
		submittedData.setUserId(userId);
		return new ResponseEntity<Map<String, Object>>(
				multiLanguagePreviewService.executeVerificationPreview(rootOrg, org, submittedData),
				HttpStatus.OK);
	}

	/**
	 * This is used in authoring to show the python verify preview
	 * 
	 * @param submittedData
	 * @param userId
	 * @param exerciseId
	 * @param rootOrg
	 * @param action
	 * @param org
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/python-code/verify-preview")
	public ResponseEntity<Map<String, Object>> pythonVerification(
			@Valid @RequestBody AssignmentSubmissionDTO submittedData, @PathVariable("user_id") String userId,
			@PathVariable("exercise_id") String exerciseId, @RequestHeader(name = "rootOrg",required=false,defaultValue = "Infosys") String rootOrg
			, @RequestHeader(name = "org",required=false,defaultValue = "Infosys Ltd") String org)
			throws Exception {
		submittedData.setResourceId(exerciseId);
		submittedData.setUserId(userId);;
		return new ResponseEntity<Map<String, Object>>(
				pythonVerifyPreviewService.executeVerificationPreview(rootOrg, org, submittedData), HttpStatus.OK);

	}
	
	/**
	 * This is used in authoring to show the java verify preview
	 * 
	 * @param submittedData
	 * @param userId
	 * @param exerciseId
	 * @param rootOrg
	 * @param action
	 * @param org
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/java-code/verify-preview")
	public ResponseEntity<Map<String, Object>> javaVerification(
			@Valid @RequestBody AssignmentSubmissionDTO submittedData, @PathVariable("user_id") String userId,
			@PathVariable("exercise_id") String exerciseId, @RequestHeader(name = "rootOrg",required=false,defaultValue = "Infosys") String rootOrg
			, @RequestHeader(name = "org",required=false,defaultValue = "Infosys Ltd") String org)
			throws Exception {
		submittedData.setResourceId(exerciseId);
		submittedData.setUserId(userId);;
		return new ResponseEntity<Map<String, Object>>(
				javaVerifyPreviewSerivce.executeVerificationPreview(rootOrg, org, submittedData), HttpStatus.OK);

	}

}
