/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.EvaluatorModel;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.ScromRequest;
import com.infosys.lexauthoringservices.service.ScromService;
import com.infosys.lexauthoringservices.serviceimpl.ContentEvaluationService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/action/scrom")
public class ScromController {

	@Autowired
	ScromService scromService;

	@PostMapping("/add")
	public ResponseEntity<Response> add(@Valid @RequestBody ScromRequest scromRequest, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		return new ResponseEntity<>(scromService.upsert(scromRequest, rootOrg, org), HttpStatus.OK);
	}

	@PostMapping("/fetch")
	public ResponseEntity<Response> fetch(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										  @RequestHeader String org) throws Exception {
		return new ResponseEntity<>(scromService.fetch(requestBody, rootOrg, org), HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> delete(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										   @RequestHeader String org) throws Exception {
		return new ResponseEntity<>(scromService.delete(requestBody, rootOrg, org), HttpStatus.OK);
	}
	
	
}
