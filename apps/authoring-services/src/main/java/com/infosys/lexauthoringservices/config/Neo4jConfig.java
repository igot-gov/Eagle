package com.infosys.lexauthoringservices.config;

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infosys.lexauthoringservices.util.DbProperties;

@Configuration
public class Neo4jConfig {

	@Autowired
	private DbProperties dbProps;

	@Bean
	public Driver Neo4jDriver() {

		if (Boolean.parseBoolean(dbProps.getNeo4jAuthEnable())) {
			return GraphDatabase.driver(dbProps.getNeo4jHost(),
					AuthTokens.basic(dbProps.getNeo4jUserName(), dbProps.getNeo4jPassword()));
		} else {
			Config config = Config.build().withConnectionTimeout(dbProps.getNeoTimeout(), TimeUnit.SECONDS)
	                .withConnectionLivenessCheckTimeout(10L, TimeUnit.SECONDS)
	                .toConfig();
			System.out.println("Using timeout config of : " + dbProps.getNeoTimeout().toString());
			return GraphDatabase.driver(dbProps.getNeo4jHost(),config);
		}
	}
}
