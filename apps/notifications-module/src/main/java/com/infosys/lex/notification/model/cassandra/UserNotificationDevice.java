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

@Table("user_notification_device")
public class UserNotificationDevice {


	@PrimaryKey
	private UserNotificationDeviceKey key;
	
	@Column("endpoint_arn")
	private String endpointArn;

	@Column("endpoint_platform")
	private String endpointPlatform;
	
	@Column("notification_platform")
	private String notificationPlatform;
	
	@Column("updated_by")
	private String updatedBy;
	
	@Column("updated_on")
	private Date updatedOn;

	public UserNotificationDeviceKey getKey() {
		return key;
	}

	public void setKey(UserNotificationDeviceKey key) {
		this.key = key;
	}

	
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}



	public String getEndpointArn() {
		return endpointArn;
	}

	public void setEndpointArn(String endpointArn) {
		this.endpointArn = endpointArn;
	}

	public String getEndpointPlatform() {
		return endpointPlatform;
	}

	public void setEndpointPlatform(String endpointPlatform) {
		this.endpointPlatform = endpointPlatform;
	}

	public String getNotificationPlatform() {
		return notificationPlatform;
	}

	public void setNotificationPlatform(String notificationPlatform) {
		this.notificationPlatform = notificationPlatform;
	}

	public UserNotificationDevice(UserNotificationDeviceKey key, String endpointArn, String endpointPlatform,
			String notificationPlatform, String updatedBy, Date updatedOn) {
		this.key = key;
		this.endpointArn = endpointArn;
		this.endpointPlatform = endpointPlatform;
		this.notificationPlatform = notificationPlatform;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "UserNotificationDevice [key=" + key + ", endpointArn=" + endpointArn + ", endpointPlatform="
				+ endpointPlatform + ", notificationPlatform=" + notificationPlatform + ", updatedBy=" + updatedBy
				+ ", updatedOn=" + updatedOn + "]";
	}

	
	public UserNotificationDevice() {
		super();
		// TODO Auto-generated constructor stub
	}
	



}
