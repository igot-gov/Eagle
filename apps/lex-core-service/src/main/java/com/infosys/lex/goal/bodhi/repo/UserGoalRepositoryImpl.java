/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.goal.bodhi.repo;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update.Where;
import com.infosys.lex.goal.dto.CreateGoalDTO;

@Repository
public class UserGoalRepositoryImpl implements UserGoalCustomRepository {

	@Autowired
	CassandraOperations cassandraOperations;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.cassandra.bodhi.repository.LearningGoalsCustom#
	 * updateUserGoals(java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void updateUserGoals(Map<String, Object> goalData) throws ParseException {
//		Date parsedTimeStamp = ProjectUtil.getDateFormatter().parse(ProjectUtil.getFormattedDate());
//		Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
		Timestamp timestamp = new Timestamp(new Date().getTime());
		Where update = QueryBuilder.update("user_learning_goals")
				.with(QueryBuilder.set("goal_content_id", goalData.get("goal_content_id")))
				.and(QueryBuilder.set("last_updated_on", timestamp))
				.where(QueryBuilder.eq("user_email", goalData.get("user_email").toString()))
				.and(QueryBuilder.eq("goal_type", goalData.get("goal_type").toString()))
				.and(QueryBuilder.eq("goal_id", UUID.fromString(goalData.get("goal_id").toString())));
		cassandraOperations.getCqlOperations().execute(update);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.cassandra.bodhi.repository.LearningGoalsCustom#bulkExecute(
	 * java.util.List)
	 */
	@Override
	public void bulkExecute(List<Statement> statements) {
		BatchStatement batchStatement = new BatchStatement();
		for (Statement statement : statements) {
			batchStatement.add(statement);
		}
		cassandraOperations.getCqlOperations().execute(batchStatement);

	}

	@Override
	public List<Statement> generateDelStmtForSharedGoal(List<String> targetUsers, String goalType, String goalId,
			String userUUID, String rootOrg) {
		List<Statement> statements = new ArrayList<Statement>();
		for (String target : targetUsers) {
			Delete.Where sharedGoal = QueryBuilder.delete().from("bodhi", "user_shared_goals")
					.where(eq("root_org", rootOrg)).and(eq("shared_with", target)).and(eq("goal_type", goalType))
					.and(eq("goal_id", UUID.fromString(goalId))).and(eq("shared_by", userUUID));
			statements.add(sharedGoal);
		}
		return statements;
	}

	@Override
	public void removeLearningGoals(CreateGoalDTO goalsData) {
		// TODO Auto-generated method stub
	}
}
