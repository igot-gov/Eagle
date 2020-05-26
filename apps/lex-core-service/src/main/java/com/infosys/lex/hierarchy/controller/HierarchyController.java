/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.hierarchy.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.hierarchy.service.HierarchyService;

@RestController
public class HierarchyController {
	
	@Autowired
	HierarchyService hierarchyService;
	
	
	/**
	 * end point to check is the application is running  
	 * @return
	 * @throws Exception 
	 * @throws BadRequestException 
	 */
	@PostMapping("/v1/content/hierarchy/{identifier}")
	public ResponseEntity<?> getHierarchy(@PathVariable String identifier,
			@RequestBody Map<String, Object> requestMap) throws BadRequestException, Exception {
		
		return new ResponseEntity<>(hierarchyService.getHierarchyOfContentNode(identifier,requestMap), HttpStatus.OK);
	}
	
	/**
	 * end point to check is the application is running  
	 * @return
	 * @throws Exception 
	 * @throws BadRequestException 
	 */
	@PostMapping("/v1/content/metas")
	public ResponseEntity<?> getMetasHierarchy(@RequestBody Map<String, Object> requestMap) throws BadRequestException, Exception {
		
		return new ResponseEntity<>(hierarchyService.getMetasHierarchy(requestMap), HttpStatus.OK);
	}
	
	
	
	

}
