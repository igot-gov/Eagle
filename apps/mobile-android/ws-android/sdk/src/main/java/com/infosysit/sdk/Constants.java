package com.infosysit.sdk;

import android.app.ProgressDialog;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by akansha.goyal on 3/12/2018.
 */

public class Constants {

    public static final String EXTRA_DOWNLOAD_RES_ID = "download_res_id";
    public static final String EXTRA_DOWNLOAD_MODE = "download_mode";
    public static final String quizSubmitTelemetry = "sharedPreference.quizSubmitTelemetry";
    public static final String quizSubmitCPTelemetry = "sharedPreference.quizSubmitCPTelemetry";
    public static int primeColor;
    public static final String STORAGE_BASE_PATH = "file:///storage/emulated/0/Android/data/com.infosysit.lex/files/";

    public static final int PERMISSION_REQUEST_CODE = 1;
    public static final String last_loggedin_date = "lastLoggedIn";
    public static final String last_session_id = "lastSessionId";
    // add dev or stage after android , player using the same
    public static final String APP_DIR_PATH = ".Lex_android/";
    public static final String TMP_DIR_PATH = APP_DIR_PATH + "temp/";
    public static final String DATA_DIR_PATH = APP_DIR_PATH + "data/";
    public static final String OPENRAP_DIR_PATH = APP_DIR_PATH + "openrap/";
    public static final String PERMISSION_FLAG = "permission_flag";
    public static final String RTMP_URL = "stream_url";

    public static final String authServer = "${BuildConfig.SERVER_URL}/auth";
    public static final String continueLearningTelemetry = "sharedPreference.continueLearningTelemetry";
    public static ProgressDialog progress = null;
    public static String environmentType = "space";
    public static boolean downloadAllowed = true;

    public static final String baseUrl = "https://space.sunbird.org";
    public static final String openRapUrl = "http://captive.openrap.com";
//public static final String openRapUrl = "";
    public static  String userId = null;
    public static final String externalPlayerResourceName = "TEXT_RESOURCE";
    public static final String TelemetryContentID = "telemetryContentId";
//    public static Stack<String> webViewPageStack = new Stack<String>();

    public static final String PATH_PARAM = "path_param";
    public static final String EXTERNAL_OPEN = "external_open";
    public static final String CHAT_BOT = "chat_bot";
    public static final String EXTERNAL_CONTENT = "external_content";
    public static final String SEARCH_PARAM = "search_param";
//    public static final  CONTENT_URL_PREFIX_REGEX = "((http|https)://private-[a-zA-Z0-9-]*/)";

    public static JsonObject contentTelemetry = new JsonObject();
    //ToDo
    public static final String playerData = "ExternalPlayerData";

    public static final String TELEMETRY_LEARNING = "telemetry_learning";

    // according to type
    //vd vs vA

    public static final String USER_ID = "user_id";

    public static final String telemetryTokenKey = "sharedPreference.telemetry";
    public static final String telemetryQuizTokenKey = "sharedPreference.telemetryQuiz";
    public static final String continueLearningTelemetryJson = "sharedPrefrence.continueLearningTelemetry";
    public static final String decryptedFileKey = "decryptedFileKey";
    public static final String continueLearningKey = Constants.persistence+"sharedPreference.continue";
    public static final String openRapDownload = "sharedPreference.openrap";


    public static List<String> decryptedFiles = new ArrayList<String>();

    public static final String webviewServer = "${BuildConfig.SERVER_URL}";
    public static final String landingpageUrl = "/home";
    public static final String dev_page = baseUrl;
    public static final String noNetNoTokenText = "Please try to Login once you are online";

    public static final String homeActivityName = ".HomeActivity";

    public static Boolean playerBackground = true;

    public static final String persistence = "com.infosysit.lexdev.persistence.";
    public static final String persistenceFile = "com.infosysit.sdk.persistence.";

    public static final String connectivityMessage = "You are currently offline please check your network connectivity.";

    public static String UserEmail=null;
    public static boolean isAuthenticated = false;
    public static String authToken = null;
    public static final long accessExpiry = 3600*24*7*1000L;
    public static final long  expiry = 3600*24*30*1000L;

    public static final String OPENRAP_RANDOM_BITS = "10391299875099539664079758308331294806";
    public static final String OPENRAP_DECRYPTION_KEY = "12403724379428733330";

    public static JsonObject contentMeta = new JsonObject();
    public static JsonObject tocMeta = new JsonObject();
    public static JsonObject supportMeta = new JsonObject();
    public static JsonObject externalPlayerTelemetryJson = new JsonObject();
    public static String typeOfEncrptDecrypt = "encryptOrDecrypt";
    public static String encryptDecryptDownloadId = "filename";
    public static String encryptDecryptContentId = "enc_dec_contentId";



    public static final String REQUESTED = "REQUESTED";
    public static final String INITIATED = "INITIATED";
    public static final String DOWNLOADED = "DOWNLOADED";
    public static final String EXPIRED = "EXPIRED";
    public static final String READY = "READY";
    public static final String FAILED = "FAILED";
    public static final String CANCELLED = "cancelled";

    public static HashMap<String, String> MIME_TO_EXTENSION;

    static {
        MIME_TO_EXTENSION = new HashMap<>();
        MIME_TO_EXTENSION.put("application/pdf", "pdf");
        MIME_TO_EXTENSION.put("video/mp4", "mp4");
        MIME_TO_EXTENSION.put("audio/mpeg", "mp3");
        MIME_TO_EXTENSION.put("image/png", "png");
        MIME_TO_EXTENSION.put("application/quiz", "zip");
        MIME_TO_EXTENSION.put("application/web-module", "zip");
    }

    public static int downloadPending = 0;

    public static String IS_DOWNLOADED = "isDownloaded";
    public static String IS_NEVER_CLICKED = "isNeverClicked";
    public static String IS_INSTALLED = "isInstalled";

//    public static HashMap<String,String> toDownloadList = new HashMap<>();





}
