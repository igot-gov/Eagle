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
package com.infosys.lex.tnc.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.tnc.bodhi.repo.UserTermsAndConditions;
import com.infosys.lex.tnc.bodhi.repo.UserTermsAndConditionsPrimaryKey;
import com.infosys.lex.tnc.bodhi.repo.UserTermsAndConditionsRepository;
import com.infosys.lex.tnc.dto.AcceptTermsDTO;
import com.infosys.lex.tnc.postgres.entities.TermsAndConditions;
import com.infosys.lex.tnc.postgres.entities.TermsAndConditionsPrimaryKey;
import com.infosys.lex.tnc.postgres.repo.TermsAndConditionsRepository;
import com.infosys.lex.tnc.validator.TnCValidator;

@Service
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

	@Autowired
	TermsAndConditionsRepository tnCRepo;

	@Autowired
	UserTermsAndConditionsRepository userTnCRepo;

	@Autowired
	TnCValidator tnCValidator;

//	private String[] docNames = new String[] { "Europe DP", "Rest of world DP", "Generic T&C" };
//	private String[] docNamesV2 = new String[] { "Generic T&C", "Data Privacy" };
	private String[] dpNames = new String[] { "Europe DP", "Rest of world DP", "Data Privacy" };
	private String tncName = "Generic T&C";

	// To hold all the available languages of the terms with the latest version.
	Map<String, Object> latestVerLangs = new HashMap<String, Object>();

	@Override
	public Map<String, Object> acceptUserTerms(String rootOrg, @Valid AcceptTermsDTO acceptData) throws Exception {

		String userId = acceptData.getUserId();
		String userType = "User";
		List<Map<String, Object>> termsAccepted = acceptData.getTermsAccepted();

		// PID done
		tnCValidator.validateUserId(rootOrg, userId);
		// Validate termsSize
		tnCValidator.validateTermsSize(termsAccepted);

		Map<String, Object> returnVal = new HashMap<>();
		Date dateAccepted = new Date();
		int insertCount = 0;

		for (Map<String, Object> t : termsAccepted) {
			String docName = (String) t.get("docName");
			double version = Double.parseDouble((String) t.get("version"));
			String acceptedLanguage = t.get("acceptedLanguage").toString();
			if (docName.equals(tncName) || docName.equals(this.dpNames[2])) {
				// Checking document and versions exists in main DB
				// Handling two languages for two documents separately
				Optional<TermsAndConditions> records = tnCRepo.findById(new TermsAndConditionsPrimaryKey(rootOrg,
						docName, userType, new BigDecimal(version), acceptedLanguage));
				if (!records.isPresent()) {
					throw new ApplicationLogicError("terms.notfound");
				}
			} else {
				throw new InvalidDataInputException("invalid.termname");
			}
		}

		for (Map<String, Object> t : termsAccepted) {
			String docName = (String) t.get("docName");
			double version = Double.parseDouble((String) t.get("version"));
			String acceptedLanguage = t.get("acceptedLanguage").toString();
			// Insert taken in separate loop to avoid partial insert due to exception from
			// data validations
			// Inserting accepted terms in database
			UserTermsAndConditions save = userTnCRepo.save(new UserTermsAndConditions(
					new UserTermsAndConditionsPrimaryKey(rootOrg, userId, docName, userType, version), dateAccepted,
					acceptedLanguage));
			if (save != null) {
				insertCount++;
			}
		}

		if (insertCount == termsAccepted.size()) {
			returnVal.put("result", "success");
			return returnVal;
		} else {
			throw new ApplicationLogicError("term.insertfailed");
		}
	}

	@Override
	public Map<String, Object> checkTermValidation(String rootOrg, List<String> langCode, String userId)
			throws Exception {
		// Removed UserId, RootOrg validation after discussion
		// Fixating the user type
		String userType = "User";
		// Validate language
		String language = tnCValidator.getLanguage(rootOrg, userId, langCode);

		// Get the latest T&C for Generic T&C
		Map<String, Object> latest = new HashMap<>();
		for (String docName : Arrays.asList(tncName, dpNames[2])) {
			latest.put(docName, this.getLatest(rootOrg, docName, userType, language));
		}
		// Return values
		Map<String, Object> ret = new HashMap<>();

		List<String> allDocNames = Arrays.asList(this.tncName, this.dpNames[0], this.dpNames[1], this.dpNames[2]);

		if (userId.equalsIgnoreCase("default")) {
			// for first time user all the latest doc is sent
			ret = latest;
		} else {
			// Get the user acceptance based on user Type
			List<UserTermsAndConditions> userAcceptance = userTnCRepo.findUserRecord(rootOrg, userId, allDocNames);

			// If user has not accepted any terms and conditions, return the latest versions
			// of all the documents
			if (userAcceptance == null || userAcceptance.isEmpty()) {
				ret = latest;
			} else {
				// Sorting it based on the date accepted descending and Version descending
				Collections.sort(userAcceptance, new Comparator<UserTermsAndConditions>() {
					@Override
					public int compare(UserTermsAndConditions m1, UserTermsAndConditions m2) {
						Double d1 = m1.getKey().getVersion();
						Double d2 = m2.getKey().getVersion();
						Date t1 = m1.getAcceptedOn();
						Date t2 = m2.getAcceptedOn();
						int ret = t2.compareTo(t1);
						if (ret != 0) {
							return ret;
						} else {
							return d2.compareTo(d1);
						}
					}
				});

				boolean dpFlag = false;
				boolean tncFlag = false;

				for (UserTermsAndConditions docs : userAcceptance) {
					String docName = docs.getKey().getDocName();
					// Checking what version user has chosen
					/**
					 * Instead of allowing only Data Privacy as above Checking Europe and Rest Of
					 * The World as well
					 */
					List<String> dpDocs = Arrays.asList(this.dpNames[2], this.dpNames[0], this.dpNames[1]);
					if ((!tncFlag && docName.equals(this.tncName)) || (!dpFlag && dpDocs.contains(docName))) {
						// TODO Check Tnc in if for performance improvement
						if (!dpDocs.contains(docName)) {
							tncFlag = true;
						} else {
							dpFlag = true;
							docName = this.dpNames[2];
						}
						// Comparing the user accepted version and latest version, if user has not
						// accepted latest version changeFlag will become true
						String[] currentData = (String[]) latest.get(docName);
						if (currentData != null) {
							currentData[2] = Double.toString(docs.getKey().getVersion());
							currentData[3] = new Timestamp(docs.getAcceptedOn().getTime()).toString();
							currentData[5] = docs.getLanguage();
							ret.put(docName, currentData);
						}
					}
					// Once tnc and dp version is checked it breaks the loop
					if (tncFlag && dpFlag) {
						break;
					}
				}
				// If user has not accepted any TnC, add it
				if (!tncFlag) {
					String docName = this.tncName;
					ret.put(docName, latest.get(docName));
				}
				// If user has not accepted any DP, add it
				if (!dpFlag) {
					String docName = this.dpNames[2];
					ret.put(docName, latest.get(docName));
				}
			}
		}

		Map<String, Object> formattedResponse = new HashMap<>();
		List<Map<String, Object>> terms = new ArrayList<>();

		// Checking language availability
		if (latestVerLangs == null || latestVerLangs.isEmpty()) {
			throw new ApplicationLogicError("terms.notfound");
		}

		// Preparing the terms to be sent to user
		boolean isAccepted = true;
		for (Map.Entry<String, Object> m : ret.entrySet()) {
			Map<String, Object> term = new HashMap<>();
			String name = m.getKey();
			term.put("name", name);
			String[] tncData = (String[]) m.getValue();
			boolean accepted = !"".equals(tncData[2])
					&& Double.parseDouble(tncData[0]) <= Double.parseDouble(tncData[2]);
			isAccepted = isAccepted && accepted;
			term.put("isAccepted", accepted);
			term.put("version", tncData[0]);
			term.put("content", tncData[1]);
			term.put("language", tncData[4]);
			term.put("availableLanguages", latestVerLangs.getOrDefault(name, new ArrayList<>()));
			term.put("acceptedVersion", tncData[2]);
			term.put("acceptedDate", tncData[3]);
			term.put("acceptedLanguage", tncData[5]);
			terms.add(term);
		}

		formattedResponse.put("termsAndConditions", terms);
		// Change flag decides whether there is a version change,
		// If change is there user needs to accept so is accepted becomes false
		formattedResponse.put("isAccepted", isAccepted);

		return formattedResponse;
	}

	private String[] getLatest(String rootOrg, String docName, String userType, String language) {

		List<TermsAndConditions> termsAndCondition = tnCRepo.fetchLatestVersionDoc(rootOrg, docName, userType);

		// If terms and conditions are not found in DB
		if (termsAndCondition == null || termsAndCondition.isEmpty()) {
			throw new ApplicationLogicError("terms.notfound");
		}

		TermsAndConditions docs = this.fetchLatestVersionAsPerLanguage(termsAndCondition, docName, language);

		// Storing the latest document
		String[] data = new String[6];
		// Stores the latest version of the document
		data[0] = Double.toString(docs.getKey().getVersion().doubleValue());
		// Stores the latest content
		// TODO Import org.apache.commons.text package and use StringEscapeUtils
		data[1] = StringEscapeUtils.unescapeHtml4(docs.getDocument());
		// Will store the accepted version of the document if available
		data[2] = "";
		// Will store the date when accepted by the user if available
		data[3] = "";
		// Will store the available language of the content
		data[4] = docs.getKey().getLanguage();
		// Will store the accepted language by user if available
		data[5] = "";

		return data;
	}

	private TermsAndConditions fetchLatestVersionAsPerLanguage(List<TermsAndConditions> termsAndCondition,
			String docName, String language) {

		termsAndCondition.sort(((a, b) -> ((b.getKey().getVersion()).compareTo(a.getKey().getVersion()))));

		List<TermsAndConditions> filteredAsPerLang = new ArrayList<TermsAndConditions>();

		BigDecimal latestVersion = termsAndCondition.get(0).getKey().getVersion();

		// To fetch all available languages of a term.
		List<String> availableLanguages = new ArrayList<String>();

		// filtering on version then on language
		for (TermsAndConditions terms : termsAndCondition) {
			if (terms.getKey().getVersion().equals(latestVersion)) {
				availableLanguages.add(terms.getKey().getLanguage());
				if (terms.getKey().getLanguage().equalsIgnoreCase(language)) {
					filteredAsPerLang.add(terms);
				}
			}
		}

		latestVerLangs.put(docName, availableLanguages);

		if (filteredAsPerLang.size() > 0) {
			return filteredAsPerLang.get(0);
		} else {
			return termsAndCondition.get(0);
		}

	}
}
