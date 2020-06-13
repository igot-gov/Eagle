package com.infosys.lexauthoringservices.model;

import java.util.Map;

//check for integer and string in index field
public class UpdateRelationRequest {

	private String startNodeId;

	private String endNodeId;

	private Map<String, Object> relationMetaData;

	public UpdateRelationRequest(String startNodeId, String endNodeId, Map<String, Object> relationMetaData) {
		this.startNodeId = startNodeId;
		this.endNodeId = endNodeId;
		this.relationMetaData = relationMetaData;
	}

	public String getStartNodeId() {
		return startNodeId;
	}

	public void setStartNodeId(String startNodeId) {
		this.startNodeId = startNodeId;
	}

	public String getEndNodeId() {
		return endNodeId;
	}

	public void setEndNodeId(String endNodeId) {
		this.endNodeId = endNodeId;
	}

	public Map<String, Object> getRelationMetaData() {
		return relationMetaData;
	}

	public void setRelationMetaData(Map<String, Object> relationMetaData) {
		this.relationMetaData = relationMetaData;
	}
}
