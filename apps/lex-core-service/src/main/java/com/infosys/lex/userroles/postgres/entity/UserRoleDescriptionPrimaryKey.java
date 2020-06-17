package com.infosys.lex.userroles.postgres.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRoleDescriptionPrimaryKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "role")
	private String role;

	@Column(name = "language")
	private String language;

	public UserRoleDescriptionPrimaryKey() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserRoleDescriptionPrimaryKey(String role, String language) {
		super();
		this.role = role;
		this.language = language;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "UserRoleDescriptionPrimaryKey [role=" + role + ", language=" + language + "]";
	}
}
