/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
// © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
// Version: 1.10
//
// Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
// this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
// the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
// by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
// this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
// under the law.
//
// Highly Confidential
//
// */
//
//substitute url based on requirement
//
//import java.util.Properties;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@ConfigurationProperties(prefix = "data.datasource")
//substitute url based on requirement
//substitute url based on requirement
////
//public class ConfigurationsDBPG {
//
//	// Providing connection params
//	@Value("${data.datasource.jdbc-url}")
//	private String url;
//	@Value("${data.datasource.username}")
//	private String username;
//substitute based on requirement
//substitute based on requirement
//
//	@Bean("secondDataSource")
//	public DataSource secondDataSource() {
//		DriverManagerDataSource dataSourceBuilder = new DriverManagerDataSource();
//		dataSourceBuilder.setUrl(url);
//		dataSourceBuilder.setUsername(username);
//substitute based on requirement
//		return dataSourceBuilder;
////		return DataSourceBuilder.create().build();
//	}
//
//	@Bean(name = "userEntityManagerSecond")
//	public LocalContainerEntityManagerFactoryBean userEntityManagerSecond() {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(secondDataSource());
//		em.setPackagesToScan(
//substitute url based on requirement
//		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaProperties(hibernateProperties());
//
//		return em;
//	}
//
//	@Bean("userTransactionManagerSecond")
//	public PlatformTransactionManager userTransactionManagerSecond() {
//
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(userEntityManagerSecond().getObject());
//		return transactionManager;
//	}
//
//	@Bean(name = "SessionFactorySecond")
//	public LocalSessionFactoryBean SessionFactorySecond() {
//		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
//		sessionFactoryBean.setDataSource(secondDataSource());
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
