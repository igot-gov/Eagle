package com.infosysit.rainforest.services;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.PlayerActivity;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.DecryptionCallback;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.services.ConnectivityUtility;
import com.infosysit.sdk.services.DecryptionTask;
import com.infosysit.sdk.services.TelemetryServices;

import static com.infosysit.sdk.Constants.contentTelemetry;

/**
 * Created by akansha.goyal on 3/14/2018.
 */

public class PlayerBridge implements DecryptionCallback {

    private PlayerActivity mPlayerActivity;
    private boolean isOnline = false;

    public PlayerBridge(PlayerActivity activity) {
        this.mPlayerActivity = activity;
    }


// Offline mode when player activity is running - for every 30 seconds
    @JavascriptInterface
    public void TELEMETRY_DATA_OUTGOING(String data) {
//        Log.d("TELEMETRY_DATA_OUTGOING",data);
//      Log.d("SomeData", "TELEMETRY_DATA_OUTGOING: "+ HomeActivity.loginPage.getUrl());
        Log.d("TELEMETRY_DATA_OUTGOING", "TestCases3 "+data);
        Log.d("TELEMETRY_DATA_OUTGOING", "TestCases3 "+contentTelemetry);
        if (contentTelemetry.has("resid") && contentTelemetry.get("resid").getAsString() != null && !Constants.playerBackground) {
            JsonParser jsonParserObj = new JsonParser();
            final JsonObject telemetryData = jsonParserObj.parse(data).getAsJsonObject();
            Log.d("TELEMETRY_DATA_OUTGOING", telemetryData.toString());
            Thread t = new Thread(new Runnable() {
                public void run() {
                    TelemetryServices telemetryServices = new TelemetryServices();
                    try {
                        if(telemetryData.get("type").getAsString().equalsIgnoreCase("submit")) {
                            JsonArray telemetryObejct = new JsonArray();
                            JsonObject requestParam = telemetryData.get("data").getAsJsonObject();
                            requestParam.addProperty("userEmail", SharedPrefrence.getItem(mPlayerActivity, "emailId", ""));
                            requestParam.addProperty("identifier", contentTelemetry.get("resid").getAsString());
                            requestParam.addProperty("title", "");
                            requestParam.addProperty("userEmail",SharedPrefrence.getItem(mPlayerActivity,"emailId",""));
                            requestParam.addProperty("identifier",contentTelemetry.get("resid").getAsString());
                            requestParam.addProperty("title","");
                            requestParam.addProperty("type",telemetryData.get("type").getAsString());
                            JsonObject finalObject = new JsonObject();
                            finalObject.add("request", requestParam);
                            String quizLearning = SharedPrefrence.getItem(mPlayerActivity, Constants.quizSubmitTelemetry, "undefined");
//                            Log.d("TelemetryData","data: "+requestParam.toString());
                            if (!quizLearning.equals("undefined") && !quizLearning.equals("")) {
                                JsonParser parser = new JsonParser();
                                telemetryObejct = parser.parse(quizLearning).getAsJsonArray();
                            }
                            telemetryObejct.add(finalObject);
                            Log.d("CheckThisValue", "Data " + finalObject.toString());
                            Log.d("CheckThisValue", "Object " + telemetryObejct.toString());

                            SharedPrefrence.setItem(mPlayerActivity, Constants.quizSubmitTelemetry, telemetryObejct.toString());
                            Log.d("TELEMETRY_DATA_OUTGOING", "Data " + SharedPrefrence.getItem(mPlayerActivity, Constants.quizSubmitTelemetry, "undefined"));
//                        }else if(telemetryData.get("type").getAsString().equalsIgnoreCase("done")){
//                            Log.d("TELEMETRY_DATA_OUTGOING","doneEvent: "+telemetryData);
                        }else{
                            telemetryServices.telemetryPlayer(mPlayerActivity, telemetryData, "offline");
                            Log.d("TELEMETRY_DATA_OUTGOING","Diff material");
                        }
                    } catch (Exception e) {
                        Log.d("ExceptionBridge",e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

    }


    @JavascriptInterface
    public void NAVIGATION_INTENT_OUTGOING(String id) {
//        Log.d("TelemetryBridge1",id);
        mPlayerActivity.finish();
//        mPlayerActivity.onBackPressed();
    }


    @JavascriptInterface
    public void NAVIGATION_NEW_TAB_DATA_OUTGOING(String value) {
//        Log.d("CHECK_VALUES",value);
    }

    //    public void PLAYER_CONTENT_INCOMING(){
//
//    }
    @JavascriptInterface
    public void NAVIGATION_DATA_OUTGOING(String data) {
        Log.d("NAVIGATION_DATA_OUTGOIN",data);
        JsonParser jsonParserObj = new JsonParser();
        JsonObject navigatedDataJson = jsonParserObj.parse(data).getAsJsonObject();
        String urlData = navigatedDataJson.get("url").getAsString();
        if (urlData.contains("home")) {
            mPlayerActivity.goHomeUrl("/home");
//            if (ConnectivityUtility.isConnected(mPlayerActivity) && isOnline) {
//                Toast.makeText(mPlayerActivity, "Assessment can't be played in offline mode. Redirecting you to assessment in online mode..", Toast.LENGTH_LONG).show();
//                Util.navigateToPage(mPlayerActivity, "/home");
//                return;
//            } else {
//                Toast.makeText(mPlayerActivity, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
//                return;
//            }

        } else {
            String[] navigatedData = urlData.split("/");

            ContentEntity content = null;
            try {
                content = AppDatabase.getDb(mPlayerActivity)
                        .contentDao().getContentByContentId(navigatedData[2]);
            } catch (Exception e) {
                Log.d("Exception","Message: "+e.getMessage());
            }
            Constants.contentMeta = jsonParserObj.parse(content.getContentMetaJson()).getAsJsonObject();
            if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/web-module")) {
                Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "/manifest.json");
            } else if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz") && !Constants.contentMeta.get("resourceType").getAsString().equalsIgnoreCase("Assessment")) {
                Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "/quiz.json");
            } else if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("video/x-youtube")) {
                if (!ConnectivityUtility.isConnected(mPlayerActivity)) {
                    Toast.makeText(mPlayerActivity, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
                }


            } else if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz") && Constants.contentMeta.get("resourceType").getAsString().equalsIgnoreCase("Assessment")) {
                Log.d(getClass().getSimpleName(),"navigatedData1 :"+navigatedData[2]);
                mPlayerActivity.goHomeUrl("/viewer/" + navigatedData[2]);
                return;
//                if (ConnectivityUtility.isConnected(mPlayerActivity) && isOnline) {
//
//                    Toast.makeText(mPlayerActivity, "Assessment can't be played in offline mode. Redirecting you to assessment in online mode..", Toast.LENGTH_LONG).show();
//                    Util.navigateToPage(mPlayerActivity, "/viewer/" + navigatedData[2]);
//                    return;
//                } else {
//                    Toast.makeText(mPlayerActivity, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
//                    return;
//                }
            } else {
                Log.d(getClass().getSimpleName(),"navigatedData2 :"+navigatedData[2]);
                Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "." + content.getExtension());
            }

            Log.d("NAVIGATION_DATA_OUTGOI","META: "+Constants.contentMeta);

            Log.d("playerbridge", "player bridge called");
            DecryptionTask decryption = new DecryptionTask(mPlayerActivity, this);
            decryption.execute(content.getContentId() + "." + content.getExtension());
        }
//        String courseId = navigatedDataJson.get("params").getAsJsonObject().get("courseId").getAsString();


//        mPlayerActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mPlayerActivity.loadPlayer();
//            }
//        });
    }


    public void onDecryptDone() {
        mPlayerActivity.loadPlayer();
    }

}