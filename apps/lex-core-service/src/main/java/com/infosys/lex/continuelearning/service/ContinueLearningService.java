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
package com.infosys.lex.continuelearning.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.infosys.lex.continuelearning.dto.ContinueLearningDTO;

/**
 * @author mannmath.samantaray
 *
 */
public interface ContinueLearningService {

	/**
	 * This method is for upserting data of a specific user to DB.
	 * 
	 * @param userId &nbsp;user's id
	 * @param data   &nbsp;data to be upserted
	 * @return status
	 */
	Map<String, Object> upsertLearningData(String rootOrg, String userId, @Valid ContinueLearningDTO data)
			throws Exception;

	/**
	 * This method is for fetching required continue learning data for a user in
	 * paginated manner.
	 * 
	 * @param userId        &nbsp;user's id
	 * @param sourceFields  &nbsp;meta fields if requested
	 * @param contextPathId &nbsp;specific context path
	 * @param pageSize      &nbsp;size of page
	 * @param pageState     &nbsp;last state of page(if available)
	 * @return user data as per request
	 */
	Map<String, Object> getLearningData(String rootOrg, String userId, Set<String> sourceFields, String contextPathId,
			String pageSize, String pageState, String isCompleted, String isInIntranet, String isStandAlone,
			String resourceType) throws Exception;

	/**
	 * This method will get the final learning data after applying all the given
	 * filters.
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param sourceFields
	 * @param contextPathId
	 * @param pageSize
	 * @param pageState
	 * @param isCompleted
	 * @param isInIntranet
	 * @param isStandAlone
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> getLearningDataWithFilters(String rootOrg, String userId, Set<String> sourceFields,
			String contextPathId, String pageSize, String pageState, String isCompleted, String isInIntranet,
			String isStandAlone, List<String> contentType) throws Exception;

	Map<String, Object> getLearningContent(String rootOrg, String userId, Set<String> sourceFields,
										   String contextPathId, String pageSize, String pageState, String isCompleted, String isInIntranet,
										   String isStandAlone, String resourceType) throws Exception;


}
