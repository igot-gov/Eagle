package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("common_learning_goals")
public class CommonLearningGoals {
	@PrimaryKey
	private String id;
	@Column("created_on")
	private Date createdOn;
	@Column("goal_content_id")
	private List<String> goalContentId;
	@Column("goal_desc")
	private String goalDescription;
	@Column("goal_group")
	private String goalGroup;
	@Column("goal_title")
	private String goalTitle;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public List<String> getGoalContentId() {
		return goalContentId;
	}

	public void setGoalContentId(List<String> goalContentId) {
		this.goalContentId = goalContentId;
	}

	public String getGoalDescription() {
		return goalDescription;
	}

	public void setGoalDescription(String goalDescription) {
		this.goalDescription = goalDescription;
	}

	public String getGoalGroup() {
		return goalGroup;
	}

	public void setGoalGroup(String goalGroup) {
		this.goalGroup = goalGroup;
	}

	public String getGoalTitle() {
		return goalTitle;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}

}
