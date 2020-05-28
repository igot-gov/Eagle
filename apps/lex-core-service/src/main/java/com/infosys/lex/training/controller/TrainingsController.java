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
package com.infosys.lex.training.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.training.service.TrainingsService;

@RestController
public class TrainingsController {

	@Autowired
	ServletContext servletContext;

	@Autowired
	TrainingsService trainingsService;

	@GetMapping("lHub/v1/content/{content_id}/trainings")
	public ResponseEntity<?> getTrainings(@PathVariable("content_id") String contentId,
			@RequestParam(required = true, name = "email") String emailId,
			@RequestParam(required = true, name = "start_dt") String startDate,
			@RequestParam(required = true, name = "end_dt") String endDate,
			@RequestParam(required = false, name = "location") String location) throws Exception {

		List<Map<String, Object>> responseMaps = trainingsService.getTrainings(contentId, emailId, startDate, endDate,
				location);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/offerings/{offering_id}/sessions")
	public ResponseEntity<?> getOfferingSessions(@PathVariable("offering_id") String offeringId) {

		List<Map<String, Object>> responseMaps = trainingsService.getOfferingsSessions(offeringId);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/offerings/{offering_id}/users/{user_id:.+}")
	public ResponseEntity<?> registerForOffering(@PathVariable("offering_id") String offeringId,
			@PathVariable("user_id") String userId) {

		Map<String, Object> responseMap = trainingsService.registerForOffering(offeringId, userId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@DeleteMapping("lHub/v1/offerings/{offering_id}/users/{user_id:.+}")
	public ResponseEntity<?> deRegisterForOffering(@PathVariable("offering_id") String offeringId,
			@PathVariable("user_id") String userId) {

		Map<String, Object> responseMap = trainingsService.deRegisterForOffering(offeringId, userId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("lHub/v1/offerings/count")
	public ResponseEntity<?> getOfferingsCount(@RequestBody Map<String, Object> request)
			throws JsonProcessingException {

		List<String> identifiers = (List<String>) request.get("identifiers");
		Map<String, Object> responseMap = trainingsService.getOfferingsCount(identifiers);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/content/{lex_id}/users/{user_id:.+}")
	public ResponseEntity<?> addContentToWatchList(@PathVariable("lex_id") String lexId,
			@PathVariable("user_id") String userId) {

		Map<String, Object> responseMap = trainingsService.addContentToWatchList(lexId, userId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@DeleteMapping("lHub/v1/content/{lex_id}/users/{user_id:.+}")
	public ResponseEntity<?> removeFromWatchList(@PathVariable("lex_id") String lexId,
			@PathVariable("user_id") String userId) {

		Map<String, Object> responseMap = trainingsService.removeContentFromWatchList(lexId, userId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/users/{user_id}/watchlist")
	public ResponseEntity<?> getWatchList(@PathVariable("user_id") String userId) {

		List<String> response = trainingsService.getWatchListContent(userId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/users/{user_id:.+}")
	public ResponseEntity<?> isJL6AndAbove(@PathVariable("user_id") String userId) {

		Map<String, Object> responseMap = trainingsService.isJL6AndAbove(userId);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/offerings/{offering_id}/users")
	public ResponseEntity<?> nominateForOfferings(@PathVariable("offering_id") String offeringId,
			@RequestBody Map<String, Object> request) {

		List<Map<String, Object>> responseMaps = trainingsService.nominateForOfferings(offeringId, request);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@DeleteMapping("lHub/v1/offerings/{offering_id}/users")
	public ResponseEntity<?> deNominateForOffering(@PathVariable("offering_id") String offeringId,
			@RequestBody Map<String, Object> request) {

		List<Map<String, Object>> responseMaps = trainingsService.denominateForOfferings(offeringId, request);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/offerings/{offering_id}/share")
	public ResponseEntity<?> shareOffering(@PathVariable("offering_id") String offeringId,
			@RequestBody Map<String, Object> request) {

		Map<String, Object> responseMap = trainingsService.shareOffering(offeringId, request);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/jit")
	public ResponseEntity<?> createJitRequest(@RequestBody Map<String, Object> request) throws JsonParseException, JsonMappingException, IOException{

		Map<String, Object> responseMap = trainingsService.createJitRequest(request);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/users/{user_id}/jit")
	public ResponseEntity<?> getJitRequestsCreatedByUser(@PathVariable("user_id") String userId) {

		List<Map<String, Object>> responseMaps = trainingsService.getJitRequestsCreatedByUser(userId);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/manager/{manager_id}/offerings")
	public ResponseEntity<?> getOfferingsManagerCanReject(@PathVariable("manager_id") String managerId) {

		List<Map<String, Object>> responseMaps = trainingsService.getOfferingsManagerCanReject(managerId);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@PatchMapping("lHub/v1/offerings/{offering_id}/users/{user_id:.+}")
	public ResponseEntity<?> rejectOffering(@PathVariable("offering_id") String offeringId,
			@PathVariable("user_id") String userId, @RequestBody Map<String, Object> request) {

		Map<String, Object> responseMap = trainingsService.rejectOffering(offeringId, userId, request);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/feedback/{template_id:.+}")
	public ResponseEntity<?> questionsForFeedback(@PathVariable("template_id") String templateId) {
		List<Map<String, Object>> responseMaps = trainingsService.questionsForFeedback(templateId);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@PostMapping("lHub/v1/offerings/{offering_id}/users/{user_id}/feedback")
	public ResponseEntity<?> submitFeedback(@PathVariable("offering_id") String offeringId,
			@PathVariable("user_id") String userId, @RequestParam(name = "template", required = true) String templateId,
			@RequestBody List<Map<String, Object>> request) {
		Map<String, Object> responseMaps = trainingsService.submitFeedback(offeringId, userId, templateId, request);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}

	@GetMapping("lHub/v1/users/{user_id}/offerings/feedback")
	public ResponseEntity<?> getOfferingsForFeedbackByUser(@PathVariable("user_id") String userId) {
		List<Map<String, Object>> responseMaps = trainingsService.getOfferingsForFeedbackByUser(userId);
		return new ResponseEntity<>(responseMaps, HttpStatus.OK);
	}
	
	/**
	 * This api is used to map the lex id to course id which is used by the learning Hub services
	 * 
	 * @param req
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping("lHub/map/lexid-courseid")
	public ResponseEntity<Map<String,Object>> mapLexIdToCourseId(@RequestBody Map<String,Object> req) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(trainingsService.mapLexidToCourseId(req),HttpStatus.OK);
	}
	
	/**
	 * It is used to fetch list of educators for contentid
	 * This api is used to test the lhub-api is working
	 * @param req
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping("lHub/educator")
	public ResponseEntity<List<String>> mapLexIdToCourseId(@RequestBody List<String> req) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(trainingsService.getEducatorDetails(req),HttpStatus.OK);
	}
	
	/**
	 * Gets the training history of user
	 * 
	 * 
	 * @param userId
	 * @param status (completed/notcompleted)
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GetMapping("lHub/v1/users/{user_id}/trainings")
	public ResponseEntity<List<Map<String,Object>>> getTrainingHistory(@PathVariable("user_id")String userId,
			@RequestParam(name = "status",required = false, defaultValue = "completed")String status) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(trainingsService.getTrainingHistory(userId, status),HttpStatus.OK);
	}

}
