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

package com.infosys.lex.notification.model.cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_notification")
public class UserNotification {

	@PrimaryKey
	private UserNotificationPrimaryKey primaryKey;

	@Column("event_id")
	private String eventId;

	@Column("group_id")
	private String groupId;

	@Column("message")
	private String messageText;

	@Column("subject")
	private String subject;

	@Column("receiving_role")
	private String receivingRole;

	@Column("received_on")
	private Date receivedOn;

	@Column("target_data")
	private String targetData;

	@Column("seen")
	private Boolean seen;

	@Column("seen_on")
	private Date seenOn;

	public UserNotificationPrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public String getEventId() {
		return eventId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getMessageText() {
		return messageText;
	}

	public String getTargetData() {
		return targetData;
	}

	public void setTargetData(String targetData) {
		this.targetData = targetData;
	}

	public String getSubject() {
		return subject;
	}

	public String getReceivingRole() {
		return receivingRole;
	}

	public Date getReceivedOn() {
		return receivedOn;
	}

	public Boolean getSeen() {
		return seen;
	}

	public Date getSeenOn() {
		return seenOn;
	}

	public void setPrimaryKey(UserNotificationPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setReceivingRole(String receivingRole) {
		this.receivingRole = receivingRole;
	}

	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public void setSeenOn(Date seenOn) {
		this.seenOn = seenOn;
	}

	public UserNotification() {
		super();
	}

	public UserNotification(UserNotificationPrimaryKey primaryKey, String eventId, String groupId,
			String messageText, String subject, String receivingRole, Date receivedOn, Boolean seen,
			Date seenOn, String targetData) {
		super();
		this.primaryKey = primaryKey;
		this.eventId = eventId;
		this.groupId = groupId;
		this.messageText = messageText;
		this.subject = subject;
		this.receivingRole = receivingRole;
		this.receivedOn = receivedOn;
		this.seen = seen;
		this.seenOn = seenOn;
		this.targetData = targetData;
	}

	@Override
	public String toString() {
		return "UserNotificationDigest [primaryKey=" + primaryKey + ", eventId=" + eventId + ", groupId=" + groupId
				+ ", messageText=" + messageText + ", subject=" + subject + ", receivingRole=" + receivingRole
				+ ", receivedOn=" + receivedOn + ", seen=" + seen + ", seenOn=" + seenOn + "]";
	}
}
