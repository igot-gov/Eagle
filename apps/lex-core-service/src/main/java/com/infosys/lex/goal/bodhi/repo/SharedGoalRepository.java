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
public interface SharedGoalRepository
		extends CassandraRepository<SharedGoal, SharedGoalKey>, SharedGoalCustomRepository {
	/**
	 * get user goals by shared by, goal type, id and status
	 * 
	 * @param sharedWith
	 * @param goalType
	 * @param goalId
	 * @param status
	 * @return
	 */
//	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and goal_id=?3 and status=?4")
//	public List<Map<String, Object>> getGoalsSharedToUser(String rootOrg, String sharedWith, String goalType,
//			UUID goalId, int status);

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and goal_id=?3 and status=?4")
	public List<Map<String, Object>> getGoalsSharedToUser(String rootOrg, String sharedWith, String goalType,
			UUID goalId, int status);

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and goal_id=?3 and status=?4")
	public SharedGoal getGoalsSharedWithUser(String rootOrg, String sharedWith, String goalType, UUID goalId,
			int status);

	/**
	 * get user goals by shared with and status
	 * 
	 * @param sharedWith
	 * @param status
	 * @return
	 */
//	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and status=?2")
//	public List<Map<String, Object>> getGoalsSharedToUserFilterStatus(String rootOrg, String sharedWith, int status);

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and status=?2")
	public List<Map<String, Object>> getGoalsSharedToUserFilterStatus(String rootOrg, String sharedWith, int status);

	/**
	 * get user goals by shared with, goal type and status
	 * 
	 * @param sharedWith
	 * @param goalType
	 * @param status
	 * @return
	 */
//	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and status=?3")
//	public List<SharedGoal> getGoalBySharedWithGoalTypeAndStatus(String rootOrg, String sharedWith, String goalType,
//			int status);

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and status=?3")
	public List<SharedGoal> getGoalBySharedWithGoalTypeAndStatus(String rootOrg, String sharedWith, String goalType,
			int status);

	/**
	 * get user goals by shared with and goal id
	 * 
	 * @param sharedWith
	 * @param goalId
	 * @return
	 */
//	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_id=?2")
//	public List<Map<String, Object>> getGoalBySharedWithAndGoalId(String rootOrg, String sharedWith, UUID goalId);
	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_id=?2")
	public List<Map<String, Object>> getGoalBySharedWithAndGoalId(String rootOrg, String sharedWith, UUID goalId);

	/**
	 * get user goals by shared with and status
	 * 
	 * @param sharedWith
	 * @param status
	 * @return
	 */
//	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and status=?2")
//	public List<SharedGoal> getGoalsSharedToUserFilterByStatus(String rootOrg, String sharedWith, int status);

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and status=?2")
	public List<SharedGoal> getGoalsSharedToUserFilterByStatus(String rootOrg, String sharedWith, int status);
}
