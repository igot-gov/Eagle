/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.controller;

import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.serviceimpl.ContentEvaluationService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/action/content/evaluator")
public class ContentEvaluationController {

	@Autowired
	ContentEvaluationService contentEvaluationService;
	
	@PostMapping("/add")
	public ResponseEntity<Response> add(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg, String org)
			throws Exception {
		requestBody.put(LexConstants.ROOT_ORG, rootOrg);
		requestBody.put(LexConstants.ORG, org);
		return new ResponseEntity<>(contentEvaluationService.upsert(requestBody), HttpStatus.OK);
	}

//	@GetMapping("/workflow")
//	public ResponseEntity<Response> FetchFromContentWorkFlow(@RequestBody Map<String, Object> requestBody)
//			throws Exception {
//		return new ResponseEntity<>(contentWorkFlowService.fetchWorkFlowData(requestBody), HttpStatus.OK);
//	}
//
//	@DeleteMapping("/workflow")
//	public ResponseEntity<Response> removeFromContentWorkFlow(@RequestBody Map<String, Object> requestBody)
//			throws Exception {
//		return new ResponseEntity<>(contentWorkFlowService.removeFromWorkFlow(requestBody), HttpStatus.OK);
//	}
	
	
}
