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
public interface TimeSpentByDateRepository extends CassandraRepository<TimeSpentByDateModel,TimeSpentByDatePrimaryKeyModel> {

	@Query("Select date as day, avg(time_spent) as duration from daily_time_spent_by_date where date in ?0 and root_org = ?1 group by date;")
	public List<Map<String, Object>> getAvgDurationStats(List<Date> dates, String rootorg);
}
