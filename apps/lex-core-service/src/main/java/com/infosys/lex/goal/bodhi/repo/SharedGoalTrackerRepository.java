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
package com.infosys.lex.goal.bodhi.repo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedGoalTrackerRepository extends CassandraRepository<SharedGoalTracker, SharedGoalTrackerKey> {

	/**
	 * get shared goals by shared by, goal type and id
	 * 
	 * @param userId   &nbsp;The user's id
	 * @param goalType &nbsp; The type of the goal
	 * @param goalId   &nbsp; The id of the shared goal
	 * 
	 * @return Data of the query.
	 */
//	@Query("select * from mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3")
//	public List<SharedGoalTracker> fetchRecordsForGoalSharedByUser(String rootOrg, String userId, String goalType,
//			UUID goalId);

	@Query("select * from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3")
	public List<SharedGoalTracker> fetchRecordsForGoalSharedByUser(String rootOrg, String userId, String goalType,
			UUID goalId);

	@Query("select * from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3 and version=?4")
	public List<SharedGoalTracker> fetchCommonGoalRecordsForGoalSharedByUser(String rootOrg, String userId,
			String goalType, UUID goalId, Float version);

	/**
	 * get shared goals by shared by and goal type
	 * 
	 * @param userId
	 * @param goalType
	 * @return
	 */
//	@Query("select goal_content_id,goal_title,goal_desc,goal_id from mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2")
//	public List<Map<String, Object>> fetchRecordsBySharedByAndGoalType(String rootOrg, String userId, String goalType);

	@Query("select goal_content_id,goal_title,goal_desc,goal_id from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2")
	public List<Map<String, Object>> fetchRecordsBySharedByAndGoalType(String rootOrg, String userId, String goalType);

	/**
	 * get shared goals by shared by
	 * 
	 * @param userId
	 * @return
	 */
//	@Query("select * from mv_shared_goals_tracker where root_org=?0 and shared_by=?1")
//	public List<SharedGoalTracker> fetchGoalsSharedByPerson(String rootOrg, String userId);

	@Query("select * from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1")
	public List<SharedGoalTracker> fetchGoalsSharedByPerson(String rootOrg, String userId);

	/**
	 * Used to fetch the goal's data as well as number of recipients count.
	 * 
	 * @param sharedBy &nbsp; The user's id
	 * @param goalType &nbsp; The type of the goal
	 * @param goalId   &nbsp; The id of the shared goal
	 * @return Data of the first hit
	 */
//	@Query("select goal_title, goal_desc, goal_content_id, goal_duration, version,count(*) from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3 group by root_org,shared_by,goal_type,goal_id")
//	public SharedGoalTracker getSharedGoalDataAndCount(String rootOrg, String sharedBy, String goalType, UUID goalId);

	@Query("select goal_title, goal_desc, goal_content_id, goal_duration, version,count(*) from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3 group by root_org,shared_by,goal_type,goal_id")
	public SharedGoalTracker getSharedGoalDataAndCount(String rootOrg, String sharedBy, String goalType, UUID goalId);

	@Query("select goal_title, goal_desc, goal_content_id, goal_duration, version,count(*) from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2 and goal_id=?3 group by root_org,shared_by,goal_type,goal_id")
	public SharedGoalTracker getCommonSharedGoalDataAndCount(String rootOrg, String sharedBy, String goalType,
			UUID goalId);

	/**
	 * get shared goals by shared by and goal type
	 * 
	 * @param userId
	 * @param goalType
	 * @return
	 */
//	@Query("select goal_content_id,goal_title,goal_desc,goal_id from mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2")
//	public List<SharedGoalTracker> fetchGoalRecordsBySharedByAndGoalType(String rootOrg, String userId,
//			String goalType);

	@Query("select goal_content_id,goal_title,goal_desc,goal_id from bodhi.mv_shared_goals_tracker where root_org=?0 and shared_by=?1 and goal_type=?2")
	public List<SharedGoalTracker> fetchGoalRecordsBySharedByAndGoalType(String rootOrg, String userId,
			String goalType);

}
