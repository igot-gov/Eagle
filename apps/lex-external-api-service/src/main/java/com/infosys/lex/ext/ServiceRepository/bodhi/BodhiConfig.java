package com.infosys.lex.ext.ServiceRepository.bodhi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.infosys.lex.ext.ServiceRepository.CassandraConfig;

@Configuration
@ConfigurationProperties("spring.data.cassandra.bodhi")
@EnableCassandraRepositories(basePackages = "com.infosys.lex.ext.ServiceRepository.bodhi", cassandraTemplateRef = "bodhiTemplate")
public class BodhiConfig extends CassandraConfig {

	@Value("${spring.data.cassandra.bodhi.username}")
	private String bodhiUser;

	@Value("${spring.data.cassandra.bodhi.password}")
	private String bodhiPassword;

	@Override
	@Primary
	@Bean(name = "bodhiTemplate")
	public CassandraAdminOperations cassandraTemplate() throws Exception {
		return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
	}

	@Override
	@Bean(name = "bodhiSession")
	public CassandraSessionFactoryBean session() {
		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();

		AuthProvider authProvider = new PlainTextAuthProvider(bodhiUser, bodhiPassword);
		session.setCluster(Cluster.builder().addContactPoint(getContactPoints()).withPort(getPort())
				.withAuthProvider(authProvider).build());
		session.setConverter(cassandraConverter());
		session.setKeyspaceName(getKeyspaceName());
		session.setSchemaAction(getSchemaAction());
		session.setStartupScripts(getStartupScripts());
		session.setShutdownScripts(getShutdownScripts());
		return session;
	}
}