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
package com.infosys.lex.progress.bodhi.repo;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentProgressRepository extends
		CassandraRepository<ContentProgressModel, ContentProgressPrimaryKeyModel>, ContentProgressRepositoryCustom {

	/**
	 * fetches the progress for the given content ids
	 * 
	 * @param userId
	 * @param contentTypes
	 * @param contentIds
	 * @return
	 */

	@Query("select content_id,progress, last_ts from user_content_progress where root_org=?0 and user_id=?1 and content_type in ?2 and content_id in ?3")
	public List<ContentProgressModel> getProgress(String rootOrg, String userId, List<String> contentTypes,
			List<String> contentIds);

	/**
	 * find all progress for all content ids
	 * @param rootOrg
	 * @param userId
	 * @param contentIds
	 * @return
	 */
	@Query("select progress, content_id, first_accessed_on, last_accessed_on from user_content_progress where root_org=?0 and user_id=?1 and content_type in ('Resource', 'Collection', 'Course', 'Learning Path', 'Knowledge Artifact') and content_id in ?2;")
	public List<ContentProgressModel> findProgress(String rootOrg, String userId, List<String> contentIds );

	@Query("select progress, time_spent, content_id, first_accessed_on, last_accessed_on from user_content_progress where root_org=?0 and user_id=?1 and content_type in ('Resource', 'Collection', 'Learning Path', 'Knowledge Artifact');")
	public List<ContentProgressModel> findTime(String rootOrg, String userId);


	/**
	 * fetches the progress for all
	 * 
	 * @param userId
	 * @param contentTypes
	 * @return
	 */
	@Query("select content_id,progress from user_content_progress where root_org=?0 and user_id=?1 and content_type in ?2")
	public List<ContentProgressModel> getProgressForAll(String rootOrg, String userId, List<String> contentTypes);

	/**
	 * fetches the progress for goals given a list of users and content ids
	 * 
	 * @param userId
	 * @param contentTypes
	 * @param contentIds
	 * @return
	 */

	@Query("select user_id,content_id,progress from user_content_progress where root_org=?0 and user_id in ?1 and content_type in ?2 and content_id in ?3")
	public List<ContentProgressModel> getProgressForGoals(String rootOrg, List<String> userId,
			List<String> contentTypes, List<String> contentIds);

	/**
	 * fetches the first accessed on date for a user's content
	 * 
	 * @param userId
	 * @param contentTypes
	 * @param contentIds
	 * @return
	 */
	@Query("select content_id,progress,first_accessed_on from user_content_progress where user_id=?0 and content_type in ?1 and content_id = ?2")
	public List<ContentProgressModel> getFirstAccessedOn(String userId, List<String> contentTypes, String contentIds);

	public List<ContentProgressModel> findByPrimaryKeyRootOrgAndPrimaryKeyUserIdAndPrimaryKeyContentType(String rootOrg,
			String userId, String contentType);
}
