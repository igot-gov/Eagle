/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.interest.service;


import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * @see com.infosys.lex.interest.service.InterestService#getInterest(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getInterest(String rootOrg, String userId)  {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		Map<String, Object> resultList = new HashMap<String, Object>();

		Optional<Interest> cassandraObject = interestCassandraRepo.findById(new InterestKey(rootOrg, userId));
		if (!cassandraObject.isPresent()) {
			resultList.put("user_interest", Collections.emptyList());
			return resultList;
		}
		if (cassandraObject.get().getInterest() == null) {
			resultList.put("user_interest", Collections.emptyList());
		} else {
			resultList.put("user_interest", cassandraObject.get().getInterest());
		}

		return resultList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.interest.service.InterestService#delete(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteInterest(String rootOrg, String userId, Map<String, Object> interestMap)  {

		if (interestMap.get("interest") == null || interestMap.get("interest").toString().isEmpty()) {
			throw new InvalidDataInputException("invalid.interest");
		}

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey(rootOrg, userId);

		String interest = interestMap.get("interest").toString();
		Optional<Interest> interestCassandra = interestCassandraRepo.findById(interestKey);

		// time stamp
		Date dateCreatedOn = new Date();
		Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());

		if (!interestCassandra.isPresent()) {
			throw new InvalidDataInputException("interest.notPresent");
		}
		if (!interestCassandra.get().getInterest().contains(interest)) {
			throw new InvalidDataInputException("interest.doesNotExist");
		}

		if (interestCassandra.get().getInterest().size() == 1) {
			interestCassandraRepo.deleteById(interestKey);
		} else {
			interestCassandra.get().setUpdatedOn(timeCreatedOn);
			interestCassandra.get().getInterest().remove(interest);
			interestCassandraRepo.save(interestCassandra.get());
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.infosys.lex.interest.service.InterestService#upsert(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void upsertInterest(String rootOrg, String userId, Map<String, Object> interestMap)  {

		if (interestMap.get("interest") == null || interestMap.get("interest").toString().isEmpty()) {
			throw new InvalidDataInputException("invalid.interest");
		}

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey(rootOrg, userId);
		Date dateCreatedOn = new Date();
		String interest = interestMap.get("interest").toString().trim();
		Timestamp timeCreatedOn = new Timestamp(dateCreatedOn.getTime());
		Optional<Interest> interestObject = interestCassandraRepo.findById(interestKey);

		if (!interestObject.isPresent()) {
			// Add User if user does not exist
			Set<String> setOfInterest = new HashSet<String>();
			setOfInterest.add(interest);
			Interest newUser = new Interest(interestKey, setOfInterest, timeCreatedOn, timeCreatedOn);
			interestCassandraRepo.save(newUser);
		} else {
			Interest existingUser = interestObject.get();
			// User already exists so update interest
			Set<String> setToBeUpdated = existingUser.getInterest();
			if (setToBeUpdated == null || setToBeUpdated.isEmpty()) {
				setToBeUpdated = new HashSet<String>();
			} else {
				setToBeUpdated.add(interest);
			}
			interestCassandraRepo.save(existingUser);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.infosys.lex.interest.service.InterestService#autoComplete(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public List<String> autoComplete(String rootOrg, String org, @NotNull String language, String query, String topic)
			throws IOException {

		if (query.trim().isEmpty()) {
			return Arrays.asList("Scale up IoT", "Leadership & Management", "Ecosystems in new agile work",
					"Artificial Intelligence", "Digitalization as key driver");
		}
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
	 * com.infosys.lex.interest.service.InterestService#suggestedComplete(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> suggestedComplete(String rootOrg, String userid, String org, @NotNull String language) throws IOException
			 {
		// Validating User
		if (!userUtilService.validateUser(rootOrg, userid)) {
			throw new InvalidDataInputException("invalid.user");
		}
		List<String> languages = new ArrayList<String>();
		languages = interestCRUD.suggestedComplete(rootOrg, org, language);
		return languages;
	}

	@Override
	public void delete(String rootOrg, String userId, String interest)  {

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey(rootOrg, userId);
//		interestKey.setRootOrg(rootOrg);
//		interestKey.setUserId(userId);
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

	}

	@Override
	public String upsert(String rootOrg, @NotNull String userId, @NotNull String interest)  {

		if (interest == null || interest.isEmpty()) {
			throw new InvalidDataInputException("invalid.interest");
		}

		// Validating User
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		InterestKey interestKey = new InterestKey(rootOrg, userId);
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

}
