package com.infosys.lex.notification.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserNotificationDeviceByDeviceTokenKey {

	@PrimaryKeyColumn(name = "device_token" ,ordinal = 0,type = PrimaryKeyType.PARTITIONED  )
	private String deviceToken;
	
	@PrimaryKeyColumn(name  = "root_org", ordinal = 1,type = PrimaryKeyType.CLUSTERED)
	private String rootOrg;
	
	@PrimaryKeyColumn(name = "user_id",ordinal = 2,type = PrimaryKeyType.CLUSTERED)
	private String userId;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

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

	@Override
	public String toString() {
		return "UserNotificationDeviceByDeviceTokenKey [deviceToken=" + deviceToken + ", rootOrg=" + rootOrg
				+ ", userId=" + userId + "]";
	}

	public UserNotificationDeviceByDeviceTokenKey(String deviceToken, String rootOrg, String userId) {
		this.deviceToken = deviceToken;
		this.rootOrg = rootOrg;
		this.userId = userId;
	}

	public UserNotificationDeviceByDeviceTokenKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
