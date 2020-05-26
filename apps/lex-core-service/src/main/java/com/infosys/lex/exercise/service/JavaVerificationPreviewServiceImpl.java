/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
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
import com.infosys.lex.exercise.util.JavaVerificationUtilService;

@Service
@Qualifier("javaVerificationPreview")
public class JavaVerificationPreviewServiceImpl implements VerificationPreviewService {

	private final ExerciseRepository exerciseRepo;

	private final UserUtilityService userUtilService;

	private final LexServerProperties lexServerProperties;

	private final JavaVerificationUtilService utilService;

	private final IAPVerificationService iapVerificationService;

	@Autowired
	public JavaVerificationPreviewServiceImpl(ExerciseRepository exerciseRepo, UserUtilityService userUtilService,
			LexServerProperties lexServerProperties, JavaVerificationUtilService utilService,
			IAPVerificationService iapVerificationService) {
		this.exerciseRepo = exerciseRepo;
		this.userUtilService = userUtilService;
		this.lexServerProperties = lexServerProperties;
		this.utilService = utilService;
		this.iapVerificationService = iapVerificationService;
	}

	@Override
	public Map<String, Object> executeVerificationPreview(String rootOrg, String org,
			AssignmentSubmissionDTO submittedData) throws Exception {
		Map<String, Object> output = new HashMap<String, Object>();

		exerciseRepo.validateExerciseID(submittedData.getResourceId());

		output = this.verifyJavaAssignment(rootOrg, org, submittedData);
		return output;
	}

	/*
	 * This method creates the submitdata object containing brief summary of the
	 * verfiication result
	 */
	private SubmitDataDTO getVerifyStatus(JSONObject verifyResJson) throws Exception {
		SubmitDataDTO submitData = new SubmitDataDTO();

		JSONObject resultSummary = verifyResJson.getJSONObject("resultSummary");
		int totalTestcases = resultSummary.getInt("totalTestcases");
		int totalTestcasesPassed = resultSummary.getInt("totalTestcasesPassed");
		float resultPercent = 0f;
		if (totalTestcases != 0)
			resultPercent = (float) (totalTestcasesPassed / totalTestcases * 100);

		submitData.setTotal_testcases(totalTestcases);
		submitData.setTestcases_passed(totalTestcasesPassed);
		submitData.setTestcases_failed(totalTestcases - totalTestcasesPassed);
		submitData.setResult_percent(resultPercent);

		return submitData;

	}

	/*
	 * In this method testcases with solution is sent to java evaluation tool and
	 * response is parsed returned.
	 */
	private Map<String, Object> verifyJavaAssignment(String rootOrg, String org, AssignmentSubmissionDTO data)
			throws Exception {

		String contentId = data.getResourceId();
		String userId = data.getUserId();
		Map<String, Object> contentMeta = userUtilService.getContentMeta(contentId, rootOrg, org, new String[] {
				"artifactUrl", "creatorContacts", "publisherDetails", "trackContacts", "identifier", "resourceType" });
		if (!userUtilService.validatePreviewUser(rootOrg, org, userId, contentMeta))
			throw new AccessForbidenError("User not Authorized");

		String verifyJson;

		verifyJson = iapVerificationService.getAnswerKeyForExerciseAuthoringPreview(contentMeta);
		JSONObject keyObject = new JSONObject(verifyJson);
		verifyJson = String.valueOf(keyObject.get("fpTestCase"));
		Map<String, Object> responseData = javaEval(keyObject.toString(), data);
		return responseData;
	}

	private Map<String, Object> javaEval(String verifyJson, AssignmentSubmissionDTO data) throws Exception {
		String responseData;

		String serviceIp = lexServerProperties.getJavaEvalServerHost();
		String servicePort = lexServerProperties.getJavaEvalServerPort();
		String serviceName = lexServerProperties.getJavaEvalEndpoint();

		JSONObject testCaseObject = new JSONObject(verifyJson);
		testCaseObject = testCaseObject.put("username", data.getUserId());
		testCaseObject.put("traineeSolution", data.getUser_solution());

		verifyJson = testCaseObject.toString();
		System.out.println(verifyJson);

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
			throw new ExerciseCodeVerifcationException("Error occured in JavaEval - Java Execution Service");
		} else {
			JSONObject parsedResponse = utilService.analyseJavaVerificationResponseForPreview(resp);
			SubmitDataDTO verifySummary = this.getVerifyStatus(parsedResponse);

			resultMap.put("verifySummary", verifySummary);
			resultMap.put("verifyResult", new ObjectMapper().readValue(parsedResponse.toString(), HashMap.class));
		}
		return resultMap;
	}

}
