package com.infosys.lex.ext.service;

import java.util.Map;

public interface BatchJobsService {

	String externalProgressImport(String rootOrg, Map<String, Object> requestBody);

	String recalculateProgress(String rootOrg, String userId, String contentId, Map<String, Object> requestBody);

	void sendNotification(String rootOrg, String notificationType, Map<String, Object> requestBody);

}
