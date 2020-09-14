/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.ext.Models.Cassandra;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("assessment_master")
public class AssessmentMasterModel {

	@PrimaryKey
	private AssessmentMasterPrimaryKeyModel primaryKey;

	@Column("correct_count")
	private Integer correctCount;
	@Column("date_created")
	private Date dateCreated;
	@Column("incorrect_count")
	private Integer incorrectCount;
	@Column("not_answered_count")
	private Integer notAnsweredCount;
	@Column("parent_content_type")
	private String parentContentType;
	@Column("pass_percent")
	private BigDecimal passPercent;
	@Column("parent_content_id")
	private String parentContentId;
	@Column("content_title")
	private String contentTitle;

	@Column("submitted_answer")
	private String submittedAnswer;

	@Column("assessment_hash")
	private String assessmentHash;

	public AssessmentMasterPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(AssessmentMasterPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
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

	public Integer getIncorrectCount() {
		return incorrectCount;
	}

	public void setIncorrectCount(Integer incorrectCount) {
		this.incorrectCount = incorrectCount;
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

	public BigDecimal getPassPercent() {
		return passPercent;
	}

	public void setPassPercent(BigDecimal passPercent) {
		this.passPercent = passPercent;
	}

	public String getParentContentId() {
		return parentContentId;
	}

	public void setParentContentId(String parentContentId) {
		this.parentContentId = parentContentId;
	}

	public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}

	public String getSubmittedAnswer() {
		return submittedAnswer;
	}

	public void setSubmittedAnswer(String submittedAnswer) {
		this.submittedAnswer = submittedAnswer;
	}

	public String getAssessmentHash() {
		return assessmentHash;
	}

	public void setAssessmentHash(String assessmentHash) {
		this.assessmentHash = assessmentHash;
	}

	@Override
	public String toString() {
		return "UserAssessmentMasterModel [primaryKey=" + primaryKey + ", correctCount=" + correctCount
				+ ", dateCreated=" + dateCreated + ", incorrectCount=" + incorrectCount + ", notAnsweredCount="
				+ notAnsweredCount + ", parentContentType=" + parentContentType + ", passPercent=" + passPercent
				+ ", parentContentId=" + parentContentId + ", contentTitle=" + contentTitle + ", submittedAnswer="
				+ submittedAnswer + ", assessmentHash=" + assessmentHash + "]";
	}

	public AssessmentMasterModel(AssessmentMasterPrimaryKeyModel primaryKey, Integer correctCount,
			Date dateCreated, Integer incorrectCount, Integer notAnsweredCount, String parentContentType,
			BigDecimal passPercent, String parentContentId, String contentTitle, String submittedAnswer,
			String assessmentHash) {
		this.primaryKey = primaryKey;
		this.correctCount = correctCount;
		this.dateCreated = dateCreated;
		this.incorrectCount = incorrectCount;
		this.notAnsweredCount = notAnsweredCount;
		this.parentContentType = parentContentType;
		this.passPercent = passPercent;
		this.parentContentId = parentContentId;
		this.contentTitle = contentTitle;
		this.submittedAnswer = submittedAnswer;
		this.assessmentHash = assessmentHash;
	}

	public AssessmentMasterModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
