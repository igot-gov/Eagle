package com.infosys.lexauthoringservices.model.cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("master_values")
public class MasterValues {

	@PrimaryKey
	@Column("entity")
	private String entity;

	@Column("date_created")
	private Date dateCreated;

	@Column("date_modified")
	private Date dateModified;

	@Column("values")
	private List<String> values;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public MasterValues(String entity, Date dateCreated, Date dateModified, List<String> values) {
		super();
		this.entity = entity;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.values = values;
	}

	public MasterValues() {
		super();
	}

}
