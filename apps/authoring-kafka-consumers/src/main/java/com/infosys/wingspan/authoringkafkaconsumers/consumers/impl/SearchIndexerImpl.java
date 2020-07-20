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
package com.infosys.wingspan.authoringkafkaconsumers.consumers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.infosys.wingspan.authoringkafkaconsumers.consumers.SearchIndexer;
import com.infosys.wingspan.authoringkafkaconsumers.utils.ProjectConstants;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchIndexerImpl {
    private final List<String> nestedFields = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestHighLevelClient esClient;

    @Value("${infosys.catalog.url}")
    private String catalogUrl;

    @Value("${infosys.es.index.type}")
    private String esIndexType;

    @Value("${infosys.es.index}")
    private String esIndex;

    @Value("${infosys.fields.nested}")
    private String nestedFieldsString;

    private Logger logger = LoggerFactory.getLogger(SearchIndexer.class);

    @PostConstruct
    private void init(){
        nestedFields.addAll(Arrays.asList(nestedFieldsString.split(",")));
    }

    public List<Map<String, Object>> processMessageEnvelope(List<Map<String, Object>> jsonObject, UUID messageUUID) {
        List<Map<String, Object>> errorMessages = new ArrayList<>();
        int cnt = 0;
        for (Map<String, Object> message : jsonObject) {
            ++cnt;
            logger.info(messageUUID + "->" + "SEARCH INDEXER MSG START " + cnt);
            try {
                if (message != null) {
                    if (message.get("operationType") != null) {
                        String nodeType = (String) message.get("nodeType");
                        String uniqueId = (String) message.get("nodeUniqueId");
                        if (Strings.isNullOrEmpty(nodeType)) {
                            message.put("SAMZA_ERROR", "nodeType missing");
                            errorMessages.add(message);
                            continue;
                        }
                        switch (nodeType) {
                            case "LEARNING_CONTENT": {
                                upsertDocument(uniqueId, message, messageUUID);
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
                } else {
                    message = new HashMap<>();
                    message.put("SAMZA-ERROR", "operationType missing");
                    errorMessages.add(message);
                }
                logger.info(messageUUID + "->" + "SEARCH INDEXER MSG END   " + cnt);
            } catch (Exception e) {
                e.printStackTrace();
                if (message == null) {
                    message = new HashMap<>();
                }
                message.put("SAMZA_ERROR", e.getMessage());
                message.put("SAMZA_ERROR_STACK_TRACE", e.getStackTrace());
                errorMessages.add(message);
                logger.info(messageUUID + "->" + "SEARCH INDEXER MSG EXCEPTION " + cnt);
            }
        }
        return errorMessages;
    }

    private void upsertDocument(String uniqueId, Map<String, Object> message, UUID messageUUID) throws Exception {
        String operationType = (String) message.get("operationType");
        switch (operationType) {
            case "CREATE": {
                logger.info(messageUUID + "->" + "#OPERATION TYPE = CREATE");
                Map<String, Object> indexDocument = getIndexDocument(message, false, messageUUID);
                routeAndAddDocumentWithId(uniqueId, indexDocument, messageUUID);
                break;
            }
            case "UPDATE": {
                logger.info(messageUUID + "->" + "#OPERATION TYPE = UPDATE");
                //message.remove(ProjectConstants.ACCESS_PATHS);
                Map<String, Object> indexDocument = getIndexDocument(message, true, messageUUID);
                routeAndAddDocumentWithId(uniqueId, indexDocument, messageUUID);
                break;
            }
            case "DELETE": {
                logger.info(messageUUID + "->" + "#OPERATION TYPE = DELETE");
                routeAndDeleteDocumentWithId(uniqueId, message, messageUUID);
                break;
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> getIndexDocument(Map<String, Object> message, boolean updateRequest, UUID messageUUID) throws Exception {
        Map<String, Object> indexDocument = new HashMap<>();
        String identifier = (String) message.get("nodeUniqueId");

        logger.info(messageUUID + "->" + "#PROCESSING FOR ID " + identifier);

        Map<String, Object> transactionData = (Map<String, Object>) message.get("transactionData");

        if (updateRequest) {
            logger.info(messageUUID + "->" + "#FETCHING DOC FROM INDEX");

            Set<String> sources = new HashSet<>();

//            if (transactionData != null) {
//                List<Map<String, Object>> removedRelations = (List<Map<String, Object>>) transactionData.get("removedRelations");
//                if (null != removedRelations && !removedRelations.isEmpty()) {
//                    for (Map<String, Object> rel : removedRelations) {
//                        String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
//                        switch (relationName) {
//                            case "Has_Sub_Content_OUT":
//                                relationName = ProjectConstants.CHILDREN;
//                                break;
//                            case "Has_Sub_Content_IN":
//                                relationName = ProjectConstants.COLLECTIONS;
//                                break;
//                            case "Is_Translation_Of_OUT":
//                                relationName = ProjectConstants.TRANSLATION_OF;
//                                break;
//                            case "Is_Translation_Of_IN":
//                                relationName = ProjectConstants.HAS_TRANSLATION;
//                                break;
//                        }
//                        sources.add(relationName);
//                    }
//                }
//
//                List<Map<String, Object>> addedRelations = (List<Map<String, Object>>) transactionData.get("addedRelations");
//                if (null != addedRelations && !addedRelations.isEmpty()) {
//                    for (Map<String, Object> rel : addedRelations) {
//                        String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
//                        switch (relationName) {
//                            case "Has_Sub_Content_OUT":
//                                relationName = ProjectConstants.CHILDREN;
//                                break;
//                            case "Has_Sub_Content_IN":
//                                relationName = ProjectConstants.COLLECTIONS;
//                                break;
//                            case "Is_Translation_Of_OUT":
//                                relationName = ProjectConstants.TRANSLATION_OF;
//                                break;
//                            case "Is_Translation_Of_IN":
//                                relationName = ProjectConstants.HAS_TRANSLATION;
//                                break;
//                        }
//                        sources.add(relationName);
//                    }
//                }
//            }

            indexDocument = getDocumentAsMapById(identifier, message, sources, messageUUID);
            if (null == indexDocument)
                throw new Exception(messageUUID + "->" + "DOCUMENT NOT FETCHED FROM ES");
        }

        if (transactionData != null) {
            Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
            if (addedProperties != null && !addedProperties.isEmpty()) {
                for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                    if (propertyMap != null && propertyMap.getKey() != null) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("newValue");
                        if (null == propertyNewValue){
                            indexDocument.remove(propertyName);
                        }
                        else {
                            if (nestedFields.contains(propertyName)) {
                                logger.info(messageUUID + "->" + "#NESTED FIELD");
                                logger.info(messageUUID + "->" + "    " + propertyName);
                                logger.info(messageUUID + "->" + "        " + propertyNewValue.getClass());
                                logger.info(messageUUID + "->" + "        " + propertyNewValue);
                            }
                            indexDocument.put(propertyName, propertyNewValue);
                            if (propertyName.equals("catalog")) {
                                logger.info(messageUUID + "->" + "#NESTED TYPE TAGS");
                                Map<String, List<String>> tagsData = processTags(propertyNewValue, messageUUID);
                                for (Map.Entry<String, List<String>> entry : tagsData.entrySet()) {
                                    indexDocument.put(entry.getKey(), entry.getValue());
                                }
                                logger.info(messageUUID + "->" + "#NESTED TYPE TAGS COMPLETE");
                            }
                        }
                    }
                }
            }
            List<Map<String, Object>> removedRelations = (List<Map<String, Object>>) transactionData.get("removedRelations");
            if (null != removedRelations && !removedRelations.isEmpty()) {
                for (Map<String, Object> rel : removedRelations) {
                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
                    switch (relationName) {
                        case "Has_Sub_Content_OUT":
                            relationName = ProjectConstants.CHILDREN;
                            break;
                        case "Has_Sub_Content_IN":
                            relationName = ProjectConstants.COLLECTIONS;
                            break;
                        case "Is_Translation_Of_OUT":
                            relationName = ProjectConstants.TRANSLATION_OF;
                            break;
                        case "Is_Translation_Of_IN":
                            relationName = ProjectConstants.HAS_TRANSLATION;
                            break;
                    }
                    List<Map<String, Object>> existingRelationList = (List<Map<String, Object>>) indexDocument.get(relationName);
                    if (null == existingRelationList)
                        existingRelationList = new ArrayList<>();
                    String id = (String) rel.get("id");
                    indexDocument.put(relationName, removeAndOrderRelations(id, existingRelationList));
                }
            }
            List<Map<String, Object>> addedRelations = (List<Map<String, Object>>) transactionData.get("addedRelations");
            if (null != addedRelations && !addedRelations.isEmpty()) {
                for (Map<String, Object> rel : addedRelations) {
                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
                    switch (relationName) {
                        case "Has_Sub_Content_OUT":
                            relationName = ProjectConstants.CHILDREN;
                            break;
                        case "Has_Sub_Content_IN":
                            relationName = ProjectConstants.COLLECTIONS;
                            break;
                        case "Is_Translation_Of_OUT":
                            relationName = ProjectConstants.TRANSLATION_OF;
                            break;
                        case "Is_Translation_Of_IN":
                            relationName = ProjectConstants.HAS_TRANSLATION;
                            break;
                    }
                    List<Map<String, Object>> existingRelationList = (List<Map<String, Object>>) indexDocument.get(relationName);
                    if (null == existingRelationList)
                        existingRelationList = new ArrayList<>();
                    String id = (String) rel.get("id");
                    Map<String, Object> relMeta = (Map<String, Object>) rel.get("relationMetadata");
                    indexDocument.put(relationName, addAndOrderRelations(id, relMeta, existingRelationList));
                }
            }
        }
        indexDocument.computeIfAbsent("children", k -> new ArrayList<>());
        indexDocument.computeIfAbsent("collections", k -> new ArrayList<>());
        if (String.valueOf(indexDocument.get("contentType")).equals("Course") || String.valueOf(indexDocument.get("contentType")).equals("Learning Path"))
            indexDocument.put("isStandAlone", true);
        indexDocument.put("rootOrg", message.get("graphId"));
        indexDocument.put("nodeId", message.get("nodeGraphId"));
        indexDocument.put("identifier", message.get("nodeUniqueId"));
        indexDocument.put("objectType", Strings.isNullOrEmpty((String) message.get("objectType")) ? "" : message.get("objectType"));
        indexDocument.put("nodeType", message.get("nodeType"));
        logger.info(messageUUID + "->" + "#INDEX DOC CREATED");
        return indexDocument;
    }

    private Map<String, Object> getDocumentAsMapById(String uniqueId, Map<String, Object> indexDocument, Set<String> sources, UUID messageUUID) {
        try {
            logger.info("indexDocument "+objectMapper.writeValueAsString(indexDocument));
            logger.info("sources "+sources);

            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            FetchSourceContext include = new FetchSourceContext(true, sources.toArray(new String[0]),null);
            logger.info("indexDocument "+objectMapper.writeValueAsString(include));

            GetResponse response = esClient.get(new GetRequest(esIndex + locale).type(esIndexType).fetchSourceContext(include).id(uniqueId), RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> removeAndOrderRelations(String id, List<Map<String, Object>> existingRelationList) {
        return existingRelationList.stream().filter(item -> !item.get("identifier").equals(id)).collect(Collectors.toList());
    }

    private List<Map<String, Object>> addAndOrderRelations(String id, Map<String, Object> relMeta, List<Map<String, Object>> existingRelationList) {
        boolean exists = existingRelationList.stream().anyMatch(item -> item.get("identifier").equals(id));
        if (!exists) {
            existingRelationList.add(relMeta);
            existingRelationList.sort(Comparator.comparingInt(o -> Integer.parseInt(String.valueOf(o.getOrDefault("index", "0")))));
        }
        return existingRelationList;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> processTags(Object propertyNewValue, UUID messageUUID) throws Exception {
        try {
            List<Map<String, Object>> oldTags = (ArrayList<Map<String, Object>>) propertyNewValue;
            logger.info(messageUUID + "->" + "#PROCESSING TAGS");

            URL tagsUrl = new URL(catalogUrl);
            List<Map<String, Object>> tagsResponse = objectMapper.readValue(tagsUrl, List.class);
            if (tagsResponse.size() <= 0) {
                throw new Exception(messageUUID + "->" + "TAGS JSON RESPONSE IS EMPTY");
            }
            Map<String, Map<String, Object>> tagsJson = new HashMap<>();
            tagsResponse.forEach(item -> tagsJson.put((String) item.get("identifier"), item));

            Set<String> categoriesIds = new HashSet<>();
            Set<String> tracksIds = new HashSet<>();
            Set<String> subTracksIds = new HashSet<>();
            Set<String> subSubTracksIds = new HashSet<>();

            oldTags.forEach(item -> {
                switch (item.get("type").toString().toLowerCase()) {
                    case "level1":
                        categoriesIds.add(String.valueOf(item.get("id")));
                        break;
                    case "level2":
                        tracksIds.add(String.valueOf(item.get("id")));
                        break;
                    case "level3":
                        subTracksIds.add(String.valueOf(item.get("id")));
                        break;
                    case "level4":
                        subSubTracksIds.add(String.valueOf(item.get("id")));
                        break;
                }
            });

            Map<String, List<String>> tagsData = new HashMap<>();
            Set<String> paths = new HashSet<>();
            subSubTracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            subTracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            tracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            categoriesIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            ArrayList<String> allIds = new ArrayList<>();
            allIds.addAll(categoriesIds);
            allIds.addAll(tracksIds);
            allIds.addAll(subTracksIds);
            allIds.addAll(subSubTracksIds);

            logger.info(messageUUID + "->" + "#FORMED PATHS");
            logger.info(messageUUID + "->" + "    " + paths);

            tagsData.put("catalogPaths", new ArrayList<>(paths));
            tagsData.put("catalogPathsIds", allIds);
            return tagsData;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> createCatalogPath(String identifier, Map<String, Map<String, Object>> catalogDataMap, String path, List<String> pathsArray, UUID messageUUID) {
        logger.info(messageUUID + "->" + "!!!!!!!!!!!!!!!" + identifier);
        if (path.isEmpty())
            path = catalogDataMap.get(identifier).get("value").toString();
        else
            path = catalogDataMap.get(identifier).get("value") + ProjectConstants.TAGS_PATH_DELIMITER + path;

        ArrayList<String> parents = (ArrayList<String>) catalogDataMap.get(identifier).get("parent");

        for (String parent : parents) {
            Map<String, Object> parentObj = catalogDataMap.get(parent);
            logger.info(messageUUID + "->" + "!!!!!!!!!!!!!!! id = " + parentObj.get("identifier"));
            logger.info(messageUUID + "->" + "!!!!!!!!!!!!!!! value = " + parentObj.get("value"));
            logger.info(messageUUID + "->" + "!!!!!!!!!!!!!!! type = " + parentObj.get("type"));
            if (!parentObj.get("type").toString().toLowerCase().equals("level0"))
                createCatalogPath((String) parentObj.get("identifier"), catalogDataMap, path, pathsArray, messageUUID);
            else
                pathsArray.add(path);
        }

        if (parents.size() == 0) {
            pathsArray.add(path);
        }
        return pathsArray;
    }

    private void routeAndDeleteDocumentWithId(String uniqueId, Map<String, Object> indexDocument, UUID messageUUID) throws Exception {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            logger.info(messageUUID + "->" + "#ES INDEX = " + esIndex + locale);
            DeleteResponse status = esClient.delete(new DeleteRequest(esIndex + locale, esIndexType, uniqueId), RequestOptions.DEFAULT);
            logger.info(messageUUID + "->" + "#ES INDEX STATUS=" + status.status().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void routeAndAddDocumentWithId(String uniqueId, Map<String, Object> indexDocument, UUID messageUUID) throws Exception {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            logger.info(messageUUID + "->" + "#ES INDEX = " + esIndex + locale);
            UpdateRequest request = new UpdateRequest(esIndex + locale, esIndexType, uniqueId)
                    .doc(indexDocument).docAsUpsert(true);

            UpdateResponse response = esClient.update(request, RequestOptions.DEFAULT);
            RestStatus status = response.status();
            logger.info(messageUUID + "->" + "#ES INDEX STATUS=" + status.getStatus());
            int x = status.getStatus();
            if (!(x >= 200 && x < 300))
                throw new Exception(status.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
