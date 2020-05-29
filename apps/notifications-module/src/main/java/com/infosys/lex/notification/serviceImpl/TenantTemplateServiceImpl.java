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
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.uuid.Generators;
import com.infosys.lex.notification.dto.EventTemplateDTO;
import com.infosys.lex.notification.entity.RecipientTags;
import com.infosys.lex.notification.entity.RecipientTagsPrimaryKey;
import com.infosys.lex.notification.entity.TenantEvent;
import com.infosys.lex.notification.entity.TenantEventPrimaryKey;
import com.infosys.lex.notification.entity.TenantTemplate;
import com.infosys.lex.notification.entity.TenantTemplatePrimaryKey;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.projection.RecipientTemplateProjection;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.repository.RecipientTagsRepository;
import com.infosys.lex.notification.repository.TenantEventRepository;
import com.infosys.lex.notification.repository.TenantTemplateRepository;
import com.infosys.lex.notification.service.TenantTemplateService;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class TenantTemplateServiceImpl implements TenantTemplateService {

	@Autowired
	TenantEventRepository tenantEventRepo;

	@Autowired
	TenantTemplateRepository tenantTemplateRepo;

	@Autowired
	ApplicationServerProperties appServerProps;

	@Autowired
	RecipientTagsRepository recipientsTagRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.TenantEventConfigurationService#
	 * configureTemplates(java.lang.String, java.lang.String,
	 * com.infosys.lex.notification.dto.EventTemplateDTO, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void configureTemplates(String rootOrg, String org, EventTemplateDTO template, String language,
			String userId) throws Exception {

		// fetch the event data for which the template has to be configured
		TenantEvent event = tenantEventRepo
				.findById(new TenantEventPrimaryKey(rootOrg, org, template.getEventId(), template.getRecipient(),
						template.getMode()))
				.orElseThrow(() -> new InvalidDataInputException("Invalid event provided!", null));

		Date date = new Date();
		Timestamp lastUpdatedOn = new Timestamp(date.getTime());
		String templateId = null;
		boolean isTemplateUpdate = false;
		// if it is not a new template, fetch templateId
		if (template.getTemplateId() != null && !template.getTemplateId().isEmpty()) {
			templateId = template.getTemplateId();
			isTemplateUpdate = true;
		} else {
			// else generate a new templateId
			templateId = Generators.timeBasedGenerator().generate().toString();
		}
		// prepare data and perform creation/updation operation,
		// also update events table with templateId in case of new template creation.
		TenantTemplatePrimaryKey primaryKey = new TenantTemplatePrimaryKey(templateId, language);
		TenantTemplate templateData = new TenantTemplate(primaryKey, template.getTemplateSubject(),
				template.getTemplateText(), lastUpdatedOn, userId);
		tenantTemplateRepo.save(templateData);
		if (!isTemplateUpdate) {
			event.setTemplateId(templateId);
			event.setUpdatedOn(lastUpdatedOn);
			event.setUpdatedBy(userId);
			tenantEventRepo.save(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.TenantEventConfigurationService#
	 * fetchTenantTemplates(java.lang.String, java.lang.String, java.util.List)
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchTenantTemplates(String rootOrg, String org, String eventId, String modeId,
			String language) {

		List<Map<String, Object>> finalData = new ArrayList<>();
		Map<String, Object> recipientWiseTemplates = new HashMap<>();
		Set<RecipientTagsPrimaryKey> recipientsForTags = new HashSet<>();

		List<String> langauges = ProjectCommonUtil.convertLanguagePreferencesToList(language);
		language = langauges.get(0);
		// fetch templates according to the settings configured by the tenant admin
		List<RecipientTemplateProjection> templates = tenantTemplateRepo.fetchTemplates(Arrays.asList(language),
				rootOrg, org, modeId, eventId);

		if (templates != null && !templates.isEmpty()) {
			// if tenant admin has not configured a template for any recipient/mode, fetch
			// the default template in that case.
			List<String> recipientsForDefaultTemplate = new ArrayList<>();
			for (Iterator<RecipientTemplateProjection> iterator = templates.iterator(); iterator.hasNext();) {
				RecipientTemplateProjection template = iterator.next();
				recipientsForTags.add(new RecipientTagsPrimaryKey(eventId, template.getRecipient()));
				if (template.getTemplateId() == null || template.getTemplateId().isEmpty()) {
					recipientsForDefaultTemplate.add(template.getRecipient());
					iterator.remove();
				}
			}
			if (!recipientsForDefaultTemplate.isEmpty()) {
				List<RecipientTemplateProjection> defaultTemplates = tenantTemplateRepo.fetchDefaultTemplates(
						appServerProps.getNotificationAdminRootOrg(), appServerProps.getNotificationAdminOrg(),
						Arrays.asList(language), eventId, modeId, recipientsForDefaultTemplate);

				if (defaultTemplates == null || defaultTemplates.isEmpty()) {
					throw new InvalidDataInputException("No default templates found!!", null);
				}
				templates.addAll(defaultTemplates);
			}
		} else {
			templates = tenantTemplateRepo.fetchTemplates(Arrays.asList(language),
					appServerProps.getNotificationAdminRootOrg(), appServerProps.getNotificationAdminOrg(), modeId,
					eventId);
			for (RecipientTemplateProjection template : templates) {
				recipientsForTags.add(new RecipientTagsPrimaryKey(eventId, template.getRecipient()));
			}
		}
		// prepare data in required format
		if (templates == null || templates.isEmpty()) {
			return finalData;
		}

		for (RecipientTemplateProjection template : templates) {
			Map<String, Object> recipientTemplate = new HashMap<>();
			if (template.getRootOrg().equalsIgnoreCase(appServerProps.getNotificationAdminRootOrg())) {
				recipientTemplate.put("template_id", null);
			} else {
				recipientTemplate.put("template_id", template.getTemplateId());
			}
			recipientTemplate.put("template_subject", template.getTemplateSubject());
			recipientTemplate.put("template_text", template.getTemplateText());
			recipientTemplate.put("tags", "");
			recipientWiseTemplates.put(template.getRecipient(), recipientTemplate);
		}
		// also fetch the tag data and append it to the template data
		List<RecipientTags> tags = (List<RecipientTags>) recipientsTagRepo.findAllById(recipientsForTags);

		for (RecipientTags tag : tags) {
			if (recipientWiseTemplates.containsKey(tag.getTagsPrimaryKey().getRecipient())) {
				Map<String, Object> templateData = (Map<String, Object>) recipientWiseTemplates
						.get(tag.getTagsPrimaryKey().getRecipient());
				templateData.put("tags", tag.getTag());
			}
		}
		for (Entry<String, Object> entry : recipientWiseTemplates.entrySet()) {
			Map<String, Object> finalTemplate = new HashMap<>();
			finalTemplate.put("recipient", entry.getKey());
			finalTemplate.put("template", entry.getValue());

			finalData.add(finalTemplate);
		}
		return finalData;
	}

	@Override
	public List<Map<String, Object>> fetchTemplates(String templateId, String eventId, String recipientRole,
			String mode, List<String> languagePreferences) {

		if (templateId == null)
			return tenantTemplateRepo.fetchDefaultTemplateForEventAndRecipientRoleAndModeAndLanguage(eventId,
					recipientRole, mode, languagePreferences);
		else
			return tenantTemplateRepo.fetchTemplateByIdAndLanguage(templateId, languagePreferences);
	}
	
	/**
	 * This methods returns templateids if they exist else returns the templates set by default rootOrg and org for that
	 * event 
	 */
	@Override
	public List<Map<String, Object>> fetchTemplatesForMultipleIds(Set<String> templateIds, String eventId, String recipientRole,
			String mode, Set<String> languagePreferences) {
		
		if(!templateIds.contains(null))
			return tenantTemplateRepo.fetchTemplatesByIdsAndLanguage(templateIds, languagePreferences);
		else {
			templateIds.remove(null);
			
			if(!templateIds.isEmpty())
				return tenantTemplateRepo.fetchAllTemplatesByIdsAndLanguagesWithDefaultTemplate(eventId, recipientRole, mode, languagePreferences, templateIds);
			else {
				return tenantTemplateRepo.fetchAllDefaultTemplatesForEventIdAndRecipientRoleAndModeAndLanguages(eventId, recipientRole, mode, new ArrayList<String>(languagePreferences));
			}
		}
		
		
	}
	
	
	@Override
	public List<Map<String,Object>> fetchAllTemplatesByTemplateIds(Set<String> templateIds, String eventId, String recipientRole,
			String mode)
	{
		if(!templateIds.isEmpty())
			//fetches tempates for given templateid and also the default templates for the given
			return tenantTemplateRepo.fetchAllTemplatesByTemplateIdsWithDefaultTemplate(eventId, recipientRole, mode, templateIds);

		else
			//fetch default templates for the given event ids
			return tenantTemplateRepo.fetchAllDefaultTemplatesForEventIdAndRecipientRoleAndMode(eventId, recipientRole, mode);

	}
}






