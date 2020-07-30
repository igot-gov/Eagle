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
import com.infosys.lex.ext.ServiceRepository.bodhi.AssessmentRepositoryCustom;

@Repository
public class AssessmentRepositoryImpl implements AssessmentRepositoryCustom {

	@Autowired
	private CassandraOperations cassandraTemplate;

	@Override
	public Map<String, Object> queryByDateCreatedAndTimeStamp(String rootOrg, List<Date> dateCreated, Date startDate,
			Date endDate, String page, Integer size) {
		Where select = QueryBuilder.select("user_id", "parent_content_id", "result_percent", "ts_created")
				.from("assessment_by_date").allowFiltering().where(QueryBuilder.eq("root_org", rootOrg))
				.and(QueryBuilder.in("date_created", dateCreated)).and(QueryBuilder.gte("ts_created", startDate))
				.and(QueryBuilder.lt("ts_created", endDate)).and(QueryBuilder.eq("parent_content_type", "Course"));

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
	public Map<String, Object> fetchAssessments(String rootOrg, List<Date> dateCreated, Integer pageSize,
			String pageSequence, Date startDateTime, Date endDateTime) {

		Where select = QueryBuilder.select().all().from("assessment_by_date")
				.where(QueryBuilder.in("date_created", dateCreated)).and(QueryBuilder.eq("root_org", rootOrg))
				.and(QueryBuilder.gte("ts_created", startDateTime)).and(QueryBuilder.lt("ts_created", endDateTime));
		select.setFetchSize(pageSize);
		if (!pageSequence.equals("0")) {
			select.setPagingState(PagingState.fromString(pageSequence));
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
