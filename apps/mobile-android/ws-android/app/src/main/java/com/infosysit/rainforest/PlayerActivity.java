package com.infosysit.rainforest;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.services.ConnectivityReceiver;
import com.infosysit.rainforest.services.DeleteDecryptedService;
import com.infosysit.rainforest.services.PlayerBridge;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.services.ApiInterfaceJava;
import com.infosysit.sdk.services.ConnectivityUtility;
import com.infosysit.sdk.services.RetrofitSingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.EXTERNAL_CONTENT;
import static com.infosysit.sdk.Constants.EXTERNAL_OPEN;
import static com.infosysit.sdk.Constants.PATH_PARAM;
import static com.infosysit.sdk.Constants.TMP_DIR_PATH;
import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.contentTelemetry;
import static com.infosysit.sdk.Constants.continueLearningTelemetry;
import static com.infosysit.sdk.Constants.continueLearningTelemetryJson;

/**
 * Created by akansha.goyal on 3/14/2018.
 */

public class PlayerActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    static WebView playerWebView = null;
    boolean isConnectedToInternet = false;

    private ConnectivityReceiver connectivityReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.player);
        changeWindowColor(this);
        loadWebView();
//        WebView.setWebContentsDebuggingEnabled(true);


        // inline



    }


    private void loadWebView(){


        playerWebView = (WebView) findViewById(R.id.playerWebViewShowContent);
        playerWebView.getSettings().setJavaScriptEnabled(true);
        playerWebView.getSettings().setDomStorageEnabled(true);
        playerWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        playerWebView.addJavascriptInterface(new PlayerBridge(this), "appRef");
        playerWebView.loadUrl("file:///android_asset/app-player/index.html");
        playerWebView.getSettings().setBuiltInZoomControls(true);
        playerWebView.getSettings().setDisplayZoomControls(false);
        playerWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        playerWebView.setLongClickable(false);
        playerWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!(url.contains("file://") || url.contains("infosys"))){
                    playerWebView.removeJavascriptInterface("appRef");
                }
                Log.d("PlayerActivityLog",url);

                if(ConnectivityUtility.isConnected(PlayerActivity.this)){
                    if(!url.contains("sparshv2")&&!url.startsWith("file:")){
                        Intent activityIntent = new Intent(PlayerActivity.this,ExternalPlayerActivity.class);
                        activityIntent.putExtra(EXTERNAL_OPEN,EXTERNAL_CONTENT);
                        activityIntent.putExtra(PATH_PARAM,url);
                        startActivity(activityIntent);
                        return true;

                    }
                    else if(url.startsWith("file:") && !url.contains(".Lex_android")){
                        Intent homeActivity = new Intent(PlayerActivity.this, HomeActivity.class);
                        homeActivity.putExtra(PATH_PARAM,url.replace("file://",""));
                        homeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(homeActivity);
                        return true;


                    }
                    else if(url.contains("sparshv2")){
                        Toast.makeText(PlayerActivity.this,R.string.intranetLink,Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                else{
                    if(!url.startsWith("file:")){
                        Toast.makeText(PlayerActivity.this,"Please check your network connectivity",Toast.LENGTH_LONG).show();
                        return true;
                    }
                }


                return super.shouldOverrideUrlLoading(view, url);

            }






            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("DecryptionTask","page finished");
                super.onPageFinished(view, url);
                loadPlayer();
            }


        });
        playerWebView.setWebChromeClient(new WebChromeClient());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Constants.playerBackground = false;
        loadPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, intentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Constants.playerBackground = true;
        unregisterReceiver(connectivityReceiver);
    }

    public void loadPlayer(){
        Log.d("NAVIGATION_DATA_OUTGOI","Reached here");
        JsonObject sample = new JsonObject();
        JsonObject contentData = new JsonObject();
        JsonParser jsonParserObj = new JsonParser();
        JsonArray continueLearningTelemetryjson = new JsonArray();
        //Log.d("loadPlayer","PinchZoom "+String.valueOf(pinchRequied(Constants.contentMeta.get("mimeType").getAsString())));
        //Log.d("loadPlayer",Constants.contentMeta.get("mimeType").getAsString());
//        if(pinchRequied(Constants.contentMeta.get("mimeType").getAsString())){
//
//            playerWebView.getSettings().setBuiltInZoomControls(true);
//            playerWebView.getSettings().setDisplayZoomControls(false);
//        }
        String resourceplayed = SharedPrefrence.getItem(this,continueLearningTelemetry,"undefined");
        if(!resourceplayed.equalsIgnoreCase("undefined")){
//            Log.d("SharedPreference","first "+resourceplayed);
            resourceplayed = resourceplayed + "," + Constants.contentMeta.get("identifier").getAsString();
            SharedPrefrence.setItem(this,continueLearningTelemetry,resourceplayed);
        }
        else{
            resourceplayed = Constants.contentMeta.get("identifier").getAsString();
            SharedPrefrence.setItem(this,continueLearningTelemetry,resourceplayed);
        }
        if(Constants.contentMeta.get("mimeType").getAsString().equals("video/x-youtube")){
//            Log.d("LoadPlayer",Constants.contentMeta.get("identifier").getAsString());
        }
        if (Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")||
                Constants.contentMeta.get("mimeType").getAsString().equals("application/web-module")){

//            Log.d("ArtifactUrl",Constants.contentMeta.get("artifactUrl").getAsString());

            String identifier = Constants.contentMeta.get("identifier").getAsString();
            File path = this.getExternalFilesDir("");
            File letDirectory;
            File filePath;
            filePath = new File(path, TMP_DIR_PATH+identifier);
            if(Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")){
                letDirectory = new File(path, TMP_DIR_PATH+identifier+"/quiz.json");
            }
            else{
                letDirectory = new File(path, TMP_DIR_PATH+identifier+"/manifest.json");

            }
            if(!letDirectory.exists()){
                Log.d("PathExists","Path: DOES NOT EXISTS");
                filePath = new File(path, TMP_DIR_PATH+identifier+"/"+identifier);
                if(Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")){


                    letDirectory = new File(path, TMP_DIR_PATH+identifier+"/"+identifier+"/quiz.json");
                }
                else{
                    letDirectory = new File(path, TMP_DIR_PATH+identifier+"/"+identifier+"/manifest.json");
                    Constants.contentMeta.addProperty("artifactUrl",letDirectory.getAbsolutePath());
                }
            }
            else{
                Log.d("PathExists","Path: EXISTS");
            }
            if(letDirectory.exists()){
                try {
                    Log.d("PathExists","Path: EXISTS1");
                    FileInputStream fis= new FileInputStream(letDirectory);
                    String ret = convertStreamToString(fis);
                    fis.close();
                    File assetFile = new File(filePath+"/assets");
                    Log.d("PathExists","Path1: "+assetFile);
                    if(assetFile.exists() && Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")){
                        Log.d("PathExists","Path1: "+1);
                        if(Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")){
                            if(!ret.contains("/assets/")){

                                ret = ret.replaceAll("(http).*?(Images/)",Constants.STORAGE_BASE_PATH+filePath+"/assets/Images/");
                            }
                            else{
                                ret = ret.replaceAll("(/static).*?(Images/)",Constants.STORAGE_BASE_PATH+ filePath+"/assets/Images/");
                            }

                        }
                        JsonObject supportData = jsonParserObj.parse(ret).getAsJsonObject();
                        sample.add("supportData",supportData);
                    }
                    else if(!assetFile.exists()&& Constants.contentMeta.get("mimeType").getAsString().equals("application/quiz")){
                        Log.d("PathExists","Path1: "+2);
                        JsonObject supportData = jsonParserObj.parse(ret).getAsJsonObject();
                        sample.add("supportData",supportData);
                    }
                    else{
                        Log.d("PathExists","Path1: "+3+"exists: "+assetFile.exists()+"type: "+Constants.contentMeta.get("mimeType").getAsString());
                        JsonArray supportData = jsonParserObj.parse(ret).getAsJsonArray();
                        Log.d("PathExists","data: "+supportData);
                        sample.add("supportData",supportData);
                    }
                    if (!Constants.tocMeta.entrySet().isEmpty()){
                        sample.add("toc",Constants.tocMeta);
                        contentData.addProperty("courseid",Constants.tocMeta.get("identifier").getAsString());
                        continueLearningApi(continueLearningTelemetryjson);
                    }
                    else{

                        sample.add("toc",null);
                        contentData.addProperty("courseid","");

                    }




                    contentData.addProperty("resid",Constants.contentMeta.get("identifier").getAsString());
                    contentData.addProperty("restype",Constants.contentMeta.get("mimeType").getAsString());
                    contentTelemetry = contentData;

                    sample.add("content",Constants.contentMeta);





                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }







        }
        else{
            if (!Constants.tocMeta.entrySet().isEmpty()){

                sample.add("toc",Constants.tocMeta);
                continueLearningApi(continueLearningTelemetryjson);
            }
            else{
//                Log.d("playernotification", "creating notification");
//                Intent intent = new Intent(this,PlayerActivity.class);
//// use System.currentTimeMillis() to have a unique ID for the pending intent
//                PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//// build notification
//// the addAction re-use the same intent to keep the example short
//                Notification n  = new Notification.Builder(this)
//                        .setContentTitle("New mail from " + "test@gmail.com")
//                        .setContentText("Subject")
//                        .setSmallIcon(R.drawable.lex_logo_trans)
//                        .setContentIntent(pIntent)
//                        .setAutoCancel(true)
////                        .addAction(R.drawable.icon, "Call", pIntent)
////                        .addAction(R.drawable.icon, "More", pIntent)
//                        .addAction(R.drawable.ic_search, "And more", pIntent).build();
//
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//                notificationManager.notify(0, n);
                sample.add("toc",null);
                continueLearningApi(continueLearningTelemetryjson);
            }
            sample.add("content",Constants.contentMeta);
//            Log.d("REsumePlayer","play before "+Constants.contentMeta.get("artifactUrl").getAsString());
            sample.add("supportData",null);
        }
        playerWebView.evaluateJavascript("initApp("+sample+")",null);
    }

    public void continueLearningApi(JsonArray continueLearningTelemetryjson) {
        JsonParser jsonParserObj = new JsonParser();
        if(UtilityJava.isOnline(this)){
            JsonObject courseDetail = new JsonObject();
            jsonObjectTelemetry(courseDetail);
            sendCourseJsontoApi(courseDetail);
        }
        else{

            String continueLearning = SharedPrefrence.getItem(this,continueLearningTelemetryJson,"undefined");
            if(!continueLearning.equalsIgnoreCase("undefined")) {
                continueLearningTelemetryjson = (JsonArray) jsonParserObj.parse(continueLearning);
                JsonObject courseDetail = new JsonObject();
                jsonObjectTelemetry(courseDetail);
                continueLearningTelemetryjson.add(courseDetail);

            }
            else{
                JsonObject courseDetail = new JsonObject();
                jsonObjectTelemetry(courseDetail);
                continueLearningTelemetryjson.add(courseDetail);


            }

            continueLearning =continueLearningTelemetryjson.toString();
            SharedPrefrence.setItem(this,continueLearningTelemetryJson,continueLearning);
            Log.d("Iwantshared",SharedPrefrence.getItem(this,continueLearningTelemetryJson,""));
        }
    }

    private void  jsonObjectTelemetry(JsonObject courseDetail) {
        String contextPathId;
        Log.d("contentTypeJson",Constants.contentMeta.get("contentType").getAsString());
        if(Constants.contentMeta.get("contentType").getAsString().equals("Resource")){
            contextPathId = Constants.contentMeta.get("identifier").getAsString();
        }
        else{
            contextPathId = Constants.tocMeta.get("identifier").getAsString();
        }
        courseDetail.addProperty("contextPathId",contextPathId);
        courseDetail.addProperty("resourceId",Constants.contentMeta.get("identifier").getAsString());
        courseDetail.add("data",null);
        courseDetail.addProperty("percentComplete",0);

    }

    private void sendCourseJsontoApi(JsonObject courseDetail) {
        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(baseUrl);
        Call<JsonObject> call = apiInterface.pushCourseDetails(courseDetail, UtilityJava.tokenValue);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(getClass().getSimpleName(),"responseCode: "+response.code());
                Log.d("ApiCallResponse","sendCoursejson worked");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("ApiCallFailure","sendCoursejson worked");
                Log.d(getClass().getSimpleName(),"sendCoursejson did not work");
                Log.d("ApiCallFailure","sendCoursejson worked");
            }
        });
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, DeleteDecryptedService.class);
        intent.putExtra("folderToDelete", TMP_DIR_PATH);
        startService(intent);
    }

    private Boolean pinchRequied(String mimeType){
        Boolean isPinchZoom = false;
        if(mimeType.equalsIgnoreCase("application/web-module") || mimeType.equalsIgnoreCase("application/pdf")){
            isPinchZoom = true;
        }
        return isPinchZoom;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (playerWebView != null)
        {
            playerWebView.stopLoading();
            playerWebView.onPause();
            playerWebView.loadUrl("");
            playerWebView.reload();
        }
    }

    public void goHomeUrl(String url){
        if (isConnectedToInternet) {
            if(url.contains("viewer")){
                Toast.makeText(this, "Assessment can't be played in offline mode. Redirecting you to assessment in online mode..", Toast.LENGTH_LONG).show();
                Util.navigateToPage(this, url);
                return;
            }
            else{
                Util.navigateToPage(this, url);
            }

        }
        else{
            if(url.contains("viewer")){
                Toast.makeText(this, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
                return;
            }
            else{
                Toast.makeText(this, Constants.connectivityMessage , Toast.LENGTH_LONG).show();
            }

        }
//


    }


    @Override
    public void connectedToInternet() {
        isConnectedToInternet = true;
    }


    @Override
    public void offline() {
        isConnectedToInternet = false;
    }
}









//class PlayerActivity : AppCompatActivity() {
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.bottom_navigation_layout.player)
//        getSupportActionBar()!!.hide();
//        var json_string = application.assets.open("index.html").bufferedReader().use{
//        it.readText()
//        }
//
//
//
////        val replacableMetaText = "var myReplacableVar = 'xxxx';"
//
//
////        val playerConfig = """{
////            "type": "pdf",
////            "base": ".",
////            "data": {
////            "file": "storage/emulated/0/Lex_Dev/Download_Resources/Resource/do_2124136470056386561315/do_2124136470056386561315.pdf",
////            "prefix": "file:///android_asset/app_players/plugins/pdf"
////            },
////            "plugins": [
////            {
////                "type": "pdf",
////                "path": "file:///android_asset/app_players/plugins/pdf"
////            }
////            ]
////        }""".trimMargin()
//
////        var ConfigToBeReplaced = "var myReplacableVar=" + playerConfig + ";";
////        json_string = json_string.replace(replacableMetaText,ConfigToBeReplaced)
////        Log.d("PlayerActivity",json_string)
//
//
//
//
//        playerWebViewShowContent.settings.javaScriptEnabled = true
//        playerWebViewShowContent.settings.domStorageEnabled = true
//        playerWebViewShowContent.settings.allowFileAccessFromFileURLs =true
//        playerWebViewShowContent.loadUrl("file:///android_asset/dist/index.html")
//
//        playerWebViewShowContent.webViewClient = (object : WebViewClient() {
//        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        view?.loadUrl(request.toString())
//        return true
//        }
//        })
//        playerWebViewShowContent.setWebChromeClient(WebChromeClient())
//
//
//
//
//
////        playerWebViewShowContent.loadData(json_string,"text/html","UTF-8")
//
//        }
//
//        }



