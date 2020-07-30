package com.infosys.lex.ext.ServiceRepository.bodhi;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.ext.Models.Cassandra.CommonLearningGoals;

@Repository
public interface CommonLearningGoalsRepository
		extends CassandraRepository<CommonLearningGoals, String>, CommonLearningGoalsCustomRepository {
	public List<CommonLearningGoals> findByGoalGroup(String goalGroup);
}
