/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.rating.service;

import java.util.Map;

import com.infosys.lex.rating.dto.ContentIdsDto;
import com.infosys.lex.rating.dto.UserContentRatingDTO;

public interface RatingService {

	Map<String, Object> getUserRatings(String rootOrg, String contentId, String userId) throws Exception;

	Map<String, Object> getAvgUserRatingAndCountForResource(String rootOrg, String contentId);
	
	Map<String, Object> updateUserRating(String rootOrg, String contentId, String userId,
			UserContentRatingDTO req) throws Exception;

	Map<String, Object> deleteUserRating(String rootOrg, String contentId, String userId) throws Exception;

	Map<String, Object> getRatingsInfoForContents(String rootOrg, ContentIdsDto contentIdsMap);

}
