/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "common_learning_goals", schema = "wingspan")
public class CommonGoal {

	@EmbeddedId
	private CommonGoalKey primaryKey;

	@Column(name = "group_id")
	private String groupId;

	@Column(name = "goal_content_id")
	private String goalContentId;

	@Column(name = "created_on")
	private Timestamp createdOn;

	@Column(name = "updated_on")
	private Timestamp updatedOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "updated_by")
	private String updatedBy;

	public CommonGoalKey getPrimaryKey() {
		return primaryKey;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGoalContentId() {
		return goalContentId;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public Timestamp getUpdatedOn() {
		return updatedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setPrimaryKey(CommonGoalKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGoalContentId(String goalContentId) {
		this.goalContentId = goalContentId;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public void setUpdatedOn(Timestamp updatedOn) {
		this.updatedOn = updatedOn;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public CommonGoal() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonGoal(CommonGoalKey primaryKey, String groupId, String goalContentId, Timestamp createdOn,
			Timestamp updatedOn, String createdBy, String updatedBy) {
		super();
		this.primaryKey = primaryKey;
		this.groupId = groupId;
		this.goalContentId = goalContentId;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "CommonGoal [primaryKey=" + primaryKey + ", groupId=" + groupId + ", goalContentId=" + goalContentId
				+ ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + "]";
	}
}
