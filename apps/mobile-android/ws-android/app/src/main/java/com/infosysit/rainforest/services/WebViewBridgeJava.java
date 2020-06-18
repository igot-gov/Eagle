package com.infosysit.rainforest.services;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.DownloadsWebView;
import com.infosysit.rainforest.ExternalPlayerActivity;
import com.infosysit.rainforest.HomeActivity;
import com.infosysit.rainforest.R;
import com.infosysit.rainforest.SettingsActivity;
import com.infosysit.rainforest.Util;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.RoutingObserver;
import com.infosysit.sdk.UtilityJava;

import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.services.DeleteTask;
import com.infosysit.sdk.services.DownloadContentService;
//import com.infosysit.sdk.services.DownloadOpenRapService;
import com.infosysit.sdk.services.JWTUtils;
import com.infosysit.sdk.services.PlayerTelemetryService;

import java.util.ArrayList;
import java.util.List;

import static com.infosysit.rainforest.HomeActivity.chatButton;
import static com.infosysit.rainforest.HomeActivity.loginPage;
import static com.infosysit.rainforest.HomeActivity.splashPage;
import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.EXTERNAL_CONTENT;
import static com.infosysit.sdk.Constants.EXTERNAL_OPEN;
import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_MODE;
import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_RES_ID;

import static com.infosysit.sdk.Constants.IS_DOWNLOADED;
import static com.infosysit.sdk.Constants.PATH_PARAM;
import static com.infosysit.sdk.Constants.PERMISSION_FLAG;
import static com.infosysit.sdk.Constants.RTMP_URL;
import static com.infosysit.sdk.Constants.isAuthenticated;
import static com.infosysit.sdk.Constants.contentTelemetry;
import static com.infosysit.sdk.Constants.primeColor;
//import static com.infosysit.sdk.Constants.version;
//import static com.infosysit.sdk.Constants.webViewPageStack;


/**
 * Created by akansha.goyal on 3/12/2018.
 */

public class WebViewBridgeJava {

    public static List<JsonObject> listOfContents = new ArrayList<JsonObject>();
    public static JsonObject contentValues = new JsonObject();
    private Context mContext;
    public JsonObject contentJson;
    private RoutingObserver mRoutingObserver;

    public WebViewBridgeJava(Context context) {
        mContext = context;
    }


    public void startDownload(String contentId, String downloadMode) {
//        if(jsonValidateDownload(contentId)){
//        if(false){
//            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
//                builder.setTitle("Message")
//                    .setMessage("This content contains external content. Please download individual resource")
//                    .setCancelable(false);
//         new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//            android.app.AlertDialog alertDialog = builder.create();
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            alertDialog.show();
//                }
//          });
//        }
//        else {
            Intent intent = new Intent(mContext, DownloadContentService.class);
            intent.putExtra(EXTRA_DOWNLOAD_MODE, downloadMode);
            intent.putExtra(EXTRA_DOWNLOAD_RES_ID, contentId);
            mContext.startService(intent);
//        }
    }

    public void setRoutingChangeListener(RoutingObserver observer) {
        this.mRoutingObserver = observer;
    }

//    @JavascriptInterface
//    public void GO_LIVE (String data) throws Exception {
//        Intent activityIntent = new Intent(mContext, StreamingRTMP.class);
////        activityIntent.putExtra(RTMP_URL, Constants.GOLIVE_URL_D);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(activityIntent);
//    }
//
//    @JavascriptInterface
//    public void BROADCAST_CLASSROOM (String data) throws Exception {
//        Intent activityIntent = new Intent(mContext, StreamingRTMP.class);
////        activityIntent.putExtra(RTMP_URL, Constants.CLASSROOM_URL_E);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(activityIntent);
//    }

    @JavascriptInterface
    public void PORTAL_THEME(String themeData){
        JsonParser jsonParserObj = new JsonParser();
        JsonObject themeJson = jsonParserObj.parse(themeData).getAsJsonObject();
        String primaryColor = themeJson.get("themeDetails").getAsJsonObject().get("primary").getAsString();
        Log.d("ThemeDetails","Data: "+themeData);
        primeColor = Color.parseColor(primaryColor);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ((HomeActivity)mContext).chatButton.setBackgroundTintList (ColorStateList.valueOf(primeColor));
//                changeToTheme((Activity) mContext,THEME_DARK);
//                Utils.changeToTheme(this, Utils.THEME_DEFAULT);
            }
        });
        changeWindowColor(mContext);
    }




    // getting value of token here
    @JavascriptInterface
    public void TOKEN_OUTGOING (String identifier) throws Exception {
        Log.d("SESSIONID_OUTGOING","TOKEN "+identifier);
        identifier = identifier.replaceAll("\"", "");
        loginPage.post(new Runnable() {
            @Override
            public void run() {
                loginPage.evaluateJavascript("getSessionId()",null);
                loginPage.evaluateJavascript("isAuthenticated()",null);
//                UserDetails.getRoles();
            }
        });

        UtilityJava.tokenValue = "Bearer "+(String) identifier;
        Util.getUserData();

    }



    @JavascriptInterface
    public String getAndroidAppVer(){
        try{
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            Log.d("getAndroidAppVer",pInfo.versionName);
            return pInfo.versionName;
        }
        catch(Exception ex){
            return "";
        }

    }


    @JavascriptInterface
    public void SESSIONID_OUTGOING (String identifier){
        Log.d("SESSIONID_OUTGOING",identifier);
        identifier = identifier.replaceAll("\"","");
        String lastSessionId = SharedPrefrence.getItem(mContext,Constants.last_session_id,"undefined");

        if(identifier != "undefined" && !lastSessionId.equalsIgnoreCase(identifier)){
            SharedPrefrence.setItem(mContext,Constants.last_session_id,identifier);

        }
        try{
            DeleteTask deleteExpiryContent = new DeleteTask(mContext);
            deleteExpiryContent.execute();
        }
        catch (Exception ex){
            Log.e("deleteContent",ex.getMessage());
        }
    }

    @JavascriptInterface
    public void ISAUTHENTICATED_OUTGOING (String identifier) throws Exception {
        Log.d("TOKEN_OUTGOING3",identifier);
        if(Boolean.valueOf(identifier)){
            isAuthenticated = true;
            JWTUtils.decoded(mContext, UtilityJava.tokenValue.split(" ")[1]);
            SharedPrefrence.setItem(mContext,Constants.last_loggedin_date,String.valueOf(System.currentTimeMillis()));
            Intent telemetryService = new Intent(mContext, TelemetryLearningService.class);
            Intent playerService = new Intent(mContext, TelemetryPlayerService.class);
            Intent quizSubmission = new Intent(mContext,TelemetryQuizSubmission.class);
            Intent downloadCheck = new Intent(mContext,DownloadAllowedCheck.class);
            mContext.startService(telemetryService);
            mContext.startService(playerService);
            mContext.startService(quizSubmission);
            mContext.startService(downloadCheck);
        }
    }




    @JavascriptInterface
    public void DISPLAY_SETTING () {
//        Log.d("DISPLAY_SETTING"," "+ value);
        Intent activityIntent = new Intent(mContext,SettingsActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(activityIntent);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @JavascriptInterface
    public void DOWNLOAD_REQUESTED(final String identifier) throws Exception {
        if(Constants.downloadAllowed){
            String permissionValue  = SharedPrefrence.getItem(mContext,PERMISSION_FLAG,"undefined");
            if(!Util.checkWriteExternalPermission(mContext)){
                if(permissionValue.equalsIgnoreCase("undefined") || permissionValue.equalsIgnoreCase("false")){
                    Util.checkPermission(mContext);
                } else{
                    Toast.makeText(mContext,R.string.permissionDeniedDesc,Toast.LENGTH_LONG).show();
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + mContext.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mContext.startActivity(i);
                }
            } else{
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                if(prefs.getBoolean(mContext.getString(R.string.pref_download_over), true)){
                    if(!ConnectivityReceiver.isWifi()){
                        boolean isNeverClickedAgainCheck = SharedPrefrence.getBooleanItem(mContext,Constants.IS_NEVER_CLICKED,false);
                        View checkBoxView = View.inflate(mContext, R.layout.checkbox_layout, null);
                        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SharedPrefrence.setBooleanItem(mContext,Constants.IS_NEVER_CLICKED,true);
                            }
                        });
                        checkBox.setText(R.string.alertDialogCheckBox);


                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Permission for Download");
                        builder.setMessage(R.string.downloadOverMob)
                                .setView(checkBoxView)
                                .setCancelable(false);

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startDownload(identifier, "LEX");
                                Toast.makeText(mContext,"Download will begin once you are connected to WiFi",Toast.LENGTH_LONG).show();

                            }
                        });

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                prefs.edit().putBoolean(mContext.getString(R.string.pref_download_over),false).apply();
                                startDownload(identifier, "LEX");
                                Toast.makeText(mContext,"Download Initiated over Mob Data. Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog alert = builder.create();
                        if(!SharedPrefrence.getBooleanItem(mContext,Constants.IS_NEVER_CLICKED,false)){
                            alert.show();
                        }
                        else{
                            startDownload(identifier, "LEX");
                            Toast.makeText(mContext,"Download Initiated. Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
                        }
                    } else{
                        Toast.makeText(mContext,"Download Initiated over Wifi. Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
                        SharedPrefrence.setBooleanItem(mContext,IS_DOWNLOADED,true);
                        startDownload(identifier, "LEX");
                    }
                } else{
                    String networkType = null;
                    if(ConnectivityReceiver.isWifi()){
                        networkType = "on Wi-Fi. ";
                    } else{
                        networkType = "on Mobile Data. ";
                    }

                    startDownload(identifier, "LEX");
                    Toast.makeText(mContext,"Download Initiated "+ networkType +"Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
                    SharedPrefrence.setBooleanItem(mContext,IS_DOWNLOADED,true);
                }
            }
        } else{
            Toast.makeText(mContext,"According to Infosys policy, the download feature has been disabled for you. As you have few days left in your tenure with Infosys. ",Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void NAVIGATION_OCCURED (final String data){
        Log.d("shouldOverrideLoading1","byUI "+data);
//        ((Activity)mContext).runOnUiThread(new Runnable() {
//            public void run() {
//                if (mRoutingObserver != null) {
//                    mRoutingObserver.routingCallback(data);
//                }
//            }
//        });

        if (data.contains("logout=true")){
            UtilityJava.tokenValue="";
            Constants.userId="";
            ((HomeActivity)mContext).chatButton.setVisibility(View.GONE);
            splashPage.post(new Runnable() {
                @Override
                public void run() {
//                    splashPage.loadUrl("https://login.microsoftonline.com/logout.srf");
                    splashPage.loadUrl("https://login.microsoftonline.com/common/oauth2/logout?post_logout_redirect_uri=https%3A%2F%2F"+Constants.environmentType+".infosysapps.com");
                }
            });


        }
    }

//    @JavascriptInterface
//    public void IS_ON_HOME(String value){
////        Log.d("TOKEN_OUTGOING5",value);
//    }

    @JavascriptInterface
    public void GO_OFFLINE(String value){
//        Log.d("GO_OFFLINE","ths is called");
        Intent activityIntent = new Intent(mContext,DownloadsWebView.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(activityIntent);
    }

    @JavascriptInterface
    public void YAMMER_REQUEST(String data){
        Log.d("shouldOverrideLoading1",data);
        String urlYammer = data.replaceAll("\"","");
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(urlYammer));
        mContext.startActivity(i);
    }

    @JavascriptInterface
    public void CHAT_BOT_VISIBILITY(String data){
        Log.d("chatBotVisible",data);

//       chatButton.post(new Runnable() {
//            @Override
//            public void run() {
//                chatButton.setVisibility(View.VISIBLE);
//            }
//       });


        if(data.replaceAll("\"","").equalsIgnoreCase("yes")){
            chatButton.post(new Runnable() {
                @Override
                public void run() {
                    chatButton.setVisibility(View.VISIBLE);
                }
            });
        }
        else{
            chatButton.post(new Runnable() {
                @Override
                public void run() {
                    chatButton.setVisibility(View.GONE);
                }
            });
        }

    }


    @JavascriptInterface
    public void GET_PLAYERCONTENT_JSON(String data){
        JsonParser jsonParserObj = new JsonParser();
        JsonObject contentDataJson = jsonParserObj.parse(data).getAsJsonObject();
        Log.d("GET_PLAYERCONTENT_JSON",contentDataJson.toString());

        if(contentDataJson.has("isIframeSupported") &&  (contentDataJson.get("isIframeSupported").getAsString().equalsIgnoreCase("no") || contentDataJson.get("isIframeSupported").getAsString().equalsIgnoreCase("maybe"))){
            Log.d("CheckValuesUrl","Reached here: "+contentDataJson);
            Intent activityIntent = new Intent(mContext,ExternalPlayerActivity.class);
            activityIntent.putExtra(PATH_PARAM,contentDataJson.get("artifactUrl").getAsString());
            activityIntent.putExtra(EXTERNAL_OPEN,EXTERNAL_CONTENT);
            Intent playerTelemetryService = new Intent(mContext, PlayerTelemetryService.class);
            String iframeSupportedValue = contentDataJson.get("isIframeSupported").getAsString();
            contentTelemetry.addProperty("courseid","");
            contentTelemetry.addProperty("resid",contentDataJson.get("identifier").getAsString());
            contentTelemetry.addProperty("restype",contentDataJson.get("mimeType").getAsString());
            playerTelemetryService.putExtra(Constants.TelemetryContentID,contentDataJson.get("identifier").getAsString());
            if(iframeSupportedValue.equalsIgnoreCase("no") && !contentDataJson.get("resourceType").getAsString().equalsIgnoreCase("live stream")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(playerTelemetryService);
                } else {
                    mContext.startService(playerTelemetryService);
                }
            }
            loginPage.post(new Runnable() {
                @Override
                public void run() {

                    loginPage.goBack();
//                    webViewPageStack.pop();
//                    HomeActivity.loginPage.evaluateJavascript("isAuthenticated()",null);
                }
            });
            mContext.startActivity(activityIntent);

        }
//        else if(true){
//            Log.d(getClass().getSimpleName(),"NewApk reached here");
//            Util.openApp(mContext,"com.example.akanshagoyal.webview");
//        }
    }

    @JavascriptInterface
    public void NAVIGATION_NEW_TAB_DATA_OUTGOING(String value){
        Log.d("CHECK_VALUES",value);
    }





}
