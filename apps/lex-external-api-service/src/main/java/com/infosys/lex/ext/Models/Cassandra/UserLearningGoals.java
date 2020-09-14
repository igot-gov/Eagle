package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_learning_goals")
public class UserLearningGoals {

	@PrimaryKey
	private UserLearningGoalsPrimaryKey userLearningGoalsPrimaryKey;

	@Column("created_on")
	private Date createdOn;

	@Column("goal_content_id")
	private List<String> goalContentId;

	@Column("goal_desc")
	private String goalDescription;

	@Column("goal_duration")
	private Integer goalDuration;

	@Column("goal_start_date")
	private Date goalStartDate;

	@Column("goal_end_date")
	private Date goalEndDate;

	@Column("goal_title")
	private String goalTitle;

	@Column("last_updated_on")
	private Date lastUpdatedOn;

	public UserLearningGoalsPrimaryKey getUserLearningGoalsPrimaryKey() {
		return userLearningGoalsPrimaryKey;
	}

	public void setUserLearningGoalsPrimaryKey(UserLearningGoalsPrimaryKey userLearningGoalsPrimaryKey) {
		this.userLearningGoalsPrimaryKey = userLearningGoalsPrimaryKey;
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

	public Integer getGoalDuration() {
		return goalDuration;
	}

	public void setGoalDuration(Integer goalDuration) {
		this.goalDuration = goalDuration;
	}

	public Date getGoalStartDate() {
		return goalStartDate;
	}

	public void setGoalStartDate(Date goalStartDate) {
		this.goalStartDate = goalStartDate;
	}

	public Date getGoalEndDate() {
		return goalEndDate;
	}

	public void setGoalEndDate(Date goalEndDate) {
		this.goalEndDate = goalEndDate;
	}

	public String getGoalTitle() {
		return goalTitle;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
}
