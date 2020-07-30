package com.infosys.lex.ext.ServiceRepositoryImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserBadgeRepositoryCustom;

@Repository
public class UserBadgeRepositoryImpl implements UserBadgeRepositoryCustom {

	@Autowired
	private CassandraOperations cassandraTemplate;

	@Override
	public Map<String, Object> queryByTimeStamp(String rootOrg, Date startDate, Date endDate, String page,
			Integer size) {

		Where select = QueryBuilder.select().from("user_badges").allowFiltering()
				.where(QueryBuilder.gte("last_received_date", startDate))
				.and(QueryBuilder.lt("last_received_date", endDate)).and(QueryBuilder.gt("received_count", 0))
				.and(QueryBuilder.eq("root_org", rootOrg));
		select.setFetchSize(size);
		if (!page.equals("0")) {
			select.setPagingState(PagingState.fromString(page));
		}
		ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("result", results);
		ret.put("nextRecord", nextPage == null ? -1 : nextPage.toString());
		ret.put("remaining", results.getAvailableWithoutFetching());

		return ret;
	}

	@Override
	public Map<String, Object> getBadges(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate) {

		Map<String, Object> resultMap = new HashMap<>();
		try {
			Where select = QueryBuilder
					.select("root_org", "user_id", "badge_id", "badge_type", "first_received_date",
							"last_received_date", "progress", "received_count")
					.from("user_badges").allowFiltering().where(QueryBuilder.gte("last_received_date", startDate))
					.and(QueryBuilder.lt("last_received_date", endDate)).and(QueryBuilder.gt("received_count", 0))
					.and(QueryBuilder.eq("root_org", rootOrg));
			select.setFetchSize(pageSize);
			if (!pagingSequence.equals("0")) {
				select.setPagingState(PagingState.fromString(pagingSequence));
			}
			ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
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
