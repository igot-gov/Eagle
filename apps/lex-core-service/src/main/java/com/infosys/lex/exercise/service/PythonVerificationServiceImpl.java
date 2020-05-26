/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.ExerciseCodeVerifcationException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.util.PythonVerificationUtilService;

@Service
@Qualifier("pythonVerification")
public class PythonVerificationServiceImpl implements VerificationService {

	private final UserUtilityService userUtilService;
	private final ContentService contentService;
	private final LexServerProperties lexServerProperties;
	private final IAPVerificationService iapVerificationService;
	private final ExerciseRepository exerciseRepo;
	private final PythonVerificationUtilService utilService;
	
	
	
	
	
	@Autowired
	public PythonVerificationServiceImpl(UserUtilityService userUtilService, ContentService contentService,
			LexServerProperties lexServerProperties, IAPVerificationService iapVerificationService,
			ExerciseRepository exerciseRepo, PythonVerificationUtilService utilService) {
		this.userUtilService = userUtilService;
		this.contentService = contentService;
		this.lexServerProperties = lexServerProperties;
		this.iapVerificationService = iapVerificationService;
		this.exerciseRepo = exerciseRepo;
		this.utilService = utilService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.PythonVerificationService#executePythonVerification(
	 * com.infosys.core.DTO.AssignmentSubmissionDTO)
	 */
	@Override
	public Map<String, Object> executeVerification(String rootOrg, AssignmentSubmissionDTO submittedData)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, submittedData.getUserId())) {
			throw new BadRequestException("Invalid User : " + submittedData.getUserId());
		}

		Map<String, Object> output = new HashMap<String, Object>();

		exerciseRepo.validateExerciseID(submittedData.getResourceId());
		output = verifyPythonAssignment(submittedData);
		return output;
	}

	/*
	 * return whether verification passed or failed
	 */
	private SubmitDataDTO getVerifyStatus(String verifyResult) throws Exception {
		SubmitDataDTO submitData = new SubmitDataDTO();

		JSONObject verifyResJson = new JSONObject(verifyResult);
		String resultSummary = String.valueOf(verifyResJson.get("ResultSummary"));
		JSONObject resSummaryJson = new JSONObject(resultSummary);
		int no_of_failedStructuralTC = Integer.parseInt((String) resSummaryJson.get("NumberFailedStructuralTC"));
		int no_of_failedActualTC = Integer.parseInt((String) resSummaryJson.get("NumberFailedActualTC"));
		int no_of_failedSampleTC = Integer.parseInt((String) resSummaryJson.get("NumberFailedSampleTC"));
		int no_of_passedStructuralTC = Integer.parseInt((String) resSummaryJson.get("NumberPassedStructuralTC"));
		int no_of_passedActualTC = Integer.parseInt((String) resSummaryJson.get("NumberPassedActualTC"));
		int no_of_passedSampleTC = Integer.parseInt((String) resSummaryJson.get("NumberPassedSampleTC"));
		int totalTestcases = no_of_failedStructuralTC + no_of_failedActualTC + no_of_failedSampleTC
				+ no_of_passedStructuralTC + no_of_passedActualTC + no_of_passedSampleTC;

		float maxMarks = Float.parseFloat((String) resSummaryJson.get("TotalMarks"));
		float marks = Float.parseFloat((String) resSummaryJson.get("MarksScored"));
		submitData.setTotal_testcases(totalTestcases);
		submitData.setTestcases_passed(no_of_passedStructuralTC + no_of_passedActualTC + no_of_passedSampleTC);
		submitData.setTestcases_failed(no_of_failedStructuralTC + no_of_failedActualTC + no_of_failedSampleTC);
		submitData.setResult_percent((float) (marks / maxMarks * 100.0));

		return submitData;

	}

	/*
	 * In this method testcases with solution is sent to pyeval tool and response is
	 * returned.
	 */
	private Map<String, Object> verifyPythonAssignment(AssignmentSubmissionDTO data) throws Exception {

		String verifyJson = "";

		List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
				Arrays.asList(new String[] { data.getResourceId() }), new String[] { "artifactUrl" }, null);
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
		// uncomment when running locally
// 		answerKeyUrl = answerKeyUrl.replaceAll("wingspan-staging.infosysapps.com", "private-content-service");

		// This was done because answer key was present in two instances
		if (answerKeyUrl.contains("private-content-service") || answerKeyUrl.contains("private-static-host"))
			verifyJson = contentService.getContentStoreData(answerKeyUrl);
		else {
			verifyJson = contentService.getKeyFromContentStore(answerKeyUrl);
		}

		JSONObject keyObject = new JSONObject(verifyJson);
		verifyJson = String.valueOf(keyObject.get("fpTestCase"));
		Map<String, Object> responseData = pyevalVerify(verifyJson, data);
		return responseData;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> pyevalVerify(String verifyJson, AssignmentSubmissionDTO data) throws Exception {
		String responseData;

		String serviceIp = lexServerProperties.getPyeValServiceIp();
		String servicePort = lexServerProperties.getPyeValServicePort();
		String serviceName = lexServerProperties.getPyeValServiceName();

		JSONObject testCaseObject = new JSONObject(verifyJson);
		testCaseObject = testCaseObject.put("unique_id", data.getUserId());
		String traineeScriptsStr = String.valueOf(testCaseObject.get("trainee_scripts"));
		JSONObject traineeScripts = new JSONObject(traineeScriptsStr);
		Iterator<String> keys = traineeScripts.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			traineeScripts = traineeScripts.put(String.valueOf(key), data.getUser_solution().trim());
		}
		testCaseObject = testCaseObject.put("trainee_scripts", traineeScripts);

		verifyJson = testCaseObject.toString();

		String url = "http-url://" + serviceIp + ":" + servicePort + serviceName;
		try {
			responseData = iapVerificationService.postSolutionString(url, verifyJson);
		} catch (Exception ex) {
			throw new ApplicationLogicError("Error in Pyeval Verifying Engine",ex);
		}
		JSONObject resp = new JSONObject(responseData);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// this is done because resultsummary is not sent in case if error.
		if (!resp.isNull("Error") || !resp.isNull("error")) {
			throw new ExerciseCodeVerifcationException("Error occured in Pyeval - Python Execution Service");
		} else {
			SubmitDataDTO verifySummary = getVerifyStatus(resp.toString());
			responseData = utilService.analysePythonVerificationResponse(resp);
			resultMap.put("verifySummary", verifySummary);
			resultMap.put("verifyResult", responseData);
		}
		return resultMap;
	}

	

}
