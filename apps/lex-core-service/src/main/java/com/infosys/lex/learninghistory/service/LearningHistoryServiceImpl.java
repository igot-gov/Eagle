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
package com.infosys.lex.learninghistory.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.sunbird.repo.UserMVRepository;
import com.infosys.lex.common.sunbird.repo.UserRepository;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressOrderByLastAccessRepository;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;

//TODO change email verification
@Service
public class LearningHistoryServiceImpl implements LearningHistoryService {

	@Autowired
	UserRepository userRepo;

	@Autowired
	UserMVRepository userMVRepo;

	@Autowired
	ContentService contentService;

	@Autowired
	ContentProgressOrderByLastAccessRepository contentProgressOrderByLastAccessRepository;

	@Autowired
	ContentProgressRepository contentProgressRepo;

	@Autowired
	UserUtilityService userUtilService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.LearningHistoryService#getUserCourseProgress(java.
	 * lang.String, java.lang.Integer, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getUserCourseProgress(String rootOrg, String userUUID, Integer pageSize,
			String pageState, String status, String contentType) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		Map<String, Object> sourceMap;
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		Map<String, Object> data = contentProgressOrderByLastAccessRepository.fetchUserCourseProgressPaginated(rootOrg,
				userUUID, status, contentType, pageState, pageSize);

		if (!data.get("page_state").equals("-1")) {
			List<Map<String, Object>> resultList = new ArrayList<>();

			// fetch meta of content to be shown
			Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(
					(List<String>) data.get("content_list"), new HashSet<>(Arrays.asList("identifier", "name",
							"children.identifier", "thumbnail", "duration", "contentType")));
			for (String contentId : (List<String>) data.get("content_list")) {
				if (contentMetaMap.containsKey(contentId)) {
					sourceMap = contentMetaMap.get(contentId);

					String hitContentId = sourceMap.get("identifier").toString();
					if (hitContentId.equals(contentId)) {
						Map<String, Object> temp = new HashMap<>();
						temp.put("name", sourceMap.get("name"));
						temp.put("contentType", contentType);
						temp.put("totalDuration", sourceMap.getOrDefault("duration", "0"));
						temp.put("thumbnail", sourceMap.getOrDefault("thumbnail", ""));

						List<String> children = new ArrayList<>();
						if (sourceMap.containsKey("children"))
							for (Map<String, String> child : (List<Map<String, String>>) sourceMap.get("children")) {
								children.add(child.get("identifier"));
							}
						temp.put("children", children);
						temp.put("progress", ((Map<String, Object>) ((Map<String, Object>) data.get("result"))
								.get(sourceMap.get("identifier"))).get("progress"));
						temp.put("pending", 1 - Float.parseFloat(temp.get("progress").toString()));
						temp.put("last_ts", ((Map<String, Object>) ((Map<String, Object>) data.get("result"))
								.get(sourceMap.get("identifier"))).get("last_accessed_on"));
						temp.put("identifier", hitContentId);
						temp.put("timeLeft", ((1 - Float.parseFloat(temp.get("progress").toString()))
								* Float.parseFloat(sourceMap.getOrDefault("duration", "0").toString())));
						resultList.add(temp);
					}
				}
			}
			ret.put("result", resultList);
			ret.put("count", resultList.size());
			ret.put("page_state", data.get("page_state").toString());
		} else {
			ret.put("result", new ArrayList<Map<String, Object>>());
			ret.put("count", 0);
			ret.put("page_state", "-1");
		}
		System.out.println(ret);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.LearningHistoryService#getUserContentListProgress(
	 * java.lang.String, java.util.List, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getUserContentListProgress(String rootOrg, String userUUID,
			List<String> contentIds) throws Exception {

		List<Map<String, Object>> ret = new ArrayList<>();
		Map<String, Object> sourceMap;
		Float progress;
		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		if (contentIds.size() > 0) {
			List<ContentProgressModel> data = contentProgressRepo.getProgress(rootOrg, userUUID,
					Arrays.asList(new String[] { "Resource", "Course", "Collection", "Learning Path" }), contentIds);
			List<String> receivedIds = new ArrayList<>();

			// get list of received ids
			for (ContentProgressModel content : data) {
				receivedIds.add(content.getPrimaryKey().getContentId());
			}

			// get meta from ES
			Map<String, Map<String, Object>> meta = new HashMap<>();
			// fetch meta of content to be shown
			Map<String, Map<String, Object>> contentMetaMap = contentService
					.filterAndFetchContentMetaToShow(receivedIds, new HashSet<>(Arrays.asList("identifier", "name",
							"children.identifier", "thumbnail", "duration", "contentType")));

			for (String contentId : receivedIds) {
				if (contentMetaMap.containsKey(contentId)) {
					sourceMap = contentMetaMap.get(contentId);

					Map<String, Object> temp = new HashMap<>();
					temp.put("name", sourceMap.get("name"));
					temp.put("contentType", sourceMap.get("contentType"));
					temp.put("totalDuration", sourceMap.getOrDefault("duration", "0"));
					temp.put("thumbnail", sourceMap.getOrDefault("thumbnail", ""));

					List<String> children = new ArrayList<>();
					if (sourceMap.containsKey("children"))
						for (Map<String, String> child : (List<Map<String, String>>) sourceMap.get("children")) {
							children.add(child.get("identifier"));
						}
					temp.put("children", children);
					temp.put("identifier", sourceMap.get("identifier"));
					meta.put(sourceMap.get("identifier").toString(), temp);
				}
			}

			String contentId;
			// add progress to meta
			for (ContentProgressModel content : data) {
				contentId = content.getPrimaryKey().getContentId();
				if (meta.containsKey(contentId)) {
					progress = content.getProgress();
					meta.get(contentId).put("progress", content.getProgress());
					meta.get(contentId).put("timeLeft",
							(1 - progress) * Float.parseFloat(meta.get(contentId).get("totalDuration").toString()));
					meta.get(contentId).put("last_ts", content.getLastTS());
					meta.get(contentId).put("pending", 1 - progress);
				}
			}

			// ordering
			for (String id : contentIds) {
				if (meta.containsKey(id))
					ret.add(meta.get(id));
			}

		} else
			throw new InvalidDataInputException("missing.request");

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.infosys.core.service.LearningHistoryService#getContentListProgress(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */

	private Map<String, Object> getProgressForContentList(String rootOrg, String userUUID, List<String> contentIds)
			throws Exception {
		Map<String, Object> ret = new HashMap<>();

		// TODO
		if (!userUtilService.validateUser(rootOrg, userUUID)) {
			throw new BadRequestException("Invalid User : " + userUUID);
		}

		List<ContentProgressModel> data = new ArrayList<>();
		if (!contentIds.isEmpty()){
			data = contentProgressRepo.getProgress(rootOrg, userUUID,
					Arrays.asList(new String[] { "Resource", "Course", "Collection", "Learning Path" }), contentIds);
			//System.out.println("ContentProgressModel data for given contentId "+new ObjectMapper().writeValueAsString(data));
		} else {
			data = contentProgressRepo.getProgressForAll(rootOrg, userUUID,
					Arrays.asList(new String[] { "Resource", "Course", "Collection", "Learning Path" }));
			//System.out.println("ContentProgressModel data for all "+new ObjectMapper().writeValueAsString(data));

		}


		List<String> contentIdList = new ArrayList<>();
		data.forEach(content -> contentIdList.add(content.getPrimaryKey().getContentId()));

//		Map<String, Map<String, Object>> contentMetaMap = contentService.filterAndFetchContentMetaToShow(contentIds,
//				new HashSet<>());

		String contentId;
		for (ContentProgressModel content : data) {
			contentId = content.getPrimaryKey().getContentId();
			// resources that are to shown are filtered
//			if (contentMetaMap.containsKey(contentId))
			ret.put(contentId, content.getProgress());
		}

		return ret;
	}

	@Override
	public Map<String, Object> getContentListStrProgress(String rootOrg, String userUUID, String contentIdStr)
			throws Exception {
		// TODO Auto-generated method stub
		return this.getProgressForContentList(rootOrg, userUUID, Arrays.asList(contentIdStr.split(",")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getProgressForContentListMap(String rootOrg, String userUUID,
			Map<String, Object> contentIdMap) throws Exception {
		if (!contentIdMap.containsKey("contentIds") && contentIdMap.get("contentIds") == null)
			throw new InvalidDataInputException("Invalid content id map");
		return this.getProgressForContentList(rootOrg, userUUID, (List<String>) contentIdMap.get("contentIds"));

	}

}
