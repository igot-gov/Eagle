package com.infosys.lexauthoringservices.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class OrdinalsPrimaryKeyModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name="root_org",type=PrimaryKeyType.PARTITIONED)
	private String root_org;
	
	@PrimaryKeyColumn(name="entity")
	private String entity;

	public OrdinalsPrimaryKeyModel() {
		super();
	}

	public OrdinalsPrimaryKeyModel(String root_org, String entity) {
		super();
		this.root_org = root_org;
		this.entity = entity;
	}

	public String getRoot_org() {
		return root_org;
	}

	public void setRoot_org(String root_org) {
		this.root_org = root_org;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

}
