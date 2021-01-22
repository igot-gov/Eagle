package com.infosys.lex.portal.department.service;

import com.infosys.lex.portal.department.dto.DepartmentRole;
import com.infosys.lex.portal.department.dto.Role;

import java.util.List;

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
}
