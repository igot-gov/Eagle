package com.infosysit.sdk.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.LexDownloadManager;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SqlCrudService extends IntentService {

    private static final String ACTION_DELETE = "com.infosysit.sdk.services.action.DELETE";
    private static final String ACTION_CANCEL = "com.infosysit.sdk.services.action.CANCEL";
    private static final String ACTION_EXPIRY = "com.infosysit.sdk.services.action.EXPIRY";

    private static final String EXTRA_CONTENT_ID = "com.infosysit.sdk.services.extra.CONTENT_ID";

    public SqlCrudService() {
        super("SqlCrudService");
    }

    public static void startActionDelete(Context context, String param1) {
        Intent intent = new Intent(context, SqlCrudService.class);
        intent.setAction(ACTION_DELETE);

        intent.putExtra(EXTRA_CONTENT_ID, param1);
        context.startService(intent);
    }

    public static void startActionExpiry(Context context, ArrayList<String> param1) {
        Intent intent = new Intent(context, SqlCrudService.class);
        intent.setAction(ACTION_EXPIRY);
        intent.putStringArrayListExtra(EXTRA_CONTENT_ID,param1);
        context.startService(intent);
    }

    public static void startActionCancel(Context context, String param1) {
        Intent intent = new Intent(context, SqlCrudService.class);
        intent.setAction(ACTION_CANCEL);
        intent.putExtra(EXTRA_CONTENT_ID, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DELETE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_CONTENT_ID);
                handleActionDelete(param1);
            } else if (ACTION_CANCEL.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_CONTENT_ID);
                handleActionCancel(param1);
            }
            else if(ACTION_EXPIRY.equals(action)){
                final ArrayList<String> param1 = intent.getStringArrayListExtra(EXTRA_CONTENT_ID);
                handleActionExpiry(param1);
            }
        }
    }


    public void handleActionDelete(String contentId) {
        try{
            deleteArtifactFiles(contentId);
            AppDatabase.getDb(this).downloadStatusDao().delete(contentId);
            AppDatabase.getDb(this).contentDao().delete(contentId);
        }
        catch (Exception ex){
            Log.e("handleActionDelete",ex.getMessage());
        }

    }

    public void handleActionExpiry(ArrayList<String> contentId) {
        try{
            for(String content : contentId){
                deleteArtifactFiles(content);
                AppDatabase.getDb(this).downloadStatusDao().delete(content);
                AppDatabase.getDb(this).contentDao().delete(content);
            }
        }
        catch (Exception ex){
            Log.e("handleActionDelete",ex.getMessage());
        }

    }

    public void handleActionCancel(String contentId) {
        try{
            deleteArtifactFiles(contentId);
            DownloadStatusEntity downloadStatus = AppDatabase.getDb(this).downloadStatusDao().getDownloadStatusByContentId(contentId);
            downloadStatus.setDownloadStatus(Constants.CANCELLED);
            downloadStatus.setPercentCompleted(0);
            AppDatabase.getDb(this).downloadStatusDao().updateDownloadStatus(downloadStatus);
        }
        catch (Exception ex){
            Log.e("handleActionDelete",ex.getMessage());
        }

    }

    private void deleteArtifactFiles(String contentId) {
        ContentEntity contentEntity = null;
        try {
            contentEntity = AppDatabase.getDb(this).contentDao().getContentByContentId(contentId);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        if (contentEntity != null) {
            File dataDir = new File(this.getExternalFilesDir(""), Constants.APP_DIR_PATH + "data");
//            Log.d("file", dataDir.getAbsolutePath());
//            Log.d("file", dataDir.isDirectory() + "is dir");
            File[] contentFiles = dataDir.listFiles();
            deleteAllFilesForContentId(contentId, contentFiles);

            if (contentEntity.getChildren() != null) {
                String[] children = contentEntity.getChildren().split(",");
                try {
                    List<Long> downloadIdsList = AppDatabase.getDb(this).contentDao().getDownloadIdsForContentIds(children);
                    long[] downloadIds = new long[downloadIdsList.size()];
                    for (int i = 0; i < downloadIds.length; i++) {
                        downloadIds[i] = downloadIdsList.get(i);
                    }
                    LexDownloadManager.getInstance(this).remove(downloadIds);
                } catch (Exception ex) {
                    Log.e("SqlCrudService", ex.getMessage());
                }
                try {
                    AppDatabase.getDb(this).downloadStatusDao().deleteAll(children);
                    AppDatabase.getDb(this).contentDao().deleteAll(children);
                } catch (Exception e) {
                    Log.d("Exception","Message: "+e.getMessage());
                }


                if (contentFiles != null) {
                    for (String child : children) {
                        deleteAllFilesForContentId(child, contentFiles);
                    }
                }
            }
        }
    }



    private void deleteAllFilesForContentId(String contentId, File[] contentFiles) {
        if(contentFiles!= null && contentFiles.length > 0){
            for (File file : contentFiles) {
                if (file.getName().startsWith(contentId)) {
//                    Log.d("delete_files", file.getName());
                    file.delete();
                }
            }
        }

    }
}
