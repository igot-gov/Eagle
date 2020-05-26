/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.service.SubmitService;
import com.infosys.lex.exercise.service.VerificationService;


@RestController
@CrossOrigin(origins = "*")
public class JavaSubmitController {

	SubmitService submitServ;

	VerificationService verifyServ;
	
	@Autowired
	public JavaSubmitController(SubmitService submitServ, @Qualifier("javaVerification")VerificationService verifyServ) {
		this.submitServ = submitServ;
		this.verifyServ = verifyServ;
	}

	
	/**
	 * verification and submission of python submissions
	 * @param submittedData
	 * @param userId
	 * @param exerciseId
	 * @param action
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/java-submission")
	public ResponseEntity<Map<String, Object>> javaCodeSubmission(
			@Valid @RequestBody AssignmentSubmissionDTO submittedData, @PathVariable("user_id") String userId,
			@PathVariable("exercise_id") String exerciseId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(required = true, value = "type") String action) throws Exception {
		Map<String, Object> resp = new HashMap<String, Object>();
		Map<String,Object> output = new HashMap<String,Object>();
		

		submittedData.setUserId(userId);
		submittedData.setResourceId(exerciseId);
		output = verifyServ.executeVerification(rootOrg,submittedData);

		if (action.toLowerCase().equals("submit")) {

			output.put("id", "api.exercise.java.submit");
			SubmitDataDTO verifySummary = (SubmitDataDTO) output.get("verifySummary");
			boolean verifyStatus = verifySummary.getTestcases_failed() == 0 && verifySummary.getTestcases_passed()>0;
			boolean toSubmit = (verifyStatus || submittedData.isIgnore_error());
			verifySummary.setResponse(submittedData.getUser_solution());
			output.put("submitResult", submitServ.submit(rootOrg,verifySummary, toSubmit, userId, exerciseId, "java"));

		} else {
			output.put("id", "api.exercise.java.verify");

		}

		resp.put("verifyResult", output.get("verifyResult"));
		if (action.toLowerCase().equals("submit"))
			resp.put("submitResult", output.get("submitResult"));
		
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}

}
