package com.infosys.lexauthoringservices.config;

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
