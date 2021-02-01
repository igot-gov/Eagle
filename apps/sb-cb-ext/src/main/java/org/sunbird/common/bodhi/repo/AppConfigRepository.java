package org.sunbird.common.bodhi.repo;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends CassandraRepository<AppConfig, AppConfigPrimaryKey> {

	public List<Map<String,Object>> findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(String rootOrg,List<String> keys);
	
	public Map<String,Object> findByPrimaryKeyRootOrgAndPrimaryKeyKey(String rootOrg,String key);

}
