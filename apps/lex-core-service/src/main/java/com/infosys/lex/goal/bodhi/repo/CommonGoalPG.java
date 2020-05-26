/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "common_learning_goals", schema = "goals")
public class CommonGoalPG {

	@Id
	private String id;

	@Column(name = "created_on")
	private Timestamp createdOn;

	@Column(name = "goal_content_id")
	private String goalContentId;

	@Column(name = "group_id")
	private String groupId;

	public String getId() {
		return id;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public String getGoalContentId() {
		return goalContentId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public void setGoalContentId(String goalContentId) {
		this.goalContentId = goalContentId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public CommonGoalPG() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommonGoalPG(String id, Timestamp createdOn, String goalContentId, String groupId) {
		super();
		this.id = id;
		this.createdOn = createdOn;
		this.goalContentId = goalContentId;
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "CommonGoalPG [id=" + id + ", createdOn=" + createdOn + ", goalContentId=" + goalContentId + ", groupId="
				+ groupId + "]";
	}
}
