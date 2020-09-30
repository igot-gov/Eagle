/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.config;

import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;

public abstract class CassandraConfig extends AbstractCassandraConfiguration {

	protected String contactPoints;
	protected int port;
	protected String keyspaceName;

	public void setContactPoints(String contactPoints) {
		this.contactPoints = contactPoints;
	}

	@Override
	protected String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	protected boolean getMetricsEnabled() {
		return false;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getContactPoints() {
		return contactPoints;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}
}
