package com.infosys.lexauthoringservices.model.cassandra;

import java.io.Serializable;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ConfigurableContentHierarchyPrimaryKeyModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name="root_org",type=PrimaryKeyType.PARTITIONED)
	private String rootOrg;
	
	@PrimaryKeyColumn(name="content_type")
	private String contentType;
	
	@PrimaryKeyColumn(name="child_content_type")
	private String childContentType;

	public ConfigurableContentHierarchyPrimaryKeyModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ConfigurableContentHierarchyPrimaryKeyModel(String rootOrg, String contentType, String childContentType) {
		super();
		this.rootOrg = rootOrg;
		this.contentType = contentType;
		this.childContentType = childContentType;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getChildContentType() {
		return childContentType;
	}

	public void setChildContentType(String childContentType) {
		this.childContentType = childContentType;
	}
	
	
}
