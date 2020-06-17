package com.infosys.lexauthoringservices.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.FeatureAccessControlService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
@RequestMapping("/action/feature")
public class FeatureAccessControlController {

	@Autowired
	FeatureAccessControlService featureService;

	@PostMapping("/create")
	public ResponseEntity<?> createFeatureNode(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.createFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
	}

	@GetMapping("/read/{identifier}")
	public ResponseEntity<?> GetFeatureNode(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@PathVariable("identifier") String identifier) throws Exception {
		return new ResponseEntity<>(featureService.getFeature(rootOrg, identifier), HttpStatus.OK);
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateFeatureNode(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.updateFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteFeatureNode(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.deleteFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping("/fetch/all")
	public ResponseEntity<?> fetchAllNodes(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {

		return new ResponseEntity<>(featureService.fetchAllData(rootOrg, org), HttpStatus.OK);

	}
	
	@PostMapping("/add/roles")
	public ResponseEntity<?> addRolesFeatureNode(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.addRolesFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/remove/roles")
	public ResponseEntity<?> removeRolesFeatureNode(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.removeRolesFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/add/groups")
	public ResponseEntity<?> addGroupsFeatureNode(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.addGroupsFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}
	
	@PostMapping("/remove/groups")
	public ResponseEntity<?> removeGroupsFeatureNode(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, featureService.removeGroupsFeatureNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

}
