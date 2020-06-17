package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

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
