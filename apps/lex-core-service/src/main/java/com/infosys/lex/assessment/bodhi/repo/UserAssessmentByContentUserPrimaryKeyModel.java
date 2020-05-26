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
package com.infosys.lex.assessment.bodhi.repo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserAssessmentByContentUserPrimaryKeyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "parent_source_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String parentSourceId;

	@PrimaryKeyColumn(name = "user_id", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(name = "ts_created", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private Date tsCreated;

	@PrimaryKeyColumn(name = "result_percent", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private BigDecimal resultPercent;

	@PrimaryKeyColumn(name = "id", ordinal = 5, type = PrimaryKeyType.CLUSTERED)
	private UUID id;

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getParentSourceId() {
		return parentSourceId;
	}

	public void setParentSourceId(String parentSourceId) {
		this.parentSourceId = parentSourceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getTsCreated() {
		return tsCreated;
	}

	public void setTsCreated(Date tsCreated) {
		this.tsCreated = tsCreated;
	}

	public BigDecimal getResultPercent() {
		return resultPercent;
	}

	public void setResultPercent(BigDecimal resultPercent) {
		this.resultPercent = resultPercent;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserAssessmentByContentUserPrimaryKeyModel() {
		super();
	}

	public UserAssessmentByContentUserPrimaryKeyModel(String rootOrg, String parentSourceId, String userId,
			Date tsCreated, BigDecimal resultPercent, UUID id) {
		this.rootOrg = rootOrg;
		this.parentSourceId = parentSourceId;
		this.userId = userId;
		this.tsCreated = tsCreated;
		this.resultPercent = resultPercent;
		this.id = id;
	}

	@Override
	public String toString() {
		return "UserAssessmentByContentUserPrimaryKeyModel [rootOrg=" + rootOrg + ", parentSourceId=" + parentSourceId
				+ ", userId=" + userId + ", tsCreated=" + tsCreated + ", resultPercent=" + resultPercent + ", id=" + id
				+ "]";
	}
}
