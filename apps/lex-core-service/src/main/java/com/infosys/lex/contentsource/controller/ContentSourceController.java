/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.contentsource.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.contentsource.dto.ContentSourceNameListDto;
import com.infosys.lex.contentsource.dto.ContentSourceUserDetailDto;
import com.infosys.lex.contentsource.service.ContentSourceService;

@RestController
@CrossOrigin(origins = "*")
public class ContentSourceController {

	@Autowired
	ContentSourceService contentSourceService;

	/**
	 * This checks whether the user has registered in the given content-source
	 * (external/internal) the ui sends the source short name
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param sourceId(source short name is passed)
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/content-sources/{source}/users/{userid}")
	public ResponseEntity<Map<String, Object>> getUser(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("userid") String userId, @PathVariable("source") String sourceShortName) throws Exception {
		return new ResponseEntity<Map<String, Object>>(contentSourceService.getUser(rootOrg, sourceShortName, userId),
				HttpStatus.OK);

	}

	/**
	 * Fetches all the sources for the given rootOrg based on user registration
	 * required
	 * 
	 * @param rootOrg
	 * @return
	 */
	@GetMapping("/v1/content-sources")
	public ResponseEntity<?> fetchAllContentSources(@RequestHeader(value = "rootOrg") String rootOrg,
			@RequestParam(name = "registrationProvided", required = false) Boolean registrationProvided) {
		return new ResponseEntity<>(
				contentSourceService.fetchAllContentsourcesForRootOrg(rootOrg, registrationProvided), HttpStatus.OK);
	}

	/**
	 * fetch content source detail for rootOrg
	 * 
	 * @param rootOrg
	 * @param sourceName(source short name is passed)
	 * @return
	 */
	@GetMapping("/v1/content-sources/{source}")
	public ResponseEntity<?> fetchContentSourceDetails(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("source") String source,
			@RequestParam(name = "registrationProvided", required = false) Boolean registrationProvided) {
		return new ResponseEntity<>(
				contentSourceService.fetchContentsourceDetails(rootOrg, source, registrationProvided), HttpStatus.OK);
	}

	/**
	 * registers the given users for the contentSource
	 * 
	 * @param rootOrg
	 * @param contentSource(source short name is passed)
	 * @param usersDetailList
	 * @return
	 */
	@PostMapping("/v1/content-sources/{source}/users")
	public ResponseEntity<?> registerUserForContentSource(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("source") String sourceShortName,
			@RequestBody @Valid List<ContentSourceUserDetailDto> contentSourceUserDetails) {
		return new ResponseEntity<>(
				contentSourceService.registerContentSourceForUsers(rootOrg, sourceShortName, contentSourceUserDetails),
				HttpStatus.OK);

	}

	/**
	 * fetch all registered user for given contentSource
	 * 
	 * @param rootOrg
	 * @param sourceShortName
	 * @return
	 */
	@GetMapping("/v1/content-sources/{source}/users")
	public ResponseEntity<?> fetchRegsiteredUsersForContentSource(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("source") String sourceShortName) {
		return new ResponseEntity<>(contentSourceService.getRegisteredUsers(rootOrg, sourceShortName), HttpStatus.OK);
	}

	/**
	 * deregister user for content-source
	 * @param rootOrg
	 * @param sourceShortName
	 * @param registeredUsers
	 * @return
	 * @throws Exception 
	 */
	@PostMapping("v1/content-sources/{source}/deregistered-users")
	public ResponseEntity<?> deleteRegisteredUserForContentSource(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("source") String sourceShortName, @RequestBody List<String> registeredUsers) throws Exception {
		contentSourceService.deRegisterUser(rootOrg, sourceShortName, registeredUsers);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * fetch content source detail for rootOrg for sourceName
	 * 
	 * @param rootOrg
	 * @param sourceName(sourcename is passed)
	 * @return
	 */
	@PostMapping("/v1/content-sources/source-short-names")
	public ResponseEntity<?> fetchContentSourceDetailsForSourceName(@RequestHeader(value = "rootOrg") String rootOrg,
			@RequestBody @Valid ContentSourceNameListDto req) {
		return new ResponseEntity<>(
				contentSourceService.fetchContentsourceDetailsForSourceName(rootOrg, req), HttpStatus.OK);
	}

}
