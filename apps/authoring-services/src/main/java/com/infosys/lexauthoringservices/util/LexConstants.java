package com.infosys.lexauthoringservices.util;

public class LexConstants {

	public static enum TagType {
		Category("Category"), Track("Track"), SubTrack("SubTrack"), SubSubTrack("SubSubTrack");

		private String indexName;

		private TagType(String name) {
			this.indexName = name;
		}

		public String getType() {
			return this.indexName;
		}
	}

	public static enum EsIndex {
		sunbird("searchindex"), sunbirdDataAudit("sunbirddataaudit"), bodhi("contentindex"),
		staging("contentindex_14"), ek_step("_contentindex"), Topic("_topic"),
		lex_user_feedback("_feedback"), authoring_tool("contentindex_authoring_tool"), skills("skillsindex"),
		unit("unitindex"), authoring_tool_bkup("contentindex_authoring_tool_backup_23_4"), client("clientindex"),
		bodhi_ui("bodhi_ui_index"), searchIndex("searchall"), mandatory_fields("mandatory_fields");

		private String indexName;

		private EsIndex(String name) {
			this.indexName = name;
		}

		public String getIndexName() {
			return this.indexName;
		}
	}

	public static enum EsType {
		course("course"), content("content"), user("user"), organisation("org"), usercourses("usercourses"),
		usernotes("usernotes"), history("history"), userprofilevisibility("userprofilevisibility"),
		feedback("feedback"), resource("resource"), skills("skills"), unit("units"), client("clienttype"),
		searchType("searchresources"), attributes("attributes");

		private String typeName;

		private EsType(String name) {
			this.typeName = name;
		}

		public String getTypeName() {
			return this.typeName;
		}
	}

	public static enum Status {
		Draft("Draft"), Live("Live"), InReview("InReview"), Reviewed("Reviewed"), Processing("Processing"),Deleted("Deleted"),Expired("Expired"),MarkedForDeletion("MarkedForDeletion"),UnPublish("Unpublished");

		private String status;

		private Status(String name) {
			this.status = name;
		}

		public String getStatus() {
			return this.status;
		}
	}

	public static enum ContentType {
		LearningPath("Learning Path"), Course("Course"), Collection("Collection"), Resource("Resource"),
		KnowledgeArtifact("Knowledge Artifact"), LeadershipReport("LeadershipReport"),
		KnowledgeBoard("Knowledge Board"), Channel("Channel"),LearningJourney("Learning Journey");

		private String indexName;

		private ContentType(String name) {
			this.indexName = name;
		}

		public String getContentType() {
			return this.indexName;
		}
	}

	public static enum Relation {
		HAS_SUB_CONTENT("Has_Sub_Content");

		private String relation;

		private Relation(String relation) {
			this.relation = relation;
		}

		public String get() {
			return this.relation;
		}
	}

	public static final String MARKED_FOR_DELETION = "Marked For Deletion";
	public static final String SUB_FEATURE = "subFeature";
	public static final String VERSION_KEY = "versionKey";
	public static final String EXCLUSIONS = "exclusions";
	public static final String ISEXTEND = "isExtend";
	public static final String ENTITY = "entity";
	public static final String TRANSLATION_OF = "isTranslationOf";
	public static final String HAS_TRANSLATION = "hasTranslations";
	public static final String LOCALE = "locale";
	public static final String APPNAME = "appName";
	public static final String APPURL = "appUrl";
	public static final String SHARED_CONTENT = "Shared";
	public static final String ISEXTERNAL = "isExternal";
	public static final String FIELDS = "fields";
	public static final String FIELDS_PASSED = "fieldsPassed";
	public static final String COMMENT = "comment";
	public static final String MODE = "mode";
	public static final String DATE = "date";
	public static final String ACTOR = "actor";
	public static final String EDITORS = "editors";
	public static final String PRICE = "price";
	public static final String AUTHOR = "author";
	public static final String PLAYLIST = "Playlist";
	public static final String K_BOARD = "Knowledge Board";
	public static final String OPERATION = "operation";
	public static final String ACCESS_PATHS = "accessPaths";
	public static final String COMBINED_ACCESS_PATHS = "combinedAccessPaths";
	public static final String VISIBILITY = "visibility";
	public static final String RESOURCE_TYPE = "resourceType";
	public static final String CONTENT_TYPE = "contentType";
	public static final String WORK_FLOW = "work_flow";
	public static final String COMPLEXITY_TYPE = "complexityLevel";
	public static final String SOURCE_NAME = "sourceName";
	public static final String UNIT = "unit";
	public static final String AUDIENCE = "audience";
	public static final String COPYRIGHT = "copyright";
	public static final String PORTAL_OWNER = "portalOwner";
	public static final String IDEAL_SCREEN_SIZE = "idealScreenSize";
	public static final String RESOURCE_CATEGORY = "resourceCategory";
	public static final String COURSE_TYPE = "courseType";
	public static final String TRACK = "track";
	public static final String CATEGORY = "Category";
	public static final String SUB_TRACK = "SubTrack";
	public static final String NEW_SERVICES_THEME = "NewServicesTheme";
	public static final String NEW_SERVICES = "NewServices";
	public static final String VERSION_DATE = "versionDate";
	public static final String TAG = "tag";
	public static final String ETA_TRACK = "etaTrack";
	public static final String LEARNING_TRACK = "learningTrack";
	public static final String LEX = "rootOrg";
	public static final String EDIT = "edit";
	public static final String LEX_URL = "https://";
	public static final String DRAFT = "Draft";
	public static final String LIVE = "Live";
	public static final String INDEX = "index";
	public static final String CERTIFICATION_LIST = "certificationList";
	public static final String COMMENTS = "comments";
	public static final String PLAYGROUND_RESOURCES = "playgroundResources";
	public static final String IDENTIFIER = "identifier";
	public static final String GROUP = "group";
	public static final String ROLES = "roles";
	public static final String ISSTANDALONE = "isStandAlone";
	public static final String NAME = "name";
	public static final String LEARNING_OBJECTIVE = "learningObjective";
	public static final String ARTIFACT_URL = "artifactUrl";
	public static final String CREATOR_CONTACTS = "creatorContacts";
	public static final String SERVER_IP = "server_ip";
	public static final String BODHI_CONTENT_PORT = "bodhi_content_port";
	public static final String IP = "ip";
	public static final String PORT = "content_port";
	public static final String COLLECTION = "Collection";
	public static final String ASSESSMENT = "Assessment";
	public static final String QUIZ = "Quiz";
	public static final String CLASSROOM = "Classroom";
	public static final String QUERY = "query";
	public static final String _LEX = "lex_";
	public static final String FILTER = "filters";
	public static final String ADMIN = "admin";
	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";
	public static final String IL_UNIQUE_ID = "IL_UNIQUE_ID";
	public static final String CREATED_BY = "createdBy";
	public static final String CREATOR = "creator";
	public static final String STATUS = "status";
	public static final String RESOURCE = "Resource";
	public static final String DOT = ".";
	public static final String KEYWORD = "keyword";
	public static final String CREATOR_DEATILS = "creatorDetails";
	public static final String LAST_UPDATED = "lastUpdatedOn";
	public static final String DESC = "description";
	public static final String MIME_TYPE = "mimeType";
	// public static final String EMAIL = "email";
	public static final String MIME_TYPE_HTML = "application/html";
	public static final String MIME_TYPE_HTMLQUIZ = "application/htmlpicker";
	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_DNDQUIZ = "application/drag-drop";
	public static final String MIME_TYPE_HANDSONQUIZ = "application/integrated-hands-on";
	public static final String MIME_TYPE_WEB = "application/web-module";
	public static final String MIME_TYPE_QUIZ = "application/quiz";
	public static final String MIME_TYPE_HANDSON = "application/integrated-hands-on";
	public static final String FILE_NAME = "fileName";
	public static final String RESOURCE_ID = "resourceId";
	public static final String SKILL = "skill";
	public static final String ID = "id";
	public static final String CHILDREN = "children";
	public static final String TYPE = "type";
	public static final String VALUE = "value";
	public static final String TITLE = "title";
	public static final String KEYWORDS = "keywords";
	public static final String PASS_PERCENTAGE = "passPercentage";
	public static final String REVIEWED_BY = "reviewedBy";
	public static final String PUBLISHED_BY = "publishedBy";
	public static final String PUBLISHER_DETAILS = "publisherDetails";
	public static final String SOFTWARE_REQUIREMENTS = "softwareRequirements";
	public static final String SYSTEM_REQUIREMENTS = "systemRequirements";
	public static final String APP_ICON = "appIcon";
	public static final String THUMBNAIL = "thumbnail";
	public static final String POSTER_IMAGE = "posterImage";
	public static final String INTRODUCTORY_VIDEO = "introductoryVideo";
	public static final String INTRODUCTORY_VIDEO_ICON = "introductoryVideoIcon";
	public static final String PRE_CONTENTS = "preContents";
	public static final String POST_CONTENTS = "postContents";
	public static final String CLIENTS = "clients";
	public static final String SUBTITLES = "subtitles";
	public static final String REFERENCES = "references";
	public static final String K_ARTIFACTS = "kArtifacts";
	public static final String TRACK_CONTACT_DETAILS = "trackContacts";
	public static final String ROOT_ORG = "rootOrg";
	public static final String ROOT_ORG_CASSANDRA = "root_org";
	public static final String ORG = "org";
	public static final String RESPONSE = "response";
	public static final String OPTIONAL = "optional_fields";
	public static final String MANDATORY = "mandatory_fields";
	public static final String ERROR = "error";
	public static final String DATA_LIST = "dataList";
	public static final String SUB_TITLES = "subTitles";
	public static final String DURATION = "duration";
	public static final String CHILD_TITLE = "childrenTitle";
	public static final String CHILD_DESC = "childrenDescription";
	public static final String ORDER = "order";
	public static final String EXTERNAL = "External";
	public static final String IMG_SUFFIX = ".img";
	public static final String AUTHORING_DISABLED = "authoringDisabled";
	public static final String META_EDIT_DISABLED = "isMetaEditingDisabled";
	public static final String NODE = "node";
	public static final String PARENT = "parent";
	public static final String CHILD = "child";
	public static final String CHILD_RELATION = "childRelation";
	public static final String PARENT_RELATION = "parentRelation";
	public static final String NODE_LIST = "node_list";
	public static final String CONTENT = "content";
	public static final String WEB_HOST_AUTH_DIR = "/hosted/auth%2F";
	public static final String NODES_MODIFIED = "nodesModified";
	public static final String HIERARCHY = "hierarchy";
	public static final String METADATA = "metadata";
	public static final String TAGS = "tags";
	public static final String SKILLS = "skills";
	public static final String USER_TYPE = "userType";
	public static final String END_NODE_ID = "endNodeId";
	public static final String START_NODE_ID = "startNodeId";
	public static final String LEARNING_CONTENT_NODE_TYPE = "LEARNING_CONTENT";
	public static final String NODE_TYPE = "nodeType";
	public static final String COLLECTIONS = "collections";
	public static final String TRACK_CREATOR_PUBLISHER_OBJECT_UUID = "id";
	public static final String VALID_TILL = "validTill";
	public static final String EXPIRY_DATE = "expiryDate";
	public static final String DOWNLOAD_URL = "downloadUrl";
	public static final String TAGS_PATH_DELIMITER = ">";
	public static final String SUBMITTER_DETAILS = "submitterDetails";
	public static final String CONCEPTS = "concepts";
	public static final String PLAG_SCAN = "plagScan";
	public static final String CATALOG = "catalog";
	public static final String IS_STAND_ALONE = "isStandAlone";
	public static final String LEARNING_MODE = "learningMode";
	public static final String FILETYPE = "fileType";
	public static final String IS_SEARCHABLE = "isSearchable";
	public static final String UNITS = "Units";
	public static final String PUBLISHED_ON = "publishedOn";
	public static final String LAST_PUBLISHED_ON = "lastPublishedOn";
	public static final String SIZE = "size";
	public static final String TRANSCODING = "transcoding";
	public static final String MS_ARTIFACT_DETAILS = "msArtifactDetails";
	public static final String VIDEO_ID = "videoId";
	public static final String CHANNEL_ID = "channelId";
	public static final String TRANSCODE_STATUS = "status";
	public static final String RETRYCOUNT = "retryCount";
	public static final String TRANSCODED_ON = "lastTranscodedOn";
	public static final String DIMENSION = "Dimension";
	public static final String OFFERING_MODE = "Offering Mode";
	public static final String CATEGORY_TYPE = "categoryType";
	public static final String REGION = "region";
	public static final String RECOREGION = "recoRegion";
	public static final String LABELS = "labels";
	public static final String REASON = "reason";
	public static final String CHILDREN_CLASSIFIERS = "childrenClassifiers";
	public static final String ADDED_ON = "addedOn";
	public static final String HAS_ASSESSMENT = "hasAssessment";
}
