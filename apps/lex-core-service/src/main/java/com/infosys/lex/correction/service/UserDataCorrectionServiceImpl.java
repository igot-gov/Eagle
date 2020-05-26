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
package com.infosys.lex.correction.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgesModel;
import com.infosys.lex.badge.bodhi.repo.UserBadgesPrimaryKeyModel;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.correction.bodhi.repo.UserDataCorrectionRepository;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;

@Service
public class UserDataCorrectionServiceImpl implements UserDataCorrectionService {

//	@Autowired
//	AssessmentRepository aRepository;

//	@Autowired
//	BadgeRepository bRepository;

//	@Autowired
//	ContentService contentService;

//	@Autowired
//	UserAssessmentByContentUserRepository userAssessmentByContentUserRepo;

	UserDataCorrectionRepository correctionRepo;
	UserUtilityService utilitySvc;
	ContentProgressRepository progressRepo;
	UserBadgeRepository userBadgeRepo;

	@Autowired
	public UserDataCorrectionServiceImpl(UserDataCorrectionRepository uDRepository, UserUtilityService utilitySvc,
			ContentProgressRepository progress, UserBadgeRepository userBadge) {
		this.correctionRepo = uDRepository;
		this.utilitySvc = utilitySvc;
		this.progressRepo = progress;
		this.userBadgeRepo = userBadge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.UserDataCorrectionService#correctData(java.lang.
	 * String)
	 */
	@Override
	public String correctData(String rootOrg, String userId) throws Exception {
		String ret = "success";
		if (!utilitySvc.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.userid");
		}
// ----------------------------------------------------------------------------------------------------------------------------------------
// Going to Assessment table and recalculate certificates and medals.
//		// Fetch all passed distinct assessment's course ids
//		List<Map<String, Object>> result = userAssessmentByContentUserRepo.findDistinctParentsByUserId(rootOrg, userId);
//		Set<String> contentIds = new HashSet<>();
//		for (Map<String, Object> assessment : result)
//			if (!assessment.get("parent_source_id").toString().isEmpty())
//				contentIds.add(assessment.get("parent_source_id").toString());
//
//		// Fetch Meta for all ids
//		for (String contentId : contentIds) {
//			List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
//					Arrays.asList(new String[] { contentId }), new String[] { "contentType", "collections" }, "Live");
//			if (sources.size() == 0) {
//				throw new InvalidDataInputException("invalid.resource");
//			}
//
//			Map<String, Object> tempSource = sources.get(0);
//
//			// If parent of course is available, fetch all parent ids(LP ids)
//			boolean parent = ((List<Object>) tempSource.get("collections")).size() > 0 ? true : false;
//			List<String> programId = new ArrayList<String>();
//			for (Map<String, Object> collection : ((List<Map<String, Object>>) (tempSource.get("collections")))) {
//				programId.add(collection.get("identifier").toString());
//			}
//			// Give certificates or medals based on completion
//			bRepository.insertInBadges(rootOrg, contentId, programId, userId, parent);
//		}
// ------------------------------------------------------------------------------------------------------------------------------------

// ------------------------------------------------------------------------------------------------------------------------------------

		// Goto progress data and fetch all certificates and medals
		// No need to recalculate for medals. Check if record exists in user_badge data.
		// If doesn't add the data else ignore.

		// Fetch all certificates and medals for the user
		List<ContentProgressModel> userProgressData = progressRepo
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentType(rootOrg, userId, "Course");

		userProgressData.addAll(progressRepo.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentType(rootOrg,
				userId, "Learning Path"));

		Set<String> completedCourseIds = new HashSet<>();
		Set<String> completedProgramIds = new HashSet<>();
		Map<String, Date> firstCompletedOnData = new HashMap<>();
		// Separate out completed courses and learning paths
		for (ContentProgressModel progressData : userProgressData) {
			if (progressData.getProgress() != null) {
				if (progressData.getProgress() >= 1f) {
					if (progressData.getPrimaryKey().getContentType().equalsIgnoreCase("course")) {
						completedCourseIds.add(progressData.getPrimaryKey().getContentId());
						firstCompletedOnData.put(progressData.getPrimaryKey().getContentId(),
								progressData.getFirstCompletedOn());
					} else if (progressData.getPrimaryKey().getContentType().equalsIgnoreCase("learning path")) {
						completedProgramIds.add(progressData.getPrimaryKey().getContentId());
						firstCompletedOnData.put(progressData.getPrimaryKey().getContentId(),
								progressData.getFirstCompletedOn());
					}
				}
			}
		}

		// Fetch user earned cert and medals
		List<Map<String, Object>> userBadgeData = userBadgeRepo.findByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg,
				userId);

		Set<String> userCertificates = new HashSet<>();
		Set<String> userMedals = new HashSet<>();
		// Separate out achieved certificates and medals
		for (Map<String, Object> badgeData : userBadgeData) {
			if (badgeData.get("badge_type").toString().equalsIgnoreCase("c")) {
				userCertificates.add(badgeData.get("badge_id").toString());
			} else if (badgeData.get("badge_type").toString().equalsIgnoreCase("m")) {
				userMedals.add(badgeData.get("badge_id").toString());
			}
		}

		// Calculate the difference between progress data and user data
		completedCourseIds.removeAll(userCertificates);
		completedProgramIds.removeAll(userMedals);

		// If anything is remaining, add them for the user record.
		List<UserBadgesModel> insertstmts = new ArrayList<>();
		Date currentDate = new Date();
		for (String courseId : completedCourseIds) {
			insertstmts.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, courseId), "C",
					currentDate, currentDate, 100f, currentDate, 1));
		}
		for (String programId : completedProgramIds) {
			insertstmts.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, programId), "M",
					currentDate, currentDate, 100f, currentDate, 1));
		}

		if (insertstmts.size() > 0) {
			userBadgeRepo.saveAll(insertstmts);
		}

// ------------------------------------------------------------------------------------------------------------------------------------
		// Give Course / Quiz related badges
		correctionRepo.insertCourseAndQuizBadge(rootOrg, userId, "Course");
		correctionRepo.insertCourseAndQuizBadge(rootOrg, userId, "Quiz");

		// Give Fledgling if already not given
		correctionRepo.insertNewUserBadge(rootOrg, userId);

		return ret;
	}
}
