package com.infosys.lexauthoringservices.service;

import java.util.List;
import java.util.Map;

import com.infosys.lexauthoringservices.exception.BadRequestException;

public interface ConfigurableContentHierarchyService {
	
	List<Map<String,Object>> getAllContentHierarchy(String rootOrg) throws BadRequestException,Exception;

	List<Map<String, Object>> getContentHierarchyForContentType(String rootOrg, String contentType) throws BadRequestException,Exception;;
}
