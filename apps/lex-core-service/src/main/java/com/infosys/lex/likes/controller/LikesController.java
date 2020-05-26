/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.likes.controller;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.likes.service.LikesService;

@RestController
@CrossOrigin("*")
public class LikesController {
	@Autowired
	private LikesService likesService;

	/**
	 * Like a content id
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("v1/user/{user_id}/likes")
	public ResponseEntity<Map<String, Object>> upsertLikes(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @RequestParam("content_id") @NotNull @NotEmpty String contentId)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(likesService.upsertLikes(rootOrg, userId, contentId),
				HttpStatus.OK);
	}

	/**
	 * Get List of liked content ids
	 * 
	 * @param rootOrg
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("v1/user/{user_id}/likes")
	public ResponseEntity<List<String>> getLikesData(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") @NotNull @NotEmpty String userId) throws Exception {
		return new ResponseEntity<List<String>>(likesService.getLikes(rootOrg, userId), HttpStatus.OK);
	}

	/**
	 * Unlike a content id
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("v1/user/{user_id}/likes")
	public ResponseEntity<Map<String, Object>> deleteLikesData(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @RequestParam("content_id") String contentId) throws Exception {
		return new ResponseEntity<Map<String, Object>>(likesService.deleteLikesData(rootOrg, userId, contentId),
				HttpStatus.OK);
	}

	/**
	 * check if a content id is liked or not
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/user/{user_id}/likes/{content_id}")
	public ResponseEntity<Map<String, Object>> getLikesData(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("content_id") String contentId) throws Exception {
		return new ResponseEntity<Map<String, Object>>(likesService.getLikesData(rootOrg, userId, contentId),
				HttpStatus.OK);
	}

	/**
	 * Get total likes of a contentId
	 * 
	 * @param rootOrg
	 * @param contentList
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/likes-count")
	public ResponseEntity<Map<String, Object>> getTotalLikes(@RequestHeader(value = "rootOrg") String rootOrg,
			@RequestBody Map<String, Object> contentList) throws Exception {

		return new ResponseEntity<Map<String, Object>>(likesService.getTotalLikes(rootOrg, contentList), HttpStatus.OK);
	}
}
