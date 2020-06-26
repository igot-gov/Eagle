/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.wingspan.authoringkafkaconsumers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Neo4jQueryHelpers {

    private ObjectMapper mapper = new ObjectMapper();
    private final List<String> fieldsToParse = Arrays.asList(ProjectConstants.COMMENTS,
            ProjectConstants.CERTIFICATION_LIST, ProjectConstants.PLAYGROUND_RESOURCES, ProjectConstants.SOFTWARE_REQUIREMENTS,
            ProjectConstants.SYSTEM_REQUIREMENTS, ProjectConstants.REFERENCES, ProjectConstants.CREATOR_CONTACTS,
            ProjectConstants.CREATOR_DEATILS, ProjectConstants.PUBLISHER_DETAILS, ProjectConstants.PRE_CONTENTS,
            ProjectConstants.POST_CONTENTS, ProjectConstants.CATALOG, ProjectConstants.CLIENTS, ProjectConstants.SKILLS,
            ProjectConstants.K_ARTIFACTS, ProjectConstants.TRACK_CONTACT_DETAILS, ProjectConstants.ORG,
            ProjectConstants.SUBMITTER_DETAILS, ProjectConstants.CONCEPTS,ProjectConstants.PLAG_SCAN, ProjectConstants.TAGS,
            "eligibility", "scoreType", "externalData", "verifiers", "verifier", "subTitles", "roles", "group",
            "msArtifactDetails", "studyMaterials", "equivalentCertifications", ProjectConstants.TRANSCODING,
            ProjectConstants.PRICE, ProjectConstants.EDITORS);

    public Map<String, Object> getNodeByIdentifier(String rootOrg, String identifier, Set<String> fields, Transaction transaction) {
        StringBuilder query = new StringBuilder("match (node) where node.identifier='" + identifier + "' and (node:" + rootOrg + " or node:Shared) return {");
        Iterator<String> iterator = fields.iterator();
        while (iterator.hasNext()) {
            String field = iterator.next();
            if (iterator.hasNext())
                query.append(field).append(": node.").append(field).append(",");
            else
                query.append(field).append(": node.").append(field).append("} as node;");
        }
              System.out.println("getNodeByIdentifier- query: "+query.toString());


        StatementResult result = transaction.run(query.toString());
        Map<String, Object> data = (Map<String, Object>) result.single().asMap().get("node");
        Map<String, Object> editableMap = new HashMap<>(data);
        mapParser(editableMap,true);
        return editableMap;
    }

    private void mapParser(Map<String, Object> contentMeta, boolean toMap) {

        for (String fieldToParse : fieldsToParse) {
            if (contentMeta.containsKey(fieldToParse) && contentMeta.get(fieldToParse) != null && !contentMeta.get(fieldToParse).toString().isEmpty()) {
                if (toMap) {
                    String fieldValue = contentMeta.get(fieldToParse).toString();
                    Object fieldDeserialised = null;
                    try {
                        fieldDeserialised = mapper.readValue(fieldValue, Object.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    contentMeta.put(fieldToParse, fieldDeserialised);
                } else {
                    Object fieldToSerialize = contentMeta.get(fieldToParse);
                    try {
                        contentMeta.put(fieldToParse, mapper.writeValueAsString(fieldToSerialize));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List<Map<String, Object>> getNodesByIdentifier(String rootOrg, ArrayList<String> identifiers, Set<String> fields, Transaction transaction) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifiers", identifiers);
        StringBuilder query = new StringBuilder("match (node) where node.identifier in $identifiers and (node:" + rootOrg + " or node:Shared) return ");
        if (null == fields) {
            query.append("node;");
        } else {
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
        StatementResult result = transaction.run(query.toString(), params);
        List<Map<String, Object>> records = new ArrayList<>();
        if (null == fields) {
            result.forEachRemaining(record -> {
                Map<String, Object> data = record.get("node").asNode().asMap();
                Map<String, Object> editableMap = new HashMap<>(data);
                mapParser(editableMap, true);
                records.add(editableMap);
            });
        } else
            result.forEachRemaining(record -> {
                Map<String, Object> data = record.get("node").asMap();
                Map<String, Object> editableMap = new HashMap<>(data);
                mapParser(editableMap, true);
                records.add(editableMap);
            });
        return records;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getNodesWithChildren(String rootOrg, ArrayList<String> identifiers, Set<String> fields, Transaction transaction) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifiers", identifiers);
        StringBuilder query = new StringBuilder("match (node) where node.identifier in $identifiers and (node:" + rootOrg + " or node:Shared)");
        query.append(" optional match (node)-[childRelation:Has_Sub_Content]->(child) where (child:").append(rootOrg).append(" or child:Shared)");
        if (null == fields) {
            query.append(" return node,childRelation,child.identifier;");
        } else {
            query.append(" return {");
            Iterator<String> iterator = fields.iterator();
            while (iterator.hasNext()) {
                String field = iterator.next();
                if (iterator.hasNext())
                    query.append(field).append(": node.").append(field).append(",");
                else
                    query.append(field).append(": node.").append(field).append("} as node, childRelation, child.identifier;");
            }
        }

        StatementResult result = transaction.run(query.toString(), params);
        List<Record> results = result.list();

        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> nodes = new HashMap<>();

        for (Record record : results) {
            Map<String, Object> child = Maps.newHashMap(record.get("childRelation").asMap(new HashMap<>()));
            child.put("endNodeId", record.get("child.identifier").asString(null));
            String nodeId = record.get("node").asMap().get("identifier").toString();

            if (nodes.containsKey(nodeId)) {
                Map<String, Object> node = (Map<String, Object>) nodes.get(nodeId);
                List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
                children.add(child);
                node.put("children", children);
                nodes.put(nodeId, node);
            } else {
                List<Map<String, Object>> children = new ArrayList<>();
                children.add(child);
                Map<String, Object> node = Maps.newHashMap(record.get("node").asMap());
                node.entrySet().removeIf(stringObjectEntry1 -> stringObjectEntry1.getValue() == null);
                node.put("children", children);
                nodes.put(nodeId, node);
            }
        }

        nodes.values().forEach(item -> {
            Map<String, Object> editableMap = new HashMap<>((Map<String, Object>)item);
            mapParser( editableMap, true);
            records.add(editableMap);
        });

        return records;
    }

    public void deleteChildRelations(String rootOrg, Set<String> identifiers, Transaction transaction) {
        Map<String, Object> params = new HashMap<>();
        params.put("identifiers", identifiers);

        String query = "unwind $identifiers as identifier match (node) where node.identifier=identifier and (node:" + rootOrg + " or node:Shared) with node match (node)-[childRelation:Has_Sub_Content]->(child) delete childRelation;";

        transaction.run(query, params);
    }

    public void updateNodes(String rootOrg, List<Map<String, Object>> updateReq, Transaction transaction) {

        updateReq.forEach(item-> {
            mapParser(item, false);
        });

        Map<String, Object> params = new HashMap<>();
        params.put("updateMetas", updateReq);

        String query = "unwind $updateMetas as data"
                + " optional match (n:" + rootOrg + "{identifier:data.identifier}) with n,data"
                + " optional match (n1:Shared{identifier:data.identifier}) with"
                + " case"
                + " when n is not NULL then n"
                + " when n1 is not NULL then n1"
                + " end as startNode,data"
                + " set startNode += data;";

        transaction.run(query, params);
    }

    public void mergeRelations(String rootOrg, List<Map<String, Object>> updateReq, Transaction transaction){

        Map<String, Object> params = new HashMap<>();
        params.put("updateRelation", updateReq);

        String query = "unwind $updateRelation as data "
                + "optional match (n:" + rootOrg
                + " {identifier:data.startNodeId}) with n,data"
                + " optional match (n1:Shared{identifier:data.startNodeId}) with"
                + " case when n is not NULL then n"
                + " when n1 is not NULL then n1"
                + " end as startNode,data"
                + " optional match (n:" + rootOrg
                + " {identifier:data.endNodeId}) with startNode,n,data"
                + " optional match (n1:Shared {identifier:data.endNodeId}) with"
                + " case when n is not NULL then n"
                + " when n1 is not NULL then n1"
                + " end as endNode,startNode,data"
                + " merge (startNode)-[r:Has_Sub_Content]->(endNode) set r=data.metadata;";

        transaction.run(query, params);
    }

    public void deleteNodes(String rootOrg, Set<String> deleteIds, Transaction transaction) {

        Map<String, Object> params = new HashMap<>();
        params.put("identifiers", deleteIds);

        String query ="unwind $identifiers as data"
                + " optional match (n:" + rootOrg + " {identifier:data}) with n,data"
                + " optional match (n1:Shared{identifier:data}) with"
                + " case"
                + " when n is not NULL then n"
                + " when n1 is not NULL then n1"
                + " end as node"
                + " detach delete node;";

        transaction.run(query, params);
    }

    public List<Map<String, Object>> getReverseHierarchyFromNeo4jForDurationUpdate(String rootOrg, String identifier, Transaction tx) {

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
}
