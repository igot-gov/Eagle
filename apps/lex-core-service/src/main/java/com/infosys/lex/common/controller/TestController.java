/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.common.bodhi.repo.AppConfig;
import com.infosys.lex.common.bodhi.repo.AppConfigPrimaryKey;
import com.infosys.lex.common.bodhi.repo.AppConfigRepository;
import com.infosys.lex.common.service.UserUtilityService;

@RestController
public class TestController {

	@Autowired
	UserUtilityService userUtilityService;

	@Autowired
	AppConfigRepository appConfigRepository;

	@PostMapping("/v1/user/{user_id}/validate")
	public ResponseEntity<?> validateUser(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId) throws Exception {
		return new ResponseEntity<>(userUtilityService.validateUser(rootOrg, userId), HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/v1/users/validate")
	public ResponseEntity<?> validateUsers(@RequestHeader("rootOrg") String rootOrg,
			@RequestBody Map<String, Object> request) throws Exception {

		return new ResponseEntity<>(userUtilityService.validateUsers(rootOrg, (List<String>) request.get("uuids")),
				HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/v1/user/{user_id}/fetchData")
	public ResponseEntity<?> fetchData(@RequestHeader("rootOrg") String rootOrg, @PathVariable("user_id") String userId,
			@RequestBody Map<String, Object> request) throws Exception {
		return new ResponseEntity<>(
				userUtilityService.getUserDataFromUserId(rootOrg, userId, (List<String>) request.get("sources")),
				HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/v1/users/fetchData")
	public ResponseEntity<?> fetchDataUsers(@RequestHeader("rootOrg") String rootOrg,
			@RequestBody Map<String, Object> request) throws Exception {
		return new ResponseEntity<>(userUtilityService.getUsersDataFromUserIds(rootOrg,
				(List<String>) request.get("uuids"), (List<String>) request.get("sources")), HttpStatus.OK);
	}

	@PostMapping("/v1/user/{user_id}/email")
	public ResponseEntity<?> fetchEmail(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId) throws Exception {
		return new ResponseEntity<>(userUtilityService.getUserEmailFromUserId(rootOrg, userId), HttpStatus.OK);
	}

	@PostMapping("/v1/update/{appConfig}")
	public ResponseEntity<?> updateAppConfig(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("appConfig") String appConfig, @RequestBody Map<String, Object> request) throws Exception {
		AppConfig insert = appConfigRepository.findById(new AppConfigPrimaryKey(rootOrg, appConfig))
				.orElse(new AppConfig(new AppConfigPrimaryKey(rootOrg, appConfig), request.get("value").toString(),
						request.get("remark").toString()));
		insert.setValue(request.get("value").toString());
		insert.setRemarks(request.get("remark").toString());
		appConfigRepository.save(insert);
		return new ResponseEntity<>(appConfigRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping("/v1/all/appConfig")
	public ResponseEntity<?> config() throws Exception {
		return new ResponseEntity<>(appConfigRepository.findAll(), HttpStatus.OK);
	}

}
