package com.infosysit.rainforest;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infosysit.rainforest.services.ConnectivityReceiver;
import com.infosysit.rainforest.services.TOCBridge;
import com.infosysit.sdk.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.connectivityMessage;
import static com.infosysit.sdk.UtilityJava.isOnline;

public class TocWebView extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    List<String> successfullDownloads = new  ArrayList<>();
    Context mContext;
    boolean isConnectedToInternet = false;

    private ConnectivityReceiver connectivityReceiver;
    WebView tocPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_toc_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView lexLogo = (ImageView) findViewById(R.id.yourlogo);
//        ImageView catalogBtn = (ImageView) findViewById(R.id.catalog);
//        ImageView marketBtn = (ImageView) findViewById(R.id.marketing);
//        ImageView infyTv = (ImageView) findViewById(R.id.infyTv);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_hamburger));


        tocPage = (WebView) findViewById(R.id.tocWebView);
        changeWindowColor(this);
        loadWebView();

    }


    private void loadWebView(){
        tocPage.getSettings().setJavaScriptEnabled(true);
        tocPage.getSettings().setDomStorageEnabled(true);
        tocPage.getSettings().setAllowFileAccessFromFileURLs(true);
        tocPage.loadUrl("file:///android_asset/app-toc/index.html");
        tocPage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tocPage.setLongClickable(false);
        tocPage.addJavascriptInterface(new TOCBridge(this),"appRef");
        tocPage.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(!(request.getUrl().toString().contains("file///")) || (request.getUrl().toString().contains("infosys"))){
                    tocPage.removeJavascriptInterface("appRef");
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                printArtifactUrl(Constants.tocMeta);
//                successfullDownloads = ContentSQLService.getAllSuccessfullDowjnloads();
//                checkDeletedContent(Constants.tocMeta);
                tocPage.evaluateJavascript("initApp('toc',"+ Constants.tocMeta+")",null);
            }
        });
    }

    private void printArtifactUrl(JsonObject tocMeta) {
        Log.d("TestingArtifact","mimeType: "+tocMeta.get("mimeType") +" artifactUrl: "+ tocMeta.get("artifactUrl"));
        JsonArray children = tocMeta.getAsJsonObject().getAsJsonArray("children");
        if(children.size() > 0){
            for (JsonElement child : children ){
                printArtifactUrl((JsonObject) child);
            }

        }
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

    public void goHome(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/home");
        }
        else{
            Toast.makeText(TocWebView.this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }

    public void goBrand(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/catalog/marketing/Brand Assets");
        }
        else{
            Toast.makeText(TocWebView.this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }

    public void goInfyTv(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/infytv");
        }
        else{
            Toast.makeText(TocWebView.this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

    }
    public void goCatalog(View view) {
        if(isConnectedToInternet){
            Util.navigateToPage(mContext,"/catalog");
        }
        else{
            Toast.makeText(TocWebView.this, connectivityMessage, Toast.LENGTH_SHORT).show();
        }

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
//            Intent homeActivity = new Intent(TocWebView.this, DownloadsWebView.class);
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
            Intent homeActivity = new Intent(TocWebView.this, SettingsActivity.class);
            startActivity(homeActivity);
        }
        else{
            if(isOnline(mContext)){
                TocWebView.super.onBackPressed();
            } else {
                Toast.makeText(mContext, connectivityMessage, Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkDeletedContent(JsonObject contentJson){

        if (contentJson.get("contentType").getAsString().equalsIgnoreCase("Resource")) {
            if(!successfullDownloads.contains(contentJson.get("identifier").getAsString()))
                contentJson.add("artifactUrl",null);
            return;
        }

        JsonArray children = contentJson.get("children").getAsJsonArray();

        for(JsonElement child : children){
            checkDeletedContent((JsonObject) child);
        }
    }

    public void goHomeUrl(String url){
        if (isConnectedToInternet) {
            if(url.contains("viewer")){
                Toast.makeText(this, "Assessment can't be played in offline mode. Redirecting you to assessment in online mode..", Toast.LENGTH_LONG).show();
                Util.navigateToPage(this, url);
            }
            else{
                Util.navigateToPage(this, url);
            }

        }
        else{
            if(url.contains("viewer")){
                Toast.makeText(this, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, Constants.connectivityMessage , Toast.LENGTH_LONG).show();
            }

        }
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
