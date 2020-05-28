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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.dto.NotificationEvent;
import com.infosys.lex.notification.dto.PushNotificationRequest;
import com.infosys.lex.notification.service.ProducerService;

/**
 * 
 * Controllers to put notification events to kafka for further processing.
 * Consumers for the events in ConsumerServiceImpl.java.
 *
 */
@RestController
@CrossOrigin(origins = "*")
public class NotificationEventController {

	@Autowired
	ProducerService producerService;
	

	/**
	 * enqueue plain text email event.
	 * 
	 * will work with both userId or email set in recipients(to,cc,bcc)
	 * 
	 * 
	 * 
	 * @param rootOrg
	 * @param emailEvent
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/v1/notification/email")
	public ResponseEntity<?> enqueueEmailEvent(@RequestHeader(name = "rootOrg") String rootOrg,
			@RequestBody EmailRequest emailEvent) throws JsonProcessingException {

		emailEvent.setRootOrg(rootOrg);
		producerService.enqueueEmailEvent(emailEvent);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	/**
	 * enqueue plain text push event.
	 * 
	 * @param rootOrg
	 * @param emailEvent
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/v1/notification/push")
	public ResponseEntity<?> enqueuePushEvent(@RequestHeader(name = "rootOrg") String rootOrg,
			@RequestBody PushNotificationRequest pushNotificationRequest) throws JsonProcessingException {

		pushNotificationRequest.setRootOrg(rootOrg);
		producerService.enqueuePushEvent(pushNotificationRequest);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	/**
	 * enqueue notification event to trigger email and in-app notification.
	 * 
	 * configuration to send sms and push as well but not enabled.
	 * 
	 * @param rootOrg
	 * @param notificationEvent
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("/v1/notification/event")
	public ResponseEntity<?> enqueueNotificationEvent(@RequestHeader(name = "rootOrg") String rootOrg,
			@RequestBody NotificationEvent notificationEvent) throws JsonProcessingException {

		notificationEvent.setRootOrg(rootOrg);
		producerService.enqueueNotificationEvent(notificationEvent);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
}