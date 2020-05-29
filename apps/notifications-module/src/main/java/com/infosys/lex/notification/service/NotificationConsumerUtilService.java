package com.infosys.lex.notification.service;

import java.util.List;
import java.util.Map;

public interface NotificationConsumerUtilService {


	Map<String, String> getOrgDomainMap(String rootOrg);

	Map<String, String> getOrgFooterEmailMap(String rootOrg, List<String> orgs);

	void saveError(String rootOrg, String eventId, Exception e, Object requestBody);

	boolean checkEventTimestamp(long timestamp);

}
