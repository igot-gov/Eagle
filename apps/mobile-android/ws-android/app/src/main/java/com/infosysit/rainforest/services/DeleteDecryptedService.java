package com.infosysit.rainforest.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;



/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DeleteDecryptedService extends IntentService {
    public DeleteDecryptedService() {
        super(DeleteDecryptedService.class.getCanonicalName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String folderToDelete = intent.getStringExtra("folderToDelete");
//            String[] decryptedFileArray = intent.getStringExtra(Constants.decryptedFileKey).split(",");
//            Log.d("Inservice",decryptedFileArray.length+"");
            File tempFolder = new File(this.getExternalFilesDir(""), folderToDelete);
            deleteDecryptedFile(tempFolder);
        }
    }

    public boolean deleteDecryptedFile(File file){
        try {
            if(file.exists()){
                File[] deleteFiles = file.listFiles();
                if (deleteFiles != null) {
                    for (File child : deleteFiles) {
                        if (child.isDirectory()) {
                            deleteDecryptedFile(child);
                        } else {
                            child.delete();
//                            Log.d("deleteDecryptedFile", "delete it " + child);
                        }
                    }
                    file.delete();
                }

            }

            Log.d("DownloadCheck","File Deleted: "+file.getAbsolutePath());


            return true;
        }

        catch (Exception ex){
//            Log.d("DeleteDecryptedFile",ex.getMessage());
            return false;
        }

    }



}
