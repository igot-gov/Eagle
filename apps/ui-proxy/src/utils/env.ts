const env = process.env
const HTTPS_HOST = env.HTTPS_HOST || 'https://10.177.157.30'
export const CONSTANTS = {
  ACCESS_CONTROL_API_BASE: env.ACCESS_CONTROL_API_BASE || env.SBEXT_API_BASE,
  ANALYTICS_TIMEOUT: env.ANALYTICS_TIMEOUT || 10000,
  APP_ANALYTICS: env.LA_HOST_PROXY || 'http://10.177.157.29',
  APP_CONFIGURATIONS: env.APP_CONFIGURATIONS || '/app-config',
  APP_LOGS: env.APP_LOGS || '/logs',
  ATTENDANCE_API_BASE: env.ATTENDANCE_API_BASE || env.SB_EXT_API_BASE_2,
  AUTHORING_BACKEND: env.SUNBIRD_BACKEND || 'http://10.177.63.204:3011',
  BADGE_API_BASE: env.BADGE_API_BASE || env.SB_EXT_API_BASE_2,
  NETWORK_HUB_SERVICE_BACKEND: env.NETWORK_HUB_SERVICE_BACKEND || 'http://localhost:3013',

  CASSANDRA_IP: env.CASSANDRA_IP || 'localhost:9042',
  CASSANDRA_KEYSPACE: env.CASSANDRA_KEYSPACE || 'bodhi',
  CASSANDRA_PASSWORD: env.CASSANDRA_PASSWORD || '',
  CASSANDRA_USERNAME: env.CASSANDRA_USERNAME || '',
  CONTENT_API_BASE: env.CONTENT_API_BASE || 'http://10.177.157.30:5903',
  CONTENT_HIERARCHY: env.CONTENT_HIERARCHY || 'http://10.177.61.54:5903/hierarchy',
  CONTENT_META_FETCH_API_BASE: env.CONTENT_META_FETCH_API_BASE || 'http://10.177.22.26:5906',
  CONTENT_STORE_DEVELOPMENT_BASE: 'https://lex-dev.infosysapps.com',
  COUNTER: 'http://10.177.157.30:5903',
  DEFAULT_ORG: env.DEFAULT_ORG || 'dopt',
  DEFAULT_ROOT_ORG: env.DEFAULT_ROOT_ORG || 'igot',
  ES_BASE: env.ES_BASE || 'http://10.177.157.30:9200',
  ES_PASSWORD: env.ES_PASSWORD || 'Infy@123+',
  ES_USERNAME: env.ES_USERNAME || 'elastic',
  FEEDBACK_API_BASE: env.FEEDBACK_API_BASE || env.SB_EXT_API_BASE_2,
  GAMIFICATION_API_BASE: env.GAMIFICATION_API_BASE || 'http://10.177.63.118',
  GOALS_API_BASE: env.GOALS_API_BASE || env.SB_EXT_API_BASE_2,
  HIERARCHY_API_BASE: env.HIERARCHY_API_BASE,
  HTTPS_HOST,
  IAP_BACKEND_AUTH: env.IAP_BACKEND_AUTH || 'https://lex-deviap.infosysapps.com/backend',
  IAP_CLIENT_SECRET: env.IAP_CLIENT_SECRET,
  IAP_CODE_API_BASE: env.IAP_CODE_API_BASE || 'https://lex-iap.infosysapps.com',
  IAP_PROFILE_API_BASE: env.IAP_PROFILE_API_BASE || 'https://lex-iap.infosysapps.com',
  ILP_FP_PROXY: env.ILP_FP_PROXY || 'http://10.177.62.155',
  INTEREST_API_BASE: env.INTEREST_API_BASE || env.SB_EXT_API_BASE_2,

  IS_CASSANDRA_AUTH_ENABLED: Boolean(env.CASSANDRA_AUTH_ENABLED),
  IS_DEVELOPMENT: env.NODE_ENV === 'development',
  JAVA_API_BASE: env.JAVA_API_BASE || 'http://10.177.157.30:5825',
  KB_TIMEOUT: env.KB_TIMEOUT || 60000,
  KEYCLOAK_ADMIN_PASSWORD: env.KEYCLOAK_ADMIN_PASSWORD || '',
  KEYCLOAK_ADMIN_USERNAME: env.KEYCLOAK_ADMIN_USERNAME || '',
  // tslint:disable-next-line: object-literal-sort-keys
  KC_NEW_USER_DEFAULT_PWD: env.KC_NEW_USER_DEFAULT_PWD || 'User@123',
  KEYCLOAK_REALM: env.KEYCLOAK_REALM || 'sunbird',
  KHUB_CLIENT_SECRET: env.KHUB_CLIENT_SECRET || 'axc123',
  KHUB_GRAPH_DATA: env.KHUB_GRAPH_DATA || 'http://10.177.157.30:3016',
  KHUB_SEARCH_BASE: env.KHUB_SEARCH_BASE || 'http://10.177.157.30:3014',
  LAB42_POST_ASSESSMENT_BASE: env.LAB42_POST_ASSESSMENT_BASE || 'https://lab42.idemo-ppc.com',
  LAB42_POST_ASSESSMENT_CLIENT_ID: env.LAB42_POST_ASSESSMENT_CLIENT_ID || 'wingspan',
  LAB42_POST_ASSESSMENT_CLIENT_SECRET: env.LAB42_POST_ASSESSMENT_CLIENT_SECRET ||
    '4ffb4f4ab7547d8c7d2320f83e1d7cfaa930d500fd55e262a825a2516c5e5b5b',
  LEADERBOARD_API_BASE: env.LEADERBOARD_API_BASE || env.SB_EXT_API_BASE_2,
  LEARNING_HISTORY_API_BASE: env.LEARNING_HISTORY_API_BASE || env.SB_SEXT_API_BASE_3,
  LEARNING_HUB_API_BASE: env.LEARNING_HUB_API_BASE || env.SB_EXT_API_BASE_2,
  LIKE_API_BASE: env.LIKE_API_BASE || env.SB_EXT_API_BASE_2,
  MULTI_TENANT_KEYCLOAK:
    env.MULTI_TENANT_KEYCLOAK ||
    'localhost,https://siemens-staging.onwingspan.com/auth,wingspan;localhost,https://siemens-staging.onwingspan.com/auth,wingspan',
  NAVIGATOR_JSON_HOST:
    env.NAVIGATOR_JSON_HOST || 'http://10.177.157.30:3007/web-hosted/navigator/json',
  NODE_API_BASE: env.NODE_API_BASE || 'http://10.177.22.26:5001',
  NODE_API_BASE_2: env.NODE_API_BASE_2 || 'http://10.177.157.30:3009',
  NODE_API_BASE_2_CLIENT_ID: env.NODE_API_BASE_2_CLIENT_ID || 'admin',
  NODE_API_BASE_2_CLIENT_SECRET: env.NODE_API_BASE_2_CLIENT_SECRET || 'MdiDn@342$',
  NODE_API_BASE_3: env.NODE_API_BASE_3 || 'http://10.177.157.30:3015',
  NOTIFICATIONS_API_BASE: env.NOTIFICATIONS_API_BASE || 'http://10.177.22.26:5805',
  DISCUSSION_HUB_API_BASE: env.DISCUSSION_HUB_API_BASE || 'http://localhost:4567',
  DISCUSSION_HUB_DEFAULT_PASSWORD: env.DISCUSSION_HUB_DEFAULT_PASSWORD || 'nodebbUser123$',
  DISCUSSION_HUB_WRITE_API_KEY: env.DISCUSSION_HUB_WRITE_API_KEY || '5aaf0ac3-c7ad-4e06-bc1b-5311d462cef3',
  DISCUSSION_HUB_WRITE_API_UID: env.DISCUSSION_HUB_WRITE_API_UID || 1,
  OPEN_SABER_USER_REGISTRY_BASE: env.OPEN_SABER_USER_REGISTRY_BASE || 'http://10-0-1-111:8005',
  PID_API_BASE: env.PID_API_BASE || 'http://10.177.22.26:9200',
  PLAYLISTV1_API_BASE: env.PLAYLISTV1_API_BASE || env.SBEXT_API_BASE_2,
  PLAYLIST_API_BASE: env.PLAYLIST_API_BASE || env.SBEXT_API_BASE,
  // tslint:disable-next-line:ban
  PORTAL_PORT: parseInt(env.PORTAL_PORT + '', 10) || 3003,
  PREFERENCE_API_BASE: env.PREFERENCE_API_BASE || env.SB_EXT_API_BASE_4,
  PROGRESS_API_BASE: env.PROGRESS_API_BASE || env.SB_EXT_API_BASE_2,
  RATING_API_BASE: env.RATING_API_BASE || env.SB_EXT_API_BASE_2 || 'http://10.177.22.26:7001',
  RECOMMENDATION_API_BASE: env.RECOMMENDATION_API_BASE || env.SBEXT_API_BASE,
  RESET_PASSWORD: 'http://siemens-staging.onwingspan.com',
  ROLES_API_BASE: env.ROLES_API_BASE || env.SB_EXT_API_BASE_2,
  SB_EXT_API_BASE: env.SBEXT_API_BASE || 'http://10.177.22.26:5902',
  SB_EXT_API_BASE_2: env.SBEXT_API_BASE_2 || 'http://10.177.22.26:7001',
  // SB_EXT_API_BASE_2: env.SB_EXT_API_BASE_2,
  SB_EXT_API_BASE_3: env.SBEXT_API_BASE_3 || env.SBEXT_API_BASE_2 || 'http://10.177.61.54:7001',
  SB_EXT_API_BASE_4:
    env.SBEXT_API_BASE_4 || env.SBEXT_API_BASE_2 || env.SB_EXT_BASE_4 || 'http://10.177.61.54:7001',
  // SB_EXT_API_BASE_4: env.SB_EXT_API_BASE_4,

  SCORM_PLAYER_BASE: env.SCORM_PLAYER_BASE || 'http://10.177.61.57',
  SEARCH_API_BASE: env.SEARCH_API_BASE || env.SBEXT_API_BASE,
  SOCIAL_TIMEOUT: env.SOCIAL_TIMEOUT || 10000,
  STATIC_ILP_PROXY: env.STATIC_ILP_PROXY || 'http://10.177.157.30:3005',
  SUBMISSION_API_BASE: env.SUBMISSION_API_BASE || env.SB_EXT_API_BASE_2,

  TELEMETRY_API_BASE: env.TELEMETRY_API_BASE || 'http://10.177.157.30:8090',
  TELEMETRY_SB_BASE: env.TELEMETRY_SB_BASE || 'http://10.177.63.199:9090',
  TIMEOUT: env.TIMEOUT || 10000,
  TIMESPENT_API_BASE: env.TIMESPENT_API_BASE || env.SB_EXT_API_BASE_2,
  TNC_API_BASE: env.TNC_API_BASE || env.SB_EXT_API_BASE_4,
  USER_ANALYTICS: `${HTTPS_HOST}/LA1`,
  USER_CREATE_API_BASE: env.USER_CREATE_API_BASE,
  USER_CREATE_PASSWORD: env.USER_CREATE_PASSWORD || 'C9Mg4@0q!J',
  USER_CREATE_USERNAME: env.USER_CREATE_USERNAME || 'ui-client',
  USER_DETAILS_API_BASE: env.USER_DETAILS_API_BASE || env.SB_EXT_API_BASE_2,
  USER_SUNBIRD_DETAILS_API_BASE: 'http://localhost:7001',
  USER_PROFILE_API_BASE: env.USER_PROFILE_API_BASE || 'http://localhost:3004',
  USER_BULK_UPLOAD_DIR: env.USER_BULK_UPLOAD_DIR,
  USE_SERVING_HOST_COUNTER: env.USE_SERVING_HOST_COUNTER,
  VIEWER_PLUGIN_RDBMS_API_BASE:
    process.env.VIEWER_PLUGIN_RDBMS_API_BASE || 'http://10.177.61.54:5801',
  WEB_HOST_PROXY: env.WEB_HOST_PROXY || 'http://10.177.157.30:3007',

  COHORTS_API_BASE: env.COHORTS_API_BASE || env.SB_EXT_API_BASE_2,
  CONTENT_SOURCE_API_BASE: env.CONTENT_SOURCE_API_BASE || env.SB_EXT_API_BASE_2,
  CONTINUE_LEARNING_API_BASE: env.CONTINUE_LEARNING_API_BASE || env.SB_EXT_API_BASE_2,
  FRAC_API_BASE: env.FRAC_API_BASE || 'https://igot-frac-dev.tarento.com',
  NETWORK_SERVICE_BACKEND: env.NETWOR_SERVICE_API_BASE || 'http:localhost:7001',
  CONTENT_VALIDATION_API_BASE: env.CONTENT_VALIDATION_API_BASE || 'http://localhost:6590',
  PROFANITY_SERVICE_API_BASE: env.PROFANITY_SERVICE_API_BASE || 'http://52.173.240.27:4001',
  DISCUSSION_CATEGORY_LIST: env.DISCUSSION_CATEGORY_LIST || 'cid[]=5&cid[]=6&cid[]=8&cid[]=9&cid[]=10&cid[]=11&cid[]=12&cid[]=13',
}

export const RESTRICTED_PYTHON_STMT: string[] = process.env.RESTRICTED_CHARACTERS
  ? process.env.RESTRICTED_CHARACTERS.split('###')
  : []
