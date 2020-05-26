/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoalDTO {

	@JsonProperty("goal_id")
	private String goalId;

	@JsonProperty("goal_type")
	private String goalType;

	@JsonProperty("goal_desc")
	private String goalDesc;

	@JsonProperty("goal_duration")

	@NotNull(message = "{goal.duration.mandatory}")
	private int goalDuration;

	@JsonProperty("goal_title")
//	@NotNull(message = "{goal.title.mandatory}")
//	@NotEmpty(message = "{goal.title.notempty}")
	private String goalTitle;

	@JsonProperty("goal_content_id")
	private List<String> contentList;

	@JsonProperty("shared_by")
	private String sharedBy;

	@JsonProperty("shared_with")
	private String sharedWith;

//	@JsonProperty("version")
//	private Float version;
//
//	public Float getVersion() {
//		return version;
//	}
//
//	public void setVersion(Float version) {
//		this.version = version;
//	}

	public String getTitle() {
		return goalTitle;
	}

	public void setTitle(String title) {
		this.goalTitle = title;
	}

	public String getGoalId() {
		return goalId;
	}

	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getDesc() {
		return goalDesc;
	}

	public void setDesc(String desc) {
		this.goalDesc = desc;
	}

	public int getDuration() {
		return goalDuration;
	}

	public void setDuration(int duration) {
		this.goalDuration = duration;
	}

	public List<String> getContentList() {
		return contentList;
	}

	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
	}

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public String getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(String sharedWith) {
		this.sharedWith = sharedWith;
	}

	@Override
	public String toString() {
		return "GoalDTO [goalId=" + goalId + ", goalType=" + goalType + ", goalDesc=" + goalDesc + ", goalDuration="
				+ goalDuration + ", goalTitle=" + goalTitle + ", contentList=" + contentList + ", sharedBy=" + sharedBy
				+ ", sharedWith=" + sharedWith + "]";
	}

	public GoalDTO(String goalId, String goalType, String goalDesc,
			@NotNull(message = "{goal.duration.mandatory}") int goalDuration,
			@NotNull(message = "{goal.title.mandatory}") @NotEmpty(message = "{goal.title.notempty}") String goalTitle,
			List<String> contentList, String sharedBy, String sharedWith) {
		super();
		this.goalId = goalId;
		this.goalType = goalType;
		this.goalDesc = goalDesc;
		this.goalDuration = goalDuration;
		this.goalTitle = goalTitle;
		this.contentList = contentList;
		this.sharedBy = sharedBy;
		this.sharedWith = sharedWith;
	}

	public GoalDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

}
