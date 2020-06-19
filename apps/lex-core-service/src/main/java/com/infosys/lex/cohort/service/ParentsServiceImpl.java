/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
//substitute url based on requirement
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.sunbird.common.models.util.JsonKey;
////
//substitute url based on requirement
//substitute url based on requirement
//substitute url based on requirement
//
//@Service
//public class ParentsServiceImpl implements ParentService {
//
//	@Autowired
//	ContentService contentService;
//
//	private LexLogger logger = new LexLogger(getClass().getName());
//
//	@SuppressWarnings("unchecked")
//	private List<Map<String,Object>> parentHierarchy(String resourceId, boolean self) {
//		List<Map<String,Object>> ret = new ArrayList<>();
//		try {
//			Map<String, Object> searchData = new HashMap<>();
//			searchData.put(JsonKey.IDENTIFIER, resourceId);
//			searchData.put(JsonKey.STATUS, LexProjectUtil.Status.LIVE.getValue());
//			List<Map<String, Object>> content = contentService.searchMatchedData(
//					LexProjectUtil.EsIndex.bodhi.getIndexName(), LexProjectUtil.EsType.resource.getTypeName(),
//					searchData, null, 1);
//			if (content == null || content.isEmpty()) {
//				return ret;
//			}
//			Map<String,Object> resource = content.get(0);
//			// System.out.println("ResourceFound: " + resource.getIdentifier() + " " +
//			// resource.getContentType());
//			List<Map<String,Object>> parents = (List<Map<String, Object>>) resource.get("collections");
//			
//			if (parents == null || parents.size() == 0) {
//				// System.out.println("ParentNotFound");
//				if (!self) {
//					// System.out.println("Adding Resource");
//					ret.add(resource);
//				}
//				// System.out.println("Returning " + ret);
//				return ret;
//			} else {
//				if (!self && resource.get("contentType").toString().equals(LexProjectUtil.ContentType.course.get())) {
//					// System.out.println("Adding Course");
//					ret.add(resource);
//				}
//				// System.out.println("ParentFound");
//				for (Map<String,Object> parent : parents) {
//					ret.addAll(parentHierarchy(parent.get("identifier").toString(), false));
//				}
//				// System.out.println("Returning " + ret);
//				return ret;
//			}
//		} catch (Exception ex) {
//			logger.error(ex);
//			return ret;
//		}
//	}
//
//	private List<Map<String,Object>> courseHierarchy(String resourceId, boolean self) {
//		List<Map<String,Object>> ret = new ArrayList<>();
//		try {
//			Map<String, Object> searchData = new HashMap<>();
//			searchData.put(JsonKey.IDENTIFIER, resourceId);
//			searchData.put(JsonKey.STATUS, LexProjectUtil.Status.LIVE.getValue());
//			List<Map<String, Object>> content = contentService.searchMatchedData(
//					LexProjectUtil.EsIndex.bodhi.getIndexName(), LexProjectUtil.EsType.resource.getTypeName(),
//					searchData, null, 1);
//			if (content.isEmpty()) {
//				return ret;
//			}
//			
//			Map<String,Object> resource = content.get(0);
//
//			List<Map<String,Object>> parents = (List<Map<String, Object>>) resource.get("collections");
//			if (parents != null && parents.size() > 0) {
//				if (!resource.getContentType().equals(LexProjectUtil.ContentType.course.get())) {
//					for (ObjectMeta parent : parents) {
//						List<ContentMeta> returnedParents = courseHierarchy(parent.getIdentifier(), false);
//						ret.addAll(returnedParents);
//					}
//				}
//			}
//			if (!self) {
//				ret.add(resource);
//			}
//			return ret;
//		} catch (Exception ex) {
//			logger.error(ex);
//			return ret;
//		}
//
//	}
//
//	
//	  (non-Javadoc)
//	  
//	  @see com.infosys.core.service.ParentService#getAllParents(java.lang.String)
//	 
//	@Override
//	public Map<String, Object> getAllParents(String resourceId) {
//		Map<String, Object> parents = new HashMap<>();
//		List<ContentMeta> learningPaths = new ArrayList<>();
//		List<ContentMeta> courses = new ArrayList<>();
//		List<ContentMeta> modules = new ArrayList<>();
//
//		// System.out.println("Parent hierachy request for resource " + resourseId);
//		List<ContentMeta> ret = parentHierarchy(resourceId, true);
//		for (ContentMeta data : ret) {
//			if (data != null && data.getIdentifier() != null && data.getContentType() != null) {
//				if (data.getContentType().equals(LexProjectUtil.ContentType.collection.get())) {
//					boolean notFound = true;
//					for (ContentMeta x : modules) {
//						if (data.getIdentifier().equals(x.getIdentifier())) {
//							notFound = false;
//							break;
//						}
//					}
//					if (notFound)
//						modules.add(data);
//				} else if (data.getContentType().equals(LexProjectUtil.ContentType.course.get())) {
//					boolean notFound = true;
//					for (ContentMeta x : courses) {
//						if (data.getIdentifier().equals(x.getIdentifier())) {
//							notFound = false;
//							break;
//						}
//					}
//					if (notFound)
//						courses.add(data);
//				} else if (data.getContentType().equals(LexProjectUtil.ContentType.learningPath.get())) {
//					boolean notFound = true;
//					for (ContentMeta x : learningPaths) {
//						if (data.getIdentifier().equals(x.getIdentifier())) {
//							notFound = false;
//							break;
//						}
//					}
//					if (notFound)
//						learningPaths.add(data);
//				}
//			}
//		}
//		parents.put("learningPaths", learningPaths);
//		parents.put("courses", courses);
//		parents.put("modules", modules);
//
//		return parents;
//	}
//
//	
//	  (non-Javadoc)
//	  
//	  @see
//	  com.infosys.core.service.ParentService#getCourseParents(java.lang.String)
//	 
//	@Override
//	public Map<String, Object> getCourseParents(String resourceId) {
//		Map<String, Object> parents = new HashMap<>();
//
//		List<ContentMeta> courses = new ArrayList<>();
//		List<ContentMeta> modules = new ArrayList<>();
//
//		// System.out.println("Parent hierachy request for resource " + resourseId);
//		List<ContentMeta> ret = courseHierarchy(resourceId, true);
//		for (ContentMeta data : ret) {
//			if (data != null && data.getIdentifier() != null && data.getContentType() != null) {
//				if (data.getContentType().equals(LexProjectUtil.ContentType.collection.get())) {
//					boolean notFound = true;
//					for (ContentMeta x : modules) {
//						if (data.getIdentifier().equals(x.getIdentifier())) {
//							notFound = false;
//							break;
//						}
//					}
//					if (notFound)
//						modules.add(data);
//				} else if (data.getContentType().equals(LexProjectUtil.ContentType.course.get())) {
//					boolean notFound = true;
//					for (ContentMeta x : courses) {
//						if (data.getIdentifier().equals(x.getIdentifier())) {
//							notFound = false;
//							break;
//						}
//					}
//					if (notFound)
//						courses.add(data);
//				}
//			}
//		}
//
//		parents.put("courses", courses);
//		parents.put("modules", modules);
//
//		return parents;
//	}
//
//	
//	  (non-Javadoc)
//	  
//substitute url based on requirement
//	  String) This method returns the first parent in the collections meta-data of
//	  the resourceId
//	 
//	@Override
//	public String getCourseParent(String resourceId) throws Exception {
//		String parent = null;
//		Map<String, Object> searchData = new HashMap<>();
//		searchData.put(JsonKey.IDENTIFIER, resourceId);
//		searchData.put(JsonKey.STATUS, LexProjectUtil.Status.LIVE.getValue());
//		List<Map<String, Object>> content = contentService.searchMatchedData(
//				LexProjectUtil.EsIndex.bodhi.getIndexName(), LexProjectUtil.EsType.resource.getTypeName(), searchData,
//				null, 1);
//		if (content.isEmpty()) {
//			return parent;
//		}
//		Map<String,Object> resource = content.get(0);
//
////		ObjectMeta[] parents = resource.getCollections();
////		if (parents != null && parents.length > 0) {
////			parent = parents[0].getIdentifier();
////		}
//		return parent;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Map<String, Object> getCourseHierarchyForResource(String resourceId)throws Exception {
//		Map<String, Object> ret = new HashMap<>();	
//		
//		//This done so that duplicates are not searched again
//		Set<String> parentSet = new HashSet<String>(); 
//		//initialize with resourceId of which parents are searched
//		parentSet.add(resourceId);
//		
//		//amongst the searched contents modules and courses are segregated
//		Set<String> modules = new HashSet<String>();
//		Set<String> courses = new HashSet<String>();
//		// a set of ids to get meta for
//		List<String> searchSet = new ArrayList<String>();
//
//		// adding the initial resource
//		searchSet.add(resourceId);
//		
//
//		while (!searchSet.isEmpty()) {
//			List<String> nextSearchSet = new ArrayList<String>();
//			List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(
//					searchSet,
//					new String[] { "identifier", "collections.identifier","contentType" });
//
//			for (Map<String, Object> source : searchHits) {
//				if(source.containsKey("contentType") && source.get("contentType")!=null)
//					if(source.get("contentType").toString().equals(LexProjectUtil.ContentType.collection.get()))
//						modules.add(source.get("identifier").toString());
//					else if(source.get("contentType").toString().equals(LexProjectUtil.ContentType.course.get()))
//						courses.add(source.get("identifier").toString());
//				
//				if (source.containsKey("collections") && source.get("collection")!=null) {
//					for (Map<String, Object> parent : ((List<Map<String, Object>>) source.get("collections"))) {
//						if(parentSet.add(parent.get("identifier").toString()))
//							nextSearchSet.add(parent.get("identifier").toString());
//					}
//				}
// 
//			}
//			searchSet = nextSearchSet;
//		}
//		ret.put("courses", new ArrayList<String>(courses));
//		ret.put("modules", new ArrayList<String>(modules));
//		return ret;
//	}
//}
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
package com.infosys.lex.cohort.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.infosys.lex.common.constants.JsonKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.cohort.constant.LexProjectUtil;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.util.ContentMetaConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.logger.LexLogger;

@Service
public class ParentsServiceImpl implements ParentService {

	@Autowired
	ContentService contentService;

	private LexLogger logger = new LexLogger(getClass().getName());

	@Override
	public String getCourseParent(String resourceId) throws Exception {
		String parent = null;
		Map<String, Object> searchData = new HashMap<>();
		searchData.put(JsonKey.IDENTIFIER, resourceId);
		searchData.put(JsonKey.STATUS, LexProjectUtil.Status.LIVE.getValue());
		List<Map<String, Object>> content = contentService.searchMatchedData(
				LexProjectUtil.EsIndex.bodhi.getIndexName(), LexProjectUtil.EsType.resource.getTypeName(), searchData,
				null, 1);
		if (content.isEmpty()) {
			return parent;
		}
		Map<String,Object> resource = content.get(0);

	/*	ObjectMeta[] parents = resource.getCollections();
		if (parents != null && parents.length > 0) {
			parent = parents[0].getIdentifier();
		}*/
		List<Map<String,Object>> parents = (List<Map<String, Object>>) resource.get("collections");
		if (parents != null && parents.size() > 0) {
			parent = parents.get(0).toString();
		}
		return parent;
	}


	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> parentHierarchy(String resourceId, boolean self) {
		List<Map<String, Object>> ret = new ArrayList<>();
		try {
			List<Map<String, Object>> content = contentService.getMetaByIDListandSource(
					Arrays.asList(new String[] { resourceId }), null, LexProjectUtil.Status.LIVE.getValue());
			if (content == null || content.isEmpty()) {
				return ret;
			}
			Map<String, Object> resource = content.get(0);
			// System.out.println("ResourceFound: " + resource.getIdentifier() + " " +
			// resource.getContentType());
			List<Map<String, Object>> parents = (List<Map<String, Object>>) resource.get("collections");
			if (parents == null || parents.size() == 0) {
				// System.out.println("ParentNotFound");
				if (!self) {
					// System.out.println("Adding Resource");
					ret.add(resource);
				}
				// System.out.println("Returning " + ret);
				return ret;
			} else {
				if (!self && resource.get("contentType").toString().equals(LexProjectUtil.ContentType.course.get())) {
					// System.out.println("Adding Course");
					ret.add(resource);
				}
				// System.out.println("ParentFound");
				for (Map<String, Object> parent : parents) {
					ret.addAll(parentHierarchy(parent.get(ContentMetaConstants.IDENTIFIER).toString(), false));
				}
				// System.out.println("Returning " + ret);
				return ret;
			}
		} catch (Exception ex) {
			logger.error(ex);
			return ret;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.ParentService#getAllParents(java.lang.String)
	 */
	@Override
	public Map<String, Object> getAllParents(String resourceId) {
		Map<String, Object> parents = new HashMap<>();
		List<Map<String, Object>> learningPaths = new ArrayList<>();
		List<Map<String, Object>> courses = new ArrayList<>();
		List<Map<String, Object>> modules = new ArrayList<>();

		// System.out.println("Parent hierachy request for resource " + resourseId);
		List<Map<String, Object>> ret = parentHierarchy(resourceId, true);
		for (Map<String, Object> data : ret) {
			if (data != null && data.get(ContentMetaConstants.IDENTIFIER) != null && data.get("contentType") != null) {
				if (data.get("contentType").toString().equals(LexProjectUtil.ContentType.collection.get())) {
					boolean notFound = true;
					for (Map<String, Object> x : modules) {
						if (data.get(ContentMetaConstants.IDENTIFIER).toString()
								.equals(x.get(ContentMetaConstants.IDENTIFIER).toString())) {
							notFound = false;
							break;
						}
					}
					if (notFound)
						modules.add(data);
				} else if (data.get("contentType").toString().equals(LexProjectUtil.ContentType.course.get())) {
					boolean notFound = true;
					for (Map<String, Object> x : courses) {
						if (data.get(ContentMetaConstants.IDENTIFIER).toString()
								.equals(x.get(ContentMetaConstants.IDENTIFIER).toString())) {
							notFound = false;
							break;
						}
					}
					if (notFound)
						courses.add(data);
				} else if (data.get("contentType").toString().equals(LexProjectUtil.ContentType.learningPath.get())) {
					boolean notFound = true;
					for (Map<String, Object> x : learningPaths) {
						if (data.get(ContentMetaConstants.IDENTIFIER).equals(x.get(ContentMetaConstants.IDENTIFIER))) {
							notFound = false;
							break;
						}
					}
					if (notFound)
						learningPaths.add(data);
				}
			}
		}
		parents.put("learningPaths", learningPaths);
		parents.put("courses", courses);
		parents.put("modules", modules);

		return parents;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCourseHierarchyForResource(String resourceId) throws Exception {
		Map<String, Object> ret = new HashMap<>();

		// This done so that duplicates are not searched again
		Set<String> parentSet = new HashSet<String>();
		// initialize with resourceId of which parents are searched
		parentSet.add(resourceId);

		// amongst the searched contents modules and courses are segregated
		Set<String> modules = new HashSet<String>();
		Set<String> courses = new HashSet<String>();
		// a set of ids to get meta for
		List<String> searchSet = new ArrayList<String>();

		// adding the initial resource
		searchSet.add(resourceId);

		while (!searchSet.isEmpty()) {
			List<String> nextSearchSet = new ArrayList<String>();

			Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(searchSet,
					new HashSet<>(Arrays.asList("identifier", "collections.identifier", "contentType")));
			Map<String, Object> sourceMap;

			for (String contentId : contentMetaMap.keySet()) {
				sourceMap = contentMetaMap.get(contentId);
				if (sourceMap.containsKey("contentType") && sourceMap.get("contentType") != null)
					if (sourceMap.get("contentType").toString().equals(LexProjectUtil.ContentType.collection.get()))
						modules.add(sourceMap.get("identifier").toString());
					else if (sourceMap.get("contentType").toString().equals(LexProjectUtil.ContentType.course.get()))
						courses.add(sourceMap.get("identifier").toString());

				if (sourceMap.containsKey("collections") && sourceMap.get("collections") != null) {
					for (Map<String, Object> parent : ((List<Map<String, Object>>) sourceMap.get("collections"))) {
						if (parentSet.add(parent.get("identifier").toString()))
							nextSearchSet.add(parent.get("identifier").toString());
					}
				}

			}
			searchSet = nextSearchSet;
		}
		ret.put("courses", new ArrayList<String>(courses));
		ret.put("modules", new ArrayList<String>(modules));
		return ret;
	}

	/**
	 * This method fetches the assessments for given children List. The children
	 * list passed is of Learning path the assessments amongst the childrenList.
	 */
	@Override
	public List<String> getAssessmentsFromContentList(List<String> contentList) throws IOException {
		List<String> assessmentIds = new ArrayList<String>();
		try {

			Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
					contentList, new HashSet<>(Arrays.asList("identifier", "contentType", "resourceType")));
			Map<String, Object> sourceMap;

			for (String contentId : contentMetaMap.keySet()) {
				sourceMap = contentMetaMap.get(contentId);

				if ("resource".equalsIgnoreCase((String) sourceMap.get("contentType"))
						&& sourceMap.containsKey("resourceType")
						&& "assessment".equalsIgnoreCase((String) sourceMap.get("resourceType"))) {
					assessmentIds.add(sourceMap.get("identifier").toString());
				}

			}
		} catch (Exception ex) {
			throw new ApplicationLogicError("Error Fetching  Assessment. Error " + ex.getMessage(),ex);
		}
		return assessmentIds;
	}

	/**
	 * This method fetches the assessments for given content List. The children list
	 * is passed in case of Learning path and parent List in case of resources the
	 * assessments amongst the children of the given content list is returned
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAssessmentsFromChildrenOfContentList(List<String> contentList) throws IOException {
		List<String> assessmentIds = new ArrayList<String>();
		try {

			Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
					contentList, new HashSet<>(Arrays.asList("identifier", "children.identifier", "contentType")));
			Map<String, Object> sourceMap;
			// Here theCourse or module children is
			// stored from which assessments are separated
			contentList = new ArrayList<String>();
			for (String contentId : contentMetaMap.keySet()) {
				sourceMap = contentMetaMap.get(contentId);
				if (sourceMap.containsKey("children") && sourceMap.get("children") != null
						&& sourceMap.get("children") instanceof List) {
					for (Map<String, Object> childrenMap : (List<Map<String, Object>>) sourceMap.get("children")) {
						contentList.add(childrenMap.get("identifier").toString());
					}
				}
			}

			contentMetaMap = contentService.filterAndFetchContentMetaToShow(contentList,
					new HashSet<>(Arrays.asList("identifier", "contentType", "resourceType")));
			for (String contentId : contentMetaMap.keySet()) {
				sourceMap = contentMetaMap.get(contentId);
				if ("resource".equalsIgnoreCase((String) sourceMap.get("contentType"))
						&& sourceMap.containsKey("resourceType")
						&& "assessment".equalsIgnoreCase((String) sourceMap.get("resourceType"))) {
					assessmentIds.add(sourceMap.get("identifier").toString());
				}
			}
		} catch (Exception ex) {
			throw new ApplicationLogicError("Error Fetching child Assessment. Error " + ex.getMessage(),ex);
		}
		return assessmentIds;
	}

}
