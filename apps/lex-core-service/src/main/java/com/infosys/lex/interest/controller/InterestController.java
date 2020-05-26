/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.interest.service.InterestService;

@RestController
@CrossOrigin(origins = "*")
public class InterestController {

	@Autowired
	InterestService interestService;

	/**
	 * add or create interest
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 * @throws Exception
	 */
	@PatchMapping("/v1/users/{userid}/interests")
	public ResponseEntity<String> upsert(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("userid") String userId, @RequestParam("interest") @NotNull String interest)
			throws Exception {

		return new ResponseEntity<String>(interestService.upsert(rootOrg, userId, interest), HttpStatus.NO_CONTENT);
	}

	/**
	 * get interests of users
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{userid}/interests")
	public ResponseEntity<Map<String, Object>> getInterestedCourses(@RequestHeader(value = "rootOrg") String rootOrg,
			@NotNull @PathVariable("userid") String userId) throws Exception {

		Map<String, Object> userInterests = new HashMap<String, Object>();
		userInterests = interestService.getInterest(rootOrg, userId);
		return new ResponseEntity<Map<String, Object>>(userInterests, HttpStatus.OK);
	}

	/**
	 * delete interests of user
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param interest
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("/v1/users/{userid}/interests")
	public ResponseEntity<String> deleteCourse(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("userid") String userId, @RequestParam(name = "interest", required = true) String interest)
			throws Exception {

		return new ResponseEntity<>(interestService.delete(rootOrg, userId, interest), HttpStatus.NO_CONTENT);
	}

	/**
	 * autocompletes users interests
	 * 
	 * @param rootOrg
	 * @param org
	 * @param language
	 * @param query
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/interests/auto")
	public ResponseEntity<List<String>> autoComplete(@RequestHeader(value = "rootOrg") String rootOrg,
			@RequestHeader(value = "org") String org, @NotNull @RequestHeader(value = "langCode") String language,
			@RequestParam("query") String query, @RequestParam(value = "type", defaultValue = "topic") String type)
			throws Exception {

		return new ResponseEntity<List<String>>(interestService.autoComplete(rootOrg, org, language, query, type),
				HttpStatus.OK);
	}

	/**
	 * get suggested interests
	 * 
	 * @param rootOrg
	 * @param userid
	 * @param org
	 * @param language
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/users/{userid}/interests/suggested")
	public ResponseEntity<List<String>> suggestedComplete(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("userid") String userid, @RequestHeader(value = "org") String org,
			@NotNull @RequestHeader(value = "langCode") String language) throws Exception {

		return new ResponseEntity<List<String>>(interestService.suggestedComplete(rootOrg, userid, org, language),
				HttpStatus.OK);

	}

}
