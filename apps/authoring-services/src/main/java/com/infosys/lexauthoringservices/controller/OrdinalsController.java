package com.infosys.lexauthoringservices.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.OrdinalsService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
@RequestMapping("/action/meta/v2")
public class OrdinalsController {
	
	@Autowired
	OrdinalsService ordinalsService;
	
	@GetMapping("/ordinals/list")
	public ResponseEntity<Map<String, Object>> getEnumsToBeDisplayed(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg) throws Exception {
		return new ResponseEntity<>(ordinalsService.getEnums(rootOrg), HttpStatus.OK);
	}
	
	@PostMapping("/upsert/ordinals")
	public ResponseEntity<String> upsertMasterValue(@RequestBody Map<String,Object> requestMap) throws Exception{
		return new ResponseEntity<>(ordinalsService.upsertMasterValue(requestMap),HttpStatus.OK);
	}
	
	@PostMapping("/action/entity")
	public ResponseEntity<String> addNewValueToEntity(@RequestBody Map<String,Object> requestMap) throws Exception{
		return new ResponseEntity<>(ordinalsService.updateValueToEntity(requestMap),HttpStatus.OK);
	}
}
