package com.infosys.lex.portal.department.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infosys.lex.portal.department.dto.UserDepartmentRole;

public interface UserDepartmentRoleRepository extends JpaRepository<UserDepartmentRole, Integer> {

	List<UserDepartmentRole> findByUserId(String userId);
	
	List<UserDepartmentRole> findAllByUserIdAndIsActiveAndIsBlocked(String userId, boolean isActive, boolean isBlocked);

	List<UserDepartmentRole> findByDeptId(Integer deptId);

//	List<UserDepartmentRole> findByDeptRoleId(List<String> userDeptIds);

	UserDepartmentRole findByUserIdAndDeptId(String userId, Integer deptId);

	List<UserDepartmentRole> findAllByUserIdAndDeptId(String userId, List<Integer> deptIds);
}
