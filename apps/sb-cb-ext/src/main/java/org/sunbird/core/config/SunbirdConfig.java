package org.sunbird.core.config;

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
import org.sunbird.common.util.DatabaseProperties;
import org.sunbird.core.logger.CbExtLogger;

@Configuration
@ConfigurationProperties("spring.data.cassandra.sunbird")
@EnableCassandraRepositories(basePackages = {
		"org.sunbird.common.repo" }, cassandraTemplateRef = "sunbirdTemplate")
public class SunbirdConfig extends AbstractCassandraConfiguration {

	@Autowired
	DatabaseProperties dbProperties;

	private CbExtLogger logger = new CbExtLogger(getClass().getName());
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