/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.badge.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.badge.postgredb.projection.BadgeDetailsProjection;
import com.infosys.lex.badge.service.BadgeService;

@RestController
@CrossOrigin(origins = "*")
public class BadgeController {

	@Autowired
	BadgeService badgeService;

	/**
	 * get all user badges with meta and progress
	 * 
	 * @param email_id
	 * @return
	 * @throws Exception
	 */
//	@GetMapping("/v2/Users/{email_id}/Achieved")
//	public ResponseEntity<Map<String, Object>> generateBagdes(@PathVariable("email_id") String emailId,
//			@PathVariable("rootOrg") String rootOrg) throws Exception {
//		return new ResponseEntity<Map<String, Object>>(badgeService.getAllBadges(rootOrg, emailId), HttpStatus.CREATED);
//	}

	/**
	 * gets the most recent badge received
	 * 
	 * @param email_id
	 * @return
	 * @throws Exception
	 */
//	@GetMapping("/v2/Users/{email_id}/Achievements/Recent")
//	public ResponseEntity<Map<String, Object>> getRecentBagde(@PathVariable("email_id") String userId,
//			@PathVariable("rootOrg") String rootOrg) throws Exception {
//		return new ResponseEntity<Map<String, Object>>(badgeService.getRecentBadge(rootOrg, userId),
//				HttpStatus.CREATED);
//	}

	@GetMapping("/v3/users/{userId}/achievements/recent")
	public ResponseEntity<Map<String, Object>> getRecentBadge(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("userId") String userId,
			@RequestHeader(name = "langCode", required = false) List<String> language) throws Exception {
		return new ResponseEntity<Map<String, Object>>(badgeService.getRecentBadge(rootOrg, userId, language),
				HttpStatus.CREATED);
	}

	@GetMapping("/v1/badges")
	public ResponseEntity<List<BadgeDetailsProjection>> getAllBadgesForRootOrg(@RequestHeader("rootOrg") String rootOrg)
			throws Exception {
		return new ResponseEntity<>(badgeService.getAllBadgesForRootOrg(rootOrg), HttpStatus.CREATED);
	}

	@GetMapping("/v3/users/{userId}/badges")
	public ResponseEntity<Map<String, Object>> generateBadges(@RequestHeader String rootOrg,
			@PathVariable("userId") String userId,
			@RequestHeader(name = "langCode", required = false) List<String> language) throws Exception {
		System.out.println("rootOrg "+rootOrg);
		System.out.println("userId "+userId);

		return new ResponseEntity<Map<String, Object>>(badgeService.getAllBadges(rootOrg, userId, language),
				HttpStatus.CREATED);
	}
}
