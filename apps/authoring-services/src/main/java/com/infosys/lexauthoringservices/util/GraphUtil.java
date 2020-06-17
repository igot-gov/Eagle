package com.infosys.lexauthoringservices.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.model.neo4j.Relation;

public class GraphUtil {

	// fields to be serialized and de-serialized while storing and fetching from
	// neo4j.
	public static final List<String> fieldsToParse = Arrays.asList(LexConstants.COMMENTS,
			LexConstants.CERTIFICATION_LIST, LexConstants.PLAYGROUND_RESOURCES, LexConstants.SOFTWARE_REQUIREMENTS,
			LexConstants.SYSTEM_REQUIREMENTS, LexConstants.REFERENCES, LexConstants.CREATOR_CONTACTS,
			LexConstants.CREATOR_DEATILS, LexConstants.PUBLISHER_DETAILS, LexConstants.PRE_CONTENTS,
			LexConstants.POST_CONTENTS, LexConstants.CATALOG, LexConstants.CLIENTS, LexConstants.SKILLS,
			LexConstants.K_ARTIFACTS, LexConstants.TRACK_CONTACT_DETAILS, LexConstants.ORG,
			LexConstants.SUBMITTER_DETAILS, LexConstants.CONCEPTS,LexConstants.PLAG_SCAN, LexConstants.TAGS,
			"eligibility", "scoreType", "externalData", "verifiers", "verifier", "subTitles", "roles", "group",
			"msArtifactDetails", "studyMaterials", "equivalentCertifications", LexConstants.TRANSCODING,
			LexConstants.PRICE, LexConstants.EDITORS);
	
	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

	public static ContentNode createContentNode(List<Record> records)
			throws JsonParseException, JsonMappingException, IOException {
		ContentNode contentNode = new ContentNode();

		Set<Relationship> childRelations = new HashSet<>();
		Set<Relationship> parentRelations = new HashSet<>();

		if (records.get(0).get(LexConstants.NODE).isNull()) {
			return null;
		}

		for (Record record : records) {
			if (record.get(LexConstants.NODE).isNull()) {
				continue;
			}
			Node nodeFetched = record.get(LexConstants.NODE).asNode();
			String rootOrg = nodeFetched.labels().iterator().next();

			contentNode.setId(nodeFetched.id());
			contentNode.setRootOrg(rootOrg);
			contentNode.setIdentifier(nodeFetched.asMap().get(LexConstants.IDENTIFIER).toString());
			contentNode.setMetadata(convertToHashMap(nodeFetched.asMap()));

			createChildRelation(contentNode, childRelations, record);
			createParentRelation(contentNode, parentRelations, record);
		}

		contentNode.setMetadata(mapParser(contentNode.getMetadata(), true));
		return contentNode;
	}

	
	public static List<ContentNode> createContentNodesForHierarchyUpdateV2(List<Record> records)
			throws JsonParseException, JsonMappingException, IOException {

		List<ContentNode> contentNodes = new ArrayList<>();

		//iterate on all existing records
		Iterator<Record> recordsIterator = records.iterator();
		while (recordsIterator.hasNext()) {
			if (recordsIterator.next().get("node").isNull()) {
				//if record does not contain 'node' remove from records
				recordsIterator.remove();
			}
		}

		//previous node identifier
		String prevNode = records.get(0).get("node.identifier").asString();

		List<Record> recordsPerContent = new ArrayList<>();

		for (Record record : records) {
			//if record does not contain identifier skip and move on
			if (record.get("node.identifier").isNull()) {
				continue;
			}
			//if previous node does not equal current record identifier then ->
			if (!prevNode.equals(record.get("node.identifier").asString())) {
				//add that node to list of contentNodes
								//below function returns the node metaData and minimal data of 1st level children and parent ie (status,contentType,isStandAlone,identifier)
				contentNodes.add(createContentNodeForHierarchyUpdateV2(recordsPerContent));
				//reset records
				recordsPerContent = new ArrayList<>();
				//make new record as previous node
				prevNode = record.get("node.identifier").asString();
			}
			//continue to add all records to new records list
			recordsPerContent.add(record);
		}

		//get and then add the details of the left over records to content nodes list
		contentNodes.add(createContentNodeForHierarchyUpdateV2(recordsPerContent));
		//return list
		return contentNodes;
	}
	
	
	public static List<ContentNode> createContentNodesForHierarchyUpdate(List<Record> records)
			throws JsonParseException, JsonMappingException, IOException {

		List<ContentNode> contentNodes = new ArrayList<>();

		Iterator<Record> recordsIterator = records.iterator();
		while (recordsIterator.hasNext()) {
			if (recordsIterator.next().get("node.identifier").isNull()) {
				recordsIterator.remove();
			}
		}

		String prevNode = records.get(0).get("node.identifier").asString();

		List<Record> recordsPerContent = new ArrayList<>();

		for (Record record : records) {
			if (record.get("node.identifier").isNull()) {
				continue;
			}
			if (!prevNode.equals(record.get("node.identifier").asString())) {
				contentNodes.add(createContentNodeForHierarchyUpdate(recordsPerContent));
				recordsPerContent = new ArrayList<>();
				prevNode = record.get("node.identifier").asString();
			}
			recordsPerContent.add(record);
		}

		contentNodes.add(createContentNodeForHierarchyUpdate(recordsPerContent));
		return contentNodes;
	}
	
	
	public static ContentNode createContentNodeForHierarchyUpdateV2(List<Record> records)throws JsonParseException, JsonMappingException, IOException {

		ContentNode contentNode = new ContentNode();
		
		Set<Relationship> childRelations = new HashSet<>();
		Set<Relationship> parentRelations = new HashSet<>();
		
		for(Record record : records) {
			if(record.get("node").isNull()) {
				continue;
			}
			
			String rootOrg = (String) record.get("labels(node)").asList().get(0);
			contentNode.setRootOrg(rootOrg);
			contentNode.setIdentifier(record.get("node.identifier").asString());
			Map<String,Object> metaData =record.get("node").asMap();
			Map<String,Object> modifiableMap = new HashMap<>(metaData);
			contentNode.setMetadata(modifiableMap);
			
			createChildRelationForHierarchyUpdate(contentNode, childRelations, record);
			createParentRelationForHierarchyUpdate(contentNode, parentRelations, record);
		}
		
		contentNode.setMetadata(mapParser(contentNode.getMetadata(), true));
		return contentNode;
	}
	
	
	public static List<ContentNode> createContentNodesForHierarchyUpdateV3(List<Record> records,List<String> fields)
			throws JsonParseException, JsonMappingException, IOException {

		List<ContentNode> contentNodes = new ArrayList<>();

		Iterator<Record> recordsIterator = records.iterator();
		while (recordsIterator.hasNext()) {
			if (recordsIterator.next().get("node.identifier").isNull()) {
				recordsIterator.remove();
			}
		}

		String prevNode = records.get(0).get("node.identifier").asString();

		List<Record> recordsPerContent = new ArrayList<>();

		for (Record record : records) {
			if (record.get("node.identifier").isNull()) {
				continue;
			}
			if (!prevNode.equals(record.get("node.identifier").asString())) {
				contentNodes.add(createContentNodeForHierarchyUpdateV3(recordsPerContent,fields));
				recordsPerContent = new ArrayList<>();
				prevNode = record.get("node.identifier").asString();
			}
			recordsPerContent.add(record);
		}

		contentNodes.add(createContentNodeForHierarchyUpdateV3(recordsPerContent,fields));
		return contentNodes;
	}
	

	public static ContentNode createContentNodeForHierarchyUpdate(List<Record> records)
			throws JsonParseException, JsonMappingException, IOException {
		ContentNode contentNode = new ContentNode();

		Set<Relationship> childRelations = new HashSet<>();
		Set<Relationship> parentRelations = new HashSet<>();

		for (Record record : records) {
			if (record.get("node.identifier").isNull()) {
				continue;
			}
			String rootOrg = (String) record.get("labels(node)").asList().get(0);
			contentNode.setId(Long.parseLong(record.get("id(node)").toString()));

			contentNode.setRootOrg(rootOrg);
			contentNode.setIdentifier(record.get("node.identifier").asString());
			Map<String, Object> metaData = new HashMap<>();
			metaData.put(LexConstants.CONTENT_TYPE, record.get("node.contentType").asString());
			metaData.put(LexConstants.STATUS, record.get("node.status").asString());
			metaData.put(LexConstants.IDENTIFIER, record.get("node.identifier").asString());
			metaData.put(LexConstants.IS_STAND_ALONE, record.get("node.isStandAlone").asBoolean(true));
			Map<String, Object> x = record.asMap();
			metaData.put(LexConstants.AUTHORING_DISABLED, x.get("node.authoringDisabled"));
			metaData.put(LexConstants.META_EDIT_DISABLED, x.get("node.isMetaEditingDisabled"));
			contentNode.setMetadata(metaData);

			createChildRelationForHierarchyUpdate(contentNode, childRelations, record);
			createParentRelationForHierarchyUpdate(contentNode, parentRelations, record);
		}

		contentNode.setMetadata(mapParser(contentNode.getMetadata(), true));
		return contentNode;
	}
	
	public static ContentNode createContentNodeForHierarchyUpdateV3(List<Record> records,List<String> fields)
			throws JsonParseException, JsonMappingException, IOException {
		ContentNode contentNode = new ContentNode();

		Set<Relationship> childRelations = new HashSet<>();
		Set<Relationship> parentRelations = new HashSet<>();

		for (Record record : records) {
			if (record.get("node.identifier").isNull()) {
				continue;
			}
			String rootOrg = (String) record.get("labels(node)").asList().get(0);
			contentNode.setId(Long.parseLong(record.get("id(node)").toString()));

			contentNode.setRootOrg(rootOrg);
			contentNode.setIdentifier(record.get("node.identifier").asString());
			Map<String, Object> metaData = new HashMap<>();
			Map<String,Object> recordMap = record.asMap();
			for(String item:fields) {
				metaData.put(item, recordMap.get("node."+item));				
			}
			
			contentNode.setMetadata(metaData);
//			metaData.put(LexConstants.CONTENT_TYPE, record.get("node.contentType").asString());
//			metaData.put(LexConstants.STATUS, record.get("node.status").asString());
//			metaData.put(LexConstants.IDENTIFIER, record.get("node.identifier").asString());
//			metaData.put(LexConstants.IS_STAND_ALONE, record.get("node.isStandAlone").asBoolean(true));
//			Map<String, Object> x = record.asMap();
//			metaData.put(LexConstants.AUTHORING_DISABLED, x.get("node.authoringDisabled"));
//			metaData.put(LexConstants.META_EDIT_DISABLED, x.get("node.isMetaEditingDisabled"));
//			contentNode.setMetadata(metaData);

			createChildRelationForHierarchyUpdate(contentNode, childRelations, record);
			createParentRelationForHierarchyUpdate(contentNode, parentRelations, record);
		}

		contentNode.setMetadata(mapParser(contentNode.getMetadata(), true));
		return contentNode;
	}

	public static List<ContentNode> createContentNodes(List<Record> records)
			throws JsonParseException, JsonMappingException, IOException {

		List<ContentNode> contentNodes = new ArrayList<>();

		if (records.get(0).get(LexConstants.NODE).isNull()) {
			return contentNodes;
		}

		Node prevNode = records.get(0).get(LexConstants.NODE).asNode();
		List<Record> recordsPerContent = new ArrayList<>();

		for (Record record : records) {

			if (record.get(LexConstants.NODE).isNull()) {
				continue;
			}
			if (!prevNode.equals(record.get(LexConstants.NODE).asNode())) {
				contentNodes.add(createContentNode(recordsPerContent));
				recordsPerContent = new ArrayList<>();
				prevNode = record.get(LexConstants.NODE).asNode();
			}
			recordsPerContent.add(record);
		}

		contentNodes.add(createContentNode(recordsPerContent));
		return contentNodes;
	}

	public static void createChildRelation(ContentNode contentNode, Set<Relationship> childRelations, Record record) {

		if (!record.get(LexConstants.CHILD).isNull() && !record.get(LexConstants.CHILD_RELATION).isNull()) {

			Node childNode = record.get(LexConstants.CHILD).asNode();
			Relationship childRelation = record.get(LexConstants.CHILD_RELATION).asRelationship();

			if (childRelation != null && !childRelations.contains(childRelation)) {
				Relation relation = new Relation();

				relation.setId(childRelation.id());
				relation.setMetadata(childRelation.asMap());

				relation.setStartNodeId(contentNode.getIdentifier());
				relation.setStartNodeMetadata(contentNode.getMetadata());

				relation.setEndNodeId(childNode.asMap().get(LexConstants.IDENTIFIER).toString());
				relation.setEndNodeMetadata(childNode.asMap());

				relation.setRelationType("Has_Sub_Content");

				contentNode.getChildren().add(relation);
				childRelations.add(childRelation);
			}
		}
	}

	public static void createChildRelationForHierarchyUpdate(ContentNode contentNode, Set<Relationship> childRelations,
			Record record) {

		if (!record.get("child.identifier").isNull() && !record.get(LexConstants.CHILD_RELATION).isNull()) {

			Relationship childRelation = record.get(LexConstants.CHILD_RELATION).asRelationship();

			if (childRelation != null && !childRelations.contains(childRelation)) {
				Relation relation = new Relation();

				relation.setId(childRelation.id());
				relation.setMetadata(childRelation.asMap());

				relation.setStartNodeId(contentNode.getIdentifier());
				relation.setStartNodeMetadata(contentNode.getMetadata());

				relation.setEndNodeId(record.get("child.identifier").asString());
				Map<String, Object> metaData = new HashMap<>();
				metaData.put(LexConstants.CONTENT_TYPE, record.get("child.contentType").asString());
				metaData.put(LexConstants.STATUS, record.get("child.status").asString());
				metaData.put(LexConstants.IDENTIFIER, record.get("child.identifier").asString());
				metaData.put(LexConstants.IS_STAND_ALONE, record.get("child.isStandAlone").asBoolean(true));
				relation.setEndNodeMetadata(metaData);

				relation.setRelationType("Has_Sub_Content");

				contentNode.getChildren().add(relation);
				childRelations.add(childRelation);
			}
		}
	}

	public static void createParentRelationForHierarchyUpdate(ContentNode contentNode,
			Set<Relationship> parentRelations, Record record) {

		if (!record.get("parent.identifier").isNull() && !record.get(LexConstants.PARENT_RELATION).isNull()) {

			Relationship parentRelation = record.get(LexConstants.PARENT_RELATION).asRelationship();

			if (parentRelation != null && !parentRelations.contains(parentRelation)) {
				Relation relation = new Relation();

				relation.setId(parentRelation.id());
				relation.setMetadata(parentRelation.asMap());

				relation.setStartNodeId(record.get("parent.identifier").asString());
				Map<String, Object> metaData = new HashMap<>();
				metaData.put(LexConstants.CONTENT_TYPE, record.get("parent.contentType").asString());
				metaData.put(LexConstants.STATUS, record.get("parent.status").asString());
				metaData.put(LexConstants.IDENTIFIER, record.get("parent.identifier").asString());
				metaData.put(LexConstants.IS_STAND_ALONE, record.get("parent.isStandAlone").asBoolean(true));
				relation.setStartNodeMetadata(metaData);

				relation.setEndNodeId(contentNode.getIdentifier());
				relation.setEndNodeMetadata(contentNode.getMetadata());

				relation.setRelationType("Has_Sub_Content");

				contentNode.getParents().add(relation);
				parentRelations.add(parentRelation);
			}
		}
	}

	public static void createParentRelation(ContentNode contentNode, Set<Relationship> parentRelations, Record record) {
		if (!record.get(LexConstants.PARENT).isNull() && !record.get(LexConstants.PARENT_RELATION).isNull()) {

			Node parentNode = record.get(LexConstants.PARENT).asNode();
			Relationship parentRelation = record.get(LexConstants.PARENT_RELATION).asRelationship();

			if (parentRelation != null && !parentRelations.contains(parentRelation)) {
				Relation relation = new Relation();

				relation.setId(parentRelation.id());
				relation.setMetadata(parentRelation.asMap());

				relation.setStartNodeId(parentNode.asMap().get(LexConstants.IDENTIFIER).toString());
				relation.setStartNodeMetadata(parentNode.asMap());

				relation.setEndNodeId(contentNode.getIdentifier());
				relation.setEndNodeMetadata(contentNode.getMetadata());

				relation.setRelationType("Has_Sub_Content");

				contentNode.getParents().add(relation);
				parentRelations.add(parentRelation);
			}
		}
	}

	public static List<Map<String, Object>> mapParser(List<Map<String, Object>> contentMetas, boolean toMap)
			throws JsonParseException, JsonMappingException, IOException {

		for (Map<String, Object> contentMeta : contentMetas) {
			mapParser(contentMeta, false);
		}
		return contentMetas;
	}

	public static Map<String, Object> mapParser(Map<String, Object> contentMeta, boolean toMap)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> updatedContentMeta = contentMeta;

		for (String fieldToParse : fieldsToParse) {

			if (updatedContentMeta.containsKey(fieldToParse) && updatedContentMeta.get(fieldToParse) != null
					&& !updatedContentMeta.get(fieldToParse).toString().isEmpty()) {
				if (toMap) {
					String fieldValue = updatedContentMeta.get(fieldToParse).toString();
					Object fieldDeserialized = mapper.readValue(fieldValue, Object.class);
					updatedContentMeta.put(fieldToParse, fieldDeserialized);
				} else if (!toMap) {
					Object fieldSerialized = updatedContentMeta.get(fieldToParse);
					updatedContentMeta.put(fieldToParse, mapper.writeValueAsString(fieldSerialized));
				}
			}
		}
		return updatedContentMeta;
	}

	public static List<UpdateRelationRequest> createUpdateRelationRequestsForImageNodes(
			List<ContentNode> imageNodesToBeCreated) {

		List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();

		// creating relations for the imageNodes.
		for (ContentNode imageNodeToBeCreated : imageNodesToBeCreated) {

			for (Relation childRelation : imageNodeToBeCreated.getChildren()) {
				
				Map<String, Object> map = childRelation.getMetadata();
				Map<String,Object> copyMap = new HashMap<>(map);
				if(!map.containsKey(LexConstants.ADDED_ON)) {
					Calendar lastUpdatedOn = Calendar.getInstance();
					String addedOn = inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString();
					copyMap.put(LexConstants.ADDED_ON, addedOn);
				}
				updateRelationRequests.add(new UpdateRelationRequest(imageNodeToBeCreated.getIdentifier(),
						childRelation.getEndNodeId(), copyMap));
			}

			for (Relation parentRelation : imageNodeToBeCreated.getParents()) {

				Map<String, Object> map = parentRelation.getMetadata();
				Map<String,Object> copyMap = new HashMap<>(map);
				if(!map.containsKey(LexConstants.ADDED_ON)) {
					Calendar lastUpdatedOn = Calendar.getInstance();
					String addedOn = inputFormatterDateTime.format(lastUpdatedOn.getTime()).toString();
					copyMap.put(LexConstants.ADDED_ON, addedOn);
				}
				updateRelationRequests.add(new UpdateRelationRequest(parentRelation.getStartNodeId(),
						imageNodeToBeCreated.getIdentifier(), copyMap));
			}
		}

		return updateRelationRequests;
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
