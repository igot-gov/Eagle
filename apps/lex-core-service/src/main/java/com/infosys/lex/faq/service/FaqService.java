/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.service;

import java.util.List;
import java.util.Map;

import com.infosys.lex.faq.dto.FaqDto;

public interface FaqService {

	/**
	 * This function return all the question and answer of a group and a langCode
	 * 
	 * @return list of all the question and answer of a group
	 * @throws Exception
	 */
	List<Map<String, Object>> getQuestionAnswer(String rootOrg, String langCode, String groupId) throws Exception;

	/**
	 * Returns all the groupIds of a langCode
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getGroupId(String rootOrg, String langCode);

	/**
	 * Updates all the question and answers of a group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	void updateQuestionAnswer(String rootOrg, String langCode, String groupId, List<FaqDto> faqDto);

	/**
	 * Deletes the group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	void deleteGroup(String rootOrg, String langCode, String groupId);

	/**
	 * creates a new group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupName
	 * @param createdBy
	 * @return
	 */
	void createGroup(String rootOrg, String langCode, String groupName, String createdBy);

	/**
	 * searches for the query all the groups of a language
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param searchMap
	 * @return
	 */
	List<Map<String, Object>> searchText(String rootOrg, String langCode, Map<String, Object> searchMap);

}
