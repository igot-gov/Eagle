/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.cohort.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.cohort.bodhi.repo.CohortUsers;
import com.infosys.lex.cohort.service.CohortsService;

@RestController
@CrossOrigin(origins = "*")
public class CohortsController {

	@Autowired
	CohortsService cohortsServ;

	/**
	 * gets all user with similar goals
	 * 
	 * @param resourceId
	 * @param userEmail
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/rootorg/{rootOrg}/resources/{resourceId}/user/{userUUID}/cohorts/commongoals")
	public ResponseEntity<List<CohortUsers>> getUsersWithSimillarusers(@PathVariable("resourceId") String resourceId,
			@PathVariable("rootOrg") String rootOrg,
			@PathVariable("userUUID") String userUUID, @RequestParam(value = "count",
			required = false,defaultValue = "20") Integer count) throws Exception
	{
		return new ResponseEntity<List<CohortUsers>>(cohortsServ.getUserWithCommonGoals(rootOrg,resourceId,userUUID,count),HttpStatus.OK);
	}

	/**
	 * gets all active users
	 * 
	 * @param resourceId
	 * @param userEmail
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/resources/{resourceId}/user/{userUUID}/cohorts/activeusers")
	public ResponseEntity<List<CohortUsers>> getActiveUsers(@PathVariable("resourceId") String contentId,
			@RequestHeader("rootOrg") String rootOrg, @PathVariable("userUUID") String userUUID,
			@RequestParam(value = "count", required = false, defaultValue = "50") Integer count,
			@RequestParam(value = "filter",required = false,defaultValue = "false")Boolean toFilter) throws Exception {
			return new ResponseEntity<List<CohortUsers>>(cohortsServ.getActiveUsers(rootOrg, contentId, userUUID, count, toFilter),
					HttpStatus.OK);
		
	}

	/**
	 * gets all authors
	 * 
	 * @param resourceId
	 * @param userEmail
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/resources/{resourceId}/user/{userUUID}/cohorts/authors")
	public ResponseEntity<List<CohortUsers>> getAuthors(@PathVariable("resourceId") String resourceId,
			@RequestHeader("rootOrg") String rootOrg, @PathVariable("userUUID") String userUUID,
			@RequestParam(value = "count", defaultValue = "20", required = false) Integer count) throws Exception {

		return new ResponseEntity<List<CohortUsers>>(cohortsServ.getAuthors(rootOrg, resourceId, userUUID, count),
				HttpStatus.OK);
	}

	/**
	 * gets all educators
	 * 
	 * @param resourceId
	 * @param userEmail
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/resources/{resourceId}/user/{userUUID}/cohorts/educators")
	public ResponseEntity<List<CohortUsers>> getEducators(@PathVariable("resourceId") String resourceId,
			@RequestHeader("rootOrg") String rootOrg, @PathVariable("userUUID") String userUUID,
			@RequestParam(value = "count", defaultValue = "20", required = false) Integer count) throws Exception {

		return new ResponseEntity<List<CohortUsers>>(cohortsServ.getEducators(rootOrg, resourceId, userUUID, count),
				HttpStatus.OK);
	}

	/**
	 * gets all top-performers
	 * 
	 * @param resourceId
	 * @param userEmail
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/resources/{resourceId}/user/{userUUID}/cohorts/top-performers")
	public ResponseEntity<List<CohortUsers>> getTopPerformers(@PathVariable("resourceId") String resourceId,
			@RequestHeader("rootOrg") String rootOrg, @PathVariable("userUUID") String userUUID,
			@RequestParam(value = "count", defaultValue = "20", required = false) Integer count) throws Exception {

		return new ResponseEntity<List<CohortUsers>>(cohortsServ.getTopPerformers(rootOrg, resourceId, userUUID, count),
				HttpStatus.OK);
	}
}
