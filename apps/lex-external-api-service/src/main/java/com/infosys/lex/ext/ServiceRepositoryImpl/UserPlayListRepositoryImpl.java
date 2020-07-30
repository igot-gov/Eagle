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
import com.datastax.driver.core.querybuilder.Select;
import com.infosys.lex.ext.ServiceRepository.bodhi.UserPlayListRepositoryCustom;
import com.infosys.lex.ext.util.LexConstants;

@Repository
public class UserPlayListRepositoryImpl implements UserPlayListRepositoryCustom {
	@Autowired
	private CassandraOperations cassandraOperations;

	@Override
	public Map<String, Object> getUserPlayList(String rootOrg, Integer pageSize, String pagingSequence, Date startDate,
			Date endDate, String dumpType) {
		Map<String, Object> resultMap = new HashMap<>();
		ResultSet results = null;
		try {
			Select select = QueryBuilder.select().all().from("user_playlist");
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
