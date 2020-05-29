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

package com.infosys.lex.notification.service;

import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import com.infosys.lex.notification.dto.EventsDTO;

public interface TenantEventConfigurationService {

	List<Map<String, Object>> getActivatedModesForEventAndRootOrgAndOrgs(String rootOrg, List<String> orgs,
			String eventId);

	List<Map<String, Object>> getActivatedEventsForRootOrgAndOrgs(String rootOrg, List<String> orgs,
			List<String> languages);

	List<Map<String, Object>> getTenantConfigurableEvents(String rootOrg, String org, String language) throws Exception;

	List<Map<String, Object>> getTenantConfiguredEvents(String rootOrg, String org, String language);

	void configureTenantNotificationEvents(String rootOrg, String org, List<EventsDTO> data, String userUUID)
			throws AuthenticationException;
}
