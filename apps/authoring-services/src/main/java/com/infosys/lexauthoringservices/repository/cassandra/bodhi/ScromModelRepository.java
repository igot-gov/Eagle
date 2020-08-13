/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

import com.infosys.lexauthoringservices.model.cassandra.ScromModel;
import com.infosys.lexauthoringservices.model.cassandra.ScromPrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface ScromModelRepository extends CassandraRepository<ScromModel, ScromPrimaryKey> {

	@Query("SELECT * from scrom_template where root_org= ?0 and org= ?1;")
	public List<ScromModel> findAllByRootOrgAndOrg(String rootOrg, String org);

	@Query("SELECT * from scrom_template where root_org= ?0 and org= ?1 and contentId= ?2 ;")
	public List<ScromModel> findByContentId(String rootOrg, String org, String contentId);

	@Query("SELECT * from scrom_template where root_org= ?0 and org= ?1 and userId= ?2 ;")
	public List<ScromModel> findByUserId(String rootOrg, String org, String userId);

}
