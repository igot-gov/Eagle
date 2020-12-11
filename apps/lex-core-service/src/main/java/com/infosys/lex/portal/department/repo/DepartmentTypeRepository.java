package com.infosys.lex.portal.department.repo;

import org.springframework.data.repository.CrudRepository;

import com.infosys.lex.portal.department.dto.DepartmentType;

public interface DepartmentTypeRepository extends CrudRepository<DepartmentType, Integer> {
	DepartmentType findByDeptTypeAndDeptSubType(String deptType, String deptSubType);
}
