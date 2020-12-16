package com.infosys.lex.portal.department.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.Department;

public interface DepartmentRepository extends CrudRepository<Department, Integer> {

	Department findByDeptId(Integer id);

	List<Department> findAll();

	boolean existsByDeptName(String deptName);
	
	List<Department> findAllByDeptTypeIdIn(Iterable<Integer> deptTypeIds);

	Department findByDeptName(String deptKey);
	
//	@Query("select dept from departments where dept.dept_type_id in (select id from department_types where dept_type = ?0)")
//	List<Department> findDeptUsingType(String deptTypeKey);
}
