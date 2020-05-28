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
package com.infosys.lex.assessment.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.infosys.lex.common.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.assessment.bodhi.repo.AssessmentRepository;
import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;
import com.infosys.lex.assessment.util.AssessmentUtilService;
import com.infosys.lex.badge.bodhi.repo.BadgeRepository;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.ContentMetaConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.progress.service.ContentProgressService;

@Service
public class AssessmentServiceImpl implements AssessmentService {

	@Autowired
	AssessmentRepository repository;

	@Autowired
	BadgeRepository bRepository;

	@Autowired
	ContentService contentService;

	@Autowired
	ContentProgressService contentProgressService;

	@Autowired
	UserUtilityService userUtilitySvc;

	@Autowired
	AssessmentUtilService assessUtilServ;

	String submissionMimeType = "backend/submission";

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.AssessmentService#submitAssessment(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> submitAssessment(String rootOrg, AssessmentSubmissionDTO data, String userId)
			throws Exception {

		// verify UserId
		if (!userUtilitySvc.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.userid");
		}

		Map<String, Object> ret = new HashMap<String, Object>();

		// assessment meta
		List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(
				new ArrayList<String>(Arrays.asList(new String[] { data.getIdentifier() })),
				new String[] { "artifactUrl", "collections", "contentType" }, "Live");

		if (sourceList.size() == 0) {
			throw new InvalidDataInputException("invalid.resource");
		}

		Map<String, Object> source = sourceList.get(0);
//		 assessment answers
		Map<String, Object> answers = new HashMap<String, Object>();
		if (data.isAssessment())
			answers = repository
					.getAssessmentAnswerKey(source.get("artifactUrl").toString().replaceAll(".json", "-key.json"));

		else
			answers = repository.getQuizAnswerKey(data);

		Map<String, Object> resultMap = assessUtilServ.validateAssessment(data.getQuestions(), answers);
		Double result = (Double) resultMap.get("result");
		Integer correct = (Integer) resultMap.get("correct");
		Integer blank = (Integer) resultMap.get("blank");
		Integer inCorrect = (Integer) resultMap.get("incorrect");

		Map<String, Object> persist = new HashMap<String, Object>();

		// Fetch parent of an assessment with status live
		List<Map<String, Object>> parentList = (List<Map<String, Object>>) source.get("collections");
		List<String> parentIds = new ArrayList<>();
		parentList.forEach(item -> parentIds.add(item.get("identifier").toString()));
		String parentId = "";
		if (parentList != null && parentList.size() > 0) {
			List<Map<String, Object>> parentMeta = contentService.getMetaByIDListandSource(parentIds,
					new String[] { "status", "identifier" }, null);
			for (Map<String, Object> parentData : parentMeta) {
				if (Arrays.asList(ContentMetaConstants.LIVE_CONTENT_STATUS).contains(parentData.get("status"))) {
					parentId = parentData.get("identifier").toString();
					break;
				}
			}
		}
		persist.put("parent", parentId);

		persist.put("result", result);
		persist.put("sourceId", data.getIdentifier());
		persist.put("title", data.getTitle());
		persist.put("rootOrg", rootOrg);
		persist.put("userId", userId);
		persist.put("correct", correct);
		persist.put("blank", blank);
		persist.put("incorrect", inCorrect);

		if (data.isAssessment()) {
			Map<String, Object> tempSource = null;
			// get parent data for assessment
			if (((List<Object>) source.get("collections")).size() > 0) {

				List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
						new ArrayList<String>(Arrays.asList(parentId)), new String[] { "contentType", "collections" },
						null);

				if (sources.size() == 0) {
					throw new InvalidDataInputException("invalid.resource");
				}
				tempSource = sources.get(0);

				persist.put("parentContentType", tempSource.get("contentType"));

			} else {
				persist.put("parentContentType", "");
			}
			// insert into assessment table
			repository.insertQuizOrAssessment(persist, true);

			if (tempSource != null && result >= 60) {
				if (tempSource.get("contentType").equals("Course")) {
					// insert certificate and medals
					String courseId = parentId;

					// Fetch live programs of the course
					List<Map<String, Object>> programsOfCourse = (List<Map<String, Object>>) tempSource
							.get("collections");
					List<String> allPrograms = new ArrayList<>();
					programsOfCourse.forEach(program -> allPrograms.add(program.get("identifier").toString()));
					boolean parent = programsOfCourse.size() > 0 ? true : false;
					List<String> programId = new ArrayList<String>();

					if (allPrograms != null && allPrograms.size() > 0) {
						List<Map<String, Object>> programMeta = contentService.getMetaByIDListandSource(allPrograms,
								new String[] { "identifier", "status" }, null);
						for (Map<String, Object> programData : programMeta) {
							if (Arrays.asList(ContentMetaConstants.LIVE_CONTENT_STATUS)
									.contains(programData.get("status"))) {
								programId.add(programData.get("identifier").toString());
							}
						}
					}

					bRepository.insertInBadges(rootOrg, courseId, programId, userId, parent);
					bRepository.insertCourseAndQuizBadge(rootOrg, userId, "Course", data.getIdentifier());
				}
			}
			contentProgressService.callProgress(rootOrg, userId, data.getIdentifier(), submissionMimeType,
					Float.parseFloat(result.toString()));
		} else {
			// insert into quiz table
			persist.remove("parent");
			repository.insertQuizOrAssessment(persist, false);
			bRepository.insertCourseAndQuizBadge(rootOrg, userId, "Quiz", data.getIdentifier());
			contentProgressService.callProgress(rootOrg, userId, data.getIdentifier(), submissionMimeType, 100f);
		}

		ret.put("result", result);
		ret.put("correct", correct);
		ret.put("inCorrect", inCorrect);
		ret.put("blank", blank);
		ret.put("total", blank + inCorrect + correct);
		ret.put("passPercent", 60);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.AssessmentService#submitAssessmentByIframe(java.util
	 * .Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> submitAssessmentByIframe(String rootOrg, Map<String, Object> request) throws Exception {
		Map<String, Object> response = new HashMap<>();
		boolean invalidRequest = true;
		boolean resultWrong = true;
		String errorMsg = "";
		Double result = (Double) request.get("result");

		// Validating RequestBody
		if (request.get("parent") == null) {
			errorMsg = "COURSE_ID_CANT_BE_NULL";
		} else if (request.get("userId") == null) {
			errorMsg = "USER_ID_CANT_BE_NULL";
		} else if (request.get("sourceId") == null) {
			errorMsg = "ASSESSMENT_ID_CANT_BE_NULL";
		} else if (request.get("title") == null) {
			errorMsg = "ASSESSMENT_NAME_CANT_BE_NULL";
		} else if (request.get("correct") == null) {
			errorMsg = "CORRECT_COUNT_CANT_BE NULL";
		} else if (request.get("incorrect") == null) {
			errorMsg = "INCORRECT_COUNT_CANT_BE NULL";
		} else if (request.get("blank") == null) {
			errorMsg = "NOT_ANSWERED_COUNT_CANT_BE NULL";
		} else if (request.get("result") == null) {
			invalidRequest = true;
			errorMsg = "RESULT_PERCENTAGE_CANT_BE NULL";
		} else if (request.get("parentContentType") == null) {
			errorMsg = "CONTENT_TYPE_CANT_BE_NULL";
		} else if (!(request.get("parentContentType").toString().equals("Course")
				|| request.get("parentContentType").toString().equals("Learning Module"))) {
			errorMsg = "CONTENT_TYPE_CAN_ONLY_BE_COURSE_OR_LEARNING_MODULE";
		} else if ((Integer) request.get("correct") < 0) {
			errorMsg = "CORRECT_COUNT_MUST_BE_POSITIVE";
		} else if ((Integer) request.get("incorrect") < 0) {
			errorMsg = "INCORRECT_COUNT_MUST_BE_POSITIVE";
		} else if ((Integer) request.get("blank") < 0) {
			errorMsg = "NOT_ANSWERED_COUNT_MUST_BE_POSITIVE";
		} else if (resultWrong) {
			Integer correctCount = (Integer) request.get("correct");
			Integer incorrectCount = (Integer) request.get("incorrect");
			Integer notAnsweredCount = (Integer) request.get("blank");
			Double resultValidation = 0d;
			resultValidation = (100d * correctCount) / (correctCount + incorrectCount + notAnsweredCount);
			resultValidation = BigDecimal.valueOf(resultValidation).setScale(2, BigDecimal.ROUND_HALF_EVEN)
					.doubleValue();
			if (result.compareTo(resultValidation) != 0) {
				errorMsg = "RESULT_IS_WRONG";
			} else {
				resultWrong = false;
				invalidRequest = false;
			}
		} else {
			invalidRequest = false;
		}

		if (invalidRequest) {
			throw new BadRequestException(errorMsg);
		}

		try {
			List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
					new ArrayList<String>(Arrays.asList(new String[] { request.get("sourceId").toString() })),
					new String[] { "artifactUrl", "collections", "contentType" }, null);

			if (sources.size() == 0) {
				throw new InvalidDataInputException("invalid.resource");
			}

			Map<String, Object> source = sources.get(0);

			sources = contentService.getMetaByIDListandSource(
					new ArrayList<String>(
							Arrays.asList(new String[] { ((List<Map<String, Object>>) (source.get("collections")))
									.get(0).get("identifier").toString() })),
					new String[] { "contentType", "collections" }, null);

			if (sources.size() == 0) {
				throw new InvalidDataInputException("invalid.resource");
			}

			Map<String, Object> tempSource = sources.get(0);

			repository.insertQuizOrAssessment(request, true);
			if (tempSource != null && result >= 60) {
				if (tempSource.get("contentType").equals("Course")) {
					// insert certificate and medals
					String courseId = request.get("parent").toString();
					boolean parent = ((List<Object>) tempSource.get("collections")).size() > 0 ? true : false;
					List<String> programId = new ArrayList<String>();
					for (Map<String, Object> collection : ((List<Map<String, Object>>) (tempSource
							.get("collections")))) {
						programId.add(collection.get("identifier").toString());
					}
					bRepository.insertInBadges(rootOrg, courseId, programId, request.get("userId").toString(), parent);
				}
			}
			contentProgressService.callProgress(rootOrg, request.get("userId").toString(),
					request.get("sourceId").toString(), submissionMimeType, Float.parseFloat(result.toString()));
			response.put("message", "Record Inserted Sucessfully !!");
		} catch (Exception e) {
			throw new ApplicationLogicError("REQUEST_COULD_NOT_BE_PROCESSED", e);
		}

		return response;
	}

	// A method to Format Data in the FrontEndFormat
	private List<Map<String, Object>> getAssessments(List<Map<String, Object>> result) {
		List<Map<String, Object>> assessments = new ArrayList<>();
		for (Map<String, Object> map : result) {
			Map<String, Object> assessmentData = new HashMap<>();
			String res = map.get("result_percent").toString();
			assessmentData.put("result", new BigDecimal(res).setScale(2, BigDecimal.ROUND_UP));
			assessmentData.put("correctlyAnswered", map.get("correct_count"));
			assessmentData.put("wronglyAnswered", map.get("incorrect_count"));
			assessmentData.put("notAttempted", map.get("not_answered_count"));
			assessmentData.put("takenOn", map.get("ts_created"));
			assessments.add(assessmentData);
		}
		return assessments;
	}

	/*
	 * A service to produce a JSON response with processed Data on all Assessments
	 * such as fristPassedts,maxScore,etc , and a list of Past Assessments.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.AssessmentService#getAssessmentByContentUser(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getAssessmentByContentUser(String rootOrg, String course_id, String user_id)
			throws Exception {
		Map<String, Object> result = new TreeMap<>();
		try {
			// get all submission data from cassandra
			List<Map<String, Object>> assessmentResults = repository.getAssessmetbyContentUser(rootOrg, course_id,
					user_id);
			// retain only those fields that need to be sent to front end
			List<Map<String, Object>> assessments = getAssessments(assessmentResults);

			// initialize variables to calculate first attempt and max score
			Integer noOfAttemptsForPass = 0;
			Integer noOfAttemptsForMaxScore = 0;
			boolean passed = false;
			Object firstPassTs = null;
			BigDecimal max = new BigDecimal(-Double.MIN_VALUE);
			Object maxScoreTs = null;

			/*
			 * Logic to Find The First Time Passed and The Max Score Attained along with
			 * Their No of Attempts and Timestamps
			 */
			for (int i = assessments.size() - 1; i > -1; i--) {
				Map<String, Object> row = assessments.get(i);
				BigDecimal percentage = (BigDecimal) row.get("result");
				/*
				 * Logic to Obtain the First Pass using a Passed flag to attain the Attempts as
				 * well as the first Time passed Time Stamp
				 */
				if (!passed) {
					noOfAttemptsForPass++;
					if (percentage.doubleValue() >= 60.0) {
						passed = true;
						firstPassTs = row.get("takenOn");
					}
				}

				/*
				 * Logic to Obtain the max scored assessment comparison to attain the Attempts
				 * as well as the Max Scored Assessment Time Stamp
				 */
				if (max.compareTo(percentage) < 0) {
					max = (BigDecimal) row.get("result");
					maxScoreTs = row.get("takenOn");
					noOfAttemptsForMaxScore = (assessments.size() - i);
				}
			}

			/* Populating the Response to give Processed Data to Front End */
			if (assessments.size() > 0) {
				if (passed) {
					result.put("firstPassOn", firstPassTs);
					result.put("attemptsToPass", noOfAttemptsForPass);
				}

				result.put("maxScore", max);
				result.put("maxScoreAttainedOn", maxScoreTs);
				result.put("attemptsForMaxScore", noOfAttemptsForMaxScore);
			}
			result.put("pastAssessments", assessments);
		} catch (NullPointerException e) {
			throw new ApplicationLogicError("REQUEST_COULD_NOT_BE_PROCESSED", e);
		}
		return result;
	}

}
