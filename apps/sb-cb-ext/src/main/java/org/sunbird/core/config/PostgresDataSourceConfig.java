package org.sunbird.core.config;

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
