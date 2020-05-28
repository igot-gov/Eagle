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

package com.infosys.lex.notification.service;

import java.util.Map;

import com.infosys.lex.notification.dto.PushNotificationRequest;

public interface PushService {

	/**
	 * generating ARNs for user
	 * 
	 * @param userID
	 * @param token
	 * @param userIdType
	 * @param platform
	 * @throws Exception
	 */

	Map<String, Object> generateARN(String userID, String rootOrg, String deviceToken, String endpointPlatform,
			String previousDeviceToken) throws Exception;



	/**
	 * this method sends push notification to all devices registered for the user.
	 * 
	 * @param pushNotificationEvent
	 * @throws Exception
	 */
	void sendPush(PushNotificationRequest pushNotificationEvent) throws Exception;



	void deleteUserDeviceArn(String rootOrg, String userId, String deviceToken);
	



	

}
