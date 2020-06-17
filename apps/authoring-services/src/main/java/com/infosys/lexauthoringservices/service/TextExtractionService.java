package com.infosys.lexauthoringservices.service;

import java.util.Map;
import com.infosys.lexauthoringservices.model.Response;

public interface TextExtractionService {

	public Response resourceTextExtraction(String rootOrg,String org, Map<String,Object> reqMap) throws Exception;

	public Response hierarchialExtraction(String rootOrg, String org, Map<String, Object> requestMap) throws Exception;
}
