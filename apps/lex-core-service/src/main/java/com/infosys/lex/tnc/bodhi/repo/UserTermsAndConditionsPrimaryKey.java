/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.tnc.bodhi.repo;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserTermsAndConditionsPrimaryKey {

	@PrimaryKeyColumn(name = "root_org", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;

	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;

	@PrimaryKeyColumn(name = "doc_name", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String docName;

	@PrimaryKeyColumn(name = "doc_for", ordinal = 3, type = PrimaryKeyType.PARTITIONED)
	private String docFor;

	@PrimaryKeyColumn(name = "version", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
	private double version;

	public String getRootOrg() {
		return rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public String getDocName() {
		return docName;
	}

	public String getDocFor() {
		return docFor;
	}

	public double getVersion() {
		return version;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public void setDocFor(String docFor) {
		this.docFor = docFor;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public UserTermsAndConditionsPrimaryKey() {
		super();
	}

	public UserTermsAndConditionsPrimaryKey(String rootOrg, String userId, String docName, String docFor,
			double version) {
		super();
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.docName = docName;
		this.docFor = docFor;
		this.version = version;
	}

	@Override
	public String toString() {
		return "TermsAndConditionsPrimaryKey [rootOrg=" + rootOrg + ", userId=" + userId + ", docName=" + docName
				+ ", docFor=" + docFor + ", version=" + version + "]";
	}
}
