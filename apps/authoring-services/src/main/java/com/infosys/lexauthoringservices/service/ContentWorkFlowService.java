package com.infosys.lexauthoringservices.service;

import java.util.Map;

import com.infosys.lexauthoringservices.model.Response;

public interface ContentWorkFlowService {

	Response upsertNewWorkFlow(Map<String, Object> requestBody) throws Exception;

	Response removeFromWorkFlow(Map<String, Object> requestBody) throws Exception;

	Response fetchWorkFlowData(Map<String, Object> requestBody) throws Exception;

	

}
