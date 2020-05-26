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
package com.infosys.lex.common.bodhi.repo;

import java.util.Set;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_access_paths")
public class UserAccessPathsModel {

	@PrimaryKey
	private UserAccessPathsPrimaryKeyModel primaryKey;

	@Column("access_paths")
	private Set<String> accessPaths;

	@Column("temporary")
	private Boolean temporary;

	@Column("ttl")
	private Integer ttl;

	public UserAccessPathsPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(UserAccessPathsPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Set<String> getAccessPaths() {
		return accessPaths;
	}

	public void setAccessPaths(Set<String> accessPaths) {
		this.accessPaths = accessPaths;
	}

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public UserAccessPathsModel(UserAccessPathsPrimaryKeyModel primaryKey, Set<String> accessPaths, Boolean temporary,
			Integer ttl) {
		this.primaryKey = primaryKey;
		this.accessPaths = accessPaths;
		this.temporary = temporary;
		this.ttl = ttl;
	}

	public UserAccessPathsModel() {
		super();
	}
}
