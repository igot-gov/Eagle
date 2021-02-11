package org.sunbird.portal.department.controller;

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
import org.sunbird.portal.department.PortalConstants;
import org.sunbird.portal.department.dto.UserDepartmentRole;
import org.sunbird.portal.department.model.DepartmentInfo;
import org.sunbird.portal.department.model.UserDepartmentInfo;
import org.sunbird.portal.department.service.MdoPortalService;
import org.sunbird.portal.department.service.PortalService;
import org.sunbird.portal.department.service.SpvPortalService;

@RestController
public class PortalController {
	@Autowired
	PortalService portalService;

	@Autowired
	SpvPortalService spvPortalService;

	@Autowired
	MdoPortalService mdoPortalService;

	// ----------------- Public APIs --------------------
	@GetMapping("/portal/listDeptNames")
	public ResponseEntity<?> getDeptNameList() throws Exception {
		return new ResponseEntity<>(portalService.getDeptNameList(), HttpStatus.OK);
	}

	@GetMapping("/portal/getAllDept")
	public ResponseEntity<?> getAllDepartment() throws Exception {
		return new ResponseEntity<>(portalService.getAllDept(), HttpStatus.OK);
	}
	
	@GetMapping("/portal/deptSearch")
	public ResponseEntity<?> searchDepartment(@RequestParam(name = "friendlyName", required = true) String deptName) throws Exception {
		return new ResponseEntity<>(portalService.searchDept(deptName), HttpStatus.OK);
	}

	// ----------------- END of Public APIs --------------------
	// ----------------- SPV APIs -----------------------
	@GetMapping("/portal/spv/isAdmin")
	public ResponseEntity<Boolean> isSpvAdmin(@RequestHeader("wid") String wid) throws Exception {
		return new ResponseEntity<Boolean>(
				portalService.isAdmin(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid),
				HttpStatus.OK);
	}

	@GetMapping("/portal/spv/mydepartment")
	public ResponseEntity<?> getMyDepartments(@RequestHeader("wid") String wid,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<>(spvPortalService.getMyDepartment(wid, isUserInfoRequired, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/portal/spv/department")
	public ResponseEntity<DepartmentInfo> addDepartment(@RequestHeader("wid") String wid,
			@RequestBody DepartmentInfo deptInfo, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<DepartmentInfo>(spvPortalService.addDepartment(wid, deptInfo, rootOrg),
				HttpStatus.OK);
	}

	@PatchMapping("/portal/spv/department")
	public ResponseEntity<DepartmentInfo> updateDepartment(@RequestHeader("wid") String wid,
			@RequestBody DepartmentInfo deptInfo, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<DepartmentInfo>(spvPortalService.updateDepartment(deptInfo, rootOrg), HttpStatus.OK);
	}

	@GetMapping("/portal/spv/department")
	public ResponseEntity<List<DepartmentInfo>> getAllDepartments(@RequestHeader("wid") String wid,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<List<DepartmentInfo>>(spvPortalService.getAllDepartments(rootOrg), HttpStatus.OK);
	}

	@GetMapping("/portal/spv/department/{dept_id}")
	public ResponseEntity<DepartmentInfo> getDepartmentById(@PathVariable("dept_id") Integer deptId,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired,
			@RequestHeader("wid") String wid, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<DepartmentInfo>(
				spvPortalService.getDepartmentById(deptId, isUserInfoRequired, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/portal/spv/userrole")
	public ResponseEntity<UserDepartmentInfo> addUserRoleInDepartmentBySPV(@RequestBody UserDepartmentRole userDeptRole,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org,
			@RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.addUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	@PatchMapping("/portal/spv/userrole")
	public ResponseEntity<UserDepartmentInfo> updateUserRoleInDepartmentBySPV(
			@RequestBody UserDepartmentRole userDeptRole, @RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.SPV_DEPT_TYPE, PortalConstants.SPV_ROLE_NAME, wid);
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.updateUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	@GetMapping("/portal/spv/role")
	public ResponseEntity<?> getSpvRoles(@RequestHeader("rootOrg") String rootOrg) {
		return new ResponseEntity<>(portalService.getAllDepartments(rootOrg), HttpStatus.OK);
	}
	// ----------------- SPV APIs -----------------------

	// ----------------- MDO APIs -----------------------
	@GetMapping("/portal/mdo/isAdmin")
	public ResponseEntity<Boolean> isMdoAdmin(@RequestHeader("wid") String wid) throws Exception {
		return new ResponseEntity<Boolean>(
				portalService.isAdmin(PortalConstants.MDO_DEPT_TYPE, PortalConstants.MDO_ROLE_NAME, wid),
				HttpStatus.OK);
	}

	@GetMapping("/portal/mdo/mydepartment")
	public ResponseEntity<?> getMyMdoDepartment(@RequestHeader("wid") String wid,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.MDO_DEPT_TYPE, PortalConstants.MDO_ROLE_NAME, wid);
		return new ResponseEntity<>(mdoPortalService.getMyDepartment(wid, isUserInfoRequired, rootOrg), HttpStatus.OK);
	}

	@PatchMapping("/portal/mdo/department")
	public ResponseEntity<DepartmentInfo> updateMdoDepartment(@RequestHeader("wid") String wid,
			@RequestBody DepartmentInfo deptInfo, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.MDO_DEPT_TYPE, PortalConstants.MDO_ROLE_NAME, wid, deptInfo.getId());
		return new ResponseEntity<DepartmentInfo>(mdoPortalService.updateDepartment(deptInfo, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/portal/mdo/userrole")
	public ResponseEntity<UserDepartmentInfo> addUserRoleInDepartmentByMDO(@RequestBody UserDepartmentRole userDeptRole,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org,
			@RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.MDO_DEPT_TYPE, PortalConstants.MDO_ROLE_NAME, wid, userDeptRole.getDeptId());
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.addUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	@PatchMapping("/portal/mdo/userrole")
	public ResponseEntity<UserDepartmentInfo> updateUserRoleInDepartmentByMDO(
			@RequestBody UserDepartmentRole userDeptRole, @RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.MDO_DEPT_TYPE, PortalConstants.MDO_ROLE_NAME, wid, userDeptRole.getDeptId());
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.updateUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	// ----------------- MDO APIs -----------------------

	// ----------------- CBP APIs -----------------------
	@GetMapping("/portal/cbp/isAdmin")
	public ResponseEntity<Boolean> isCBPAdmin(@RequestHeader("wid") String wid) throws Exception {
		return new ResponseEntity<Boolean>(portalService.validateCBPUserLogin(wid), HttpStatus.OK);
	}

	@GetMapping("/portal/cbp/mydepartment")
	public ResponseEntity<?> getMyCbpDepartment(@RequestHeader("wid") String wid,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateCBPUserAccess(wid);
		return new ResponseEntity<>(portalService.getMyCbpDepartment(wid, rootOrg), HttpStatus.OK);
	}

	@PatchMapping("/portal/cbp/department")
	public ResponseEntity<DepartmentInfo> updateCbpDepartment(@RequestHeader("wid") String wid,
			@RequestBody DepartmentInfo deptInfo, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		validateUserAccess(PortalConstants.CBP_DEPT_TYPE, PortalConstants.CBP_ROLE_NAME, wid);
		return new ResponseEntity<DepartmentInfo>(mdoPortalService.updateDepartment(deptInfo, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/portal/cbp/userrole")
	public ResponseEntity<UserDepartmentInfo> addUserRoleInDepartmentByCBP(@RequestBody UserDepartmentRole userDeptRole,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org,
			@RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.CBP_DEPT_TYPE, PortalConstants.CBP_ROLE_NAME, wid, userDeptRole.getDeptId());
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.addUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	@PatchMapping("/portal/cbp/userrole")
	public ResponseEntity<UserDepartmentInfo> updateUserRoleInDepartmentByCBP(
			@RequestBody UserDepartmentRole userDeptRole, @RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader("wid") String wid) throws Exception {
		validateUserAccess(PortalConstants.CBP_DEPT_TYPE, PortalConstants.CBP_ROLE_NAME, wid, userDeptRole.getDeptId());
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.updateUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

	@GetMapping("/portal/cbp/searchUser/deptId/{dept_id}/role/{role_name}/userlike/{username}")
	public ResponseEntity<?> searchUserForRole(@PathVariable("dept_id") Integer deptId,
			@PathVariable("role_name") String roleName, @PathVariable("username") String userName) throws Exception {
		return new ResponseEntity<>(portalService.searchUserForRole(deptId, roleName, userName), HttpStatus.OK);
	}
	// ----------------- END OF CBP APIs -----------------------

	@GetMapping("/portal/department/{dept_id}")
	public ResponseEntity<DepartmentInfo> getDepartmentById(@PathVariable("dept_id") Integer deptId,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired,
			@RequestHeader("rootOrg") String rootOrg) {
		return new ResponseEntity<DepartmentInfo>(portalService.getDepartmentById(deptId, isUserInfoRequired, rootOrg),
				HttpStatus.OK);
	}

	@GetMapping("/portal/mydepartment")
	public ResponseEntity<DepartmentInfo> getMyDepartmentDetails(@RequestHeader("wid") String userId,
			@RequestParam(name = "allUsers", required = false) boolean isUserInfoRequired) throws Exception {
		return new ResponseEntity<DepartmentInfo>(portalService.getMyDepartmentDetails(userId, isUserInfoRequired),
				HttpStatus.OK);
	}

	@PatchMapping("/portal/department")
	public ResponseEntity<DepartmentInfo> updateDepartment(@RequestBody DepartmentInfo deptInfo,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<DepartmentInfo>(portalService.updateDepartment(deptInfo, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/portal/userrole")
	public ResponseEntity<UserDepartmentInfo> updateUserRoleInDepartment(
			@Valid @RequestBody UserDepartmentRole userDeptRole, @RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("org") String org, @RequestHeader("wid") String wid) throws Exception {
		return new ResponseEntity<UserDepartmentInfo>(
				portalService.addUserRoleInDepartment(userDeptRole, wid, rootOrg, org), HttpStatus.OK);
	}

//	@GetMapping("/portal/user/{user_id}/departmentRoles")
//	public ResponseEntity<List<UserDepartmentInfo>> getUserDepartments(@PathVariable("user_id") String userId) {
//		return new ResponseEntity<List<UserDepartmentInfo>>(portalService.getUserDepartments(userId), HttpStatus.OK);
//	}

	@GetMapping("/portal/department/{dept_id}/user/{user_id}/isAdmin")
	public ResponseEntity<Boolean> checkAdminPrivilage(@PathVariable("dept_id") Integer deptId,
			@PathVariable("user_id") String userId, @RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<Boolean>(portalService.checkAdminPrivilage(deptId, userId, rootOrg), HttpStatus.OK);
	}

//	@GetMapping("/portal/{dept_key}/isAdmin")
//	public ResponseEntity<Boolean> checkMDOAdminPrivilage(@PathVariable("dept_key") String deptKey,
//			@RequestHeader("wid") String wid) throws Exception {
//		return new ResponseEntity<Boolean>(portalService.checkMdoAdminPrivilage(deptKey, wid), HttpStatus.OK);
//	}

	private void validateUserAccess(String deptType, String roleName, String wid) throws Exception {
		validateUserAccess(deptType, roleName, wid, -1);
	}

	private void validateUserAccess(String deptType, String roleName, String wid, Integer deptId) throws Exception {
		if (!portalService.isAdmin(deptType, roleName, wid)) {
			throw new Exception("User is not assigned with Role: '" + roleName + "'.");
		}
	}

	private void validateCBPUserAccess(String userId) throws Exception {
		if (!portalService.validateCBPUserLogin(userId)) {
			throw new Exception("User is not assigned with any CBP related roles.");
		}
	}
}