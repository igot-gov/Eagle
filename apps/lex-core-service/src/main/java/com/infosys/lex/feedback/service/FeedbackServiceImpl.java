/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.common.bodhi.repo.AppConfig;
import com.infosys.lex.common.bodhi.repo.AppConfigPrimaryKey;
import com.infosys.lex.common.bodhi.repo.AppConfigRepository;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.AccessForbidenError;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.exception.ResourceNotFoundException;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.feedback.dto.Feedback;
import com.infosys.lex.feedback.dto.FeedbackSearchDTO;
import com.infosys.lex.feedback.dto.FeedbackSubmitDTO;
import com.infosys.lex.feedback.repo.FeedbackCRUD;
import com.infosys.lex.userroles.service.UserRolesService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	private LexLogger logger = new LexLogger(getClass().getName());
	private Map<String, Object> rolesType = new HashMap<>();
	private List<String> rolesList = new ArrayList<>();
	private Map<String, Object> features = new HashMap<>();

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	UserUtilityService userUtilityService;

	@Autowired
	FeedbackCRUD feedbackCRUD;

	@Autowired
	AppConfigRepository appConfig;

	@Autowired
	ContentService contentService;

	@Autowired
	UserRolesService rolesService;

	@Autowired
	LexServerProperties serviceConfig;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	public FeedbackServiceImpl() {

		rolesType.put("platform_feedback", "platform-feedback-admin");
		rolesType.put("content_request", "content-request-admin");
		rolesType.put("service_request", "service-request-admin");
		rolesType.put("content_feedback", "author");
		rolesType.put("platform-feedback-admin", "platform_feedback");
		rolesType.put("content-request-admin", "content_request");
		rolesType.put("service-request-admin", "service_request");
		rolesType.put("author", "content_feedback");
		rolesType.put("content-feedback-admin", "content_feedback");
		features.put("content_feedback", true);
		features.put("author", true);
		features.put("platform_feedback", true);
		features.put("platform-feedback-admin", true);

		rolesList = Arrays.asList("privileged", "platform-feedback-admin", "content-request-admin",
				"service-request-admin", "author");
	}

	public static final String CONTENT_NAME = "name";
	public static final String CONTENT_TYPE = "contentType";
	public static final String CREATOR_CONTACTS = "creatorContacts";
	public static final String CREATOR_DETAILS = "creatorDetails";

	public static final String STATUS = "status";
	private static AtomicInteger atomicInteger = new AtomicInteger();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> submitFeedback(FeedbackSubmitDTO feedbackSubmitDTO, String rootOrg, String role)
			throws Exception {
		Map<String, Object> result = new HashMap<>();
		String feedbackId = generateFeedbackId();
		String rootFeedbackId = feedbackId;
		String contentName = "";
		String contentType = "";
		String status = "";
		Integer count = 0;
		String feedbackCategory = feedbackSubmitDTO.getFeedbackCategory() == null ? ""
				: feedbackSubmitDTO.getFeedbackCategory();
		String feedbackSentiment = feedbackSubmitDTO.getFeedbackSentimentCategory() == null ? ""
				: feedbackSubmitDTO.getFeedbackSentimentCategory();
		String rootFeedbackText = "";
		String contentId = feedbackSubmitDTO.getContentId() == null ? "" : feedbackSubmitDTO.getContentId();
		String feedbackText = feedbackSubmitDTO.getFeedbackText();

//		notification request
		Map<String, Object> notification = new HashMap<>();
		Map<String, Object> tagValuePair = new HashMap<>();
		Map<String, Object> recipients = new HashMap<>();
		Map<String, Object> targetData = new HashMap<>();

		List<String> learner = new ArrayList<>();

		String rootFeedbackBy = "";

		Boolean newThread = true;
		Boolean replied = false;
		Boolean create = false;
		Boolean update = true;

		List<Map<String, Object>> content = new ArrayList<>();
		Set<String> authors = new HashSet<>();
		Map<String, Object> updateMap = new HashMap<>();

		if (feedbackText.length() > 2000) {
			throw new InvalidDataInputException("Feedback.text.length_out_of_range");
		}

		if (!feedbackSubmitDTO.getFeedbackType().equals("content_feedback") && !contentId.isEmpty()) {
			throw new InvalidDataInputException("Content.id.not_allowed here");
		}

		AppConfig config = appConfig.findById(new AppConfigPrimaryKey(rootOrg, "feedbackSentimentMode")).orElse(null);

		if (config != null && !Boolean.parseBoolean(config.getValue()) && !feedbackSentiment.isEmpty()) {
			throw new InvalidDataInputException("feedback.sentiment.not_allowed here");
		}

//		verify UserId
		if (!userUtilityService.validateUser(rootOrg, feedbackSubmitDTO.getUserId())) {
			throw new BadRequestException("Invalid User : " + feedbackSubmitDTO.getUserId());
		}

		logger.info("feedback-Submit feedbackSubmitDTO below: ");
		logger.info(mapper.writeValueAsString(feedbackSubmitDTO));

//		verify ContentId
		if (contentId != "") {
			content = contentService.getMetaByIDListandSource(new ArrayList<String>(Arrays.asList(contentId)),
					new String[] { CREATOR_DETAILS, CREATOR_CONTACTS, CONTENT_NAME, CONTENT_TYPE, STATUS }, null);

			if (content.size() != 1) {
				throw new ResourceNotFoundException("No such content Found With Content Id : " + contentId);
			}
			contentName = content.get(0).get(CONTENT_NAME).toString();
			contentType = content.get(0).get(CONTENT_TYPE).toString();
			status = content.get(0).get(STATUS).toString();

			tagValuePair.put("#contentType", contentType);
			tagValuePair.put("#contentTitle", contentName);

			for (Map<String, Object> creators : (List<Map<String, Object>>) content.get(0).get(CREATOR_CONTACTS)) {
				try {
					UUID.fromString(creators.get("id").toString());
					authors.add(creators.get("id").toString());
				} catch (Exception e) {
					continue;
				}
			}

			for (Map<String, Object> creators : (List<Map<String, Object>>) content.get(0).get(CREATOR_DETAILS)) {
				try {
					UUID.fromString(creators.get("id").toString());
					authors.add(creators.get("id").toString());
				} catch (Exception e) {
					continue;
				}
			}

		}

//		Check if Update To Parent is Required
		if (feedbackSubmitDTO.getRootFeedbackId() != null) {
			newThread = false;
			rootFeedbackId = feedbackSubmitDTO.getRootFeedbackId();
//			Optional Validation of Request
//			Get Root Feedback Text and Reply Logic

			Map<String, Object> replying_data = validate(feedbackSubmitDTO, rootOrg, role, count);
			replied = (Boolean) replying_data.get("replied");
			rootFeedbackText = (String) replying_data.get("rootText");
			rootFeedbackBy = (String) replying_data.get("rootFeedbackBy");

		}

		// Special Status reply Logic
		if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("live") && newThread) {
			throw new InvalidDataInputException("content.status.invalid");
		}

		try {
			Feedback feedback = new Feedback(rootOrg, feedbackId, rootFeedbackId, feedbackSubmitDTO.getFeedbackText(),
					feedbackSubmitDTO.getFeedbackType(), "", feedbackSubmitDTO.getUserId(), new ArrayList<>(authors), contentId,
					contentName, contentType, feedbackSentiment, false, false);
			create = feedbackCRUD.createThread(feedback);
			notification.put("event-id", "content_feedback");
			tagValuePair.put("#feedback", feedbackText);
			targetData.put("viewingAs", "author");
			targetData.put("feedbackType", "content_feedback");

			if (!newThread) {
				updateMap.put("lastActivityOn", feedback.getLastActivityOn());
				updateMap.put("seenReply", false);
				updateMap.put("replied", replied);
				updateMap.put("replyCount", count + 1);
				updateMap.put("feedbackCategory", feedbackCategory);
				update = feedbackCRUD.updateThread(rootFeedbackId, updateMap);

				notification.put("event-id", "content_feedback_response");
				tagValuePair.put("#response", feedbackText);
				tagValuePair.put("#feedback", rootFeedbackText);
				targetData.put("viewingAs", "user");
				targetData.put("feedbackType", "content_feedback");

			}
			targetData.put("feedbackId", rootFeedbackId);
			String user = newThread ? feedback.getFeedbackBy() : rootFeedbackBy;
			learner.add(user);

			if (create && update) {
				result = feedback.toMap();
				Map<String, Object> userObj = (Map<String, Object>) userUtilityService
						.getUserDataFromUserId(rootOrg, feedback.getFeedbackBy(),
								Arrays.asList(PIDConstants.EMAIL, PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME))
						.get(feedback.getFeedbackBy());
				String name = userObj.get(PIDConstants.LAST_NAME) == null ? userObj.get(PIDConstants.FIRST_NAME) + ""
						: (userObj.get(PIDConstants.FIRST_NAME) + " " + userObj.get(PIDConstants.LAST_NAME));
				userObj.put("name", name);
				userObj.put("userId", userObj.get(PIDConstants.UUID));
				userObj.remove(PIDConstants.UUID);
				userObj.remove(PIDConstants.FIRST_NAME);
				userObj.remove(PIDConstants.LAST_NAME);
				result.put("feedbackBy", userObj);
			}

			// sending Notification
			notification.put("tag-value-pair", tagValuePair);
			recipients.put("learner", learner);
			recipients.put("author", authors);
			notification.put("recipients", recipients);
			notification.put("target-data", targetData);
			HttpHeaders headers = new HttpHeaders();
			headers.set("rootOrg", rootOrg);
			String url = "http://" + lexServerProps.getNotifIp() + ":" + lexServerProps.getNotifPort()
					+ "/v1/notification/event";
			headers.set("rootOrg", rootOrg);
			try {
//				System.out.println(notification);
//				System.out.println(url);
//				System.out.println(headers);
				if (feedbackSubmitDTO.getFeedbackType().toLowerCase().equals("content_feedback")) {
					restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<Object>(notification, headers),
							Void.class);
				}
			} catch (Exception e) {
				// do nothing
			}

			logger.info("feedback-submit result below: ");
			logger.info(mapper.writeValueAsString(result));
			return result;
		} catch (Exception e) {
			throw new ApplicationLogicError("Failed While Updating Database", e);
		}
	}

	private Map<String, Object> validate(FeedbackSubmitDTO feedbackSubmitDTO, String rootOrg, String role,
			Integer count) throws Exception {

		// validate

		Map<String, Object> result = new HashMap<>();

		String userId = feedbackSubmitDTO.getUserId();
		String rootFeedbackId = feedbackSubmitDTO.getRootFeedbackId();
		String feeedbackCategory = feedbackSubmitDTO.getFeedbackCategory();

		Feedback rootFeedback = feedbackCRUD.fetchThread(rootFeedbackId);
		result.put("rootText", rootFeedback.getFeedbackText());
		result.put("rootFeedbackBy", rootFeedback.getFeedbackBy());

		count = rootFeedback.getReplyCount();

		if (rootFeedbackId != null && !rootFeedbackId.isEmpty()) {

			if (!rootFeedbackId.equals(rootFeedback.getFeedbackId())) {
				throw new InvalidDataInputException(rootFeedbackId + " this rootFeedbackId is Invalid");
			}

			if (!((!"user".equals(role) && validateRole(rootOrg, userId, rootFeedback))
					|| ("user".equals(role) && rootFeedback.getFeedbackBy().equals(userId)))) {
				throw new InvalidDataInputException(
						"User : " + userId + " not Authorized to reply back on this content as a " + role);
			}

		}

		if (feeedbackCategory != null && !feeedbackCategory.isEmpty()) {
			List<String> feedbackCategories = new ArrayList<>();
			AppConfig apModel = appConfig.findById(new AppConfigPrimaryKey(rootOrg, "feedbackCategories")).get();
			try {
				feedbackCategories = Arrays.asList(apModel.getValue().split(","));
				if (!feedbackCategories.contains(feeedbackCategory.toLowerCase())) {
					throw new InvalidRequestException("Feedback Category must be one of these : " + feedbackCategories);
				}
			} catch (Exception e) {
				throw new ApplicationLogicError("FeedBack Categories Not Found in Database", e);
			}
		}

		AppConfig config = appConfig.findById(new AppConfigPrimaryKey(rootOrg, "feedbackAllowSelfReply")).orElse(null);
		if (config != null && Boolean.parseBoolean(config.getValue())) {
			result.put("replied", !"user".equals(role));
		} else {
			result.put("replied", (!(rootFeedback.getFeedbackBy().equals(userId))));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchFeedbacks(String feedbackId, String userId, String rootOrg) throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
		// verify UserId
		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		List<Feedback> feedbacks = feedbackCRUD.fetchThreads(rootOrg, feedbackId);

		// Produce Results
		Set<String> uuidstoFetch = new HashSet<>();

		for (Feedback hit : feedbacks) {
			uuidstoFetch.add(hit.getFeedbackBy());
		}

		Map<String, Object> userData = (Map<String, Object>) userUtilityService.getUsersDataFromUserIds(rootOrg,
				new ArrayList<String>(uuidstoFetch),
				Arrays.asList(PIDConstants.EMAIL, PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.UUID));
		for (String key : userData.keySet()) {
			Map<String, Object> userObj = (Map<String, Object>) userData.get(key);
			String name = userObj.get(PIDConstants.LAST_NAME) == null ? userObj.get(PIDConstants.FIRST_NAME) + ""
					: (userObj.get(PIDConstants.FIRST_NAME) + " " + userObj.get(PIDConstants.LAST_NAME));
			userObj.put("name", name);
			userObj.put("userId", userObj.get(PIDConstants.UUID));
			userObj.remove(PIDConstants.UUID);
			userObj.remove(PIDConstants.FIRST_NAME);
			userObj.remove(PIDConstants.LAST_NAME);
		}

		for (Feedback hit : feedbacks) {
			Map<String, Object> record = hit.toMap();
			record.put("feedbackBy", userData.get(hit.getFeedbackBy()));
			result.add(record);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> searchFeedback(FeedbackSearchDTO feedbackSearchDTO, String rootOrg) throws Exception {

		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> hits = new ArrayList<>();

		Set<String> roles = rolesService.getUserRoles(rootOrg, feedbackSearchDTO.getUserId());
		if (roles.contains("super-admin")) {
			roles.add("platform-feedback-admin");
			roles.add("service-request-admin");
			roles.add("content-request-admin");
		}

		// System.out.println(roles);

		if (!("user".equals(feedbackSearchDTO.getViewedBy())) && !roles.contains(feedbackSearchDTO.getViewedBy())) {
			throw new AccessForbidenError("The User : " + feedbackSearchDTO.getUserId() + " does not have this role "
					+ feedbackSearchDTO.getViewedBy());
		}

		// Search Threads
		if ((roles.contains("super-admin") || roles.contains("content-feedback-admin"))
				&& "author".equals(feedbackSearchDTO.getViewedBy().toLowerCase())) {
			feedbackSearchDTO.setViewedBy("content-feedback-admin");
		}

		Map<String, Object> response = feedbackCRUD.searchThreads(feedbackSearchDTO, rootOrg);
		List<Feedback> feedbacks = (List<Feedback>) response.get("hits");

		// Produce Results
		Set<String> uuidstoFetch = new HashSet<>();

		for (Feedback hit : feedbacks) {
			uuidstoFetch.add(hit.getFeedbackBy());
		}
		if (!uuidstoFetch.isEmpty()) {
			Map<String, Object> userData = (Map<String, Object>) userUtilityService.getUsersDataFromUserIds(rootOrg,
					new ArrayList<String>(uuidstoFetch), Arrays.asList(PIDConstants.EMAIL, PIDConstants.FIRST_NAME,
							PIDConstants.LAST_NAME, PIDConstants.UUID));

			for (String key : userData.keySet()) {

				Map<String, Object> userObj = (Map<String, Object>) userData.get(key);
				String name = userObj.get(PIDConstants.LAST_NAME) == null ? userObj.get(PIDConstants.FIRST_NAME) + ""
						: (userObj.get(PIDConstants.FIRST_NAME) + " " + userObj.get(PIDConstants.LAST_NAME));
				userObj.put("name", name);
				userObj.put("userId", userObj.get(PIDConstants.UUID));
				userObj.remove(PIDConstants.UUID);
				userObj.remove(PIDConstants.FIRST_NAME);
				userObj.remove(PIDConstants.LAST_NAME);
			}

			for (Feedback hit : feedbacks) {
				Map<String, Object> record = hit.toMap();
				record.put("feedbackBy", userData.get(hit.getFeedbackBy()));
				hits.add(record);
			}
		}
		result.put("result", hits);
		result.put("hits", response.get("totalHits"));
		return result;
	}

	private String generateFeedbackId() {
		Random random = new Random();
		int environmentId = random.nextInt(99999);
		long env = (environmentId + random.nextInt(99999)) / 10000000;
		long uid = System.currentTimeMillis() + random.nextInt(999999);
		uid = uid << 13;
		return "fed_" + env + "" + uid + "" + atomicInteger.getAndIncrement();
	}

	@Override
	public Map<String, Object> fetchLatestFeedbacksReport(String userId, String rootOrg) throws Exception {
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> summary = new ArrayList<>();

		// System.out.println(appConfig);
		List<Map<String, Object>> configs = appConfig.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(rootOrg,
				Arrays.asList(new String[] { "content_request_active", "service_request_active" }));

		features.put("content-request-admin", Boolean.parseBoolean(configs.get(0).get("value").toString()));
		features.put("service-request-admin", Boolean.parseBoolean(configs.get(1).get("value").toString()));

		// fetch Roles
		Set<String> roles = rolesService.getUserRoles(rootOrg, userId);

		if (roles.contains("super-admin")) {
			roles.add("platform-feedback-admin");
			roles.add("service-request-admin");
			roles.add("content-request-admin");
		}

		logger.info("feedback-summary appconfig below: ");
		logger.info(mapper.writeValueAsString(configs));
		logger.info("feedback-summary roles below: ");
		logger.info(mapper.writeValueAsString(roles));
		// System.out.println(roles);

		Long totalNotSeen = 0l;

		try {
			@SuppressWarnings("unused")
			Long total = 0l;
			Long seen = 0l;

			for (String role : rolesList) {
				Map<String, Object> roleSummary = new HashMap<>();
				Map<String, Object> counts = new HashMap<>();
				if (role.toLowerCase().equals("privileged")) {

					roleSummary.put("role", "user");
					roleSummary.put("enabled", true);
					roleSummary.put("hasAccess", true);

					counts = feedbackCRUD.fetchHits(userId, "", 0);
					total = (Long) counts.get("total");
					seen = (Long) counts.get("notSeen");

					roleSummary.put("totalCount", counts.get("total"));
					roleSummary.put("forActionCount", counts.get("notSeen"));

				} else {

					roleSummary.put("role", role);
					roleSummary.put("enabled", features.get(role));
					Boolean hasAccess = roles.contains(role);
					roleSummary.put("hasAccess", roles.contains(role));
					if (hasAccess) {
						if ("author".equals(role)
								&& (roles.contains("super-admin") || roles.contains("content-feedback-admin"))) {
							counts = feedbackCRUD.fetchHits(userId, rolesType.get(role).toString(), -1);
						} else {
							counts = feedbackCRUD.fetchHits(userId, rolesType.get(role).toString(), 1);
						}
						total = (Long) counts.get("total");
						seen = (Long) counts.get("notSeen");
						roleSummary.put("totalCount", counts.get("total"));
						roleSummary.put("forActionCount", counts.get("notSeen"));
					} else {
						total = 0l;
						seen = 0l;
						roleSummary.put("totalCount", 0);
						roleSummary.put("forActionCount", 0);

					}
				}

				totalNotSeen += seen;

				summary.add(roleSummary);
			}

			result.put("roles", summary);
			result.put("forActionCount", totalNotSeen);

		} catch (Exception e) {
			throw new ApplicationLogicError("Data Missing in Database .pls Check RootOrg.", e);
		}
		logger.info("feedback-summary result below: ");
		logger.info(mapper.writeValueAsString(result));
		return result;
	}

	@Override
	public Map<String, Object> updateStatus(String userId, String feedbackId, String rootOrg, String category)
			throws Exception {

//		verify UserId
		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		Map<String, Object> updateMap = new HashMap<>();

// 		check valid FeedbackId
		Feedback feedback = feedbackCRUD.fetchThread(feedbackId);

		if (feedback == null) {
			throw new ResourceNotFoundException("No such content Found With Feedback Id : " + feedbackId);
		}

		if (!(feedback.getFeedbackBy().equals(userId) || validateRole(rootOrg, userId, feedback))) {
			throw new AccessForbidenError("The user is Not Authorised to Update This Content");
		}

		if (!"un_defined".equals(category)) {
			if ("none".equals(category.toLowerCase())) {
				updateMap.put("feedbackCategory", "");
			} else {
				updateMap.put("feedbackCategory", category);
			}

		}

		try {
			updateMap.put("seenReply", true);
			feedbackCRUD.updateThread(feedbackId, updateMap);
			return feedbackCRUD.fetchThread(feedbackId).toMap();
		} catch (Exception e) {
			throw new ApplicationLogicError("Update Failed", e);
		}
	}

	private Boolean validateRole(String rootOrg, String userId, Feedback feedback) throws Exception {

		Set<String> roles = rolesService.getUserRoles(rootOrg, userId);
		if (roles.contains("super-admin")) {
			return true;
		} else if ("content_feedback".equals(feedback.getFeedbackType())) {
			if (feedback.getAssignedTo().contains(userId)) {
				return true;
			} else {
				return roles.contains("content-feedback-admin");
			}
		} else {
			return roles.contains(rolesType.get(feedback.getFeedbackType()));
		}
	}

	@Override
	public Map<String, Object> getFeedbackCategory(String rootOrg) throws Exception {
		Map<String, Object> result = new HashMap<>();
		List<String> typesList = new ArrayList<>(
				Arrays.asList("content_feedback", "platform_feedback", "service_request", "content_request"));

		List<Map<String, Object>> configs = appConfig.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(rootOrg,
				Arrays.asList(new String[] { "feedbackCategories", "feedbackSentimentMode", "content_request_active",
						"service_request_active" }));

		try {
			for (Map<String, Object> config : configs) {

				if (config.get("value").toString().contains(",")) {
					result.put((String) config.get("key"), config.get("value").toString().split(","));
				} else {
					result.put((String) config.get("key"), Boolean.parseBoolean(config.get("value").toString()));
				}
			}

			if (result.get("content_request_active") != null && !((boolean) result.get("content_request_active"))) {
				typesList.remove("content_request");
			}

			if (result.get("service_request_active") != null && !((boolean) result.get("service_request_active"))) {
				typesList.remove("service_request");
			}
		} catch (Exception e) {
			throw new ApplicationLogicError("Data Missing in Database .pls Check RootOrg.", e);
		}
		result.remove("content_request_active");
		result.remove("service_request_active");

		result.put("feedbackType", typesList);

		return result;
	}

	@Override
	public Map<String, Object> feedbackDump(String rootOrg, Long startDate, Long endDate, Integer size, String scrollId)
			throws IOException {
		return feedbackCRUD.paginatedDump(rootOrg, startDate, endDate, size, scrollId);
	}

}
