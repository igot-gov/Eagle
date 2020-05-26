/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.service;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface UserService {

	Map<String, Object> getUserPreferences(String rootOrg, String userId) throws JsonMappingException, IOException;

	void setUserPreferences(String rootOrg, String userId, Map<String, Object> preferences)
			throws JsonProcessingException;
}
