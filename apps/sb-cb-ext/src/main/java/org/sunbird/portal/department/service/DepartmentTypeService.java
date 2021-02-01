package org.sunbird.portal.department.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.sunbird.portal.department.dto.DepartmentType;

public interface DepartmentTypeService {

	Map<String, List<DepartmentType>> getAllDepartmentTypes();

	List<DepartmentType> getDepartmentByType(String deptType);

	DepartmentType getDepartmentTypeById(Integer id);

	Map<String, List<String>> getDepartmentTypeNames();
}
