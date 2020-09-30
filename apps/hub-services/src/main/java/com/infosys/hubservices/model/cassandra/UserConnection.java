/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Date;

@Table("user_connection")
public class UserConnection {

	@PrimaryKey
	private UserConnectionPrimarykey userConnectionPrimarykey;

	@Column("connection_status")
	private String connectionStatus;

	@Column("connection_type")
	private String connectionType;

	@Column("start_on")
	private Date startedOn;

	@Column("end_on")
	private Date endOn;


	public UserConnection(UserConnectionPrimarykey userConnectionPrimarykey, String connectionStatus, String connectionType, Date startedOn) {
		super();
		this.userConnectionPrimarykey = userConnectionPrimarykey;
		this.connectionStatus = connectionStatus;
		this.connectionType = connectionType;
		this.startedOn = startedOn;
	}

	public UserConnection() {
		super();
	}

	public UserConnectionPrimarykey getUserConnectionPrimarykey() {
		return userConnectionPrimarykey;
	}

	public void setUserConnectionPrimarykey(UserConnectionPrimarykey userConnectionPrimarykey) {
		this.userConnectionPrimarykey = userConnectionPrimarykey;
	}

	public String getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(String connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public Date getStartedOn() {
		return startedOn;
	}

	public void setStartedOn(Date startedOn) {
		this.startedOn = startedOn;
	}

	public Date getEndOn() {
		return endOn;
	}

	public void setEndOn(Date endOn) {
		this.endOn = endOn;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
}
