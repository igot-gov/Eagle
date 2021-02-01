package org.sunbird.portal.department.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.sunbird.portal.department.dto.DepartmentRole;

public interface DepartmentRoleRepository extends CrudRepository<DepartmentRole, Integer> {
	DepartmentRole findByDeptTypeIgnoreCase(String deptType);
	List<DepartmentRole> findAllByDeptTypeIn(List<String> deptTypes);
	
//	DepartmentRole findByRoleIdAndDeptId(Integer roleId, Integer deptId);
//
//	List<DepartmentRole> findByRoleId(Integer roleId);
//	
//	List<DepartmentRole> findAllByRoleIdAndDeptTypeIdIn(Integer roleId, List<Integer> deptIds);
//	
//	Iterable<DepartmentRole> findByDeptTypeIds(List<Integer> deptTypeIds);

//	@Query("select dRole from department_roles where dRole.role_id in (:role_ids)")
//	Iterable<DepartmentRole> findByRoleIds(@Param("role_ids") List<Integer> role_ids);
	
//	@Query("select id from department_roles where id = (select dept_role_id from user_department_role where user_id = ?0)")
//	Iterable<Integer> findDepartmentRoleForUser_old(String userId);

//	@Query(value = "select * from department_roles where role_id in (select id from roles where role_name='ADMIN') and id in (select dept_role_id from user_department_role where user_id =:user_id)", nativeQuery = true)
//	Iterable<DepartmentRole> findDepartmentRoleForUser(@Param("user_id") String userId);
}
