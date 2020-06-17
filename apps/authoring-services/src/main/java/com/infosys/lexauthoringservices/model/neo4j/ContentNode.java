package com.infosys.lexauthoringservices.model.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentNode {

	private Long id;
	private String identifier;
	private String rootOrg;
	private Map<String, Object> metaData;
	private List<Relation> parents;
	private List<Relation> children;

	public ContentNode() {
		parents = new ArrayList<>();
		children = new ArrayList<>();
		metaData = new HashMap<>();
		parents = new ArrayList<>();
		children = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public Map<String, Object> getMetadata() {
		return metaData;
	}

	public void setMetadata(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	public List<Relation> getParents() {
		return parents;
	}

	public void setParents(List<Relation> parents) {
		this.parents = parents;
	}

	public List<Relation> getChildren() {
		return children;
	}

	public void setChildren(List<Relation> children) {
		this.children = children;
	}

}
