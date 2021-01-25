

package org.sunbird.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface ScoreCriteriaRepository extends CassandraRepository<EvaluationCriteria, String> {


	@Query("SELECT * from evaluation_criteria where root_org= ?0 and org= ?1 and criteria= ?2;")
	public EvaluationCriteria findCriteriaByName(String rootOrg, String org, String criteria);


}
