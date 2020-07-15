/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.model.cassandra;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
public class ContentEvaluationPrimaryKey implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "root_org",type=PrimaryKeyType.PARTITIONED)
	private String root_org;

	@PrimaryKeyColumn(name = "org")
	private String org;

	@PrimaryKeyColumn(name = "content_id")
	private String contentId;

	@PrimaryKeyColumn(name = "user_id")
	private String userId;

	@PrimaryKeyColumn(name = "header")
	private String header;


	public ContentEvaluationPrimaryKey(){
		super();
	}

	public ContentEvaluationPrimaryKey(String root_org, String org, String contentId, String userId, String header) {
		super();
		this.root_org = root_org;
		this.org = org;
		this.contentId = contentId;
		this.userId = userId;
		this.header = header;
	}

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) { this.userId = userId; }

	public String getHeader() { return header; }

	public void setHeader(String header) { this.header = header; }
}
