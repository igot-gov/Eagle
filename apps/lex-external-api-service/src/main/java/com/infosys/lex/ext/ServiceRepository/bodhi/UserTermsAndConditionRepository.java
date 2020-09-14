package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserTermsAndCondition;
import com.infosys.lex.ext.Models.Cassandra.UserTermsAndConditionPrimaryKey;

@Repository
public interface UserTermsAndConditionRepository
		extends CassandraRepository<UserTermsAndCondition, UserTermsAndConditionPrimaryKey>,
		UserTermsAndConditionRepositoryCustom {

}
