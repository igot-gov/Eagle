/**
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
package com.infosys.lex.hierarchy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.hierarchy.properties.HierarchyProperties;
import com.infosys.lex.progress.service.ContentProgressService;

@Service
public class HierarchyServiceImpl implements HierarchyService {

	@Autowired
	RestHighLevelClient client;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	HierarchyProperties accessProps;
	
	@Autowired
	UserUtilityService userService;
	
	LexLogger logger = new LexLogger(HierarchyServiceImpl.class.getName());
	
	@Autowired
	ContentProgressService progressService;

	List<String> masterAllowedStatus = Arrays.asList("Live", "Expired", "Deleted","MarkedForDeletion");
	
	List<String> masterEmailFetchFields = Arrays.asList("creatorContacts", "verifiers", "creatorDetails");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getHierarchyOfContentNode(String identifier, Map<String, Object> reqMap)
			throws BadRequestException, IOException, Exception {
		String rootOrg = null;
		String org = null;
		String userId = null;
		String[] sources = null;
		List<String> fields = new ArrayList<>();
		boolean fieldsPassed = false;

		rootOrg = (String) reqMap.get("rootOrg");
		org = (String) reqMap.get("org");
		userId = (String) reqMap.get("userId");
		fieldsPassed = (boolean) reqMap.get("fieldsPassed");
		Boolean fetchOneLevel = (Boolean) reqMap.get("fetchOneLevel");
		Boolean skipAccessCheck = (Boolean) reqMap.get("skipAccessCheck");
		
		if (fieldsPassed) {
			fields = (List<String>) reqMap.get("fields");
			if (fields == null || fields.isEmpty()) {
				throw new BadRequestException("Invalid request fields, null or empty");
			}
			if (!fields.contains("identifier") || !fields.contains("status") || !fields.contains("children")) {
				fields.add("identifier");
				fields.add("status");
				fields.add("children");
			}
			sources = fields.toArray(new String[0]);
		}
		if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || userId == null
				|| userId.isEmpty()) {
			throw new BadRequestException("Invalid request body, [rootOrg,org,userId] null or empty");
		}
		
		logger.info("Starting fetch of hierarchy from es : ");
		Map<String, Object> hMap = new HashMap<>();
		if(fetchOneLevel==null||fetchOneLevel==false) {
			 hMap = HierarchyApi(identifier, sources);
		}
		else {
			hMap = HierarchyAPI(identifier, sources);
		}
		logger.info("Es fetch for hierarchy is complete : ");
		List<String> allIds = getAllIds(hMap);
		ResponseEntity<HashMap> response = null;
		try {
			
			if(skipAccessCheck==null||skipAccessCheck==false) {
			// restCall for accessCheck
			String urlPostFix = "/accesscontrol/users/contents?rootOrg=@rootOrg";
			String sbExtUrl = "http://" + accessProps.getSbExtIp() + ":" + accessProps.getSbExtPort() + urlPostFix;

			sbExtUrl = sbExtUrl.replace("@rootOrg", rootOrg);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String,Object> requestMap = new HashMap<>();
			Map<String,Object> mainReqBody = new HashMap<>();
			mainReqBody.put(userId, allIds);
			requestMap.put("request", mainReqBody);
			HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(requestMap,headers);
//			logger.info("Access Check called for : " +allIds.toString());
			logger.info("Access check started at : ");
			response = restTemplate.postForEntity(sbExtUrl, request, HashMap.class);
			logger.info("Access check response received in : ");
			}
			else {
				hMap.put("hasAccess", true);
				return hMap;
			}
		} catch (Exception e) {
			logger.info("Access Check called for : " +allIds.toString());
			throw new Exception(e);
		}
		
		
		Map<String,Object> responseMap = response.getBody(); 
		responseMap = (Map<String, Object>) responseMap.get("result");
		responseMap = (Map<String, Object>) responseMap.get("response");
		responseMap = (Map<String, Object>) responseMap.get(userId);
		if(responseMap==null || responseMap.isEmpty()) {
			throw new ApplicationLogicError("AccessControl API could not return result for :" +allIds);
		}
		accessCheck(hMap,responseMap);		
		Map<String, Object> userEmailMap = new HashMap<>();
		
		try {
			returnOnlyRootOrgMetrics(hMap,rootOrg);
			userEmailMap = traverseAndCallPid(hMap, rootOrg);
			traverseAndInsertEmail(hMap, userEmailMap);
		} catch (Exception e) {
			logger.info("Failed to get Emails from PID for : " + identifier);
		}
		
		try {
			learningProgressMetrics(hMap, rootOrg, userId, allIds);
		} catch (Exception e) {
			logger.info("Failed to get progress data : " + identifier);
		}

		return hMap;

	}
	
	
	@SuppressWarnings("unchecked")
	private void traverseAndInsertEmail(Map<String, Object> hMap, Map<String, Object> userEmailMap) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> children = (List<Map<String, Object>>) parent.getOrDefault("children",
					new ArrayList<>());
			for (String key : parent.keySet()) {
				if (masterEmailFetchFields.contains(key)) {
					List<Map<String, Object>> allUserDataList = (List<Map<String, Object>>) parent.get(key);
					List<Map<String, Object>> userDataWithEmail = new ArrayList<>();
					for (Map<String, Object> userData : allUserDataList) {
						String email = (String) userEmailMap.getOrDefault(userData.get("id"),"");
						Map<String, Object> dataWithEmail = new HashMap<>(userData);
						dataWithEmail.put("email", email);
						userDataWithEmail.add(dataWithEmail);
					}
					parent.put(key, userDataWithEmail);
				}
			}
			parent.put("children", children);
			parentObjs.addAll(children);
		}

	}

	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> traverseAndCallPid(Map<String, Object> hMap, String rootOrg) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		Map<String, Object> allUserEmailData = new HashMap<>();
		List<String> uuids = new ArrayList<>();
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenCopy = (List<Map<String, Object>>) parent.getOrDefault("children",
					new ArrayList<>());
			for (String item : masterEmailFetchFields) {
				if (parent.containsKey(item)) {
					List<Map<String, Object>> allUserData = (List<Map<String, Object>>) parent.getOrDefault(item,
							new ArrayList<>());
					logger.info("field exists for ID : "+ parent.getOrDefault("identifier", "").toString() + " value of field(" +item +") : " + allUserData.toString());
					uuids.addAll(getUuidsFromList(allUserData));
				}
			}
			parent.put("children", childrenCopy);
			parentObjs.addAll(childrenCopy);
		}
		Set<String> temp = new HashSet<>(uuids);
		uuids = new ArrayList<>(temp);
		if(!uuids.isEmpty()) {
			logger.info("Data present in uuids list : " + uuids.toString()); 
			allUserEmailData.putAll(userService.getUserEmailsFromUserIds(rootOrg,uuids));
		}
		else {
			logger.info("Not calling Pid as uuids are empty :"+ hMap.toString());
		}
		return allUserEmailData;

	}
	
	private List<String> getUuidsFromList(List<Map<String, Object>> details) {
		List<String> uuids = new ArrayList<>();
		for (Map<String, Object> item : details) {
			String uuid = (String) item.getOrDefault("id", "");
			if (!uuid.isEmpty()) {
				uuids.add(uuid);
			}
		}
		return uuids;
	}



	@SuppressWarnings("unchecked")
	private void learningProgressMetrics(Map<String, Object> hMap, String rootOrg, String userId,List<String>allIds) throws IOException, Exception  {
		Map<String,Object> progressMap = progressService.metaForProgress(rootOrg, userId, allIds);
		Queue<Map<String,Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		while(!parentObjs.isEmpty()) {
			Map<String,Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenCopy = (List<Map<String, Object>>) parent.get("children");
			String identifier = (String) parent.get("identifier");
			if(progressMap.containsKey(identifier)) {
				parent.put("progress",progressMap.get(identifier));
			}
			parent.put("children", childrenCopy);
			parentObjs.addAll(childrenCopy);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void returnOnlyRootOrgMetrics(Map<String, Object> hMap,String rootOrg) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenCopy = (List<Map<String, Object>>) parent.get("children");
			Map<String,Object> viewCount = (Map<String, Object>) parent.get("viewCount");
			Map<String,Object> totalRating =(Map<String, Object>) parent.get("totalRating");
			Map<String,Object> uniqueUsersCount = (Map<String, Object>) parent.get("uniqueUsersCount");
			Map<String,Object> averageRating = (Map<String, Object>) parent.get("averageRating");
			if(viewCount!=null) {
				Number val = (Number) viewCount.getOrDefault(rootOrg,0);
				parent.put("viewCount",val );
			}
			if(totalRating !=null) {
				Number val = (Number) totalRating.getOrDefault(rootOrg, 0);
				parent.put("totalRating", val);
			}
			if(uniqueUsersCount !=null) {
				Number val = (Number) uniqueUsersCount.getOrDefault(rootOrg, 0);
				parent.put("uniqueUsersCount", val);
			}
			if(averageRating !=null) {
				Number val = (Number) averageRating.getOrDefault(rootOrg, 0);
				parent.put("averageRating", val);
			}
			parent.put("children", childrenCopy);
			parentObjs.addAll(childrenCopy);
		}
		
	}

	@SuppressWarnings({ "unchecked" })
	private void accessCheck(Map<String, Object> hMap, Map<String, Object> accessMap) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get("children");
			String identifier = (String) parent.get("identifier");
			Map<String, Object> hasAccessMap = (Map<String, Object>) accessMap.get(identifier);
			boolean hasAccess = (boolean) hasAccessMap.get("hasAccess");
			parent.put("hasAccess", hasAccess);
			parent.put("children", childrenList);
			parentObjs.addAll(childrenList);
		}
	}

//	@SuppressWarnings("unchecked")
//	private void checkExpiredDeleted(Map<String, Object> hMap) {
//		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
//		parentObjs.add(hMap);
//		List<String> filterStatuses = Arrays.asList("Expired", "Deleted");
//		while (!parentObjs.isEmpty()) {
//			Map<String, Object> parent = parentObjs.poll();
//			List<Map<String, Object>> childrenCopy = (List<Map<String, Object>>) parent.get("children");
//			String parentStatus = (String) parent.get("status");
//			if (filterStatuses.contains(parentStatus)) {
//				childrenCopy = new ArrayList<>();
//			}
//			parent.put("children", childrenCopy);
//			parentObjs.addAll(childrenCopy);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	private void filterNonLiveContents(Map<String, Object> hMap) {
//		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
//		parentObjs.add(hMap);
//		while (!parentObjs.isEmpty()) {
//			Map<String, Object> parent = parentObjs.poll();
//			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get("children");
//			List<Map<String, Object>> childrenCopy = new ArrayList<>(childrenList);
//			for (Map<String, Object> childObj : childrenList) {
//				if (!masterAllowedStatus.contains((String) childObj.get("status"))) {
//					childrenCopy.remove(childObj);
//				}
//			}
//			parent.put("children", childrenCopy);
//			parentObjs.addAll(childrenCopy);
//		}
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> getAllIds(Map<String, Object> hMap) {

		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(hMap);
		List<String> identifierSet = new ArrayList<>();
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (ArrayList) parent.getOrDefault("children",new ArrayList<>());
			identifierSet.add((String) parent.get("identifier"));
			List<Map<String, Object>> validChildren = new ArrayList<>();
			for (Map<String, Object> child : childrenList) {
				validChildren.add(child);
			}
			List<Map<String, Object>> childrenCopy = new ArrayList<>(validChildren);
			for (Map<String, Object> copyChild : childrenCopy) {
				String childId = (String) copyChild.get("identifier");
				String itIdImg = childId + ".img";
				if (identifierSet.contains(childId) && identifierSet.contains(itIdImg)) {
					// remove img node
					Map<String, Object> imgNode = new HashMap<>();
					for (Map<String, Object> child : childrenCopy) {
						if (child.get("identifier").toString().equals(itIdImg)) {
							imgNode = child;
						}
					}
					validChildren.remove(imgNode);
				}
			}
			parent.put("children", validChildren);
			parentObjs.addAll(childrenList);
		}
		return identifierSet;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map<String, Object>> getMetasHierarchy(Map<String, Object> reqMap)
			throws BadRequestException, IOException {
		String rootOrg = null;
		String org = null;
		String userId = null;
		List<String> identifiers = new ArrayList<>();
		String[] sources = null;
		List<String> fields = new ArrayList<>();
		List<Map<String, Object>> allContentMetas = new ArrayList<>();
		List<Map<String,Object>> sortedMetas =new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<>();
		boolean fieldsPassed = false;
		boolean accessCheck = false;
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		identifiers = (List<String>) reqMap.get("identifiers");
		rootOrg = (String) reqMap.get("rootOrg");
		org = (String) reqMap.get("org");
		if (reqMap.get("accessCheck") == null || reqMap.get("accessCheck").toString().isEmpty()) {
			throw new BadRequestException("Invalid request accessCheck, null or empty");
		}
		accessCheck = (boolean) reqMap.get("accessCheck");
		if (reqMap.get("accessCheck") == null) {
			accessCheck = false;
		}
		if (accessCheck) {
			userId = (String) reqMap.get("userId");
			if (userId == null || userId.isEmpty()) {
				throw new BadRequestException("Invalid request userId, null or empty");
			}
		}

		if (reqMap.get("fieldsPassed") == null || reqMap.get("fieldsPassed").toString().isEmpty()) {
			throw new BadRequestException("Invalid request fieldsPassed, null or empty");
		}

		fieldsPassed = (boolean) reqMap.get("fieldsPassed");
		if (fieldsPassed) {
			fields = (List<String>) reqMap.get("fields");

			if (fields == null || fields.isEmpty()) {
				throw new BadRequestException("Invalid request fields, null or empty");
			}
			if (!fields.contains("identifier") || !fields.contains("status")) {
				fields.add("identifier");
				fields.add("status");
			}
			try {
				sources = fields.toArray(new String[0]);
			} catch (Exception e) {
				throw new ApplicationLogicError("list parse for fields failed",e);
			}

		}
		if (rootOrg == null || rootOrg.isEmpty() || org == null || org.isEmpty() || identifiers == null
				|| identifiers.isEmpty()) {
			throw new BadRequestException("Invalid request body, [rootOrg,org,identifiers] null or empty");
		}

		allContentMetas = getMetaForLexIds(searchRequest, sources, identifiers);

		if (accessCheck == true) {
//			String preFix = accessProps.getAccessUrlPrefix();
//			String postFix = accessProps.getAccessUrlPostFix();
//			postFix = postFix.replace("@userId", userId);
//			postFix = postFix.replace("@rootOrg", rootOrg);
//			String contentIdsParam = identifiers.get(0).trim();
//			for (int i = 1; i < identifiers.size(); i++) {
//				contentIdsParam = contentIdsParam + "," + identifiers.get(i).trim();
//			}
//			postFix = postFix.replace("@ids", contentIdsParam);
//			String uri = preFix + postFix;
//			List<Map<String, Object>> metaList = new ArrayList<>();
//			Map<String, Object> result = restTemplate.getForObject(uri, HashMap.class);
//			result = (Map<String, Object>) result.get("result");
//			result = (Map<String, Object>) result.get("response");

			String urlPostFix = "/accesscontrol/users/contents?rootOrg=@rootOrg";
			String sbExtUrl = "http://" + accessProps.getSbExtIp() + ":" + accessProps.getSbExtPort() + urlPostFix;
			List<Map<String, Object>> metaList = new ArrayList<>();

			sbExtUrl = sbExtUrl.replace("@rootOrg", rootOrg);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, Object> requestMap = new HashMap<>();
			Map<String, Object> mainReqBody = new HashMap<>();
			mainReqBody.put(userId, identifiers);
			requestMap.put("request", mainReqBody);
			HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestMap, headers);
			ResponseEntity<HashMap> response = restTemplate.postForEntity(sbExtUrl, request, HashMap.class);
			Map<String, Object> responseMap = response.getBody();
			responseMap = (Map<String, Object>) responseMap.get("result");
			responseMap = (Map<String, Object>) responseMap.get("response");
			responseMap = (Map<String, Object>) responseMap.get(userId);
			if (responseMap == null || responseMap.isEmpty()) {
				throw new ApplicationLogicError("AccessControl API could not return result for :" + identifiers);
			}

			for (Map<String, Object> contentMeta : allContentMetas) {
				String id = (String) contentMeta.get("identifier");
				Map<String, Object> accessCheckMap = (Map<String, Object>) responseMap.get(id);
				boolean hasAccess = (boolean) accessCheckMap.get("hasAccess");
				contentMeta.put("hasAccess", hasAccess);
				metaList.add(contentMeta);
			}

//			return metaList;
			sortedMetas = sortInOrder(metaList,identifiers);
			resultList = filterNonLiveContents(sortedMetas);
			if (resultList == null || resultList.isEmpty()) {
				throw new BadRequestException("Passed Identifiers are not Live/Expired");
			}
			return resultList;
		}
//		return allContentMetas;
		sortedMetas = sortInOrder(allContentMetas,identifiers);
		resultList = filterNonLiveContents(sortedMetas);
		if (resultList == null || resultList.isEmpty()) {
			throw new BadRequestException("Passed Identifiers are not Live/Expired");
		}
		return resultList;
	}
	
	private List<Map<String, Object>> sortInOrder(List<Map<String, Object>> metaList, List<String> identifiers) {
		List<Map<String,Object>> sortedList = new ArrayList<>();
		for(String id : identifiers) {
			for(Map<String, Object> item : metaList) {
				String metaId = (String) item.get("identifier"); 
				if(metaId.equals(id)) {
					sortedList.add(item);
				}
			}
		}
		return sortedList;
	}

	private List<Map<String, Object>> filterNonLiveContents(List<Map<String, Object>> hMap) {
		List<Map<String, Object>> finalMetas = new ArrayList<>();
		for (Map<String, Object> mapObj : hMap) {
			String mapStatus = (String) mapObj.get("status");
			if (masterAllowedStatus.contains(mapStatus)) {
				finalMetas.add(mapObj);
			}
		}
		return finalMetas;
	}

	public List<Map<String, Object>> getMetaForLexIdsHierarchy(SearchRequest searchRequest, String[] fields,
			List<String> lexIds) throws IOException {

		List<Map<String, Object>> allMetas = new ArrayList<>();
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("identifier", lexIds))
				.must(QueryBuilders.termsQuery("status", masterAllowedStatus));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (fields != null && fields.length > 0) {
			searchSourceBuilder.fetchSource(fields, new String[] {});
		}
		searchSourceBuilder.size(lexIds.size());
		searchRequest.source(searchSourceBuilder);
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		for (SearchHit searchHit : response.getHits()) {
			Map<String, Object> source = searchHit.getSourceAsMap();
			allMetas.add(source);
		}
		return allMetas;

	}

	public List<Map<String, Object>> getMetaForLexIds(SearchRequest searchRequest, String[] fields, List<String> lexIds)
			throws IOException {

		List<Map<String, Object>> allMetas = new ArrayList<>();
		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("identifier", lexIds));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (fields != null && fields.length > 0) {
			searchSourceBuilder.fetchSource(fields, new String[] {});
		}
		searchSourceBuilder.size(lexIds.size());
		searchRequest.source(searchSourceBuilder);
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		for (SearchHit searchHit : response.getHits()) {
			Map<String, Object> source = searchHit.getSourceAsMap();
//			Map<String,Object> resultMap = new HashMap<>();
//			resultMap.put((String) source.get("identifier"), source);
			allMetas.add(source);
		}
		return allMetas;

	}

	public Map<String, Object> getMetaForLexId(SearchRequest searchRequest, String[] fields, String lexId)
			throws IOException {

		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("identifier", lexId))
				.must(QueryBuilders.termsQuery("status", masterAllowedStatus));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		if (fields != null && fields.length > 0) {
			searchSourceBuilder.fetchSource(fields, new String[] {});
		}
		searchSourceBuilder.size(1);
		searchRequest.source(searchSourceBuilder);
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		for (SearchHit searchHit : response.getHits()) {
			Map<String, Object> source = searchHit.getSourceAsMap();
			return source;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void getChildrenReasons(Map<String, Object> contentMeta,Map<String,Object> resultMap) {

		List<Map<String, Object>> objectMetas = (List<Map<String, Object>>) contentMeta.getOrDefault("children",new ArrayList<>());
		for (Map<String, Object> meta : objectMetas) {
			if(!resultMap.containsKey(meta.get("identifier"))) {
			String reason = (String) meta.get("reason");
			String addedOn = (String) meta.get("addedOn");
			List<String> childrenClassifier = (List<String>) meta.get("childrenClassifiers");
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("reason", reason);
			tempMap.put("addedOn", addedOn);
			tempMap.put("childrenClassifiers", childrenClassifier);
			resultMap.put(String.valueOf(meta.get("identifier")), tempMap);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getChildrenIds(Map<String, Object> contentMeta) {
		List<String> childrenId = new ArrayList<String>();
		List<Map<String, Object>> objectMetas = (List<Map<String, Object>>) contentMeta.getOrDefault("children",
				new ArrayList<>());
		for (Map<String, Object> meta : objectMetas) {
			childrenId.add(String.valueOf(meta.get("identifier")));
		}
		return childrenId;
	}

	public List<Map<String, Object>> getMetaForLexIds(SearchRequest searchRequest, List<String> lexIds)
			throws IOException {
		BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("identifier", lexIds));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);

		searchSourceBuilder.size(lexIds.size());
		searchRequest.source(searchSourceBuilder);
		SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
		if (response.getHits().totalHits == 0) {
			return null;
		}
		List<Map<String, Object>> allMetas = new ArrayList<>();
		for (SearchHit searchHit : response.getHits()) {
			Map<String, Object> source = searchHit.getSourceAsMap();
			allMetas.add(source);
		}
		return allMetas;

	}

	@SuppressWarnings("unchecked")
	public static List<String> getChildrenIds(List<Map<String, Object>> contentMetas) {
		List<String> childrenId = new ArrayList<String>();
		for (Map<String, Object> contentMeta : contentMetas) {
			List<Map<String, Object>> objectMetas = (List<Map<String, Object>>) contentMeta.get("children");
			if (objectMetas == null) {

			}
			for (Map<String, Object> meta : objectMetas) {
				childrenId.add(String.valueOf(meta.get("identifier")));
			}
		}
		return childrenId;
	}
	
	
	@SuppressWarnings("null")
	public Map<String,Object> HierarchyAPI(String identifier,String[] fields) throws IOException 
	{
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		// gives metadata of passed resourceId only
		Map<String, Object> parentMeta = getMetaForLexId(searchRequest, fields, identifier);
		if (parentMeta == null || parentMeta.isEmpty()) {
			throw new ResourceNotFoundException("Requested meta is not available");
		}		
		
		Map<String, Object> reasons = new HashMap<>();
		// returns all immediate children of passed Identifier
		getChildrenReasons(parentMeta,reasons);
		List<String> children = getChildrenIds(parentMeta);
		
		if(children!=null||!children.isEmpty()) {
			List<Map<String,Object>> childrenMetaData = new ArrayList<>();
			List<Map<String, Object>> childrenMeta = getMetaForLexIdsHierarchy(searchRequest, fields, children);
			for(Map<String, Object> childMeta:childrenMeta) {
				childMeta.remove("children");
				childrenMetaData.add(childMeta);
			}
			createChildHierarchy(Arrays.asList(parentMeta), childrenMeta, reasons);
		}
		return parentMeta;
	}

	@SuppressWarnings({ "unused" })
	public Map<String, Object> HierarchyApi(String resourceId, String[] fields) throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("mlsearch_*");
		searchRequest.types("searchresources");

		// gives metadata of passed resourceId only
		Map<String, Object> parentMeta = getMetaForLexId(searchRequest, fields, resourceId);
		if (parentMeta == null || parentMeta.isEmpty()) {
			throw new ResourceNotFoundException("Requested meta is not available");
		}		
		
		Map<String, Object> reasons = new HashMap<>();
		Map<String,Object> childrenClassifiers = new HashMap<>();
		Queue<String> metaQueue = new LinkedList<String>();
		// returns all immediate children of passed Identifier
		getChildrenReasons(parentMeta,reasons);
		List<String> children = getChildrenIds(parentMeta);
		metaQueue.addAll(getChildrenIds(parentMeta));

		List<Map<String, Object>> parentMetas = new ArrayList<Map<String, Object>>(Arrays.asList(parentMeta));
		// iterating on all immediate children
		while (!metaQueue.isEmpty()) {
			List<String> idsToFetch = new ArrayList<String>();
			while (!metaQueue.isEmpty()) {
				metaQueue.size();
				// all immediate children added to new list
				idsToFetch.add(metaQueue.poll());
			}

			// all immediate children are added to this list
			List<Map<String, Object>> childrenMeta = getMetaForLexIdsHierarchy(searchRequest, fields, idsToFetch);
			
			for (Map<String, Object> childMeta : childrenMeta) {
				getChildrenReasons(childMeta,reasons);
			}

			// should be enclosed in if for single level fetch
			// adding all children of above immediate children
			metaQueue.addAll(getChildrenIds(childrenMeta));
			// hierarchial structure formed upto 1st level
			createChildHierarchy(parentMetas, childrenMeta, reasons);
			parentMetas = childrenMeta;
		}
		return parentMeta;
	}

	@SuppressWarnings("unchecked")
	public void createChildHierarchy(List<Map<String, Object>> parentMetas, List<Map<String, Object>> childrenMeta,
			Map<String, Object> reasonMap) {

		for (Map<String, Object> parentMeta : parentMetas) {
			List<Map<String, Object>> childrenHierarchy = new ArrayList<Map<String, Object>>();
			List<String> childrenId = getChildrenIds(parentMeta);
			for (String childId : childrenId) {
				for (Map<String, Object> childMeta : childrenMeta) {
					if (childMeta.get("identifier").toString().equals(childId)) {
							Map<String, Object> tempMap = (Map<String, Object>) reasonMap.get(childId);
							childMeta.put("reason", tempMap.get("reason"));
							childMeta.put("addedOn", tempMap.get("addedOn"));
							childMeta.put("childrenClassifiers", tempMap.get("childrenClassifiers"));
							childrenHierarchy.add(childMeta);
					}
				}
			}
			parentMeta.put("children", childrenHierarchy);
		}
	}
}
