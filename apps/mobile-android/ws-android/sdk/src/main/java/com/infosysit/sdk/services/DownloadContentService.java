package com.infosysit.sdk.services;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.LexDownloadManager;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.models.ContentDownloadEntity;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.infosysit.sdk.Constants.CONTENT_URL_PREFIX_REGEX;
import static com.infosysit.sdk.Constants.DATA_DIR_PATH;
import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_MODE;
import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_RES_ID;
import static com.infosysit.sdk.Constants.MIME_TO_EXTENSION;
import static com.infosysit.sdk.Constants.STORAGE_BASE_PATH;
import static com.infosysit.sdk.Constants.TMP_DIR_PATH;
import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.openRapUrl;

public class DownloadContentService extends IntentService {

    private static final String TAG = DownloadContentService.class.getCanonicalName();

    String mContentId;
    String mDownloadMode;
    Context mContext;
    StringBuilder children = new StringBuilder();
    String parentId = null;
    List<String> downloadedResource = new ArrayList<String>();
    ToastHandlerJava mToastHandler = new ToastHandlerJava(this);


    public DownloadContentService() {
        super("DownloadContentService");
        mContext = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            mContentId = intent.getStringExtra(EXTRA_DOWNLOAD_RES_ID).replace("\"", "");
            Log.d("DownloadContentOP","ID: "+mContentId);
            mDownloadMode = intent.getStringExtra(EXTRA_DOWNLOAD_MODE);
            if (mDownloadMode == null) {
                mDownloadMode = "LEX";
            }

            if (mDownloadMode.equals("OPENRAP")) {
                handleOpenRapDownload();
            } else {
                handleInternetDownload();
            }
        }
    }

    private void handleInternetDownload() {
        ContentEntity contentEntity = null;
        try {
            contentEntity = AppDatabase.getDb(mContext).contentDao().getContentByContentId(mContentId);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        if (contentEntity != null) {
            JsonParser parser = new JsonParser();

            try {
                hierarchyFetched((JsonObject) (parser.parse(contentEntity.getContentMetaJson())));
            } catch (Exception ex) {
//                    Log.d("DownloadContent",ex.getMessage());
            }

        }
    }

    private void handleOpenRapDownload() {
        DownloadStatusEntity existingDownload = AppDatabase.getDb(this).downloadStatusDao().getDownloadStatusByContentId(mContentId);
        if (existingDownload != null && !existingDownload.getDownloadStatus().equals(Constants.FAILED)) {
            Log.d("existingdownload", existingDownload.getDownloadUrl());
            Log.d("existingdownload", existingDownload.getDownloadStatus());
            return;
        }

        long downloadId = LexDownloadManager.getInstance(this)
                .enqueue(openRapUrl + "/public/lex-content/" + mContentId + ".lex", TMP_DIR_PATH, mContentId + ".lex", true);
        Log.d("openRapTesting","DownloadId: "+downloadId);
        DownloadStatusEntity downloadStatusEntity = new DownloadStatusEntity(
                mContentId,
                true,
                openRapUrl + "/public/" + mContentId + ".zip",
                "INITIATED",
                0,
                downloadId,
                0
//                ,Constants.UserEmail

        );
        try {
            AppDatabase.getDb(this).downloadStatusDao().insertAll(downloadStatusEntity);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        Log.d("openRapTesting","DownloadId: "+mContentId);
        TelemetryServices.pushDownloadTelemetryData(mContext, mContentId, "OPENRAP", "INITITATED");
    }

    public  void showSizeAlert(String size){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Permission for Download");
        builder.setMessage("Size of this content is "+size+". Do you really want to continue?")
                .setCancelable(false);

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(mContext,"Download will begin once you are connected to WiFi",Toast.LENGTH_LONG).show();

            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(mContext,"Download Initiated over Mob Data. Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                alertDialog.show();
            }
        });
    }

    private void hierarchyFetched(final JsonObject contentJson) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        Log.d("DownloadContentOP",contentJson.toString());
//        showSizeAlert(contentJson.get("size").getAsString());
        try {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    downloadedResource = AppDatabase.getDb(mContext).downloadStatusDao().getSuccessfulDownloads();
                    if(!downloadedResource.contains(mContentId)) {
                        if(checkExternalContent(contentJson)){
                            ContentDownloadEntity contentDownloadEntity = downloadContentNew(contentJson, new ContentDownloadEntity());
                            try {
                                AppDatabase.getDb(mContext).contentDao()
                                        .insertAll(contentDownloadEntity.contentEntities.toArray(new ContentEntity[contentDownloadEntity.contentEntities.size()]));
                                AppDatabase.getDb(mContext).downloadStatusDao()
                                        .insertAll(contentDownloadEntity.downloadStatusEntities.toArray(new DownloadStatusEntity[contentDownloadEntity.downloadStatusEntities.size()]));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            TelemetryServices.pushDownloadTelemetryData(mContext, contentJson, mDownloadMode, Constants.INITIATED);

                        }
                        else{
                            mToastHandler.showToast("Contains external content. Please Download individual resources.", Toast.LENGTH_LONG);
                        }
                    }

                    else{
                        mToastHandler.showToast("Already Exists.", Toast.LENGTH_LONG);
                    }
                }
            });
            t.start();
        } catch (Exception ex) {
            Log.e("DownloadContent", ex.getMessage());
        }
    }

    private boolean checkExternalContent(JsonObject contentJSON) {
        if (contentJSON.get("contentType").getAsString().equals("Resource")) {
            if (contentJSON.get("downloadUrl").getAsString().equalsIgnoreCase("")) {
                return false;
            }
        }

        HashSet<Boolean> results = new HashSet<>();
        Log.d("DownloadCrash","Values "+contentJSON.get("identifier").getAsString());
        for (JsonElement module : contentJSON.getAsJsonArray("children")) {
            results.add(checkExternalContent((JsonObject) module));
        }
        Log.d("DownloadService","Service reached here");
        return !results.contains(Boolean.FALSE);

    }



    public ContentDownloadEntity downloadContentNew(JsonObject contentJSON, ContentDownloadEntity entities) {
        String identifier = contentJSON.get("identifier").getAsString();
        boolean initiatedByUser = mContentId.equals(identifier);

        downloadThumbnail(contentJSON.get("appIcon").getAsString(), identifier);
        contentJSON.addProperty("appIcon", STORAGE_BASE_PATH + DATA_DIR_PATH + identifier + ".image.png");

        Log.d("DemoTesting","1: "+contentJSON.get("contentType").getAsString());
        if (contentJSON.get("contentType").getAsString().equals("Resource")) {
            if(contentJSON.get("mimeType").getAsString().equalsIgnoreCase("application/x-mpegURL")){
                contentJSON.addProperty("mimeType","video/mp4");
            }
            String mimeType = contentJSON.get("mimeType").getAsString();
            String extension = MIME_TO_EXTENSION.get(mimeType);
            Log.d("LexTestingDownloads","MimeType: "+mimeType);
            Log.d("LexTestingDownload","Identifier "+identifier+" downloadUrl "+getDownloadUrl(contentJSON));

            long downloadId = LexDownloadManager.getInstance(mContext)
                    .enqueue(getDownloadUrl(contentJSON), DATA_DIR_PATH, identifier + "." + extension, false);
            entities.contentEntities.add(createContentEntity(contentJSON, downloadId, extension));
            entities.downloadStatusEntities.add(createDownloadEntity(contentJSON, downloadId, initiatedByUser));

            return entities;
        } else {
            entities.contentEntities.add(createContentEntity(contentJSON, -1, ""));
            entities.downloadStatusEntities.add(createDownloadEntity(contentJSON, -1, initiatedByUser));
        }

        for (JsonElement module : contentJSON.getAsJsonArray("children")) {
            if(!downloadedResource.contains(module.getAsJsonObject().get("identifier").getAsString())){
                Log.d("DemoTesting","id: "+module.getAsJsonObject().get("identifier").getAsString());
                downloadContentNew((JsonObject) module, entities);
            }
            else{
                Log.d("AlreadyExits","id "+module.getAsJsonObject().get("identifier").getAsString());
            }
        }

        return entities;
    }

    private ContentEntity createContentEntity(JsonObject contentJson, long downloadId, String extension) {
        long currentMillis = System.currentTimeMillis();
        StringBuilder childs = getAllChildren(contentJson);
        ContentEntity contentEntity = new ContentEntity(
                contentJson.get("identifier").getAsString(),
                downloadId,
                contentJson.get("contentType").getAsString(),
                String.valueOf(currentMillis / 1000),
                String.valueOf(currentMillis / 1000),
                String.valueOf(currentMillis + Constants.expiry),
                contentJson.toString(),
                childs.deleteCharAt(childs.length() - 1).toString(),
                "",
                extension
        );
        Log.d("DownloadContent", "Inserting " + contentEntity.toString());
        return contentEntity;
    }

    private DownloadStatusEntity createDownloadEntity(JsonObject contentJson, long downloadId, boolean initiatedByUser) {
        String downloadUrl = "";
        if(contentJson.get("contentType").getAsString().equalsIgnoreCase("resource")){
            downloadUrl = getDownloadUrl(contentJson);
        }
        DownloadStatusEntity downloadStatusEntity = new DownloadStatusEntity(
                contentJson.get("identifier").getAsString(),
                initiatedByUser,
                downloadUrl,
                "INITIATED",
                0,
                downloadId,
                1
//                ,Constants.UserEmail
        );
        Log.d("DownloadContent", "Inserting " + downloadStatusEntity.toString());
        return downloadStatusEntity;
    }

    public String getDownloadUrl(JsonObject contentJSON) {
        String url;
        if(!contentJSON.get("downloadUrl").getAsString().isEmpty()){
            url = downloadableUrl(contentJSON.get("downloadUrl").getAsString().trim()).replace("?type=","?ns=true&type=");
        }else{
            url = downloadableUrl(contentJSON.get("artifactUrl").getAsString().trim()).replace("?type=","?ns=true&type=");
        }
        contentJSON.addProperty("artifactUrl",url);
        return url;
//        if (mimeType.equalsIgnoreCase("application/web-module") || mimeType.equalsIgnoreCase("application/quiz")) {
//            return downloadableUrl(contentJSON.get("downloadUrl").getAsString().trim()).replace("?type=","?ns=true&type=");
//        } else {
//            if(!contentJSON.get("artifactUrl").getAsString().isEmpty())
//                return downloadableUrl(contentJSON.get("artifactUrl").getAsString().trim()).replace("?type=","?ns=true&type=");
//            return downloadableUrl(contentJSON.get("downloadUrl").getAsString().trim()).replace("?type=","?ns=true&type=");
//        }
    }


    private String downloadableUrl(String url){
        Uri urlUri = Uri.parse(url);
        int uriPort = urlUri.getPort();
        String downloadUrl;
        if(uriPort != -1){
            downloadUrl = baseUrl +"/"+url.split(uriPort + "/")[1];
        }
        else{
            if(url.contains("http://private-")){
                downloadUrl = baseUrl+removeScript(url);
            }
            else if(url.startsWith("/")){
                downloadUrl = baseUrl+url;
            }
            else{
                downloadUrl = url;
            }
        }
        return downloadUrl.replace(" ", "%20");
    }

    public void downloadThumbnail(String thumbnailUrl, String identifier) {
        createNoImageFile(mContext);


        LexDownloadManager.getInstance(mContext).enqueue(downloadableUrl(thumbnailUrl), DATA_DIR_PATH, identifier + ".image.png", false);
    }

    private void createNoImageFile(Context context){
        File noImage = new File(mContext.getExternalFilesDir(""), DATA_DIR_PATH +".nomedia");
        if (!noImage.exists()) {
            try {
                noImage.createNewFile();
            } catch (IOException ex) {
                Log.e("ThumbnailCreation", ex.getMessage());
            } catch (Exception ex) {
                Log.e("ThumbnailCreation", ex.getMessage());
            }
        }
    }

    public StringBuilder getAllChildren(JsonObject contentJSON) {

        JsonArray modules = contentJSON.getAsJsonArray("children");
        if (modules.size() > 0) {
            for (JsonElement children : modules) {
                getAllChildren((JsonObject) children);
            }
        } else {
            children.append(contentJSON.get("identifier").getAsString() + ",");
        }
        return children;
    }

    private String removeScript(String content) {
        Pattern p = Pattern.compile("((http|https)://private-[a-zA-Z0-9-]*/)",
                Pattern.CASE_INSENSITIVE);
        return p.matcher(content).replaceAll("/");
    }

    public boolean urlExists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            con.setRequestProperty("authorization", UtilityJava.tokenValue);
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}