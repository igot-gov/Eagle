/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.leaderboard.bodhi.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderBoardRankRepository extends CassandraRepository<LeaderBoardRank,LeaderBoardRankPrimaryKey> {

	
	public List<Map<String,Object>> findByLeaderBoardRankKeyRootOrgAndLeaderBoardRankKeyRankAndLeaderBoardRankKeyDurationTypeAndLeaderBoardRankKeyLeaderBoardType(String rootOrg,Integer rank,String durationType,String leaderBoardType);
	
	
	@Query("select * from leaderboard_rank where root_org = ?0 and rank in ?1 and duration_type = ?2 and  leaderboard_type = ?3 and  leaderboard_year in ?4 and duration_value in ?5 ")
	public List<Map<String,Object>> findAllLeadersByRankList(String rootOrg,List<Integer> rankList,String durationType,String leaderBoardType,List<Integer> leaderBoardYear,List<Integer> durationValue);


}
