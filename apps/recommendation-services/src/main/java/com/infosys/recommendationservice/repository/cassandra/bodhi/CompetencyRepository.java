/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.repository.cassandra.bodhi;

import com.infosys.recommendationservice.model.cassandra.UserCompetency;
import com.infosys.recommendationservice.model.cassandra.UserCompetencyPrimarykey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface UserCompetencyRepository
		extends CassandraRepository<UserCompetency, UserCompetencyPrimarykey> {

//	@Query("SELECT * from content_work_flow where root_org= ?0 AND org= ?1 AND content_type= ?2;")
//	public ContentWorkFlowModel findByPrimaryKeyContentWorkFlow(String root_org, String org, String content_type);
//
//	@Query("Select * from content_work_flow where root_org= ?0 AND org= ?1;")
//	public List<ContentWorkFlowModel> findAllByRootOrg(String rootOrg, String org);
}
