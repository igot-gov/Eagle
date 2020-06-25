package com.infosys.lex.userroles.entities;

import java.util.Set;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("user_roles")
public class UserRoles {

	@PrimaryKey
	private UserRolesKey userRolesKey;

	private Set<String> roles;

	public UserRolesKey getUserRoleKey() {
		return userRolesKey;
	}

	public void setUserRolesKey(UserRolesKey userId) {
		this.userRolesKey = userId;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

}
