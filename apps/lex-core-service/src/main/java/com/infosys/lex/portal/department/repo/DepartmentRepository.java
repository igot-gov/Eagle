package com.infosys.lex.portal.department.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.Department;

public interface DepartmentRepository extends CrudRepository<Department, Integer> {

	Department findByDeptId(Integer id);

	List<Department> findAll();

	boolean existsByDeptName(String deptName);
}
