package com.infosys.lex.portal.department.repo;

import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
	Role findRoleByRoleName(String roleName);
}
