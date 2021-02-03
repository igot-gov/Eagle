package org.sunbird.portal.department.service;

import java.util.List;
import java.util.Set;

import org.sunbird.portal.department.dto.DepartmentRole;
import org.sunbird.portal.department.dto.Role;

public interface RoleService {

	Role addRole(Role role) throws Exception;

	Role updateRole(Role role) throws Exception;

	Role getRoleById(Integer roleId) throws Exception;

	Iterable<Role> getAllRoles();

	DepartmentRole addDepartmentRole(DepartmentRole deptRole) throws Exception;
	
	DepartmentRole getDepartmentRoleById(String deptRoleId);

	boolean removeDepartmentRole(Integer deptRoleId) throws Exception;

	Iterable<DepartmentRole> getAllDepartmentRoles();

	public List<String> getUserDepartMentRoles(String userId);

	Set<String> getUserRoles(String userId);
}
