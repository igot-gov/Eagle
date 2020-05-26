/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.userroles.controller;

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
			@RequestParam(value = "userid", defaultValue = "undefined") @NotNull @NotEmpty String userId)
			throws Exception {
		if (userId.equals("undefined")) {
			throw new BadRequestException("Send a valid User-id");
		}
		return new ResponseEntity<Set<String>>(userRolesService.getUserRoles(rootOrg, userId), HttpStatus.OK);
	}

	@PatchMapping("/v1/update/roles")
	public ResponseEntity<String> getUserRoles(@RequestHeader(value = "rootOrg") String rootOrg,
			@Valid @RequestBody UserRolesDTO userRoles) throws Exception {
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
			throw new Exception("Invalid Operation");

		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

	}
}
