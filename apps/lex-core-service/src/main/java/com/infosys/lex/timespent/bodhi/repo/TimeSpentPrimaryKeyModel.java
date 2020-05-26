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
public class TimeSpentPrimaryKeyModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String root_org;
	
	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String user_id;
	
	@PrimaryKeyColumn(name = "type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private Date date;
	


	public TimeSpentPrimaryKeyModel(String root_org, String user_id,
			Date date) {
		super();
		this.root_org = root_org;
		this.user_id = user_id;
		this.date = date;
		
	}

	public TimeSpentPrimaryKeyModel() {
		super();
	}

	public String getRootorg() {
		return root_org;
	}

	public void setRootorg(String root_org) {
		this.root_org = root_org;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUuid() {
		return user_id;
	}

	public void setUuid(String user_id) {
		this.user_id = user_id;
	}

	@Override
	public String toString() {
		return "TimeSpentByDatePrimaryKeyModel [root_org=" + root_org + ", user_id=" + user_id
				+ ", date=" + date + "]";
	}
	
}