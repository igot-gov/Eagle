package com.infosys.lexauthoringservices.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.Response;

public interface ContentCrudService {

	String createContentNode(String rootOrg, String org, Map<String, Object> requestMap) throws Exception;

	void createContentNodeForMigration(String rootOrg, String org, Map<String, Object> requestMap) throws Exception;
	
	String createContentNodeForMigrationV2(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException,Exception;

	void updateContentNode(String rootOrg, String org, String identifier, Map<String, Object> requestMap) throws Exception;

	Map<String, Object> getContentNode(String rootOrg, String identifier) throws Exception;

	Map<String, Object> getContentHierarchy(String identifier, String rootOrg, String org) throws BadRequestException, Exception;
	
	Map<String, Object> getOnlyLiveContentHierarchy(String identifier, String rootOrg, String org) throws BadRequestException, Exception;

	void updateContentHierarchy(String rootOrg, String org, Map<String, Object> requestMap, String migration)throws Exception;

//	Response publishContent(String identifier, String rootOrg, String org, String creatorEmail) throws Exception;

	Response statusChange(String identifier, Map<String, Object> requestBody) throws BadRequestException, Exception;

	Map<String, String> contentDelete(Map<String,Object> requestMap, String rootOrg,String org)
			throws Exception;
	
//	Map<String,Object> contentEditorDelete(String rootOrg,String org,Map<String,Object> requestMap) throws Exception;

//	Response externalContentPublish(String identifier, Map<String, Object> requestBody) throws Exception;

//	Map<String, Object> getContentHierarchyFields(String identifier, String rootOrg, String org,
//			Map<String, Object> requestMap) throws JsonParseException, JsonMappingException, IOException, Exception;

//	Response updatePlaylistNode(String rootOrg, String identifier, Map<String, Object> requestMap)throws Exception;

	Response extendContentExpiry(Map<String, Object> requestBody) throws Exception;

	void updateContentMetaNode(String rootOrg, String org, String identifier, Map<String, Object> requestMap) throws Exception;

	Map<String, Object> getContentHierarchyV2(String identifier, String rootOrg, String org, Map<String,Object> requestMap)
			throws BadRequestException, Exception;

	void contentUnpublish(Map<String, Object> requestMap, String rootOrg, String org) throws BadRequestException, Exception;

	List<Map<String, Object>> getMultipleHierarchy(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException, Exception;

	String createContentNodeForMigrationV3(String rootOrg, String org, Map<String, Object> requestMap)throws BadRequestException,Exception;

	void updateContentHierarchyV2(String rootOrg, String org, Map<String, Object> requestMap, String migration) throws Exception;

}
