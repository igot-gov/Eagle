package com.infosysit.sdk.services;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.ContentEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akansha.goyal on 5/13/2018.
 */

public class DeleteTask extends AsyncTask<String, String, String> {
    private Context mContext;

    public DeleteTask(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        List<ContentEntity> contentDownloaded = null;
        try {
            contentDownloaded = AppDatabase.getDb(mContext).contentDao().getAllDownloadedContents(Constants.DOWNLOADED);
        } catch (Exception e) {
            Log.d("Exception","Message: "+e.getMessage());
        }
        final ArrayList<String> contentIdDelete = new ArrayList<>();
        for (ContentEntity content: contentDownloaded) {
            if(content.getExpiryDate() == null){
                Log.d("getExpiryDate","date"+content.getExpiryDate());
                long nowInMillis = System.currentTimeMillis();
                String expiryDate = String.valueOf(nowInMillis + Constants.expiry);
                content.setExpiryDate(expiryDate);
                content.setRequestedDate(String.valueOf(System.currentTimeMillis()));
                try {
                    AppDatabase.getDb(mContext).contentDao().updateContent(content);
                } catch (Exception e) {
                    Log.d("Exception","Message: "+e.getMessage());
                }
            }
            else{
                long expiryDate = Long.parseLong(content.getExpiryDate(), 10);
                // need to make change here
                if(expiryDate <= System.currentTimeMillis() && content.getParentId() == null){
                    contentIdDelete.add(content.getContentId());
                }
            }

        }
        if(contentIdDelete.size() > 0){
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Downloaded Resources");
                    builder.setMessage("Some of the content will be deleted as they have expired.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SqlCrudService.startActionExpiry(mContext,contentIdDelete);
                        }
                    });


                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });


        }
        return null;
    }
}
