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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.bodhi.repo.ApiAuthenticationModel;
import com.infosys.lex.common.bodhi.repo.AuthenticationRepository;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;

@Service
public class CertificationsServiceImpl implements CertificationsService {

	@Autowired
	ServletContext servletContext;

	@Autowired
	RestTemplate restTemplateForCertification;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	ContentService contentService;

	@Autowired
	AuthenticationRepository authRepo;

	@Autowired
	LexServerProperties serverProp;

	private static SimpleDateFormat istDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat istTimeStampFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static SimpleDateFormat dateDisplayFormat = new SimpleDateFormat("dd MMM YYYY");
	private static Map<String, Object> reasonMap = new HashMap<String, Object>();

	static {

		istDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		istTimeStampFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		dateDisplayFormat.setTimeZone(TimeZone.getTimeZone("IST"));

		reasonMap.put("-1", "Certification not enabled for registration by ACC Team");
		reasonMap.put("-2", "Certification not applicable for your job level");
		reasonMap.put("-3", "Certification not applicable for your PU");
		reasonMap.put("-4", "Certification not enabled for you");
		reasonMap.put("-5", "Certification not applicable for your role code");
		reasonMap.put("-6", "You have given the certification within the last 15 days");

	}

	@Override
	public List<Map<String, Object>> getTestCenters(String certificationId)
			throws JsonParseException, JsonMappingException, IOException {

		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetOffshoreACCCenters?certification_id=" + certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		return responseMaps;
	}

	@Override
	public List<Map<String, Object>> getCountries() throws JsonParseException, JsonMappingException, IOException {

		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();
		String url = apiEndPointPrefix + "/GetlistofCountriesforonsite";

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		return responseMaps;

	}

	@Override
	public List<Map<String, Object>> getLocationsForCountry(String countryCode)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/Getlocationsforcountry?country_code=" + countryCode;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		return responseMaps;

	}

	@Override
	public List<Map<String, Object>> getSlots()
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/Getonsiteslots";

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		String timeZone = "IST";

		for (Map<String, Object> slot : responseMaps) {
			if (slot.containsKey("time_zone"))
				timeZone = slot.get("time_zone").toString();
			if (slot.containsKey("date")) {
				slot.put("date", this.createDateMapFromDateString(slot.get("date").toString(), timeZone));

			}
		}

		return responseMaps;

	}

	@Override
	public Map<String, Object> bookAtDeskCertificationSlot(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/Createslotbookingrequestforonsite?user_id=" + userId + "&certification_id="
				+ certificationId;

		@SuppressWarnings("unchecked")
		String date = this.createDateStringFromDateMap((Map<String, Object>) requestMap.get("date"));

		if (requestMap.containsKey("date")) {
			requestMap.put("date", date);
		}
		
		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getProctorApprovalRequests(String userId, String type)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		List<Map<String, Object>> respList = null;
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetActionItems?user_id=" + userId;
		if (type != null)
			url += "&type=" + type;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		if (type == null) {

			Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

			respList = (List<Map<String, Object>>) responseMap.get("proctorInboxDetails");
			respList.addAll((List<Map<String, Object>>) responseMap.get("approverInboxDetails"));
			respList.addAll((List<Map<String, Object>>) responseMap.get("verifierInboxDetails"));

		} else {
			respList = new ObjectMapper().readValue(responseEntity.getBody(), new TypeReference<List<Object>>() {
			});
		}

		String timeZone = "IST";
		List<String> contentList = new ArrayList<String>();
		for (Map<String, Object> approvals : respList) {
			if (approvals.get("record_type").toString().equalsIgnoreCase("result_verification")) {
				contentList.add(approvals.get("certification").toString());
				approvals.put("sampleCertificationUrls", new ArrayList<String>());
			}
			if (approvals.containsKey("time_zone"))
				timeZone = approvals.get("time_zone").toString();
			if (approvals.containsKey("exam_date")) {
				approvals.put("exam_date",
						this.createDateMapFromDateString(approvals.get("exam_date").toString(), timeZone));
			} else if (approvals.containsKey("date")) {
				approvals.put("date", this.createDateMapFromDateString(approvals.get("date").toString(), timeZone));
			}

		}

		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSourceIfSourceFieldsExists(contentList,
				new String[] { "identifier", "sampleCertificates" }, new String[] { "sampleCertificates" }, null);

		for (Map<String, Object> contentMeta : searchHits) {
			String contentId = contentMeta.get("identifier").toString();
			if (contentMeta.containsKey("sampleCertificates") && contentMeta.get("sampleCertificates") != null
					&& ((List<String>) contentMeta.get("sampleCertificates")).size() != 0) {
				List<String> sampleCertificationUrls = (List<String>) contentMeta.get("sampleCertificates");
				for (Map<String, Object> approvals : respList) {
					if (approvals.get("record_type").toString().equalsIgnoreCase("result_verification") && approvals.get("certification").toString().equals(contentId)) {
						approvals.put("sampleCertificationUrls", sampleCertificationUrls);
						break;
					}

				}
			}
		}
		return respList;
	}

	@Override
	public Map<String, Object> proctorApproval(String icfdId, Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {

		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/ApproveorRejectonsitebooking?icfd_id=" + icfdId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> budgetApproval(String userId, String certificationId, String sino, String ecdpId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {

		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/ApproveorRejectbudgetapprovalrequest?user_id=" + userId
				+ "&certification_id=" + certificationId + "&sino=" + sino + "&ecdp_id=" + ecdpId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> budgetApprovalRequest(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/SubmitBudgetApprovalRequest?user_id=" + userId + "&certification_id="
				+ certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;

	}

	@Override
	public Map<String, Object> cancelBudgetRequest(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/CancelBudgetApprovalRequest?user_id=" + userId + "&certification_id="
				+ certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public List<Map<String, Object>> getCurrencies() throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetlistofCurrencies";

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		return responseMaps;
	}

	/*
	 * 
	 * Here response is changed
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getRequestsSentForApproval(String userId, String type, String startDate,
			String endDate) throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		startDate = istDateFormat.format(new Date(Long.valueOf(startDate)));
		endDate = istDateFormat.format(new Date(Long.valueOf(endDate)));
		List<Map<String, Object>> respList = null;
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();
		String url = apiEndPointPrefix + "/GetUserRequests?user_id=" + userId + "&start_date=" + startDate
				+ "&end_date=" + endDate;

		if (type != null) {
			url += "&type=" + type;
		}

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);
		if (type == null) {
			Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

			respList = (List<Map<String, Object>>) responseMap.get("withProctor");
			respList.addAll((List<Map<String, Object>>) responseMap.get("withApprover"));
			respList.addAll((List<Map<String, Object>>) responseMap.get("withVerifier"));
		} else {
			respList = new ObjectMapper().readValue(responseEntity.getBody(), new TypeReference<List<Object>>() {
			});
		}

		String timeZone = "IST";
		for (Map<String, Object> respMap : respList) {
			if (respMap.containsKey("time_zone"))
				timeZone = respMap.get("time_zone").toString();
			if (respMap.containsKey("date"))
				respMap.put("date", this.createDateMapFromDateString(respMap.get("date").toString(), timeZone));
			else if (respMap.containsKey("exam_date"))
				respMap.put("exam_date",
						this.createDateMapFromDateString(respMap.get("exam_date").toString(), timeZone));

			if (respMap.containsKey("raised_on"))
				respMap.put("raised_on",
						this.createDateMapFromDateString(respMap.get("raised_on").toString(), timeZone));
		}

		return respList;
	}

	@Override
	public Map<String, Object> getDatesAndSlotsForTestEnters(String location, String testCenter, String certificationId)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetDatesandAvailableSlotsForOffshore?dc=" + location + "&testcenter="
				+ testCenter + "&certification_id=" + certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		if (responseMap.containsKey("slotdata") && responseMap.get("slotdata") != null) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> slotDataList = (List<Map<String, Object>>) responseMap.get("slotdata");
			String timeZone = "IST";
			for (Map<String, Object> slotData : slotDataList) {
				if (slotData.containsKey("time_zone"))
					timeZone = slotData.get("time_zone").toString();
				if (slotData.containsKey("date"))
					slotData.put("date",
							this.createDateMapFromTimeStampString(slotData.get("date").toString(), timeZone));
			}
		}

		return responseMap;
	}

	@Override
	public List<Map<String, Object>> getCertificationCompleted(String userId, String status)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetCompletedCertifications?user_id=" + userId + "&status=" + status;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseList = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String, Object>>>() {
				});

		for (Map<String, Object> certMap : responseList) {
			String timeZone = "IST";
			if (certMap.containsKey("time_zone"))
				timeZone = certMap.get("time_zone").toString();

			if (certMap.containsKey("examDate") && certMap.get("examDate") != null)
				certMap.put("examDate", this.createDateMapFromDateString(certMap.get("examDate").toString(), timeZone));
		}

		return responseList;
	}

	@Override
	public Map<String, Object> bookSlotForOffshore(String userId, String certificationId, String slotNo)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/BookSlotForOffshore?user_id=" + userId + "&certification_id="
				+ certificationId + "&slot_no=" + slotNo;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> deleteCertificationDocForUserId(String userId, String certificationId,
			String documentName) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/DeleteExternalResultFile?user_id=" + userId + "&certification_id="
				+ certificationId + "&document=" + documentName;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> cancelSlotBooking(String userId, String certificationId, String slotNo, String icfdId)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/CancelSlotBooking?user_id=" + userId + "&certification_id=" + certificationId
				+ "&slot_no=" + slotNo;

		if (icfdId != null) {
			url += "&icfd_id=" + icfdId;
		}

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> updateExternalCertificationResultStatus(Map<String, Object> requestMap, String userId,
			String certificationId, String action)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/SubmitRecallExternalCertResult?user_id=" + userId + "&certification_id="
				+ certificationId + "&action=" + action;
		if (requestMap.containsKey("exam_date")) {
			requestMap.put("exam_date",
					istDateFormat.format(new Date(Long.valueOf(requestMap.get("exam_date").toString()))));
		}

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> submitIAPUserCertficationDetails(Map<String, Object> requestMap)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/ResultSubmissions";

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> resultVerificationApproval(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();
		String user = requestMap.get("user").toString();
		String uploadId = requestMap.get("upload_id").toString();

		String url = apiEndPointPrefix + "/ApproveorRejectresultverificationrequest?user_id=" + userId
				+ "&certification_id=" + certificationId + "&user=" + user + "&upload_id=" + uploadId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public List<Map<String, Object>> getUserCertificationSubmissions(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetSubmittedCertificationStatus?user_id=" + userId + "&certification_id="
				+ certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		List<Map<String, Object>> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Object>>() {
				});

		return responseMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getBookingInformation(String userId, String certificationId)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		int noOfDaysGap = serverProp.getCertificationRetryGapInDays();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetBookingInformation?user_id=" + userId + "&certification_id="
				+ certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		String timeZone = "IST";

		// this is temp
		if (responseMap.containsKey("time_zone"))
			timeZone = responseMap.get("time_zone").toString();

		Map<String, Object> eligibility = (Map<String, Object>) responseMap.get("eligibility");
		// reasonObj a map will replace the reason field in the response
		Map<String, Object> reasonObj = new HashMap<String, Object>();

		// This key indicates the next date the user can apply for the certificate and
		// will be null
		// null in all cases except when the user has given the certification within the
		// last 15 days
		reasonObj.put("nextRegisterDate", null);

		String lastTakenDate = null;
		// Here we check whether the lasttakendate is within 15 days
		if (responseMap.containsKey("lastTakenDate") && responseMap.get("lastTakenDate") != null
				&& !responseMap.get("lastTakenDate").toString().equals("NA")) {

			lastTakenDate = responseMap.get("lastTakenDate").toString();

			responseMap.put("lastTakenDate", this.createDateMapFromTimeStampString(lastTakenDate, timeZone));
		}
		// compare last takendate and date for the certification for online(IAP) the
		// lastest one
		// should be less than 15 days
		long currentDateInMillis = getMillisForDatePart(new Date());
		boolean isLessThanOrEqualDaysGap = false;
		SimpleDateFormat timeZoneSpecificDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		timeZoneSpecificDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

		long lastTakenDateInMillis = 0;
		if (lastTakenDate != null) {
			lastTakenDateInMillis = timeZoneSpecificDateFormat.parse(lastTakenDate).getTime();
			long diffInMillies = currentDateInMillis - lastTakenDateInMillis;
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			isLessThanOrEqualDaysGap = diff <= noOfDaysGap ? true : false;
		}

		// Iap last date is compared only if the existing offline date is greater then
		// 15 (check using the flag)
		if (!isLessThanOrEqualDaysGap) {
			String userEmail = userId;
			if (!userId.contains("@"))
				userEmail = userId + "@ad.infosys.com";
			// compare the last taken date with iap and return the lastest date in millis
			lastTakenDateInMillis = getLatestDateInMillisForCertification(userEmail, certificationId,
					lastTakenDateInMillis);
			long diffInMillies = currentDateInMillis - lastTakenDateInMillis;
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			isLessThanOrEqualDaysGap = diff <= noOfDaysGap ? true : false;
		}

		// If last taken date for either online or offline is less or equal then 15 days
		if (isLessThanOrEqualDaysGap) {
			Date nextRegDate = new Date(lastTakenDateInMillis + (noOfDaysGap+1)*86400000);
			String lastTakenDateStr = timeZoneSpecificDateFormat.format(new Date(lastTakenDateInMillis));
			String nxtRegDateStr = timeZoneSpecificDateFormat.format(nextRegDate);
			eligibility.put("eligible", false);
			eligibility.put("reason", "-6");
			reasonObj.put("nextRegisterDate", this.createDateMapFromTimeStampString(nxtRegDateStr, timeZone));
			
			//update last taken date incase of online certification
			responseMap.put("lastTakenDate", this.createDateMapFromTimeStampString(lastTakenDateStr, timeZone));
		}

		if (Boolean.valueOf(eligibility.get("eligible").toString())) {
			reasonObj.put("code", 0);
			reasonObj.put("message", "Allowed");
		} else {

			String reason = eligibility.get("reason").toString();
			reasonObj.put("code", Integer.valueOf(reason));
			if (reasonMap.containsKey(reason)) {
				reasonObj.put("code", Integer.valueOf(reason));
				reasonObj.put("message", reasonMap.get(reason));
			} else {
				reasonObj.put("code", -10);
				reasonObj.put("message", "You are not eligible for this certification");
			}
		}
		eligibility.put("reason", reasonObj);

		if (responseMap.containsKey("slotFreezeDate") && responseMap.get("slotFreezeDate") != null
				&& !responseMap.get("slotFreezeDate").toString().equals("NA")) {
			responseMap.put("slotFreezeDate",
					this.createDateMapFromTimeStampString(responseMap.get("slotFreezeDate").toString(), timeZone));
		}

		Map<String, Object> verificationRequest = (Map<String, Object>) responseMap.get("verification_request");

		if (verificationRequest.containsKey("exam_date") && verificationRequest.get("exam_date") != null) {
			verificationRequest.put("exam_date",
					this.createDateMapFromTimeStampString(verificationRequest.get("exam_date").toString(), timeZone));
		}

		Map<String, Object> booking = (Map<String, Object>) responseMap.get("booking");

		if (booking.containsKey("date") && booking.get("date") != null) {
			booking.put("date", this.createDateMapFromTimeStampString(booking.get("date").toString(), timeZone));
		}

		return responseMap;
	}

	/*
	 * (This api is not used)
	 */
	@Override
	public Map<String, Object> uploadExtCertificationResult(String userId, String certificationId,
			Map<String, Object> requestMap) throws IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();
		if (requestMap.containsKey("exam_date")) {
			requestMap.put("exam_date",
					istDateFormat.format(new Date(Long.valueOf(requestMap.get("exam_date").toString()))));
		}

		String url = apiEndPointPrefix + "/UploadExternalCertResult?user_id=" + userId + "&certification_id="
				+ certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});
		return responseMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> submitExtCertificationResult(String userId, String certificationId,
			Map<String, Object> requestMap) throws IOException, ParseException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		if (requestMap.containsKey("exam_date")) {
			requestMap.put("exam_date",
					istDateFormat.format(new Date(Long.valueOf(requestMap.get("exam_date").toString()))));
		}
		Map<String, Object> responseMap = null;
		ResponseEntity<String> responseEntity = null;
		Map<String, Object> bookingInfo = this.getBookingInformation(userId, certificationId);
		Map<String, Object> verificationRequest = (Map<String, Object>) bookingInfo.get("verification_request");
		if (verificationRequest.get("status") == null
				|| !(verificationRequest.get("status").toString().toLowerCase().equals("recalled")
						|| verificationRequest.get("status").toString().toLowerCase().equals("uploaded")))

		{
			String url = apiEndPointPrefix + "/UploadExternalCertResult?user_id=" + userId + "&certification_id="
					+ certificationId;

			responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST, new HttpEntity<Object>(requestMap, headers),
					String.class);

			responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

		}

		if (responseMap == null
				|| (responseMap.containsKey("res_code") && responseMap.get("res_code").toString().equals("1"))) {
			String url = apiEndPointPrefix + "/SubmitRecallExternalCertResult?user_id=" + userId + "&certification_id="
					+ certificationId + "&action=submit";
			requestMap.remove("file");
			responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST, new HttpEntity<Object>(requestMap, headers),
					String.class);

			responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

		}

		return responseMap;

	}

	@Override
	public Map<String, Object> getSubmittedDocument(String document)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/GetDocument?Document=" + document;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> submitCertificationDetails(String userId, String certificationId,
			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		String url = apiEndPointPrefix + "/ResultSubmissions?user_id=" + userId + "&certification_id="
				+ certificationId;
		requestMap.put("certification_code", certificationId);
		requestMap.put("user_id", userId);

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(requestMap, headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public Map<String, Object> mapLexidToCertId(Map<String, Object> req)
			throws JsonParseException, JsonMappingException, IOException {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubCertificationUrl();

		if (!req.containsKey("lex_id") || req.get("lex_id") == null)
			throw new InvalidDataInputException("Invalid input");

		if (!req.containsKey("certification_id") || req.get("certification_id") == null)
			throw new InvalidDataInputException("Invalid input");

		String lexId = req.get("lex_id").toString();
		String certificationId = req.get("certification_id").toString();
		String url = apiEndPointPrefix + "/MapLexIdToCertCode?lex_id=" + lexId + "&certification_id=" + certificationId;

		ResponseEntity<String> responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>(headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	private HttpHeaders getRestCallHeader() {
		String accessToken = (String) servletContext.getAttribute("lhub_access_token");
		String clientId = lexServerProps.getLhubAthClientId();
		Optional<ApiAuthenticationModel> authDetailsRes = authRepo.findById(clientId);
		if (!authDetailsRes.isPresent())
			throw new ApplicationLogicError("Lhub auth details not found");
		String clientKey = authDetailsRes.get().getValue();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		headers.set("Client_Id", clientId);
		headers.set("Api_Key", clientKey);
		return headers;
	}

	/*
	 * This method returns the date in millis for only the date part of timestamp
	 * like if 10-12-2019 12:20:00 is passed then millis for 10-12-2019 00:00:00 is
	 * provided
	 */
	private long getMillisForDatePart(Date date) throws ParseException {
		return new SimpleDateFormat("dd-MM-yyyy").parse((new SimpleDateFormat("dd-MM-yyyy")).format(date)).getTime();

	}

	private Map<String, Object> createDateMapFromDateString(String date, String timeZone) {
		Map<String, Object> dateMap = new HashMap<String, Object>();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(date, formatter);

		dateMap.put("year", localDate.getYear());
		dateMap.put("month", localDate.getMonthOfYear());
		dateMap.put("day", localDate.getDayOfMonth());
		dateMap.put("timeZone", timeZone);
		return dateMap;
	}

	private Map<String, Object> createDateMapFromTimeStampString(String timeStamp, String timeZone) {
		Map<String, Object> dateMap = new HashMap<String, Object>();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
		LocalDate localDate = LocalDate.parse(timeStamp, formatter);
		dateMap.put("year", localDate.getYear());
		dateMap.put("month", localDate.getMonthOfYear());
		dateMap.put("day", localDate.getDayOfMonth());
		dateMap.put("timeZone", timeZone);
		return dateMap;
	}

	private String createDateStringFromDateMap(Map<String, Object> dateMap) {
		int year = Integer.valueOf(dateMap.get("year").toString());
		int month = Integer.valueOf(dateMap.get("month").toString());
		int day = Integer.valueOf(dateMap.get("day").toString());
		LocalDate localDate = new LocalDate(year, month, day);
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		return localDate.toString(formatter);
	}

	/**
	 * 
	 * 
	 * @param certificationId
	 * @param lastTakenDateInMillis
	 * @return
	 * @throws IOException
	 */
	private long getLatestDateInMillisForCertification(String userEmail, String certificationId,
			long lastTakenDateInMillis) throws IOException {
		// Fetch meta for the certification Id

		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(Arrays.asList(certificationId),
				new String[] { "identifier", "sourceShortName", "contentIdAtSource" }, null);

		if (searchHits.size() == 0)
			throw new InvalidDataInputException("Invalid Certification Id");
		else {
			Map<String, Object> certificationMeta = searchHits.get(0);
			if (certificationMeta.containsKey("sourceShortName") && certificationMeta.get("sourceShortName") != null) {
				String sourceShortName = certificationMeta.get("sourceShortName").toString();

				// This comparision is done only for IAP certification
				if (sourceShortName.toLowerCase().equals("iap")) {

					if (!certificationMeta.containsKey("contentIdAtSource")
							|| certificationMeta.get("contentIdAtSource") == null)
						throw new ApplicationLogicError("Certification Code Not Found");

					String certificationCode = certificationMeta.get("contentIdAtSource").toString();
					long lastTakenDateOnline = getLastTakenDateOnlineForCertification(userEmail, certificationCode);
					lastTakenDateInMillis = lastTakenDateOnline > lastTakenDateInMillis ? lastTakenDateOnline
							: lastTakenDateInMillis;
				}
			}

		}

		return lastTakenDateInMillis;
	}

	/**
	 * This method returns the last taken date online for the certification if
	 * exists else return 0
	 * 
	 * @param certificationCode
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	private long getLastTakenDateOnlineForCertification(String userEmail, String certificationCode)
			throws JsonParseException, JsonMappingException, IOException {
		long lastOnlineCertificationSubmission = 0;
		String url = serverProp.getIapCertificationUrl();
		String clientId = serverProp.getIapCertificationClientId();
		String clientSecret = serverProp.getIapCertificationClientSecret();

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("userId", userEmail);
		request.put("clientId", clientId);
		request.put("clientSecret", clientSecret);
		ResponseEntity<String> responseEntity = null;
		try {
			HttpHeaders headers = new HttpHeaders();

			responseEntity = restTemplateForCertification.exchange(url, HttpMethod.POST, new HttpEntity<Object>(request, headers),
					String.class);

			Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});
			if (responseMap.containsKey("resultList") && responseMap.get("resultList") != null) {
				;
				List<Map<String, Object>> certificationMetaList = (List<Map<String, Object>>) responseMap
						.get("resultList");
				for (Map<String, Object> certificationMeta : certificationMetaList) {
					if (certificationMeta.get("certificationCode").toString().toLowerCase()
							.equals(certificationCode.toLowerCase())) {
						SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
						format.setTimeZone(TimeZone.getTimeZone("IST"));
						lastOnlineCertificationSubmission = format.parse(certificationMeta.get("endTime").toString())
								.getTime();

					}
				}
			}

		} catch (HttpServerErrorException httpServerErrorException) {
			throw new ApplicationLogicError("IAP Rest Call Exception",httpServerErrorException);
		} catch (ParseException e) {
			throw new ApplicationLogicError("Error while parsing date from IAP",e);
		}
		return lastOnlineCertificationSubmission;
	}

}
