package com.infosys.lex.notification.bodhi.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.repository.CassandraRepository;

import com.infosys.lex.notification.model.cassandra.AppConfig;
import com.infosys.lex.notification.model.cassandra.AppConfigPrimaryKey;

public interface AppConfigRepository extends CassandraRepository<AppConfig, AppConfigPrimaryKey> {

	@Cacheable(value = "config_key_value", key = "#rootOrg.concat('-').concat(#key)", unless = "#result == null")
	public AppConfig findAllByPrimaryKeyRootOrgAndPrimaryKeyKey(String rootOrg,String key);
}
