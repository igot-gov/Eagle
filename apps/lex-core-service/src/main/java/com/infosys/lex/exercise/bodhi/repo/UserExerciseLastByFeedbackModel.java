/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.exercise.bodhi.repo;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_exercise_last_by_feedback")
public class UserExerciseLastByFeedbackModel {

	@PrimaryKey
	private UserExerciseLastByFeedbackPrimaryKeyModel primaryKey;

	@Column("submission_id")
	private UUID submissionId;

	@Column("feedback_by")
	private String feedbackBy;

	@Column("feedback_submission_id")
	private UUID feedbackSubmissionId;

	public UserExerciseLastByFeedbackPrimaryKeyModel getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(UserExerciseLastByFeedbackPrimaryKeyModel primaryKey) {
		this.primaryKey = primaryKey;
	}

	public UUID getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(UUID submissionId) {
		this.submissionId = submissionId;
	}

	public String getFeedbackBy() {
		return feedbackBy;
	}

	public void setFeedbackBy(String feedbackBy) {
		this.feedbackBy = feedbackBy;
	}

	public UUID getFeedbackSubmissionId() {
		return feedbackSubmissionId;
	}

	public void setFeedbackSubmissionId(UUID feedbackSubmissionId) {
		this.feedbackSubmissionId = feedbackSubmissionId;
	}

	public UserExerciseLastByFeedbackModel(UserExerciseLastByFeedbackPrimaryKeyModel primaryKey, UUID submissionId,
			String feedbackBy, UUID feedbackSubmissionId) {
		super();
		this.primaryKey = primaryKey;
		this.submissionId = submissionId;
		this.feedbackBy = feedbackBy;
		this.feedbackSubmissionId = feedbackSubmissionId;
	}

	public UserExerciseLastByFeedbackModel() {
		super();
	}

	@Override
	public String toString() {
		return "UserExerciseLastByFeedbackModel [primaryKey=" + primaryKey + ", submissionId=" + submissionId
				+ ", feedbackBy=" + feedbackBy + ", feedbackSubmissionId=" + feedbackSubmissionId + "]";
	}

}
