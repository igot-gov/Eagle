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
package com.infosys.lex.goal.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.cohort.service.ParentService;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.LoggerService;
import com.infosys.lex.common.service.UserService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.sunbird.repo.UserMVRepository;
import com.infosys.lex.common.util.ContentMetaConstants;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.exception.ResourceNotFoundException;
import com.infosys.lex.goal.bodhi.repo.CommonGoal;
import com.infosys.lex.goal.bodhi.repo.CommonGoalKey;
import com.infosys.lex.goal.bodhi.repo.CommonGoalLang;
import com.infosys.lex.goal.bodhi.repo.CommonGoalLangRepository;
import com.infosys.lex.goal.bodhi.repo.CommonGoalProjection;
import com.infosys.lex.goal.bodhi.repo.CommonGoalRepository;
import com.infosys.lex.goal.bodhi.repo.GroupProjection;
import com.infosys.lex.goal.bodhi.repo.SharedGoal;
import com.infosys.lex.goal.bodhi.repo.SharedGoalKey;
import com.infosys.lex.goal.bodhi.repo.SharedGoalRepository;
import com.infosys.lex.goal.bodhi.repo.SharedGoalTracker;
import com.infosys.lex.goal.bodhi.repo.SharedGoalTrackerRepository;
import com.infosys.lex.goal.bodhi.repo.UserGoal;
import com.infosys.lex.goal.bodhi.repo.UserGoalKey;
import com.infosys.lex.goal.bodhi.repo.UserGoalRepository;
import com.infosys.lex.goal.bodhi.repo.UserGoalTrackerRepository;
import com.infosys.lex.goal.dto.ActionDTO;
import com.infosys.lex.goal.dto.GoalDTO;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;

@Service
public class GoalsServiceImpl implements GoalsService {

	UserUtilityService userService;

	UserService userSvc;

	ContentService contentService;

	UserGoalRepository learningGoalsRepo;

	SharedGoalRepository sharedGoalsRepo;

	SharedGoalTrackerRepository sharedGoalTrackerRepo;

	UserGoalTrackerRepository learningGoalTrackerRepo;

	ContentProgressRepository contentProgressRepo;

	UserMVRepository userMVRepo;

	LoggerService logService;

	ParentService parentObj;

	GoalsHelper helper;

	LexServerProperties props;

	RestTemplate restTemplate;

	CommonGoalRepository commonGoalsRepo;

	CommonGoalLangRepository commonGoalLangRepo;

	@Autowired
	public GoalsServiceImpl(UserUtilityService userService, ContentService contentService, GoalsHelper helper,
			UserGoalRepository learningGoalsRepo, SharedGoalRepository sharedGoalsRepo,
			SharedGoalTrackerRepository sharedGoalTrackerRepo, ContentProgressRepository contentProgressRepo,
			UserMVRepository userMVRepo, LoggerService logService, ParentService parentObj, LexServerProperties props,
			RestTemplate restTemplate, CommonGoalRepository commonGoalsRepo, UserService userSvc,
			CommonGoalLangRepository commonGoalLangRepo) {
		super();
		this.userService = userService;
		this.contentService = contentService;
		this.learningGoalsRepo = learningGoalsRepo;
		this.sharedGoalsRepo = sharedGoalsRepo;
		this.helper = helper;
		this.sharedGoalTrackerRepo = sharedGoalTrackerRepo;
		this.contentProgressRepo = contentProgressRepo;
		this.userMVRepo = userMVRepo;
		this.logService = logService;
		this.parentObj = parentObj;
		this.props = props;
		this.restTemplate = restTemplate;
		this.commonGoalsRepo = commonGoalsRepo;
		this.userSvc = userSvc;
		this.commonGoalLangRepo = commonGoalLangRepo;
	}

	public GoalsServiceImpl() {
	}

	/**
	 * This method is used to check for existence of a particular shared goal shared
	 * by the user in user_shared_goal table and then return it.
	 * 
	 * @param goalType
	 * @param userUUID
	 * @param goalId
	 * @return
	 * @throws Exception
	 */
	private SharedGoal validateAndGetSharedByGoal(String rootOrg, String userUUID, String goalType, String goalId){

		SharedGoalTracker result = sharedGoalTrackerRepo.getSharedGoalDataAndCount(rootOrg, userUUID, goalType,
				UUID.fromString(goalId));
		SharedGoal userSharedGoal = null;
		if (result != null) {
			userSharedGoal = new SharedGoal(null, result.getGoalContentId(), result.getGoalDescription(),
					result.getGoalDuration(), null, null, result.getGoalTitle(), null, null, 0, null,
					result.getVersion());
		}
		return userSharedGoal;
	}

	/**
	 * This method is used to check for existence of a particular common goal shared
	 * with the user in user_shared_goal table.
	 * 
	 * @param goalType
	 * @param userUUID
	 * @param goalId
	 * @return
	 * @throws Exception
	 */
	private boolean checkIfSharedWithCommonGoalExists(String rootOrg, String goalType, String userUUID, String goalId) {

		boolean isCommonSharedGoalExists = false;

		List<SharedGoal> sharedGoals = sharedGoalsRepo.getGoalBySharedWithGoalTypeAndStatus(rootOrg, userUUID, goalType,
				1);

		if (sharedGoals != null && !sharedGoals.isEmpty()) {
			for (SharedGoal sharedGoal : sharedGoals) {
				if (goalId.equalsIgnoreCase(sharedGoal.getUserSharedGoalsPrimaryKey().getGoalId().toString())) {
					isCommonSharedGoalExists = true;
					break;
				}
			}
		}
		return isCommonSharedGoalExists;
	}

	/**
	 * This method is used to remove a particular goal from user_learning_goals
	 * table.
	 */
	@Override
	public void removeUserLearningGoal(String rootOrg, String goalType, String userUUID, String goalId) throws Exception{

		helper.validateULGoalType(goalType);
		helper.validateUUID(goalId);
		this.validateUser(rootOrg, userUUID);
		UserGoalKey primaryKey = new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId));
		if (learningGoalsRepo.existsById(primaryKey)) {
			learningGoalsRepo.deleteById(primaryKey);
		} else {
			throw new InvalidDataInputException("invalid.goal");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#insertLearningGoals(com.infosys.lex
	 * .goal.dto.GoalDTO, java.lang.String)
	 */

	@Override
	public void createLearningGoal(String rootOrg, GoalDTO goalsData, String userUUID) throws Exception {

		helper.validateUUID(userUUID);
		this.validateUser(rootOrg, userUUID);

		List<String> goalContents = new ArrayList<>();
		if (goalsData.getContentList() != null && !goalsData.getContentList().isEmpty()) {
			goalContents.addAll(goalsData.getContentList());
		}
		if (goalContents != null && !goalContents.isEmpty()) {
			this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), goalContents, new HashMap<>(), false, rootOrg,
					null, null);
		}
		learningGoalsRepo.save(this.getLearningGoalObject(goalsData, userUUID, rootOrg));
	}

	/**
	 * @param rootOrg
	 * @param org
	 * @param goalsData
	 * @param userUUID
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws Exception
	 */
	private UserGoal getLearningGoalObject(GoalDTO goalsData, String userUUID, String rootOrg) throws IOException, ParseException{
		// validate the common goal and fetch its details
		String goalType = goalsData.getGoalType();
		Map<String, Object> validatedData = validateCommonGoalForInsertion(rootOrg, goalsData, userUUID, goalType);

		if (goalsData.getContentList() == null || goalsData.getContentList().isEmpty()) {
			CommonGoal commonGoal = (CommonGoal) validatedData.get("commonGoal");

			this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID),
					Arrays.asList(commonGoal.getGoalContentId().split(",")), new HashMap<>(), false, rootOrg, null,
					null);
		}
		// setup data and create goal.
		UserGoal learningGoal = setUpGoalDataForInsertion(goalsData, userUUID, rootOrg, goalType, validatedData);
		return learningGoal;
	}

	/**
	 * This method sets up data and creates a learning goal object to be inserted.
	 * 
	 * @param goalsData
	 * @param userUUID
	 * @param goalType
	 * @param commonGoal
	 * @return
	 */
	private UserGoal setUpGoalDataForInsertion(GoalDTO goalsData, String userUUID, String rootOrg, String goalType,
			Map<String, Object> validatedData) {

		// set up primary key
		UserGoal learningGoal = null;
		UserGoalKey learningGoalsPkey = new UserGoalKey();
		learningGoalsPkey.setUUID(userUUID);
		learningGoalsPkey.setRootOrg(rootOrg);
		learningGoalsPkey.setGoalType(goalsData.getGoalType());

		// set up timestamp for audit fields
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());

		// In case of common goal pick missing attributes from common goal
		if (goalType.equalsIgnoreCase("common") || goalType.equalsIgnoreCase("commonshared")) {
			learningGoalsPkey.setGoalId(UUID.fromString(goalsData.getGoalId()));
			// if the common goal is being created from goals page, pick data from request
			// body
			if (goalsData.getContentList() != null) {
				if (goalType.equalsIgnoreCase("commonshared")) {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, goalsData.getContentList(), null,
							goalsData.getDuration(), null, null, null, 1f, timeStamp);
				} else {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, goalsData.getContentList(), null,
							goalsData.getDuration(), timeStamp,
							(helper.calculateGoalEndDate(date, goalsData.getDuration())), null, 1f, timeStamp);
				}
			}
			// if the common goal is being created from the Navigator page, fetch that
			// common goal and pick data from it
			else {
				CommonGoal commonGoal = (CommonGoal) validatedData.get("commonGoal");
				List<String> contentList = Arrays.asList(commonGoal.getGoalContentId().split(","));

				if (goalType.equalsIgnoreCase("commonshared")) {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, contentList, null,
							goalsData.getDuration(), null, null, null, 1f, timeStamp);
				} else {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, contentList, null,
							goalsData.getDuration(), timeStamp,
							(helper.calculateGoalEndDate(date, goalsData.getDuration())), null, 1f, timeStamp);
				}
			}
			// if the goal to be created is of type user or tobeshared
		} else {
			// if a new goal is created generate new goalId
			if (goalsData.getGoalId() == null || goalsData.getGoalId().isEmpty()) {
				learningGoalsPkey.setGoalId(UUIDs.timeBased());

				if (goalType.equalsIgnoreCase("tobeshared")) {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, goalsData.getContentList(),
							goalsData.getDesc(), goalsData.getDuration(), null, null, goalsData.getTitle(), null,
							timeStamp);
				} else {
					learningGoal = new UserGoal(learningGoalsPkey, timeStamp, goalsData.getContentList(),
							goalsData.getDesc(), goalsData.getDuration(), timeStamp,
							(helper.calculateGoalEndDate(date, goalsData.getDuration())), goalsData.getTitle(), null,
							timeStamp);
				}
			}
			// it is a case of updation and fetch goalId
			else {
				learningGoalsPkey.setGoalId(UUID.fromString(goalsData.getGoalId()));

				UserGoal ulg = (UserGoal) validatedData.get("userGoal");
				if (goalType.equalsIgnoreCase("tobeshared")) {
					learningGoal = new UserGoal(learningGoalsPkey, ulg.getCreatedOn(), goalsData.getContentList(),
							goalsData.getDesc(), goalsData.getDuration(), null, null, goalsData.getTitle(), null,
							timeStamp);
				} else {
					learningGoal = new UserGoal(learningGoalsPkey, ulg.getCreatedOn(), goalsData.getContentList(),
							goalsData.getDesc(), goalsData.getDuration(), ulg.getGoalStartDate(),
							(helper.calculateGoalEndDate(ulg.getGoalStartDate(), goalsData.getDuration())),
							goalsData.getTitle(), null, timeStamp);
				}
			}
		}
		return learningGoal;
	}

	/**
	 * This method applies certain validations to the data received in case the goal
	 * to be inserted is of common type.
	 * 
	 * @param goalsData
	 * @param userUUID
	 * @param goalType
	 * @return validated common goal
	 */
	private Map<String, Object> validateCommonGoalForInsertion(String rootOrg, GoalDTO goalsData, String userUUID,
			String goalType) {

		// goal type must not be empty and must be among allowed values
		helper.validateULGoalType(goalType);
		// validate goal duration
		int duration = goalsData.getDuration();
		if (duration > 366 || duration <= 0) {
			throw new InvalidDataInputException("invalid.duration");
		}

		UserGoal ulg = null;
		if (goalsData.getGoalId() != null && !goalsData.getGoalId().isEmpty())
			ulg = learningGoalsRepo.findById(
					new UserGoalKey(rootOrg, userUUID, goalsData.getGoalType(), UUID.fromString(goalsData.getGoalId())))
					.orElse(null);

		// For common goal, validate and return the common goal
		CommonGoal commonGoal = null;
		if (Arrays.asList("common", "commonshared").contains(goalType)) {
			// for common goal goalId must be present
			if (goalsData.getGoalId() == null || goalsData.getGoalId().isEmpty()) {
				throw new InvalidDataInputException("goalid.nullOrEmpty");
			}
			// If goal type is common then it must exist already
			commonGoal = commonGoalsRepo.findById(new CommonGoalKey(rootOrg, goalsData.getGoalId(), 1f))
					.orElseThrow(() -> new InvalidDataInputException("invalid.commongoal"));

			// User must not already have the same common goal - created by himself or
			// shared by others

			boolean isCommonGoalAlreadyExists = false;
			if (!goalType.equalsIgnoreCase("commonshared")) {
				isCommonGoalAlreadyExists = this.checkIfSharedWithCommonGoalExists(rootOrg, "common_shared", userUUID,
						goalsData.getGoalId());
			}
			if (ulg != null || isCommonGoalAlreadyExists) {
				throw new InvalidDataInputException("goal.alreadyexists");
			}
		} else {
			if (goalsData.getTitle() == null || goalsData.getTitle().isEmpty()) {
				throw new InvalidDataInputException("goaltitle.required");
			}
			if (goalsData.getContentList() == null || goalsData.getContentList().isEmpty()) {
				throw new InvalidDataInputException("goalContent.nullorEmpty");
			}
		}
		// return common goal in case, goal type is common, else return null
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("commonGoal", commonGoal);
		returnMap.put("userGoal", ulg);
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#shareGoal(java.util.List,java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> shareGoal_v1(List<String> recipients, String userUUID, String goalId, String goalType,
			String rootOrg, String message) throws Exception {

		try {
			UUID.fromString(userUUID);
		} catch (Exception e) {
			throw new InvalidDataInputException("invalid.uuid", e);
		}

		this.validateUser(rootOrg, userUUID);
		// validates the shared goal type
		helper.validateSharedGoalType(goalType);
		// checks whether when a learning goal is being shared for first time(in ULG) or
		// ith time(in USG), exists or not.
		UserGoal ulg = null;
		SharedGoal usgDetails = null;
		if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
			ulg = learningGoalsRepo.findById(new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId)))
					.orElseThrow(() -> new InvalidDataInputException("invalid.usergoal"));
		} else {
			usgDetails = this.validateAndGetSharedByGoal(rootOrg, userUUID, goalType, goalId);
			if (usgDetails == null) {
				throw new InvalidDataInputException("invalid.sharedgoal");
			}
		}
		// If any goal content has deleted or its access has been revoked from the user,
		// fail goal sharing
		List<String> goalContentIds = null;
		// ---------------------*********************-------------------------------------
		if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
			goalContentIds = ulg.getGoalContentId();
		} else {
			goalContentIds = usgDetails.getGoalContentId();
		}
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), goalContentIds, new HashMap<>(), false, rootOrg,
				null, null);
		// ---------------------*********************-------------------------------------

		// processes the list of recipients and filter it on the basis of a number of
		// conditions like user validity checks and recipient list size must be less
		// than or equal to 50
		Map<String, Object> processedData = new HashMap<>();
		processedData = this.processUsersToBeSharedWith(recipients, userUUID, helper.getNewSharedGoalType(goalType),
				goalId, goalContentIds, rootOrg);
		List<String> invalidUsers = (List<String>) processedData.get("invalid_users");
		List<String> finalRecipients = (List<String>) processedData.get("final_user_list");
		if (finalRecipients != null && !finalRecipients.isEmpty()) {
			// setup goal data and insert based on whether the goal is being shared for the
			// first time or it being shared again
			if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
				this.setUpDataForFirstTimeSharingAndInsert(rootOrg, userUUID, goalId, goalType, ulg, finalRecipients,
						message);
			} else {
				this.setUpGoalDataAndInsertIfSharedAgain(rootOrg, userUUID, goalId, goalType, usgDetails,
						finalRecipients, message);
			}
		}

		List<String> unAuthorizedUserEmails = new ArrayList<>();
		List<String> alreadySharedUserEmails = new ArrayList<>();
		List<String> unAuthorizedUsers = (List<String>) processedData.get("unauthorized_users");
		List<String> alreadySharedUUIDs = (List<String>) processedData.get("already_shared");
		List<String> totalUsers = new ArrayList<>();
		if (unAuthorizedUsers.size() > 0) {
			totalUsers.addAll(unAuthorizedUsers);
		}
		if (alreadySharedUUIDs.size() > 0) {
			totalUsers.addAll(alreadySharedUUIDs);
		}
		if (totalUsers.size() > 0) {
			Map<String, Object> userList = getEmailsFromUUIDs(rootOrg, totalUsers);

			for (String user : unAuthorizedUsers) {
				if (userList.containsKey(user)) {
					unAuthorizedUserEmails.add(userList.get(user).toString());
				}
			}
			for (String user : alreadySharedUUIDs) {
				if (userList.containsKey(user)) {
					alreadySharedUserEmails.add(userList.get(user).toString());
				}
			}
		}
		processedData.put("unauthorized_users", unAuthorizedUserEmails);
		processedData.put("already_shared", alreadySharedUserEmails);
		processedData.put("invalid_users", invalidUsers);

		// prepare response data
		Map<String, Object> finalData = new HashMap<>();
		finalData.put("result", "success");
		processedData.remove("final_user_list");
		finalData.putAll(processedData);
		return finalData;
	}

	private void putNotificationEventInKafka(String rootOrg, String sharedBy, List<String> sharedWith, String goalTitle,
			String message) {

		if (message == null || message.isEmpty())
			message = "";

		Map<String, Object> requestBody = new HashMap<>();

		requestBody.put("event-id", "share_goal");

		Map<String, Object> tagValuePair = new HashMap<>();
		tagValuePair.put("#contentTitle", goalTitle);
//		tagValuePair.put("#targetUrl", "https://CLIENT-staging.onwingspan.com/app/goals/me/all");
		tagValuePair.put("#message", message);
		requestBody.put("tag-value-pair", tagValuePair);

		Map<String, List<String>> recipients = new HashMap<>();
		recipients.put("sharedWith", sharedWith);
		recipients.put("sharedBy", Arrays.asList(sharedBy));
		requestBody.put("recipients", recipients);

		String url = "http://" + props.getNotifIp() + ":" + props.getNotifPort() + "/v1/notification/event";
		HttpHeaders headers = new HttpHeaders();
		headers.set("rootOrg", rootOrg);
		try {
			restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(requestBody, headers), Void.class);
		} catch (Exception e) {
			// do nothing
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> shareGoal(List<String> recipients, String userUUID, String goalId, String goalType,
			String rootOrg, String message) throws Exception {

		try {
			UUID.fromString(userUUID);
		} catch (Exception e) {
			throw new InvalidDataInputException("invalid.uuid", e);
		}

		this.validateUser(rootOrg, userUUID);
		// validates the shared goal type
		helper.validateSharedGoalType(goalType);
		// checks whether when a learning goal is being shared for first time(in ULG) or
		// ith time(in USG), exists or not.
		UserGoal ulg = null;
		SharedGoal usgDetails = null;
		if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
			ulg = learningGoalsRepo.findById(new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId)))
					.orElseThrow(() -> new InvalidDataInputException("invalid.usergoal"));
		} else {
			usgDetails = this.validateAndGetSharedByGoal(rootOrg, userUUID, goalType, goalId);
			if (usgDetails == null) {
				throw new InvalidDataInputException("invalid.sharedgoal");
			}
		}
		// If any goal content has deleted or its access has been revoked from the user,
		// fail goal sharing
		List<String> goalContentIds = null;
		// ---------------------*********************-------------------------------------
		if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
			goalContentIds = ulg.getGoalContentId();
		} else {
			goalContentIds = usgDetails.getGoalContentId();
		}
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), goalContentIds, new HashMap<>(), false, rootOrg,
				null, null);
		// ---------------------*********************-------------------------------------

		// processes the list of recipients and filter it on the basis of a number of
		// conditions like user validity checks and recipient list size must be less
		// than or equal to 50
		Map<String, Object> processedData = new HashMap<>();
		processedData = this.processUsersToBeSharedWith(recipients, userUUID, helper.getNewSharedGoalType(goalType),
				goalId, goalContentIds, rootOrg);
		List<String> invalidUsers = (List<String>) processedData.get("invalid_users");
		List<String> finalRecipients = (List<String>) processedData.get("final_user_list");
		if (finalRecipients != null && !finalRecipients.isEmpty()) {
			// setup goal data and insert based on whether the goal is being shared for the
			// first time or it being shared again
			if (goalType.equalsIgnoreCase("commonshared") || goalType.equalsIgnoreCase("tobeshared")) {
				this.setUpDataForFirstTimeSharingAndInsert(rootOrg, userUUID, goalId, goalType, ulg, finalRecipients,
						message);
			} else {
				this.setUpGoalDataAndInsertIfSharedAgain(rootOrg, userUUID, goalId, goalType, usgDetails,
						finalRecipients, message);
			}
		}

		List<String> unAuthorizedUsers = (List<String>) processedData.get("unauthorized_users");
		List<String> alreadySharedUUIDs = (List<String>) processedData.get("already_shared");
		List<Map<String, Object>> unAuthorizedUserData = new ArrayList<>();
		List<Map<String, Object>> alreadySharedUserData = new ArrayList<>();
		List<String> totalUsers = new ArrayList<>();
		if (unAuthorizedUsers.size() > 0) {
			totalUsers.addAll(unAuthorizedUsers);
		}
		if (alreadySharedUUIDs.size() > 0) {
			totalUsers.addAll(alreadySharedUUIDs);
		}
		if (totalUsers.size() > 0) {
			Map<String, Object> userList = new HashMap<>();
			Map<String, Object> details = this.getMultipleUserData(rootOrg, totalUsers);
			for (String user : details.keySet()) {
				userList.put(user, details.get(user));
			}
			for (String user : unAuthorizedUsers) {
				if (userList.containsKey(user)) {
					unAuthorizedUserData.add((Map<String, Object>) userList.get(user));
				}
			}
			for (String user : alreadySharedUUIDs) {
				if (userList.containsKey(user)) {
					alreadySharedUserData.add((Map<String, Object>) userList.get(user));
				}
			}
		}
		processedData.put("unauthorized_users", unAuthorizedUserData);
		processedData.put("already_shared", alreadySharedUserData);
		processedData.put("invalid_users", invalidUsers);

		// prepare response data
		Map<String, Object> finalData = new HashMap<>();
		finalData.put("result", "success");
		processedData.remove("final_user_list");
		finalData.putAll(processedData);
		return finalData;
	}

	/**
	 * This method prepares data and inserts the data if the goal is being shared
	 * again.
	 * 
	 * @param userUUID
	 * @param goalId
	 * @param goalType
	 * @param usgDetails
	 * @param finalRecipients
	 * @throws IOException
	 * @throws JsonMappingException
	 */
	private void setUpGoalDataAndInsertIfSharedAgain(String rootOrg, String userUUID, String goalId, String goalType,
			SharedGoal usgDetails, List<String> finalRecipients, String message)
			throws JsonMappingException, IOException {
		List<SharedGoal> recordsToBeInserted = new ArrayList<SharedGoal>();
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());

		String goalDesc = "";
		String goalTitle = "";
		for (String eachRecipient : finalRecipients) {
			SharedGoal usg = null;
			SharedGoalKey sharedPKey = new SharedGoalKey(rootOrg, eachRecipient, goalType, UUID.fromString(goalId),
					userUUID);

			if (!goalType.equalsIgnoreCase("common_shared")) {
				goalDesc = usgDetails.getGoalDescription();
				goalTitle = usgDetails.getGoalTitle();
			}
			usg = new SharedGoal(sharedPKey, usgDetails.getGoalContentId(), goalDesc, usgDetails.getGoalDuration(),
					null, null, goalTitle, timeStamp, timeStamp, 0, "", usgDetails.getVersion());

			recordsToBeInserted.add(usg);
		}
		sharedGoalsRepo.bulkInsert(recordsToBeInserted);

		// send notification event to kafka
		String contentTitle = "";
		if (goalType.equalsIgnoreCase("common_shared")) {
			CommonGoalLang goal = commonGoalLangRepo.fetchCommonGoalDetails(rootOrg, goalId, Arrays.asList("en"),
					usgDetails.getVersion());
			contentTitle = goal.getGoalTitle();
		} else {
			contentTitle = goalTitle;
		}
		this.putNotificationEventInKafka(rootOrg, userUUID, finalRecipients, contentTitle, message);
	}

	/**
	 * This method prepares data and inserts the data if the goal is being shared
	 * for the first time.
	 * 
	 * @param userUUID
	 * @param goalId
	 * @param goalType
	 * @param ulg
	 * @param finalRecipients
	 * @throws IOException
	 * @throws JsonMappingException
	 */
	private void setUpDataForFirstTimeSharingAndInsert(String rootOrg, String userUUID, String goalId, String goalType,
			UserGoal ulg, List<String> finalRecipients, String message) throws JsonMappingException, IOException {
		List<SharedGoal> recordsToBeInserted = new ArrayList<SharedGoal>();
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());

		SharedGoalKey sharedPKeyForReference = null;
		String goalDesc = "";
		String goalTitle = "";
		for (String eachRecipient : finalRecipients) {
			SharedGoal usg = null;
			SharedGoalKey sharedPKey = new SharedGoalKey(rootOrg, eachRecipient, helper.getNewSharedGoalType(goalType),
					UUID.fromString(goalId), userUUID);

			if (!goalType.equalsIgnoreCase("commonshared")) {
				goalDesc = ulg.getGoalDescription();
				goalTitle = ulg.getGoalTitle();
			}
			usg = new SharedGoal(sharedPKey, ulg.getGoalContentId(), goalDesc, ulg.getGoalDuration(), null, null,
					goalTitle, timeStamp, timeStamp, 0, null, ulg.getVersion());
			recordsToBeInserted.add(usg);

			sharedPKeyForReference = sharedPKey;
		}
		sharedGoalsRepo.bulkInsert(recordsToBeInserted);
		if (sharedGoalsRepo.existsById(sharedPKeyForReference)) {
			learningGoalsRepo.deleteById(ulg.getUserLearningGoalsPrimaryKey());

			// send notification event to kafka
			String contentTitle = "";
			if (goalType.equalsIgnoreCase("commonshared")) {

				CommonGoalLang goal = commonGoalLangRepo.fetchCommonGoalDetails(rootOrg, goalId, Arrays.asList("en"),
						ulg.getVersion());
				contentTitle = goal.getGoalTitle();
			} else {
				contentTitle = goalTitle;
			}
			this.putNotificationEventInKafka(rootOrg, userUUID, finalRecipients, contentTitle, message);
		}

	}

	/**
	 * This method gives the final list of the users, this goal can be shared with.
	 * First the user list is validated and after that, a list of valid users among
	 * the users in the list is prepared. The list is again filtered based on
	 * whether there some users in the list with whom the goal has already been
	 * shared. Also, the list is filtered if the user is trying to share with self.
	 * The resultant list can only contain not more than 50 users at the maximum.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> processUsersToBeSharedWith(List<String> recipients, String userUUID, String newGoalType,
			String goalId, List<String> goalContentIds, String rootOrg){

		Map<String, Object> processedData = new HashMap<>();

		// checks whether the recipient list also contains the user. If so, remove the
		// user and set corresponding flag.
		processedData.put("self_shared", false);
		if (recipients.contains(userUUID)) {
			processedData.put("self_shared", true);
			recipients.remove(userUUID);
		}

		// if by accident email is coming in one of the recipients, mark it as invalid
		List<String> invalidUsers = new ArrayList<>();

		for (String userId : recipients) {
			try {
				UUID.fromString(userId);
			} catch (Exception e) {
				invalidUsers.add(userId);
			}
		}

		recipients.removeAll(invalidUsers);

		// separates the valid users and invalid user from the list of recipients
		List<String> alreadySharedWithUsers = new ArrayList<>();
		List<String> unAuthorizedUsers = new ArrayList<>();
		Map<String, Object> userValidityResults = this.verifyUsers(rootOrg, recipients);
		List<String> initialValidUsersList = (List<String>) userValidityResults.get("valid_users");
		List<String> invalidFromValidityResults = (List<String>) userValidityResults.get("invalid_users");
		invalidUsers.addAll(invalidFromValidityResults);
		processedData.put("invalid_users", invalidUsers);
		if (initialValidUsersList.isEmpty()) {
			processedData.put("already_shared", alreadySharedWithUsers);
			processedData.put("final_user_list", initialValidUsersList);
			processedData.put("unauthorized_users", unAuthorizedUsers);
			return processedData;
		}
		// filter the recipient list to find out the users with whom this goal has
		// already been shared with while also checking if the final recipient list size
		// is becoming greater than 50.
		List<SharedGoalTracker> goalRecordsSharedByUser = new ArrayList<>();
		int finalSharedUserCount = 0;
		this.fetchAlreadySharedWithUsers(userUUID, newGoalType, goalId, initialValidUsersList, alreadySharedWithUsers,
				goalRecordsSharedByUser, rootOrg);

		// checking whether all the recipients have access to all the contents of the
		// goal
		Map<String, Object> statusData = new HashMap<>();
		this.checkForAccessStatus(initialValidUsersList, goalContentIds, statusData, rootOrg);

		for (String user : initialValidUsersList) {
			if (statusData.containsKey(user)) {
				if (!(boolean) statusData.get(user)) {
					unAuthorizedUsers.add(user);
				}
			}
		}
		// remove unauthorized users from valid users list
		initialValidUsersList.removeAll(unAuthorizedUsers);

		// add new users who have not logged in yet whose access has been bypassed
		// above
		initialValidUsersList.addAll((List<String>) userValidityResults.get("new_users"));

		// final users count will be the summation of the filtered valid users and the
		// users with whom this has already been shared with
		finalSharedUserCount = initialValidUsersList.size() + alreadySharedWithUsers.size();
		if (finalSharedUserCount > 50) {
			throw new InvalidDataInputException("sharelimit.exceeded_50");
		}

		processedData.put("already_shared", alreadySharedWithUsers);
		processedData.put("final_user_list", initialValidUsersList);
		processedData.put("unauthorized_users", unAuthorizedUsers);
		return processedData;
	}

	/**
	 * This method processes the list of recipients to check how many of users among
	 * them, user has already shared that goal.
	 * 
	 * @param userUUID
	 * @param newGoalType
	 * @param goalId
	 * @param initialValidUsersList
	 * @return
	 */
	private void fetchAlreadySharedWithUsers(String userUUID, String newGoalType, String goalId,
			List<String> initialValidUsersList, List<String> alreadySharedWithUsers,
			List<SharedGoalTracker> goalRecordsSharedByUser, String rootOrg) {

		// fetches all the records of this goal shared by the user
		goalRecordsSharedByUser.addAll(sharedGoalTrackerRepo.fetchRecordsForGoalSharedByUser(rootOrg, userUUID,
				newGoalType, UUID.fromString(goalId)));

		// if this goal has been shared even once, then filter out the recipients with
		// whom this goal has already been shared by the user
		if (goalRecordsSharedByUser != null && !goalRecordsSharedByUser.isEmpty()) {
			for (SharedGoalTracker record : goalRecordsSharedByUser) {
				if (record.getStatus() != null && record.getStatus() == 1) {
					String sharedWithUser = record.getSharedGoalTrackerPrimaryKey().getSharedWith();
					if (initialValidUsersList.contains(sharedWithUser)) {
						// Removing username from sharedlist if already shared and accepted by user.
						initialValidUsersList.remove(sharedWithUser);
						alreadySharedWithUsers.add(sharedWithUser);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.goal.service.GoalsService#takeAction(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Map<String, Object> takeAction(String action, ActionDTO actionData, Boolean confirm, String userUUID,
			String goalId, String rootOrg) throws Exception {

		// check if the goal shared is of valid type
		helper.validateUserSharedGoalType(actionData.getGoalType());
		// check if the user with whom the goal is shared with and user who has shared
		// the goal are valid users or not
		this.validateUser(rootOrg, userUUID);
		String sharedBy = actionData.getSharedBy();
		// check if shared goal exists or not
		SharedGoal sharedGoalDetails = sharedGoalsRepo.findById(
				new SharedGoalKey(rootOrg, userUUID, actionData.getGoalType(), UUID.fromString(goalId), sharedBy))
				.orElseThrow(() -> new InvalidDataInputException("invalid.sharedgoal"));
		// checking whether the goal on which the action is being taken does not
		// contain an access revoked content or a deleted content
		if (confirm.booleanValue() && action.equalsIgnoreCase("accept")) {
			this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), sharedGoalDetails.getGoalContentId(),
					new HashMap<>(), false, rootOrg, null, null);
		}

		// message is required in case of goal rejection
		String message = actionData.getMessage();
		if (!confirm.booleanValue() && action.equalsIgnoreCase("reject")) {
			if (message == null || message.isEmpty()) {
				throw new InvalidDataInputException("message.required");
			}
		}
		// In case of common goal, set up flags indicating whether the user has created
		// the same goal or the user has accepted that goal earlier when someone shared
		// it.
		boolean isCommonUserGoalExists = false;
		boolean isCommonSharedGoalExists = false;
		UserGoal ulg = learningGoalsRepo.findById(new UserGoalKey(rootOrg, userUUID, "common", UUID.fromString(goalId)))
				.orElse(null);
		if (ulg != null) {
			isCommonUserGoalExists = true;
		}
		if (!sharedGoalsRepo.getGoalsSharedToUser(rootOrg, userUUID, "common_shared", UUID.fromString(goalId), 1)
				.isEmpty()) {
			isCommonSharedGoalExists = true;
		}
		String goalDesc = "";
		String goalTitle = "";
		if (!sharedGoalDetails.getUserSharedGoalsPrimaryKey().getGoalType().equalsIgnoreCase("common_shared")) {
			goalDesc = sharedGoalDetails.getGoalDescription();
			goalTitle = sharedGoalDetails.getGoalTitle();
		}
		// setup data and perform necessary actions
		Map<String, Object> returnMap = new HashMap<>();
		SharedGoal usg = null;
		SharedGoalKey sharedPKey = new SharedGoalKey(rootOrg, userUUID, actionData.getGoalType(),
				UUID.fromString(goalId), sharedBy);
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		// If user has accepted and clicked on check, prepare goal flags for response
		if (!confirm.booleanValue() && action.equalsIgnoreCase("accept")) {
			returnMap.put("commonUserGoal", isCommonUserGoalExists);
			returnMap.put("commonSharedGoal", isCommonSharedGoalExists);
		}
		// If the user has confirmed to accept the goal, in case of common goal, if the
		// user common goal flag is set
		// then the goal will be deleted from ULG and a new goal record will be inserted
		// in USG.
		else if (confirm.booleanValue()) {
			if (isCommonUserGoalExists) {
				learningGoalsRepo.deleteById(new UserGoalKey(rootOrg, userUUID, "common", UUID.fromString(goalId)));
			}
			usg = new SharedGoal(sharedPKey, sharedGoalDetails.getGoalContentId(), goalDesc,
					sharedGoalDetails.getGoalDuration(), timeStamp,
					helper.calculateGoalEndDate(date, sharedGoalDetails.getGoalDuration()), goalTitle, timeStamp,
					sharedGoalDetails.getSharedOn(), 1, null, sharedGoalDetails.getVersion());
			sharedGoalsRepo.save(usg);
			returnMap.put("Status", "Accept Successful");
		}
		// If the user has chosen to reject the goal then prepare data with the message
		// as well
		else if (!confirm.booleanValue() && action.equalsIgnoreCase("reject")) {

			usg = new SharedGoal(sharedPKey, sharedGoalDetails.getGoalContentId(), goalDesc,
					sharedGoalDetails.getGoalDuration(), null, null, goalTitle, timeStamp,
					sharedGoalDetails.getSharedOn(), -1, message, sharedGoalDetails.getVersion());
			sharedGoalsRepo.save(usg);
			returnMap.put("Status", "Reject Successful");
		}
		return returnMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.goal.service.GoalsService#fetchGoalsForAction(java.lang.
	 * String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchGoalsForAction_v1(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws Exception {

		// check if the user is valid
		this.validateUser(rootOrg, userUUID);
		// fetch all goals for user for which the user has not taken any action
		List<Map<String, Object>> finalGoalsData = new ArrayList<>();
		List<SharedGoal> forActionGoals = sharedGoalsRepo.getGoalsSharedToUserFilterByStatus(rootOrg, userUUID, 0);
		if (forActionGoals == null || forActionGoals.isEmpty()) {
			return finalGoalsData;
		}
		// prepare a common content list from all goals to fetch meta
		Set<String> contentList = new HashSet<>();
		for (SharedGoal goal : forActionGoals) {
			List<String> cList = new ArrayList<>();
			cList = goal.getGoalContentId();
			contentList.addAll(cList);
		}
		// fetch meta for all resources and map each resource to its meta
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
		}

		Map<String, Object> contentData = new HashMap<>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), new ArrayList<>(contentList), contentData, true,
				rootOrg, null, source);
		Map<String, Object> statusData = new HashMap<>();
		if (!contentData.isEmpty()) {
			statusData = (Map<String, Object>) contentData.get(userUUID);
		}
		// for each goal prepare required data. Also for each of its contents, fetch the
		// meta which has been prepared above, but first we need to fetch the common
		// learning goals if there are any common goals in the list so that their title
		// and description can be shown according to given language
		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();
		for (SharedGoal goal : forActionGoals) {
			if (goal.getUserSharedGoalsPrimaryKey().getGoalType().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString());
				versions.add(goal.getVersion());
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> goalResults = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			List<CommonGoalProjection> commonGoals = this.getCommonGoalsByLanguage(goalResults, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}
		for (SharedGoal goal : forActionGoals) {
			Map<String, Object> eachGoalData = new HashMap<>();
			eachGoalData.put("goal_id", goal.getUserSharedGoalsPrimaryKey().getGoalId());
			if (goalMap.containsKey(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString())) {
				eachGoalData.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString()))
								.getGoalTitle());
				eachGoalData.put("goal_description",
						((CommonGoalProjection) goalMap.get(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString()))
								.getGoalDesc());
			} else {
				eachGoalData.put("goal_title", goal.getGoalTitle());
				eachGoalData.put("goal_description", goal.getGoalDescription());
			}

			eachGoalData.put("shared_by",
					this.getEmailFromUUID(rootOrg, goal.getUserSharedGoalsPrimaryKey().getSharedBy()));
			eachGoalData.put("goal_duration", goal.getGoalDuration());
			eachGoalData.put("goal_type", goal.getUserSharedGoalsPrimaryKey().getGoalType());
			eachGoalData.put("last_updated_on", goal.getLastUpdatedOn());
			eachGoalData.put("goal_content_id", goal.getGoalContentId());

			List<String> goalContent = goal.getGoalContentId();
			List<Map<String, Object>> requiredContentData = new ArrayList<>();
			if (!statusData.isEmpty()) {
				for (String eachContent : goalContent) {
					if (((Map<String, Object>) statusData.get(eachContent)).keySet().size() > 1) {

						Map<String, Object> meta = (Map<String, Object>) statusData.get(eachContent);

						Map<String, Object> metaForContentWithRating = new HashMap<>();
						metaForContentWithRating.putAll(meta);

						if (metaForContentWithRating.containsKey("averageRating")) {
							Float avgRating = 0f;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("averageRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									avgRating = Float.parseFloat(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("averageRating", avgRating);
						}
						if (metaForContentWithRating.containsKey("totalRating")) {
							Integer totalRating = 0;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("totalRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									totalRating = Integer.parseInt(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("totalRating", totalRating);
						}
						requiredContentData.add(metaForContentWithRating);
					}
				}
			}
			eachGoalData.put("content_data", requiredContentData);
			finalGoalsData.add(eachGoalData);
		}
		// finally sort the goal in descending order according to their last updated
		// date
		Collections.sort(finalGoalsData,
				(goalA, goalB) -> ((Date) goalB.get("last_updated_on")).compareTo((Date) goalA.get("last_updated_on")));
		return finalGoalsData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.goal.service.GoalsService#fetchGoalsForAction(java.lang.
	 * String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchGoalsForAction(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws Exception {

		// check if the user is valid
		this.validateUser(rootOrg, userUUID);

		// fetch all goals for user for which the user has not taken any action
		List<Map<String, Object>> finalGoalsData = new ArrayList<>();
		List<SharedGoal> forActionGoals = sharedGoalsRepo.getGoalsSharedToUserFilterByStatus(rootOrg, userUUID, 0);
		if (forActionGoals == null || forActionGoals.isEmpty()) {
			return finalGoalsData;
		}

		// prepare a common content list from all goals to fetch meta
		Set<String> contentList = new HashSet<>();
		for (SharedGoal goal : forActionGoals) {
			List<String> cList = new ArrayList<>();
			cList = goal.getGoalContentId();
			contentList.addAll(cList);
		}

		// fetch meta for all resources and map each resource to its meta
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating", "expiryDate" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
		}

		Map<String, Object> contentData = new HashMap<>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), new ArrayList<>(contentList), contentData, true,
				rootOrg, null, source);
		Map<String, Object> statusData = new HashMap<>();
		if (!contentData.isEmpty()) {
			statusData = (Map<String, Object>) contentData.get(userUUID);
		}

		// for each goal prepare required data. Also for each of its contents, fetch the
		// meta which has been prepared above, but first we need to fetch the common
		// learning goals if there are any common goals in the list so that their title
		// and description can be shown according to given language
		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();

		List<String> listOfUsersForWhomDataIsToBeFetched = new ArrayList<>();
		for (SharedGoal goal : forActionGoals) {
			if (goal.getUserSharedGoalsPrimaryKey().getGoalType().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString());
				versions.add(goal.getVersion());
			}
			if (goal.getUserSharedGoalsPrimaryKey().getSharedBy() != null) {
				listOfUsersForWhomDataIsToBeFetched.add(goal.getUserSharedGoalsPrimaryKey().getSharedBy());
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> goalResults = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			List<CommonGoalProjection> commonGoals = this.getCommonGoalsByLanguage(goalResults, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}

		Map<String, Object> userData = this.getMultipleUserData(rootOrg, listOfUsersForWhomDataIsToBeFetched);

		for (SharedGoal goal : forActionGoals) {
			Map<String, Object> eachGoalData = new HashMap<>();
			eachGoalData.put("goal_id", goal.getUserSharedGoalsPrimaryKey().getGoalId());
			if (goalMap.containsKey(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString())) {
				eachGoalData.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString()))
								.getGoalTitle());
				eachGoalData.put("goal_description",
						((CommonGoalProjection) goalMap.get(goal.getUserSharedGoalsPrimaryKey().getGoalId().toString()))
								.getGoalDesc());
			} else {
				eachGoalData.put("goal_title", goal.getGoalTitle());
				eachGoalData.put("goal_description", goal.getGoalDescription());
			}

			eachGoalData.put("shared_by", userData.get(goal.getUserSharedGoalsPrimaryKey().getSharedBy()));
			eachGoalData.put("goal_duration", goal.getGoalDuration());
			eachGoalData.put("goal_type", goal.getUserSharedGoalsPrimaryKey().getGoalType());
			eachGoalData.put("last_updated_on", goal.getLastUpdatedOn());
			eachGoalData.put("goal_content_id", goal.getGoalContentId());

			List<String> goalContent = goal.getGoalContentId();
			List<Map<String, Object>> requiredContentData = new ArrayList<>();

			if (!statusData.isEmpty()) {
				for (String eachContent : goalContent) {
					if (((Map<String, Object>) statusData.get(eachContent)).keySet().size() > 1) {
						Map<String, Object> meta = (Map<String, Object>) statusData.get(eachContent);

						Map<String, Object> metaForContentWithRating = new HashMap<>();
						metaForContentWithRating.putAll(meta);

						if (metaForContentWithRating.containsKey("averageRating")) {

							Float avgRating = 0f;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("averageRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									avgRating = Float.parseFloat(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("averageRating", avgRating);
						}
						if (metaForContentWithRating.containsKey("totalRating")) {
							Integer totalRating = 0;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("totalRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									totalRating = Integer.parseInt(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("totalRating", totalRating);
						}
						requiredContentData.add(metaForContentWithRating);
					}
				}
			}
			eachGoalData.put("content_data", requiredContentData);
			finalGoalsData.add(eachGoalData);
		}

		// finally sort the goal in descending order according to their last updated
		// date
		Collections.sort(finalGoalsData,
				(goalA, goalB) -> ((Date) goalB.get("last_updated_on")).compareTo((Date) goalA.get("last_updated_on")));
		return finalGoalsData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#getSuggestedGoals(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getSuggestedGoals(String rootOrg, String userUUID, String language) throws Exception{

		// checks if the user exists or not
		this.validateUser(rootOrg, userUUID);

		// process all the common goals which user has created for self, for others and
		// shared and for others and not yet shared
		Map<String, Object> userCommonGoalsWithCount = this.processULGDataAndUSGData(userUUID, rootOrg);

		// fetch all common goals present in the system and for each common goal check
		// whether it has already been created by the user 2 times that is,one for self
		// and one for others initially all common goals will be ungrouped
		List<Map<String, Object>> unGroupedCommonGoals = new ArrayList<Map<String, Object>>();
		Set<String> languages = new LinkedHashSet<>();
		if (language == null) {
			language = this.fetchPreferredLanguage(userUUID, rootOrg);
			if (language != null && !language.isEmpty()) {
				languages.addAll(Arrays.asList(language.split(",")));
			}
		} else {
			languages.addAll(Arrays.asList(language.split(",")));
		}
		languages.add("en");
		languages.remove("");
		languages.remove(null);

		this.processCommonGoals(rootOrg, userCommonGoalsWithCount, unGroupedCommonGoals, languages, userUUID, null);

		// group the ungrouped goals according to their goal groups
		List<Map<String, Object>> finalCommnGoalData = new ArrayList<>();
		this.groupGoals(unGroupedCommonGoals, finalCommnGoalData);

		// this is done for user access check/content status check. If either condition
		// is encountered, a new field in response "warning_message" will be sent.
		Set<String> contentIds = new HashSet<>();
		for (Map<String, Object> commonGoal : finalCommnGoalData) {
			List<Map<String, Object>> goals = (List<Map<String, Object>>) commonGoal.get("goals");
			for (Map<String, Object> goal : goals) {
				contentIds.addAll((List<String>) goal.get("goalContentId"));
			}
		}
		Map<String, Object> statusData = new HashMap<>();
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), new ArrayList<>(contentIds), statusData, true,
				rootOrg, null, null);
		Map<String, Object> contentMeta = new HashMap<>();
		if (!statusData.isEmpty()) {
			contentMeta = (Map<String, Object>) statusData.get(userUUID);
		}
		if (!contentMeta.isEmpty()) {
			for (Map<String, Object> commonGoal : finalCommnGoalData) {
				List<Map<String, Object>> goals = (List<Map<String, Object>>) commonGoal.get("goals");
				for (Map<String, Object> goal : goals) {
					List<String> ids = (List<String>) goal.get("goalContentId");
					for (String id : ids) {
						if (contentMeta.keySet().contains(id)) {
							if ((((Map<String, Object>) contentMeta.get(id)).containsKey("hasAccess"))
									&& (!(boolean) ((Map<String, Object>) contentMeta.get(id)).get("hasAccess"))) {
								goal.put("warning_message", "content.accessRestricted");
								break;
							} else if ((((Map<String, Object>) contentMeta.get(id)).containsKey("status"))
									&& (((Map<String, Object>) contentMeta.get(id)).get("status")).toString()
											.equalsIgnoreCase("expired")) {
								goal.put("warning_message", "content.expired");
								break;
							} else if ((((Map<String, Object>) contentMeta.get(id)).containsKey("status"))
									&& (((Map<String, Object>) contentMeta.get(id)).get("status")).toString()
											.equalsIgnoreCase("deleted")) {
								goal.put("warning_message", "content.deleted");
								break;
							}
						}
					}
				}
			}
		}

		return finalCommnGoalData;
	}

	/**
	 * This method is used to process all the common goal present in the system
	 * according to the common goals user has already created.
	 * 
	 * @param userCommonGoalsWithCount
	 * @param unGroupedCommonGoals
	 * @throws IOException
	 * @throws JsonMappingException
	 */
	@SuppressWarnings("unchecked")
	private void processCommonGoals(String rootOrg, Map<String, Object> userCommonGoalsWithCount,
			List<Map<String, Object>> unGroupedCommonGoals, Set<String> languages, String userId, String goalGroup)
			throws JsonMappingException, IOException {

		List<CommonGoalProjection> resultGoals = null;
		if (goalGroup == null) {
			resultGoals = commonGoalsRepo.fetchAllCommonGoalsByRootOrgAndLanguage(rootOrg, new ArrayList<>(languages),
					1f);
		} else {
			resultGoals = commonGoalsRepo.fetchAllCommonGoalsByGoalGroup(rootOrg, new ArrayList<>(languages), 1f,
					goalGroup);
		}
		List<CommonGoalProjection> commonLearningGoals = this.getCommonGoalsByLanguage(resultGoals, languages);
		for (CommonGoalProjection clg : commonLearningGoals) {
			Map<String, Object> commonGoalWithStatus = new HashMap<>();
			commonGoalWithStatus.put("id", clg.getId());
			commonGoalWithStatus.put("createdOn", clg.getCreatedOn());
			commonGoalWithStatus.put("goalContentId", Arrays.asList(clg.getGoalContentId().split(",")));
			commonGoalWithStatus.put("goalDescription", clg.getGoalDesc());
			commonGoalWithStatus.put("groupName", clg.getGroupName());
			commonGoalWithStatus.put("groupId", clg.getGroupId());
			commonGoalWithStatus.put("goalTitle", clg.getGoalTitle());
			commonGoalWithStatus.put("updatedOn", clg.getUpdatedOn());
			commonGoalWithStatus.put("version", Float.parseFloat(clg.getVersion()));
			commonGoalWithStatus.put("language", clg.getLanguage());

			commonGoalWithStatus.put("createdForSelf", false);
			commonGoalWithStatus.put("createdForOthers", false);

			Map<String, Object> tempMap = (Map<String, Object>) userCommonGoalsWithCount.getOrDefault(clg.getId(),
					new HashMap<String, Object>());
			if (tempMap != null && !tempMap.isEmpty()) {
				// if count is 2 that means, it has been created 2 times and it will not be
				// shown to the user
				int countCreated = Integer.parseInt(tempMap.getOrDefault("count", -1).toString());
				if (countCreated == 2) {
					if (goalGroup == null) {
						continue;
					} else {
						commonGoalWithStatus.put("createdForSelf", true);
						commonGoalWithStatus.put("createdForOthers", true);
					}
				}
				// if count created is 1, then add flag to it indicating whether it was created
				// for self or others
				else if (countCreated == 1) {
					String flag = ((Map<String, Object>) userCommonGoalsWithCount.getOrDefault(clg.getId(),
							new HashMap<String, Object>())).getOrDefault("flag", "").toString();
					if (flag.equalsIgnoreCase("createdForOthers")) {
						commonGoalWithStatus.put(flag, true);
					} else {
						commonGoalWithStatus.put(flag, true);
					}
				}
			}
//			commonGoalWithStatus.put("goalContentId",
//					Arrays.asList(commonGoalWithStatus.get("goalContentId").toString().split(",")));
			if (goalGroup == null) {
				commonGoalWithStatus.put("warning_message", "");
			}
			unGroupedCommonGoals.add(commonGoalWithStatus);
		}
	}

	/**
	 * This method is used to process the common goals data from ULG as well as
	 * common goals data from USG. It processes all the common goals data while
	 * appending the flags indicating whether the goal was created for self or
	 * others. ULG and USG here are abbreviations for user_learning_goals table and
	 * user_shared_goals table respectively.
	 * 
	 * @param userUUID
	 * @return
	 */
	private Map<String, Object> processULGDataAndUSGData(String userUUID, String rootOrg) {

		Map<String, Object> userCommonGoalsWithCount = new HashMap<>();
		// process common ULG data and fetch its count indicating how many times the
		// common goal has been created
		// by user and flag indicating whether it was created for self or others
		this.processCommonGoalsInULG(userUUID, userCommonGoalsWithCount, rootOrg);
		// Similarly process common USG data and fetch its count indicating how many
		// times the common goal has been created
		// by user and flag indicating it was created for self or others
		this.processCommonGoalsInUSG(userUUID, userCommonGoalsWithCount, rootOrg);
		return userCommonGoalsWithCount;
	}

	/**
	 * This method is used to process common goals for the user present in USG.
	 * 
	 * @param userUUID
	 * @param userCommonGoalsWithCount
	 */
	@SuppressWarnings("unchecked")
	private void processCommonGoalsInUSG(String userUUID, Map<String, Object> userCommonGoalsWithCount,
			String rootOrg) {

		// fetch all common user shared goals
		List<SharedGoalTracker> sharedLearningGoals = sharedGoalTrackerRepo
				.fetchGoalRecordsBySharedByAndGoalType(rootOrg, userUUID, "common_shared");
		// check how many times it was created
		for (SharedGoalTracker sharedGoal : sharedLearningGoals) {
			Map<String, Object> countAndFlag = new HashMap<>();
			String goalId = sharedGoal.getSharedGoalTrackerPrimaryKey().getGoalId().toString();
			if (Integer.parseInt(((Map<String, Object>) userCommonGoalsWithCount.getOrDefault(goalId, new HashMap<>()))
					.getOrDefault("count", -1).toString()) == 1) {
				countAndFlag.put("count", 2);
				countAndFlag.put("flag", "createdForOthers");
			} else if (Integer
					.parseInt(((Map<String, Object>) userCommonGoalsWithCount.getOrDefault(goalId, new HashMap<>()))
							.getOrDefault("count", -1).toString()) == 2) {
				continue;
			} else {
				countAndFlag.put("count", 1);
				countAndFlag.put("flag", "createdForOthers");
			}
			userCommonGoalsWithCount.put(goalId, countAndFlag);
		}
	}

	/**
	 * This method is used to process common goals for the user present in ULG.
	 * 
	 * @param userUUID
	 * @param userCommonGoalsWithCount
	 */
	@SuppressWarnings("unchecked")
	private void processCommonGoalsInULG(String userUUID, Map<String, Object> userCommonGoalsWithCount,
			String rootOrg) {

		// fetch all common user learning goals
		List<UserGoal> userLearningGoals = learningGoalsRepo.fetchByLearningGoalTypesAndUuid(rootOrg, userUUID,
				Arrays.asList("common", "commonshared"));
		// for each goal, check how many times it was created and whether it was created
		// for self or others
		for (UserGoal userGoal : userLearningGoals) {
			Map<String, Object> countAndFlag = new HashMap<>();
			String goalId = userGoal.getUserLearningGoalsPrimaryKey().getGoalId().toString();
			if (userCommonGoalsWithCount.containsKey(goalId)) {
				countAndFlag.put("count",
						(int) ((Map<String, Object>) userCommonGoalsWithCount.get(goalId)).get("count") + 1);
			} else {
				countAndFlag.put("count", 1);
			}
			if (userGoal.getUserLearningGoalsPrimaryKey().getGoalType().equalsIgnoreCase("common")) {
				countAndFlag.put("flag", "createdForSelf");
			} else {
				countAndFlag.put("flag", "createdForOthers");
			}
			userCommonGoalsWithCount.put(goalId, countAndFlag);
		}
	}

	/**
	 * This method is used to group a common goal into a goal group. It returns a
	 * map containing a list of grouped and ungrouped common goals.
	 * 
	 * @param clg
	 * @param commonGoalWithStatus
	 * @param grouped
	 * @param notGrouped
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void groupGoals(List<Map<String, Object>> commonGoals, List<Map<String, Object>> finalCommnGoalData) {

		// map each common goal to its goal group
		List<String> alreadyProcessedGroups = new ArrayList<>();
		Map<String, Object> groupedCommonGoals = new HashMap<>();
		List<Object> notGroupedCommonGoals = new ArrayList<>();
		Map<String, Object> groupNames = new HashMap<>();
		for (Map<String, Object> goal : commonGoals) {
			List<Object> groupedList = new ArrayList<>();
			String goalGroup = (String) goal.getOrDefault("groupId", null);
			groupNames.put(goalGroup, goal.get("groupName").toString());
			if (goalGroup != null) {
				if (alreadyProcessedGroups.isEmpty() || !alreadyProcessedGroups.contains(goalGroup)) {
					alreadyProcessedGroups.add(goalGroup);
					groupedList.add(goal);
					groupedCommonGoals.put(goalGroup, groupedList);
				} else {
					groupedList = (List<Object>) groupedCommonGoals.get(goalGroup);
					groupedList.add(goal);
					groupedCommonGoals.put(goalGroup, groupedList);
				}
			} else {
				notGroupedCommonGoals.add(goal);
			}
		}
		// prepare final data to be sent as response
		for (Entry<String, Object> data : groupedCommonGoals.entrySet()) {
			Map<String, Object> commonGoal = new HashMap<>();
			commonGoal.put("group_id", data.getKey());
			commonGoal.put("group_name", groupNames.get(data.getKey()));
			commonGoal.put("goals", data.getValue());
			finalCommnGoalData.add(commonGoal);
		}
		if (!notGroupedCommonGoals.isEmpty()) {
			Map<String, Object> commonGoal = new HashMap<>();
			commonGoal.put("group_id", "Others");
			commonGoal.put("group_name", "Others");
			commonGoal.put("goals", notGroupedCommonGoals);
			finalCommnGoalData.add(commonGoal);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#updateCommonGoalDuration(java.lang.
	 * String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public Map<String, Object> updateCommonGoalDuration(String rootOrg, String userUUID, String goalType, String goalId,
			int duration) throws Exception {

		// user can only update the duration of a common goal created for self and
		// created for others but not yet shared
		if (!goalType.equalsIgnoreCase("common") && !goalType.equalsIgnoreCase("commonshared")) {
			throw new InvalidDataInputException("invalid.goaltype");
		}

		// new duration must be greater than 0.
		if (!(duration > 0) || duration > 366) {
			throw new InvalidDataInputException("invalid.duration");
		}

		// user must exist
		this.validateUser(rootOrg, userUUID);

		// common learning goal must be present for the user
		UserGoal learningGoal = learningGoalsRepo
				.findById(new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId)))
				.orElseThrow(() -> new InvalidDataInputException("invalid.goal"));

		// calculate new goal end date and update other audit fields
		learningGoal.setGoalDuration(duration);
		learningGoal.setLastUpdatedOn(new Timestamp((new Date()).getTime()));
		if (!goalType.equalsIgnoreCase("commonshared")) {
			Timestamp endDate = helper.calculateGoalEndDate(learningGoal.getGoalStartDate(), duration);
			learningGoal.setGoalEndDate(endDate);
		}

		learningGoalsRepo.save(learningGoal);

		Map<String, Object> updateResult = new HashMap<>();
		updateResult.put("result", "Success");
		return updateResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#deleteResourceFromUserGoal(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> deleteResourceFromUserGoal(String rootOrg, String userUUID, String goalId, String lexId,
			String goalType) throws Exception {

		// check if the user is valid
		this.validateUser(rootOrg, userUUID);

		// check if the goal type is valid
		helper.validateULGoalType(goalType);

		// check if the goal exists
		UserGoal ulg = learningGoalsRepo.findById(new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId)))
				.orElseThrow(() -> new InvalidDataInputException("invalid.goal"));

		// check if the content which is being deleted exists in the goal or not
		List<String> contentIds = ulg.getGoalContentId();
		if (!contentIds.contains(lexId)) {
			throw new InvalidDataInputException("invalid.content");
		}

		// fetch required meta
		List<Map<String, Object>> contentMeta = contentService.getMetaByIDListandSource(contentIds,
				new String[] { "identifier", "duration" }, null);

		if (contentIds.size() != contentMeta.size()) {
			throw new ResourceNotFoundException("meta.notFound");
		}
		// final response map
		Map<String, Object> deleteMap = new HashMap<>();
		// delete the goal if all the contents from the goal have been removed after
		// deleting this goal
		if (this.checkIfResourceExistsInMeta(contentMeta, lexId)) {
			Map<String, Object> durationData = this.calcNewGoalEndDate(contentMeta, lexId, ulg.getGoalStartDate(),
					ulg.getGoalEndDate(), false);
			contentIds.remove(lexId);
			if (contentIds.size() == 0) {
				learningGoalsRepo.deleteById(ulg.getUserLearningGoalsPrimaryKey());
				deleteMap.put("goal_deleted", true);
				deleteMap.put("content_deleted", true);
				deleteMap.put("message", "Goal deleted as it was the only content in goal.");
			}
			// update the goal
			else {
				Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				ulg.setGoalDuration((int) durationData.get("newDuration"));
				ulg.setGoalEndDate((Date) durationData.get("endDate"));
				ulg.setLastUpdatedOn(timestamp);
				learningGoalsRepo.save(ulg);
				deleteMap.put("goal_deleted", false);
				deleteMap.put("content_deleted", true);
				deleteMap.put("message", "Content deleted.");
			}
		} else {
			deleteMap.put("goal_deleted", false);
			deleteMap.put("content_deleted", false);
			deleteMap.put("message", "Not able to delete content.");
		}
		return deleteMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.goal.service.GoalsService#addContentUserGoal(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> addContentToUserGoal(String userUUID, String goalId, String lexId, String goalType,
			String rootOrg) throws Exception {


		// check if user exists
		this.validateUser(rootOrg, userUUID);

		// check if goal type is valid
		helper.validateULGoalType(goalType);

		// check if the goal exists
		UserGoal ulg = learningGoalsRepo.findById(new UserGoalKey(rootOrg, userUUID, goalType, UUID.fromString(goalId)))
				.orElseThrow(() -> new InvalidDataInputException("invalid.goal"));

		// check for access or deleted status for the lexId that is being tried to be
		// added
		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), Arrays.asList(lexId), new HashMap<>(), false,
				rootOrg, null, null);

		// the content to be added must not already be present in the goal
		List<String> contentIds = ulg.getGoalContentId();
		if (contentIds.contains(lexId)) {
			throw new InvalidDataInputException("content.alreadyexists");
		}

		// add the latest resource and fetch every content's meta including new resource
		contentIds.add(lexId);
		List<Map<String, Object>> contentMeta = contentService.getMetaByIDListandSource(contentIds,
				new String[] { "identifier", "duration" }, null);

		if (!this.checkIfResourceExistsInMeta(contentMeta, lexId)) {
			throw new InvalidDataInputException("meta.notFound");
		}

		// remove subset from goal content list based on addition of new content
		Map<String, Object> parentResourceMap = this.updateGoalReosurces(contentIds);
		List<String> newResourceList = (List<String>) parentResourceMap.get("resource_list");
		if (newResourceList == null || newResourceList.isEmpty()) {
			throw new InvalidDataInputException("internal.error");
		}
		if (!newResourceList.contains(lexId)) {
			throw new InvalidDataInputException("content.parent.alreadyExists");
		}

		List<Map<String, Object>> newContentMeta = new ArrayList<>();
		List<String> resourcesFoundInMeta = new ArrayList<>();
		Map<String, Object> metaFoundForEachContent = new HashMap<>();
		for (Map<String, Object> map : contentMeta) {
			resourcesFoundInMeta.add(map.get("identifier").toString());
			metaFoundForEachContent.put(map.get("identifier").toString(), map);
		}
		for (String content : newResourceList) {
			if (resourcesFoundInMeta.contains(content)) {
				newContentMeta.add((Map<String, Object>) metaFoundForEachContent.get(content));
			} else {
				throw new ResourceNotFoundException("meta.notFound");
			}
		}
		// update the goal with updated contentIds and other audit fields and populate
		// the response data
		Map<String, Object> output = new HashMap<>();
		ulg.setGoalContentId(newResourceList);
		Map<String, Object> newEndDateData = this.calcNewGoalEndDate(newContentMeta, lexId, ulg.getGoalStartDate(),
				ulg.getGoalEndDate(), true);
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		ulg.setGoalDuration((int) newEndDateData.get("newDuration"));
		ulg.setGoalEndDate((Date) newEndDateData.get("endDate"));
		ulg.setLastUpdatedOn(timestamp);
		learningGoalsRepo.save(ulg);
		output.put("result", "success");
		if (newResourceList.size() != contentIds.size()) {
			output.put("resource_removal_message", parentResourceMap.get("goal_message"));
		} else {
			output.put("resource_removal_message", null);
		}
		return output;
	}

	/**
	 * This method is used to check if a given resource exists in the given meta.
	 * 
	 * @param meta
	 * @param resource
	 * @return
	 */
	private boolean checkIfResourceExistsInMeta(List<Map<String, Object>> meta, String resource) {

		if (meta == null || meta.isEmpty()) {
			throw new ResourceNotFoundException("meta.notFound");
		}

		for (Map<String, Object> eachMeta : meta) {
			if (eachMeta.get("identifier").equals(resource)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to calculate the new end date as well as new duration of
	 * the goal on addition of a new resource. First it calculates the weighted
	 * duration of the resource based on the old goal duration. It is then converted
	 * to seconds and the end date is incremented to that number of seconds. In the
	 * end, to get new goal duration, the difference between the new end date and
	 * the goal start date is taken.
	 * 
	 * @param contentMeta
	 * @param contentId
	 * @param goalStartDate
	 * @param endDate
	 * @param isAdded
	 * @return
	 */
	private Map<String, Object> calcNewGoalEndDate(List<Map<String, Object>> contentMeta, String contentId,
			Date goalStartDate, Date endDate, boolean isAdded) {

		// calculate total goal duration in seconds as well as duration of the resource
		// to be added/deleted
		int totalGoalDurationInSeconds = 0;
		int singleResouceDuration = 0;
		if (contentMeta != null && !contentMeta.isEmpty()) {
			for (Map<String, Object> meta : contentMeta) {
				if (!meta.get("identifier").equals(contentId)) {
					totalGoalDurationInSeconds += (int) meta.get("duration");
				} else {
					singleResouceDuration = (int) meta.get("duration");
				}
			}
		}
		// calculate goal end date
		int goalDuration = (int) ((endDate.getTime() - goalStartDate.getTime()) / (24 * 60 * 60 * 1000));
		// if resource is to be deleted, then its duration will also be a part of total
		// goal duration
		if (!isAdded) {
			totalGoalDurationInSeconds += singleResouceDuration;
		}
		// calculate weighted average for a resource, which gives how many days will
		// increment/decrement
		// in the goal if the resource is added/deleted
		float excessDuration = (goalDuration * singleResouceDuration / totalGoalDurationInSeconds);
		// convert excess duration into seconds
		float seconds = excessDuration * 24 * 3600;
		// calculate new goal end date
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		if (!isAdded) {
			cal.add(Calendar.SECOND, -(int) seconds);
		} else {
			cal.add(Calendar.SECOND, (int) seconds);
		}
		Date newEndDate = cal.getTime();
		Date currentDate = Calendar.getInstance().getTime();
		// If end date was before today, set endDate as today.
		if (endDate.before(currentDate)) {
			endDate = currentDate;
		}
		// calculate new duration based on new goal end date
		int newDuration = (int) ((newEndDate.getTime() - goalStartDate.getTime()) / (24 * 60 * 60 * 1000));
		if (newDuration == 0) {
			newDuration = 1;
		} else if (newDuration > 366) {
			newDuration = 366;

			Calendar calInstance = Calendar.getInstance();
			calInstance.setTime(goalStartDate);
			calInstance.add(Calendar.DATE, newDuration);
			newEndDate = calInstance.getTime();
		}

		// prepare finalData
		Map<String, Object> result = new HashMap<>();
		result.put("newDuration", newDuration);
		result.put("endDate", newEndDate);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#updateGoalReosurces(java.util.List)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> updateGoalReosurces(List<String> goalContentList) throws IOException {

		if (goalContentList == null || goalContentList.isEmpty()) {
			throw new InvalidDataInputException("contentlist.nullorEmpty");
		}
		// Remove duplicate goal contents
		this.removeDuplicates(goalContentList);
		// Fetch the required meta for every resource
		// ******************-------------------*************************************************************
		List<Map<String, Object>> resourceMetaList = contentService.getMetaByIDListandSource(goalContentList,
				new String[] { "status", "duration", "contentType", "name", "identifier" }, null);
		// _____________________________________**************************************________________________
		// process required meta
		Map<String, Object> resourceWiseMeta = new HashMap<>();
		List<String> resourceRemovalMessages = new ArrayList<>();
		List<String> updatedResourceList = new ArrayList<>();
		int totalSuggestedTime = 0; // Suggested time to complete the goal/resource
		if (resourceMetaList != null && !resourceMetaList.isEmpty()) {
			// remove contents whose status is not live
			for (Map<String, Object> eachMeta : resourceMetaList) {
				Map<String, Object> requiredMeta = new HashMap<>();
				if (eachMeta.get("status").toString().equalsIgnoreCase("live")) {
					requiredMeta.putAll(eachMeta);
					requiredMeta.put("contentType", this.getUIContentType(eachMeta.get("contentType").toString()));
					resourceWiseMeta.put(eachMeta.get("identifier").toString(), requiredMeta);
				}
			}

			// fetch resource removal messages and update the content list while calculating
			// suggested time
			for (Entry<String, Object> content : resourceWiseMeta.entrySet()) {
				Map<String, Object> parentData = this.fetchParents(content.getKey(), goalContentList);
				Map<String, Object> updatedContentData = this.getUpdatedResourceData(parentData, content,
						goalContentList);
				resourceRemovalMessages.addAll((List<String>) updatedContentData.get("resourceRemovalMessages"));
				updatedResourceList.addAll((List<String>) updatedContentData.get("updatedResourceList"));
				totalSuggestedTime += (int) updatedContentData.get("totalSuggestedTime");
			}
		}

		// populate the final map
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("suggested_time", totalSuggestedTime);
		this.removeDuplicates(updatedResourceList);
		output.put("resource_list", updatedResourceList);
		output.put("goal_message", resourceRemovalMessages);
		return output;
	}

	/**
	 * This method is used to convert a list to a list with distinct values.
	 * 
	 * @param contentIds
	 * @return
	 */
	private void removeDuplicates(List<String> contentIds) {

		Set<String> set = new LinkedHashSet<>();
		set.addAll(contentIds);
		contentIds.clear();
		contentIds.addAll(set);
	}

	/**
	 * This method is used to fetch the content type as visible on UI against the
	 * content type stored in meta.
	 * 
	 * @param contentType
	 * @return
	 */
	private String getUIContentType(String contentType) {

		if (contentType != null) {
			contentType = contentType.toLowerCase();
			if (contentType.equals("collection")) {
				contentType = "learning module";
			} else if (contentType.equals("learning path")) {
				contentType = "program";
			}
		} else {
			contentType = "";
		}
		return contentType;
	}

	/**
	 * This method is used to check whether a parent of the resource exists in goal
	 * content list.
	 * 
	 * @param parents
	 * @param goalContentList
	 * @return
	 */
	private boolean checkIfParentExistsInContentList(List<Map<String, Object>> parents, List<String> goalContentList) {
		boolean parentExist = false;
		for (Map<String, Object> parent : parents) {
			if (goalContentList.contains(parent.get(ContentMetaConstants.IDENTIFIER).toString())) {
				parentExist = true;
				break;
			}
		}
		return parentExist;
	}

	/**
	 * This method is used to fetch the parent data belonging to a resource and a
	 * flag indicating what type of parent exists, whether a learning path, course
	 * or a module.
	 * 
	 * @param resource
	 * @param goalContentList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchParents(String resource, List<String> goalContentList) {
		Map<String, Object> returnMap = new HashMap<>();

		// fetch all the parents of a resource
		Map<String, Object> parentData = parentObj.getAllParents(resource);
		// check if parent exist in goal content based on type of parent that is,
		// Learning path, Module or course and set flag accordingly
		List<Map<String, Object>> parents = new ArrayList<>();
		String flag = "";
		if (parentData != null && !parentData.isEmpty()) {
			if (parentData.get("learningPaths") != null
					&& ((List<Map<String, Object>>) parentData.get("learningPaths")).size() > 0) {
				if (this.checkIfParentExistsInContentList((List<Map<String, Object>>) parentData.get("learningPaths"),
						goalContentList)) {
					parents = (List<Map<String, Object>>) parentData.get("learningPaths");
					flag = "LP";
				}
			}
			if (parentData.get("courses") != null && ((List<Map<String, Object>>) parentData.get("courses")).size() > 0
					&& flag == "") {
				if (this.checkIfParentExistsInContentList((List<Map<String, Object>>) parentData.get("courses"),
						goalContentList)) {
					parents = (List<Map<String, Object>>) parentData.get("courses");
					flag = "C";
				}
			}
			if (parentData.get("modules") != null && ((List<Map<String, Object>>) parentData.get("modules")).size() > 0
					&& flag == "") {
				if (this.checkIfParentExistsInContentList((List<Map<String, Object>>) parentData.get("modules"),
						goalContentList)) {
					parents = (List<Map<String, Object>>) parentData.get("modules");
					flag = "M";
				}
			}
		}
		returnMap.put("parents", parents);
		returnMap.put("flag", flag);
		return returnMap;
	}

	/**
	 * This method is used to update the goal content list based on the parents of
	 * the resource fetched. It also fetches the appropriate resource removal
	 * messages based on the situation and calculates the total suggested time.
	 * 
	 * @param parentData
	 * @param contentData
	 * @param goalContentList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getUpdatedResourceData(Map<String, Object> parentData,
			Entry<String, Object> contentData, List<String> goalContentList) {

		List<String> resourceRemovalMessages = new ArrayList<>();
		List<String> updatedResourceList = new ArrayList<>();
		int totalSuggestedTime = 0; // Suggested time to complete the goal/resource
		String resource = contentData.getKey();
		Map<String, Object> content = (Map<String, Object>) contentData.getValue();
		if (parentData.get("parents") != null && !((List<Map<String, Object>>) parentData.get("parents")).isEmpty()) {
			List<Map<String, Object>> parents = (List<Map<String, Object>>) parentData.get("parents");
			String flag = (String) parentData.get("flag");
			for (Map<String, Object> parent : parents) {
				// if the parent is being added after the child has been added to the goal
				if (goalContentList.indexOf(resource) < goalContentList
						.indexOf(parent.get(ContentMetaConstants.IDENTIFIER).toString())) {
					if (flag.equalsIgnoreCase("LP")) {
						resourceRemovalMessages
								.add("Goal updated and \"" + content.get("name") + "\" " + content.get("contentType")
										+ " has been moved to \"" + parent.get("name").toString() + "\" program.");
					} else if (flag.equalsIgnoreCase("C")) {
						resourceRemovalMessages
								.add("Goal updated and \"" + content.get("name") + "\" " + content.get("contentType")
										+ " has been moved to \"" + parent.get("name").toString() + "\" course.");
					} else if (flag.equalsIgnoreCase("M")) {
						resourceRemovalMessages.add("Goal updated and \"" + content.get("name") + "\" "
								+ content.get("contentType") + " has been moved to \"" + parent.get("name").toString()
								+ "\" learning module.");
					}
					updatedResourceList.add(goalContentList
							.get(goalContentList.indexOf(parent.get(ContentMetaConstants.IDENTIFIER).toString())));
					// Else the resource can not be added since it already has been added
					// as part of its parent
				} else {
					if (goalContentList.contains(parent.get(ContentMetaConstants.IDENTIFIER).toString())) {
						if (flag.equalsIgnoreCase("LP")) {
							resourceRemovalMessages.add("\"" + content.get("name") + "\" " + content.get("contentType")
									+ " cannot be added to this goal as its parent program \""
									+ parent.get("name").toString() + "\" is already part of this goal.");
						} else if (flag.equalsIgnoreCase("C")) {
							resourceRemovalMessages.add("\"" + content.get("name") + "\" " + content.get("contentType")
									+ " cannot be added to this goal as its parent course \""
									+ parent.get("name").toString() + "\" is already part of this goal.");
						} else if (flag.equalsIgnoreCase("M")) {
							resourceRemovalMessages.add("\"" + content.get("name") + "\" " + content.get("contentType")
									+ " cannot be added to this goal as its parent module \""
									+ parent.get("name").toString() + "\" is already part of this goal.");
						}
					}
				}
			}
		}
		// if no parent for a resource exist in goal content, add the resource itself
		else {
			updatedResourceList.add(resource);
			totalSuggestedTime += (int) content.get("duration");
		}

		// final data to be returned for each resource
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("updatedResourceList", updatedResourceList);
		returnMap.put("resourceRemovalMessages", resourceRemovalMessages);
		returnMap.put("totalSuggestedTime", totalSuggestedTime);
		return returnMap;
	}

	// *********************************************************************************************************
	// New Code
	// Author- Mannmath Samantaray
	// **********************************************************************************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#modifySharedGoal(java.util.Map,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> removeGoalSharing_v1(Map<String, Object> targetIdsMap, String userUUID, String goalId,
			String goalType, String rootOrg) throws Exception{

		Map<String, Object> finalMap = new HashMap<String, Object>();

		helper.validateUUID(goalId);
		helper.validateUserSharedGoalType(goalType);
		this.validateUser(rootOrg, userUUID);

		// Extract and validate all target ids from input
		List<String> targetIds = (List<String>) targetIdsMap.get("users");

		List<SharedGoalTracker> sharedGoalData = sharedGoalTrackerRepo.fetchRecordsForGoalSharedByUser(rootOrg,
				userUUID, goalType, UUID.fromString(goalId));

		if (sharedGoalData.isEmpty() || sharedGoalData == null) {
			throw new InvalidDataInputException("invalid.goal");
		}

		Set<String> recipientSet = new HashSet<String>();
		Set<String> usersThatCanBeDeleted = new HashSet<>(targetIds);
		Set<String> unableToDeleteUsers = new HashSet<>(targetIds);

		for (SharedGoalTracker goalData : sharedGoalData) {
			recipientSet.add(goalData.getSharedGoalTrackerPrimaryKey().getSharedWith());
		}

		usersThatCanBeDeleted.retainAll(recipientSet);
		unableToDeleteUsers.removeAll(recipientSet);
		recipientSet.removeAll(targetIds);

		learningGoalsRepo.bulkExecute(learningGoalsRepo.generateDelStmtForSharedGoal(
				new ArrayList<>(usersThatCanBeDeleted), goalType, goalId, userUUID, rootOrg));

		if (recipientSet.size() == 0) {
			reinsertGoalToUserLearningGoals(rootOrg, sharedGoalData.get(0), userUUID, goalType, goalId);
		} else {
			List<String> emails = new ArrayList<>();
			if (!unableToDeleteUsers.isEmpty()) {
				Map<String, Object> listOfUsers = this.getEmailsFromUUIDs(rootOrg,
						new ArrayList<>(unableToDeleteUsers));
				if (listOfUsers != null && !listOfUsers.isEmpty()) {
					for (String user : listOfUsers.keySet()) {
						emails.add(listOfUsers.get(user).toString());
					}
				} else {
					emails = new ArrayList<>(unableToDeleteUsers);
				}
			}
			finalMap.put("not_able_to_delete", emails);
		}
		return finalMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> removeGoalSharing(Map<String, Object> targetIdsMap, String userUUID, String goalId,
			String goalType, String rootOrg) throws Exception{

		Map<String, Object> finalMap = new HashMap<String, Object>();

		helper.validateUUID(goalId);
		helper.validateUserSharedGoalType(goalType);
		this.validateUser(rootOrg, userUUID);

		// Extract and validate all target ids from input
		List<String> targetIds = (List<String>) targetIdsMap.get("users");

		List<SharedGoalTracker> sharedGoalData = sharedGoalTrackerRepo.fetchRecordsForGoalSharedByUser(rootOrg,
				userUUID, goalType, UUID.fromString(goalId));

		if (sharedGoalData.isEmpty() || sharedGoalData == null) {
			throw new InvalidDataInputException("invalid.goal");
		}

		Set<String> recipientSet = new HashSet<String>();
		Set<String> usersThatCanBeDeleted = new HashSet<>(targetIds);
		Set<String> unableToDeleteUsers = new HashSet<>(targetIds);

		for (SharedGoalTracker goalData : sharedGoalData) {
			recipientSet.add(goalData.getSharedGoalTrackerPrimaryKey().getSharedWith());
		}

		usersThatCanBeDeleted.retainAll(recipientSet);
		unableToDeleteUsers.removeAll(recipientSet);
		recipientSet.removeAll(targetIds);

		learningGoalsRepo.bulkExecute(learningGoalsRepo.generateDelStmtForSharedGoal(
				new ArrayList<>(usersThatCanBeDeleted), goalType, goalId, userUUID, rootOrg));

		if (recipientSet.size() == 0) {
			reinsertGoalToUserLearningGoals(rootOrg, sharedGoalData.get(0), userUUID, goalType, goalId);
		} else {
			List<Map<String, Object>> userData = new ArrayList<>();
			if (!unableToDeleteUsers.isEmpty()) {
				Map<String, Object> data = this.getMultipleUserData(rootOrg, new ArrayList<>(unableToDeleteUsers));
				if (data != null && !data.isEmpty()) {
					for (String user : unableToDeleteUsers) {
						if (data.containsKey(user)) {
							userData.add((Map<String, Object>) data.get(user));
						}
					}
				} else {
					for (String userId : unableToDeleteUsers) {
						Map<String, Object> uIdMap = new HashMap<>();
						uIdMap.put("userId", userId);

						userData.add(uIdMap);
					}
				}
			}
			finalMap.put("not_able_to_delete", userData);
		}
		return finalMap;
	}

	/**
	 * This method reinserts the goal back to ULG after all shared users are
	 * removed.
	 * 
	 * @param backupGoalData &nbsp;The backed-up goal data
	 * @param goalId         &nbsp;The id of the shared goal
	 * @param goalType       &nbsp;The type of the goal
	 * @param userUUID       &nbsp;The user's id
	 */
	private void reinsertGoalToUserLearningGoals(String rootOrg, SharedGoalTracker backupGoalData, String userUUID,
			String goalType, String goalId) {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		UserGoalKey learningGoalpk = new UserGoalKey();
		learningGoalpk = setupPrimaryKeyforULG(rootOrg, userUUID, goalType, goalId);
		UserGoal learningGoal = new UserGoal();
		learningGoal = setupGoalDataforULG(backupGoalData, learningGoalpk, timestamp);

		learningGoalsRepo.save(learningGoal);
	}

	/**
	 * This method conditions the backup data to setup primary key for ULG
	 * insertion.
	 * 
	 * @param userUUID &nbsp;The user's id
	 * @param goalType &nbsp;The type of the goal
	 * @param goalId   &nbsp;The id of the shared goal
	 * 
	 * @return primary key object
	 */
	private UserGoalKey setupPrimaryKeyforULG(String rootOrg, String userUUID, String goalType, String goalId) {

		// If goalType was "common_shared" it must have been "commonshared" earlier.
		// If it was "custom_shared", chage it to "tobeshared".
		String oldgoaltype = (goalType.equalsIgnoreCase("common_shared")) ? "commonshared" : "tobeshared";

		return new UserGoalKey(rootOrg, userUUID, oldgoaltype, UUID.fromString(goalId));
	}

	/**
	 * This method conditions the backup data for ULG insertion.
	 * 
	 * @param backupGoalData &nbsp;The backed-up goal data
	 * @param learningGoalpk &nbsp;The primary key for insertion
	 * @param timestamp      &nbsp;Current time-stamp
	 * 
	 * @return Conditioned data to be inserted
	 */
	private UserGoal setupGoalDataforULG(SharedGoalTracker backupGoalData, UserGoalKey learningGoalpk,
			Timestamp timestamp) {

		return new UserGoal(learningGoalpk, timestamp, backupGoalData.getGoalContentId(),
				backupGoalData.getGoalDescription(), backupGoalData.getGoalDuration(), null, null,
				backupGoalData.getGoalTitle(), backupGoalData.getVersion(), timestamp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#fetchGoalsListDependingOnGoalType(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> fetchGoalsByGoalType(String userUUID, String rootOrg, String goalType) throws Exception {

		this.validateUser(rootOrg, userUUID);

		// GoalType validation
		if (!Arrays.asList("user", "tobeshared").contains(goalType)) {
			throw new InvalidDataInputException("invalid.goaltype");
		}

		Map<String, Object> finalMap = new HashMap<String, Object>();

		List<Map<String, Object>> completeGoalData = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList(goalType));

		List<Map<String, Object>> requiredGoalData = removeUnwantedGoalAttributes(completeGoalData);
		finalMap.put(goalType, requiredGoalData);

		return finalMap;
	}

	/**
	 * @param completeGoalData
	 * @return
	 */
	private List<Map<String, Object>> removeUnwantedGoalAttributes(List<Map<String, Object>> completeGoalData) {

		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> goaldata : completeGoalData) {
			Map<String, Object> filteredData = new HashMap<String, Object>();
			filteredData.put("goal_title", goaldata.get("goal_title"));
			filteredData.put("goal_content_id", goaldata.get("goal_content_id"));
			filteredData.put("goal_desc", goaldata.get("goal_desc"));
			filteredData.put("goal_id", goaldata.get("goal_id"));
			filteredData.put("goal_type", goaldata.get("goal_type"));

			finalList.add(filteredData);
		}

		return finalList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#fetchMyGoalsAndGoalProgress(java.
	 * lang.String)
	 */
	@Override
	public Map<String, Object> fetchMyGoalsWithProgress_v1(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws Exception {

		this.validateUser(rootOrg, userUUID);

		// fetch user learning goals of type "common" and "user".
		List<Map<String, Object>> selfCreatedGoals = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList("common", "user"));

		// fetch user shared goals that are accepted
		List<Map<String, Object>> acceptedRecords = sharedGoalsRepo.getGoalsSharedToUserFilterStatus(rootOrg, userUUID,
				1);

		// combine and sort by last updated on
		List<Map<String, Object>> myGoals = new ArrayList<>(selfCreatedGoals);
		myGoals.addAll(acceptedRecords);
		Collections.sort(myGoals,
				(goal1, goal2) -> ((Date) goal2.get("last_updated_on")).compareTo((Date) goal1.get("last_updated_on")));

		// Extract all goal contents and remove duplicates.
		List<String> goalsContents = getGoalContentWithoutDeplicates(myGoals);

		// Fetch meta from elastic search and progress for the user for these content
		// from Cassandra
		Map<String, Object> requiredContentData = getMetaAndProgress(userUUID, goalsContents, rootOrg, metaFields);

		computeGoalProgress(myGoals, requiredContentData);

		List<Map<String, Object>> completedGoals = new ArrayList<>();
		List<Map<String, Object>> incompleteGoals = new ArrayList<>();

		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();
		for (Map<String, Object> goal : myGoals) {
			if (goal.get("goal_type").toString().equalsIgnoreCase("common")
					|| goal.get("goal_type").toString().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.get("goal_id").toString());
				versions.add(Float.parseFloat(goal.get("version").toString()));
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> commonGoals = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			commonGoals = this.getCommonGoalsByLanguage(commonGoals, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}
		for (Map<String, Object> goal : myGoals) {
			if (goalMap.containsKey(goal.get("goal_id").toString())) {
				goal.put("goal_desc",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalDesc());
				goal.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalTitle());
			}
		}
		Set<String> uuids = new HashSet<>();
		for (Map<String, Object> goal : myGoals) {
			if (goal.containsKey("shared_by")) {
				uuids.add(goal.get("shared_by").toString());
			} else if (goal.containsKey("shared_with")) {
				uuids.add(goal.get("shared_with").toString());
			} else if (goal.containsKey("user_id")) {
				uuids.add(goal.get("user_id").toString());
			}
		}
		Map<String, Object> userData = this.getEmailsFromUUIDs(rootOrg, new ArrayList<>(uuids));

		for (Map<String, Object> goal : myGoals) {
			if (goal.containsKey("user_id") && userData.containsKey(goal.get("user_id").toString())) {
				goal.put("user_email", userData.get(goal.get("user_id").toString()));
				goal.remove("user_id");
			} else if (goal.containsKey("shared_by") && userData.containsKey(goal.get("shared_by").toString())) {
				goal.put("shared_by", userData.get(goal.get("shared_by").toString()));
			} else if (goal.containsKey("shared_with") && userData.containsKey(goal.get("shared_with").toString())) {
				goal.put("shared_with", userData.get(goal.get("shared_with").toString()));
			}
		}
		separateGoalsByCompletion(myGoals, completedGoals, incompleteGoals);

		Map<String, Object> finalMap = new HashMap<String, Object>();
		finalMap.put("completed_goals", completedGoals);
		finalMap.put("goals_in_progress", incompleteGoals);

		return finalMap;
	}

	@Override
	public Map<String, Object> fetchMyGoalsWithProgress(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws Exception {

		System.out.println("metaFields "+metaFields);
		this.validateUser(rootOrg, userUUID);

		// fetch user learning goals of type "common" and "user".
		List<Map<String, Object>> selfCreatedGoals = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList("common", "user"));

		// fetch user shared goals that are accepted
		List<Map<String, Object>> acceptedRecords = sharedGoalsRepo.getGoalsSharedToUserFilterStatus(rootOrg, userUUID,
				1);

		// combine and sort by last updated on
		List<Map<String, Object>> myGoals = new ArrayList<>(selfCreatedGoals);
		myGoals.addAll(acceptedRecords);
		Collections.sort(myGoals,
				(goal1, goal2) -> ((Date) goal2.get("last_updated_on")).compareTo((Date) goal1.get("last_updated_on")));

		// Extract all goal contents and remove duplicates.
		List<String> goalsContents = getGoalContentWithoutDeplicates(myGoals);

		// Fetch meta from elastic search and progress for the user for these content
		// from Cassandra
		Map<String, Object> requiredContentData = getMetaAndProgress(userUUID, goalsContents, rootOrg, metaFields);

		computeGoalProgress(myGoals, requiredContentData);

		List<Map<String, Object>> completedGoals = new ArrayList<>();
		List<Map<String, Object>> incompleteGoals = new ArrayList<>();

		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();
		for (Map<String, Object> goal : myGoals) {
			if (goal.get("goal_type").toString().equalsIgnoreCase("common")
					|| goal.get("goal_type").toString().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.get("goal_id").toString());
				versions.add(Float.parseFloat(goal.get("version").toString()));
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> commonGoals = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			commonGoals = this.getCommonGoalsByLanguage(commonGoals, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}
		for (Map<String, Object> goal : myGoals) {
			if (goalMap.containsKey(goal.get("goal_id").toString())) {
				goal.put("goal_desc",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalDesc());
				goal.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalTitle());
			}
		}
		List<String> uuids = new ArrayList<>();
		for (Map<String, Object> goal : myGoals) {
			if (goal.containsKey("shared_by")) {
				uuids.add(goal.get("shared_by").toString());
			}
			if (goal.containsKey("shared_with")) {
				uuids.add(goal.get("shared_with").toString());
			}
			if (goal.containsKey("user_id")) {
				uuids.add(goal.get("user_id").toString());
			}
		}
		Map<String, Object> userDetails = this.getMultipleUserData(rootOrg, uuids);

		for (Map<String, Object> goal : myGoals) {
			if (goal.containsKey("user_id")) {
				if (userDetails.containsKey(goal.get("user_id").toString())) {
					goal.put("user", userDetails.get(goal.get("user_id").toString()));
				} else {
					goal.put("user", new HashMap<>());
				}
			}
			if (goal.containsKey("shared_by")) {
				if (userDetails.containsKey(goal.get("shared_by").toString())) {
					goal.put("shared_by", userDetails.get(goal.get("shared_by").toString()));
				} else {
					goal.put("shared_by", new HashMap<>());
				}
			}
			if (goal.containsKey("shared_with")) {
				if (userDetails.containsKey(goal.get("shared_with").toString())) {
					goal.put("shared_with", userDetails.get(goal.get("shared_with").toString()));
				} else {
					goal.put("shared_with", new HashMap<>());
				}
			}
		}
		separateGoalsByCompletion(myGoals, completedGoals, incompleteGoals);

		Map<String, Object> finalMap = new HashMap<String, Object>();
		finalMap.put("completed_goals", completedGoals);
		finalMap.put("goals_in_progress", incompleteGoals);

		return finalMap;
	}

	@Override
	public Map<String, Object> fetchUserGoalsContentWithProgress(String userUUID, String rootOrg, List<String> metaFields) throws Exception{

		this.validateUser(rootOrg, userUUID);

		String[] requiredFields = new String[] {"artifactUrl","children","complexityLevel","creatorContacts","downloadUrl","isExternal","lastUpdatedOn","learningMode","learningObjective","me_totalSessionsCount","resourceCategory","sourceName","hasAccess"};

		if(metaFields!=null){
			List<String> fieldsRequiredForProcessing = Arrays.asList(requiredFields);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

		}
		// fetch user learning goals of type "common" and "user".
		List<Map<String, Object>> selfCreatedGoals = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList("common", "user"));

		// fetch user shared goals that are accepted
		List<Map<String, Object>> acceptedRecords = sharedGoalsRepo.getGoalsSharedToUserFilterStatus(rootOrg, userUUID,
				1);

		// combine and sort by last updated on
		List<Map<String, Object>> myGoals = new ArrayList<>(selfCreatedGoals);
		myGoals.addAll(acceptedRecords);
		Collections.sort(myGoals,
				(goal1, goal2) -> ((Date) goal2.get("last_updated_on")).compareTo((Date) goal1.get("last_updated_on")));

		// Extract all goal contents and remove duplicates.
		List<String> goalsContents = getGoalContentWithoutDeplicates(myGoals);

		// Fetch meta from elastic search and progress for the user for these content
		// from Cassandra
		Map<String, Object> requiredContentMap = getMetaAndProgress(userUUID, goalsContents, rootOrg, metaFields!=null ?metaFields: new ArrayList<String>(Arrays.asList(requiredFields)));

		//compute complete and incomplete goals
		computeGoalProgress(myGoals, requiredContentMap);
		System.out.println("Computed goals "+new ObjectMapper().writeValueAsString(myGoals));

		List<Object> progressContent = new ArrayList<>();
		myGoals.forEach(goal ->{
			List<Map<String, Object>> resourceProgress = (List<Map<String, Object>>) goal.get("resource_progress");
			progressContent.addAll(resourceProgress.stream().filter((Map<String, Object> metaProgress) -> ((float)metaProgress.get("timeLeft"))>0).collect(Collectors.toList()));

		});

		Map<String, Object> finalMap = new HashMap<String, Object>();
		finalMap.put("contents", progressContent);
		return finalMap;
	}

	@SuppressWarnings("unchecked")
	private List<String> getGoalContentWithoutDeplicates(List<Map<String, Object>> myGoals) {

		// New set for all the content ids for all the goals.
		Set<String> goalsContentSet = new HashSet<>();
		for (Map<String, Object> goal : myGoals) {
			goalsContentSet.addAll((ArrayList<String>) goal.get("goal_content_id"));
		}

		return new ArrayList<String>(goalsContentSet);
	}

	/**
	 * This method is used to extract required metadata for the goals
	 * 
	 * @param goalsContentSet &nbsp;Goal content ids set
	 * @param goalsMetaData   &nbsp;Complete Metadata of each goal content
	 * @param userUUID        The mailId of the user
	 * 
	 * @return Put all the required meta data into a map and return it.
	 * @throws IOException 
	 * @throws ParseException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getMetaAndProgress(String userUUID, List<String> goalsContents, String rootOrg,
			List<String> metaFields) throws IOException, ParseException {

		// fetch meta from elastic search
		Map<String, Object> contentData = new HashMap<>();
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating", "expiryDate" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
			System.out.println("metaFields again "+metaFields);

		}

		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), goalsContents, contentData, true, rootOrg, null,
				source);
		// convert meta to format expected by UI
		// also compute different types of content found in goals
		Map<String, Object> statusData = new HashMap<>();
		if (!contentData.isEmpty()) {
			statusData = (Map<String, Object>) contentData.get(userUUID);
		}
		Set<String> contentTypes = new HashSet<>();
		Map<String, Object> contentMap = new HashMap<>();
		for (String content : goalsContents) {
			Map<String, Object> meta = new HashMap<>();

			if (statusData.containsKey(content)) {
				meta = (Map<String, Object>) statusData.get(content);

				if (meta.containsKey("contentType")) {
					contentTypes.add(meta.get("contentType").toString());
				}
			} else {
				meta.put("identifier", content);
			}

			if (meta.containsKey("averageRating")) {
				Float avgRating = 0f;
				Map<String, Object> rating = (Map<String, Object>) (meta.getOrDefault("averageRating",
						new HashMap<>()));
				if (!rating.isEmpty()) {
					if (rating.containsKey(rootOrg)) {
						avgRating = Float.parseFloat(rating.get(rootOrg).toString());
					}
				}
				meta.put("averageRating", avgRating);
			}
			if (meta.containsKey("totalRating")) {
				Integer totalRating = 0;
				Map<String, Object> rating = (Map<String, Object>) (meta.getOrDefault("totalRating", new HashMap<>()));
				if (!rating.isEmpty()) {
					if (rating.containsKey(rootOrg)) {
						totalRating = Integer.parseInt(rating.get(rootOrg).toString());
					}
				}
				meta.put("totalRating", totalRating);
			}
			contentMap.put(content, meta);
		}

		List<ContentProgressModel> progressData = contentProgressRepo.getProgress(rootOrg, userUUID,
				new ArrayList<String>(contentTypes), goalsContents);

		// Add progress to requiredContentData
		for (ContentProgressModel progress : progressData) {
			String contentId = progress.getPrimaryKey().getContentId();
			Map<String, Object> tempMap = (Map<String, Object>) contentMap.get(contentId);
			tempMap.put("progress", progress.getProgress());
		}
		return contentMap;
	}

	/**
	 * This method returns complete and incomplete goals after calculating total
	 * goal progress using the meta data
	 * 
	 * @param myGoals             &nbsp;Goals data
	 * @param requiredContentData &nbsp;Required meta data
	 * 
	 * @return Complete and incomplete goals
	 */
	@SuppressWarnings("unchecked")
	private void computeGoalProgress(List<Map<String, Object>> myGoals, Map<String, Object> requiredContentData) {

		for (Map<String, Object> goal : myGoals) {
			int totalGoalDuration = 0;
			float totalCompletionDuration = 0;

			List<Map<String, Object>> resourceProgress = new ArrayList<Map<String, Object>>();

			for (String contentId : ((List<String>) goal.get("goal_content_id"))) {
				if (requiredContentData.containsKey(contentId)) {
					Map<String, Object> metaAndProgress = (Map<String, Object>) requiredContentData.get(contentId);

					int totalContentDuration = (int) metaAndProgress.getOrDefault("duration", 0);
					totalGoalDuration += totalContentDuration;

					// Get the duration and multiply it with progress to get completion duration.
					float completionForContent = ((int) metaAndProgress.getOrDefault("duration", 0))
							* ((float) (metaAndProgress.getOrDefault("progress", 0.0F)));

					totalCompletionDuration += completionForContent;
					float timeLeftForContent = totalContentDuration - completionForContent;

					// Front end requirement
					if (metaAndProgress.keySet().size() > 1) {
						metaAndProgress.put("identifier", contentId);
						// Naming is done as per previous code.
						metaAndProgress.put("resource_progress",
								(float) (metaAndProgress.getOrDefault("progress", 0.0F)));
						metaAndProgress.put("timeLeft", timeLeftForContent);

						resourceProgress.add(metaAndProgress);
					}
				}
			}

			if (totalGoalDuration == 0) {
				// Just to avoid divide by zero exception
				totalGoalDuration = 1;
			}
			// Get total percentage of goal
			float totalGoalCompletion = (totalCompletionDuration / totalGoalDuration);
			// Incase something is wrong with the meta and duration exceeds 100%.
			if (totalGoalCompletion > 1) {
				totalGoalCompletion = 1;
			}
			goal.put("goalProgess", totalGoalCompletion);
			goal.put("resource_progress", resourceProgress);
		}

	}

	/**
	 * This method returns complete and incomplete goals after calculating total
	 * goal progress using the meta data
	 * 
	 * @param myGoals             &nbsp;Goals data
	 * @param requiredContentData &nbsp;Required meta data
	 * 
	 * @return Complete and incomplete goals
	 */
	private void separateGoalsByCompletion(List<Map<String, Object>> myGoals, List<Map<String, Object>> completedGoals,
			List<Map<String, Object>> incompleteGoals) {

		for (Map<String, Object> goal : myGoals) {
			if ((float) goal.get("goalProgess") == 1) {
				completedGoals.add(goal);
			} else
				incompleteGoals.add(goal);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#fetchGoalsSharedByMe(java.lang.
	 * String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchGoalsSharedByMe_v1(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws Exception {

		// fetch goals created for others but not yet shared
		List<Map<String, Object>> notYetSharedGoals = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList("commonshared", "tobeshared"));
		// fetch goals already shared with others
		List<SharedGoalTracker> goalsIHaveShared = sharedGoalTrackerRepo.fetchGoalsSharedByPerson(rootOrg, userUUID);
		List<Map<String, Object>> groupedGoals = groupGoalsBasedOnSharedBy(goalsIHaveShared);

		// Combine results, fetch meta and sort
		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
		finalList.addAll(notYetSharedGoals);
		finalList.addAll(groupedGoals);
		finalList = addContentDetails(userUUID, finalList, rootOrg, metaFields);

		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();
		for (Map<String, Object> goal : finalList) {
			if (goal.get("goal_type").toString().equalsIgnoreCase("commonshared")
					|| goal.get("goal_type").toString().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.get("goal_id").toString());
				versions.add(Float.parseFloat(goal.get("version").toString()));
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> commonGoals = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			commonGoals = this.getCommonGoalsByLanguage(commonGoals, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}
		for (Map<String, Object> goal : finalList) {
			if (goalMap.containsKey(goal.get("goal_id").toString())) {
				goal.put("goal_desc",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalDesc());
				goal.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalTitle());
			}
		}

		Set<String> uuids = new HashSet<>();
		for (Map<String, Object> goal : finalList) {
			if (goal.containsKey("recipient_list")) {
				uuids.addAll((List<String>) goal.get("recipient_list"));
			}
			if (goal.containsKey("user_id")) {
				uuids.add(goal.get("user_id").toString());
			}
			if (goal.containsKey("goal_shared_by")) {
				uuids.add(goal.get("goal_shared_by").toString());
			}
		}

		Map<String, Object> userData = this.getEmailsFromUUIDs(rootOrg, new ArrayList<>(uuids));

		for (Map<String, Object> goal : finalList) {
			if (goal.containsKey("recipient_list")) {
				List<String> recipientEmails = new ArrayList<>();
				List<String> recipients = (List<String>) goal.get("recipient_list");

				for (String userId : recipients) {
					if (userData.containsKey(userId)) {
						if (userData.get(userId) != null && !userData.get(userId).toString().isEmpty()) {
							recipientEmails.add(userData.get(userId).toString());
						} else {
							recipientEmails.add(userId);
						}
					}
				}

				goal.put("recipient_list", recipientEmails);
			}
			if (goal.containsKey("user_id")) {
				if (userData.containsKey(goal.get("user_id"))) {
					goal.put("user_email", userData.get(goal.get("user_id")));
					goal.remove("user_id");
				}
			}
			if (goal.containsKey("goal_shared_by")) {
				if (userData.containsKey(goal.get("goal_shared_by"))) {
					goal.put("goal_shared_by", userData.get(goal.get("goal_shared_by")));
				}
			}

			goal.put("goal_start_date", null);
			goal.put("goal_end_date", null);
		}

		Collections.sort(finalList,
				(goal1, goal2) -> ((Date) goal2.get("last_updated_on")).compareTo((Date) goal1.get("last_updated_on")));

		return finalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchGoalsSharedByMe(String userUUID, String rootOrg, String language,
			List<String> metaFields) throws IOException, ParseException {

		// fetch goals created for others but not yet shared
		List<Map<String, Object>> notYetSharedGoals = learningGoalsRepo
				.findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
						rootOrg, userUUID, Arrays.asList("commonshared", "tobeshared"));
		// fetch goals already shared with others
		List<SharedGoalTracker> goalsIHaveShared = sharedGoalTrackerRepo.fetchGoalsSharedByPerson(rootOrg, userUUID);
		List<Map<String, Object>> groupedGoals = groupGoalsBasedOnSharedBy(goalsIHaveShared);

		// Combine results, fetch meta and sort
		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
		finalList.addAll(notYetSharedGoals);
		finalList.addAll(groupedGoals);
		finalList = addContentDetails(userUUID, finalList, rootOrg, metaFields);

		Set<String> commonGoalIds = new HashSet<>();
		Set<Float> versions = new HashSet<>();
		for (Map<String, Object> goal : finalList) {
			if (goal.get("goal_type").toString().equalsIgnoreCase("commonshared")
					|| goal.get("goal_type").toString().equalsIgnoreCase("common_shared")) {
				commonGoalIds.add(goal.get("goal_id").toString());
				versions.add(Float.parseFloat(goal.get("version").toString()));
			}
		}
		Map<String, Object> goalMap = new HashMap<>();
		if (!commonGoalIds.isEmpty()) {
			Set<String> languages = new LinkedHashSet<>();
			if (language == null) {
				language = this.fetchPreferredLanguage(userUUID, rootOrg);
				if (language != null && !language.isEmpty()) {
					languages.addAll(Arrays.asList(language.split(",")));
				}
			} else {
				languages.addAll(Arrays.asList(language.split(",")));
			}
			languages.add("en");
			languages.remove("");
			languages.remove(null);

			List<CommonGoalProjection> commonGoals = commonGoalsRepo.fetchCommonGoalsByRootOrgIdAndLanguage(rootOrg,
					new ArrayList<>(commonGoalIds), new ArrayList<>(languages), new ArrayList<>(versions));

			commonGoals = this.getCommonGoalsByLanguage(commonGoals, languages);

			for (CommonGoalProjection commonGoal : commonGoals) {
				goalMap.put(commonGoal.getId(), commonGoal);
			}
		}
		for (Map<String, Object> goal : finalList) {
			if (goalMap.containsKey(goal.get("goal_id").toString())) {
				goal.put("goal_desc",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalDesc());
				goal.put("goal_title",
						((CommonGoalProjection) goalMap.get(goal.get("goal_id").toString())).getGoalTitle());
			}
		}

		List<String> uuids = new ArrayList<>();
		for (Map<String, Object> goal : finalList) {
			if (goal.containsKey("recipient_list")) {
				uuids.addAll((List<String>) goal.get("recipient_list"));
			}
			if (goal.containsKey("user_id")) {
				uuids.add(goal.get("user_id").toString());
			}
			if (goal.containsKey("goal_shared_by")) {
				uuids.add(goal.get("goal_shared_by").toString());
			}
		}
		Map<String, Object> userDetails = this.getMultipleUserData(rootOrg, uuids);

		for (Map<String, Object> goal : finalList) {
			if (goal.containsKey("recipient_list")) {
				List<Map<String, Object>> userData = new ArrayList<>();
				List<String> recipients = (List<String>) goal.get("recipient_list");

				for (String userId : recipients) {
					if (userDetails.containsKey(userId)) {
						userData.add((Map<String, Object>) userDetails.get(userId));
					} else {
						Map<String, Object> data = new HashMap<>();

						data.put("userId", userId);
						userData.add(data);
					}
				}
				goal.put("recipient_list", userData);
			}
			if (goal.containsKey("user_id")) {
				if (userDetails.containsKey(goal.get("user_id"))) {
					goal.put("user", userDetails.get(goal.get("user_id")));
					goal.remove("user_id");
				}
			}
			if (goal.containsKey("goal_shared_by")) {
				if (userDetails.containsKey(goal.get("goal_shared_by"))) {
					goal.put("goal_shared_by", userDetails.get(goal.get("goal_shared_by")));
				}
			}
			goal.put("goal_start_date", null);
			goal.put("goal_end_date", null);
		}

		Collections.sort(finalList,
				(goal1, goal2) -> ((Date) goal2.get("last_updated_on")).compareTo((Date) goal1.get("last_updated_on")));

		return finalList;
	}

	/**
	 * This method groups goals based on userEmail since there can be multiple
	 * records present for a single goal based on the number of recipients it's
	 * shared with.
	 * 
	 * @param goalsIHaveShared &nbsp;Un-grouped shared goal data
	 * 
	 * @return Grouped goal data based on shared by.
	 */

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> groupGoalsBasedOnSharedBy(List<SharedGoalTracker> goalsIHaveShared) {

		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();

		Collections.sort(goalsIHaveShared,
				(goal1, goal2) -> (goal2.getLastUpdatedOn()).compareTo(goal1.getLastUpdatedOn()));
		for (SharedGoalTracker goalDetails : goalsIHaveShared) {

			// Some goals have already been added to finalList
			if (!finalList.isEmpty()) {
				// Flag to indicate whether this goal is already added or not
				boolean isAdded = false;
				for (Map<String, Object> goal : finalList) {
					if (goal.containsValue(goalDetails.getSharedGoalTrackerPrimaryKey().getGoalId())) {
						isAdded = true;
						// If goal has been added, just add the recipients to the list
						((ArrayList<String>) goal.get("recipient_list"))
								.add(goalDetails.getSharedGoalTrackerPrimaryKey().getSharedWith());
						break;
					}
				}
				if (!isAdded) {
					// If goal is not already added, add it to finalList
					setupGoalData(finalList, goalDetails);
				}
			} else {
				// If no goals are added, add the first goal
				setupGoalData(finalList, goalDetails);
			}
		}
//		for (Map<String, Object> goal : finalList) {
//			Collections.reverse(((ArrayList<String>) goal.get("recipient_list")));
//		}
		return finalList;
	}

	/**
	 * This method adds the received goalData to the finalList.
	 * 
	 * @param finalList   &nbsp; list to which goalData is added
	 * @param goalDetails &nbsp; details of each shared goal
	 */
	@SuppressWarnings("unchecked")
	private void setupGoalData(List<Map<String, Object>> finalList, SharedGoalTracker goalDetails) {

		Map<String, Object> goalData = new HashMap<String, Object>();

		goalData.put("goal_content_id", goalDetails.getGoalContentId());
		goalData.put("goal_desc", goalDetails.getGoalDescription());
		goalData.put("goal_type", goalDetails.getSharedGoalTrackerPrimaryKey().getGoalType());
		goalData.put("goal_shared_by", goalDetails.getSharedGoalTrackerPrimaryKey().getSharedBy());
		goalData.put("goal_duration", goalDetails.getGoalDuration());
		goalData.put("goal_start_date", null);
		goalData.put("goal_end_date", null);
		goalData.put("goal_title", goalDetails.getGoalTitle());
		goalData.put("goal_id", goalDetails.getSharedGoalTrackerPrimaryKey().getGoalId());
		goalData.put("last_updated_on", goalDetails.getLastUpdatedOn());
		goalData.put("version", goalDetails.getVersion());

		// New list is being added so that multiple shared_with users can be held.
		goalData.put("recipient_list", new ArrayList<String>());
		((ArrayList<String>) goalData.get("recipient_list"))
				.add(goalDetails.getSharedGoalTrackerPrimaryKey().getSharedWith());

		finalList.add(goalData);
	}

	/**
	 * This method is responsible for adding required metadata
	 * 
	 * @param finalList &nbsp;The goal data for which metadata is required
	 * 
	 * @return Goal data along with content metadata.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked" })
	private List<Map<String, Object>> addContentDetails(String userUUID, List<Map<String, Object>> finalList,
			String rootOrg, List<String> metaFields) throws IOException, ParseException {

		Map<String, Object> IdToMetaMap = new HashMap<String, Object>();

		// Create a new set which will contain all the content_ids
		Set<String> goalsContentSet = new HashSet<>();
		for (Map<String, Object> goal : finalList) {
			goalsContentSet.addAll((ArrayList<String>) goal.get("goal_content_id"));
		}

		Map<String, Object> contentData = new HashMap<>();
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating", "expiryDate" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
		}

		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), new ArrayList<String>(goalsContentSet),
				contentData, true, rootOrg, null, source);
		/*
		 * Iterate through the metaData and fetch contentTypes for our content_ids and
		 * add it to corresponding content_id, which are required for the front_end
		 */
		Map<String, Object> statusData = new HashMap<>();
		if (!contentData.isEmpty()) {
			statusData = (Map<String, Object>) contentData.get(userUUID);
		}
		for (String content : goalsContentSet) {
			Map<String, Object> meta = new HashMap<>();
			if (statusData.containsKey(content)) {
				meta = (Map<String, Object>) statusData.get(content);
			} else {
				meta.put("identifier", content);
			}

			if (meta.containsKey("averageRating")) {
				Float avgRating = 0f;
				Map<String, Object> rating = (Map<String, Object>) (meta.getOrDefault("averageRating",
						new HashMap<>()));
				if (!rating.isEmpty()) {
					if (rating.containsKey(rootOrg)) {
						avgRating = Float.parseFloat(rating.get(rootOrg).toString());
					}
				}
				meta.put("averageRating", avgRating);
			}
			if (meta.containsKey("totalRating")) {
				Integer totalRating = 0;
				Map<String, Object> rating = (Map<String, Object>) (meta.getOrDefault("totalRating", new HashMap<>()));
				if (!rating.isEmpty()) {
					if (rating.containsKey(rootOrg)) {
						totalRating = Integer.parseInt(rating.get(rootOrg).toString());
					}
				}
				meta.put("totalRating", totalRating);
			}
			IdToMetaMap.put(content, meta);
		}
		// Add metaData to the corresponding goals
		for (Map<String, Object> goal : finalList) {
			List<String> contentIds = (List<String>) goal.get("goal_content_id");
			List<Map<String, Object>> goalContentDetails = new ArrayList<Map<String, Object>>();
			for (String contentId : contentIds) {
				if (((Map<String, Object>) IdToMetaMap.get(contentId)).keySet().size() > 1) {
					goalContentDetails.add((Map<String, Object>) IdToMetaMap.get(contentId));
				}

			}
			goal.put("goal_content_details", goalContentDetails);
		}
		return finalList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.goal.service.GoalsService#trackSharedGoal(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> trackSharedGoal_v1(String rootOrg, String userUUID, String goalId, String goalType,
			List<String> metaFields) throws IOException {

		Map<String, Object> finalMap = new HashMap<String, Object>();

		// Fetch all the records for each recipient irrespective of the status.
		List<SharedGoalTracker> goalDataForEveryRecipient = sharedGoalTrackerRepo
				.fetchRecordsForGoalSharedByUser(rootOrg, userUUID, goalType, UUID.fromString(goalId));

		List<SharedGoalTracker> acceptedRecords = new ArrayList<>();
		List<SharedGoalTracker> rejectedGoals = new ArrayList<>();
		List<SharedGoalTracker> pendingGoals = new ArrayList<>();

		separateGoalDataBasedOnStatus(goalDataForEveryRecipient, acceptedRecords, rejectedGoals, pendingGoals);

		if (pendingGoals.size() > 0) {
			List<Map<String, Object>> pendingData = processPendingOrRejectedData(pendingGoals, false);
			finalMap.put("pending", pendingData);
			finalMap.put("pending_count", pendingGoals.size());
		}
		if (rejectedGoals.size() > 0) {
			List<Map<String, Object>> rejectedData = processPendingOrRejectedData(rejectedGoals, true);
			finalMap.put("rejected", rejectedData);
			finalMap.put("rejected_count", rejectedGoals.size());
		}
		if (acceptedRecords.size() > 0) {
			List<Map<String, Object>> acceptedData = processAcceptedDataAndCalculateProgress(rootOrg, acceptedRecords,
					metaFields);
			finalMap.put("accepted", acceptedData);
			finalMap.put("accepted_count", acceptedRecords.size());
		}

		List<String> uuids = new ArrayList<>();
		if (finalMap.containsKey("pending")) {
			List<Map<String, Object>> pendingData = (List<Map<String, Object>>) finalMap.get("pending");
			for (Map<String, Object> data : pendingData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}
		if (finalMap.containsKey("rejected")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("rejected");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}
		if (finalMap.containsKey("accepted")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("accepted");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}

		Map<String, Object> dataMap = this.getEmailsFromUUIDs(rootOrg, uuids);

		if (finalMap.containsKey("pending")) {
			List<Map<String, Object>> pendingData = (List<Map<String, Object>>) finalMap.get("pending");
			for (Map<String, Object> data : pendingData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						if (data.get("shared_with") != null && !data.get("shared_with").toString().isEmpty()) {
							data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
						} else {
							data.put("shared_with", data.get("shared_with").toString());
						}
					}
				}
			}
		}
		if (finalMap.containsKey("rejected")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("rejected");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						if (data.get("shared_with") != null && !data.get("shared_with").toString().isEmpty()) {
							data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
						} else {
							data.put("shared_with", data.get("shared_with").toString());
						}
					}
				}
			}
		}
		if (finalMap.containsKey("accepted")) {
			List<Map<String, Object>> acceptedData = (List<Map<String, Object>>) finalMap.get("accepted");
			for (Map<String, Object> data : acceptedData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						if (data.get("shared_with") != null && !data.get("shared_with").toString().isEmpty()) {
							data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
						} else {
							data.put("shared_with", data.get("shared_with").toString());
						}
					}
				}
			}
		}
		return finalMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> trackSharedGoal(String rootOrg, String userUUID, String goalId, String goalType,
			List<String> metaFields) throws IOException {

		Map<String, Object> finalMap = new HashMap<String, Object>();

		// Fetch all the records for each recipient irrespective of the status.
		List<SharedGoalTracker> goalDataForEveryRecipient = sharedGoalTrackerRepo
				.fetchRecordsForGoalSharedByUser(rootOrg, userUUID, goalType, UUID.fromString(goalId));

		List<SharedGoalTracker> acceptedRecords = new ArrayList<>();
		List<SharedGoalTracker> rejectedGoals = new ArrayList<>();
		List<SharedGoalTracker> pendingGoals = new ArrayList<>();

		separateGoalDataBasedOnStatus(goalDataForEveryRecipient, acceptedRecords, rejectedGoals, pendingGoals);

		if (pendingGoals.size() > 0) {
			List<Map<String, Object>> pendingData = processPendingOrRejectedData(pendingGoals, false);
			finalMap.put("pending", pendingData);
			finalMap.put("pending_count", pendingGoals.size());
		}
		if (rejectedGoals.size() > 0) {
			List<Map<String, Object>> rejectedData = processPendingOrRejectedData(rejectedGoals, true);
			finalMap.put("rejected", rejectedData);
			finalMap.put("rejected_count", rejectedGoals.size());
		}
		if (acceptedRecords.size() > 0) {
			List<Map<String, Object>> acceptedData = processAcceptedDataAndCalculateProgress(rootOrg, acceptedRecords,
					metaFields);
			finalMap.put("accepted", acceptedData);
			finalMap.put("accepted_count", acceptedRecords.size());
		}

		List<String> uuids = new ArrayList<>();
		if (finalMap.containsKey("pending")) {
			List<Map<String, Object>> pendingData = (List<Map<String, Object>>) finalMap.get("pending");
			for (Map<String, Object> data : pendingData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}
		if (finalMap.containsKey("rejected")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("rejected");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}
		if (finalMap.containsKey("accepted")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("accepted");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					uuids.add(data.get("shared_with").toString());
				}
			}
		}

		Map<String, Object> dataMap = this.getMultipleUserData(rootOrg, uuids);

		if (finalMap.containsKey("pending")) {
			List<Map<String, Object>> pendingData = (List<Map<String, Object>>) finalMap.get("pending");
			for (Map<String, Object> data : pendingData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
					} else {
						Map<String, Object> userData = new HashMap<>();
						userData.put("userId", data.get("shared_with").toString());

						data.put("shared_with", userData);
					}
				}
			}
		}
		if (finalMap.containsKey("rejected")) {
			List<Map<String, Object>> rejectedData = (List<Map<String, Object>>) finalMap.get("rejected");
			for (Map<String, Object> data : rejectedData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
					} else {
						Map<String, Object> userData = new HashMap<>();
						userData.put("userId", data.get("shared_with").toString());

						data.put("shared_with", userData);
					}
				}
			}
		}
		if (finalMap.containsKey("accepted")) {
			List<Map<String, Object>> acceptedData = (List<Map<String, Object>>) finalMap.get("accepted");
			for (Map<String, Object> data : acceptedData) {
				if (data.containsKey("shared_with")) {
					if (dataMap.containsKey(data.get("shared_with").toString())) {
						data.put("shared_with", dataMap.get(data.get("shared_with").toString()));
					} else {
						Map<String, Object> userData = new HashMap<>();
						userData.put("userId", data.get("shared_with").toString());

						data.put("shared_with", userData);
					}
				}
			}
		}
		return finalMap;
	}

	/**
	 * This method separates the complete goal data into three categories namely;
	 * accepted_goals, rejected_goals and pending_goals.
	 * 
	 * @param goalDataForEveryRecipient &nbsp;Combined goal data
	 * @param pendingGoals              &nbsp;Pending goals
	 * @param rejectedGoals             &nbsp;Rejected goals
	 * @param acceptedRecords           &nbsp;Accepted goals
	 * 
	 */
	private void separateGoalDataBasedOnStatus(List<SharedGoalTracker> goalDataForEveryRecipient,
			List<SharedGoalTracker> acceptedRecords, List<SharedGoalTracker> rejectedGoals,
			List<SharedGoalTracker> pendingGoals) {

		for (SharedGoalTracker sharedGoal : goalDataForEveryRecipient) {
			switch (sharedGoal.getStatus()) {
			case 1:
				acceptedRecords.add(sharedGoal);
				break;
			case 0:
				pendingGoals.add(sharedGoal);
				break;
			case -1:
				rejectedGoals.add(sharedGoal);
			}
		}
	}

	/**
	 * This method finds out the pending users or rejected users and returns the
	 * details
	 * 
	 * @param pendingGoals &nbsp; The goal data
	 * 
	 * @return Required data for pending or rejected goals
	 */
	/**
	 * This method finds out the pending users or rejected users and returns the
	 * details
	 * 
	 * @param goalsData  &nbsp; The goal data
	 * @param isRejected &nbsp; If goal was rejected pass TRUE, else FALSE.
	 * 
	 * @return
	 */
	private List<Map<String, Object>> processPendingOrRejectedData(List<SharedGoalTracker> goalsData,
			boolean isRejected) {

		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();

		for (SharedGoalTracker goalData : goalsData) {
			Map<String, Object> requiredData = new HashMap<String, Object>();
			requiredData.put("last_updated_on", goalData.getLastUpdatedOn());
			requiredData.put("shared_with", goalData.getSharedGoalTrackerPrimaryKey().getSharedWith());
			requiredData.put("status", goalData.getStatus());
			if (isRejected) {
				requiredData.put("status_message", goalData.getStatusMessage());
			}

			finalList.add(requiredData);
		}

		return finalList;
	}

	/**
	 * This method finds out the users who accepted the goal and returns the details
	 * along with individual progress
	 * 
	 * @param acceptedGoals &nbsp; The goal data
	 * 
	 * @return Required data for accepted goals along with their progress
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> processAcceptedDataAndCalculateProgress(String rootOrg,
			List<SharedGoalTracker> acceptedGoals, List<String> metaFields) throws IOException {

		List<String> goalSharedWithIds = new ArrayList<>();
		List<String> goalContentIds = new ArrayList<>();

		// Here userId is mapped to user specific data like; goal_start_date,
		// goal_end_date and last_updated_on
		Map<String, Object> userIdToGoalData = new HashMap<>();
		for (SharedGoalTracker goalData : acceptedGoals) {
			Map<String, Object> reqGoalData = new HashMap<String, Object>();
			reqGoalData.put("goal_start_date", goalData.getGoalStartDate());
			reqGoalData.put("goal_end_date", goalData.getGoalEndDate());
			reqGoalData.put("last_updated_on", goalData.getLastUpdatedOn());
			userIdToGoalData.put(goalData.getSharedGoalTrackerPrimaryKey().getSharedWith(), reqGoalData);

			goalSharedWithIds.add(goalData.getSharedGoalTrackerPrimaryKey().getSharedWith());
			if (goalSharedWithIds.size() == 1) {
				// This is done because we want to fetch the content id just once
				goalContentIds.addAll(goalData.getGoalContentId());
			}
		}

		// Fetch all the meta for our goal contents.
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
		}

		List<Map<String, Object>> goalsMetaData = contentService.getMetaByIDListandSource(goalContentIds, source, null);

		List<Map<String, Object>> goalWithProgress = getProgressForEachRecipient(rootOrg, acceptedGoals,
				goalSharedWithIds, goalContentIds, goalsMetaData);

		for (Map<String, Object> recipientData : goalWithProgress) {
			Map<String, Object> remainingGoalData = (Map<String, Object>) userIdToGoalData
					.get(recipientData.get("shared_with"));
			recipientData.put("goal_start_date", remainingGoalData.get("goal_start_date"));
			recipientData.put("goal_end_date", remainingGoalData.get("goal_end_date"));
			recipientData.put("last_updated_on", remainingGoalData.get("last_updated_on"));
		}

		return goalWithProgress;
	}

	/**
	 * This method filters the metadata for required metadata and initializes
	 * progress for each resource. It will also fetch user's actual progress from
	 * user_content_progress
	 * 
	 * @param acceptedGoals     &nbsp;Goal data
	 * @param goalSharedWithIds &nbsp;Users who have accepted the goal
	 * @param goalContentIds    &nbsp;Goal Resource List
	 * @param goalsMetaData     &nbsp;Metadata for each resource
	 * 
	 * @return It returns the final processed data from
	 *         "calculateProgressForEachRecipient"
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getProgressForEachRecipient(String rootOrg, List<SharedGoalTracker> acceptedGoals,
			List<String> goalSharedWithIds, List<String> goalContentIds, List<Map<String, Object>> goalsMetaData){

		Map<String, Object> resourceIdToMeta = new HashMap<String, Object>();
		Set<String> contentTypes = new HashSet<String>();

		if (goalsMetaData != null && !goalsMetaData.isEmpty()) {
			for (Map<String, Object> goalMeta : goalsMetaData) {
				contentTypes.add(goalMeta.get("contentType").toString());

				Map<String, Object> requiredData = new HashMap<String, Object>();
				requiredData.put("contentType", goalMeta.get("contentType"));
				requiredData.put("mimeType", goalMeta.get("mimeType"));
				requiredData.put("duration", goalMeta.get("duration"));
				requiredData.put("name", goalMeta.get("name"));
				requiredData.put("description", goalMeta.get("description"));
				requiredData.put("appIcon", goalMeta.get("appIcon"));
				requiredData.put("identifier", goalMeta.get("identifier"));

				if (goalMeta.containsKey("averageRating")) {
					Float avgRating = 0f;
					Map<String, Object> rating = (Map<String, Object>) (goalMeta.getOrDefault("averageRating",
							new HashMap<>()));
					if (!rating.isEmpty()) {
						if (rating.containsKey(rootOrg)) {
							avgRating = Float.parseFloat(rating.get(rootOrg).toString());
						}
					}
					requiredData.put("averageRating", avgRating);
				}
				if (goalMeta.containsKey("totalRating")) {
					Integer totalRating = 0;
					Map<String, Object> rating = (Map<String, Object>) (goalMeta.getOrDefault("totalRating",
							new HashMap<>()));
					if (!rating.isEmpty()) {
						if (rating.containsKey(rootOrg)) {
							totalRating = Integer.parseInt(rating.get(rootOrg).toString());
						}
					}
					requiredData.put("totalRating", totalRating);
				}

				resourceIdToMeta.put((String) goalMeta.get("identifier"), requiredData);
			}
		}

		// Fetch user progress for each content
		List<ContentProgressModel> userProgressForResource = new ArrayList<>();
		if (!contentTypes.isEmpty()) {
			userProgressForResource = contentProgressRepo.getProgressForGoals(rootOrg, goalSharedWithIds,
					new ArrayList<String>(contentTypes), new ArrayList<String>(goalContentIds));
		}

		// userToResourceProgress -> {user_email:{resource:progress,.,.},.,.}
		Map<String, Object> userToResourceProgress = new HashMap<String, Object>();
		if (userProgressForResource == null || userProgressForResource.isEmpty()) {
			for (String eachUser : goalSharedWithIds) {
				Map<String, Object> progressMap = new HashMap<>();
				for (String id : goalContentIds) {
					progressMap.put(id, 0.0F);
				}
				userToResourceProgress.put(eachUser, progressMap);
			}
		} else {
			for (ContentProgressModel contentProgressModel : userProgressForResource) {
				if (!userToResourceProgress.containsKey(contentProgressModel.getPrimaryKey().getUserId())) {
					Map<String, Object> newResourceToProgressMap = new HashMap<>();
					newResourceToProgressMap.put(contentProgressModel.getPrimaryKey().getContentId(),
							contentProgressModel.getProgress());

					userToResourceProgress.put(contentProgressModel.getPrimaryKey().getUserId(),
							newResourceToProgressMap);
				} else {
					Map<String, Object> existingResourceToProgressMap = (Map<String, Object>) userToResourceProgress
							.get(contentProgressModel.getPrimaryKey().getUserId());
					existingResourceToProgressMap.put(contentProgressModel.getPrimaryKey().getContentId(),
							contentProgressModel.getProgress());
				}
			}
		}
		return calculateProgressForEachRecipient(userToResourceProgress, resourceIdToMeta, goalSharedWithIds);
	}

	/**
	 * This method calculates individual resource progress along with total goal
	 * progress and returns the required data.
	 * 
	 * @param userProgressForResource &nbsp;Progress data from user_content_progress
	 * @param userToResourceProgress  &nbsp;User mapping with individual resource
	 * @param resourceIdToMeta        &nbsp;Resource Id with meta mapping
	 * 
	 * @return &nbsp;Individual resource progress along with total goal progress for
	 *         each user
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> calculateProgressForEachRecipient(Map<String, Object> userToResourceProgress,
			Map<String, Object> resourceIdToMeta, List<String> goalSharedWithIds) {

		List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();

		for (String user : goalSharedWithIds) {

			Map<String, Object> progressForEachUser = new HashMap<String, Object>();
			progressForEachUser.put("shared_with", user);
			progressForEachUser.put("status", 1);

			List<Map<String, Object>> resourceProgressTracker = new ArrayList<Map<String, Object>>();
			Map<String, Object> resourceMap = (Map<String, Object>) userToResourceProgress.getOrDefault(user,
					new HashMap<>());
			int totalGoalDuration = 0;
			float totalGoalCompletion = 0.0F;

			if (!resourceIdToMeta.isEmpty()) {
				for (String resourceId : resourceIdToMeta.keySet()) {
					Map<String, Object> metaMap = (Map<String, Object>) resourceIdToMeta.get(resourceId);
					int contentDuration = (int) metaMap.get("duration");
					float contentProgress = (float) resourceMap.getOrDefault(resourceId, 0.0F);

					float completedTime = contentDuration * contentProgress;
					float remainingTime = contentDuration - completedTime;

					totalGoalDuration += contentDuration;
					totalGoalCompletion += completedTime;

					// Set up the required data to be returned
					Map<String, Object> resourceProgressData = new HashMap<String, Object>();
					resourceProgressData.put("contentType", metaMap.get("contentType"));
					resourceProgressData.put("mimeType", metaMap.get("mimeType"));
					resourceProgressData.put("duration", contentDuration);
					resourceProgressData.put("identifier", resourceId);
					resourceProgressData.put("name", metaMap.get("name"));
					resourceProgressData.put("progress", contentProgress);
					resourceProgressData.put("timeLeft", remainingTime);
					resourceProgressData.put("appIcon", metaMap.get("appIcon"));
					if (metaMap.containsKey("averageRating")) {
						resourceProgressData.put("averageRating", metaMap.get("averageRating"));
					}
					if (metaMap.containsKey("totalRating")) {
						resourceProgressData.put("totalRating", metaMap.get("totalRating"));
					}

					resourceProgressTracker.add(resourceProgressData);
				}
			}
			progressForEachUser.put("resource_progress_tracker", resourceProgressTracker);

			if (totalGoalDuration == 0) {
				// Just to avoid divide by zero exception
				totalGoalDuration = 1;
			}

			float goalCompletionPercentage = (totalGoalCompletion / totalGoalDuration);
			progressForEachUser.put("goal_progress", goalCompletionPercentage);

			finalList.add(progressForEachUser);
		}

		return finalList;
	}

	/**
	 * 
	 * @param uID             &nbsp;&nbsp;-&nbsp;&nbsp;UserId for user
	 * @param goalContentList &nbsp;&nbsp;-&nbsp;&nbsp;list of resource Ids against
	 *                        which the access/status needs to be checked
	 * @param statusData      &nbsp;&nbsp;-&nbsp;&nbsp;data to be returned to the
	 *                        calling method if required
	 * @param isDataRequired  &nbsp;&nbsp;-&nbsp;&nbsp;If true data will be
	 *                        populated in the given map, else exception will be
	 *                        thrown if certain conditions fail
	 * @param rootOrg         &nbsp;&nbsp;-&nbsp;&nbsp;root org
	 * @param status          &nbsp;&nbsp;-&nbsp;&nbsp;If any filter is required on
	 *                        status in meta while fetching
	 * @param source          &nbsp;&nbsp;-&nbsp;&nbsp;Array to limit the fields
	 *                        returned in meta
	 * @throws IOException 
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	private void checkForAccessAndRetiredStatus(List<String> uID, List<String> goalContentList,
			Map<String, Object> statusData, boolean isDataRequired, String rootOrg, String status, String[] source) throws IOException, ParseException{

		List<Map<String, Object>> metaData = contentService.getMetaByIDListandSource(goalContentList, source, status);

		if (!isDataRequired) {
			for (Map<String, Object> data : metaData) {
				if (data.get("contentType") != null) {
					if ("Channel".equalsIgnoreCase(data.get("contentType").toString())
							|| "Knowledge Board".equalsIgnoreCase(data.get("contentType").toString())
							|| "Learning Journeys".equalsIgnoreCase(data.get("contentType").toString())) {
						throw new InvalidDataInputException("invalid.content");
					}
				}
			}
		}
		if (metaData != null && !metaData.isEmpty()) {
			final String sbExtHost = props.getSbextServiceHost();
			final String sbExtPort = props.getSbextPort();

			List<Map<String, Object>> allContentResponse = new ArrayList<>();
			Map<String, Object> requestBody = new HashMap<>();
			for (String user : uID) {
				requestBody.put(user, goalContentList);
			}
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("request", requestBody);

			Map<String, Object> accessResponseData = restTemplate.postForObject(
					"http://" + sbExtHost + ":" + sbExtPort + "/accesscontrol/users/contents?rootOrg=" + rootOrg,
					requestMap, Map.class);

			Map<String, Object> result = (Map<String, Object>) accessResponseData.get("result");
			Map<String, Object> userAccessReponse = (Map<String, Object>) result.get("response");
			for (String user : userAccessReponse.keySet()) {
				Map<String, Object> data = new HashMap<>();
				data.put(user, userAccessReponse.get(user));

				allContentResponse.add(data);
			}

			Map<String, Object> contentMeta = new HashMap<>();
			for (Map<String, Object> data : metaData) {
				contentMeta.put(data.get("identifier").toString(), data);
			}
			for (Map<String, Object> eachUserData : allContentResponse) {
				String userId = (String) eachUserData.keySet().toArray()[0];
				Map<String, Object> accessData = (Map<String, Object>) eachUserData.get(userId);

				Map<String, Object> contentData = new HashMap<>();
				for (String content : goalContentList) {
					Map<String, Object> meta = new HashMap<>();
					boolean hasAccess = false;
					if (accessData.containsKey(content)) {
						hasAccess = (boolean) ((Map<String, Object>) accessData.get(content)).get("hasAccess");
					}
					if (contentMeta.containsKey(content)) {
						meta = (Map<String, Object>) contentMeta.get(content);

						if (!isDataRequired && !hasAccess) {
							throw new InvalidDataInputException("content.accessrestricted");
						} else if (!isDataRequired && meta.get("status").toString().equalsIgnoreCase("deleted")) {
							throw new InvalidDataInputException("content.deleted");
						} else if (!isDataRequired && meta.get("status").toString().equalsIgnoreCase("expired")) {
							throw new InvalidDataInputException("content.expired");
						} else if (meta.get("status").toString().equalsIgnoreCase("live")
								|| meta.get("status").toString().equalsIgnoreCase("marked for deletion")) {

							if (meta.containsKey("expiryDate") && meta.get("expiryDate") != null
									&& !meta.get("expiryDate").toString().isEmpty()) {
								SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
								Date expiryDate = formatterDateTime.parse(meta.get("expiryDate").toString());
								Date currentDate = new Date();

								if (!isDataRequired && expiryDate.before(currentDate)) {
									throw new InvalidDataInputException("content.expired");
								} else if (isDataRequired && expiryDate.before(currentDate)) {
									meta.put("status", "Expired");
								}
							}
						}
						meta.put("hasAccess", hasAccess);
					} else {
						if (!isDataRequired) {
							throw new InvalidDataInputException("invalid.LexId");
						}
						meta.put("identifier", content);
					}
					if (!meta.containsKey("hasAccess")) {
						meta.put("hasAccess", hasAccess);
					}
					contentData.put(content, meta);
				}
				statusData.put(userId, contentData);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkForAccessStatus(List<String> uID, List<String> goalContentList, Map<String, Object> statusData,
			String rootOrg) {

		final String sbExtHost = props.getSbextServiceHost();
		final String sbExtPort = props.getSbextPort();

		List<Map<String, Object>> allContentResponse = new ArrayList<>();
		Map<String, Object> requestBody = new HashMap<>();
		for (String user : uID) {
			requestBody.put(user, goalContentList);
		}
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("request", requestBody);

		Map<String, Object> accessResponseData = restTemplate.postForObject(
				"http://" + sbExtHost + ":" + sbExtPort + "/accesscontrol/users/contents?rootOrg=" + rootOrg,
				requestMap, Map.class);
		Map<String, Object> result = (Map<String, Object>) accessResponseData.get("result");
		Map<String, Object> userAccessReponse = (Map<String, Object>) result.get("response");
		for (String user : userAccessReponse.keySet()) {
			Map<String, Object> data = new HashMap<>();
			data.put(user, userAccessReponse.get(user));

			allContentResponse.add(data);
		}

		for (Map<String, Object> eachUserData : allContentResponse) {
			String userId = (String) eachUserData.keySet().toArray()[0];
			Map<String, Object> accessData = (Map<String, Object>) eachUserData.get(userId);

			boolean hasAccess = true;
			for (String content : goalContentList) {
				if (accessData.containsKey(content)) {
					hasAccess = (boolean) ((Map<String, Object>) accessData.get(content)).get("hasAccess");
				} else {
					hasAccess = false;
				}
				if (!hasAccess) {
					break;
				}
			}
			statusData.put(userId, hasAccess);
		}
	}

	/**
	 * gets the user preferred language
	 * 
	 * @param userId
	 * @param rootOrg
	 * @return
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private String fetchPreferredLanguage(String userId, String rootOrg) throws JsonMappingException, IOException {
		String language = null;
		try {
			Map<String, Object> preferences = userSvc.getUserPreferences(rootOrg, userId);
			if (preferences != null && !preferences.isEmpty()) {
				language = preferences.getOrDefault("selectedLangGroup", "en").toString();
			}
		} catch (Exception e) {
			language = "en";
		}
		return language;
	}

//	private List<CommonGoalProjection> getCommonGoalsByLanguage(List<CommonGoalProjection> commonGoals,
//			String language) {
//
//		List<CommonGoalProjection> requiredGoals = new ArrayList<>();
//		List<String> listOfGoalIds = new ArrayList<>();
//		for (CommonGoalProjection goal : commonGoals) {
//			if (goal.getLanguage().equalsIgnoreCase(language)) {
//				listOfGoalIds.add(goal.getId().toString());
//				requiredGoals.add(goal);
//			}
//		}
//		for (CommonGoalProjection goal : commonGoals) {
//			if (!listOfGoalIds.contains(goal.getId())) {
//				requiredGoals.add(goal);
//			}
//		}
//		return requiredGoals;
//	}

	/**
	 * This method is used to fetch the common goals by the precedence order of the
	 * languages chosen by the user.
	 * 
	 * @param commonGoals
	 * @param language
	 * @return
	 */
	private List<CommonGoalProjection> getCommonGoalsByLanguage(List<CommonGoalProjection> commonGoals,
			Set<String> languages) {

		List<CommonGoalProjection> requiredGoals = new ArrayList<>();
		List<String> listOfGoalIds = new ArrayList<>();

		for (String language : languages) {
			for (CommonGoalProjection goal : commonGoals) {
				if (!listOfGoalIds.contains(goal.getId().toString())) {
					if (goal.getLanguage().equalsIgnoreCase(language)) {
						listOfGoalIds.add(goal.getId().toString());
						requiredGoals.add(goal);
					}
				}
			}
		}
		return requiredGoals;
	}

	@Override
	public List<Map<String, Object>> getGoalGroups(String rootOrg, String userUUID, String language)
			throws JsonMappingException, IOException {

		Set<String> languages = new LinkedHashSet<>();
		if (language == null) {
			language = this.fetchPreferredLanguage(userUUID, rootOrg);
			if (language != null && !language.isEmpty()) {
				languages.addAll(Arrays.asList(language.split(",")));
			}
		} else {
			languages.addAll(Arrays.asList(language.split(",")));
		}
		languages.add("en");
		languages.remove("");
		languages.remove(null);

		List<GroupProjection> goalGroups = commonGoalsRepo.fetchAllCommonGoalGroups(rootOrg, new ArrayList<>(languages),
				1f);
		List<GroupProjection> finalGroups = this.filterGoalGroupsByLanguage(languages, goalGroups);

		List<Map<String, Object>> finalData = new ArrayList<>();
		for (GroupProjection group : finalGroups) {
			Map<String, Object> groupData = new HashMap<>();

			groupData.put("group_id", group.getGroupId());
			groupData.put("group_name", group.getGroupName());

			finalData.add(groupData);

		}
		return finalData;
	}

	/**
	 * This method is used to filter out the groups based on precedence order of the
	 * languages chosen by the user.
	 * 
	 * @param languages
	 * @param goalGroups
	 * @return
	 */
	private List<GroupProjection> filterGoalGroupsByLanguage(Set<String> languages, List<GroupProjection> goalGroups) {

		List<String> listOfGroupIds = new ArrayList<>();
		List<GroupProjection> requiredGroups = new ArrayList<>();
		for (String language : languages) {
			for (GroupProjection group : goalGroups) {
				if (!listOfGroupIds.contains(group.getGroupId())) {
					if (group.getLanguage().equalsIgnoreCase(language)) {
						listOfGroupIds.add(group.getGroupId());
						requiredGroups.add(group);
					}
				}
			}
		}
		return requiredGroups;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getSuggestedGoalsByGoalGroup(String rootOrg, String userUUID, String goalGroup,
			String language, List<String> metaFields) throws Exception {

		this.validateUser(rootOrg, userUUID);
		Map<String, Object> userCommonGoalsWithCount = this.processULGDataAndUSGData(userUUID, rootOrg);

		// fetch user preferred languages
		Set<String> languages = new LinkedHashSet<>();
		if (language == null) {
			language = this.fetchPreferredLanguage(userUUID, rootOrg);
			if (language != null && !language.isEmpty()) {
				languages.addAll(Arrays.asList(language.split(",")));
			}
		} else {
			languages.addAll(Arrays.asList(language.split(",")));
		}
		languages.add("en");
		languages.remove("");
		languages.remove(null);

		// fetch all common goals present in the system and for each common goal check
		// whether it has already been created by the user 2 times that is,one for self
		// and one for others initially all common goals will be ungrouped
		List<Map<String, Object>> processedGoals = new ArrayList<Map<String, Object>>();
		this.processCommonGoals(rootOrg, userCommonGoalsWithCount, processedGoals, languages, userUUID, goalGroup);

		Set<String> contentIds = new HashSet<>();
		for (Map<String, Object> commonGoal : processedGoals) {
			contentIds.addAll((List<String>) commonGoal.get("goalContentId"));
		}

		Map<String, Object> statusData = new HashMap<>();
		String[] source = new String[] { "contentType", "resourceType", "mimeType", "name", "identifier", "status",
				"duration", "appIcon", "description", "averageRating", "totalRating", "expiryDate" };

		if (metaFields != null && !metaFields.isEmpty()) {

			List<String> fieldsRequiredForProcessing = Arrays.asList(source);
			fieldsRequiredForProcessing.forEach(field -> {
				if (!metaFields.contains(field))
					metaFields.add(field);
			});

			source = metaFields.stream().toArray(String[]::new);
		}

		this.checkForAccessAndRetiredStatus(Arrays.asList(userUUID), new ArrayList<>(contentIds), statusData, true,
				rootOrg, null, source);
		Map<String, Object> contentMeta = new HashMap<>();
		if (!statusData.isEmpty()) {
			contentMeta = (Map<String, Object>) statusData.get(userUUID);
		}

		String groupName = this.getGroupNameAccordingToUserChosenLanguage(processedGoals, languages);

		List<Map<String, Object>> filteredGoals = new ArrayList<>();
		for (Map<String, Object> eachGoal : processedGoals) {

			if (Boolean.parseBoolean(eachGoal.get("createdForSelf").toString()) == true
					&& Boolean.parseBoolean(eachGoal.get("createdForOthers").toString()) == true) {
				continue;
			} else {
				List<String> goalContentIds = (List<String>) eachGoal.get("goalContentId");
				List<Map<String, Object>> resources = new ArrayList<>();
				for (String eachContent : goalContentIds) {
					if (contentMeta.containsKey(eachContent)) {
						Map<String, Object> meta = (Map<String, Object>) contentMeta.get(eachContent);

						Map<String, Object> metaForContentWithRating = new HashMap<>();
						metaForContentWithRating.putAll(meta);

						if (metaForContentWithRating.containsKey("averageRating")) {
							Float avgRating = 0f;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("averageRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									avgRating = Float.parseFloat(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("averageRating", avgRating);
						}
						if (metaForContentWithRating.containsKey("totalRating")) {
							Integer totalRating = 0;
							Map<String, Object> rating = (Map<String, Object>) (metaForContentWithRating
									.getOrDefault("totalRating", new HashMap<>()));
							if (!rating.isEmpty()) {
								if (rating.containsKey(rootOrg)) {
									totalRating = Integer.parseInt(rating.get(rootOrg).toString());
								}
							}
							metaForContentWithRating.put("totalRating", totalRating);
						}
						resources.add(metaForContentWithRating);
					}
				}
				eachGoal.put("resources", resources);

				filteredGoals.add(eachGoal);
			}
		}

		Map<String, Object> finalData = new HashMap<>();
		finalData.put("group_id", goalGroup);
		finalData.put("group_name", groupName);
		finalData.put("goals", filteredGoals);
		return finalData;
	}

	/**
	 * This method is used to fetch a group name based on precedence of languages
	 * chosen by the user from a list of goals belonging to that group which can be
	 * in multiple languages.
	 * 
	 * @param goals
	 * @param languages
	 * @return
	 */
	private String getGroupNameAccordingToUserChosenLanguage(List<Map<String, Object>> goals, Set<String> languages) {

		Map<Integer, String> langPrecedenceMap = new HashMap<>();
		int count = 1;
		for (String lang : languages) {
			langPrecedenceMap.put(count, lang);
			count++;
		}
		List<Integer> precedenceWeightage = new ArrayList<>(langPrecedenceMap.keySet());
		String groupName = "";
		for (Integer i : precedenceWeightage) {
			boolean found = false;
			for (Map<String, Object> eachGoal : goals) {
				if (eachGoal.get("language").toString().equalsIgnoreCase(langPrecedenceMap.get(i))) {
					groupName = eachGoal.get("groupName").toString();
					found = true;
					break;
				}
			}
			if (found)
				break;
		}
		return groupName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllCommonGoals(String rootOrg) {

		List<CommonGoalProjection> commonGoals = commonGoalsRepo.fetchAllCommonGoalsByRootOrg(rootOrg);
		List<Map<String, Object>> result = new ArrayList<>();
		for (CommonGoalProjection goal : commonGoals) {
			Map<String, Object> goalMap = new ObjectMapper().convertValue(goal, Map.class);

			List<String> goalContentId = goalMap.get("goalContentId") == null
					|| !(goalMap.get("goalContentId").toString().isEmpty())
							? (Arrays.asList(goalMap.get("goalContentId").toString().split(",")))
							: null;
			goalMap.put("goalContentId", goalContentId);
			goalMap.put("version", Float.parseFloat(goal.getVersion()));
			result.add(goalMap);
		}
		return result;
	}

	// ------------------------------------------------------------------------------------------------------------------------
	// ----------------------------------------VALIDATION
	// METHODS--------------------------------------------------------------

	private void validateUser(String rootOrg, String userId) throws Exception {
		if (!userService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

	}

	private Map<String, Object> verifyUsers(String rootOrg, List<String> uuids) {
		return userService.validateAndFetchNewUsers(rootOrg, uuids);
	}

	private Map<String, Object> getEmailsFromUUIDs(String rootOrg, List<String> uuids) {
		return userService.getUserEmailsFromUserIds(rootOrg, uuids);
	}

	private String getEmailFromUUID(String rootOrg, String uuid) throws Exception{
		return userService.getUserEmailFromUserId(rootOrg, uuid);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMultipleUserData(String rootOrg, List<String> uuids) {

		Map<String, Object> result = new HashMap<>();

		if (uuids == null || uuids.isEmpty())
			return result;

		result = userService.getUsersDataFromUserIds(rootOrg, uuids,
				new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL)));

		for (String userId : result.keySet()) {
			Map<String, Object> userData = (Map<String, Object>) result.get(userId);

			userData.put("name", this.getUserDisplayName(userData));
			userData.put("userId", userId);
			userData.remove(PIDConstants.FIRST_NAME);
			userData.remove(PIDConstants.LAST_NAME);
			userData.remove(PIDConstants.UUID);

			result.put(userId, userData);
		}

		return result;
	}

	private String getUserDisplayName(Map<String, Object> userData) {

		String name = "";
		if (userData.get(PIDConstants.FIRST_NAME) != null
				&& !(userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty()
				&& userData.get(PIDConstants.LAST_NAME) != null
				&& !(userData.get(PIDConstants.LAST_NAME)).toString().isEmpty()) {

			name = userData.get(PIDConstants.FIRST_NAME) + " " + userData.get(PIDConstants.LAST_NAME);

		} else if (userData.get(PIDConstants.FIRST_NAME) != null
				&& !(userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty()
				&& (userData.get(PIDConstants.LAST_NAME) == null
						|| (userData.get(PIDConstants.LAST_NAME)).toString().isEmpty())) {

			name = (userData.get(PIDConstants.FIRST_NAME)).toString();

		} else if ((userData.get(PIDConstants.FIRST_NAME) == null
				|| (userData.get(PIDConstants.FIRST_NAME)).toString().isEmpty())
				&& userData.get(PIDConstants.LAST_NAME) != null
				&& !(userData.get(PIDConstants.LAST_NAME)).toString().isEmpty()) {
			name = (userData.get(PIDConstants.LAST_NAME)).toString();
		}
		return name;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getUserData(String rootOrg, String uuid) {
		Map<String, Object> result = userService.getUserDataFromUserId(rootOrg, uuid,
				new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL)));

		Map<String, Object> userData = new HashMap<>();
		if (result == null || result.isEmpty()) {
			return userData;
		}
		if (((Map<String, Object>) result.get(uuid)) == null || ((Map<String, Object>) result.get(uuid)).isEmpty()) {
			return userData;
		}

		userData = (Map<String, Object>) result.get(uuid);
		userData.put("name", this.getUserDisplayName(userData));
		userData.put("userId", uuid);
		userData.remove(PIDConstants.FIRST_NAME);
		userData.remove(PIDConstants.LAST_NAME);
		userData.remove(PIDConstants.UUID);

		return userData;
	}
}