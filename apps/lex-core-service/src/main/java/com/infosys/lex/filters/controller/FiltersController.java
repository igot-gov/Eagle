/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.filters.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.filters.dto.FiltersUpsertDTO;
import com.infosys.lex.filters.service.FiltersService;

@RestController
@CrossOrigin(origins = "*")
public class FiltersController {
	
	@Autowired
	FiltersService filtersService;
	
	@GetMapping("/filters/{language}")
	public ResponseEntity<Map<String,Object>> fetchFilters(@PathVariable("language") String language,
			@RequestHeader("rootOrg") String rootOrg, @RequestHeader("org") String org)
			throws Exception {
		return new ResponseEntity<Map<String,Object>>(filtersService
					.getAllFiltersAndValues(rootOrg, org, language), HttpStatus.OK
				);
	}
	
	@PostMapping("/filters/create")
	public ResponseEntity<Map<String, Object>> postFilters(@RequestHeader("rootOrg") String rootOrg, 
			@RequestHeader("org") String org, @RequestBody FiltersUpsertDTO filtersDTO) throws Exception {
		return new ResponseEntity<Map<String, Object>>(filtersService
					.postAllFiltersAndValues(rootOrg, org, filtersDTO), HttpStatus.CREATED
				);
	}
	
}