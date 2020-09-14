package com.infosys.lex.ext.ServiceRepositoryImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.ext.ServiceRepository.bodhi.LeaderBoardRepositoryCustom;

@Repository
public class LeaderBoardRepositoryImpl implements LeaderBoardRepositoryCustom {

	@Autowired
	private CassandraOperations cassandraOperations;

	@Override
	public Map<String, Object> getLeaderBoardsData(String rootOrg, Integer pageSize, String pagingSequence,
			int leaderBoardYear, String durationType, int durationValue, String leaderBoardType) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Where select = QueryBuilder.select("root_org", "user_id", "points", "rank").from("leaderboard")
					.where(QueryBuilder.eq("leaderboard_year", leaderBoardYear))
					.and(QueryBuilder.eq("duration_type", durationType))
					.and(QueryBuilder.eq("duration_value", durationValue))
					.and(QueryBuilder.eq("leaderboard_type", leaderBoardType))
					.and(QueryBuilder.eq("root_org", rootOrg));
			select.setFetchSize(pageSize);
			if (!pagingSequence.equals("0")) {
				select.setPagingState(PagingState.fromString(pagingSequence));
			}
			ResultSet results = cassandraOperations.getCqlOperations().queryForResultSet(select);
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