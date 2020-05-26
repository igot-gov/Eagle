/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.timespent.bodhi.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("daily_time_spent_by_date")
public class TimeSpentByDateModel {
	
	@PrimaryKey
	private TimeSpentByDatePrimaryKeyModel primaryKey;
	
	@Column("time_spent")
	private Double time_spent;

	public TimeSpentByDatePrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(TimeSpentByDatePrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Double getTimespent() {
		return time_spent;
	}

	public void setTimespent(Double time_spent) {
		this.time_spent = time_spent;
	}

	public TimeSpentByDateModel(TimeSpentByDatePrimaryKeyModel primaryKey, Double time_spent) {
		super();
		this.primaryKey = primaryKey;
		this.time_spent = time_spent;
	}

	public TimeSpentByDateModel() {
		super();
		
	}
	
	@Override
	public String toString() {
		return "TimeSpentByUuidModel [primaryKey=" + primaryKey + ",time_spent" + time_spent+ "]" ;
	}
	
	

	
}
