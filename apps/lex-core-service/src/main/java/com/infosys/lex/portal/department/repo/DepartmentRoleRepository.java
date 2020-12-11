package com.infosys.lex.portal.department.repo;

import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.DepartmentRole;

public interface DepartmentRoleRepository extends CrudRepository<DepartmentRole, Integer> {
	DepartmentRole findByRoleIdAndDeptId(Integer roleId, Integer deptId);
	Iterable<DepartmentRole> findByDeptId(Integer deptId);
}
