/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.userroles.bodhi.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.lex.userroles.entities.UserRoles;

@Repository
public interface UserRolesRepository extends CassandraRepository<UserRoles, String> {

	public UserRoles findByUserRolesKeyRootOrgAndUserRolesKeyUserId(String rootOrg, String userId);

	@Query("select * from user_roles where root_org=?0 and user_id in ?1")
	public List<UserRoles> findById(String rootOrg, Set<String> users);
}
