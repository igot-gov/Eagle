/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.notification.exception.InvalidDataInputException;
import com.infosys.lex.notification.service.UserValidation;
import com.infosys.lex.notification.sunbird.repository.UserMVModel;
import com.infosys.lex.notification.sunbird.repository.UserMVRepository;
import com.infosys.lex.notification.sunbird.repository.UserModel;
import com.infosys.lex.notification.sunbird.repository.UserRepository;
import com.infosys.lex.notification.util.LexNotificationLogger;


//TODO integrate pid service
@Service
public class UserValidationImpl implements UserValidation {

//	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	UserMVRepository userMVRepository;
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * .String, java.lang.String)
//	 */
//	@Override
//	public String checkUserExistence(String userId, String userIdType) throws Exception {
//		if (userIdType.toLowerCase().equals("uuid")) {
//			Optional<UserModel> user = userRepository.findById(userId);
//			if (user.orElse(null) == null)
//				throw new InvalidDataInputException("Invalid User Id", null);
//			// getting uuid from email
//		} else if (userIdType.toLowerCase().equals("email")) {
//			Optional<UserMVModel> user = userMVRepository.findById(userId.toLowerCase());
//
//			UserMVModel userMVModel = user.orElse(null);
//			if (userMVModel != null) {
//				userId = userMVModel.getId();
//			} else {
//				throw new InvalidDataInputException("Invalid User Id", null);
//			}
//		}
//		return userId;
//
//	}
//
//	@Override
//	public String getValidUser(String userId, String userIdType) throws Exception {
//		if (userIdType.toLowerCase().equals("uuid")) {
//			Optional<UserModel> user = userRepository.findById(userId);
//			if (user.orElse(null) == null) {
//				logger.error(new Exception("Invalid UserId " + userId));
//				return null;
//			}
//			// getting uuid from email
//		} else if (userIdType.toLowerCase().equals("email")) {
//			Optional<UserMVModel> user = userMVRepository.findById(userId.toLowerCase());
//
//			UserMVModel userMVModel = user.orElse(null);
//			if (userMVModel != null)
//				userId = userMVModel.getId();
//			else {
//				logger.error(new Exception("Invalid UserId " + userId));
//				return null;
//			}
//		}
//		return userId;
//	}
}
