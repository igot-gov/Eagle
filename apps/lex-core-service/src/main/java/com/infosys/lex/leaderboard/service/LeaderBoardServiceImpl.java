/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.leaderboard.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.mongo.repo.BatchExecutionData;
import com.infosys.lex.common.mongo.repo.BatchExecutionRepository;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.exception.NoContentException;
import com.infosys.lex.leaderboard.bodhi.repo.LeaderBoardRankRepository;
import com.infosys.lex.leaderboard.bodhi.repo.LeaderBoardRepository;

@Service
public class LeaderBoardServiceImpl implements LeaderBoardService {

	@Autowired
	BatchExecutionRepository beRepository;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	LeaderBoardRepository leaderBoardRepo;

	@Autowired
	LeaderBoardRankRepository leaderBoardRankRepo;

	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public Map<String, Object> getLeaderBoard(String rootOrg, String durationType, String leaderboardType,
			String userId, String year, String durationValue) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.error");
		}

		// Parameter Validations
		this.validateParameters(durationType, leaderboardType, year, durationValue);

		Map<String, Object> returnValue = new HashMap<String, Object>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		Integer leaderboardYear = 0;
		Integer durationNumber = 0;
		Integer leaderboardYearPrev = 0;
		Integer durationNumberPrev = 0;
		Integer leaderboardYearNext = 0;
		Integer durationNumberNext = 0;
		String startDate = "";
		String endDate = "";
		LocalDate currentDate = LocalDate.now();
		LocalDate calDate = LocalDate.now();

		if (durationType.toUpperCase().equals("M")) {
			calDate = LocalDate.of(!year.equals("0") ? Integer.parseInt(year) : calDate.get(ChronoField.YEAR),
					!durationValue.equals("0") ? Integer.parseInt(durationValue)
							: calDate.get(ChronoField.MONTH_OF_YEAR),
					calDate.get(ChronoField.DAY_OF_MONTH));

			// If date to be calculated from is 1st date of month then month is updated to previous month
			if (currentDate.get(ChronoField.DAY_OF_MONTH) == 1
					&& calDate.get(ChronoField.MONTH_OF_YEAR) == currentDate.get(ChronoField.MONTH_OF_YEAR)) {
				calDate = calDate.minus(1, ChronoUnit.MONTHS);
			}

			durationNumber = calDate.get(ChronoField.MONTH_OF_YEAR);
			leaderboardYear = calDate.get(ChronoField.YEAR);

			//first day of the month
			calDate = calDate.withDayOfMonth(1);

			startDate = dateTimeFormatter.format(calDate);
			//date updated to first day of next month
			calDate = calDate.plus(1, ChronoUnit.MONTHS);

			//next month year and month is updated
			durationNumberNext = calDate.get(ChronoField.MONTH_OF_YEAR);
			leaderboardYearNext = calDate.get(ChronoField.YEAR);

			//current month last day
			calDate = calDate.minus(1, ChronoUnit.DAYS);
			
			endDate = dateTimeFormatter.format(calDate);

			//previous month last day
			calDate = calDate.minus(1, ChronoUnit.MONTHS);
			
			//previous month/year is updated
			durationNumberPrev = calDate.get(ChronoField.MONTH_OF_YEAR);
			leaderboardYearPrev = calDate.get(ChronoField.YEAR);
		} else {
			calDate = calDate.with(IsoFields.WEEK_BASED_YEAR,
					!year.equals("0") ? Integer.parseInt(year) : calDate.get(IsoFields.WEEK_BASED_YEAR));
			calDate = calDate.with(IsoFields.WEEK_OF_WEEK_BASED_YEAR,
					!durationValue.equals("0") ? Integer.parseInt(durationValue)
							: calDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
			
			//if the current date is the first day of the week then update the week to previous week
			if (currentDate.get(ChronoField.DAY_OF_WEEK) == 1 && currentDate
					.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == calDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)) {
				calDate = calDate.minusWeeks(1);
			}
			
			//update the year and the week number in the year
			durationNumber = calDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
			leaderboardYear = calDate.get(IsoFields.WEEK_BASED_YEAR);

			//update to first day of the week
			calDate = calDate.with(ChronoField.DAY_OF_WEEK, 1);
			
			
			startDate = dateTimeFormatter.format(calDate);
			
			//update the date to first day of the next week
			calDate = calDate.plusWeeks(1);

			//update the next week date year and the next week number in the year
			durationNumberNext = calDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
			leaderboardYearNext = calDate.get(IsoFields.WEEK_BASED_YEAR);

			//update the date to the last date of the current week set
			calDate = calDate.minus(1, ChronoUnit.DAYS);

			
			endDate = dateTimeFormatter.format(calDate);

			//update to last date of previous week last day
			calDate = calDate.minusWeeks(1);

			//update the previous week year and the previous week number in the year
			durationNumberPrev = calDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
			leaderboardYearPrev = calDate.get(IsoFields.WEEK_BASED_YEAR);
		}

		propertyMap.put("leaderboard_year", leaderboardYear);
		propertyMap.put("duration_type", durationType.toUpperCase());
		propertyMap.put("duration_value", durationNumber);
		propertyMap.put("leaderboard_type", leaderboardType.toUpperCase());

		returnValue.putAll(propertyMap);// adds to return
		returnValue.put("start_date", startDate);
		returnValue.put("end_date", endDate);

		propertyMap.put("user_id", userId);

		List<Map<String, Object>> result = leaderBoardRepo.findRankOfUserInLeaderBoard(rootOrg, leaderboardYear,
				durationType.toUpperCase(), durationNumber, leaderboardType.toUpperCase(), userId);

		List<Integer> leaderBoardYearList = Arrays.asList(leaderboardYearPrev, leaderboardYear, leaderboardYearNext);
		List<Integer> durationList = Arrays.asList(durationNumberPrev, durationNumber, durationNumberNext);
		List<Integer> rankList = null;
		if (result.size() != 0) {
			Integer rank = Integer.parseInt(((HashMap<String, Object>) result.get(0)).get("rank").toString());
			rankList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, rank);
		} else {
			rankList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

		result = leaderBoardRankRepo.findAllLeadersByRankList(rootOrg, rankList, durationType, leaderboardType,
				leaderBoardYearList, durationList);
		if (result.size() != 0) {
			
			//previous and next year are added if exits to response and those records are removed from the final result List
			for (Iterator<Map<String, Object>> i = result.iterator(); i.hasNext();) {
				Map<String, Object> map = i.next();
				Integer y = Integer.parseInt(map.get("leaderboard_year").toString());
				Integer v = Integer.parseInt(map.get("duration_value").toString());
				if (y.equals(leaderboardYearPrev) && v.equals(durationNumberPrev) && !returnValue.containsKey("prev")) {
					Map<String, Object> prev = new HashMap<String, Object>();
					prev.put("leaderboard_year", y);
					prev.put("duration_value", v);
					returnValue.put("prev", prev);
				} else if (y.equals(leaderboardYearNext) && v.equals(durationNumberNext)
						&& !returnValue.containsKey("next")) {
					Map<String, Object> next = new HashMap<String, Object>();
					next.put("leaderboard_year", y);
					next.put("duration_value", v);
					returnValue.put("next", next);
				} else if (y.equals(leaderboardYear) && v.equals(durationNumber))
					continue;
				i.remove();
			}
			this.updateUserDetails(rootOrg, result, true);
		}
		returnValue.put("items", result);
		
		List<BatchExecutionData> data = beRepository.findByBatchName("leader_batch4",
				PageRequest.of(0, 1, new Sort(Sort.Direction.DESC, "batch_started_on")));
		String lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(new Date(0));
		if (data.size() > 0)
			lastUpdatedDate = new SimpleDateFormat("dd MMM yyyy 00:00 z").format(data.get(0).getBatchStartedOn());
		returnValue.put("lastUpdatedDate", lastUpdatedDate);

		return returnValue;
	}

	@Override
	public List<Map<String, Object>> pastTopLearners(String rootOrg, String userId, String duration_type,
			String leaderboard_type) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User" + userId);
		}

		// Validating Parameters
		this.validateParameters(duration_type, leaderboard_type);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("duration_type", duration_type.toUpperCase());
		propertyMap.put("leaderboard_type", leaderboard_type.toUpperCase());
		propertyMap.put("rank", 1);

//		LeaderBoardPrimaryKey 
		result = leaderBoardRankRepo
				.findByLeaderBoardRankKeyRootOrgAndLeaderBoardRankKeyRankAndLeaderBoardRankKeyDurationTypeAndLeaderBoardRankKeyLeaderBoardType(
						rootOrg, 1, duration_type, leaderboard_type);

		// ranks of current month is not shown
		if (result.size() > 0)
			if (Integer.parseInt(
					result.get(0).get("duration_value").toString()) == (Calendar.getInstance().get(Calendar.MONTH) + 1)
					&& Integer.parseInt(result.get(0).get("leaderboard_year").toString()) == (Calendar.getInstance()
							.get(Calendar.YEAR)))
				result.remove(0);

		this.updateUserDetails(rootOrg, result, false);
		return result;
	}

	@SuppressWarnings("unchecked")
	private void updateUserDetails(String rootOrg, List<Map<String, Object>> userMapList, boolean isLeaderboard)
			throws Exception {
		if (!userMapList.isEmpty()) {
			Float maxPoints = Float.parseFloat(userMapList.get(0).get("points").toString());

			List<String> userEmails = new ArrayList<String>();
			List<String> userUUIDs = new ArrayList<String>();
			for (Map<String, Object> map : userMapList)
				userUUIDs.add(map.get("user_id").toString());

			Map<String, Object> userUUIDEmailMap = userUtilService.getUserEmailsFromUserIds(rootOrg, userUUIDs);
			for (Map<String, Object> map : userMapList) {
				if (map.get("user_id").toString() != "") {
					String userUUID = map.get("user_id").toString();
					String userEmail = "";
					if (userUUIDEmailMap.containsKey(userUUID)) {
						map.put("email_id", userUUIDEmailMap.get(userUUID).toString());
						userEmails.add(userUUIDEmailMap.get(userUUID).toString());
					}
					map.put("email_id", userEmail);

				}
			}
			if ("su".equals(userUtilService.getUserDataSource(rootOrg))) {
				List<Map<String, Object>> userData = userUtilService.getUsersFromActiveDirectory(userEmails);

				Map<String, Object> userDataMap = new HashMap<String, Object>();
				for (Map<String, Object> user : userData) {
					userDataMap.put(user.get("onPremisesUserPrincipalName").toString().toLowerCase(), user);
				}
				// Map<String, String> userPhotos =
				// userDataUtil.getMultipleUserPhotoFromActiveDirectory(users);

				for (Map<String, Object> map : userMapList) {
					Map<String, Object> user = null;

					if (userDataMap.containsKey(map.get("email_id").toString().toLowerCase()))
						user = (Map<String, Object>) userDataMap.get(map.get("email_id").toString().toLowerCase());

					if (user != null) {
						String temp = user.get("department") == null ? "" : user.get("department").toString();
						map.put("unit", temp);

						temp = user.get("jobTitle") == null ? "" : user.get("jobTitle").toString();
						map.put("designation", temp);

						temp = user.get("surname") == null ? "" : user.get("surname").toString();
						map.put("last_name", temp);

						temp = user.get("givenName") == null ? "" : user.get("givenName").toString();

						map.put("first_name", temp);

					} else {
						String temp = "";
						map.put("unit", temp);
						map.put("designation", temp);
						map.put("last_name", temp);
						map.put("first_name", temp);

					}
					map.remove("start_date");
					map.remove("end_date");

					// These keys are updated in map for leaderboard api call but not for past
					// top-performer api
					if (isLeaderboard) {
						map.put("percentile",
								maxPoints == 0 ? 0 : (Float.parseFloat(map.get("points").toString())) / maxPoints);
						map.remove("leaderboard_year");
						map.remove("leaderboard_type");
						map.remove("duration_type");
						map.remove("duration_value");
					}
				}
			} else {

				Map<String, Object> pidUsersData = new HashMap<String, Object>();
				if (!userUUIDs.isEmpty())
					pidUsersData = userUtilService.getUsersDataFromUserIds(rootOrg, userUUIDs,
							Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.MIDDLE_NAME, PIDConstants.LAST_NAME,
									PIDConstants.DEPARTMENT_NAME, PIDConstants.JOB_ROLE, PIDConstants.JOB_TITLE,
									PIDConstants.ORG, PIDConstants.UNIT_NAME));

				for (Map<String, Object> map : userMapList) {
					Map<String, Object> userPidData = null;

					if (pidUsersData.containsKey(map.get("user_id").toString().toLowerCase()))
						userPidData = (Map<String, Object>) pidUsersData
								.get(map.get("user_id").toString().toLowerCase());

					if (userPidData != null) {
						String department = userPidData.get(PIDConstants.DEPARTMENT_NAME) != null
								? userPidData.get(PIDConstants.DEPARTMENT_NAME).toString()
								: "";

						department = department.isEmpty() ? (userPidData.get(PIDConstants.UNIT_NAME) != null
								? userPidData.get(PIDConstants.UNIT_NAME).toString()
								: "") : department;
						department = department.isEmpty()
								? (userPidData.get(PIDConstants.ORG) != null
										? userPidData.get(PIDConstants.ORG).toString()
										: "")
								: department;
						// sets department as org if department is empty else if department is shown if
						// both are empty then unit name
						map.put("unit", department);

						String jobTitle = userPidData.get(PIDConstants.JOB_TITLE) != null
								? userPidData.get(PIDConstants.JOB_TITLE).toString()
								: (userPidData.get(PIDConstants.JOB_ROLE) != null
										? userPidData.get(PIDConstants.JOB_ROLE).toString()
										: "");

						map.put("designation", jobTitle);

						String lastName = userPidData.get(PIDConstants.MIDDLE_NAME) != null
								? userPidData.get(PIDConstants.MIDDLE_NAME).toString()
								: "";
						lastName += userPidData.get(PIDConstants.LAST_NAME) != null
								? userPidData.get(PIDConstants.LAST_NAME).toString()
								: "";
						map.put("last_name", lastName);

						String firstName = userPidData.get(PIDConstants.FIRST_NAME) != null
								? userPidData.get(PIDConstants.FIRST_NAME).toString()
								: "";

						map.put("first_name", firstName);

					} else {
						String temp = "";
						map.put("unit", temp);
						map.put("designation", temp);
						map.put("last_name", temp);
						map.put("first_name", temp);

					}

					// These keys are updated in map for leaderboard api call but not for past
					// top-performer api
					if (isLeaderboard) {
						map.put("percentile",
								maxPoints == 0 ? 0 : (Float.parseFloat(map.get("points").toString())) / maxPoints);
						map.remove("leaderboard_year");
						map.remove("leaderboard_type");
						map.remove("duration_type");
						map.remove("duration_value");
					}

					map.remove("start_date");
					map.remove("end_date");

				}

			}
		}
	}

	private void validateParameters(String duration_type, String leaderboard_type, String year, String duration_value) {

		if (!Arrays.asList("L", "C").contains(leaderboard_type)) {
			throw new InvalidDataInputException("Bad Request");
		}

		if (!Arrays.asList("M", "W").contains(duration_type)) {
			throw new InvalidDataInputException("Bad Request");
		}

		int yearAsInt, duration;
		try {
			yearAsInt = Integer.parseInt(year);
			duration = Integer.parseInt(duration_value);
		} catch (NumberFormatException e) {
			throw new InvalidDataInputException("Bad Request");
		}

		if (yearAsInt < 2017 || yearAsInt > Year.now().getValue()) {
			throw new NoContentException("Bad Request");
		}
		if (duration_type.equals("M") && (duration < 0 || duration > 12)) {
			throw new InvalidDataInputException("Bad Request");
		}

		if (duration_type.equals("W") && (duration < 0 || duration > 53)) {
			throw new InvalidDataInputException("Bad Request");
		}
	}

	private void validateParameters(String duration_type, String leaderboard_type) {

		if (!Arrays.asList("L", "C").contains(leaderboard_type)) {
			throw new InvalidDataInputException("Bad Request");
		}

		if (!Arrays.asList("M", "W").contains(duration_type)) {
			throw new InvalidDataInputException("Bad Request");
		}
	}
}
