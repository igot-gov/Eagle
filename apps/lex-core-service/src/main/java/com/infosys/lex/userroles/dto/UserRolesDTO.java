package com.infosys.lex.userroles.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserRolesDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "{userroles.operation.notempty}")
	@NotNull(message = "{userroles.operation.mandatory}")
	private String operation;

	@NotEmpty(message = "{userroles.userIds.notempty}")
	private List<String> userIds;

	@NotEmpty(message = "{userroles.roles.notempty}")
	private List<String> roles;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
