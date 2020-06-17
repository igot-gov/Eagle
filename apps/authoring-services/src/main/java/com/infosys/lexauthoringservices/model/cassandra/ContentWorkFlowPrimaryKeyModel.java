package com.infosys.lexauthoringservices.model.cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ContentWorkFlowPrimaryKeyModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "root_org",type=PrimaryKeyType.PARTITIONED)
	private String root_org;

	@PrimaryKeyColumn(name = "org")
	private String org;
	
	@PrimaryKeyColumn(name = "content_type")
	private String contentType;

	public ContentWorkFlowPrimaryKeyModel(){
		super();
	}
	
	public ContentWorkFlowPrimaryKeyModel(String root_org, String org, String contentType) {
		super();
		this.root_org = root_org;
		this.org = org;
		this.contentType = contentType;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
