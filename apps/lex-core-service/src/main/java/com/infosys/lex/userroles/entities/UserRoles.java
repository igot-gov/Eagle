/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
