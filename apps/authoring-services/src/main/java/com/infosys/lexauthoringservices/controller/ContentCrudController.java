package com.infosys.lexauthoringservices.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.exception.ResourceNotFoundException;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.service.ValidationsService;
import com.infosys.lexauthoringservices.util.LexConstants;

@RestController
@RequestMapping("/action/content")
public class ContentCrudController {

	@Autowired
	ContentCrudService contentCrudService;

	@Autowired
	ValidationsService validationsService;

	@PostMapping("/create")
	public ResponseEntity<?> createContentNode(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(LexConstants.IDENTIFIER, contentCrudService.createContentNode(rootOrg, org, requestMap));
		return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
	}
	
	@PostMapping("/createMigration")
	public ResponseEntity<?> createContentNodeForMigration(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		contentCrudService.createContentNodeForMigration(rootOrg, org, requestMap);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/createMigrationV2")
	public ResponseEntity<?> createContentNodeForMigrationV2(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		String identifier = contentCrudService.createContentNodeForMigrationV2(rootOrg, org, requestMap);
		return new ResponseEntity<>(identifier,HttpStatus.CREATED);
	}
	
	@PostMapping("/createMigrationV3")
	public ResponseEntity<?> createContentNodeForMigrationV3(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		String identifier = contentCrudService.createContentNodeForMigrationV3(rootOrg, org, requestMap);
		return new ResponseEntity<>(identifier,HttpStatus.CREATED);
	}

	@GetMapping("/read/{identifier}")
	public ResponseEntity<?> getContentNode(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {

		return new ResponseEntity<>(contentCrudService.getContentNode(rootOrg, identifier), HttpStatus.OK);
	}

	@PostMapping("/update/{identifier}")
	public ResponseEntity<?> updateContentNode(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		contentCrudService.updateContentNode(rootOrg, org, identifier, requestMap);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/updateMeta/{identifier}")
	public ResponseEntity<?> updateContentMetaNode(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		contentCrudService.updateContentMetaNode(rootOrg, org, identifier, requestMap);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/delete")
	public ResponseEntity<?> contentDelete(@RequestBody Map<String, Object> requestMap,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {

		return new ResponseEntity<>(contentCrudService.contentDelete(requestMap, rootOrg,org),
				HttpStatus.OK);
	}
	
	@PostMapping("/unpublish")
	public ResponseEntity<?> contentUnpublish(@RequestBody Map<String, Object> requestMap,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {

		try {
			contentCrudService.contentUnpublish(requestMap, rootOrg,org);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		catch (ResourceNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
//	@PostMapping("/editor/delete")
//	public ResponseEntity<?> contenEditortDelete(@RequestBody Map<String, Object> requestMap,
//			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
//			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org) throws Exception {
//
//		return new ResponseEntity<>(contentCrudService.contentEditorDelete(rootOrg,org, requestMap),
//				HttpStatus.OK);
//	}
	

//	@PostMapping("/action/playlist/update/{identifier}")
//	public ResponseEntity<Response> updatePlaylistNode(@PathVariable("identifier") String identifier,
//			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
//			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys Ltd") String org,
//			@RequestBody Map<String,Object> requestMap) throws Exception{
//		contentCrudService.updatePlaylistNode(rootOrg,identifier,requestMap);
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

	@GetMapping("/hierarchy/{identifier}")
	public ResponseEntity<?> getContentHierarchy(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org)
			throws BadRequestException, Exception {

		return new ResponseEntity<>(contentCrudService.getContentHierarchy(identifier, rootOrg, org), HttpStatus.OK);
	}
	
	@GetMapping("/live/hierarchy/{identifier}")
	public ResponseEntity<?> getOnlyLiveContentHierarchy(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org)
			throws BadRequestException, Exception {

		return new ResponseEntity<>(contentCrudService.getOnlyLiveContentHierarchy(identifier, rootOrg, org), HttpStatus.OK);
	}
	
	@PostMapping("/v2/hierarchy/{identifier}")
	public Map<String,Object> getContentHierarchyV2(@PathVariable("identifier") String identifier,
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String,Object> requestMap)
			throws BadRequestException, Exception {

		Map<String,Object> hMap = contentCrudService.getContentHierarchyV2(identifier, rootOrg, org,requestMap);
		return hMap;
	}
	
	@PostMapping("/multiple/hierarchy")
	public List<Map<String,Object>> getMultipleHierarchy(@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestBody Map<String,Object> requestMap)
			throws BadRequestException, Exception {

		List<Map<String,Object>> hMap = contentCrudService.getMultipleHierarchy(rootOrg, org,requestMap);
		return hMap;
	}

//	@PostMapping("/hierarchy/fields/{identifier}")
//	public ResponseEntity<?> getContentHierarchyFields(@PathVariable("identifier") String identifier,
//			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
//			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
//			@RequestBody Map<String, Object> requestMap) throws Exception {
//
//		return new ResponseEntity<>(contentCrudService.getContentHierarchyFields(identifier, rootOrg, org, requestMap),
//				HttpStatus.OK);
//	}

	@PostMapping("/hierarchy/update")
	public ResponseEntity<Response> updateContentHierarchy(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestParam(value = "migration", defaultValue = "no") String migration,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		contentCrudService.updateContentHierarchy(rootOrg, org, requestMap, migration);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/v2/hierarchy/update")
	public ResponseEntity<Response> updateContentHierarchyV2(
			@RequestParam(value = LexConstants.ROOT_ORG, defaultValue = "Infosys") String rootOrg,
			@RequestParam(value = LexConstants.ORG, defaultValue = "Infosys Ltd") String org,
			@RequestParam(value = "migration", defaultValue = "no") String migration,
			@RequestBody Map<String, Object> requestMap) throws Exception {

		contentCrudService.updateContentHierarchyV2(rootOrg, org, requestMap, migration);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/status/change/{identifier}")
	public ResponseEntity<Response> statusChange(@PathVariable("identifier") String identifier,
			@RequestBody Map<String, Object> requestBody) throws BadRequestException, Exception {

		return new ResponseEntity<>(contentCrudService.statusChange(identifier, requestBody), HttpStatus.OK);
	}

//	@PostMapping("/action/content/external/publish/{identifier}")
//	public ResponseEntity<Response> externalContentPublish(@PathVariable("identifier") String identifier,
//			@RequestBody Map<String, Object> requestBody) throws Exception {
//
//		return new ResponseEntity<>(contentCrudService.externalContentPublish(identifier, requestBody), HttpStatus.OK);
//	}

	@PostMapping("/extend")
	public ResponseEntity<Response> extendContentExpiry(@RequestBody Map<String, Object> requestBody) throws Exception {

		return new ResponseEntity<>(contentCrudService.extendContentExpiry(requestBody), HttpStatus.OK);
	}
}
