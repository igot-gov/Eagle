package com.infosysit.sdk.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.DecryptionCallback;
import com.infosysit.sdk.UtilityJava;

/**
 * Created by akansha.goyal on 4/6/2018.
 */

public class DecryptionTask extends AsyncTask<String, String, String> {

    private DecryptionCallback mCallback;
    private Context mContext;

    public DecryptionTask(Context context) {
        mContext = context;
    }

    public DecryptionTask(Context context, DecryptionCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (!Constants.decryptedFiles.contains(strings[0])){
            Constants.decryptedFiles.add(strings[0]);
        }
        try {
            Log.d("DecryptionTask",strings[0]);
            String mimeType = Constants.contentMeta.get("mimeType").getAsString();
            if(!Constants.contentMeta.get("mimeType").getAsString().equalsIgnoreCase("video/x-youtube")){
                Log.d("MimeType", "mimetype " + mimeType + " " + Constants.MIME_TO_EXTENSION.get(mimeType));


                EncryptionDecryption.decryptDataOpenRap(
                        UtilityJava.getUniqueNumber(mContext),
                        Constants.DATA_DIR_PATH,
                        strings[0].split("\\.")[0] + ".lex",
                        Constants.TMP_DIR_PATH,
                        strings[0],mContext);
            }

        } catch (Exception e) {
            Log.d("DecryptionTask", e.getMessage());
            e.printStackTrace();
        }
        return "successfully decrypted";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("DecryptionTask","on post execute ");
        if (mCallback != null) {
            Log.d("DecryptionTask","Callback called");
            mCallback.onDecryptDone();
        }
    }
}