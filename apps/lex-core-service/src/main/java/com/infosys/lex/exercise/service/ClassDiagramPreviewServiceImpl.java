/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.exercise.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.bodhi.repo.Evaluatedmarks;
import com.infosys.lex.exercise.dto.AssignmentSubmissionDTO;
import com.infosys.lex.exercise.util.ClassDiagramUtilService;

@Service
@Qualifier("classDiagramVerificationPreview")
public class ClassDiagramPreviewServiceImpl implements VerificationPreviewService {

	private final ClassDiagramUtilService utilServ;

	private final UserUtilityService userUtilService;
	
	
	@Autowired
	public ClassDiagramPreviewServiceImpl(ClassDiagramUtilService utilServ, UserUtilityService userUtilService) {
		this.utilServ = utilServ;
		this.userUtilService = userUtilService;
	}

	/**
	 * This method returns the map of class diagram preview
	 * 
	 * @param rootOrg
	 * @param org
	 * @param submittedData
	 * @return
	 * @throws Exception
	 */

	@Override
	public Map<String, Object> executeVerificationPreview(String rootOrg, String org,
			AssignmentSubmissionDTO submittedData) throws Exception {
		String contentId = submittedData.getResourceId();
		String userId = submittedData.getUserId();

		Map<String, Object> contentMeta = userUtilService.getContentMeta(contentId, rootOrg, org, new String[] {
				"artifactUrl", "creatorContacts", "publisherDetails", "trackContacts", "identifier", "resourceType" });
		if (!userUtilService.validatePreviewUser(rootOrg, org, userId, contentMeta))
			throw new AccessForbidenError("User not Authorized");
		Map<String, Object> output = new HashMap<String, Object>();
		Evaluatedmarks verifyResult = null;

		if (!contentMeta.containsKey("resourceType") || contentMeta.get("resourceType") == null
				|| !contentMeta.get("resourceType").toString().equalsIgnoreCase("exercise"))
			throw new InvalidDataInputException("invalid.exercise");

		verifyResult = verifyAssignmentForPreview(submittedData, contentMeta);
		boolean verifyStatus = (verifyResult.getMarksPercent() == 100.0);

		output.put("verifyResult", verifyResult);
		output.put("verifyStatus", verifyStatus);

		return output;
	}

	private Evaluatedmarks verifyAssignmentForPreview(AssignmentSubmissionDTO submittedData,
			Map<String, Object> contentMeta) throws Exception {
		Evaluatedmarks evaluatedAssignment = new Evaluatedmarks();
		if (submittedData != null) {
			if (!(submittedData.getUser_solution() == null || submittedData.getUser_solution().equals(""))) {

				String answerKey = "";
				int qpMarks = 100;

				answerKey = userUtilService.getAnswerKeyForExerciseAuthoringPreview(contentMeta);

				if (!(answerKey == null || answerKey.equals(""))) {
//					Map<String, Object> formattedDataMap = new HashMap<String, Object>();
//					generateArrays(answerKey, false, formattedDataMap);
//					generateArrays(submittedData.getUser_solution(), true, formattedDataMap);
//
//					evaluatedAssignment = evaluateScripts( qpMarks, formattedDataMap);

					utilServ.formatAndEvaluateUserSolution(submittedData.getUser_solution(), answerKey,
							qpMarks);

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
}
