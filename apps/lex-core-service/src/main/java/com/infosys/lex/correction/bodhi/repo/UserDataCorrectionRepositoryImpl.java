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
package com.infosys.lex.correction.bodhi.repo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.infosys.lex.assessment.bodhi.repo.UserAssessmentSummaryRepository;
import com.infosys.lex.assessment.bodhi.repo.UserQuizSummaryRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgesModel;
import com.infosys.lex.badge.bodhi.repo.UserBadgesPrimaryKeyModel;
import com.infosys.lex.badge.postgredb.entity.Badge;
import com.infosys.lex.badge.postgredb.repository.BadgeRepoPg;

@Repository
public class UserDataCorrectionRepositoryImpl implements UserDataCorrectionRepository {

	@Autowired
	UserBadgeRepository userBadgeRepo;

//	@Autowired
//	ConstantBadgeRepository constantBadgeRepository;

	@Autowired
	UserAssessmentSummaryRepository assessmentSummaryRepo;

	@Autowired
	UserQuizSummaryRepository quizSummaryRepo;

//	@PostConstruct
//	private void init() {
//		badgeRepoPG = new JpaRepositoryFactory(configurationsDatabase).getRepository(BadgeRepoPg.class);
//	}
	@Autowired
	private BadgeRepoPg badgeRepoPG;

//	@Autowired
//	@Qualifier("userEntityManagerSecond")
//	private EntityManager configurationsDatabase;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.repository.UserDataCorrectionRepository#
	 * insertCourseAndQuizBadge(java.lang.String, java.lang.String)
	 */
	@Override
	public void insertCourseAndQuizBadge(String rootOrg, String userId, String type) throws Exception {
		List<UserBadgesModel> badgesToUpdate = new ArrayList<>();
		long count = this.getContentCompletedCount(rootOrg, userId, type);
		Map<String, Integer> meta = null;
		if (type.equalsIgnoreCase("course")) {
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
		} else {
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
		List<Badge> badgeDetails = badgeRepoPG.findByKeyRootOrgAndBadgeGroup(rootOrg, badgeGroup);
		for (Badge row : badgeDetails) {
			ret.put(row.getKey().getBadgeId(), row.getThreshold1().intValue());
		}
		return ret;
	}

	private long getContentCompletedCount(String rootOrg, String userId, String type) throws Exception {
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
	 * com.infosys.core.repository.UserDataCorrectionRepository#insertNewUserBadge(
	 * java.lang.String)
	 */
	@Override
	public void insertNewUserBadge(String rootOrg, String userId) {
		Date now = new Date();
		if (!userBadgeRepo.existsById(new UserBadgesPrimaryKeyModel(rootOrg, userId, "NewUser")))
			userBadgeRepo.insert(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, "NewUser"), "O",
					now, now, 100f, now, 1));
	}

}
