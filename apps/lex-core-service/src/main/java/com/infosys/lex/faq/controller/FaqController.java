/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.faq.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.faq.dto.FaqDto;
import com.infosys.lex.faq.service.FaqService;

@RestController
@CrossOrigin("*")
public class FaqController {

	@Autowired
	FaqService faqService;

	/**
	 * This function return all the question and answer of a group and a langCode
	 * 
	 * @return list of all the question and answer of a group
	 * @throws Exception
	 */
	@GetMapping("/v1/faq-group/{groupId}/questions")
	public ResponseEntity<List<Map<String, Object>>> getQuestionAnswer(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode, @PathVariable("groupId") String groupId) throws Exception {

		return new ResponseEntity<List<Map<String, Object>>>(faqService.getQuestionAnswer(rootOrg, langCode, groupId),
				HttpStatus.OK);
	}

	/**
	 * Returns all the groupIds of a langCode
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/faq-group")
	public ResponseEntity<List<Map<String, Object>>> getGroupId(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode) throws Exception {

		return new ResponseEntity<List<Map<String, Object>>>(faqService.getGroupId(rootOrg, langCode), HttpStatus.OK);

	}

	/**
	 * Updates all the question and answers of a group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/v1/faq-group/{groupId}/questions")
	public ResponseEntity<?> updateQuestionAnswer(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode, @PathVariable("groupId") String groupId,
			@Valid @RequestBody List<FaqDto> faqDto) throws Exception {
		faqService.updateQuestionAnswer(rootOrg, langCode, groupId, faqDto);
		return new ResponseEntity<>(HttpStatus.CREATED);

	}

	/**
	 * Deletes the group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("/v1/faq-group/{groupId}")
	public ResponseEntity<?> deleteGroup(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode, @PathVariable("groupId") String groupId) {

		faqService.deleteGroup(rootOrg, langCode, groupId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	/**
	 * creates a new group
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param groupName
	 * @param createdBy
	 * @return
	 */
	@PutMapping("/v1/faq-group/{groupName}")
	public ResponseEntity<?> createGroup(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode, @PathVariable("groupName") String groupName,
			@RequestParam("created_by") String createdBy) {

		faqService.createGroup(rootOrg, langCode, groupName, createdBy);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * Searches for a query in all groups of a language
	 * 
	 * @param rootOrg
	 * @param langCode
	 * @param searchMap
	 * @return
	 */
	@GetMapping("/v1/faq-group/search")
	public ResponseEntity<List<Map<String, Object>>> searchText(@RequestHeader("rootOrg") String rootOrg,
			@RequestHeader("langCode") String langCode, @RequestBody Map<String, Object> searchMap) {

		return new ResponseEntity<List<Map<String, Object>>>(faqService.searchText(rootOrg, langCode, searchMap),
				HttpStatus.OK);

	}
}
