package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserSharedGoals;
import com.infosys.lex.ext.Models.Cassandra.UserSharedGoalsPrimaryKey;

@Repository
public interface UserSharedGoalsRepository
		extends CassandraRepository<UserSharedGoals, UserSharedGoalsPrimaryKey>, UserSharedGoalsRepositoryCustom {

	@Query("select * from user_shared_goals where root_org=?0 and shared_with=?1 and goal_type=?2 and status = ?3")
	public List<UserSharedGoals> sharedGoalsByRootOrgUserIdAndTypeAndStatus(String rootOrg, String userId,
			String goalType, Integer status);

	@Query("select shared_with,goal_id,status from user_shared_goals where root_org=?0 and shared_with in ?1 and goal_type=?2")
	public List<UserSharedGoals> sharedGoalsIdsByEmailAndType(String rootOrg, List<String> userIds, String goalType);

}
