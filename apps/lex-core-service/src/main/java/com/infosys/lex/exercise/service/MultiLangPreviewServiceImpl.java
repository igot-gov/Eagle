/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.util.MultiLangVerificationUtilService;

@Service
@Qualifier("multiLangVerificationPreview")
public class MultiLangPreviewServiceImpl implements VerificationPreviewService {

	private final UserUtilityService userUtilService;
	private final LexServerProperties lexServerProperties;
	private final IAPVerificationService iapVerificationService;
	private final ExerciseRepository exerciseRepo;
	private final MultiLangVerificationUtilService utilServ;

	
	
	@Autowired
	public MultiLangPreviewServiceImpl(UserUtilityService userUtilService,
			LexServerProperties lexServerProperties, IAPVerificationService iapVerificationService,
			ExerciseRepository exerciseRepo, MultiLangVerificationUtilService utilServ) {
		this.userUtilService = userUtilService;
		this.lexServerProperties = lexServerProperties;
		this.iapVerificationService = iapVerificationService;
		this.exerciseRepo = exerciseRepo;
		this.utilServ = utilServ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.MultiLangVerificationService#
	 * executeMultiLangVerification(com.infosys.core.DTO.AssignmentSubmissionDTO)
	 */
	@Override
	public Map<String, Object> executeVerificationPreview(String rootOrg, String org,
			AssignmentSubmissionDTO submittedData) throws Exception {

		Map<String, Object> output = new HashMap<String, Object>();
		if (submittedData.getLanguage_code() == null)
			throw new InvalidDataInputException("missing.language.code");

		exerciseRepo.validateExerciseID(submittedData.getResourceId());
		output = verifyMultiLangAssignment(rootOrg, org, submittedData);
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
	private Map<String, Object> verifyMultiLangAssignment(String rootOrg, String org, AssignmentSubmissionDTO data)
			throws Exception {
		Map<String, Object> responseData = null;
		String verifyJson = "";
		String contentId = data.getResourceId();
		String userId = data.getUserId();
		Map<String, Object> contentMeta = userUtilService.getContentMeta(contentId, rootOrg, org, new String[] {
				"artifactUrl", "creatorContacts", "publisherDetails", "trackContacts", "identifier", "resourceType" });
		if (!userUtilService.validatePreviewUser(rootOrg, org, userId, contentMeta))
			throw new AccessForbidenError("User not Authorized");

		verifyJson = iapVerificationService.getAnswerKeyForExerciseAuthoringPreview(contentMeta);

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
			throw new ApplicationLogicError("Error in Verifying Engine",ex);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();

		SubmitDataDTO verifySummary = getAllLangVerifyStatus(responseData);
		resultMap.put("verifySummary", verifySummary);

//		JSONObject resp = new JSONObject(responseData);
//
//		resp = analyseAllLangVerification(resp);
//		addExpected(testcaseArray, resp);

		JSONObject resp = utilServ.analyseVerifcationWithExpectedOutput(testcaseArray, responseData, true);
		if (!(String.valueOf(resp.get("status")).toLowerCase().equals("done")))
			throw new ApplicationLogicError("Error in verifying");
		resultMap.put("verifyResult", resp.toString());

		return resultMap;

	}

}
