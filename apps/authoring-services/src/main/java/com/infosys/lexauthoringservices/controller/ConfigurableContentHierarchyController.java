package com.infosys.lexauthoringservices.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.ConfigurableContentHierarchyService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
@RequestMapping("/action/content/auth/hierarchy")
public class ConfigurableContentHierarchyController {
	
	@Autowired
	ConfigurableContentHierarchyService configurableContentHierarchyService;
	
	@GetMapping("/get/all")
	public ResponseEntity<?> getAllContentHierarchy(@RequestParam (value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
	@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {
		
		return new ResponseEntity<>(configurableContentHierarchyService.getAllContentHierarchy(rootOrg),HttpStatus.OK);
		
	}
	
	@GetMapping("/{content_type}/get")
	public ResponseEntity<?> getAllContentHierarchy(@PathVariable("content_type") String contentType,@RequestParam (value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
	@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {
		
		return new ResponseEntity<>(configurableContentHierarchyService.getContentHierarchyForContentType(rootOrg,contentType),HttpStatus.OK);
		
	}

}
