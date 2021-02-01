package org.sunbird.portal.department.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.sunbird.portal.department.dto.UserDepartmentRole;

import java.util.List;

public interface UserDepartmentRoleRepository extends JpaRepository<UserDepartmentRole, Integer> {

	List<UserDepartmentRole> findByUserId(String userId);
	
	List<UserDepartmentRole> findAllByUserIdAndIsActiveAndIsBlocked(String userId, boolean isActive, boolean isBlocked);

	List<UserDepartmentRole> findByDeptId(Integer deptId);

//	List<UserDepartmentRole> findByDeptRoleId(List<String> userDeptIds);

	UserDepartmentRole findByUserIdAndDeptId(String userId, Integer deptId);

	List<UserDepartmentRole> findAllByUserIdAndDeptId(String userId, List<Integer> deptIds);

	@Query(value = "SELECT count(*) FROM wingspan.user_department_role WHERE ?1 = ANY (role_ids) and dept_id = ?2", nativeQuery = true)
	int getTotalUserCountOnRoleIdAndDeptId(Integer roleId, Integer deptId);
}
