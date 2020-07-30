package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserBadgeData;
import com.infosys.lex.ext.Models.Cassandra.UserBadgeDataPrimaryKey;

@Repository
public interface UserBadgeRepository
		extends CassandraRepository<UserBadgeData, UserBadgeDataPrimaryKey>, UserBadgeRepositoryCustom {

}
