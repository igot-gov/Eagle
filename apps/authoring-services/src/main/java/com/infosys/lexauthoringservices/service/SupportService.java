package com.infosys.lexauthoringservices.service;

import java.util.Map;

public interface SupportService {

	String recreateNode(String rootOrg, String org, Map<String, Object> requestMap) throws Exception;

}
