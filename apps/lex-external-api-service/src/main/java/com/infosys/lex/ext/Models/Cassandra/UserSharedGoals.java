package com.infosys.lex.ext.Models.Cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_shared_goals")
public class UserSharedGoals {

	@PrimaryKey
	UserSharedGoalsPrimaryKey userSharedGoalsPrimaryKey;

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

	@Column("shared_on")
	private Date sharedOn;

	@Column("status")
	private Integer status;

	@Column("status_message")
	private String statusMessage;

	public UserSharedGoalsPrimaryKey getUserSharedGoalsPrimaryKey() {
		return userSharedGoalsPrimaryKey;
	}

	public void setUserSharedGoalsPrimaryKey(UserSharedGoalsPrimaryKey userSharedGoalsPrimaryKey) {
		this.userSharedGoalsPrimaryKey = userSharedGoalsPrimaryKey;
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

	public Date getSharedOn() {
		return sharedOn;
	}

	public void setSharedOn(Date sharedOn) {
		this.sharedOn = sharedOn;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

}
