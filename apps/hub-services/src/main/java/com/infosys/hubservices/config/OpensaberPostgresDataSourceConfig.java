package com.infosys.hubservices.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.infosys.hubservices.autocpmplete.repository")
public class OpensaberPostgresDataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.opensaber.datasource")
	public DataSource opensaberDataSource() {
		return DataSourceBuilder.create().build();
	}
}
