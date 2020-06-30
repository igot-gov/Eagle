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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.dto.InAppNotificationRequest;
import com.infosys.lex.notification.dto.NotificationEvent;
import com.infosys.lex.notification.dto.PushNotificationRequest;
import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.projection.EventRecipientsProjection;
import com.infosys.lex.notification.repository.RecipientTagsRepository;
import com.infosys.lex.notification.service.EmailNotificationProcessingService;
import com.infosys.lex.notification.service.NotificationConsumerService;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.service.ProducerService;
import com.infosys.lex.notification.service.TenantEventConfigurationService;
import com.infosys.lex.notification.service.TenantTemplateService;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.service.UserNotificationsService;
import com.infosys.lex.notification.util.LexConstants;
import com.infosys.lex.notification.util.LexNotificationLogger;
import com.infosys.lex.notification.util.NotificationTemplateUtil;
import com.infosys.lex.notification.util.ProjectCommonUtil;

@Service
public class NotificationConsumerServiceImpl implements NotificationConsumerService {

	@Autowired
	UserInformationService userInfoService;

	@Autowired
	RecipientTagsRepository recipientTagsRepo;

	@Autowired
	TenantTemplateService tenantTemplateService;

	@Autowired
	TenantEventConfigurationService tenantEventService;

	@Autowired
	UserNotificationsService userNotificationService;

	@Autowired
	ProducerService producerService;

	@Autowired
	NotificationConsumerUtilService consumerUtilServ;

	@Autowired
	EmailNotificationProcessingService emailProcessingServ;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	@SuppressWarnings("unchecked")
	@Override
	public void consumeNotificationEvent(NotificationEvent notificationEvent) {

		logger.info("** recipients: "+notificationEvent.getRecipients().toString());

		String rootOrg = notificationEvent.getRootOrg();
		Map<String, String> orgDomainMap = consumerUtilServ.getOrgDomainMap(rootOrg);
		logger.info("** orgDomainMap: "+orgDomainMap.toString());

		String eventId = notificationEvent.getEventId();


		Map<String, List<String>> recipients = notificationEvent.getRecipients();
        //update recipients map add manager if manager recipientRole exists for that event
		this.addManagerRecipientUsersIfExists(rootOrg, recipients, eventId);

		// getting target url for email if not found in the request body
		Map<String, String> targetDataMapping = this.getTargetData(notificationEvent);

		// getting all unique userIds in the recipients of request body.
		Set<String> userIds = notificationEvent.getRecipients().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream()).collect(Collectors.toSet());

		// getting users personal data and notification configuration for event.
		Map<String, UserInfo> usersInfoMap = userInfoService.getUserInfoAndNotificationPreferences(rootOrg, eventId,
				new ArrayList<>(notificationEvent.getRecipients().keySet()), new ArrayList<>(userIds));

		// getting all distinct org's for the user's in request body.
		Set<String> distinctOrgs = usersInfoMap.entrySet().stream()
				.flatMap(userInfo -> userInfo.getValue().getOrgs().stream()).collect(Collectors.toSet());

		// get the app email to replaced in template for all the orgs
		Map<String, String> orgAppEmailMap = consumerUtilServ.getOrgFooterEmailMap(rootOrg,
				new ArrayList<>(distinctOrgs));

		// getting all the receiving roles and modes configured for the given eventId.
		List<Map<String, Object>> tenantNotificationConfigMaps = tenantEventService
				.getActivatedModesForEventAndRootOrgAndOrgs(rootOrg, new ArrayList<>(distinctOrgs), eventId);

		List<InAppNotificationRequest> inAppRequests = new ArrayList<>();

		try{
			logger.info("** All User notification Config: "+new ObjectMapper().writeValueAsString(tenantNotificationConfigMaps));
			logger.info("** orgAppEmailMap: "+new ObjectMapper().writeValueAsString(orgAppEmailMap));
			logger.info("** usersInfoMap: "+new ObjectMapper().writeValueAsString(usersInfoMap));


		}catch (JsonProcessingException e){
			logger.error("could not print all config: ");
		}

		for (Map.Entry<String, List<String>> recipient : recipients.entrySet()) {

			// templateid to org map used for configured receiver events
			Map<String, String> templateIdToOrgMap = new HashMap<>();
			// this is used for events where receiver email is configured
			Map<String, String[]> userIdConfiguredRecieverEmailMap = new HashMap<>();

			String recipientRole = recipient.getKey();
			List<String> recipientsUserIds = new ArrayList<>(recipient.getValue());
            logger.info("** recipientRole: "+recipientRole);
            logger.info("** recipientsUserIds: "+recipientsUserIds);
			boolean emailToBeSentAnyUser = false;
			List<String> eventRecipientUserIdsForEmail = new ArrayList<String>();
			boolean isEventRecieverConfigured = false;
			for (String userId : recipientsUserIds) {

				// if userid data not available dont send any notification
				if (!usersInfoMap.containsKey(userId))
					continue;

				UserInfo userInfo = usersInfoMap.get(userId);
				String domainName = NotificationTemplateUtil.getDomainForUser(userInfo.getOrgs(), orgDomainMap);

				Map<String, Object> resp = this.getTenantConfiguredModesForUser(eventId, tenantNotificationConfigMaps,
						recipientRole, userInfo);
				// getting all the notification modes for this user for given recipient role
				List<Map<String, Object>> userNotificationModes = (List<Map<String, Object>>) resp
						.get("configuredModes");
                try{
                    logger.info("** All User notification Config: "+new ObjectMapper().writeValueAsString(tenantNotificationConfigMaps));
                    logger.info("** All TenantConfiguredModesForUser: "+new ObjectMapper().writeValueAsString(resp));



                }catch (JsonProcessingException e){
                    logger.error("could not print userNotificationDigests: " + userNotificationModes);
                }

				isEventRecieverConfigured = (boolean) resp.get("isEventRecieverConfigured");
				String[] receiverEmails = (String[]) resp.get("receiverEmails");
				for (Map<String, Object> userNotificationMode : userNotificationModes) {

				    logger.info("** User notification mode: "+userNotificationMode.get("mode"));
					if (userNotificationMode.get("mode").equals("email")) {

						emailToBeSentAnyUser = true;
						if (!eventRecipientUserIdsForEmail.contains(userId))
							eventRecipientUserIdsForEmail.add(userId);
						// this is for receiver email configured events as for every user
						// the receiver emails configured may be different based on their org
						if (isEventRecieverConfigured)
							userIdConfiguredRecieverEmailMap.put(userId, receiverEmails);
						// for preset receiver list event
						if (userNotificationMode.get("template_id") != null)
							templateIdToOrgMap.put(userNotificationMode.get("template_id").toString(),
									userNotificationMode.get("org").toString());

					} else if (userNotificationMode.get("mode").equals("inApp")) {
						inAppRequests.add(this.createInAppRequest(notificationEvent, userId, recipientRole,
								usersInfoMap, userNotificationMode, targetDataMapping, domainName));
					} else if (userNotificationMode.get("mode").equals("push")) {
						try {
							producerService.enqueuePushEvent(this.createPushRequest(notificationEvent, userId,
									recipientRole, usersInfoMap, userNotificationMode, targetDataMapping, domainName));
						} catch (Exception e) {
							logger.error("Could not send push notification event to kafka");
							consumerUtilServ.saveError(rootOrg, eventId, e, new HashMap<>());
						}
					} else if (userNotificationMode.get("mode").equals("sms")) {
						this.createSmsRequest(notificationEvent, userId, recipientRole, usersInfoMap,
								userNotificationMode, targetDataMapping, domainName);
					}
				}
			}

			if (!emailToBeSentAnyUser)
				continue;

			// If any email is required to be sent for the given recipient role

			if (eventRecipientUserIdsForEmail.size() == 1) {
				// when there is only one user for given recipient role of the event raised(No
				// bucketing done based on org
				// and language
                logger.info("** User eventRecipientUserIdsForEmail: "+eventRecipientUserIdsForEmail);

                emailProcessingServ.enqueueEmailNotificationForSingleUser(rootOrg, eventId, recipientRole,
						eventRecipientUserIdsForEmail.get(0), recipients, usersInfoMap,
						notificationEvent.getTagValues(), templateIdToOrgMap, orgDomainMap, targetDataMapping,
						userIdConfiguredRecieverEmailMap, orgAppEmailMap, isEventRecieverConfigured);
			}

			else if (isEventRecieverConfigured) {
                logger.info("** User isEventRecieverConfigured: "+isEventRecieverConfigured);

                emailProcessingServ.enqueueEmailNotfificationForRecieverConfigedEvent(rootOrg, eventId, recipientRole,
						eventRecipientUserIdsForEmail, recipients, usersInfoMap, notificationEvent.getTagValues(),
						templateIdToOrgMap, orgDomainMap, targetDataMapping, userIdConfiguredRecieverEmailMap,
						orgAppEmailMap);
			} else {
                logger.info("** User sending email to all the users in given recipient role");

                // sending email to all the users in given recipient role
				emailProcessingServ.enqueueEmailNotification(rootOrg, eventId, recipientRole,
						eventRecipientUserIdsForEmail, recipients, usersInfoMap, notificationEvent.getTagValues(),
						templateIdToOrgMap, orgDomainMap, targetDataMapping, orgAppEmailMap);
			}
		}

		if (!inAppRequests.isEmpty())
			userNotificationService.sendInAppNotifications(notificationEvent, inAppRequests);
	}

	private Map<String, String> getTargetData(NotificationEvent notificationEvent) {

		Map<String, String> targetDataMapping = null;

		if (notificationEvent.getTagValues() != null && !notificationEvent.getTagValues().containsKey("#targetUrl")) {

			targetDataMapping = new HashMap<>();
			List<Map<String, Object>> targetData = recipientTagsRepo.getTargetUrls(notificationEvent.getEventId(),
					new ArrayList<>(notificationEvent.getRecipients().keySet()));

			for (Map<String, Object> targetDataMap : targetData) {
				if (targetDataMap.containsKey("recipient") && targetDataMap.get("recipient") != null
						&& targetDataMap.containsKey("target_url") && targetDataMap.get("target_url") != null) {

					targetDataMapping.put(targetDataMap.get("recipient").toString(),
							targetDataMap.get("target_url").toString());
				}
			}
		}

		return targetDataMapping;
	}

	private Map<String, Object> createSmsRequest(NotificationEvent notificationEvent, String userId,
			String recipientRole, Map<String, UserInfo> usersInfoMap, Map<String, Object> inAppConfig,
			Map<String, String> targetDataMapping, String domainName) {

		Map<String, Object> smsTemplate = fetchTemplate((String) inAppConfig.get("template_id"),
				usersInfoMap.get(userId).getPreferedLanguages(), notificationEvent.getEventId(), recipientRole, "sms");

		smsTemplate = ProjectCommonUtil.convertToHashMap(smsTemplate);
		ProjectCommonUtil.templateValidations(notificationEvent.getRootOrg(), notificationEvent.getEventId(),
				recipientRole, smsTemplate);

		smsTemplate.put("template_subject",
				NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(), notificationEvent.getTagValues(),
						smsTemplate.get("template_subject").toString(), usersInfoMap, notificationEvent.getRecipients(),
						recipientRole, targetDataMapping, domainName, null));

		smsTemplate.put("template_text",
				NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(), notificationEvent.getTagValues(),
						smsTemplate.get("template_text").toString(), usersInfoMap, notificationEvent.getRecipients(),
						recipientRole, targetDataMapping, domainName, null));

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("user_id", userId);
		responseMap.put("template_text", smsTemplate.get("template_text"));
		responseMap.put("template_subject", smsTemplate.get("template_subject"));

		return responseMap;
	}

	private InAppNotificationRequest createInAppRequest(NotificationEvent notificationEvent, String userId,
			String recipientRole, Map<String, UserInfo> usersInfoMap, Map<String, Object> inAppConfig,
			Map<String, String> targetDataMapping, String domainName) {

		Map<String, Object> inAppTemplate = fetchTemplate((String) inAppConfig.get("template_id"),
				usersInfoMap.get(userId).getPreferedLanguages(), notificationEvent.getEventId(), recipientRole,
				"inApp");

		inAppTemplate = ProjectCommonUtil.convertToHashMap(inAppTemplate);
		ProjectCommonUtil.templateValidations(notificationEvent.getRootOrg(), notificationEvent.getEventId(),
				recipientRole, inAppTemplate);

		inAppTemplate.put("template_subject",
				NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(), notificationEvent.getTagValues(),
						inAppTemplate.get("template_subject").toString(), usersInfoMap,
						notificationEvent.getRecipients(), recipientRole, targetDataMapping, domainName, null));

		inAppTemplate.put("template_text",
				NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(), notificationEvent.getTagValues(),
						inAppTemplate.get("template_text").toString(), usersInfoMap, notificationEvent.getRecipients(),
						recipientRole, targetDataMapping, domainName, null));

		InAppNotificationRequest inAppRequest = new InAppNotificationRequest(userId,
				inAppTemplate.get("template_subject").toString(), inAppTemplate.get("template_text").toString(),
				notificationEvent.getEventId(), recipientRole);

		return inAppRequest;
	}

	private PushNotificationRequest createPushRequest(NotificationEvent notificationEvent, String userId,
			String recipientRole, Map<String, UserInfo> usersInfoMap, Map<String, Object> inAppConfig,
			Map<String, String> targetDataMapping, String domainName) {

		Map<String, Object> pushTemplate = fetchTemplate((String) inAppConfig.get("template_id"),
				usersInfoMap.get(userId).getPreferedLanguages(), notificationEvent.getEventId(), recipientRole, "push");

		String targetUrl = this.fetchTargetUrlForPushNotification(targetDataMapping, notificationEvent.getTagValues(),
				domainName, recipientRole);
		pushTemplate = ProjectCommonUtil.convertToHashMap(pushTemplate);

		ProjectCommonUtil.templateValidations(notificationEvent.getRootOrg(), notificationEvent.getEventId(),
				recipientRole, pushTemplate);

		String subject = NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(),
				notificationEvent.getTagValues(), pushTemplate.get("template_subject").toString(), usersInfoMap,
				notificationEvent.getRecipients(), recipientRole, targetDataMapping, domainName, null);

		String body = NotificationTemplateUtil.replaceTags(notificationEvent.getRootOrg(),
				notificationEvent.getTagValues(), pushTemplate.get("template_text").toString(), usersInfoMap,
				notificationEvent.getRecipients(), recipientRole, targetDataMapping, domainName, null);

		PushNotificationRequest pushRequest = new PushNotificationRequest(notificationEvent.getRootOrg(),
				notificationEvent.getEventId(), userId, subject, body, targetUrl);

		return pushRequest;

	}

	private Map<String, Object> fetchTemplate(String templateId, List<String> preferedLanguages, String eventId,
			String recipientRole, String mode) {

		List<Map<String, Object>> templatesFetched = tenantTemplateService.fetchTemplates(templateId, eventId,
				recipientRole, mode, preferedLanguages);

		for (String preferedLanguage : preferedLanguages) {
			for (Map<String, Object> templateFetched : templatesFetched) {
				if (preferedLanguage.equals(templateFetched.get("language")))
					return templateFetched;
			}
		}

		if (templatesFetched == null || templatesFetched.isEmpty())
			throw new ApplicationLogicException("could not fetch any templates for the given event and recipient role");

		return templatesFetched.get(0);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getTenantConfiguredModesForUser(String eventId,
			List<Map<String, Object>> tenantNotificationConfigMaps, String recipientRole, UserInfo userInfo) {
		Map<String, Object> resp = new HashMap<>();
		List<String> userOrgs = userInfo.getOrgs();
		String[] receiverEmails = null;
		List<Map<String, Object>> tenantConfiguredModesForUser = new ArrayList<>();
		// this flag is used when receiver_email is configured for the event by rootOrg
		// and org
		// in which case the mail is sent from the user to the receiver email list
		boolean isEventRecieverConfigured = false;
		// if any mode is enabled by tenant then inApp is also sent
		boolean isAnyModeEnabledByTenant = false;
		for (Map<String, Object> tenantNotificationConfigMap : tenantNotificationConfigMaps) {
			String tenantMode = (String) tenantNotificationConfigMap.get("mode");
			logger.info("tenantMode :: "+tenantMode);
			// tenantMode
			if (userOrgs.contains(tenantNotificationConfigMap.get("org"))
					&& tenantNotificationConfigMap.get("recipient").equals(recipientRole)) {
				isAnyModeEnabledByTenant = true;
				boolean isModeEnabled = false;
				Map<String, Object> userRecieveConfig = userInfo.getRecieveConfig();

				if (!userRecieveConfig.containsKey(recipientRole)) {
					isModeEnabled = true;
				} else {
					Map<String, Object> userRecieveConfigPerRole = (Map<String, Object>) userRecieveConfig
							.get(recipientRole);
					if (!userRecieveConfigPerRole.containsKey(tenantMode)) {
						isModeEnabled = true;
					} else {
						boolean isModeEnabledByUser = (boolean) userRecieveConfigPerRole.get(tenantMode);
						if (isModeEnabledByUser) {
							isModeEnabled = true;
						}
					}
				}

				if (isModeEnabled) {
					tenantConfiguredModesForUser.add(tenantNotificationConfigMap);
					if (tenantMode.equals("email") && tenantNotificationConfigMap.containsKey("receiver_emails")
							&& tenantNotificationConfigMap.get("receiver_emails") != null) {
						isEventRecieverConfigured = true;
						receiverEmails = tenantNotificationConfigMap.get("receiver_emails").toString().split(",");
					}
				}
			}
		}

		if (isAnyModeEnabledByTenant && !isEventRecieverConfigured) {
			Map<String, Object> inAppModeConfig = new HashMap<>();
			inAppModeConfig.put("event_id", eventId);
			inAppModeConfig.put("recipient", recipientRole);
			inAppModeConfig.put("mode", "inApp");
			inAppModeConfig.put("mode_activated", true);
			inAppModeConfig.put("template_id", null);
			tenantConfiguredModesForUser.add(inAppModeConfig);
		}

		ProjectCommonUtil.retainUniqueNonEmailEvents(tenantConfiguredModesForUser);
		resp.put("configuredModes", ProjectCommonUtil.convertToHashMap(tenantConfiguredModesForUser));
		resp.put("isEventRecieverConfigured", isEventRecieverConfigured);
		resp.put("receiverEmails", receiverEmails);
		return resp;
	}

	/**
	 * This method fetches the tagert url for the push notification. This method
	 * return domainName if no targetUrl could be generated(if email notification
	 * doesn't have a target url)
	 * 
	 **/
	private String fetchTargetUrlForPushNotification(Map<String, String> targetDataMapping,
			Map<String, Object> tagValuePairs, String domainName, String recipientRole) {
		if (targetDataMapping == null && tagValuePairs.containsKey("#targetUrl")) {
			return tagValuePairs.get("#targetUrl").toString();
		} else if (targetDataMapping != null) {
			if (targetDataMapping.get(recipientRole).contains("{lexId}")) {
				if (tagValuePairs.containsKey("#lexId") && tagValuePairs.get("#lexId") != null
						|| !tagValuePairs.get("#lexId").toString().isEmpty()) {

					String url = targetDataMapping.get(recipientRole);
					url = url.replace("{lexId}", tagValuePairs.get("#lexId").toString());
					targetDataMapping.put(recipientRole, url);
				} else {
					throw new ApplicationLogicException("Required lexId for processing the event not present!");
				}
			}

			return domainName + "/" + targetDataMapping.get(recipientRole);
		}

		return domainName;

	}

	private void addManagerRecipientUsersIfExists(String rootOrg,Map<String, List<String>> recipients, String eventId) {
		// If manager list already exist in event raised then no need to fetch the
		// manager for the user
		if (recipients.get(LexConstants.MANAGER) != null && !recipients.get(LexConstants.MANAGER).isEmpty())
			return;

		// check if event has a manager recipientrole
		List<EventRecipientsProjection> eventRecipients = recipientTagsRepo.findAllByTagsPrimaryKeyEventId(eventId);
		boolean hasManagerRecipient = eventRecipients.stream()
				.anyMatch(eventRecipient -> eventRecipient.getRecipient().equals(LexConstants.MANAGER));
		if (!hasManagerRecipient)
			return;

		List<String> userIds = new ArrayList<>();
		for (String recipientRole : recipients.keySet()) {
			if (recipients.get(recipientRole) != null)
				userIds.addAll(recipients.get(recipientRole));
		}

		Map<String, String> userManagerMap = userInfoService.fetchManager(rootOrg, userIds);
		if(!userManagerMap.values().isEmpty())
			recipients.put(LexConstants.MANAGER, new ArrayList<>(userManagerMap.values()));

	}

}