package com.infosysit.sdk.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InstallAPK extends AsyncTask<String,Void,Void> {

    private Context contextRef;
    private int status = 0;

    public InstallAPK(Context context) {
        this.contextRef = context;
    }



    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL("https://apkpure.com/google-news/com.google.android.apps.magazines/download?from=home%2Feditors_picks");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            File sdcard = contextRef.getExternalFilesDir("");
            File myDir = new File(sdcard,"/akansha/temp");
            myDir.mkdirs();
            File outputFile = new File(myDir, "temp.apk");
//            if(outputFile.exists()){
//                outputFile.delete();
//            }
//            FileOutputStream fos = new FileOutputStream(outputFile);
//
//            InputStream is = c.getInputStream();
//
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            while ((len1 = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, len1);
//            }
//            fos.flush();
//            fos.close();
//            is.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(sdcard,"/Download/try.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            contextRef.startActivity(intent);


        } catch (FileNotFoundException fnfe) {
            status = 1;
            Log.e("File", "FileNotFoundException! " + fnfe);
        }

        catch(Exception e)
        {
            Log.e("UpdateAPP", "Exception " + e);
        }
        return null;
    }

    public void onPostExecute(Void unused) {
        if(status == 1)
            Toast.makeText(contextRef,"Game Not Available",Toast.LENGTH_LONG).show();
    }
}

