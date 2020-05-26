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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.util.MultiLangVerificationUtilService;

@Service
@Qualifier("multiLangVerification")
public class MultiLangVerificationServiceImpl implements VerificationService {

	private final UserUtilityService userUtilService;
	private final ContentService contentService;
	private final LexServerProperties lexServerProperties;
	private final IAPVerificationService iapVerificationService;
	private final ExerciseRepository exerciseRepo;
	private final MultiLangVerificationUtilService utilServ;

	
	@Autowired
	public MultiLangVerificationServiceImpl(UserUtilityService userUtilService, ContentService contentService,
			LexServerProperties lexServerProperties, IAPVerificationService iapVerificationService,
			ExerciseRepository exerciseRepo, MultiLangVerificationUtilService exerciseUtilServ) {
		this.userUtilService = userUtilService;
		this.contentService = contentService;
		this.lexServerProperties = lexServerProperties;
		this.iapVerificationService = iapVerificationService;
		this.exerciseRepo = exerciseRepo;
		this.utilServ = exerciseUtilServ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.MultiLangVerificationService#
	 * executeMultiLangVerification(com.infosys.core.DTO.AssignmentSubmissionDTO)
	 */
	@Override
	public Map<String, Object> executeVerification(String rootOrg, AssignmentSubmissionDTO submittedData)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, submittedData.getUserId())) {
			throw new BadRequestException("Invalid User : " + submittedData.getUserId());
		}
		Map<String, Object> output = new HashMap<String, Object>();
		if (submittedData.getLanguage_code() == null)
			throw new InvalidDataInputException("missing.language.code");

		exerciseRepo.validateExerciseID(submittedData.getResourceId());
		output = verifyMultiLangAssignment(submittedData);
		return output;
	}

	private SubmitDataDTO getAllLangVerifyStatus(String verifyResult) throws JSONException {
		JSONObject verifiedResult = new JSONObject(verifyResult);
		SubmitDataDTO submitData = new SubmitDataDTO();
		JSONArray testcaseOutputs = verifiedResult.getJSONArray("testCaseOutputs");
		int testcasePassed = 0;
		int testcaseFailed = 0;
		for (int i = 0; i < testcaseOutputs.length(); i++) {
			JSONObject testcase = testcaseOutputs.getJSONObject(i);
			if (String.valueOf(testcase.get("result")).toLowerCase().equals("passed"))
				testcasePassed++;
			else
				testcaseFailed++;
		}
		int totalTestcase = testcaseFailed + testcasePassed;
		submitData.setTestcases_failed(testcaseFailed);
		submitData.setTestcases_passed(testcasePassed);
		submitData.setTotal_testcases(totalTestcase);
		submitData.setResult_percent((float) (Math.round((float) testcasePassed * 100 * 100 / totalTestcase) / 100));

		return submitData;
	}

	/*
	 * This method uses IAP verification api
	 */
	private Map<String, Object> verifyMultiLangAssignment(AssignmentSubmissionDTO data) throws Exception {
		Map<String, Object> responseData = null;
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
//		answerKeyUrl = answerKeyUrl.replaceAll("private-static-host","IPaddress");
		if (answerKeyUrl.contains("private-content-service") || answerKeyUrl.contains("private-static-host"))
			verifyJson = contentService.getContentStoreData(answerKeyUrl);
		else {
			verifyJson = contentService.getKeyFromContentStore(answerKeyUrl);
		}
		JSONObject keyObject = new JSONObject(verifyJson);
		JSONObject testcaseArray = new JSONObject(String.valueOf(keyObject.get("testcases")));

		JSONObject verificationJson = new JSONObject();
		JSONArray finalTestcaseArray = new JSONArray();
		verificationJson.put("languageCode", data.getLanguage_code());
		verificationJson.put("userId", data.getUserId());
		verificationJson.put("userSol", data.getUser_solution());
		JSONObject testcase = null;
		if (testcaseArray.has("sample") || testcaseArray.get("sample") != null) {
			JSONArray sampleArray = testcaseArray.getJSONArray("sample");
			for (int i = 0; i < sampleArray.length(); i++) {
				testcase = sampleArray.getJSONObject(i);
				if (testcase.has("explanation"))
					testcase.remove("explanation");
				testcase.put("type", "sample");
				testcase.put("id", String.valueOf(testcase.get("id")));
				finalTestcaseArray = finalTestcaseArray.put((Object) testcase);
			}

		}
		JSONArray hiddenArray = testcaseArray.getJSONArray("hidden");

		for (int i = 0; i < hiddenArray.length(); i++) {
			testcase = hiddenArray.getJSONObject(i);
			testcase.put("type", "hidden");
			testcase.put("id", String.valueOf(testcase.get("id")));
			finalTestcaseArray = finalTestcaseArray.put((Object) testcase);
		}

		verificationJson.put("testCases", (Object) finalTestcaseArray);

		responseData = allLangVerify(testcaseArray, verificationJson.toString(), data);

		return responseData;
	}

	/*
	 * This method adds expected output to verified result
	 */
	private Map<String, Object> allLangVerify(JSONObject testcaseArray, String verifyJson, AssignmentSubmissionDTO data)
			throws Exception {
		String responseData = null;
		String serviceIp = lexServerProperties.getIapServiceIp();
		String servicePort = lexServerProperties.getIapServicePort();
		String serviceName = lexServerProperties.getIapServiceName();

		String url = "http-url://" + serviceIp + ":" + servicePort + serviceName;

		try {
			responseData = iapVerificationService.postSolutionString(url, verifyJson);
		} catch (Exception ex) {
			throw new ApplicationLogicError("Error in Verification Engine",ex);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();

		SubmitDataDTO verifySummary = getAllLangVerifyStatus(responseData);
		resultMap.put("verifySummary", verifySummary);

//		JSONObject resp = new JSONObject(responseData);
//
//		resp = analyseAllLangVerification(resp);
//		addExpected(testcaseArray, resp);
//		
		
		JSONObject resp = utilServ.analyseVerifcationWithExpectedOutput(testcaseArray, responseData,false);
		if (!(String.valueOf(resp.get("status")).toLowerCase().equals("done")))
			throw new ApplicationLogicError("Error in verifying");
		resultMap.put("verifyResult", resp.toString());

		return resultMap;

	}
	
	
}
