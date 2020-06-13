package com.infosys.lexauthoringservices.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.KbService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
public class KbController {

	@Autowired
	KbService kbService;
	
	@PostMapping("/action/content/kb/add")
	public ResponseEntity<?> addChildNodes(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception{
		
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put("Message", kbService.addChildren(rootOrg,org,requestMap));
		return new ResponseEntity<>(responseMap,HttpStatus.OK);
	}
	
	@PostMapping("/action/content/kb/delete")
	public ResponseEntity<?> deleteChildNodes(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception{
		
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put("Message", kbService.deleteChildren(rootOrg,org,requestMap));
		return new ResponseEntity<>(responseMap,HttpStatus.OK);
	}
	
	@DeleteMapping("/action/content/delete/{identifier}/kb")
	public ResponseEntity<?> deleteKB(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception{
		
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put("Message", kbService.deleteKbContent(rootOrg,org,identifier));
		return new ResponseEntity<>(responseMap,HttpStatus.OK);
	}
}
