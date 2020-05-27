/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
//import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
//import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
//import org.springframework.data.cassandra.config.SchemaAction;
//import org.springframework.data.cassandra.core.CassandraOperations;
//import org.springframework.data.cassandra.core.CassandraTemplate;
//import org.springframework.data.cassandra.core.convert.CassandraConverter;
//import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
//import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
//import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
//import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
//
//@Configuration
//public class DaoConfiguration {
//
//	public static final String KEYSPACE = "bodhi";
//
//	@Bean
//	public CassandraClusterFactoryBean cluster() {
//		CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
//		cluster.setContactPoints("IPaddress");
//		cluster.setPort(9142);
//		return cluster;
//	}
//
//	@Bean
//	public CassandraMappingContext mappingContext() throws ClassNotFoundException {
//		CassandraMappingContext mappingContext = new CassandraMappingContext();
//		mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), KEYSPACE));
//		return mappingContext;
//	}
//
//	@Bean
//	public CassandraConverter converter() throws ClassNotFoundException {
//		return new MappingCassandraConverter(mappingContext());
//	}
//
//	@Bean
//	public CassandraSessionFactoryBean session() throws Exception {
//		CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
//		session.setCluster(cluster().getObject());
//		session.setKeyspaceName(KEYSPACE);
//		session.setConverter(converter());
//		session.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);
//		return session;
//	}
//
//	@Bean
//	public CassandraOperations cassandraTemplate() throws Exception {
//		return new CassandraTemplate(session().getObject());
//	}
//
//}