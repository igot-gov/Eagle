/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */
package com.infosys.lexauthoringservices.util;

import com.google.common.base.Strings;
import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.service.GraphService;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import samza.neo4jtransactions.model.JobMetrics;

@Service
public class InterMLSearchIndexer {

    @Autowired
    private Driver driver;

    @Autowired
    private GraphService graphService;

    private Logger logger = LoggerFactory.getLogger(InterMLSearchIndexer.class);


    @SuppressWarnings({"rawtypes", "unchecked"})
    private void processIndexDocument(Map<String, Object> message, boolean updateRequest, Session session) throws Exception {
        Map<String, Object> transactionData = (Map<String, Object>) message.get("transactionData");
        System.out.println("#TRANSACTION DATA");
        System.out.println("    " + transactionData);
        if (transactionData != null) {
            List<Map<String, Object>> addedRelations = (List<Map<String, Object>>) transactionData.get("addedRelations");
            if (null != addedRelations && !addedRelations.isEmpty()) {
                for (Map<String, Object> rel : addedRelations) {
                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
                    switch (relationName) {
                        case "Is_Translation_Of_OUT":
                            processTranslations(message,session);
                            break;
                        case "Is_Translation_Of_IN":
                            processTranslations(message,session);
                            break;
                    }
                }
            }
        }
    }

    private void processTranslations(Map<String, Object> message, Session session) {
        List<Map<String, Object>> finalContentNodes = session.readTransaction(transaction -> {
            StatementResult data = transaction.run("MATCH p=(n{identifier:'" + message.get("nodeUniqueId") + "'})-[:Is_Translation_Of]->(n1)<-[:Is_Translation_Of]-(n2) where n:" + message.get("graphId") + " and n1:" + message.get("graphId") + " and n2:" + message.get("graphId") + " return nodes(p) as contentNodes;");
            List<Map<String, Object>> contentNodes = (List<Map<String, Object>>) data.single().asMap().get("contentNodes");

            Map<String, Object> allMeta = new HashMap<>();
            for (Map<String, Object> contentNode : contentNodes) {
                String locale = String.valueOf(contentNode.getOrDefault("locale", "en"));
                allMeta.put("name_" + locale, String.valueOf(contentNode.getOrDefault("name", "")));
                allMeta.put("description_" + locale, String.valueOf(contentNode.getOrDefault("description", "")));
                allMeta.put("keywords_" + locale, contentNode.getOrDefault("keywords", new ArrayList<>()));
                allMeta.put("subtitle_" + locale, contentNode.getOrDefault("subtitle", new ArrayList<>()));
                allMeta.put("childrenTitle_" + locale, contentNode.getOrDefault("childrenTitle", new ArrayList<>()));
                allMeta.put("childrenDescription_" + locale, contentNode.getOrDefault("childrenDescription", new ArrayList<>()));
                allMeta.put("catalogPaths_" + locale, contentNode.getOrDefault("catalogPaths", new ArrayList<>()));
            }

            for (Map<String, Object> contentNode : contentNodes) {
                String locale = String.valueOf(contentNode.getOrDefault("locale", "en"));
                String identifier = String.valueOf(contentNode.get("identifier"));
                HashMap<String, Object> currentMeta = new HashMap<>(allMeta);
                contentNode.clear();
                currentMeta.remove("name_" + locale);
                currentMeta.remove("description_" + locale);
                currentMeta.remove("keywords_" + locale);
                currentMeta.remove("subtitle_" + locale);
                currentMeta.remove("childrenTitle_" + locale);
                currentMeta.remove("childrenDescription_" + locale);
                currentMeta.remove("catalogPaths_" + locale);
                contentNode.put("identifier", identifier);
                contentNode.putAll(currentMeta);
            }
            return contentNodes;
        });

        List<UpdateMetaRequest> allUpdates = new ArrayList<>();
        for (Map<String, Object> finalContentNode : finalContentNodes) {
            UpdateMetaRequest updateMetaRequest = new UpdateMetaRequest(String.valueOf(finalContentNode.get("identifier")), finalContentNode);
            allUpdates.add(updateMetaRequest);
        }
        Transaction transaction = session.beginTransaction();
        try {
            graphService.updateNodesV2(String.valueOf(message.get("graphId")), allUpdates, transaction);
            transaction.success();
            transaction.close();
        } catch (Exception e) {
            transaction.failure();
            transaction.close();
        }
    }

    private void upsertDocument(Map<String, Object> message, Session session) throws Exception {
        String operationType = (String) message.get("operationType");
        switch (operationType) {
            case "CREATE": {
                System.out.println("#OPERATION TYPE = CREATE");
                processIndexDocument(message, false,session);
                break;
            }
            case "UPDATE": {
                System.out.println("#OPERATION TYPE = UPDATE");
                processIndexDocument(message, true,session);
                break;
            }
        }
    }

    public List<Map<String, Object>> processMessageEnvelope(List<Map<String, Object>> jsonObject) {
        List<Map<String, Object>> errorMessages = new ArrayList<>();
        int cnt = 0;
        Session session = driver.session();
        for (Map<String, Object> message : jsonObject) {
            cnt++;
            System.out.println("===============PROCESSING MESSAGE " + cnt);
            System.out.println("#INCOMING MESSAGE");
            System.out.println("    " + message);
            try {

                if (message != null && message.get("operationType") != null) {
                    String nodeType = (String) message.get("nodeType");
                    if (Strings.isNullOrEmpty(nodeType)) {
                        message.put("SAMZA_ERROR", "nodeType missing");
                        errorMessages.add(message);
                        continue;
                    }
                    switch (nodeType) {
                        case "LEARNING_CONTENT": {
                            upsertDocument(message,session);
                            break;
                        }
                        default:
                            message.put("SAMZA_ERROR", "nodeType not expected");
                            errorMessages.add(message);
                    }
                } else {
                    message.put("SAMZA-ERROR", "operationType missing");
                    errorMessages.add(message);
                }
                System.out.println("===============END PROCESSING MESSAGE " + cnt);
            } catch (Exception e) {
                e.printStackTrace();
                message.put("SAMZA_ERROR", e.getMessage());
                message.put("SAMZA_ERROR_STACK_TRACE", e.getStackTrace());
                errorMessages.add(message);
            }
        }
        session.close();
        return errorMessages;
    }
}
