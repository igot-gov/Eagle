package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.AppConfig;
import com.infosys.lex.ext.Models.Cassandra.AppConfigPrimaryKey;

@Repository
public interface AppConfigRepository extends CassandraRepository<AppConfig, AppConfigPrimaryKey> {

	public List<Map<String,Object>> findAllByPrimaryKeyRootOrgAndPrimaryKeyKeyIn(String rootOrg,List<String> keys);
	
	public Map<String,Object> findByPrimaryKeyRootOrgAndPrimaryKeyKey(String rootOrg,String key);

}
