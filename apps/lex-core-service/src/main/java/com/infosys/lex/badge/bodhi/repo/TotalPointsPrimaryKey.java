/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.badge.bodhi.repo;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class TotalPointsPrimaryKey {

	
	@PrimaryKeyColumn(name = "root_org",ordinal = 0,type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;
	
	@PrimaryKeyColumn(name = "user_id",ordinal = 1,type = PrimaryKeyType.PARTITIONED)
	private String userId;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TotalPointsPrimaryKey(String rootOrg, String userId) {
		super();
		this.rootOrg = rootOrg;
		this.userId = userId;
	}
	
	
	
}
