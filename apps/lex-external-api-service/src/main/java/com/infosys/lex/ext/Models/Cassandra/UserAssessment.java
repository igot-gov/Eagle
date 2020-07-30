package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_assessment_master")
public class UserAssessment {
	@PrimaryKey
	UserAssessmentPrimaryKey userAssessmentPrimaryKey;
	@Column("correct_count")
	private Integer correctCount;
	@Column("date_created")
	private Date dateCreated;
	@Column("incorrect_count")
	private Integer inCorrectCount;
	@Column("not_answered_count")
	private Integer notAnsweredCount;
	@Column("parent_content_type")
	private String parentContentType;
	@Column("pass_percent")
	private Double passPercent;
	@Column("source_id")
	private String sourceId;
	@Column("source_title")
	private String sourceTitle;
	@Column("user_id")
	private String userId;

	public UserAssessmentPrimaryKey getUserAssessmentPrimaryKey() {
		return userAssessmentPrimaryKey;
	}

	public void setUserAssessmentPrimaryKey(UserAssessmentPrimaryKey userAssessmentPrimaryKey) {
		this.userAssessmentPrimaryKey = userAssessmentPrimaryKey;
	}

	public Integer getCorrectCount() {
		return correctCount;
	}

	public void setCorrectCount(Integer correctCount) {
		this.correctCount = correctCount;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Integer getInCorrectCount() {
		return inCorrectCount;
	}

	public void setInCorrectCount(Integer inCorrectCount) {
		this.inCorrectCount = inCorrectCount;
	}

	public Integer getNotAnsweredCount() {
		return notAnsweredCount;
	}

	public void setNotAnsweredCount(Integer notAnsweredCount) {
		this.notAnsweredCount = notAnsweredCount;
	}

	public String getParentContentType() {
		return parentContentType;
	}

	public void setParentContentType(String parentContentType) {
		this.parentContentType = parentContentType;
	}

	public Double getPassPercent() {
		return passPercent;
	}

	public void setPassPercent(Double passPercent) {
		this.passPercent = passPercent;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
