/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface ScoreQualifierRepository extends CassandraRepository<ScoreQualifier, String> {


	@Query("SELECT * from score_qualifier where root_org= ?0 and org= ?1 and ref_criteria_id= ?2;")
	public List<ScoreQualifier> findQualifiersByCriteria(String rootOrg, String org, String criteriaId);


}
