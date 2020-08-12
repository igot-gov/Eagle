/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.serviceimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.scoringengine.exception.BadRequestException;
import com.infosys.scoringengine.models.EvaluatorModel;
import com.infosys.scoringengine.models.Response;
import com.infosys.scoringengine.repository.cassandra.bodhi.ScoreCriteriaRepository;
import com.infosys.scoringengine.repository.cassandra.bodhi.ScoreQualifierRepository;
import com.infosys.scoringengine.repository.cassandra.bodhi.ScoreTemplate;
import com.infosys.scoringengine.schema.model.ScoringSchema;
import com.infosys.scoringengine.schema.model.ScoringTemplate;
import com.infosys.scoringengine.service.ScoringEngineService;
import com.infosys.scoringengine.util.ComputeScores;
import com.infosys.scoringengine.util.IndexerService;
import com.infosys.scoringengine.util.ScoringSchemaLoader;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScoringEngineServiceImpl implements ScoringEngineService {


	@Autowired
	ScoringSchema scoringSchema;
	@Autowired
	ScoreCriteriaRepository scoreCriteriaRepository;

	@Autowired
	ScoreQualifierRepository scoreQualifierRepository;

	@Autowired
	IndexerService indexerService;

	private ObjectMapper mapper = new ObjectMapper();
	@Value("${es.score.index}")
	private String esIndex;

	@Value("${es.score.index.type}")
	private String esIndexType;

	@Value("${scoring.template.id}")
	private String scoringTemplateId;

	@Value("${es.scoring.enabled}")
	private boolean esScoringEnabled;
	
	public static SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Logger logger = LoggerFactory.getLogger(ScoringEngineServiceImpl.class);

	@Override
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

	@Override
	public Response addV3(EvaluatorModel evaluatorModel) throws Exception{
		Response response = new Response();
		try{

			// doComputations of all fields
			ScoringTemplate scoringTemplate = null;
			if (evaluatorModel.getTemplateId() == null || evaluatorModel.getTemplateId().isEmpty()){
				scoringTemplate = scoringSchema.getScoringTemplates().stream().filter(t -> t.getTemplate_id().equals(scoringTemplateId)).findFirst().get();
			}
			ComputeScores computeScores = new ComputeScores(scoringTemplate);
			computeScores.compute(evaluatorModel);
			logger.info("evaluatorModel : {}",mapper.writeValueAsString(evaluatorModel));

			// post the data into ES index
			if(esScoringEnabled){
				Map<String, Object> indexDocument = mapper.convertValue(evaluatorModel, new TypeReference<Map<String, Object>>() {});
				RestStatus status = indexerService.addEntity(esIndex, esIndexType, evaluatorModel.getIdentifier(), indexDocument);
				response.put("status", status);
				response.put("id", evaluatorModel.getIdentifier());

			} else {
				response.put("result", evaluatorModel);
			}

			response.put("Message", "Successfully operation");

		} catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}

		return response;
	}

	@Override
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


	
}
