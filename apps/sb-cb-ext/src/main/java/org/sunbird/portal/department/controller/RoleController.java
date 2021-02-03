package org.sunbird.portal.department.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.portal.department.dto.DepartmentRole;
import org.sunbird.portal.department.dto.Role;
import org.sunbird.portal.department.service.RoleService;

import java.util.Set;

@RestController
public class RoleController {

	@Autowired
	RoleService roleService;

	@GetMapping("/portal/role")
	public ResponseEntity<Iterable<Role>> getAllRoles() throws Exception {
		return new ResponseEntity<Iterable<Role>>(roleService.getAllRoles(), HttpStatus.OK);
	}

	@GetMapping("/portal/role/{role_id}")
	public ResponseEntity<Role> getRoleById(@PathVariable("role_id") Integer roleId) throws Exception {
		return new ResponseEntity<Role>(roleService.getRoleById(roleId), HttpStatus.OK);
	}

	@PostMapping("/portal/role")
	public ResponseEntity<Role> addRole(@Valid @RequestBody Role role) throws Exception {
		return new ResponseEntity<Role>(roleService.addRole(role), HttpStatus.OK);
	}

	@PatchMapping("/portal/role")
	public ResponseEntity<Role> updateRole(@Valid @RequestBody Role role) throws Exception {
		return new ResponseEntity<Role>(roleService.updateRole(role), HttpStatus.OK);
	}

	@GetMapping("/portal/deptRole")
	public ResponseEntity<Iterable<DepartmentRole>> getAllDepartmentRoles() throws Exception {
		return new ResponseEntity<Iterable<DepartmentRole>>(roleService.getAllDepartmentRoles(), HttpStatus.OK);
	}

	@GetMapping("/portal/deptRole/{deptType}")
	public ResponseEntity<DepartmentRole> getDepartmentRolesById(@PathVariable("deptType") String deptType)
			throws Exception {
		return new ResponseEntity<DepartmentRole>(roleService.getDepartmentRoleById(deptType), HttpStatus.OK);
	}

	@PostMapping("/portal/deptRole")
	public ResponseEntity<DepartmentRole> addDepartmentRole(@Valid @RequestBody DepartmentRole deptRole)
			throws Exception {
		return new ResponseEntity<DepartmentRole>(roleService.addDepartmentRole(deptRole), HttpStatus.OK);
	}

	@DeleteMapping("/portal/deptRole/{dept_role_id}")
	public ResponseEntity<Boolean> removeDepartmentRole(@PathVariable("dept_role_id") Integer deptRoleId)
			throws Exception {
		return new ResponseEntity<Boolean>(roleService.removeDepartmentRole(deptRoleId), HttpStatus.OK);
	}

	@GetMapping("/portal/{user_id}/roles")
	public ResponseEntity<Set> getUserRoles(@PathVariable("user_id") String userId)
			throws Exception {
		return new ResponseEntity<Set>(roleService.getUserRoles(userId), HttpStatus.OK);
	}
}