/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.goal.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionDTO {
	
	@NotNull(message= "{goal.action.sharedby.mandatory}")
	@NotEmpty(message="{goal.action.sharedby.notempty}")
	@JsonProperty("shared_by")
	private String sharedBy;
	
	@JsonProperty("message")
	private String message;
	
	@NotNull(message= "{goal.action.sharedby.mandatory}")
	@NotEmpty(message="{goal.action.sharedby.notempty}")
	@JsonProperty("goal_type")
	private String goalType;

	public String getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(String sharedBy) {
		this.sharedBy = sharedBy;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public ActionDTO(
			@NotNull(message = "{goal.action.sharedby.mandatory}") @NotEmpty(message = "{goal.action.sharedby.notempty}") String sharedBy,
			String message,
			@NotNull(message = "{goal.action.sharedby.mandatory}") @NotEmpty(message = "{goal.action.sharedby.notempty}") String goalType) {
		super();
		this.sharedBy = sharedBy;
		this.message = message;
		this.goalType = goalType;
	}

	public ActionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "ActionDTO [sharedBy=" + sharedBy + ", message=" + message + ", goalType=" + goalType + "]";
	}
	
	
}
