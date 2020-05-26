/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.ContentMetaConstants;
import com.infosys.lex.core.exception.ApplicationLogicError;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingModel;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingPrimaryKeyModel;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingRepository;
import com.infosys.lex.rating.dto.ContentIdsDto;
import com.infosys.lex.rating.dto.UserContentRatingDTO;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
	private UserContentRatingRepository userResourceRatingRepo;

	@Autowired
	private UserUtilityService userUtilService;

	@Autowired
	private ContentService contentServ;

	private static final DecimalFormat df = new DecimalFormat("0.0");

	/*
	 * returns rating for user for the given contentId and returns null id doesnt
	 * exits
	 */

	@Override
	public Map<String, Object> getUserRatings(String rootOrg, String contentId, String userId) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		Map<String, Object> resp = new HashMap<String, Object>();
		UserContentRatingPrimaryKeyModel primaryKey = new UserContentRatingPrimaryKeyModel(rootOrg, contentId, userId);
		Optional<UserContentRatingModel> userResourceRating = userResourceRatingRepo.findById(primaryKey);
		Float rating = null;
		if (userResourceRating.isPresent())
			rating = userResourceRating.get().getRating();
		resp.put("rating", rating);
		return resp;

	}

	/*
	 * In user rating table : It updates the existing data if exists else inserts.
	 * in content rating data table
	 * 
	 */

	@Override
	public Map<String, Object> updateUserRating(String rootOrg, String contentId, String userId,
			UserContentRatingDTO req) throws Exception {

		// TODO
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		// check whether contentId exists
		if (!contentServ.validateContentIdToShow(contentId))
			throw new InvalidDataInputException("Content Id doesn't exist");

		Float rating = req.getRating();

		if (rating <= 0.0f || rating > 5.0f)
			throw new InvalidDataInputException("invalid user rating");

		
		// check

		UserContentRatingPrimaryKeyModel userResourceRatingPrimaryKey = new UserContentRatingPrimaryKeyModel(rootOrg,
				contentId, userId);

		UserContentRatingModel userResourceRating = new UserContentRatingModel(userResourceRatingPrimaryKey, rating);
		userResourceRatingRepo.save(userResourceRating);
		
		Map<String, Object> ratingDetailMap = userResourceRatingRepo.getAvgRatingAndRatingCountForContentId(rootOrg,
				contentId);
		ratingDetailMap.put("contentId", contentId);
		ratingDetailMap.put("averageRating",df.format((float)ratingDetailMap.get("averageRating")));
		return ratingDetailMap;
	}

	@Override
	public Map<String, Object> getAvgUserRatingAndCountForResource(String rootOrg, String contentId) {
		return userResourceRatingRepo.getAvgRatingAndRatingCountForContentId(rootOrg, contentId);
	}

	/**
	 * Deletes rating for given user and contentId and rootOrg
	 * 
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> deleteUserRating(String rootOrg, String contentId, String userId) throws Exception {
		// validate userId
		if (!userUtilService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("Invalid User : " + userId);
		}

		// check whether contentId existsgit sgit
		if (!contentServ.validateContentIdToShow(contentId))
			throw new InvalidDataInputException("Content Id doesn't exist");

		UserContentRatingPrimaryKeyModel userContentRatingPrimaryKey = new UserContentRatingPrimaryKeyModel(rootOrg,
				contentId, userId);
		userResourceRatingRepo.deleteById(userContentRatingPrimaryKey);
		Map<String, Object> ratingDetailMap = userResourceRatingRepo.getAvgRatingAndRatingCountForContentId(rootOrg,
				contentId);
		ratingDetailMap.put("contentId", contentId);
		ratingDetailMap.put("averageRating",df.format((float)ratingDetailMap.get("averageRating")));
		return ratingDetailMap;
	}
	
	@Override
	public Map<String,Object> getRatingsInfoForContents(String rootOrg,ContentIdsDto contentIdsMap)
	{
		Map<String,Object> resp = new HashMap<>();
		String contentId;
		
		List<String> contentIds = contentIdsMap.getContentIds();
		List<Map<String,Object>> ratingsInfoList = userResourceRatingRepo.getAvgRatingAndRatingCountForContentIds(rootOrg, contentIds);
		
		for(Map<String,Object> ratingsInfo : ratingsInfoList)
		{
			contentId = ratingsInfo.get("content_id").toString();
			ratingsInfo.remove("content_id");
			ratingsInfo.put("averageRating",df.format((float)ratingsInfo.get("averageRating") ));
			resp.put(contentId, ratingsInfo);
		}
		return resp;
		
	}

}
