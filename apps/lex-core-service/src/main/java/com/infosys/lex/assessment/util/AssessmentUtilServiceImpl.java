/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at http-urls://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.assessment.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.common.util.LexServerProperties;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.ResourceNotFoundException;

@Service
public class AssessmentUtilServiceImpl implements AssessmentUtilService {

	
	@Autowired
	LexServerProperties props;

	@Autowired
	RestTemplate restTemplate;
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> validateAssessment(List<Map<String, Object>> questions, Map<String, Object> answers) {
		try {
			Integer correct = 0;
			Integer blank = 0;
			Integer inCorrect = 0;
			Double result = 0d;
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for (Map<String, Object> question : questions) {
				List<String> marked = new ArrayList<String>();
				if (question.containsKey("questionType")) {
					if (question.get("questionType").toString().toLowerCase().equals("mtf")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if (options.containsKey("response") && !options.get("response").toString().isEmpty())
								marked.add(options.get("optionId").toString() + "-"
										+ options.get("text").toString().toLowerCase() + "-"
										+ options.get("response").toString().toLowerCase());
						}
					} else if (question.get("questionType").toString().toLowerCase().equals("fitb")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if (options.containsKey("response") && !options.get("response").toString().isEmpty())
								marked.add(options.get("optionId").toString() + "-"
										+ options.get("response").toString().toLowerCase());
						}
					} else if (question.get("questionType").toString().toLowerCase().equals("mcq-sca")
							|| question.get("questionType").toString().toLowerCase().equals("mcq-mca")) {
						for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
							if ((boolean) options.get("userSelected"))
								marked.add(options.get("optionId").toString());
						}
					}
				} else {
					for (Map<String, Object> options : (List<Map<String, Object>>) question.get("options")) {
						if ((boolean) options.get("userSelected"))
							marked.add(options.get("optionId").toString());
					}
				}

				if (marked.size() == 0)
					blank++;
				else {
					List<String> answer = (List<String>) answers.get(question.get("questionId"));
					if (answer.size() > 1)
						Collections.sort(answer);
					if (marked.size() > 1)
						Collections.sort(marked);
					if (answer.equals(marked))
						correct++;
					else
						inCorrect++;
				}
			}
			result = ((correct * 100d) / (correct + blank + inCorrect));
			resultMap.put("result", result);
			resultMap.put("incorrect", inCorrect);
			resultMap.put("blank", blank);
			resultMap.put("correct", correct);
			return resultMap;
		} catch (Exception ex) {
			throw new ApplicationLogicError("Error when verifying assessment. Error : " + ex.getMessage(),ex);
		}

	}
	
	
	
	/*
	 * This method fetches the answer key for assessment
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAnswerKeyForAssessmentAuthoringPreview(Map<String, Object> contentMeta) {
		if (contentMeta.containsKey("artifactUrl") && contentMeta.get("artifactUrl") != null) {
			String artifactUrl = contentMeta.get("artifactUrl").toString();
			
			try {
				artifactUrl = artifactUrl.replaceAll(".json", "-key.json");
				
				String strCompare = "content-store/";
				int index = artifactUrl.indexOf(strCompare);
				if (index == -1)
					throw new ApplicationLogicError("Invalid assessment key url");
				int startIndexOfLocation = index + strCompare.length();
				String location = artifactUrl.substring(startIndexOfLocation);
				String urlEncodedLocation = location.replaceAll("/", "%2F");
				String contentHost = props.getContentServiceHost();
				String contentPort = props.getBodhiContentPort();

				String fetchUrl = "http-url://" + contentHost + ":" + contentPort + "/contentv3/download/"
						+ urlEncodedLocation;
				System.out.println(fetchUrl);
				return restTemplate.getForObject(fetchUrl, Map.class);

			} catch (HttpStatusCodeException ex) {
				throw new ApplicationLogicError("Error in fetching solution Json!!",ex);
			}
		} else {
			throw new ResourceNotFoundException("Invalid artifact Url");
		}
	}

}
