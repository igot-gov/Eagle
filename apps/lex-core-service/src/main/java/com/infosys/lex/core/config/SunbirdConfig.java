/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.infosys.lex.common.util.DatabaseProperties;
import com.infosys.lex.core.logger.LexLogger;

@Configuration
@ConfigurationProperties("spring.data.cassandra.sunbird")
@EnableCassandraRepositories(basePackages = {
		"com.infosys.lex.common.sunbird.repo" }, cassandraTemplateRef = "sunbirdTemplate")
public class SunbirdConfig extends AbstractCassandraConfiguration {

	@Autowired
	DatabaseProperties dbProperties;

	private LexLogger logger = new LexLogger(getClass().getName());
	protected String contactPoints;
	protected Integer port;
	protected String keyspaceName;

	@Override
	protected boolean getMetricsEnabled() { return false; }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.cassandra.config.java.AbstractClusterConfiguration#
	 * getContactPoints()
	 */
	@Override
	public String getContactPoints() {
		return contactPoints;
	}

	/**
	 * @param contactPoints
	 */
	public void setContactPoints(String contactPoints) {
		this.contactPoints = contactPoints;
	}

	/**
	 * @param keyspaceName
	 */
	public void setKeyspaceName(String keyspaceName) {
		this.keyspaceName = keyspaceName;
	}

	@Override
	public String getKeyspaceName() {
		return keyspaceName;
	}

	@Override
	public int getPort() {
		return port;
	}

	/**
	 * @param contactPoints
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	@Bean(name = "sunbirdTemplate")
	public CassandraAdminTemplate cassandraTemplate() throws Exception {
		return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
	}

	@Override
	@Bean(name = "sunbirdSession")
	public CassandraSessionFactoryBean session() {
		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(Cluster.builder().addContactPoint(getContactPoints()).withPort(getPort()).withAuthProvider(
				new PlainTextAuthProvider(dbProperties.getCassandraUserName(), dbProperties.getCassandraPassword()))
				.build());
		session.setConverter(cassandraConverter());
		session.setKeyspaceName(getKeyspaceName());
		logger.info("IP:" + getContactPoints() + " PORT:" + getPort() + " keyspace:" + getKeyspaceName());
		session.setSchemaAction(getSchemaAction());
		session.setStartupScripts(getStartupScripts());
		session.setShutdownScripts(getShutdownScripts());
		return session;
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	@Bean("sunbirdOperations")
	public CassandraOperations cassandraOperationsB() throws ClassNotFoundException {
		return new CassandraTemplate(session().getObject(), cassandraConverter());
	}

}