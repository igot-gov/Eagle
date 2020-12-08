/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.rating.dto.RatingSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.core.exception.BadRequestException;
import com.infosys.lex.core.exception.InvalidDataInputException;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingModel;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingPrimaryKeyModel;
import com.infosys.lex.rating.bodhi.repo.UserContentRatingRepository;
import com.infosys.lex.rating.dto.ContentIdsDto;
import com.infosys.lex.rating.dto.UserContentRatingDTO;
import org.springframework.util.CollectionUtils;

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
	public Map<String,Object> getRatingsInfoForContents(String rootOrg,ContentIdsDto contentIdsMap) {
		Map<String, Object> resp = new HashMap<>();
		String contentId;
		List<String> contentIds = contentIdsMap.getContentIds();
		List<Map<String, Object>> ratingsInfoList = userResourceRatingRepo.getAvgRatingAndRatingCountForContentIds(rootOrg, contentIds);
		for (Map<String, Object> ratingsInfo : ratingsInfoList) {
			contentId = ratingsInfo.get("content_id").toString();
			resp.put(contentId, getRatingsInfo(rootOrg, contentId, ratingsInfo));
		}
		return resp;

	}

	/**
	 *
	 * @param rootOrg root org
	 * @param contentId content Id
	 * @return return rating info for a content Id
	 */
	@Override
	public Map<String, Object> getRatingsInfoForContents(String rootOrg, String contentId) {
		List<String> contentIds = new ArrayList<>(Collections.singletonList(contentId));
		List<Map<String, Object>> ratingsInfoList = userResourceRatingRepo.getAvgRatingAndRatingCountForContentIds(rootOrg, contentIds);
		Map<String, Object> resp = null;
		if (!CollectionUtils.isEmpty(ratingsInfoList)) {
			resp = getRatingsInfo(rootOrg, contentId, ratingsInfoList.get(0));
		}
		return resp;
	}

	/**
	 *
	 * @param rootOrg
	 * @param ratingSearchDTO
	 * @return
	 */
	@Override
	public HashMap<String, Object> getAllRatingsForContent(String rootOrg, RatingSearchDTO ratingSearchDTO) {
		Integer pageSize = ratingSearchDTO.getPageSize() == null ? 0 : ratingSearchDTO.getPageSize();
		Integer pageNo = ratingSearchDTO.getPageNo() == null ? 0 : ratingSearchDTO.getPageNo();
		Integer limit  =  pageSize * (pageNo + 1);
		Integer offset =  pageNo * pageSize;
		HashMap<String, Object> responseMap = new HashMap<>();
		List<Object> responseArray = new ArrayList<>();
		int totalSize;
		HashMap<String, Object> response;
		List<Map<String, Object>> ratings = userResourceRatingRepo.getRatingInfoForContent(rootOrg, ratingSearchDTO.getContentId());
		totalSize = ratings.size();
		List<Map<String, Object>> subListOfRatings = ratings;
		if(limit != 0 && limit <= totalSize)
			subListOfRatings = ratings.subList(offset, limit);
		if(limit > totalSize)
			subListOfRatings = Collections.emptyList();
		Map<String, Object> pidResponse = new HashMap<>();
        if(!CollectionUtils.isEmpty(subListOfRatings)){
			List<String> uuids = subListOfRatings.stream().map(rating -> (String) rating.get("user_id")).collect(Collectors.toList());
			pidResponse = userUtilService.getUsersDataFromUserIds(rootOrg, uuids,
					new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL)));
		}
		for (Map<String, Object> rating : subListOfRatings) {
			response = new HashMap<>();
			response.put("ratingInfo", rating);
			response.put("userInfo", pidResponse.get((String) rating.get("user_id")));
			responseArray.add(response);
		}
		responseMap.put("ratings", responseArray);
		responseMap.put("totalSize", totalSize);
		return responseMap;
	}

	/**
	 *
	 * @param rootOrg rootOrg
	 * @param contentId contentId
	 * @return return rating list
	 */
	private Map<String, Object> getRatingsInfo(String rootOrg, String contentId, Map<String, Object> ratingsInfo) {
		Map<String, Object> resp = new HashMap<>();
		ArrayList<Object> ratingInfo = new ArrayList<>();
		HashMap<String, Object> ratingValue;
		List<Integer> defaultRating = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
		List<Float> ratings = userResourceRatingRepo.getRatingsForContent(rootOrg, contentId);
		resp.put("averageRating", df.format((float) ratingsInfo.get("averageRating")));
		resp.put("ratingCount", ratingsInfo.get("ratingCount"));
		Map<Float, Long> streamResult = ratings.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		for (Integer defaultValue : defaultRating) {
			ratingValue = new HashMap<>(2);
			ratingValue.put("rating", defaultValue);
			ratingValue.put("count", streamResult.getOrDefault(Float.valueOf(defaultValue), 0L));
			ratingInfo.add(ratingValue);
		}
		resp.put("ratingInfo", ratingInfo);
		return resp;
	}

}
