package com.infosys.lexauthoringservices.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.model.neo4j.Relation;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.service.SupportService;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class SupportServiceImpl implements SupportService {
	
	@Autowired
	GraphService graphService;
	
	@Autowired
	Driver neo4jDriver;

	@SuppressWarnings("unchecked")
	@Override
	public String recreateNode(String rootOrg, String org, Map<String, Object> requestMap) throws Exception {
		
		String identifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		
		if(rootOrg==null||org==null||identifier==null||requestMap==null||rootOrg.isEmpty()||org.isEmpty()||identifier.isEmpty()||requestMap.isEmpty()) {
			throw new BadRequestException("Invalid Input Meta");
		}
		
		Map<String,Object> metaData = (Map<String, Object>) requestMap.getOrDefault("metaData",new HashMap<>());
		
		//cannot edit identifier and status
		metaData.remove(LexConstants.IDENTIFIER);
		metaData.remove(LexConstants.STATUS);
		
		List<String> keysToRemove = (List<String>) requestMap.get("keysToRemove");

		Session session = neo4jDriver.session();
		Transaction tx = session.beginTransaction();
		try {

			ContentNode node = graphService.getNodeByUniqueIdV3(rootOrg, identifier, tx);
			Map<String,Object> currentMetaData = node.getMetadata();
			
			if(node==null||node.getMetadata().isEmpty()) {
				throw new BadRequestException("No such node with identifier : " + identifier + " exists");
			}
			
			List<Relation> parents = node.getParents();
			List<Relation> children = node.getChildren();
			List<UpdateRelationRequest> updateRelationRequests = new ArrayList<>();
			
			//copy relations
			for (Relation parentRelation : parents) {
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(parentRelation.getStartNodeId(),
						identifier, parentRelation.getMetadata());
	
				updateRelationRequests.add(updateRelationRequest);
			}
			
			for (Relation childRelation : children) {
				UpdateRelationRequest updateRelationRequest = new UpdateRelationRequest(identifier,
						childRelation.getEndNodeId(), childRelation.getMetadata());
	
				updateRelationRequests.add(updateRelationRequest);
			}
	
			
			//removing keys that are to be removed
			for(String key:keysToRemove) {
				currentMetaData.remove(key);
			}
			
			//updating values that are to be updated
			for(String meta: metaData.keySet()) {
				currentMetaData.put(meta, metaData.get(meta));
			}
	
			//delete old node
			List<String> ids = new ArrayList<>();
			ids.add(identifier);
			graphService.deleteNodes(rootOrg, ids, tx);
			
			//recreate new node with new data
			graphService.createNodeV2(rootOrg, currentMetaData, tx);
			
			//connect existing relations
			graphService.mergeRelations(rootOrg, updateRelationRequests, tx);
			tx.commitAsync().toCompletableFuture().get();
		}
		catch (Exception e) {
			tx.rollbackAsync().toCompletableFuture().get();
			e.printStackTrace();
			throw e;
		}
		finally{
			tx.close();
			session.close();
		}
		return identifier;
	}
	
	
	
	

}
