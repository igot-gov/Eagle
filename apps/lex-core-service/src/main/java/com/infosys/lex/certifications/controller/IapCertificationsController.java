/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.certifications.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.certifications.service.IapCertificationsService;
import com.infosys.lex.common.util.LexServerProperties;

@RestController
public class IapCertificationsController {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	IapCertificationsService certificationService;

	@GetMapping("v1/InfyTQ/Attempts")
	public ResponseEntity<Map<String, Object>> getAttempts(@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "lexResourceId", required = true) String lexResourceId)
			throws JsonParseException, JsonMappingException, IOException {

		Map<String, Object> responseMap = certificationService.getAttempts(userEmail, lexResourceId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
}
