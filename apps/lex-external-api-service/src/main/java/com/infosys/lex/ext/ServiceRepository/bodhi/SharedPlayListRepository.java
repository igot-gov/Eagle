package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.SharedPlayLists;
import com.infosys.lex.ext.Models.Cassandra.SharedPlayListsPrimaryKey;

@Repository
public interface SharedPlayListRepository
		extends CassandraRepository<SharedPlayLists, SharedPlayListsPrimaryKey>, SharedPlayListCustomRepository {

}
