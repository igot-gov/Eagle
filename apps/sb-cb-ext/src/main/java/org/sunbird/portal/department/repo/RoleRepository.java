package org.sunbird.portal.department.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.sunbird.portal.department.dto.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
	Role findRoleByRoleName(String roleName);
	List<Role> findAllByRoleNameIn(List<String> roleNames);
}
