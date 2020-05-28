package com.infosys.lex.notification.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;

public class ProjectCommonUtil {

	public static List<String> convertLanguagePreferencesToList(String language) {

		List<String> languages = new ArrayList<>();
		String[] languageArray = language.split(",");

		for (int i = 0; i < languageArray.length; ++i)
			languages.add(languageArray[i]);

		if (!languages.contains("en"))
			languages.add("en");

		return languages;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> convertToResponseStructure(List<Map<String, Object>> validEvents) {

		List<Map<String, Object>> groupMaps = new ArrayList<>();
		String receiverEmail ;
		for (Map<String, Object> validEvent : validEvents) {

			String groupId = validEvent.get("group_id").toString();
			String groupName = validEvent.get("group_name").toString();
			String eventId = validEvent.get("event_id").toString();
			String eventName = validEvent.get("event_name").toString();
			String eventDescription = validEvent.get("event_description").toString();
			String recipient = validEvent.get("recipient").toString();
			String recipientName = validEvent.get("recipient_name").toString();
			String recipientDescription = validEvent.get("recipient_description").toString();
			String adminDescription = validEvent.get("admin_description").toString();
			String modeId = validEvent.get("mode").toString();
			String modeName = validEvent.get("mode_name").toString();
			boolean activated = (boolean) validEvent.get("mode_activated");

			Map<String, Object> currentGroup = new HashMap<>();
			for (Map<String, Object> groupMap : groupMaps) {
				if (groupMap.get("group_id").equals(groupId)) {
					currentGroup = groupMap;
				}
			}

			if (currentGroup.isEmpty()) {
				currentGroup.put("group_id", groupId);
				currentGroup.put("group_name", groupName);
				currentGroup.put("events", new ArrayList<>());
				groupMaps.add(currentGroup);
			}

			Map<String, Object> currentEvent = new HashMap<>();
			List<Map<String, Object>> presentEvents = (List<Map<String, Object>>) currentGroup.get("events");
			for (Map<String, Object> presentEvent : presentEvents) {
				if (presentEvent.get("event_id").equals(eventId)) {
					currentEvent = presentEvent;
				}
			}

			if (currentEvent.isEmpty()) {
				currentEvent.put("event_id", eventId);
				currentEvent.put("event_name", eventName);
				currentEvent.put("event_description", eventDescription);
				currentEvent.put("recipients", new ArrayList<>());
				((List<Map<String, Object>>) currentGroup.get("events")).add(currentEvent);
			}

			Map<String, Object> currentRecipient = new HashMap<>();
			List<Map<String, Object>> presentRecipients = (List<Map<String, Object>>) currentEvent.get("recipients");
			for (Map<String, Object> presentRecipient : presentRecipients) {
				if (presentRecipient.get("recipient").equals(recipient)) {
					currentRecipient = presentRecipient;
				}
			}
			if (currentRecipient.isEmpty()) {
				currentRecipient.put("recipient", recipient);
				currentRecipient.put("recipient_name", recipientName);
				currentRecipient.put("recipient_description", recipientDescription);
				currentRecipient.put("admin_desciption", adminDescription);
				currentRecipient.put("modes", new ArrayList<>());
				((List<Map<String, Object>>) currentEvent.get("recipients")).add(currentRecipient);
			}

			Map<String, Object> currentMode = new HashMap<>();
			List<Map<String, Object>> presentModes = (List<Map<String, Object>>) currentRecipient.get("modes");
			for (Map<String, Object> presentMode : presentModes) {
				if (presentMode.get("mode_id").equals(modeId)) {
					currentMode = presentMode;
				}
			}
			if (currentMode.isEmpty()) {
				currentMode.put("mode_id", modeId);
				currentMode.put("mode_name", modeName);
				currentMode.put("status", activated);
				//send receiver email as list if present else null
				if (modeId.equals("email") && validEvent.containsKey("receiver_emails")) {
					receiverEmail = (String)validEvent.get("reveiver_emails");
					if(receiverEmail == null)
						currentMode.put("receiver_emails", null);
					else {
						currentMode.put("receiver_emails", receiverEmail.split(","));
					}
				}
				((List<Map<String, Object>>) currentRecipient.get("modes")).add(currentMode);
			}
		}
		return groupMaps;
	}

	public static List<String> removeInvalidEmailIds(List<String> invalidDomains, List<String> emailIds) {

		List<String> validEmailIds = new ArrayList<>();

		if (emailIds == null || emailIds.isEmpty())
			return validEmailIds;

		for (String emailId : emailIds) {
			boolean removeEmailId = false;
			for (String invalidDomain : invalidDomains) {
				if (emailId.endsWith(invalidDomain))
					removeEmailId = true;
			}
			if (!removeEmailId)
				validEmailIds.add(emailId);
		}

		return validEmailIds;
	}

	public static void retainUniqueNonEmailEvents(List<Map<String, Object>> tenantActivatedEvents) {

		Set<String> eventRecipientMode = new HashSet<>();

		Iterator<Map<String, Object>> tenantsEventsIterator = tenantActivatedEvents.iterator();

		while (tenantsEventsIterator.hasNext()) {
			Map<String, Object> tenantEvent = tenantsEventsIterator.next();
			//we ignore email as it will be used to fetch the templates that may be applicable to the users
			//and different or can contain different template in the prefered language of user
			if (tenantEvent.get("mode").toString().equalsIgnoreCase("email")||!eventRecipientMode.contains(
					tenantEvent.get("event_id") + "-" + tenantEvent.get("recipient") + "-" + tenantEvent.get("mode"))) {
				eventRecipientMode.add(tenantEvent.get("event_id") + "-" + tenantEvent.get("recipient") + "-"
						+ tenantEvent.get("mode"));
			} else {
				tenantsEventsIterator.remove();
			}
		}
		
	}

	public static String getDisplayName(Map<String, Object> userData) {
		String name = "";
		if (userData.get("first_name") != null && !(userData.get("first_name")).toString().isEmpty()
				&& userData.get("last_name") != null && !(userData.get("last_name")).toString().isEmpty()) {

			name = userData.get("first_name") + " " + userData.get("last_name");

		} else if (userData.get("first_name") != null && !(userData.get("first_name")).toString().isEmpty()
				&& (userData.get("last_name") == null || (userData.get("last_name")).toString().isEmpty())) {

			name = (userData.get("first_name")).toString();

		} else if ((userData.get("first_name") == null || (userData.get("first_name")).toString().isEmpty())
				&& userData.get("last_name") != null && !(userData.get("last_name")).toString().isEmpty()) {

			name = (userData.get("last_name")).toString();
		}
		return name;
	}

	public static void templateValidations(String rootOrg, String eventId, String recipientRole,
			Map<String, Object> emailTemplate) {

		if (emailTemplate == null || emailTemplate.isEmpty())
			throw new ApplicationLogicException(
					"template not found for " + rootOrg + ", " + eventId + ", " + recipientRole + ", email");

		if (emailTemplate.get("template_text") == null || emailTemplate.get("template_text").toString().isEmpty()
				|| emailTemplate.get("template_subject") == null
				|| emailTemplate.get("template_subject").toString().isEmpty())
			throw new ApplicationLogicException(
					"Empty email template found for " + rootOrg + ", " + eventId + ", " + recipientRole + ", email");

	}

	public static Map<String, Object> convertToHashMap(Map<String, Object> tupleMap) {

		Map<String, Object> validEventHashMap = new HashMap<>();
		tupleMap.entrySet().forEach(entry -> validEventHashMap.put(entry.getKey(), entry.getValue()));
		return validEventHashMap;
	}

	public static List<Map<String, Object>> convertToHashMap(List<Map<String, Object>> tupleBackedMaps) {

		List<Map<String, Object>> hashMapList = new ArrayList<>();

		for (Map<String, Object> tupleBackedMap : tupleBackedMaps) {
			Map<String, Object> hashMap = new HashMap<>();
			tupleBackedMap.entrySet().forEach(entry -> hashMap.put(entry.getKey(), entry.getValue()));
			hashMapList.add(hashMap);
		}
		return hashMapList;
	}

	public static Map<String, String> getUserIdEmailMap(List<String> userIds, Map<String, UserInfo> userInfoMap) {
		String userEmail;
		UserInfo userInfo;
		Map<String, String> userIdEmailMap = new HashMap<>();
		for (String userId : userIds) {
			if (userInfoMap.containsKey(userId) && userInfoMap.get(userId) != null) {
				userInfo = userInfoMap.get(userId);
				userEmail = userInfo.getEmail();
				if (userEmail != null && !userEmail.isEmpty())
					userIdEmailMap.put(userId, userEmail);
			}
		}
		return userIdEmailMap;
	}

}