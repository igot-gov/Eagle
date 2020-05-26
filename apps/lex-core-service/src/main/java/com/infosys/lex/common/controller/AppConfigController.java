/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.common.service.AppConfigService;

@RestController
@CrossOrigin(origins = "*")
public class AppConfigController {

	@Autowired
	private AppConfigService appConfigSvc;
	
	@GetMapping("/config")
	public ResponseEntity<?> getUserPreferences(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam("keys") List<String> keys) {

		return new ResponseEntity<>(appConfigSvc.getConfig(rootOrg, keys), HttpStatus.OK);
	}

}
