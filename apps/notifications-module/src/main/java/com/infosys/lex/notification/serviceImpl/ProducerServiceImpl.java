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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.common.network.InvalidReceiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.dto.NotificationEvent;
import com.infosys.lex.notification.dto.PushNotificationRequest;
import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.service.ProducerService;
import com.infosys.lex.notification.service.UserInformationService;

@Service
public class ProducerServiceImpl implements ProducerService {

	@Autowired
	KafkaTemplate<String, String> kafkaProducer;

	@Autowired
	UserInformationService userInfoService;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void enqueueNotificationEvent(NotificationEvent notificationEvent) throws JsonProcessingException {

		if (notificationEvent.getRootOrg() == null || notificationEvent.getRootOrg().isEmpty())
			throw new InvalidReceiveException("rootOrg is mandatory");

		kafkaProducer.send("notification_events", null, mapper.writeValueAsString(notificationEvent));
	}

	@Override
	public void enqueueEmailEvent(EmailRequest emailEvent) throws JsonProcessingException {

		if (emailEvent.getRootOrg() == null || emailEvent.getRootOrg().isEmpty())
			throw new InvalidReceiveException("rootOrg is mandatory");

		replaceUserIdsWithEmail(emailEvent);

		kafkaProducer.send("email_notification_events", null, mapper.writeValueAsString(emailEvent));
	}

	@Override
	public void enqueuePushEvent(PushNotificationRequest pushEvent) throws JsonProcessingException {

		if (pushEvent.getRootOrg() == null || pushEvent.getRootOrg().isEmpty())
			throw new InvalidReceiveException("rootOrg is mandatory");

		kafkaProducer.send("push_notification_events", null, mapper.writeValueAsString(pushEvent));
	}

	private void replaceUserIdsWithEmail(EmailRequest emailEvent) {

		if (emailEvent.isEmailsPassed())
			return;

		Set<String> userIds = new HashSet<>();

		if (emailEvent.getTo() != null && !emailEvent.getTo().isEmpty())
			userIds.addAll(emailEvent.getTo());

		if (emailEvent.getCc() != null && !emailEvent.getCc().isEmpty())
			userIds.addAll(emailEvent.getCc());

		if (emailEvent.getBcc() != null && !emailEvent.getBcc().isEmpty())
			userIds.addAll(emailEvent.getBcc());

		if (userIds.isEmpty())
			throw new ApplicationLogicException("No recipients set");

		Map<String, UserInfo> usersInfo = userInfoService.getUserInfo(emailEvent.getRootOrg(),
				new ArrayList<>(userIds));

		if (emailEvent.getTo() != null && !emailEvent.getTo().isEmpty()) {
			List<String> toEmails = new ArrayList<>();
			List<String> toUserIds = emailEvent.getTo();
			for (String toUserId : toUserIds) {
				if (usersInfo.containsKey(toUserId))
					toEmails.add(usersInfo.get(toUserId).getEmail());
			}
			if (toEmails.isEmpty())
				throw new ApplicationLogicException("Email TO is empty or user data not found for users In TO field");
			emailEvent.setTo(toEmails);
		}

		if (emailEvent.getCc() != null && !emailEvent.getCc().isEmpty()) {
			List<String> ccEmails = new ArrayList<>();
			List<String> ccUserIds = emailEvent.getCc();
			for (String ccUserId : ccUserIds) {
				if (usersInfo.containsKey(ccUserId))
					ccEmails.add(usersInfo.get(ccUserId).getEmail());
			}
			emailEvent.setCc(ccEmails);
		}

		if (emailEvent.getBcc() != null && !emailEvent.getBcc().isEmpty()) {
			List<String> bccEmails = new ArrayList<>();
			List<String> bccUserIds = emailEvent.getBcc();
			for (String bccUserId : bccUserIds) {
				if (usersInfo.containsKey(bccUserId))
					bccEmails.add(usersInfo.get(bccUserId).getEmail());
			}
			emailEvent.setBcc(bccEmails);
		}
	}
}