package com.infosys.lex.userroles.postgres.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRolePrimaryKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "root_org")
	private String rootOrg;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "role")
	private String role;

	public UserRolePrimaryKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserRolePrimaryKey(String rootOrg, String userId, String role) {
		super();
		this.rootOrg = rootOrg;
		this.userId = userId;
		this.role = role;
	}

	public String getRootOrg() {
		return rootOrg;
	}

	public void setRootOrg(String rootOrg) {
		this.rootOrg = rootOrg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserRolesPrimaryKey [rootOrg=" + rootOrg + ", userId=" + userId + ", role=" + role + "]";
	}

}
