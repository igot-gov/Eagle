package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.UserPlayList;
import com.infosys.lex.ext.Models.Cassandra.UserPlayListPrimaryKey;

@Repository
public interface UserPlayListRepository
		extends CassandraRepository<UserPlayList, UserPlayListPrimaryKey>, UserPlayListRepositoryCustom {

}
