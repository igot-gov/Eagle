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
package com.infosys.lex.learninghistory.service;

import java.util.List;
import java.util.Map;

public interface LearningHistoryService {

	
	
	/**
	 * get progress and meta for a list of content ids
	 * @param userUUID
	 * @param contentIds
	 * @param userIdType
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUserContentListProgress(String rootOrg,String userUUID, List<String> contentIds) throws Exception;
	
	/**
	 * gets progress for a list of content ids or all ids
	 * @param userUUID
	 * @param contentIds
	 * @param userIdType
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getContentListStrProgress(String rootOrg,String userUUID, String contentIds)
			throws Exception;

	
	
	/**
	 * gets the progress for a user. Paginated
	 * @param userUUID
	 * @param pageSize
	 * @param pageState
	 * @param status
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	
	public Map<String, Object> getUserCourseProgress(String rootOrg,String userUUID, Integer pageSize, String pageState, String status,
			String contentType) throws Exception;

	
	Map<String, Object> getProgressForContentListMap(String rootOrg, String userUUID, Map<String, Object> contentIdStr)
			throws Exception;
	
}
