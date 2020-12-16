package com.infosys.lex.portal.department.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.portal.department.dto.UserDepartmentRole;
import com.infosys.lex.portal.department.model.DepartmentInfo;
import com.infosys.lex.portal.department.model.UserDepartmentInfo;
import com.infosys.lex.portal.department.service.PortalService;

@RestController
public class PortalController {
	@Autowired
	PortalService portalService;

	@GetMapping("/portal/department")
	public ResponseEntity<List<DepartmentInfo>> getAllDirectories() {
		return new ResponseEntity<List<DepartmentInfo>>(portalService.getAllDepartments(), HttpStatus.OK);
	}

	@GetMapping("/portal/department/{dept_id}")
	public ResponseEntity<DepartmentInfo> getDepartmentById(@PathVariable("dept_id") Integer deptId,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired) {
		return new ResponseEntity<DepartmentInfo>(portalService.getDepartmentById(deptId, isUserInfoRequired),
				HttpStatus.OK);
	}

	@GetMapping("/portal/mydepartment")
	public ResponseEntity<DepartmentInfo> getMyDepartmentDetails(@RequestHeader("wid") String userId,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired) throws Exception {
		return new ResponseEntity<DepartmentInfo>(portalService.getMyDepartmentDetails(userId, isUserInfoRequired),
				HttpStatus.OK);
	}

	@PostMapping("/portal/department")
	public ResponseEntity<DepartmentInfo> addDepartment(@RequestBody DepartmentInfo deptInfo) throws Exception {
		return new ResponseEntity<DepartmentInfo>(portalService.addDepartment(deptInfo), HttpStatus.OK);
	}
	
	@PatchMapping("/portal/department")
	public ResponseEntity<DepartmentInfo> updateDepartment(@RequestBody DepartmentInfo deptInfo) throws Exception {
		return new ResponseEntity<DepartmentInfo>(portalService.updateDepartment(deptInfo), HttpStatus.OK);
	}

	@PostMapping("/portal/userrole")
	public ResponseEntity<UserDepartmentInfo> updateUserRoleInDepartment(
			@Valid @RequestBody UserDepartmentRole userDeptRole) throws Exception {
		return new ResponseEntity<UserDepartmentInfo>(portalService.updateUserRoleInDepartment(userDeptRole),
				HttpStatus.OK);
	}

	@GetMapping("/portal/user/{user_id}/departmentRoles")
	public ResponseEntity<List<UserDepartmentInfo>> getUserDepartments(@PathVariable("user_id") String userId) {
		return new ResponseEntity<List<UserDepartmentInfo>>(portalService.getUserDepartments(userId), HttpStatus.OK);
	}

	@GetMapping("/portal/department/{dept_id}/user/{user_id}/isAdmin")
	public ResponseEntity<Boolean> checkAdminPrivilage(@PathVariable("dept_id") Integer deptId,
			@PathVariable("user_id") String userId) {
		return new ResponseEntity<Boolean>(portalService.checkAdminPrivilage(deptId, userId), HttpStatus.OK);
	}

	@GetMapping("/portal/{dept_key}/isAdmin")
	public ResponseEntity<Boolean> checkMDOAdminPrivilage(@PathVariable("dept_key") String deptKey,
			@RequestHeader("wid") String wid) throws Exception {
		return new ResponseEntity<Boolean>(portalService.checkMdoAdminPrivilage(deptKey, wid), HttpStatus.OK);
	}
}