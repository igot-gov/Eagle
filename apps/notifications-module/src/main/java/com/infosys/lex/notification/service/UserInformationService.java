package com.infosys.lex.notification.service;

import java.util.List;
import java.util.Map;

import com.infosys.lex.notification.dto.UserInfo;

public interface UserInformationService {

	Map<String, UserInfo> getUserInfoAndNotificationPreferences(String rootOrg, String eventId,
			List<String> recipientRoles, List<String> userIds);

	Map<String, UserInfo> getUserInfo(String rootOrg, List<String> userIds);

	boolean validateUser(String rootOrg, String userId) throws Exception;

	Map<String, String> fetchManager(String rootOrg, List<String> userIds);
}
