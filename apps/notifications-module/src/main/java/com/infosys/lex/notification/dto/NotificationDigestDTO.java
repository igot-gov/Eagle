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

package com.infosys.lex.notification.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import javax.validation.constraints.NotNull;

public class NotificationDigestDTO implements Serializable {

	private static final long serialVersionUID = 1592723614274186098L;

	@NotNull(message = "Notification Id Required")
	private String notificationId;

	private String classifiedAs;

	private Boolean seen;

	private String eventId;


	private String message;

	private Timestamp receivedOn;

	private Map<String, Object> targetData;

	private String userId;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public Map<String, Object> getTargetData() {
		return targetData;
	}

	public void setTargetData(Map<String, Object> targetData) {
		this.targetData = targetData;
	}

	public String getClassifiedAs() {
		return classifiedAs;
	}

	public Boolean getSeen() {
		return seen;
	}

	

	public String getMessage() {
		return message;
	}

	public Timestamp getReceivedOn() {
		return receivedOn;
	}

	public String getUserId() {
		return userId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public void setClassifiedAs(String classifiedAs) {
		this.classifiedAs = classifiedAs;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setReceivedOn(Timestamp receivedOn) {
		this.receivedOn = receivedOn;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public NotificationDigestDTO() {
		super();
	}

	public NotificationDigestDTO(@NotNull(message = "Notification Id Required") String notificationId,
			String classifiedAs, Boolean seen, String message, Timestamp receivedOn, String userId,
			Map<String, Object> targetData, String eventId) {
		this.notificationId = notificationId;
		this.classifiedAs = classifiedAs;
		this.seen = seen;
		this.message = message;
		this.receivedOn = receivedOn;
		this.userId = userId;
		this.targetData = targetData;
		this.eventId = eventId;
	}

	@Override
	public String toString() {
		return "NotificationDigestDTO [notificationId=" + notificationId + ", classifiedAs=" + classifiedAs + ", seen="
				+ seen +  ", message=" + message + ", receivedOn=" + receivedOn + ", userId="
				+ userId + "]";
	}
}
