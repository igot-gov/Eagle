/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.timespent.bodhi.repo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class TimeSpentByDatePrimaryKeyModel  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String root_org;
	
	@PrimaryKeyColumn(name = "date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private Date date;
	
	@PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private String user_id;
	


	public TimeSpentByDatePrimaryKeyModel(String root_org, Date date,
			String user_id) {
		super();
		this.root_org = root_org;
		this.user_id = user_id;
		this.date = date;
		
	}

	public TimeSpentByDatePrimaryKeyModel() {
		super();
	}

	public String getroot_org() {
		return root_org;
	}

	public void setroot_org(String root_org) {
		this.root_org = root_org;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getuser_id() {
		return user_id;
	}

	public void setuser_id(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String toString() {
		return "TimeSpentByuser_idPrimaryKeyModel [root_org=" + root_org + ", date=" + date
				+ ", user_id=" + user_id + "]";
	}
	
}
