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

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PostgresDataSourceConfig {
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}
}

//@Configuration
//@EnableTransactionManagement
//substitute url based on requirement
//@ConfigurationProperties(prefix = "spring.datasource")
//substitute url based on requirement
//substitute url based on requirement
//public class PostgresDataSourceConfig {
//
//	// Providing connection params
////	@Value("${spring.datasource.jdbc-url}")
////	private String url;
////	@Value("${spring.datasource.username}")
////	private String username;
//substitute based on requirement
//substitute based on requirement
//
////	@Bean("primaryDataSource")
//	@Primary
//	public DataSource primaryDataSource() {
////		DriverManagerDataSource dataSourceBuilder = new DriverManagerDataSource();
////		dataSourceBuilder.setUrl(url);
////		dataSourceBuilder.setUsername(username);
//substitute based on requirement
////		return dataSourceBuilder;
//
//		return DataSourceBuilder.create().build();
//	}

//----------------------------------------------------------------
//	@Bean(name = "EntityManagerPrimary")
//	public LocalContainerEntityManagerFactoryBean EntityManagerPrimary() {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(primaryDataSource());
//substitute url based on requirement
//		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaProperties(hibernateProperties());
//		return em;
//	}
//
//	@Bean("TransactionManagerPrimary")
//	public PlatformTransactionManager TransactionManagerPrimary() {
//
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(EntityManagerPrimary().getObject());
//		return transactionManager;
//	}
//
//	@Bean(name = "SessionFactoryPrimary")
//	@Primary
//	public LocalSessionFactoryBean SessionFactoryPrimary() {
//		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
//		sessionFactoryBean.setDataSource(primaryDataSource());
//substitute url based on requirement
//		sessionFactoryBean.setHibernateProperties(hibernateProperties());
//		return sessionFactoryBean;
//	}
//
//	private Properties hibernateProperties() {
//		Properties properties = new Properties();
////        properties.put("hibernate.hbm2ddl.auto", false);
//		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//		properties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
//		properties.put("hibernate.show_sql", true);
//		properties.put("hibernate.format_sql", true);
//substitute url based on requirement
//		properties.put("connection.release_mode", "auto");
//		return properties;
//	}
//}
