/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
//substitute url based on requirement
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Repository;
//
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//
//@Repository
//public class BadgeAssignmentPGImpl implements BadgeAssignmentPG {
//
//	ContentProgressRepository contentProgressRepo;
//	UserAssessmentSummaryRepository assessmentSummaryRepo;
//	UserQuizSummaryRepository quizSummaryRepo;
//	ContentService contentService;
//	UserBadgeRepository userBadgeRepo;
//
////	@PostConstruct
////	private void init() {
////		badgeRepoPG = new JpaRepositoryFactory(configurationsDatabase).getRepository(BadgeRepoPg.class);
////	}
//
//	@Autowired
//	private BadgeRepoPg badgeRepoPG;
//
////	@Autowired
////	@Qualifier("userEntityManagerSecond")
////	private EntityManager configurationsDatabase;
//
//	@Autowired
//	public BadgeAssignmentPGImpl(ContentProgressRepository contentProgressRepo,
//			UserAssessmentSummaryRepository assessmentSummaryRepo, UserQuizSummaryRepository quizSummaryRepo,
//			ContentService contentService, UserBadgeRepository userBadgeRepo) {
//		this.contentProgressRepo = contentProgressRepo;
//		this.assessmentSummaryRepo = assessmentSummaryRepo;
//		this.quizSummaryRepo = quizSummaryRepo;
//		this.contentService = contentService;
//		this.userBadgeRepo = userBadgeRepo;
//	}
//
//	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//substitute url based on requirement
//	 * java.lang.String, java.util.List, java.lang.String, boolean)
//	 */
//	@Async
//	@Override
//	public void insertInBadges(String rootOrg, String courseId, List<String> programId, String userId, boolean parent)
//			throws Exception {
//
//		// insert into badges
//		this.insertCertificatesAndMedals(rootOrg, courseId, userId, "C", 0);
//		if (parent) {
//			for (String program : programId)
//				if (checkProgram(rootOrg, program, courseId, userId)) {
//					// insert medal
//					this.insertCertificatesAndMedals(rootOrg, program, userId, "M", 0);
//				}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private boolean checkProgram(String rootOrg, String programId, String courseId, String userId) {
//
//		Boolean ret = false;
//		try {
//			List<String> ids = new ArrayList<String>();
//			List<Map<String, Object>> sources = contentService.getMetaByIDListandSource(
//					Arrays.asList(new String[] { programId }), new String[] { "contentType", "children" }, null);
//			if (sources.size() == 0) {
//				throw new InvalidDataInputException("invalid.resource", null);
//			}
//
//			Map<String, Object> source = sources.get(0);
//
//			if (source.get("contentType").toString().toLowerCase().equals("learning path")) {
//				for (Map<String, Object> child : (List<Map<String, Object>>) source.get("children")) {
//					ids.add(child.get("identifier").toString());
//				}
//				List<ContentProgressModel> result = contentProgressRepo.getProgress(rootOrg, userId,
//						Arrays.asList(new String[] { "Course" }), ids);
//				int courseCount = 0;
//				// to check if the record is already present
//				boolean recentCourseFlag = false;
//				for (ContentProgressModel d : result) {
//					if (d.getProgress() == 1) {
//						ret = true;
//						courseCount++;
//						continue;
//					} else if (d.getPrimaryKey().getContentId().equals(courseId)) {
//						recentCourseFlag = true;
//						ret = true;
//						courseCount++;
//						continue;
//					} else {
//						ret = false;
//						break;
//					}
//				}
//				if (ret && recentCourseFlag && courseCount < ids.size()) {
//					ret = false;
//				} else if (ret && !recentCourseFlag && courseCount < ids.size() - 1) {
//					ret = false;
//				}
//			}
//		} catch (Exception e) {
//			ret = false;
//		}
//		return ret;
//	}
//
//	private Map<String, Object> insertCertificatesAndMedals(String rootOrg, String id, String userId, String type,
//			int retryCounter) throws Exception {
//
//		Map<String, Object> response = new HashMap<>();
//		Date now = new Date();
//		try {
//			List<UserBadgesModel> badges = new ArrayList<>();
//			if (!userBadgeRepo.existsById(new UserBadgesPrimaryKeyModel(rootOrg, userId, id)))
//				badges.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, id), type, now, now, 100f,
//						now, 1));
//
//			insertEventBasedBadges(rootOrg, id, userId, "Elf", badges);
//
//			userBadgeRepo.insertBadges(badges);
//			response.put("response", "success");
//		} catch (Exception e) {
//
//			// retry on fail
//			if (retryCounter < 3)
//				this.insertCertificatesAndMedals(rootOrg, id, userId, type, ++retryCounter);
//			else
//				throw new Exception(e);
//		}
//		return response;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//substitute url based on requirement
//	 * insertCourseAndQuizBadge(java.lang.String, java.lang.String,
//	 * java.lang.String)
//	 */
//	@Async
//	@Override
//	public void insertCourseAndQuizBadge(String rootOrg, String userId, String type, String contentId)
//			throws Exception {
//
//		List<UserBadgesModel> badgesToUpdate = new ArrayList<>();
//
//		// get number of courses and quizzes
//		long count = this.getContentCompletedCount(rootOrg, userId, type, contentId);
//
//		Map<String, Integer> meta = null;
//
//		// get course count based badges
//		if (type.equalsIgnoreCase("course")) {
//			meta = this.getBadgeMeta(rootOrg, "Course");
//			Map<String, Float> badges = this.getUserExistingBadges(rootOrg,
//					Arrays.asList(meta.keySet().toArray(new String[0])), userId);
//			for (String badgeId : meta.keySet()) {
//				Float progress = (count * 100.0f / meta.get(badgeId));
//				Float oldProgress = badges.getOrDefault(badgeId, 0.0f);
//				if (oldProgress < 100) {
//					if (progress >= 100) {
//						Date d = new Date();
//						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
//								"O", d, d, 100f, d, 1));
//					} else {
//						Date d = new Date(0);
//						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
//								"O", d, d, progress, new Date(), 0));
//					}
//				}
//			}
//		}
//		// get quiz count based badges
//		else {
//			meta = this.getBadgeMeta(rootOrg, "Quiz");
//			Map<String, Float> badges = this.getUserExistingBadges(rootOrg,
//					Arrays.asList(meta.keySet().toArray(new String[0])), userId);
//			for (String badgeId : meta.keySet()) {
//				Float progress = (count * 100.0f / meta.get(badgeId));
//				Float oldProgress = badges.getOrDefault(badgeId, 0.0f);
//				if (oldProgress < 100) {
//					if (progress >= 100) {
//						Date d = new Date();
//						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
//								"O", d, d, 100f, d, 1));
//					} else {
//						Date d = new Date(0);
//						badgesToUpdate.add(new UserBadgesModel(new UserBadgesPrimaryKeyModel(rootOrg, userId, badgeId),
//								"O", d, d, progress, new Date(), 0));
//					}
//				}
//			}
//		}
//		userBadgeRepo.updateBadges(badgesToUpdate);
//	}
//
//	private Map<String, Integer> getBadgeMeta(String rootOrg, String badgeGroup) {
//		Map<String, Integer> ret = new HashMap<>();
////		List<BadgeModel> badges = constantBadgeRepository.findByBadgeGroup(badgeGroup);
//		List<Badge> badgeDetails = badgeRepoPG.findByKeyRootOrgAndBadgeGroup(rootOrg, badgeGroup);
//		for (Badge row : badgeDetails) {
//			ret.put(row.getKey().getBadgeId(), row.getThreshold1().intValue());
//		}
//		return ret;
//	}
//
//	private long getContentCompletedCount(String rootOrg, String userId, String type, String contentId)
//			throws Exception {
//		if (type.equalsIgnoreCase("course")) {
//			return assessmentSummaryRepo.countByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg, userId);
//		} else {
//			return quizSummaryRepo.countByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg, userId);
//		}
//	}
//
//	private Map<String, Float> getUserExistingBadges(String rootOrg, List<String> ids, String userId) throws Exception {
//		Map<String, Float> ret = new HashMap<>();
//		List<UserBadgesModel> badges = userBadgeRepo
//				.findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyBadgeIdIn(rootOrg, userId, ids);
//		for (UserBadgesModel row : badges) {
//			ret.put(row.getPrimaryKey().getBadgeId(), row.getProgress());
//		}
//		return ret;
//	}
//
////		@Override
////		public void insertSanta(String userId) {
////			Date now = new Date();
////			if (!userBadgeRepo.existsById(new UserBadgesPrimaryKeyModel(userId, "Santa")))
////				userBadgeRepo.insert(new UserBadgesModel(new UserBadgesPrimaryKeyModel(userId, "Santa"), "O", now, now,
////						100f, now, 1));
////		}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//substitute url based on requirement
//	 * insertEventBasedBadges(java.lang.String, java.lang.String, java.lang.String,
//	 * java.util.List)
//	 */
//	@Override
//	public void insertEventBasedBadges(String rootOrg, String id, String userId, String type,
//			List<UserBadgesModel> badges) throws Exception {
//		Calendar cal = Calendar.getInstance();
//		Date now = new Date();
//		// check and insert the Elf badge
//		if (type.equalsIgnoreCase("elf") & cal.get(Calendar.MONTH) == 11) {
//			List<ContentProgressModel> accessedDateList = contentProgressRepo.getFirstAccessedOn(userId,
//					Arrays.asList(new String[] { "Learning Path" }), id);
//			if (accessedDateList.size() > 0) {
//				Date accessedTime = accessedDateList.get(0).getFirstAccessedOn();
//				Badge elfBadge = badgeRepoPG.findByKeyRootOrgAndKeyBadgeId(rootOrg, "Elf").get(0);
//				Date startDate = new Date(elfBadge.getStartDate().getTime());
//				Date endDate = new Date(elfBadge.getEndDate().getTime());
//				if (accessedTime.after(startDate) && accessedTime.before(endDate))
//					badges.add(new UserBadgesModel(
//							new UserBadgesPrimaryKeyModel(rootOrg, userId, elfBadge.getKey().getBadgeId()),
//							elfBadge.getBadgeType(), now, now, 100f, now, 1));
//			}
//		}
//	}
//
//}
