package com.infosys.lexauthoringservices.model.cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("master_values_v2")
public class OrdinalsModel {

	@Column("date_modified")
	private Date dateModified;
	
	@Column("values")
	private List<String> values;
	
	@Column("strignify")
	Boolean stringify;
	
	@PrimaryKey
	OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel;

	public OrdinalsModel() {
		super();
	}

	public OrdinalsModel(Date dateModified, List<String> values, Boolean stringify,
			OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel) {
		super();
		this.dateModified = dateModified;
		this.values = values;
		this.stringify = stringify;
		this.ordinalsPrimaryKeyModel = ordinalsPrimaryKeyModel;
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

	public Boolean getStringify() {
		return stringify;
	}

	public void setStringify(Boolean stringify) {
		this.stringify = stringify;
	}

	public OrdinalsPrimaryKeyModel getOrdinalsPrimaryKeyModel() {
		return ordinalsPrimaryKeyModel;
	}

	public void setOrdinalsPrimaryKeyModel(OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel) {
		this.ordinalsPrimaryKeyModel = ordinalsPrimaryKeyModel;
	}
	
	
}
