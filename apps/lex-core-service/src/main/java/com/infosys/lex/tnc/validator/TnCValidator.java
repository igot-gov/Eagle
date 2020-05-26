/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */
package com.infosys.lex.tnc.validator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.infosys.lex.common.service.UserServiceImpl;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Component
public class TnCValidator {

	@Autowired
	UserUtilityService userUtilitySvc;

	@Autowired
	UserServiceImpl userSvc;

	public void validateUserId(String rootOrg, String userId) throws Exception {
		if (!userId.equalsIgnoreCase("default")) {
			if (!userUtilitySvc.validateUser(rootOrg, userId)) {
				throw new BadRequestException("invalid.userid");
			}
		}
	}

	public String getLanguage(String rootOrg, String userId, List<String> language) throws Exception {
		if (language == null) {
			try {
				Map<String, Object> userPreferenceData = userSvc.getUserPreferences(rootOrg, userId);
				if (userPreferenceData != null) {
					String preferredlanguage = userPreferenceData.getOrDefault("selectedLanguage", null).toString();
					if (preferredlanguage != null && !preferredlanguage.isEmpty()) {
						return preferredlanguage;
					}
				}
				return "en";
			} catch (Exception e) {
				return "en";
			}
		} else {
			return language.get(0);
		}
	}

	public void validateTermsSize(List<Map<String, Object>> termsAccepted) {
		if (termsAccepted.size() < 1 || termsAccepted.size() > 2) {
			throw new InvalidDataInputException("invalid.numberofterms");
		}

		boolean dpFlag = false, tncFlag = false;
		if (termsAccepted.size() == 2) {
			for (Map<String, Object> map : termsAccepted) {
				if (map.get("docName").toString().equalsIgnoreCase("Generic T&C")) {
					tncFlag = true;
				} else if (map.get("docName").toString().equalsIgnoreCase("Data Privacy")) {
					dpFlag = true;
				}
			}
			if (!tncFlag || !dpFlag) {
				throw new InvalidDataInputException("incorrect.duplicateterms");
			}
		}
	}

}
