package com.infosys.lex.ext.ServiceRepository;

import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;

public abstract class CassandraConfig extends AbstractCassandraConfiguration {
	protected String contactPoints;
	protected String keyspaceName;
	protected Integer port;

	@Override
	public String getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(String contactPoints) {
		this.contactPoints = contactPoints;
	}

	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	@Override
	protected String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	public int getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
}