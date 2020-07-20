package com.infosysit.rainforest;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.services.ConnectivityReceiver;
import com.infosysit.rainforest.services.DownloadsBridge;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;
import com.infosysit.sdk.services.SqlCrudService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.CANCELLED;
import static com.infosysit.rainforest.services.WebViewBridgeJava.listOfContents;
import static com.infosysit.sdk.Constants.FAILED;
import static com.infosysit.sdk.Constants.INITIATED;
import static com.infosysit.sdk.Constants.connectivityMessage;
import static com.infosysit.sdk.Constants.openRapUrl;
import static com.infosysit.sdk.UtilityJava.isOnline;

public class DownloadsWebView extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Context mContext;
    String selectedValue = "Resource";
    public static WebView tocPage;
//    ImageView catalogBtn;
//    ImageView marketBtn;
//    ImageView infyTv;
    ConstraintLayout mConstraintLayout;

    private ConnectivityReceiver connectivityReceiver;


    private FloatingActionButton hotSpotButton;
    private View viewResource;
    private View viewModule;
    private View viewCourse;

    private Snackbar mSnackbarProgress;

    private int progressCount = 0;
    boolean isConnectedToInternet = false;
    private int courseCount;

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(int collectionCount) {
        this.collectionCount = collectionCount;
    }

    private int collectionCount;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(R.layout.activity_downloads_web_view);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tocPage = (WebView) findViewById(R.id.downloadWebView);
//        mContext = this;
//        hotSpotButton = (FloatingActionButton) findViewById(R.id.floatingHotspotButton);
//        testMethod();
//    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_downloads_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tocPage = (WebView) findViewById(R.id.downloadWebView);
//        final BottomNavigationView bottomToolbar = (BottomNavigationView) findViewById(R.id.navigationView);
//        BottomNavigationViewHelper.removeShiftMode(bottomToolbar);

        mContext = this;

        final Button showResource = findViewById(R.id.resourceClicked);
        final Button showCourse = findViewById(R.id.courseClicked);
        final Button showModule = findViewById(R.id.moduleClicked);
        viewResource = findViewById(R.id.divider3);
        viewModule = findViewById(R.id.divider2);
        viewCourse = findViewById(R.id.divider1);
        hotSpotButton = (FloatingActionButton) findViewById(R.id.floatingHotspotButton);
        mConstraintLayout = findViewById(R.id.downloads_layout);


//        marketBtn =  findViewById(R.id.marketing);
//        infyTv = findViewById(R.id.infyTv);
//        catalogBtn = findViewById(R.id.catalog);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_hamburger));
        changeWindowColor(this);
//        bottomToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navigation_learning:
//                        Util.navigateToPage(mContext,"/sub-apps/gl");
//                        break;
//                    case R.id.navigation_goals:
//                        Util.navigateToPage(mContext,"/profile/goals");
//                        break;
//                    case R.id.navigation_assessment:
//                        Util.navigateToPage(mContext,"/sub-apps/assessment/home");
//                        break;
//                    case R.id.navigation_profile:
//                        Util.navigateToPage(mContext,"/profile/goals");
//                        break;
//                    case R.id.navigation_search:
//                        Util.navigateToPage(mContext,"/search");
//                        break;
//                    default:
//                        Toast.makeText(mContext, "Some error occured", Toast.LENGTH_LONG).show();
//                        break;
//                }
//                return false;
//            }
//        });

//        final ProgressBar loader = (ProgressBar) findViewById(R.id.progressBar);

        tocPage = (WebView) findViewById(R.id.downloadWebView);


        loadWebView();





        showDownloadProgress();
    }

    private void loadWebView(){
        final DownloadsBridge downloadsBridge =new DownloadsBridge(this);

        tocPage.getSettings().setJavaScriptEnabled(true);
        tocPage.getSettings().setDomStorageEnabled(true);
        tocPage.getSettings().setAllowFileAccessFromFileURLs(true);
        tocPage.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        tocPage.loadUrl("file:///android_asset/mobile-apps/index.html");
        tocPage.loadUrl("file:///android_asset/app-toc/index.html");
//        tocPage.loadUrl("file:///android_asset/mobile-apps/index.html");
        tocPage.addJavascriptInterface(downloadsBridge, "appRef");
        tocPage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tocPage.setLongClickable(false);

        tocPage.setWebViewClient(new WebViewClient() {
            //            boolean loadingFinished = true;
//            boolean redirect = false;
//
//            long last_page_start;
//            long now;
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(!(request.getUrl().toString().contains("infosys")|| request.getUrl().toString().contains("file:///"))){
                    tocPage.removeJavascriptInterface("appRef");
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("DownloadsWebViewTime","StartTime "+System.currentTimeMillis());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final int courseCount = AppDatabase.getDb(mContext).contentDao().getContentByTypeInitiatedByUserCount("Course");
                        final int collectionCount = AppDatabase.getDb(mContext).contentDao().getContentByTypeInitiatedByUserCount("Collection");
                        Log.d("CourseCount", "onPageFinished outside: "+getCourseCount()+" "+getCollectionCount());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(courseCount > 0){
                                    courseIsClicked(view);
                                }
                                else if(collectionCount > 0){
                                    moduleIsClicked(view);
                                }
                                else{
                                    resourceIsClicked(view);
                                }
                            }
                        });

                    }
                });



                Log.d("CourseCount", "onPageFinished: "+getCourseCount()+" "+getCollectionCount());

//                }
                Log.d("DownloadsWebViewTime","EndTime "+System.currentTimeMillis());
            }



        });
    }





    public void goBrand(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/catalog/marketing/Brand Assets");
        }
        else{
            Toast.makeText(this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }

    public void goInfyTv(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/infytv");
        }
        else{
            Toast.makeText(this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }
    public void goCatalog(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/catalog");
        }
        else{
            Toast.makeText(this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }

    public void goHome(View view) {
        if (isConnectedToInternet) {
            Util.navigateToPage(mContext,"/home");
        } else {
            Toast.makeText(this, "Please check your Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        tocPage.resumeTimers();
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
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onBackPressed() {
        ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if(taskList != null && taskList.size() < 3){
            finish();
        }
        super.onBackPressed();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_feedback) {
//            if (isConnectedToInternet) {
//                Util.navigateToPage(mContext, "/feedback/application");
//            } else {
//                Toast.makeText(this, "Please check your Internet Connectivity", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }
//
//        else if (id== R.id.action_downloads){
//            Intent homeActivity = new Intent(DownloadsWebView.this, DownloadsWebView.class);
//            startActivity(homeActivity);
//        }
//        else if(id == R.id.action_leaderboard){
//            if (isConnectedToInternet) {
//                Util.navigateToPage(mContext, "/leaderboard");
//            } else {
//                Toast.makeText(this, "Please check your Internet Connectivity", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//
//        else if(id == R.id.action_profile){
//            if (isConnectedToInternet) {
//                Util.navigateToPage(mContext, "/profile/goals");
//            } else {
//                Toast.makeText(this, "Please check your Internet Connectivity", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }



        if(id == R.id.action_settings){
//            Log.d("action_settings","Settings is clicked");
            Intent homeActivity = new Intent(DownloadsWebView.this, SettingsActivity.class);
            startActivity(homeActivity);
        }
        else{
            if(isOnline(mContext)){
                DownloadsWebView.super.onBackPressed();
            } else {
                Toast.makeText(mContext, connectivityMessage, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialogRemove(final String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setMessage("Are you sure you want to delete this?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
//                Boolean isSuccess = ContentSQLService.removeContent(fileName);
//                if (isSuccess) {
//                    Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(mContext, "Please Try again.", Toast.LENGTH_LONG).show();
//                }
                SqlCrudService.startActionDelete(mContext, fileName.split("\\.")[0]);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                ((DownloadsWebView) mContext).showContent();

                            }
                        }, 500);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogCancel(final String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setMessage("Are you sure you want to cancel this?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
//                Boolean isSuccess = ContentSQLService.removeContent(fileName);
//                if (isSuccess) {
//                    Toast.makeText(mContext, "Successfully deleted", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(mContext, "Please Try again.", Toast.LENGTH_LONG).show();
//                }
                SqlCrudService.startActionCancel(mContext, fileName.split("\\.")[0]);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                ((DownloadsWebView) mContext).showContent();

                            }
                        }, 500);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    public static JsonObject testObject = new JsonObject();

    public void testMethod(){
        JsonObject downloadJson = new JsonObject();
        List<String> contentList = new ArrayList<String>();

        contentList.add("Course");
        contentList.add("Collection");
        contentList.add("Resource");
        JsonObject contentJson = new JsonObject();

        for (String type : contentList){
            List<ContentEntity> contentEntities = null;
            try {
                contentEntities = AppDatabase.getDb(mContext).contentDao().getContentByTypeInitiatedByUser(type);
            } catch (Exception e) {
                Log.d("Exception","Message: "+e.getMessage());
            }
            JsonArray resourceList = new JsonArray();
            for(ContentEntity values : contentEntities){
                JsonObject downloadJsons = new JsonObject();
                downloadJsons.addProperty("id",values.getContentId());
                downloadJsons.addProperty("downloadFinishedOn","");
                downloadJsons.addProperty("downloadInitOn","");
                downloadJsons.addProperty("expires","");
                downloadJsons.addProperty("status","");
                JsonParser parser = new JsonParser();
                JsonObject contentSqlJson = parser.parse(values.getContentMetaJson()).getAsJsonObject();
                JsonObject refinedJsonObject = new JsonObject();
                refinedJsonObject.addProperty("identifier",contentSqlJson.get("identifier").getAsString());
                refinedJsonObject.addProperty("appIcon",contentSqlJson.get("appIcon").getAsString());
                refinedJsonObject.addProperty("artifactUrl",contentSqlJson.get("artifactUrl").getAsString());
                refinedJsonObject.addProperty("contentType",contentSqlJson.get("contentType").getAsString());
                refinedJsonObject.addProperty("mimeType",contentSqlJson.get("mimeType").getAsString());
                refinedJsonObject.addProperty("children",values.getChildren());
                String[] children = values.getChildren().split(",");
                addChild(children);
                contentJson.add(values.getContentId(),refinedJsonObject);
                resourceList.add(downloadJsons);
            }
            downloadJson.add(type,resourceList);
        }
        testObject.add("download",downloadJson);
        testObject.add("content",contentJson);



        Log.d("TestingV3","JsonMethod: "+testObject);


    }

    private void addChild(String[] children){

    }



    public void showContent() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("resourceCount", "user email and selected values: "+selectedValue+" "+SharedPrefrence.getItem(mContext, "emailId", ""));
                List<ContentEntity> contentEntities = null;
                try {
                    contentEntities = AppDatabase.getDb(mContext).contentDao().getContentByTypeInitiatedByUser(selectedValue);
                } catch (Exception e) {
                    Log.d("Exception","Message: "+e.getMessage());
                }
                Log.d("resourceCount", "run: "+contentEntities.size());
                if (selectedValue.equals("Resource")) {
                    long[] downloadIds = new long[contentEntities.size()];
                    for (int i = 0; i < contentEntities.size(); i++) {
                        downloadIds[i] = contentEntities.get(i).getDownloadId();
                    }
//            updateDownloadStatus(downloadIds);
                }
//        Log.d("DownloadWebView", "content by type " + contentEntities.size());
                if (contentEntities.size() < 1) {
                    if (progressCount == 0) {
                        Snackbar.make(mConstraintLayout, "No " + selectedValue + " Exists", Snackbar.LENGTH_LONG).show();
                    }
                }
//        Log.d("IS_ON_HOME", String.valueOf(selectedValue));

                JsonParser jsonParser = new JsonParser();
                listOfContents = new ArrayList<>();
                for (ContentEntity content : contentEntities) {

                    JsonObject contentStructure = new JsonObject();
                    JsonObject contentsVar = jsonParser.parse(content.getContentMetaJson()).getAsJsonObject();
//            Log.d("ShowContent","Content "+contentsVar.toString());
                    contentStructure.add("content", contentsVar);
//            Log.d("GO_OFFLINE", contentsVar.get("appIcon").getAsString());
                    JsonObject metaData = new JsonObject();
                    metaData.addProperty("downloadFinishedOn", content.getModifiedDate());
                    metaData.addProperty("downloadInitOn", content.getRequestedDate());


                    DownloadStatusEntity downloadStatus = AppDatabase
                            .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(content.getContentId());
                    if(downloadStatus.getDownloadStatus() != null && downloadStatus.getDownloadStatus().equalsIgnoreCase(Constants.DOWNLOADED)){
                        metaData.addProperty("expires", content.getExpiryDate());
                    }
//                    Log.d("checkuseremailTable", "run: "+downloadStatus.getUser_email());

//            if(downloadStatus.equalsIgnoreCase(Constants.DOWNLOADED)){
//                metaData.addProperty("expires", (content.getExpiryDate()));
//            }
                    if (downloadStatus != null) {
                        if (content.getChildren() != null) {
                            String[] children = content.getChildren().split(",");
                            String status = downloadStatusForCourse(content.getContentId(), children);
                            if(status.equalsIgnoreCase(Constants.DOWNLOADED)){
                                metaData.addProperty("expires", (content.getExpiryDate()));
                            }
                            metaData.addProperty("status", status );
                        } else {
//                    metaData.addProperty("progress", downloadStatus.getPercentCompleted());
//                    if(content.getContentId().equalsIgnoreCase(content.getContentId())){
//                        DownloadStatusEntity entity = new DownloadStatusEntity();
//                        entity.setContentId(content.getContentId());
//                        entity.setDownloadStatus("FAILED");
//                        AppDatabase.getDb(mContext).downloadStatusDao().updateDownloadStatus(entity);
//                    }
//                    else{
                            metaData.addProperty("status", downloadStatus.getDownloadStatus());
//                    }

                        }

                        downloadStatus.setDownloadStatus(metaData.get("status").getAsString());
//                Log.d("DownloadsWebView", "Setting download status " + downloadStatus.getDownloadStatus());
                        try {
                            AppDatabase.getDb(mContext).downloadStatusDao().updateDownloadStatus(downloadStatus);
                        } catch (Exception e) {
                            Log.d("Exception","Message: "+e.getMessage());
                        }

                        contentStructure.add("meta", metaData);
                        if (downloadStatus.isInitiatedByUser()) {
//                    Log.d("DownloadsWebn", content.getContentId());
                            listOfContents.add(contentStructure);
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tocPage.evaluateJavascript("initApp('downloads'," + listOfContents + ")", null);
                    }
                });
            }
        });
    }



    private String downloadStatusForCourse(String parentId, String[] contentIds) {
        DownloadStatusEntity downloadStatus = AppDatabase.getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(parentId);
        Log.d("downloadstatus", "download url " + downloadStatus.getDownloadUrl());
        if (downloadStatus.getDownloadUrl() != null && downloadStatus.getDownloadUrl().contains(openRapUrl)) {
            return downloadStatus.getDownloadStatus();
        }
        if (downloadStatus.getDownloadStatus().equals(CANCELLED)) {
            return CANCELLED;
        }
        List<String> allStatus = null;
        try {
            allStatus = AppDatabase.getDb(mContext).downloadStatusDao().getDownloadStatusForIds(contentIds);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
//        Log.d("downloadStatusForCourse", "All Status " + allStatus.size());
//        Log.d("downloadStatusForCourse", "Content Ids " + contentIds.length);
        if (allStatus.size() < contentIds.length && !downloadStatus.getDownloadStatus().equals(INITIATED)) {
//            Log.d("StatusCourse", "Partial Status sent");
            return "PARTIAL";
        }

        HashSet<String> allStatusSet = new HashSet<>(allStatus);

        if (allStatusSet.contains(FAILED)) {
            return FAILED;
        } else if (allStatusSet.contains(CANCELLED)) {
            return CANCELLED;
        }

        if (allStatusSet.size() == 1 && allStatus.get(0).equals("DOWNLOADED")) {
            return "DOWNLOADED";
        } else {
            return "INITIATED";
        }
    }

    private void showDownloadProgress() {
        try{
        progressCount = AppDatabase.getDb(mContext).downloadStatusDao().downloadInProgressCount();
        Log.d("downloadprogress", "Count - " + progressCount);
        if (progressCount > 0) {
            mSnackbarProgress = Snackbar.make(mConstraintLayout, progressCount + " Ongoing download(s). Check notifications to see progress", Snackbar.LENGTH_INDEFINITE);
            mSnackbarProgress.show();
        } else {
            if (mSnackbarProgress != null) {
                mSnackbarProgress.dismiss();
            }
        }
        }catch(Exception e){
            Log.d("DownloadProgress", "showDownloadProgress: "+e.getMessage());
        }
    }

    @Override
    public void connectedToInternet() {
        Log.d("ReachedhereDownloads","connected to internet");
        isConnectedToInternet = true;
        hotSpotButton.setVisibility(View.GONE);
//        catalogBtn.setVisibility(View.VISIBLE);
//        marketBtn.setVisibility(View.VISIBLE);
//        infyTv.setVisibility(View.VISIBLE);

    }




    @Override
    public void offline() {
        Log.d("Reachedhere","connected to offline");
        isConnectedToInternet = false;
        hotSpotButton.setVisibility(View.GONE);
        Log.d("DownloadsWebView", "Status Offline");
    }


    public void courseIsClicked(View view) {
        selectedValue = "Course";
        viewResource.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        viewCourse.setBackgroundColor(Color.parseColor("#FF3f51b5"));
        viewModule.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        showContent();
    }

    public void moduleIsClicked(View view) {
        selectedValue = "Collection";
        viewResource.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        viewCourse.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        viewModule.setBackgroundColor(Color.parseColor("#FF3f51b5"));
        showContent();
    }

    public void resourceIsClicked(View view) {
        selectedValue = "Resource";
        viewResource.setBackgroundColor(Color.parseColor("#FF3f51b5"));
        viewCourse.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        viewModule.setBackgroundColor(Color.parseColor("#FFD7D2D2"));
        showContent();
    }


}