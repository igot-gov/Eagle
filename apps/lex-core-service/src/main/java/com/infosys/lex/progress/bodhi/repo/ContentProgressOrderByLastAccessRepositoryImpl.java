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
package com.infosys.lex.progress.bodhi.repo;

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
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Repository
public class ContentProgressOrderByLastAccessRepositoryImpl implements ContentProgressOrderByLastAccessRepositoryCustom {
	
	@Autowired
	private CassandraOperations cassandraTemplate;

	/* (non-Javadoc)
	 * @see com.infosys.core.cassandra.bodhi.repository.ContentProgressOrderByLastAccessRepositoryCustom#fetchUserCourseProgressPaginated(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public Map<String, Object> fetchUserCourseProgressPaginated(String rootOrg,String userUUID, String status, String contentType,
			String pageState, Integer pageSize) {
		
		Map<String, Object> ret = new HashMap<String, Object>();
		
		//create query
		Where select = QueryBuilder.select("content_id", "last_accessed_on", "progress").from("user_content_progress_order_last_assess").allowFiltering()
				.where(QueryBuilder.eq("user_id", userUUID)).and(QueryBuilder.eq("root_org", rootOrg));
		if (contentType.toLowerCase().equals("learning path")) {
			select.and(QueryBuilder.eq("content_type", "Learning Path"));
		} else if (contentType.toLowerCase().equals("course")) {
			select.and(QueryBuilder.eq("content_type", "Course"));
		} else if (contentType.toLowerCase().equals("collection")) {
			select.and(QueryBuilder.eq("content_type", "Collection"));
		} else if (contentType.toLowerCase().equals("resource")) {
			select.and(QueryBuilder.eq("content_type", "Resource"));
		} else {
			throw new InvalidDataInputException("invalid.contenttype");
		}

		if (status != null)
			if (status.toLowerCase().equals("completed"))
				select.and(QueryBuilder.eq("progress", 1));
			else
				select.and(QueryBuilder.lt("progress", 1));

		select.setFetchSize(pageSize);
		
		if (!pageState.equals("0")) {
			select.setPagingState(PagingState.fromString(pageState));
		}
		
		//convert the response to list of maps
		{
			Map<String,Map<String,Object>> data=new HashMap<>();
			List<String> contentIds=new ArrayList<>();
			ResultSet results = cassandraTemplate.getCqlOperations().queryForResultSet(select);
			PagingState nextPage = results.getExecutionInfo().getPagingState();
			int remaining=results.getAvailableWithoutFetching();
			for(Row row:results) {
				Map<String,Object> temp=new HashMap<>();
				temp.put("progress", row.getFloat("progress"));
				temp.put("content_id", row.getString("content_id"));
				temp.put("last_accessed_on", row.getTimestamp("last_accessed_on"));
				data.put(row.getString("content_id"), temp);
				contentIds.add(row.getString("content_id"));
				if (--remaining == 0)
					break;
			}
			
			ret.put("result", data);
			ret.put("page_state", nextPage == null ? -1 : nextPage.toString());
			ret.put("content_list", contentIds);
		}
		return ret;
	}

}
