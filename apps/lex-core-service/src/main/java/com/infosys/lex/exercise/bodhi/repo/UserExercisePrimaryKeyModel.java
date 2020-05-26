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

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class UserExercisePrimaryKeyModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "root_org", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String rootOrg;
	
	@PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String userId;
	
	@PrimaryKeyColumn(name = "content_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
	private String contentId;
	
	@PrimaryKeyColumn(name = "submission_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
	private UUID submissionId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public UUID getSubmissionId() {
		return submissionId;
	}

	public void setSubmissionId(UUID submissionId) {
		this.submissionId = submissionId;
	}

	public UserExercisePrimaryKeyModel(String rootOrg,String userId, String contentId, UUID submissionId) {
		super();
		this.userId = userId;
		this.contentId = contentId;
		this.submissionId = submissionId;
		this.rootOrg = rootOrg;
		
	}

	public UserExercisePrimaryKeyModel() {
		super();
	}

	@Override
	public String toString() {
		return "UserExercisePrimaryKeyModel [userId=" + userId + ", contentId=" + contentId + ", submissionId="
				+ submissionId + "]";
	}
	
	
}
