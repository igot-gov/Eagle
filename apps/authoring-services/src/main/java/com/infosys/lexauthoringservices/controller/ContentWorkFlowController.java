package com.infosys.lexauthoringservices.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.service.ContentWorkFlowService;

@RestController
@RequestMapping("/action/content")
public class ContentWorkFlowController {

	@Autowired
	ContentWorkFlowService contentWorkFlowService;
	
	@PostMapping("/workflow")
	public ResponseEntity<Response> addToContentWorkFlow(@RequestBody Map<String, Object> requestBody)
			throws Exception {
		return new ResponseEntity<>(contentWorkFlowService.upsertNewWorkFlow(requestBody), HttpStatus.OK);
	}

	@GetMapping("/workflow")
	public ResponseEntity<Response> FetchFromContentWorkFlow(@RequestBody Map<String, Object> requestBody)
			throws Exception {
		return new ResponseEntity<>(contentWorkFlowService.fetchWorkFlowData(requestBody), HttpStatus.OK);
	}

	@DeleteMapping("/workflow")
	public ResponseEntity<Response> removeFromContentWorkFlow(@RequestBody Map<String, Object> requestBody)
			throws Exception {
		return new ResponseEntity<>(contentWorkFlowService.removeFromWorkFlow(requestBody), HttpStatus.OK);
	}
	
	
}
