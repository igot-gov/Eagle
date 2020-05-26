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
package com.infosys.lex.exercise.bodhi.repo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.exercise.postgres.entity.EducatorGroupModel;
import com.infosys.lex.exercise.postgres.entity.GroupUserModel;
import com.infosys.lex.exercise.postgres.repository.EducatorGroupMappingRepo;
import com.infosys.lex.exercise.postgres.repository.GroupUserMappingRepo;

@Repository
public class ExerciseRepositoryImpl implements ExerciseRepository {

//	@Autowired
//	UserRepository userRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	UserExerciseRepository uExerciseRepository;

	@Autowired
	UserExerciseLastRepository uExerciseLastRepository;

	@Autowired
	UserExerciseLastByFeedbackRepository uExerciseLastByFeebackRepository;

	@Autowired
	EducatorGroupMappingRepo eGroupRepository;

	@Autowired
	GroupUserMappingRepo gUserRepository;

	@Autowired
	ContentService contentService;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getAllExerciseSubmitted(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getAllExerciseSubmitted(String rootOrg, String userUUID, String contentId)
			throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<UserExerciseModel> result = uExerciseRepository
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentId(rootOrg, userUUID, contentId);
		List<String> feedbackIds = new ArrayList<>();
		// TODO create method population method
		for (UserExerciseModel row : result) {
			Map<String, Object> temp = new HashMap<>();
			temp.put("submission_id", row.getPrimaryKey().getSubmissionId());
			temp.put("submission_time",
					row.getSubmissionTime() == null ? null : row.getSubmissionTime());
			temp.put("result_percent", row.getResultPercent());
			temp.put("submission_url", row.getSubmissionUrl());
			temp.put("feedback_by_uuid", row.getFeedbackBy());
			if (row.getFeedbackBy() == null) {
				temp.put("feedback_by", null);
			} else {
				feedbackIds.add(row.getFeedbackBy());
			}
			temp.put("feedback_type", row.getFeedbackType());
			temp.put("feedback_time",
					row.getFeedbackTime() == null ? null :row.getFeedbackTime());
			temp.put("feedback_url", row.getFeedbackUrl());
			temp.put("testcases_failed", row.getTestcasesFailed());
			temp.put("testcases_passed", row.getTestcasesPassed());
			temp.put("total_testcases", row.getTotalTestcases());
			temp.put("submission_type", row.getSubmissionType());
			ret.add(temp);
		}

		Map<String, Map<String, String>> names = this.getUserDetailsForUserId(rootOrg, feedbackIds);

		for (Map<String, Object> data : ret) {
			if (!data.containsKey("feedback_by"))
				data.put("feedback_by", names.get(data.get("feedback_by_uuid")).get("firstname").toString() + " "
						+ names.get(data.get("feedback_by_uuid")).get("lastname").toString());
			data.remove("feedback_by_uuid");
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getLatestExerciseSubmitted(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getLatestExerciseSubmitted(String rootOrg, String userUUID, String contentId)
			throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<UserExerciseLastModel> result = uExerciseLastRepository
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentId(rootOrg, userUUID, contentId);

		List<String> userIds = new ArrayList<>();

		for (UserExerciseLastModel row : result) {
			Map<String, Object> temp = new HashMap<>();
			temp.put("submission_id", row.getSubmissionId());
			temp.put("submission_time",
					row.getSubmissionTime() == null ? null : row.getSubmissionTime());
			temp.put("result_percent", row.getResultPercent());
			temp.put("submission_url", row.getSubmissionUrl());
			temp.put("submitted_by", row.getPrimaryKey().getUserId());
			userIds.add(row.getPrimaryKey().getUserId());
			temp.put("feedback_by_uuid", row.getFeedbackBy());
			if (row.getFeedbackBy() == null) {
				temp.put("feedback_by", null);
			} else {
				userIds.add(row.getFeedbackBy());
			}
			temp.put("feedback_type", row.getFeedbackType());
			temp.put("feedback_time",
					row.getFeedbackTime() == null ? null : row.getFeedbackTime());
			temp.put("feedback_url", row.getFeedbackUrl());
			temp.put("testcases_failed", row.getTestcasesFailed());
			temp.put("testcases_passed", row.getTestcasesPassed());
			temp.put("total_testcases", row.getTotalTestcases());
			temp.put("submission_type", row.getSubmissionType());
			if (row.getFeedbackTime() != null) {
				if (row.getSubmissionId().timestamp() > row.getFeedbackSubmissionId().timestamp()) {
					temp.put("is_feedback_for_older_sumbission", 1);
					temp.put("old_feedback_submission_id", row.getFeedbackSubmissionId());
					temp.remove("feedback_url");
				}
			}
			ret.add(temp);
		}

		Map<String, Map<String, String>> names = this.getUserDetailsForUserId(rootOrg, userIds);

		for (Map<String, Object> data : ret) {
			if (!data.containsKey("feedback_by"))
				data.put("feedback_by", names.get(data.get("feedback_by_uuid")).get("firstname").toString() + " "
						+ names.get(data.get("feedback_by_uuid")).get("lastname").toString());
			data.put("submitted_by_email", names.get(data.get("submitted_by")).get("email").toString());
			data.remove("feedback_by_uuid");
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getSpecificExercise(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getSpecificExercise(String rootOrg, String userUUID, String contentId,
			String submissionId) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		List<UserExerciseModel> result = uExerciseRepository
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentIdAndPrimaryKeySubmissionId(rootOrg,
						userUUID, contentId, UUID.fromString(submissionId));

		List<String> userIds = new ArrayList<>();

		for (UserExerciseModel row : result) {
			Map<String, Object> temp = new HashMap<>();
			temp.put("submission_id", row.getPrimaryKey().getSubmissionId());
			temp.put("submission_time",
					row.getSubmissionTime() == null ? null : row.getSubmissionTime());
			temp.put("result_percent", row.getResultPercent());
			temp.put("submission_url", row.getSubmissionUrl());
			temp.put("submitted_by", row.getPrimaryKey().getUserId());
			userIds.add(row.getPrimaryKey().getUserId());
			temp.put("feedback_by_uuid", row.getFeedbackBy());
			if (row.getFeedbackBy() == null) {
				temp.put("feedback_by", null);
			} else {
				userIds.add(row.getFeedbackBy());
			}
			temp.put("feedback_type", row.getFeedbackType());
			temp.put("feedback_time",
					row.getFeedbackTime() == null ? null : row.getFeedbackTime());
			temp.put("feedback_url", row.getFeedbackUrl());
			temp.put("testcases_failed", row.getTestcasesFailed());
			temp.put("testcases_passed", row.getTestcasesPassed());
			temp.put("total_testcases", row.getTotalTestcases());
			temp.put("submission_type", row.getSubmissionType());
			ret.add(temp);
		}

		Map<String, Map<String, String>> names = this.getUserDetailsForUserId(rootOrg, userIds);

		for (Map<String, Object> data : ret) {
			if (!data.containsKey("feedback_by"))
				data.put("feedback_by", names.get(data.get("feedback_by_uuid")).get("firstname").toString() + " "
						+ names.get(data.get("feedback_by_uuid")).get("lastname").toString());
			data.put("submitted_by_email", names.get(data.get("submitted_by")).get("email").toString());
			data.remove("feedback_by_uuid");
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getAllGroupsForEducators(java.
	 * lang.String)
	 */
	@Override
	public List<Map<String, String>> getAllGroupsForEducators(String rootOrg, String educatorUUID) throws Exception {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		List<EducatorGroupModel> result = eGroupRepository.findByKeyRootOrgAndKeyEducatorId(rootOrg, educatorUUID);

		for (EducatorGroupModel row : result) {
			Map<String, String> temp = new HashMap<>();
			temp.put("group_id", row.getKey().getGroupId());
			temp.put("group_name", row.getGroupName());
			ret.add(temp);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getSubmissionsByGroups(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getSubmissionsByGroups(String rootOrg, String groupId, String contentId)
			throws Exception {
		Map<String, Object> ret = new HashMap<>();
		List<Map<String, Object>> dataList = new ArrayList<>();
		int feedbackCount = 0;
		List<UserExerciseLastModel> result = uExerciseLastRepository
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdInAndPrimaryKeyContentId(rootOrg,
						this.getUsersByGroup(rootOrg, groupId), contentId);

		List<String> userIds = new ArrayList<>();

		for (UserExerciseLastModel row : result) {
			Map<String, Object> temp = new HashMap<>();
			temp.put("submitted_by", row.getPrimaryKey().getUserId());
			userIds.add(row.getPrimaryKey().getUserId());
			temp.put("submission_id", row.getSubmissionId());
			temp.put("submission_time",
					row.getSubmissionTime() == null ? null : row.getSubmissionTime());
			temp.put("result_percent", row.getResultPercent());
			temp.put("submission_url", row.getSubmissionUrl());
			temp.put("feedback_by_uuid", row.getFeedbackBy());
			if (row.getFeedbackBy() == null) {
				temp.put("feedback_by", null);
			} else {
				feedbackCount++;
				userIds.add(row.getFeedbackBy());
			}
			temp.put("feedback_type", row.getFeedbackType());
			temp.put("feedback_time",
					row.getFeedbackTime() == null ? null : row.getFeedbackTime());
			temp.put("feedback_url", row.getFeedbackUrl());
			temp.put("testcases_failed", row.getTestcasesFailed());
			temp.put("testcases_passed", row.getTestcasesPassed());
			temp.put("total_testcases", row.getTotalTestcases());
			temp.put("submission_type", row.getSubmissionType());
			// This is if latest submission was given is after the last feedback
			if (row.getFeedbackTime() != null) {
				if (row.getSubmissionId().timestamp() > row.getFeedbackSubmissionId().timestamp()) {
					temp.put("is_feedback_for_older_sumbission", 1);
					temp.put("old_feedback_submission_id", row.getFeedbackSubmissionId());
				}
			}

			dataList.add(temp);
		}

		Map<String, Map<String, String>> names = this.getUserDetailsForUserId(rootOrg, userIds);
		for (Map<String, Object> data : dataList) {
			if (!data.containsKey("feedback_by"))
				data.put("feedback_by", names.get(data.get("feedback_by_uuid")).get("firstname").toString() + " "
						+ names.get(data.get("feedback_by_uuid")).get("lastname").toString());
			if (names.containsKey(data.get("submitted_by"))) {
				data.put("submitted_by_email", names.get(data.get("submitted_by")).get("email").toString());
				data.put("submitted_by_name", names.get(data.get("submitted_by")).get("firstname").toString() + " "
						+ names.get(data.get("submitted_by")).get("lastname").toString());
			}
			data.remove("feedback_by_uuid");
		}

		ret.put("submissions", dataList);
		ret.put("feedback_count", feedbackCount);
		ret.put("submission_count", dataList.size());
		return ret;
	}

	private List<String> getUsersByGroup(String rootOrg, String groupId) throws Exception {
		List<String> ret = new ArrayList<String>();
		List<GroupUserModel> result = gUserRepository.findByKeyRootOrgAndKeyGroupId(rootOrg, UUID.fromString(groupId));

		for (GroupUserModel row : result) {
			ret.add(row.getKey().getUserId());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#getNotificationForUser(java.
	 * lang.String)
	 */
	@Override
	public List<Map<String, Object>> getNotificationForUser(String rootOrg, String userUUID) throws Exception {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		Calendar cal = Calendar.getInstance();
		Date upperLimit = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, -15);
		Date lowerLimit = cal.getTime();

		List<UserExerciseLastByFeedbackModel> result = uExerciseLastByFeebackRepository
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyFeedbackTimeGreaterThanAndPrimaryKeyFeedbackTimeLessThan(
						rootOrg, userUUID, lowerLimit, upperLimit);

		List<String> contentIdList = new ArrayList<>();
		for (UserExerciseLastByFeedbackModel row : result) {
			contentIdList.add(row.getPrimaryKey().getContentId());
		}

		List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(contentIdList,
				new String[] { "name", "identifier" }, null);
		Map<String, String> contentNames = new HashMap<>();
		for (Map<String, Object> source : sourceList) {
			contentNames.put(source.get("identifier").toString(), source.get("name").toString());
		}

		List<String> userIds = new ArrayList<>();

		for (UserExerciseLastByFeedbackModel row : result) {
			Map<String, Object> temp = new HashMap<>();
			temp.put("submission_id", row.getSubmissionId());
			temp.put("content_id", row.getPrimaryKey().getContentId());
			temp.put("content_name", contentNames.get(row.getPrimaryKey().getContentId()));
			userIds.add(row.getFeedbackBy());
			temp.put("feedback_by_uuid", row.getFeedbackBy());
			temp.put("feedback_time", row.getPrimaryKey().getFeedbackTime() == null ? null
					: row.getPrimaryKey().getFeedbackTime());
			if (row.getSubmissionId().timestamp() > row.getFeedbackSubmissionId().timestamp()) {
				temp.put("is_feedback_for_older_sumbission", 1);
				temp.put("old_feedback_submission_id", row.getFeedbackSubmissionId());
			}
			ret.add(temp);
		}

		Map<String, Map<String, String>> names = this.getUserDetailsForUserId(rootOrg, userIds);

		for (Map<String, Object> data : ret) {
			data.put("feedback_by", names.get(data.get("feedback_by_uuid")).get("firstname").toString() + " "
					+ names.get(data.get("feedback_by_uuid")).get("lastname").toString());
			data.remove("feedback_by_uuid");
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.ExerciseRepository#validateSubmissionID(java.lang
	 * .String)
	 */
	@Override
	public void validateSubmissionID(String submissionId) throws Exception {
		if (UUIDUtil.typeOf(UUID.fromString(submissionId)) == null)
			throw new InvalidDataInputException("invalid.submissionid");
	}

	@Override
	public void validateExerciseID(String contentId) throws Exception {
		if (contentId == null || contentId.isEmpty())
			throw new InvalidDataInputException("missing.exercise");

		List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
				Arrays.asList(new String[] { contentId }), new String[] { "resourceType" }, null);

		if (sources.size() == 0 || !sources.get(0).containsKey("resourceType")
				|| !sources.get(0).get("resourceType").toString().toLowerCase().equals("exercise"))
			throw new InvalidDataInputException("invalid.exercise");
	}

	private Map<String, Map<String, String>> getUserDetailsForUserId(String rootOrg, List<String> userIds) {
		Map<String, Map<String, String>> names = new HashMap<>();
		{
			if (!userIds.isEmpty()) {
				Map<String, Object> usersData = userUtilService.getUsersDataFromUserIds(rootOrg, userIds, Arrays.asList(
						PIDConstants.EMAIL, PIDConstants.FIRST_NAME, PIDConstants.MIDDLE_NAME, PIDConstants.LAST_NAME));

				for (String userId : usersData.keySet()) {
					@SuppressWarnings("unchecked")
					Map<String, Object> userPidData = (Map<String, Object>) usersData.get(userId);
					String lastName = userPidData.get(PIDConstants.MIDDLE_NAME) != null
							? userPidData.get(PIDConstants.MIDDLE_NAME).toString()
							: "";
					lastName += userPidData.get(PIDConstants.LAST_NAME) != null
							? userPidData.get(PIDConstants.LAST_NAME).toString()
							: "";
					Map<String, String> temp = new HashMap<>();
					temp.put("firstname",
							userPidData.get(PIDConstants.FIRST_NAME) != null
									? userPidData.get(PIDConstants.FIRST_NAME).toString()
									: "");
					temp.put("lastname", lastName);
					temp.put("email",
							userPidData.get(PIDConstants.EMAIL) != null ? userPidData.get(PIDConstants.EMAIL).toString()
									: "");
					names.put(userId, temp);
				}
			}
		}
		return names;
	}

}
