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
package com.infosys.lex.badge.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.infosys.lex.badge.bodhi.repo.TotalPoints;
import com.infosys.lex.badge.bodhi.repo.TotalPointsPrimaryKey;
import com.infosys.lex.badge.bodhi.repo.TotalPointsRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgeRepository;
import com.infosys.lex.badge.bodhi.repo.UserBadgesModel;
import com.infosys.lex.badge.bodhi.repo.UserBadgesPrimaryKeyModel;
import com.infosys.lex.badge.postgredb.projection.BadgeDetailsProjection;
import com.infosys.lex.badge.postgredb.repository.BadgeRepo;
import com.infosys.lex.common.mongo.repo.BatchExecutionData;
import com.infosys.lex.common.mongo.repo.BatchExecutionRepository;
import com.infosys.lex.common.service.AppConfigService;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserServiceImpl;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Service
public class BadgeServiceImpl implements BadgeService {

	private BatchExecutionRepository batchExecutionRepo;
	private BadgeRepo badgeRepo;
	private UserBadgeRepository userBadgeRepo;
	private TotalPointsRepository totalPointsRepo;
	private ContentService contentService;
	private UserUtilityService utilitySvc;
	private UserServiceImpl userSvc;

	@Autowired
	public BadgeServiceImpl(BatchExecutionRepository batchExecutionRepo, BadgeRepo badgeRepo,
			UserBadgeRepository userBadgeRepo, TotalPointsRepository totalPointsRepo, ContentService contentService,
			UserUtilityService utilitySvc, UserServiceImpl userSvc) {
		this.batchExecutionRepo = batchExecutionRepo;
		this.badgeRepo = badgeRepo;
		this.userBadgeRepo = userBadgeRepo;
		this.totalPointsRepo = totalPointsRepo;
		this.contentService = contentService;
		this.utilitySvc = utilitySvc;
		this.userSvc = userSvc;
	}

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

//	private Comparator<Map<String, Object>> intCompare = new Comparator<Map<String, Object>>() {
//		@Override
//		public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//			return ((Double) (Double.parseDouble(m2.get("progress").toString())
//					- Double.parseDouble((m1.get("progress").toString())))).intValue();
//		}
//	};
//
//	private Comparator<Map<String, Object>> dateCompare = new Comparator<Map<String, Object>>() {
//		@Override
//		public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//			try {
//				int c = (formatter.parse((m2.get("first_received_date")).toString()))
//						.compareTo(formatter.parse((m1.get("first_received_date")).toString()));
//				if (c == 0)
//					c = (m1.get("badge_order").toString()).compareTo((m2.get("badge_order").toString()));
//				if (c == 0)
//					c = (m1.get("badge_id").toString()).compareTo((m2.get("badge_id").toString()));
//				return c;
//			} catch (ParseException e) {
//				return 0;
//			}
//		}
//	};
//
//	private Comparator<Map<String, Object>> stringCompare = new Comparator<Map<String, Object>>() {
//		@Override
//		public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//			return (m1.get("badge_order").toString()).compareTo((m2.get("badge_order").toString()));
//		}
//	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.BadgeService#getAllBadges(java.lang.String)
	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public HashMap<String, Object> getAllBadges(String rootOrg, String email_id) throws Exception {
//
//		HashMap<String, Object> returnMap = new HashMap<String, Object>();
//		List<Map<String, Object>> latest = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> earned = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> closeToEarn = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> canEarn = new ArrayList<Map<String, Object>>();
//		DateTime lastWeek = new DateTime((formatter.parse(formatter.format(new Date())))).minusWeeks(1);
//
//		// get all application badges
//		List<Map<String, Object>> allBadges = badgeRepo.findAllBadges();// empty badges
//
//		Map<String, Object> userBadgeDataMap = new HashMap<>();
//
//		// create a map for all the user's badges
//		{
//			Map<String, Object> certificateMap = new HashMap<>();
//			Set<String> contentIds = new HashSet<>();
//			{
//				List<Map<String, Object>> badgeData = userBadgeRepo.findByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg,
//						email_id);
//				for (Map<String, Object> userBadgeData : badgeData) {
//					if (userBadgeData.get("badge_type").toString().toUpperCase().matches("M|C")) {
//						Map<String, Object> temp = new HashMap<>();
//						temp.put("progress", userBadgeData.get("progress"));
//						temp.put("badge_type", userBadgeData.get("badge_type"));
//						temp.put("first_received_date",
//								formatter.format(((Date) userBadgeData.get("first_received_date"))));
//						temp.put("received_count", userBadgeData.get("received_count"));
//						temp.put("last_received_date",
//								formatter.format(((Date) userBadgeData.get("last_received_date"))));
//						if (lastWeek.compareTo(new DateTime(formatter
//								.parse(formatter.format(((Date) userBadgeData.get("first_received_date")))))) < 0)
//							temp.put("is_new", 1);
//						else {
//							temp.put("is_new", 0);
//						}
//						temp.put("threshold", 1);
//						certificateMap.put(userBadgeData.get("badge_id").toString(), temp);
//						contentIds.add(userBadgeData.get("badge_id").toString());
//					} else {
//						userBadgeDataMap.put(userBadgeData.get("badge_id").toString().toLowerCase(), userBadgeData);
//					}
//				}
//			}
//
//			// add certificates and medals to the earned list
//			List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(new ArrayList<>(contentIds),
//					new String[] { "identifier", "name" }, null);
//
//			for (Map<String, Object> source : sourceList) {
//				Map<String, Object> temp = (Map<String, Object>) certificateMap
//						.get(source.get("identifier").toString());
//				if (temp != null) {
//					temp.put("badge_id", source.get("identifier").toString());
//					temp.put("badge_group", temp.get("badge_type"));
//					temp.put("badge_name", source.get("name").toString());
//					if (temp.get("badge_type").toString().toUpperCase().equals("C")) {
//						temp.put("message", "Congratulations on completing the course '" + source.get("name") + "'.");
//						temp.put("hover_text", "For completing '" + source.get("name") + "' course.");
//						temp.put("how_to_earn", "Complete the course");
//						temp.put("image", "/content/Achievements/Achieved/Certificate.png?type=assets");// get image
//					} else {
//						temp.put("message", "Hats off to you, elite learner! Congrats.");
//						temp.put("hover_text", "For completing '" + source.get("name") + "' learning path.");
//						temp.put("how_to_earn", "Complete the learning path");
//						temp.put("image", "/content/Achievements/Achieved/Eilte.png?type=assets");// get image
//					}
//					temp.put("badge_order", temp.get("badge_type"));
//					if (Double.parseDouble(temp.get("progress").toString()) == 100
//							&& !temp.get("first_received_date").toString().equals("")) {
//						temp.put("how_to_earn", temp.get("how_to_earn").toString().replace("Complete", "Completed"));
//						earned.add(temp);
//					}
//				}
//			}
//		}
//
//		DateTime yesterday = new DateTime((formatter.parse(formatter.format(new Date())))).minusDays(1);
//
//		// add the user badges to the earned, close to earn and can earn list
//		for (Iterator<Map<String, Object>> badgeIter = allBadges.iterator(); badgeIter.hasNext();) {
//			Map<String, Object> badge = badgeIter.next();
//			Map<String, Object> userBadgeData = (Map<String, Object>) userBadgeDataMap
//					.get(badge.get("badge_id").toString().toLowerCase());
//			if (userBadgeData == null) {
//				continue;
//			}
//
//			badge.remove("created_date");
//
//			if (badge.get("badge_type").toString().toUpperCase().equals("R") && yesterday.compareTo(
//					new DateTime(formatter.parse(formatter.format(((Date) userBadgeData.get("progress_date")))))) < 0)
//				badge.put("progress", 0);
//			else
//				badge.put("progress", userBadgeData.get("progress"));
//
//			badge.put("first_received_date", formatter.format(((Date) userBadgeData.get("first_received_date"))));
//			badge.put("received_count", userBadgeData.get("received_count"));
//			badge.put("last_received_date", formatter.format(((Date) userBadgeData.get("last_received_date"))));
//
//			if (lastWeek.compareTo(new DateTime(formatter.parse(badge.get("first_received_date").toString()))) < 0) {
//				badge.put("is_new", 1);
//			} else {
//				badge.put("is_new", 0);
//			}
//
//			badge.put("how_to_earn", badge.get("description"));
//			badge.remove("description");
//
//			Double progress = Double.parseDouble(badge.get("progress").toString());
//			badge.put("hover_text", this.getHoverMessage(badge.get("badge_id").toString(), progress));
//			if (progress == 100 || Integer.parseInt(badge.get("received_count").toString()) > 0) {
//				if (badge.get("badge_type").toString().toUpperCase().equals("R")) {
//					if (badge.get("received_count").toString() == "1")
//						badge.put("hover_text", "Earned 1 time");
//					else
//						badge.put("hover_text", "Earned " + badge.get("received_count").toString() + " times");
//				}
//				badge.put("how_to_earn", badge.get("past_description"));
//				badge.remove("past_description");
//				earned.add(badge);
//			} else if (progress >= 85) {
//				badge.remove("message");
//				badge.remove("past_description");
//				closeToEarn.add(badge);
//			} else {
//				// badge.remove("hover_text");
//				badge.remove("message");
//				badge.remove("last_received_date");
//				badge.remove("first_received_date");
//				badge.remove("past_description");
//				canEarn.add(badge);
//			}
//
//			badgeIter.remove();
//		}
//
//		// add the remaining badges to can earn list
//		for (Map<String, Object> badge : allBadges) {
//			badge.remove("created_date");
//			badge.put("progress", 0);
//			badge.put("received_count", 0);
//			badge.put("is_new", 0);
//			badge.remove("message");
//			badge.put("how_to_earn", badge.get("description"));
//			badge.remove("description");
//			badge.remove("past_description");
//			badge.put("hover_text", this.getHoverMessage(badge.get("badge_id").toString(), 0d));
//			canEarn.add(badge);
//		}
//
//		// sort the badges
//		Collections.sort(earned, dateCompare);
//		Collections.sort(closeToEarn, intCompare);
//		Collections.sort(canEarn, stringCompare);
//		if (earned.size() > 0)
//			latest.add(earned.remove(0));
//
//		if (canEarn.size() > 0)
//			if (canEarn.get(0).get("badge_id").equals("NewUser"))
//				canEarn.remove(0);
//
//		returnMap.put("recent", latest);
//		returnMap.put("earned", earned);
//		returnMap.put("closeToEarning", closeToEarn);
//		returnMap.put("canEarn", canEarn);
//		returnMap.put("totalPoints", this.getTotalPoints(email_id));
//
//		// get last updated date from the batch execution collection
//		List<BatchExecutionData> data = batchExecutionRepo.findByBatchName("badge_batch3",
//				PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "batch_started_on")));
//		String lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(new Date(0));
//		if (data.size() > 0)
//			lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(data.get(0).getBatchStartedOn());
//
//		returnMap.put("lastUpdatedDate", lastUpdatedDate);
//		return returnMap;
//	}

	private List<Map<String, Object>> getTotalPoints(String rootOrg, String userId) {

		TotalPoints points = totalPointsRepo.findById(new TotalPointsPrimaryKey(rootOrg, userId)).orElse(null);// total
																												// points
		List<Map<String, Object>> totalPoints = new ArrayList<>();
		if (points == null) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("learning_points", 0);
			temp.put("collaborative_points", 0);
			totalPoints.add(temp);
		} else {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("learning_points", points.getLearningPoints());
			temp.put("collaborative_points", points.getCollaborativePoints());
			totalPoints.add(temp);
		}
		return totalPoints;
	}

//	private String getHoverMessage(String badge_id, Double progress) {
//		String message = "";
//		Integer value = 0;
//		if (badge_id.equals("Quiz1")) {
//			message = "Complete a quiz and get this!";
//		} else if (badge_id.equals("Quiz25")) {
//			value = 25 - ((Double) (progress * 0.25)).intValue();
//			message = value + " more quizzes, to go!";
//		} else if (badge_id.equals("Quiz100")) {
//			value = 100 - progress.intValue();
//			message = value + " more quizzes, to go!";
//		} else if (badge_id.equals("Quiz250")) {
//			value = 250 - ((Double) (progress * 2.5)).intValue();
//			message = value + " more quizzes, to go!";
//		} else if (badge_id.equals("Quiz1000")) {
//			value = 1000 - ((Double) (progress * 10)).intValue();
//			message = value + " more quizzes, to go!";
//		} else if (badge_id.equals("4Day")) {
//			value = 240 - ((Double) (progress * 2.4)).intValue();
//			message = value + " minutes to go!";
//		} else if (badge_id.equals("20Week")) {
//			value = 1200 - ((Double) (progress * 12)).intValue();
//			message = value + " minutes to go!";
//		} else if (badge_id.equals("30MWeek")) {
//			value = 5 - ((Double) (progress * 0.05)).intValue();
//			if (value == 1)
//				message = "One day to go!";
//			else
//				message = value + " more days to go!";
//		} else if (badge_id.equals("30MMonth")) {
//			value = 25 - ((Double) (progress * 0.25)).intValue();
//			if (value == 1)
//				message = "One day to go!";
//			else
//				message = value + " more days to go!";
//		} else if (badge_id.equals("Course1")) {
//			message = "Complete a course and get this!";
//		} else if (badge_id.equals("Course10")) {
//			value = 10 - ((Double) ((progress * 10) / 100)).intValue();
//			message = value + " more courses to go!";
//		} else if (badge_id.equals("Course25")) {
//			value = 25 - ((Double) ((progress * 25) / 100)).intValue();
//			message = value + " more courses to go!";
//		} else if (badge_id.equals("Course100")) {
//			value = 100 - progress.intValue();
//			message = value + " more courses to go!";
//		}
//
//		return message;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.BadgeService#getRecentBadge(java.lang.String)
	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public Map<String, Object> getRecentBadge(String rootOrg, String email_id) throws Exception {
//		Map<String, Object> returnVal = new HashMap<String, Object>();
//		DateTime lastWeek = new DateTime((formatter.parse(formatter.format(new Date())))).minusWeeks(1);
//		Map<String, Object> latest = new HashMap<String, Object>();
//		List<Map<String, Object>> earned = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> allBadges = badgeRepo.findAllBadges();// empty badges
//
//		Map<String, Object> userBadgeDataMap = new HashMap<>();
//
//		{
//			Map<String, Object> certificateMap = new HashMap<>();
//			Set<String> contentIds = new HashSet<>();
//			{
//				List<Map<String, Object>> badgeData = userBadgeRepo.findByRootOrgAndUserIdAndReceivedCount(rootOrg,
//						email_id);
//				for (Map<String, Object> userBadgeData : badgeData) {
//					if (userBadgeData.get("badge_type").toString().toUpperCase().matches("M|C")) {
//						Map<String, Object> temp = new HashMap<>();
//						temp.put("progress", userBadgeData.get("progress"));
//						temp.put("badge_type", userBadgeData.get("badge_type"));
//						temp.put("first_received_date",
//								formatter.format(((Date) userBadgeData.get("first_received_date"))));
//						temp.put("received_count", userBadgeData.get("received_count"));
//						temp.put("last_received_date",
//								formatter.format(((Date) userBadgeData.get("last_received_date"))));
//						if (lastWeek.compareTo(new DateTime(formatter
//								.parse(formatter.format(((Date) userBadgeData.get("first_received_date")))))) < 0)
//							temp.put("is_new", 1);
//						else {
//							temp.put("is_new", 0);
//						}
//						temp.put("threshold", 1);
//						certificateMap.put(userBadgeData.get("badge_id").toString(), temp);
//						contentIds.add(userBadgeData.get("badge_id").toString());
//					} else {
//						userBadgeDataMap.put(userBadgeData.get("badge_id").toString().toLowerCase(), userBadgeData);
//					}
//
//				}
//			}
//
//			for (String id : contentIds) {
//				Map<String, Object> temp = (Map<String, Object>) certificateMap.get(id);
//				if (temp != null) {
//					temp.put("badge_id", id);
//					temp.put("badge_group", temp.get("badge_type"));
//					temp.put("badge_order", temp.get("badge_type"));
//					earned.add(temp);
//				}
//			}
//		}
//
//		DateTime yesterday = new DateTime((formatter.parse(formatter.format(new Date())))).minusDays(1);
//
//		for (Iterator<Map<String, Object>> badgeIter = allBadges.iterator(); badgeIter.hasNext();) {
//			Map<String, Object> badge = badgeIter.next();
//			Map<String, Object> userBadgeData = (Map<String, Object>) userBadgeDataMap
//					.get(badge.get("badge_id").toString().toLowerCase());
//			if (userBadgeData == null) {
//				continue;
//			}
//
//			badge.remove("created_date");
//
//			if (badge.get("badge_type").toString().toUpperCase().equals("R") && yesterday.compareTo(
//					new DateTime(formatter.parse(formatter.format(((Date) userBadgeData.get("progress_date")))))) < 0)
//				badge.put("progress", 0);
//			else
//				badge.put("progress", userBadgeData.get("progress"));
//
//			badge.put("first_received_date", formatter.format(((Date) userBadgeData.get("first_received_date"))));
//			badge.put("received_count", userBadgeData.get("received_count"));
//			badge.put("last_received_date", formatter.format(((Date) userBadgeData.get("last_received_date"))));
//			badge.put("how_to_earn", badge.get("past_description"));
//			badge.remove("description");
//			badge.remove("past_description");
//			if (lastWeek.compareTo(new DateTime(formatter.parse(badge.get("first_received_date").toString()))) < 0) {
//				badge.put("is_new", 1);
//			} else {
//				badge.put("is_new", 0);
//			}
//			Double progress = Double.parseDouble(badge.get("progress").toString());
//			badge.put("hover_text", this.getHoverMessage(badge.get("badge_id").toString(), progress));
//			if (progress == 100 || Integer.parseInt(badge.get("received_count").toString()) > 0) {
//				if (badge.get("badge_type").toString().toUpperCase().equals("R")) {
//					if (badge.get("received_count").toString() == "1")
//						badge.put("hover_text", "Earned 1 time");
//					else
//						badge.put("hover_text", "Earned " + badge.get("received_count").toString() + " times");
//				}
//				earned.add(badge);
//			}
//
//			badgeIter.remove();
//		}
//
//		Collections.sort(earned, dateCompare);
//		if (earned.size() > 0) {
//			latest = earned.remove(0);
//			if (latest.get("badge_type").toString().toUpperCase().matches("M|C")) {
//
//				List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(
//						Arrays.asList(new String[] { latest.get("badge_id").toString() }),
//						new String[] { "identifier", "name" }, null);
//
//				for (Map<String, Object> source : sourceList) {
//					latest.put("badge_name", source.get("name").toString());
//					if (latest.get("badge_type").toString().toUpperCase().equals("C")) {
//						latest.put("message", "Congratulations on completing the course '" + source.get("name") + "'.");
//						latest.put("hover_text", "For completing '" + source.get("name") + "' course.");
//						latest.put("how_to_earn", "Complete the course");
//						latest.put("image", "/content/Achievements/Achieved/Certificate.png?type=assets");// get
//																										// image
//					} else {
//						latest.put("message", "Hats off to you, elite learner! Congrats.");
//						latest.put("hover_text", "For completing '" + source.get("name") + "' learning path.");
//						latest.put("how_to_earn", "Complete the learning path");
//						latest.put("image", "/content/Achievements/Achieved/Eilte.png?type=assets");// get image
//					}
//				}
//			}
//		}
//
//		returnVal.put("recent_badge", latest);
//		returnVal.put("totalPoints", this.getTotalPoints(email_id));
//
//		return returnVal;
//	}

	// ***********************************************************************************
	// NEW APIs using postgres
	// ***********************************************************************************
	@Override
	public Map<String, Object> getAllBadges(String rootOrg, String userId, List<String> language) throws Exception {

		// verify UserId
		if (!utilitySvc.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.user");
		}

		Set<String> preferredLangs = this.validateLanguage(rootOrg, userId, language);

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> latest = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> earned = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> closeToEarn = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> canEarn = new ArrayList<Map<String, Object>>();

		Map<String, Object> userBadgeDataMap = new HashMap<>();

		// Separate user's badges from certificates and medals.
		assignCertificatesAndBadges(rootOrg, userId, earned, userBadgeDataMap);

		// Fetch all available badges.
		List<BadgeDetailsProjection> badgeData = badgeRepo.fetchBadgeDetailsByRootOrgAndLanguage(rootOrg,
				new ArrayList<>(preferredLangs));

		List<Map<String, Object>> allBadges = new ArrayList<Map<String, Object>>();

		for (Iterator<BadgeDetailsProjection> badgeIter = badgeData.iterator(); badgeIter.hasNext();) {
			allBadges.add(extractBadgeDetails(badgeIter.next()));
		}

		// Filtering based on Language preference.
		allBadges = this.filterBadgesBasedOnLanguage(allBadges, preferredLangs);

		// Add badges to earned, close_to_earn and can_earn list for the user.
		for (Iterator<Map<String, Object>> badgeIter = allBadges.iterator(); badgeIter.hasNext();) {
			setupBadgeDataAndProgress(earned, closeToEarn, canEarn, badgeIter, userBadgeDataMap);
		}

		// add the remaining badges(with no progress) to can_earn list.
		addToCanEarn(canEarn, allBadges);

		// sort the badges
		organizeBadges(latest, earned, closeToEarn, canEarn);

		returnMap.put("recent", latest);
		returnMap.put("earned", earned);
		returnMap.put("closeToEarning", closeToEarn);
		returnMap.put("canEarn", canEarn);
		returnMap.put("totalPoints", this.getTotalPoints(rootOrg, userId));

		// get last updated date from the batch execution collection
		setLastUpdatedOn(returnMap);

		return returnMap;
	}

	/**
	 * Process badges based on the lang options received, prioritizing the lang
	 * order.
	 * 
	 * @param allBadges
	 * @param preferredLangs
	 * @return
	 */
	private List<Map<String, Object>> filterBadgesBasedOnLanguage(List<Map<String, Object>> allBadges,
			Set<String> preferredLangs) {

		List<Map<String, Object>> filteredBadges = new ArrayList<Map<String, Object>>();

		Map<String, Map<String, Map<String, Object>>> langBasedBadgeData = this.langBasedBadgeData(allBadges);
		Set<String> processedBadgeIds = new HashSet<>();

		for (String langCode : preferredLangs) {
			Map<String, Map<String, Object>> BadgesForLang = langBasedBadgeData.getOrDefault(langCode, null);
			if (BadgesForLang == null) {
				continue;
			}
			Set<String> badgeIdsForLang = BadgesForLang.keySet();
			for (String badgeId : badgeIdsForLang) {
				if (!processedBadgeIds.contains(badgeId)) {
					filteredBadges.add(langBasedBadgeData.get(langCode).get(badgeId));
					processedBadgeIds.add(badgeId);
				}
			}
		}

		return filteredBadges;
	}

	/**
	 * @param latest
	 * @param earned
	 * @param closeToEarn
	 * @param canEarn
	 */
	private void organizeBadges(List<Map<String, Object>> latest, List<Map<String, Object>> earned,
			List<Map<String, Object>> closeToEarn, List<Map<String, Object>> canEarn) {

		sortBadges(earned, closeToEarn, canEarn);

		removeNotRequiredKeys(Arrays.asList(earned, closeToEarn, canEarn));

		if (earned.size() > 0)
			latest.add(earned.remove(0));

		if (canEarn.size() > 0 && canEarn.get(0).get("badge_id").equals("NewUser"))
			canEarn.remove(0);
	}

	private void removeNotRequiredKeys(List<List<Map<String, Object>>> mapData) {

		for (List<Map<String, Object>> object : mapData) {
			for (Map<String, Object> map : object) {
				map.remove("end_date");
				map.remove("sharable");
//				map.remove("language");
				map.remove("completed_description");
				map.remove("created_by");
				map.remove("group_order");
				map.remove("threshold2");
				map.remove("time_period");
				map.remove("start_date");
			}
		}
	}

	private void sortBadges(List<Map<String, Object>> earned, List<Map<String, Object>> closeToEarn,
			List<Map<String, Object>> canEarn) {

		// Sorting earned badges
		sortBasedOnBadgeId(earned);
		sortBasedOnBadgeOrder(earned);
		sortBasedOnFirstReceivedDate(earned);

		// Sorting closeToEarn badges
		sortBasedOnProgress(closeToEarn);

		// Sorting canEarn badges
		sortBasedOnBadgeOrder(canEarn);
	}

	private void sortBasedOnProgress(List<Map<String, Object>> badgeMap) {

		badgeMap.sort((a, b) -> (((Double) b.get("progress")).compareTo((Double) a.get("progress"))));
	}

	private void sortBasedOnBadgeId(List<Map<String, Object>> badgeMap) {

		badgeMap.sort((a, b) -> ((a.get("badge_id").toString()).compareTo(b.get("badge_id").toString())));
	}

	private void sortBasedOnBadgeOrder(List<Map<String, Object>> badgeMap) {

		badgeMap.sort((a, b) -> (((Integer) a.getOrDefault("threshold2", 0))
				.compareTo((Integer) b.getOrDefault("threshold2", 0))));
		badgeMap.sort((a, b) -> (((BigDecimal) a.getOrDefault("threshold1", BigDecimal.ZERO))
				.compareTo((BigDecimal) b.getOrDefault("threshold1", BigDecimal.ZERO))));
		badgeMap.sort((a, b) -> (((Integer) a.getOrDefault("time_period", 0))
				.compareTo((Integer) b.getOrDefault("time_period", 0))));
		badgeMap.sort((a, b) -> (((Integer) a.getOrDefault("group_order", 0))
				.compareTo((Integer) b.getOrDefault("group_order", 0))));
	}

	private void sortBasedOnFirstReceivedDate(List<Map<String, Object>> badgeMap) {

		badgeMap.sort((a, b) -> {
			try {
				return ((formatter.parse(b.get("first_received_date").toString()))
						.compareTo((formatter.parse(a.get("first_received_date").toString()))));
			} catch (ParseException e) {
				return 0;
			}
		});
	}

	/**
	 * Get the last updated on from py-batch.
	 * 
	 * @param returnMap
	 */
	private void setLastUpdatedOn(HashMap<String, Object> returnMap) {
		List<BatchExecutionData> data = batchExecutionRepo.findByBatchName("badge_batch3",
				PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "batch_started_on")));
		String lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(new Date(0));
		if (data.size() > 0)
			lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(data.get(0).getBatchStartedOn());

		returnMap.put("lastUpdatedDate", lastUpdatedDate);
	}

	/**
	 * Add the remaining badges to can_earn list.
	 * 
	 * @param canEarn
	 * @param allBadges
	 */
	private void addToCanEarn(List<Map<String, Object>> canEarn, List<Map<String, Object>> allBadges) {
		for (Map<String, Object> badge : allBadges) {
			badge.put("progress", 0);
			badge.put("received_count", 0);
			badge.put("is_new", 0);
			badge.put("how_to_earn", badge.get("badge_description"));
			badge.put("hover_text", this.getHoverMessagePG(badge, 0d));
			canEarn.add(badge);
		}
	}

	private String getHoverMessagePG(Map<String, Object> badge, Double progress) {

		String message = "";
		Integer value = 0;

		Integer groupOrder = Integer.parseInt(badge.get("group_order").toString());
		BigDecimal threshold1 = BigDecimal.valueOf(Double.parseDouble(badge.get("threshold1").toString()));
		Integer threshold2 = Integer.parseInt(badge.get("threshold2").toString());

		switch (groupOrder) {

		case 2:

			if (threshold1 == BigDecimal.ONE) {
				message = "Complete a quiz and get this!";
			} else {
//					value = threshold1 - ((progress * (threshold1 / 100))).intValue();
				value = threshold1
						.subtract((threshold1.divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(progress)))
						.intValue();
				message = value + " more quizzes to go!";
			}
			break;

		case 3:

			if (threshold1 == BigDecimal.ONE) {
				message = "Complete a course and get this!";
			} else {
//					value = threshold1 - ((progress * (threshold1 / 100))).intValue();
				value = threshold1
						.subtract((threshold1.divide(BigDecimal.valueOf(100))).multiply(BigDecimal.valueOf(progress)))
						.intValue();
				message = value + " more courses to go!";
			}
			break;

		case 4:

//				value = (threshold1*60) - ((progress * ((threshold1*60) / 100))).intValue();
			double maxMinutes = threshold1.multiply(BigDecimal.valueOf(60)).doubleValue();
			value = ((Long) Math.round(maxMinutes - (progress * (maxMinutes / 100)))).intValue();
			message = value + " minutes to go!";
			break;

		case 5:

//				double a = progress * ((threshold2 / 100));
			value = threshold2 - ((Long) Math.round((progress * (threshold2 / 100)))).intValue();
			if (value == 1)
				message = "One day to go!";
			else
				message = value + " more days to go!";
			break;

		default:
			break;
		}

		return message;
	}

	/**
	 * Categorize and separate the badges based on progress.
	 * 
	 * @param earned
	 * @param closeToEarn
	 * @param canEarn
	 * @param badgeIter
	 * @param userBadgeDataMap
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	private void setupBadgeDataAndProgress(List<Map<String, Object>> earned, List<Map<String, Object>> closeToEarn,
			List<Map<String, Object>> canEarn, Iterator<Map<String, Object>> badgeIter,
			Map<String, Object> userBadgeDataMap) throws ParseException {

		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.WEEK_OF_MONTH, -1);
		Calendar timeToCompare = Calendar.getInstance();

		Map<String, Object> badge = badgeIter.next();

		Map<String, Object> userBadgeData = (Map<String, Object>) userBadgeDataMap
				.get(badge.get("badge_id").toString().toLowerCase());
		if (userBadgeData == null) {
			return;
		}

		badge.put("how_to_earn", badge.get("badge_description"));
		badge.remove("created_date");
		badge.remove("badge_description");

		// For repetitive badge, consecutive progress is considered. Else set to zero.
		timeToCompare.setTime((Date) userBadgeData.get("progress_date"));
		if (badge.get("badge_type").toString().equalsIgnoreCase("R") && yesterday.compareTo(timeToCompare) < 0)
			badge.put("progress", 0);
		else
			badge.put("progress", userBadgeData.get("progress"));

		badge.put("first_received_date", formatter.format(((Date) userBadgeData.get("first_received_date"))));
		badge.put("received_count", userBadgeData.get("received_count"));
		badge.put("last_received_date", formatter.format(((Date) userBadgeData.get("last_received_date"))));

		// Check if badge is new or not.
		timeToCompare.setTime((Date) userBadgeData.get("first_received_date"));
		if (lastWeek.compareTo(timeToCompare) < 0) {
			badge.put("is_new", 1);
		} else {
			badge.put("is_new", 0);
		}

		categorizeBadges(earned, closeToEarn, canEarn, badge);

		badgeIter.remove();
	}

	private Map<String, Object> extractBadgeDetails(BadgeDetailsProjection badgedetails) {

		Map<String, Object> badge = new HashMap<>();

		badge.put("badge_id", badgedetails.getbadgeId());
		badge.put("image", badgedetails.getimage());
		badge.put("badge_type", badgedetails.getbadgeType());

		Optional<Optional<BigDecimal>> threshold1 = Optional.ofNullable(badgedetails.getthreshold1());
		badge.put("threshold1", threshold1.isPresent() ? badgedetails.getthreshold1().get() : BigDecimal.ZERO);

		Optional<Optional<Integer>> timePeriod = Optional.ofNullable(badgedetails.gettimePeriod());
		badge.put("time_period", timePeriod.isPresent() ? badgedetails.gettimePeriod().get() : 0);

		Optional<Optional<Integer>> threshold2 = Optional.ofNullable(badgedetails.getthreshold2());
		badge.put("threshold2", threshold2.isPresent() ? badgedetails.getthreshold2().get() : 0);

		badge.put("created_date", badgedetails.getcreatedDate());
		badge.put("created_by", badgedetails.getcreatedBy());

		Optional<Optional<Timestamp>> startDate = Optional.ofNullable(badgedetails.getstartDate());
		badge.put("start_date",
				startDate.isPresent() ? badgedetails.getstartDate().get() : new Timestamp(System.currentTimeMillis()));

		Optional<Optional<Timestamp>> endDate = Optional.ofNullable(badgedetails.getendDate());
		badge.put("end_date",
				endDate.isPresent() ? badgedetails.getendDate().get() : new Timestamp(System.currentTimeMillis()));

		badge.put("sharable", badgedetails.getsharable());
		badge.put("badge_group", badgedetails.getbadgeGroup());
		badge.put("group_order", badgedetails.getgroupOrder());
		badge.put("language", badgedetails.getlanguage());
		badge.put("badge_name", badgedetails.getbadgeName());
		badge.put("badge_description", badgedetails.getbadgeDescription());
		badge.put("completed_description", badgedetails.getcompletedDescription());
		badge.put("message", badgedetails.getmessage());

		Optional<Optional<String>> badgeGroupText = Optional.ofNullable(badgedetails.getbadgeGroupText());
		badge.put("badge_group_text", badgeGroupText.isPresent() ? badgedetails.getbadgeGroupText().get() : "no data");
		return badge;
	}

	/**
	 * Categorize the badges based on completion status.
	 * 
	 * @param earned
	 * @param closeToEarn
	 * @param canEarn
	 * @param badge
	 */
	private void categorizeBadges(List<Map<String, Object>> earned, List<Map<String, Object>> closeToEarn,
			List<Map<String, Object>> canEarn, Map<String, Object> badge) {

		Double progress = Double.parseDouble(badge.get("progress").toString());
		badge.put("hover_text", this.getHoverMessagePG(badge, progress));

		String badgeReceivedCount = badge.get("received_count").toString();
		String badgeType = badge.get("badge_type").toString().toUpperCase();
		if (progress == 100 || (Integer.parseInt(badgeReceivedCount) > 0 && badgeType.equals("R"))) {
			if (badgeType.equals("R")) {
				if (badgeReceivedCount.equals("1"))
					badge.put("hover_text", "Earned 1 time");
				else
					badge.put("hover_text", "Earned " + badgeReceivedCount + " times");
			}
//			badge.put("how_to_earn", badge.get("past_description"));
			badge.remove("past_description");
			earned.add(badge);
		} else if (progress >= 85) {
			badge.remove("message");
			badge.remove("past_description");
			closeToEarn.add(badge);
		} else {
			// badge.remove("hover_text");
			badge.remove("message");
			badge.remove("last_received_date");
			badge.remove("first_received_date");
			badge.remove("past_description");
			canEarn.add(badge);
		}
	}

	/**
	 * Process certificates and Medals for the user. Add necessary messages for
	 * completion.
	 * 
	 * @param userId
	 * @param earned
	 * @param userBadgeDataMap
	 * @throws ParseException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void assignCertificatesAndBadges(String rootOrg, String userId, List<Map<String, Object>> earned,
			Map<String, Object> userBadgeDataMap) throws ParseException, IOException {

		Map<String, Object> certificateMap = new HashMap<>();
		Set<String> contentIds = new HashSet<>();

		separatingMedalsAndCertificates(rootOrg, userId, userBadgeDataMap, certificateMap, contentIds);

		// add certificates and medals to the earned list
		List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(new ArrayList<>(contentIds),
				new String[] { "identifier", "name" }, "Live");

		for (Map<String, Object> source : sourceList) {
			Map<String, Object> temp = (Map<String, Object>) certificateMap.get(source.get("identifier").toString());
			if (temp != null) {
				temp.put("badge_id", source.get("identifier").toString());
				temp.put("badge_group", temp.get("badge_type"));
				temp.put("badge_name", source.get("name").toString());
				if (temp.get("badge_type").toString().equalsIgnoreCase("C")) {
					temp.put("message", "Congratulations on completing the course '" + source.get("name") + "'.");
					temp.put("hover_text", "For completing '" + source.get("name") + "' course.");
					temp.put("how_to_earn", "Complete the course");
					temp.put("image", "/content/Achievements/Achieved/Certificate.png?type=assets");
				} else {
					temp.put("message", "Hats off to you, elite learner! Congrats.");
					temp.put("hover_text", "For completing '" + source.get("name") + "' learning path.");
					temp.put("how_to_earn", "Complete the learning path");
					temp.put("image", "/content/Achievements/Achieved/Eilte.png?type=assets");
				}
				temp.put("badge_order", temp.get("badge_type"));
				if (Double.parseDouble(temp.get("progress").toString()) == 100
						&& !temp.get("first_received_date").toString().equals("")) {
					temp.put("how_to_earn", temp.get("how_to_earn").toString().replace("Complete", "Completed"));
					earned.add(temp);
				}
			}
		}
	}

	/**
	 * Process certificates and Medals for the user. Add them to earned list and
	 * rest to userBadgeDataMap.
	 * 
	 * @param userId
	 * @param userBadgeDataMap
	 * @param certificateMap
	 * @param contentIds
	 * @throws ParseException
	 */
	private void separatingMedalsAndCertificates(String rootOrg, String userId, Map<String, Object> userBadgeDataMap,
			Map<String, Object> certificateMap, Set<String> contentIds) throws ParseException {

		List<Map<String, Object>> badgeData = userBadgeRepo.findByPrimaryKeyRootOrgAndPrimaryKeyUserId(rootOrg, userId);

		Calendar lastWeek = Calendar.getInstance();
		lastWeek.add(Calendar.WEEK_OF_MONTH, -1);

		Calendar timeToCompare = Calendar.getInstance();

		for (Map<String, Object> userBadgeData : badgeData) {
			if (userBadgeData.get("badge_type").toString().toUpperCase().matches("M|C")) {
				Map<String, Object> temp = new HashMap<>();
				temp.put("progress", userBadgeData.get("progress"));
				temp.put("badge_type", userBadgeData.get("badge_type"));
				temp.put("first_received_date", formatter.format(((Date) userBadgeData.get("first_received_date"))));
				temp.put("received_count", userBadgeData.get("received_count"));
				temp.put("last_received_date", formatter.format(((Date) userBadgeData.get("last_received_date"))));

				timeToCompare.setTime((Date) userBadgeData.get("first_received_date"));

				if (lastWeek.compareTo(timeToCompare) < 0)
					temp.put("is_new", 1);
				else {
					temp.put("is_new", 0);
				}
				temp.put("threshold", 1);
				certificateMap.put(userBadgeData.get("badge_id").toString(), temp);
				contentIds.add(userBadgeData.get("badge_id").toString());
			} else {
				userBadgeDataMap.put(userBadgeData.get("badge_id").toString().toLowerCase(), userBadgeData);
			}
		}
	}

	// Recent Badge Method
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getRecentBadge(String rootOrg, String userId, List<String> language) throws Exception {

		// verify UserId
		if (!utilitySvc.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		Set<String> preferredLangs = this.validateLanguage(rootOrg, userId, language);

		Map<String, Object> returnVal = new HashMap<String, Object>();
		DateTime lastWeek = new DateTime((formatter.parse(formatter.format(new Date())))).minusWeeks(1);
		Map<String, Object> latest = new HashMap<String, Object>();
		List<Map<String, Object>> earned = new ArrayList<Map<String, Object>>();
		List<BadgeDetailsProjection> badgeDetails = badgeRepo.fetchBadgeDetailsByRootOrgAndLanguage(rootOrg, language);
		Map<String, Object> userBadgeDataMap = new HashMap<>();

		{
			Map<String, Object> certificateMap = new HashMap<>();
			Set<String> contentIds = new HashSet<>();
			{
				List<Map<String, Object>> badgeData = userBadgeRepo.findByRootOrgAndUserIdAndReceivedCount(rootOrg,
						userId);
				for (Map<String, Object> userBadgeData : badgeData) {
					if (userBadgeData.get("badge_type").toString().toUpperCase().matches("M|C")) {
						Map<String, Object> temp = new HashMap<>();
						temp.put("progress", userBadgeData.get("progress"));
						temp.put("badge_type", userBadgeData.get("badge_type"));
						temp.put("first_received_date",
								formatter.format(((Date) userBadgeData.get("first_received_date"))));
						temp.put("received_count", userBadgeData.get("received_count"));
						temp.put("last_received_date",
								formatter.format(((Date) userBadgeData.get("last_received_date"))));
						if (lastWeek.compareTo(new DateTime(formatter
								.parse(formatter.format(((Date) userBadgeData.get("first_received_date")))))) < 0)
							temp.put("is_new", 1);
						else {
							temp.put("is_new", 0);
						}
						temp.put("threshold", 1);
						certificateMap.put(userBadgeData.get("badge_id").toString(), temp);
						contentIds.add(userBadgeData.get("badge_id").toString());
					} else {
						userBadgeDataMap.put(userBadgeData.get("badge_id").toString().toLowerCase(), userBadgeData);
					}

				}
			}

			for (String id : contentIds) {
				Map<String, Object> temp = (Map<String, Object>) certificateMap.get(id);
				if (temp != null) {
					temp.put("badge_id", id);
					temp.put("badge_group", temp.get("badge_type"));
					temp.put("badge_order", temp.get("badge_type"));
					earned.add(temp);
				}
			}
		}

		DateTime yesterday = new DateTime((formatter.parse(formatter.format(new Date())))).minusDays(1);

		List<Map<String, Object>> allBadges = new ArrayList<Map<String, Object>>();

		for (Iterator<BadgeDetailsProjection> badgeIter = badgeDetails.iterator(); badgeIter.hasNext();) {
			allBadges.add(this.extractBadgeDetails(badgeIter.next()));
		}

		allBadges = this.filterBadgesBasedOnLanguage(allBadges, preferredLangs);

		for (Map<String, Object> badge : allBadges) {
			Map<String, Object> userBadgeData = (Map<String, Object>) userBadgeDataMap
					.get(badge.get("badge_id").toString().toLowerCase());
			if (userBadgeData == null) {
				continue;
			}

			badge.remove("created_date");

			if (badge.get("badge_type").toString().toUpperCase().equals("R") && yesterday.compareTo(
					new DateTime(formatter.parse(formatter.format(((Date) userBadgeData.get("progress_date")))))) < 0)
				badge.put("progress", 0);
			else
				badge.put("progress", userBadgeData.get("progress"));

			badge.put("first_received_date", formatter.format(((Date) userBadgeData.get("first_received_date"))));
			badge.put("received_count", userBadgeData.get("received_count"));
			badge.put("last_received_date", formatter.format(((Date) userBadgeData.get("last_received_date"))));
			badge.put("how_to_earn", badge.get("past_description"));
			badge.remove("description");
			badge.remove("past_description");
			if (lastWeek.compareTo(new DateTime(formatter.parse(badge.get("first_received_date").toString()))) < 0) {
				badge.put("is_new", 1);
			} else {
				badge.put("is_new", 0);
			}
			Double progress = Double.parseDouble(badge.get("progress").toString());
			// badge.put("hover_text",
			// this.getHoverMessagePG(badge.get("badge_id").toString(), progress));
			if (progress == 100 || Integer.parseInt(badge.get("received_count").toString()) > 0) {
				if (badge.get("badge_type").toString().toUpperCase().equals("R")) {
					if (badge.get("received_count").toString() == "1")
						badge.put("hover_text", "Earned 1 time");
					else
						badge.put("hover_text", "Earned " + badge.get("received_count").toString() + " times");
				}
				earned.add(badge);
			}
		}

		this.sortBasedOnBadgeId(earned);
		this.sortBasedOnBadgeOrder(earned);
		this.sortBasedOnFirstReceivedDate(earned);

		if (earned.size() > 0) {
			latest = earned.remove(0);
			if (latest.get("badge_type").toString().toUpperCase().matches("M|C")) {

				List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(
						Arrays.asList(new String[] { latest.get("badge_id").toString() }),
						new String[] { "identifier", "name" }, null);

				for (Map<String, Object> source : sourceList) {
					latest.put("badge_name", source.get("name").toString());
					if (latest.get("badge_type").toString().toUpperCase().equals("C")) {
						latest.put("message", "Congratulations on completing the course '" + source.get("name") + "'.");
						latest.put("hover_text", "For completing '" + source.get("name") + "' course.");
						latest.put("how_to_earn", "Complete the course");
						latest.put("image", "/content/Achievements/Achieved/Certificate.png?type=assets");

					} else {
						latest.put("message", "Hats off to you, elite learner! Congrats.");
						latest.put("hover_text", "For completing '" + source.get("name") + "' learning path.");
						latest.put("how_to_earn", "Complete the learning path");
						latest.put("image", "/content/Achievements/Achieved/Eilte.png?type=assets");
					}
				}
			}
		}

		this.removeNotRequiredKeys(latest);
		returnVal.put("recent_badge", latest);
		returnVal.put("totalPoints", this.getTotalPoints(rootOrg, userId));

		return returnVal;
	}

	private void removeNotRequiredKeys(Map<String, Object> map) {
		map.remove("end_date");
		map.remove("sharable");
		map.remove("language");
		map.remove("completed_description");
		map.remove("created_by");
		map.remove("group_order");
		map.remove("threshold2");
		map.remove("time_period");
		map.remove("start_date");
	}

	@Override
	public List<BadgeDetailsProjection> getAllBadgesForRootOrg(String rootOrg) {
		List<BadgeDetailsProjection> badges = badgeRepo.fetchBadgeDetailsByRootOrgAndLang(rootOrg);
		return badges;

	}

	/**
	 * This method is used to convert all badge data into a form
	 * {"lang_X":{"badge_id_Y":{badge_details_Y}}}
	 * 
	 * @param allBadges
	 */
	private Map<String, Map<String, Map<String, Object>>> langBasedBadgeData(List<Map<String, Object>> allBadges) {

		Map<String, Map<String, Map<String, Object>>> langBasedBadges = new HashMap<>();
		for (Map<String, Object> map : allBadges) {
			String lang = map.get("language").toString();
			Map<String, Map<String, Object>> badgeIdToBadgeDataForLang = langBasedBadges.getOrDefault(lang, null);
			if (badgeIdToBadgeDataForLang == null) {
				Map<String, Map<String, Object>> badgeIdToBadgeData = new HashMap<>();
				badgeIdToBadgeData.put(map.get("badge_id").toString(), map);
				langBasedBadges.put(map.get("language").toString(), badgeIdToBadgeData);
			} else {
				Map<String, Object> badgeKey = badgeIdToBadgeDataForLang.getOrDefault("badge_id", null);
				if (badgeKey == null) {
					badgeIdToBadgeDataForLang.put(map.get("badge_id").toString(), map);
				}
			}
		}

		return langBasedBadges;
	}

	// Utility Method(s)
	private Set<String> validateLanguage(String rootOrg, String userId, List<String> language) {
		Set<String> preferredLangs = new LinkedHashSet<>();
		if (language == null || language.size() == 0) {
			try {
				Map<String, Object> userPreferenceData = userSvc.getUserPreferences(rootOrg, userId);
				if (userPreferenceData != null) {
					String preferredlanguage = userPreferenceData.getOrDefault("selectedLanguage", null).toString();
					if (preferredlanguage != null && !preferredlanguage.isEmpty()) {
						preferredLangs.addAll(Arrays.asList(preferredlanguage.split(",")));
					}
				}
			} catch (Exception e) {
				preferredLangs.add("en");
			}
		} else {
			preferredLangs.addAll(language);
		}
		preferredLangs.add("en");

		return preferredLangs;
	}
}
