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
package com.infosys.lex.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProperties {

	@Value("${spring.data.cassandra.bodhi.contact-points}")
	private String bodhiContactPoint;

	@Value("${spring.data.cassandra.bodhi.port}")
	private String bodhiContactPort;

	@Value("${spring.data.cassandra.bodhi.keyspace-name}")
	private String bodhiKeyspace;

	@Value("${spring.data.cassandra.sunbird.contact-points}")
	private String sunbirdContactPoint;

	@Value("${spring.data.cassandra.sunbird.port}")
	private String sunbirdContactPort;

	@Value("${spring.data.cassandra.sunbird.keyspace-name}")
	private String sunbirdKeyspace;

	@Value("${spring.data.cassandra.username}")
	private String cassandraUserName;

	@Value("${spring.data.cassandra.password}")
	private String cassandraPassword;

	@Value("${spring.data.mongodb.uri}")
	private String mongoURI;

	@Value("${spring.data.mongodb.database}")
	private String mongoDatabase;

	@Value("${spring.data.elasticsearch.ip}")
	private String elasticIp;

	@Value("${spring.data.elasticsearch.port}")
	private String elasticPort;
	
	@Value("${spring.data.elasticsearch.cluster-name}")
	private String elasticCluster;

	@Value("${spring.data.elasticsearch.username}")
	private String elasticUser;


	@Value("${spring.data.elasticsearch.password}")
	private String elasticPassword;

	@Value("${spring.datasource.jdbc-url}")
	private String springDataPostgresUrl;

	@Value("${spring.datasource.username}")
	private String springDataPostgresUserName;


	@Value("${spring.datasource.password}")
	private String springDataPostgresPassword;
	
	public String getSpringDataPostgresUrl() {
		return springDataPostgresUrl;
	}

	public void setSpringDataPostgresUrl(String springDataPostgresUrl) {
		this.springDataPostgresUrl = springDataPostgresUrl;
	}

	public String getSpringDataPostgresUserName() {
		return springDataPostgresUserName;
	}

	public void setSpringDataPostgresUserName(String springDataPostgresUserName) {
		this.springDataPostgresUserName = springDataPostgresUserName;
	}

	public String getSpringDataPostgresPassword() {
		return springDataPostgresPassword;
	}

	public void setSpringDataPostgresPassword(String springDataPostgresPassword) {
		this.springDataPostgresPassword = springDataPostgresPassword;
	}


	public String getElasticIp() {
		return elasticIp;
	}

	public void setElasticIp(String elasticIp) {
		this.elasticIp = elasticIp;
	}

	public String getElasticCluster() {
		return elasticCluster;
	}

	public void setElasticCluster(String elasticCluster) {
		this.elasticCluster = elasticCluster;
	}

	public String getBodhiContactPoint() {
		return bodhiContactPoint;
	}

	public void setBodhiContactPoint(String bodhiContactPoint) {
		this.bodhiContactPoint = bodhiContactPoint;
	}

	public String getBodhiContactPort() {
		return bodhiContactPort;
	}

	public void setBodhiContactPort(String bodhiContactPort) {
		this.bodhiContactPort = bodhiContactPort;
	}

	public String getBodhiKeyspace() {
		return bodhiKeyspace;
	}

	public void setBodhiKeyspace(String bodhiKeyspace) {
		this.bodhiKeyspace = bodhiKeyspace;
	}

	public String getSunbirdContactPoint() {
		return sunbirdContactPoint;
	}

	public void setSunbirdContactPoint(String sunbirdContactPoint) {
		this.sunbirdContactPoint = sunbirdContactPoint;
	}

	public String getSunbirdContactPort() {
		return sunbirdContactPort;
	}

	public void setSunbirdContactPort(String sunbirdContactPort) {
		this.sunbirdContactPort = sunbirdContactPort;
	}

	public String getSunbirdKeyspace() {
		return sunbirdKeyspace;
	}

	public void setSunbirdKeyspace(String sunbirdKeyspace) {
		this.sunbirdKeyspace = sunbirdKeyspace;
	}

	public String getCassandraUserName() {
		return cassandraUserName;
	}

	public void setCassandraUserName(String cassandraUserName) {
		this.cassandraUserName = cassandraUserName;
	}

	public String getCassandraPassword() {
		return cassandraPassword;
	}

	public void setCassandraPassword(String cassandraPassword) {
		this.cassandraPassword = cassandraPassword;
	}

	public String getMongoURI() {
		return mongoURI;
	}

	public void setMongoURI(String mongoURI) {
		this.mongoURI = mongoURI;
	}

	public String getMongoDatabase() {
		return mongoDatabase;
	}

	public void setMongoDatabase(String mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public String getElasticPort() {
		return elasticPort;
	}

	public void setElasticPort(String elasticPort) {
		this.elasticPort = elasticPort;
	}

	public String getElasticUser() {
		return elasticUser;
	}

	public void setElasticUser(String elasticUser) {
		this.elasticUser = elasticUser;
	}

	public String getElasticPassword() {
		return elasticPassword;
	}

	public void setElasticPassword(String elasticPassword) {
		this.elasticPassword = elasticPassword;
	}

	@Override
	public String toString() {
		return "DatabaseProperties [bodhiContactPoint=" + bodhiContactPoint + ", bodhiContactPort=" + bodhiContactPort
				+ ", bodhiKeyspace=" + bodhiKeyspace + ", sunbirdContactPoint=" + sunbirdContactPoint
				+ ", sunbirdContactPort=" + sunbirdContactPort + ", sunbirdKeyspace=" + sunbirdKeyspace
				+ ", cassandraUserName=" + cassandraUserName + ", cassandraPassword=" + cassandraPassword
				+ ", mongoURI=" + mongoURI + ", mongoDatabase=" + mongoDatabase + ", elasticIp=" + elasticIp
				+ ", elasticPort=" + elasticPort + ", elasticCluster=" + elasticCluster + ", elasticUser=" + elasticUser
				+ ", elasticPassword=" + elasticPassword + ", springDataPostgresUrl=" + springDataPostgresUrl
				+ ", springDataPostgresUserName=" + springDataPostgresUserName + ", springDataPostgresPassword="
				+ springDataPostgresPassword + "]";
	}

	

}
