package com.infosys.lex.notification.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.dto.TemplateInfo;
import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.service.EmailNotificationProcessingService;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.service.ProducerService;
import com.infosys.lex.notification.service.TenantTemplateService;
import com.infosys.lex.notification.util.LexConstants;
import com.infosys.lex.notification.util.LexNotificationLogger;
import com.infosys.lex.notification.util.NotificationTemplateUtil;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class EmailNotificationProcessingServiceImpl implements EmailNotificationProcessingService {

	@Autowired
	NotificationConsumerUtilService consumerUtilServ;

	@Autowired
	ProducerService producerServ;

	@Autowired
	TenantTemplateService tenantTemplateService;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	/**
	 * This method is called when only one user is for the event recipient role is there in which case no bucketing based
	 * on language and org is required
	 * @param rootOrg
	 * @param eventId
	 * @param recipientRole
	 * @param eventRecipientUserId
	 * @param recipients
	 * @param usersInfoMap
	 * @param tagValuePairs
	 * @param templateIdToOrgMap
	 * @param orgDomainMap
	 * @param targetDataMapping
	 * @param userIdReceiverEmailMap
	 * @param orgAppEmailMap
	 * @param isEventReceiverConfigured
	 */
	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public void enqueueEmailNotificationForSingleUser(String rootOrg, String eventId, String recipientRole,
			String eventRecipientUserId, Map<String, List<String>> recipients, Map<String, UserInfo> usersInfoMap,
			Map<String, Object> tagValuePairs, Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String[]> userIdReceiverEmailMap,
			Map<String, String> orgAppEmailMap, Boolean isEventReceiverConfigured) {
		try {

			List<String> recieverConfiguredEmailIds = null;
			// if event recipient user details not found then no notification is sent
			if (!usersInfoMap.containsKey(eventRecipientUserId))
				throw new InvalidDataInputException("No userDetail found for userId :" + eventRecipientUserId, null);

			if (isEventReceiverConfigured) {
				if (!userIdReceiverEmailMap.containsKey(eventRecipientUserId)||userIdReceiverEmailMap.get(eventRecipientUserId) == null
						|| userIdReceiverEmailMap.get(eventRecipientUserId).length == 0) {
					throw new ApplicationLogicException(
							"No reciever Email found for User id :" + eventRecipientUserId + " and Event : " + eventId);
				}
				recieverConfiguredEmailIds = Arrays.asList(userIdReceiverEmailMap.get(eventRecipientUserId));
			}

			String eventRecipientUserEmail = usersInfoMap.get(eventRecipientUserId).getEmail();

			List<String> userOrgs = usersInfoMap.get(eventRecipientUserId).getOrgs();

			List<String> userPreferredLanguages = usersInfoMap.get(eventRecipientUserId).getPreferedLanguages();
			Set<String> templateIds = templateIdToOrgMap.keySet();

			Map<String, Object> languageOrgTemplateMap = this.getLanguageOrgsMapForTemplates(templateIdToOrgMap,
					templateIds, eventId, recipientRole, "email");

			// org of the template for which user email is sent
			String selectedOrg = null;
			String selectedTemplateLanguage = null;
			// template for user
			Map<String, Object> selectedTemplate = null;

			// check if template exist which matches user language preference and org

			for (String preferredLanguage : userPreferredLanguages) {
				if (languageOrgTemplateMap.containsKey(preferredLanguage.toLowerCase())) {
					Map<String, Map<String, Object>> orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
							.get(preferredLanguage.toLowerCase());
					selectedOrg = userOrgs.stream().filter(userOrg -> orgTemplateDetailMap.containsKey(userOrg))
							.findFirst().orElse(null);
					if (selectedOrg != null) {
						selectedTemplateLanguage = preferredLanguage.toLowerCase();
						selectedTemplate = orgTemplateDetailMap.get(selectedOrg);
						break;

					}
				}
			}

			// if no template found then select the default template
			if (selectedOrg == null) {
				for (String preferredlanguage : userPreferredLanguages) {
					if (languageOrgTemplateMap.containsKey("DEFAULT_" + preferredlanguage.toLowerCase())) {
						Map<String, Map<String, Object>> orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
								.get("DEFAULT_" + preferredlanguage.toLowerCase());
						selectedTemplate = orgTemplateDetailMap.get("default");
						selectedTemplateLanguage = preferredlanguage.toLowerCase();
						selectedOrg = "default";
						break;
					}
				}
			}
			if (selectedTemplate == null) {
				throw new ApplicationLogicException("Template not found for userId : " + eventRecipientUserId);
			}

			String templateText = (String) selectedTemplate.get("template_text");
			String templateSubject = (String) selectedTemplate.get("template_subject");
			if (templateText == null || templateText.isEmpty() || templateSubject == null || templateSubject.isEmpty())
				throw new ApplicationLogicException("Empty email template found for " + rootOrg + ", " + eventId + ", "
						+ recipientRole + ", email");
			String userDomainName = NotificationTemplateUtil
					.getDomainForUser(usersInfoMap.get(eventRecipientUserId).getOrgs(), orgDomainMap);

			String appEmail = !orgAppEmailMap.isEmpty()
					? NotificationTemplateUtil.getAppEmailForUser(usersInfoMap.get(eventRecipientUserId).getOrgs(),
							orgAppEmailMap)
					: "";
			templateSubject = NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs, templateSubject,
					usersInfoMap, recipients, recipientRole, targetDataMapping, userDomainName, appEmail);

			templateText = NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs, templateText, usersInfoMap,
					recipients, recipientRole, targetDataMapping, userDomainName, appEmail);

			if (isEventReceiverConfigured) {
				// for smtp config we need to send the org Here we will be sending the user orgs
				producerServ.enqueueEmailEvent(
						new EmailRequest(rootOrg, userOrgs, eventRecipientUserEmail, templateText, templateSubject,
								eventId, recieverConfiguredEmailIds, new ArrayList<>(), new ArrayList<>(), true));
			} else {
				producerServ.enqueueEmailEvent(new EmailRequest(rootOrg, userOrgs, "", templateText, templateSubject,
						eventId, new ArrayList<>(Arrays.asList(eventRecipientUserEmail)), new ArrayList<>(),
						new ArrayList<>(), true));
			}

		} catch (Exception e) {
			// not throwing exception as it would stop other notifications getting sent
			consumerUtilServ.saveError(rootOrg, eventId, e, new HashMap<>());
			logger.error("Could not send email " + eventRecipientUserId + " for rootOrg " + rootOrg);
		}
	}

	/**
	 * this is used to send the notification incase the recever emails is configured
	 * in which case the mail will be sent from the recipients to pre-configured
	 * list of email ids(tenant configured)
	 * 
	 * @param rootOrg
	 * @param eventId
	 * @param recipientRole
	 * @param emailFromUserIds
	 * @param recipients(this        param is used for template creation)
	 * @param usersInfoMap
	 * @param tagValues
	 * @param orgToTemplateIdMap
	 * @param orgDomainMap
	 * @param targetDAtaMapping
	 * @param userIdReceiverEmailMap
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void enqueueEmailNotfificationForRecieverConfigedEvent(String rootOrg, String eventId, String recipientRole,
			List<String> emailFromUserIds, Map<String, List<String>> recipients, Map<String, UserInfo> usersInfoMap,
			Map<String, Object> tagValuePairs, Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String[]> userIdReceiverEmailMap,
			Map<String, String> orgAppEmailMap) {
		Map<String, String> userIdEmailMap = ProjectCommonUtil.getUserIdEmailMap(emailFromUserIds, usersInfoMap);
		List<String> emailIds = new ArrayList<>(userIdEmailMap.values());

		List<String> userOrgs;
		String userDomainName;
		String appEmail;
		List<String> userPreferredLanguages;
		Set<String> templateIds = templateIdToOrgMap.keySet();

		Map<String, Object> languageOrgTemplateMap = this.getLanguageOrgsMapForTemplates(templateIdToOrgMap,
				templateIds, eventId, recipientRole, "email");
		UserInfo userInfo;
		Map<String, TemplateInfo> userIdTemplateMap = new HashMap<>();

		// org of the template for which user email is sent
		String selectedOrg = null;
		String selectedTemplateLanguage = null;
		// template for user
		Map<String, Object> selectedTemplate = null;

		for (String userId : emailFromUserIds) {
			if (!usersInfoMap.containsKey(userId)) {
				consumerUtilServ.saveError(rootOrg, eventId,
						new InvalidDataInputException("User data not found For userId : " + userId, null), "");
				continue;
			}
			userInfo = usersInfoMap.get(userId);
			userPreferredLanguages = userInfo.getPreferedLanguages();
			userOrgs = userInfo.getOrgs();

			// check if template exist which matches user language preference and org

			for (String preferredLanguage : userPreferredLanguages) {
				if (languageOrgTemplateMap.containsKey(preferredLanguage.toLowerCase())) {
					Map<String, Map<String, Object>> orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
							.get(preferredLanguage.toLowerCase());
					selectedOrg = userOrgs.stream().filter(userOrg -> orgTemplateDetailMap.containsKey(userOrg))
							.findFirst().orElse(null);
					if (selectedOrg != null) {
						selectedTemplateLanguage = preferredLanguage.toLowerCase();
						selectedTemplate = orgTemplateDetailMap.get(selectedOrg);
						break;

					}
				}
			}

			// if no template found then select the default template
			if (selectedOrg == null) {
				for (String preferredlanguage : userPreferredLanguages) {
					if (languageOrgTemplateMap.containsKey("DEFAULT_" + preferredlanguage.toLowerCase())) {
						Map<String, Map<String, Object>> orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
								.get("DEFAULT_" + preferredlanguage.toLowerCase());
						selectedTemplate = orgTemplateDetailMap.get("default");
						selectedTemplateLanguage = preferredlanguage.toLowerCase();
						selectedOrg = "default";
						break;
					}
				}
			}
			if (selectedTemplate != null) {
				TemplateInfo templateInfo = new TemplateInfo((String) selectedTemplate.get("template_text"),
						(String) selectedTemplate.get("template_subject"), selectedOrg, selectedTemplateLanguage);
				userIdTemplateMap.put(userId, templateInfo);
			} else
				consumerUtilServ.saveError(rootOrg, eventId, new Exception("Template not found for userId : " + userId),
						"");

		}
		for (String userId : userIdTemplateMap.keySet()) {
			try {
				if (!userIdReceiverEmailMap.containsKey(userId) || userIdReceiverEmailMap.get(userId) == null
						|| userIdReceiverEmailMap.get(userId).length == 0) {
					throw new ApplicationLogicException(
							"No reciever Email found for User id :" + userId + " and Event : " + eventId);
				}

				if (!userIdEmailMap.containsKey(userId))
					throw new InvalidDataInputException("No userDetail found for userId :" + userId, null);

				List<String> receiverEmails = Arrays.asList(userIdReceiverEmailMap.get(userId));
				TemplateInfo emailTemplate = userIdTemplateMap.get(userId);
				String templateText = emailTemplate.getTemplateText();
				String templateSubject = emailTemplate.getTemplateSubject();
				if (templateText == null || templateText.isEmpty() || templateSubject == null
						|| templateSubject.isEmpty())
					throw new ApplicationLogicException("Empty email template found for " + rootOrg + ", " + eventId
							+ ", " + recipientRole + ", email");
				userDomainName = NotificationTemplateUtil.getDomainForUser(usersInfoMap.get(userId).getOrgs(),
						orgDomainMap);

				appEmail = !orgAppEmailMap.isEmpty() ? NotificationTemplateUtil
						.getAppEmailForUser(usersInfoMap.get(userId).getOrgs(), orgAppEmailMap) : "";
				templateSubject = NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs, templateSubject,
						usersInfoMap, recipients, recipientRole, targetDataMapping, userDomainName, appEmail);

				templateText = NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs, templateSubject,
						usersInfoMap, recipients, recipientRole, targetDataMapping, userDomainName, appEmail);

				// for smtp config we need to send the org Here we will be sending the user orgs
				producerServ.enqueueEmailEvent(new EmailRequest(rootOrg, usersInfoMap.get(userId).getOrgs(),
						userIdEmailMap.get(userId), templateText, templateSubject, eventId, receiverEmails,
						new ArrayList<>(), new ArrayList<>(), true));
			} catch (Exception e) {
				// not throwing exception as it would stop other notifications getting sent
				consumerUtilServ.saveError(rootOrg, eventId, e, new HashMap<>());
				logger.error("Could not send email " + userId.toString() + " for rootOrg " + rootOrg + " emails "
						+ emailIds.toString());
			}
		}
	}

	/**
	 * 
	 * This method divides user by their org and language preference and enqueues
	 * into email_notification kafka topic
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void enqueueEmailNotification(String rootOrg, String eventId, String recipientRole,
			List<String> emailRecipientUserIds, Map<String, List<String>> recipients,
			Map<String, UserInfo> usersInfoMap, Map<String, Object> tagValuePairs,
			Map<String, String> templateIdToOrgMap, Map<String, String> orgDomainMap,
			Map<String, String> targetDataMapping, Map<String, String> orgAppEmailMap) {

		List<String> userOrgs;
		String domainName;
		String appEmail;
		List<String> userPreferredLanguages;
		Set<String> templateIds = templateIdToOrgMap.keySet();

		// this map will be used to store the final list segregated user based on thier
		// org
		// and language preference
		Map<String, Object> languageOrgUserBucket = new HashMap<>();

		// language map has language as key and value as map of the org and templates
		// Note : We dont create map with org and value as language mapped to templates
		// as
		// the users preferred langauge is ordered but not orgs he belong to so
		// our priority is fetching template of language that user has most preferred
		Map<String, Object> languageOrgTemplateMap = this.getLanguageOrgsMapForTemplates(templateIdToOrgMap,
				templateIds, eventId, recipientRole, "email");
		UserInfo userInfo;

		// This map will be used for langaugeorguserbucket map
		Map<String, List<String>> orgUserIdsMap;

		// org of the template for which user email is sent
		String selectedOrg = null;
		String selectedLang = null;

		for (String userId : emailRecipientUserIds) {
			if (!usersInfoMap.containsKey(userId)) {
				consumerUtilServ.saveError(rootOrg, eventId,
						new InvalidDataInputException("User data not found For userId : " + userId, null), "");
				continue;
			}
			userInfo = usersInfoMap.get(userId);
			userPreferredLanguages = userInfo.getPreferedLanguages();
			userOrgs = userInfo.getOrgs();

			// check if template exist which matches user language preference and org
			for (String preferredLanguage : userPreferredLanguages) {
				if (languageOrgTemplateMap.containsKey(preferredLanguage.toLowerCase())) {
					Map<String, Map<String, Object>> orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
							.get(preferredLanguage.toLowerCase());
					selectedOrg = userOrgs.stream().filter(userOrg -> orgTemplateDetailMap.containsKey(userOrg))
							.findFirst().orElse(null);
					if (selectedOrg != null) {
						selectedLang = preferredLanguage;
						break;

					}
				}
			}

			// if no template found then select the default template
			if (selectedOrg == null) {
				for (String preferredlanguage : userPreferredLanguages) {
					if (languageOrgTemplateMap.containsKey(
							LexConstants.DEFAULT_TEMPLATE_LANGUAGE_PREFIX + preferredlanguage.toLowerCase())) {
						selectedOrg = LexConstants.DEFAULT_TEMPLATE_ORG;
						selectedLang = preferredlanguage;
						break;
					}
				}
			}

			// here we create/update map with key as language and values as map of the org
			// and userId list
			// Here we put the user into groups of the selected org and language for the
			// user
			if (selectedOrg != null) {
				if (languageOrgUserBucket.containsKey(selectedLang)) {
					orgUserIdsMap = (Map<String, List<String>>) languageOrgUserBucket.get(selectedLang);
					if (orgUserIdsMap.containsKey(selectedOrg)) {
						orgUserIdsMap.get(selectedOrg).add(userId);
					} else {
						orgUserIdsMap.put(selectedOrg, new ArrayList<String>(Arrays.asList(userId)));
					}
				} else {
					orgUserIdsMap = new HashMap<String, List<String>>();
					orgUserIdsMap.put(selectedOrg, new ArrayList<String>(Arrays.asList(userId)));
					languageOrgUserBucket.put(selectedLang, orgUserIdsMap);
				}
			} else
				consumerUtilServ.saveError(rootOrg, eventId, new Exception("Template not found for userId : " + userId),
						"");

		}

		// this iterates through the selected lang and the org and fetches the list
		// of userids and send mail to this set of ids
		for (String templateLanguage : languageOrgUserBucket.keySet()) {
			orgUserIdsMap = (Map<String, List<String>>) languageOrgUserBucket.get(templateLanguage);
			
			for (String orgKey : orgUserIdsMap.keySet()) {
				Map<String, Map<String, Object>> orgTemplateDetailMap;
				List<String> orgsForUserIds;

				// if orgkey is default orgs for userIds will be empty
				// Note : This is case when even template with language 'en' is not available
				// for the users orgs and rootorg. In this case we set the orgs which will be
				// used to select the appemail ,domainName smtpconfig etc will be empty
				if (orgKey.equals(LexConstants.DEFAULT_TEMPLATE_ORG)) {
					orgsForUserIds = new ArrayList<>();
					//for default org the templates will be stored in "{prefix}_{langcode}" 
					orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
							.get(LexConstants.DEFAULT_TEMPLATE_LANGUAGE_PREFIX+templateLanguage);
				} else {
					orgsForUserIds = new ArrayList<>(Arrays.asList(orgKey));
					orgTemplateDetailMap = (Map<String, Map<String, Object>>) languageOrgTemplateMap
							.get(templateLanguage);
				}

				Map<String, String> userIdEmailMap = ProjectCommonUtil.getUserIdEmailMap(orgUserIdsMap.get(orgKey),
						usersInfoMap);
				List<String> emailIds = new ArrayList<>(userIdEmailMap.values());
				try {
					Map<String, Object> emailTemplate = orgTemplateDetailMap.get(orgKey);
					emailTemplate = ProjectCommonUtil.convertToHashMap(emailTemplate);
					ProjectCommonUtil.templateValidations(rootOrg, eventId, recipientRole, emailTemplate);
					domainName = NotificationTemplateUtil.getDomainForUser(orgsForUserIds, orgDomainMap);
					appEmail = !orgAppEmailMap.isEmpty()
							? NotificationTemplateUtil.getAppEmailForUser(orgsForUserIds, orgAppEmailMap)
							: "";
					emailTemplate.put("template_subject",
							NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs,
									emailTemplate.get("template_subject").toString(), usersInfoMap, recipients,
									recipientRole, targetDataMapping, domainName, appEmail));

					emailTemplate.put("template_text",
							NotificationTemplateUtil.replaceTags(rootOrg, tagValuePairs,
									emailTemplate.get("template_text").toString(), usersInfoMap, recipients,
									recipientRole, targetDataMapping, domainName, appEmail));

					producerServ.enqueueEmailEvent(
							new EmailRequest(rootOrg, orgsForUserIds, "", emailTemplate.get("template_text").toString(),
									emailTemplate.get("template_subject").toString(), eventId, emailIds,
									new ArrayList<>(), new ArrayList<>(), true));
				} catch (Exception e) {
					// not throwing exception as it would stop other notifications getting sent
					consumerUtilServ.saveError(rootOrg, eventId, e, new HashMap<>());
					logger.error("Could not send email  for rootOrg " + rootOrg + " emails " + emailIds.toString());
				}
			}
		}

	}

	/*
	 * This method returns a map where the keys are languages which is mapped to
	 * another map where the key is org and the value is the template detail In case
	 * of default org the langauge key will in the format of DEFAULT_{langcode} for
	 * the main map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getLanguageOrgsMapForTemplates(Map<String, String> templateIdToOrgMap,
			Set<String> templateIds, String eventId, String recipientRole, String mode) {

		// fetch all the templates for template id
		List<Map<String, Object>> templates = tenantTemplateService.fetchAllTemplatesByTemplateIds(templateIds, eventId,
				recipientRole, mode);
		Map<String, Object> resp = new HashMap<String, Object>();

		String templateId;
		String templateLang;
		String org;
		Map<String, Map<String, Object>> orgTemplateDetailMap;

		// process the templates(create the map)
		for (Map<String, Object> templateMap : templates) {
			templateId = templateMap.get("template_id").toString();
			templateLang = templateMap.get("language").toString().toLowerCase();
			// templates configured by default rootorg
			if (templateId.equals(LexConstants.DEFAULT_TEMPLATE_ORG)) {
				templateLang = LexConstants.DEFAULT_TEMPLATE_LANGUAGE_PREFIX + templateLang;
				orgTemplateDetailMap = new HashMap<>();
				orgTemplateDetailMap.put(LexConstants.DEFAULT_TEMPLATE_ORG, templateMap);

			} else {
				org = templateIdToOrgMap.get(templateId);
				if (resp.containsKey(templateLang))
					orgTemplateDetailMap = (Map<String, Map<String, Object>>) resp.get(templateLang);
				else
					orgTemplateDetailMap = new HashMap<>();

				orgTemplateDetailMap.put(org, templateMap);
			}
			resp.put(templateLang, orgTemplateDetailMap);
		}
		return resp;
	}

}
