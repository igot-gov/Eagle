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
import com.infosys.lexauthoringservices.serviceimpl.ContentEvaluationService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/action/content/evaluator")
public class ContentEvaluationController {

	@Autowired
	ContentEvaluationService contentEvaluationService;

	@PostMapping("/add/v2")
	public ResponseEntity<Response> add(@RequestBody EvaluatorModel evaluatorModel, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		evaluatorModel.setRootOrg(rootOrg);
		evaluatorModel.setOrg(org);
		System.out.println("request : "+new ObjectMapper().writeValueAsString(evaluatorModel));
		return new ResponseEntity<>(contentEvaluationService.addV2(evaluatorModel), HttpStatus.OK);
	}

	@PostMapping("/fetch/v2")
	public ResponseEntity<Response> search(@RequestBody EvaluatorModel evaluatorModel, @RequestHeader String rootOrg,
												   @RequestHeader String org) throws Exception {
		evaluatorModel.setRootOrg(rootOrg);
		evaluatorModel.setOrg(org);
		System.out.println("request : "+new ObjectMapper().writeValueAsString(evaluatorModel));
		return new ResponseEntity<>(contentEvaluationService.searchV2(evaluatorModel), HttpStatus.OK);
	}

	@PostMapping("/add")
	public ResponseEntity<Response> add(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		requestBody.put(LexConstants.ROOT_ORG, rootOrg);
		requestBody.put(LexConstants.ORG, org);
		return new ResponseEntity<>(contentEvaluationService.upsert(requestBody), HttpStatus.OK);
	}

	@PostMapping("/fetchbyheader")
	public ResponseEntity<Response> get(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		requestBody.put(LexConstants.ROOT_ORG, rootOrg);
		requestBody.put(LexConstants.ORG, org);
		return new ResponseEntity<>(contentEvaluationService.fetch(requestBody), HttpStatus.OK);
	}

	@PostMapping("/fetch")
	public ResponseEntity<Response> getByContentId(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										@RequestHeader String org) throws Exception {
		requestBody.put(LexConstants.ROOT_ORG, rootOrg);
		requestBody.put(LexConstants.ORG, org);
		return new ResponseEntity<>(contentEvaluationService.fetchByContentId(requestBody), HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Response> delete(@RequestBody Map<String, Object> requestBody, @RequestHeader String rootOrg,
										   @RequestHeader String org) throws Exception {
		requestBody.put(LexConstants.ROOT_ORG, rootOrg);
		requestBody.put(LexConstants.ORG, org);
		return new ResponseEntity<>(contentEvaluationService.delete(requestBody), HttpStatus.OK);
	}
	
	
}
