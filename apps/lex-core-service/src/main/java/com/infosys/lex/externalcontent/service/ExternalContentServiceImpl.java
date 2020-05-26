/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.externalcontent.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.externalcontent.bodhi.repo.ExternalContentRepo;
import com.infosys.lex.externalcontent.entities.ContentKey;
import com.infosys.lex.externalcontent.entities.ExternalContent;


@Service
public class ExternalContentServiceImpl implements ExternalContentService {

	@Autowired
	ExternalContentRepo externalRepo;

	@Autowired
	UserUtilityService userUtil;

	@Override
	public Map<String, Object> getUser(String rootOrg, String source, String userId) throws Exception {

		Boolean userExists = userUtil.validateUser(rootOrg, userId);
		if (!userExists) {
			throw new BadRequestException("invalid.user");
		}
		Map<String, Object> returnObject = new HashMap<String, Object>();
		ContentKey contentKey = new ContentKey();
		contentKey.setUserId(userId);
		contentKey.setRootOrg(rootOrg);
		contentKey.setSource(source);
		Optional<ExternalContent> externalContent = externalRepo.findById(contentKey);
		if (!externalContent.isPresent()) {
			returnObject.put("hasAccess", false);
			return returnObject;
		}
		returnObject.put("hasAccess", true);
		return returnObject;

	}

}
