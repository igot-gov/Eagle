package com.infosys.lex.ext.Models.Cassandra;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_assessment_by_date")
public class AssessmentData {
	@PrimaryKey
	private AssessmentDataPrimaryKey key;
	@Column("user_id")
	private String userId;
	@Column("pass_percent")
	private Float passPercent;
	@Column("source_id")
	private String sourceId;

	public AssessmentDataPrimaryKey getKey() {
		return key;
	}

	public void setKey(AssessmentDataPrimaryKey key) {
		this.key = key;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Float getPassPercent() {
		return passPercent;
	}

	public void setPassPercent(Float passPercent) {
		this.passPercent = passPercent;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public AssessmentData(AssessmentDataPrimaryKey key, String userId, Float passPercent, String sourceId) {
		super();
		this.key = key;
		this.userId = userId;
		this.passPercent = passPercent;
		this.sourceId = sourceId;
	}

	public AssessmentData() {
	}

}
