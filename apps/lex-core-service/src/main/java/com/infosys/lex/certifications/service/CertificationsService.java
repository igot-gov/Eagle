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
package com.infosys.lex.certifications.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface CertificationsService {

	List<Map<String, Object>> getTestCenters(String certificationId)
			throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getCountries() throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getLocationsForCountry(String countryCode)
			throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getSlots() throws JsonParseException, JsonMappingException, IOException, ParseException;

	Map<String, Object> bookAtDeskCertificationSlot(String userId, String certificationId, Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getProctorApprovalRequests(String userId, String type)
			throws JsonParseException, JsonMappingException, IOException, ParseException;

	Map<String, Object> proctorApproval(String icfdId, Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException;


	Map<String, Object> budgetApprovalRequest(String userId, String certificationId, Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> cancelBudgetRequest(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getCurrencies() throws JsonParseException, JsonMappingException, IOException;


	Map<String, Object> resultVerificationApproval(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> budgetApproval(String userId, String certificationId, String sino, String ecdpid,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> getDatesAndSlotsForTestEnters(String location, String testCenter,String certificationId)
			throws JsonParseException, JsonMappingException, IOException, ParseException;

	List<Map<String, Object>> getCertificationCompleted(String userId, String status)
			throws JsonParseException, JsonMappingException, IOException, ParseException;

	Map<String, Object> bookSlotForOffshore(String userId, String certificationId, String slotNo)
			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> deleteCertificationDocForUserId(String userId, String certificationId, String documentName)
			throws JsonParseException, JsonMappingException, IOException;

	
	Map<String, Object> submitIAPUserCertficationDetails(Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> updateExternalCertificationResultStatus(Map<String, Object> requestMap, String userId,
			String certificationId, String action) throws JsonParseException, JsonMappingException, IOException, ParseException;

	List<Map<String, Object>> getUserCertificationSubmissions(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> getBookingInformation(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException, ParseException;
	
	
	Map<String, Object> getSubmittedDocument(String document) throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> submitCertificationDetails(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException;

	Map<String, Object> submitExtCertificationResult(String userId, String certificationId,
			Map<String, Object> requestMap) throws IOException, ParseException;
	
	Map<String, Object> uploadExtCertificationResult(String userId, String certificationId,
			Map<String, Object> requestMap) throws IOException, ParseException;

	Map<String, Object> cancelSlotBooking(String userId, String certificationId, String slotNo, String icfdId) throws JsonParseException, JsonMappingException, IOException;

	List<Map<String, Object>> getRequestsSentForApproval(String userId, String type, String startDate, String endDate)
			throws JsonParseException, JsonMappingException, IOException, ParseException;



	Map<String, Object> mapLexidToCertId(Map<String, Object> req)
			throws JsonParseException, JsonMappingException, IOException;




}
