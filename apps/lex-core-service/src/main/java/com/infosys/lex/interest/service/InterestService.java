/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

public interface InterestService {

	/**
	 * get interests of users
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getInterest(String rootOrg, String userId) throws Exception;

	/**
	 * delete interests of user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 * @throws Exception
	 */
	public String delete(String rootOrg, String userId, String interest) throws Exception;

	/**
	 * add or create interest
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 * @throws Exception
	 */
	public String upsert(String rootOrg, @NotNull String userId, @NotNull String interest) throws Exception;

	/**
	 * autocompletes users interests
	 * 
	 * @param rootOrg
	 * @param org
	 * @param language
	 * @param query
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<String> autoComplete(String rootOrg, String org, @NotNull String language, String query, String type)
			throws Exception;

	/**
	 * get suggested interests
	 * 
	 * @param rootOrg
	 * @param userid
	 * @param org
	 * @param language
	 * @return
	 * @throws Exception
	 */

	public List<String> suggestedComplete(String rootOrg, String userid, String org, @NotNull String language)
			throws Exception;
}
