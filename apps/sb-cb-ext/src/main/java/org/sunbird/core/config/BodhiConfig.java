package org.sunbird.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@ConfigurationProperties("spring.data.cassandra.bodhi")
@EnableCassandraRepositories(basePackages = "org.sunbird.*.bodhi.repo", cassandraTemplateRef = "bodhiTemplate")
public class BodhiConfig extends AbstractCassandraConfiguration {

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
	@Primary
	@Bean(name = "bodhiTemplate")
	public CassandraAdminTemplate cassandraTemplate() throws Exception {
		return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
	}

	@Override
	@Bean(name = "bodhiSession")
	public CassandraSessionFactoryBean session() {
		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(Cluster.builder().addContactPoint(getContactPoints()).withPort(getPort()).withAuthProvider(
				new PlainTextAuthProvider(dbProperties.getCassandraUserName(), dbProperties.getCassandraPassword()))
				.build());
		session.setConverter(cassandraConverter());
		session.setKeyspaceName(getKeyspaceName());
		logger.info("IP:" + getContactPoints() + " PORT:" + getPort() + " keysapce:" + getKeyspaceName());
		session.setSchemaAction(getSchemaAction());
		session.setStartupScripts(getStartupScripts());
		session.setShutdownScripts(getShutdownScripts());
		return session;
	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 */
	@Bean("bodhiOperations")
	public CassandraOperations cassandraOperationsB() throws ClassNotFoundException {
		return new CassandraTemplate(session().getObject(), cassandraConverter());
	}
}