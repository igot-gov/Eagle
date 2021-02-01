package org.sunbird.portal.department.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.sunbird.portal.department.dto.DepartmentType;

public interface DepartmentTypeRepository extends CrudRepository<DepartmentType, Integer> {

	DepartmentType findByDeptTypeAndDeptSubType(String deptType, String deptSubType);

	// @Query("select * from department_types where dept_type = ?0")
	List<DepartmentType> findByDeptTypeIgnoreCase(String deptType);

//	@Query("select distinct d.dept_type, d.dept_subtype from department_types d")
//	List<DepartmentType> findDistinctDeptTypeAndDeptSubType();
}
