package com.infosys.lex.userroles.controller;

import java.util.Map;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.userroles.dto.UserRolesDTO;
import com.infosys.lex.userroles.service.UserRolesService;

@RestController()
@CrossOrigin("*")
public class UserRoleController {

	@Autowired
	UserRolesService userRolesService;

	@GetMapping("v1/user/roles")
	public ResponseEntity<Set<String>> getUserRoles(@RequestHeader(value = "rootOrg") String rootOrg,
			@RequestParam(value = "userid", defaultValue = "undefined") @NotNull @NotEmpty String userId) {
		if (userId.equals("undefined")) {
			throw new BadRequestException("Send a valid User-id");
		}
		return new ResponseEntity<Set<String>>(userRolesService.getUserRoles(rootOrg, userId), HttpStatus.OK);
	}

	@PatchMapping("/v1/update/roles")
	public ResponseEntity<String> getUserRoles(@RequestHeader(value = "rootOrg") String rootOrg,
			@Valid @RequestBody UserRolesDTO userRoles) {
		if (userRoles.getOperation().toLowerCase().trim().equals("add")
				|| userRoles.getOperation().toLowerCase().trim().equals("remove")) {
//			Operation is either add or remove
			for (int i = 0; i < userRoles.getUserIds().size(); i++) {
				if (userRoles.getOperation().toLowerCase().trim().equals("add")) {
					userRolesService.addRoles(rootOrg, userRoles.getUserIds().get(i), userRoles.getRoles());
				} else if (userRoles.getOperation().toLowerCase().trim().equals("remove")) {
					userRolesService.removeRoles(rootOrg, userRoles.getUserIds().get(i), userRoles.getRoles());
				}
			}
		} else
			throw new BadRequestException("invalid.operation");

		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

	}

	/**
	 * get all the roles given to a user_id
	 *
	 * @param rootOrg
	 * @param user_id
	 * @return @
	 */
	@GetMapping("v2/users/{user_id}/roles")
	public ResponseEntity<Map<String, Object>> getUserRolesByUserId(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String user_id) {

		return new ResponseEntity<Map<String, Object>>(userRolesService.getUserRolesByUserId(rootOrg, user_id),
				HttpStatus.OK);
	}

	/**
	 * get all the users that have a particular role
	 *
	 * @param rootOrg
	 * @param role
	 * @return @
	 */
	@GetMapping("/v2/roles/{role}/users")
	public ResponseEntity<Map<String, Object>> getUserDetailsByRole(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("role") String role,
			@RequestParam(name = "prevPageNo", required = false, defaultValue = "-1") Integer prevPageNo,
			@RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize) {

		return new ResponseEntity<Map<String, Object>>(
				userRolesService.getUserDetailsByRole(rootOrg, role, prevPageNo, pageSize), HttpStatus.OK);
	}

	/**
	 * get all the default roles and master-roles
	 *
	 * @param rootOrg
	 * @return
	 */
	@GetMapping("v2/all-roles")
	public ResponseEntity<Map<String, Object>> getAllRoles(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String language) {
		return new ResponseEntity<Map<String, Object>>(userRolesService.getAllRoles(rootOrg, language), HttpStatus.OK);
	}

	/**
	 * add roles to a user
	 *
	 * @param rootOrg
	 * @param userId
	 * @param role
	 * @param userRoleMap
	 * @return @
	 */
	@PostMapping("/v2/roles")
	public ResponseEntity<?> addOrDeleteUserRoles(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(name = "userId", required = false, defaultValue = "defaultuser") String userId,
			@RequestParam(name = "role", required = false, defaultValue = "defaultrole") String role,
			@RequestBody Map<String, Object> userRoleMap) {

		userRolesService.addOrDeleteUserRoles(rootOrg, userId, role, userRoleMap);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

//	/**
//	 * delete roles of a user
//	 *
//	 * @param rootOrg
//	 * @param userId
//	 * @param role
//	 * @param userRoleMap
//	 * @return
//	 * @
//	 */
//	@DeleteMapping("/v2/roles")
//	public ResponseEntity<?> deleteRoles(@RequestHeader("rootOrg") String rootOrg,
//			@RequestParam(name = "userId", required = false, defaultValue = "defaultuser") String userId,
//			@RequestParam(name = "role", required = false, defaultValue = "defaultrole") String role,
//			@RequestBody Map<String, Object> userRoleMap)  {
//
//		userRolesService.removeUserRoles(rootOrg, userId, role, userRoleMap);
//		return new ResponseEntity<>(HttpStatus.CREATED);
//	}

}
