

package org.sunbird.scoringengine.repository.cassandra.sunbird;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.sunbird.scoringengine.config.CassandraConfig;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;

@Configuration
@ConfigurationProperties("spring.data.cassandra.sunbird")
@EnableCassandraRepositories(basePackages = "org.sunbird.recommendationservice.repository.cassandra.sunbird", cassandraTemplateRef = "sunbirdTemplate")
public class SunbirdConfig extends CassandraConfig {

	//private LexLogger logger = new LexLogger(getClass().getName());

	@Value("${spring.data.cassandra.sunbird.username}")
	private String sunbirdUser;

	@Value("${spring.data.cassandra.sunbird.password}")
	private String sunbirdPassword;

	@Override
	@Bean(name = "sunbirdTemplate")
	public CassandraAdminTemplate cassandraTemplate() throws Exception {
		return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
	}

	@Override
	@Bean(name = "sunbirdSession")
	public CassandraSessionFactoryBean session() {

		AuthProvider authProvider = new PlainTextAuthProvider(sunbirdUser, sunbirdPassword);

		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
		session.setCluster(Cluster.builder().addContactPoint(getContactPoints()).withPort(getPort())
				.withAuthProvider(authProvider).withoutJMXReporting().build());
		session.setConverter(cassandraConverter());
		session.setKeyspaceName(getKeyspaceName());
		session.setSchemaAction(getSchemaAction());
		session.setStartupScripts(getStartupScripts());
		session.setShutdownScripts(getShutdownScripts());
		System.out.println("Cassandra session created for " + getKeyspaceName() + "keyspace with IP : " + getContactPoints()
				+ ":" + getPort());
		return session;
	}
}