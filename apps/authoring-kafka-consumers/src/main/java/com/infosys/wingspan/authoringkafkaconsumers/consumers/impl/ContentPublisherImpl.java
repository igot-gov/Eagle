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

import com.google.common.collect.Sets;
import com.infosys.wingspan.authoringkafkaconsumers.consumers.ContentPublisher;
import com.infosys.wingspan.authoringkafkaconsumers.utils.Neo4jQueryHelpers;
import com.infosys.wingspan.authoringkafkaconsumers.utils.ProjectConstants;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ContentPublisherImpl {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Driver neo4jDriver;

    @Autowired
    private Neo4jQueryHelpers neo4JQueryHelpers;

    @Value("${notification.engine.ip}")
    private String notificationEngineIp;

    @Value("${notification.engine.port}")
    private String notificationEnginePort;

    @Value("${notification.engine.publish.path}")
    private String notificationEnginePublishPath;

    @Value("${infosys.content.service.zip.url}")
    private String contentServiceZipUrl;

    @Value("${infosys.content.service.publish.url}")
    private String contentServicePublishUrl;

    @Value("${infosys.core.content-sources.url}")
    private  String contentSourcesUrl;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
    private Logger logger = LoggerFactory.getLogger(ContentPublisher.class);
    private List<String> noZipButCopyAUrlMimeTypes = Arrays.asList("audio/mp3","audio/mpeg","application/pdf","application/x-mpegURL","video/mp4");
    private List<String> supportedMimeTypesForZip = Arrays.asList("application/htmlpicker", "application/drag-drop", "resource/collection", "application/web-module");

    public Map<String, Object> processMessage(Map<String, Object> message, UUID uuid) {
        logger.info(uuid + "#PROCESSING");

        String rootOrg = message.get("rootOrg").toString();
        String org = message.get("org").toString();
        String topLevelContentId = message.get("topLevelContentId").toString();
        String appUrl = message.get("appUrl").toString();

        ArrayList<String> allContentIds = (ArrayList < String >) message.get("contentIds");

        Map<String, Object> errors = new HashMap<>();

        Session mainSession = neo4jDriver.session();
        Transaction transaction = mainSession.beginTransaction();

        try {
            logger.info(uuid + "    STARTING UPDATE NEO$J STATUS");
            if (!updateNeo4jStatus(topLevelContentId, allContentIds, rootOrg, transaction, uuid, errors).isEmpty()) {
                logger.error(uuid + "#updateNeo4jStatus FAILED");
                transaction.failure();
                transaction.close();
                mainSession.close();
                return errors;
            }
            logger.info(uuid + "    END UPDATE NEO$J STATUS");

            logger.info(uuid + "    STARTING FILE MOVEMENT");
            if (!callContentAPIForFileMovement(rootOrg, org, allContentIds, transaction, uuid, errors).isEmpty()) {
                logger.error(uuid + "#callContentAPIForFileMovement FAILED");
                transaction.failure();
                transaction.close();
                mainSession.close();
                return errors;
            }
            logger.info(uuid + "    END FILE MOVEMENT");
        } catch (Exception e){
            logger.error(uuid + " Exception message " + e.getMessage());
            e.printStackTrace();
            transaction.failure();
            transaction.close();
            mainSession.close();
            errors.put("Exception",e);
            return errors;
        }

        transaction.success();
        transaction.close();
        mainSession.close();

        logger.info(uuid + "    STARTING EMAIL");
        Session emailSession = neo4jDriver.session();
        try {
            callEmailService(rootOrg, topLevelContentId, emailSession, appUrl, uuid);
        }catch (Exception e){
            logger.error(uuid + "    ERROR IN EMAIL FUNCTION");
            logger.error(uuid + "     "+e);
        }
        emailSession.close();
        logger.info(uuid + "    END EMAIL");

//        logger.info(uuid + "    STARTING SEARCH AUTOCOMPLETE POPULATION");
//        Session searchAutocompleteSession = neo4jDriver.session();
//        try {
//            populateSearchAutocomplete(rootOrg, topLevelContentId, searchAutocompleteSession, uuid);
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error(uuid + "    ERROR IN SEARCH AUTOCOMPLETE POPULATION");
//            logger.error(uuid + "     "+e);
//        }
//        searchAutocompleteSession.close();
//        logger.info(uuid + "    END SEARCH AUTOCOMPLETE POPULATION");

        return errors;
    }


    @SuppressWarnings("unchecked")
    private Map<String, Object> updateNeo4jStatus(String topLevelContentId, ArrayList<String> allContentIds, String rootOrg, Transaction transaction, UUID uuid, Map<String, Object> errors) {

        Map<String,Object> beforeUpdateTopLevelContentNode;
        try {
            String temp = topLevelContentId.replace(ProjectConstants.IMG_SUFFIX, "");
            beforeUpdateTopLevelContentNode = neo4JQueryHelpers.getNodeByIdentifier(rootOrg,temp,Sets.newHashSet("identifier","duration","size"),transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 0 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        logger.info(uuid + "        STEP1");
        List<Map<String,Object>> allNodes;
        try {
            allNodes = neo4JQueryHelpers.getNodesWithChildren(rootOrg,allContentIds,null,transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 0 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }

        logger.info(uuid + "        STEP3");
        ArrayList<String> sources = new ArrayList<>();
        allNodes.forEach(item -> {
            if (!item.getOrDefault(ProjectConstants.SOURCE_NAME,"").toString().isEmpty())
                sources.add(item.get(ProjectConstants.SOURCE_NAME).toString());
        });

        if (!sources.isEmpty()){
            logger.info(uuid + "        STEP4");
            Map<String,Object> request = new HashMap<>();
            request.put("contentSourceNames",sources);

            Map<String, Object> response;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("rootOrg",rootOrg);
                headers.set("Content-Type","application/json");
                HttpEntity<Map> entity = new HttpEntity<>(request,headers);
                ResponseEntity<Object> tempResponse = restTemplate.exchange(contentSourcesUrl, HttpMethod.POST, entity, Object.class);
                response = (Map<String, Object>) tempResponse.getBody();
            } catch (Exception e){
                e.printStackTrace();
                logger.error(uuid + "#updateNeo4jStatus 1 FAILED");
                transaction.failure();
                errors.put("ERROR_MESSAGE",e.getMessage());
                errors.put("ERROR_STACK_TRACE",e.getStackTrace());
                return errors;
            }

            logger.info(uuid + "        STEP5");
            if (null != response && !response.isEmpty()){
                logger.info(uuid + "        STEP6");
                allNodes.forEach(item->{
                    if(!item.getOrDefault(ProjectConstants.SOURCE_NAME, "").toString().isEmpty()) {
                        Map<String, Object> sourceMap = (Map<String, Object>) response.getOrDefault(item.getOrDefault(ProjectConstants.SOURCE_NAME, "").toString(), Collections.EMPTY_MAP);
                        item.put(ProjectConstants.SOURCE_SHORT_NAME, sourceMap.getOrDefault("sourceShortName",""));
                    }
                });
            }
        }

        logger.info(uuid + "        STEP7");
        List<Map<String,Object>> imageNodes = new ArrayList<>();
        List<Map<String,Object>> originalNodes = new ArrayList<>();
        allNodes.forEach(item -> {
            if (item.get(ProjectConstants.IDENTIFIER).toString().contains(ProjectConstants.IMG_SUFFIX))
                imageNodes.add(item);
            else
                originalNodes.add(item);
        });

        List<Map<String,Object>> updateMetaRequestsImageNodes = new ArrayList<>();
        Set<String> deleteChildrenIds = new HashSet<>();
        Set<String> deleteIds = new HashSet<>();
        List<Map<String, Object>> updateChildrenRequest = new ArrayList<>();
        for ( Map<String, Object> imageNode : imageNodes) {
            deleteIds.add(imageNode.get(ProjectConstants.IDENTIFIER).toString());
            String originalId = imageNode.get(ProjectConstants.IDENTIFIER).toString().replace(ProjectConstants.IMG_SUFFIX, "");
            HashMap<String, Object> tempMap = new HashMap<>(imageNode);
            tempMap.remove(ProjectConstants.CHILDREN);
            tempMap.remove(ProjectConstants.COLLECTIONS);
            tempMap.put(ProjectConstants.STATUS, ProjectConstants.LIVE);
            tempMap.put(ProjectConstants.IDENTIFIER, originalId);
            tempMap.put(ProjectConstants.SOURCE_SHORT_NAME, imageNode.getOrDefault(ProjectConstants.SOURCE_SHORT_NAME, ""));
            tempMap.put(ProjectConstants.LAST_PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
            if (imageNode.getOrDefault(ProjectConstants.PUBLISHED_ON, "").toString().isEmpty()) {
                tempMap.put(ProjectConstants.PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
            }
            updateMetaRequestsImageNodes.add(tempMap);
            deleteChildrenIds.add(originalId);
            ((List<Map<String,Object>>)imageNode.getOrDefault("children", new ArrayList<Map<String, Object>>())).forEach(item -> {
                Map<String, Object> temp = new HashMap<>();
                temp.put("startNodeId", originalId);
                temp.put("endNodeId", item.get("endNodeId"));
                item.remove("endNodeId");
                temp.put("metadata", item);
                updateChildrenRequest.add(temp);
            });
        }

        List<Map<String,Object>> updateMetaRequestsOriginalNodes = new ArrayList<>();
        for (Map<String, Object> originalNode : originalNodes) {
            HashMap<String, Object> tempMap = new HashMap<>();
            tempMap.put(ProjectConstants.IDENTIFIER,originalNode.get(ProjectConstants.IDENTIFIER));
            tempMap.put(ProjectConstants.STATUS, ProjectConstants.LIVE);
            tempMap.put(ProjectConstants.SOURCE_SHORT_NAME, originalNode.getOrDefault(ProjectConstants.SOURCE_SHORT_NAME, ""));
            tempMap.put(ProjectConstants.LAST_PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
            if (originalNode.getOrDefault(ProjectConstants.PUBLISHED_ON, "").toString().isEmpty()) {
                tempMap.put(ProjectConstants.PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
            }
            updateMetaRequestsOriginalNodes.add(tempMap);
        }

        logger.info(uuid + "        STEP8");
        neo4JQueryHelpers.deleteChildRelations(rootOrg, deleteChildrenIds, transaction);
        logger.info(uuid + "        STEP9");
        try {
            neo4JQueryHelpers.updateNodes(rootOrg, updateMetaRequestsImageNodes, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 2 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        logger.info(uuid + "        STEP10");
        neo4JQueryHelpers.mergeRelations(rootOrg, updateChildrenRequest, transaction);
        logger.info(uuid + "        STEP11");
        neo4JQueryHelpers.deleteNodes(rootOrg, deleteIds, transaction);
        logger.info(uuid + "        STEP12");

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(updateChildrenRequest);
        System.out.println(updateMetaRequestsImageNodes);
        System.out.println(updateMetaRequestsOriginalNodes);

        try {
            neo4JQueryHelpers.updateNodes(rootOrg, updateMetaRequestsOriginalNodes, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 3 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        logger.info(uuid + "        STEP13");

        topLevelContentId = topLevelContentId.replace(ProjectConstants.IMG_SUFFIX, "");
        Map<String, Object> afterUpdateTopLevelContentNode;
        try {
            afterUpdateTopLevelContentNode = neo4JQueryHelpers.getNodeByIdentifier(rootOrg, topLevelContentId, Sets.newHashSet(ProjectConstants.IDENTIFIER, ProjectConstants.DURATION, ProjectConstants.SIZE), transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 4 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        logger.info(uuid + "        STEP14");
        long durationDifference = Long.parseLong(String.valueOf(afterUpdateTopLevelContentNode.get(ProjectConstants.DURATION))) - Long.parseLong(String.valueOf(beforeUpdateTopLevelContentNode.get(ProjectConstants.DURATION)));
        double sizeDifference = Double.parseDouble(String.valueOf(afterUpdateTopLevelContentNode.get(ProjectConstants.SIZE)))- Double.parseDouble(String.valueOf(beforeUpdateTopLevelContentNode.get(ProjectConstants.SIZE)));
        ArrayList<Map<String,Object>> allContentsUpdateMetaRequest = new ArrayList<>();
        if (durationDifference != 0 || sizeDifference != 0) {
            try {
                List<Map<String, Object>> data = neo4JQueryHelpers.getReverseHierarchyFromNeo4jForDurationUpdate(topLevelContentId, rootOrg, transaction);
                if (!data.isEmpty()) {
                    for (Map<String, Object> datum : data) {
                        if (null != datum.get(ProjectConstants.DURATION) && !datum.get(ProjectConstants.DURATION).equals("null")) {
                            datum.put(ProjectConstants.DURATION, Long.parseLong(String.valueOf(datum.get(ProjectConstants.DURATION))) + durationDifference);
                        }
                        if (null != datum.get(ProjectConstants.SIZE) && !datum.get(ProjectConstants.SIZE).equals("null")) {
                            datum.put(ProjectConstants.SIZE, Double.parseDouble(String.valueOf(datum.get(ProjectConstants.SIZE))) + sizeDifference);
                        }
                        allContentsUpdateMetaRequest.add(datum);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(uuid + "#updateNeo4jStatus 5 FAILED");
                transaction.failure();
                errors.put("ERROR_MESSAGE",e.getMessage());
                errors.put("ERROR_STACK_TRACE",e.getStackTrace());
                return errors;
            }
        }

        logger.info(uuid + "        STEP15");
        try {
            neo4JQueryHelpers.updateNodes(rootOrg, allContentsUpdateMetaRequest, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#updateNeo4jStatus 6 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        return errors;
    }

    private Map<String, Object> callContentAPIForFileMovement(String rootOrg, String org, ArrayList<String> allContentIds, Transaction transaction, UUID uuid, Map<String, Object> errors) {

        ListIterator<String> iterator = allContentIds.listIterator();
        while (iterator.hasNext()) {
            String val = iterator.next();
            if (val.contains(ProjectConstants.IMG_SUFFIX)) {
                iterator.set(val.replace(ProjectConstants.IMG_SUFFIX, ""));
            }
        }
        logger.info(uuid + "        STEP1");
        List<Map<String,Object>> allContentData;
        try {
            allContentData = neo4JQueryHelpers.getNodesByIdentifier(rootOrg, allContentIds, Sets.newHashSet(ProjectConstants.IDENTIFIER,ProjectConstants.ARTIFACT_URL,ProjectConstants.MIME_TYPE,ProjectConstants.ISEXTERNAL,ProjectConstants.RESOURCE_TYPE) , transaction);
            if (allContentData.isEmpty() || allContentData.size() != allContentIds.size()) {
                logger.error(uuid + "#callContentAPIForFileMovement 0 FAILED");
                transaction.failure();
                errors.put("ERROR_MESSAGE","#callContentAPIForFileMovement 0 FAILED");
                errors.put("ERROR_STACK_TRACE",null);
                return errors;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#callContentAPIForFileMovement 1 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        logger.info(uuid + "        STEP2");
        List<Map<String,Object>> zipUpdateList = new ArrayList<>();
        for (Map<String, Object> contentData : allContentData) {
            org = org.replaceAll(" ", "_");
            HashMap<String, Object> tempMap = new HashMap<>();
            try {
                String artifactUrl = String.valueOf(contentData.get(ProjectConstants.ARTIFACT_URL));
                logger.info(uuid +  "            A-URL" + artifactUrl);
                boolean callZipApi = false;
                if (artifactUrl.matches("^https://.*/content-store/.*/.*/web-hosted/.*$")){
                    logger.info(uuid +  "            MATCHES REGEX");
                    String mimeType = String.valueOf(contentData.get(ProjectConstants.MIME_TYPE));
                    if (mimeType.equals("application/html")){
                        logger.info(uuid +  "            HTML");
                        Boolean isExternal = (Boolean) contentData.get(ProjectConstants.ISEXTERNAL);
                        if (!isExternal){
                            callZipApi = true;
                        }
                    }else if (mimeType.equals("application/quiz")){
                        logger.info(uuid +  "            QUIZ");
                        String resourceType = String.valueOf(contentData.get(ProjectConstants.RESOURCE_TYPE));
                        if (resourceType.equals("Quiz")){
                            callZipApi = true;
                        }
                    }else if (supportedMimeTypesForZip.contains(mimeType)){
                        logger.info(uuid +  "            OTHER MIME TYPES");
                        callZipApi = true;
                    }

                    if (callZipApi){
                        String url = contentServiceZipUrl + "/" + rootOrg + "%2F" + org + "%2FPublic%2F" + contentData.get(ProjectConstants.IDENTIFIER);
                        URI uri = URI.create(url);
                        logger.info(uuid +  "            CALLING ZIP API " + url);
                        ResponseEntity<Map> response = restTemplate.postForEntity(uri, null, Map.class);
                        if (!response.getStatusCode().is2xxSuccessful()) {
                            logger.error(uuid +  "            ZIP API FAILED WITH STATUS " + response.getStatusCodeValue());
                            errors.put("ERROR_MESSAGE","ZIP API FAILED WITH " + response.getStatusCodeValue());
                            errors.put("ERROR_STACK_TRACE",null);
                            return errors;
                        }
                        String downloadUrl = String.valueOf(response.getBody().get(ProjectConstants.DOWNLOAD_URL));
                        tempMap.put(ProjectConstants.DOWNLOAD_URL, downloadUrl);
                    } else {
                        logger.info(uuid +  "            NOT CALLING ZIP API");
                        if (noZipButCopyAUrlMimeTypes.contains(mimeType)) {
                            tempMap.put(ProjectConstants.DOWNLOAD_URL, artifactUrl);
                        }
                    }
                }
                tempMap.put(ProjectConstants.IDENTIFIER, contentData.get(ProjectConstants.IDENTIFIER));
                zipUpdateList.add(tempMap);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(uuid + "#callContentAPIForFileMovement 2 FAILED");
                transaction.failure();
                errors.put("ERROR_MESSAGE",e.getMessage());
                errors.put("ERROR_STACK_TRACE",e.getStackTrace());
                return errors;
            }

            String url = contentServicePublishUrl + "/" + rootOrg + "%2F" + org + "%2FPublic%2F" + contentData.get(ProjectConstants.IDENTIFIER);
            URI uri = URI.create(url);
            logger.info(uuid + "        CALLING PUBLISH API: " + url);
            try {
                ResponseEntity<Object> response = restTemplate.postForEntity(uri, null, Object.class);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    transaction.failure();
                    logger.error(uuid + "PUBLISH API FAILED WITH STATUS " + response.getStatusCodeValue());
                    errors.put("ERROR_MESSAGE","PUBLISH API FAILED WITH " + response.getStatusCodeValue());
                    errors.put("ERROR_STACK_TRACE",null);
                    return errors;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(uuid + "#callContentAPIForFileMovement 4 FAILED");
                transaction.failure();
                errors.put("ERROR_MESSAGE",e.getMessage());
                errors.put("ERROR_STACK_TRACE",e.getStackTrace());
                return errors;
            }
        }

        logger.info(uuid + "        STEP3");
        try {
            neo4JQueryHelpers.updateNodes(rootOrg, zipUpdateList, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(uuid + "#callContentAPIForFileMovement 3 FAILED");
            transaction.failure();
            errors.put("ERROR_MESSAGE",e.getMessage());
            errors.put("ERROR_STACK_TRACE",e.getStackTrace());
            return errors;
        }
        return errors;
    }

    @SuppressWarnings("unchecked")
    @Async
    private void callEmailService(String rootOrg, final String topLevelContentId, Session session1, String appUrl, UUID uuid) {

        Map<String, Object> data = session1.readTransaction(transaction -> {
            Map<String,Object> returnMap = new HashMap<>();
            String topLevelContentId1 = topLevelContentId.replace(ProjectConstants.IMG_SUFFIX, "");
            try {
                Map<String, Object> node = neo4JQueryHelpers.getNodeByIdentifier(rootOrg, topLevelContentId1, Sets.newHashSet(ProjectConstants.COMMENTS, ProjectConstants.IDENTIFIER, ProjectConstants.PUBLISHED_BY, ProjectConstants.NAME, ProjectConstants.CONTENT_TYPE, ProjectConstants.PUBLISHER_DETAILS, ProjectConstants.CREATOR_CONTACTS), transaction);
                List<Map<String, Object>> creators = (List<Map<String, Object>>) node.get(ProjectConstants.CREATOR_CONTACTS);
                List<Map<String, Object>> publishers = (List<Map<String, Object>>) node.get(ProjectConstants.PUBLISHER_DETAILS);
                String contentType = String.valueOf(node.get(ProjectConstants.CONTENT_TYPE));
                String name = String.valueOf(node.get(ProjectConstants.NAME));
                String publishedBy = String.valueOf(node.get(ProjectConstants.PUBLISHED_BY));
                String identifier = String.valueOf(node.get(ProjectConstants.IDENTIFIER));
                List<Map<String, Object>> comments = (List<Map<String, Object>>) node.get(ProjectConstants.COMMENTS);

                returnMap.put("creators",creators);
                returnMap.put("publishers",publishers);
                returnMap.put("contentType",contentType);
                returnMap.put("publishedBy",publishedBy);
                returnMap.put("comments",comments);
                returnMap.put("name",name);
                returnMap.put("identifier",identifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnMap;
        });

        if (!data.isEmpty()) {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("event-id", "publish_content");

            Map<String, Object> targetDataMap = new HashMap<>();
            targetDataMap.put("identifier", data.get("identifier"));
            requestMap.put("target-data", targetDataMap);

            Map<String, Object> tagValuePairMap = new HashMap<>();
            tagValuePairMap.put("#contentType", data.get("contentType"));
            tagValuePairMap.put("#contentTitle", data.get("name"));
            if (appUrl.endsWith("/")){
                appUrl = appUrl + "app/toc/" + data.get("identifier") +"/overview";
            } else {
                appUrl = appUrl + "/app/toc/" + data.get("identifier") +"/overview";
            }
            tagValuePairMap.put("#targetUrl", appUrl);
            String comment = "";
            List<Map<String, Object>> comments = (List<Map<String, Object>>) data.get("comments");
            if (!comments.isEmpty()) {
                Map<String, Object> commentMap = comments.get(comments.size() - 1);
                comment = (String) commentMap.getOrDefault("comment", "");
            }
            tagValuePairMap.put("#comment", comment);
            tagValuePairMap.put("#publisher", data.get("publishedBy"));
            requestMap.put("tag-value-pair", tagValuePairMap);

            Map<String, Object> recipientsMap = new HashMap<>();
            List<Map<String, Object>> creators = (List<Map<String, Object>>) data.get("creators");
            List<String> creatorsUUID = new ArrayList<>();
            for (Map<String, Object> creator : creators) {
                creatorsUUID.add(String.valueOf(creator.get(ProjectConstants.ID)));
            }
            recipientsMap.put("author", creatorsUUID);

            List<Map<String, Object>> publishers = (List<Map<String, Object>>) data.get("publishers");
            List<String> publishersUUID = new ArrayList<>();
            for (Map<String, Object> publisher : publishers) {
                publishersUUID.add(String.valueOf(publisher.get(ProjectConstants.ID)));
            }
            recipientsMap.put("publisher", publishersUUID);
            requestMap.put("recipients", recipientsMap);

            logger.info(uuid + "     DATA TO SEND EMAIL");
            logger.info(uuid + "         " + data);
            logger.info(uuid + "     CALLING NOTIFICATION ENGINE");
            HttpHeaders headers = new HttpHeaders();
            headers.set("rootOrg", rootOrg);
            HttpEntity entity = new HttpEntity<>(requestMap, headers);
            try {
                restTemplate.exchange("http://" + notificationEngineIp + ":" + notificationEnginePort + notificationEnginePublishPath, HttpMethod.POST, entity, Object.class);
            } catch (HttpStatusCodeException e) {
                logger.error(uuid + "     ERROR IN CALLING NOTIFICATION ENGINE");
                logger.error(uuid + "         " + e.getStatusCode());
                logger.error(uuid + "         " + e.getResponseBodyAsString());
            }
            logger.info(uuid + "     CALLED NOTIFICATION ENGINE");
        }else {
            logger.info(uuid + "     NO DATA TO SEND EMAIL");
            logger.info(uuid + "      " + data);
        }
    }


}
