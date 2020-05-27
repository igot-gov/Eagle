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
package com.infosys.lex.training.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface TrainingsService {

	/**
	 * GET training offerings for a course
	 * 
	 * @param contentId
	 * @param emailId
	 * @param startDate
	 * @param endDate
	 * @param location
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getTrainings(String contentId, String emailId, String startDate, String endDate,
			String location) throws Exception;

	/**
	 * GET sessions for a training offering
	 * 
	 * @param offeringId
	 * @return
	 */
	List<Map<String, Object>> getOfferingsSessions(String offeringId);

	/**
	 * POST register for an offering
	 * 
	 * @param offeringId
	 * @param userId
	 * @return
	 */
	Map<String, Object> registerForOffering(String offeringId, String userId);

	/**
	 * deregister from a training
	 * 
	 * @param offeringId
	 * @param userId
	 * @return
	 */
	Map<String, Object> deRegisterForOffering(String offeringId, String userId);

	/**
	 * Get offering count
	 * 
	 * @param request
	 * @return
	 * @throws JsonProcessingException
	 */
	Map<String, Object> getOfferingsCount(List<String> identifiers) throws JsonProcessingException;

	/**
	 * Add to watchlist
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> addContentToWatchList(String lexId, String userId);

	/**
	 * remove from watchlist
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> removeContentFromWatchList(String lexId, String userId);

	/**
	 * GET watchlist
	 * 
	 * @param userId
	 * @return
	 */
	List<String> getWatchListContent(String userId);

	/**
	 * Check if the user is JL6 or above
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> isJL6AndAbove(String userId);

	/**
	 * nominate users for a training
	 * 
	 * @param offeringId
	 * @return
	 */
	List<Map<String, Object>> nominateForOfferings(String offeringId, Map<String, Object> request);

	/**
	 * denominate users for a training
	 * 
	 * @param offeringId
	 * @return
	 */
	List<Map<String, Object>> denominateForOfferings(String offeringId, Map<String, Object> request);

	/**
	 * share an offering
	 * 
	 * @param request
	 * @return
	 */
	Map<String, Object> shareOffering(String offeringId, Map<String, Object> request);

	/**
	 * create a JIT request
	 * 
	 * @param request
	 * @return
	 */
	Map<String, Object> createJitRequest(Map<String, Object> request) throws JsonParseException, JsonMappingException, IOException ;

	/**
	 * Get list of all JIT requests created by me
	 * 
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> getJitRequestsCreatedByUser(String userId);

	/**
	 * Get list of offerings that a manager can reject
	 * 
	 * @param managerId
	 * @return
	 */
	List<Map<String, Object>> getOfferingsManagerCanReject(String managerId);

	/**
	 * Reject a registration
	 * 
	 * @param offeringId
	 * @param userId
	 * @return
	 */
	Map<String, Object> rejectOffering(String offeringId, String userId, Map<String, Object> request);

	/**
	 * Get questions for feedback
	 * 
	 * @param templateId
	 * @return
	 */
	List<Map<String, Object>> questionsForFeedback(String templateId);

	/**
	 * Submit Feedback
	 * 
	 * @param offeringId
	 * @param userId
	 * @return
	 */
	Map<String, Object> submitFeedback(String offeringId, String userId, String templateId,
			List<Map<String, Object>> request);

	/**
	 * Get Offerings For Feedback By User
	 * 
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> getOfferingsForFeedbackByUser(String userId);

	/**
substitute url based on requirement
	 * 
	 * @param req
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	Map<String, Object> mapLexidToCourseId(Map<String, Object> req)
			throws JsonParseException, JsonMappingException, IOException;

	/**
	 * gets educator detail for the given contentId
	 * 
	 * @param contentIds
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	List<String> getEducatorDetails(List<String> contentIds)
			throws JsonParseException, JsonMappingException, IOException;

	/**
	 * Get Training history for user
	 * 
	 * @param userId
	 * @param status
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	List<Map<String, Object>> getTrainingHistory(String userId, String status)
			throws JsonParseException, JsonMappingException, IOException;
}
