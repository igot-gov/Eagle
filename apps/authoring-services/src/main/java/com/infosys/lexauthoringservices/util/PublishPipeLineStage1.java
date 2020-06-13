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

import com.infosys.lexauthoringservices.model.UpdateMetaRequest;
import com.infosys.lexauthoringservices.model.UpdateRelationRequest;
import com.infosys.lexauthoringservices.model.neo4j.ContentNode;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.AppConfigRepository;
import com.infosys.lexauthoringservices.repository.cassandra.sunbird.UserRepository;
import com.infosys.lexauthoringservices.service.GraphService;
import com.infosys.lexauthoringservices.serviceimpl.ContentCrudServiceImpl;
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
public class PublishPipeLineStage1 {

	@Autowired
	private LexServerProperties lexServerProps;
	@Autowired
	private RestTemplate restTemplate;
	private Logger logger = LoggerFactory.getLogger(PublishPipeLineStage1.class);
	@Autowired
	private Driver neo4jDriver;
	@Autowired
	private GraphService graphService;
	@Autowired
	private ContentCrudServiceImpl contentCrudServiceImpl;
	@Autowired
	private UserRepository userRepository;
	public static SimpleDateFormat inputFormatterDateTime = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");
	Calendar calendar = Calendar.getInstance();
	@Autowired
    private AppConfigRepository appConfigRepo;
	@Autowired
    private LexServerProperties lexServerProperties;
    @Value("${notification.engine.ip}")
    private String notificationEngineIp;
    @Value("${notification.engine.port}")
    private String notificationEnginePort;
    @Value("${notification.engine.publish.path}")
    private String notificationEnginePublishPath;
	private List<String> noZipButCopyAUrlMimeTypes = Arrays.asList("audio/mp3","audio/mpeg","application/pdf","application/x-mpegURL","video/mp4");
	private List<String> supportedMimeTypesForZip = Arrays.asList("application/htmlpicker", "application/drag-drop", "resource/collection", "application/web-module");

    public boolean processMessage(Map<String, Object> message, UUID uuid) {
		System.out.println(uuid + "#PROCESSING");
		System.out.println(uuid + "    " + message);
		String rootOrg = (String) message.get(LexConstants.ROOT_ORG);
		String org = (String) message.get(LexConstants.ORG);
		String topLevelContentId = (String) message.get("topLevelContentId");
		String appName = (String) message.get("appName");
		String appUrl = (String) message.get("appUrl");

		List<String> allContentIds = (ArrayList<String>) message.get("contentIds");

		Session session = neo4jDriver.session();
		Transaction transaction = session.beginTransaction();
		System.out.println(uuid + "    STARTING UPDATE NEO$J STATUS");
		if (!updateNeo4jStatus(topLevelContentId, allContentIds, rootOrg, transaction, uuid)) {
			System.out.println(uuid + "#updateNeo4jStatus FAILED");
			transaction.close();
			session.close();
			return false;
		}
		System.out.println(uuid + "    END UPDATE NEO$J STATUS");
		System.out.println(uuid + "    STARTING FILE MOVEMENT");
		if (!callContentAPIForFileMovement(rootOrg, org, allContentIds, transaction, uuid)) {
			System.out.println(uuid + "#callContentAPIForFileMovement FAILED");
			transaction.close();
			session.close();
			return false;
		}
		System.out.println(uuid + "    END FILE MOVEMENT");
		transaction.success();
		transaction.close();
		session.close();
		System.out.println(uuid + "    STARTING EMAIL");
		Session session1 = neo4jDriver.session();
		try {
            callEmailService(rootOrg, topLevelContentId, session1, appName, appUrl, uuid);
        }catch (Exception e){
            System.out.println(uuid + "    ERROR IN EMAIL FUNCTION");
            System.out.println(uuid + "     "+e);
        }
		session1.close();
		System.out.println(uuid + "    END EMAIL");
		return true;
	}

	private boolean updateNeo4jStatus(String topLevelContentId, List<String> allContentIds, String rootOrg,
			Transaction transaction, UUID uuid) {

		ContentNode beforeUpdateTopLevelContentNode = null;
		try {
			String temp = topLevelContentId.replace(LexConstants.IMG_SUFFIX, "");
			beforeUpdateTopLevelContentNode = graphService.getNodeByUniqueIdV2(rootOrg, temp, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#updateNeo4jStatus 0 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP1");
		List<ContentNode> allNodes = new ArrayList<>();
		try {
			allNodes = graphService.getNodesByUniqueIdV2(rootOrg, allContentIds, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#updateNeo4jStatus 0 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP2");
		List<ContentNode> imageNodes = new ArrayList<>();
		List<ContentNode> originalNodes = new ArrayList<>();
		allNodes.forEach(item -> {
			if (item.getIdentifier().contains(LexConstants.IMG_SUFFIX))
				imageNodes.add(item);
			else
				originalNodes.add(item);
		});

		List<UpdateMetaRequest> updateMetaRequestsImageNodes = new ArrayList<>();
		List<String> deleteChildrenIds = new ArrayList<>();
		List<String> deleteIds = new ArrayList<>();
		List<UpdateRelationRequest> updateChildrenRequest = new ArrayList<>();
		for (ContentNode imageNode : imageNodes) {
			deleteIds.add(imageNode.getIdentifier());
			String originalId = imageNode.getIdentifier().replace(LexConstants.IMG_SUFFIX, "");
			HashMap<String, Object> tempMap = new HashMap<>(imageNode.getMetadata());
			tempMap.put(LexConstants.STATUS, LexConstants.LIVE);
			tempMap.put(LexConstants.IDENTIFIER, originalId);
			tempMap.put(LexConstants.LAST_PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
			if (imageNode.getMetadata().getOrDefault(LexConstants.PUBLISHED_ON, "").toString().isEmpty()) {
				tempMap.put(LexConstants.PUBLISHED_ON, inputFormatterDateTime.format(calendar.getTime()));
			}
			updateMetaRequestsImageNodes.add(new UpdateMetaRequest(originalId, tempMap));
			deleteChildrenIds.add(originalId);
			imageNode.getChildren().forEach(item -> {
				updateChildrenRequest
						.add(new UpdateRelationRequest(originalId, item.getEndNodeId(), item.getMetadata()));
			});
		}

		List<UpdateMetaRequest> updateMetaRequestsOriginalNodes = new ArrayList<>();
		for (ContentNode originalNode : originalNodes) {
			updateMetaRequestsOriginalNodes.add(new UpdateMetaRequest(originalNode.getIdentifier(),
					Collections.singletonMap(LexConstants.STATUS, LexConstants.LIVE)));
		}
		System.out.println(uuid + "        STEP3");
//        graphService.deleteNodes(rootOrg, deleteIds, transaction);
		graphService.deleteChildren(rootOrg, deleteChildrenIds, transaction);
		System.out.println(uuid + "        STEP4");
		try {
			graphService.updateNodesV2(rootOrg, updateMetaRequestsImageNodes, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#updateNeo4jStatus 1 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP5");
//        graphService.deleteChildren(rootOrg, deleteChildrenIds, transaction);
		graphService.mergeRelations(rootOrg, updateChildrenRequest, transaction);
		System.out.println(uuid + "        STEP6");
//        graphService.createRelations(rootOrg, updateChildrenRequest, transaction);
		graphService.deleteNodes(rootOrg, deleteIds, transaction);
		System.out.println(uuid + "        STEP7");
		try {
			graphService.updateNodesV2(rootOrg, updateMetaRequestsOriginalNodes, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#updateNeo4jStatus 2 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP8");

		topLevelContentId = topLevelContentId.replace(LexConstants.IMG_SUFFIX, "");
		ContentNode afterUpdateTopLevelContentNode = null;
		try {
			afterUpdateTopLevelContentNode = graphService.getNodeByUniqueIdV2(rootOrg, topLevelContentId, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#updateNeo4jStatus 3 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP9");
		long durationDifference = Long
				.parseLong(String.valueOf(afterUpdateTopLevelContentNode.getMetadata().get(LexConstants.DURATION)))
				- Long.parseLong(
						String.valueOf(beforeUpdateTopLevelContentNode.getMetadata().get(LexConstants.DURATION)));
		ArrayList<UpdateMetaRequest> allContentsUpdateMetaRequest = new ArrayList<>();
		if (durationDifference != 0) {
			try {
				List<Map<String, Object>> data = contentCrudServiceImpl
						.getReverseHierarchyFromNeo4jForDurationUpdate(topLevelContentId, rootOrg, transaction);
				if (!data.isEmpty()) {
					for (Map<String, Object> datum : data) {
						if (null != datum.get(LexConstants.DURATION)
								&& !datum.get(LexConstants.DURATION).equals("null")) {
							if (null != datum.get(LexConstants.IDENTIFIER)
									&& !datum.get(LexConstants.IDENTIFIER).equals("null")) {
								Map<String, Object> updateMap = new HashMap<>();
								updateMap.put(LexConstants.DURATION,
										Long.parseLong(String.valueOf(datum.get(LexConstants.DURATION)))
												+ durationDifference);
								UpdateMetaRequest updateMetaRequest = new UpdateMetaRequest(
										String.valueOf(datum.get(LexConstants.IDENTIFIER)), updateMap);
								allContentsUpdateMetaRequest.add(updateMetaRequest);
							}
						}
					}
				} else {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(uuid + "#updateNeo4jStatus 4 FAILED");
				transaction.failure();
				return false;
			}
			System.out.println(uuid + "        STEP10");
			try {
				graphService.updateNodesV2(rootOrg, allContentsUpdateMetaRequest, transaction);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(uuid + "#updateNeo4jStatus 5 FAILED");
				transaction.failure();
				return false;
			}
		}
		System.out.println(uuid + "        STEP11");
		double sizeDifference = Double
				.parseDouble(String.valueOf(afterUpdateTopLevelContentNode.getMetadata().get(LexConstants.SIZE)))
				- Double.parseDouble(
						String.valueOf(beforeUpdateTopLevelContentNode.getMetadata().get(LexConstants.SIZE)));
		allContentsUpdateMetaRequest = new ArrayList<>();
		if (sizeDifference != 0) {
			try {
				List<Map<String, Object>> data = contentCrudServiceImpl
						.getReverseHierarchyFromNeo4jForDurationUpdate(topLevelContentId, rootOrg, transaction);
				if (!data.isEmpty()) {
					for (Map<String, Object> datum : data) {
						if (null != datum.get(LexConstants.SIZE) && !datum.get(LexConstants.SIZE).equals("null")) {
							if (null != datum.get(LexConstants.IDENTIFIER)
									&& !datum.get(LexConstants.IDENTIFIER).equals("null")) {
								Map<String, Object> updateMap = new HashMap<>();
								updateMap.put(LexConstants.SIZE,
										Double.parseDouble(String.valueOf(datum.get(LexConstants.SIZE)))
												+ sizeDifference);
								UpdateMetaRequest updateMetaRequest = new UpdateMetaRequest(
										String.valueOf(datum.get(LexConstants.IDENTIFIER)), updateMap);
								allContentsUpdateMetaRequest.add(updateMetaRequest);
							}
						}
					}
				} else {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(uuid + "#updateNeo4jStatus 5 FAILED");
				transaction.failure();
				return false;
			}
			System.out.println(uuid + "        STEP12");
			try {
				graphService.updateNodesV2(rootOrg, allContentsUpdateMetaRequest, transaction);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("uuid + #updateNeo4jStatus 6 FAILED");
				transaction.failure();
				return false;
			}
		}

		return true;
	}

	private boolean callContentAPIForFileMovement(String rootOrg, String org, List<String> allContentIds,
			Transaction transaction, UUID uuid) {

		ListIterator<String> iterator = allContentIds.listIterator();
		while (iterator.hasNext()) {
			String val = iterator.next();
			if (val.contains(LexConstants.IMG_SUFFIX)) {
				iterator.set(val.replace(LexConstants.IMG_SUFFIX, ""));
			}
		}
		System.out.println(uuid + "        STEP1");
		List<ContentNode> allContentData = null;
		try {
			allContentData = graphService.getNodesByUniqueIdV2(rootOrg, allContentIds, transaction);
			if (allContentData.isEmpty() || allContentData.size() != allContentIds.size()) {
				System.out.println(uuid + "#callContentAPIForFileMovement 0 FAILED");
				transaction.failure();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#callContentAPIForFileMovement 1 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP2");
		List<UpdateMetaRequest> zipUpdateList = new ArrayList<>();
		for (ContentNode contentData : allContentData) {
			org = org.replaceAll(" ", "_");
			HashMap<String, Object> tempMap = new HashMap<>();
			try {
				String artifactUrl = String.valueOf(contentData.getMetadata().get(LexConstants.ARTIFACT_URL));
				boolean callZipApi = false;
				System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! " + artifactUrl);
				if (artifactUrl.matches("^https://.*/content-store/.*/.*/web-hosted/.*$")){
					System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! MATCHES");
					String mimeType = String.valueOf(contentData.getMetadata().get(LexConstants.MIME_TYPE));
					System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! " + mimeType);
					if (mimeType.equals("application/html")){
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! HTML");
						Boolean isExternal = (Boolean) contentData.getMetadata().get(LexConstants.ISEXTERNAL);
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! " + isExternal);
						if (!isExternal){
							callZipApi = true;
						}
					}else if (mimeType.equals("application/quiz")){
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! QUIZ");
						String resourceType = String.valueOf(contentData.getMetadata().get(LexConstants.RESOURCE_TYPE));
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! " + resourceType);
						if (resourceType.equals("Quiz")){
							callZipApi = true;
						}
					}else if (supportedMimeTypesForZip.contains(mimeType)){
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! OTHER ALLOWED");
						callZipApi = true;
					}

					if (callZipApi){
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! CALLING ZIP");
						String url = lexServerProps.getContentServiceZipUrl() + "/" + rootOrg + "%2F" + org + "%2FPublic%2F" + contentData.getIdentifier();
						URI uri = URI.create(url);
						System.out.println(uuid + " CALLING ZIP API: " + uri);
						ResponseEntity<Map> response = restTemplate.postForEntity(uri, null, Map.class);
						if (!response.getStatusCode().is2xxSuccessful()) {
							return false;
						}
						String downloadUrl = String.valueOf(response.getBody().get(LexConstants.DOWNLOAD_URL));
						tempMap.put(LexConstants.DOWNLOAD_URL, downloadUrl);
					} else {
						System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! NOT CALLING ZIP");
						if (noZipButCopyAUrlMimeTypes.contains(mimeType)) {
							System.out.println(uuid + "!!!!!!!!!!!!!!!!!!!!!!!!! SAME AS ARTIFACT URL");
							tempMap.put(LexConstants.DOWNLOAD_URL, artifactUrl);
						}
					}
				}

				UpdateMetaRequest updateMetaRequest = new UpdateMetaRequest(contentData.getIdentifier(), tempMap);
				zipUpdateList.add(updateMetaRequest);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(uuid + "#callContentAPIForFileMovement 2 FAILED");
				transaction.failure();
				return false;
			}
			System.out.println(uuid + "        STEP3");
			String url = lexServerProps.getContentServicePublishUrl() + "/" + rootOrg + "%2F" + org + "%2FPublic%2F" + contentData.getIdentifier();
			URI uri = URI.create(url);
			System.out.println(uuid + " CALLING PUBLISH API " + uri);
			try {
				ResponseEntity<Object> response = restTemplate.postForEntity(uri, null, Object.class);
				if (!response.getStatusCode().is2xxSuccessful()) {
					transaction.failure();
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(uuid + "#callContentAPIForFileMovement 4 FAILED");
				transaction.failure();
				return false;
			}
			System.out.println(uuid + "        STEP4");
		}

		try {
			graphService.updateNodesV2(rootOrg, zipUpdateList, transaction);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(uuid + "#callContentAPIForFileMovement 3 FAILED");
			transaction.failure();
			return false;
		}
		System.out.println(uuid + "        STEP5");
		return true;
	}

	@Async
	private void callEmailService(String rootOrg, final String topLevelContentId, Session session1, String appName,
                                  String appUrl, UUID uuid) {

        Map<String, Object> data = session1.readTransaction(transaction -> {
            Map<String,Object> returnMap = new HashMap<>();
            String topLevelContentId1 = topLevelContentId.replace(LexConstants.IMG_SUFFIX, "");
            try {
                ContentNode node = graphService.getNodeByUniqueIdV2(rootOrg, topLevelContentId1, transaction);
                List<Map<String, Object>> creators = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.CREATOR_CONTACTS);
                List<Map<String, Object>> publishers = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.PUBLISHER_DETAILS);
                String contentType = String.valueOf(node.getMetadata().get(LexConstants.CONTENT_TYPE));
                String name = String.valueOf(node.getMetadata().get(LexConstants.NAME));
                String publishedBy = String.valueOf(node.getMetadata().get(LexConstants.PUBLISHED_BY));
                String identifier = String.valueOf(node.getMetadata().get(LexConstants.IDENTIFIER));
                List<Map<String, Object>> comments = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.COMMENTS);

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
                creatorsUUID.add(String.valueOf(creator.get(LexConstants.ID)));
            }
            recipientsMap.put("author", creatorsUUID);

            List<Map<String, Object>> publishers = (List<Map<String, Object>>) data.get("publishers");
            List<String> publishersUUID = new ArrayList<>();
            for (Map<String, Object> publisher : publishers) {
                publishersUUID.add(String.valueOf(publisher.get(LexConstants.ID)));
            }
            recipientsMap.put("publisher", publishersUUID);
            requestMap.put("recipients", recipientsMap);

            System.out.println(uuid + "     DATA TO SEND EMAIL");
            System.out.println(uuid + "         " + data);
            System.out.println(uuid + "     CALLING NOTIFICATION ENGINE");
            HttpHeaders headers = new HttpHeaders();
            headers.set("rootOrg", rootOrg);
            HttpEntity entity = new HttpEntity<>(requestMap, headers);
            try {
                restTemplate.exchange("http://" + notificationEngineIp + ":" + notificationEnginePort + notificationEnginePublishPath, HttpMethod.POST, entity, Object.class);
            } catch (HttpStatusCodeException e) {
                System.out.println(uuid + "     ERROR IN CALLING NOTIFICATION ENGINE");
                System.out.println(uuid + "         " + e.getStatusCode());
                System.out.println(uuid + "         " + e.getResponseBodyAsString());
            }
            System.out.println(uuid + "     CALLED NOTIFICATION ENGINE");
        }else {
            System.out.println(uuid + "     NO DATA TO SEND EMAIL");
            System.out.println(uuid + "      " + data);
        }

//		Transaction transaction = session1.beginTransaction();
//		try {
//			topLevelContentId = topLevelContentId.replace(LexConstants.IMG_SUFFIX, "");
//			ContentNode node = graphService.getNodeByUniqueIdV2(rootOrg, topLevelContentId, transaction);
//			List<Map<String, Object>> creators = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.CREATOR_CONTACTS);
//			List<Map<String, Object>> publishers = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.PUBLISHER_DETAILS);
//			List<Map<String, Object>> trackContacts = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.TRACK_CONTACT_DETAILS);
//			String contentType = String.valueOf(node.getMetadata().get(LexConstants.CONTENT_TYPE));
//			String name = String.valueOf(node.getMetadata().get(LexConstants.NAME));
//			String publishedBy = String.valueOf(node.getMetadata().get(LexConstants.PUBLISHED_BY));
//			List<Map<String, Object>> comments = (List<Map<String, Object>>) node.getMetadata().get(LexConstants.COMMENTS);
//
//
//
//
//			List<String> creatorsUUID = new ArrayList<>();
//			for (Map<String, Object> creator : creators) {
//				creatorsUUID.add(String.valueOf(creator.get(LexConstants.TRACK_CREATOR_PUBLISHER_OBJECT_UUID)));
//			}
//
//			List<String> publishersUUID = new ArrayList<>();
//			for (Map<String, Object> publisher : publishers) {
//				publishersUUID.add(String.valueOf(publisher.get(LexConstants.TRACK_CREATOR_PUBLISHER_OBJECT_UUID)));
//			}
//
//			List<String> trackContactsUUID = new ArrayList<>();
//			for (Map<String, Object> trackContact : trackContacts) {
//				trackContactsUUID
//						.add(String.valueOf(trackContact.get(LexConstants.TRACK_CREATOR_PUBLISHER_OBJECT_UUID)));
//			}
//
//			Map<String, Object> emailData = new HashMap<>();
//			String publisher = "<Publisher Name Not Found>";
//			Map<String, Object> artifactData = new HashMap<>();
//			artifactData.put("name", name);
//			artifactData.put("identifier", node.getIdentifier());
//			artifactData.put("contentType", contentType);
//			artifactData.put("comments", comments);
//			artifactData.put("creatorContacts", creators);
//			artifactData.put("trackContacts", trackContacts);
//
//			List<Map<String, String>> ccTo = new ArrayList<>();
//			List<Map<String, String>> emailTo = new ArrayList<>();
//			if (!creatorsUUID.isEmpty()) {
//                Map<String, Object> data = getUsersDataFromUserIds(rootOrg, creatorsUUID, Collections.singletonList(PIDConstants.EMAIL));
//				data.forEach((k,v)->{
//                    Map<String, String> tempMap = new HashMap<>();
//                    tempMap.put("email", (String) ((Map<String,Object>)v).getOrDefault(PIDConstants.EMAIL,""));
//                    emailTo.add(tempMap);
//                });
//			}
//			emailData.put("emailTo", emailTo);
//			if (!creatorsUUID.isEmpty()) {
//                Map<String, Object> data = getUsersDataFromUserIds(rootOrg, publishersUUID, Collections.singletonList(PIDConstants.EMAIL));
//                for (Map.Entry<String, Object> stringObjectEntry : data.entrySet()) {
//                    Map<String, String> tempMap = new HashMap<>();
//                    String email = (String) ((Map<String,Object>)stringObjectEntry.getValue()).getOrDefault(PIDConstants.EMAIL,"");
//                    tempMap.put("email", email);
//                    ccTo.add(tempMap);
//                    if (publishedBy.equals(email))
//                        publisher = email;
//                }
////				List<User> publishersResponse = userRepository.findAllById(publishersUUID);
////				for (User user : publishersResponse) {
////					Map<String, String> tempMap = new HashMap<>();
////					tempMap.put("email", user.getEmail());
////					ccTo.add(tempMap);
////					if (publishedBy.equals(user.getId()))
////						publisher = user.getEmail();
////				}
//			}
//
//			if (!creatorsUUID.isEmpty()) {
//                Map<String, Object> data = getUsersDataFromUserIds(rootOrg, trackContactsUUID, Collections.singletonList(PIDConstants.EMAIL));
//                data.forEach((k,v)->{
//                    Map<String, String> tempMap = new HashMap<>();
//                    tempMap.put("email", (String) ((Map<String,Object>)v).getOrDefault(PIDConstants.EMAIL,""));
//                    ccTo.add(tempMap);
//                });
////				List<User> trackContactsResponse = userRepository.findAllById(trackContactsUUID);
////				for (User user : trackContactsResponse) {
////					Map<String, String> tempMap = new HashMap<>();
////					tempMap.put("email", user.getEmail());
////					ccTo.add(tempMap);
////				}
//			}
//			emailData.put("ccTo", ccTo);
//
//			emailData.put("emailType", "publisherPublished");
//			emailData.put("subject", contentType + " published - " + name);
//			String body = "Congratulations ! " + publisher + " has published your " + contentType + " in " + appName
//					+ ". You can access it at: <a href=\"" + appUrl + "/toc/" + node.getIdentifier() + "\">.";
//			Map<String, Object> bodyData = new HashMap<>();
//			bodyData.put("text", body);
//			emailData.put("body", bodyData);
//			HashMap<Object, Object> actionByMap = new HashMap<>();
//			actionByMap.put("email", publisher);
//			emailData.put("actionBy", Arrays.asList(actionByMap));
//			emailData.put("appURL", appUrl);
//			emailData.put("artifact", artifactData);
//
//			String url = lexServerProps.getEmailServiceUrl();
//			ResponseEntity<Map> response = restTemplate.postForEntity(url, emailData, Map.class);
//			if (!response.getStatusCode().is2xxSuccessful()) {
//
//			}
//			System.out.println(response.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("#callEmailService 1 FAILED");
//			transaction.failure();
//			transaction.close();
//		}
	}
}
