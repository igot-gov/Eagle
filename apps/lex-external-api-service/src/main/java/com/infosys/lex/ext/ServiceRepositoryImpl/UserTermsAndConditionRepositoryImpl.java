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
import com.infosys.lex.ext.ServiceRepository.bodhi.UserTermsAndConditionRepositoryCustom;

@Repository
public class UserTermsAndConditionRepositoryImpl implements UserTermsAndConditionRepositoryCustom {

	@Autowired
	private CassandraOperations cassandraOperations;

	@Override
	public Map<String, Object> getUserTermsAndCondtion(String rootOrg, Integer pageSize, String pagingSequence) {

		Where select = QueryBuilder.select("root_org", "user_id", "version", "accepted_on", "language")
				.from("user_terms_condition").allowFiltering().where(QueryBuilder.eq("doc_name", "Generic T&C")).and(QueryBuilder.eq("root_org", rootOrg));
		
		select.setFetchSize(pageSize);

		if (!pagingSequence.equals("0")) {
			select.setPagingState(PagingState.fromString(pagingSequence));
		}
		ResultSet results = cassandraOperations.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", results);
		resultMap.put("nextRecord", nextPage == null ? -1 : nextPage.toString());
		resultMap.put("currentlyFetched", results.getAvailableWithoutFetching());
		return resultMap;
	}

	@Override
	public Map<String, Object> getUserTermsAndCondtionV2(Integer pageSize, String pagingSequence, Date startDate,
			Date endDate) {

		Where select = QueryBuilder.select("email", "version", "date_accepted").from("user_terms_condition")
				.allowFiltering().where(QueryBuilder.gte("date_accepted", startDate))
				.and(QueryBuilder.lt("date_accepted", endDate)).and(QueryBuilder.eq("docName", "Generic T&C"));
		select.setFetchSize(pageSize);
		if (!pagingSequence.equals("0")) {
			select.setPagingState(PagingState.fromString(pagingSequence));
		}
		ResultSet results = cassandraOperations.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", results);
		resultMap.put("nextRecord", nextPage == null ? -1 : nextPage.toString());
		resultMap.put("currentlyFetched", results.getAvailableWithoutFetching());
		return resultMap;
	}
}
