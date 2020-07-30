package com.infosys.lex.ext.ServiceRepositoryImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserLearningGoalsRepositoryCustom;
import com.infosys.lex.ext.serviceImpl.UserDataUtilImpl;
import com.infosys.lex.ext.util.LexConstants;

@Repository
public class UserLearningGoalsRepositoryImpl implements UserLearningGoalsRepositoryCustom {

	@Autowired
	private CassandraOperations cassandraOperations;

	@Override
	public List<Map<String, Object>> getUserLearningGoalsByEmail(String userEmail, String goalType,
			Set<String> goalIds) {
		List<Map<String, Object>> resultMaps = new ArrayList<>();
		try {
			List<UUID> goalIdsInUUID = new ArrayList<>();
			for (String goalId : goalIds) {
				goalIdsInUUID.add(UUID.fromString(goalId));
			}
			Where select = QueryBuilder.select("goal_id", "goal_title", "goal_desc", "goal_start_date", "goal_end_date")
					.from("user_learning_goals").where(QueryBuilder.eq("user_email", userEmail))
					.and(QueryBuilder.eq("goal_type", goalType)).and(QueryBuilder.in("goal_id", goalIdsInUUID));
			ResultSet results = cassandraOperations.getCqlOperations().queryForResultSet(select);
			for (Row row : results) {
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("goalId", row.getObject("goal_id"));
				resultMap.put("goalTitle", row.getObject("goal_title"));
				resultMap.put("goalDesc", row.getObject("goal_desc"));
				resultMap.put("goalStartDate", convertDate(row.getObject("goal_start_date")));
				resultMap.put("goalEndDate", convertDate(row.getObject("goal_end_date")));
				resultMaps.add(resultMap);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		return resultMaps;
	}

	// common util bana ke dal
	public String convertDate(Object date) {
		if (date == null) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) (date));
		return UserDataUtilImpl.formatter.format(calendar.getTime());
	}

	@Override
	public Map<String, Object> getUserLearningGoals(String rootOrg, Integer pageSize, String pagingSequence,
			Date startDate, Date endDate, String dumpType) {
		Map<String, Object> resultMap = new HashMap<>();
		ResultSet results = null;
		try {
			Select select = QueryBuilder.select().all().from("user_learning_goals");
			if (dumpType.equals(LexConstants.DUMP_TYPE_INCREMENTAL)) {
				select.allowFiltering().where(QueryBuilder.gte("last_updated_on", startDate))
						.and(QueryBuilder.lt("last_updated_on", endDate));
			}
			select.where(QueryBuilder.eq("root_org", rootOrg));
			select.setFetchSize(pageSize);
			if (!pagingSequence.equals("0")) {
				select.setPagingState(PagingState.fromString(pagingSequence));
			}
			results = cassandraOperations.getCqlOperations().queryForResultSet(select);
			PagingState nextPage = results.getExecutionInfo().getPagingState();
			resultMap.put("result", results);
			resultMap.put("nextRecord", nextPage == null ? -1 : nextPage.toString());
			resultMap.put("currentlyFetched", results.getAvailableWithoutFetching());
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		return resultMap;
	}

}
