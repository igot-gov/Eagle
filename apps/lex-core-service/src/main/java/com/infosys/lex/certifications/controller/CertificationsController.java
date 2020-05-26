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
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lex.certifications.service.CertificationsService;

@RestController
@CrossOrigin(origins = "*")
public class CertificationsController {

	@Autowired
	CertificationsService certificationService;

	/**
	 * Get Offshore ACC Centers for certification
	 * 
	 * @param certificationId
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GetMapping("lHub/certifications/{certification_id}/test-centers")
	public ResponseEntity<List<Map<String, Object>>> getTestCenters(@PathVariable("certification_id") String certificationId)
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<>(certificationService.getTestCenters( certificationId), HttpStatus.OK);
	}

	/**
	 * Get list of Countries for at desk certifications
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GetMapping("lHub/countries")
	public ResponseEntity<List<Map<String, Object>>> getCountries()
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<>(certificationService.getCountries(), HttpStatus.OK);
	}

	/**
	 * Get locations for a given country for at desk certification
	 * 
	 * @param countryCode
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GetMapping("lHub/countries/{country-code}/locations")
	public ResponseEntity<List<Map<String, Object>>> getLocationsForCountry(
			@PathVariable("country-code") String countryCode)
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<>(certificationService.getLocationsForCountry(countryCode), HttpStatus.OK);
	}

	/**
	 * get slots for at desk certification
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException 
	 */
	@GetMapping("lHub/slots")
	public ResponseEntity<List<Map<String, Object>>> getSlots()
			throws JsonParseException, JsonMappingException, IOException, ParseException {

		return new ResponseEntity<>(certificationService.getSlots(), HttpStatus.OK);
	}

	/**
	 * Create a slot booking request for at desk certification
	 * 
	 * @param userId
	 * @param certificationId
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@PostMapping("lHub/users/{user-id}/certifications/{certification-id}/atdesk-booking")
	public ResponseEntity<Map<String, Object>> bookCertificationSlot(@PathVariable("user-id") String userId,
			@PathVariable("certification-id") String certificationId, @RequestBody Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<>(certificationService.bookAtDeskCertificationSlot(userId, certificationId, requestMap),
				HttpStatus.OK);
	}

	/**
	 * Get items for action for proctors (onsite) there is no approval/rejection by
	 * manager for offshore
	 * 
	 * type=(proctor_approval||budget_approval||result_verification)
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException 
	 */
	@GetMapping("lHub/users/{user-id}/certification-approvals")
	public ResponseEntity<List<Map<String,Object>>> getProctorApprovalRequests(@PathVariable("user-id") String userId,
			@RequestParam(value = "type",required = false) String type) throws JsonParseException, JsonMappingException, IOException, ParseException {

		return new ResponseEntity<>(certificationService.getProctorApprovalRequests(userId, type), HttpStatus.OK);
	}

	/**
	 * Approve/Reject an at desk booking by Proctor
	 * 
	 * @param icfdId
	 * @param requestMap
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@PostMapping("lHub/certification-requests/{icfd_id}")
	public ResponseEntity<Map<String, Object>> proctorApproval(@PathVariable("icfd_id") String icfdId,
			@RequestBody Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<>(certificationService.proctorApproval(icfdId, requestMap), HttpStatus.OK);
	}

	/**
	 * Submit Budget approval request for External Certification
	 * 
	 * @param userId
	 * @param certificationId
	 * @param requestMap
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@PostMapping("lHub/users/{user-id}/certifications/{certification-id}/budget-request")
	public ResponseEntity<Map<String, Object>> budgetApprovalRequest(@PathVariable("user-id") String userId,
			@PathVariable("certification-id") String certificationId, @RequestBody Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<Map<String, Object>>(
				certificationService.budgetApprovalRequest(userId, certificationId, requestMap), HttpStatus.OK);
	}

	/**
	 * Budget approval request cancellation for External Certification before it is
	 * approved
	 * 
	 * @param userId
	 * @param certificationId
	 * @param requestMap
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@DeleteMapping("lHub/users/{user-id}/certifications/{certification-id}/budget-request")
	public ResponseEntity<Map<String, Object>> cancelBudgetRequest(@PathVariable("user-id") String userId,
			@PathVariable("certification-id") String certificationId)
			throws JsonParseException, JsonMappingException, IOException {

		return new ResponseEntity<Map<String, Object>>(
				certificationService.cancelBudgetRequest(userId, certificationId), HttpStatus.OK);
	}

	/**
	 * get list of currencies
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@GetMapping("lHub/currencies")
	public ResponseEntity<List<Map<String, Object>>> getCurrencies()
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<List<Map<String, Object>>>(certificationService.getCurrencies(), HttpStatus.OK);
	}

	/**
	 * Get status of requests raised by user
	 * 
	 * type=(proctor_approval||budget_approval||result_verification)
	 * 
	 * @path variable userId
	 * @param type
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 */
	@GetMapping("lHub/users/{user-id}/certifications/certification-requests")
	public ResponseEntity<List<Map<String,Object>>> getRequestsSentForApproval(@PathVariable("user-id") String userId,
			@RequestParam(value = "type",required =false) String type,@RequestParam("start_date")String startDate,@RequestParam("end_date")String endDate) throws JsonParseException, JsonMappingException, IOException, ParseException {

		return new ResponseEntity<>(certificationService.getRequestsSentForApproval(userId, type,startDate,endDate),
				HttpStatus.OK);

	}

	/**
	 * Approve/Reject a budget approval request
	 * 
	 * @param user_id
	 * @param certification_id
	 * @RequestParam sino
	 * @RequestParam ecdp_id
	 * @param requestMap
 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@PostMapping("lHub/users/{user-id}/certifications/{certification-id}/budget-request-approval")
	public ResponseEntity<Map<String, Object>> budgetRequestApproval(@PathVariable("user-id") String userId,
			@PathVariable("certification-id") String certificationId, @RequestParam("sino") String sino,
			@RequestParam("ecdp_id") String ecdpId, @RequestBody Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.budgetApproval(userId, certificationId, sino, ecdpId, requestMap), HttpStatus.OK);
	}

	/**
	 * Approve/Reject result-verification request
	 * 
	 * @param user_id
	 * @param certification_id
	 * @param                  requestMap(@param status,@param reason,@param
	 *                         user,@param upload_id)
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@PostMapping("lHub/users/{user-id}/certifications/{certification-id}/result-verification-requests")
	public ResponseEntity<Map<String, Object>> resultVerificationRequestApproval(@PathVariable("user-id") String userId,
			@PathVariable("certification-id") String certificationId, @RequestBody Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.resultVerificationApproval(userId, certificationId, requestMap), HttpStatus.OK);
	}

	/**
	 * Get dates and available slots for test centers
	 * 
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 **/

	@GetMapping("lHub/certifications/{certification_id}/locations/{location}/test-centers/{test-center}/slots")
	public ResponseEntity<Map<String, Object>> getSlotsForDcAndTestCenter(@PathVariable("location") String location,
			@PathVariable("test-center") String testCenter,@PathVariable("certification_id")String certificationId)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.getDatesAndSlotsForTestEnters(location, testCenter,certificationId), HttpStatus.OK);
	}

	/**
	 * Book a slot/Update a slot for acc certification
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@PostMapping("lHub/users/{user_id}/certifications/{certification_id}/booking/{slot_no}")
	public ResponseEntity<Map<String, Object>> bookSlotForAccCertification(@PathVariable("user_id") String userId,
			@PathVariable("certification_id") String certificationId, @PathVariable("slot_no") String slotNo)
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.bookSlotForOffshore(userId, certificationId, slotNo), HttpStatus.OK);
	}

	/**
	 * Cancel a booking for acc as well as atdesk certification
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@DeleteMapping("lHub/users/{user_id}/certifications/{certification_id}/slots/{slot_no}")
	public ResponseEntity<Map<String, Object>> cancelBookingForAtDeskCertification(
			@PathVariable("user_id") String userId, @PathVariable("certification_id") String certificationId,
			@PathVariable("slot_no")String slotNo,@RequestParam(value = "icfd_id",required = false)String icfdId) throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.cancelSlotBooking(userId, certificationId, slotNo,icfdId), HttpStatus.OK);
	}

	/**
	 * withdraw/submit external External Certification
	 * 
	 * @param user_id
	 * @param certification_id
	 * @param action
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 */

	@PatchMapping("lHub/users/{user_id}/certifications/{certification_id}/result")
	public ResponseEntity<Map<String, Object>> updateResultStatus(@PathVariable("user_id") String userId,
			@PathVariable("certification_id") String certificationId, @RequestParam("action") String action,
			@RequestBody Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return new ResponseEntity<Map<String, Object>>(certificationService
				.updateExternalCertificationResultStatus(requestMap, userId, certificationId, action), HttpStatus.OK);
	}

	/**
	 * Delete document for External Certification
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */

	@DeleteMapping("lHub/users/{user_id}/certifications/{certification_id}/document")
	public ResponseEntity<Map<String, Object>> deleteExtCertificationDoc(@PathVariable("user_id") String userId,
			@PathVariable("certification_id") String certificationId,@RequestParam("filename")String documentName)
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<Map<String, Object>>(
				certificationService.deleteCertificationDocForUserId(userId, certificationId, documentName),
				HttpStatus.OK);
	}

	/**
	 * Get Certification completed for users. This is to show Certification history on history
	 * page.
	 * 
	 * @param user_id
	 * @param status
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException 
	 */

	@GetMapping("lHub/users/{user_id}/certifications")
	public ResponseEntity<List<Map<String, Object>>> getCompletedCertificationsByUser(@PathVariable("user_id") String userId,
			@RequestParam("status") String status) throws JsonParseException, JsonMappingException, IOException, ParseException {
		return new ResponseEntity<>(certificationService.getCertificationCompleted(userId, status),HttpStatus.OK);
	}
	
	
	/**
	 * Get details of all IAP certification submissions by a user
	 * 
	 * @param user_id
	 * @param certifiation_id
	 * 
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	
	@GetMapping("lHub/users/{user_id}/certifications/{certification_id}/submissions")
	public ResponseEntity<List<Map<String,Object>>> getCertificationSubmissions(@PathVariable("user_id")String userId,
			@PathVariable("certification_id")String certificationId) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(certificationService.getUserCertificationSubmissions(userId, certificationId),HttpStatus.OK);
	}

	
	/**
	 * Submit IAP certification details for users
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * 
	 */
	
	@PostMapping("lHub/users/{user_id}/certifications/{certification_id}/submissions")
	public ResponseEntity<Map<String,Object>> submitIAPCertificationDetails(@PathVariable("user_id") String userId,
	@PathVariable("certification_id")String certificationId,@RequestBody Map<String,Object> requestMap) throws JsonParseException, JsonMappingException, IOException 
	{
			return new ResponseEntity<Map<String,Object>>(certificationService.submitCertificationDetails(userId, certificationId, requestMap),HttpStatus.OK);
	}
	
	
	
	/**
	 * Get eligibility and booking information for a user for a given certification
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws ParseException 
	 * 
	 */
	@GetMapping("lHub/users/{user_id}/certifications/{certification_id}/booking-information")
	public ResponseEntity<Map<String,Object>> getBookingInformation(@PathVariable("user_id")String userId,
			@PathVariable("certification_id")String certificationId) throws JsonParseException, JsonMappingException, IOException, ParseException
	{
		return new ResponseEntity<>(certificationService.getBookingInformation(userId, certificationId),HttpStatus.OK);
	}
	
	
	/**
	 * Get API for documents submitted
	 *  
	 * @param document
	 *  
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 *  
	 */
	@GetMapping("lHub/submitted-document")
	public ResponseEntity<Map<String,Object>> getDocumentSubmitted(@RequestParam("document") String document) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(certificationService.getSubmittedDocument(document),HttpStatus.OK);
	}
	
	/**
	 * Uploads result of External Certifications to Proctor
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * 
	 * (This api is not used)
	 */

	
	@PostMapping("lHub/users/{user_id}/certifications/{certification_id}/save-result")
	public ResponseEntity<Map<String,Object>> uploadCertificationResult(@PathVariable("user_id")String userId,
			@PathVariable("certification_id")String certificationId,@RequestBody Map<String,Object> requestMap) throws  IOException, ParseException
	{
		return new ResponseEntity<>(certificationService.uploadExtCertificationResult(userId, certificationId, requestMap),HttpStatus.OK);
	}
	
	/**
	 * Submits result of External Certifications to Proctor
	 * 
	 * @param user_id
	 * @param certification_id
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 * 
	 * 
	 */
	@PostMapping("lHub/users/{user_id}/certifications/{certification_id}/result")
	public ResponseEntity<Map<String,Object>> submitCertificationResult(@PathVariable("user_id")String userId,
			@PathVariable("certification_id")String certificationId,@RequestBody Map<String,Object> requestMap) throws  IOException, ParseException
	{
		return new ResponseEntity<>(certificationService.submitExtCertificationResult(userId, certificationId, requestMap),HttpStatus.OK);
	}

	/**
substitute url based on requirement
	 * 
	 * @param req
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping("lHub/map/lexid-certificationid")
	public ResponseEntity<Map<String,Object>> mapLexIdToCertId(@RequestBody Map<String,Object> req) throws JsonParseException, JsonMappingException, IOException
	{
		return new ResponseEntity<>(certificationService.mapLexidToCertId(req),HttpStatus.OK);
	}
	
	

	
}

