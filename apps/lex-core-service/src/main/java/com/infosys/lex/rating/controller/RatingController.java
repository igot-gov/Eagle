/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.rating.dto.ContentIdsDto;
import com.infosys.lex.rating.dto.UserContentRatingDTO;
import com.infosys.lex.rating.service.RatingService;

@RestController
@CrossOrigin(origins = "*")
public class RatingController {

	@Autowired
	RatingService ratingServ;

	/**
	 * Fetches rating given by the user for the resourceid and rootorg and null if
	 * doesn't Exists
	 * 
	 * @param rootOrg
	 * @param resourceId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("v1/contents/{content_id}/users/{user_id}/ratings")
	public ResponseEntity<Map<String, Object>> getUserRatings(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("content_id") String contentId, @PathVariable("user_id") String userId) throws Exception {
		return new ResponseEntity<>(ratingServ.getUserRatings(rootOrg, contentId, userId), HttpStatus.OK);
	}

	/**
	 * Updates rating given by the user for the resourceid and rootorg
	 * 
	 * @param rootOrg
	 * @param resourceId
	 * @param userId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@PostMapping("v1/contents/{content_id}/users/{user_id}/ratings")
	public ResponseEntity<Map<String, Object>> updateUserRatings(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("content_id") String contentId, @PathVariable("user_id") String userId,
			@RequestBody @Valid UserContentRatingDTO req) throws Exception {
		return new ResponseEntity<>(ratingServ.updateUserRating(rootOrg, contentId, userId, req), HttpStatus.OK);
	}

	/**
	 * Fetches avg rating for the resource from the rootOrg
	 * 
	 * @param rootOrg
	 * @param resourceId
	 * @return
	 * @throws Exception
	 */

	@GetMapping("v1/root-org/{root_org}/contents/{content_id}/avg-ratings")
	public ResponseEntity<Map<String, Object>> getAvgRatingForResource(@PathVariable("root_org") String rootOrg,
			@PathVariable("content_id") String contentId) throws Exception {
		return new ResponseEntity<>(ratingServ.getAvgUserRatingAndCountForResource(rootOrg, contentId), HttpStatus.OK);
	}

	/**
	 * Deletes rating given by the user for the contentid and rootorg
	 * 
	 * @param rootOrg
	 * @param resourceId
	 * @param userId
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("v1/contents/{content_id}/users/{user_id}/ratings")
	public ResponseEntity<?> deleteUserRatings(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("content_id") String contentId, @PathVariable("user_id") String userId) throws Exception {
		ratingServ.deleteUserRating(rootOrg, contentId, userId);
		return new ResponseEntity<>(ratingServ.deleteUserRating(rootOrg, contentId, userId),HttpStatus.OK);
	}
	
	/**
	 * This method returns the rating info(average rating and total rating count) for the
	 * content Ids passed.
	 * 
	 * Only those content ids rating  will be returned for which rating
	 * exists
	 * 
	 * (Info)Can't fetch for all contents as that may require using allow filtering. 
	 * 
	 */
	@PostMapping("v1/contents/average-rating")
	public ResponseEntity<?> getContentRatings(@RequestHeader("rootOrg") String rootOrg,@Valid @RequestBody ContentIdsDto req)
	{
		return new ResponseEntity<>(ratingServ.getRatingsInfoForContents(rootOrg, req),HttpStatus.OK);
	}

}
