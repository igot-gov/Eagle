/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.bodhi.repo.AppConfigRepository;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Service
public class AppConfigServiceImpl implements AppConfigService {

	@Autowired
	AppConfigRepository appConfigRepo;

	@Override
	public Map<String, Object> getConfig(String rootOrg, List<String> keys) {

		if (keys == null || keys.isEmpty()) {
			throw new InvalidDataInputException("invalid.keys");
		}

		List<Map<String, Object>> config = appConfigRepo.findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(rootOrg, keys);
		if (config == null || config.isEmpty()) {
			throw new InvalidDataInputException("invalid.keys");
		}

		Map<String, Object> returnMap = new HashMap<>();
		for (Map<String, Object> conf : config) {
			returnMap.put(conf.get("key").toString(), conf.get("value").toString());
		}

		return returnMap;
	}

	@Cacheable(value = "config_key_value", key = "#rootOrg.concat('-').concat(#key)", unless = "#result == null")
	@Override
	public String getConfigForKey(String rootOrg, String key) {
		if (key == null || key.trim().isEmpty())
			throw new InvalidDataInputException("Invalid key");

		Map<String,Object>  appConfig = appConfigRepo.findByPrimaryKeyRootOrgAndPrimaryKeyKey(rootOrg, key);
		if(appConfig == null || appConfig.isEmpty())
			 throw new ApplicationLogicError("App config key not found") ;
		else 
			return appConfig.get("value").toString();

	}

}
