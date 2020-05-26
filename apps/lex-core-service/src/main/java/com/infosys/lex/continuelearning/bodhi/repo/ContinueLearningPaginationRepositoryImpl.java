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
package com.infosys.lex.continuelearning.bodhi.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.PagingStateException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Repository
public class ContinueLearningPaginationRepositoryImpl implements ContinueLearningPaginationRepository {

	@Autowired
	CassandraOperations cassandraOperations;

	/**
	 * Builds SELECT statements and hits the respective tables as per provided
	 * data,i.e.,
	 * 
	 * <pre>
	 * if context_path_id is present
	 * 	goto mv_continue_learning
	 * else
	 * 	goto continue_learning
	 * </pre>
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contextPathId
	 * @param pageSize
	 * @param pageState
	 * @return
	 */
	private Where buildSelectStatement(String rootOrg, String userId, String contextPathId, String pageSize,
			String pageState) {
		Select select;
		Where finalstatement;

		if (!contextPathId.equals("all")) {
			select = QueryBuilder.select("context_path_id", "resource_id", "data").from("bodhi", "continue_learning");
			finalstatement = select.where(QueryBuilder.eq("root_org", rootOrg)).and(QueryBuilder.eq("user_id", userId))
					.and(QueryBuilder.eq("context_path_id", contextPathId));
		} else {
			select = QueryBuilder.select("context_path_id", "resource_id", "data").from("bodhi",
					"mv_continue_learning");
			finalstatement = select.where(QueryBuilder.eq("root_org", rootOrg)).and(QueryBuilder.eq("user_id", userId));
		}
		finalstatement.setFetchSize(Integer.parseInt(pageSize));
		if (!pageState.equals("0")) {
			try {
				finalstatement.setPagingState(PagingState.fromString(pageState));
			} catch (PagingStateException e) {
				throw new InvalidDataInputException("pagingstate.mismatch", e);
			}
		}

		return finalstatement;
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * ContinueLearningPaginationRepository#fetchPagedData(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> fetchPagedData(String rootOrg, String userId, String contextPathId,
			String pageSize, String pageState) throws Exception {

		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

		Where statement = this.buildSelectStatement(rootOrg, userId, contextPathId, pageSize, pageState);

		ResultSet result = cassandraOperations.getCqlOperations().queryForResultSet(statement);

		int count = 0;
		for (Row row : result) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("resourceId", (row.getObject("resource_id").toString()));
			data.put("contextPathId", (row.getObject("context_path_id").toString()));
			data.put("data", (row.getObject("data").toString()));
			if (count == 0) {
				data.put("currentlyFetched", result.getAvailableWithoutFetching() + 1);
				Object pageStateRaw = result.getExecutionInfo().getPagingState();
				data.put("nextPage", pageStateRaw == null ? null : pageStateRaw.toString());
				count++;
			}
			retList.add(data);
		}

		return retList;
	}

}
