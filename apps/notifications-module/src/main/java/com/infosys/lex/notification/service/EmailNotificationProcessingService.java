package com.infosys.lex.notification.service;

import java.util.List;
import java.util.Map;

import com.infosys.lex.notification.dto.UserInfo;

public interface EmailNotificationProcessingService {

	void enqueueEmailNotification(String rootOrg, String eventId, String recipientRole,
			List<String> emailRecipientUserIds, Map<String, List<String>> recipients,
			Map<String, UserInfo> usersInfoMap, Map<String, Object> tagValuePairs,
			Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String> orgAppEmailMap);

	void enqueueEmailNotfificationForRecieverConfigedEvent(String rootOrg, String eventId, String recipientRole,
			List<String> emailRecipientUserIds, Map<String, List<String>> recipients,
			Map<String, UserInfo> usersInfoMap, Map<String, Object> tagValuePairs,
			Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String[]> userIdReceiverEmailMap,
			Map<String, String> orgAppEmailMap);

	void enqueueEmailNotificationForSingleUser(String rootOrg, String eventId, String recipientRole,
			String eventRecipientUserId, Map<String, List<String>> recipients, Map<String, UserInfo> usersInfoMap,
			Map<String, Object> tagValuePairs, Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String[]> userIdReceiverEmailMap,
			Map<String, String> orgAppEmailMap, Boolean isEventReceiverConfigured);

}
