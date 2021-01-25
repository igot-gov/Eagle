

package org.sunbird.scoringengine.repository.cassandra.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface ScoreQualifierRepository extends CassandraRepository<ScoreQualifier, String> {


	@Query("SELECT * from score_qualifier where root_org= ?0 and org= ?1 and ref_criteria_id= ?2;")
	public List<ScoreQualifier> findQualifiersByCriteria(String rootOrg, String org, String criteriaId);


}
