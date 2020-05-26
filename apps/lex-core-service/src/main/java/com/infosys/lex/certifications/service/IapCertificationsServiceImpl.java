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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.util.LexServerProperties;

@Service
public class IapCertificationsServiceImpl implements IapCertificationsService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LexServerProperties lexServerProps;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAttempts(String userEmail, String lexResourceId)throws JsonParseException, JsonMappingException, IOException {

		String url = lexServerProps.getIapSubmissonsUrl() + "?userEmail=" + userEmail + "&lexResourceId="
				+ lexResourceId;
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
				new HttpEntity<Object>(getRestCallHeader()), String.class);

		Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

		List<Map<String, Object>> contestResults = (List<Map<String, Object>>) responseMap.get("contestResults");

		contestResults.iterator().forEachRemaining(contestResult -> {
			contestResult.remove("resultConstruct");
		});

		return responseMap;
	}

	private HttpHeaders getRestCallHeader() {

		HttpHeaders headers = new HttpHeaders();
		headers.set("clientId", lexServerProps.getIapSubmissonsClientId());
		headers.set("clientSecret", lexServerProps.getIapSubmissonsClientSecret());
		return headers;
	}
}
