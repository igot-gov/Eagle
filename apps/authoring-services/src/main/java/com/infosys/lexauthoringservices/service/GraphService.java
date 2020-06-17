package com.infosys.lexauthoringservices.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.neo4j.driver.v1.Transaction;

import com.infosys.lexauthoringservices.model.DeleteRelationRequest;
import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;

/**
 * CRUD layer for neo4j database
 * 
 * @since 27 may 2019
 * @version 1
 */
public interface GraphService {

	/**
	 * creates a node in graph with rootOrg as the label. converts objects to json
	 * before creating.
	 * 
	 * @param rootOrg
	 * @param contentMeta
	 * @param transaction
	 * @throws Exception
	 */
	void createNodeV2(String rootOrg, Map<String, Object> contentMeta, Transaction transaction) throws Exception;

	/**
	 * creates a Feature node in graph with Feature_rootOrg as the label. converts
	 * objects to stringified json before creating.
	 * 
	 * @param rootOrg
	 * @param contentMeta
	 * @param transaction
	 * @throws Exception
	 */
	void createFeatureNode(String rootOrg, Map<String, Object> contentMeta, Transaction transaction) throws Exception;

	/**
	 * create nodes in bulk. does not create relations. convert objects to json
	 * before creating
	 * 
	 * @param rootOrg
	 * @param contentMetas
	 * @param transaction
	 * @throws Exception
	 */
	void createNodes(String rootOrg, List<Map<String, Object>> contentMetas, Transaction transaction) throws Exception;

	/**
	 * updates the node with the given meta. only the fields being updated need to
	 * be passed.
	 * 
	 * @param rootOrg
	 * @param identifier
	 * @param contentMetaToUpdate
	 * @param transaction
	 * @return
	 */
	ContentNode updateNodeV2(String rootOrg, String identifier, Map<String, Object> contentMetaToUpdate,
			Transaction transaction) throws Exception;

	/**
	 * Updates nodes in bulk. only the metadata being updated needs to be passed.
	 * convert objects to json
	 * 
	 * @param rootOrg
	 * @param updateMetaRequest
	 * @param transaction
	 * @throws Exception
	 */
	void updateNodesV2(String rootOrg, List<UpdateMetaRequest> updateMetaRequests, Transaction transaction)
			throws Exception;

	/**
	 * Updates nodes in bulk. only the metadata being updated needs to be passed.
	 * 
	 * @param rootOrg
	 * @param updateMetaRequest
	 * @param transaction
	 * @throws Exception
	 */
	void updateFeatureNodesV2(String rootOrg, List<UpdateMetaRequest> updateMetaRequests, Transaction transaction)
			throws Exception;

	/**
	 * get's a node by "identifier" field and rootOrg as the label.
	 * 
	 * @param rootOrg
	 * @param identifier
	 * @return
	 * @throws Exception
	 */
	//UNUSED
	ContentNode getNodeByUniqueIdV2(String rootOrg, String identifier, Transaction transaction) throws Exception;

	/**
	 * 
	 * 
	 * @param rootOrg
	 * @param identifier
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	ContentNode getNodeByUniqueIdV3(String rootOrg, String identifier, Transaction transaction) throws Exception;

	/**
	 * get's nodes with the given identifiers and from the label given as rootOrg.
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @return
	 */
	List<ContentNode> getNodesByUniqueIdV2(String rootOrg, List<String> identifiers, Transaction transaction)
			throws Exception;

	/**
	 * fetch nodes with relations and only few fields in the meta.check query.
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	List<ContentNode> getNodesByUniqueIdForHierarchyUpdate(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception;

	
	/**
	 * fetch nodes with relations and all fields in the meta and limited fields for the (parents&children).
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param  transaction
	 * @return
	 * @throws Exception
	 */
	//UNUSED
	List<ContentNode> getNodesByUniqueIdForHierarchyUpdateV2(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception;
	
	/**
	 * fetch nodes with relations and specified fields in the meta and limited fields for the (parents&children).
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param  transaction
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	List<ContentNode> getNodesByUniqueIdForHierarchyUpdateV3(String rootOrg, List<String> identifiers,List<String> fields,
			Transaction transaction) throws Exception;
	
	
	
	/**
	 * get's nodes with the given identifiers and from the label given as rootOrg
	 * without the relations
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param transaction
	 * @return
	 * @throws Exception
	 */
	//UNUSED
	List<ContentNode> getNodesByUniqueIdV2WithoutRelations(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception;

	/**
	 * creates a one way relation between startNode and endNode with the given index
	 * as relation metadata.
	 * 
	 * startNode and endNode must exist for given label (rootOrg).
	 * 
	 * @param rootOrg
	 * @param startNodeId
	 * @param endNodeId
	 * @param index
	 */
	//UNUSED
	void createRelation(String rootOrg, String startNodeId, String endNodeId, Integer index, Transaction transaction)
			throws Exception;

	/**
	 * bulk creation for relation between given startNode and endNode with specified
	 * index.
	 * 
	 * @param rootOrg
	 * @param updateRelationRequests
	 * @param transaction
	 */
	//UNUSED
	void createRelations(String rootOrg, List<UpdateRelationRequest> updateRelationRequests, Transaction transaction);

	/**
	 * bulk creation for relation between given startNode and endNode with specified
	 * index. if already exists then it is not created
	 * 
	 * @param rootOrg
	 * @param updateRelationRequests
	 * @param transaction
	 */
	void mergeRelations(String rootOrg, List<UpdateRelationRequest> updateRelationRequests, Transaction transaction);

	/**
	 * delete all the children(under any label) for the given identifiers
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param transaction
	 */
	void deleteChildren(String rootOrg, List<String> identifiers, Transaction transaction);

	/**
	 * deletes one way relation between startNode and endNode's.
	 * 
	 * startNode and endNodes must exist for the given label (rootOrg).
	 * 
	 * @param rootOrg
	 * @param startNodeId
	 * @param endNodeIds
	 */
	//UNUSED
	void deleteRelation(String rootOrg, String startNodeId, List<String> endNodeIds, Transaction transaction);

	/**
	 * delete one way relation of Has_Sub_Content from between startNode and endNode
	 * in bulk.
	 * 
	 * @param rootOrg
	 * @param deleteRelationRequests
	 * @param transaction
	 */
	//UNUSED
	void deleteRelations(String rootOrg, List<DeleteRelationRequest> deleteRelationRequests, Transaction transaction);

	/**
	 * detach deletes the nodes with given identifiers.
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param transaction
	 */
	//UNUSED
	void deleteNodes(String rootOrg, List<String> identifiers, Transaction transaction);

	/**
	 * 
	 * @param rootOrg
	 * @param startNodeId
	 * @param updateRequests
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	void createChildRelations(String rootOrg, String startNodeId, List<Map<String, Object>> updateRequests,
			Transaction transaction);
	
	/**
	 * 
	 * @param rootOrg
	 * @param startNodeId
	 * @param updateRequests
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	void createChildRelationsV2(String rootOrg, String startNodeId, List<UpdateRelationRequest> updateRequests,
			Transaction transaction);

	/**
	 * 
	 * @param rootOrg
	 * @param endNodeId
	 * @param updateRequests
	 * @param transaction
	 */
	//UNUSED
	void createParentRelations(String rootOrg, String endNodeId, List<Map<String, Object>> updateRequests,
			Transaction transaction);

	/**
	 * detach deletes the Feature nodes with given identifiers.
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param transaction
	 */
	void deleteFeatureNodes(String rootOrg, Set<String> identifiers, Transaction transaction);

	/**
	 * returns metadata of specified fields for multiple ids
	 * 
	 * @param rootOrg
	 * @param identifiers
	 * @param fields
	 * @param transaction
	 */
	//UNUSED
	List<Map<String, Object>> getNodesByIdentifier(String rootOrg, List<String> identifiers, List<String> fields,
			Transaction transaction);



}
