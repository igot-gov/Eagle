/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.externalcontent.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.externalcontent.service.ExternalContentService;

@RestController
@CrossOrigin(origins = "*")
public class ExternalContentController {

	@Autowired
	ExternalContentService externalService;

	@GetMapping("/v1/sources/{sourceid}/users/{userid}")
	public ResponseEntity<Map<String, Object>> getUser(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable("userid") String userId, @PathVariable("sourceid") String sourceId) throws Exception {
		return new ResponseEntity<Map<String, Object>>(externalService.getUser(rootOrg, sourceId, userId),
				HttpStatus.OK);

	}

}
