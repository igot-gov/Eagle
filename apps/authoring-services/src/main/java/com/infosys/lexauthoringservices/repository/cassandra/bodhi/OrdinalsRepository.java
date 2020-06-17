package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lexauthoringservices.model.cassandra.OrdinalsModel;
import com.infosys.lexauthoringservices.model.cassandra.OrdinalsPrimaryKeyModel;

@Repository
public interface OrdinalsRepository extends CassandraRepository<OrdinalsModel,OrdinalsPrimaryKeyModel> {
	

	@Query("SELECT * from master_values_v2 where root_org= ?0;")
	public List<OrdinalsModel> findByRootOrg(String rootOrg);
	
	@Query("SELECT * from master_values_v2 where root_org= ?0 and entity= ?1;")
	public OrdinalsModel findByRootOrgEntity(String rootOrg,String entity);
}
