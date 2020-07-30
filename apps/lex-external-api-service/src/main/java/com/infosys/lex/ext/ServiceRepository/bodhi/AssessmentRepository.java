package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.AssessmentData;
import com.infosys.lex.ext.Models.Cassandra.AssessmentDataPrimaryKey;

@Repository
public interface AssessmentRepository
		extends CassandraRepository<AssessmentData, AssessmentDataPrimaryKey>, AssessmentRepositoryCustom {

}
