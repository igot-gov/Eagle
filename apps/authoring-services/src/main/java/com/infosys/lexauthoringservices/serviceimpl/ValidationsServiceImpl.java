package com.infosys.lexauthoringservices.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.elasticsearch.ResourceNotFoundException;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.ApplicationLogicError;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.service.ValidationsService;
import com.infosys.lexauthoringservices.util.AuthoringUtil;
import com.infosys.lexauthoringservices.util.LexConstants;
import com.infosys.lexauthoringservices.util.LexLogger;
import com.infosys.lexauthoringservices.util.LexServerProperties;
import com.infosys.lexauthoringservices.validation.ValidatorV2;
import com.infosys.lexauthoringservices.validation.model.Path;
import com.infosys.lexauthoringservices.validation.model.Paths;

@Service
public class ValidationsServiceImpl implements ValidationsService {

	private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.USE_LONG_FOR_INTS, true);

	@Autowired
	GraphService graphService;
	// test push
	@Autowired
	Driver neo4jDriver;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private LexLogger logger;

	@Autowired
	ContentCrudServiceImpl contentCrud;

	@Override
	public Map<String, Object> getValidationNode(String identifier) throws Exception {

		Session session = neo4jDriver.session();
		Statement statement = new Statement("match (node:Validation{identifier:'" + identifier + "'}) return node");

		StatementResult result = session.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			throw new ResourceNotFoundException("Validation node with " + identifier + " does not exist");
		}
		Record record = records.get(0);

		Map<String, Object> validationNode = record.get("node").asMap();

		validationNode = convertToHashMap(validationNode);
		validationNode.put("validateHere", objectMapper.readValue(validationNode.get("validateHere").toString(),
				new TypeReference<List<Map<String, Object>>>() {
				}));

		return validationNode;
	}

	@Override
	public void putValidationNode(String identifier, Map<String, Object> validationNode) throws Exception {

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		try {
			validationNode.put("validateHere", objectMapper.writeValueAsString(validationNode.get("validateHere")));
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("data", validationNode);

			Statement fetchStatement = new Statement(
					"match (node:Validation{identifier:'" + identifier + "'}) return node", paramMap);

			StatementResult result = transaction.run(fetchStatement);
			List<Record> records = result.list();

			if (records == null || records.isEmpty()) {
				Statement createStatement = new Statement("create (node:Validation $data)", paramMap);
				transaction.run(createStatement);
				transaction.commitAsync().toCompletableFuture().get();
			} else {
				Statement putStatement = new Statement(
						"match(node:Validation{identifier:'" + identifier + "'}) set node=$data", paramMap);
				transaction.run(putStatement);
				transaction.commitAsync().toCompletableFuture().get();
			}
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}

//	public static void main(String args[]) throws Exception {
//
//		Session session = neo4jDriver.session();
//
//		Transaction transaction = session.beginTransaction();
//
//		ContentNode contentNode = new GraphServiceImpl().getNodeByUniqueIdV3("Infosys", "lex_49750091094696960",
//				transaction);
//
//		contentNode.getMetadata().put("children", new ArrayList<>());
//		contentNode.getMetadata().put("resourceType", "Certification");
//		contentNode.getMetadata().put("passPercentage", 10L);
//		System.out.println(new ValidationsServiceImpl().validationsV2("Infosys", contentNode.getMetadata()));
//	}

	@Override
	public Map<String, Object> getValidationRelation(String startNodeId, String endNodeId) {

		Session session = neo4jDriver.session();

		Map<String, Object> relationMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {

			@Override
			public Map<String, Object> execute(Transaction tx) {

				Statement statement = new Statement("match (node:Validation{identifier:'" + startNodeId
						+ "'})-[r]->(node1:Validation{identifier:'" + endNodeId + "'}) return r");

				StatementResult result = tx.run(statement);
				return result.list().get(0).get("r").asRelationship().asMap();
			}
		});

		session.close();
		return relationMap;
	}

	@Override
	public void putValidationRelation(String startNodeId, String endNodeId, Map<String, Object> relationMap)
			throws Exception {

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("data", relationMap);

		try {

			Statement statement = new Statement("match (node:Validation{identifier:'" + startNodeId
					+ "'})-[r]->(node1:Validation{identifier:'" + endNodeId + "'}) set r=$data", paramMap);

			statement = new Statement(
					"match (node:Validation{identifier:'" + startNodeId + "'}), (node1:Validation{identifier:'"
							+ endNodeId + "'}) create (node)-[rel:traversal]->(node1) set rel=$data",
					paramMap);

			transaction.run(statement);

			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
		} finally {
			transaction.close();
			session.close();
		}
	}

	public void validations(Paths paths, Map<String, Object> contentMeta, Node startNode,
			Set<String> validatedProperties, List<Map<String, Object>> toValidateMaps)
			throws JsonParseException, JsonMappingException, IOException {

		System.out.println(startNode);
		ListValue validationsBelows = (ListValue) startNode.get("validateBelow");

		for (Object validateBelow : validationsBelows.asList()) {
			//gets endNode based on relation route value
			Node endNode = paths.getEndNode(startNode,
					validateBelow.toString() + "_" + contentMeta.get(validateBelow.toString()));

			if (endNode != null) {
				validations(paths, contentMeta, endNode, validatedProperties, toValidateMaps);
			}
		}

		List<Map<String, Object>> validateMaps = objectMapper.readValue(
				startNode.asMap().get("validateHere").toString(), new TypeReference<List<Map<String, Object>>>() {
				});

		// pass the end(leaf) node "validationsHere" stringified Json to function
		validate(validateMaps, contentMeta, validatedProperties, toValidateMaps);
	}

	public void validate(List<Map<String, Object>> validateMaps, Map<String, Object> contentMeta, Set<String> validated,
			List<Map<String, Object>> toValidateMaps) {

		// function checks if the provided property has already been validated before
		// if not sends to another function multiple validations on same property is
		// allowed for validated here of one node, it will be ignored for other nodes.
		Set<String> validatedProperties = new HashSet<>();

		for (Map<String, Object> validateMap : validateMaps) {
			if (!validated.contains(validateMap.get("property").toString())) {
				toValidateMaps.add(validateMap);
				validatedProperties.add(validateMap.get("property").toString());
			}
		}
		// add validated properties after validations
		validated.addAll(validatedProperties);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Set<String>> contentHierarchyValidations(String rootOrg, Map<String, Object> contentMeta)
			throws Exception {

		Paths paths = getHierarchy();
		Node startNode = paths.getNodeById("val_node_" + rootOrg.toLowerCase());

		Queue<Map<String, Object>> contentQueue = new LinkedList<>();
		contentQueue.add(contentMeta);

		Map<String, Set<String>> errors = new HashMap<>();
		while (!contentQueue.isEmpty()) {
			Set<String> validatedProperties = new HashSet<>();
			List<Map<String, Object>> toValidateMaps = new ArrayList<>();

			Map<String, Object> contentToValidate = contentQueue.poll();
			List<Map<String, Object>> childrenMeta = (List<Map<String, Object>>) contentToValidate
					.getOrDefault(LexConstants.CHILDREN, new ArrayList<>());

			for (Map<String, Object> childMeta : childrenMeta) {
				if (AuthoringUtil.haveSameAuthors(contentMeta, childMeta)) {
					contentQueue.add(childMeta);
				}
			}
			
			//based on contentToValidate it traverses the validation graph and gets corresponding validations
			validations(paths, contentToValidate, startNode, validatedProperties, toValidateMaps);
			Set<String> contentErrors = new HashSet<>();

			// finding out validations conflict for the given content.
			transcodeValidation(rootOrg, contentToValidate, contentErrors);
			questionsValidation(rootOrg,contentToValidate,contentErrors);
			//run validations only if content is not 'Live'
			if(!contentToValidate.get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {
				for (Map<String, Object> validateMap : toValidateMaps) {
					ValidatorV2.validate(contentToValidate, validateMap, contentErrors);
				}
				if (contentErrors != null && !contentErrors.isEmpty()) {
					errors.put(contentToValidate.get(LexConstants.IDENTIFIER).toString(), contentErrors);
				}
			}
			else {
				logger.info("Skipping validations because content is already 'Live' for content : " + contentToValidate.get(LexConstants.IDENTIFIER));
			}
		}
		return errors;
	}

	@SuppressWarnings("unchecked")
	private void questionsValidation(String rootOrg, Map<String, Object> contentToValidate, Set<String> contentErrors) {
		
		String mimeType = (String) contentToValidate.get(LexConstants.MIME_TYPE);
		
		if(mimeType.equals(LexConstants.MIME_TYPE_QUIZ)) {
			String resourceType = (String) contentToValidate.get(LexConstants.RESOURCE_TYPE);
			if(resourceType.equals(LexConstants.QUIZ)) {
				//TODO call for rootOrg and see number of applicable questions
				String dataUrl = lexServerProps.getContentServiceUrl()+"/contentv3/download/";
				String artifactUrl = (String) contentToValidate.get(LexConstants.ARTIFACT_URL);
				String[] values = artifactUrl.split("/");
				dataUrl=dataUrl+values[4];
				for(int i=5;i<values.length;i++) {
					dataUrl = dataUrl + "%2F" + values[i];
				}
				
				Map<String,Object> dataMap = restTemplate.getForObject(dataUrl, HashMap.class);
				List<Map<String,Object>> questions = (List<Map<String, Object>>) dataMap.get("questions");
				if(questions.size()<1) {
					contentErrors.add("Quiz should contain more than 1 quiz question(s)");
				}
			}
			else if(resourceType.equals(LexConstants.ASSESSMENT)) {
				//TODO call for rootOrg and see number of applicable questions
				String dataUrl = lexServerProps.getContentServiceUrl()+"/contentv3/download/";
				String artifactUrl = (String) contentToValidate.get(LexConstants.ARTIFACT_URL);
				String[] values = artifactUrl.split("/");
				dataUrl=dataUrl+values[4];
				for(int i=5;i<values.length;i++) {
					dataUrl = dataUrl + "%2F" + values[i];
				}
				
				Map<String,Object> dataMap = restTemplate.getForObject(dataUrl, HashMap.class);
				List<Map<String,Object>> questions = (List<Map<String, Object>>) dataMap.get("questions");
				if(questions.size()<10) {
					contentErrors.add("Assessment should contain more than 10 quiz question(s)");
				}
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private void transcodeValidation(String rootOrg, Map<String, Object> contentMeta, Set<String> contentErrors)
			throws Exception {

		String mimeType = (String) contentMeta.getOrDefault(LexConstants.MIME_TYPE, "");
		if (mimeType == null || mimeType.isEmpty()) {
			contentErrors.add("MimeType must not be null");
		}

		if (mimeType.equalsIgnoreCase("application/x-mpegurl")) {
			Map<String, Object> transcodeMap = (Map<String, Object>) contentMeta.getOrDefault(LexConstants.TRANSCODING,
					new HashMap<>());
			String TranscodeStatus = (String) transcodeMap.getOrDefault(LexConstants.TRANSCODE_STATUS, "NA");
			if (TranscodeStatus == null || !TranscodeStatus.equalsIgnoreCase("completed")) {
				contentErrors.add(
						"Transcoding is not complete please try again after sometime or try re-uploading the video");
			}
		}
		if (mimeType.equalsIgnoreCase("video/mp4")) {

			String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			identifier = identifier.replace(".img", "");
			String transcodeStatus = null;

			if (contentMeta.containsKey(LexConstants.TRANSCODING)) {
				Map<String, Object> transcodeMap = (Map<String, Object>) contentMeta.get(LexConstants.TRANSCODING);
				transcodeStatus = (String) transcodeMap.get(LexConstants.TRANSCODE_STATUS);
			} else {
				throw new ApplicationLogicError("Meta is Corrupt no transcode map present");
			}

			if (transcodeStatus == null) {

				// trigger endpoint
				String endPoint = lexServerProps.getContentServiceUrl() + "/contentv3/video-transcoding/start/@id";
				endPoint = endPoint.replace("@id", identifier);

				String urlFromMeta = (String) contentMeta.get(LexConstants.ARTIFACT_URL);
				if (urlFromMeta == null || urlFromMeta.isEmpty()) {
					throw new ApplicationLogicError("Invalid artifactUrl for identifier : " + identifier);
				}

				// creating artifactUrl reqd to trigger transcode job
				String[] splitValues = urlFromMeta.split("/");
				String fileNameWithExt = splitValues[splitValues.length - 1];
				fileNameWithExt = fileNameWithExt.split("\\?")[0];
				String artifactUrl = "http://private-content-service/contentv3/download/"
						+ lexServerProps.getContentUrlPart() + "%2FPublic%2F" + identifier + "%2Fassets%2F"
						+ fileNameWithExt;

				// rest call json to trigger transcoding
				Map<String, Object> restCallMap = new HashMap<>();
				restCallMap.put("authArtifactURL", artifactUrl);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(restCallMap, headers);

				// rest call made, validation message added
				// ListenableFuture<ResponseEntity<String>> futureEntity =
				// (ListenableFuture<ResponseEntity<String>>) restTemplate.postForEntity(
				// endPoint, request , String.class );
				logger.info("Triggering Transcode for : " + contentMeta.get(LexConstants.IDENTIFIER)
						+ " with artifactUrl : " + artifactUrl);
				ResponseEntity<String> response = restTemplate.postForEntity(endPoint, request, String.class);
				System.out.println(response.getStatusCodeValue());
				logger.info("Transcode call response status code : " + response.getStatusCodeValue()
						+ " Transcode message : " + response.getBody());
				if (response.getStatusCodeValue() == 200) {
					contentCrud.updateTranscodeMap(rootOrg, (String) contentMeta.get(LexConstants.IDENTIFIER));
				}
			}
			contentErrors.add("Video Processing has been initiated. Try again in a few hours");
		}
	}

	public Paths getHierarchy() {

		List<Path> paths = new ArrayList<>();

		try (Session session = neo4jDriver.session()) {
//			Statement statement = new Statement("MATCH (n:Validation)-[r]->(n1:Validation) return n,r,n1");
			Statement statement = new Statement(
					"match(n:Validation) optional match(n)-[r]->(n1:Validation) return n,r,n1");
			StatementResult result = session.run(statement);

			while (result.hasNext()) {
				Record record = result.next();
				Node startNode = record.get("n").asNode();

				Relationship relation = null;
				if (!record.get("r").isNull()) {
					relation = record.get("r").asRelationship();
				}
				Node endNode =null;
				if (!record.get("n1").isNull()) {
					 endNode = record.get("n1").asNode();
				}
				paths.add(new Path(startNode, endNode, relation));
			}
			return new Paths(paths);
		}
	}

	@Override
	public void validateMetaFields(String rootOrg, Map<String, Object> contentMeta) throws Exception {
		// all valid fields for given root_org
		Set<String> validFields = getRequiredFieldsForRootOrg(rootOrg);

		// used to validate whether the fields given in request are valid or not
		for (String metaKey : contentMeta.keySet()) {
			if (!validFields.contains(metaKey)) {
				throw new BadRequestException("Invalid meta field " + metaKey);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getRequiredFieldsForRootOrg(String rootOrg) throws Exception {
		Session session = neo4jDriver.session();
		List<String> validFields = new ArrayList<>();
		Statement statement = new Statement(
				"match (node:Validation{identifier:'val_" + rootOrg.toLowerCase() + "_node'}) return node");
		StatementResult result = session.run(statement);
		List<Record> records = result.list();
		if (records == null || records.isEmpty() || records.size() > 1) {
			throw new Exception("zero or multiple validation node found for " + rootOrg);
		}
		Record record = records.get(0);
		validFields = (List<String>) record.get("node").asNode().asMap().get("validFields");
		session.close();
		return new HashSet<>(validFields);
	}

	/**
	 * converts unModifieableMap returned from neo4j to HashMap.
	 * 
	 * @param unModifieableMap
	 * @return
	 */
	public static Map<String, Object> convertToHashMap(Map<String, Object> unModifieableMap) {

		Map<String, Object> contentMeta = new HashMap<>();

		unModifieableMap.entrySet().forEach(entry -> contentMeta.put(entry.getKey(), entry.getValue()));

		return contentMeta;
	}

}
