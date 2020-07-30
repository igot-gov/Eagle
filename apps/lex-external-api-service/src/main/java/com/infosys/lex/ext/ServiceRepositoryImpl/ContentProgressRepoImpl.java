package com.infosys.lex.ext.ServiceRepositoryImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.ext.ServiceRepository.bodhi.ContentProgressRepoCustom;

@Repository
public class ContentProgressRepoImpl implements ContentProgressRepoCustom {

	@Autowired
	private CassandraOperations cassandraTemplate;

	@Override
	public Map<String, Object> fetchProgress(String rootOrg, Integer size, String page, List<Date> filterDates) {
		Where select = QueryBuilder.select().all().from("user_content_progress_by_date_updated")
				.where(QueryBuilder.in("date_updated", filterDates)).and(QueryBuilder.eq("root_org", rootOrg));

		select.setFetchSize(size);
		if (!page.equals("0")) {
			select.setPagingState(PagingState.fromString(page));
		}

		ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();
		Map<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("result", results);
		resultMap.put("nextRecord", nextPage == null ? -1 : nextPage.toString());
		resultMap.put("currentlyFetched", results.getAvailableWithoutFetching());

		return resultMap;
	}

}
