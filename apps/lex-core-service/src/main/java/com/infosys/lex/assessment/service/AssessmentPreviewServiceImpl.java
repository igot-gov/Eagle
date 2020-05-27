/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.assessment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;
import com.infosys.lex.assessment.util.AssessmentUtilService;
import com.infosys.lex.common.service.UserUtilityService;

@Service
public class AssessmentPreviewServiceImpl implements AssessmentPreviewService {

	@Autowired
	UserUtilityService userUtilServ;

	@Autowired
	AssessmentUtilService assessUtilServ;

	@Override
	public Map<String, Object> getAssessmentVerifyPreview(String rootOrg, String org, String userId,
			AssessmentSubmissionDTO data) throws Exception {
		String contentId = data.getIdentifier();
		Map<String, Object> contentMeta = userUtilServ.getContentMeta(contentId, rootOrg, org, new String[] {
				"artifactUrl", "creatorContacts", "publisherDetails", "trackContacts", "identifier", "resourceType" });
		if (!userUtilServ.validatePreviewUser(rootOrg, org, userId, contentMeta))
			throw new AccessForbidenError("User not Authorized");
		Map<String, Object> ret = new HashMap<String, Object>();

		Map<String, Object> answers = this.fetchAndProcessAnswerKeyForAssessment(contentMeta);
		Map<String, Object> resultMap = assessUtilServ.validateAssessment(data.getQuestions(), answers);
		Double result = (Double) resultMap.get("result");
		Integer correct = (Integer) resultMap.get("correct");
		Integer blank = (Integer) resultMap.get("blank");
		Integer inCorrect = (Integer) resultMap.get("incorrect");

		ret.put("result", result);
		ret.put("correct", correct);
		ret.put("inCorrect", inCorrect);
		ret.put("blank", blank);
		ret.put("total", blank + inCorrect + correct);
		ret.put("passPercent", 60);
		ret.put("answer", answers);

		return ret;

	}

	/*
	 * This method fetches the answer key and processes it from authoring bucket
	 */

	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchAndProcessAnswerKeyForAssessment(Map<String, Object> contentMeta) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> m = assessUtilServ.getAnswerKeyForAssessmentAuthoringPreview(contentMeta);
		try {
			for (Map<String, Object> question : (List<Map<String, Object>>) m.get("questions")) {
				List<String> correctOption = new ArrayList<String>();
				if (question.containsKey("questionType")) {
					if (question.get("questionType").toString().toLowerCase().equals("mtf")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if ((boolean) options.get("isCorrect"))
								correctOption.add(options.get("optionId").toString() + "-"
										+ options.get("text").toString().toLowerCase() + "-"
										+ options.get("match").toString().toLowerCase());
						}
					} else if (question.get("questionType").toString().toLowerCase().equals("fitb")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if ((boolean) options.get("isCorrect"))
								correctOption.add(options.get("optionId").toString() + "-"
										+ options.get("text").toString().toLowerCase());
						}
					} else if (question.get("questionType").toString().toLowerCase().equals("mcq-sca")
							|| question.get("questionType").toString().toLowerCase().equals("mcq-mca")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if ((boolean) options.get("isCorrect"))
								correctOption.add(options.get("optionId").toString());
						}
					}
				} else {
					for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
						if ((boolean) options.get("isCorrect"))
							correctOption.add(options.get("optionId").toString());
					}
				}
				ret.put(question.get("questionId").toString(), correctOption);
			}
		} catch (Exception e) {
			throw new ApplicationLogicError("Error processing answer Key json. Error : " + e.getMessage(),e);
		}
		return ret;
	}

}
