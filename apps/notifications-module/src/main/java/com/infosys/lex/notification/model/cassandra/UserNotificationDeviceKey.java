package com.infosys.lex.notification.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserNotificationDeviceKey {
	
	@PrimaryKeyColumn(name = "root_org",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;
	
	
	@PrimaryKeyColumn(name = "user_id", ordinal = 0 ,type = PrimaryKeyType.PARTITIONED)
	private String userId;
	
	@PrimaryKeyColumn(name = "device_token",ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String deviceToken;


	public String getRootOrg() {
		return rootOrg;
	}


	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getDeviceToken() {
		return deviceToken;
	}


	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}


	public UserNotificationDeviceKey() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toString() {
		return "UserNotificationDeviceKey [rootOrg=" + rootOrg + ", userId=" + userId + ", deviceToken=" + deviceToken
				+ "]";
	}


	public UserNotificationDeviceKey(String rootOrg, String userId, String deviceToken) {
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.deviceToken = deviceToken;
	}

	
	

	


		
}

