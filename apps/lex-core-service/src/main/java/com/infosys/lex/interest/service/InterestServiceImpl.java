/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.ValidLanguages;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.exception.ResourceNotFoundException;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.interest.bodhi.repo.InterestCassandraRepo;
import com.infosys.lex.interest.entities.Interest;
import com.infosys.lex.interest.entities.InterestKey;
import com.infosys.lex.interest.repo.InterestCRUD;

@Service
public class InterestServiceImpl implements InterestService {

	@Autowired
	InterestCassandraRepo interestCassandraRepo;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	InterestCRUD interestCRUD;

	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Autowired
	ValidLanguages validLanguages;

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getInterest(String rootOrg, String userId) throws Exception {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		Map<String, Object> resultList = new HashMap<String, Object>();
		InterestKey interestKey = new InterestKey();
		interestKey.setUserId(userId);
		interestKey.setRootOrg(rootOrg);
		Optional<Interest> cassandraObject = interestCassandraRepo.findById(interestKey);
		if (!cassandraObject.isPresent()) {
			resultList.put("user_interest", Collections.emptyList());
			return resultList;
		} else {
			if (cassandraObject.get().getInterest() == null) {
				resultList.put("user_interest", Collections.emptyList());
			} else {
				resultList.put("user_interest", cassandraObject.get().getInterest());
			}

		}
		return resultList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String delete(String rootOrg, String userId, String interest) throws Exception {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey();
		interestKey.setRootOrg(rootOrg);
		interestKey.setUserId(userId);
		Optional<Interest> interestCassandra = interestCassandraRepo.findById(interestKey);
		// time stamp
		Date dateCreatedOn = new Date();
		Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());

		if (!interestCassandra.isPresent()) {
			throw new ResourceNotFoundException("interests.notPresent");
		}
		if (!interestCassandra.get().getInterest().contains(interest)) {
			throw new ResourceNotFoundException("intrest.doesNotExist");
		}

		if (interestCassandra.get().getInterest().size() == 1) {
			interestCassandraRepo.deleteById(interestKey);
		} else {
			interestCassandra.get().setUpdatedOn(timeCreatedOn);
			interestCassandra.get().getInterest().remove(interest);
			interestCassandraRepo.save(interestCassandra.get());
		}

		return "deleted";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String upsert(String rootOrg, @NotNull String userId, @NotNull String interest) throws Exception {

		if (interest == null || interest.isEmpty()) {
			throw new InvalidDataInputException("invalid.interest");
		}

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey();
		interestKey.setRootOrg(rootOrg);
		interestKey.setUserId(userId);
		Date dateCreatedOn = new Date();
		Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());
		Optional<Interest> cassandraObject = interestCassandraRepo.findById(interestKey);
		if (!cassandraObject.isPresent()) {
			// Add User if user does not exist
			Interest newUser = new Interest();
			newUser.setInterestKey(interestKey);
			newUser.setCreatedOn(timeCreatedOn);
			newUser.setUpdatedOn(timeCreatedOn);
			Set<String> setOfInterest = new HashSet<String>();
			setOfInterest.add(interest);
			newUser.setInterest(setOfInterest);
			interestCassandraRepo.save(newUser);
		} else {
			// User exists update it
			Set<String> setToBeUpdated = new HashSet<String>();
			if (cassandraObject.get().getInterest() != null) {
				setToBeUpdated.addAll(cassandraObject.get().getInterest());
			}
			setToBeUpdated.add(interest);
			cassandraObject.get().setInterest(setToBeUpdated);
			cassandraObject.get().setUpdatedOn(timeCreatedOn);
			interestCassandraRepo.save(cassandraObject.get());
		}
		return "success";
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<String> autoComplete(String rootOrg, String org, @NotNull String language, String query, String topic)
			throws IOException {
		String alias = new String();
		String scriptTopic = new String();
		List<String> interest = new ArrayList<String>();
		List<String> allowedLanguages = validLanguages.allowedLanguages();
		String[] listOfLanguages = language.split(",");
		List<String> allowed = new ArrayList<String>();
		if (topic.equals("search")) {
			alias = "searchautocomplete_";
			scriptTopic = "searchactemplate";
		} else if (topic.equals("topic")) {
			alias = "topicautocomplete_";
			scriptTopic = "topicsactemplate";
		}
		for (int i = 0; i < listOfLanguages.length; i++) {
			if (allowedLanguages.contains(listOfLanguages[i])) {
				listOfLanguages[i] = alias + listOfLanguages[i];
				allowed.add(listOfLanguages[i]);
			}
		}
		if (allowed.isEmpty()) {
			throw new ResourceNotFoundException("No language found");
		}
		SearchTemplateRequest request = new SearchTemplateRequest();
		request.setRequest(new SearchRequest(allowed.toArray(new String[allowed.size()])));
		request.setScriptType(ScriptType.STORED);
		request.setScript(scriptTopic);
		Map<String, Object> params = new HashMap<>();
		params.put("rootOrg", rootOrg);
		params.put("org", org);
		params.put("searchTerm", query.toLowerCase());
		request.setScriptParams(params);
		try {
			SearchTemplateResponse response = restHighLevelClient.searchTemplate(request, RequestOptions.DEFAULT);
			SearchResponse searchResponse = new SearchResponse();
			searchResponse = response.getResponse();
			for (SearchHit h : searchResponse.getHits()) {
				interest.add(h.getSourceAsMap().get("searchTerm").toString());
			}
			return interest;
		} catch (RuntimeException ex) {

		}
		return interest;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> suggestedComplete(String rootOrg, String userid, String org, @NotNull String language)
			throws Exception {
		// Validating User
		if (!userUtilService.validateUser(rootOrg, userid)) {
			throw new InvalidDataInputException("invalid.user");
		}
		List<String> languages = new ArrayList<String>();
		languages = interestCRUD.suggestedComplete(rootOrg, org, language);
		return languages;
	}
}
