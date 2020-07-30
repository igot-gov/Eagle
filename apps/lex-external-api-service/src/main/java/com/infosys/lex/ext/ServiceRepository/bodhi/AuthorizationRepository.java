package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.AuthorizeData;

@Repository
public interface AuthorizationRepository extends CassandraRepository<AuthorizeData, String> {

}
