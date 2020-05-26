/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.assessment.controller;

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

import com.infosys.lex.assessment.service.AssessmentPreviewService;
import com.infosys.lex.assessment.dto.AssessmentSubmissionDTO;

@RestController
@CrossOrigin(origins = "*")
public class AssessmentPreviewController {

	@Autowired
	AssessmentPreviewService assessmentPrevierService;

	@PostMapping("/v1/users/{userId}/assessment/verify-preview")
	public ResponseEntity<Map<String, Object>> submitAssessmentByCassandra(
			@Valid @RequestBody AssessmentSubmissionDTO requestBody, @PathVariable("userId") String userId,
			@RequestHeader(name = "rootOrg",required=false,defaultValue = "Infosys Ltd") String rootOrg, @RequestHeader(name = "org",required=false,defaultValue = "Infosys") String org) throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				assessmentPrevierService.getAssessmentVerifyPreview(rootOrg, org, userId, requestBody), HttpStatus.OK);
	}
}
