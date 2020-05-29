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

package com.infosys.lex.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.dto.NotificationEvent;
import com.infosys.lex.notification.dto.PushNotificationRequest;

public interface ProducerService {

	void enqueueNotificationEvent(NotificationEvent notificationEvent) throws JsonProcessingException;

	/**
	 * 
	 * will accept userId and will convert it to user email to send the email if
	 * "emailsPassed" property is set to false.
	 * 
	 * enqueues email event in kafka queue.
	 * 
	 * recipients in "to" will be sent as separate email's to each recipient if
	 * sendType is "Single",else will send single mail to all recipients if the
	 * rootOrg's configuration permits the size of the recipients else the multiple
	 * email's will be sent by dividing the number of recipients per email as set by
	 * rootOrg's configuration.
	 * 
	 * @param emailRequest
	 * @throws JsonProcessingException
	 */
	void enqueueEmailEvent(EmailRequest emailRequest) throws JsonProcessingException;

	/**
	 * 
	 * sends a push notification to the specified user if ARN's for the user exists.
	 * 
	 * will send to ios,android and web based on the user recorded ARN's.
	 * 
	 * 
	 * @param pushEvent
	 * @throws JsonProcessingException
	 */
	void enqueuePushEvent(PushNotificationRequest pushEvent) throws JsonProcessingException;
}