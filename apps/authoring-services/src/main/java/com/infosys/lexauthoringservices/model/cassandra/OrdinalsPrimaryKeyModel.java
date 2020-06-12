/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"*/
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */


package com.infosys.lexauthoringservices.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class OrdinalsPrimaryKeyModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name="root_org",type=PrimaryKeyType.PARTITIONED)
	private String root_org;
	
	@PrimaryKeyColumn(name="entity")
	private String entity;

	public OrdinalsPrimaryKeyModel() {
		super();
	}

	public OrdinalsPrimaryKeyModel(String root_org, String entity) {
		super();
		this.root_org = root_org;
		this.entity = entity;
	}

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

}
