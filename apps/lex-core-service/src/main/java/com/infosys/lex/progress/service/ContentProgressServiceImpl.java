/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
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
package com.infosys.lex.progress.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.sunbird.repo.UserMVRepository;
import com.infosys.lex.common.sunbird.repo.UserRepository;
import com.infosys.lex.contentsource.postgres.projection.ContentSourceProj;
import com.infosys.lex.contentsource.service.ContentSourceService;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.core.logger.LexLogger;
import com.infosys.lex.progress.bodhi.repo.*;
import com.infosys.lex.progress.dto.AssessmentRecalculateDTO;
import com.infosys.lex.progress.dto.ContentProgressDTO;
import com.infosys.lex.progress.dto.ExternalProgressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentProgressServiceImpl implements ContentProgressService {

	@Autowired
	ContentProgressRepository contentProgressRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	UserMVRepository userMVRepo;

	@Autowired
	ContentService contentService;

	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	DiscretePointsService discretePointService;

	@Autowired
	ContentSourceService contentSourceService;

	@Autowired
    private MandatoryContentRepository mandatoryContentRepository;

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private LexLogger logger = new LexLogger(getClass().getName());

	public static final String PROGRESS_CONSTANT = "progress";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.core.service.ContentProgressService#callProgress(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.Double)
	 */
	@Async("progressExecutor")
	@Override
	public void callProgress(String rootOrg, String email, String contentId, String mimeType, Float result)
			throws Exception {
		ContentProgressDTO progressData = new ContentProgressDTO();
		progressData.setMax_size(100f);
		progressData.setCurrent(new ArrayList<Float>(Arrays.asList(new Float[] { result })));
		progressData.setMime_type(mimeType);
		progressData.setContent_type("Resource");
		progressData.setUser_id(email);
		progressData.setResource_id(contentId);
		progressData.setMarkAsComplete(false);
		progressData.setRoot_org(rootOrg);
		this.updateProgress(progressData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.ContentProgressService#updateProgress(java.lang.
	 * String, java.lang.String, java.util.Map)pro
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateProgress(ContentProgressDTO resourceInfo) throws Exception {

		// TODO
		String userId = resourceInfo.getUser_id();
		String contentId = resourceInfo.getResource_id();
		Boolean markAsRead = resourceInfo.getMarkAsComplete();
		String rootOrg = resourceInfo.getRoot_org();
		
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		Boolean updateOnlyLastAccessedOn = false;
		Boolean completedAssessment = false;

		if (markAsRead) {
			Map<String, Object> showProgressMeta = metaForProgress(rootOrg, userId,
					Arrays.asList(new String[] { contentId }));

			Map<String, Object> tempObj = (Map<String, Object>) showProgressMeta.get(contentId);
			Boolean showProgress = (Boolean) tempObj.get("showMarkAsComplete");
			if (showProgress)
				resourceInfo = this.markedReadPreprocess(rootOrg, resourceInfo);
			else
				throw new Exception("markAsRead not available for this content");
		}

		Map<String, ContentProgressModel> meta = new HashMap<>();
		Map<String, ContentProgress> contentProgressMap = new HashMap<>();

		// meta for the resource(1 es call 1 cas call)
		Map<String, Boolean> flags = this.getMetaAndProgressForResource(rootOrg, userId, contentId, resourceInfo, meta,
				contentProgressMap, markAsRead);
		updateOnlyLastAccessedOn = flags.get("update_Only_last_accessed");
		completedAssessment = flags.get("completed_assessment");

		// meta for the hierarchy(es calls=Level number-1 and 1 cas call)

		List<String> missingIds = this.getMetaAndProgressForHierarchy(rootOrg, meta, contentProgressMap, userId,
				contentId, updateOnlyLastAccessedOn, completedAssessment, flags.get("exercise_with_feedback"), false);

		// logging missing ids
		if (missingIds.size() > 0) {
			resourceInfo.setMissing_ids(missingIds);
			resourceInfo.setUser_id(userId);
			resourceInfo.setResource_id(contentId);
			logger.error(new Exception(
					new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(resourceInfo)));
		}

		try {
			ContentProgress cProgress = (ContentProgress) contentProgressMap.get(contentId);
			ContentProgressModel cpm = (ContentProgressModel) meta.get(contentId);
			cpm.setTimespent(cProgress.getDuration() * cProgress.getProgress());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// updating in the db
		contentProgressRepo.updateProgress(meta.values());
		return "Success";
	}

	private ContentProgressDTO markedReadPreprocess(String rootOrg, ContentProgressDTO resourceInfo) {

		Map<String, String> mimes = contentService.getMimeTypes();
		String mimeType = resourceInfo.getMime_type().toLowerCase();

		List<Float> current = new ArrayList<>();

		// page based
		if (mimes.get("page").contains(mimeType)) {
			for (float i = 1; i <= resourceInfo.getMax_size(); i++)
				current.add(i);
		}
		// time and result based
		else {
			current.add(resourceInfo.getMax_size());
		}

		resourceInfo.setCurrent(current);

		return resourceInfo;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Boolean> getMetaAndProgressForResource(String rootOrg, String userUUID, String contentId,
			ContentProgressDTO resourceInfo, Map<String, ContentProgressModel> meta,
			Map<String, ContentProgress> contentProgressMap, Boolean markAsRead) throws Exception {

		Boolean completedAssessment = false;
		Boolean updateOnlyLastAccessedOn = false;
		Boolean exerciseWithFeedback = false;

		// get meta for the resource
		Map<String, Boolean> ret = new HashMap<>();
		Map<String, Object> temp = this.getMetaForResource(rootOrg, contentId, userUUID);

		// get the progress of the resource
		ContentProgressModel resourceProgress = contentProgressRepo.findById(
				new ContentProgressPrimaryKeyModel(rootOrg, userUUID, resourceInfo.getContent_type(), contentId))
				.orElse(null);

		// add the resource to the map with new progress to be updated in cassandra
		meta.putAll((Map<String, ContentProgressModel>) temp.get("meta"));
		float oldProgress = 0f;

		float maxSize = resourceInfo.getMax_size();
		List<Float> current = resourceInfo.getCurrent() == null ? new ArrayList<Float>(0)
				: resourceInfo.getCurrent().size() == 0 ? new ArrayList<Float>(0) : resourceInfo.getCurrent();
		String mimeType = resourceInfo.getMime_type().toLowerCase();
		Set<Float> progressPages = new HashSet<>();
		if (resourceProgress != null) {
			oldProgress = resourceProgress.getProgress();
			progressPages = resourceProgress.getVisitedSet() != null ? resourceProgress.getVisitedSet()
					: new HashSet<>();
		} else {
			meta.get(contentId).setFirstAccessedOn(new Date());
		}
		if (markAsRead) {
			oldProgress = 1f;
			meta.get(contentId).setLastTS(new Date());
			meta.get(contentId).setDateUpdated(format.parse(format.format(new Date())));
			meta.get(contentId).setFirstCompletedOn(new Date());
			meta.get(contentId).setUpdatedBy("marked_read");

		}

		Map<String, String> mimes = contentService.getMimeTypes();

		if (oldProgress != 1f) {
			Boolean overrideProgress = true;
			float newProgress = oldProgress;
			// page based
			if (mimes.get("page").contains(mimeType)) {
				progressPages.addAll(current);
				newProgress = ((float) progressPages.size()) / maxSize;
			}
			// time based
			else if (mimes.get("time").contains(mimeType)) {
				float previousTime = !progressPages.isEmpty() ? progressPages.toArray(new Float[1])[0] : 0;

				// create old duration
				float oldMax = (previousTime) / oldProgress;

				if (Math.abs(oldMax - maxSize) < 10.0) {
					if (current.get(0) > previousTime) {
						progressPages = new HashSet<>();
						progressPages.addAll(current);
						newProgress = current.get(0) / maxSize;
					}
				} else {
					progressPages = new HashSet<>();
					progressPages.addAll(current);
					newProgress = current.get(0) / maxSize;
					overrideProgress = false;
				}
				if (newProgress >= 0.95)
					newProgress = 1;
			}
			// result based
			else if (mimes.get("result").contains(mimeType)) {
				float previousResult = !progressPages.isEmpty() ? progressPages.toArray(new Float[1])[0] : 0;
				if (current.get(0) > previousResult || current.get(0) == -1) {
					progressPages = new HashSet<>();
					progressPages.addAll(current);
					if (temp.get("resource_type").toString().toLowerCase().equals("exercise")) {
						if (current.get(0) == -1) {
							exerciseWithFeedback = true;
							newProgress = 100;
						} else {
							if ((current.get(0) / maxSize) == 1)
								newProgress = 1;
							else
								newProgress = 0;
						}
					} else {
						if (current.get(0) >= 60) {
							newProgress = 1;
							if (temp.get("resource_type").toString().toLowerCase().equals("assessment"))
								completedAssessment = true;
						}
					}
				}
			}

			if (overrideProgress && resourceProgress != null && (newProgress <= oldProgress)) {
				newProgress = oldProgress;
				updateOnlyLastAccessedOn = true;
			}
			if (newProgress >= 1f) {
				newProgress = 1;
				meta.get(contentId).setFirstCompletedOn(new Date());
			}
			meta.get(contentId).setProgress(newProgress);
			if (!updateOnlyLastAccessedOn) {
				meta.get(contentId).setLastTS(new Date());
				meta.get(contentId).setDateUpdated(format.parse(format.format(new Date())));
			}
		} else {
			meta.get(contentId).setProgress(oldProgress);
			if (!markAsRead)
				updateOnlyLastAccessedOn = true;
		}
		meta.get(contentId).setLastAccessedOn(new Date());
		meta.get(contentId).setVisitedSet(progressPages);
		contentProgressMap.put(contentId, new ContentProgress(temp.get("resource_type").toString(),
				meta.get(contentId).getProgress(), Long.parseLong(temp.get("duration").toString())));

		ret.put("completed_assessment", completedAssessment);
		ret.put("update_Only_last_accessed", updateOnlyLastAccessedOn);
		ret.put("exercise_with_feedback", exerciseWithFeedback);

		return ret;
	}

	// get all required meta for resource and validates if the id exists
	@SuppressWarnings("unchecked")
	private Map<String, Object> getMetaForResource(String rootOrg, String contentId, String userUUID) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		Map<String, ContentProgressModel> contentDataMap = new HashMap<>();
		List<String> parentContentType = new ArrayList<>();
		parentContentType.add("Collection");
		parentContentType.add("Course");
		parentContentType.add("Learning Path");
//		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(
//				Arrays.asList(new String[] { contentId }),
//				new String[] { "identifier", "collections.identifier", "resourceType", "contentType", "duration" },
//				"Live");
		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandStatusList(
				Arrays.asList(new String[] { contentId }),
				new String[] { "identifier", "collections.identifier", "resourceType", "contentType", "duration"},
				(new String[] { "Live", "Marked For Deletion", "Deleted", "Expired", "Unpublished" }));
		if (searchHits.size() != 0) {
			Map<String, Object> source = searchHits.get(0);
			ContentProgressPrimaryKeyModel primaryKey = new ContentProgressPrimaryKeyModel(rootOrg, userUUID,
					source.get("contentType").toString(), source.get("identifier").toString());
			ContentProgressModel contentData = new ContentProgressModel(primaryKey);
			List<String> parentList = new ArrayList<>();
			if (source.containsKey("collections")) {

				Set<String> idMetaList = new HashSet<String>();
				List<String> liveIdList = new ArrayList<>();
				for (Map<String, Object> parent : ((List<Map<String, Object>>) source.get("collections"))) {
					idMetaList.add(parent.get("identifier").toString());
				}
//				List<Map<String, Object>> metaHits = contentService.getMetaByIDListandSource(
//						new ArrayList<String>(idMetaList), new String[] { "identifier", "contentType" }, "Live");
				List<Map<String, Object>> metaHits = contentService.getMetaByIDListandStatusList(
						new ArrayList<String>(idMetaList), new String[] { "identifier", "contentType" }, 
						(new String[] { "Live", "Marked For Deletion", "Deleted", "Expired", "Unpublished" }));
				for (Map<String, Object> sourceData : metaHits) {
					if (parentContentType.contains(sourceData.get("contentType").toString())) {
						liveIdList.add(sourceData.get("identifier").toString());
//						System.out.println(sourceData.get("identifier").toString());
					}
				}

				for (Map<String, Object> parent : ((List<Map<String, Object>>) source.get("collections"))) {
					if (liveIdList.contains(parent.get("identifier").toString()))
						parentList.add(parent.get("identifier").toString());
				}
			}
			contentData.setParentList(parentList);
			contentDataMap.put(source.get("identifier").toString(), contentData);
			ret.put("duration", source.get("duration").toString());
			ret.put("resource_type", source.getOrDefault("resourceType", "").toString());
		} else
			throw new InvalidDataInputException("invalid.resource");
		ret.put("meta", contentDataMap);
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<String> getMetaAndProgressForHierarchy(String rootOrg, Map<String, ContentProgressModel> meta,
			Map<String, ContentProgress> contentProgressMap, String userUUID, String contentId,
			Boolean updateOnlyLastAccessedOn, Boolean completedAssessment, Boolean exerciseWithFeedback,
			Boolean recalculate) throws Exception {

		// meta for the hierarchy
		Map<String, Object> hierarchy = getHierarchyForResource(rootOrg, meta.get(contentId).getParentList(), userUUID);
		meta.putAll((Map<String, ContentProgressModel>) hierarchy.get("meta"));

		List<String> contentIds = new ArrayList<String>((Set<String>) hierarchy.get("progress_id_set"));
//		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(contentIds,
//				new String[] { "identifier", "resourceType", "duration" }, "Live");
		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandStatusList(
				contentIds, 
				new String[] { "identifier", "resourceType", "duration" }, 
				(new String[] { "Live", "Marked For Deletion", "Deleted", "Expired", "Unpublished" }));
		for (Map<String, Object> source : searchHits) {
			if (!contentProgressMap.containsKey(source.get("identifier").toString()))
				contentProgressMap.put(source.get("identifier").toString(),
						new ContentProgress(source.getOrDefault("resourceType", "").toString(), -1,
								Long.parseLong(source.get("duration").toString())));
		}
		
//		System.out.println(contentProgressMap);
		// content progress
		List<ContentProgressModel> contentProgressList = contentProgressRepo.getProgress(rootOrg, userUUID,
				new ArrayList<String>((Set<String>) hierarchy.get("content_type_set")), contentIds);
//		System.out.println(contentProgressList);
		for (ContentProgressModel cpm : contentProgressList) {
//			System.out.println(cpm);
			contentProgressMap.get(cpm.getPrimaryKey().getContentId()).setProgress(cpm.getProgress());
		}

		if (!updateOnlyLastAccessedOn)
			discretePointService.PutPoints(rootOrg, userUUID, contentId,
					contentProgressMap.get(contentId).getResourceType(),
					hierarchy.containsKey("parent") ? hierarchy.get("parent").toString() : "", exerciseWithFeedback);

		return this.updateLatestProgressInMeta(meta, contentProgressMap, contentId, updateOnlyLastAccessedOn,
				completedAssessment,
				hierarchy.containsKey("parent") ? hierarchy.get("parent").toString().toLowerCase() : "", recalculate);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getHierarchyForResource(String rootOrg, List<String> contentParentList, String userUUID)
			throws Exception {
		Map<String, Object> ret = new HashMap<>();

		Boolean fetchParent = false;
		// content meta to be updates
		// a set for content types for the query
		Map<String, ContentProgressModel> contentDataMap = new HashMap<>();
		// set of ids for fetching cassandra progress
		Set<String> contentIDSet = new HashSet<String>();
		// a set to avoid repeated searches
		Set<String> parentIDSet = new HashSet<String>();
		Set<String> contentTypeSet = new HashSet<String>();
		contentTypeSet.add("Resource");
		// a set of ids to get meta for
		Set<String> searchSet = new HashSet<String>();

		List<String> allContentType = new ArrayList<>();
		allContentType.add("Collection");
		allContentType.add("Course");
		allContentType.add("Learning Path");
		allContentType.add("Resource");
		allContentType.add("Knowledge Artifact");

		// adding parents for the resource
		searchSet.addAll(contentParentList);
		contentIDSet.addAll(contentParentList);
		parentIDSet.addAll(contentParentList);

		while (!searchSet.isEmpty()) {
			Set<String> nextSearchSet = new HashSet<String>();
//			List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(
//					new ArrayList<String>(searchSet),
//					new String[] { "identifier", "collections.identifier", "children.identifier", "contentType" },
//					"Live");
			List<Map<String, Object>> searchHits = contentService.getMetaByIDListandStatusList(
					new ArrayList<String>(searchSet), 
					new String[] { "identifier", "collections.identifier", "children.identifier", "contentType"},
					(new String[] { "Live", "Marked For Deletion", "Deleted", "Expired", "Unpublished" }));

			Set<String> idMetaList = new HashSet<String>();
			List<String> liveIdList = new ArrayList<>();
			for (Map<String, Object> source : searchHits) {

				if (source.containsKey("collections")) {
					for (Map<String, Object> parent : ((List<Map<String, Object>>) source.get("collections"))) {
						idMetaList.add(parent.get("identifier").toString());

					}
				}
				if (source.containsKey("children")) {

					for (Map<String, Object> child : ((List<Map<String, Object>>) source.get("children"))) {
						idMetaList.add(child.get("identifier").toString());
					}
				}
			}
//			List<Map<String, Object>> metaHits = contentService.getMetaByIDListandSource(
//					new ArrayList<String>(idMetaList), new String[] { "identifier", "contentType" }, "Live");
			List<Map<String, Object>> metaHits = contentService.getMetaByIDListandStatusList(
					new ArrayList<String>(idMetaList), 
					new String[] { "identifier", "contentType" }, 
					(new String[] { "Live", "Marked For Deletion", "Deleted", "Expired", "Unpublished" }));
			for (Map<String, Object> sourceData : metaHits) {
				if (allContentType.contains(sourceData.get("contentType").toString()))
					liveIdList.add(sourceData.get("identifier").toString());
			}

			for (Map<String, Object> source : searchHits) {
				ContentProgressPrimaryKeyModel primaryKey = new ContentProgressPrimaryKeyModel(rootOrg, userUUID,
						source.get("contentType").toString(), source.get("identifier").toString());
				ContentProgressModel contentData = new ContentProgressModel(primaryKey);

				contentTypeSet.add(source.get("contentType").toString());

				List<String> childrenList = new ArrayList<>();
//				List<String> childrenListTemp = new ArrayList<>();
				if (source.containsKey("children")) {

					for (Map<String, Object> child : ((List<Map<String, Object>>) source.get("children"))) {
						if (liveIdList.contains(child.get("identifier")))
							childrenList.add(child.get("identifier").toString());
					}

				}
				contentData.setChildrenList(childrenList);
				contentIDSet.addAll(childrenList);

				List<String> parentList = new ArrayList<>();
//				List<String> parentListTemp = new ArrayList<>();
				if (source.containsKey("collections")) {
					for (Map<String, Object> parent : ((List<Map<String, Object>>) source.get("collections"))) {
						if (liveIdList.contains(parent.get("identifier"))) {
							parentList.add(parent.get("identifier").toString());
							if (!parentIDSet.contains(parent.get("identifier").toString()))
								nextSearchSet.add(parent.get("identifier").toString());
						}
					}

				}
				contentData.setParentList(parentList);
				contentIDSet.addAll(parentList);
				parentIDSet.addAll(parentList);

				if (!fetchParent) {
					if (!ret.containsKey("parent"))
						ret.put("parent", source.get("contentType").toString());
					if (ret.get("parent").toString().toLowerCase().equals("collection")
							&& source.get("contentType").toString().toLowerCase().equals("course"))
						ret.put("parent", source.get("contentType").toString());
				}
				// added meta
				contentDataMap.put(source.get("identifier").toString(), contentData);
			}
			// set false to find the parent for points at the first level only
			fetchParent = true;
			searchSet = nextSearchSet;
		}
		ret.put("meta", contentDataMap);
		ret.put("progress_id_set", contentIDSet);
		ret.put("content_type_set", contentTypeSet);
		return ret;
	}

	private List<String> updateLatestProgressInMeta(Map<String, ContentProgressModel> meta,
			Map<String, ContentProgress> contentProgressMap, String contentId, Boolean updateOnlyLastAccessedOn,
			Boolean completedAssessment, String parentContentType, Boolean recalculate) throws Exception {

		List<String> missingIds = new ArrayList<>();
		List<String> updateProgressList = new ArrayList<>();
		updateProgressList.addAll(meta.get(contentId).getParentList());

		// update progress for the parents of the resource
		while (!updateProgressList.isEmpty()) {
			List<String> nextProgressList = new ArrayList<String>();
			for (String id : updateProgressList) {
				float oldProgress = 0f;
				if (meta.containsKey(id)) {
					if (contentProgressMap.get(id) != null) {
						if (contentProgressMap.get(id).getProgress() != -1)
							oldProgress = contentProgressMap.get(id).getProgress();
						else {
							meta.get(id).setFirstAccessedOn(new Date());
							meta.get(id).setLastAccessedOn(new Date());
						}
						if (recalculate) {
							float newProgress = oldProgress;
							if (completedAssessment) {
								newProgress = 1;
							} else {
								if (meta.get(id).getProgress() == null)
									newProgress = this.getProgressByWeightedAverage(meta.get(id).getChildrenList(),
											contentProgressMap, meta, 0);
								else
									newProgress = meta.get(id).getProgress();
							}
							meta.get(id).setProgress(newProgress);
							meta.get(id).setLastTS(new Date());
							meta.get(id).setDateUpdated(format.parse(format.format(new Date())));
							
							
							if(meta.get(contentId).getFirstCompletedOn() != null && meta.get(id).getLastAccessedOn() != null) {
								if (!(meta.get(id).getLastAccessedOn().compareTo(meta.get(contentId).getFirstCompletedOn()) > 0))
									meta.get(id).setLastAccessedOn(meta.get(contentId).getFirstCompletedOn());
								if ((meta.get(id).getFirstAccessedOn().compareTo(meta.get(contentId).getFirstCompletedOn()) > 0))
									meta.get(id).setFirstAccessedOn(meta.get(contentId).getFirstCompletedOn());
								
							}
								
							if (newProgress == 1f)
								meta.get(id).setFirstCompletedOn(meta.get(contentId).getFirstCompletedOn());
						} else {
							if (oldProgress == 1f || updateOnlyLastAccessedOn) {
								meta.get(id).setProgress(oldProgress);
							} else {
								float newProgress = oldProgress;
								if (completedAssessment) {
									newProgress = 1;
								} else {
									if (meta.get(id).getProgress() == null)
										newProgress = this.getProgressByWeightedAverage(meta.get(id).getChildrenList(),
												contentProgressMap, meta, 0);
									else
										newProgress = meta.get(id).getProgress();
								}
								meta.get(id).setProgress(newProgress);
								meta.get(id).setLastTS(new Date());
								meta.get(id).setDateUpdated(format.parse(format.format(new Date())));
								if (newProgress == 1f)
									meta.get(id).setFirstCompletedOn(new Date());
							}
							meta.get(id).setLastAccessedOn(new Date());
						}
					}
					nextProgressList.addAll(meta.get(id).getParentList());

				} else
					missingIds.add(id);
//				System.out.println(id);
//				System.out.println(contentProgressMap.get(id).getProgress());
			}
			updateProgressList = nextProgressList;
			completedAssessment = false;
		}
		return missingIds;
	}

	private Float getProgressByWeightedAverage(List<String> childrenList,
			Map<String, ContentProgress> contentProgressMap, Map<String, ContentProgressModel> contentMeta, int counter)
			throws Exception {
		try {
			float sum = 0;
			float duration = 0;
			Boolean hasAssessment = false;
			// recursion counter
			if (counter >= 10) {
				throw new Exception("Invalid Hirarchy");
			}

			// calculate progress using the existing progress of children
			for (String child : childrenList) {
				if (!contentProgressMap.get(child).getResourceType().toLowerCase().equals("assessment")) {
					if (contentMeta.get(child) != null) {
						if (contentMeta.get(child).getProgress() != null) {
							sum += contentMeta.get(child).getProgress() * contentProgressMap.get(child).getDuration();
							duration += contentProgressMap.get(child).getDuration();
						} else {
							float childProgress = this.getProgressByWeightedAverage(
									contentMeta.get(child).getChildrenList(), contentProgressMap, contentMeta,
									counter + 1);
							contentMeta.get(child).setProgress(childProgress);
							sum += childProgress * contentProgressMap.get(child).getDuration();
							duration += contentProgressMap.get(child).getDuration();
						}
					} else {
						sum += contentProgressMap.get(child).getProgress() == -1 ? 0
								: contentProgressMap.get(child).getProgress()
										* contentProgressMap.get(child).getDuration();
						duration += contentProgressMap.get(child).getDuration();
					}
				} else
					hasAssessment = true;

			}
			return hasAssessment ? (sum / duration) * 0.7f : (sum / duration);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> metaForProgressForContentId(String rootOrg, String userUUID, String contentId) throws Exception {
		
		Map<String,Object> progressMap = this.metaForProgress(rootOrg, userUUID, new ArrayList<>(Arrays.asList(contentId)));
		
		if(!progressMap.containsKey(contentId) || progressMap.get(contentId) == null )
			throw new InvalidDataInputException("Invalid Content Id ");
		return (Map<String,Object>)progressMap.get(contentId);
	}

	@Override
	public Map<String, Object> metaForProgressHierarchy(String rootOrg, String userUUID, String contentId) throws Exception {

		Map<String,Object> progressMap = this.progressMetaData(rootOrg, userUUID, new ArrayList<>(Arrays.asList(contentId)));

		//System.out.println("Final hierarchy progressMap->" +new ObjectMapper().writeValueAsString(progressMap));

		if(!progressMap.containsKey(contentId) || progressMap.get(contentId) == null )
			throw new InvalidDataInputException("Invalid Content Id ");
		return (Map<String,Object>)progressMap.get(contentId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> metaForProgress(String rootOrg, String userUUID, List<String> idsList) throws Exception {

		Map<String, Object> ret = new HashMap<>();
		// Map<String, Object> meta = new HashMap<>();
		Map<String, Boolean> contentSourceMap = new HashMap<>();
		Map<String, String> mimes = contentService.getMimeTypes();
		Map<String, Float> contentProgressMap = new HashMap<>();
		List<String> contentList = Arrays.asList("Course", "Collection", "Resource", "Learning Path");
		
//		System.out.println(contentSourceMap);
//		contentSourceMap.put("Lex",true);
		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(idsList,
				new String[] { "identifier", "children.identifier", "mimeType", "contentType", "resourceType",
						"sourceShortName", "learningMode", "isExternal" },
				"Live");
		List<ContentProgressModel> contentProgressList = contentProgressRepo.getProgress(rootOrg, userUUID, contentList,
				idsList);
		//System.out.println("ContentProgressModel data  "+new ObjectMapper().writeValueAsString(contentProgressList));

		for (ContentProgressModel cpm : contentProgressList) {
			contentProgressMap.put(cpm.getPrimaryKey().getContentId(), cpm.getProgress());
		}
		for (Map<String, Object> source : searchHits) {

			// this flag is to mark if for this lex id the progress is provided from
			// external source in which case the markAsComplete reason would remain same
			// whether its
			// completed or not
			boolean isExternalProgressProvided = false;
			Map<String, Object> meta = new HashMap<>();
			meta.put("progressStatus", null);
			meta.put("showMarkAsComplete", null);
			meta.put("markAsCompleteReason", null);
			meta.put("progressSupported", null);
			String contentType = source.get("contentType").toString();
			String id = source.get("identifier").toString();
//			System.out.println(id);
//			System.out.println(contentProgressMap.containsKey(id));
			if (contentProgressMap.containsKey(id))
				meta.put("progress", contentProgressMap.get(id));
			else
				meta.put("progress", null);

			if (!contentList.contains(contentType))
				meta.put("progressSupported", false);

			else {
				meta.put("progressSupported", true);

				String sourceShortName = (String) source.getOrDefault("sourceShortName", null);
				Boolean isExternal = (Boolean) source.getOrDefault("isExternal", null);
				String learningMode = (String) source.getOrDefault("learningMode", null);

				if (isExternal == null  || learningMode == null) {
					throw new ApplicationLogicError("Invalid meta for mark as complete for contentId : " + id);
				}

				Map<String, String> macConfigMap = contentService.getMACConfiguration(rootOrg);
				Boolean rootOrgExtContentEnabled = Boolean.valueOf(macConfigMap.get("mac_for_external"));
				Boolean rootOrgILContentEnabled = Boolean.valueOf(macConfigMap.get("mac_for_instructor_led"));
				Boolean rootOrgSourceShortNameEnabled = Boolean.valueOf(macConfigMap.get("mac_for_source_short_name"));
				if (rootOrgSourceShortNameEnabled ) {
					List<ContentSourceProj> contentSourceList = contentSourceService.fetchAllContentsourcesForRootOrg(rootOrg,
							null);
					contentSourceList.forEach(contentSource -> contentSourceMap.put(contentSource.getSourceShortName(),
							contentSource.getProgressProvided()));
					if (contentSourceMap.containsKey(sourceShortName)) {
					if (contentSourceMap.get(sourceShortName) == true) {
						isExternalProgressProvided = true;
						meta.put("showMarkAsComplete", false);
						meta.put("markAsCompleteReason", "external.vendor.provided"); // todo- Add enum
					} 
					else {
						if (source.get("contentType").toString().toLowerCase().equals("resource")) {
							String mimeType = source.get("mimeType").toString();
							List<String> mimeTypes = Arrays.asList(mimes.get("result").split(","));
							if (mimeTypes.contains(mimeType)) {
								meta.put("showMarkAsComplete", false);
								if (source.get("resourceType").toString().toLowerCase().equals("assessment"))
									meta.put("markAsCompleteReason", "pass.required");
								else
									meta.put("markAsCompleteReason", "submission.required");
							} else {
								meta.put("showMarkAsComplete", true);
							}
						} else if (source.containsKey("children")) {

							List<Map<String, Object>> child = ((List<Map<String, Object>>) source.get("children"));
							if (!child.isEmpty()) {
								meta.put("showMarkAsComplete", false);
								meta.put("markAsCompleteReason", "has.children"); // todo- Add enum
							} else {
								meta.put("showMarkAsComplete", true);
							}
						} else
							meta.put("showMarkAsComplete", true);
					}
					}
					else {

						throw new Exception("sourceShortName not found");

					}
				} 
				else {
				if (isExternal == true && !rootOrgExtContentEnabled) {
					// configured for infosys content in which case if external content the mark as
					// complete is
					// not shown.
					if (source.get("mimeType").toString().equals("video/x-youtube"))
					{
						meta.put("showMarkAsComplete", true);
						isExternalProgressProvided = false;
					}
					else {
					isExternalProgressProvided = true;
					meta.put("showMarkAsComplete", false);
					meta.put("markAsCompleteReason", "external.vendor.provided");
					}
				} else if (learningMode.equalsIgnoreCase("Instructor-Led") && !rootOrgILContentEnabled) {
					meta.put("showMarkAsComplete", false);
					meta.put("markAsCompleteReason", "instructor.led");
				} else if (source.containsKey("resourceType")
						&& source.get("resourceType").toString().equalsIgnoreCase("certification")) {
					meta.put("showMarkAsComplete", false);
					meta.put("markAsCompleteReason", "pass.required");

				} 
//					else if (isExternal == false && !rootOrgExtContentEnabled) {
//					// configured for infosys internal content in which case mark as complete is
//					// always shown.
//					meta.put("showMarkAsComplete", true);
//				}
				else {
					if (source.get("contentType").toString().toLowerCase().equals("resource")) {
						String mimeType = source.get("mimeType").toString();
						List<String> mimeTypes = Arrays.asList(mimes.get("result").split(","));
						if (mimeTypes.contains(mimeType)) {
							meta.put("showMarkAsComplete", false);
							if (source.get("resourceType").toString().toLowerCase().equals("assessment"))
								meta.put("markAsCompleteReason", "pass.required");
							else
								meta.put("markAsCompleteReason", "submission.required");
						} else {
							meta.put("showMarkAsComplete", true);
						}
					} else if (source.containsKey("children")) {

						List<Map<String, Object>> child = ((List<Map<String, Object>>) source.get("children"));
						if (!child.isEmpty()) {
							meta.put("showMarkAsComplete", false);
							meta.put("markAsCompleteReason", "has.children"); // todo- Add enum
						} else {
							meta.put("showMarkAsComplete", true);
						}
					} else
						meta.put("showMarkAsComplete", true);
				}
			}


				if (meta.get("progress") != null) {
					Float pro = (Float) meta.get("progress");
					if (pro >= 1f) {
						meta.put("progressStatus", "completed");
						meta.put("showMarkAsComplete", false);
						// update if its not external provided content
						if (!isExternalProgressProvided)
							meta.put("markAsCompleteReason", "already.completed");

					} else
						meta.put("progressStatus", "started");
				} else
					meta.put("progressStatus", "open");
			}
			ret.put(id, meta);
		}
		return ret;
	}


	private Map<String, Object> progressMetaData(String rootOrg, String userUUID, List<String> idsList) throws Exception {

		Map<String, Object> ret = new HashMap<>();
		// Map<String, Object> meta = new HashMap<>();
		Map<String, Boolean> contentSourceMap = new HashMap<>();
		Map<String, String> mimes = contentService.getMimeTypes();
		Map<String, Float> contentProgressMap = new HashMap<>();
		List<String> contentList = Arrays.asList("Course", "Collection", "Resource", "Learning Path");

//		System.out.println(contentSourceMap);
//		contentSourceMap.put("Lex",true);
		List<Map<String, Object>> searchHits = contentService.getMetaByIDListandSource(idsList,
				new String[] { "identifier", "children.identifier", "mimeType", "contentType", "resourceType",
						"sourceShortName", "learningMode", "isExternal" },
				"Live");
		List<ContentProgressModel> contentProgressList = contentProgressRepo.getProgress(rootOrg, userUUID, contentList,
				idsList);
		//System.out.println("ContentProgressModel data  "+new ObjectMapper().writeValueAsString(contentProgressList));

		for (ContentProgressModel cpm : contentProgressList) {
			contentProgressMap.put(cpm.getPrimaryKey().getContentId(), cpm.getProgress());
		}
		for (Map<String, Object> source : searchHits) {

			// this flag is to mark if for this lex id the progress is provided from
			// external source in which case the markAsComplete reason would remain same
			// whether its
			// completed or not
			boolean isExternalProgressProvided = false;
			Map<String, Object> meta = new HashMap<>();
			meta.put("progressStatus", null);
			meta.put("showMarkAsComplete", null);
			meta.put("markAsCompleteReason", null);
			meta.put("progressSupported", null);
			String contentType = source.get("contentType").toString();
			String id = source.get("identifier").toString();
//			System.out.println(id);
//			System.out.println(contentProgressMap.containsKey(id));
			if (contentProgressMap.containsKey(id))
				meta.put("progress", contentProgressMap.get(id));
			else
				meta.put("progress", null);

			if (!contentList.contains(contentType))
				meta.put("progressSupported", false);

			else {
				meta.put("progressSupported", true);

				String sourceShortName = (String) source.getOrDefault("sourceShortName", null);
				Boolean isExternal = (Boolean) source.getOrDefault("isExternal", null);
				String learningMode = (String) source.getOrDefault("learningMode", null);

				if (isExternal == null  || learningMode == null) {
					throw new ApplicationLogicError("Invalid meta for mark as complete for contentId : " + id);
				}

				Map<String, String> macConfigMap = contentService.getMACConfiguration(rootOrg);
				Boolean rootOrgExtContentEnabled = Boolean.valueOf(macConfigMap.get("mac_for_external"));
				Boolean rootOrgILContentEnabled = Boolean.valueOf(macConfigMap.get("mac_for_instructor_led"));
				Boolean rootOrgSourceShortNameEnabled = Boolean.valueOf(macConfigMap.get("mac_for_source_short_name"));
				if (rootOrgSourceShortNameEnabled ) {
					List<ContentSourceProj> contentSourceList = contentSourceService.fetchAllContentsourcesForRootOrg(rootOrg,
							null);
					contentSourceList.forEach(contentSource -> contentSourceMap.put(contentSource.getSourceShortName(),
							contentSource.getProgressProvided()));
					if (contentSourceMap.containsKey(sourceShortName)) {
						if (contentSourceMap.get(sourceShortName) == true) {
							isExternalProgressProvided = true;
							meta.put("showMarkAsComplete", false);
							meta.put("markAsCompleteReason", "external.vendor.provided"); // todo- Add enum
						}
						else {
							if (source.get("contentType").toString().toLowerCase().equals("resource")) {
								String mimeType = source.get("mimeType").toString();
								List<String> mimeTypes = Arrays.asList(mimes.get("result").split(","));
								if (mimeTypes.contains(mimeType)) {
									meta.put("showMarkAsComplete", false);
									if (source.get("resourceType").toString().toLowerCase().equals("assessment"))
										meta.put("markAsCompleteReason", "pass.required");
									else
										meta.put("markAsCompleteReason", "submission.required");
								} else {
									meta.put("showMarkAsComplete", true);
								}
							} else if (source.containsKey("children")) {

								List<Map<String, Object>> child = ((List<Map<String, Object>>) source.get("children"));
								if (!child.isEmpty()) {
									meta.put("showMarkAsComplete", false);
									meta.put("markAsCompleteReason", "has.children"); // todo- Add enum

									//add child progress data
									List<String> childIds = child.stream().map(c -> c.get("identifier").toString()).collect(Collectors.toList());
									Map<String, Object> childrenProgress = progressMetaData(rootOrg, userUUID, childIds);
									//System.out.println("Child ids ->"+childIds+" child progress data: "+childrenProgress);
									meta.put("childrenProgress", childrenProgress);

								} else {
									meta.put("showMarkAsComplete", true);
								}
							} else
								meta.put("showMarkAsComplete", true);
						}
					}
					else {

						throw new Exception("sourceShortName not found");

					}
				}
				else {
					if (isExternal == true && !rootOrgExtContentEnabled) {
						// configured for infosys content in which case if external content the mark as
						// complete is
						// not shown.
						if (source.get("mimeType").toString().equals("video/x-youtube"))
						{
							meta.put("showMarkAsComplete", true);
							isExternalProgressProvided = false;
						}
						else {
							isExternalProgressProvided = true;
							meta.put("showMarkAsComplete", false);
							meta.put("markAsCompleteReason", "external.vendor.provided");
						}
					} else if (learningMode.equalsIgnoreCase("Instructor-Led") && !rootOrgILContentEnabled) {
						meta.put("showMarkAsComplete", false);
						meta.put("markAsCompleteReason", "instructor.led");
					} else if (source.containsKey("resourceType")
							&& source.get("resourceType").toString().equalsIgnoreCase("certification")) {
						meta.put("showMarkAsComplete", false);
						meta.put("markAsCompleteReason", "pass.required");

					}
//					else if (isExternal == false && !rootOrgExtContentEnabled) {
//					// configured for infosys internal content in which case mark as complete is
//					// always shown.
//					meta.put("showMarkAsComplete", true);
//				}
					else {
						if (source.get("contentType").toString().toLowerCase().equals("resource")) {
							String mimeType = source.get("mimeType").toString();
							List<String> mimeTypes = Arrays.asList(mimes.get("result").split(","));
							if (mimeTypes.contains(mimeType)) {
								meta.put("showMarkAsComplete", false);
								if (source.get("resourceType").toString().toLowerCase().equals("assessment"))
									meta.put("markAsCompleteReason", "pass.required");
								else
									meta.put("markAsCompleteReason", "submission.required");
							} else {
								meta.put("showMarkAsComplete", true);
							}
						} else if (source.containsKey("children")) {

							List<Map<String, Object>> child = ((List<Map<String, Object>>) source.get("children"));
							if (!child.isEmpty()) {
								meta.put("showMarkAsComplete", false);
								meta.put("markAsCompleteReason", "has.children"); // todo- Add enum

								//add child progress data
								List<String> childIds = child.stream().map(c -> c.get("identifier").toString()).collect(Collectors.toList());
								Map<String, Object> childrenProgress = progressMetaData(rootOrg, userUUID, childIds);
								meta.put("childrenProgress", childrenProgress);

							} else {
								meta.put("showMarkAsComplete", true);
							}
						} else
							meta.put("showMarkAsComplete", true);
					}
				}


				if (meta.get("progress") != null) {
					Float pro = (Float) meta.get("progress");
					if (pro >= 1f) {
						meta.put("progressStatus", "completed");
						meta.put("showMarkAsComplete", false);
						// update if its not external provided content
						if (!isExternalProgressProvided)
							meta.put("markAsCompleteReason", "already.completed");

					} else
						meta.put("progressStatus", "started");
				} else
					meta.put("progressStatus", "open");
			}
			ret.put(id, meta);
		}
		//System.out.println("result -> "+new ObjectMapper().writeValueAsString(ret));
		return ret;
	}


	@SuppressWarnings("unchecked")
	@Override
	public String updateAssessmentRecalculate(AssessmentRecalculateDTO assessmentInfo) throws Exception{
		
		
		String userId = assessmentInfo.getUser_id();
		String contentId = assessmentInfo.getResource_id();
		String rootOrg = assessmentInfo.getRoot_org();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		Date completedOn=formatter.parse(assessmentInfo.getFirstCompletedOn());  
		Map<String, ContentProgressModel> meta = new HashMap<>();
		Map<String, ContentProgress> contentProgressMap = new HashMap<>();
		

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		if (assessmentInfo.getProgress() < 1) {
			throw new Exception("Assessment not completed");
		}
		
		ContentProgressModel resourceProgress = contentProgressRepo.findById(
				new ContentProgressPrimaryKeyModel(rootOrg, userId, "Resource", contentId))
				.orElse(null);
		

		
		Map<String, Object> temp = this.getMetaForResource(rootOrg, contentId, userId);

		// add the resource to the map with new progress to be updated in cassandra
		meta.putAll((Map<String, ContentProgressModel>) temp.get("meta"));
		meta.get(contentId).setFirstCompletedOn(completedOn);
		meta.get(contentId).setProgress(assessmentInfo.getProgress());
		meta.get(contentId).setLastTS(new Date());
		meta.get(contentId).setDateUpdated(format.parse(format.format(new Date())));
		if (resourceProgress == null)
		{
			meta.get(contentId).setFirstAccessedOn(completedOn);
			meta.get(contentId).setLastAccessedOn(completedOn);
		}
		contentProgressMap.put(contentId, new ContentProgress(temp.get("resource_type").toString(),
				meta.get(contentId).getProgress(), Long.parseLong(temp.get("duration").toString())));
		
		List<String> missingIds = this.getMetaAndProgressForHierarchy(rootOrg, meta, contentProgressMap, userId,
				contentId, false, true, false, true);

		contentProgressRepo.updateProgress(meta.values());
		
		return "success";
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public String updateExternalProgress(ExternalProgressDTO externalInfo) throws Exception{
		
		
		String userId = externalInfo.getUser_id();
		String contentId = externalInfo.getContent_id();
		String rootOrg = externalInfo.getRoot_org();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");  
		  
		Date startedOn=formatter.parse(externalInfo.getFirst_activity_date());
		Map<String, ContentProgressModel> meta = new HashMap<>();
		Map<String, ContentProgress> contentProgressMap = new HashMap<>();
		
		
		Float progress = externalInfo.getPercent_complete()/100;

		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}
		
		Map<String, Object> temp = this.getMetaForResource(rootOrg, contentId, userId);

		// add the resource to the map with new progress to be updated in cassandra
		meta.putAll((Map<String, ContentProgressModel>) temp.get("meta"));
		meta.get(contentId).setFirstAccessedOn(startedOn);
		meta.get(contentId).setProgress(progress);
		meta.get(contentId).setLastTS(new Date());
		meta.get(contentId).setDateUpdated(format.parse(format.format(new Date())));
		meta.get(contentId).setLastAccessedOn(new Date());
		
//		meta.get(contentId).setUpdatedBy("content_progress_batch");
		
		// Added updated_by 
		meta.get(contentId).setUpdatedBy(externalInfo.getUpdated_by());
		String cdate = externalInfo.getCompletion_date();
		if (( cdate != null && cdate != "" ))
		{
			Date completedOn=formatter.parse(externalInfo.getCompletion_date());
			meta.get(contentId).setFirstCompletedOn(completedOn);
		}
		contentProgressMap.put(contentId, new ContentProgress(temp.get("resource_type").toString(),
				meta.get(contentId).getProgress(), Long.parseLong(temp.get("duration").toString())));
		
		List<String> missingIds = this.getMetaAndProgressForHierarchy(rootOrg, meta, contentProgressMap, userId,
				contentId, false, false, false, false);

		contentProgressRepo.updateProgress(meta.values());
		
		return "success";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean getMandatoryContentStatusForUser(String rootOrg, String org, String userId) {
		List<MandatoryContentModel> contentList = mandatoryContentRepository.getMandatoryContentsInfo(rootOrg, org);
		if (CollectionUtils.isEmpty(contentList)) {
			return Boolean.TRUE;
		}
		List<String> contentIds = contentList.stream().map(content -> content.getPrimaryKey().getContent_id())
				.collect(Collectors.toList());
		Map<String, MandatoryContentModel> contentModelMap = contentList.stream().collect(Collectors.toMap(content -> content.getPrimaryKey().getContent_id(), content -> content, (e1, e2) -> e1));
		try {
			Map<String, Object> progressMap = this.metaForProgress(rootOrg, userId, contentIds);
			if (CollectionUtils.isEmpty(progressMap)) {
				return Boolean.FALSE;
			}
			Map<String, Object> resultMap = new HashMap<>();
			for (String contentId : contentIds) {
				if (!progressMap.containsKey(contentId)) {
					return Boolean.FALSE;
				}
				resultMap = (Map<String, Object>) progressMap.get(contentId);
				Float progress = (Float) resultMap.get(PROGRESS_CONSTANT);
				Float minimumCriteria = contentModelMap.get(contentId).getMinProgressCheck();
				if (progress < minimumCriteria)
					return Boolean.FALSE;
			}
		} catch (Exception e) {
			logger.error(e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}