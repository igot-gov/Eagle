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
package com.infosys.lex.continuelearning.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.PagingState;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.infosys.lex.common.service.ContentService;
import com.infosys.lex.continuelearning.bodhi.repo.ContinueLearningPaginationRepository;
import com.infosys.lex.continuelearning.bodhi.repo.ContinueLearningRepository;
import com.infosys.lex.continuelearning.dto.ContinueLearningDTO;
import com.infosys.lex.continuelearning.entities.ContinueLearning;
import com.infosys.lex.continuelearning.entities.ContinueLearningKey;
import com.infosys.lex.continuelearning.validator.ContinueLearningValidator;
import com.infosys.lex.progress.bodhi.repo.ContentProgressModel;
import com.infosys.lex.progress.bodhi.repo.ContentProgressRepository;

@Service
public class ContinueLearningServiceImpl implements ContinueLearningService {

	ContinueLearningRepository continueLearningRepository;
	ContinueLearningPaginationRepository paginationRepository;
	ContentService contentService;
	ContinueLearningValidator validator;
	ContentProgressRepository progress;

	@Autowired
	public ContinueLearningServiceImpl(ContinueLearningRepository continueLearningRepository,
			ContinueLearningPaginationRepository paginationRepository, ContentService contentService,
			ContinueLearningValidator validator, ContentProgressRepository progress) {
		this.continueLearningRepository = continueLearningRepository;
		this.paginationRepository = paginationRepository;
		this.contentService = contentService;
		this.validator = validator;
		this.progress = progress;
	}

	@Override
	public Map<String, Object> upsertLearningData(String rootOrg, String userId, @Valid ContinueLearningDTO data) throws Exception{
		validator.validateUser(rootOrg, userId);

		Map<String, Object> retMap = new HashMap<String, Object>();
		ContinueLearningKey continuelearningKey = new ContinueLearningKey(rootOrg, userId, data.getContextPathId());
		ContinueLearning continueLearning = new ContinueLearning(continuelearningKey, data.getData(),
				data.getDateAccessed(), data.getResourceId());
		continueLearningRepository.save(continueLearning);
		retMap.put("result", "success");
		return retMap;
	}

	@Override
	public Map<String, Object> getLearningData(String rootOrg, String userId, Set<String> sourceFields,
			String contextPathId, String pageSize, String pageState, String isCompleted, String isInIntranet,
			String isStandAlone, String resourceType) throws Exception{
		// Initial list of meta fields
		List<String> requiredFields = new ArrayList<String>(Arrays.asList("appIcon", "artifactUrl", "complexityLevel",
				"contentType", "description", "downloadUrl", "duration", "identifier", "lastUpdatedOn",
				"me_totalSessionsCount", "mediaType", "mimeType", "name", "resourceType", "size", "sourceShortName",
				"status", "averageRating", "totalRating", "isInIntranet", "isStandAlone"));

		validator.validateUser(rootOrg, userId);
		validator.validateContextPathId(contextPathId);
		validator.validatePageSize(pageSize);
		validator.validatePageStatus(pageState);

		Map<String, Object> retMap = new HashMap<String, Object>();

		List<Map<String, Object>> result = paginationRepository.fetchPagedData(rootOrg, userId, contextPathId, pageSize,
				pageState);

		PagingState nextPage = null;
		List<Map<String, Object>> resultMaps = new ArrayList<>();
		if (!result.isEmpty() && result != null) {
			Object pageStateRaw = result.get(0).getOrDefault("nextPage", null);
			nextPage = pageStateRaw != null ? PagingState.fromString(pageStateRaw.toString()) : null;

			int currentlyFetched = (int) result.get(0).get("currentlyFetched");
			int count = currentlyFetched;

			// Add source fields for fetching meta if requested
			if (sourceFields != null && sourceFields.size() > 0) {
				requiredFields.addAll(sourceFields);
			}

			Set<String> ids = new HashSet<>();
			Map<String, Map<String, Object>> metaData = this.fetchMetaData(rootOrg, result, count, ids, requiredFields);

			this.addProgressForUser(rootOrg, userId, metaData, ids);

			for (Map<String, Object> map : result) {
				this.formatProcessedData(rootOrg, resultMaps, metaData, map, ids, requiredFields);

				if (--currentlyFetched == 0) {
					break;
				}
			}
		}

		retMap.put("results", resultMaps);
		retMap.put("pageSize", pageSize);
		retMap.put("pageState", nextPage == null ? -1 : nextPage.toString());
		retMap.put("intranetMode", "greyOut");
		retMap.put("deletedMode", "greyOut");

		return retMap;
	}

	/**
	 * Fetch progress of user for the provided content ids.
	 * 
	 * @param rootOrg
	 * @param userId
	 * @param metaData
	 * @param ids
	 */
	private void addProgressForUser(String rootOrg, String userId, Map<String, Map<String, Object>> metaData,
			Set<String> ids) {
		Map<String, Object> contentToProgress = new HashMap<>();
		List<ContentProgressModel> progressData = progress.getProgress(rootOrg, userId,
				Arrays.asList("Resource", "Collection", "Course", "Learning Path", "Knowledge Artifact"),
				new ArrayList<>(ids));

		for (ContentProgressModel contentProgress : progressData) {
			contentToProgress.put(contentProgress.getPrimaryKey().getContentId(), contentProgress.getProgress());
		}

		for (String contentId : metaData.keySet()) {
			Map<String, Object> meta = metaData.get(contentId);
			meta.put("progress", contentToProgress.getOrDefault(contentId, 0.0F));
		}
	}

	/**
	 * This method is used to process the required data with its meta data
	 * 
	 * @param resultMaps
	 * @param metaData
	 * @param userData
	 * @param requiredFields
	 */
	@SuppressWarnings("serial")
	private void formatProcessedData(String rootOrg, List<Map<String, Object>> resultMaps,
			Map<String, Map<String, Object>> metaData, Map<String, Object> userData, Set<String> ids,
			List<String> requiredFields) {

		Map<String, Object> continueLearningData = new HashMap<>();
		Map<String, Object> retMap = new HashMap<>();

		// Add only those lex_ids which are returned as live
		if (ids.contains(userData.get("contextPathId").toString())) {
			continueLearningData.put("contextPathId", userData.get("contextPathId"));
			continueLearningData.put("resourceId", userData.get("resourceId"));

			// Convert the json that was stored in DB as a string, back to a map using GSON.
			Map<String, Object> gsonMap = new Gson().fromJson((String) userData.get("data"),
					new TypeToken<HashMap<String, Object>>() {
					}.getType());
			continueLearningData.put("data", gsonMap);

			retMap.put("continueLearningData", continueLearningData);

			Map<String, Object> meta = metaData.get(userData.get("contextPathId").toString());

			// Adding meta to the required values after discussion
			Set<String> metaFields = new HashSet<>(requiredFields);
			metaFields.add("progress");

			if (meta != null) {
				for (String metaKey : metaFields) {
					Object metaValue;
					if (meta.containsKey(metaKey)) {

						// Set the ratings for the content
						metaValue = meta.get(metaKey) == null ? "" : meta.get(metaKey);
//						if (metaKey.equalsIgnoreCase("averageRating") || metaKey.equalsIgnoreCase("totalRating")) {
//							metaValue = this.getRatings(rootOrg, metaKey, meta);
//						} else {
//							// If meta is null for a field, set it as blank.
//							metaValue = meta.get(metaKey) == null ? "" : meta.get(metaKey);
//						}
					} else {
						// If meta is not available for a field, set it as blank.
						metaValue = "";
					}
					retMap.put(metaKey, metaValue);
				}
			}
		}
		if (retMap.size() > 0)
			resultMaps.add(retMap);
	}

	/**
	 * This method is used to fetch meta data from ES
	 * 
	 * @param result         for which meta is to be fetched
	 * @param requiredFields
	 * @return Meta in a map format
	 * @throws IOException
	 * @throws ParseException
	 */
	private Map<String, Map<String, Object>> fetchMetaData(String rootOrg, List<Map<String, Object>> result, int count,
			Set<String> ids, List<String> requiredFields) throws IOException, ParseException {

		for (Map<String, Object> map : result) {
			ids.add(map.get("contextPathId").toString());
			// This is done to avoid unnecessary meta calls
			if (--count == 0) {
				break;
			}
		}

		Map<String, Map<String, Object>> meta = contentService.filterAndFetchContentMetaToShow(
				new ArrayList<>(ids), new HashSet<String>(requiredFields));
		
		// Clear out all previous ids
		ids.clear();

		if (meta != null && meta.size() > 0) {
			// Add only valid ones
			meta.keySet().forEach(ids::add);
		}
		return meta;
	}

	/**
	 * Retrieve the rating data for a content, which we have fetched earlier.
	 * 
	 * @param rootOrg
	 * @param metaKey
	 * @param meta
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
//	@SuppressWarnings("unchecked")
//	private Object getRatings(String rootOrg, String metaKey, Map<String, Object> meta) {
//		Object rating = 0;
//		Map<String, Object> ratingMap = (Map<String, Object>) meta.getOrDefault(metaKey, new HashMap<>());
//		if (!ratingMap.isEmpty()) {
//			if (ratingMap.containsKey(rootOrg)) {
//				if (metaKey.equalsIgnoreCase("totalRating")) {
//					rating = Integer.parseInt(ratingMap.get(rootOrg).toString());
//				} else {
//					rating = Float.parseFloat(ratingMap.get(rootOrg).toString());
//				}
//			}
//		}
//		return rating;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.infosys.lex.continuelearning.service.ContinueLearningService#
	 * getLearningDataWithFilters(java.lang.String, java.lang.String, java.util.Set,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Map<String, Object> getLearningDataWithFilters(String rootOrg, String userId, Set<String> sourceFields,
			String contextPathId, String pageSize, String pageState, String isCompleted, String isInIntranet,
			String isStandAlone, List<String> contentType) throws Exception {
		// Initial list of meta fields
		List<String> requiredFields = new ArrayList<String>(Arrays.asList("appIcon", "artifactUrl", "complexityLevel",
				"contentType", "description", "downloadUrl", "duration", "identifier", "lastUpdatedOn",
				"me_totalSessionsCount", "mediaType", "mimeType", "name", "resourceType", "size", "sourceShortName",
				"status", "averageRating", "totalRating", "isInIntranet", "isStandAlone"));

		validator.validateUser(rootOrg, userId);
		validator.validateContextPathId(contextPathId);
		validator.validatePageSize(pageSize);
		validator.validatePageStatus(pageState);

		Map<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> filteredData = new ArrayList<>();

		// Building the filter map from provided filter values
		Map<String, Object> filterMap = this.buildFilterMap(isCompleted, isInIntranet, isStandAlone, contentType);

		PagingState nextPage = null;
		// Keep paginating cassandra until we get equal or more than the size of
		// required data
		// In some cases it can be: pageSize <= data_size < (2 * pageSize)
		while (filteredData.size() < Integer.parseInt(pageSize)) {

			List<Map<String, Object>> result = paginationRepository.fetchPagedData(rootOrg, userId, contextPathId,
					pageSize, nextPage == null ? pageState : nextPage.toString());

			if (result.isEmpty() || result == null) {
				nextPage = null;
				break;
			}

			// Extract pageState from the result
			Object pageStateRaw = result.get(0).getOrDefault("nextPage", null);
			nextPage = pageStateRaw != null ? PagingState.fromString(pageStateRaw.toString()) : null;

			// To limit the meta hit, so that meta is fetched only for the required contents
			int currentlyFetched = (int) result.get(0).get("currentlyFetched");

			// Add source fields for fetching meta if requested
			if (sourceFields != null && sourceFields.size() > 0) {
				requiredFields.addAll(sourceFields);
			}

			Set<String> ids = new HashSet<>();
			Map<String, Map<String, Object>> metaData = this.fetchMetaData(rootOrg, result, currentlyFetched, ids,
					requiredFields);

			this.addProgressForUser(rootOrg, userId, metaData, ids);

			List<Map<String, Object>> formattedData = new ArrayList<>();
			for (Map<String, Object> map : result) {
				formatProcessedData(rootOrg, formattedData, metaData, map, ids, requiredFields);

				if (--currentlyFetched == 0) {
					break;
				}
			}

			// Apply filters if available.
			if (filterMap.size() > 0) {
				this.applyFilter(formattedData, filteredData, filterMap);
			} else {
				filteredData.addAll(formattedData);
			}

			// If nextPage is null, there are no more records
			if (nextPage == null) {
				break;
			}
		}
		retMap.put("results", filteredData);
		retMap.put("pageSize", pageSize);
		retMap.put("pageState", nextPage == null ? -1 : nextPage.toString());
		retMap.put("greyOut", "false");

		return retMap;
	}

	/**
	 * Build a filter map from the provided data,only if requested.(do not make an
	 * entry in filter map if default data is collected)
	 * 
	 * @param isCompleted
	 * @param isInIntranet
	 * @param isStandAlone
	 * @param contentType
	 * @return
	 */
	private Map<String, Object> buildFilterMap(String isCompleted, String isInIntranet, String isStandAlone,
			List<String> contentType) {
		Map<String, Object> filterMap = new HashMap<>();
		if (!"default".equals(isInIntranet)) {
			filterMap.put("isInIntranet", isInIntranet);
		}
		if (!"default".equals(isStandAlone)) {
			filterMap.put("isStandAlone", isStandAlone);
		}
		if (!"default".equals(isCompleted)) {
			filterMap.put("progress", isCompleted);
		}
		if (!Arrays.asList("all").equals(contentType)) {
			filterMap.put("contentType", contentType);
		}
		return filterMap;
	}

	/**
	 * Applies filter(s) to each data. If multiple filters are provided, the data
	 * must pass all the filtration before it can be returned.
	 * 
	 * @param formattedData
	 * @param filteredData
	 * @param filterMap
	 */
	@SuppressWarnings("unchecked")
	private void applyFilter(List<Map<String, Object>> formattedData, List<Map<String, Object>> filteredData,
			Map<String, Object> filterMap) {

		for (Map<String, Object> map : formattedData) {
			// If any of the filter fails, this must be false.
			boolean isFiltered = true;
			for (String condition : filterMap.keySet()) {
				// intranet and standalone filters
				if (Arrays.asList("isInIntranet", "isStandAlone").contains(condition)) {
					if (Boolean.parseBoolean(map.get(condition).toString()) != Boolean
							.parseBoolean(filterMap.get(condition).toString())) {
						isFiltered = false;
						break;
					}
				}
				// progress based filter
				if ("progress".equals(condition)) {
					if (Boolean.parseBoolean(filterMap.get(condition).toString()) == true) {
						if ((float) map.get("progress") < 1.0F) {
							isFiltered = false;
							break;
						}
					} else {
						if ((float) map.get("progress") >= 1.0F) {
							isFiltered = false;
							break;
						}
					}
				}
				// content type based filters
				if ("contentType".equalsIgnoreCase(condition)) {
					if (!((List<String>) filterMap.get(condition)).contains(map.get(condition).toString())) {
						isFiltered = false;
						break;
					}
				}
			}

			if (isFiltered) {
				filteredData.add(map);
			}
		}
	}

	@Override
	public Map<String, Object> getLearningContent(String rootOrg, String userId, Set<String> sourceFields,
												  String contextPathId, String pageSize, String pageState, String isCompleted, String isInIntranet,
												  String isStandAlone, String resourceType) throws Exception{
		// Initial list of meta fields
		List<String> requiredFields = new ArrayList<String>(Arrays.asList("appIcon", "artifactUrl", "complexityLevel",
				"contentType", "description", "downloadUrl", "duration", "identifier", "lastUpdatedOn",
				"me_totalSessionsCount", "mediaType", "mimeType", "name", "resourceType", "size", "sourceShortName",
				"status", "averageRating", "totalRating", "isInIntranet", "isStandAlone"));

		validator.validateUser(rootOrg, userId);
		validator.validateContextPathId(contextPathId);
		validator.validatePageSize(pageSize);
		validator.validatePageStatus(pageState);

		Map<String, Object> retMap = new HashMap<String, Object>();

		List<Map<String, Object>> result = paginationRepository.fetchPagedData(rootOrg, userId, contextPathId, pageSize,
				pageState);

		List<String> contentIds = new ArrayList<>();
		for( Map resMap: result){
			contentIds.add(resMap.get("resourceId").toString());
		}

		retMap.put("resource_ids", contentIds);
		retMap.put("pageSize", pageSize);
		//retMap.put("pageState", nextPage == null ? -1 : nextPage.toString());
		retMap.put("intranetMode", "greyOut");
		retMap.put("deletedMode", "greyOut");

		return retMap;
	}
}
