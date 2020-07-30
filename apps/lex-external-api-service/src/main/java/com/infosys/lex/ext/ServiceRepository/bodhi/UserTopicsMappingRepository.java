package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserTopicsMapping;

@Repository
public interface UserTopicsMappingRepository
		extends CassandraRepository<UserTopicsMapping, String>, UserTopicsMappingRepositoryCustom {

}
