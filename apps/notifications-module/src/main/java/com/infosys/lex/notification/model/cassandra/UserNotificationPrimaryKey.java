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

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserNotificationPrimaryKey {

	@PrimaryKeyColumn(name = "root_org", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
	private String rootOrg;

	@PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED, ordinal = 1)
	private String userId;

	@PrimaryKeyColumn(name = "classified_as", type = PrimaryKeyType.PARTITIONED, ordinal = 2)
	private String classifiedAs;

	@PrimaryKeyColumn(name = "notification_id", type = PrimaryKeyType.CLUSTERED, ordinal = 3)
	private UUID notificationId;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public String getClassifiedAs() {
		return classifiedAs;
	}

	public UUID getNotificationId() {
		return notificationId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setClassifiedAs(String classifiedAs) {
		this.classifiedAs = classifiedAs;
	}

	public void setNotificationId(UUID notificationId) {
		this.notificationId = notificationId;
	}

	public UserNotificationPrimaryKey(String rootOrg, String userId, String classifiedAs, UUID notificationId) {
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.classifiedAs = classifiedAs;
		this.notificationId = notificationId;
	}

	@Override
	public String toString() {
		return "UserNotificationDigestPrimaryKey [userId=" + userId + ", classifiedAs=" + classifiedAs
				+ ", notificationId=" + notificationId + "]";
	}
}
