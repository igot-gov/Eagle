package com.infosys.lex.notification.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sns_notification_config",schema = "wingspan")
public class SNSNotificationConfig {

	@Id
	@Column(name = "endpoint_platform")
	private String endpointPlatform;
	
	public SNSNotificationConfig(String endpointPlatform, String notificationPlatform, String platformArn,
			Date updatedOn, String updatedBy) {
		this.endpointPlatform = endpointPlatform;
		this.notificationPlatform = notificationPlatform;
		this.platformArn = platformArn;
		this.updatedOn = updatedOn;
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "SNSNotificationConfig [endpointPlatform=" + endpointPlatform + ", notificationPlatform="
				+ notificationPlatform + ", platformArn=" + platformArn + ", updatedOn=" + updatedOn + ", updatedBy="
				+ updatedBy + "]";
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


	@Column(name = "notification_platform")
	private String notificationPlatform;
	
	@Column(name = "platform_arn")
	private String platformArn;
	
	@Column(name = "updated_on")
	private Date updatedOn;
	
	@Column(name = "updated_by")
	private String updatedBy;


	public String getPlatformArn() {
		return platformArn;
	}

	public void setPlatformArn(String platformArn) {
		this.platformArn = platformArn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	
	public SNSNotificationConfig() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
