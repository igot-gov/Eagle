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

package com.infosys.lex.notification.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.bodhi.repository.UserNotificationRepository;
import com.infosys.lex.notification.dto.InAppNotificationRequest;
import com.infosys.lex.notification.dto.NotificationEvent;
import com.infosys.lex.notification.dto.NotificationSendDTO;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.model.cassandra.UserNotification;
import com.infosys.lex.notification.model.cassandra.UserNotificationPrimaryKey;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.repository.RecipientTagsRepository;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.service.UserNotificationsService;
import com.infosys.lex.notification.util.LexNotificationLogger;

@Service
public class UserNotificationsSeviceImpl implements UserNotificationsService {

	@Autowired
	UserNotificationRepository userNotificationRepo;

	@Autowired
	UserInformationService userInfoSvc;

	@Autowired
	RecipientTagsRepository classificationRepo;

//	@Autowired
//	NotificationClassificationRepository classificationRepo_test;

	@Autowired
	ApplicationServerProperties notificationProps;

	private ObjectMapper mapper = new ObjectMapper();

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	@Override
	public void markAsSeen(String rootOrg, String userId, String notificationId) throws Exception {

		if (!userInfoSvc.validateUser(rootOrg, userId))
			throw new InvalidDataInputException("invalid.user", null);

		List<UserNotification> userNotifications = userNotificationRepo.fetchAllUserNotifications(rootOrg, userId,
				Arrays.asList("Information", "Action"), UUID.fromString(notificationId));

		if (userNotifications == null || userNotifications.isEmpty())
			throw new InvalidDataInputException("Invalid notification", null);

		if (userNotifications.size() > 1)
			throw new ApplicationLogicException("Corrupted notification");

		// only one notification should exist per notification id.
		UserNotification userNotification = userNotifications.get(0);

		String classification = userNotification.getPrimaryKey().getClassifiedAs();

		// if record is already marked then do not update
		if (userNotification.getSeen() == null || !userNotification.getSeen())
			userNotificationRepo.markAsSeen(new Timestamp(new Date().getTime()), userId,
					UUID.fromString(notificationId), classification, rootOrg);
	}

	@Override
	public NotificationSendDTO getAllNotifications(String rootOrg, String userId, String page, Integer size,
			String classification, Boolean seen) throws Exception {

		if (seen != null)
			return userNotificationRepo.getNotificationsBySeen(rootOrg, userId, page, size, classification, seen);
		else
			return userNotificationRepo.getAllNotifications(rootOrg, userId, page, size, classification);
	}

	@Override
	public void sendInAppNotifications(NotificationEvent notificationEvent,
			List<InAppNotificationRequest> inAppRequests) {

		Map<String, String> classificationPerRecipient = new HashMap<>();

		classificationRepo
				.getNotificationClassificationByEventId(notificationEvent.getEventId(),
						new ArrayList<String>(notificationEvent.getRecipients().keySet()))
				.forEach(notificationClassification -> classificationPerRecipient.put(
						notificationClassification.getTagsPrimaryKey().getRecipient(),
						notificationClassification.getClassification()));

		List<UserNotification> userNotificationDigests = new ArrayList<>();

		for (InAppNotificationRequest inAppRequest : inAppRequests) {

			String targetData = "";
			if (notificationEvent.getTargetData() != null && !notificationEvent.getTargetData().isEmpty()) {
				try {
					targetData = mapper.writeValueAsString(notificationEvent.getTargetData());
				} catch (JsonProcessingException e) {
					logger.error("could not process target data " + notificationEvent.getTargetData().toString());
				}
			}

			UserNotification userNotification = new UserNotification(
					new UserNotificationPrimaryKey(notificationEvent.getRootOrg(), inAppRequest.getUserId(),
							classificationPerRecipient.get(inAppRequest.getRecipientRole()), UUIDs.timeBased()),
					notificationEvent.getEventId(), null, inAppRequest.getBody(), inAppRequest.getSubject(),
					inAppRequest.getRecipientRole(), new Date(), false, null, targetData);

			userNotificationDigests.add(userNotification);
		}
		userNotificationRepo.saveAll(userNotificationDigests);
	}

	@Override
	public Map<String, Object> getCountOfAllUnSeenNotifications(String rootOrg, String userId) throws Exception {

		Map<String, Object> returnMap = new HashMap<>();

		Map<String, Object> unseenActionNotifCountMap = userNotificationRepo.fetchUnSeenNotifCount(rootOrg, userId,
				"Action");
		int actionCount = Integer.parseInt(unseenActionNotifCountMap.get("count").toString());

		Map<String, Object> unseenInformationNotifCount = userNotificationRepo.fetchUnSeenNotifCount(rootOrg, userId,
				"Information");
		int informationCount = Integer.parseInt(unseenInformationNotifCount.get("count").toString());

		returnMap.put("actionCount", actionCount);
		returnMap.put("informationCount", informationCount);

		int totalCount = actionCount + informationCount;
		returnMap.put("totalCount", totalCount);

		return returnMap;
	}

	@Override
	public void markAllAsRead(String rootOrg, String userId, String classification, List<String> notificationIds)
			throws Exception {

		if (!userInfoSvc.validateUser(rootOrg, userId))
			throw new InvalidDataInputException("invalid.user", null);

		if (notificationIds == null) {
			if (!classification.equals("all") && !Arrays.asList("Action", "Information").contains(classification))
				throw new InvalidDataInputException("invalid.classification", null);

			if (classification.equalsIgnoreCase("all")) {
				this.markNotifAsRead(rootOrg, userId, "Action");
				this.markNotifAsRead(rootOrg, userId, "Information");
			} else {
				this.markNotifAsRead(rootOrg, userId, classification);
			}
		} else {

			List<UUID> notifs = new ArrayList<>();
			for (String notifId : notificationIds) {
				try {
					notifs.add(UUID.fromString(notifId));
				} catch (Exception e) {
					throw new InvalidDataInputException("invalid.notificationId", null);
				}
			}

			List<UserNotification> unseenNotifs = userNotificationRepo.fetchUnSeenNotifications(rootOrg, userId,
					notifs);

			List<UserNotification> seenNotifs = new ArrayList<>();
			for (UserNotification notification : unseenNotifs) {
				if (!notification.getSeen()) {
					Timestamp seenOn = new Timestamp(new Date().getTime());
					notification.setSeen(true);
					notification.setSeenOn(seenOn);
				} else {
					seenNotifs.add(notification);
				}
			}

			unseenNotifs.removeAll(seenNotifs);
			if (unseenNotifs.isEmpty())
				throw new InvalidDataInputException("unseenNotifications.notFound", null);

			userNotificationRepo.saveAll(unseenNotifs);
		}
	}

	private void markNotifAsRead(String rootOrg, String userId, String classification) {

		while (true) {
			List<UserNotification> unseenActionNotifs = userNotificationRepo.fetchUnSeenNotifications(rootOrg, userId,
					classification, notificationProps.getMarkAllAsReadCount());
			if (unseenActionNotifs.isEmpty()) {
				break;
			}
			for (UserNotification notification : unseenActionNotifs) {
				Timestamp seenOn = new Timestamp(new Date().getTime());

				notification.setSeen(true);
				notification.setSeenOn(seenOn);
			}
			userNotificationRepo.saveAll(unseenActionNotifs);
		}
	}

//	@Override
//	public List<String> generateUpdateStmts() {
//
//		List<Classification> res = classificationRepo_test.getNotificationClassification();
//
//		List<String> stmts = new ArrayList<>();
//		for (Classification clas : res) {
//
//			String stmt = "UPDATE wingspan.event_recipient set classification='" + clas.getClassification()
//					+ "' where event_id='" + clas.getPrimaryKey().getEventId() + "' and recipient='"
//					+ clas.getPrimaryKey().getRecipient() + "'";
//
//			stmts.add(stmt);
//		}
//		return stmts;
//	}
}