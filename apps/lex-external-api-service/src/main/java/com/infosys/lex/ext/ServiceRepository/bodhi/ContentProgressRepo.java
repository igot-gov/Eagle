package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserContentProgressByDateUpdated;
import com.infosys.lex.ext.Models.Cassandra.UserContentProgressByDateUpdatedPrimaryKey;

@Repository
public interface ContentProgressRepo
		extends CassandraRepository<UserContentProgressByDateUpdated, UserContentProgressByDateUpdatedPrimaryKey>,
		ContentProgressRepoCustom {
}
