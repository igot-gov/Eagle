package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.infosys.lexauthoringservices.model.cassandra.ConfigurableContentHierarchyModel;
import com.infosys.lexauthoringservices.model.cassandra.ConfigurableContentHierarchyPrimaryKeyModel;

public interface ConfigurableContentHierarchyRepository extends CassandraRepository<ConfigurableContentHierarchyModel, ConfigurableContentHierarchyPrimaryKeyModel> {
	
	@Query("SELECT * from configurable_content_hierarchy where root_org= ?0 allow filtering;")
	public List<ConfigurableContentHierarchyModel> findAllByRootOrg(String rootOrg);
	
	@Query("SELECT * from configurable_content_hierarchy where root_org= ?0 and content_type= ?1;")
	public List<ConfigurableContentHierarchyModel> findById(String rootOrg,String contentType);

}
