/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
//substitute url based on requirement
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
//
//@Configuration
//@EnableCassandraRepositories
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//	protected String contactPoints;
//	protected Integer port;
//	protected String keyspaceName;
//
//	/* (non-Javadoc)
//	 * @see org.springframework.cassandra.config.java.AbstractClusterConfiguration#getContactPoints()
//	 */
//	@Override
//	public String getContactPoints() {
//		return contactPoints;
//	}
//
//	/**
//	 * @param contactPoints
//	 */
//	public void setContactPoints(String contactPoints) {
//		this.contactPoints = contactPoints;
//	}
//
//	/**
//	 * @param keyspaceName
//	 */
//	public void setKeyspaceName(String keyspaceName) {
//		this.keyspaceName = keyspaceName;
//	}
//
//	@Override
//	public String getKeyspaceName() {
//		return keyspaceName;
//	}
//	
//	@Override
//	public int getPort() {
//		return port;
//	}
//
//	/**
//	 * @param contactPoints
//	 */
//	public void setPort(Integer port) {
//		this.port = port;
//	}
//}
