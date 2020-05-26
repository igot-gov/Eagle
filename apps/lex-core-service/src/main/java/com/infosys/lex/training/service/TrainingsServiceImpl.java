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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.bodhi.repo.ApiAuthenticationModel;
import com.infosys.lex.common.bodhi.repo.AuthenticationRepository;
import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.logger.LexLogger;

@Service
public class TrainingsServiceImpl implements TrainingsService {

	@Autowired
	ServletContext servletContext;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	AuthenticationRepository authRepo;

	private LexLogger logger = new LexLogger(getClass().getName());

	private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

	private static final SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yyyy");

	// private static final String apiEndPointPrefix =
	// "https://itgatewaytst.infosys.com/extapilex/api/Learning";

	// private static final String apiEndPointPrefix =
	// "http://IPaddress:8740/api/Learning";

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getTrainings(String contentId, String emailId, String startDate, String endDate,
			String location) throws Exception {
		HttpHeaders headers = getRestCallHeader();
		String apiEndPointPrefix = lexServerProps.getLhubUrl();
		String url = apiEndPointPrefix + "/GetOfferingDetails?content_id=" + contentId + "&email=" + emailId;

		if (startDate != null && endDate != null && !startDate.isEmpty() && !startDate.isEmpty()) {
			url += "&start_dt=" + startDate + "&end_dt=" + endDate;
		}

		if (location != null && !location.isEmpty()) {
			url += "&location=" + location;
		}

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			responseMaps.forEach(responseMap -> {
				List<Map<String, Object>> sessions = (List<Map<String, Object>>) responseMap.get("sessions");
				sessions.forEach(session -> {
					String receivedStartDate = session.get("start_dt").toString();
					String receivedEndDate = session.get("end_dt").toString();
					Date oldFormatStartDate;
					Date oldFormatEndDate;
					try {
						oldFormatStartDate = formatter.parse(receivedStartDate);
						oldFormatEndDate = formatter.parse(receivedEndDate);
						session.put("start_dt", newFormat.format(oldFormatStartDate));
						session.put("end_dt", newFormat.format(oldFormatEndDate));
					} catch (ParseException e) {
						System.out.println(e.getMessage());
					}
				});
			});

			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> getOfferingsSessions(String offeringId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetSessionDetails?offering_id=" + offeringId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);
			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});
			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> registerForOffering(String offeringId, String userId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/RegisterCourseOffering?offering_id=" + offeringId + "&user_id=" + userId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Object>(headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> deRegisterForOffering(String offeringId, String userId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/UnregisterCourseOffering?offering_id=" + offeringId + "&user_id=" + userId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Object>(headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> getOfferingsCount(List<String> identifiers) throws JsonProcessingException {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetOfferingsCount";
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<List<String>>(identifiers, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");

		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
substitute url based on requirement
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
substitute url based on requirement

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Object>(headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
substitute url based on requirement
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
substitute url based on requirement

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Object>(headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<String> getWatchListContent(String userId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetEmployeeWatchListDetails?user_id=" + userId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);
			List<String> response = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<String>>() {
					});
			return response;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> isJL6AndAbove(String userId) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetUserInfo?user_id=" + userId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<Map<String, Object>>() {
					});

			return responseMap;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}

	}

	@Override
	public List<Map<String, Object>> nominateForOfferings(String offeringId, Map<String, Object> request) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/Nominate?offering_id=" + offeringId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Map<String, Object>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<List<Map<String, Object>>>() {
						});
				return responseMaps;
			}
			throw new Exception("Error while calling post request");

		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> denominateForOfferings(String offeringId, Map<String, Object> request) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/Denominate?offering_id=" + offeringId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Map<String, Object>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<List<Map<String, Object>>>() {
						});
				return responseMaps;
			}
			throw new Exception("Error while calling post request");

		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> shareOffering(String offeringId, Map<String, Object> request) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/ShareOfferings?offering_id=" + offeringId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Map<String, Object>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");

		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> createJitRequest(Map<String, Object> request) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/CreateJITRequest";
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Map<String, Object>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");

		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> getJitRequestsCreatedByUser(String userId) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetJITRequests?user_id=" + userId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			// parsing date into new format for UI
			responseMaps.forEach(responseMap -> {
				String receivedDate = responseMap.get("start_date").toString();
				Date oldFormatDate;
				try {
					oldFormatDate = formatter.parse(receivedDate);
					responseMap.put("start_date", newFormat.format(oldFormatDate));
				} catch (ParseException e) {
					System.out.println(e.getMessage());
				}
			});

			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> getOfferingsManagerCanReject(String managerId) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetOfferingDetailsForManager?manager_id=" + managerId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public Map<String, Object> rejectOffering(String offeringId, String userId, Map<String, Object> request) {

		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/RejectRegistration?offering_id=" + offeringId + "&user_id=" + userId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<Map<String, Object>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> questionsForFeedback(String templateId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/GetFeedbackQuestions?template_id=" + templateId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}

	}

	@Override
	public Map<String, Object> submitFeedback(String offeringId, String userId, String templateId,
			List<Map<String, Object>> request) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/InsertFeedback?offering_id=" + offeringId + "&user_id=" + userId
				+ "&template=" + templateId;
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
					new HttpEntity<List<Map<String, Object>>>(request, headers), String.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				return responseMap;
			}
			throw new Exception("Error while calling post request");
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}

	@Override
	public List<Map<String, Object>> getOfferingsForFeedbackByUser(String userId) {
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		String url = apiEndPointPrefix + "/ListOfferingsforfeedback?user_id=" + userId;

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<Object>(headers), String.class);

			List<Map<String, Object>> responseMaps = new ObjectMapper().readValue(responseEntity.getBody(),
					new TypeReference<List<Map<String, Object>>>() {
					});

			return responseMaps;
		} catch (HttpServerErrorException httpServerErrorException) {
			logger.error(httpServerErrorException);
			throw new ApplicationLogicError("Learning Hub Rest Call Exception",httpServerErrorException);
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception);
			throw new ApplicationLogicError(exception.getMessage(),exception);
		}
	}
	
	@Override
	public Map<String,Object> mapLexidToCourseId(Map<String,Object> req) throws JsonParseException, JsonMappingException, IOException
	{
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement

substitute url based on requirement
			throw new InvalidDataInputException("Invalid input");
		
		if(!req.containsKey("course_id") || req.get("course_id")== null)
			throw new InvalidDataInputException("Invalid input");

substitute url based on requirement
		String courseId = req.get("course_id").toString();
substitute url based on requirement


		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<Object>( headers), String.class);

		Map<String, Object> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		return responseMap;
	}

	@Override
	public List<String> getEducatorDetails(List<String> contentIds) throws JsonParseException, JsonMappingException, IOException
	{
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement

		String url = apiEndPointPrefix + "/GetEducatorDetails";


		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<List<String>>(contentIds, headers), String.class);

		List<String> responseMap = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<String>>() {
				});

		return responseMap;
	}
	
	@Override
	public List<Map<String,Object>> getTrainingHistory(String userId,String status) throws JsonParseException, JsonMappingException, IOException
	{
		HttpHeaders headers = getRestCallHeader();
substitute url based on requirement
		
		String url = apiEndPointPrefix + "GetTrainingDetailsHistory?user_id="+userId+"&status="+status;
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<>( headers), String.class);
		List<Map<String,Object>> responseList = new ObjectMapper().readValue(responseEntity.getBody(),
				new TypeReference<List<Map<String,Object>>>() {
				});
		
		String timeZone = "IST";

		for(Map<String,Object> training:responseList)
		{
			if (training.containsKey("time_zone"))
				timeZone = training.get("time_zone").toString();
			if (training.containsKey("start_date")) {
				training.put("start_date", this.createDateMapFromDateString(training.get("start_date").toString(), timeZone));
			}
			
		}

		return responseList;
	}
	

	private HttpHeaders getRestCallHeader() {
		String accessToken = (String) servletContext.getAttribute("lhub_access_token");
		
substitute url based on requirement
substitute based on requirement
		if(!authDetailsRes.isPresent())
			throw new ApplicationLogicError("Lhub auth details not found");
		String clientKey = authDetailsRes.get().getValue();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
substitute based on requirement
		headers.set("Api_Key", clientKey);
		return headers;
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
}
