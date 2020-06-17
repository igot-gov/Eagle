package com.infosys.lexauthoringservices.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.DeleteRelationRequest;
import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.util.GraphUtil;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class GraphServiceImpl implements GraphService {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void createNodeV2(String rootOrg, Map<String, Object> contentMeta, Transaction transaction)
			throws Exception {

		GraphUtil.mapParser(contentMeta, false);

		Map<String, Object> params = new HashMap<>();
		params.put("data", contentMeta);

		Statement statement = new Statement("create (node:" + rootOrg + " $data) return node", params);

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			throw new Exception("Something went wrong");
		}
	}

	// to be removed after migration
	public void createNodeV2(String rootOrg, Map<String, Object> contentMeta, Session session) throws Exception {

		contentMeta = GraphUtil.mapParser(contentMeta, false);

		Map<String, Object> params = new HashMap<>();
		params.put("data", contentMeta);

		Statement statement = new Statement("create (node:" + rootOrg + " $data) return node", params);

		StatementResult result = session.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			throw new Exception("Something went wrong");
		}
	}

	@Override
	public void createFeatureNode(String rootOrg, Map<String, Object> contentMeta, Transaction transaction)
			throws Exception {

		GraphUtil.mapParser(contentMeta, false);
		System.out.println(contentMeta);
		Map<String, Object> params = new HashMap<>();
		params.put("data", contentMeta);

		Statement statement = new Statement("create (node:Feature_" + rootOrg + " $data) return node", params);
		System.out.println(statement);
		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			throw new Exception("Something went wrong");
		}
	}

	@Override
	public void createNodes(String rootOrg, List<Map<String, Object>> contentMetas, Transaction transaction)
			throws Exception {

		if (contentMetas == null || contentMetas.isEmpty()) {
			return;
		}

		GraphUtil.mapParser(contentMetas, false);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("createRequests", contentMetas);

		Statement statement = new Statement(
				"unwind $createRequests as data create (node:" + rootOrg + ") with node,data set node=data return node",
				paramMap);

		StatementResult statementResult = transaction.run(statement);

		List<Record> records = statementResult.list();
		if (records == null || records.size() == 0) {
			throw new Exception("Bulk create operation failed");
		}
	}

	// to be removed after migration
	public void createNodes(String rootOrg, List<Map<String, Object>> contentMetas, Session session) throws Exception {

		if (contentMetas == null || contentMetas.isEmpty()) {
			return;
		}

		GraphUtil.mapParser(contentMetas, false);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("createRequests", contentMetas);

		Statement statement = new Statement(
				"unwind $createRequests as data create (node:" + rootOrg + ") with node,data set node=data return node",
				paramMap);

		StatementResult statementResult = session.run(statement);

		List<Record> records = statementResult.list();
		if (records == null || records.size() == 0) {
			throw new Exception("Bulk create operation failed");
		}
	}

	@Override
	public ContentNode updateNodeV2(String rootOrg, String identifier, Map<String, Object> contentMetaToUpdate,
			Transaction transaction) throws Exception {

		contentMetaToUpdate.remove(LexConstants.LABELS);
		GraphUtil.mapParser(contentMetaToUpdate, false);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("data", contentMetaToUpdate);

		Statement statement = new Statement("match (node) where  node.identifier='" + identifier + "' and (node:"
				+ rootOrg + " or node:Shared) set node+= $data return node", paramMap);

		StatementResult statementResult = transaction.run(statement);
		List<Record> records = statementResult.list();

		if (records == null || records.size() == 0) {
			throw new Exception("Update operation failed");
		}

		return GraphUtil.createContentNode(records);
	}

	@Override
	public void updateNodesV2(String rootOrg, List<UpdateMetaRequest> updateMetaRequests, Transaction transaction)
			throws Exception {

		if (updateMetaRequests == null || updateMetaRequests.isEmpty()) {
			return;
		}

		List<Map<String, Object>> updateRequests = new ArrayList<>();
		for (UpdateMetaRequest updateMetaRequest : updateMetaRequests) {
			Map<String,Object> removeLabels = updateMetaRequest.getMetaData();
			removeLabels.remove(LexConstants.LABELS);
			updateMetaRequest.setMetaData(GraphUtil.mapParser(removeLabels, false));
			updateRequests.add(mapper.convertValue(updateMetaRequest, new TypeReference<Map<String, Object>>() {
			}));
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("updateMeta", updateRequests);

		Statement statement = new Statement(
				"unwind $updateMeta as data optional match (n:" + rootOrg + "{identifier:data.identifier}) with n,data "
						+ "optional match (n1:Shared{identifier:data.identifier}) with " + "case "
						+ "when n is not NULL " + "then n when n1 is not NULL "
						+ "then n1  end as startNode,data set startNode+=data.metaData return startNode ",
				paramMap);

		StatementResult statementResult = transaction.run(statement);
		List<Record> records = statementResult.list();

		if (records == null || records.size() == 0) {
			throw new Exception("Update operation failed");
		}
	}

	@Override
	public ContentNode getNodeByUniqueIdV2(String rootOrg, String identifier, Transaction transaction)
			throws Exception {

		Statement statement = new Statement("match (node) where node.identifier='" + identifier + "' and (node:"
				+ rootOrg + " or node:Shared) with node optional match (node)-[childRelation:Has_Sub_Content]->(child) "
				+ "where child:" + rootOrg + " or child:Shared with node,child,childRelation "
				+ "optional match (parent)-[parentRelation:Has_Sub_Content]->(node)  where parent:" + rootOrg
				+ " or parent:Shared return node,childRelation,child,parentRelation,parent");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			throw new Exception("identifier not found");
		}

		ContentNode contentNode = GraphUtil.createContentNode(records);
		return contentNode;
	}


	@Override
	public ContentNode getNodeByUniqueIdV3(String rootOrg, String identifier, Transaction transaction)
			throws Exception {

		Statement statement = new Statement("optional match (tenantNode:" + rootOrg + "{identifier:'" + identifier
				+ "'}) with tenantNode optional match (sharedNode:Shared{identifier:'" + identifier + "'})"
				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
				+ "	return node,childRelation,child,parentRelation,parent");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return null;
		}

		ContentNode contentNode = GraphUtil.createContentNode(records);
		return contentNode;
	}

	@Override
	public List<ContentNode> getNodesByUniqueIdV2(String rootOrg, List<String> identifiers, Transaction transaction)
			throws Exception {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement("unwind " + identifiers + " as data optional match(tenantNode:" + rootOrg
				+ "{identifier:data}) with tenantNode,data" + " optional match (sharedNode:Shared{identifier:data})"
				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
				+ " return node,childRelation,child,parentRelation,parent");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return new ArrayList<>();
		}
		return GraphUtil.createContentNodes(records);
	}

	@Override
	public List<ContentNode> getNodesByUniqueIdForHierarchyUpdate(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement("unwind " + identifiers + " as data optional match(tenantNode:" + rootOrg
				+ "{identifier:data}) with tenantNode,data" + " optional match (sharedNode:Shared{identifier:data})"
				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
				+ " return node.identifier,node.status,node.contentType,node.isStandAlone,node.authoringDisabled,node.isMetaEditingDisabled,labels(node),id(node),"
				+ "childRelation,child.identifier,child.status,child.contentType,child.isStandAlone,labels(child),id(child),child.authoringDisabled,child.isMetaEditingDisabled,"
				+ "parentRelation,parent.identifier,parent.status,parent.contentType,parent.isStandAlone,labels(parent),id(parent),parent.authoringDisabled,parent.isMetaEditingDisabled");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return new ArrayList<>();
		}

		return GraphUtil.createContentNodesForHierarchyUpdate(records);
	}
	
	@Override
	public List<ContentNode> getNodesByUniqueIdForHierarchyUpdateV2(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement("unwind " + identifiers + " as data optional match(tenantNode:" + rootOrg
				+ "{identifier:data}) with tenantNode,data" + " optional match (sharedNode:Shared{identifier:data})"
				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
				+ " return node,labels(node),node.identifier,"
				+ "childRelation,child.identifier,child.status,child.contentType,child.isStandAlone,labels(child),id(child),"
				+ "parentRelation,parent.identifier,parent.status,parent.contentType,parent.isStandAlone,labels(parent),id(parent)");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return new ArrayList<>();
		}

		return GraphUtil.createContentNodesForHierarchyUpdateV2(records);
	}
	
	@Override
	public List<ContentNode> getNodesByUniqueIdForHierarchyUpdateV3(String rootOrg, List<String> identifiers,List<String> fields,
			Transaction transaction) throws Exception {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());
		StringBuilder query = new StringBuilder("unwind " + identifiers + " as data optional match(tenantNode:" + rootOrg
				+ "{identifier:data}) with tenantNode,data" + " optional match (sharedNode:Shared{identifier:data})"
				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
				+ " return labels(node),id(node),node.identifier,"
				+ "childRelation,child.identifier,child.status,child.contentType,child.isStandAlone,labels(child),id(child),"
				+ "parentRelation,parent.identifier,parent.status,parent.contentType,parent.isStandAlone,labels(parent),id(parent), ");
		

//		Statement statement = new Statement("unwind " + identifiers + " as data optional match(tenantNode:" + rootOrg
//				+ "{identifier:data}) with tenantNode,data" + " optional match (sharedNode:Shared{identifier:data})"
//				+ " with case when tenantNode is not null then tenantNode when sharedNode is not null then sharedNode"
//				+ " end as node optional match (node)-[childRelation:Has_Sub_Content]->(child)"
//				+ " with node,child,childRelation optional match (parent)-[parentRelation:Has_Sub_Content]->(node)"
//				+ " return node.identifier,"
//				+ "childRelation,child.identifier,child.status,child.contentType,child.isStandAlone,labels(child),id(child),"
//				+ "parentRelation,parent.identifier,parent.status,parent.contentType,parent.isStandAlone,labels(parent),id(parent) ");
		
		if (null == fields) {
            query.append("node;");
        } else {
            Iterator<String> iterator = fields.iterator();
            while (iterator.hasNext()) {
                String field = iterator.next();
                if (iterator.hasNext())
                    query.append("node.").append(field).append(",");
                else
                    query.append("node.").append(field).append(";");
            }
        }

		StatementResult result = transaction.run(query.toString());
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return new ArrayList<>();
		}

		return GraphUtil.createContentNodesForHierarchyUpdateV3(records,fields);
	}
		

	@Override
	public void deleteChildren(String rootOrg, List<String> identifiers, Transaction transaction) {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement("unwind " + identifiers
				+ " as data match (node) where node.identifier=data and (node:" + rootOrg
				+ " or node:Shared) with node match (node)-[childRelation:Has_Sub_Content]->(child) delete childRelation");
		transaction.run(statement);

	}

	@Override
	public List<ContentNode> getNodesByUniqueIdV2WithoutRelations(String rootOrg, List<String> identifiers,
			Transaction transaction) throws Exception {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement("match (node) where node.identifier in " + identifiers + " and (node:"
				+ rootOrg + " or node:Shared) return node");

		StatementResult result = transaction.run(statement);
		List<Record> records = result.list();

		if (records == null || records.isEmpty()) {
			return new ArrayList<>();
		}
		return GraphUtil.createContentNodes(records);
	}

	@Override
	public void updateFeatureNodesV2(String rootOrg, List<UpdateMetaRequest> updateMetaRequests,
			Transaction transaction) throws Exception {

		if (updateMetaRequests == null || updateMetaRequests.isEmpty()) {
			return;
		}

		List<Map<String, Object>> updateRequests = new ArrayList<>();
		for (UpdateMetaRequest updateMetaRequest : updateMetaRequests) {
			updateMetaRequest.setMetaData(GraphUtil.mapParser(updateMetaRequest.getMetaData(), false));
			updateRequests.add(mapper.convertValue(updateMetaRequest, new TypeReference<Map<String, Object>>() {
			}));
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("updateMeta", updateRequests);
		Statement statement = new Statement(
				"unwind $updateMeta as data match (node) where node.identifier=data.identifier and (node:Feature_"
						+ rootOrg + ") set node+=data.metaData return node",
				paramMap);
		StatementResult statementResult = transaction.run(statement);
		List<Record> records = statementResult.list();

		if (records == null || records.size() == 0) {
			throw new Exception("Update operation failed");
		}
	}

	@Override
	public void createRelation(String rootOrg, String startNodeId, String endNodeId, Integer index,
			Transaction transaction) throws Exception {

		Statement statement = new Statement("match (startNode),(endNode) where startNode.identifier='" + startNodeId
				+ "' and endNode.identifier='" + endNodeId + "' and (startNode:" + rootOrg
				+ " or startNode:Shared) and (endNode:" + rootOrg + " or endNode:Shared)");

		StatementResult statementResult = transaction.run(statement);

		List<Record> records = statementResult.list();
		if (records == null || records.size() == 0) {
			throw new Exception("Create relation operation failed");
		}
	}

	@Override
	public void createRelations(String rootOrg, List<UpdateRelationRequest> updateRelationRequests,
			Transaction transaction) {

		if (updateRelationRequests == null || updateRelationRequests.isEmpty()) {
			return;
		}

		List<Map<String, Object>> updateRequests = new ArrayList<>();
		for (UpdateRelationRequest updateRelationRequest : updateRelationRequests) {
			updateRequests.add(mapper.convertValue(updateRelationRequest, new TypeReference<Map<String, Object>>() {
			}));
		}

		Map<String, Object> params = new HashMap<>();
		params.put("updateRelation", updateRequests);

		Statement statement = new Statement("unwind $updateRelation as data " + "optional match (n:" + rootOrg
				+ "{identifier:data.startNodeId}) with n,data "
				+ "optional match (n1:Shared{identifier:data.startNodeId}) with " + "case " + "when n is not NULL "
				+ "then n " + "when n1 is not NULL " + "then n1 " + "end as startNode,data " + "optional match (n:"
				+ rootOrg + "{identifier:data.endNodeId}) with startNode,n,data "
				+ "optional match (n1:Shared{identifier:data.endNodeId}) with " + "case " + "when n is not NULL "
				+ "then n " + "when n1 is not NULL " + "then n1 " + "end as endNode,startNode,data "
				+ "create (startNode)-[r:Has_Sub_Content{index:data.index}]->(endNode) "
				+ "return startNode.identifier,endNode.identifier,data.index", params);

		transaction.run(statement);
	}
	
	@Override
	public void createChildRelationsV2(String rootOrg, String startNodeId, List<UpdateRelationRequest> updateRelationRequests,
			Transaction transaction) {
		
		if (updateRelationRequests == null || updateRelationRequests.isEmpty()) {
			return;
		}
		
		List<Map<String, Object>> updateRequests = new ArrayList<>();
		for (UpdateRelationRequest updateRelationRequest : updateRelationRequests) {
			updateRequests.add(mapper.convertValue(updateRelationRequest, new TypeReference<Map<String, Object>>() {
			}));
		}

		Map<String, Object> params = new HashMap<>();
		params.put("updateRelation", updateRequests);
		//reason:data.relationMetaData.reason,childrenClassifiers:data.relationMetaData.childrenClassifiers

		Statement statement = new Statement(
				"unwind $updateRelation as data match(startNode:" + rootOrg + "{identifier:'" + startNodeId
						+ "'}) , (endNode{identifier:data.endNodeId}) where endNode:Shared or endNode:" + rootOrg
						+ " create (startNode)-[r:Has_Sub_Content{index:data.relationMetaData.index,reason:data.relationMetaData.reason,childrenClassifiers:data.relationMetaData.childrenClassifiers,addedOn:data.relationMetaData.addedOn}]->(endNode) return r",
				params);
		transaction.run(statement);

	}

	@Override
	public void mergeRelations(String rootOrg, List<UpdateRelationRequest> updateRelationRequests,
			Transaction transaction) {

		if (updateRelationRequests == null || updateRelationRequests.isEmpty()) {
			return;
		}

		List<Map<String, Object>> updateRequests = new ArrayList<>();
		for (UpdateRelationRequest updateRelationRequest : updateRelationRequests) {
			updateRequests.add(mapper.convertValue(updateRelationRequest, new TypeReference<Map<String, Object>>() {
			}));
		}

		Map<String, Object> params = new HashMap<>();
		params.put("updateRelation", updateRequests);

		Statement statement = new Statement("unwind $updateRelation as data " + "optional match (n:" + rootOrg
				+ "{identifier:data.startNodeId}) with n,data "
				+ "optional match (n1:Shared{identifier:data.startNodeId}) with " + "case " + "when n is not NULL "
				+ "then n " + "when n1 is not NULL " + "then n1 " + "end as startNode,data " + "optional match (n:"
				+ rootOrg + "{identifier:data.endNodeId}) with startNode,n,data "
				+ "optional match (n1:Shared{identifier:data.endNodeId}) with " + "case " + "when n is not NULL "
				+ "then n " + "when n1 is not NULL " + "then n1 " + "end as endNode,startNode,data "
				+ "merge (startNode)-[r:Has_Sub_Content{index:data.relationMetaData.index,reason:data.relationMetaData.reason,childrenClassifiers:data.relationMetaData.childrenClassifiers,addedOn:data.relationMetaData.addedOn}]->(endNode) "
				+ "return startNode.identifier,endNode.identifier,data.index", params);

		transaction.run(statement);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteRelations(String rootOrg, List<DeleteRelationRequest> deleteRelationRequests,
			Transaction transaction) {

		List<Map<String, Object>> deleteRequests = new ArrayList<>();
		for (DeleteRelationRequest deleteRelationRequest : deleteRelationRequests) {
			deleteRequests.add(mapper.convertValue(deleteRelationRequest, Map.class));
		}

		Map<String, Object> params = new HashMap<>();
		params.put("deleteRelation", deleteRequests);

		Statement statement = new Statement(
				"unwind $deleteRelation as data match(startNode)-[r:Has_Sub_Content]->(endNode) " + "where"
						+ "(startNode:" + rootOrg + " or startNode:Shared) and (endNode:" + rootOrg
						+ " or endNode:Shared) and "
						+ "startNode.identifier=data.startNodeId and endNode.identifier=data.endNodeId delete r",
				params);
		transaction.run(statement);

	}

	@Override
	public void createChildRelations(String rootOrg, String startNodeId, List<Map<String, Object>> updateRequests,
			Transaction transaction) {

		Map<String, Object> params = new HashMap<>();
		params.put("updateRelation", updateRequests);

		Statement statement = new Statement(
				"unwind $updateRelation as data match(startNode:" + rootOrg + "{identifier:'" + startNodeId
						+ "'}) , (endNode{identifier:data.endNodeId}) where endNode:Shared or endNode:" + rootOrg
						+ " create (startNode)-[r:Has_Sub_Content{index:data.index}]->(endNode) return r",
				params);
		transaction.run(statement);

	}
	
	

	@Override
	public void createParentRelations(String rootOrg, String endNodeId, List<Map<String, Object>> updateRequests,
			Transaction transaction) {

		Map<String, Object> params = new HashMap<>();
		params.put("updateRelation", updateRequests);

		Statement statement = new Statement("unwind $updateRelation as data match(endNode:" + rootOrg + "{identifier:'"
				+ endNodeId + "'}) , (startNode:" + rootOrg
				+ "{identifier:data.startNodeId}) create (startNode)-[r:Has_Sub_Content{index:data.index}]->(endNode) return r",
				params);

		transaction.run(statement);

	}

	@Override
	public void deleteRelation(String rootOrg, String startNodeId, List<String> endNodeIds, Transaction transaction) {

		endNodeIds = endNodeIds.stream().map(endNodeId -> "'" + endNodeId + "'").collect(Collectors.toList());

		Statement statement = new Statement(
				"unwind " + endNodeIds + " as data match (startNode:" + rootOrg + "{identifier:'" + startNodeId
						+ "'})-[r:Has_Sub_Content]->(endNode:" + rootOrg + "{identifier:data}) delete r");

		transaction.run(statement);
	}

	@Override
	public void deleteNodes(String rootOrg, List<String> identifiers, Transaction transaction) {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toList());

		Statement statement = new Statement(
				"match (node:" + rootOrg + ") where node.identifier in " + identifiers + " detach delete node");

		transaction.run(statement);
	}

	@Override
	public void deleteFeatureNodes(String rootOrg, Set<String> identifiers, Transaction transaction) {

		identifiers = identifiers.stream().map(identifier -> "'" + identifier + "'").collect(Collectors.toSet());

		Statement statement = new Statement("unwind " + identifiers + " as data match (node:Feature_" + rootOrg
				+ "{identifier:data}) detach delete node");

		transaction.run(statement);
	}
	
	@Override
	public List<Map<String,Object>> getNodesByIdentifier(String rootOrg, List<String> identifiers, List<String> fields, Transaction transaction){
	    Map<String,Object> params = new HashMap<>();
	    params.put("identifiers", identifiers);
	    StringBuilder query = new StringBuilder("match (node) where node.identifier in $identifiers and (node:" + rootOrg + " or node:Shared) return ");
	    if (null == fields){
	        query.append("node;");
	    }else {
	    	query.append("{");
	        Iterator<String> iterator = fields.iterator();
	        while (iterator.hasNext()) {
	            String field = iterator.next();
	            if (iterator.hasNext())
	                query.append(field).append(": node.").append(field).append(",");
	            else
	                query.append(field).append(": node.").append(field).append("} as node;");
	        }
	    }
	    StatementResult result = transaction.run(query.toString(),params);
	    List<Map<String,Object>> records = new ArrayList<>();
	    if (null == fields) {
			result.forEachRemaining(record -> records.add(record.get("node").asNode().asMap()));
		} else
			result.forEachRemaining(record -> records.add(record.get("node").asMap()));
	    return records;
	}
}
