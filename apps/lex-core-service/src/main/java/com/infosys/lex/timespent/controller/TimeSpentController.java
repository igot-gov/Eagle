/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.timespent.controller;

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
import com.infosys.lex.timespent.service.TimeSpentService;

@RestController
@CrossOrigin(origins = "*")
public class TimeSpentController {
	@Autowired
	TimeSpentService tsService;

	@GetMapping("/v3/users/{uuid}/dashboard/timespent")
	public ResponseEntity<Map<String, Object>> getTimeSpent(@RequestHeader("rootOrg") String rootorg , @PathVariable("uuid") String uuid,
			@RequestParam(defaultValue = "0", required = false, name = "startdate") String startDate,
			@RequestParam(defaultValue = "0", required = false, name = "enddate") String endDate) throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				tsService.getUserDashboard(rootorg, uuid, startDate, endDate),
				HttpStatus.OK);
	}
}
