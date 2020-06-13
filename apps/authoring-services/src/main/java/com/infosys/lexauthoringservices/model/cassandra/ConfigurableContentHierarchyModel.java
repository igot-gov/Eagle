package com.infosys.lexauthoringservices.model.cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("configurable_content_hierarchy")
public class ConfigurableContentHierarchyModel {
	
	@Column("position")
	private String position;
	
	@Column("condition")
	private String condition;
	
	@PrimaryKey
	ConfigurableContentHierarchyPrimaryKeyModel configurableContentHierarchyPrimaryKeyModel;

	public ConfigurableContentHierarchyModel() {
		super();
	}

	public ConfigurableContentHierarchyModel(String position, String condition,
			ConfigurableContentHierarchyPrimaryKeyModel configurableContentHierarchyPrimaryKeyModel) {
		super();
		this.position = position;
		this.condition = condition;
		this.configurableContentHierarchyPrimaryKeyModel = configurableContentHierarchyPrimaryKeyModel;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public ConfigurableContentHierarchyPrimaryKeyModel getConfigurableContentHierarchyPrimaryKeyModel() {
		return configurableContentHierarchyPrimaryKeyModel;
	}

	public void setConfigurableContentHierarchyPrimaryKeyModel(
			ConfigurableContentHierarchyPrimaryKeyModel configurableContentHierarchyPrimaryKeyModel) {
		this.configurableContentHierarchyPrimaryKeyModel = configurableContentHierarchyPrimaryKeyModel;
	}

}
