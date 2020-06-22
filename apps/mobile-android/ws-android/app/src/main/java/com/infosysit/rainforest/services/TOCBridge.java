package com.infosysit.rainforest.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.PlayerActivity;
import com.infosysit.rainforest.TocWebView;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;
import com.infosysit.sdk.services.DecryptionTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akansha.goyal on 3/31/2018.
 */

public class TOCBridge {
    List<String> succesfullResource = new ArrayList<String>();
    private Context mContext;
    
    public TOCBridge(Context context) {
        mContext = context;
    }
    

    @JavascriptInterface
    public void NAVIGATION_DATA_OUTGOING(String data) {
        Log.d("NAVIGATION_DATA_OUTGOI",data);
        JsonParser jsonParserObj = new JsonParser();
        JsonObject navigatedDataJson = jsonParserObj.parse(data).getAsJsonObject();
        String urlData = navigatedDataJson.get("url").getAsString();
        String[] navigatedData =  urlData.split("/");
//
        ContentEntity content  = null;
        try {
            content = AppDatabase.getDb(mContext).contentDao().getContentByContentId(navigatedData[2]);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        DownloadStatusEntity downloadStatus = AppDatabase.getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(navigatedData[2]);
        Constants.contentMeta = jsonParserObj.parse(content.getContentMetaJson()).getAsJsonObject();
        Log.d("JSONTEST","JSON: "+Constants.contentMeta);
        if(!downloadStatus.getDownloadStatus().equalsIgnoreCase("DOWNLOADED")){
            Toast.makeText(mContext,"This content is not Downloaded yet",Toast.LENGTH_LONG).show();
            return;
        }
        else if(Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("video/x-youtube")){
            Constants.contentMeta.add("artifactUrl",null);
            return;
        }
        if( Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/web-module")){
//            Log.d("Web module", "path" + Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2]+"/manifest.json");
            Constants.contentMeta.addProperty("artifactUrl",Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2]+"/manifest.json");
        }
        else if(Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz") && !Constants.contentMeta.get("resourceType").getAsString().equalsIgnoreCase("Assessment")){
            Constants.contentMeta.addProperty("artifactUrl",Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2]+"/quiz.json");
        }
        else if(Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz") && Constants.contentMeta.get("resourceType").getAsString().equalsIgnoreCase("Assessment")){
            ((TocWebView)mContext).goHomeUrl("/viewer/"+navigatedData[2]);
            return;
        }
        else{
            // hardcode change
            Constants.contentMeta.addProperty("artifactUrl",Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2]+"."+content.getExtension());
        }

        Log.d("NAVIGATION_DATA_OUTGOI","DATA: "+Constants.contentMeta.get("artifactUrl"));



        DecryptionTask decryption = new DecryptionTask(mContext);
        decryption.execute(content.getContentId()+"."+content.getExtension());
        if(navigatedDataJson.has("params")){
            String courseId = navigatedDataJson.get("params").getAsJsonObject().get("courseId").getAsString();
            ContentEntity tocContent  = null;
            try {
                tocContent = AppDatabase.getDb(mContext).contentDao().getContentByContentId(courseId);
            } catch (Exception e) {
                Log.d("Exception","Message: "+e.getMessage());
            }
            Constants.tocMeta = jsonParserObj.parse(tocContent.getContentMetaJson()).getAsJsonObject();
            Intent homePage = new Intent(mContext, PlayerActivity.class);
            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(homePage);
//
//
        }




    }








    @JavascriptInterface
    public void NAVIGATION_NEW_TAB_DATA_OUTGOING(String value){
//        Log.d("CHECK_VALUES",value);
    }


}
