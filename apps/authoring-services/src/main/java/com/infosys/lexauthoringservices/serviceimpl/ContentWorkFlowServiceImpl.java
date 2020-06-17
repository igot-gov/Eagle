package com.infosys.lexauthoringservices.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.model.cassandra.ContentWorkFlowModel;
import com.infosys.lexauthoringservices.model.cassandra.ContentWorkFlowPrimaryKeyModel;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ContentWorkFlowRepository;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.service.ContentWorkFlowService;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class ContentWorkFlowServiceImpl implements ContentWorkFlowService{

	@Autowired
	ContentWorkFlowRepository contentWorkFlowRepo;
	
	@Autowired
	ContentCrudService contentCrudService;
	
	@Autowired
	GraphService graphService;
	
	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	@SuppressWarnings("unchecked")
	@Override
	public Response upsertNewWorkFlow(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		String contentType = null;
		List<String> workFlow = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		List<String> workFlowOperations = new ArrayList<>();

		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			contentType = (String) requestBody.get(LexConstants.CONTENT_TYPE);
			workFlow = (List<String>) requestBody.get(LexConstants.WORK_FLOW);
			if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || contentType == null
					|| contentType.isEmpty() || workFlow == null || workFlow.isEmpty()) {
				throw new BadRequestException("Invalid Request Body");
			}
			for (String workFlowObj : workFlow) {
				Map<String, Object> flowOperation = new HashMap<>();
				List<String> operationsArray = new ArrayList<>();
				operationsArray.add("validations");
				flowOperation.put(workFlowObj, operationsArray);
				workFlowOperations.add(mapper.writeValueAsString(flowOperation));
			}
			ContentWorkFlowPrimaryKeyModel primaryKey = new ContentWorkFlowPrimaryKeyModel(rootOrg, org, contentType);
			ContentWorkFlowModel tableModel = new ContentWorkFlowModel(workFlow, workFlowOperations, primaryKey);
			contentWorkFlowRepo.save(tableModel);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		response.put("Message", "Successfully upserted data");
		return response;
	}

	@Override
	public Response removeFromWorkFlow(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		String contentType = null;
		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			contentType = (String) requestBody.get(LexConstants.CONTENT_TYPE);
			if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || contentType == null
					|| contentType.isEmpty()) {
				throw new BadRequestException("Invalid Request Body");
			}
			ContentWorkFlowPrimaryKeyModel primaryKey = new ContentWorkFlowPrimaryKeyModel(rootOrg, org, contentType);
			contentWorkFlowRepo.deleteById(primaryKey);
		} catch (Exception e) {
			throw e;
		}
		response.put("Message", "Successfully deleted data");
		return response;
	}

	@Override
	public Response fetchWorkFlowData(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		List<Map<String, Object>> resultList = new ArrayList<>();
		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty()) {
				throw new BadRequestException("Invalid Request Body");
			}
			List<ContentWorkFlowModel> casResults = contentWorkFlowRepo.findAllByRootOrg(rootOrg, org);
			for (ContentWorkFlowModel modelObj : casResults) {
				List<String> contentWorkFlow = modelObj.getContent_work_flow();
				ContentWorkFlowPrimaryKeyModel primaryModelObj = modelObj.getContentWorkFlowPrimaryKeyModel();
				String contentType = primaryModelObj.getContentType();
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put(LexConstants.ROOT_ORG_CASSANDRA, rootOrg);
				resultMap.put(LexConstants.ORG, org);
				resultMap.put(LexConstants.CONTENT_TYPE, contentType);
				resultMap.put(LexConstants.WORK_FLOW, contentWorkFlow);
				resultList.add(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		response.put("data", resultList);
		return response;
	}

	
}
