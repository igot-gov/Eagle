/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"*/
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */
package com.infosys.lexauthoringservices.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.OrdinalsService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
@RequestMapping("/action/meta/v2")
public class OrdinalsController {
	
	@Autowired
	OrdinalsService ordinalsService;
	
	@GetMapping("/ordinals/list")
	public ResponseEntity<Map<String, Object>> getEnumsToBeDisplayed(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg) throws Exception {
		return new ResponseEntity<>(ordinalsService.getEnums(rootOrg), HttpStatus.OK);
	}
	
	@PostMapping("/upsert/ordinals")
	public ResponseEntity<String> upsertMasterValue(@RequestBody Map<String,Object> requestMap) throws Exception{
		return new ResponseEntity<>(ordinalsService.upsertMasterValue(requestMap),HttpStatus.OK);
	}
	
	@PostMapping("/action/entity")
	public ResponseEntity<String> addNewValueToEntity(@RequestBody Map<String,Object> requestMap) throws Exception{
		return new ResponseEntity<>(ordinalsService.updateValueToEntity(requestMap),HttpStatus.OK);
	}
}
