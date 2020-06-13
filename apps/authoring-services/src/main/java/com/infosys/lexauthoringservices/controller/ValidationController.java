package com.infosys.lexauthoringservices.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.ValidationsService;

@RestController
@RequestMapping("/action/content/validation")
public class ValidationController {

	@Autowired
	ValidationsService validationService;

	@GetMapping("/{identifier}")
	public ResponseEntity<?> getValidationNode(@PathVariable("identifier") String identifier) throws Exception {

		return new ResponseEntity<>(validationService.getValidationNode(identifier), HttpStatus.OK);
	}

	@PutMapping("/{identifier}")
	public ResponseEntity<?> putValidationNode(@PathVariable("identifier") String identifier,
			@RequestBody Map<String, Object> validationNode) throws Exception {

		validationService.putValidationNode(identifier, validationNode);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{identifierFrom}/relation/{identifierTo}")
	public ResponseEntity<?> getValidationRelation(@PathVariable("identifierFrom") String identifierFrom,
			@PathVariable("identifierTo") String identifierTo) {

		return new ResponseEntity<>(validationService.getValidationRelation(identifierFrom, identifierTo),
				HttpStatus.OK);
	}

	@PutMapping("/{identifierFrom}/relation/{identifierTo}")
	public ResponseEntity<?> createValidationRelation(@PathVariable("identifierFrom") String identifierFrom,
			@PathVariable("identifierTo") String identifierTo, @RequestBody Map<String, Object> relationMap)
			throws Exception {

		validationService.putValidationRelation(identifierFrom, identifierTo, relationMap);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
