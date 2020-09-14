package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.ApplicationProperties;
import com.infosys.lex.ext.Models.Cassandra.AppPropertiesPrimaryKey;

@Repository
public interface AppPropertiesRepository extends CassandraRepository<ApplicationProperties, AppPropertiesPrimaryKey> {

	@Query("select * from app_config where root_org=?0 and key in ?1")
	public List<ApplicationProperties> findByKeyIn(String rootOrg, List<String> keys);

}
