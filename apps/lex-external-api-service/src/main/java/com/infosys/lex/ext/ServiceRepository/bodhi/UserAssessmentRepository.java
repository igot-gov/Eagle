package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserAssessment;
import com.infosys.lex.ext.Models.Cassandra.UserAssessmentPrimaryKey;

@Repository
public interface UserAssessmentRepository
		extends CassandraRepository<UserAssessment, UserAssessmentPrimaryKey>, UserAssessmentCustomRepository {

}
