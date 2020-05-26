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

package com.infosys.lex.exercise.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.ResourceNotFoundException;

@Service
public class IAPVerificationServiceImpl implements IAPVerificationService {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	LexServerProperties props;
	
	/* (non-Javadoc)
	 * @see com.infosys.core.service.IAPVerificationService#postSolutionString(java.lang.String, java.lang.String)
	 */
	@Override
	public String postSolutionString(String url, String entityData) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<String>(entityData,
				headers);
		return restTemplate.postForObject(url, entity, String.class);
	}
	

	/*
	 * This method fetches the answer key for exercise
	 */
	
	@Override
	public String getAnswerKeyForExerciseAuthoringPreview(Map<String, Object> contentMeta) {
		if (contentMeta.containsKey("artifactUrl") && contentMeta.get("artifactUrl") != null) {
			String artifactUrl = contentMeta.get("artifactUrl").toString();
			try {
				String strCompare = "content-store/";
				int index = artifactUrl.indexOf(strCompare);
				if (index == -1)
					throw new ApplicationLogicError("Invalid assessment key url");
				int startIndexOfLocation = index + strCompare.length();
				String location = artifactUrl.substring(startIndexOfLocation);
				String urlEncodedLocation = location.replaceAll("/", "%2F").replaceAll(".json", "-key.json");
				String contentHost = props.getContentServiceHost();
				String contentPort = props.getBodhiContentPort();

				String fetchUrl = "http://" + contentHost + ":" + contentPort + "/contentv3/download/"
						+ urlEncodedLocation;
				ResponseEntity<String> response = restTemplate.getForEntity(fetchUrl, String.class);

				return response.getBody();

			} catch (HttpStatusCodeException ex) {
				throw new ApplicationLogicError("Error in fetching solution Json!!",ex);
			}
		} else {
			throw new ResourceNotFoundException("Invalid artifact Url");
		}
	}

}