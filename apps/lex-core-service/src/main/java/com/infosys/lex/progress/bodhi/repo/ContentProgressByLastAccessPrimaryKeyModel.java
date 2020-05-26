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
package com.infosys.lex.progress.bodhi.repo;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ContentProgressByLastAccessPrimaryKeyModel implements Serializable {

	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "content_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String contentId;
	


	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}

	public void setLastAccessedOn(Date lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
	}

	@PrimaryKeyColumn(name = "last_accessed_on", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private Date lastAccessedOn;

	@PrimaryKeyColumn(name = "user_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private String userId;

	@PrimaryKeyColumn(name = "content_type", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private String contentType;

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Date getLastAssessedOn() {
		return lastAccessedOn;
	}

	public void setLastAssessedOn(Date lastAssessedOn) {
		this.lastAccessedOn = lastAssessedOn;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public ContentProgressByLastAccessPrimaryKeyModel(String rootOrg,String contentId, Date lastAccessedOn, String userId,
			String contentType) {
		super();
		this.contentId = contentId;
		this.lastAccessedOn = lastAccessedOn;
		this.userId = userId;
		this.contentType = contentType;
		this.rootOrg = rootOrg;
	}

	public ContentProgressByLastAccessPrimaryKeyModel() {
		super();
	}

	@Override
	public String toString() {
		return "ContentProgressByLastAccessPrimaryKeyModel [contentId=" + contentId + ", lastAccessedOn="
				+ lastAccessedOn + ", userId=" + userId + ", contentType=" + contentType + "]";
	}

}
