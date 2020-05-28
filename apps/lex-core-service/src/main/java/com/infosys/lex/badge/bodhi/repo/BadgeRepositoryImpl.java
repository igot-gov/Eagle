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
package com.infosys.lex.badge.bodhi.repo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.infosys.lex.assessment.bodhi.repo.UserAssessmentSummaryRepository;
import com.infosys.lex.assessment.bodhi.repo.UserQuizSummaryRepository;
import com.infosys.lex.badge.postgredb.entity.Badge;
import com.infosys.lex.badge.postgredb.projection.BadgeDetailsProjection;
import com.infosys.lex.badge.postgredb.repository.BadgeRepo;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.sunbird.repo.UserMVRepository;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;

@Repository
public class BadgeRepositoryImpl implements BadgeRepository {

	@Autowired
	ContentProgressRepository contentProgressRepo;

	@Autowired
	UserAssessmentSummaryRepository assessmentSummaryRepo;

	@Autowired
	UserQuizSummaryRepository quizSummaryRepo;

	@Autowired
	ContentService contentService;

	@Autowired
	UserMVRepository userMVRepo;

	@Autowired
	UserBadgeRepository userBadgeRepo;

//	@Autowired
//	ConstantBadgeRepository constantBadgeRepository;

	@Autowired
	private BadgeRepo badgeRepo;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.BadgeRepository#insertInBadges(java.lang.String,
	 * java.util.List, java.lang.String, boolean)
	 */
	@Async
	@Override
	public void insertInBadges(String rootOrg, String courseId, List<String> programId, String userId, boolean parent)
			throws Exception {

		// insert into badges
		this.insertCertificatesAndMedals(rootOrg, courseId, userId, "C", 0);
		if (parent) {
			for (String program : programId)
				if (checkProgram(rootOrg, program, courseId, userId)) {
					// insert medal
					this.insertCertificatesAndMedals(rootOrg, program, userId, "M", 0);
				}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean checkProgram(String rootOrg, String programId, String courseId, String userId) {

		Boolean ret = false;
		try {
			List<String> ids = new ArrayList<String>();
			List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
					Arrays.asList(new String[] { programId }), new String[] { "contentType", "children" }, null);
			if (sources.size() == 0) {
				throw new InvalidDataInputException("invalid.resource");
			}

			Map<String, Object> source = sources.get(0);

			if (source.get("contentType").toString().toLowerCase().equals("learning path")) {
				for (Map<String, Object> child : (List<Map<String, Object>>) source.get("children")) {
					ids.add(child.get("identifier").toString());
				}
				List<ContentProgressModel> result = contentProgressRepo.getProgress(rootOrg, userId,
						Arrays.asList(new String[] { "Course" }), ids);
				int courseCount = 0;
				// to check if the record is already present
				boolean recentCourseFlag = false;
				for (ContentProgressModel d : result) {
					if (d.getProgress() == 1) {
						ret = true;
						courseCount++;
						continue;
					} else if (d.getPrimaryKey().getContentId().equals(courseId)) {
						recentCourseFlag = true;
						ret = true;
						courseCount++;
						continue;
					} else {
						ret = false;
						break;
					}
				}
				if (ret && recentCourseFlag && courseCount < ids.size()) {
					ret = false;
				} else if (ret && !recentCourseFlag && courseCount < ids.size() - 1) {
					ret = false;
				}
			}
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	private Map<String, Object> insertCertificatesAndMedals(String rootOrg, String id, String userId, String type,
			int retryCounter) throws Exception {

		Map<String, Object> response = new HashMap<>();
		Date now = new Date();
		try {
			List<UserBadgesModel> badges = new ArrayList<>();
			if (!userBadgeRepo.existsById(new UserBadgesPrimaryKeyModel(rootOrg, userId, id)))
				badges.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, id), type, now, now, 100f,
						now, 1));
			Calendar cal = Calendar.getInstance();

			// check and insert the Elf badge
			if (type.equals("M") & cal.get(Calendar.MONTH) == 11) {
				List<ContentProgressModel> accessedDateList = contentProgressRepo.getFirstAccessedOn(userId,
						Arrays.asList(new String[] { "Learning Path" }), id);
				if (accessedDateList.size() > 0) {
					cal.setTime(accessedDateList.get(0).getFirstAccessedOn());
					if (cal.get(Calendar.MONTH) == 11)
						badges.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, "Elf"), "O", now,
								now, 100f, now, 1));
				}
			}

			userBadgeRepo.insertBadges(badges);
			response.put("response", "SUCCESS");
		} catch (Exception e) {

			// retry on fail
			if (retryCounter < 3)
				this.insertCertificatesAndMedals(rootOrg, id, userId, type, ++retryCounter);
			else
				throw new Exception(e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.BadgeRepository#insertCourseAndQuizBadge(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Async
	@Override
	public void insertCourseAndQuizBadge(String rootOrg, String userId, String type, String contentId)
			throws Exception {

		List<UserBadgesModel> badgesToUpdate = new ArrayList<>();

		// get number of courses and quizzes
		long count = this.getContentCompletedCount(rootOrg, userId, type, contentId);

		Map<String, Integer> meta = null;

		// get course count based badges
		if (type.toLowerCase().equals("course")) {
			meta = this.getBadgeMeta(rootOrg, "Course");
			Map<String, Float> badges = this.getUserExistingBadges(rootOrg,
					Arrays.asList(meta.keySet().toArray(new String[0])), userId);
			for (String badgeId : meta.keySet()) {
				Float progress = (count * 100.0f / meta.get(badgeId));
				Float oldProgress = badges.getOrDefault(badgeId, 0.0f);
				if (oldProgress < 100) {
					if (progress >= 100) {
						Date d = new Date();
						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
								"O", d, d, 100f, d, 1));
					} else {
						Date d = new Date(0);
						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
								"O", d, d, progress, new Date(), 0));
					}
				}
			}
		}
		// get quiz count based badges
		else {
			meta = this.getBadgeMeta(rootOrg, "Quiz");
			Map<String, Float> badges = this.getUserExistingBadges(rootOrg,
					Arrays.asList(meta.keySet().toArray(new String[0])), userId);
			for (String badgeId : meta.keySet()) {
				Float progress = (count * 100.0f / meta.get(badgeId));
				Float oldProgress = badges.getOrDefault(badgeId, 0.0f);
				if (oldProgress < 100) {
					if (progress >= 100) {
						Date d = new Date();
						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
								"O", d, d, 100f, d, 1));
					} else {
						Date d = new Date(0);
						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
								"O", d, d, progress, new Date(), 0));
					}
				}
			}
		}
		userBadgeRepo.updateBadges(badgesToUpdate);
	}

	private Map<String, Integer> getBadgeMeta(String rootOrg, String badgeGroup) {
		Map<String, Integer> ret = new HashMap<>();
//		List<BadgeModel> badges = constantBadgeRepository.findByBadgeGroup(badgeGroup);
		List<Badge> badges = badgeRepo.findByKeyRootOrgAndBadgeGroup(rootOrg, badgeGroup);
		for (Badge row : badges) {
			ret.put(row.getKey().getBadgeId(), row.getThreshold1().intValue());
		}
		return ret;
	}

	private long getContentCompletedCount(String rootOrg, String userId, String type, String contentId)
			throws Exception {
		if (type.equalsIgnoreCase("course")) {
			return assessmentSummaryRepo.countByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg, userId);
		} else {
			return quizSummaryRepo.countByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg, userId);
		}
	}

	private Map<String, Float> getUserExistingBadges(String rootOrg, List<String> ids, String userId) throws Exception {
		Map<String, Float> ret = new HashMap<>();
		List<UserBadgesModel> badges = userBadgeRepo
				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyBadgeIdIn(rootOrg, userId, ids);
		for (UserBadgesModel row : badges) {
			ret.put(row.getPrimaryKey().getBadgeId(), row.getProgress());
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.repository.BadgeRepository#insertSanta(java.lang.String)
	 */
	/*
	 * @Override public void insertSanta(String rootOrg, String userId) { Date now =
	 * new Date(); if (!userBadgeRepo.existsById(new
	 * UserBadgesPrimaryKeyModel(rootOrg, userId, "Santa")))
	 * userBadgeRepo.insert(new UserBadgesModel(new
	 * UserBadgesPrimaryKeyModel(rootOrg, userId, "Santa"), "O", now, now, 100f,
	 * now, 1)); }
	 */
}
