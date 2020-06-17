package com.infosys.lexauthoringservices.model.neo4j;

import java.io.Serializable;
import java.util.Map;

public class Relation implements Serializable {

	private static final long serialVersionUID = -7207054262120122453L;
	private long id;
	private String graphId;
	private String relationType;
	private String startNodeId;
	private String endNodeId;
	private String startNodeName;
	private String endNodeName;
	private String startNodeType;
	private String endNodeType;
	private String startNodeObjectType;
	private String endNodeObjectType;
	private Map<String, Object> metadata;
	private Map<String, Object> startNodeMetadata;
	private Map<String, Object> endNodeMetadata;

	public Relation() {

	}

	public Relation(String startNodeId, String relationType, String endNodeId) {
		this.startNodeId = startNodeId;
		this.endNodeId = endNodeId;
		this.relationType = relationType;
	}

	public Relation(Long id, String startNodeId, String relationType, String endNodeId, Map<String, Object> metadata) {
		this.id = id;
		this.startNodeId = startNodeId;
		this.relationType = relationType;
		this.endNodeId = endNodeId;
		this.metadata = metadata;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
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

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public String getGraphId() {
		return graphId;
	}

	public void setGraphId(String graphId) {
		this.graphId = graphId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStartNodeName() {
		return startNodeName;
	}

	public void setStartNodeName(String startNodeName) {
		this.startNodeName = startNodeName;
	}

	public String getEndNodeName() {
		return endNodeName;
	}

	public void setEndNodeName(String endNodeName) {
		this.endNodeName = endNodeName;
	}

	public String getStartNodeType() {
		return startNodeType;
	}

	public void setStartNodeType(String startNodeType) {
		this.startNodeType = startNodeType;
	}

	public String getEndNodeType() {
		return endNodeType;
	}

	public void setEndNodeType(String endNodeType) {
		this.endNodeType = endNodeType;
	}

	public String getStartNodeObjectType() {
		return startNodeObjectType;
	}

	public void setStartNodeObjectType(String startNodeObjectType) {
		this.startNodeObjectType = startNodeObjectType;
	}

	public String getEndNodeObjectType() {
		return endNodeObjectType;
	}

	public void setEndNodeObjectType(String endNodeObjectType) {
		this.endNodeObjectType = endNodeObjectType;
	}

	public Map<String, Object> getStartNodeMetadata() {
		return startNodeMetadata;
	}

	public void setStartNodeMetadata(Map<String, Object> startNodeMetadata) {
		this.startNodeMetadata = startNodeMetadata;
	}

	public Map<String, Object> getEndNodeMetadata() {
		return endNodeMetadata;
	}

	public void setEndNodeMetadata(Map<String, Object> endNodeMetadata) {
		this.endNodeMetadata = endNodeMetadata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endNodeId == null) ? 0 : endNodeId.hashCode());
		result = prime * result + ((relationType == null) ? 0 : relationType.hashCode());
		result = prime * result + ((startNodeId == null) ? 0 : startNodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (endNodeId == null) {
			if (other.endNodeId != null)
				return false;
		} else if (!endNodeId.equals(other.endNodeId))
			return false;
		if (relationType == null) {
			if (other.relationType != null)
				return false;
		} else if (!relationType.equals(other.relationType))
			return false;
		if (startNodeId == null) {
			if (other.startNodeId != null)
				return false;
		} else if (!startNodeId.equals(other.startNodeId))
			return false;
		return true;
	}
}
