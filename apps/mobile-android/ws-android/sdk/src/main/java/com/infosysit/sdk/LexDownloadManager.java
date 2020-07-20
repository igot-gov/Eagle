package com.infosysit.sdk;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sujeet_Jaiswal on 4/13/2018.
 */

public class LexDownloadManager {

    private static LexDownloadManager sLexDownloadManager;
    private DownloadManager mDownloadManager;
    private Context mContext;

    private LexDownloadManager(Context context) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mContext = context;
    }

    public static LexDownloadManager getInstance(Context context) {
        if (sLexDownloadManager == null) {
            sLexDownloadManager = new LexDownloadManager(context);
        }

        return sLexDownloadManager;
    }

    public void remove(long... downloadIds) {
        mDownloadManager.remove(downloadIds);
    }

    // openrap change

    public long enqueue(String url, String filePath, String fileName, boolean showNotification) {
        DownloadStatusEntity downloadStatus = AppDatabase.getDb(mContext)
                .downloadStatusDao().getDownloadStatusByContentId(fileName.split("\\.")[0]);
        if (downloadStatus != null) {
            String status = downloadStatus.getDownloadStatus();
            if (status.equals("INITIATED") || status.equals("DOWNLOADED") || status.equals("READY")) {
                return downloadStatus.getDownloadId();
            }
        }


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.addRequestHeader("authorization", UtilityJava.tokenValue);
        if (!showNotification){
            request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_HIDDEN);
        } else {
            request.setTitle("Lex Download");
        }
        Log.d("RequestMade","Reached here");
        Log.d("RequestMade",url);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Log.d("SQLiteHelperJava",String.valueOf(prefs.getBoolean("Wi-Fi",true)));
        if(prefs.getBoolean("Wi-Fi", true)){
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }

//        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalFilesDir(mContext,filePath,fileName);
//        request.setDestinationInExternalPublicDir(filePath, fileName);
        return mDownloadManager.enqueue(request);
    }



    public List<Integer> checkDownloadStatus(long[] downloadIds) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadIds);
        Cursor c = mDownloadManager.query(query);

        List<Integer> downloadStatuses = new ArrayList<>();
        while (c.moveToNext()) {
            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
            downloadStatuses.add(c.getInt(columnIndex));
        }

        return downloadStatuses;
    }

}
