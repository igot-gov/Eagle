package com.infosys.lex.ext.Models.Cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("application_properties")
public class ApplicationProperties {

	@PrimaryKey
	private AppPropertiesPrimaryKey appConfigPrimaryKey;

	@Column("remarks")
	private String remarks;

	@Column("value")
	private String value;

	public AppPropertiesPrimaryKey getAppConfigPrimaryKey() {
		return appConfigPrimaryKey;
	}

	public void setAppConfigPrimaryKey(AppPropertiesPrimaryKey appConfigPrimaryKey) {
		this.appConfigPrimaryKey = appConfigPrimaryKey;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
