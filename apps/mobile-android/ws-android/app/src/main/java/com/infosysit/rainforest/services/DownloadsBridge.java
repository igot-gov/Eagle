package com.infosysit.rainforest.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.rainforest.DownloadsWebView;
import com.infosysit.rainforest.PlayerActivity;
import com.infosysit.rainforest.R;
import com.infosysit.rainforest.TocWebView;
import com.infosysit.rainforest.Util;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.services.ConnectivityUtility;
import com.infosysit.sdk.services.DecryptionTask;
import com.infosysit.sdk.services.DownloadContentService;

import java.util.ArrayList;
import java.util.List;

import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_RES_ID;
import static com.infosysit.sdk.UtilityJava.getExtension;

/**
 * Created by akansha.goyal on 3/31/2018.
 */

public class DownloadsBridge {
    List<ContentEntity> retryDownload = new ArrayList<>();
    List<String> succesfullDownlods = new ArrayList<>();

    private Context mContext;

    public DownloadsBridge(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void NAVIGATION_DATA_OUTGOING(String data) {
        Log.d("NAVIGATION_DATA_OUTGOI", data);
        JsonParser jsonParserObj = new JsonParser();
        JsonObject navigatedDataJson = jsonParserObj.parse(data).getAsJsonObject();
        String urlData = navigatedDataJson.get("url").getAsString();
//        String courseId = navigatedDataJson.get("params").getAsJsonObject().get("courseId").getAsString();
        String[] navigatedData = urlData.split("/");

//        DownloadStatusEntity entity = new DownloadStatusEntity();
//        entity.setContentId(navigatedData[2]);
//        entity.setDownloadStatus("FAILED");
//        AppDatabase.getDb(mContext).downloadStatusDao().updateDownloadStatus(entity);
//
        ContentEntity content = null;
        try {
            content = AppDatabase.getDb(mContext).contentDao().getContentByContentId(navigatedData[2]);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        Constants.contentMeta = jsonParserObj.parse(content.getContentMetaJson()).getAsJsonObject();
        Log.d("JsonObject","JSON: "+Constants.contentMeta);
        Log.d("mimetype", "mimetype of content to play " + Constants.contentMeta.get("mimeType").getAsString());
        if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/web-module")) {
            Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "/manifest.json");
        } else if (Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz")) {
            Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "/quiz.json");
        } else {
            //For file save
//            Constants.contentMeta.addProperty("artifactUrl",PACKAGE_BASE_PATH + navigatedData[2]+"."+content.getExtension());

            Constants.contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + navigatedData[2] + "." + content.getExtension());
        }


        if (navigatedData[1].equalsIgnoreCase("viewer") && !(Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz") && Constants.contentMeta.get("resourceType").getAsString().equalsIgnoreCase("Assessment"))) {
            Constants.tocMeta = new JsonObject();
            //For file save
            DecryptionTask decryption = new DecryptionTask(mContext);
            Log.d("downloadsbrdige", "extension " + content.getExtension());
            decryption.execute(content.getContentId() + "." + content.getExtension());
            Intent playerPage = new Intent(mContext, PlayerActivity.class);
            playerPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(playerPage);

//            Intent intent = new Intent();
//            intent.setAction(android.content.Intent.ACTION_VIEW);
//            Log.d("PlayerActivity",Constants.contentMeta.get("artifactUrl").getAsString());
//            intent.setDataAndType(Uri.parse(Constants.contentMeta.get("artifactUrl").getAsString()), "audio/*");
//            mContext.startActivity(intent);
        } else if (navigatedData[1].equalsIgnoreCase("toc")) {
            succesfullDownlods = AppDatabase.getDb(mContext).downloadStatusDao().getSuccessfulDownloads();
            Constants.tocMeta = Constants.contentMeta;
            changeJSONForImage(Constants.tocMeta);

//            for (JsonElement module : modules){
//                String identifier = module.getAsJsonObject().get("identifier").getAsString();
//                module.getAsJsonObject().addProperty("appIcon","file:///storage/emulated/0/Lex_android/"+identifier+"image.png");
//            }

//            Log.d("OfflineLayoutBridge",objectContent.getAsString());
            Intent homePage = new Intent(mContext, TocWebView.class);
            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(homePage);
        } else {
            if (ConnectivityUtility.isConnected(mContext)) {
                Util.navigationWebView(mContext, "/viewer/" + navigatedData[2]);
                return;
            } else {
                Toast.makeText(mContext, "This content is not available in offline mode", Toast.LENGTH_LONG).show();
                return;
            }

        }
//        Log.d("OfflineLayoutBridge", data);
//        Log.d("onPageFinished", course);

    }

    public void changeJSONForImage(JsonObject contentMeta) {
        String identifier = contentMeta.getAsJsonObject().get("identifier").getAsString();
        contentMeta.getAsJsonObject().addProperty("appIcon", Constants.STORAGE_BASE_PATH + Constants.DATA_DIR_PATH + identifier + ".image.png");

        if (contentMeta.has("children") && contentMeta.get("children").getAsJsonArray().size() > 0) {
            JsonArray children = contentMeta.get("children").getAsJsonArray();
            for (JsonElement child : children) {
                changeJSONForImage((JsonObject) child);
            }
        } else {
            if (!succesfullDownlods.contains(identifier) || contentMeta.getAsJsonObject().get("mimeType").getAsString().equalsIgnoreCase("video/x-youtube")) {
                contentMeta.getAsJsonObject().add("artifactUrl", null);
            } else {
                String extension = getExtension(contentMeta.get("artifactUrl").getAsString());
                if (contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/web-module")) {
                    contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + identifier + "/manifest.json");
                } else if (contentMeta.get("mimeType").getAsString().equalsIgnoreCase("application/quiz")) {
                    contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + identifier + "/quiz.json");
                } else {
                    contentMeta.addProperty("artifactUrl", Constants.STORAGE_BASE_PATH + Constants.TMP_DIR_PATH + identifier + "." + extension);
                }
            }
        }
    }




    @JavascriptInterface
    public void DOWNLOAD_RETRY(String data) {
        try {
            if (ConnectivityUtility.isConnected(mContext)) {
                Toast.makeText(mContext, "Download Re-initiated.", Toast.LENGTH_LONG).show();
                JsonParser jsonParserObj = new JsonParser();
                String identifier = jsonParserObj.parse(data).getAsJsonObject().get("id").getAsString();
                Intent intent = new Intent(mContext, DownloadContentService.class);
                intent.putExtra(EXTRA_DOWNLOAD_RES_ID, identifier);
                mContext.startService(intent);
                try {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((DownloadsWebView) mContext).showContent();
                        }
                    }, 500);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("DownloadsBridge", ex.getMessage());
                }
//                ContentEntity contentEntity = AppDatabase.getDb(mContext).contentDao().getContentByContentId(identifier);
//                if(!contentEntity.getContentType().equalsIgnoreCase("Resource")){
//                    String[]idsToDownload = contentEntity.getChildren().split("\\,");
//                    for (String idToDownload : idsToDownload){
//                        ContentEntity contentToUpdate =  AppDatabase.getDb(mContext)
//                                .contentDao().getContentByContentId(idToDownload);
//                        retryDownload.add(contentToUpdate);
//                    }
//                }
//                else{
//                    retryDownload.add(contentEntity);
//                }
//                retryDownloadContent();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.errorConnectivityMsg), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {

        }
    }

//    public void retryDownloadContent(){
//        for(ContentEntity contentToDownload : retryDownload){
//            String url_media = null;
//            JsonParser parser = new JsonParser();
//            JsonObject contentJSON = (JsonObject) parser.parse(contentToDownload.getContentMetaJson());
//
//            if (contentJSON.get("mimeType").getAsString().equalsIgnoreCase("application/web-module")){
//                url_media = contentJSON.get("downloadUrl").getAsString();
//                contentJSON.addProperty("artifactUrl","file:///storage/emulated/0/.Lex_android/data/"+contentToDownload.getContentId()+"/manifest.json");
//            }
//            else{
//                Uri artifactUrlUri = Uri.parse(contentJSON.get("artifactUrl").getAsString());
//                int port = artifactUrlUri.getPort();
//                String mediaContent = contentJSON.get("artifactUrl").getAsString().split(port+"/")[1];
//                Log.d("mediacontent",mediaContent);
//                String encodeMediaContent = mediaContent.replace(" ","%20");
//                url_media = Constants.dev_page + "/" + encodeMediaContent;
//                contentJSON.addProperty("artifactUrl","file:///storage/emulated/0/.Lex_android/data/"+contentToDownload.getContentId()+"."+getExtension(url_media));
//            }
//
//            final long downloadId = LexDownloadManager
//                    .getInstance()
//                    .enqueue(url_media, Constants.DATA_DIR_PATH, contentToDownload.getContentId()+"."+contentToDownload.getExtension());
//            ContentEntity content = new ContentEntity();
//            Log.d("downloadContent", "its a resource" + contentToDownload.getContentId());
//            content.setContentId(contentToDownload.getContentId());
//            content.setDownloadId(downloadId);
//            AppDatabase.getDb(mContext).contentDao().updateContent(content);
//
//            DownloadStatusEntity downloadStatus = new DownloadStatusEntity();
//            downloadStatus.setContentId(contentToDownload.getContentId());
//            downloadStatus.setDownloadStatus("INITIATED");
//            downloadStatus.setDownloadId(downloadId);
//            AppDatabase.getDb(mContext).downloadStatusDao().updateDownloadStatus(downloadStatus);
//        }
//    }

    @JavascriptInterface
    public void DOWNLOAD_REMOVE(String data) {
//        Log.d("REMOVE_THIS",data);
        JsonParser jsonParserObj = new JsonParser();
        String identifier = jsonParserObj.parse(data).getAsJsonObject().get("id").getAsString();
        ((DownloadsWebView) mContext).showDialogRemove(identifier);


    }

    @JavascriptInterface
    public void NAVIGATION_NEW_TAB_DATA_OUTGOING(String value) {
//        Log.d("CHECK_VALUES",value);
    }

    @JavascriptInterface
    public void DOWNLOAD_CANCEL(String data) {
//        Log.d("DOWNLOAD_CANCEL",data);
        JsonParser jsonParserObj = new JsonParser();
        String identifier = jsonParserObj.parse(data).getAsJsonObject().get("id").getAsString();
        ((DownloadsWebView) mContext).showDialogCancel(identifier);

    }






}