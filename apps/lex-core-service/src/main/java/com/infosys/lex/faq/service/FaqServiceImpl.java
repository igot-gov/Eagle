/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datastax.driver.core.utils.UUIDs;
import com.infosys.lex.common.util.ValidLanguages;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.faq.dto.FaqDto;
import com.infosys.lex.faq.postgredb.entity.FaqGroup;
import com.infosys.lex.faq.postgredb.entity.FaqGroupPrimaryKey;
import com.infosys.lex.faq.postgredb.entity.FaqQuestion;
import com.infosys.lex.faq.postgredb.entity.FaqQuestionPrimaryKey;
import com.infosys.lex.faq.postgredb.projection.FaqGroupProjection;
import com.infosys.lex.faq.postgredb.projection.FaqQuestionProjection;
import com.infosys.lex.faq.postgredb.repository.FaqGroupRepo;
import com.infosys.lex.faq.postgredb.repository.FaqQuestionRepo;

@Service
public class FaqServiceImpl implements FaqService {

	@Autowired
	FaqQuestionRepo faqQuestionRepo;

	@Autowired
	FaqGroupRepo faqGroupRepo;

	@Autowired
	ValidLanguages validLanguages;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getQuestionAnswer(String rootOrg, String language, String groupId)
			throws Exception {

		// get all the questions
		List<FaqQuestionProjection> faqQuestions = faqQuestionRepo.findQuestion(rootOrg, language, groupId);

		List<Map<String, Object>> allQuestions = new ArrayList<>();
		for (FaqQuestionProjection faqQuestionProjection : faqQuestions) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("question", faqQuestionProjection.getQuestion());
			map.put("answer", faqQuestionProjection.getAnswer());
			map.put("group_id", faqQuestionProjection.getGroupId());
			map.put("question_seq", faqQuestionProjection.getQuestionSeq());
			allQuestions.add(map);
		}

		return allQuestions;
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getGroupId(String rootOrg, String language) {

		// get list of group_id and group_name
		List<FaqGroupProjection> group = faqGroupRepo.findGroup(rootOrg, language);

		List<Map<String, Object>> allGroups = new ArrayList<>();
		for (FaqGroupProjection faqGroupProjection : group) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("group_id", faqGroupProjection.getGroupId());
			map.put("group_name", faqGroupProjection.getGroupName());
			allGroups.add(map);

		}

		return allGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void updateQuestionAnswer(String rootOrg, String language, String groupId, List<FaqDto> faqDto) {

		// validate language
		List<String> languages = validLanguages.allowedLanguages();
		if (!languages.contains(language)) {
			throw new InvalidDataInputException("invalid.language");
		}

		List<FaqQuestion> questions = new ArrayList<>();

		// check if groupID is present
		FaqGroupPrimaryKey faqGroupPrimaryKey = new FaqGroupPrimaryKey(rootOrg, language, groupId);
		Optional<FaqGroup> oldFaqGroup = faqGroupRepo.findById(faqGroupPrimaryKey);
		if (!oldFaqGroup.isPresent()) {
			throw new InvalidDataInputException("invalid.groupId");
		}

		FaqGroup faqGroup = oldFaqGroup.get();

		// set updated on
		Date date = new Date();
		Timestamp updatedOn = new Timestamp(date.getTime());
		faqGroup.setUpdatedOn(updatedOn);

		// create list of questions for faq questions
		for (FaqDto faqs : faqDto) {

			FaqQuestionPrimaryKey faqQuestionPrimaryKey = new FaqQuestionPrimaryKey(rootOrg, language, groupId,
					faqs.getQuestionSequence());
			FaqQuestion faqQuestion = new FaqQuestion(faqQuestionPrimaryKey, faqs.getQuestion(), faqs.getAnswer());

			questions.add(faqQuestion);

			// Set updated_by in faq group
			faqGroup.setUpdatedBy(faqs.getUpdatedBy());

		}

		// insert new faq group
		faqGroupRepo.save(faqGroup);

		// delete previous questions
		faqQuestionRepo.deleteQuestion(rootOrg, language, groupId);

		// update new questions
		faqQuestionRepo.saveAll(questions);
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional
	public void deleteGroup(String rootOrg, String language, String groupId) {

		// check if id is present or not
		Optional<FaqGroup> optionalFaqGroup = faqGroupRepo.findById(new FaqGroupPrimaryKey(rootOrg, language, groupId));
		if (!optionalFaqGroup.isPresent()) {
			throw new InvalidDataInputException("invalid.GroupId");
		}

		// Set attributes for faqGroup
		FaqGroupPrimaryKey faqGroupPrimaryKey = optionalFaqGroup.get().getFaqGroupPrimaryKey();

		FaqGroup faqGroup = new FaqGroup();
		faqGroup.setFaqGroupPrimaryKey(faqGroupPrimaryKey);

		faqGroupRepo.delete(faqGroup);

		faqQuestionRepo.deleteQuestion(rootOrg, language, groupId);

	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createGroup(String rootOrg, String language, String groupName, String createdBy) {

		// validate language
		List<String> languages = validLanguages.allowedLanguages();
		if (!languages.contains(language)) {
			throw new InvalidDataInputException("invalid.language");
		}

		// check if group is already present
		Optional<FaqGroup> optionalFaq = faqGroupRepo.getGroup(rootOrg, language, groupName);

		if (optionalFaq.isPresent()) {
			throw new InvalidDataInputException("groupName.AlreadyPresent");
		}

		Date date = new Date();
		Timestamp updatedOn = new Timestamp(date.getTime());

		String groupId = UUIDs.timeBased().toString();
		FaqGroupPrimaryKey faqGroupPrimaryKey = new FaqGroupPrimaryKey(rootOrg, language, groupId);
		FaqGroup faqGroup = new FaqGroup(faqGroupPrimaryKey, groupName, updatedOn, createdBy);

		// create new group
		faqGroupRepo.save(faqGroup);
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public List<Map<String, Object>> searchText(String rootOrg, String langCode, Map<String, Object> searchMap) {

		String query = new String();
		List<Map<String, Object>> filteredQuestion = new ArrayList<>();
		// check if query is present,not null,not empty
		if (searchMap.containsKey("query") && searchMap.get("query") != null
				&& !searchMap.get("query").toString().trim().isEmpty()) {
			query = (String) searchMap.get("query");
			// Explicitly defining english for langCode en so that postgres can convert it
//			to lexemes
			String queryParam = new String();
			if (langCode.equals("en")) {
				queryParam = "english";
			} else {
				queryParam = "simple";
			}

			filteredQuestion = faqQuestionRepo.searchText(rootOrg, langCode, queryParam, query);
		} else {
			filteredQuestion = faqQuestionRepo.getAllQuestions(rootOrg, langCode);
		}

		return filteredQuestion;
	}

}
