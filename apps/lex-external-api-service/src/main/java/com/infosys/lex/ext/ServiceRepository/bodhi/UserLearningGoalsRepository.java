package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserLearningGoals;
import com.infosys.lex.ext.Models.Cassandra.UserLearningGoalsPrimaryKey;

@Repository
public interface UserLearningGoalsRepository
		extends CassandraRepository<UserLearningGoals, UserLearningGoalsPrimaryKey>, UserLearningGoalsRepositoryCustom {

	@Query("select * from user_learning_goals where root_org=?0 and user_id=?1 and goal_type=?2")
	public List<UserLearningGoals> learningGoalsByRootOrgAndUserIdAndType(String rootOrg, String userId,
			String goalType);

	@Query("select user_id,goal_id from user_learning_goals where root_org=?0 and user_id in ?1 and goal_type=?2")
	public List<UserLearningGoals> learningGoalsByRootOrgAndUserIdsAndGoalType(String rootOrg, List<String> userIds,
			String goalType);
}
