/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.leaderboard.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.leaderboard.service.LeaderBoardService;




@RestController
@CrossOrigin(origins = "*")
public class LeaderBoardController {

	
	@Autowired
	LeaderBoardService leaderBoardService;

	/**
	 * Fetches the top 10 user ranked by total points and  includes the given user
	 * 
	 * @param leaderboardType
	 * @param durationType
	 * @param userId
	 * @param rootOrg
	 * @param year
	 * @param durationValue
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/LeaderBoard")
	public ResponseEntity<Map<String,Object>> getLeaderBoard(
			@RequestParam(defaultValue = "L", required = false, name = "leaderboard_type") String leaderboardType,
			@RequestParam(defaultValue = "M", required = false, name = "duration_type") String durationType,
			@RequestParam("user_id") String userId,
			@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(required = true, name = "year") String year,
			@RequestParam(required = true, name = "duration_value") String durationValue) throws Exception {
		

		Map<String,Object> resp = leaderBoardService.getLeaderBoard(rootOrg,durationType, leaderboardType, userId, year, durationValue);
		
		return new ResponseEntity<Map<String,Object>>(resp,HttpStatus.OK);
		
	}
	/**
	 * Get top ranked user by duration_type(week/month) excluding current month 
	 * 
	 * @param leaderboard_type
	 * @param durationType
	 * @param rootOrg
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v2/TopLearners")
	public ResponseEntity<List<Map<String,Object>>> getPastTopLearners(@RequestParam("leaderboard_type") String leaderboard_type,
			@RequestParam("duration_type") String durationType,
			@RequestParam("user_id")String userId,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		
			List<Map<String,Object>> resp =  leaderBoardService.pastTopLearners(rootOrg,userId,durationType, leaderboard_type);
		
		return new ResponseEntity<>(resp,HttpStatus.OK);
	}
	
	
}
