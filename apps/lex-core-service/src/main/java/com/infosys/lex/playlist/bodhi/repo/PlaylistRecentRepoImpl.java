/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.playlist.bodhi.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;

import com.datastax.driver.core.PagingState;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;

public class PlaylistRecentRepoImpl implements PlaylistRecentCustomRepository {

	@Autowired
	CassandraOperations cassandraTemplate;

	@Override
	public Map<String, Object> fetchRecentPlaylistContents(String rootOrg, String userId, String page, Integer size) {

		Where select = QueryBuilder.select("resource_id", "playlist_id", "last_updated_on").from("mv_playlist_recent")
				.where(QueryBuilder.eq("root_org", rootOrg)).and(QueryBuilder.eq("user_id", userId));

		select.setFetchSize(size);

		if (!page.equals("0"))
			select.setPagingState(PagingState.fromString(page));

		ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
		PagingState nextPage = results.getExecutionInfo().getPagingState();
		int remaining = results.getAvailableWithoutFetching();
		List<String> resourceIds = new ArrayList<>();
		for (Row record : results) {
			if (!resourceIds.contains(record.getString("resource_id")))
				resourceIds.add(record.getString("resource_id"));
			if (--remaining == 0)
				break;
		}

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("resourceIds", resourceIds);
		returnMap.put("page", nextPage == null ? "-1" : nextPage.toString());

		return returnMap;

	}
}
