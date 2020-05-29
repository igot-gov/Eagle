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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.infosys.lex.notification.configuration.AWSConfig;
import com.infosys.lex.notification.service.SmsService;

@Service
public class SmsServiceImpl implements SmsService {

	@Autowired
	AWSConfig awsConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.lex.notification.service.SmsService#sendSMS(java.lang.String,
	 * java.lang.String)
	 */

	@Override
	public String sendSMS(String mobileNo, String message) {

		mobileNo = "918198069959";
		Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();

		// The sender ID shown on the device.
		smsAttributes.put("AWS.SNS.SMS.SenderID",
				new MessageAttributeValue().withStringValue("ININF").withDataType("String"));

		// Sets the type to promotional.
		smsAttributes.put("AWS.SNS.SMS.SMSType",
				new MessageAttributeValue().withStringValue("Transactional").withDataType("String"));

		PublishResult result = awsConfig.snsClient().publish(new PublishRequest().withMessage(message)
				.withPhoneNumber(mobileNo).withMessageAttributes(smsAttributes));

		System.out.println(result.getMessageId());
		return result.getMessageId();
	}

}
