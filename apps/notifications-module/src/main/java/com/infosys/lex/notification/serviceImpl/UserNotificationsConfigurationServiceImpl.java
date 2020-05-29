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

package com.infosys.lex.notification.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.bodhi.repository.UserEventRepository;
import com.infosys.lex.notification.dto.EventsDTO;
import com.infosys.lex.notification.dto.ModesDTO;
import com.infosys.lex.notification.dto.RecipientsDTO;
import com.infosys.lex.notification.dto.TenantEventConfigurationDTO;
import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.exception.ResourceNotFoundException;
import com.infosys.lex.notification.model.cassandra.UserEvent;
import com.infosys.lex.notification.model.cassandra.UserEventPrimaryKey;
import com.infosys.lex.notification.service.TenantEventConfigurationService;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.service.UserNotificationsConfigurationService;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class UserNotificationsConfigurationServiceImpl implements UserNotificationsConfigurationService {

	@Autowired
	TenantEventConfigurationService tenantEventService;

	@Autowired
	UserEventRepository eventRepo;

	@Autowired
	UserEventRepository userEventRepo;

//	@Autowired
//	NotificationClassificationRepository classificationRepo;

	@Autowired
	UserInformationService userInfoService;

	@Override
	public List<Map<String, Object>> getUserNotificationPreferences(String rootOrg, String eventId,
			List<String> receivingRoles, List<String> userIds) {

		return userEventRepo.findUserNotificationSettings(rootOrg, eventId, receivingRoles, userIds);
	}

	@Override
	public List<Map<String, Object>> getNotificationEventsForUser(String rootOrg, String userId, List<String> languages)
			throws Exception {

		List<String> orgs = getOrgsForUser(rootOrg, userId);

		// fetching only activated events configuration from the rootOrg and org's for
		// the user.
		List<Map<String, Object>> validEvents = tenantEventService.getActivatedEventsForRootOrgAndOrgs(rootOrg, orgs,
				languages);

		// if there are event config for the same event fetched from multiple org's then
		// only one of them is shown to the user
		retainUniqueEvents(validEvents);
		validEvents = convertToHashMap(validEvents);

		Set<String> applicableRecievingRoles = validEvents.stream()
				.map(validEvent -> validEvent.get("recipient").toString()).collect(Collectors.toSet());

		// fetching all user preferences for notification modes.
		List<Map<String, Object>> userEvents = userEventRepo.findByUserId(rootOrg, userId,
				new ArrayList<>(applicableRecievingRoles));

		// override user selected notification preferences to the master list of
		// notification events fetched from user org's
		applyUserPreferences(validEvents, userEvents);

		return ProjectCommonUtil.convertToResponseStructure(validEvents);
	}

	@Override
	public void setAllEvent(String rootOrg, List<TenantEventConfigurationDTO> eventData, String userId)
			throws Exception {

		if (!userInfoService.validateUser(rootOrg, userId))
			throw new InvalidDataInputException("invalid.user", null);

		Date updatedOnDate = new Date();
		Timestamp updatedOnTimestamp = new Timestamp(updatedOnDate.getTime());
		List<UserEvent> output = new ArrayList<UserEvent>();
		// Extracting data event wise from the input
		for (TenantEventConfigurationDTO tenant : eventData) {
			for (EventsDTO event : tenant.getEvents()) {
				// Extracting data recipient wise from the input
				for (RecipientsDTO reciever : event.getRecipients()) {
					Map<String, Boolean> map = new HashMap<String, Boolean>();
					for (ModesDTO mode : reciever.getModes()) {
						map.put(mode.getModeId(), mode.getStatus());
					}
					// Adding data in table
					output.add(new UserEvent(
							new UserEventPrimaryKey(rootOrg, userId, event.getEventId(), reciever.getRecipient()), map,
							tenant.getGroupId(), updatedOnTimestamp));
				}
			}
		}
		eventRepo.saveAll(output);
	}

	@SuppressWarnings("unchecked")
	private void applyUserPreferences(List<Map<String, Object>> validEvents, List<Map<String, Object>> userEvents) {
		for (Map<String, Object> validEvent : validEvents) {
			String eventId = validEvent.get("event_id").toString();
			String recipient = validEvent.get("recipient").toString();
			String mode = validEvent.get("mode").toString();
			for (Map<String, Object> userEvent : userEvents) {
				if (userEvent.get("event_id").equals(eventId) && userEvent.get("receiving_role").equals(recipient)) {
					Map<String, Object> userModes = (Map<String, Object>) userEvent.get("modes_activated");
					if (userModes.containsKey(mode) && (boolean) userModes.get(mode) == false) {
						validEvent.put("mode_activated", false);
					} else {
						validEvent.put("mode_activated", true);
					}
				}
			}
			if (!validEvent.containsKey("mode_activated")) {
				validEvent.put("mode_activated", true);
			}
		}
	}

	private void retainUniqueEvents(List<Map<String, Object>> tenantActivatedEvents) {
		Set<String> eventRecipientMode = new HashSet<>();

		Iterator<Map<String, Object>> tenantsEventsIterator = tenantActivatedEvents.iterator();

		while (tenantsEventsIterator.hasNext()) {
			Map<String, Object> tenantEvent = tenantsEventsIterator.next();
			if (!eventRecipientMode.contains(
					tenantEvent.get("event_id") + "-" + tenantEvent.get("recipient") + "-" + tenantEvent.get("mode"))) {
				eventRecipientMode.add(tenantEvent.get("event_id") + "-" + tenantEvent.get("recipient") + "-"
						+ tenantEvent.get("mode"));
			} else {
				tenantsEventsIterator.remove();
			}
		}
	}

	private List<Map<String, Object>> convertToHashMap(List<Map<String, Object>> validEvents) {

		List<Map<String, Object>> hashMapList = new ArrayList<>();

		for (Map<String, Object> validEvent : validEvents) {
			Map<String, Object> validEventHashMap = new HashMap<>();
			validEvent.entrySet().forEach(entry -> validEventHashMap.put(entry.getKey(), entry.getValue()));
			hashMapList.add(validEventHashMap);
		}
		return hashMapList;
	}

	private List<String> getOrgsForUser(String rootOrg, String userId) {

		Map<String, UserInfo> usersInfoMap = userInfoService.getUserInfo(rootOrg, Arrays.asList(userId));
		if (!usersInfoMap.containsKey(userId))
			throw new ResourceNotFoundException("User with userId " + userId + " does not exist.");

		UserInfo userInfo = usersInfoMap.get(userId);

		if (userInfo == null)
			throw new ApplicationLogicException("User personal info not found");

		if (userInfo.getOrgs() == null || userInfo.getOrgs().isEmpty())
			throw new ApplicationLogicException("could not fetch orgs for user");

		return userInfo.getOrgs();
	}
}