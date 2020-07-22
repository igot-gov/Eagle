/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.repository.cassandra.bodhi;

import com.infosys.recommendationservice.model.cassandra.UserPositionCompetency;
import com.infosys.recommendationservice.model.cassandra.UserPositionCompetencyPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface UserPositionCompetencyRepository
		extends CassandraRepository<UserPositionCompetency, UserPositionCompetencyPrimarykey> {

	@Query("SELECT * from user_position_competency where root_org= ?0 AND org= ?1 AND user_id= ?2 AND user_role=?3;")
	public List<UserPositionCompetency> findAllByUserAndPosition(String root_org, String org, String userId, String userrole);

	@Query("DELETE * from user_position_competency where root_org= ?0 AND org= ?1 AND user_id= ?2 AND user_role=?3;")
	public void deleteAllByUserAndPosition(String root_org, String org, String userId, String userrole);

//	@Query("Select * from content_work_flow where root_org= ?0 AND org= ?1;")
//	public List<ContentWorkFlowModel> findAllByRootOrg(String rootOrg, String org);
}
