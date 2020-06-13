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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.infosys.lexauthoringservices.service.GraphService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
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

//import samza.neo4jtransactions.model.JobMetrics;

@Service
public class SearchIndexer {
    private final List<String> nestedFields = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private RestHighLevelClient esClient;

    @Value("${infosys.es.auth.enabled}")
    private boolean esAuthEnabled;

    @Value("${infosys.es.host}")
    private String esHost;

    @Value("${infosys.es.port}")
    private String esPort;

    @Value("${infosys.catalog.url}")
    private String catalogUrl;

    @Value("${infosys.es.username}")
    private String esUsername;

    @Value("${infosys.es.password}")
    private String esPassword;

    @Value("${infosys.es.index.type}")
    private String esIndexType;

    @Value("${infosys.es.index}")
    private String esIndex;

    @Value("${infosys.fields.nested}")
    private String nestedFieldsString;

    @Autowired
    private GraphService graphService;

    private Logger logger = LoggerFactory.getLogger(SearchIndexer.class);

    @PostConstruct
    public void init() {
        System.out.println("#INITIALIZING SEARCH INDEXER");
        HttpHost[] hosts = new HttpHost[1];
        hosts[0] = new HttpHost(esHost, Integer.parseInt(esPort));
        System.out.println("  " + esHost);
        System.out.println("  " + esPort);
        System.out.println("  " + esUsername);
        System.out.println("  " + esPassword);
        RestClientBuilder builder = RestClient.builder(hosts);
        if (esAuthEnabled) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUsername, esPassword));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        esClient = new RestHighLevelClient(builder);
        System.out.println("#ES CLIENT INITIALIZED");
        nestedFields.addAll(GraphUtil.fieldsToParse);
//        nestedFields.addAll(Arrays.asList(nestedFieldsString.split(",")));
//        System.out.println(nestedFields.toString());
        System.out.println(catalogUrl);
        System.out.println("#INITIALIZING SEARCH INDEXER FINISHED");
    }

    public void processESMessage(String graphId, String objectType, String uniqueId, String messageId, Map<String, Object> message, UUID messageUUID) throws Exception {
        upsertDocument(uniqueId, message, true, messageUUID);
//        upsertDocument(uniqueId, message, false);
    }

    private Map<String, Object> getDocumentAsMapById(String uniqueId, Map<String, Object> indexDocument, UUID messageUUID) {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            System.out.println(messageUUID+"->"+"#ES INDEX = " + esIndex + locale);
            System.out.println(messageUUID+"->"+"   ID= " + uniqueId);
            GetResponse response = esClient.get(new GetRequest(esIndex + locale).type(esIndexType).id(uniqueId), RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    private Map<String, Object> getDocumentAsMapByNodeId(long uniqueId) {
//        try {
////            Object locale = indexDocument.get("locale");
////            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
////                locale = "_en";
////            else
////                locale = "_" + locale;
//            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
//            boolQueryBuilder.filter(QueryBuilders.termQuery("nodeId",uniqueId));
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(boolQueryBuilder);
//            SearchRequest searchRequest = new SearchRequest().indices(esIndex + "_*").types(esIndexType).source(searchSourceBuilder);
//            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
//            if (response.getHits().totalHits != 1){
//                throw new Exception("MORE THAN ONE DOCUMENT FOUND FOR GIVEN NODE ID "+uniqueId);
//            }
////            GetResponse response = esClient.get(new GetRequest(esIndex + locale).type(esIndexType), RequestOptions.DEFAULT);
//            Map<String,Object> result = new HashMap<>();
//            for (SearchHit hit : response.getHits()) {
//                result = hit.getSourceAsMap();
//                break;
//            }
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> getIndexDocument(Map<String, Object> message, boolean updateRequest, boolean isSearchIndex, UUID messageUUID) throws Exception {
        Map<String, Object> indexDocument = new HashMap<>();
        String identifier = (String) message.get("nodeUniqueId");
        if (updateRequest) {
            System.out.println(messageUUID+"->"+"#FETCHING DOC FROM INDEX");
            indexDocument = getDocumentAsMapById(identifier, message, messageUUID);
            if (null == indexDocument || indexDocument.isEmpty())
                throw new Exception(messageUUID+"->"+"DOCUMENT NOT FETCHED FROM ES");
        }

        Map<String, Object> transactionData = (Map<String, Object>) message.get("transactionData");
        System.out.println(messageUUID+"->"+"#TRANSACTION DATA");
        System.out.println(messageUUID+"->"+"    " + transactionData);
        if (transactionData != null) {
            Map<String, Object> addedProperties = (Map<String, Object>) transactionData.get("properties");
            if (addedProperties != null && !addedProperties.isEmpty()) {
                for (Map.Entry<String, Object> propertyMap : addedProperties.entrySet()) {
                    if (propertyMap != null && propertyMap.getKey() != null) {
                        String propertyName = propertyMap.getKey();
                        Object propertyNewValue = ((Map<String, Object>) propertyMap.getValue()).get("newValue");
                        if (null == propertyNewValue)
                            indexDocument.remove(propertyName);
                        else {
                            if (nestedFields.contains(propertyName)) {
//                                propertyNewValue = objectMapper.readValue(propertyNewValue, new TypeReference<Object>() {});
                                System.out.println(messageUUID+"->"+"#NESTED FIELD");
                                System.out.println(messageUUID+"->"+"    " + propertyName);
                                System.out.println(messageUUID+"->"+"        " + propertyNewValue.getClass());
                                System.out.println(messageUUID+"->"+"        " + propertyNewValue);
                            }
                            indexDocument.put(propertyName, propertyNewValue);
                            if (propertyName.equals("catalog") && isSearchIndex) {
                                System.out.println(messageUUID+"->"+"#NESTED TYPE TAGS");
                                Map<String, List<String>> tagsData = processTags(propertyNewValue, messageUUID);
                                for (Map.Entry<String, List<String>> entry : tagsData.entrySet()) {
                                    indexDocument.put(entry.getKey(), entry.getValue());
                                }
                                System.out.println(messageUUID+"->"+"#NESTED TYPE TAGS COMPLETE");
                            }
                        }
                    }
                }
            }
            List<Map<String, Object>> removedRelations = (List<Map<String, Object>>) transactionData.get("removedRelations");
            if (null != removedRelations && !removedRelations.isEmpty()) {
                for (Map<String, Object> rel : removedRelations) {
//                    String key = rel.get("direction") + "_" + rel.get("type") + "_" + rel.get("relationName");
//                    String title = (String) relationMap.get(key);
//                    if (StringUtils.isNotBlank(title)) {
                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
                    switch (relationName) {
                        case "Has_Sub_Content_OUT":
                            relationName = LexConstants.CHILDREN;
                            break;
                        case "Has_Sub_Content_IN":
                            relationName = LexConstants.COLLECTIONS;
                            break;
                        case "Is_Translation_Of_OUT":
                            relationName = LexConstants.TRANSLATION_OF;
                            break;
                        case "Is_Translation_Of_IN":
                            relationName = LexConstants.HAS_TRANSLATION;
                            break;
                    }
                    List<Map<String, Object>> existingRelationList = (List<Map<String, Object>>) indexDocument.get(relationName);
                    if (null == existingRelationList)
                        existingRelationList = new ArrayList<>();
                    String id = (String) rel.get("id");
                    indexDocument.put(relationName, removeAndOrderRelations(id, existingRelationList));
//                    }
                }
            }
            List<Map<String, Object>> addedRelations = (List<Map<String, Object>>) transactionData.get("addedRelations");
            if (null != addedRelations && !addedRelations.isEmpty()) {
                for (Map<String, Object> rel : addedRelations) {
//                    String key = rel.get("direction") + "_" + rel.get("type") + "_" + rel.get("relationName");
//                    String title = relationMap.get(key);
//                    if (StringUtils.isNotBlank(title)) {
                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
                    switch (relationName) {
                        case "Has_Sub_Content_OUT":
                            relationName = LexConstants.CHILDREN;
                            break;
                        case "Has_Sub_Content_IN":
                            relationName = LexConstants.COLLECTIONS;
                            break;
                        case "Is_Translation_Of_OUT":
                            relationName = LexConstants.TRANSLATION_OF;
                            break;
                        case "Is_Translation_Of_IN":
                            relationName = LexConstants.HAS_TRANSLATION;
                            break;
                    }
                    List<Map<String, Object>> existingRelationList = (List<Map<String, Object>>) indexDocument.get(relationName);
                    if (null == existingRelationList)
                        existingRelationList = new ArrayList<>();
                    String id = (String) rel.get("id");
                    Map<String, Object> relMeta = (Map<String, Object>) rel.get("relationMetadata");
                    indexDocument.put(relationName, addAndOrderRelations(id, relMeta, existingRelationList));
//                    }
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
        System.out.println(messageUUID+"->"+"#INDEX DOC CREATED");
        System.out.println(messageUUID+"->"+"    " + indexDocument);
        return indexDocument;
    }

    private List<Map<String, Object>> removeAndOrderRelations(String id, List<Map<String, Object>> existingRelationList) {
        return existingRelationList.stream().filter(item -> !item.get("identifier").equals(id)).collect(Collectors.toList());
    }

    private List<Map<String, Object>> removeAndOrderDelRelations(String id, List<Map<String, Object>> existingRelationList) {
        return existingRelationList.stream().filter(item -> !item.get("identifier").equals(id)).collect(Collectors.toList());
    }

    private List<Map<String, Object>> addAndOrderRelations(String id, Map<String, Object> relMeta, List<Map<String, Object>> existingRelationList) {
        boolean exists = existingRelationList.stream().anyMatch(item -> item.get("identifier").equals(id));
        if (!exists) {
            existingRelationList.add(relMeta);
            existingRelationList.sort(new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return Integer.parseInt(String.valueOf(o1.getOrDefault("index", "0"))) - Integer.parseInt(String.valueOf(o2.getOrDefault("index", "0")));
                }
            });
        }
        return existingRelationList;
    }

    private Map<String, List<String>> processTags(Object propertyNewValue, UUID messageUUID) throws Exception {
        try {
            List<Map<String, Object>> oldTags = (ArrayList<Map<String, Object>>) propertyNewValue;
            System.out.println(messageUUID+"->"+"#PROCESSING TAGS");

            URL tagsUrl = new URL(catalogUrl);
            List<Map<String, Object>> tagsResponse = objectMapper.readValue(tagsUrl, List.class);
            if (tagsResponse.size() <= 0) {
                throw new Exception(messageUUID+"->"+"TAGS JSON RESPONSE IS EMPTY");
            }
            Map<String, Map<String, Object>> tagsJson = new HashMap<>();
            tagsResponse.forEach(item -> {
                tagsJson.put((String) item.get("identifier"), item);
            });

            Set<String> categoriesIds = new HashSet<>();
            Set<String> tracksIds = new HashSet<>();
            Set<String> subTracksIds = new HashSet<>();
            Set<String> subSubTracksIds = new HashSet<>();
            Set<String> categoriesKeywords = new HashSet<>();
            Set<String> tracksKeywords = new HashSet<>();
            Set<String> subTracksKeywords = new HashSet<>();
            Set<String> subSubTracksKeywords = new HashSet<>();

            oldTags.forEach(item -> {
                if (item.get("type").toString().toLowerCase().equals("level1")) {
                    categoriesIds.add(String.valueOf(item.get("id")));
                    categoriesKeywords.add(String.valueOf(item.get("value")).toLowerCase());
                } else if (item.get("type").toString().toLowerCase().equals("level2")) {
                    tracksIds.add(String.valueOf(item.get("id")));
                    tracksKeywords.add(String.valueOf(item.get("value")).toLowerCase());
                } else if (item.get("type").toString().toLowerCase().equals("level3")) {
                    subTracksIds.add(String.valueOf(item.get("id")));
                    subTracksKeywords.add(String.valueOf(item.get("value")).toLowerCase());
                } else if (item.get("type").toString().toLowerCase().equals("level4")) {
                    subSubTracksIds.add(String.valueOf(item.get("id")));
                    subSubTracksKeywords.add(String.valueOf(item.get("value")).toLowerCase());
                }
            });

            Map<String, List<String>> tagsData = new HashMap<>();
            Set<String> paths = new HashSet<>();
            subSubTracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(),messageUUID)));
            subTracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            tracksIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            categoriesIds.forEach(item -> paths.addAll(createCatalogPath(item, tagsJson, "", new ArrayList<>(), messageUUID)));
            ArrayList<String> allIds = new ArrayList<>();
            allIds.addAll(categoriesIds);
            allIds.addAll(tracksIds);
            allIds.addAll(subTracksIds);
            allIds.addAll(subSubTracksIds);

            System.out.println(messageUUID+"->"+"#FORMED PATHS");
            System.out.println(messageUUID+"->"+"    " + paths);

//            tagsData.put("tags", new ArrayList<>(paths));
            tagsData.put("catalogPaths", new ArrayList<>(paths));
//            tagsData.put("subSubTracks", new ArrayList<>(subSubTracksKeywords));
//            tagsData.put("subTracks", new ArrayList<>(subTracksKeywords));
//            tagsData.put("tracks", new ArrayList<>(tracksKeywords));
//            tagsData.put("categories", new ArrayList<>(categoriesKeywords));
            tagsData.put("catalogPathsIds", allIds);
            return tagsData;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<String> createCatalogPath(String identifier, Map<String, Map<String, Object>> catalogDataMap, String path, List<String> pathsArray, UUID messageUUID) {
        System.out.println(messageUUID+"->"+"!!!!!!!!!!!!!!!" + identifier);
        if (path.isEmpty())
            path = catalogDataMap.get(identifier).get("value").toString();
        else
            path = catalogDataMap.get(identifier).get("value") + LexConstants.TAGS_PATH_DELIMITER + path;

        ArrayList<String> parents = (ArrayList<String>) catalogDataMap.get(identifier).get("parent");

        for (String parent : parents) {
            Map<String, Object> parentObj = catalogDataMap.get(parent);
            System.out.println(messageUUID+"->"+"!!!!!!!!!!!!!!! id = " + parentObj.get("identifier"));
            System.out.println(messageUUID+"->"+"!!!!!!!!!!!!!!! value = " + parentObj.get("value"));
            System.out.println(messageUUID+"->"+"!!!!!!!!!!!!!!! type = " + parentObj.get("type"));
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

    private void addDocumentWithId(String uniqueId, Map<String, Object> indexDocument) {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            esClient.index(new IndexRequest(esIndex + locale, esIndexType, uniqueId).source(indexDocument), RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upsertDocument(String uniqueId, Map<String, Object> message, boolean isSearchIndex, UUID messageUUID) throws Exception {
        String operationType = (String) message.get("operationType");
        switch (operationType) {
            case "CREATE": {
                System.out.println(messageUUID+"->"+"#OPERATION TYPE = CREATE");
                Map<String, Object> indexDocument = getIndexDocument(message, false, isSearchIndex, messageUUID);
                routeAndAddDocumentWithId(uniqueId, indexDocument, isSearchIndex, messageUUID);
                break;
            }
            case "UPDATE": {
                System.out.println(messageUUID+"->"+"#OPERATION TYPE = UPDATE");
                message.remove(LexConstants.ACCESS_PATHS);
                Map<String, Object> indexDocument = getIndexDocument(message, true, isSearchIndex, messageUUID);
                routeAndAddDocumentWithId(uniqueId, indexDocument, isSearchIndex, messageUUID);
                break;
            }
            case "DELETE": {
                System.out.println(messageUUID+"->"+"#OPERATION TYPE = DELETE");
                routeAndDeleteDocumentWithId(uniqueId, message, isSearchIndex, messageUUID);
                break;
            }
        }
    }

    private void routeAndDeleteDocumentWithId(String uniqueId, Map<String, Object> indexDocument, boolean isSearchIndex, UUID messageUUID) throws Exception {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            if (isSearchIndex) {
                System.out.println(messageUUID+"->"+"#ES INDEX = " + esIndex + locale);
                DeleteResponse status = esClient.delete(new DeleteRequest(esIndex + locale, esIndexType, uniqueId), RequestOptions.DEFAULT);
                System.out.println(messageUUID+"->"+"#ES INDEX STATUS=" + status.status().getStatus());
            }else {
                System.out.println(messageUUID+"->"+"#ES INDEX = " + esIndex + locale);
                DeleteResponse status = esClient.delete(new DeleteRequest("lexcontentindex", "resource", uniqueId), RequestOptions.DEFAULT);
                System.out.println(messageUUID+"->"+"#ES INDEX STATUS=" + status.status().getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void routeAndAddDocumentWithId(String uniqueId, Map<String, Object> indexDocument, boolean isSearchIndex, UUID messageUUID) throws Exception {
        try {
            Object locale = indexDocument.get("locale");
            if (null == locale || Strings.isNullOrEmpty(String.valueOf(locale)))
                locale = "_en";
            else
                locale = "_" + locale;
            if (isSearchIndex) {
                System.out.println(messageUUID+"->"+"#ES INDEX = " + esIndex + locale);
                RestStatus status = esClient.index(new IndexRequest(esIndex + locale, esIndexType, uniqueId).source(indexDocument).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE), RequestOptions.DEFAULT).status();
                System.out.println(messageUUID+"->"+"#ES INDEX STATUS=" + status.getStatus());
                int x = status.getStatus();
                if (!(x >= 200 && x < 300)){
                    throw new Exception(status.toString());
                }
            } else {
                System.out.println(messageUUID+"->"+"#ES INDEX = " + esIndex + locale);
                RestStatus status = esClient.index(new IndexRequest("lexcontentindex", "resource", uniqueId).source(indexDocument), RequestOptions.DEFAULT).status();
                System.out.println(messageUUID+"->"+"#ES INDEX STATUS=" + status.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public void errorDocIndexing(String messageUUID,Map<String,Object> indexDocument) throws Exception {
        try {
        	System.out.println(messageUUID + "->" + "#ES ERROR INDEX = " + "search_error_index");
        	RestStatus status = esClient.index(new IndexRequest("search_error_index","searchresources").source(indexDocument).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE), RequestOptions.DEFAULT).status();
        	System.out.println(messageUUID+"->"+"#ES ERROR INDEX STATUS=" + status.getStatus());
        	int x = status.getStatus();
        	if(!(x>=200 && x<300)) {
        		throw new Exception(status.toString());
        	}
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> processMessageEnvelope(List<Map<String, Object>> jsonObject, UUID messageUUID) {
        List<Map<String, Object>> errorMessages = new ArrayList<>();
        int cnt = 0;
        for (Map<String, Object> message : jsonObject) {
            cnt++;
            System.out.println(messageUUID+"->"+"SEARCH INDEXER MSG START " + cnt + " " + message);
            try {

                if (message != null && message.get("operationType") != null) {
//                    if (message.get("operationType").equals("DELETE_RELATION")){
//                        deleteRelation(message);
//                        continue;
//                    }
                    String nodeType = (String) message.get("nodeType");
                    String objectType = (String) message.get("objectType");
                    String graphId = (String) message.get("graphId");
                    String uniqueId = (String) message.get("nodeUniqueId");
                    String messageId = (String) message.get("mid");
                    if (Strings.isNullOrEmpty(nodeType)) {
                        message.put("SAMZA_ERROR", "nodeType missing");
                        errorMessages.add(message);
                        continue;
                    }
                    switch (nodeType) {
                        case "LEARNING_CONTENT": {
                            processESMessage(graphId, objectType, uniqueId, messageId, message, messageUUID);
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
                System.out.println(messageUUID+"->"+"SEARCH INDEXER MSG END   " + cnt);
            } catch (Exception e) {
                e.printStackTrace();
                message.put("SAMZA_ERROR", e.getMessage());
                message.put("SAMZA_ERROR_STACK_TRACE", e.getStackTrace());
                errorMessages.add(message);
                System.out.println(messageUUID+"->"+"SEARCH INDEXER MSG EXCEPTION " + cnt);
            }
        }
        return errorMessages;
    }

//    private void deleteRelation(Map<String, Object> message) throws Exception {
//        Map<String, Object> transactionData = (Map<String, Object>) message.get("transactionData");
//        long nodeId = Long.parseLong(message.get("nodeGraphId").toString());
//        Map<String, Object> document = getDocumentAsMapByNodeId(nodeId);
//        if (null != document && !document.isEmpty()) {
//            List<Map<String, Object>> removedRelations = (List<Map<String, Object>>) transactionData.get("removedRelations");
//            if (null != removedRelations && !removedRelations.isEmpty()) {
//                for (Map<String, Object> rel : removedRelations) {
//                    String relationName = String.valueOf(rel.get("relationName")) + "_" + String.valueOf(rel.get("direction"));
//                    switch (relationName) {
//                        case "Has_Sub_Content_OUT":
//                            relationName = LexConstants.CHILDREN;
//                            break;
//                        case "Has_Sub_Content_IN":
//                            relationName = LexConstants.COLLECTIONS;
//                            break;
//                        case "Is_Translation_Of_OUT":
//                            relationName = LexConstants.TRANSLATION_OF;
//                            break;
//                        case "Is_Translation_Of_IN":
//                            relationName = LexConstants.HAS_TRANSLATION;
//                            break;
//                    }
//                    List<Map<String, Object>> existingRelationList = (List<Map<String, Object>>) document.get(relationName);
//                    if (null == existingRelationList)
//                        existingRelationList = new ArrayList<>();
//                    String id =  document.get("identifier").toString();
//                    document.put(relationName, removeAndOrderDelRelations(id, existingRelationList));
//                }
//                routeAndAddDocumentWithId(document.get("identifier").toString(), document, true);
//            }
//        }
//    }
}
