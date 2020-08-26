/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.searchv6.util;

import java.util.*;

public class SearchConstantsv6 {

	public static final String ML_SEARCH_TEMPLATE = "mlsearchtemplatev6";
	public static final String FILTERS = "filters";
	public static final String IS_STAND_ALONE = "isStandAlone";
	public static final String USER_ID = "userId";
	public static final String ACCESS_PATHS = "accessPaths";
	public static final String ROOT_ORG = "rootOrg";
	public static final String ORG = "org";

	public static final String DURATION_SHORT = "-inf-20";
	public static final String DURATION_MEDIUM = "20-60";
	public static final String DURATION_LONG = "60-180";
	public static final String DURATION_DETAILED = "180-inf";
	public static final Long DURATION_SHORT_MAX = 1200l;
	public static final Long DURATION_MEDIUM_MIN = 1200l;
	public static final Long DURATION_MEDIUM_MAX = 3600l;
	public static final Long DURATION_LONG_MIN = 3600l;
	public static final Long DURATION_LONG_MAX = 10800l;
	public static final Long DURATION_DETAILED_MIN = 10800l;
	public static final String DURATION_SHORT_DISPLAY_NAME = "Less than 20 mins";
	public static final String DURATION_MEDIUM_DISPLAY_NAME = "20 to 60 mins";
	public static final String DURATION_LONG_DISPLAY_NAME = "60 to 180 mins";
	public static final String DURATION_DETAILED_DISPLAY_NAME = "More than 180 mins";

	public static final String FILTER_CONCEPTS_FIELD_KEY = "concepts";
	public static final String FILTER_CONCEPTS_IDENTIFIER_FIELD_KEY = "concepts.identifier";
	public static final String FILTER_CONCEPTS_NAME_FIELD_KEY = "concepts.name.keyword";
	public static final String FILTER_SOURCE_SHORT_NAME_RAW_FIELD_KEY = "sourceShortName.keyword";
	public static final String FILTER_SOURCE_SHORT_NAME_FIELD_KEY = "sourceShortName";
	public static final String FILTER_RESOURCE_CATEGORY_FIELD_KEY = "resourceCategory";
	public static final String FILTER_CONTENT_TYPE_FIELD_KEY = "contentType";
	public static final String FILTER_FILE_TYPE_FIELD_KEY = "fileType";
	public static final String FILTER_DURATION_FIELD_KEY = "duration";
	public static final String FILTER_COMPLEXITY_LEVEL_FIELD_KEY = "complexityLevel";
	public static final String FILTER_CATALOG_PATHS_FIELD_KEY = "catalogPaths";
	public static final String FILTER_LAST_UPDATED_ON_FIELD_KEY = "lastUpdatedOn";
	public static final String FILTER_COLLECTIONS_IDENTIFIER_FIELD_KEY = "collections.identifier";
	public static final String FILTER_RESOURCE_TYPE_FIELD_KEY = "resourceType";
	public static final String FILTER_KEYWORDS_FIELD_KEY = "keywords";
	public static final String FILTER_KEYWORDS_RAW_FIELD_KEY = "keywords.keyword";
	public static final String FILTER_IS_EXTERNAL_FIELD_KEY = "isExternal";
	public static final String FILTER_LEARNING_MODE_FIELD_KEY = "learningMode";
	public static final String FILTER_CREATOR_CONTACTS_FIELD_KEY = "creatorContacts";
	public static final String FILTER_TRACK_CONTACTS_FIELD_KEY = "trackContacts";
	public static final String FILTER_PUBLISHER_DETAILS_FIELD_KEY = "publisherDetails";
	public static final String FILTER_UNIT_FIELD_KEY = "unit";
	public static final String FILTER_STATUS_FIELD_KEY = "status";
	public static final String FILTER_SKILLS_FIELD_KEY = "skills";
	public static final String FILTER_REGION_FIELD_KEY = "region";
	public static final String FILTER_JOB_PROFILE_FIELD_KEY = "jobProfile";
	public static final String FILTER_INSTANCE_CATALOG_FIELD_KEY = "instanceCatalog";
	public static final String FILTER_EXCLUSIVE_CONTENT_FIELD_KEY = "exclusiveContent";

	public static final List<String> LIST_FILTER_FIELD_KEYS = new ArrayList<>(Arrays.asList(FILTER_UNIT_FIELD_KEY,FILTER_STATUS_FIELD_KEY,FILTER_PUBLISHER_DETAILS_FIELD_KEY,FILTER_TRACK_CONTACTS_FIELD_KEY,FILTER_CREATOR_CONTACTS_FIELD_KEY,FILTER_LEARNING_MODE_FIELD_KEY, FILTER_IS_EXTERNAL_FIELD_KEY,FILTER_CONTENT_TYPE_FIELD_KEY,FILTER_RESOURCE_TYPE_FIELD_KEY,FILTER_SOURCE_SHORT_NAME_FIELD_KEY,FILTER_RESOURCE_CATEGORY_FIELD_KEY,FILTER_FILE_TYPE_FIELD_KEY,FILTER_DURATION_FIELD_KEY,FILTER_COMPLEXITY_LEVEL_FIELD_KEY, FILTER_CATALOG_PATHS_FIELD_KEY,FILTER_LAST_UPDATED_ON_FIELD_KEY,FILTER_KEYWORDS_FIELD_KEY));
	public static final Map<String, List<String>> aggAndFilterNamesMap = new HashMap<>();

	public static final String CATALOG_PATHS_AGGS_KEY = "catalogPaths_aggs";
	public static final String DURATION_AGGS_KEY = "duration_aggs";
	public static final String SOURCE_SHORT_NAME_AGGS_KEY = "sourceShortName_aggs";
	public static final String COMPLEXITY_LEVEL_AGGS_KEY = "complexityLevel_aggs";
	//	public static final String FILE_TYPE_AGGS_KEY = "fileType_aggs";
	public static final String CONTENT_TYPE_AGGS_KEY = "contentType_aggs";
	public static final String CONCEPTS_AGGS_KEY = "concepts_aggs";
	public static final String LAST_UPDATED_ON_AGGS_KEY = "lastUpdatedOn_aggs";
	public static final String RESOURCE_TYPE_AGGS_KEY = "resourceType_aggs";
	public static final String RESOURCE_CATEGORY_AGGS_KEY = "resourceCategory_aggs";
	public static final String IS_EXTERNAL_AGGS_KEY = "isExternal_aggs";
	public static final String LEARNING_MODE_AGGS_KEY = "learningMode_aggs";
	public static final String UNIT_AGGS_KEY = "unit_aggs";
	public static final String REGION_AGGS_KEY = "region_aggs";
	public static final String JOB_PROFILE_AGGS_KEY = "jobProfile_aggs";
	public static final String LABELS_AGGS_KEY = "labels_aggs";
	public static final String EXCLUSIVE_CONTENT_AGGS_KEY = "exclusiveContent_aggs";

	public static final HashMap<String,List<String>> FIELD_DISPLAYNAME_PAIR = new HashMap<>();
	public static final String SORT_ASC_ORDER = "asc";
	public static final String SORT_DESC_ORDER = "desc";
	public static final String RESOURCE = "Resource";
	public static final String COURSE = "Course";
	public static final String LEARNING_MODULE = "Collection";
	public static final String LEARNING_PATH = "Learning Path";
	public static final String IS_ACCESS_CONTROL_ENABLED = "accessControlEnabled";
	public static final String TEMPLATE_FILTER_PREFIX = "filter";
	public static final String TEMPLATE_FILTER_SUFFIX = "Val";
	public static final String TEMPLATE_AGGS_SUFFIX = "Aggs";
	public static final String TEMPLATE_AGGS_NAME_SUFFIX = "_aggs";
	public static final String CONCEPTS_IDENTIFIER_AGGS_KEY = "concepts_identifier_aggs";
	public static final String CONCEPTS_NAME_AGGS_KEY = "concepts_name_aggs";
	public static final String TAGS_PATH_DELIMITER = ">";
	public static final String FILTER_IDENTIFIER_FIELD_KEY = "identifier";
	public static final String IDENTIFIER_QUERY_DELIMITER = ",";
	public static final String SEARCH_INDEX_LOCALE_DELIMITER = "_";
	public static final String FILTER_LOCALE = "locale";
	public static final String VIEW_COUNT = "viewCount";
	public static final String AVERAGE_RATING = "averageRating";
	public static final String UNIQUE_USERS_COUNT = "uniqueUsersCount";
	public static final String TOTAL_RATING = "totalRating";
	public static final String TEMPLATE_AND_FILTER_PREFIX = "";
	public static final String TEMPLATE_NOT_FILTER_PREFIX = "Not";
	public static final String SEARCH_INDEX_NAME_PREFIX = "mlsearch";
	public static final String IMAGE_POSTFIX = ".img";
	public static final String TEMPLATE_AGGS_ORDER_SUFFIX = "AggsOrder";

	static {
		FIELD_DISPLAYNAME_PAIR.put(CATALOG_PATHS_AGGS_KEY, Arrays.asList("catalogPaths","Catalog"));
		FIELD_DISPLAYNAME_PAIR.put(DURATION_AGGS_KEY, Arrays.asList("duration","Duration"));
		FIELD_DISPLAYNAME_PAIR.put(SOURCE_SHORT_NAME_AGGS_KEY, Arrays.asList("sourceShortName","Source"));
		FIELD_DISPLAYNAME_PAIR.put(COMPLEXITY_LEVEL_AGGS_KEY, Arrays.asList("complexityLevel","Level"));
		FIELD_DISPLAYNAME_PAIR.put(CONTENT_TYPE_AGGS_KEY, Arrays.asList("contentType","Content Type"));
		FIELD_DISPLAYNAME_PAIR.put(CONCEPTS_AGGS_KEY, Arrays.asList("concepts","Concepts"));
		FIELD_DISPLAYNAME_PAIR.put(LAST_UPDATED_ON_AGGS_KEY, Arrays.asList("lastUpdatedOn","Published Date"));
		FIELD_DISPLAYNAME_PAIR.put(RESOURCE_TYPE_AGGS_KEY, Arrays.asList("resourceType","Format"));
		FIELD_DISPLAYNAME_PAIR.put(RESOURCE_CATEGORY_AGGS_KEY, Arrays.asList("resourceCategory","Category"));
		FIELD_DISPLAYNAME_PAIR.put(IS_EXTERNAL_AGGS_KEY, Arrays.asList("isExternal","External"));
		FIELD_DISPLAYNAME_PAIR.put(LEARNING_MODE_AGGS_KEY, Arrays.asList("learningMode","Mode"));
		FIELD_DISPLAYNAME_PAIR.put(UNIT_AGGS_KEY, Arrays.asList("unit","Unit"));
		FIELD_DISPLAYNAME_PAIR.put(JOB_PROFILE_AGGS_KEY, Arrays.asList("jobProfile","Job Profile"));
		FIELD_DISPLAYNAME_PAIR.put(REGION_AGGS_KEY, Arrays.asList("region","Region"));
		FIELD_DISPLAYNAME_PAIR.put(LABELS_AGGS_KEY, Arrays.asList("labels","Labels"));
		FIELD_DISPLAYNAME_PAIR.put(EXCLUSIVE_CONTENT_AGGS_KEY, Arrays.asList("exclusiveContent","Costs"));


		aggAndFilterNamesMap.put(CONCEPTS_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(CONCEPTS_AGGS_KEY));
		aggAndFilterNamesMap.put(CONTENT_TYPE_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(CONTENT_TYPE_AGGS_KEY));
		aggAndFilterNamesMap.put(COMPLEXITY_LEVEL_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(COMPLEXITY_LEVEL_AGGS_KEY));
		aggAndFilterNamesMap.put(SOURCE_SHORT_NAME_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(SOURCE_SHORT_NAME_AGGS_KEY));
		aggAndFilterNamesMap.put(CATALOG_PATHS_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(CATALOG_PATHS_AGGS_KEY));
		aggAndFilterNamesMap.put(DURATION_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(DURATION_AGGS_KEY));
		aggAndFilterNamesMap.put(REGION_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(REGION_AGGS_KEY));
		aggAndFilterNamesMap.put(LABELS_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(LABELS_AGGS_KEY));
		aggAndFilterNamesMap.put(EXCLUSIVE_CONTENT_AGGS_KEY,FIELD_DISPLAYNAME_PAIR.get(EXCLUSIVE_CONTENT_AGGS_KEY));
	}

	public SearchConstantsv6() {
	}
	
	
}