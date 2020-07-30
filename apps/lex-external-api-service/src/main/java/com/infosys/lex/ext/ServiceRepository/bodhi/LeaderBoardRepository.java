package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.LeaderBoard;
import com.infosys.lex.ext.Models.Cassandra.LeaderBoardPrimaryKey;

@Repository
public interface LeaderBoardRepository
		extends TypedIdCassandraRepository<LeaderBoard, LeaderBoardPrimaryKey>, LeaderBoardRepositoryCustom {

}
