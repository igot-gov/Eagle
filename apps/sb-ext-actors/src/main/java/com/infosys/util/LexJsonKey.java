/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.util;

import java.text.SimpleDateFormat;

/**
 * Created by Krishnendu_C on 8/14/2018.
 */
public class LexJsonKey {

	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat formatterCass = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
	public static SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat inputFormatterDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String TOPIC_USER_ID = "user_id";
	public static final String TOPIC_NAMES = "topic_names";
	public static final String DATE_CREATED = "date_created";
	public static final String DATE_MODIFIED = "date_modified";
	public static final String DATE_DOWNLOADED = "date_downloaded";
	public static final String TOPICS_DB = "topicsDb";
	public static final String USER_FEEDBACK_DB = "feedbackDb";
	public static final String USER_TOPICS_MAPPING_DB = "userTopicsMappingDb";
	public static final String USER_ROLES_DB = "userRolesDb";
	public static final String USER_CONTENT_DOWNLOAD_DB = "userContentDownloadDb";
	public static final String USER_TERMS_CONDITION_DB = "userTermsConditionDb";
	public static final String AUTHOR_TERMS_CONDITION_DB = "authorTermsConditionDb";
	public static final String TERMS_CONDITION_DB = "termsConditionDb";
	public static final String BODHI_DB_KEYSPACE = "bodhi";
	public static final String RESOURCES = "resources";
	public static final String LEX= "lex";
	public static final String VALUE = "value";
	public static final String KEYWORD = "keyword";
	public static final String INREVIEW = "InReview";
	public static final String DRAFT = "Draft";
	public static final String LIVE = "Live";
	public static final String PUBLISH = "Published";
	public static final String PENDING = "Pending";
	public static final String PARENTS = "parents";
	public static final String LEARNING_PATH = "learning path";
	public static final String LeaderboardTable = "leaderboard";
	public static final String LeaderboardRankTable = "leaderboard_rank";
	public static final String totalPoints = "total_points";
	public static final String AllBadges = "badge";
	public static final String UserBadges = "user_badges";
	public static final String USER_Learning_Goals_Table = "user_learning_goals";
	public static final String Common_Learning_Goals_Table = "common_learning_goals";
	public static final String Assessments_By_Course_Result_Table = "user_assessment_top_performer";
	public static final String Educators_Table = "educators";
	public static final String CONTINUE_LEARNING_TABLE = "continue_learning";
	public static final String RESOURCE_COMPLETION_TABLE = "resource_visit_completion";
	public static final String REGION = "region";
	public static final String DISCIPLINE_ID = "disciplineId";
	public static final String DATE_ACCEPTED = "date_accepted";
	public static final String ACCEPTED_DATE = "acceptedDate";
	public static final String Mongo_host = "mongodb.host";
	public static final String Mongo_port = "mongodb.port";
	public static final String Mongo_authentication_database = "mongodb.database";
	public static final String Mongo_username = "mongodb.username";
	public static final String Mongo_password = "mongodb.password";
	public static final String Mongo_Telemetry_DB = "telemetry";
	public static final String DOC_NAME = "docName";
	public static final String DOC_FOR = "docFor";
	public static final String DOC_TEXT = "docText";
	public static final String TERMS_ACCEPTED = "termsAccepted";
	public static final String TERMS_AND_CONDTIONS = "termsAndConditions";
	public static final String APPLICATION_PROPERTIES_TABLE = "app_config";
	public static final String IS_ACCEPTED = "isAccepted";
	public static final String Educator_Course_Identifier = "source_id";
	public static final String Assessment_Result_Identifier = "parent_source_id";
	public static final String COMMENTS = "comments";
	public static final String STAGE_ICONS = "stageIcons";
	public static final String EDITOR_STATE = "editorState";
	public static final String Telemetry_Progress_Collection = "user_content_progress_collection";
	public static final String Telemetry_Execution_Collection = "batch_execution_collection";
	public static final String Emails = "emails";
	public static final String USER_TYPE = "userType";
	public static final String ACCEPTED_VERSION = "acceptedVersion";
	public static final String REVIEWED = "Reviewed";
	public static final String USER_PLAYLIST_DB = "user_playlist";
	public static final String CONTENT_TAGS_DB = "content_tag";
	public static final String RESOURCE = "Resource";
	public static final String RECENT_PLAYLIST_DB = "playlist_recent";
	public static final String SHARED_PLAYLIST_DB = "playlist_shared";
	public static final String SMTP_HOST = "smtp.host";
	public static final String SMTP_PORT = "smtp.port";
	public static final String USER_ROLES = "user_roles";
	public static final String CONTENT_SERVICE_HOST = "content.service.host";
	public static final String Content_Port = "bodhi_content_port";
	public static final String Assessment_Master = "user_assessment_master";
	public static final String Quiz_Master = "user_quiz_master";
	public static final String USER_SHARED_GOALS = "user_shared_goals";
	public static final String SHARED_GOALS_TRACKER = "mv_shared_goals_tracker";
	public static final String USER_GOALS_TRACKER = "mv_user_goals_tracker";
	public static final String HTTP_PROTOCOL = "http";
	public static final String UPDATE_BY_QUERY = "_update_by_query";
	public static final String IP_FILE = "contentAPIUrl.properties";
	public static final String IDENTIFIER_KEYWORD = "identifier.keyword";
	public static final String TERMS = "terms";
	public static final String INLINE = "inline";
	public static final String CONTEXT_SRC = "ctx._source";
	public static final String VIEW_COUNT_NUMBER = "1";
	public static final String SESSIONS_COUNT = "me_totalSessionsCount";
	public static final String SCRIPT = "script";
	public static final String SUNBIRD_ES_REST_PORT = "sunbird_es_rest_port";
	public static final String TOPICS = "topics";
	public static final String MASTER_VALUES = "master_valuesDB";
	public static final String USER_SEPERATION = "user_separation";
	public static final String USER_PERSONALROLE = "user_personalrole";
	public static final String EMAIL_VALIDATE_OPTIONS = "email_validate_options";
	public static final String MV_USER = "mv_user";
	public static final String ErrorUserBadges = "user_badge_error";
	public static final String ASSESSMENT_BY_CONTENT_USER = "assessment_by_content_user";

	public static final String USER_TOPICS_MAPPING = "user_topics_mapping";

	public static final String EMAIL = "email";
	public static final String USER_ACCESS = "user_access";

	public static final String SURVEY_DB = "survey";
	public static final String EDUCATOR_GROUP_MAPPINGS = "educator_group_mapping";
	public static final String GROUP_USER_MAPPING = "group_user_mapping";

	public static final String USER_PREFERENCES = "user_preferences";
	public static final String RECENT_PLAYLIST_MV = "mv_recent_playlist";

	public LexJsonKey() {

	}
}
