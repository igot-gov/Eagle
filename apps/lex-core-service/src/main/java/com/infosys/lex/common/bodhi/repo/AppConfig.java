/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.bodhi.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("app_config")
public class AppConfig {
	
	@PrimaryKey
	private AppConfigPrimaryKey primaryKey;

	@Column("value")
	private String value;
	
	@Column
	private String remarks;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public AppConfigPrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(AppConfigPrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AppConfig(AppConfigPrimaryKey primaryKey, String value, String remarks) {
		super();
		this.primaryKey = primaryKey;
		this.value = value;
		this.remarks = remarks;
	}



}
