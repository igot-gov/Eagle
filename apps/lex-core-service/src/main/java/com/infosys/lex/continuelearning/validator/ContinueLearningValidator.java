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
package com.infosys.lex.continuelearning.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.exceptions.PagingStateException;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Component
public class ContinueLearningValidator {

	@Autowired
	UserUtilityService utilityService;

	@Autowired
	ContentService contentService;

	// Pid Done
	public void validateUser(String rootOrg, String userId) throws Exception {
		if (!utilityService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.userid");
		}
	}

	public void validateContextPathId(String contextPathId) throws Exception {
		if (!contextPathId.equals("all")) {
			List<Map<String, Object>> sourceList = contentService.getMetaByIDListandSource(Arrays.asList(contextPathId),
					new String[] { "identifier" }, null);
			if (sourceList == null || sourceList.isEmpty()) {
				throw new InvalidDataInputException("invalid.contextpathid");
			}
		}
	}

	public void validatePageSize(String pageSize) {
		try {
			if (Integer.parseInt(pageSize) > 25000 || Integer.parseInt(pageSize) <= 0) {
				throw new InvalidDataInputException("pagesize.invalid");
			}
		} catch (NumberFormatException e) {
			throw new InvalidDataInputException("pagesize.type.invalid",e);
		}
	}

	public void validatePageStatus(String pageStatus) {
		try {
			if (!pageStatus.equals("0")) {
				PagingState.fromString(pageStatus);
			}
		} catch (PagingStateException e) {
			throw new InvalidDataInputException("pagestate.invalid",e);
		}
	}
}
