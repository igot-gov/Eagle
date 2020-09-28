/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class UserConnectionPrimarykey implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org",type=PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "user_id")
	private String userId;

	@PrimaryKeyColumn(name = "connection_id")
	private String connectionId;

	public UserConnectionPrimarykey(){
		super();
	}

	public UserConnectionPrimarykey(String rootOrg, String org, String userId, String connectionId) {
		super();
		this.userId = userId;
		this.rootOrg = rootOrg;
		this.connectionId = connectionId;
	}

	public String getUserId() {
		return userId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
}
