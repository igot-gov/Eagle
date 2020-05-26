/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.ExerciseCodeVerifcationException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.util.PythonVerificationUtilService;

@Service
@Qualifier("pythonVerificationPreview")
public class PythonVerifyPreviewServiceImpl implements VerificationPreviewService {

	private final UserUtilityService userUtilService;
	private final LexServerProperties lexServerProperties;
	private final IAPVerificationService iapVerificationService;
	private final ExerciseRepository exerciseRepo;
	private final PythonVerificationUtilService utilServ;

	
	
	
	
	@Autowired
	public PythonVerifyPreviewServiceImpl(UserUtilityService userUtilService, 
			LexServerProperties lexServerProperties, IAPVerificationService iapVerificationService,
			ExerciseRepository exerciseRepo, PythonVerificationUtilService utilServ) {
		this.userUtilService = userUtilService;
		this.lexServerProperties = lexServerProperties;
		this.iapVerificationService = iapVerificationService;
		this.exerciseRepo = exerciseRepo;
		this.utilServ = utilServ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.PythonVerificationService#executePythonVerification(
	 * com.infosys.core.DTO.AssignmentSubmissionDTO)
	 */
	@Override
	public Map<String, Object> executeVerificationPreview(String rootOrg, String org,
			AssignmentSubmissionDTO submittedData) throws Exception {

		Map<String, Object> output = new HashMap<String, Object>();

		exerciseRepo.validateExerciseID(submittedData.getResourceId());
		output = verifyPythonAssignment(rootOrg, org, submittedData);
		return output;
	}

	/*
	 * In this method testcases with solution is sent to pyeval tool and response is
	 * returned.
	 */
	private Map<String, Object> verifyPythonAssignment(String rootOrg, String org, AssignmentSubmissionDTO data)
			throws Exception {

		String verifyJson = "";
		String contentId = data.getResourceId();
		String userId = data.getUserId();
		Map<String, Object> contentMeta = userUtilService.getContentMeta(contentId, rootOrg, org, new String[] {
				"artifactUrl", "creatorContacts", "publisherDetails", "trackContacts", "identifier", "resourceType" });
		if (!userUtilService.validatePreviewUser(rootOrg, org, userId, contentMeta))
			throw new AccessForbidenError("User not Authorized");

		verifyJson = iapVerificationService.getAnswerKeyForExerciseAuthoringPreview(contentMeta);

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
			responseData = utilServ.analysePythonVerificationResponse(resp);
			String responseDataWithAllTestcases = utilServ.verifySolutionToolAnalyzeForPreview(resp);
			resultMap.put("verifySummary", verifySummary);
			resultMap.put("verifyResult", responseData);
			resultMap.put("verifyResultAllTC", responseDataWithAllTestcases);

		}
		return resultMap;
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
}
