/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.service;
import java.io.IOException;
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
	 */
	public Map<String, Object> getInterest(String rootOrg, String userId);

	/**
	 * delete interests of user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 */
	public void deleteInterest(String rootOrg, String userId, Map<String,Object> interestMap);

	/**
	 * add or create interest
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 */
	public void upsertInterest(String rootOrg,  String userId,  Map<String,Object> interest);

	/**
	 * autocompletes users interests
	 * 
	 * @param rootOrg
	 * @param org
	 * @param language
	 * @param query
	 * @param type
	 * @return
	 * @throws IOException 
	 */
	public List<String> autoComplete(String rootOrg, String org, @NotNull String language, String query, String type) throws IOException;

	/**
	 * get suggested interests
	 * 
	 * @param rootOrg
	 * @param userid
	 * @param org
	 * @param language
	 * @return
	 * @throws IOException 
	 */

	public List<String> suggestedComplete(String rootOrg, String userid, String org,  String language) throws IOException;


	public void delete(String rootOrg, String userId, String interest);

	public String upsert(String rootOrg, String userId,  String interest) ;
}
