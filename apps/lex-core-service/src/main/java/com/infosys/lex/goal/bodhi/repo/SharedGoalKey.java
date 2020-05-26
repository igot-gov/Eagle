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
package com.infosys.lex.goal.bodhi.repo;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class SharedGoalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;
	@PrimaryKeyColumn(name = "shared_with", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String sharedWith;
	@PrimaryKeyColumn(name = "goal_type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private String goalType;
	@PrimaryKeyColumn(name = "goal_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private UUID goalId;
	@PrimaryKeyColumn(name = "shared_by", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private String sharedBy;

	public String getSharedWith() {
		return sharedWith;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public String getGoalType() {
		return goalType;
	}

	public UUID getGoalId() {
		return goalId;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedWith(String sharedWith) {
		this.sharedWith = sharedWith;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public void setGoalId(UUID goalId) {
		this.goalId = goalId;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public SharedGoalKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SharedGoalKey(String rootOrg, String sharedWith, String goalType, UUID goalId, String sharedBy) {
		super();
		this.rootOrg = rootOrg;
		this.sharedWith = sharedWith;
		this.goalType = goalType;
		this.goalId = goalId;
		this.sharedBy = sharedBy;
	}

	@Override
	public String toString() {
		return "SharedGoalKey [sharedWith=" + sharedWith + ", rootOrg=" + rootOrg + ", goalType=" + goalType
				+ ", goalId=" + goalId + ", sharedBy=" + sharedBy + "]";
	}
}
