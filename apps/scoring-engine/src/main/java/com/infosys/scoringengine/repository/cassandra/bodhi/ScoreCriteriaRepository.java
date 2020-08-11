/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface ScoreCriteriaRepository extends CassandraRepository<EvaluationCriteria, String> {


	@Query("SELECT * from evaluation_criteria where root_org= ?0 and org= ?1 and criteria= ?2;")
	public EvaluationCriteria findCriteriaByName(String rootOrg, String org, String criteria);


}
