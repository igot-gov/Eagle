package com.infosys.lex.notification.projection;

import org.springframework.beans.factory.annotation.Value;

public interface UserDeviceArnsProjection {

	@Value("#{target.key.userId}")
	public String getUserId();

	@Value("#{target.key.rootOrg}")
	public String getRootOrg();

	public String getEndpointArn();
	
	@Value("#{target.key.deviceToken}")
	public String getDeviceToken();
	
	public String getNotificationPlatform();
	
	
}
