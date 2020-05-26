/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.likes.service;

import java.util.List;
import java.util.Map;

public interface LikesService {

	/**
	 * Like a content id
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */

	public Map<String, Object> upsertLikes(String rootOrg, String userId, String contentId) throws Exception;

	/**
	 * Get List of liked content ids
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */

	public List<String> getLikes(String rootOrg, String userId) throws Exception;

	/**
	 * Unlike a content id
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> deleteLikesData(String rootOrg, String userId, String contentId) throws Exception;

	/**
	 * check if a content id is liked or not
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getLikesData(String rootOrg, String userId, String contentId) throws Exception;

	/**
	 * Get total likes of a contentId
	 * 
	 * @param rootOrg
	 * @param contentList
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getTotalLikes(String rootOrg, Map<String, Object> contentId);

}
