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
import com.infosys.lex.ext.ServiceRepository.bodhi.UserTopicsMappingRepositoryCustom;

@Repository
public class UserTopicsMappingRepositoryImpl implements UserTopicsMappingRepositoryCustom {

	@Autowired
	CassandraOperations cassandraOperations;

	@Override
	public Map<String, Object> getInterests(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			Where select = QueryBuilder.select("root_org", "user_id", "updated_on", "interest").from("user_interest")
					.allowFiltering().where(QueryBuilder.gte("updated_on", startDate))
					.and(QueryBuilder.lt("updated_on", endDate)).and(QueryBuilder.eq("root_org", rootOrg));
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
