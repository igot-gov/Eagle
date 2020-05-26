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
package com.infosys.lex.exercise.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.Evaluatedmarks;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.util.ClassDiagramUtilService;

@Service
@Qualifier("classDiagramVerification")
public class ClassDiagramVerificationServiceImpl implements VerificationService {

	
	private final UserUtilityService userUtilService;

	
	private final ContentService contentService;

	
	private final ExerciseRepository exerciseRepo;
	

	
	private final ClassDiagramUtilService utilServ;
	
	
	@Autowired
	public ClassDiagramVerificationServiceImpl(UserUtilityService userUtilService, ContentService contentService,
			ExerciseRepository exerciseRepo, ClassDiagramUtilService utilServ) {
		this.userUtilService = userUtilService;
		this.contentService = contentService;
		this.exerciseRepo = exerciseRepo;
		this.utilServ = utilServ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.ClassDiagramVerificationService#
	 * executeOOADVerification(com.infosys.core.DTO.AssignmentSubmissionDTO)
	 */
	@Override
	public Map<String, Object> executeVerification(String rootOrg,
			AssignmentSubmissionDTO submittedData) throws Exception {
		String userUUID = submittedData.getUserId();
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		Map<String, Object> output = new HashMap<String, Object>();
		Evaluatedmarks verifyResult = null;

		exerciseRepo.validateExerciseID(submittedData.getResourceId());
		verifyResult = verifyAssignment(submittedData);
		boolean verifyStatus = (verifyResult.getMarksPercent() == 100.0);

		output.put("verifyResult", verifyResult);
		output.put("verifyStatus", verifyStatus);

		return output;
	}

	private Evaluatedmarks verifyAssignment(AssignmentSubmissionDTO submittedData) throws Exception {
		Evaluatedmarks evaluatedAssignment = new Evaluatedmarks();
		if (submittedData != null) {
			if (!(submittedData.getUser_solution() == null || submittedData.getUser_solution().equals(""))) {

				String answerKey = "";
				int qpMarks = 100;

				answerKey = getOOADAnswerKey(submittedData.getResourceId());

				if (!(answerKey == null || answerKey.equals(""))) {
//					Map<String, Object> formattedDataMap = new HashMap<String, Object>();
//					generateArrays(answerKey, false, formattedDataMap);
//					generateArrays(submittedData.getUser_solution(), true, formattedDataMap);
//
//					evaluatedAssignment = evaluateScripts( qpMarks, formattedDataMap);
					
					utilServ.formatAndEvaluateUserSolution(submittedData.getUser_solution(), answerKey, qpMarks);

				} else {
					throw new ApplicationLogicError("Couldn't find actual solution!!");
				}

			} else {

				throw new InvalidDataInputException("missing.assignmenttype");

			}
		} else {
			throw new InvalidDataInputException("impropper.input.format");

		}
		return evaluatedAssignment;
	}

	/*
	 * This method retrieves ooad answer key from content store
	 */
	private String getOOADAnswerKey(String resourceId) throws Exception {
		String answerKey = "";
		List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
				Arrays.asList(new String[] { resourceId }), new String[] { "artifactUrl" }, null);
		if (sources.size() == 0) {
			throw new InvalidDataInputException("invalid.resource");
		}

		Map<String, Object> source = sources.get(0);

		String answerKeyUrl = String.valueOf(source.get("artifactUrl"));
		int index = answerKeyUrl.lastIndexOf("/");
		String completeFileName = answerKeyUrl.substring(index + 1);
		String fileName = completeFileName.split("\\.")[0];
		fileName = fileName + "-key.json";
		answerKeyUrl = answerKeyUrl.substring(0, index + 1) + fileName;
//		answerKeyUrl = answerKeyUrl.replace("private-static-host", "IPaddress");

		// This was done because answer key was present in two instances
		if (answerKey.contains("private-content-service") || answerKey.contains("private-static-host"))
			answerKey = contentService.getContentStoreData(answerKeyUrl);
		else
			answerKey = contentService.getKeyFromContentStore(answerKeyUrl);

		return answerKey;
	}

	
	
	


}
