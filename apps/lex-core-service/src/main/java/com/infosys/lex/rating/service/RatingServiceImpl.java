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

	@Value("${content.ratingsearch.defaultlimit}")
	private Integer defaultLimit;


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

		List<Integer> defaultRating = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
		HashMap<String, Object> ratingValue;
		ArrayList<Object> ratingInfo = new ArrayList<>();
		for (Map<String, Object> ratingsInfo : ratingsInfoList) {
			contentId = ratingsInfo.get("content_id").toString();
			ratingsInfo.remove("content_id");
			ratingsInfo.put("averageRating", df.format((float) ratingsInfo.get("averageRating")));

			List<Float> ratings = userResourceRatingRepo.getRatingsForContent(rootOrg, contentId);
			Map<Float, Long> streamResult = ratings.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			for (Integer defaultValue : defaultRating) {
				ratingValue = new HashMap<>(2);
				ratingValue.put("rating", defaultValue);
				ratingValue.put("count", streamResult.getOrDefault(Float.valueOf(defaultValue), 0L));
				ratingInfo.add(ratingValue);
			}
			ratingsInfo.put("ratingInfo", ratingInfo);

			resp.put(contentId, ratingsInfo);
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
		ArrayList<Object> ratingInfo = new ArrayList<>();
		List<String> contentIds = new ArrayList<>(Collections.singletonList(contentId));
		List<Map<String, Object>> ratingsInfoList = userResourceRatingRepo.getAvgRatingAndRatingCountForContentIds(rootOrg, contentIds);
		Map<String, Object> resp = new HashMap<>();
		List<Integer> defaultRating = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
		if (!CollectionUtils.isEmpty(ratingsInfoList)) {
			HashMap<String, Object> ratingValue;
			Map<String, Object> ratingResponse = ratingsInfoList.get(0);
			resp.put("averageRating", df.format((float) ratingResponse.get("averageRating")));
			resp.put("ratingCount", ratingResponse.get("ratingCount"));

			List<Float> ratings = userResourceRatingRepo.getRatingsForContent(rootOrg, contentId);
			Map<Float, Long> streamResult = ratings.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			for (Integer defaultValue : defaultRating) {
				ratingValue = new HashMap<>(2);
				ratingValue.put("rating", defaultValue);
				ratingValue.put("count", streamResult.getOrDefault(Float.valueOf(defaultValue), 0L));
				ratingInfo.add(ratingValue);
			}
			resp.put("ratingInfo", ratingInfo);
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
	public List<Object> getAllRatingsForContent(String rootOrg, RatingSearchDTO ratingSearchDTO) {
		Integer limit = ratingSearchDTO.getPageSize() == null ? defaultLimit : ratingSearchDTO.getPageSize();
		List<Object> responseArray = new ArrayList<>();
		HashMap<String, Object> response;
		List<Map<String, Object>> ratings = userResourceRatingRepo.getRatingInfoForContent(rootOrg, ratingSearchDTO.getContentId(), limit);
		List<String> uuids = ratings.stream().map(rating -> (String)rating.get("user_id")).collect(Collectors.toList());
		Map<String, Object> pidResponse = userUtilService.getUsersDataFromUserIds(rootOrg, uuids,
				new ArrayList<>(Arrays.asList(PIDConstants.FIRST_NAME, PIDConstants.LAST_NAME, PIDConstants.EMAIL)));
		for (Map<String, Object> rating : ratings) {
			response = new HashMap<>();
			response.put("ratingInfo", rating);
			response.put("userInfo", pidResponse.get((String)rating.get("user_id")));
			responseArray.add(response);
		}
		return responseArray;
	}

}
