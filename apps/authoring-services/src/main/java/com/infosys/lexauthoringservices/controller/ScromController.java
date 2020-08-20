/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.controller;

import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.ScromData;
import com.infosys.lexauthoringservices.service.ScromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/action/scrom")
public class ScromController {

	@Autowired
	ScromService scromService;

	@PostMapping("/add")
	public ResponseEntity<Response> add(@Valid @RequestBody ScromData scromData, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		return new ResponseEntity<>(scromService.upsert(scromData, rootOrg, org), HttpStatus.CREATED);
	}

	@GetMapping("/fetch")
	public ResponseEntity<Response> fetch(@RequestHeader String rootOrg, @RequestHeader String org,
										  @RequestParam String contentId, @RequestParam String userId) throws Exception {
		return new ResponseEntity<>(scromService.fetch(rootOrg, org, contentId, userId), HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> delete(@RequestHeader String rootOrg, @RequestHeader String org,
										   @RequestParam String contentId, @RequestParam String userId) throws Exception {
		return new ResponseEntity<>(scromService.delete(rootOrg, org, contentId, userId), HttpStatus.OK);
	}
	
	
}
