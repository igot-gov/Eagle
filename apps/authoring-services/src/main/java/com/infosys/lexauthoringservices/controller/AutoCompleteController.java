package com.infosys.lexauthoringservices.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.AutoCompleteService;

@RestController
@RequestMapping("/action/meta/v1")
public class AutoCompleteController {

	@Autowired
	AutoCompleteService autoCompleteService;

	@PostMapping("/units")
	public ResponseEntity<List<Map<String, Object>>> getUnitsToBeDisplayed(@RequestBody Map<String, Object> reqMap) {

		return new ResponseEntity<>(autoCompleteService.getUnits(reqMap), HttpStatus.OK);
	}

	@PostMapping("/skills")
	public ResponseEntity<List<Map<String, Object>>> getSkillsToBeDisplayed(@RequestBody Map<String, Object> reqMap) {

		return new ResponseEntity<>(autoCompleteService.getSkills(reqMap), HttpStatus.OK);
	}

	@PostMapping("/clients")
	public ResponseEntity<List<Map<String, Object>>> getClientsToBeDisplayed(@RequestBody Map<String, Object> reqMap) {

		return new ResponseEntity<>(autoCompleteService.getClients(reqMap), HttpStatus.OK);
	}

	@GetMapping("/ordinals/list")
	public ResponseEntity<Map<String, Object>> getEnumsToBeDisplayed() {

		return new ResponseEntity<>(autoCompleteService.getEnums(), HttpStatus.OK);
	}

	@GetMapping("/ordinals/tracks")
	public ResponseEntity<Map<String, Object>> trackEntity() {

		return new ResponseEntity<Map<String, Object>>(autoCompleteService.getEmail(), HttpStatus.OK);
	}

}
