/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.attendence.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.lex.attendence.postgredb.repo.AttendanceRepo;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.common.service.UserUtilityService;
import com.infosys.lex.common.util.PIDConstants;
import com.infosys.lex.core.exception.BadRequestException;

@Service
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	AttendanceRepo attendanceRepo;

	@Autowired
	ContentService contentService;

	@Autowired
	UserUtilityService userUtilityService;

	public static final String LIVE_STATUS = "Live";
	public static Set<String> SOURCE_FIELDS = new HashSet<>(Arrays.asList(new String[] { "me_totalSessionsCount",
			"creatorContacts", "isIframeSupported", "publishedOn", "description", "mimeType", "locale", "learningMode",
			"skills", "duration", "expiryDate", "creatorDetails", "uniqueUsersCount", "appIcon", "certificationList",
			"collections", "children", "trackContacts", "hasAssessment", "averageRating", "msArtifactDetails",
			"lastUpdatedOn", "viewCount", "contentType", "identifier", "isExternal", "totalRating", "publisherDetails",
			"uniqueLearners", "catalogPaths", "complexityLevel", "unit", "size", "isInIntranet", "name", "isStandAlone",
			"learningObjective", "sourceName", "sourceShortName", "status", "artifactUrl", "displayContentType",
			"downloadUrl", "introductoryVideo", "introductoryVideoIcon", "playgroundResources", "subTitles" }));

	@Override
	public Map<String, Object> verifyUserAttencdance(String rootOrg, String userId, List<String> contentIds)
			throws Exception {
		Map<String, Object> result = new HashMap<>();

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.userId");
		}

		List<String> verifiedIds = attendanceRepo.findByRootorgAndUserIdandContentId(rootOrg, userId, contentIds);

		for (String verifiedId : verifiedIds) {
			result.put(verifiedId, true);
		}

		contentIds.removeAll(verifiedIds);

		for (String unverifiedId : contentIds) {
			result.put(unverifiedId, false);
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> fetchAttendedContent(String rootOrg, String userId, List<String> sourceFields)
			throws Exception {

		if (!userUtilityService.validateUser(rootOrg, userId)) {
			throw new BadRequestException("invalid.userId");
		}

		List<String> contentIds = attendanceRepo.findByRootorgAndUserId(rootOrg, userId);
		sourceFields.addAll(SOURCE_FIELDS);
		return contentService.getMetaByIDListandSource(contentIds,
				sourceFields.toArray(new String[sourceFields.size()]), LIVE_STATUS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> fetchCohorts(String rootOrg, String contentId) {

		List<String> userIds = attendanceRepo.findByRootorgAndContentId(rootOrg, contentId);

		List<Map<String, Object>> cohorts = new ArrayList<>();

		if (!userIds.isEmpty()) {
			Map<String, Object> userData = (Map<String, Object>) userUtilityService.getUsersDataFromUserIds(rootOrg,
					new ArrayList<String>(userIds), Arrays.asList(PIDConstants.EMAIL, PIDConstants.FIRST_NAME,
							PIDConstants.LAST_NAME, PIDConstants.UUID));
			for (String key : userData.keySet()) {
				Map<String, Object> userObj = (Map<String, Object>) userData.get(key);
				String name = userObj.get(PIDConstants.LAST_NAME) == null ? userObj.get(PIDConstants.FIRST_NAME) + ""
						: (userObj.get(PIDConstants.FIRST_NAME) + " " + userObj.get(PIDConstants.LAST_NAME));
				userObj.put("name", name);
				userObj.put("userId", userObj.get(PIDConstants.UUID));
				userObj.remove(PIDConstants.UUID);
				userObj.remove(PIDConstants.FIRST_NAME);
				userObj.remove(PIDConstants.LAST_NAME);
				cohorts.add(userObj);
			}
		}
		return cohorts;
	}

}
