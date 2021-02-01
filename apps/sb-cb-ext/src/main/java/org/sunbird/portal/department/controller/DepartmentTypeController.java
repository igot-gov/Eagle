package org.sunbird.portal.department.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.portal.department.dto.DepartmentType;
import org.sunbird.portal.department.service.DepartmentTypeService;

@RestController
public class DepartmentTypeController {
	@Autowired
	private DepartmentTypeService deptService;

	@GetMapping("/portal/departmentType")
	public ResponseEntity<Map<String, List<DepartmentType>>> getAllDepartmentTypes() {
		return new ResponseEntity<Map<String, List<DepartmentType>>>(deptService.getAllDepartmentTypes(),
				HttpStatus.OK);
	}

	@GetMapping("/portal/departmentType/{deptTypeName}")
	public ResponseEntity<List<DepartmentType>> getDepartmentByTypeName(
			@PathVariable("deptTypeName") String deptTypeName) {
		return new ResponseEntity<List<DepartmentType>>(deptService.getDepartmentByType(deptTypeName), HttpStatus.OK);
	}

	@GetMapping("/portal/departmentTypeById/{deptTypeId}")
	public ResponseEntity<DepartmentType> getDepartmentByTypeId(@PathVariable("deptTypeId") Integer deptTypeId) {
		return new ResponseEntity<DepartmentType>(deptService.getDepartmentTypeById(deptTypeId), HttpStatus.OK);
	}

	@GetMapping("portal/departmentTypeName")
	public ResponseEntity<Map<String, List<String>>> getDepartmentTypeNames() {
		return new ResponseEntity<Map<String, List<String>>>(deptService.getDepartmentTypeNames(), HttpStatus.OK);
	}
}
