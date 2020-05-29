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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.NotFoundException;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.infosys.lex.notification.bodhi.repository.UserDeviceByDeviceTokenRepo;
import com.infosys.lex.notification.bodhi.repository.UserDeviceRepository;
import com.infosys.lex.notification.configuration.AWSConfig;
import com.infosys.lex.notification.dto.PushNotificationRequest;
import com.infosys.lex.notification.entity.SNSNotificationConfig;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.model.cassandra.UserNotificationDevice;
import com.infosys.lex.notification.model.cassandra.UserNotificationDeviceKey;
import com.infosys.lex.notification.projection.UserDeviceArnsProjection;
import com.infosys.lex.notification.repository.SNSConfigRepository;
import com.infosys.lex.notification.service.NotificationConsumerUtilService;
import com.infosys.lex.notification.service.PushService;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.util.LexConstants;
import com.infosys.lex.notification.util.LexNotificationLogger;
import com.infosys.lex.notification.util.PushNotificationUtil;

@Service
public class PushServiceImpl implements PushService {

	@Autowired
	UserInformationService userInfoService;

	@Autowired
	AWSConfig awsConfig;

	@Autowired
	UserDeviceRepository userDeviceRepo;

	@Autowired
	UserDeviceByDeviceTokenRepo userDeviceTokenRepo;

	@Autowired
	NotificationConsumerUtilService utilService;

	@Autowired
	SNSConfigRepository snsConfigRepo;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	private static final String UPDATED_BY_USER = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.PushService#generateARN(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */

	@Override
	public Map<String, Object> generateARN(String userID, String rootOrg, String deviceToken, String endpointPlatform,
			String previousDeviceToken) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();
		endpointPlatform = endpointPlatform.trim();
		if (!userInfoService.validateUser(rootOrg, userID))
			throw new InvalidDataInputException("invalid.user", null);

		SNSNotificationConfig snsConfig = snsConfigRepo.findById(endpointPlatform).orElse(null);
		if (snsConfig == null)
			throw new InvalidDataInputException("No sns config for given platform", null);

		String notificationPlatform = snsConfig.getNotificationPlatform();
		String platformArn = snsConfig.getPlatformArn();

		if (!LexConstants.VALID_NOTFICATION_PLATFORMS.contains(notificationPlatform))
			throw new ApplicationLogicException("Invalid platform arn found for given endpoint : " + endpointPlatform);

		if (platformArn != null) {
			this.updateUserDeviceToken(rootOrg, userID, platformArn, deviceToken, endpointPlatform,
					notificationPlatform, previousDeviceToken);
			returnMap.put("result", "End-point ARN successfully generated!");
		} else {
			logger.error("End-point ARN generation failed -  AWS error!");
			throw new ApplicationLogicException("End-point ARN generation failed -  No platform ARN found!");
		}
		return returnMap;
	}

	/**
	 * Deletes the  endpoint for the device token and then delets the user device record
	 * for db
	 */
	@Override
	public void deleteUserDeviceArn(String rootOrg, String userId, String deviceToken) {

		UserNotificationDeviceKey key = new UserNotificationDeviceKey(rootOrg, userId, deviceToken);
		UserNotificationDevice userDevice = userDeviceRepo.findById(key).orElse(null);
		if (userDevice != null && userDevice.getEndpointArn() != null) {
			// delete endpoint arn from sns
			this.deleteSNSEndpointArn(userDevice.getEndpointArn());

			// delete user device mapping from table
			userDeviceRepo.deleteById(key);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.notification.service.PushService#sendPush
	 */
	@Override
	public void sendPush(PushNotificationRequest pushNotificationEvent) {
		String endpointArn;
		String notificationPlatform;
		List<UserDeviceArnsProjection> arns = userDeviceRepo
				.findAllByKeyRootOrgAndKeyUserId(pushNotificationEvent.getRootOrg(), pushNotificationEvent.getUserId());

		if (arns.isEmpty())
			return;

		for (UserDeviceArnsProjection arn : arns) {
			notificationPlatform = arn.getNotificationPlatform();
			endpointArn = arn.getEndpointArn();
			if (notificationPlatform == null || notificationPlatform.isEmpty() || endpointArn == null
					|| endpointArn.isEmpty()) {
				logger.error("devicePlatform or endpointarn is empty for UserId : " + arn.getUserId());
				continue;
			}
			try {
				String message = "{\r\n  \"APNS_SANDBOX\": \"{\\\"aps\\\":{\\\"alert\\\":\\\"Sample message for iOS development endpoints\\\"}}\"\r\n}";
				message = this.fetchMessageForPlatformType(pushNotificationEvent.getSubject(),
						pushNotificationEvent.getBody(), pushNotificationEvent.getTargetUrl(), notificationPlatform);

				PublishRequest request = new PublishRequest();
				System.out.println(message);
				request.setTargetArn(endpointArn);
				request.setMessage(message);
				request.setMessageStructure("json");
				awsConfig.snsClient().publish(request);
			} catch (Exception ex) {
				logger.error(ex);
			}

		}
	}

	/*
	 * If previousDeviceToken we update the existing device token with new one for the deviceArn
	 * else new deviceArn is created for the device token and stored mapped to userId 
	 */
	private void updateUserDeviceToken(String rootOrg, String userId, String platformArn, String deviceToken,
			String endpointPlatform, String notificationPlatform, String previousDeviceToken) {
		UserDeviceArnsProjection previousDevice = null;
		try {
			// check if any user exists for the given devicetoken
			// if devicetoken exists we need to delete existing device token and update
			// device token for the user
			if (previousDeviceToken != null)
				previousDevice = userDeviceTokenRepo.findByKeyDeviceToken(previousDeviceToken);
			if (previousDevice != null) {
				String deviceArn = previousDevice.getEndpointArn();
				this.deleteNotificationDeviceRecord(previousDevice.getRootOrg(), previousDevice.getUserId(),
						previousDeviceToken);

				this.validateAndUpdateNotificationDevice(rootOrg, userId, deviceToken, deviceArn, endpointPlatform,
						notificationPlatform, platformArn);

			} else {

				UserDeviceArnsProjection existingDevice = userDeviceTokenRepo.findByKeyDeviceToken(deviceToken);
				if (existingDevice != null) {
					if (existingDevice.getRootOrg().equals(rootOrg) && existingDevice.getUserId().equals(userId)) {
						String deviceArn = existingDevice.getEndpointArn();
						this.validateAndUpdateNotificationDevice(rootOrg, userId, deviceToken, deviceArn,
								endpointPlatform, notificationPlatform, platformArn);

					} else {
						String deviceArn = existingDevice.getEndpointArn();

						this.deleteNotificationDeviceRecord(existingDevice.getRootOrg(), existingDevice.getUserId(),
								deviceToken);
						this.validateAndUpdateNotificationDevice(rootOrg, userId, deviceToken, deviceArn,
								endpointPlatform, notificationPlatform, platformArn);

					}

				} else {
					String deviceArn = this.createEndpoint(platformArn, deviceToken);

					this.saveNotificationDeviceRecord(rootOrg, userId, deviceToken, endpointPlatform,
							notificationPlatform, deviceArn);
				}

			}
		} catch (Exception ex) {
			logger.error(ex);
			throw new ApplicationLogicException("Error updating user device arn. Ex  : " + ex.getMessage());
		}

	}

	/*
	 * This method creates the endpoint arn for the given device token with platformAnr
	 * Note :  This method handles the exception if the deviceToken mapping already exists in the 
	 * 		sns for the platformarn.
	 */
	private String createEndpoint(String platformArn, String token) {

		String endpointArn = null;
		try {
			CreatePlatformEndpointRequest cpeReq = new CreatePlatformEndpointRequest()
					.withPlatformApplicationArn(platformArn).withToken(token);
			CreatePlatformEndpointResult cpeRes = awsConfig.snsClient().createPlatformEndpoint(cpeReq);
			endpointArn = cpeRes.getEndpointArn();
		} catch (InvalidParameterException ipe) {
			String message = ipe.getErrorMessage();
			System.out.println("Exception message: " + message);
			Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " + "with the same [Tt]oken.*");
			Matcher m = p.matcher(message);
			if (!m.matches()) {
				throw ipe;
			}
		}
		return endpointArn;
	}

	/*This method checks if the deviceArn exists for the given platformArn 
	 * if exists then its device token is updated and enabled else if doesnt exist the endpoint arn is created.
	 */
	private void validateAndUpdateNotificationDevice(String rootOrg, String userId, String deviceToken,
			String deviceArn, String endpointPlatform, String notificationPlatform, String platformArn) {
		boolean createNeeded = false;
		try {
			// validate the device token with sns
			Map<String, String> attribs = new HashMap<>();
			attribs.put("Token", deviceToken);
			attribs.put("Enabled", "true");
			SetEndpointAttributesRequest saeReq = new SetEndpointAttributesRequest().withEndpointArn(deviceArn)
					.withAttributes(attribs);
			awsConfig.snsClient().setEndpointAttributes(saeReq);
		} catch (NotFoundException nfe) {
			// We had a stored ARN, but the platform endpoint associated with it
			// disappeared. Recreate it.
			createNeeded = true;
		}
		if (createNeeded)
			deviceArn = this.createEndpoint(platformArn, deviceToken);

		this.saveNotificationDeviceRecord(rootOrg, userId, deviceToken, endpointPlatform, notificationPlatform,
				deviceArn);

	}

	private void deleteNotificationDeviceRecord(String rootOrg, String userId, String deviceToken) {
		// delete previous device token data
		UserNotificationDeviceKey Key = new UserNotificationDeviceKey(rootOrg, userId, deviceToken);
		userDeviceRepo.deleteById(Key);

	}

	/*
	 * Updates or inserts user device token
	 */
	private void saveNotificationDeviceRecord(String rootOrg, String userId, String deviceToken,
			String endpointPlatform, String notificationPlatform, String deviceArn) {
		UserNotificationDeviceKey key = new UserNotificationDeviceKey(rootOrg, userId, deviceToken);
		UserNotificationDevice userDevice = new UserNotificationDevice(key, deviceArn, endpointPlatform,
				notificationPlatform, UPDATED_BY_USER, new Date());
		userDeviceRepo.save(userDevice);
	}

	/*
	 * This method fetches the stringified message payload for platformtype
	 */
	private String fetchMessageForPlatformType(String subject, String body, String url, String notificationPlatform)
			throws JSONException {
		String message = "";
		if (notificationPlatform.equalsIgnoreCase(LexConstants.FIREBASE))
			message = PushNotificationUtil.getFirebaseJson(subject, body, url);
		else if (notificationPlatform.equalsIgnoreCase(LexConstants.APNS_SANDBOX))
			message = PushNotificationUtil.getAPNSSandboxJson(subject, body, url);
		else if (notificationPlatform.equalsIgnoreCase(LexConstants.APNS))
			message = PushNotificationUtil.getAPNSJson(subject, body, url);
		return message;
	}

	private void deleteSNSEndpointArn(String endpointArn) {
		try {
			DeleteEndpointRequest deleteRequest = new DeleteEndpointRequest();
			deleteRequest.setEndpointArn(endpointArn);
			awsConfig.snsClient().deleteEndpoint(deleteRequest);
		} catch (Exception ex) {
			logger.error(ex);
		}

	}

}