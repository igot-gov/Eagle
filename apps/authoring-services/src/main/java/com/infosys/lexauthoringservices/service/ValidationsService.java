package com.infosys.lexauthoringservices.service;

import java.util.Map;
import java.util.Set;

public interface ValidationsService {

//	Map<String, Set<String>> validations(List<Map<String, Object>> contentMetas)
//			throws JsonParseException, JsonMappingException, IOException;

//	Set<String> validations(Map<String, Object> contentMeta)
//			throws JsonParseException, JsonMappingException, IOException;

	Map<String, Set<String>> contentHierarchyValidations(String rootOrg, Map<String, Object> contentMeta) throws Exception;

	Set<String> getRequiredFieldsForRootOrg(String rootOrg) throws Exception;

	Map<String, Object> getValidationNode(String identifier) throws Exception;

	void putValidationNode(String identifier, Map<String, Object> validationNode) throws Exception;

	void validateMetaFields(String rootOrg, Map<String, Object> contentMeta) throws Exception;

	Map<String, Object> getValidationRelation(String startNodeId, String endNodeId);

	void putValidationRelation(String startNodeId, String endNodeId, Map<String, Object> relationMap) throws Exception;

}
