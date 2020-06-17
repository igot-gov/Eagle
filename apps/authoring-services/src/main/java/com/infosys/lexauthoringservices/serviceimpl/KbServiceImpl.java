package com.infosys.lexauthoringservices.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.exception.ResourceNotFoundException;
import com.infosys.lexauthoringservices.model.DeleteRelationRequest;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.model.neo4j.Relation;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.service.KbService;
import com.infosys.lexauthoringservices.util.LexConstants;
import com.infosys.lexauthoringservices.util.LexLogger;

@Service
public class KbServiceImpl implements KbService{
	
	@Autowired
	GraphService graphService;
	
	@Autowired
	Driver neo4jDriver;

	@Autowired
	private LexLogger logger;
	
	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
	
	@SuppressWarnings("unchecked")
	@Override
	public String addChildren(String rootOrg, String org, Map<String, Object> requestMap) throws Exception {
		String kbIdentifier = null;
		List<Map<String,Object>> children = new ArrayList<>();
		List<UpdateRelationRequest> updateRequests = new ArrayList<>();
		boolean createForImg = false;
		if(requestMap==null||requestMap.isEmpty() || rootOrg==null || rootOrg.isEmpty() || org==null || org.isEmpty()) {
			throw new BadRequestException("Invalid Input");
		}
		
		kbIdentifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		if(kbIdentifier==null || kbIdentifier.isEmpty()) {
			throw new BadRequestException("Invalid identifier");
		}
		
		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		logger.info("Add Children to Kb transaction started");
		ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, kbIdentifier, tx);
		String status = (String) node.getMetadata().get(LexConstants.STATUS);
		String contentType = (String) node.getMetadata().get(LexConstants.CONTENT_TYPE);
		
		if(!status.equals(LexConstants.Status.Live.getStatus())) {
			throw new BadRequestException("Cannot add children KB is not Live");
		}
		
		if(!contentType.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())) {
			throw new BadRequestException("API only to add for KB, Invalid ContentType : " + contentType);
		}
		
		ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, kbIdentifier + LexConstants.IMG_SUFFIX, tx);
//		System.out.println(imgNode);
		
		if(imgNode!=null) {
			createForImg=true;
		}
		
		children = (List<Map<String, Object>>) requestMap.get(LexConstants.CHILDREN);	
		if(children==null || children.isEmpty()) {
			throw new BadRequestException("Children are null or empty");
		}
		
		//remove duplicates from request body and existing children
		Set<String> existingChildIds = new HashSet<>();
		Set<String> newChildrenIds = new HashSet<>();
		List<Relation> existingChildren = node.getChildren();
		
		//iterate on existing children
		for(Relation existingChild : existingChildren) {
			existingChildIds.add(existingChild.getEndNodeId());
		}
		
		//iterate on new addition of children
		for(Map<String, Object> child:children) {
			newChildrenIds.add((String) child.get(LexConstants.IDENTIFIER));
		}
		
		//final set of ids to be inserted
		newChildrenIds.removeAll(existingChildIds);
		List<String> validChildren = new ArrayList<>(newChildrenIds);
		
		//get all validChildrenIds from DB
		List<ContentNode> childrenNodes = graphService.getNodesByUniqueIdV2(rootOrg, validChildren, tx);
		List<String> childrenInDbs = new ArrayList<>();
		
		
		for(ContentNode childNode : childrenNodes) {
			childrenInDbs.add(childNode.getIdentifier());
		}
		
		
		int index = existingChildren.size();
		
		for(Map<String, Object> child: children) {
			Map<String,Object> relationMetaData = new HashMap<>();
			relationMetaData.put(LexConstants.INDEX, index);
			String childId = (String) child.getOrDefault(LexConstants.IDENTIFIER, "");
			if(childId==null || childId.isEmpty()) {
				throw new BadRequestException("Invalid Child Id");
			}
			
			//if child TBA exists in both validChildren and is present in DB, then run below block to create relation
			if(validChildren.contains(childId) && childrenInDbs.contains(childId)) {
				
				String reasonAdded = (String) child.getOrDefault(LexConstants.REASON, "");
				List<String> childrenClassifiers = (List<String>) child.getOrDefault(LexConstants.CHILDREN_CLASSIFIERS, new ArrayList<>());
				relationMetaData.put(LexConstants.INDEX, index);
				index = index +1;
				relationMetaData.put(LexConstants.REASON, reasonAdded);
				relationMetaData.put(LexConstants.CHILDREN_CLASSIFIERS, childrenClassifiers);
				Calendar lastUpdatedOn = Calendar.getInstance();
				relationMetaData.put(LexConstants.ADDED_ON, inputFormatterDateTime.format(lastUpdatedOn.getTime()));
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(kbIdentifier, childId, relationMetaData);
				updateRequests.add(updateRelationRequest);
			} 		
		}
		
		if(!updateRequests.isEmpty()) {
			try {
				
				Map<String,Object> dataMap = new HashMap<>();
				Calendar validTill = Calendar.getInstance();
				dataMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
				graphService.updateNodeV2(rootOrg, kbIdentifier, dataMap, tx);
				
				graphService.createChildRelationsV2(rootOrg, kbIdentifier, updateRequests, tx);
				if(createForImg) {
					//create children relations for imgNode as well
					graphService.createChildRelationsV2(rootOrg, kbIdentifier + LexConstants.IMG_SUFFIX, updateRequests, tx);
				}
				tx.commitAsync().toCompletableFuture().get();
				logger.info("Created Relations");
				return "Success";
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollbackAsync().toCompletableFuture().get();
				logger.info("Failed to create relations graphService failed");
				throw e;
			}
			finally {
				tx.close();
				session.close();
			}
		}
		else {
			return "Invalid Children";
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String deleteChildren(String rootOrg, String org, Map<String, Object> requestMap) throws Exception {
		String kbIdentifier = null;
		List<String> childrenIds = new ArrayList<>();
		List<DeleteRelationRequest> deleteRequests = new ArrayList<>();
		boolean deleteForImg = false;
		if(requestMap==null||requestMap.isEmpty() || rootOrg==null || rootOrg.isEmpty() || org==null || org.isEmpty()) {
			throw new BadRequestException("Invalid Input");
		}
		
		kbIdentifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		if(kbIdentifier==null || kbIdentifier.isEmpty()) {
			throw new BadRequestException("Invalid identifier");
		}
		
		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		logger.info("Add Children to Kb transaction started");
		ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, kbIdentifier, tx);
		String status = (String) node.getMetadata().get(LexConstants.STATUS);
		String contentType = (String) node.getMetadata().get(LexConstants.CONTENT_TYPE);
		
		if(!status.equals(LexConstants.Status.Live.getStatus())) {
			throw new BadRequestException("Cannot add children KB is not Live");
		}
		
		if(!contentType.equals(LexConstants.ContentType.KnowledgeBoard.getContentType())) {
			throw new BadRequestException("API only to delete for KB, Invalid ContentType : " + contentType);
		}
		
		//Checking if imgNode exists
		ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, kbIdentifier + LexConstants.IMG_SUFFIX, tx);
		System.out.println(imgNode);
		
		if(imgNode!=null) {
			//If exists, delete relations for img node of KB as well
			deleteForImg=true;
		}
		
		//all children Ids to be deleted
		childrenIds = (List<String>) requestMap.get(LexConstants.CHILDREN);
		Set<String> noDuplicateChildren = new HashSet<>(childrenIds);
		childrenIds = new ArrayList<>(noDuplicateChildren);
		
		if(childrenIds==null || childrenIds.isEmpty()) {
			throw new BadRequestException("Children is null/empty");
		}
		
		List<ContentNode> childrenNodes = graphService.getNodesByUniqueIdV2(rootOrg, childrenIds, tx);
		List<String> childrenInDbs = new ArrayList<>();
		
		//checking if children exists in db
		for(ContentNode childNode : childrenNodes) {
			childrenInDbs.add(childNode.getIdentifier());
		}
		
		for(String childId : childrenIds) {
			// only if ids to be deleted are present in db, add to final delete object
			if(childrenInDbs.contains(childId)) {
				DeleteRelationRequest deleteRelationRequest = new DeleteRelationRequest(kbIdentifier, childId);
				deleteRequests.add(deleteRelationRequest);
			}
		}
		
		if(!deleteRequests.isEmpty()) {
			try {
				
				Map<String,Object> dataMap = new HashMap<>();
				Calendar validTill = Calendar.getInstance();
				dataMap.put(LexConstants.LAST_UPDATED, inputFormatterDateTime.format(validTill.getTime()));
				graphService.updateNodeV2(rootOrg, kbIdentifier, dataMap, tx);
				
				graphService.deleteRelations(rootOrg,deleteRequests , tx);
				if(deleteForImg) {
					//deleting child relations for img KB as well
					List<DeleteRelationRequest> deleteRequestsImg = new ArrayList<>();
					for(String childId : childrenIds) {
						if(childrenInDbs.contains(childId)) {
							DeleteRelationRequest deleteRelationRequest = new DeleteRelationRequest(kbIdentifier + LexConstants.IMG_SUFFIX, childId);
							deleteRequestsImg.add(deleteRelationRequest);
						}
					}
					graphService.deleteRelations(rootOrg, deleteRequestsImg, tx);
				}
				tx.commitAsync().toCompletableFuture().get();
				logger.info("Created Relations");
				return "Success";
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollbackAsync().toCompletableFuture().get();
				logger.info("Failed to delete relations graphService failed");
				throw e;
			}
			finally {
				tx.close();
				session.close();
			}
		}
		else {
			return "Invalid Children";
		}
	}

	@Override
	public String deleteKbContent(String rootOrg, String org, String identifier) throws Exception {
		
		List<String> softDeleteStatuses = Arrays.asList(LexConstants.Status.Live.getStatus(),LexConstants.Status.MarkedForDeletion.getStatus(),LexConstants.Status.Deleted.getStatus());
		if(identifier==null || identifier.isEmpty()) {
			throw new BadRequestException("Identifier is not valid");
		}
		
		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		
		ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
		
		if(node == null) {
			throw new ResourceNotFoundException("Does not exists : " + identifier);
		}
		
		Map<String,Object> metadata = node.getMetadata();
		
		Boolean isStandAlone = (Boolean) metadata.getOrDefault(LexConstants.IS_STAND_ALONE, false);
		
		if(isStandAlone==null||isStandAlone==false) {
			throw new BadRequestException("Cannot delete content is not stand alone");
		}
		
		String imgIdentifier = identifier+LexConstants.IMG_SUFFIX;
		
		ContentNode imgNode = graphService.getNodeByUniqueIdV3(rootOrg, imgIdentifier, tx);
		
		if(imgNode!=null) {
			throw new BadRequestException("Image node exists, delete image node before deleting original node");
		}
		
		if(metadata.get(LexConstants.STATUS)==null) {
			throw new BadRequestException("Status cannot be null");
		}
		String status = (String) metadata.get(LexConstants.STATUS);
		
		if(status==LexConstants.Status.Expired.getStatus()) {
			throw new BadRequestException("Content is already expired cannot delete");
		}
		
		if(softDeleteStatuses.contains(status)) {
			try {
				Map<String,Object> contentMetaToUpdate = new HashMap<>();
				contentMetaToUpdate.put(LexConstants.STATUS, LexConstants.Status.Deleted.getStatus());
				graphService.updateNodeV2(rootOrg, identifier, contentMetaToUpdate, tx);
				tx.commitAsync().toCompletableFuture().get();
				logger.info("Soft-Deleted content with identifier : "+ identifier);
				return "Success";
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollbackAsync().toCompletableFuture().get();
				logger.info("Failed to delete identifier : " +identifier);
				throw e;
			}
			finally {
				tx.close();
				session.close();
			}
		}
		
		else {
			try {
				graphService.deleteNodes(rootOrg, Arrays.asList(identifier), tx);
				tx.commitAsync().toCompletableFuture().get();
				logger.info("Hard-Deleted content with identifier : "+ identifier);
				return "Success";
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollbackAsync().toCompletableFuture().get();
				logger.info("Failed to delete identifier : " +identifier);
				throw e;
			}
			finally {
				tx.close();
				session.close();
			}
			
		}
	}
}
