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
package com.infosys.lex.exercise.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.common.constants.JsonKey;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.exercise.dto.SubmitDataDTO;
import com.infosys.lex.exercise.service.SubmitService;


@RestController
@CrossOrigin(origins = "*")
public class DBMSController {
	
	@Autowired
	SubmitService submitServ;

	/**
	 * submits dbms exercises
	 * @param userId
	 * @param contentId
	 * @param requestBody
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/users/{user_id}/exercises/{content_id}/dbms-submission")
	public ResponseEntity<Map<String, Object>> submitDBMS(@PathVariable("user_id") String userId,
			@PathVariable("content_id") String contentId,
			@RequestHeader(name = "rootOrg",required = false,defaultValue = JsonKey.INFOSYS_ROOTORG ) String rootOrg,
			 @Valid @RequestBody SubmitDataDTO requestBody)
			throws Exception {
		Map<String, Object> resp = new HashMap<String, Object>();

		

		submitServ.dbmsSubmit(rootOrg,requestBody, true, userId, (String) contentId, "dbms");

		resp.put("response", "success");

		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.OK);
	}
}
