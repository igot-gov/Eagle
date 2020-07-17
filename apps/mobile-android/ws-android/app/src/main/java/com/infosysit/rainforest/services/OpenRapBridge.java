package com.infosysit.rainforest.services;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.infosysit.sdk.services.DownloadContentService;

import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_MODE;
import static com.infosysit.sdk.Constants.EXTRA_DOWNLOAD_RES_ID;

public class OpenRapBridge {

    private Context mContext;


    public OpenRapBridge(Context context) {
        mContext = context;
    }

    public void startDownload(String contentId) {
        Intent intent = new Intent(mContext, DownloadContentService.class);
        intent.putExtra(EXTRA_DOWNLOAD_MODE, "OPENRAP");
        intent.putExtra(EXTRA_DOWNLOAD_RES_ID, contentId);
        mContext.startService(intent);
    }

    @JavascriptInterface
    public void DOWNLOAD_REQUESTED_OPENRAP (String data){
        startDownload(data.replaceAll("\"",""));
        Toast.makeText(mContext,"Download Initiated. Please switch to Downloads page for playing the content.",Toast.LENGTH_LONG).show();
    }
}
