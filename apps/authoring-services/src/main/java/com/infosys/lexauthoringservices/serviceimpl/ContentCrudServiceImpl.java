package com.infosys.lexauthoringservices.serviceimpl;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.ApplicationLogicError;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.exception.ConflictErrorException;
import com.infosys.lexauthoringservices.exception.ResourceNotFoundException;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.cassandra.ContentWorkFlowModel;
import com.infosys.lexauthoringservices.model.cassandra.User;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.model.neo4j.Relation;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.AppConfig;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.AppConfigPrimaryKey;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.AppConfigRepository;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.ContentWorkFlowRepository;
import com.infosys.lexauthoringservices.repository.cassandra.sunbird.UserRepository;
import com.infosys.lexauthoringservices.service.ConfigurableContentHierarchyService;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.service.ValidationsService;
import com.infosys.lexauthoringservices.util.GraphUtil;
import com.infosys.lexauthoringservices.util.LexConstants;
import com.infosys.lexauthoringservices.util.LexLogger;
import com.infosys.lexauthoringservices.util.LexServerProperties;
import com.infosys.lexauthoringservices.util.PIDConstants;
import com.rockymadden.stringmetric.similarity.RatcliffObershelpMetric;

@Service
public class ContentCrudServiceImpl implements ContentCrudService {

	private static AtomicInteger atomicInteger = new AtomicInteger();

	@Autowired
	GraphService graphService;

	@Autowired
	ValidationsService validationsService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	AppConfigRepository appConfigRepo;

	@Autowired
	ContentWorkFlowRepository contentWorkFlowRepo;

	@Autowired
	LexServerProperties lexServerProps;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Driver neo4jDriver;

	@Autowired
	private LexLogger logger;
	
	@Autowired
	ConfigurableContentHierarchyService contentTypeHierarchyService;

	public static SimpleDateFormat inputFormatterDate = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

	public static List<String> master_locale = Arrays.asList("ar", "br", "cz", "da", "de", "el", "en", "es", "fr",
			"fr_ca", "hr", "hu", "id", "it", "ja", "ko", "nl", "no", "pl", "pt", "ru", "sl", "sv", "th", "tr", "zh");

	@Override
	@SuppressWarnings("unchecked")
	public void createContentNodeForMigration(String rootOrg, String org, Map<String, Object> requestMap)
			throws Exception {

		List<Map<String, Object>> contentMetas = (List<Map<String, Object>>) requestMap.get(LexConstants.CONTENT);

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		try {
			graphService.createNodes(rootOrg, contentMetas, transaction);
			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String createContentNodeForMigrationV3(String rootOrg, String org, Map<String, Object> requestMap)
			throws BadRequestException, Exception {
		
		Map<String, Object> reqContentMeta = new HashMap<>();
		Session session = neo4jDriver.session();
		Map<String, Object> boolMap = new HashMap<>();
		boolMap.put("bool", false);
		reqContentMeta = (Map<String, Object>) requestMap.get(LexConstants.CONTENT);
		List<String> childrenIds = (List<String>) requestMap.get(LexConstants.CHILDREN);
		Map<String,Object> contentMeta = defaultMetaPopulationBasicValidations(rootOrg, org, reqContentMeta, childrenIds, boolMap);
		List<String> existingChildren = new ArrayList<>();
		Transaction tx = session.beginTransaction();
		// todo create node
		try {
			if ((boolean) boolMap.get("bool")) {
				graphService.createNodeV2(rootOrg, contentMeta, tx);
			} else {
				String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
				graphService.updateNodeV2(rootOrg, identifier, contentMeta, tx);
				ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
				List<Relation> existingRelations = node.getChildren();
				for(Relation rel:existingRelations) {
					existingChildren.add(rel.getEndNodeId());
				}
				System.out.println(existingChildren);
			}
			// todo create relations
			String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			List<UpdateRelationRequest> updateRelations = new ArrayList<>();
			List<UpdateMetaRequest> updateIsStandAloneList = new ArrayList<>();
			for (int i = 0; i < childrenIds.size(); i++) {
				String child = childrenIds.get(i);
				int index = i;
				Map<String, Object> map = new HashMap<>();
				map.put(LexConstants.END_NODE_ID, child);
				map.put(LexConstants.REASON, "");
				map.put(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>());
				map.put(LexConstants.INDEX, index);
				Calendar c = Calendar.getInstance();
				String addedOn = inputFormatterDateTime.format(c.getTime()).toString();
				map.put(LexConstants.ADDED_ON,addedOn);
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(identifier, child, map);
				updateRelations.add(updateRelationRequest);
				
				Map<String,Object> updateMap = new HashMap<>();
				updateMap.put(LexConstants.IS_STAND_ALONE, false);
				UpdateMetaRequest updateChildStandAlone = new UpdateMetaRequest(child, updateMap);
				updateIsStandAloneList.add(updateChildStandAlone);
				
				existingChildren.remove(child);
				
			}
			
			if(existingChildren.size()>0) {
				List<ContentNode> oldNodes = graphService.getNodesByUniqueIdV2(rootOrg, existingChildren, tx);
				for(ContentNode oldNode:oldNodes) {
					if(oldNode.getParents().size()>1) {
						Map<String,Object> updateMap = new HashMap<>();
						updateMap.put(LexConstants.IS_STAND_ALONE, false);
						UpdateMetaRequest updateIsStandAloneTrue = new UpdateMetaRequest(oldNode.getIdentifier(), updateMap);
						updateIsStandAloneList.add(updateIsStandAloneTrue);
					}
					else {
						Map<String,Object> updateMap = new HashMap<>();
						updateMap.put(LexConstants.IS_STAND_ALONE, true);
						UpdateMetaRequest updateIsStandAloneTrue = new UpdateMetaRequest(oldNode.getIdentifier(), updateMap);
						updateIsStandAloneList.add(updateIsStandAloneTrue);
					}
					
				}
			}
			
			graphService.updateNodesV2(rootOrg, updateIsStandAloneList, tx);
			graphService.deleteChildren(rootOrg, Arrays.asList(identifier), tx);
			graphService.createChildRelationsV2(rootOrg, identifier, updateRelations, tx);
			Map<String,Object> hierarchyMap = getHierarchyFromNeo4j(identifier, rootOrg, tx, false, new ArrayList<>());
			hierachyForViewing(hierarchyMap);
			//entire meta construction is now complete, now running final validations if successful committing transaction else throwing back errors
			Map<String, Set<String>> errors = validationsService.contentHierarchyValidations(rootOrg, hierarchyMap);
			// if corrupt data in children, investigate
			if (!errors.isEmpty()) {
				throw new ConflictErrorException("Validations Failed", errors);
			}
			tx.commitAsync().toCompletableFuture().get();
		}
		catch (Exception e) {
			e.printStackTrace();
			tx.rollbackAsync().toCompletableFuture().get();
			throw new Exception(e);
		} finally {
			session.close();
		}
		return (String) contentMeta.get(LexConstants.IDENTIFIER);
	}

	private Map<String,Object> defaultMetaPopulationBasicValidations(String rootOrg, String org, Map<String, Object> reqMap,
			List<String> children, Map<String, Object> boolMap) throws BadRequestException,Exception
	{ 
			Map<String, Object> contentMeta = new HashMap<>(reqMap);

//			contentMeta.remove(LexConstants.STATUS);
			List<String> accessPaths = (List<String>) contentMeta.get(LexConstants.ACCESS_PATHS);
			contentMeta.remove(LexConstants.COLLECTIONS);
			contentMeta.remove(LexConstants.CHILDREN);
			contentMeta.remove(LexConstants.ROOT_ORG);
			contentMeta.remove(LexConstants.ORG);
			contentMeta.remove(LexConstants.TRANSLATION_OF);

			Boolean isSearchable = (Boolean) contentMeta.getOrDefault(LexConstants.IS_SEARCHABLE,true);
			if(isSearchable==null) {
				isSearchable=true;
			}
			
			contentMeta.put(LexConstants.IS_STAND_ALONE, true);
			contentMeta.put(LexConstants.ROOT_ORG, rootOrg);
			if(contentMeta.get(LexConstants.ISEXTERNAL)==null || contentMeta.get(LexConstants.ISEXTERNAL).toString().isEmpty()) {
				contentMeta.put(LexConstants.ISEXTERNAL, true);
			}
			if(contentMeta.get(LexConstants.STATUS)==null || contentMeta.get(LexConstants.STATUS).toString().isEmpty()) {
				contentMeta.put(LexConstants.STATUS, LexConstants.Status.Live.getStatus());
			}
			contentMeta.put(LexConstants.IS_SEARCHABLE, isSearchable);
			contentMeta.put(LexConstants.NODE_TYPE, LexConstants.LEARNING_CONTENT_NODE_TYPE);
			// todo remove learningMode
			contentMeta.put(LexConstants.LEARNING_MODE, "Self-Paced");
			// todo calc for children duration and size and childTitle etc
			contentMeta.put(LexConstants.CHILD_TITLE, new ArrayList<>());
			if(accessPaths==null||accessPaths.isEmpty()) {
				contentMeta.put(LexConstants.ACCESS_PATHS, Arrays.asList(rootOrg + "/" + org));
			}
			contentMeta.put(LexConstants.CHILD_DESC, new ArrayList<>());

			Calendar validTill = Calendar.getInstance();
			contentMeta.put(LexConstants.VERSION_DATE, inputFormatterDateTime.format(validTill.getTime()));
			contentMeta.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
			validTill.add(Calendar.YEAR, 50);

			Map<String, Object> orgMap = new HashMap<>();
			orgMap.put(LexConstants.ORG, org);
			orgMap.put(LexConstants.VALID_TILL, inputFormatterDateTime.format(validTill.getTime()));
			List<Map<String, Object>> orgsList = new ArrayList<>();
			orgsList.add(orgMap);

			contentMeta.put(LexConstants.ORG, orgsList);

			//TODO expiry based on rootOrg from appConfig
			String dataSource = null;
			AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "expiry_date")).orElse(null);
			if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
				dataSource = config.getValue();
			}
			
			if(dataSource==null || dataSource.equalsIgnoreCase("0")) {
				contentMeta.put(LexConstants.EXPIRY_DATE, "99991231T235959+0000");
			}
			else {
				Integer months = Integer.parseInt(dataSource);
				Calendar dueDate = Calendar.getInstance();
				dueDate.add(Calendar.MONTH, months);
				contentMeta.put(LexConstants.EXPIRY_DATE,inputFormatterDateTime.format(dueDate.getTime()));
			}

			Map<String, Object> transcodeMap = new HashMap<>();
			transcodeMap.put(LexConstants.TRANSCODE_STATUS, null);
			transcodeMap.put(LexConstants.TRANSCODED_ON, null);
			transcodeMap.put(LexConstants.RETRYCOUNT, 0);

			contentMeta.put(LexConstants.TRANSCODING, transcodeMap);

			Number duration = (Number) contentMeta.getOrDefault(LexConstants.DURATION, 0);
			contentMeta.put(LexConstants.DURATION, duration.longValue());
			Number size = (Number) contentMeta.getOrDefault(LexConstants.SIZE, 0);
			contentMeta.put(LexConstants.SIZE, size);
			boolean calculateSize = false;
			if (children.size() >= 1) {
				calculateSize = true;
				contentMeta.remove(LexConstants.SIZE);
				contentMeta.remove(LexConstants.DURATION);
			}

			String identifier = null;
			List<Map<String, Object>> creatorContacts = new ArrayList<>();

			identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			if (identifier == null || identifier.isEmpty()) {
				identifier = lexServerProps.getContentIdPrefix() + getUniqueIdFromTimestamp(0);
				contentMeta.put(LexConstants.IDENTIFIER, identifier);
				boolMap.put("bool", true);
			}

			creatorContacts = (List<Map<String, Object>>) contentMeta.get(LexConstants.CREATOR_CONTACTS);
			if (creatorContacts == null || creatorContacts.isEmpty()) {
				throw new BadRequestException("creatorContacts is null/empty " + creatorContacts.toString());
			}

			String locale = (String) contentMeta.get(LexConstants.LOCALE);

			if (locale == null || locale.isEmpty()) {
				throw new BadRequestException("Invalid locale");
			}

			if (!master_locale.contains(locale)) {
				throw new BadRequestException("Invalid locale : " + locale + " must be one of : " + master_locale);
			}

			Session session = neo4jDriver.session();
			final String ro = rootOrg;
			List<Map<String, Object>> childrenMeta = new ArrayList<>();
			childrenMeta = session.readTransaction(new TransactionWork<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> execute(Transaction tx) {
					return queryResult(ro, children, tx);
				}
			});

			if (calculateSize) {
				Map<String, Object> durationMap = calculateSizeDuration(childrenMeta);
				Map<String, Object> childDescChildTitle = calculateTitleDesc(childrenMeta, contentMeta);
				contentMeta.put(LexConstants.DURATION, durationMap.get(LexConstants.DURATION));
				contentMeta.put(LexConstants.SIZE, durationMap.get(LexConstants.SIZE));
				contentMeta.put(LexConstants.CHILD_TITLE, childDescChildTitle.get(LexConstants.CHILD_TITLE));
				contentMeta.put(LexConstants.CHILD_DESC, childDescChildTitle.get(LexConstants.CHILD_DESC));
			}
			return contentMeta;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String createContentNodeForMigrationV2(String rootOrg, String org, Map<String, Object> requestMap)
			throws BadRequestException, Exception {
		Map<String, Object> reqContentMeta = new HashMap<>();
		Session session = neo4jDriver.session();
		Map<String, Object> boolMap = new HashMap<>();
		boolMap.put("bool", false);
		reqContentMeta = (Map<String, Object>) requestMap.get(LexConstants.CONTENT);
		List<String> childrenIds = (List<String>) requestMap.get(LexConstants.CHILDREN);
		Map<String, Object> contentMeta = migrationValidations(rootOrg, org, reqContentMeta, childrenIds, boolMap);
		List<String> existingChildren = new ArrayList<>();
		Transaction tx = session.beginTransaction();
		// todo create node
		try {
			if ((boolean) boolMap.get("bool")) {
				graphService.createNodeV2(rootOrg, contentMeta, tx);
			} else {
				String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
				graphService.updateNodeV2(rootOrg, identifier, contentMeta, tx);
				ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
				List<Relation> existingRelations = node.getChildren();
				for(Relation rel:existingRelations) {
					existingChildren.add(rel.getEndNodeId());
				}
				System.out.println(existingChildren);
			}
			// todo create relations
			String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			List<UpdateRelationRequest> updateRelations = new ArrayList<>();
			List<UpdateMetaRequest> updateIsStandAloneList = new ArrayList<>();
			for (int i = 0; i < childrenIds.size(); i++) {
				String child = childrenIds.get(i);
				int index = i;
				Map<String, Object> map = new HashMap<>();
				map.put(LexConstants.END_NODE_ID, child);
				map.put(LexConstants.REASON, "");
				map.put(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>());
				map.put(LexConstants.INDEX, index);
				Calendar c = Calendar.getInstance();
				String addedOn = inputFormatterDateTime.format(c.getTime()).toString();
				map.put(LexConstants.ADDED_ON,addedOn);
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(identifier, child, map);
				updateRelations.add(updateRelationRequest);
				
				Map<String,Object> updateMap = new HashMap<>();
				updateMap.put(LexConstants.IS_STAND_ALONE, false);
				UpdateMetaRequest updateChildStandAlone = new UpdateMetaRequest(child, updateMap);
				updateIsStandAloneList.add(updateChildStandAlone);
				
				existingChildren.remove(child);
				
			}
			
			if(existingChildren.size()>0) {
				List<ContentNode> oldNodes = graphService.getNodesByUniqueIdV2(rootOrg, existingChildren, tx);
				for(ContentNode oldNode:oldNodes) {
					if(oldNode.getParents().size()>1) {
						Map<String,Object> updateMap = new HashMap<>();
						updateMap.put(LexConstants.IS_STAND_ALONE, false);
						UpdateMetaRequest updateIsStandAloneTrue = new UpdateMetaRequest(oldNode.getIdentifier(), updateMap);
						updateIsStandAloneList.add(updateIsStandAloneTrue);
					}
					else {
						Map<String,Object> updateMap = new HashMap<>();
						updateMap.put(LexConstants.IS_STAND_ALONE, true);
						UpdateMetaRequest updateIsStandAloneTrue = new UpdateMetaRequest(oldNode.getIdentifier(), updateMap);
						updateIsStandAloneList.add(updateIsStandAloneTrue);
					}
					
				}
			}
			
			graphService.updateNodesV2(rootOrg, updateIsStandAloneList, tx);
			graphService.deleteChildren(rootOrg, Arrays.asList(identifier), tx);
			graphService.createChildRelationsV2(rootOrg, identifier, updateRelations, tx);
			tx.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollbackAsync().toCompletableFuture().get();
			throw new Exception(e);
		} finally {
			session.close();
		}
		return (String) contentMeta.get(LexConstants.IDENTIFIER);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> migrationValidations(String rootOrg, String org, Map<String, Object> reqMap,
			List<String> children, Map<String, Object> boolMap) throws BadRequestException,Exception {
		Map<String, Object> contentMeta = new HashMap<>(reqMap);

//		contentMeta.remove(LexConstants.STATUS);
		List<String> accessPaths = (List<String>) contentMeta.get(LexConstants.ACCESS_PATHS);
		contentMeta.remove(LexConstants.COLLECTIONS);
		contentMeta.remove(LexConstants.CHILDREN);
		contentMeta.remove(LexConstants.ROOT_ORG);
		contentMeta.remove(LexConstants.ORG);
		contentMeta.remove(LexConstants.TRANSLATION_OF);

		Boolean isSearchable = (Boolean) contentMeta.getOrDefault(LexConstants.IS_SEARCHABLE,true);
		if(isSearchable==null) {
			isSearchable=true;
		}
		
		contentMeta.put(LexConstants.IS_STAND_ALONE, true);
		contentMeta.put(LexConstants.ROOT_ORG, rootOrg);
		contentMeta.put(LexConstants.ISEXTERNAL, true);
		if(contentMeta.get(LexConstants.STATUS)==null || contentMeta.get(LexConstants.STATUS).toString().isEmpty()) {
			contentMeta.put(LexConstants.STATUS, LexConstants.Status.Live.getStatus());
		}
		contentMeta.put(LexConstants.IS_SEARCHABLE, isSearchable);
		contentMeta.put(LexConstants.NODE_TYPE, LexConstants.LEARNING_CONTENT_NODE_TYPE);
		// todo remove learningMode
		contentMeta.put(LexConstants.LEARNING_MODE, "Self-Paced");
		// todo calc for children duration and size and childTitle etc
		contentMeta.put(LexConstants.CHILD_TITLE, new ArrayList<>());
		if(accessPaths==null||accessPaths.isEmpty()) {
			contentMeta.put(LexConstants.ACCESS_PATHS, Arrays.asList(rootOrg + "/" + org));
		}
		contentMeta.put(LexConstants.CHILD_DESC, new ArrayList<>());

		Calendar validTill = Calendar.getInstance();
		contentMeta.put(LexConstants.VERSION_DATE, inputFormatterDateTime.format(validTill.getTime()));
		contentMeta.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
		validTill.add(Calendar.YEAR, 50);

		Map<String, Object> orgMap = new HashMap<>();
		orgMap.put(LexConstants.ORG, org);
		orgMap.put(LexConstants.VALID_TILL, inputFormatterDateTime.format(validTill.getTime()));
		List<Map<String, Object>> orgsList = new ArrayList<>();
		orgsList.add(orgMap);

		contentMeta.put(LexConstants.ORG, orgsList);

		//TODO expiry based on rootOrg from appConfig
		String dataSource = null;
		AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "expiry_date")).orElse(null);
		if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
			dataSource = config.getValue();
		}
		
		if(dataSource==null || dataSource.equalsIgnoreCase("0")) {
			contentMeta.put(LexConstants.EXPIRY_DATE, "99991231T235959+0000");
		}
		else {
			Integer months = Integer.parseInt(dataSource);
			Calendar dueDate = Calendar.getInstance();
			dueDate.add(Calendar.MONTH, months);
			contentMeta.put(LexConstants.EXPIRY_DATE,inputFormatterDateTime.format(dueDate.getTime()));
		}

		Map<String, Object> transcodeMap = new HashMap<>();
		transcodeMap.put(LexConstants.TRANSCODE_STATUS, null);
		transcodeMap.put(LexConstants.TRANSCODED_ON, null);
		transcodeMap.put(LexConstants.RETRYCOUNT, 0);

		contentMeta.put(LexConstants.TRANSCODING, transcodeMap);

		Number duration = (Number) contentMeta.getOrDefault(LexConstants.DURATION, 0);
		contentMeta.put(LexConstants.DURATION, duration.longValue());
		Number size = (Number) contentMeta.getOrDefault(LexConstants.SIZE, 0);
		contentMeta.put(LexConstants.SIZE, size);
		boolean calculateSize = false;
		if (children.size() >= 1) {
			calculateSize = true;
			contentMeta.remove(LexConstants.SIZE);
			contentMeta.remove(LexConstants.DURATION);
		}

		String identifier = null;
		List<Map<String, Object>> creatorContacts = new ArrayList<>();

		identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
		if (identifier == null || identifier.isEmpty()) {
			identifier = lexServerProps.getContentIdPrefix() + getUniqueIdFromTimestamp(0);
			contentMeta.put(LexConstants.IDENTIFIER, identifier);
			boolMap.put("bool", true);
		}

		creatorContacts = (List<Map<String, Object>>) contentMeta.get(LexConstants.CREATOR_CONTACTS);
		if (creatorContacts == null || creatorContacts.isEmpty()) {
			throw new BadRequestException("creatorContacts is null/empty " + creatorContacts.toString());
		}

		String locale = (String) contentMeta.get(LexConstants.LOCALE);

		if (locale == null || locale.isEmpty()) {
			throw new BadRequestException("Invalid locale");
		}

		if (!master_locale.contains(locale)) {
			throw new BadRequestException("Invalid locale : " + locale + " must be one of : " + master_locale);
		}

		Session session = neo4jDriver.session();
		final String ro = rootOrg;
		List<Map<String, Object>> childrenMeta = new ArrayList<>();
		childrenMeta = session.readTransaction(new TransactionWork<List<Map<String, Object>>>() {
			@Override
			public List<Map<String, Object>> execute(Transaction tx) {
				return queryResult(ro, children, tx);
			}
		});

		if (calculateSize) {
			Map<String, Object> durationMap = calculateSizeDuration(childrenMeta);
			Map<String, Object> childDescChildTitle = calculateTitleDesc(childrenMeta, contentMeta);
			contentMeta.put(LexConstants.DURATION, durationMap.get(LexConstants.DURATION));
			contentMeta.put(LexConstants.SIZE, durationMap.get(LexConstants.SIZE));
			contentMeta.put(LexConstants.CHILD_TITLE, childDescChildTitle.get(LexConstants.CHILD_TITLE));
			contentMeta.put(LexConstants.CHILD_DESC, childDescChildTitle.get(LexConstants.CHILD_DESC));

		}

		contentMeta.put(LexConstants.CHILDREN, new ArrayList<>());
		Map<String, Set<String>> errors = validationsService.contentHierarchyValidations(rootOrg, contentMeta);
		// if corrupt data in children, investigate
		if (!errors.isEmpty()) {
			throw new ConflictErrorException("Validations Failed", errors);
		}
		contentMeta.remove(LexConstants.CHILDREN);
		return contentMeta;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> calculateTitleDesc(List<Map<String, Object>> childrenMeta,
			Map<String, Object> contentMeta) {
		Map<String, Object> resultMap = new HashMap<>();
		String parentTitle = (String) contentMeta.getOrDefault(LexConstants.NAME, "Java");
		String parentDesc = (String) contentMeta.getOrDefault(LexConstants.DESC, "Java");
		List<String> allChildrenTitles = new ArrayList<>();
		List<String> allChildrenDescription = new ArrayList<>();
		for (Map<String, Object> child : childrenMeta) {
			allChildrenTitles.add((String) child.getOrDefault(LexConstants.NAME, ""));
			allChildrenTitles
					.addAll((List<String>) child.getOrDefault(LexConstants.CHILD_TITLE, Collections.EMPTY_LIST));
			allChildrenDescription.add((String) child.getOrDefault(LexConstants.DESC, ""));
			allChildrenDescription
					.addAll((List<String>) child.getOrDefault(LexConstants.CHILD_DESC, Collections.EMPTY_LIST));
		}
		List<String> childrenTitles = checkSimilar(parentTitle, allChildrenTitles);
		List<String> childrenDescs = checkSimilar(parentDesc, allChildrenDescription);
		resultMap.put(LexConstants.CHILD_TITLE, childrenTitles);
		resultMap.put(LexConstants.CHILD_DESC, childrenDescs);
		return resultMap;
	}

	public String getUserDataSource(String rootOrg) {
		String dataSource = null;
		AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "user_data_source")).orElse(null);
		if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
			dataSource = config.getValue();
		}
		return dataSource;
//        return "PID";
	}

	private Map<String, Object> calculateSizeDuration(List<Map<String, Object>> childrenMeta) {
		Number totalDuration = 0;
		Number totalSize = 0;
		for (Map<String, Object> contentObj : childrenMeta) {
			Number duration = (Number) contentObj.getOrDefault(LexConstants.DURATION, 0);
			Number size = (Number) contentObj.getOrDefault(LexConstants.SIZE, 0);
			totalDuration = totalDuration.longValue() + duration.longValue();
			totalSize = totalSize.longValue() + size.longValue();
		}
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(LexConstants.DURATION, totalDuration);
		tempMap.put(LexConstants.SIZE, totalSize);
		return tempMap;
	}

	private List<Map<String, Object>> queryResult(String rootOrg, List<String> children, Transaction tx) {
		List<Map<String, Object>> childrenMeta = new ArrayList<>();
		List<String> allIds = new ArrayList<>();
		if (children != null) {
			allIds.addAll(children);
		}
		allIds = allIds.stream().map(id -> "'" + id + "'").collect(Collectors.toList());
		int size = allIds.size();
		String query = "match(n:" + rootOrg + ") where n.identifier in " + allIds + " return n.identifier,n.status,n";
		StatementResult statementresult = tx.run(query);
		List<Record> records = statementresult.list();
		int counter = 0;
		for (Record record : records) {
			Value x = record.get("n.status");
			String status = x.asString();
			status = status.replace("\"", "");
			if (!status.equals(LexConstants.Status.Live.getStatus())) {
				throw new BadRequestException("Not live identifier found : " + record.get("n.identifier"));
			}
			counter += 1;
			Node node = record.get("n").asNode();
			Map<String, Object> nodeMap = node.asMap();
			childrenMeta.add(nodeMap);
		}
		if (size != counter) {
			throw new BadRequestException("Not all Ids Found");
		}
		return childrenMeta;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String createContentNode(String rootOrg, String org, Map<String, Object> requestMap) throws Exception {

		Map<String, Object> contentMeta = (Map<String, Object>) requestMap.get(LexConstants.CONTENT);
		
//		boolean isReco = (boolean) requestMap.getOrDefault("isRecoKb", false);
//		String region = (String) requestMap.get(LexConstants.RECOREGION);
//		
//		if(isReco) {
//			if(region==null || region.isEmpty()) {
//				throw new BadRequestException("Region cannot be null/empty");
//			}
//			String validReco = recoKbValidations(region,rootOrg,contentMeta);
//			if(!validReco.isEmpty()) {
//				contentMeta.put(LexConstants.LABELS, Arrays.asList(validReco));
//			}
//		}
		
		createOperationValidations(contentMeta);

		// generate unique id for content
		String identifier = lexServerProps.getContentIdPrefix() + getUniqueIdFromTimestamp(0);
		contentMeta.put(LexConstants.IDENTIFIER, identifier);

		logger.info("identifier has been generated for content " + identifier);

		// user validations
		// User user = verifyUserV2(contentMeta);

		if (contentMeta.get(LexConstants.CREATED_BY) == null
				|| contentMeta.get(LexConstants.CREATED_BY).toString().isEmpty()) {
			throw new BadRequestException("content creator is not populated");
		}

		String locale = (String) contentMeta.get(LexConstants.LOCALE);

		if (locale == null || locale.isEmpty()) {
			throw new BadRequestException("Invalid locale");
		}

		if (!master_locale.contains(locale)) {
			throw new BadRequestException("Invalid locale : " + locale + " must be one of : " + master_locale);
		}

		String userId = contentMeta.get(LexConstants.CREATED_BY).toString();
		
		logger.info("Starting PID Call");
		Map<String, Object> userData = getUserDataFromUserId(rootOrg, userId,
				Arrays.asList(PIDConstants.UUID, PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL));
		logger.info("PID Call is complete");
		
		if (userData == null || userData.isEmpty()) {
			throw new BadRequestException("No user with id : " + userId);
		}

		// populate all required fields for given rootOrg as null
		if (validateContentType(contentMeta)) {
			logger.info("Starting default meta population");
			populateMetaForCreation(rootOrg, org, contentMeta, userData, userId);
			logger.info("Default meta population complete");
			String orgLangId = getOriginalLangNode(rootOrg, contentMeta);
			logger.info("Starting s3 folder creation call");
			//createContentFolderInS3(rootOrg, org, identifier, contentMeta);
			logger.info("s3 folder created");
			contentMeta.remove(LexConstants.TRANSLATION_OF);
			logger.info("node creation started");
			identifier = createNode(rootOrg, contentMeta);
			logger.info("node created on neo4j");
			if (!orgLangId.isEmpty() || orgLangId != null) {
				createTranslationRelation(identifier, orgLangId, rootOrg);
			}
		} else {
			throw new BadRequestException("invalid contentType " + contentMeta.get(LexConstants.CONTENT_TYPE));
		}
		return identifier;
	}

	private String recoKbValidations(String region,String rootOrg,Map<String,Object> contentMeta) {
		
		String recoValue = "Recommendation KB for " + region.replace(" ", "").toLowerCase();
		String contentType = (String) contentMeta.getOrDefault(LexConstants.CONTENT_TYPE, "");
		
		if(contentType==null||contentType.isEmpty()) {
			throw new BadRequestException("Invalid contentType, cannot be null");
		}
		
		if(!contentType.equals(LexConstants.K_BOARD)) {
			throw new BadRequestException("Recommendations can only be provided for KB");
		}
		Session session = neo4jDriver.session();
		
		Map<String,Object> resultMap = session.readTransaction(new TransactionWork<Map<String,Object>>() {
			@Override
			public Map<String,Object> execute(Transaction tx){
				return node_with_labels(rootOrg,recoValue,tx);
			}
		});
		
		if(resultMap==null || resultMap.isEmpty()) {
			return recoValue;
		}
		else {
			return "";
		}
	}

	protected Map<String, Object> node_with_labels(String rootOrg, String labelValue, Transaction tx) {
		String query = "match(n:" + rootOrg + ") where '" + labelValue +"' in n.labels return n";
		System.out.println(query);
		Map<String,Object> resultMap = new HashMap<>();
		StatementResult statementResult = tx.run(query);
		List<Record> records = statementResult.list();
		for(Record record: records) {
			resultMap = record.get("n").asMap();
		}
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public Map<String, Object> getUserDataFromUserId(String rootOrg, String userId, List<String> source) {

		Map<String, Object> result = new HashMap<>();
		String dataSource = getUserDataSource(rootOrg);
		if ("su".equalsIgnoreCase(dataSource)) {
			User user = userRepo.findById(userId).orElse(null);
			if (user != null) {
				result.put(user.getId(), new HashMap<String, String>() {
					{
						put(PIDConstants.UUID, user.getId());
						put(PIDConstants.FIRST_NAME, user.getFirstName());
						put(PIDConstants.LAST_NAME, user.getLastName());
						put(PIDConstants.EMAIL, user.getEmail());
					}
				});
			} else {
				throw new BadRequestException("No user found with userId : " + userId);
			}
		} else {

			List<String> sources = new ArrayList<>(source);

			if (!sources.contains("wid")) {
				sources.add("wid");
			}

			if (!sources.contains("root_org")) {
				sources.add("root_org");
			}

			Map<String, Object> pidRequestMap = new HashMap<String, Object>() {
				{
					put("source_fields", sources);
					put("conditions", new HashMap<String, String>() {
						{
							put("root_org", rootOrg);
							put("wid", userId);
						}
					});
				}
			};
			try {
				List<Map<String, Object>> pidResponse = restTemplate.postForObject(
						"http://" + lexServerProps.getPidIp() + ":" + lexServerProps.getPidPort().toString() + "/user",
						pidRequestMap, List.class);

				if (!pidResponse.isEmpty()) {
					Map<String, Object> record = pidResponse.get(0);
					if (record.get("wid") != null && record.get("wid").equals(userId) && record.get("root_org") != null
							&& rootOrg.equalsIgnoreCase(record.get("root_org").toString())) {
						result.put(record.get("wid").toString(), record);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new BadRequestException("No data obtained from PID");
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void createTranslationRelation(String identifier, String orgLangId, String rootOrg)
			throws InterruptedException, ExecutionException {
		String query = "match(n{identifier:'" + identifier + "'}) where n:Shared or n:" + rootOrg
				+ " with n match (n1{identifier:'" + orgLangId + "'}) where n1:Shared or n1:" + rootOrg
				+ " with n,n1 merge (n)-[r:Is_Translation_Of]->(n1)";
		Session translationSession = neo4jDriver.session();
		Transaction tx = translationSession.beginTransaction();
		try {
			StatementResult res = tx.run(query);
			tx.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			tx.rollbackAsync().toCompletableFuture().get();
		} finally {
			translationSession.close();
		}
	}

	private String getOriginalLangNode(String rootOrg, Map<String, Object> contentMeta) {
		String locale = (String) contentMeta.get(LexConstants.LOCALE);
		String translationOf = (String) contentMeta.getOrDefault(LexConstants.TRANSLATION_OF, "");
		String orgLangId = null;
		if (locale == null || locale.isEmpty()) {
			throw new BadRequestException("locale cannot be null or empty");
		}
		if (!translationOf.isEmpty() || (translationOf != null)) {
			orgLangId = findOriginalLangContent(translationOf, rootOrg);
		}
		return orgLangId;
	}

	@Override
	public Map<String, Object> getContentNode(String rootOrg, String identifier) throws BadRequestException, Exception {

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		if (identifier.contains(LexConstants.IMG_SUFFIX))
			identifier = identifier.substring(0, identifier.indexOf(LexConstants.IMG_SUFFIX));

		try {
			ContentNode contentNode = getNodeFromDb(rootOrg, identifier, transaction);
			transaction.commitAsync().toCompletableFuture().get();
			return contentNode.getMetadata();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}

	@Override
	public void updateContentMetaNode(String rootOrg, String org, String identifier, Map<String, Object> requestMap)
			throws Exception {

		Session session = neo4jDriver.session();

		if (requestMap == null || requestMap.isEmpty())
			throw new BadRequestException("empty request body");

		Transaction tx = session.beginTransaction();
		try {
			updateMeta(rootOrg, identifier, requestMap, tx);

			tx.commitAsync().toCompletableFuture().get();

		} catch (Exception e) {
			tx.rollbackAsync().toCompletableFuture().get();
		} finally {
			tx.close();
			session.close();
		}

	}

	@Override
	public void updateContentNode(String rootOrg, String org, String identifier, Map<String, Object> requestMap)
			throws Exception {

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		if (requestMap == null || requestMap.isEmpty())
			throw new BadRequestException("empty request body");

		if (identifier.contains(LexConstants.IMG_SUFFIX))
			identifier = identifier.substring(0, identifier.indexOf(LexConstants.IMG_SUFFIX));

		try {
			updateNode(rootOrg, identifier, requestMap, transaction);
//			Map<String, Set<String>> errors = validationsService.contentHierarchyValidations(rootOrg,
//					contentNode.getMetadata());
//			if (!errors.isEmpty())
//				throw new ConflictErrorException("Validation Failed", errors);

			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			e.printStackTrace();
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateContentHierarchy(String rootOrg, String org, Map<String, Object> requestMap, String migration)
			throws Exception {

		if (!requestMap.containsKey(LexConstants.NODES_MODIFIED))
			throw new BadRequestException("Request body does not contain nodesModified");

		if (!requestMap.containsKey(LexConstants.HIERARCHY))
			throw new BadRequestException("Request body does not contain hierarchy");

		Map<String, Object> nodesModified = (Map<String, Object>) requestMap.get(LexConstants.NODES_MODIFIED);
		Map<String, Object> hierarchy = (Map<String, Object>) requestMap.get(LexConstants.HIERARCHY);

		nodesModified = removeImageSuffixFromNodesModified(nodesModified);
		hierarchy = removeImageNodeSuffixFromHierarchyModified(hierarchy);

		logger.info("Update Hierarchy api called for request body. \n nodesModified: " + nodesModified.toString()
				+ "\n hierarchy:" + hierarchy.toString());
		// fetching the root node
		String rootNodeIdentifier = getRootNode(rootOrg, nodesModified, hierarchy);

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		try {
			//method to update meta data
			updateNodes(rootOrg, nodesModified, transaction);
			//method to update hierarchy
			updateHierarchy(rootOrg, hierarchy, transaction, migration);
			//updateHierarchyV2(rootOrg, hierarchy, transaction);
			if (migration.equals("no")) {
				validateAndReCalcSizeAndDuration(rootOrg, org, rootNodeIdentifier, transaction);
			}
			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void updateContentHierarchyV2(String rootOrg, String org, Map<String, Object> requestMap, String migration)
			throws Exception {

		if (!requestMap.containsKey(LexConstants.NODES_MODIFIED))
			throw new BadRequestException("Request body does not contain nodesModified");

		if (!requestMap.containsKey(LexConstants.HIERARCHY))
			throw new BadRequestException("Request body does not contain hierarchy");

		Map<String, Object> nodesModified = (Map<String, Object>) requestMap.get(LexConstants.NODES_MODIFIED);
		Map<String, Object> hierarchy = (Map<String, Object>) requestMap.get(LexConstants.HIERARCHY);

		nodesModified = removeImageSuffixFromNodesModified(nodesModified);
		hierarchy = removeImageNodeSuffixFromHierarchyModified(hierarchy);

		logger.info("Update Hierarchy api called for request body. \n nodesModified: " + nodesModified.toString()
				+ "\n hierarchy:" + hierarchy.toString());
		// fetching the root node
		String rootNodeIdentifier = getRootNode(rootOrg, nodesModified, hierarchy);

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		try {
			//method to update meta data
			updateNodes(rootOrg, nodesModified, transaction);
			//method to update hierarchy
			//updateHierarchy(rootOrg, hierarchy, transaction, migration);
			updateHierarchyV2(rootOrg, hierarchy, transaction);
			if (migration.equals("no")) {
				validateAndReCalcSizeAndDuration(rootOrg, org, rootNodeIdentifier, transaction);
			}
			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}
	
	

	

	public void updateTranscodeMap(String rootOrg, String identifier) throws Exception {
		Map<String, Object> updateMap = new HashMap<>();
		Map<String, Object> transcodeMap = new HashMap<>();
		transcodeMap.put(LexConstants.TRANSCODE_STATUS, "STARTED");
		transcodeMap.put(LexConstants.RETRYCOUNT, 0);
		Calendar lastUpdatedOn = Calendar.getInstance();
		transcodeMap.put(LexConstants.TRANSCODED_ON, inputFormatterDateTime.format(lastUpdatedOn.getTime()));

		updateMap.put(LexConstants.TRANSCODING, transcodeMap);

		UpdateMetaRequest updateObj = new UpdateMetaRequest(identifier, updateMap);
		List<UpdateMetaRequest> updateList = new ArrayList<>();
		updateList.add(updateObj);

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		try {
			graphService.updateNodesV2(rootOrg, updateList, transaction);
			transaction.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			transaction.close();
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	private void updateNodes(String rootOrg, Map<String, Object> nodesModified, Transaction transaction)
			throws Exception {
		
		List<String> notAllowedToUpdateStatuses = Arrays.asList(LexConstants.Status.Expired.getStatus(),LexConstants.Status.Deleted.getStatus(),LexConstants.Status.UnPublish.getStatus());
		if (nodesModified == null || nodesModified.isEmpty()) {
			return;
		}

		//add all original + .img ids to list
		List<String> identifiersToFetch = new ArrayList<>(nodesModified.keySet());
		identifiersToFetch.addAll(identifiersToFetch.stream().map(identifier -> identifier + LexConstants.IMG_SUFFIX)
				.collect(Collectors.toList()));

		//add all nodes from db to map with key as identifier and node as value for all values found
		Map<String, ContentNode> idToContentMapping = new HashMap<>();
		graphService.getNodesByUniqueIdV2(rootOrg, identifiersToFetch, transaction)
				.forEach(contentNode -> idToContentMapping.put(contentNode.getIdentifier(), contentNode));

		List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
		List<ContentNode> imageNodesToBeCreated = new ArrayList<>();
		List<String> authoringDisabledIds = new ArrayList<>();
		
		//iterating on requestMaps from request
		for (Map.Entry<String, Object> entry : nodesModified.entrySet()) {
			String identifier = entry.getKey();
			//if id in request does not exist in idToContent map throw error
			if (!idToContentMapping.containsKey(identifier)) {
				throw new ResourceNotFoundException("Content with identifier: " + entry.getKey() + " does not exist");
			}
			
			//add all data to be updated to updateMap
			Map<String, Object> updateMap = (Map<String, Object>) entry.getValue();
			updateMap = (Map<String, Object>) updateMap.get(LexConstants.METADATA);
			updateOperationValidations(updateMap);
			Calendar lastUpdatedOn = Calendar.getInstance();
			updateMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(lastUpdatedOn.getTime()));
			ContentNode contentNode = idToContentMapping.get(entry.getKey());

			Boolean authoringDisabled = (Boolean) contentNode.getMetadata().get(LexConstants.AUTHORING_DISABLED);
			Boolean metaEditDisabled = (Boolean) contentNode.getMetadata().get(LexConstants.META_EDIT_DISABLED);
			
			if(authoringDisabled==null) {
				authoringDisabled=false;
			}
			
			if(metaEditDisabled==null) {
				metaEditDisabled=false;
			}
			//if authoringDisabled is true then we do not allow contents to be edited
			if(authoringDisabled) {
				authoringDisabledIds.add(contentNode.getIdentifier());
			}
			
			//if isMetaEditingDisabled is true then we do not allow contents to be edited
			if(metaEditDisabled) {
				if(!authoringDisabledIds.contains(contentNode.getIdentifier())) {
					authoringDisabledIds.add(contentNode.getIdentifier());
				}
			}
			
			//if authoringDisabled is null or false continue with update and img node logic
			if(authoringDisabled==null||authoringDisabled==false) {
				//if metaEditDisabled is null or false continue with update and img node logic
				if(metaEditDisabled==null||metaEditDisabled==false) {
					//if node being updated is live then img node logic --->>
					if (contentNode.getMetadata().get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {
						String imageNodeIdentifier = identifier + LexConstants.IMG_SUFFIX;
		
						//if .img node already exists in idToContent map perform updates on existsing .img node
						if (idToContentMapping.containsKey(imageNodeIdentifier)) {
							updateMetaRequests.add(new UpdateMetaRequest(imageNodeIdentifier, updateMap));
						} else {
							// create image node
							ContentNode imageNode = populateMetaForImageNodeCreation(contentNode, idToContentMapping,
									updateMap);
							idToContentMapping.put(imageNode.getIdentifier(), imageNode);
							// adding the image node as parent in the children of the original node.
							// ensureHierarchyCorrectnessForMetaUpdate(idToContentMapping, imageNode);
							imageNodesToBeCreated.add(imageNode);
						}
					}
					else if(notAllowedToUpdateStatuses.contains(contentNode.getMetadata().get(LexConstants.STATUS))){
						authoringDisabledIds.add(contentNode.getIdentifier());
					}
					//if node is not at live status then update on original node
					else {
						updateMetaRequests.add(new UpdateMetaRequest(identifier, updateMap));
					}
				}
			}
		}		

		List<Map<String, Object>> imageNodesMetas = imageNodesToBeCreated.stream()
				.map(imageNode -> imageNode.getMetadata()).collect(Collectors.toList());

		//create all image nodes
		graphService.createNodes(rootOrg, imageNodesMetas, transaction);
		//update all relevant nodes
		graphService.updateNodesV2(rootOrg, updateMetaRequests, transaction);
		//create all corresponding relations to .img node
		graphService.mergeRelations(rootOrg, GraphUtil.createUpdateRelationRequestsForImageNodes(imageNodesToBeCreated),
				transaction);
		
		if(authoringDisabledIds.size()>0) {
			throw new BadRequestException("The following Ids cannot be edited because either status is in ['Deleted','Expired','Unpublished'] or contents are imported : " + authoringDisabledIds);
		}

	}
	
	private void updateHierarchyV2(String rootOrg, Map<String, Object> hierarchy, Transaction transaction) throws Exception {
		
		//if hierarchy is null or empty return
		if(hierarchy==null||hierarchy.isEmpty()) {
			return;
		}
		
		//ADD all parentIds and child ids along with .img ids to idsFetchList list --> contd
		List<String> contentIdsToFetch = new ArrayList<>(hierarchy.keySet());
		//all possible identifiers are added to contentIdsToFetch list
		getChildrenGettingAttached(contentIdsToFetch, hierarchy);
		// contd here-->
		contentIdsToFetch.addAll(contentIdsToFetch.stream().map(identifier -> identifier + LexConstants.IMG_SUFFIX)
				.collect(Collectors.toList()));
		
		//fetch all nodes from above created list
		//create a idToContentMap with key as identifier and value as the ContentNode
		Map<String,ContentNode> idToContentMapping = new HashMap<>();
		
		List<Map<String,Object>> configHierarchyData = contentTypeHierarchyService.getAllContentHierarchy(rootOrg);

		List<String> fields = getFieldsFromConfigData(configHierarchyData);
		
		if(!fields.contains(LexConstants.CONTENT_TYPE)) {
			fields.add(LexConstants.CONTENT_TYPE);
		}
		
		if(!fields.contains(LexConstants.STATUS)) {
			fields.add(LexConstants.STATUS);
		}
		
		//TODO change to v3 and test
		graphService.getNodesByUniqueIdForHierarchyUpdateV3(rootOrg, contentIdsToFetch,fields, transaction)
		//adding all contentNodes to the map with key as identifier and value being the complete node
			.forEach(contentNode -> idToContentMapping.put(contentNode.getIdentifier(), contentNode));
		
		
//		//returns all contentNodes with complete meta along with minimal parent and child data
//		graphService.getNodesByUniqueIdForHierarchyUpdateV2(rootOrg, contentIdsToFetch, transaction)
//		//adding all contentNodes to the map with key as identifier and value being the complete node
//			.forEach(contentNode -> idToContentMapping.put(contentNode.getIdentifier(), contentNode));
		
		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();
		List<String> idsForChildrenDeletion = new ArrayList<>();
		List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
		List<String> authoringDisabledIds = new ArrayList<>();
		
		//iterating on requestMap
				for (Map.Entry<String, Object> entry : hierarchy.entrySet()) {
					Boolean isLearningPath = false;
					// original node not found
					if (!idToContentMapping.containsKey(entry.getKey())) {
						throw new ResourceNotFoundException("Content with identifier: " + entry.getKey() + " does not exist.");
					}
					
					//if content is KB OR content is not live -> original id and node is returned
					//else .img id and node is returned
					ContentNode contentNodeToUpdate = findContentNodeToUpdate(idToContentMapping, entry);
					
					// content is in live and image node does not exist so not updating the hierarchy
					if (contentNodeToUpdate == null)
						continue;
					
					if(contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE).equals(LexConstants.ContentType.LearningPath.getContentType())) {
						isLearningPath=true;
					}
					
					//TODO get data from configurable content hierarchy table for this contentType
					List<Map<String, Object>> applicableChildrenData = getContentTypeDataFromConfigRootOrgData(configHierarchyData,(String) contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE));
//					List<Map<String, Object>> applicableChildrenData = contentTypeHierarchyService.getContentHierarchyForContentType(rootOrg, (String) contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE));

					//if authoring disabled is null or false continue else add to list
					Boolean authoringDisabled =  (Boolean) contentNodeToUpdate.getMetadata().get(LexConstants.AUTHORING_DISABLED);
					Boolean isMetaEditingDisabled = (Boolean) contentNodeToUpdate.getMetadata().get(LexConstants.META_EDIT_DISABLED);
					
					if(authoringDisabled==null) {
						authoringDisabled=false;
					}
					
					if(isMetaEditingDisabled==null) {
						isMetaEditingDisabled=false;
					}
					
					if(authoringDisabled==true||isMetaEditingDisabled==true) {
						authoringDisabledIds.add(contentNodeToUpdate.getIdentifier());
					}
					
					else {
						// removing this particular id from parents of its children.
						//??????????????
						ensureHierarchyCorrectnessForHierarchyUpdate(idToContentMapping, contentNodeToUpdate);
						idsForChildrenDeletion.add(contentNodeToUpdate.getIdentifier());
			
						// create the new Relations, image node will be in the same index
						List<Map<String, Object>> childrenMaps = (List<Map<String, Object>>) ((Map<String, Object>) entry
								.getValue()).get(LexConstants.CHILDREN);
						List<Map<String,Object>>newList = removeDuplicatesFromList(childrenMaps);
						childrenMaps = newList;
			//			Set<Map<String,Object>> childrenSets = new HashSet<>(childrenMaps);
			//			childrenMaps = new ArrayList<>(childrenSets);
			
						int index = 0;
						for (Map<String, Object> childMap : childrenMaps) {
							
							int position = childrenMaps.indexOf(childMap);
							int size = childrenMaps.size()-1;
			
							String childIdentifier = childMap.get(LexConstants.IDENTIFIER).toString();
							String reasonAdded = (String) childMap.getOrDefault(LexConstants.REASON, "");
							Calendar lastUpdatedOn = Calendar.getInstance();
							List<String> childrenClassifiers = (List<String>) childMap
									.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>());
							String addedOn = (String) childMap.getOrDefault(LexConstants.ADDED_ON,inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString() );
							if(addedOn==null) {
								addedOn = inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString();
							}
							
							if (!idToContentMapping.containsKey(childIdentifier)) {
								throw new ResourceNotFoundException(
										"Content with identifier: " + childIdentifier + " does not exist.");
							}
			
							ContentNode childNode = idToContentMapping.get(childIdentifier);
							if (!childNode.getMetadata().containsKey(LexConstants.IS_STAND_ALONE)) {
								childNode.getMetadata().put(LexConstants.IS_STAND_ALONE, true);
							}
							
							if(isLearningPath) {
								if(!childNode.getIdentifier().contains(LexConstants.IMG_SUFFIX)) {
									String childStatus = (String) childNode.getMetadata().get(LexConstants.STATUS);
									if(!childStatus.equals(LexConstants.Status.Live.getStatus())) {
										throw new BadRequestException("Can only add Live Children to a Learning Path");
									}
								}
							}
							//TODO FUNCTION TO ENSURE CONTENT_TYPE CORRECTNESS WITHIN A HIERARCHY
							validateChildContentType(applicableChildrenData,childNode,position,size);
							
			
							// courses and learning path will always have isStandAlone as true
							// children of KBoard must have isStandAlone as true
							if ((boolean) childNode.getMetadata().get(LexConstants.IS_STAND_ALONE) == true
									&& !childNode.getMetadata().get(LexConstants.CONTENT_TYPE)
											.equals(LexConstants.ContentType.Course.getContentType())
									&& !childNode.getMetadata().get(LexConstants.CONTENT_TYPE)
											.equals(LexConstants.ContentType.LearningPath.getContentType())
									&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
											.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
									&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
											.equals(LexConstants.ContentType.Channel.getContentType())) {
			
								Map<String, Object> updateMap = new HashMap<>();
								updateMap.put(LexConstants.IS_STAND_ALONE, false);
								updateMetaRequests.add(new UpdateMetaRequest(childNode.getIdentifier(), updateMap));
							}
			
							
							checkContentSharingConstraints(contentNodeToUpdate, childNode);
			
							Map<String, Object> relationMetaData = new HashMap<>();
			
							relationMetaData.put(LexConstants.INDEX, index);
							relationMetaData.put(LexConstants.REASON, reasonAdded);
							relationMetaData.put(LexConstants.CHILDREN_CLASSIFIERS, childrenClassifiers);
							relationMetaData.put(LexConstants.ADDED_ON, addedOn);
			
							updateRelationRequests.add(new UpdateRelationRequest(contentNodeToUpdate.getIdentifier(),
									childIdentifier, relationMetaData));
			
							if (idToContentMapping.containsKey(childIdentifier + LexConstants.IMG_SUFFIX)) {
			
								ContentNode childImageNode = idToContentMapping.get(childIdentifier + LexConstants.IMG_SUFFIX);
								if (!childImageNode.getMetadata().containsKey(LexConstants.IS_STAND_ALONE)) {
									childImageNode.getMetadata().put(LexConstants.IS_STAND_ALONE, true);
								}
			
								if ((boolean) childImageNode.getMetadata().get(LexConstants.IS_STAND_ALONE) == true
										&& !childImageNode.getMetadata().get(LexConstants.CONTENT_TYPE)
												.equals(LexConstants.ContentType.Course.getContentType())
										&& !childImageNode.getMetadata().get(LexConstants.CONTENT_TYPE)
												.equals(LexConstants.ContentType.LearningPath.getContentType())
										&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
												.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
										&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
												.equals(LexConstants.ContentType.Channel.getContentType())) {
									Map<String, Object> updateMap = new HashMap<>();
									updateMap.put(LexConstants.IS_STAND_ALONE, false);
									updateMetaRequests.add(new UpdateMetaRequest(childImageNode.getIdentifier(), updateMap));
								}
			
								checkContentSharingConstraints(contentNodeToUpdate,
										idToContentMapping.get(childIdentifier + LexConstants.IMG_SUFFIX));
			
								Map<String, Object> imageRelationMetaData = new HashMap<>();
								imageRelationMetaData.put(LexConstants.INDEX, index);
								imageRelationMetaData.put(LexConstants.REASON, reasonAdded);
								imageRelationMetaData.put(LexConstants.CHILDREN_CLASSIFIERS, childrenClassifiers);
								imageRelationMetaData.put(LexConstants.ADDED_ON, addedOn);
			
								updateRelationRequests.add(new UpdateRelationRequest(contentNodeToUpdate.getIdentifier(),
										childIdentifier + LexConstants.IMG_SUFFIX, imageRelationMetaData));
							}
							index++;
						}
					}
				}

				graphService.deleteChildren(rootOrg, idsForChildrenDeletion, transaction);
				graphService.updateNodesV2(rootOrg, updateMetaRequests, transaction);
				graphService.mergeRelations(rootOrg, updateRelationRequests, transaction);
				
				if(authoringDisabledIds.size()>0) {
					throw new BadRequestException("Invalid ids : " + authoringDisabledIds);
				}
	}

	private List<Map<String, Object>> getContentTypeDataFromConfigRootOrgData(
			List<Map<String, Object>> configHierarchyData, String contentType) {
		
		List<Map<String,Object>> applicableChildrenData = new ArrayList<>();
		for(Map<String, Object> data:configHierarchyData) {
			if(data.get(LexConstants.CONTENT_TYPE).equals(contentType)) {
				applicableChildrenData.add(data);
			}
		}
		
		if(applicableChildrenData==null||applicableChildrenData.isEmpty()) {
			throw new BadRequestException("No data in configHierarchy table for " + contentType);
		}
		
		return applicableChildrenData;
	}

	@SuppressWarnings("unchecked")
	private List<String> getFieldsFromConfigData(List<Map<String, Object>> configHierarchyData) {

		List<String> fields = new ArrayList<>();
		for(Map<String, Object> data:configHierarchyData) {
			Map<String,Object> conditionsMap = (Map<String, Object>) data.get("condition");
			if(!(conditionsMap==null) && !conditionsMap.isEmpty()) {
				for(String entry:conditionsMap.keySet()) {
					fields.add(entry);
				}
			}
		}
//		if(fields.isEmpty()) {
//			return null;
//		}
		return fields;
	}

	@SuppressWarnings("unchecked")
	private void updateHierarchy(String rootOrg, Map<String, Object> hierarchy, Transaction transaction,
			String migration) throws Exception {

		if (hierarchy == null || hierarchy.isEmpty())
			return;

		//add all original ids and .img ids to idsToFetch list 
		List<String> contentIdsToFetch = new ArrayList<>(hierarchy.keySet());
		//all possible identifiers are added to contentIdsToFetch list
		getChildrenGettingAttached(contentIdsToFetch, hierarchy);
		contentIdsToFetch.addAll(contentIdsToFetch.stream().map(identifier -> identifier + LexConstants.IMG_SUFFIX)
				.collect(Collectors.toList()));

		//get all nodes from above created list
		//creates a map idToContentMapping with key as id and value as node
		Map<String, ContentNode> idToContentMapping = new HashMap<>();
		graphService.getNodesByUniqueIdForHierarchyUpdate(rootOrg, contentIdsToFetch, transaction)
				.forEach(contentNode -> idToContentMapping.put(contentNode.getIdentifier(), contentNode));

		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();
		List<String> idsForChildrenDeletion = new ArrayList<>();
		List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
		List<String> authoringDisabledIds = new ArrayList<>();
		
		//iterating on requestMap
		for (Map.Entry<String, Object> entry : hierarchy.entrySet()) {
			Boolean isLearningPath = false;
			// original node not found
			if (!idToContentMapping.containsKey(entry.getKey())) {
				throw new ResourceNotFoundException("Content with identifier: " + entry.getKey() + " does not exist.");
			}
			
			//if content is KB OR content is not live -> original id and node is returned
			//else .img id and node is returned
			ContentNode contentNodeToUpdate = findContentNodeToUpdate(idToContentMapping, entry);
			
			// content is in live and image node does not exist so not updating the hierarchy
			if (contentNodeToUpdate == null)
				continue;

			if(contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE).equals(LexConstants.ContentType.LearningPath.getContentType())) {
				isLearningPath=true;
			}
			
			//if authoring disabled is null or false continue else add to list
			Boolean authoringDisabled =  (Boolean) contentNodeToUpdate.getMetadata().get(LexConstants.AUTHORING_DISABLED);
			Boolean isMetaEditingDisabled = (Boolean) contentNodeToUpdate.getMetadata().get(LexConstants.META_EDIT_DISABLED);
			
			if(authoringDisabled==null) {
				authoringDisabled=false;
			}
			
			if(isMetaEditingDisabled==null) {
				isMetaEditingDisabled=false;
			}
			
			if(authoringDisabled==true||isMetaEditingDisabled==true) {
				authoringDisabledIds.add(contentNodeToUpdate.getIdentifier());
			}
			
			else {
				// removing this particular id from parents of its children.
				//??????????????
				ensureHierarchyCorrectnessForHierarchyUpdate(idToContentMapping, contentNodeToUpdate);
				idsForChildrenDeletion.add(contentNodeToUpdate.getIdentifier());
	
				// create the new Relations, image node will be in the same index
				List<Map<String, Object>> childrenMaps = (List<Map<String, Object>>) ((Map<String, Object>) entry
						.getValue()).get(LexConstants.CHILDREN);
				List<Map<String,Object>>newList = removeDuplicatesFromList(childrenMaps);
				childrenMaps = newList;
	//			Set<Map<String,Object>> childrenSets = new HashSet<>(childrenMaps);
	//			childrenMaps = new ArrayList<>(childrenSets);
	
				int index = 0;
				for (Map<String, Object> childMap : childrenMaps) {
					
					String childIdentifier = childMap.get(LexConstants.IDENTIFIER).toString();
					String reasonAdded = (String) childMap.getOrDefault(LexConstants.REASON, "");
					Calendar lastUpdatedOn = Calendar.getInstance();
					List<String> childrenClassifiers = (List<String>) childMap
							.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>());
					String addedOn = (String) childMap.getOrDefault(LexConstants.ADDED_ON,inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString() );
					if(addedOn==null) {
						addedOn = inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString();
					}
					
					if (!idToContentMapping.containsKey(childIdentifier)) {
						throw new ResourceNotFoundException(
								"Content with identifier: " + childIdentifier + " does not exist.");
					}
	
					ContentNode childNode = idToContentMapping.get(childIdentifier);
					if (!childNode.getMetadata().containsKey(LexConstants.IS_STAND_ALONE)) {
						childNode.getMetadata().put(LexConstants.IS_STAND_ALONE, true);
					}
					
					if(isLearningPath) {
						if(!childNode.getIdentifier().contains(LexConstants.IMG_SUFFIX)) {
							String childStatus = (String) childNode.getMetadata().get(LexConstants.STATUS);
							if(!childStatus.equals(LexConstants.Status.Live.getStatus())) {
								throw new BadRequestException("Can only add Live Children to a Learning Path");
							}
						}
					}
					//FUNCTION TO ENSURE CONTENT_TYPE CORRECTNESS WITHIN A HIERARCHY
					//validateChildContentType(contentNodeToUpdate,childNode,position,size);
					
	
					// courses and learning path will always have isStandAlone as true
					// children of KBoard must have isStandAlone as true
					if ((boolean) childNode.getMetadata().get(LexConstants.IS_STAND_ALONE) == true
							&& !childNode.getMetadata().get(LexConstants.CONTENT_TYPE)
									.equals(LexConstants.ContentType.Course.getContentType())
							&& !childNode.getMetadata().get(LexConstants.CONTENT_TYPE)
									.equals(LexConstants.ContentType.LearningPath.getContentType())
							&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
									.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
							&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
									.equals(LexConstants.ContentType.Channel.getContentType())) {
	
						Map<String, Object> updateMap = new HashMap<>();
						updateMap.put(LexConstants.IS_STAND_ALONE, false);
						updateMetaRequests.add(new UpdateMetaRequest(childNode.getIdentifier(), updateMap));
					}
	
					if (migration.toLowerCase().equals("no")) {
						checkLearningPathConstraints(contentNodeToUpdate, idToContentMapping.get(childIdentifier));
					}
					checkContentSharingConstraints(contentNodeToUpdate, childNode);
	
					Map<String, Object> relationMetaData = new HashMap<>();
	
					relationMetaData.put(LexConstants.INDEX, index);
					relationMetaData.put(LexConstants.REASON, reasonAdded);
					relationMetaData.put(LexConstants.CHILDREN_CLASSIFIERS, childrenClassifiers);
					relationMetaData.put(LexConstants.ADDED_ON, addedOn);
	
					updateRelationRequests.add(new UpdateRelationRequest(contentNodeToUpdate.getIdentifier(),
							childIdentifier, relationMetaData));
	
					if (idToContentMapping.containsKey(childIdentifier + LexConstants.IMG_SUFFIX)) {
	
						ContentNode childImageNode = idToContentMapping.get(childIdentifier + LexConstants.IMG_SUFFIX);
						if (!childImageNode.getMetadata().containsKey(LexConstants.IS_STAND_ALONE)) {
							childImageNode.getMetadata().put(LexConstants.IS_STAND_ALONE, true);
						}
	
						if ((boolean) childImageNode.getMetadata().get(LexConstants.IS_STAND_ALONE) == true
								&& !childImageNode.getMetadata().get(LexConstants.CONTENT_TYPE)
										.equals(LexConstants.ContentType.Course.getContentType())
								&& !childImageNode.getMetadata().get(LexConstants.CONTENT_TYPE)
										.equals(LexConstants.ContentType.LearningPath.getContentType())
								&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
										.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
								&& !contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
										.equals(LexConstants.ContentType.Channel.getContentType())) {
							Map<String, Object> updateMap = new HashMap<>();
							updateMap.put(LexConstants.IS_STAND_ALONE, false);
							updateMetaRequests.add(new UpdateMetaRequest(childImageNode.getIdentifier(), updateMap));
						}
	
						checkContentSharingConstraints(contentNodeToUpdate,
								idToContentMapping.get(childIdentifier + LexConstants.IMG_SUFFIX));
	
						Map<String, Object> imageRelationMetaData = new HashMap<>();
						imageRelationMetaData.put(LexConstants.INDEX, index);
						imageRelationMetaData.put(LexConstants.REASON, reasonAdded);
						imageRelationMetaData.put(LexConstants.CHILDREN_CLASSIFIERS, childrenClassifiers);
						imageRelationMetaData.put(LexConstants.ADDED_ON, addedOn);
	
						updateRelationRequests.add(new UpdateRelationRequest(contentNodeToUpdate.getIdentifier(),
								childIdentifier + LexConstants.IMG_SUFFIX, imageRelationMetaData));
					}
					index++;
				}
			}
		}

		graphService.deleteChildren(rootOrg, idsForChildrenDeletion, transaction);
		graphService.updateNodesV2(rootOrg, updateMetaRequests, transaction);
		graphService.mergeRelations(rootOrg, updateRelationRequests, transaction);
		
		if(authoringDisabledIds.size()>0) {
			throw new BadRequestException("Invalid ids : " + authoringDisabledIds);
		}
	}

	private List<Map<String, Object>> removeDuplicatesFromList(List<Map<String, Object>> childrenMaps) {
		List<Map<String,Object>> resultList = new ArrayList<>();
		for(Map<String, Object> item:childrenMaps) {
			if(!resultList.contains(item)) {
				resultList.add(item);
			}
		}
		return resultList;
	}

	@SuppressWarnings("unchecked")
	private void validateChildContentType(List<Map<String,Object>> validChildrenData, ContentNode childNode,int position,int size) {
		//ENHANCE CONDITIONS MAP TO SUPPORT STATUS CHECK
		List<String> validChildrenContentTypes = new ArrayList<>();
		List<String> notAllowedToUpdateStatuses = Arrays.asList(LexConstants.Status.Expired.getStatus(),LexConstants.Status.Deleted.getStatus(),LexConstants.Status.UnPublish.getStatus());
		for(Map<String, Object> childrenData : validChildrenData) {
			validChildrenContentTypes.add((String) childrenData.get("childContentType"));
		}
		
		String childContentType = (String) childNode.getMetadata().get(LexConstants.CONTENT_TYPE);
		
		if(!validChildrenContentTypes.contains(childContentType)) {
			throw new ConflictErrorException("Cannot add content of type : " + childContentType + " into parent contentType", null);
		}
		
		Map<String,Object> dataToCompare = new HashMap<>();
		
		for(Map<String, Object> childData:validChildrenData) {
			if(childData.get("childContentType").equals(childNode.getMetadata().get(LexConstants.CONTENT_TYPE))) {
				dataToCompare=childData;
			}
		}
		
			Map<String,Object> conditionsMap = (Map<String, Object>) dataToCompare.get("condition");
			String positionData = (String) dataToCompare.get("position");
			if(conditionsMap!=null && !(conditionsMap.isEmpty())) {
				Set<String> metaKeysToCheck = conditionsMap.keySet();
				for(String metaKey : metaKeysToCheck) {
					String metaValueToCheck = (String) conditionsMap.get(metaKey);
					String valueFromMeta = (String) childNode.getMetadata().get(metaKey);
					System.out.println(childNode.getMetadata());
					if(!metaValueToCheck.equals(valueFromMeta)) {
						throw new ConflictErrorException("Cannot add : " + childContentType + " unless " + metaKey + " is equal to : " + metaValueToCheck, null);
					}
				}
			}
			
			if(notAllowedToUpdateStatuses.contains(childNode.getMetadata().get(LexConstants.STATUS))) {
				throw new ConflictErrorException("Cannot add : " + childNode.getIdentifier() + "as status is either ['Deleted','Expired','Unpublished'] ", null);
			}
			
			if(positionData!=null &&  !(positionData.isEmpty())) {
				if(positionData.equalsIgnoreCase("last")) {
					if(position!=size) {
						throw new ConflictErrorException("Child Content Type " + childContentType + " must be in last position in hierarchy" , null);
					}
				}
				else if(positionData.equalsIgnoreCase("first")) {
					if(position!=0) {
						throw new ConflictErrorException("Child Content Type " + childContentType + " must be in first position in hierarchy" , null);
					}
				}
				
			}
	}

	private ContentNode findContentNodeToUpdate(Map<String, ContentNode> idToContentMapping,
			Map.Entry<String, Object> entry) {

		ContentNode contentNodeToUpdate = idToContentMapping.get(entry.getKey());

		if (contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
				.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())) {
			return contentNodeToUpdate;
		}
		
		if (contentNodeToUpdate.getMetadata().get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {

			String imageNodeIdentifier = entry.getKey() + LexConstants.IMG_SUFFIX;
			if (idToContentMapping.containsKey(imageNodeIdentifier)) {
				return idToContentMapping.get(imageNodeIdentifier);
			} else {
				return null;
			}
		}

		return contentNodeToUpdate;
	}

	@Override
	public Map<String, Object> getContentHierarchy(String identifier, String rootOrg, String org)
			throws BadRequestException, Exception {

		Session session = neo4jDriver.session();

		Map<String, Object> hierarchyMap = new HashMap<>();
		List<String> fields = new ArrayList<>();
		hierarchyMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {
			
			@Override
			public Map<String, Object> execute(Transaction tx) {
				try {
					return getHierarchyFromNeo4j(identifier, rootOrg, tx, false, fields);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
		});
//		System.out.println(hierarchyMap);
		hierachyForViewing(hierarchyMap);
		session.close();
		return hierarchyMap;
	}
	
	
	@Override
	public Map<String, Object> getOnlyLiveContentHierarchy(String identifier, String rootOrg, String org)
			throws BadRequestException, Exception {

		Session session = neo4jDriver.session();

		Map<String, Object> hierarchyMap = new HashMap<>();
		List<String> fields = new ArrayList<>();
		hierarchyMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {
			
			@Override
			public Map<String, Object> execute(Transaction tx) {
				try {
					return getOnlyLiveHierarchyFromNeo4j(identifier, rootOrg, tx, false, fields);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				
			}
		});
		
		if(hierarchyMap==null) {
			throw new BadRequestException("Identifier does not exist : " + identifier);
		}
		removeAllDraftImgNodes(hierarchyMap);
		session.close();
		return hierarchyMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getContentHierarchyV2(String identifier, String rootOrg, String org,Map<String,Object> requestMap)
			throws BadRequestException, Exception {

		Session session = neo4jDriver.session();
		List<String> fields = (List<String>) requestMap.getOrDefault("fields", new ArrayList<>());
		Map<String, Object> hierarchyMap = new HashMap<>();
		hierarchyMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> execute(Transaction tx) {
				try {
					return getHierarchyFromNeo4jV2(identifier, rootOrg, tx, true, fields);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		
		if(hierarchyMap==null) {
			throw new BadRequestException("Could not find identifier with : " + identifier);
		}
		hierachyForViewing(hierarchyMap);
		session.close();
		return hierarchyMap;
	}
	
	
	
	public Map<String, String> contentEditorDelete(String rootOrg,String org, Map<String, Object> requestMap) throws Exception {
		
		String identifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		if(identifier==null||identifier.isEmpty()) {
			throw new BadRequestException("Invalid input");
		}
		
		List<String> idsToLogicalDelete = new ArrayList<>();
		List<String> idsToHardDelete = new ArrayList<>();
		
		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		
		ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
		
		Map<String, Object> nodeMap = node.getMetadata();
		
		Boolean isStandAlone = (Boolean) nodeMap.getOrDefault(LexConstants.IS_STAND_ALONE,false);
		
		if(isStandAlone==null||isStandAlone==false) {
			throw new ConflictErrorException("Content is stand alone is false cannot delete", null);
		}
		
		List<Relation> children = node.getChildren();
		
		if(children.size()>0) {
			throw new ConflictErrorException("Content cannot be deleted as children are present", null);
		}
		
		if(!identifier.contains(LexConstants.IMG_SUFFIX))
		{
			ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, identifier+LexConstants.IMG_SUFFIX, tx);
			if(!(imgNode==null)) {
				throw new ConflictErrorException("Content cannot be deleted as a copy img node exists, delete img node before deleting original node", null);
			}
		}
		
		String status = (String) nodeMap.get(LexConstants.STATUS);
		
		try {
			if(status.equals(LexConstants.Status.Live.getStatus())||status.equals(LexConstants.Status.MarkedForDeletion.getStatus())||status.equals(LexConstants.Status.Deleted.getStatus())) {
				idsToLogicalDelete.add(identifier);
				List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
				Map<String, Object> updateMap = new HashMap<>();
				updateMap.put(LexConstants.STATUS, LexConstants.Status.Deleted.getStatus());
				updateMetaRequests.add(new UpdateMetaRequest(identifier, updateMap));
				graphService.updateNodesV2(rootOrg, updateMetaRequests, tx);
				tx.commitAsync().toCompletableFuture().get();
			}
			else if(status.equals(LexConstants.Status.Expired.getStatus())) {
				logger.info("content is expired cannot delete : " + identifier);
			}
			else {
				idsToHardDelete.add(identifier);
				graphService.deleteNodes(rootOrg, idsToHardDelete, tx);
				tx.commitAsync().toCompletableFuture().get();
			}
			Map<String,String> resultMap = new HashMap<>();
			resultMap.put("Message", "Success");
			return resultMap;
		} 
		catch (Exception e) {
			tx.rollbackAsync().toCompletableFuture().get();
			throw e;
		}
		finally {
			tx.close();
			session.close();	
		}	
	}


	@Override
	public Map<String, String> contentDelete(Map<String, Object> requestMap, String rootOrg,String org) throws Exception {

		String identifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		String authorUuid = (String) requestMap.get(LexConstants.AUTHOR);
		Boolean isAdmin = (Boolean) requestMap.get("isAdmin");

		if (identifier == null || identifier.isEmpty() || authorUuid == null || authorUuid.isEmpty()) {
			throw new BadRequestException("Invalid input");
		}
		
		if(isAdmin==null) {
			isAdmin=false;
		}
		logger.info("Delete called for : " + identifier);
		logger.info("Request Body : " + requestMap);
		
		if(isAdmin) {
			Map<String,String> resultMap = contentEditorDelete(rootOrg, org, requestMap);
			return resultMap;
		}
		
		else {
			
			Session session = neo4jDriver.session();
			Transaction tx = session.beginTransaction();
			Map<String, Object> boolMap = new HashMap<>();
			boolMap.put("isFirstCall", true);
			ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
	
			if (node == null) {
				throw new ResourceNotFoundException("content with identifier : " + identifier + " not found");
			}
	
			Map<String, Object> idsToRetire = new HashMap<>();
			List<String> idsToHardDelete = new ArrayList<>();
			List<String> idsToLogicalDelete = new ArrayList<>();
			Map<String, String> errorMap = new HashMap<>();
			String topLevelStatus = (String) node.getMetadata().get(LexConstants.STATUS);
	
			Queue<ContentNode> contentQueue = new LinkedList<>();
			contentQueue.add(node);
	
			while (!contentQueue.isEmpty()) {
				ContentNode subNode = contentQueue.poll();
	
				// check if deletion is allowed
				boolean deletable = allowedToDelete(boolMap, subNode, authorUuid, errorMap, topLevelStatus,tx,rootOrg);
	
				if (deletable) {
					idsToRetire.put(subNode.getIdentifier(), subNode.getMetadata().get(LexConstants.STATUS));
				}
				
				//if not channel or KB add children to contentQueue
				if (checkKbChannelConstraints(subNode)) {
					contentQueue.addAll(graphService.getNodesByUniqueIdV2(rootOrg,
							subNode.getChildren().stream().map(child -> child.getEndNodeId()).collect(Collectors.toList()),
							tx));
				}
				boolMap.put("isFirstCall", false);
			}
	
			try {
				List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
				logger.info("Delete logic is applied for master data: " + idsToRetire.toString());
				hardOrLogicalDelete(idsToRetire, idsToHardDelete, idsToLogicalDelete);
				logger.info("IDs to logical delete : " + idsToLogicalDelete);
				logger.info("IDs to hard delete : " + idsToLogicalDelete);
				for (String id : idsToLogicalDelete) {
					Map<String, Object> updateMap = new HashMap<>();
					updateMap.put(LexConstants.STATUS, LexConstants.Status.Deleted.getStatus());
					updateMetaRequests.add(new UpdateMetaRequest(id, updateMap));
				}
				graphService.updateNodesV2(rootOrg, updateMetaRequests, tx);
				graphService.deleteNodes(rootOrg, idsToHardDelete, tx);
				tx.commitAsync().toCompletableFuture().get();
				return errorMap;
			} catch (Exception e) {
				tx.rollbackAsync().toCompletableFuture().get();
				throw e;
			} finally {
				tx.close();
				session.close();
			}
		}
		
	}

	private boolean allowedToDelete(Map<String, Object> boolMap, ContentNode node, String uuid,
			Map<String, String> errorMap, String topStatus,Transaction tx,String rootOrg) throws Exception {
 
		if((boolean) boolMap.get("isFirstCall")) {
			if(node.getIdentifier().contains(".img")) {
				//fetch original node parents and compare with 'node' if not same, do not allow 
				String orgIdentifier = node.getIdentifier().substring(0, node.getIdentifier().indexOf(LexConstants.IMG_SUFFIX));
				ContentNode orgNode = graphService.getNodeByUniqueIdV3(rootOrg, orgIdentifier, tx);
				if(orgNode==null) {
					throw new ApplicationLogicError("img node present, original node missing : " + orgIdentifier);
				}
				List<Relation> parents = node.getParents();
				Set<Relation> relParents = new HashSet<>(parents);
				List<Relation> orgParents = node.getParents();
				Set<Relation> orgRelParents = new HashSet<>(orgParents);
				
				boolean equality = relParents.equals(orgRelParents);
				if(!equality) {
					throw new ConflictErrorException("cannot delete as original content parents different from .img node " + orgIdentifier, null);
				}
			}
			else {
				if(node.getParents().size() > 0) {
					throw new ConflictErrorException("cannot delete content as it is reused", null);
				}
			}
			
			if(topStatus.equals(LexConstants.Status.Live.getStatus())||topStatus.equals(LexConstants.Status.MarkedForDeletion.getStatus())||topStatus.equals(LexConstants.Status.Deleted.getStatus())||topStatus.equals(LexConstants.Status.Expired.getStatus())) {
				// check to see if .img exists for 'node' if exists do not allow delete
				String identifier = node.getIdentifier() + LexConstants.IMG_SUFFIX;
				ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg,identifier , tx);
				
				if(imgNode!=null) {
					throw new ConflictErrorException("cannot delete original node because .img node is present", null);
				}
			}
		}
		
//		if ((boolean) boolMap.get("isFirstCall") && node.getParents().size() > 0) {
//			throw new ConflictErrorException("cannot delete content as it is reused", null);
//		}

		if (!(boolean) boolMap.get("isFirstCall") && node.getParents().size() > 1) {
			errorMap.put(node.getIdentifier(), "cannot delete content as it is reused");
			return false;
		}

		// top level
		if ((boolean) boolMap.get("isFirstCall")) {
			// check if content is off specified author
			if (!isGivenAuthorsContent(node.getMetadata(), uuid)) {
				throw new ConflictErrorException("cannot delete content as you are not the owner.", null);
			}
		}
		// children
		else {
			// check if content is off specified author
			if (!isGivenAuthorsContent(node.getMetadata(), uuid)) {
				errorMap.put(node.getIdentifier(), "cannot delete content as you are not the owner.");
				return false;
			}

			// if top status is not live
			if (!topStatus.equals(LexConstants.Status.Live.getStatus())) {
				String childStatus = (String) node.getMetadata().get(LexConstants.STATUS);
				// child status is live
				if (childStatus.equals(LexConstants.Status.Live.getStatus())) {
					// cannot delete, no operation
					errorMap.put(node.getIdentifier(),"Not deleting Content as parent is not Live while child is Live");
					System.out.println("Not deleting Content as parent is not Live while child is Live");
					logger.info("Not deleting Content as parent is not Live while child is Live");
					return false;
				}
			}
		}
		return true;
	}

//	public Map<String, String> contentDeletev2(String identifier, String authorEmail, String rootOrg, String userType)
//			throws Exception {
//
//		Session session = neo4jDriver.session();
//		Transaction transaction = session.beginTransaction();
//
//		if (!identifier.contains(LexConstants.IMG_SUFFIX)) {
//			identifier = identifier + LexConstants.IMG_SUFFIX;
//		}
//
//		//precedence to img node
//		ContentNode contentNode = graphService.getNodeByUniqueIdV3(rootOrg, identifier, transaction);
//		if (contentNode == null) {
//			//if no img node fetch original node
//			contentNode = graphService.getNodeByUniqueIdV3(rootOrg,
//					identifier.substring(0, identifier.indexOf(LexConstants.IMG_SUFFIX)), transaction);
//			if (contentNode == null) {
//				throw new ResourceNotFoundException("content with identifier not found");
//			}
//		}
//
//		Queue<ContentNode> contentQueue = new LinkedList<>();
//		contentQueue.add(contentNode);
//
//		boolean isFirstCall = true;
//		Map<String,Object> idsToRetire = new HashMap<>();
//		List<String> idsToHardDelete = new ArrayList<>();
//		List<String> idsToLogicalDelete = new ArrayList<>();
//
//		Map<String, String> errorMap = new HashMap<>();
//
//		// filtering out the content that can be deleted from the hierarchy.
//		while (!contentQueue.isEmpty()) {
//
//			contentNode = contentQueue.poll();
//			//isDeleteable if no parents, and owners content
//			//not deletable otherwise
//			if (isDeletable(contentNode, authorEmail, userType, isFirstCall, errorMap)) {
//				//adding all deletable ids to list
//				idsToRetire.put(contentNode.getIdentifier(), contentNode.getMetadata().get(LexConstants.STATUS));
////				idsToRetire.add(contentNode.getIdentifier());
//
//				//adding children of passed identifier other than KB and channel
//				if (checkKbChannelConstraints(contentNode)) {
//					contentQueue.addAll(graphService.getNodesByUniqueIdV2(rootOrg, contentNode.getChildren().stream()
//							.map(child -> child.getEndNodeId()).collect(Collectors.toList()), transaction));
//				}
//				isFirstCall = false;
//			}
//		}
//
//		try {
//			List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();
//			hardOrLogicalDelete(idsToRetire,idsToHardDelete,idsToLogicalDelete);
//			for (String id : idsToLogicalDelete) {
//				Map<String, Object> updateMap = new HashMap<>();
//				updateMap.put(LexConstants.STATUS, LexConstants.Status.Deleted.getStatus());
//				updateMetaRequests.add(new UpdateMetaRequest(id, updateMap));
//			}
//			graphService.updateNodesV2(rootOrg, updateMetaRequests, transaction);
//			graphService.deleteNodes(rootOrg, idsToHardDelete, transaction);
//			transaction.commitAsync().toCompletableFuture().get();
//			
//			return errorMap;
//		} catch (Exception e) {
//			transaction.rollbackAsync().toCompletableFuture().get();
//			throw e;
//		} finally {
//			transaction.close();
//			session.close();
//		}
//	}

	private void hardOrLogicalDelete(Map<String, Object> inputList, List<String> idsToHardDelete,
			List<String> idsToLogicalDelete) {
		for (String id : inputList.keySet()) {
			if (id.contains(".img")) {
				idsToHardDelete.add(id);
			}
			if (inputList.get(id).equals("Live")||inputList.get(id).equals(LexConstants.Status.MarkedForDeletion.getStatus())||inputList.get(id).equals(LexConstants.Status.Deleted.getStatus())) {
				idsToLogicalDelete.add(id);
			}
			else if(inputList.get(id).equals("Expired")) {
				logger.info("Cannot delete as content is Expired : " + id);
			}
			else {
				idsToHardDelete.add(id);
			}
		}
	}

	private boolean checkKbChannelConstraints(ContentNode contentNode) {

		return !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
				.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
				&& !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.Channel.getContentType());
	}

	@SuppressWarnings("unused")
	private boolean isDeletable(ContentNode contentNode, String authorEmail, String userType, boolean isFirstCall,
			Map<String, String> errorMap) {

		if (isFirstCall && contentNode.getParents().size() > 0) {
			throw new ConflictErrorException("cannot delete content as it is reused", null);
		}

		if (!isFirstCall && contentNode.getParents().size() > 1) {
			errorMap.put(contentNode.getIdentifier(), "cannot delete content as it is reused");
			return false;
		}

		if (isFirstCall) {
			if (!isGivenAuthorsContent(contentNode.getMetadata(), authorEmail)) {
				throw new ConflictErrorException("cannot delete content as you are not the owner.", null);
			}
		} else {
			if (!isGivenAuthorsContent(contentNode.getMetadata(), authorEmail)) {
				errorMap.put(contentNode.getIdentifier(), "cannot delete content as you are not the owner.");
				return false;
			}
		}

		return true;
	}

//	@SuppressWarnings("unchecked")
//	private Response publishContent(Map<String, Object> contentMeta, String rootOrg) throws Exception {
//		Response response = new Response();
//		String identifier = contentMeta.get(LexConstants.IDENTIFIER).toString();
//		Session session = neo4jDriver.session();
//		Transaction transaction = session.beginTransaction();
//		Map<String, Object> kafkaMap = new HashMap<>();
//		List<String> listOfIds = new ArrayList<>();
//		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
//		parentObjs.add(contentMeta);
//		while (!parentObjs.isEmpty()) {
//			Map<String, Object> parent = parentObjs.poll();
//			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
//			parent.put(LexConstants.STATUS, "Processing");
//			listOfIds.add(parent.get(LexConstants.IDENTIFIER).toString());
//			for (Map<String, Object> child : childrenList) {
//				parentObjs.add(child);
//			}
//		}
//		listOfIds = listOfIds.stream().map(id -> "'" + id + "'").collect(Collectors.toList());
//		kafkaMap.put("topLevelContentId", identifier);
//		kafkaMap.put("contentIds", listOfIds);
//		kafkaMap.put("rootOrg", rootOrg);
//		ObjectMapper mapper = new ObjectMapper();
//
//		try {
//			String query = "unwind " + listOfIds + " as data match(n:" + rootOrg
//					+ "{identifier:data}) set n.status=\"Processing\" return n";
//			List<Record> records = transaction.run(query).list();
//			if (listOfIds.size() == records.size()) {
//				transaction.commitAsync().toCompletableFuture().get();
//				String kafkaMessage = mapper.writer().writeValueAsString(kafkaMap);
//				kafkaTemplate.send("dev.learning.graph.events", "Publish-Pipeline-Stage-1", kafkaMessage);
//				response.put("Message", "Operation Successful: Sent to Publish Pipeline");
//				return response;
//			} else {
//				throw new ApplicationLogicError("Something went wrong");
//			}
//		} catch (Exception e) {
//			transaction.rollbackAsync().toCompletableFuture().get();
//			throw e;
//		} finally {
//			session.close();
//		}
//	}

	@SuppressWarnings("unchecked")
	private boolean isGivenAuthorsContent(Map<String, Object> contentMeta, String authorEmail)
			throws BadRequestException {

		List<Map<String, Object>> creatorContacts = (List<Map<String, Object>>) contentMeta
				.get(LexConstants.CREATOR_CONTACTS);

		for (Map<String, Object> creatorContact : creatorContacts) {
			if (creatorContact.get(LexConstants.ID).equals(authorEmail)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getHierarchyFromNeo4j(String identifier, String rootOrg, Transaction tx,
			boolean fieldsPassed, List<String> fields) throws BadRequestException,Exception{

		// query for image node fetch
//		String query = "match(n{identifier:'" + identifier + ".img'}) where n:Shared or n:" + rootOrg
//				+ " with n optional match(n)-[r:Has_Sub_Content*]->(s) where s:Shared or s:" + rootOrg
//				+ " return n,r,s";
		String query = "optional match(x:Shared{identifier:'" + identifier + ".img'}) with x optional match(x1:"
				+ rootOrg + "{identifier:'" + identifier + ".img'})"
				+ " with case when x is not NULL then x when x1 is not NULL then x1 end as n with n optional match(n)-[r:Has_Sub_Content*]->(s) return n,r,s";
		boolean gotResultFromImage = false;
		StatementResult statementResult = tx.run(query);
		List<Record> records = statementResult.list();
		for (Record recordimg : records) {
			if (!recordimg.get("n").isNull()) {
				gotResultFromImage = true;
			}
			// hierarchy fetched for resource.img
			Map<String, Object> resourceMap = new HashMap<>();
			if ((!recordimg.get("n").isNull()) && (recordimg.get("r").isNull() && recordimg.get("s").isNull())) {
				gotResultFromImage = true;
				org.neo4j.driver.v1.types.Node startNode = recordimg.get("n").asNode();
				resourceMap = startNode.asMap();
				Map<String, Object> newResourceMap = new HashMap<>();
				if (fieldsPassed) {
					newResourceMap = fieldsRequired(fields, resourceMap);
				} else {
					newResourceMap = new HashMap<>(resourceMap);
				}
				List<Map<String, Object>> childrenList = new ArrayList<>();
				newResourceMap.put(LexConstants.CHILDREN, childrenList);
				return newResourceMap;
			}
		}
		if (!gotResultFromImage) {
//		if (records.size() == 0) {
			// query for node fetch //if img node does not exist
//			query = "match(n{identifier:'" + identifier + "'}) where n:Shared or n:" + rootOrg
//					+ " with n optional match(n)-[r:Has_Sub_Content*]->(s) where s:Shared or s:" + rootOrg
//					+ " return n,r,s";
			query = "optional match(x:Shared{identifier:'" + identifier + "'}) with x optional match(x1:" + rootOrg
					+ "{identifier:'" + identifier + "'})"
					+ " with case when x is not NULL then x when x1 is not NULL then x1 end as n with n optional match(n)-[r:Has_Sub_Content*]->(s) return n,r,s";
			statementResult = tx.run(query);
			records = statementResult.list();
			for (Record record : records) {
				if (record.get("n").isNull()) {
					throw new BadRequestException("Identifier does not exist : " + identifier);
				}
			}
//			if (records == null || records.size() == 0) {
//				
//				if (records.size() == 0) {
//					throw new BadRequestException("Identifier does not exist : " + identifier);
//				}
//			}
		}

		Map<String, Map<String, Object>> idToNodeMapping = new HashMap<>();
		Map<String, String> relationToLexIdMap = new HashMap<>();
		Map<String, Object> hierarchyMap = new HashMap<>();
		Map<String, Object> visitedMap = new HashMap<>();
		for (Record record : records) {
			// hierarchy fetched for resource
			if (record.get("r").isNull() && record.get("s").isNull()) {
				Map<String, Object> resourceMap = new HashMap<>();
				org.neo4j.driver.v1.types.Node startNode = record.get("n").asNode();
				resourceMap = startNode.asMap();
				Map<String, Object> newResourceMap = new HashMap<>();
				if (fieldsPassed) {
					newResourceMap = fieldsRequired(fields, resourceMap);
				} else {
					newResourceMap = new HashMap<>(resourceMap);
				}
				List<Map<String, Object>> childrenList = new ArrayList<>();
				newResourceMap.put(LexConstants.CHILDREN, childrenList);
				return newResourceMap;
			}

			List<Object> relations = (record.get("r")).asList();
			org.neo4j.driver.v1.types.Node startNode = record.get("n").asNode();
			org.neo4j.driver.v1.types.Node endNode = record.get("s").asNode();

			String sourceId = startNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");
			String destinationId = endNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");

			if (fieldsPassed) {
				Map<String, Object> sNodeMap = fieldsRequired(fields, startNode.asMap());
				Map<String, Object> eNodeMap = fieldsRequired(fields, endNode.asMap());
				idToNodeMapping.put(sourceId, sNodeMap);
				idToNodeMapping.put(destinationId, eNodeMap);
			} else {
				idToNodeMapping.put(sourceId, startNode.asMap());
				idToNodeMapping.put(destinationId, endNode.asMap());
			}

			String immediateParentId = sourceId;

			for (Object relation : relations) {
				if (!relationToLexIdMap.containsKey(relation.toString())) {
					relationToLexIdMap.put(relation.toString(), destinationId);
					Map<String, Object> parentMap = new HashMap<>();
					// called only once for that identifier whose hierarchy is
					// begin fetched
					if (!visitedMap.containsKey(immediateParentId)) {
						parentMap.putAll(idToNodeMapping.get(immediateParentId));
						hierarchyMap.put("content", parentMap);
						visitedMap.put(immediateParentId, parentMap);
					} else {
						parentMap = (Map<String, Object>) visitedMap.get(immediateParentId);
					}
					List<Map<String, Object>> children = new ArrayList<>();
					if (parentMap.containsKey(LexConstants.CHILDREN)) {
						children = (List<Map<String, Object>>) parentMap.get(LexConstants.CHILDREN);
					}
					Map<String, Object> child = new HashMap<>();
					visitedMap.put(destinationId, child);
					// child.put("id", destinationId);
					child.putAll(idToNodeMapping.get(destinationId));
					child.put(LexConstants.INDEX, ((Relationship) relation).asMap().get(LexConstants.INDEX));
					child.put(LexConstants.REASON,
							((Relationship) relation).asMap().getOrDefault(LexConstants.REASON, ""));
					child.put(LexConstants.CHILDREN_CLASSIFIERS, ((Relationship) relation).asMap()
							.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>()));
					child.put(LexConstants.CHILDREN, new ArrayList<>());
					children.add(child);
					parentMap.put(LexConstants.CHILDREN, children);
				} else {
					immediateParentId = relationToLexIdMap.get(relation.toString());
				}
			}
		}
		return orderChildren(hierarchyMap);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getOnlyLiveHierarchyFromNeo4j(String identifier, String rootOrg, Transaction tx,
			boolean fieldsPassed, List<String> fields) throws BadRequestException,Exception{

			String query = "optional match(x:Shared{identifier:'" + identifier + "'}) with x optional match(x1:" + rootOrg
					+ "{identifier:'" + identifier + "'})"
					+ " with case when x is not NULL then x when x1 is not NULL then x1 end as n with n optional match(n)-[r:Has_Sub_Content*]->(s) return n,r,s";
			StatementResult statementResult = tx.run(query);
			List<Record> records = statementResult.list();
			for (Record record : records) {
				if (record.get("n").isNull()) {
					throw new BadRequestException("Identifier does not exist : " + identifier);
				}
			}

		Map<String, Map<String, Object>> idToNodeMapping = new HashMap<>();
		Map<String, String> relationToLexIdMap = new HashMap<>();
		Map<String, Object> hierarchyMap = new HashMap<>();
		Map<String, Object> visitedMap = new HashMap<>();
		for (Record record : records) {
			// hierarchy fetched for resource
			if (record.get("r").isNull() && record.get("s").isNull()) {
				Map<String, Object> resourceMap = new HashMap<>();
				org.neo4j.driver.v1.types.Node startNode = record.get("n").asNode();
				resourceMap = startNode.asMap();
				Map<String, Object> newResourceMap = new HashMap<>();
				if (fieldsPassed) {
					newResourceMap = fieldsRequired(fields, resourceMap);
				} else {
					newResourceMap = new HashMap<>(resourceMap);
				}
				List<Map<String, Object>> childrenList = new ArrayList<>();
				newResourceMap.put(LexConstants.CHILDREN, childrenList);
				return newResourceMap;
			}

			List<Object> relations = (record.get("r")).asList();
			org.neo4j.driver.v1.types.Node startNode = record.get("n").asNode();
			org.neo4j.driver.v1.types.Node endNode = record.get("s").asNode();

			String sourceId = startNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");
			String destinationId = endNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");

			if (fieldsPassed) {
				Map<String, Object> sNodeMap = fieldsRequired(fields, startNode.asMap());
				Map<String, Object> eNodeMap = fieldsRequired(fields, endNode.asMap());
				idToNodeMapping.put(sourceId, sNodeMap);
				idToNodeMapping.put(destinationId, eNodeMap);
			} else {
				idToNodeMapping.put(sourceId, startNode.asMap());
				idToNodeMapping.put(destinationId, endNode.asMap());
			}

			String immediateParentId = sourceId;

			for (Object relation : relations) {
				if (!relationToLexIdMap.containsKey(relation.toString())) {
					relationToLexIdMap.put(relation.toString(), destinationId);
					Map<String, Object> parentMap = new HashMap<>();
					// called only once for that identifier whose hierarchy is
					// begin fetched
					if (!visitedMap.containsKey(immediateParentId)) {
						parentMap.putAll(idToNodeMapping.get(immediateParentId));
						hierarchyMap.put("content", parentMap);
						visitedMap.put(immediateParentId, parentMap);
					} else {
						parentMap = (Map<String, Object>) visitedMap.get(immediateParentId);
					}
					List<Map<String, Object>> children = new ArrayList<>();
					if (parentMap.containsKey(LexConstants.CHILDREN)) {
						children = (List<Map<String, Object>>) parentMap.get(LexConstants.CHILDREN);
					}
					Map<String, Object> child = new HashMap<>();
					visitedMap.put(destinationId, child);
					// child.put("id", destinationId);
					child.putAll(idToNodeMapping.get(destinationId));
					child.put(LexConstants.INDEX, ((Relationship) relation).asMap().get(LexConstants.INDEX));
					child.put(LexConstants.REASON,
							((Relationship) relation).asMap().getOrDefault(LexConstants.REASON, ""));
					child.put(LexConstants.CHILDREN_CLASSIFIERS, ((Relationship) relation).asMap()
							.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>()));
					child.put(LexConstants.CHILDREN, new ArrayList<>());
					children.add(child);
					parentMap.put(LexConstants.CHILDREN, children);
				} else {
					immediateParentId = relationToLexIdMap.get(relation.toString());
				}
			}
		}
		return orderChildren(hierarchyMap);
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getHierarchyFromNeo4jV2(String identifier, String rootOrg, Transaction tx,
			boolean fieldsPassed, List<String> fields) throws BadRequestException,Exception {
		
		Map<String,Object> hMap = new HashMap<>();
		Map<String, Map<String, Object>> idToNodeMapping = new HashMap<>();
		Map<String, String> relationToLexIdMap = new HashMap<>();
		Map<String, Object> hierarchyMap = new HashMap<>();
		Map<String, Object> visitedMap = new HashMap<>();
		fields = new ArrayList<>(fields);
		boolean gotResultFromImage = false;
		
		if(fieldsPassed) {
			if(fields==null||fields.isEmpty()) {
				hMap = getHierarchyFromNeo4j(identifier, rootOrg, tx, false, fields);
				return hMap;
			}
			fields.add("identifier");
			fields.add("creatorContacts");
			Set<String> fieldValues = new HashSet<>(fields);
			fields = new ArrayList<>(fieldValues);
			String query = constructFetchQuery(identifier,rootOrg,fields,true);
			
			StatementResult statementResult = tx.run(query);
			List<Record> records = statementResult.list();
			for(Record recordImg : records) {
				if(!recordImg.get("n.identifier").isNull()) {
					gotResultFromImage = true;
				}
				if((!recordImg.get("n.identifier").isNull()) && (recordImg.get("r").isNull() && recordImg.get("s.identifier").isNull())) {
					gotResultFromImage = true;
					Map<String,Object> nodeMap = createNodeMapData("n",fields,recordImg);
//					nodeMap = GraphUtil.mapParser(nodeMap, true);
					nodeMap.put(LexConstants.CHILDREN, new ArrayList<>());
					return nodeMap;
				}
			}
			
			if(!gotResultFromImage) {
				query = constructFetchQuery(identifier, rootOrg, fields, false);
				System.out.println(query);
				statementResult = tx.run(query);
				records = statementResult.list();
				for(Record record:records) {
					if (record.get("n.identifier").isNull()) {
						throw new BadRequestException("Identifier does not exist : " + identifier);
					}
				}
			}
			
			for(Record record:records) {

				if (record.get("r").isNull() && record.get("s.identifier").isNull()) {
					Map<String,Object> nodeMap = createNodeMapData("n", fields, record);
//					nodeMap = GraphUtil.mapParser(nodeMap, true);
					nodeMap.put(LexConstants.CHILDREN, new ArrayList<>());
					return nodeMap;
				}
				
				List<Object> relations = (record.get("r")).asList();
				Map<String,Object> startNode = createNodeMapData("n", fields, record);
				Map<String,Object> endNode = createNodeMapData("s", fields, record);
				
				String sourceId = startNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");
				String destinationId = endNode.get(LexConstants.IDENTIFIER).toString().replace("\"", "");
				
				String immediateParentId = sourceId;
				
				idToNodeMapping.put(sourceId, startNode);
				idToNodeMapping.put(destinationId, endNode);
				
				for (Object relation : relations) {
					if (!relationToLexIdMap.containsKey(relation.toString())) {
						relationToLexIdMap.put(relation.toString(), destinationId);
						Map<String, Object> parentMap = new HashMap<>();
						// called only once for that identifier whose hierarchy is
						// begin fetched
						if (!visitedMap.containsKey(immediateParentId)) {
							parentMap.putAll(idToNodeMapping.get(immediateParentId));
//							parentMap = GraphUtil.mapParser(parentMap, true);
							hierarchyMap.put("content", parentMap);
							visitedMap.put(immediateParentId, parentMap);
						} else {
							parentMap = (Map<String, Object>) visitedMap.get(immediateParentId);
						}
						List<Map<String, Object>> children = new ArrayList<>();
						if (parentMap.containsKey(LexConstants.CHILDREN)) {
							children = (List<Map<String, Object>>) parentMap.get(LexConstants.CHILDREN);
						}
						Map<String, Object> child = new HashMap<>();
						visitedMap.put(destinationId, child);
						// child.put("id", destinationId);
						child.putAll(idToNodeMapping.get(destinationId));
						child.put(LexConstants.INDEX, ((Relationship) relation).asMap().get(LexConstants.INDEX));
						child.put(LexConstants.REASON,
								((Relationship) relation).asMap().getOrDefault(LexConstants.REASON, ""));
						child.put(LexConstants.CHILDREN_CLASSIFIERS, ((Relationship) relation).asMap()
								.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>()));
						child.put(LexConstants.CHILDREN, new ArrayList<>());
						children.add(child);
						parentMap.put(LexConstants.CHILDREN, children);
					} else {
						immediateParentId = relationToLexIdMap.get(relation.toString());
					}
				}
				
			}
			return orderChildren(hierarchyMap);
			
		}
		else {
			hMap = getHierarchyFromNeo4j(identifier, rootOrg, tx, false, fields);
			return hMap;
		}
	}

	private Map<String, Object> createNodeMapData(String nodeId, List<String> fields,Record record) {
		
		Map<String,Object> nodeMap = new HashMap<>();
		for(String field: fields) {
//				Value value = record.get(nodeId+"."+field);
				Map<String,Object> x = record.asMap();
				nodeMap.put(field, x.get(nodeId+"."+field));
		}
		return nodeMap;
	}

	private String constructFetchQuery(String identifier, String rootOrg, List<String> fields,Boolean isImg) {
		String vals = " return n.identifier ,s.identifier, n.creatorContacts, s.creatorContacts";
		String query = null;
		if(isImg) {
			 query = "optional match(x:Shared{identifier:'" + identifier + ".img'}) with x optional match(x1:"
					+ rootOrg + "{identifier:'" + identifier + ".img'})"
					+ " with case when x is not NULL then x when x1 is not NULL then x1 end as n with n optional match(n)-[r:Has_Sub_Content*]->(s) ";
			for (String item :fields) {
				if(!item.equals(LexConstants.IDENTIFIER)) {
					if(!item.equals(LexConstants.CREATOR_CONTACTS)) {
						vals = vals + " ,n." + item + " ,s." +item;
					}
				}
			}
			vals = vals + " ,r";
			query = query + vals;
		}
		else {
			 query = "optional match(x:Shared{identifier:'" + identifier + "'}) with x optional match(x1:"
						+ rootOrg + "{identifier:'" + identifier + "'})"
						+ " with case when x is not NULL then x when x1 is not NULL then x1 end as n with n optional match(n)-[r:Has_Sub_Content*]->(s) ";
			 for (String item :fields) {
					if(!item.equals(LexConstants.IDENTIFIER)) {
						if(!item.equals(LexConstants.CREATOR_CONTACTS)) {
							vals = vals + " ,n." + item + " ,s." +item;
						}
					}
				}
				vals = vals + " ,r";
				query = query + vals;
		}
		return query;
	}

	public List<Map<String, Object>> getReverseHierarchyFromNeo4jForDurationUpdate(String identifier, String rootOrg,
			Transaction tx) {

		// query for image node fetch
		String query = "match(n{identifier:'" + identifier + ".img'}) where n:Shared or n:" + rootOrg
				+ " with n optional match(n)<-[r:Has_Sub_Content*]-(s) where s:Shared or s:" + rootOrg
				+ " return {identifier:s.identifier,duration:s.duration,size:s.size} as parentData";

		StatementResult statementResult = tx.run(query);
		List<Record> records = statementResult.list();

		if (null != records && records.size() > 0) {
			List<Map<String, Object>> parentsData = new ArrayList<>();
			for (Record recordImg : records) {
				Map<String, Object> parentData = recordImg.get("parentData").asMap();
				if (!parentData.isEmpty())
					parentsData.add(parentData);
			}
			return parentsData;
		}

		query = "match(n{identifier:'" + identifier + "'}) where n:Shared or n:" + rootOrg
				+ " with n optional match(n)<-[r:Has_Sub_Content*]-(s) where s:Shared or s:" + rootOrg
				+ " return {identifier:s.identifier,duration:s.duration} as parentData";

		statementResult = tx.run(query);
		records = statementResult.list();

		if (null != records && records.size() > 0) {
			List<Map<String, Object>> parentsData = new ArrayList<>();
			for (Record record : records) {
				Map<String, Object> parentData = record.get("parentData").asMap();
				if (!parentData.isEmpty())
					parentsData.add(parentData);
			}
			return parentsData;
		}

		return new ArrayList<>();
	}

//	@Override
//	public Response externalContentPublish(String identifier, Map<String, Object> requestBody)
//			throws BadRequestException, Exception {
//		String rootOrg = null;
//		String org = null;
//		String commentMessage;
//		String actor;
//		String appName;
//		String appUrl;
//		try {
//			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
//			if (rootOrg.isEmpty() || rootOrg == null) {
//				throw new BadRequestException("rootOrg is Empty");
//			}
//			org = (String) requestBody.get(LexConstants.ORG);
//			if (org.isEmpty() || org == null) {
//				throw new BadRequestException("org is Empty");
//			}
//			commentMessage = (String) requestBody.get(LexConstants.COMMENT);
//			if (commentMessage == null || commentMessage.isEmpty()) {
//				throw new BadRequestException("commentMessage is Empty");
//			}
//			actor = (String) requestBody.get(LexConstants.ACTOR);
//			if (actor.isEmpty() || actor == null) {
//				throw new BadRequestException("actor is Empty");
//			}
//			appName = (String) requestBody.get(LexConstants.APPNAME);
//			if (appName == null || appName.isEmpty()) {
//				throw new BadRequestException("appName is Empty");
//			}
//			appUrl = (String) requestBody.get(LexConstants.APPURL);
//			if (appUrl == null || appUrl.isEmpty()) {
//				throw new BadRequestException("appUrl is Empty");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e);
//		}
//
//		Session session = neo4jDriver.session();
//
//		// transaction started
//		Transaction transaction = session.beginTransaction();
//		// returns complete contentMeta along with conflicting siblings
//		List<String> fields = Arrays.asList();
//		Map<String, Object> contentMeta = getContentHierarchy(identifier, rootOrg, org);
//		Response response = new Response();
//		boolean isExternal = false;
//		try {
//			isExternal = (boolean) contentMeta.get(LexConstants.ISEXTERNAL);
//		} catch (Exception e) {
//			throw new BadRequestException("isExternal is corrupt not bool value");
//		}
//
//		if (!isExternal) {
//			throw new BadRequestException("Content is not external");
//		}
//		// returns all contentMaps in a flat list
//		List<Map<String, Object>> listOfContentMetas = convertToFlatList(contentMeta);
//		// returns all contentMaps along with comments
//		List<Map<String, Object>> allContentMetas = addComments(listOfContentMetas, identifier, commentMessage, actor);
//
//		Map<String, Set<String>> errorList = validationsService.contentHierarchyValidations(rootOrg, contentMeta);
//		if (!errorList.isEmpty()) {
//			throw new ConflictErrorException("Message", errorList);
//		}
//		Set<String> contentIds = getIdsFromHierarchyMap(contentMeta);
//		List<UpdateMetaRequest> updateListMap = createUpdateListForStatusChange(allContentMetas, "Processing", actor,
//				true);
//		response = publishContentV2(rootOrg, updateListMap, transaction, contentIds, identifier, appName, appUrl, org);
//		return response;
//	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Response statusChange(String identifier, Map<String, Object> requestBody)
			throws BadRequestException, Exception {

		String rootOrg = null;
		String org = null;
		Integer change = null;
		String commentMessage;
		String actor;
		String appName;
		String appUrl;
		Boolean skipValidations = (Boolean) requestBody.get("skipValidations");
		
		rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
		if (rootOrg.isEmpty() || rootOrg == null) {
			throw new BadRequestException("rootOrg is Empty");
		}
		org = (String) requestBody.get(LexConstants.ORG);
		if (org.isEmpty() || org == null) {
			throw new BadRequestException("org is Empty");
		}
		change = (Integer) requestBody.get(LexConstants.OPERATION);
		commentMessage = (String) requestBody.get(LexConstants.COMMENT);
		if (commentMessage == null || commentMessage.isEmpty()) {
			throw new BadRequestException("commentMessage is Empty");
		}
		actor = (String) requestBody.get(LexConstants.ACTOR);
		if (actor.isEmpty() || actor == null) {
			throw new BadRequestException("actor is Empty");
		}
		appName = (String) requestBody.get(LexConstants.APPNAME);
		if (appName == null || appName.isEmpty()) {
			throw new BadRequestException("appName is Empty");
		}
		appUrl = (String) requestBody.get(LexConstants.APPURL);
		if (appUrl == null || appUrl.isEmpty()) {
			throw new BadRequestException("appUrl is Empty");
		}

		// returns complete contentMeta without conflicting siblings
		Map<String,Object> requestMap = new HashMap<>();
		Map<String, Object> contentMeta = getContentHierarchyV2(identifier, rootOrg, org, requestMap);

		// remove children for KnowledgeBoard and channel
		if (contentMeta.get(LexConstants.CONTENT_TYPE).equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
				|| contentMeta.get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.Channel.getContentType())) {

			contentMeta.put(LexConstants.CHILDREN, new ArrayList<>());
		}

		Response response = new Response();
		String currentStatus = contentMeta.get(LexConstants.STATUS).toString();
		String contentType = contentMeta.get(LexConstants.CONTENT_TYPE).toString();
		// delta is used for +1 -1 logic in contentWorkFlow
		Integer delta = null;
		try {
			if (change > 0) {
				delta = 1;
			} else if (change < 0) {
				delta = -1;
			} else if (change == 0) {
				delta = 0;
			}
		} catch (Exception e) {
			throw new BadRequestException("Invalid Input change : " + change);
		}

		// cassandra operation to fetch work-flow for given root_org
		ContentWorkFlowModel casResult = contentWorkFlowRepo.findByPrimaryKeyContentWorkFlow(rootOrg, org, contentType);
		if (casResult == null) {
			throw new BadRequestException("Could not find any data from table for rootOrg: " + rootOrg + ", org: " + org
					+ ", contentType: " + contentType);
		}
		// Stores the contentWorkFlow LifeCycle for given root_org and contentType
		List<String> contentWorkFlow = casResult.getContent_work_flow();
		if (contentWorkFlow == null || contentWorkFlow.isEmpty()) {
			throw new ApplicationLogicError(
					"Table data is corrupt for root_org: " + rootOrg + " org: " + org + " contentType: " + contentType);
		}
		// Stores all functions to be performed during a statusChange operation
		List<String> workFlowOperations = casResult.getWork_flow_operations();
		if (workFlowOperations == null || workFlowOperations.isEmpty()) {
			throw new ApplicationLogicError(
					"Table data is corrupt for root_org: " + rootOrg + " org: " + org + " contentType: " + contentType);
		}

		// index stores integer value of current status
		int index = contentWorkFlow.indexOf(currentStatus);
		if (index == 0 && delta == -1) {
			throw new BadRequestException(
					"Content already at stage: " + currentStatus + ", Cannot go back any further");
		}

		String nextStatus;
		if (delta == 0) {
			// draft
			nextStatus = contentWorkFlow.get(0);
		} else {
			nextStatus = contentWorkFlow.get(index + delta);
		}

		int opVal = contentWorkFlow.indexOf(nextStatus);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> operationsMap = new HashMap<>();

		try {
			// stores all operations that need to be performed for given nextStatus
			operationsMap = mapper.readValue(workFlowOperations.get(opVal), HashMap.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		boolean operationsComplete = false;
		if(skipValidations==null||skipValidations==false) {
			List<String> operationsToBePerformed = (List<String>) operationsMap.get(nextStatus);
			// call validations only in-case of forward movement of content
			if (delta > 0) {
				for (String operation : operationsToBePerformed) {
					if (operation.equalsIgnoreCase("validations")) {
						Map<String, Set<String>> errorList = validationsService.contentHierarchyValidations(rootOrg,
								contentMeta);
						if (!errorList.isEmpty()) {
							throw new ConflictErrorException("Validation Failed", errorList);
						}
					}
				}
			}
		}
		operationsComplete = true;

		// returns the contents corresponding to the top-level creators
		hierarchyForStatusChange(contentMeta);

		// validates all children to check if status is appropriate
		// validateChildrenStatus(contentMeta, currentStatus);
		
		//calculating hasAssessment
		calculateHasAssessment(contentMeta);

		// all content-ids corresponding to the given author
		Set<String> contentIds = getIdsFromHierarchyMap(contentMeta);

		// the main identifier
		identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);

		// childTitle and childDesc is calculated here
		calcChildTitleDesc(contentMeta);

		// check if top level identifier is standAlone or not
		boolean isStandAlone = checkStandAlone(identifier, rootOrg, contentType);
		contentMeta.put(LexConstants.ISSTANDALONE, isStandAlone);

		// returns all contentMaps in a flat list
		List<Map<String, Object>> listOfContentMetas = convertToFlatList(contentMeta);

		List<Map<String, Object>> finalListOfContentMeta = new ArrayList<>();
		// incase of forward movement
		if (delta > 0) {
			// removes all Live & those contents which are ahead
			finalListOfContentMeta = filterLiveContentsForwardMovement(listOfContentMetas, currentStatus,
					contentWorkFlow);
		}
		// incase of backward movement
		else {
			// removes all Live, allows contents which are ahead to be pulled to previous
			// state
			finalListOfContentMeta = filterLiveContentsBackwardMovement(listOfContentMetas, currentStatus,
					contentWorkFlow);
		}

		// returns all contentMaps along with comments
		List<Map<String, Object>> allContentMetas = addComments(finalListOfContentMeta, identifier, commentMessage,
				actor);

		Session session = neo4jDriver.session();

		Transaction transaction = session.beginTransaction();
		if (operationsComplete) {
			if (opVal == (contentWorkFlow.size() - 1)) {
				List<UpdateMetaRequest> updateListMap = createUpdateListForStatusChange(allContentMetas, nextStatus,
						actor, true);
				response = publishContentV2(rootOrg, updateListMap, transaction, contentIds, identifier, appName,
						appUrl, org);
			} else {
				try {
					List<UpdateMetaRequest> updateListMap = createUpdateListForStatusChange(allContentMetas, nextStatus,
							actor, false);
					graphService.updateNodesV2(rootOrg, updateListMap, transaction);
					transaction.commitAsync().toCompletableFuture().get();
					response.put("Message", "Operation Successful, Status has been changed to: " + nextStatus);
				} catch (Exception e) {
					e.printStackTrace();
					transaction.rollbackAsync().toCompletableFuture().get();
					throw e;
				} finally {
					session.close();
				}
			}
		}
		return response;
	}

	

	private boolean checkStandAlone(String identifier, String rootOrg, String contentType) {
		String query = "match(n{identifier:'" + identifier + "'})-[r:Has_Sub_Content]->(s) where n:Shared or n:"
				+ rootOrg + " and s:Shared or s:" + rootOrg + " return s";
		Session session = neo4jDriver.session();
		Map<String, Object> parentMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> execute(Transaction tx) {
				return runQuery(query, tx);
			}
		});
		session.close();
		if (contentType.equals(LexConstants.ContentType.LearningPath.getContentType())
				|| contentType.equals(LexConstants.ContentType.Course.getContentType())) {
			return true;
		}
		if (parentMap == null || parentMap.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	private List<Map<String, Object>> filterLiveContentsBackwardMovement(List<Map<String, Object>> listOfContentMetas,
			String currentStatus, List<String> validStatus) throws BadRequestException {
		List<Map<String, Object>> contentMetas = new ArrayList<>();
		int currentIndex = validStatus.indexOf(currentStatus);
		List<String> filterStatus = Arrays.asList(LexConstants.Status.Live.getStatus(),
				LexConstants.Status.Deleted.getStatus(), LexConstants.Status.MarkedForDeletion.getStatus(),
				LexConstants.Status.Expired.getStatus());
		for (Map<String, Object> mapObj : listOfContentMetas) {
			if (!filterStatus.contains(mapObj.get(LexConstants.STATUS))) {
				String mapStatus = (String) mapObj.get(LexConstants.STATUS);
				int mapIndex = validStatus.indexOf(mapStatus);
				if (mapIndex > currentIndex) {
					contentMetas.add(mapObj);
				} else if (mapIndex == currentIndex) {
					contentMetas.add(mapObj);
				}
			}
		}
		return contentMetas;
	}

	private List<Map<String, Object>> filterLiveContentsForwardMovement(List<Map<String, Object>> listOfContentMetas,
			String currentStatus, List<String> validStatus) throws BadRequestException {
		List<Map<String, Object>> contentMetas = new ArrayList<>();
		int currentIndex = validStatus.indexOf(currentStatus);
		String errors = null;
		List<String> filterStatus = Arrays.asList(LexConstants.Status.Live.getStatus(),
				LexConstants.Status.Deleted.getStatus(), LexConstants.Status.MarkedForDeletion.getStatus(),
				LexConstants.Status.Expired.getStatus());
		for (Map<String, Object> mapObj : listOfContentMetas) {
			if (!filterStatus.contains(mapObj.get(LexConstants.STATUS))) {
				String mapStatus = (String) mapObj.get(LexConstants.STATUS);
				int mapIndex = validStatus.indexOf(mapStatus);
				if (mapIndex < currentIndex) {
					errors = errors + "Cannot change status " + mapObj.get(LexConstants.IDENTIFIER)
							+ " is at a previous status : " + mapStatus + "\n";
				} else if (mapIndex == currentIndex) {
					contentMetas.add(mapObj);
				}
			}
		}
		if (errors != null) {
			throw new BadRequestException(errors);
		}
		return contentMetas;
	}

	@SuppressWarnings("unused")
	private List<UpdateMetaRequest> createUpdateListForStatusChange(List<Map<String, Object>> allContents,
			String nextStatus, String lastUpdatedBy, boolean populatePublishedBy) {
		List<UpdateMetaRequest> updateList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for (Map<String, Object> contentMeta : allContents) {
			Map<String, Object> updateMap = new HashMap<>();
			if (populatePublishedBy) {
				updateMap.put(LexConstants.ISSTANDALONE, contentMeta.get(LexConstants.ISSTANDALONE));
				updateMap.put(LexConstants.CHILD_TITLE, contentMeta.get(LexConstants.CHILD_TITLE));
				updateMap.put(LexConstants.CHILD_DESC, contentMeta.get(LexConstants.CHILD_DESC));
				updateMap.put(LexConstants.PUBLISHED_BY, lastUpdatedBy);
			}

			updateMap.put(LexConstants.IDENTIFIER, contentMeta.get(LexConstants.IDENTIFIER));
			updateMap.put(LexConstants.ACTOR, lastUpdatedBy);
			updateMap.put(LexConstants.COMMENTS, contentMeta.get(LexConstants.COMMENTS));
			updateMap.put(LexConstants.HAS_ASSESSMENT, contentMeta.getOrDefault(LexConstants.HAS_ASSESSMENT, false));
			Map<String, String> timeMap = getTimeAndEpochAtPresent();
			Calendar validTill = Calendar.getInstance();
			updateMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
			updateMap.put(LexConstants.VERSION_KEY, timeMap.get("versionKey"));
			updateMap.put(LexConstants.STATUS, nextStatus);
			UpdateMetaRequest updateMapReq = new UpdateMetaRequest((String) contentMeta.get(LexConstants.IDENTIFIER),
					updateMap);
			updateList.add(updateMapReq);
		}
		return updateList;
	}

	private Map<String, String> getTimeAndEpochAtPresent() {
		Map<String, String> timeAndEpochMap = new HashMap<String, String>();

		Date presentDate = new Date();
		String format = inputFormatterDateTime.format(presentDate);
		String formattedDate = format;
		String versionKey = String.valueOf(presentDate.getTime());
		timeAndEpochMap.put("formattedDate", formattedDate);
		timeAndEpochMap.put("versionKey", versionKey);
		return timeAndEpochMap;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<Map<String, Object>> addComments(List<Map<String, Object>> allContents, String topLevelId,
			String message, String actor) throws BadRequestException, Exception {
		UUID tempObj = null;
		try {
			tempObj = UUID.fromString(actor);
		} catch (ClassCastException | IllegalArgumentException e) {
			throw new BadRequestException("ACTOR MUST BE A VALID UUID");
		} catch (Exception e) {
			throw new ApplicationLogicError("userId");
		}
		List<Map<String, Object>> updatedContentObjs = new ArrayList<>();
		for (Map<String, Object> content : allContents) {
			List<Map<String, Object>> allPreviousComments = new ArrayList<>();
			if (content.containsKey(LexConstants.COMMENTS)) {
				allPreviousComments = (List<Map<String, Object>>) content.get(LexConstants.COMMENTS);
			}
			int sizeOfComments = allPreviousComments.size();
			if (sizeOfComments > 5) {
				System.out.println("Exceeded comments limit");
				allPreviousComments = sortComments(allPreviousComments);
			}
			Map<String, String> timeMap = getTimeAndEpochAtPresent();
			String value = timeMap.get("formattedDate");
			List<Map<String, Object>> copyOfList = new ArrayList<>(allPreviousComments);
			Map<String, Object> newComment = new HashMap<>();
			if (content.get(LexConstants.IDENTIFIER).equals(topLevelId)) {
				newComment.put(LexConstants.COMMENT, message);
				newComment.put(LexConstants.DATE, value);
				newComment.put(LexConstants.ID, actor);
				copyOfList.add(newComment);
			} else {
				String autoMessage = "Auto moved along with identifier : " + topLevelId + " Message: " + message;
				newComment.put(LexConstants.COMMENT, autoMessage);
				newComment.put(LexConstants.DATE, value);
				newComment.put(LexConstants.ID, actor);
				copyOfList.add(newComment);
			}
			content.put(LexConstants.COMMENTS, copyOfList);
			updatedContentObjs.add(content);
		}
		return updatedContentObjs;

	}

	private List<Map<String, Object>> sortComments(List<Map<String, Object>> masterComments) {
		List<Map<String, Object>> latestFilteredComments = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
		Collections.sort(masterComments, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				try {
					return df.parse((String) o1.get(LexConstants.DATE))
							.compareTo(df.parse((String) o2.get(LexConstants.DATE)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
		Collections.reverse(masterComments);
		latestFilteredComments = masterComments.subList(0, 5);
		return latestFilteredComments;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Set<String> getIdsFromHierarchyMap(Map<String, Object> contentMeta) {
		Set<String> listOfContentIds = new HashSet<>();
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			listOfContentIds.add(parent.get(LexConstants.IDENTIFIER).toString());
			ArrayList childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			parentObjs.addAll(childrenList);
		}
		return listOfContentIds;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> orderChildren(Map<String, Object> hierarchyMap) {

		hierarchyMap = (Map<String, Object>) hierarchyMap.get(LexConstants.CONTENT);
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hierarchyMap);

		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			ArrayList childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);

			Collections.sort(childrenList, new Comparator<Map<String, Long>>() {
				@Override
				public int compare(Map<String, Long> o1, Map<String, Long> o2) {
					Long indexValo1 = o1.getOrDefault(LexConstants.INDEX, (long) 0);
					if (indexValo1 == null) {
						indexValo1 = (long) 0;
					}
					Long indexValo2 = o2.getOrDefault(LexConstants.INDEX, (long) 1);
					if (indexValo2 == null) {
						indexValo2 = (long) 1;
					}
					return (int) indexValo1.compareTo(indexValo2);
//					return (long) o1.getOrDefault("index",(long) 0).compareTo(o2.getOrDefault("index",(long) 1));
				}
			});
			parentObjs.addAll(childrenList);
		}
		return hierarchyMap;
	}

	
	@SuppressWarnings("unchecked")
	private void calculateHasAssessment(Map<String, Object> contentMeta) {
		
		Queue<Map<String,Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);
		while(!parentObjs.isEmpty()) {
			
			Map<String,Object> parent = parentObjs.poll();
			
			String categoryType = (String) parent.get(LexConstants.CATEGORY_TYPE);
			String resourceType = (String) parent.get(LexConstants.RESOURCE_TYPE);
			
			if(categoryType.equals(LexConstants.ASSESSMENT)||resourceType.equals(LexConstants.ASSESSMENT)) {
				parent.put(LexConstants.HAS_ASSESSMENT, true);
			}
			
			List<Map<String,Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
			for(Map<String, Object> child:childrenList) {
				Boolean hasAssessment = (Boolean) child.get(LexConstants.HAS_ASSESSMENT);
				
				if(hasAssessment!=null && hasAssessment==true) {
					parent.put(LexConstants.HAS_ASSESSMENT, true);
				}
				
				categoryType = (String) child.get(LexConstants.CATEGORY_TYPE);
				resourceType = (String) child.get(LexConstants.RESOURCE_TYPE);
				
				if(categoryType.equals(LexConstants.ASSESSMENT)||resourceType.equals(LexConstants.ASSESSMENT)) {
					child.put(LexConstants.HAS_ASSESSMENT, true);
					parent.put(LexConstants.HAS_ASSESSMENT, true);
				}
				
			}
			
			parentObjs.addAll(childrenList);
		}
		
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, Object>> convertToFlatList(Map<String, Object> contentMeta) {
		List<Map<String, Object>> contentList = new ArrayList<>();
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			contentList.add(parent);
			parentObjs.addAll(childrenList);
		}
		return contentList;
	}

	@SuppressWarnings("unchecked")
	private void hierarchyForStatusChange(Map<String, Object> contentMeta) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);

		List<Map<String, Object>> creatorContacts = (List<Map<String, Object>>) contentMeta
				.get(LexConstants.CREATOR_CONTACTS);
		Set<String> masterCreators = getAllCreators(creatorContacts);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
			List<Map<String, Object>> validChildren = new ArrayList<>();
			for (Map<String, Object> child : childrenList) {
				List<Map<String, Object>> creators = (List<Map<String, Object>>) child
						.get(LexConstants.CREATOR_CONTACTS);
				Set<String> creatorsOfContent = getAllCreators(creators);
				creatorsOfContent.retainAll(masterCreators);
				if (creatorsOfContent.size() > 0) {
					validChildren.add(child);
				}
			}
			parent.put(LexConstants.CHILDREN, validChildren);
			parentObjs.addAll(childrenList);
		}
	}

	private Response publishContentV2(String rootOrg, List<UpdateMetaRequest> updateListMap, Transaction transaction,

			Set<String> contentIds, String topLevelIdentifier, String appName, String appUrl, String org)
			throws Exception {

		Response response = new Response();
		Map<String, Object> kafkaMap = new HashMap<>();
		Session session = neo4jDriver.session();
		kafkaMap.put("topLevelContentId", topLevelIdentifier);
		kafkaMap.put("contentIds", contentIds);
		kafkaMap.put("org", org);
		kafkaMap.put(LexConstants.APPNAME, appName);
		kafkaMap.put(LexConstants.APPURL, appUrl);
		kafkaMap.put(LexConstants.ROOT_ORG, rootOrg);
		ObjectMapper mapper = new ObjectMapper();
		try {
			graphService.updateNodesV2(rootOrg, updateListMap, transaction);
			transaction.commitAsync().toCompletableFuture().get();
			String kafkaMessage = mapper.writeValueAsString(kafkaMap);
			System.out.println("---------------------");
			System.out.println(kafkaMessage);
			kafkaTemplate.send("publishpipeline-stage1", null, kafkaMessage);
			response.put("Message", "Operation Successful: Sent to Publish Pipeline");
			return response;
		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void validateChildrenStatus(Map<String, Object> contentMeta, String currentStatus) {
		Map<String, Object> copyMap = new HashMap<>(contentMeta);
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(copyMap);
		List<Map<String, String>> invalidIds = new ArrayList<>();
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
			for (Map<String, Object> child : childrenList) {
				if (!child.get(LexConstants.STATUS).equals(currentStatus)) {
					Map<String, String> testMap = new HashMap<>();
					testMap.put((String) child.get(LexConstants.IDENTIFIER), (String) child.get(LexConstants.STATUS));
					invalidIds.add(testMap);
				}
			}
			parent.put(LexConstants.CHILDREN, childrenList);
			parentObjs.addAll(childrenList);
		}
		if (invalidIds.size() > 0) {
			throw new BadRequestException(
					"Not all Ids are at current common status of " + currentStatus + " Invalid Ids are " + invalidIds);
		}
	}

	private Set<String> getAllCreators(List<Map<String, Object>> creatorContacts) {

		Set<String> allCreators = new HashSet<>();

		for (Map<String, Object> creatorObj : creatorContacts) {
			try {
				allCreators.add(creatorObj.get(LexConstants.ID).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allCreators;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getAllCreators(String creatorContacts) {
		ObjectMapper mapper = new ObjectMapper();
		Set<String> allCreators = new HashSet<>();
		List<Map<String, Object>> listObjs = new ArrayList<>();
		try {
			listObjs = mapper.readValue(creatorContacts, ArrayList.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Map<String, Object> creatorObj : listObjs) {
			try {
				allCreators.add(creatorObj.get(LexConstants.ID).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return allCreators;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void removeAllDraftImgNodes(Map<String, Object> hierarchy) throws BadRequestException, Exception {

		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hierarchy);
		
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			GraphUtil.mapParser(parent, true);
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			List<Map<String,Object>> validChildren = new ArrayList<>();
			for(Map<String, Object> child:childrenList) {
				if(child.get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {
					validChildren.add(child);
				}
			}
			parent.put(LexConstants.CHILDREN, validChildren);
			parentObjs.addAll(validChildren);
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void hierachyForViewing(Map<String, Object> hierarchy) throws BadRequestException, Exception {

		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hierarchy);
		if (hierarchy.get(LexConstants.CREATOR_CONTACTS) == null
				|| hierarchy.get(LexConstants.CREATOR_CONTACTS).toString().isEmpty()) {
			throw new BadRequestException(
					"Corrput Meta CreatorContacts does not exist for : " + hierarchy.get("identifier"));
		}
		String stringCreatorContacts = hierarchy.get(LexConstants.CREATOR_CONTACTS).toString();
		Set<String> masterCreators = getAllCreators(stringCreatorContacts);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
//			StringValue x = (StringValue) parent.get(LexConstants.IDENTIFIER);
//			String data = x.asString();
//			parent.put(LexConstants.IDENTIFIER, data);
//			parent = createUsableMap(parent);
			GraphUtil.mapParser(parent, true);
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			Set<String> identifierSet = new HashSet<>();
			List<Map<String, Object>> validChildren = new ArrayList<>();
			for (Map<String, Object> child : childrenList) {
				validChildren.add(child);
				identifierSet.add(child.get(LexConstants.IDENTIFIER).toString());
			}
			List<Map<String, Object>> validChildrenCopy = new ArrayList<>(validChildren);
			for (Map<String, Object> validChild : validChildrenCopy) {
				String itId = validChild.get(LexConstants.IDENTIFIER).toString();
				String itIdImg = itId + LexConstants.IMG_SUFFIX;

				if (identifierSet.contains(itId) && identifierSet.contains(itIdImg)) {
					Map<String, Object> orgNode = new HashMap<>();
					Map<String, Object> imgNode = new HashMap<>();
					for (Map<String, Object> child : validChildrenCopy) {
						if (child.get(LexConstants.IDENTIFIER).toString().equals(itId)) {
							orgNode = child;
						}
						if (child.get(LexConstants.IDENTIFIER).toString().equals(itIdImg)) {
							imgNode = child;
						}
					}
					String orgCreators = (String) orgNode.get(LexConstants.CREATOR_CONTACTS);
					Set<String> orgCreatorEmails = getAllCreators(orgCreators);
					String imgCreators = (String) imgNode.get(LexConstants.CREATOR_CONTACTS);
					Set<String> imgCreatorEmails = getAllCreators(imgCreators);
					Set<String> orgCopySet = new HashSet<>(orgCreatorEmails);
					Set<String> imgCopySet = new HashSet<>(imgCreatorEmails);
					orgCopySet.retainAll(masterCreators);
					imgCopySet.retainAll(masterCreators);
					if (orgCopySet.size() > 0 && imgCopySet.size() > 0) {
						validChildren.remove(orgNode);
					} else if (orgCopySet.size() > 0 && imgCopySet.size() == 0) {
						validChildren.remove(imgNode);
					} else if (orgCopySet.size() == 0 && imgCopySet.size() > 0) {
						validChildren.remove(orgNode);
					} else if (orgCopySet.size() == 0 && imgCopySet.size() == 0) {
						validChildren.remove(imgNode);
					}
				}
			}
			parent.put(LexConstants.CHILDREN, validChildren);
			parentObjs.addAll(childrenList);
		}
	}

	// DO NOT REMOVE WILL BE USED LATER
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void hierarchyForSpecificAuthor(Map<String, Object> contentMeta, String creatorEmail) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		// TODO check if creatorEMail is valid UUID
		parentObjs.add(contentMeta);
		ObjectMapper mapper = new ObjectMapper();
		while (!parentObjs.isEmpty()) {
			// pull out top level map
			Map<String, Object> parent = parentObjs.poll();
			// iterate on its children
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			Set<String> iteratorSet = new HashSet<>();
			List<Map<String, Object>> validChildren = new ArrayList<>();
			for (Map<String, Object> child : childrenList) {
				validChildren.add(child);
				// add all 1st level children Ids to a set
				iteratorSet.add(child.get(LexConstants.IDENTIFIER).toString());
			}
			List<Map<String, Object>> validChildrenCopy = new ArrayList<>(validChildren);
			// iterate on all 1st level children
			for (Map<String, Object> validChild : validChildrenCopy) {
				String itId = validChild.get(LexConstants.IDENTIFIER).toString();
				String itIdImg = itId + LexConstants.IMG_SUFFIX;
				// if 1st level children Ids contains both orgNode and imgNode,
				// then one of them
				// must be removed
				if (iteratorSet.contains(itId) && iteratorSet.contains(itIdImg)) {
					Set<String> creatorEmails = new HashSet<>();
					String listOfCreators = (String) validChild.get(LexConstants.CREATOR_CONTACTS);
					List<Map<String, Object>> listObj = new ArrayList<>();
					try {
						// we get list of all creators from map
						listObj = mapper.readValue(listOfCreators, ArrayList.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (Map<String, Object> creatorObj : listObj) {
						// iterate on above obtained list and all emails to a
						// set
						creatorEmails.add(creatorObj.get(LexConstants.ID).toString());
					}
					// if UI provided email is present in set obtained above
					if (creatorEmails.contains(creatorEmail)) {
						// then remove the orgNode
						validChildren.remove(validChild);
					} else {
						// else remove corresponding .img node
						validChildrenCopy.forEach(vcc -> {
							if (vcc.get(LexConstants.IDENTIFIER).toString().equals(itIdImg)) {
								validChildren.remove(vcc);
							}
						});
					}
				}
			}
			parent.put(LexConstants.CHILDREN, validChildren);
			parentObjs.addAll(childrenList);
		}
	}

	private User verifyUser(String userEmail) throws Exception {

		logger.info("Verifying email " + userEmail);
		User user = userRepo.findByEmail(userEmail);

		if (user == null) {
			logger.error("No user found with email : " + userEmail);
			throw new ResourceNotFoundException("No user found with email : " + userEmail);
		}

		logger.info(userEmail + " verified successfully");
		return user;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void filterContentOnAccessPaths(Map<String, Object> contentMeta, String author, String rootOrg) {
		User user = null;
		try {
			user = verifyUser(author);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String uuid = user.getId();
		// URL prefix to fetch all User Access Paths
		String accessUrlPrefix = lexServerProps.getAccessUrlPrefix();
		String uri = accessUrlPrefix + "/user/" + uuid + "?rootOrg=" + rootOrg;
		Map<String, Object> result = restTemplate.getForObject(uri, HashMap.class);
		result = (Map<String, Object>) result.get("result");
		// all Aps for a user
		Set<String> accessPathsForUser = new HashSet<>((List<String>) result.get(LexConstants.COMBINED_ACCESS_PATHS));
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);

		while (!parentObjs.isEmpty()) {
			// pull out top-level contentMeta
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			List<Map<String, Object>> validChildren = new ArrayList<>();
			for (Map<String, Object> child : childrenList) {
				// add all 1st level children Ids to a list<Map>
				validChildren.add(child);
			}

			List<Map<String, Object>> validChildrenCopy = new ArrayList<>(validChildren);
			// iterate on all 1st level children
			try {
				for (Map<String, Object> validChild : validChildrenCopy) {
					Set<String> accessPathsFromContent = new HashSet<>(
							(List<String>) validChild.get(LexConstants.ACCESS_PATHS));
					Set<String> tempSet = new HashSet<>(accessPathsFromContent);
					// get intersection of APs from content and Aps of user
					tempSet.retainAll(accessPathsForUser);
					// if no elements in intersection set
					if (tempSet.size() <= 0) {
						// remove child from list of 1st level children
						System.out.println("Invalid Child : " + validChild.get(LexConstants.IDENTIFIER).toString());
						int i = validChildrenCopy.indexOf(validChild);
						Map<String, Object> tempMap = new HashMap<>();
						tempMap.put(LexConstants.IDENTIFIER, validChild.get(LexConstants.IDENTIFIER));
						tempMap.put(LexConstants.DURATION, validChild.get(LexConstants.DURATION));
						validChildren.set(i, tempMap);
//						validChildren.remove(validChild);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// put all validChildren in the contentMeta Popped earlier
			parent.put(LexConstants.CHILDREN, validChildren);
			// continue iteration for remaining valid children
			parentObjs.addAll(childrenList);
		}
	}

	@SuppressWarnings("unchecked")
	private void populateMetaForCreation(String rootOrg, String org, Map<String, Object> contentMeta,
			Map<String, Object> userData, String userId) throws Exception {

		logger.info("populating meta for identifier :" + contentMeta.get(LexConstants.IDENTIFIER));

		// status cannot be set while creating content.
		if (contentMeta.containsKey(LexConstants.STATUS)) {
			throw new BadRequestException("status cannot be set while creation");
		}
		String postUrl = lexServerProps.getAccessUrlPostFix();
		postUrl = postUrl.replace("@userId", userId);
		postUrl = postUrl.replace("@rootOrg", rootOrg);
		String url = lexServerProps.getAccessUrlPrefix() + postUrl;
		logger.info("fetching access paths");
		Map<String, Object> userAPMap = restTemplate.getForObject(url, Map.class);
		logger.info("fetched access paths");
		userAPMap = (Map<String, Object>) userAPMap.get("result");
		List<String> combinedAccessPaths = (List<String>) userAPMap.get(LexConstants.COMBINED_ACCESS_PATHS);

		//ACCESS PATH LOGIC
		String apFromReq = (String) contentMeta.get(LexConstants.ACCESS_PATHS);
		contentMeta.remove(LexConstants.ACCESS_PATHS);
		
		List<String> accessPaths = new ArrayList<>();
		if (combinedAccessPaths.contains(apFromReq)) {
			accessPaths.add(apFromReq);
		} else {
			accessPaths.add(rootOrg + "/" + org);
		}
		
		contentMeta.put(LexConstants.ACCESS_PATHS, accessPaths);

		String contentType = (String) contentMeta.get(LexConstants.CONTENT_TYPE);
		String mimeType = (String) contentMeta.get(LexConstants.MIME_TYPE);
		String createdBy = (String) contentMeta.get(LexConstants.CREATED_BY);
		String fileType = null;
		if (mimeType.contains("mp4") || mimeType.contains("youtube") || mimeType.contains("/x-mpeg")) {
			fileType = "Video";
		} else if (mimeType.contains("mpeg")) {
			fileType = "Audio";
		} else if (mimeType.contains("web-module")) {
			fileType = "Web Page";
		} else {
			fileType = "Document";
		}
		// bare minimum for content creation
		if (contentType == null || contentType.isEmpty() || mimeType == null || mimeType.isEmpty() || createdBy == null
				|| createdBy.isEmpty()) {
			throw new BadRequestException(
					"Invalid meta for creation. request body must contain contentType, mimeType, createdBy");
		}
		
		contentMeta.put(LexConstants.ROOT_ORG, rootOrg);

		if (!contentMeta.containsKey(LexConstants.IS_SEARCHABLE))
			contentMeta.put(LexConstants.IS_SEARCHABLE, true);

		contentMeta.remove(LexConstants.CREATED_BY);
		if (!contentMeta.containsKey(LexConstants.CREATOR))
			contentMeta.put(LexConstants.CREATOR, createdBy);

		if (!contentMeta.containsKey(LexConstants.NODE_TYPE))
			contentMeta.put(LexConstants.NODE_TYPE, LexConstants.LEARNING_CONTENT_NODE_TYPE);

		contentMeta.put(LexConstants.STATUS, LexConstants.DRAFT);
		contentMeta.put(LexConstants.HAS_ASSESSMENT, false);

		if (!contentMeta.containsKey(LexConstants.DURATION))
			contentMeta.put(LexConstants.DURATION, 0);

		if (!contentMeta.containsKey(LexConstants.ARTIFACT_URL))
			contentMeta.put(LexConstants.ARTIFACT_URL, "");

		

		if (!contentMeta.containsKey(LexConstants.CHILD_TITLE))
			contentMeta.put(LexConstants.CHILD_TITLE, new ArrayList<>());

		if (!contentMeta.containsKey(LexConstants.CHILD_DESC))
			contentMeta.put(LexConstants.CHILD_DESC, new ArrayList<>());

		if (!contentMeta.containsKey(LexConstants.IS_STAND_ALONE))
			contentMeta.put(LexConstants.IS_STAND_ALONE, true);

		if (!contentMeta.containsKey(LexConstants.LEARNING_MODE))
			contentMeta.put(LexConstants.LEARNING_MODE, "Self-Paced");

		if (!contentMeta.containsKey(LexConstants.FILETYPE))
			contentMeta.put(LexConstants.FILETYPE, fileType);

		if (!contentMeta.containsKey(LexConstants.SIZE))
			contentMeta.put(LexConstants.SIZE, 0);

		Map<String, Object> transcodeMap = new HashMap<>();
		transcodeMap.put(LexConstants.TRANSCODE_STATUS, null);
		transcodeMap.put(LexConstants.TRANSCODED_ON, null);
		transcodeMap.put(LexConstants.RETRYCOUNT, 0);

		contentMeta.put(LexConstants.TRANSCODING, transcodeMap);

		// populating org list
		Calendar validTill = Calendar.getInstance();
		contentMeta.put(LexConstants.VERSION_DATE, inputFormatterDateTime.format(validTill.getTime()));
		contentMeta.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
		validTill.add(Calendar.YEAR, 50);

		Map<String, Object> orgMap = new HashMap<>();
		orgMap.put(LexConstants.ORG, org);
		orgMap.put(LexConstants.VALID_TILL, inputFormatterDateTime.format(validTill.getTime()));
		List<Map<String, Object>> orgsList = new ArrayList<>();
		orgsList.add(orgMap);
		contentMeta.put(LexConstants.ORG, orgsList);

		
		//TODO expiry based on rootOrg from appConfig
		String dataSource = null;
		logger.info("fetching config for expiry-date");
		AppConfig config = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "expiry_date")).orElse(null);
		logger.info("expiry date fetch completed");
		if (config != null && (config.getValue() != null && !config.getValue().isEmpty())) {
			dataSource = config.getValue();
		}
		
		if(dataSource==null || dataSource.equalsIgnoreCase("0")) {
			contentMeta.put(LexConstants.EXPIRY_DATE, "99991231T235959+0000");
		}
		else {
			
			Integer months = Integer.parseInt(dataSource);
			Calendar dueDate = Calendar.getInstance();
			dueDate.add(Calendar.DAY_OF_MONTH, months);
			contentMeta.put(LexConstants.EXPIRY_DATE,inputFormatterDateTime.format(dueDate.getTime()));
			
//			Integer months = Integer.parseInt(dataSource);
//			Calendar dueDate = Calendar.getInstance();
//			dueDate.add(Calendar.MONTH, months);
//			contentMeta.put(LexConstants.EXPIRY_DATE,inputFormatterDateTime.format(dueDate.getTime()));
		}

		Map<String, Object> creatorContact = new HashMap<>();
		creatorContact.put(LexConstants.ID, userId);
		Map<String, Object> userMap = (Map<String, Object>) userData.get(userId);
		String name = (String) userMap.getOrDefault(PIDConstants.FIRST_NAME, "Default") + " "
				+ (String) userMap.getOrDefault(PIDConstants.LAST_NAME, "User");
		creatorContact.put(LexConstants.NAME, name);
		List<Map<String, Object>> creatorContacts = Arrays.asList(creatorContact);
		contentMeta.put(LexConstants.CREATOR_CONTACTS, creatorContacts);

		logger.info("populated meta succesfully for identifier :" + contentMeta.get(LexConstants.IDENTIFIER));
	}

	private String findOriginalLangContent(String translationOf, String rootOrg) {
		String orgIdentifier = translationOf;
		String query = "match(n{identifier:'" + translationOf + "'})-[r:Is_Translation_Of]->(s) where n:Shared or n:"
				+ rootOrg + " and s:Shared or s:" + rootOrg + " return s";
		Session session = neo4jDriver.session();
		Map<String, Object> originalLangNode = session.readTransaction(new TransactionWork<Map<String, Object>>() {
			@Override
			public Map<String, Object> execute(Transaction tx) {
				return runQuery(query, tx);
			}
		});
		if (originalLangNode != null) {
			if (originalLangNode.size() > 0) {
				orgIdentifier = (String) originalLangNode.get(LexConstants.IDENTIFIER);
			}
		}
		session.close();
		return orgIdentifier;
	}

	private Map<String, Object> runQuery(String query, Transaction tx) {
		logger.debug("Running Query : " + query);
		Map<String, Object> resultMap = new HashMap<>();
		StatementResult statementResult = tx.run(query);
		List<Record> records = statementResult.list();
		for (Record rec : records) {
			resultMap = rec.get("s").asMap();
		}
		return resultMap;
	}

	List<UpdateRelationRequest> copyChildrenRelationsForImageNode(String rootOrg, ContentNode node,
			Transaction transaction) throws Exception {

		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();
		if (node.getChildren() != null && !node.getChildren().isEmpty()) {
			String startNodeId = node.getIdentifier() + LexConstants.IMG_SUFFIX;

			for (Relation childRelation : node.getChildren()) {
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(startNodeId,
						childRelation.getEndNodeId(), childRelation.getMetadata());

				updateRelationRequests.add(updateRelationRequest);
			}
		}
		return updateRelationRequests;
	}

	List<UpdateRelationRequest> copyParentRelationsForImageNode(String rootOrg, ContentNode node,
			Transaction transaction) throws Exception {

		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();
		if (node.getParents() != null && !node.getParents().isEmpty()) {
			String endNodeId = node.getIdentifier() + LexConstants.IMG_SUFFIX;

			for (Relation parentRelation : node.getParents()) {
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(parentRelation.getStartNodeId(),
						endNodeId, parentRelation.getMetadata());

				updateRelationRequests.add(updateRelationRequest);
			}
		}

		return updateRelationRequests;
	}

	private void checkContentSharingConstraints(ContentNode parentNode, ContentNode childNode) {

		if (!parentNode.getRootOrg().equals(childNode.getRootOrg())
				&& !childNode.getRootOrg().equals(LexConstants.SHARED_CONTENT)) {
			throw new BadRequestException(
					"Content in shared state cannot have children which are in non shared state.");
		}

		//TODO RISKY CHANGE DONE HERE
		if (parentNode.getIdentifier()
				.equals(childNode.getIdentifier())) {
			throw new BadRequestException("A content cannot be the children of itself :"
					+ parentNode.getMetadata().get(LexConstants.IDENTIFIER));
		}
	}

	private ContentNode getNodeFromDb(String rootOrg, String identifier, Transaction transaction) throws Exception {

		String imageNodeIdentifier = "";
		if (identifier.contains(LexConstants.IMG_SUFFIX)) {
			imageNodeIdentifier = identifier;
			identifier = identifier.substring(0, identifier.indexOf(LexConstants.IMG_SUFFIX));
		} else {
			imageNodeIdentifier = identifier + LexConstants.IMG_SUFFIX;
		}

		ContentNode imageNode = graphService.getNodeByUniqueIdV3(rootOrg, imageNodeIdentifier, transaction);

		if (imageNode != null) {
			logger.info("image node found for " + identifier);
			return imageNode;
		}

		logger.info("image node not found for " + identifier);
		// fetch original node when image node not found
		ContentNode contentNode = graphService.getNodeByUniqueIdV3(rootOrg, identifier, transaction);

		if (contentNode == null) {
			logger.error("Content with given identifier does not exist");
			throw new ResourceNotFoundException("Content with given identifier does not exist");
		}

		logger.info("Original node found for identifier " + identifier);

		return contentNode;
	}

	/**
	 * creates image node for the given id and provided metadata.
	 *
	 * @param rootOrg
	 * @param contentNode
	 * @param copyParents
	 * @param copyChildren
	 * @param identifier
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	private ContentNode createImageNode(String rootOrg, ContentNode contentNode, boolean copyParents,
			boolean copyChildren, String identifier, Transaction transaction) throws Exception {

		logger.info("Attempting to create image node for " + identifier);

		String imageNodeIdentifier = identifier + LexConstants.IMG_SUFFIX;
		contentNode.getMetadata().put(LexConstants.IDENTIFIER, imageNodeIdentifier);
		contentNode.getMetadata().put(LexConstants.STATUS, LexConstants.DRAFT);
		contentNode.getMetadata().put(LexConstants.PUBLISHER_DETAILS, new ArrayList<>());
		contentNode.getMetadata().put(LexConstants.COMMENTS, new ArrayList<>());

		graphService.createNodeV2(rootOrg, contentNode.getMetadata(), transaction);

		logger.info("Image node created for " + identifier);

		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();

		if (copyParents) {
			updateRelationRequests.addAll(copyParentRelationsForImageNode(rootOrg, contentNode, transaction));
		}

		if (copyChildren) {
			updateRelationRequests.addAll(copyChildrenRelationsForImageNode(rootOrg, contentNode, transaction));
		}

		graphService.createRelations(rootOrg, updateRelationRequests, transaction);
		logger.info("Relation's copied to image node for " + identifier);

		return graphService.getNodeByUniqueIdV3(rootOrg, imageNodeIdentifier, transaction);
	}

	private boolean validateContentType(Map<String, Object> contentMeta) {

		String contentType = contentMeta.get(LexConstants.CONTENT_TYPE).toString();
		if (contentType.equals(LexConstants.ContentType.Resource.getContentType())
				|| contentType.equals(LexConstants.ContentType.Collection.getContentType())
				|| contentType.equals(LexConstants.ContentType.Course.getContentType())
				|| contentType.equals(LexConstants.ContentType.LearningPath.getContentType())
				|| contentType.equals(LexConstants.ContentType.KnowledgeArtifact.getContentType())
				|| contentType.equals(LexConstants.ContentType.LeadershipReport.getContentType())
				|| contentType.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
				|| contentType.equals(LexConstants.ContentType.Channel.getContentType())
				|| contentType.equals(LexConstants.ContentType.LearningJourney.getContentType())) {
			return true;
		}
		return false;
	}

	private User verifyUserV2(Map<String, Object> contentMeta) throws Exception {

		if (contentMeta.get(LexConstants.CREATED_BY) == null
				|| contentMeta.get(LexConstants.CREATED_BY).toString().isEmpty()) {
			throw new BadRequestException("content creator is not populated");
		}

		String userId = contentMeta.get(LexConstants.CREATED_BY).toString();

		logger.info("Verifying userId " + userId);

		Optional<User> user = userRepo.findById(userId);

		if (!user.isPresent()) {
			logger.error("No user found with userId : " + userId);
			throw new BadRequestException("No user found with userId : " + userId);
		}

		logger.info(userId + " verified successfully.");
		return user.get();
	}

	private String createNode(String rootOrg, Map<String, Object> contentMeta) throws Exception {

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();

		logger.debug("Attempting to create content node for identifier : " + contentMeta.get(LexConstants.IDENTIFIER));
		try {

			graphService.createNodeV2(rootOrg, contentMeta, transaction);
			transaction.commitAsync().toCompletableFuture().get();

			logger.info(
					"Content node created successfully for identifier : " + contentMeta.get(LexConstants.IDENTIFIER));
			return contentMeta.get(LexConstants.IDENTIFIER).toString();

		} catch (Exception e) {
			transaction.rollbackAsync().toCompletableFuture().get();
			logger.info("Content node creation failed for identifier : " + contentMeta.get(LexConstants.IDENTIFIER));
			throw e;
		} finally {
			transaction.close();
			session.close();
		}
	}

	private void createChildRelationsPlaylist(String identifier, List<String> children, String rootOrg)
			throws InterruptedException, ExecutionException {
		List<Map<String, Object>> listForCreatingChildRelations = new ArrayList<>();
		for (int i = 0; i < children.size(); i++) {
			String child = children.get(i);
			int index = i;
			Map<String, Object> map = new HashMap<>();
			map.put(LexConstants.END_NODE_ID, child);
			map.put(LexConstants.INDEX, index);
			listForCreatingChildRelations.add(map);
		}
		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		try {
			graphService.createChildRelations(rootOrg, identifier, listForCreatingChildRelations, tx);
			tx.commitAsync().toCompletableFuture().get();
		} catch (Exception e) {
			tx.rollbackAsync().toCompletableFuture().get();
			throw e;
		} finally {
			tx.close();
			session.close();
		}
	}

	private void updateNodeValidation(ContentNode contentNode) {

		if (!contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
				.equals(LexConstants.ContentType.Resource.getContentType())
				&& !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.KnowledgeArtifact.getContentType())
				&& !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())
				&& !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.Channel.getContentType())
				&& !contentNode.getMetadata().get(LexConstants.CONTENT_TYPE)
						.equals(LexConstants.ContentType.LearningJourney.getContentType())) {
			throw new BadRequestException("Update operation not supported for collections");
		}
	}

	private void updateMeta(String rootOrg, String identifier, Map<String, Object> requestMap, Transaction tx)
			throws Exception {

		if(requestMap.get(LexConstants.LAST_UPDATED)==null||requestMap.get(LexConstants.LAST_UPDATED).toString().isEmpty()) {
			Calendar currentDateTime = Calendar.getInstance();
			requestMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(currentDateTime.getTime()));
		}

		ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);

		if (node == null) {
			throw new BadRequestException("No node with identifier : " + identifier + " exists");
		}
		graphService.updateNodeV2(rootOrg, identifier, requestMap, tx);

	}

	// only valid for content type resource
	private ContentNode updateNode(String rootOrg, String identifier, Map<String, Object> requestMap,
			Transaction transaction) throws Exception {

		updateOperationValidations(requestMap);
		// validationsService.validateMetaFields(rootOrg, requestMap);

		Calendar currentDateTime = Calendar.getInstance();
		requestMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(currentDateTime.getTime()));

		String imageNodeIdentifier = "";
		if (identifier.contains(LexConstants.IMG_SUFFIX)) {
			imageNodeIdentifier = identifier;
			identifier = identifier.substring(0, identifier.indexOf(LexConstants.IMG_SUFFIX));
		} else {
			imageNodeIdentifier = identifier + LexConstants.IMG_SUFFIX;
		}

		ContentNode imageNode = graphService.getNodeByUniqueIdV3(rootOrg, imageNodeIdentifier, transaction);

		if (imageNode != null) {
			updateNodeValidation(imageNode);
			imageNode = graphService.updateNodeV2(rootOrg, imageNodeIdentifier, requestMap, transaction);
			return imageNode;
		}

		ContentNode contentNode = graphService.getNodeByUniqueIdV3(rootOrg, identifier, transaction);
		if (contentNode == null) {
			throw new ResourceNotFoundException("Content with given identifier does not exist");
		}

		updateNodeValidation(contentNode);
		if (contentNode.getMetadata().get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {
			// population entries to updated and creating the image node
			for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
				contentNode.getMetadata().put(entry.getKey(), entry.getValue());
			}
			// not copying children as resource should not have children
			contentNode = createImageNode(rootOrg, contentNode, true, false, identifier, transaction);
		} else {
			contentNode = graphService.updateNodeV2(rootOrg, identifier, requestMap, transaction);
		}

		return contentNode;
	}

	private void updateOperationValidations(Map<String, Object> requestMap) {

		if (requestMap.containsKey(LexConstants.STATUS) || requestMap.containsKey(LexConstants.IDENTIFIER)) {
			throw new BadRequestException("Cannot set status, identifier of content in update operation");
		}

//		for (String metaKey : updateMap.keySet()) {
//		if (!reqdFields.contains(metaKey)) {
//			throw new BadRequestException("Invalid meta field " + metaKey);
//		}
//	}
	}

	@SuppressWarnings("unchecked")
	private String getRootNode(String rootOrg, Map<String, Object> nodesModified, Map<String, Object> hierarchy)
			throws Exception {

		String rootNodeIdentifier = "";

		for (Map.Entry<String, Object> nodeModified : nodesModified.entrySet()) {
			Map<String, Object> nodeModifiedMetaMap = (Map<String, Object>) nodeModified.getValue();

			if (!nodeModifiedMetaMap.containsKey("root")) {
				throw new BadRequestException(
						"Invalid request body for update hierarchy operation. Should include root field");
			}
			if ((boolean) nodeModifiedMetaMap.get("root") == true) {
				if (rootNodeIdentifier.equals("")) {
					rootNodeIdentifier = nodeModified.getKey().toString();
				} else if (!nodeModified.getKey().toString().equals(rootNodeIdentifier)) {
					throw new BadRequestException("Invalid contract multiple root nodes found in request body.");
				}
			}
		}

		for (Map.Entry<String, Object> hierarchyEntry : hierarchy.entrySet()) {
			Map<String, Object> hierarchyEntryMap = (Map<String, Object>) hierarchyEntry.getValue();
			if (!hierarchyEntryMap.containsKey("root")) {
				throw new BadRequestException(
						"Invalid request body for update hierarchy operation. Should include root field");
			}
			if ((boolean) hierarchyEntryMap.get("root") == true) {
				if (rootNodeIdentifier.equals("")) {
					rootNodeIdentifier = hierarchyEntry.getKey().toString();
				} else if (!hierarchyEntry.getKey().toString().equals(rootNodeIdentifier)) {
					throw new BadRequestException("Invalid contract multiple root node found in request body.");
				}
			}
		}

		if (rootNodeIdentifier.equals("")) {
			throw new BadRequestException("Invalid request body for update Hierachy operation root node not present.");
		}
		return rootNodeIdentifier;
	}

	private void createContentFolderInS3(String rootOrg, String org, String identifier,
			Map<String, Object> contentMeta) {

		rootOrg = rootOrg.replace(" ", "_");
		org = org.replace(" ", "_");

		String url = lexServerProps.getContentServiceUrl() + "/contentv3/directory/" + rootOrg + "%2F" + org + "%2F"
				+ "Public%2F" + identifier;

		if (contentMeta.get(LexConstants.CONTENT_TYPE).equals(LexConstants.ContentType.Resource.getContentType())
				|| contentMeta.get(LexConstants.CONTENT_TYPE).equals(LexConstants.ContentType.Channel.getContentType())) {
			url += "%2Fweb-hosted";
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		UriComponents components = builder.build(true);
		URI uri = components.toUri();

		try {
			logger.info("Creating folder in s3 using url " + url);
			restTemplate.postForEntity(uri, null, String.class);
			logger.info("Folder created successfully in s3 for identifier : " + identifier);
		} catch (HttpStatusCodeException httpStatusCodeException) {
			logger.error("Folder creation in s3 failed for identifier : " + identifier + ". Content Service returned "
					+ httpStatusCodeException.getStatusCode() + "  "
					+ httpStatusCodeException.getResponseBodyAsString());
			throw httpStatusCodeException;
		}
	}

	private void checkLearningPathConstraints(ContentNode contentNodeToUpdate, ContentNode childNode) {
		if (contentNodeToUpdate.getMetadata().get(LexConstants.CONTENT_TYPE)
				.equals(LexConstants.ContentType.LearningPath.getContentType())
				&& !childNode.getMetadata().get(LexConstants.STATUS).equals(LexConstants.Status.Live.getStatus())) {
			throw new BadRequestException(childNode.getIdentifier() + " is not in live state");
		}
	}

	private void ensureHierarchyCorrectnessForHierarchyUpdate(Map<String, ContentNode> idToContentMapping,
			ContentNode contentNode) {

		if (contentNode.getChildren() == null || contentNode.getChildren().isEmpty()) {
			return;
		}

		// try removing by overriding equals method of relation class
		List<Relation> childRelations = contentNode.getChildren();
		for (Relation childRelation : childRelations) {
			if (idToContentMapping.containsKey(childRelation.getEndNodeId())) {
				ContentNode childNode = idToContentMapping.get(childRelation.getEndNodeId());
				Iterator<Relation> parentRelationsIterator = childNode.getParents().iterator();
				while (parentRelationsIterator.hasNext()) {
					Relation parentRelation = parentRelationsIterator.next();
					if (parentRelation.getStartNodeId().equals(contentNode.getIdentifier())) {
						parentRelationsIterator.remove();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void getChildrenGettingAttached(List<String> identifiers, Map<String, Object> hierarchy) {

		List<String> childrenGettingAttached = new ArrayList<>();

		// all the nodes which are getting attached are collected in this list
		// there are multiple hierarchy maps, for each map we are combining all children to a common list element
		for (Map.Entry<String, Object> entrySet : hierarchy.entrySet()) {
			Map<String, Object> newHierachyMap = (Map<String, Object>) entrySet.getValue();

			List<Map<String, Object>> childMaps = (List<Map<String, Object>>) newHierachyMap.get(LexConstants.CHILDREN);
			List<String> idsToAdd = childMaps.stream().map(childMap -> childMap.get(LexConstants.IDENTIFIER).toString())
					.collect(Collectors.toList());
			childrenGettingAttached.addAll(idsToAdd);
		}

		identifiers.addAll(childrenGettingAttached);
	}

	private ContentNode populateMetaForImageNodeCreation(ContentNode contentNode,
			Map<String, ContentNode> idToContentMapping, Map<String, Object> updateMap) {

		ContentNode imageNode = new ContentNode();

		String imageNodeIdentifier = contentNode.getIdentifier() + LexConstants.IMG_SUFFIX;

		imageNode.setIdentifier(imageNodeIdentifier);
		imageNode.setRootOrg(contentNode.getRootOrg());

		imageNode.setMetadata(new HashMap<>(contentNode.getMetadata()));
		imageNode.getMetadata().put(LexConstants.IDENTIFIER, imageNodeIdentifier);
		imageNode.getMetadata().put(LexConstants.STATUS, LexConstants.DRAFT);
		imageNode.getMetadata().put(LexConstants.PUBLISHER_DETAILS, new ArrayList<>());
		imageNode.getMetadata().put(LexConstants.COMMENTS, new ArrayList<>());

		for (Relation childRelation : contentNode.getChildren()) {
			Relation relation = new Relation(childRelation.getId(), imageNodeIdentifier,
					childRelation.getRelationType(), childRelation.getEndNodeId(), childRelation.getMetadata());
			imageNode.getChildren().add(relation);
			if (idToContentMapping.containsKey(childRelation.getEndNodeId())) {
				ContentNode childNode = idToContentMapping.get(childRelation.getEndNodeId());
				if (!childNode.getParents().contains(relation)) {
					childNode.getParents().add(relation);
				}
			}
		}

		for (Relation parentRelation : contentNode.getParents()) {
			Relation relation = new Relation(parentRelation.getId(), parentRelation.getStartNodeId(),
					parentRelation.getRelationType(), imageNodeIdentifier, parentRelation.getMetadata());
			imageNode.getParents().add(relation);
			if (idToContentMapping.containsKey(parentRelation.getStartNodeId())) {
				ContentNode parentNode = idToContentMapping.get(parentRelation.getStartNodeId());
				if (!parentNode.getChildren().contains(relation)) {
					parentNode.getChildren().add(relation);
				}
			}
		}

		if (updateMap != null && !updateMap.isEmpty())
			updateMap.entrySet()
					.forEach(updateEntry -> imageNode.getMetadata().put(updateEntry.getKey(), updateEntry.getValue()));

		return imageNode;
	}

	private void validateAndReCalcSizeAndDuration(String rootOrg, String org, String rootNodeIdentifier,
			Transaction transaction) throws Exception {

		//TODO CHANGE TO V2
		List<String> fields = Arrays.asList(LexConstants.STATUS,LexConstants.DURATION,LexConstants.SIZE);
		Map<String, Object> hierarchyMap = getHierarchyFromNeo4jV2(rootNodeIdentifier, rootOrg, transaction, true, fields);
//		Map<String, Object> hierarchyMap = getHierarchyFromNeo4j(rootNodeIdentifier, rootOrg, transaction, false, null);
		hierachyForViewing(hierarchyMap);
		
		// running validations on the entire hierarchy.
//		Map<String, Set<String>> errors = validationsService.contentHierarchyValidations(rootOrg, hierarchyMap);
//		if (!errors.isEmpty())
//			throw new ConflictErrorException("Validation Failed", errors);

		calcDurationUtil(hierarchyMap);
		calcSizeUtil(hierarchyMap);
		updateNewSizeAndDuration(rootOrg, hierarchyMap, transaction);
	}

	@SuppressWarnings("unchecked")
	private void updateNewSizeAndDuration(String rootOrg, Map<String, Object> contentHierarchy, Transaction transaction)
			throws Exception {

		List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();

		Queue<Map<String, Object>> queue = new LinkedList<>();
		queue.add(contentHierarchy);

		while (!queue.isEmpty()) {
			Map<String, Object> contentMeta = queue.poll();

			Map<String, Object> contentMetaToUpdate = new HashMap<>();
			contentMetaToUpdate.put(LexConstants.DURATION, contentMeta.get(LexConstants.DURATION));
			contentMetaToUpdate.put(LexConstants.SIZE, contentMeta.get(LexConstants.SIZE));

			UpdateMetaRequest updateMetaRequest = new UpdateMetaRequest(
					contentMeta.get(LexConstants.IDENTIFIER).toString(), contentMetaToUpdate);
			updateMetaRequests.add(updateMetaRequest);

			List<Map<String, Object>> children = (List<Map<String, Object>>) contentMeta.get(LexConstants.CHILDREN);
			if (children != null && !children.isEmpty()) {
				queue.addAll(children);
			}
		}
		graphService.updateNodesV2(rootOrg, updateMetaRequests, transaction);
	}

	@SuppressWarnings("unchecked")
	private void calcDurationUtil(Map<String, Object> contentHierarchy) {

		Stack<Map<String, Object>> stack = new Stack<>();
		stack.push(contentHierarchy);

		Map<String, Integer> durationMap = new HashMap<>();
		while (!stack.isEmpty()) {

			Map<String, Object> parent = stack.peek();
			List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);

			if (children != null && !children.isEmpty()) {
				boolean durationExistsForChildren = true;
				for (Map<String, Object> child : children) {
					if (!durationMap.containsKey(child.get(LexConstants.IDENTIFIER).toString())) {
						durationExistsForChildren = false;
						stack.push(child);
					}
				}
				if (durationExistsForChildren) {
					stack.pop();
					int parentDuration = 0;
					for (Map<String, Object> child : children) {
						parentDuration += durationMap.get(child.get(LexConstants.IDENTIFIER).toString());
					}
					// if the content node is live then do not update the duration
					if (!parent.get(LexConstants.STATUS).toString().equals(LexConstants.LIVE)) {
						parent.put(LexConstants.DURATION, parentDuration);
					}
					durationMap.put(parent.get(LexConstants.IDENTIFIER).toString(), parentDuration);
				}
			} else {
				stack.pop();
				durationMap.put(parent.get(LexConstants.IDENTIFIER).toString(),
						Integer.parseInt(parent.get(LexConstants.DURATION).toString()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void calcSizeUtil(Map<String, Object> contentHierarchy) {

		Stack<Map<String, Object>> stack = new Stack<>();
		stack.push(contentHierarchy);

		Map<String, Double> sizeMap = new HashMap<>();
		while (!stack.isEmpty()) {

			Map<String, Object> parent = stack.peek();
			List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);

			if (children != null && !children.isEmpty()) {
				boolean sizeExistsForChildren = true;
				for (Map<String, Object> child : children) {
					if (!sizeMap.containsKey(child.get(LexConstants.IDENTIFIER).toString())) {
						sizeExistsForChildren = false;
						stack.push(child);
					}
				}
				if (sizeExistsForChildren) {
					stack.pop();
					Double parentSize = 0d;
					for (Map<String, Object> child : children) {
						parentSize += sizeMap.get(child.get(LexConstants.IDENTIFIER).toString());
					}
					// if the content node is live then do not update the duration
					if (!parent.get(LexConstants.STATUS).toString().equals(LexConstants.LIVE)) {
						parent.put(LexConstants.SIZE, parentSize);
					}
					sizeMap.put(parent.get(LexConstants.IDENTIFIER).toString(), parentSize);
				}
			} else {
				stack.pop();
				sizeMap.put(parent.get(LexConstants.IDENTIFIER).toString(),
						Double.parseDouble(parent.get(LexConstants.SIZE).toString()));
			}
		}
	}

	private Map<String, Object> removeImageSuffixFromNodesModified(Map<String, Object> nodesModified) {

		Map<String, Object> nodesModifiedNew = new HashMap<>();
		for (Map.Entry<String, Object> entry : nodesModified.entrySet()) {
			if (entry.getKey().contains(LexConstants.IMG_SUFFIX)) {
				String key = entry.getKey().substring(0, entry.getKey().indexOf(LexConstants.IMG_SUFFIX));
				nodesModifiedNew.put(key, entry.getValue());
			} else {
				nodesModifiedNew.put(entry.getKey(), entry.getValue());
			}
		}

		return nodesModifiedNew;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> removeImageNodeSuffixFromHierarchyModified(Map<String, Object> hierarchy) {

		Map<String, Object> hierarchyNew = new HashMap<>();
		for (Map.Entry<String, Object> entry : hierarchy.entrySet()) {

			Map<String, Object> newHierarchyContentMap = new HashMap<>((Map<String, Object>) entry.getValue());

			List<Map<String, Object>> childrenMaps = (List<Map<String, Object>>) newHierarchyContentMap
					.get(LexConstants.CHILDREN);

			for (Map<String, Object> childMap : childrenMaps) {
				String childIdentifier = childMap.get(LexConstants.IDENTIFIER).toString();
				if (childIdentifier.endsWith(LexConstants.IMG_SUFFIX)) {
					childMap.put(LexConstants.IDENTIFIER,
							childIdentifier.substring(0, childIdentifier.indexOf(LexConstants.IMG_SUFFIX)));
				}
			}

			if (entry.getKey().contains(LexConstants.IMG_SUFFIX)) {
				String key = entry.getKey().substring(0, entry.getKey().indexOf(LexConstants.IMG_SUFFIX));
				hierarchyNew.put(key, newHierarchyContentMap);
			} else {
				hierarchyNew.put(entry.getKey(), newHierarchyContentMap);
			}
		}
		return hierarchyNew;
	}

//	@SuppressWarnings({ "unchecked", "unused" })
//	@Override
//	public Map<String, Object> getContentHierarchyFields(String identifier, String rootOrg, String org,
//			Map<String, Object> reqMap) throws Exception {
//		Session session = neo4jDriver.session();
//
//		Map<String, Object> hierarchyMap = new HashMap<>();
//		List<String> fields = new ArrayList<>();
//		boolean fieldsPassed = false;
//		try {
//			fields = (List<String>) reqMap.get(LexConstants.FIELDS);
//			System.out.println(fields);
//			fieldsPassed = (boolean) reqMap.get(LexConstants.FIELDS_PASSED);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(e.getMessage());
//		}
//
//		final boolean effFinalValue = fieldsPassed;
//		final List<String> effFinalFields = new ArrayList<>(fields);
//
//		hierarchyMap = session.readTransaction(new TransactionWork<Map<String, Object>>() {
//			@Override
//			public Map<String, Object> execute(Transaction tx) {
//				//DECOMMISION
//				return getHierarchyFromNeo4j(identifier, rootOrg, tx, effFinalValue, effFinalFields);
//			}
//		});
//
//		String creatorContactsJson = hierarchyMap.get(LexConstants.CREATOR_CONTACTS).toString();
//		List<Map<String, Object>> creatorContacts = new ObjectMapper().readValue(creatorContactsJson, List.class);
//		hierachyForViewing(hierarchyMap);
//		session.close();
//		return hierarchyMap;
//
//	}

	public static Map<String, Object> fieldsRequired(List<String> fields, Map<String, Object> nodeMap) {
		Map<String, Object> resultMap = new HashMap<>();
		String identifier = (String) nodeMap.get(LexConstants.IDENTIFIER);
		String creatorContacts = (String) nodeMap.get(LexConstants.CREATOR_CONTACTS);
		resultMap.put(LexConstants.IDENTIFIER, identifier);
		resultMap.put(LexConstants.CREATOR_CONTACTS, creatorContacts);
		for (String field : fields) {
			if (nodeMap.containsKey(field)) {
				resultMap.put(field, nodeMap.getOrDefault(field, " "));
			}
		}
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Response extendContentExpiry(Map<String, Object> requestBody) throws Exception {
		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();
		Response response = new Response();
		List<String> exclusions = new ArrayList();
		String identifier = null;
		String rootOrg = null;
		String org = null;
		String expiryDate = null;
		boolean isExtend = false;
		Date newDate = null;
		List<UpdateMetaRequest> updateList = new ArrayList<>();
		try {
			identifier = (String) requestBody.get(LexConstants.IDENTIFIER);
			rootOrg = (String) requestBody.get(LexConstants.ROOT_ORG);
			org = (String) requestBody.get(LexConstants.ORG);
			exclusions = (List<String>) requestBody.getOrDefault(LexConstants.EXCLUSIONS, new ArrayList<>());
			isExtend = (boolean) requestBody.get(LexConstants.ISEXTEND);
			Date someValue = inputFormatterDateTime.parse((String) requestBody.get(LexConstants.EXPIRY_DATE));
			expiryDate = (String) inputFormatterDateTime.format(someValue);
			if (identifier == null || identifier.isEmpty() || rootOrg == null || rootOrg.isEmpty() || org == null
					|| org.isEmpty() || exclusions == null) {
				throw new BadRequestException("Invalid request body");
			}
			newDate = inputFormatterDateTime.parse(expiryDate);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		if (isExtend) {
			ContentNode contentNode = graphService.getNodeByUniqueIdV3(rootOrg, identifier, transaction);
			Date existingDate = inputFormatterDateTime
					.parse((String) contentNode.getMetadata().get(LexConstants.EXPIRY_DATE));
			if (existingDate.compareTo(newDate) > 0) {
				throw new BadRequestException("Invalid expiryDate");
			}
			Map<String, Object> contentData = contentNode.getMetadata();
			updateList = createUpdateListForExpiryChange(identifier, contentData, expiryDate);
			System.out.println(updateList);
			try {
				graphService.updateNodesV2(rootOrg, updateList, transaction);
				transaction.commitAsync().toCompletableFuture().get();
				response.put("Message", "Extended content Expiry Date to : " + expiryDate);
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollbackAsync().toCompletableFuture().get();
				throw e;
			} finally {
				session.close();
			}
		} else {
			updateList = createListForExpiredContent(identifier);
			System.out.println(updateList);
			try {
				graphService.updateNodesV2(rootOrg, updateList, transaction);
				transaction.commitAsync().toCompletableFuture().get();
				response.put("Message", "Operation performed Contents Marked For Deletion");
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollbackAsync().toCompletableFuture().get();
				throw e;
			} finally {
				session.close();
			}
		}
		return response;
	}

	private List<UpdateMetaRequest> createListForExpiredContent(String identifier) {

		List<UpdateMetaRequest> updateList = new ArrayList<>();
		Map<String, Object> updateMap = new HashMap<>();
		Calendar validTill = Calendar.getInstance();
		updateMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()) + "+0000");
		updateMap.put(LexConstants.STATUS, LexConstants.MARKED_FOR_DELETION);
		UpdateMetaRequest updateMapReq = new UpdateMetaRequest(identifier, updateMap);
		updateList.add(updateMapReq);

		return updateList;
	}

	private List<Map<String, Object>> filterExclusionContents(List<String> ids, List<String> exclusions) {
		List<Map<String, Object>> flatListOfMaps = new ArrayList<>();
		for (String identifier : ids) {
			if (!exclusions.contains(identifier)) {
				Map<String, Object> tempMap = new HashMap<>();
				Calendar dueDate = Calendar.getInstance();
				dueDate.add(Calendar.MONTH, 6);
				tempMap.put(LexConstants.IDENTIFIER, identifier);
				System.out.println(dueDate);
				tempMap.put(LexConstants.EXPIRY_DATE, inputFormatterDateTime.format(dueDate.getTime()) + "+0000");
				flatListOfMaps.add(tempMap);
			}
		}
		return flatListOfMaps;
	}

	private List<UpdateMetaRequest> createUpdateListForExpiryChange(String identifier, Map<String, Object> contentMeta,
			String expiryDate) {

		List<UpdateMetaRequest> updateList = new ArrayList<>();
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put(LexConstants.IDENTIFIER, identifier);
		Calendar validTill = Calendar.getInstance();
		updateMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()) + "+0000");
		updateMap.put(LexConstants.EXPIRY_DATE, expiryDate);
		UpdateMetaRequest updateMapReq = new UpdateMetaRequest((String) contentMeta.get(LexConstants.IDENTIFIER),
				updateMap);
		updateList.add(updateMapReq);
		return updateList;
	}

	private List<String> getToBeDeletedContents(String identifier, String rootOrg) {
		Session session = neo4jDriver.session();
		List<String> tbdContent = session.readTransaction(new TransactionWork<List<String>>() {

			@Override
			public List<String> execute(Transaction tx) {
				String query = "match(n{identifier:'" + identifier + "'}) where n:Shared or n:" + rootOrg
						+ " with n optional match(n)-[r:Has_Sub_Content*]->(s) where s:Shared or s:" + rootOrg
						+ " and n.status='Live' and s.status='Live' return s.identifier";
				System.out.println("Runnning query");
				StatementResult statementResult = tx.run(query);
				List<Record> records = statementResult.list();
				List<String> tbdContents = new ArrayList<>();
				for (Record rec : records) {
					String id = rec.get("s.identifier").toString();
					id = id.replace("\"", "");
					tbdContents.add(id);
				}
				return tbdContents;
			}
		});
		tbdContent.add(identifier);
		return tbdContent;

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static Map<String, Object> getHierarchyForAuthor(Map<String, Object> mapObj, String creatorEmail)
			throws IOException {

		// adding hierarchy map to queue
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(mapObj);
		ObjectMapper mapper = new ObjectMapper();
		UUID tempObj = null;
		try {
			tempObj = UUID.fromString(creatorEmail);
		} catch (ClassCastException | IllegalArgumentException e) {
			throw new BadRequestException("MUST BE A VALID UUID");
		} catch (Exception e) {
			throw new ApplicationLogicError("userId");
		}

		while (!parentObjs.isEmpty()) {

			// pull out top-level parent
			Map<String, Object> parent = parentObjs.poll();
			// added children of parent to list
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			// set to log all visited child-ids for a given parent
			Set<String> iteratorSet = new HashSet<>();
			List<Map<String, Object>> validChildren = new ArrayList<>();

			for (Map<String, Object> child : childrenList) {

				Set<String> creatorEmails = new HashSet<>();
				List<Map<String, Object>> creators = new ArrayList<>();
				try {
					creators = mapper.readValue(child.get(LexConstants.CREATOR_CONTACTS).toString(), ArrayList.class);
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
				for (Map<String, Object> creator : creators) {
					// obtain all creatorEmails from creatorContacts
					creatorEmails.add(creator.get(LexConstants.ID).toString());
				}
				// if provided email is in above created SET then child is valid
				// for author
				if (creatorEmails.contains(creatorEmail)) {
					// add child-id to set
					iteratorSet.add(child.get(LexConstants.IDENTIFIER).toString());
					// add child object to a list of validChildren
					validChildren.add(child);
				}
			}
			List<Map<String, Object>> validChildrenTest = new ArrayList<>(validChildren);
			// iterate on List of Valid Children
			for (Map<String, Object> validChild : validChildrenTest) {
				String itId = validChild.get(LexConstants.IDENTIFIER).toString();
				String itIdImg = itId + LexConstants.IMG_SUFFIX;
				// remove org node if image node is present
				if (iteratorSet.contains(itId) && iteratorSet.contains(itIdImg)) {
					validChildren.remove(validChild);
				}
			}
			// finally add all valid children to parent
			parent.put(LexConstants.CHILDREN, validChildren);
			parentObjs.addAll(childrenList);
		}
		return mapObj;
	}

	private static String getUniqueIdFromTimestamp(int environmentId) {

		Random random = new Random();
		long env = (environmentId + random.nextInt(99999)) / 10000000;
		long uid = System.currentTimeMillis() + random.nextInt(999999);
		uid = uid << 13;
		return env + "" + uid + "" + atomicInteger.getAndIncrement();
	}

	private void createOperationValidations(Map<String, Object> contentMeta) {

		if (contentMeta.containsKey(LexConstants.IDENTIFIER))
			throw new BadRequestException("identifier cannot not be present while creating content");

		if (contentMeta.containsKey(LexConstants.CHILDREN))
			throw new BadRequestException("children cannot be present while creating a content");

		if (contentMeta.containsKey(LexConstants.COLLECTION))
			throw new BadRequestException("collections cannot be present while creating a content");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void calcChildTitleDesc(Map<String, Object> contentHierarchy) {

		Stack<Map<String, Object>> stack = new Stack<>();
		stack.push(contentHierarchy);

		Map<String, Object> childTitle = new HashMap<>();
		Map<String, Object> childDesc = new HashMap<>();
		while (!stack.isEmpty()) {

			Map<String, Object> parent = stack.peek();
			List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);

			if (children == null || children.isEmpty()) {
				stack.pop();
				childTitle.put(parent.get(LexConstants.IDENTIFIER).toString(), parent.get(LexConstants.NAME));
				childDesc.put(parent.get(LexConstants.IDENTIFIER).toString(), parent.get(LexConstants.DESC));
			} else {
				boolean childTitleExists = true;
				boolean childDescExists = true;
				for (Map<String, Object> child : children) {
					if (!childTitle.containsKey(child.get(LexConstants.IDENTIFIER).toString())
							&& !childDesc.containsKey(child.get(LexConstants.IDENTIFIER).toString())) {
						childTitleExists = false;
						childDescExists = false;
						stack.push(child);
					}
				}
				if (childTitleExists && childDescExists) {
					stack.pop();
					List<String> parentTitle = new ArrayList<>();
					List<String> parentDesc = new ArrayList<>();
					for (Map<String, Object> child : children) {
						parentTitle.add((String) childTitle.get(child.get(LexConstants.IDENTIFIER).toString()));
						parentTitle.addAll((List) child.getOrDefault(LexConstants.CHILD_TITLE, new ArrayList<>()));
						parentDesc.add((String) childDesc.get(child.get(LexConstants.IDENTIFIER).toString()));
						parentDesc.addAll((List) child.getOrDefault(LexConstants.CHILD_DESC, new ArrayList<>()));
					}

					List<String> finalTitleList = checkSimilar((String) parent.get(LexConstants.NAME), parentTitle);
					List<String> finalDescList = checkSimilar((String) parent.get(LexConstants.DESC), parentDesc);
					parent.put(LexConstants.CHILD_TITLE, finalTitleList);
					parent.put(LexConstants.CHILD_DESC, finalDescList);
					childDesc.put(parent.get(LexConstants.IDENTIFIER).toString(), parent.get(LexConstants.NAME));
					childTitle.put(parent.get(LexConstants.IDENTIFIER).toString(), parent.get(LexConstants.DESC));
				}
			}
		}
	}

	private List<String> checkSimilar(String mainString, List<String> listVals) {
		List<String> returnList = new ArrayList<>();
		for (String strVal : listVals) {
			if (!strVal.isEmpty()) {
				if (strVal.length() > mainString.length()) {
					double val = (double) RatcliffObershelpMetric.compare(strVal, mainString).get();
					if (val <= 0.3) {
						returnList.add(strVal);
					}
				} else {
					double val = (double) RatcliffObershelpMetric.compare(mainString, strVal).get();
					if (val <= 0.3) {
						returnList.add(strVal);
					}
				}
			}
		}
		return returnList;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void contentUnpublish(Map<String, Object> requestMap, String rootOrg, String org) throws BadRequestException, Exception {
		
		String identifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		Boolean unPublish = (Boolean) requestMap.get("unpublish");
		ObjectMapper mapper = new ObjectMapper();
		if(rootOrg==null||rootOrg.isEmpty()||org==null||org.isEmpty()||identifier==null||identifier.isEmpty()||unPublish==null) {
			throw new BadRequestException("Invalid request body");
		}
		
		
		if(unPublish) {
			
			Session session = neo4jDriver.session();
			Transaction tx = session.beginTransaction();
			Map<String, Object> boolMap = new HashMap<>();
			boolMap.put("isFirstCall", true);
			Map<String,Object> errorMap = new HashMap<>();
			ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
			
			if(node==null) {
				throw new ResourceNotFoundException("Content with identifier does not exist");
			}
			
			if(!identifier.contains(LexConstants.IMG_SUFFIX)) {
				String imgId = identifier + LexConstants.IMG_SUFFIX;
				ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, imgId, tx);
				if(imgNode!=null) {
					throw new BadRequestException("Cannot unpublish content as img node is present");
				}
			}
			else if(identifier.contains(LexConstants.IMG_SUFFIX)) {
				throw new BadRequestException("Cannot unpublish as img node is present");
				}
			
			List<String> idsToUnpublish = new ArrayList<>();
			
			Queue<ContentNode> contentQueue = new LinkedList<>();
			contentQueue.add(node);
			Set<String> masterCreators = returnCreatorIds(node);
			
			while (!contentQueue.isEmpty()) {
				ContentNode subNode = contentQueue.poll();
	
				// check if deletion is allowed
				boolean unpublish = allowedToUnpublish(boolMap, subNode,errorMap, tx,rootOrg,true);
				
				if (unpublish) {
					Set<String> creators = returnCreatorIds(subNode);
					creators.retainAll(masterCreators);
					if(creators.size()>0) {
						idsToUnpublish.add(subNode.getIdentifier());
					}
					
				}
				
				//if not channel or KB add children to contentQueue
				if (checkKbChannelConstraints(subNode)) {
					contentQueue.addAll(graphService.getNodesByUniqueIdV2(rootOrg,
							subNode.getChildren().stream().map(child -> child.getEndNodeId()).collect(Collectors.toList()),
							tx));
				}
				boolMap.put("isFirstCall", false);
			}
			
			
			List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();

			
			for(String lexId : idsToUnpublish) {
				Map<String,Object> updateMap = new HashMap<>();
				updateMap.put(LexConstants.STATUS, LexConstants.Status.UnPublish.getStatus());
				updateMetaRequests.add(new UpdateMetaRequest(lexId, updateMap));
			}
			try {
				graphService.updateNodesV2(rootOrg, updateMetaRequests, tx);
				tx.commitAsync().toCompletableFuture().get();
			} catch (Exception e) {
				tx.commitAsync().toCompletableFuture().get();
				throw new Exception(e);
			}
			finally {
				tx.close();
				session.close();
			}
//			return "Successfully changed status to Unpublish";
		}
		
		else {
			

			Session session = neo4jDriver.session();
			Transaction tx = session.beginTransaction();
			Map<String, Object> boolMap = new HashMap<>();
			boolMap.put("isFirstCall", true);
			Map<String,Object> errorMap = new HashMap<>();
			ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
			
			if(node==null) {
				throw new ResourceNotFoundException("Content with identifier does not exist");
			}
			
			if(!identifier.contains(LexConstants.IMG_SUFFIX)) {
				String imgId = identifier + LexConstants.IMG_SUFFIX;
				ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, imgId, tx);
				if(imgNode!=null) {
					throw new BadRequestException("Cannot change to Draft content as img node is present");
				}
			}
			else if(identifier.contains(LexConstants.IMG_SUFFIX)) {
				throw new BadRequestException("Cannot change to Draft as img node is present");
				}
			
			List<String> idsToUnpublish = new ArrayList<>();
			
			Queue<ContentNode> contentQueue = new LinkedList<>();
			contentQueue.add(node);
			Set<String> masterCreators = returnCreatorIds(node);
			
			while (!contentQueue.isEmpty()) {
				ContentNode subNode = contentQueue.poll();
	
				// check if deletion is allowed
				boolean unpublish = allowedToUnpublish(boolMap, subNode,errorMap, tx,rootOrg,false);
				
				if (unpublish) {
					Set<String> creators = returnCreatorIds(subNode);
					creators.retainAll(masterCreators);
					if(creators.size()>0) {
						idsToUnpublish.add(subNode.getIdentifier());
					}
					
				}
				
				//if not channel or KB add children to contentQueue
				if (checkKbChannelConstraints(subNode)) {
					contentQueue.addAll(graphService.getNodesByUniqueIdV2(rootOrg,
							subNode.getChildren().stream().map(child -> child.getEndNodeId()).collect(Collectors.toList()),
							tx));
				}
				boolMap.put("isFirstCall", false);
			}
			
			
			List<UpdateMetaRequest> updateMetaRequests = new ArrayList<>();

			
			for(String lexId : idsToUnpublish) {
				Map<String,Object> updateMap = new HashMap<>();
				updateMap.put(LexConstants.STATUS, LexConstants.Status.Draft.getStatus());
				updateMetaRequests.add(new UpdateMetaRequest(lexId, updateMap));
			}
			try {
				graphService.updateNodesV2(rootOrg, updateMetaRequests, tx);
				tx.commitAsync().toCompletableFuture().get();
			} catch (Exception e) {
				tx.commitAsync().toCompletableFuture().get();
				throw new Exception(e);
			}
			finally {
				tx.close();
				session.close();
			}
//			return "Successfully changed status to Draft";
		}
	}

	private boolean allowedToUnpublish(Map<String, Object> boolMap, ContentNode node, Map<String, Object> errorMap,
			Transaction tx, String rootOrg,Boolean toUnpublish) {
		
		if(toUnpublish) {
			if((boolean) boolMap.get("isFirstCall")) {
				if(node.getParents().size()>0) {
					throw new BadRequestException("Cannot unpublish as parent exists");
				}
			}
			else {
				if(node.getParents().size()>1) {
					errorMap.put(node.getIdentifier(), "Cannot unpublish as it is being re-used");
					return false;
				}
			}
		}
		return true;
	}	
	
	@SuppressWarnings("unchecked")
	private Set<String> returnCreatorIds(ContentNode node) throws JsonParseException, JsonMappingException, IOException{
		
		Set<String> creators = new HashSet<>();
		List<Map<String,Object>> creatorContacts = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.CREATOR_CONTACTS);
		
		for(Map<String, Object> creatorObj:creatorContacts) {
			creators.add((String) creatorObj.get("id"));
		}
		return creators;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getMultipleHierarchy(String rootOrg, String org, Map<String, Object> requestMap) throws BadRequestException,Exception {
		
		List<String> identifiers = (List<String>) requestMap.get(LexConstants.IDENTIFIER);
		List<String> fields = (List<String>) requestMap.get(LexConstants.FIELDS);
		if(rootOrg==null||rootOrg.isEmpty()||org==null||org.isEmpty()||identifiers==null||identifiers.isEmpty()) {
			throw new BadRequestException("Invalid input");
		}
		
		Map<String,Object> rMap = new HashMap<>();
		rMap.put(LexConstants.FIELDS, fields);
		List<Map<String,Object>> hierarchyList = new ArrayList<>();
		
		for(String identifier:identifiers) {
			Map<String,Object> hMap = getContentHierarchyV2(identifier,rootOrg,org,rMap);
			hierarchyList.add(hMap);
		}
		
		return hierarchyList;
	}

	
}
