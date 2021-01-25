
package org.sunbird.scoringengine.serviceimpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.sunbird.scoringengine.exception.BadRequestException;
import org.sunbird.scoringengine.models.EvaluatorModel;
import org.sunbird.scoringengine.models.PropertyFilterMixIn;
import org.sunbird.scoringengine.models.Response;
import org.sunbird.scoringengine.repository.cassandra.bodhi.ScoreCriteriaRepository;
import org.sunbird.scoringengine.repository.cassandra.bodhi.ScoreQualifierRepository;
import org.sunbird.scoringengine.schema.model.ScoringTemplate;
import org.sunbird.scoringengine.service.ScoringEngineService;
import org.sunbird.scoringengine.util.ComputeScores;
import org.sunbird.scoringengine.util.IndexerService;
import org.sunbird.scoringengine.util.ScoringSchemaLoader;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScoringEngineServiceImpl implements ScoringEngineService {


	@Autowired
	private ScoringSchemaLoader schemaLoader;

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

	private static final String[] ignorableFields = {"rootOrg", "org", "version", "weightage", "max_score", "min_acceptable_score", "fixed_score", "range_score", "modify_max_score", "10-20", "20-30", "30-40", "modify_max_score", "max_score_modify_value", "min_score_weightage_enable", "min_score_weightage", "score_grades", "isQualifiedMinCriteria", "status_on_min_criteria"};

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
	public Response addV3(EvaluatorModel evaluatorModel) throws Exception {
		Response response = new Response();
		try {
			String templateId = (evaluatorModel.getTemplateId() == null || evaluatorModel.getTemplateId().isEmpty()) == true ? scoringTemplateId
			: evaluatorModel.getTemplateId();

			ScoringTemplate scoringTemplate = schemaLoader.getScoringSchema().getScoringTemplates().stream().filter(t -> t.getTemplate_id().equals(templateId)).findFirst().get();
			ComputeScores computeScores = new ComputeScores(scoringTemplate);
			computeScores.computeV2(evaluatorModel);
			logger.info("evaluatorModel : {}", mapper.writeValueAsString(evaluatorModel));

			// post the data into ES index
			if (esScoringEnabled) {
				Map<String, Object> indexDocument = mapper.convertValue(evaluatorModel, new TypeReference<Map<String, Object>>() {
				});
				RestStatus status = indexerService.addEntity(esIndex, esIndexType, evaluatorModel.getIdentifier(), indexDocument);
				response.put("status", status);
				response.put("id", evaluatorModel.getIdentifier());

			} else {
				response.put("result", evaluatorModel);
			}

			response.put("Message", "Successfully operation");

		} catch (Exception e) {
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
			searchQuery.put("isGetLatestRecordEnabled", evaluatorModel.isGetLatestRecordEnabled());
            if(!StringUtils.isEmpty(evaluatorModel.getIdentifier())){
				searchQuery.put("identifier", evaluatorModel.getIdentifier());
			}
			JsonNode searchResponse= indexerService.search(esIndex, searchQuery);

			response.put("resources", searchResponse);
			response.put("Message", "Successfully operation");

		} catch (Exception e){
			e.printStackTrace();
			throw new Exception(e);
		}

		return response;
	}

	@Override
	public Response getTemplate(String templateId, String rootOrg, String org) throws Exception {
		Response response = new Response();
		if (StringUtils.isEmpty(templateId)) {
			throw new BadRequestException("Template Id is required!");
		}
		ScoringTemplate scoringTemplate = schemaLoader.getScoringSchema().getScoringTemplates().stream().filter(t -> t.getTemplate_id().equals(templateId)).findFirst().get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(Object.class, PropertyFilterMixIn.class);
		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",SimpleBeanPropertyFilter.serializeAllExcept(ignorableFields));
		ObjectWriter writer = mapper.writer(filters);
		response.put("result", mapper.readValue(writer.writeValueAsString(scoringTemplate), Object.class));
		response.put("Message", "Successfully operation");
		response.put("resources", HttpStatus.OK);
		return response;
	}


}
