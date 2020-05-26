/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.language.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.language.service.LanguageService;

@RestController
@CrossOrigin("*")
public class LanguageController {

	@Autowired
	LanguageService languageService;

	/**
	 * Get language details of the root_org , lang and country
	 * @param rootOrg
	 * @param lang
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/v1/{country}/language/{lang}")
	public ResponseEntity<Map<String, Object>> getLanguages(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "lang") String lang,@PathVariable("country") String country) throws Exception {

		return new ResponseEntity<>(languageService.getLanguages(rootOrg, lang,country), HttpStatus.OK);
	}

	/**
	 * create a languge for root_org and lang
	 * @param rootOrg
	 * @param lang
	 * @param languages
	 * @return
	 */
	@PostMapping("/v1/language/{lang}")
	public ResponseEntity<?> createLanguage(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "lang") String lang, @RequestBody Map<String, Object> languages) {

		languageService.createLanguages(rootOrg, lang, languages);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	/**
	 * delete record of language and root_org
	 * @param rootOrg
	 * @param lang
	 * @return
	 */
	@DeleteMapping("/v1/language/{lang}")
	public ResponseEntity<?> deleteLanguages(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "lang") String lang) {

		languageService.deleteLanguage(rootOrg, lang);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * update the record for a root_org and lang
	 * @param rootOrg
	 * @param lang
	 * @param masterLanguage
	 * @return
	 */
	@PatchMapping("/v1/language/{lang}")
	public ResponseEntity<?> updateLanguage(@RequestHeader(value = "rootOrg") String rootOrg,
			@PathVariable(value = "lang") String lang, @RequestBody Map<String, Object> masterLanguage) {

		languageService.updateLanguage(rootOrg, lang, masterLanguage);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
