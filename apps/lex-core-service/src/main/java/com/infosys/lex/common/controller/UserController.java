/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.common.service.UserService;

@RestController
@RequestMapping("/v1/user")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/{identifier}/preferences")
	public ResponseEntity<?> getUserPreferences(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("identifier") String userId) throws JsonMappingException, IOException {

		return new ResponseEntity<>(userService.getUserPreferences(rootOrg, userId), HttpStatus.OK);
	}

	@PutMapping("/{identifier}/preferences")
	public ResponseEntity<?> updateUserPrefereces(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("identifier") String userId, @RequestBody Map<String, Object> preferences)
			throws JsonProcessingException {

		userService.setUserPreferences(rootOrg, userId, preferences);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
