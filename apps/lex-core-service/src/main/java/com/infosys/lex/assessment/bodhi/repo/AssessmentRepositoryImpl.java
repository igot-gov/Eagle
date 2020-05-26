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
package com.infosys.lex.assessment.bodhi.repo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.utils.UUIDs;
import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;
import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.common.service.ContentService;

@Repository
public class AssessmentRepositoryImpl implements AssessmentRepository {

	@Autowired
	ContentService contentService;

	@Autowired
	UserAssessmentSummaryRepository userAssessmentSummaryRepo;

	@Autowired
	UserQuizSummaryRepository userQuizSummaryRepo;

	@Autowired
	UserAssessmentMasterRepository userAssessmentMasterRepo;

	@Autowired
	UserQuizMasterRepository userQuizMasterRepo;

	@Autowired
	UserAssessmentByContentUserRepository userAssessmentByContentUserRepo;

	@Autowired
	UserAssessmentTopPerformerRepository userAssessmentTopPerformerRepository;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.AssessmentRepository#getAssessmentAnswerKey(java.
	 * lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAssessmentAnswerKey(String artifactUrl) throws Exception {

		Map<String, Object> ret = new HashMap<String, Object>();
		Map<String, Object> m = new HashMap<String, Object>();
		// get answers from the content store
		if (artifactUrl.contains("private-content-service") || artifactUrl.contains("private-static-host")) {
			m = contentService.getMapFromContentStore(artifactUrl);
		} else {
			m = contentService.getAssessmentKeyFromContentStore(artifactUrl);
		}

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
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.AssessmentRepository#getQuizAnswerKey(java.util.
	 * Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> getQuizAnswerKey(AssessmentSubmissionDTO quizMap) throws Exception {

		// create the quiz answer key
		Map<String, Object> ret = new HashMap<String, Object>();
		for (Map<String, Object> question : quizMap.getQuestions()) {
			List<String> correctOption = new ArrayList<String>();
			if (question.containsKey("questionType")) {
				if (question.get("questionType").toString().toLowerCase().equals("mtf")) {
					for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
						correctOption.add(
								options.get("optionId").toString() + "-" + options.get("text").toString().toLowerCase()
										+ "-" + options.get("match").toString().toLowerCase());
					}
				} else if (question.get("questionType").toString().toLowerCase().equals("fitb")) {
					for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
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
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.AssessmentRepository#insertQuizOrAssessment(java.
	 * util.Map, java.lang.Boolean)
	 */
	@Override
	public Map<String, Object> insertQuizOrAssessment(Map<String, Object> persist, Boolean isAssessment)
			throws Exception {
		Map<String, Object> response = new HashMap<>();
		Date record = new Date();

		// insert assessment and assessment summary
		if (isAssessment) {
			UserAssessmentMasterModel assessment = new UserAssessmentMasterModel(
					new UserAssessmentMasterPrimaryKeyModel(persist.get("rootOrg").toString(), record,
							persist.get("parent").toString(), new BigDecimal((Double) persist.get("result")),
							UUIDs.timeBased()),
					Integer.parseInt(persist.get("correct").toString()), formatter.parse(formatter.format(record)),
					Integer.parseInt(persist.get("incorrect").toString()),
					Integer.parseInt(persist.get("blank").toString()), persist.get("parentContentType").toString(),
					new BigDecimal(60), persist.get("sourceId").toString(), persist.get("title").toString(),
					persist.get("userId").toString());
			UserAssessmentSummaryModel summary = new UserAssessmentSummaryModel();
			UserAssessmentSummaryModel data = userAssessmentSummaryRepo
					.findById(new UserAssessmentSummaryPrimaryKeyModel(persist.get("rootOrg").toString(),
							persist.get("userId").toString(), persist.get("sourceId").toString()))
					.orElse(null);

			if (persist.get("parentContentType").toString().toLowerCase().equals("course")) {
				if (data != null) {
					if (data.getFirstMaxScore() < Float.parseFloat(persist.get("result").toString())) {
						summary = new UserAssessmentSummaryModel(
								new UserAssessmentSummaryPrimaryKeyModel(persist.get("rootOrg").toString(),
										persist.get("userId").toString(), persist.get("sourceId").toString()),
								Float.parseFloat(persist.get("result").toString()), record, data.getFirstPassesScore(),
								data.getFirstPassesScoreDate());
					}
				} else if (Float.parseFloat(persist.get("result").toString()) > JsonKey.ASSESSMENT_PASS_SCORE) {
					summary = new UserAssessmentSummaryModel(
							new UserAssessmentSummaryPrimaryKeyModel(persist.get("rootOrg").toString(),
									persist.get("userId").toString(), persist.get("sourceId").toString()),
							Float.parseFloat(persist.get("result").toString()), record,
							Float.parseFloat(persist.get("result").toString()), record);
				} else {
					summary = new UserAssessmentSummaryModel(
							new UserAssessmentSummaryPrimaryKeyModel(persist.get("rootOrg").toString(),
									persist.get("userId").toString(), persist.get("sourceId").toString()),
							Float.parseFloat(persist.get("result").toString()), record, null, null);
					userAssessmentSummaryRepo.save(summary);

				}
			}
			userAssessmentMasterRepo.updateAssessment(assessment, summary);
		}
		// insert quiz and quiz summary
		else {

			UserQuizMasterModel quiz = new UserQuizMasterModel(
					new UserQuizMasterPrimaryKeyModel(persist.get("rootOrg").toString(), record,
							new BigDecimal((Double) persist.get("result")), UUIDs.timeBased()),
					Integer.parseInt(persist.get("correct").toString()), formatter.parse(formatter.format(record)),
					Integer.parseInt(persist.get("incorrect").toString()),
					Integer.parseInt(persist.get("blank").toString()), new BigDecimal(60),
					persist.get("sourceId").toString(), persist.get("title").toString(),
					persist.get("userId").toString());
			UserQuizSummaryModel summary = new UserQuizSummaryModel(
					new UserQuizSummaryPrimaryKeyModel(persist.get("rootOrg").toString(),
							persist.get("userId").toString(), persist.get("sourceId").toString()),
					record);

			userQuizMasterRepo.updateQuiz(quiz, summary);
		}

		response.put("response", "SUCCESS");
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.AssessmentRepository#getAssessmetbyContentUser(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getAssessmetbyContentUser(String rootOrg, String courseId, String userId)
			throws Exception {

		// get user assessment
		List<Map<String, Object>> assessmentResults = userAssessmentByContentUserRepo
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyParentSourceId(rootOrg, userId, courseId);
		return assessmentResults;
	}

}
