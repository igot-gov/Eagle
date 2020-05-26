/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.bodhi.repo.UserPreferencePrimaryKey;
import com.infosys.lex.common.bodhi.repo.UserPreferencesModel;
import com.infosys.lex.common.bodhi.repo.UserPreferencesRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserPreferencesRepository userPreferencesRepo;

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Map<String, Object> getUserPreferences(String rootOrg, String userId)
			throws JsonMappingException, IOException {

		Optional<UserPreferencesModel> userPreference = userPreferencesRepo
				.findById(new UserPreferencePrimaryKey(rootOrg, userId));

		if (!userPreference.isPresent())
			return new HashMap<>();

		return mapper.readValue(userPreference.get().getPreference(), new TypeReference<Object>() {
		});
	}

	@Override
	public void setUserPreferences(String rootOrg, String userId, Map<String, Object> preferences)
			throws JsonProcessingException {
		UserPreferencesModel userPreferences = new UserPreferencesModel(new UserPreferencePrimaryKey(rootOrg, userId),
				Calendar.getInstance().getTime(), mapper.writeValueAsString(preferences));
		userPreferencesRepo.save(userPreferences);

	}
}
