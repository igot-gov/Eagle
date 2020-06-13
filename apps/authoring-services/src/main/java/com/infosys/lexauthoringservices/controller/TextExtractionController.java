package com.infosys.lexauthoringservices.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.TextExtractionService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
public class TextExtractionController {
	
	@Autowired
	TextExtractionService textExtractionService;
	
	@PostMapping("/action/extract/topics")
	public ResponseEntity<?> resourceTopics(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,@RequestBody Map<String, Object> requestMap) throws Exception{
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, textExtractionService.resourceTextExtraction(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
	
	//Most likely will not be used
	@PostMapping("/action/hierarchy/topics")
	public ResponseEntity<?> textBlockData(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,@RequestBody Map<String, Object> requestMap) throws Exception{
				
		Map<String,Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, textExtractionService.hierarchialExtraction(rootOrg,org,requestMap));
		return new ResponseEntity<>(responseMap,HttpStatus.OK);	
	}

}
