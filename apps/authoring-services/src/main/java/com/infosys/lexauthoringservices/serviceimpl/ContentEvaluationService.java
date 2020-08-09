/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.CriteriaModel;
import com.infosys.lexauthoringservices.model.EvaluatorModel;
import com.infosys.lexauthoringservices.model.QualifierModel;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.cassandra.*;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ContentEvaluationRepository;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.EvaluationCriteria;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreQualifier;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreQualifierRepository;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.evaluation.ScoreCriteriaRepository;
import com.infosys.lexauthoringservices.util.ComputeScores;
import com.infosys.lexauthoringservices.util.IndexerService;
import com.infosys.lexauthoringservices.util.LexConstants;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentEvaluationService {

	private static final String CONTENT_ID = "contentId";
	private static final String USER_ID = "userId";
	private static final String EVALUATIONS = "evaluations";
	private static final String ROLE = "role";
	private static final String HEADER = "header";
	private static final String DESCRIPTION = "description";

	@Autowired
	ContentEvaluationRepository contentEvaluationRepository;

	@Autowired
	ScoreCriteriaRepository scoreCriteriaRepository;

	@Autowired
	ScoreQualifierRepository scoreQualifierRepository;

	@Autowired
	IndexerService indexerService;

	private ObjectMapper mapper = new ObjectMapper();
	@Value("${infosys.es.score.index}")
	private String esIndex;

	@Value("${infosys.es.score.index.type}")
	private String esIndexType;
	
	public static SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger logger = LoggerFactory.getLogger(ContentEvaluationService.class);


	public Response addV2(EvaluatorModel evaluatorModel) throws Exception{
		Response response = new Response();
		try{

			// doComputations of all fields
			ComputeScores computeScores = new ComputeScores(scoreCriteriaRepository, scoreQualifierRepository);
			computeScores.compute(evaluatorModel);
			logger.info("evaluatorModel : {}",mapper.writeValueAsString(evaluatorModel));

			// post the data into ES index
			Map<String, Object> indexDocument = mapper.convertValue(evaluatorModel, new TypeReference<Map<String, Object>>() {});
			RestStatus status = indexerService.addEntity(esIndex, esIndexType, evaluatorModel.getIdentifier(), indexDocument);

			response.put("status", status);
			response.put("id", evaluatorModel.getIdentifier());
			response.put("Message", "Successfully operation");

		} catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}

		return response;
	}

	public Response searchV2(EvaluatorModel evaluatorModel) throws Exception{
		Response response = new Response();
		try{

			//request all fields
			logger.info("evaluatorModel : {}",mapper.writeValueAsString(evaluatorModel));

			if ((null == evaluatorModel.getUserId() || evaluatorModel.getUserId().isEmpty()) && (null == evaluatorModel.getResourceId() || evaluatorModel.getResourceId().isEmpty())) {
				throw new BadRequestException("Required fields, userId or resourceId is not valid ");
			}
			// post the data into ES index
			Map<String, Object> searchQuery = new HashMap<>();
			searchQuery.put("userId", evaluatorModel.getUserId());
			searchQuery.put("resourceId", evaluatorModel.getResourceId());

			JsonNode searchResponse= indexerService.search(esIndex, searchQuery);

			response.put("resources", searchResponse);
			response.put("Message", "Successfully operation");

		} catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}

		return response;
	}


	public Response upsert(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		String rootOrg = null;
		String org = null;
		String contentId = null;
		String userId = null;
		String header = null;
		String description = null;
		String role = null;

		ObjectMapper mapper = new ObjectMapper();

		try {
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			contentId = (String) requestBody.get(CONTENT_ID);
			userId = (String) requestBody.get(USER_ID);
			role = (String) requestBody.get(ROLE);

			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
//			List<ContentEvaluation>  evaluationObjs = (List<ContentEvaluation>) requestBody.get(EVALUATIONS);
//			List<ContentEvaluation> pojos = mapper.convertValue(evaluationObjs, new TypeReference<List<ContentEvaluation>>() { });
//			System.out.println("pojos : "+mapper.writeValueAsString(pojos));
//			pojos.forEach(evaluation->{
//				evaluation.setDate(formatterDateTime.format(new Date()));
//				contentEvaluationRepository.save(evaluation);
//
//			});

			for(Map<String,Object> evalution: evaluations){
				header = (String) evalution.get(HEADER);
				description = (String) evalution.get(DESCRIPTION);
				Map<String, String> items = (Map) evalution.get("items");
				ContentEvaluation tableModel = new ContentEvaluation(new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header));
				tableModel.setDate(formatterDateTime.format(new Date()));
				tableModel.setItems(items);
				tableModel.setHeader(header);
				tableModel.setDescription(description);
				tableModel.setRole(role);
				System.out.println("ContentEvaluation : "+new ObjectMapper().writeValueAsString(tableModel));
				contentEvaluationRepository.save(tableModel);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		response.put("Message", "Successfully upserted data");
		return response;
	}

	public Response fetch(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		List<ContentEvaluation> contentEvaluationList = new ArrayList<>();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			String contentId = (String) requestBody.get(CONTENT_ID);
			String userId = (String) requestBody.get(USER_ID);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
			for(Map<String,Object> evalution: evaluations) {

				String header = (String) evalution.get(HEADER);
				ContentEvaluationPrimaryKey pk =new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header);
				Optional<ContentEvaluation> contentEvaluation = contentEvaluationRepository.findById(pk);
				contentEvaluationList.add(contentEvaluation.get());
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successful");
		response.put(EVALUATIONS, contentEvaluationList);
		return response;
	}

	public Response fetchByContentId(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();
		List<ContentEvaluation> contentEvaluationList = new ArrayList<>();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			String contentId = (String) requestBody.get(CONTENT_ID);
			String userId = (String) requestBody.get(USER_ID);

			contentEvaluationList.addAll(contentEvaluationRepository.findById(rootOrg, org, contentId, userId));

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successful");
		response.put(EVALUATIONS, contentEvaluationList);
		return response;
	}

	public Response delete(Map<String, Object> requestBody) throws Exception {
		Response response = new Response();

		try{
			String rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			String org = (String) requestBody.get(LexConstants.ORG);
			String contentId = (String) requestBody.get(CONTENT_ID);
			String userId = (String) requestBody.get(USER_ID);
			List<Map<String,Object>>  evaluations = (List<Map<String,Object>>) requestBody.get(EVALUATIONS);
			for(Map<String,Object> evalution: evaluations) {
				String header = (String) evalution.get(HEADER);

				ContentEvaluationPrimaryKey pk =new ContentEvaluationPrimaryKey(rootOrg, org, contentId, userId, header);
				contentEvaluationRepository.deleteById(pk);
			}

		}catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);

		}
		response.put("Message", "Successfully deleted");
		return response;
	}


	
}
