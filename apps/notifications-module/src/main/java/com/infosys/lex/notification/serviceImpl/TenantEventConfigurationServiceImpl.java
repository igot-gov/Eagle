/**
Â© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (â€œProgramâ€�),
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.dto.EventsDTO;
import com.infosys.lex.notification.dto.ModesDTO;
import com.infosys.lex.notification.dto.RecipientsDTO;
import com.infosys.lex.notification.entity.TenantEvent;
import com.infosys.lex.notification.entity.TenantEventPrimaryKey;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.repository.ModesRepository;
import com.infosys.lex.notification.repository.TenantEventRepository;
import com.infosys.lex.notification.repository.TenantTemplateRepository;
import com.infosys.lex.notification.service.TenantEventConfigurationService;
import com.infosys.lex.notification.service.TenantModeConfigurationService;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class TenantEventConfigurationServiceImpl implements TenantEventConfigurationService {

	@Autowired
	TenantEventRepository tenantEventRepo;

	@Autowired
	ModesRepository tenantModeRepository;

	@Autowired
	ApplicationServerProperties appServerProp;

	@Autowired
	TenantModeConfigurationService tenantModeConfigService;

	@Autowired
	TenantTemplateRepository tenantTemplateRepo;

	@Override
	public List<Map<String, Object>> getTenantConfigurableEvents(String rootOrg, String org, String language)
			throws Exception {

		// select the first language in preferred language list
		List<String> languages = ProjectCommonUtil.convertLanguagePreferencesToList(language);
		language = languages.get(0);

		// mode = inApp will never be configured for the tanant as even default record
		// doesn't exists
		// for the mode = inApp
		List<Map<String, Object>> defaultActivatedEvents = tenantEventRepo
				.getDefaultActivatedEventsWithLangPreference(language);

		List<Map<String, Object>> tenantActivatedEvents = tenantEventRepo
				.getTenantConfiguredEventsWithLangPreference(rootOrg, org, language);

		List<String> tenantActivatedModes = tenantModeRepository.getActivatedModesForRootOrgAndOrg(rootOrg, org);

		List<Map<String, Object>> mergedEvents = mergeDefaultAndTenantEventsConfig(defaultActivatedEvents,
				tenantActivatedEvents, tenantActivatedModes);

		return ProjectCommonUtil.convertToResponseStructure(mergedEvents);
	}

	@Override
	public void configureTenantNotificationEvents(String rootOrg, String org, List<EventsDTO> data, String userUUID)
			throws AuthenticationException {

		List<TenantEvent> eventList = new ArrayList<TenantEvent>();

		// For each event, for each recipient, for each mode prepare data and
		// insert a row indicating an event that is configured
		for (EventsDTO event : data) {

			for (RecipientsDTO recipient : event.getRecipients()) {
				for (ModesDTO mode : recipient.getModes()) {
					Timestamp lastUpdated = new Timestamp((new Date()).getTime());
					String templateId = null;
					String receiverEmails = null;

					TenantEventPrimaryKey tenantEventPrimaryKey = new TenantEventPrimaryKey(rootOrg, org,
							event.getEventId(), recipient.getRecipient(), mode.getModeId());
					if(mode.getReceiverEmails() != null && mode.getModeId().equals("email"))
						receiverEmails = String.join(",", mode.getReceiverEmails());
					eventList.add(new TenantEvent(tenantEventPrimaryKey, mode.getStatus(), templateId, lastUpdated,
							userUUID, receiverEmails));
				}
			}
		}
		if (!eventList.isEmpty())
			tenantEventRepo.saveAll(eventList);
		else
			throw new InvalidDataInputException("No events found to configure!", null);
	}

	@Override
	public List<Map<String, Object>> getTenantConfiguredEvents(String rootOrg, String org, String language) {

		List<String> languages = ProjectCommonUtil.convertLanguagePreferencesToList(language);
		language = languages.get(0);

		return ProjectCommonUtil.convertToResponseStructure(
				tenantEventRepo.getTenantConfiguredEvents(rootOrg, org, Arrays.asList(language)));
	}

	@Override
	public List<Map<String, Object>> getActivatedEventsForRootOrgAndOrgs(String rootOrg, List<String> orgs,
			List<String> languages) {

		// considered default removed events as well
		List<Map<String, Object>> tenantActivatedEvents = tenantEventRepo.getActivatedEventsByRootOrgAndOrgs(rootOrg,
				orgs, languages);
		
		
	
		// considered default removed modes as well
		List<Map<String, Object>> tenantActivatedModes = tenantModeRepository
				.getActivatedTenantModesByRootOrgAndOrgs(rootOrg, orgs);
		
		

		List<Map<String, Object>> filteredEventsByLanguagePref = this
				.getTenantEventsFilteredByLanguage(tenantActivatedEvents, languages);


		
		removeInactiveModes(filteredEventsByLanguagePref, tenantActivatedModes);

		return tenantActivatedEvents;
	}

	private List<Map<String, Object>> getTenantEventsFilteredByLanguage(List<Map<String, Object>> events,
			List<String> languages) {
		List<Map<String, Object>> filteredEvents = new ArrayList<>();

		List<String> eventIds = new ArrayList<>();
		for (String language : languages) {
			for (Map<String, Object> event : events) {
				if (event.get("language").equals(language)) {
					if (!eventIds.contains(event.get("event_id"))) {
						filteredEvents.add(event);
						eventIds.add(event.get("event_id").toString());
					}

				}
			}
		}
		return filteredEvents;
	}

	@Override
	public List<Map<String, Object>> getActivatedModesForEventAndRootOrgAndOrgs(String rootOrg, List<String> orgs,
			String eventId) {

		// considered default removed events
		List<Map<String, Object>> activatedEvents = tenantEventRepo.getActivatedModesForRootOrgAndOrgs(rootOrg, orgs,
				eventId);

		// considered default removed modes
		List<Map<String, Object>> activatedModes = tenantModeRepository.getActivatedTenantModesByRootOrgAndOrgs(rootOrg,
				orgs);

		removeInactiveModes(activatedEvents, activatedModes);
		
		return activatedEvents;
	}

	private void removeInactiveModes(List<Map<String, Object>> tenantActivatedEvents,
			List<Map<String, Object>> tenantActivatedModes) {
		Iterator<Map<String, Object>> tenantEventsIterator = tenantActivatedEvents.iterator();

		while (tenantEventsIterator.hasNext()) {
			Map<String, Object> tenantEvent = tenantEventsIterator.next();
			String org = tenantEvent.get("org").toString();
			String mode = tenantEvent.get("mode").toString();
			boolean canRemove = true;
			for (Map<String, Object> tenantActivatedMode : tenantActivatedModes) {
				if (tenantActivatedMode.get("org").equals(org) && tenantActivatedMode.get("mode").equals(mode)) {
					canRemove = false;
				}
			}
			if (canRemove) {
				tenantEventsIterator.remove();
			}
		}
	}

	private List<Map<String, Object>> mergeDefaultAndTenantEventsConfig(
			List<Map<String, Object>> defaultActivatedEvents, List<Map<String, Object>> tenantActivatedEvents,
			List<String> tenantActivatedModes) {

		defaultActivatedEvents = ProjectCommonUtil.convertToHashMap(defaultActivatedEvents);
		tenantActivatedEvents = ProjectCommonUtil.convertToHashMap(tenantActivatedEvents);

		List<Map<String, Object>> mergedEvents = new ArrayList<>();
		for (Map<String, Object> defaultActivatedEvent : defaultActivatedEvents) {

			boolean eventConfigExistsForTenant = false;
			for (Map<String, Object> tenantActivatedEvent : tenantActivatedEvents) {

				if (defaultActivatedEvent.get("event_id").equals(tenantActivatedEvent.get("event_id"))
						&& defaultActivatedEvent.get("recipient").equals(tenantActivatedEvent.get("recipient"))
						&& defaultActivatedEvent.get("mode").equals(tenantActivatedEvent.get("mode"))) {
					eventConfigExistsForTenant = true;
					mergedEvents.add(tenantActivatedEvent);
				}
			}
			// here tenant activated modes is used to know which modes the tenant has
			// enables and to filter out event-recipient rows which have modes which are not
			// in tenant list.
			if (!eventConfigExistsForTenant && tenantActivatedModes.contains(defaultActivatedEvent.get("mode"))) {
				defaultActivatedEvent.put("mode_activated", false);
				mergedEvents.add(defaultActivatedEvent);
			}
		}
		return mergedEvents;
	}
}