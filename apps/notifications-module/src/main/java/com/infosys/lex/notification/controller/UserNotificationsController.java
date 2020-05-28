
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
package com.infosys.lex.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.notification.dto.NotificationSendDTO;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.service.PushService;
import com.infosys.lex.notification.service.UserNotificationsService;

@RestController
@RequestMapping("/v1/users/{user_id}")
@CrossOrigin(origins = "*")
public class UserNotificationsController {

	@Autowired
	UserNotificationsService userNotificationService;
	
	@Autowired
	PushService pushService;

	/**
	 * THis returns the all notification based on classification provided and
	 * filters the unseen/seen results based on flag showUnseen. (Warn)
	 * Classification should be passed for if unseen or seen notification has to be
	 * shown as unseen/seen for all classified notification cannot be fetched.
	 * 
	 * @param userId
	 * @param rootOrg
	 * @param page
	 * @param size
	 * @param classification
	 * @param showUnseen
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/notifications")
	public NotificationSendDTO getUserNotifications(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg", required = true) String rootOrg,
			@RequestParam(defaultValue = "0", required = false, name = "page") String page,
			@RequestParam(defaultValue = "10", required = false, name = "size") Integer size,
			@RequestParam(defaultValue = "all", required = false, name = "classification") String classification,
			@RequestParam(name = "seen", required = false) Boolean seen) throws Exception {

		return userNotificationService.getAllNotifications(rootOrg, userId, page, size, classification, seen);
	}

	@PatchMapping("/notifications/{notification_id}")
	public ResponseEntity<?> markAsSeen(@RequestHeader(required = true, name = "rootOrg") String rootOrg,
			@PathVariable("notification_id") String notificationId, @PathVariable("user_id") String userId)
			throws Exception {

		userNotificationService.markAsSeen(rootOrg, userId, notificationId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/notification-summary")
	public ResponseEntity<?> getCountOfAllUnseenNotifications(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg", required = true) String rootOrg) throws Exception {

		return new ResponseEntity<>(userNotificationService.getCountOfAllUnSeenNotifications(rootOrg, userId),
				HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PatchMapping("/notifications")
	public ResponseEntity<?> markAllAsRead(@PathVariable("user_id") String userId,
			@RequestHeader(name = "rootOrg", required = true) String rootOrg,
			@RequestParam(defaultValue = "all", required = false, name = "classification") String classification,
			@RequestParam(defaultValue = "false", required = false, name = "dataProvided") Boolean dataProvided,
			@RequestBody(required = false) Map<String, Object> notificationData) throws Exception {

		List<String> notificationIds = null;
		if (dataProvided) {
			if (notificationData == null || notificationData.isEmpty())
				throw new InvalidDataInputException("notificationData.required", null);
			notificationIds = (List<String>) notificationData.get("notificationIds");
			if (notificationIds == null || notificationIds.isEmpty())
				throw new InvalidDataInputException("notificationIds.required", null);
		}
		userNotificationService.markAllAsRead(rootOrg, userId, classification, notificationIds);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

//	@GetMapping("/notifications/stmts")
//	public ResponseEntity<?> getUpdate() throws Exception {
//
//		return new ResponseEntity<>(userNotificationService.generateUpdateStmts(), HttpStatus.OK);
//	}
	
	
}
