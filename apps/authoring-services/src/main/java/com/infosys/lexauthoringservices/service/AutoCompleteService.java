package com.infosys.lexauthoringservices.service;

import java.util.List;
import java.util.Map;

public interface AutoCompleteService {

	List<Map<String, Object>> getUnits(Map<String, Object> reqMap);

	List<Map<String, Object>> getSkills(Map<String, Object> reqMap);

	List<Map<String, Object>> getClients(Map<String, Object> reqMap);

	Map<String, Object> getEnums();

	Map<String, Object> getEmail();

}
