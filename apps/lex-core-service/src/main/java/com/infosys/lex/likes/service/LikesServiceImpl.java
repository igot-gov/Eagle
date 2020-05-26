/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.likes.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.likes.bodhi.repo.LikesMaterializedView;
import com.infosys.lex.likes.bodhi.repo.LikesRepository;
import com.infosys.lex.likes.entities.Likes;
import com.infosys.lex.likes.entities.LikesKey;

@Service
public class LikesServiceImpl implements LikesService {

	@Autowired
	LikesRepository likesRepo;

	@Autowired
	ContentService content;

	@Autowired
	UserUtilityService userUtilityService;

	@Autowired
	LikesMaterializedView likesMaterailizedRepo;

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> upsertLikes(String rootOrg, String userId, String contentId) throws Exception {

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		LikesKey likesKey = new LikesKey(rootOrg, userId, contentId);
		List<Map<String, Object>> verifiedContentId = new ArrayList<Map<String, Object>>();
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());

		Likes likes = new Likes(likesKey, ts);
		// verify if the content is live or not
		List<String> ids = new ArrayList<String>();
		ids.add(contentId);
		String[] source = new String[] { "identifier" };
		verifiedContentId = content.getMetaByIDListandSource(ids, source, "Live");

		if (verifiedContentId.isEmpty()) {
			throw new InvalidDataInputException("invalid.contentId");
		}

		likesRepo.save(likes);
		Map<String, Object> contentMap = likesMaterailizedRepo.getTotalLikesOfContent(rootOrg, contentId);

		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put(contentId, contentMap.getOrDefault("count", 0));
		return retMap;
	}

	/*
	 * (non-Javadoc)
	 * 
substitute url based on requirement
	 * java.lang.String)
	 */
	@Override
	public List<String> getLikes(String rootOrg, String userId) throws Exception {

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		List<Likes> data = new ArrayList<Likes>();
		List<String> result = new ArrayList<String>();
		data = likesRepo.findByLikesKeyRootOrgAndLikesKeyUserId(rootOrg, userId);

		if (data.isEmpty()) {
			return result;
		}

		for (Likes likeData : data) {
			result.add(likeData.getLikesKey().getContentId());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> deleteLikesData(String rootOrg, String userId, String contentId) throws Exception {

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		LikesKey likesKey = new LikesKey(rootOrg, userId, contentId);
		Likes likes = new Likes(likesKey);

		Likes verifiedLikesObject = new Likes();
		verifiedLikesObject = this.checkForObject(rootOrg, userId, contentId);
		// if content is present
		if (verifiedLikesObject == null) {
			throw new InvalidDataInputException("invalid.record");
		} else {
			likesRepo.delete(likes);

			Map<String, Object> contentMap = likesMaterailizedRepo.getTotalLikesOfContent(rootOrg, contentId);
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put(contentId, contentMap.getOrDefault("count", 0));
			return retMap;
		}
	}

	public Likes checkForObject(String rootOrg, String userId, String contentId) throws Exception {
		return likesRepo.findByLikesKeyRootOrgAndLikesKeyUserIdAndLikesKeyContentId(rootOrg, userId, contentId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getLikesData(String rootOrg, String userId, String contentId) throws Exception {

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new InvalidDataInputException("invalid.user");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		Likes verifiedLikesObject = new Likes();

		// check if record is present in table or not
		verifiedLikesObject = checkForObject(rootOrg, userId, contentId);

		if (verifiedLikesObject == null) {
			result.put("isLiked", false);
		} else {
			result.put("isLiked", true);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
substitute url based on requirement
	 * java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getTotalLikes(String rootOrg, Map<String, Object> contentList) {

		List<String> contentId = (List<String>) contentList.get("content_id");

		List<Map<String, Object>> contentLikes = likesMaterailizedRepo.getTotalLikes(rootOrg, contentId);

		Map<String, Object> contentLike = new HashMap<String, Object>();
		for (String s : contentId) {
			contentLike.put(s, 0);
		}
		for (Map<String, Object> map : contentLikes) {
			String content = (String) map.get("content_id");
			contentLike.put(content, map.get("count"));
		}
		return contentLike;

	}

}
