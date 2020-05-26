/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.timespent.bodhi.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSpentRepository extends CassandraRepository<TimeSpentModel,TimeSpentPrimaryKeyModel>{

	@Query("select date as day,time_spent as duration from daily_time_spent where root_org = ?0 and user_id = ?1 and  date >= ?2 and date <= ?3 ;")
	public List<Map<String, Object>> getUserDurationStats(String rootorg,String uuid, Date startDate, Date endDate);
	
}
