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
public interface UserGoalRepository extends CassandraRepository<UserGoal, UserGoalKey>, UserGoalCustomRepository {

	/**
	 * gets learning goals by user id and type
	 * 
	 * @param userId
	 * @param goalType
	 * @return
	 */
//	@Query("select goal_title,goal_content_id,goal_desc,goal_id,goal_type,goal_duration from user_learning_goals where root_org=?0 and user_email=?1 and goal_type=?2")
//	public List<Map<String, Object>> learningGoalsByEmailAndType(String rootOrg, String userId, String goalType);

	@Query("select goal_title,goal_content_id,goal_desc,goal_id,goal_type,goal_duration from user_learning_goals where root_org=?0 and user_email=?1 and goal_type=?2")
	public List<Map<String, Object>> learningGoalsByEmailAndType(String rootOrg, String userId, String goalType);

	/**
	 * gets user learning goals given a list of content ids
	 * 
	 * @param similarGoalResourceId
	 * @return
	 */
//	@Query("select * from user_learning_goals where goal_content_id contains ?0")
//	public List<Map<String, Object>> learningGoalsContainResources(String similarGoalResourceId);

	@Query("select * from user_learning_goals where goal_content_id contains ?0")
	public List<Map<String, Object>> learningGoalsContainResources(String similarGoalResourceId);

//-------------------------------------------------------------------------------------------------------------------------	

	/**
	 * fetch user goals by goal types and user id
	 * 
	 * @param goalTypes
	 * @param userUUID
	 * @return
	 */

	public List<Map<String, Object>> findByUserLearningGoalsPrimaryKeyRootOrgAndUserLearningGoalsPrimaryKeyUuidAndUserLearningGoalsPrimaryKeyGoalTypeIn(
			String rootOrg, String userUUID, List<String> goalTypes);

//	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 goal_type in ?2")
//	public List<Map<String, Object>> fetchByGoalTypesAndUuid(String rootOrg, String userUUID, List<String> goalTypes);

	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 goal_type in ?2")
	public List<Map<String, Object>> fetchByGoalTypesAndUuid(String rootOrg, String userUUID, List<String> goalTypes);

	/**
	 * fetch user goals by goal types and user id
	 * 
	 * @param goalTypes
	 * @param userUUID
	 * @return
	 */
//	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 and goal_type in ?2")
//	public List<UserGoal> fetchByLearningGoalTypesAndUuid(String rootOrg, String userUUID, List<String> goalTypes);

	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 and goal_type in ?2")
	public List<UserGoal> fetchByLearningGoalTypesAndUuid(String rootOrg, String userUUID, List<String> goalTypes);

	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 and goal_type=?2 and goal_id=?3 and version=?4")
	public UserGoal validateGoalInULG(String rootOrg, String userId, String goalType, UUID goalId, Float version);

//	public void removeLearningGoals() {
//		bulkExecute()
//	}

}
