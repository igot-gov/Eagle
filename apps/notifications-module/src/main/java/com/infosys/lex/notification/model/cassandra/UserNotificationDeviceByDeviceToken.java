package com.infosys.lex.notification.model.cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_notification_device_by_device_token")
public class UserNotificationDeviceByDeviceToken {

	@PrimaryKey
	private UserNotificationDeviceByDeviceTokenKey key;
	
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

	public UserNotificationDeviceByDeviceTokenKey getKey() {
		return key;
	}

	public void setKey(UserNotificationDeviceByDeviceTokenKey key) {
		this.key = key;
	}

	public String getEndpointArn() {
		return endpointArn;
	}

	public void setEndpointArn(String endpointArn) {
		this.endpointArn = endpointArn;
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

	

	public UserNotificationDeviceByDeviceToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getEndpointPlatform() {
		return endpointPlatform;
	}

	public void setEndpointPlatform(String endpointPlatform) {
		this.endpointPlatform = endpointPlatform;
	}

	@Override
	public String toString() {
		return "UserNotificationDeviceByDeviceToken [key=" + key + ", endpointArn=" + endpointArn
				+ ", endpointPlatform=" + endpointPlatform + ", notificationPlatform=" + notificationPlatform
				+ ", updatedBy=" + updatedBy + ", updatedOn=" + updatedOn + "]";
	}

	public UserNotificationDeviceByDeviceToken(UserNotificationDeviceByDeviceTokenKey key, String endpointArn,
			String endpointPlatform, String notificationPlatform, String updatedBy, Date updatedOn) {
		this.key = key;
		this.endpointArn = endpointArn;
		this.endpointPlatform = endpointPlatform;
		this.notificationPlatform = notificationPlatform;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
	}

	
	
}
