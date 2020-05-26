/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.attendence.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.attendence.service.AttendanceService;

@RestController
@CrossOrigin(origins = "*")
public class AttendanceController {

	@Autowired
	AttendanceService attendanceService;

	@GetMapping("v1/users/{user_id}/verify-attendence")
	public ResponseEntity<Map<String, Object>> verifyAttendence(@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg, @RequestParam("content_id") List<String> contentIds)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				attendanceService.verifyUserAttencdance(rootOrg, userId, contentIds), HttpStatus.OK);
	}

	@GetMapping("v1/users/{user_id}/attended-content")
	public ResponseEntity<List<Map<String, Object>>> fetchAttendedContent(@PathVariable("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg, @RequestParam(name = "source_fields",defaultValue = "", required = false) List<String> sourceFields)
			throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(
				attendanceService.fetchAttendedContent(rootOrg, userId, sourceFields), HttpStatus.OK);
	}
	
	@GetMapping("v1/content/{content_id}/attended-users")
	public ResponseEntity<List<Map<String, Object>>> fetchCohorts(@PathVariable("content_id") String contentId,
			@RequestHeader("rootOrg") String rootOrg)
			throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(
				attendanceService.fetchCohorts(rootOrg,contentId), HttpStatus.OK);
	}
	

}