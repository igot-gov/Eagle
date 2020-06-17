package com.infosys.lexauthoringservices.service;

import java.util.Map;

public interface KbService {
	
	String addChildren(String rootOrg,String org, Map<String,Object> requestMap) throws Exception;
	
	String deleteChildren(String rootOrg,String org,Map<String,Object> requestMap) throws Exception;

	String deleteKbContent(String rootOrg, String org, String identifier) throws Exception;

}
