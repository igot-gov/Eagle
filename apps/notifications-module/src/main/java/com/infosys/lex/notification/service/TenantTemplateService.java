package com.infosys.lex.notification.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.infosys.lex.notification.dto.EventTemplateDTO;

public interface TenantTemplateService {

	void configureTemplates(String rootOrg, String org, EventTemplateDTO template, String language, String userId)
			throws Exception;

	List<Map<String, Object>> fetchTenantTemplates(String rootOrg, String org, String eventId, String modeId,
			String language);

	List<Map<String, Object>> fetchTemplates(String templateId, String eventId, String recipientRole, String mode,
			List<String> languagePreferences);

	
	List<Map<String, Object>> fetchTemplatesForMultipleIds(Set<String> templateIds, String eventId,
			String recipientRole, String mode, Set<String> languagePreferences);


	List<Map<String, Object>> fetchAllTemplatesByTemplateIds(Set<String> templateIds, String eventId,
			String recipientRole, String mode);

}
