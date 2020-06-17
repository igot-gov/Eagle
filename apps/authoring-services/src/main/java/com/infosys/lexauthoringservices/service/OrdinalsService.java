package com.infosys.lexauthoringservices.service;

import java.util.Map;

public interface OrdinalsService {

	Map<String, Object> getEnums(String rootOrg) throws Exception;

	String upsertMasterValue(Map<String, Object> requestMap) throws Exception;

	String updateValueToEntity(Map<String, Object> requestMap) throws Exception;
}
