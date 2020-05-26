/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.bodhi.repo;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "common_learning_goals_language", schema = "wingspan")
public class CommonGoalLang {

	@EmbeddedId
	private CommonGoalLangKey primaryKey;

	@Column(name = "goal_desc")
	private String goalDesc;

	@Column(name = "group_name")
	private String groupName;

	public CommonGoalLang(CommonGoalLangKey primaryKey, String goalDesc, String groupName, String goalTitle) {
		super();
		this.primaryKey = primaryKey;
		this.goalDesc = goalDesc;
		this.groupName = groupName;
		this.goalTitle = goalTitle;
	}

	@Column(name = "goal_title")
	private String goalTitle;

	public CommonGoalLangKey getPrimaryKey() {
		return primaryKey;
	}

	public String getGoalDesc() {
		return goalDesc;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGoalTitle() {
		return goalTitle;
	}

	public void setPrimaryKey(CommonGoalLangKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setGoalDesc(String goalDesc) {
		this.goalDesc = goalDesc;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}

	public CommonGoalLang() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "CommonGoalLang [primaryKey=" + primaryKey + ", goalDesc=" + goalDesc + ", groupName=" + groupName
				+ ", goalTitle=" + goalTitle + "]";
	}
}
