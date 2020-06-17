package com.infosys.lexauthoringservices.model.cassandra;

import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("content_work_flow")
public class ContentWorkFlowModel {

	@Column("work_flow")
	private List<String> content_work_flow;
	
	@Column("flow_operations")
	private List<String> work_flow_operations;
	
	public List<String> getContent_work_flow() {
		return content_work_flow;
	}

	public void setContent_work_flow(List<String> content_work_flow) {
		this.content_work_flow = content_work_flow;
	}

	public List<String> getWork_flow_operations() {
		return work_flow_operations;
	}

	public void setWork_flow_operations(List<String> work_flow_operations) {
		this.work_flow_operations = work_flow_operations;
	}

	public ContentWorkFlowPrimaryKeyModel getContentWorkFlowPrimaryKeyModel() {
		return contentWorkFlowPrimaryKeyModel;
	}

	public void setContentWorkFlowPrimaryKeyModel(ContentWorkFlowPrimaryKeyModel contentWorkFlowPrimaryKeyModel) {
		this.contentWorkFlowPrimaryKeyModel = contentWorkFlowPrimaryKeyModel;
	}

	@PrimaryKey
	ContentWorkFlowPrimaryKeyModel contentWorkFlowPrimaryKeyModel;
	
	public ContentWorkFlowModel(List<String> content_work_flow, List<String> work_flow_operations,
			ContentWorkFlowPrimaryKeyModel contentWorkFlowPrimaryKeyModel) {
		super();
		this.content_work_flow = content_work_flow;
		this.work_flow_operations = work_flow_operations;
		this.contentWorkFlowPrimaryKeyModel = contentWorkFlowPrimaryKeyModel;
	}

	public ContentWorkFlowModel() {
		super();
	}

}
