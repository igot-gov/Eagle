package com.infosysit.sdk;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infosysit.sdk.persistence.SharedPrefrence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by akansha.goyal on 3/12/2018.
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 Version: 01.01

 //Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”), this Program is protected by copyright laws, //international treaties and other pending or existing intellectual property rights in India, the United States and other countries. Except as expressly permitted, any unauthorized //reproduction, storage, transmission in any form or by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any //distribution of this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible under the law.

 */

public class UtilityJava {



    public static Boolean isOnline(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =  connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String tokenValue = null;
    public static String widValue = null;



    public static String getExtension(String urlOfArtifact){
        String extension = null;
        String[] pathSPlit = urlOfArtifact.split("/");
        String filenameWithExte = pathSPlit[pathSPlit.length - 1];
        int positionOfDot = filenameWithExte.indexOf(".");
        String extensions = filenameWithExte.substring(positionOfDot + 1);
        extension = extensions.split("\\?")[0];
        return extension;
    }


    public static String getUniqueNumber (Context context) {
        String userId = SharedPrefrence.getItem(context, "userId", "");

        return userId;
    }

    public static void showPermissionScreen(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
        }
    }



    public static String getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return  height + " * " + width ;
    }





    public static void unzipFile (String fileName, Context context) throws IOException {
        Log.d("openRapTesting","filePath: "+fileName);
        String [] fileNameWithex = fileName.split("\\.");
        File path = context.getExternalFilesDir("");
        File letDirectory = new File(path, Constants.TMP_DIR_PATH+fileName);
        File toSave = new File(path,Constants.TMP_DIR_PATH+fileNameWithex[0]);

        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(letDirectory)))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(toSave, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
            Log.d("openRapTesting","unzip done");
        }
    }

    public static void moveFile(String inputPath, String inputFile, String outputPath, String outputFile) {
        Log.d("movefile", inputPath);
        Log.d("movefile", inputPath);
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    /*
     *
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     */

    public static boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                for (File file: sourceFile.listFiles()) {
                    if (file.isDirectory()) {
                        zipSubFolder(out, file, file.getParent().length());
                    } else {
                        byte data[] = new byte[BUFFER];
                        FileInputStream fi = new FileInputStream(file.getAbsolutePath());
                        origin = new BufferedInputStream(fi, BUFFER);
                        ZipEntry entry = new ZipEntry(getLastPathComponent(file.getAbsolutePath()));
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                        }
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("utilityzip", e.getMessage());
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private static void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        if (fileList.length == 0) {
            Log.d("emptyfolder", folder.getName());
            File emptyFile = new File(folder.getAbsolutePath() + File.separator + ".temp");
            emptyFile.createNewFile();
            fileList = folder.listFiles();
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    private static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        return segments[segments.length - 1];
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static JsonObject getJsonFromFile (String filePath) {
        File fl = new File(filePath);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fl);
            int size = fin.available();
            byte[] buffer = new byte[size];
            fin.read(buffer);
            fin.close();
            String str = new String(buffer, "UTF-8");
            JsonParser jsonParserObj = new JsonParser();

            return jsonParserObj.parse(str).getAsJsonObject();
        } catch (IOException e) {
            Log.d("jsonfromfile", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

//    public static void unzip0penRapFile (String fileName ) throws IOException {
////        Log.d("Filename",fileName);
//        String [] fileNameWithex = fileName.split("\\.");
//        File path = Environment.getExternalStorageDirectory();
//        File letDirectory = new File(path, Constants.OPENRAP_DIR_PATH+fileName);
//        File toSave = new File(path,Constants.TMP_DIR_PATH);
//
//        ZipInputStream zis = new ZipInputStream(
//                new BufferedInputStream(new FileInputStream(letDirectory)));
//        try {
//            ZipEntry ze;
//            int count;
//            byte[] buffer = new byte[8192];
//            while ((ze = zis.getNextEntry()) != null) {
//                File file = new File(toSave, ze.getName());
//                File dir = ze.isDirectory() ? file : file.getParentFile();
//                if (!dir.isDirectory() && !dir.mkdirs())
//                    throw new FileNotFoundException("Failed to ensure directory: " +
//                            dir.getAbsolutePath());
//                if (ze.isDirectory())
//                    continue;
//                FileOutputStream fout = new FileOutputStream(file);
//                try {
//                    while ((count = zis.read(buffer)) != -1)
//                        fout.write(buffer, 0, count);
//                } finally {
//                    fout.close();
//                }
//            /* if time should be restored as well
//            long time = ze.getTime();
//            if (time > 0)
//                file.setLastModified(time);
//            */
//            }
//        } finally {
//            zis.close();
//        }
//    }

    public static void showProgress (Context context){
//        Log.d("showProgress","Loading");
        Constants.progress = new ProgressDialog(context);
        Constants.progress.setMessage("Loading..");
        Constants.progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        Constants.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Constants.progress.show();
//        Log.d("showProgress","Loading Show");
// To dismiss the dialog

    }

    public static void hideProgress(){
//        Log.d("showProgress","Loading Hide");
        Constants.progress.dismiss();
    }

    public static int getStatus(Context context , long downloadId) {
        DownloadManager downloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);// filter your download bu download Id
        Cursor c = downloadManager.query(query);
        Log.d("SQLiteHelperJava","Reached utility");
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            int value = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
            c.close();
            Log.d("SQLiteHelperJava", "Reason "+String.valueOf(value));
            Log.d("SQLiteHelperJava", "Status "+String.valueOf(status));
            return status;
        }
        Log.d("SQLiteHelperJava", "DEFAULT");
        return -1;
    }

}

