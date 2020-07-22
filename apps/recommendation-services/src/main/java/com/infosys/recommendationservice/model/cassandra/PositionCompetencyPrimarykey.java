/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class PositionCompetencyPrimarykey implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org",type=PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "org")
	private String org;

	@PrimaryKeyColumn("user_role")
	private String userRole;

	@PrimaryKeyColumn("competency")
	private String competency;


	public PositionCompetencyPrimarykey(){
		super();
	}

	public PositionCompetencyPrimarykey(String rootOrg, String org, String userRole, String competency ) {
		super();
		this.rootOrg = rootOrg;
		this.org = org;
		this.competency = competency;
		this.userRole = userRole;

	}
	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
}
