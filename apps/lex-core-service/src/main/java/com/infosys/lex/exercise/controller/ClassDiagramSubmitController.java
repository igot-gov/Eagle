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

import com.infosys.lex.exercise.bodhi.repo.Evaluatedmarks;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.service.SubmitService;
import com.infosys.lex.exercise.service.VerificationService;


@RestController
@CrossOrigin(origins = "*")
public class ClassDiagramSubmitController {
	
	
	
	private final SubmitService submitServ;
	
	
	private final VerificationService verifyServ;
	
	
	
	
	@Autowired
	public ClassDiagramSubmitController(SubmitService submitServ, @Qualifier("classDiagramVerification") VerificationService verifyServ) {
		this.submitServ = submitServ;
		this.verifyServ = verifyServ;
	}





	/**
	 * submits the ooad or class diagram exercise
	 * @param submittedData
	 * @param userId
	 * @param exerciseId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{exercise_id}/classdiagram-submission")
	public ResponseEntity<Map<String, Object>> ooadSubmit(@Valid @RequestBody AssignmentSubmissionDTO submittedData,
			@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("exercise_id") String exerciseId) throws Exception {

		Map<String, Object> resp = new HashMap<String, Object>();

		Map<String, Object> output = new HashMap<String, Object>();

		submittedData.setUserId(userId);
		submittedData.setResourceId(exerciseId);


		output = verifyServ.executeVerification(rootOrg,submittedData);

		Evaluatedmarks verifyResult = new Evaluatedmarks();

		verifyResult = (Evaluatedmarks) (output.get("verifyResult"));

		SubmitDataDTO submitData = new SubmitDataDTO();
		submitData.setResponse(submittedData.getUser_solution());
		submitData.setResult_percent(verifyResult.getMarksPercent());
		submitData.setTestcases_failed(-1);
		submitData.setTestcases_passed(-1);
		submitData.setTotal_testcases(-1);
		boolean toSubmit = (boolean) output.get("verifyStatus") || submittedData.isIgnore_error();

		output.put("submitResult", submitServ.submit(rootOrg,submitData, toSubmit, userId, exerciseId, "ooad"));

		resp.put("verifyResult", verifyResult);

		resp.put("submitResult", output.get("submitResult"));

		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}

}
