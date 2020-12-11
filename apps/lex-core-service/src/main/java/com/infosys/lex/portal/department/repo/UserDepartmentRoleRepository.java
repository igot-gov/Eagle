package com.infosys.lex.portal.department.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.UserDepartmentRole;

public interface UserDepartmentRoleRepository extends CrudRepository<UserDepartmentRole, Integer> {

	List<UserDepartmentRole> findByUserId(String userId);

	List<UserDepartmentRole> findByDeptId(Integer deptId);

	UserDepartmentRole findByUserIdAndDeptIdAndDeptRoleId(String userId, Integer deptId, Integer deptRoleId);
	
	List<UserDepartmentRole> findByUserIdAndDeptId(String userId, Integer deptId);
}
