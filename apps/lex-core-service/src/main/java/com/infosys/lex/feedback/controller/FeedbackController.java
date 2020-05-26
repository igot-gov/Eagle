/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.feedback.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.feedback.dto.FeedbackSearchDTO;
import com.infosys.lex.feedback.dto.FeedbackSubmitDTO;
import com.infosys.lex.feedback.service.FeedbackService;

@RestController
@CrossOrigin(origins = "*")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping("/v1/feedback/submit")
	public ResponseEntity<Map<String, Object>> submitFeedback(@Valid @RequestBody FeedbackSubmitDTO feedbackSubmitDTO,
			@RequestHeader("rootOrg") String rootOrg, @RequestParam(name = "role", required = true) String role)
			throws Exception {
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("response", feedbackService.submitFeedback(feedbackSubmitDTO, rootOrg, role));
		return new ResponseEntity<Map<String, Object>>(resp, HttpStatus.CREATED);
	}

	@GetMapping("/v1/feedback/{feedback_id}")
	public ResponseEntity<List<Map<String, Object>>> fetchFeedbacks(@PathVariable("feedback_id") String feedbackId,
			@RequestHeader("rootOrg") String rootOrg, @RequestParam(name = "user_Id", required = true) String userId)
			throws Exception {
		return new ResponseEntity<List<Map<String, Object>>>(
				feedbackService.fetchFeedbacks(feedbackId, userId, rootOrg), HttpStatus.OK);
	}

	@PostMapping("/v1/feedback/search")
	public ResponseEntity<Map<String, Object>> searchFeedbacks(@Valid @RequestBody FeedbackSearchDTO feedbackSearchDTO,
			@RequestHeader("rootOrg") String rootOrg) throws Exception {
		return new ResponseEntity<Map<String, Object>>(feedbackService.searchFeedback(feedbackSearchDTO, rootOrg),
				HttpStatus.OK);
	}

	@PatchMapping("/v1/users/{user_id}/feedback/{feedback_id}")
	public ResponseEntity<Map<String, Object>> updateStatus(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("feedback_id") String feedbackId,
			@RequestParam(name = "category", required = false, defaultValue = "un_defined") String category)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(
				feedbackService.updateStatus(userId, feedbackId, rootOrg, category), HttpStatus.OK);
	}

	@GetMapping("/v1/users/{user_id}/feedback-summary")
	public ResponseEntity<Map<String, Object>> fetchLatestFeedbacks(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId) throws Exception {
		return new ResponseEntity<Map<String, Object>>(feedbackService.fetchLatestFeedbacksReport(userId, rootOrg),
				HttpStatus.OK);
	}

	@GetMapping("/v1/feedback/config")
	public ResponseEntity<Map<String, Object>> fetchLatestFeedbacks(@RequestHeader("rootOrg") String rootOrg)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(feedbackService.getFeedbackCategory(rootOrg), HttpStatus.OK);
	}

	@GetMapping("/v1/feedback/dump")
	public ResponseEntity<Map<String, Object>> fetchLatestFeedbacks(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(name = "startDate", required = true) String startdate,
			@RequestParam(name = "endDate", required = true) String enddate,
			@RequestParam(name = "size", required = true) String size,
			@RequestParam(name = "scrollId", required = false, defaultValue = "none") String scrollId
			
			)
			throws Exception {
		return new ResponseEntity<Map<String, Object>>(feedbackService.feedbackDump(rootOrg,Long.parseLong(startdate),Long.parseLong(enddate),Integer.parseInt(size),scrollId),HttpStatus.OK);
	}

}
