package com.infosys.lex.ext.ServiceRepositoryImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.infosys.lex.ext.ServiceRepository.bodhi.CommonLearningGoalsCustomRepository;

@Repository
public class CommonLearningGoalsRepositoryImpl implements CommonLearningGoalsCustomRepository {

	@Autowired
	private CassandraOperations cassandraOperations;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCommonLearningGoalsMap() {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Select select = QueryBuilder.select("id", "goal_content_id").from("common_learning_goals");
			ResultSet results = cassandraOperations.getCqlOperations().queryForResultSet(select);
			for (Row row : results) {
				System.out.println(row.getObject("id"));
				resultMap.put(row.getString("id").toString(), row.getObject("goal_content_id"));
			}
			System.out.println(resultMap);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		return resultMap;

	}
}
