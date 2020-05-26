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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.ResourceNotFoundException;
import com.infosys.lex.exercise.bodhi.repo.ExerciseRepository;
import com.infosys.lex.exercise.bodhi.repo.UserExerciseLastModel;
import com.infosys.lex.exercise.bodhi.repo.UserExerciseLastPrimaryKeyModel;
import com.infosys.lex.exercise.bodhi.repo.UserExerciseRepository;
import com.infosys.lex.exercise.dto.NewCodeExerciseDTO;
import com.infosys.lex.exercise.dto.NewExerciseFeedbackDTO;
import com.infosys.lex.exercise.dto.NewLNDExerciseDTO;
import com.infosys.lex.progress.service.ContentProgressService;


@Service
public class ExerciseServiceImpl implements ExerciseService {

	@Autowired
	ExerciseRepository exerciseRepo;



	@Autowired
	UserExerciseRepository userExerciseRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	ContentProgressService contentProgressService;

	@Autowired
	LexServerProperties serverProps;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

	String submissionMimeType = "backend/submission";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#insertLNDSubmission(com.infosys.core
	 * .DTO.NewLNDExerciseDTO, java.lang.String, java.lang.String)
	 */
	@Override
	public String insertLNDSubmission(String rootOrg, NewLNDExerciseDTO meta, String contentId, String userUUID)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		String ret = "Success";

		if (meta.getResult_percent() == null)
			meta.setResult_percent(0f);
		if (meta.getTotal_testcases() == null)
			meta.setTotal_testcases(0);
		if (meta.getTestcases_passed() == null)
			meta.setTestcases_passed(0);
		if (meta.getTestcases_failed() == null)
			meta.setTestcases_failed(0);

		// validate that the user and content exists
		exerciseRepo.validateExerciseID(contentId);

		// store submission in the database
		String url = meta.getUrl();
		UserExerciseLastPrimaryKeyModel pk = new UserExerciseLastPrimaryKeyModel(rootOrg, userUUID, contentId);
		UserExerciseLastModel exercise = new UserExerciseLastModel(pk, UUIDs.timeBased(), new Date(),
				meta.getResult_percent(), url, meta.getSubmission_type(), meta.getTestcases_failed(),
				meta.getTestcases_passed(), meta.getTotal_testcases(), null, null, null, null, null);
		userExerciseRepo.updateExercise(rootOrg, exercise);

		// Recalculate the progress and update last accessed time stamp
		if (serverProps.getEnableRealTime())
			contentProgressService.callProgress(rootOrg, userUUID, contentId, submissionMimeType, -1f);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#insertCodeSubmission(com.infosys.
	 * core.DTO.NewCodeExerciseDTO, java.lang.String, java.lang.String)
	 */
	@Override
	public String insertCodeSubmission(String rootOrg, NewCodeExerciseDTO meta, String contentId, String userUUID)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		String ret = "Success";

		// validate that the user and content exists
		exerciseRepo.validateExerciseID(contentId);

		// store submission in the database
		String url = meta.getUrl();
		UserExerciseLastPrimaryKeyModel pk = new UserExerciseLastPrimaryKeyModel(rootOrg, userUUID, contentId);
		UserExerciseLastModel exercise = new UserExerciseLastModel(pk, UUIDs.timeBased(), new Date(),
				meta.getResult_percent(), url, meta.getSubmission_type(), meta.getTestcases_failed(),
				meta.getTestcases_passed(), meta.getTotal_testcases(), null, null, null, null, null);
		userExerciseRepo.updateExercise(rootOrg, exercise);

		// Recalculate the progress and update last accessed time stamp
		if (serverProps.getEnableRealTime())
			contentProgressService.callProgress(rootOrg, userUUID, contentId, submissionMimeType,
					meta.getResult_percent());
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#getExerciseSubmissionsByUser(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getExerciseSubmissionsByUser(String rootOrg, String userUUID, String contentId)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		// validate that the user and content exists
		List<Map<String, Object>> ret = new ArrayList<>();
		exerciseRepo.validateExerciseID(contentId);

		// get all exercises submitted by the user
		ret = exerciseRepo.getAllExerciseSubmitted(rootOrg, userUUID, contentId);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.ExerciseService#getLatestData(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getLatestData(String rootOrg, String userUUID, String contentId) throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		List<Map<String, Object>> ret = new ArrayList<>();

		// validate that the user and content exists
		exerciseRepo.validateExerciseID(contentId);

		// get the latest exercise submitted by the user
		ret = exerciseRepo.getLatestExerciseSubmitted(rootOrg, userUUID, contentId);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.ExerciseService#getOneData(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getOneData(String rootOrg, String userUUID, String contentId, String submissionId)
			throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		List<Map<String, Object>> ret = new ArrayList<>();

		// validate that the user, submission and content exists
		exerciseRepo.validateExerciseID(contentId);
		exerciseRepo.validateSubmissionID(submissionId);

		// get a specific exercise
		ret = exerciseRepo.getSpecificExercise(rootOrg, userUUID, contentId, submissionId);
		if (ret.size() == 0)
			throw new ResourceNotFoundException("No such data exists!");
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#insertFeedback(com.infosys.core.DTO.
	 * NewExerciseFeedbackDTO, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String insertFeedback(String rootOrg, NewExerciseFeedbackDTO meta, String contentId, String userUUID,
			String submissionId) throws Exception {
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		if (!userUtilService.validateUser(rootOrg, meta.getEducator_id())) {
			throw new BadRequestException("Invalid Educator Id : " + userUUID);
		}
		String ret = "Success";

		// validate that the user and content exists
		exerciseRepo.validateExerciseID(contentId);
		exerciseRepo.validateSubmissionID(submissionId);

		// add a feedback for a submission
		String url = meta.getUrl();
		UUID uid = UUID.fromString(submissionId);
		Float result = (meta.getRating() * 100f) / meta.getMax_rating();

		UserExerciseLastPrimaryKeyModel pk = new UserExerciseLastPrimaryKeyModel(rootOrg, userUUID, contentId);
		UserExerciseLastModel exercise = new UserExerciseLastModel(pk, result, meta.getEducator_id(), new Date(), url,
				meta.getFeedback_type(), uid);

		userExerciseRepo.updateExercise(rootOrg, exercise);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#getEducatorGroups(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<Map<String, String>> getEducatorGroups(String rootOrg, String educatorId) throws Exception {
		List<Map<String, String>> ret = new ArrayList<>();
		// TODO
		if (!userUtilService.validateUser(rootOrg, educatorId)) {
			throw new BadRequestException("Invalid Educator Id : " + educatorId);
		}

		// get all groups for the educator
		ret = exerciseRepo.getAllGroupsForEducators(rootOrg, educatorId);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#getSubmissionsByGroups(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getSubmissionsByGroups(String rootOrg, String groupId, String contentId)
			throws Exception {
		Map<String, Object> ret = new HashMap<>();
		// validate that the content exists
		exerciseRepo.validateExerciseID(contentId);

		// get all the latest submissions by the users in a group
		ret = exerciseRepo.getSubmissionsByGroups(rootOrg, groupId, contentId);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ExerciseService#getExerciseNotification(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getExerciseNotification(String rootOrg, String userUUID) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}
		List<Map<String, Object>> ret = new ArrayList<>();

		// get notification for the user (notify the submission of a feedback)
		ret = exerciseRepo.getNotificationForUser(rootOrg, userUUID);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.service.ExerciseService#insertFile(java.util.Map,
	 * java.lang.String, java.lang.String) This method saves solution along with the
	 * result
	 */
	@Override
	public String insertInExercise(String rootOrg, Map<String, Object> meta, String contentId, String userUUID)
			throws Exception {

		// insert in the exercise table
		String ret = "Success";
		UserExerciseLastPrimaryKeyModel pk = new UserExerciseLastPrimaryKeyModel(rootOrg, userUUID, contentId);
		UserExerciseLastModel exercise = new UserExerciseLastModel(pk, UUIDs.timeBased(), new Date(),
				Float.parseFloat(meta.get("result_percent").toString()), meta.get("url").toString(),
				meta.get("submission_type").toString(), Integer.parseInt(meta.get("testcases_failed").toString()),
				Integer.parseInt(meta.get("testcases_passed").toString()),
				Integer.parseInt(meta.get("total_testcases").toString()), null, null, null, null, null);
		userExerciseRepo.updateExercise(rootOrg, exercise);

		// update progress for the resource
		if (serverProps.getEnableRealTime())
			contentProgressService.callProgress(rootOrg, userUUID, contentId, submissionMimeType,
					Float.parseFloat(meta.get("result_percent").toString()));
		return ret;
	}
}
