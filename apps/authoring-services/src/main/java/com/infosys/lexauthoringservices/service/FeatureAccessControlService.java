package com.infosys.lexauthoringservices.service;

import java.util.List;
import java.util.Map;

import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.Response;

public interface FeatureAccessControlService {

	Map<String,Object> getFeature(String rootOrg,String identifier) throws Exception;
	
	String createFeatureNode(String rootOrg,String org, Map<String,Object> requestBody) throws Exception;
	
	Response updateFeatureNode(String rootOrg,String org, Map<String,Object> requestBody) throws Exception;
	
	Response deleteFeatureNode(String rootOrg,String org, Map<String,Object> requestBody) throws Exception;

	List<Map<String,Object>> fetchAllData(String rootOrg, String org)throws Exception;

	String addRolesFeatureNode(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException, Exception;

	String removeRolesFeatureNode(String rootOrg, String org, Map<String, Object> requestMap)  throws BadRequestException, Exception;

	String addGroupsFeatureNode(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException, Exception;

	String removeGroupsFeatureNode(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException, Exception;
	
	
}
