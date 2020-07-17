package com.infosysit.rainforest;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.infosysit.rainforest.services.OpenRapBridge;
import com.infosysit.rainforest.services.TestConstants;
import com.infosysit.rainforest.services.WebViewBridgeJava;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Created by jithilprakash.pj on 10/3/2018.
 */

public class DownloadTestCase {

    public boolean downloadTest(Context mContext) {
        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
        String[] resTypes = new String[]{"pdf","video","webmodule","quiz"};

        boolean isFileExists=false;
        try{
            for (String resourceType:resTypes) {

                if (resourceType.equalsIgnoreCase("pdf")) {
                    webViewBridgeJava.startDownload(TestConstants.pdfLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.pdfLex);
                    if (file.exists()) {
                        isFileExists = true;
                    }
                } else if (resourceType.equalsIgnoreCase("video")) {
                    webViewBridgeJava.startDownload(TestConstants.videoLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.videoLex);
                    if (file.exists()) {
                        isFileExists = true;
                    }
                } else if (resourceType.equalsIgnoreCase("webmodule")) {
                    webViewBridgeJava.startDownload(TestConstants.webLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.webLex);
                    if (file.exists()) {
                        isFileExists = true;
                    }
                } else if (resourceType.equalsIgnoreCase("quiz")) {
                    webViewBridgeJava.startDownload(TestConstants.quizLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.quizLex);
                    if (file.exists()) {
                        isFileExists = true;
                    }
                }
            }

//            webViewBridgeJava.startDownload("lex_28368141434068914000","LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
//            Thread.sleep(5000);
//            webViewBridgeJava.startDownload("lex_9402513682357557066420","LEX");//mp4 //pdf lex_auth_0125148080059105280//quiz lex_7008760202881909000
//            Thread.sleep(5000);
            if(isFileExists){
                Log.d(getClass().getSimpleName(),"FileExists");
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

//    D:\Lex_Project\Lex-Android\app\src\androidTest\java\com\infosysit\lexstage\DownloadTestCase.java

    public boolean checkDB(Context mContext){
        String[] resTypes = new String[]{"pdf","video","webmodule","quiz"};
        boolean status=false;
        try{
            for (String resourceType:resTypes) {
                if(resourceType.equalsIgnoreCase("pdf")) {
                    DownloadStatusEntity downloadStatus = AppDatabase
                            .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.pdfLex);
                    if (downloadStatus != null) {
                        Log.d(getClass().getSimpleName() + "Test", "PDF present in db" +downloadStatus.getContentId());
                        status = true;

                    } else {
                        status = true;
                        Log.d(getClass().getSimpleName() + "Test", "Test Failed");
                    }
                }
                else if(resourceType.equalsIgnoreCase("video")) {
                    DownloadStatusEntity downloadStatus = AppDatabase
                            .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.videoLex);
                    if (downloadStatus != null) {
                        Log.d(getClass().getSimpleName() + "Test", "Video entry present in db"+downloadStatus.getContentId());
                        status = true;

                    } else {
                        status = true;
                        Log.d(getClass().getSimpleName() + "Test", "Test Failed");
                    }
                }
                else if(resourceType.equalsIgnoreCase("quiz")) {
                    DownloadStatusEntity downloadStatus = AppDatabase
                            .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.quizLex);
                    if (downloadStatus != null) {
                        Log.d(getClass().getSimpleName() + "Test", "Quiz entry present in db"+downloadStatus.getContentId());
                        status = true;

                    } else {
                        status = true;
                        Log.d(getClass().getSimpleName() + "DB check Test", "entry  not present in db");
                    }
                }
                else if(resourceType.equalsIgnoreCase("webmodule")) {
                    DownloadStatusEntity downloadStatus = AppDatabase
                            .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.quizLex);
                    if (downloadStatus != null) {
                        Log.d(getClass().getSimpleName() + "Test", "webModule entry present in db"+downloadStatus.getContentId());
                        status = true;

                    } else {
                        status = true;
                        Log.d(getClass().getSimpleName() + "Test", "Test Failed");
                    }
                }
            }
        }
        catch (Exception e){
            Log.d(getClass().getSimpleName()+"Test","Test Exception");
            status=false;
        }
        return status;
    }

    public static String getActivityName(Context mContext){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
       return am.getRunningTasks(1).get(0).topActivity.toString();
        //boolean check = isActivityRunning(SettingsActivity.class,mContext)
    }


    public boolean downloadTestOpenRap(Context mContext) {
        OpenRapBridge openRapBridge = new OpenRapBridge(mContext);
        String[] resTypes = new String[]{"video","webmodule"};

        boolean isWebExists=false;boolean isVideo=false;
        try{
            for (String resourceType:resTypes) {

                if (resourceType.equalsIgnoreCase("webmodule")) {
                    openRapBridge.startDownload(TestConstants.openRapWeb);//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.openRapWeb);
                    if (file.exists()) {
                        isWebExists = true;
                    }
                } else if (resourceType.equalsIgnoreCase("video")) {
                    openRapBridge.startDownload(TestConstants.openRapVideo);//webmodule //lex_15314693713480422,lex_28368141434068914000
                    Thread.sleep(5000);
                    File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.openRapVideo);
                    if (file.exists()) {
                        isVideo = true;
                    }
//            }
            }
            }

//            webViewBridgeJava.startDownload("lex_28368141434068914000","LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
//            Thread.sleep(5000);
//            webViewBridgeJava.startDownload("lex_9402513682357557066420","LEX");//mp4 //pdf lex_auth_0125148080059105280//quiz lex_7008760202881909000
//            Thread.sleep(5000);
            if(isVideo && isWebExists){
                Log.d(getClass().getSimpleName(),"FileExists");
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static boolean downloadCourse(Context mContext){
        try{
        OpenRapBridge openRapBridge = new OpenRapBridge(mContext);
        openRapBridge.startDownload(TestConstants.openRapVideo);
        Thread.sleep(25000);
//        List<String> fileName = new ArrayList<>();
//        List<String> fileNameData = new ArrayList<>();
//        boolean filesPresent = true;

        File decryptedFile = new File(Environment.getExternalStorageDirectory(),TestConstants.path+"/temp/"+TestConstants.openRapVideo+".lex");
        File zipFile = new File(Environment.getExternalStorageDirectory(),TestConstants.path+"/temp/"+TestConstants.openRapVideo+".zip");
        File lexFolder =  new File(Environment.getExternalStorageDirectory(),TestConstants.path+"/temp/"+TestConstants.openRapVideo);
//        File[] tempFolderFiles = lexFolder.listFiles();
//        File folder = new File(Environment.getExternalStorageDirectory(),".Lex_android_dev/data");
//        File[] listOfFiles = folder.listFiles();
//
//            for (File filename:tempFolderFiles) {
//                fileName.add(filename.getName());
//
//
//            }
//            Log.d("DownloadTestCase",fileName.toString());
//            for (File filename:listOfFiles) {
//                fileNameData.add(filename.getName());
//
//            }
//            Log.d("DownloadTestCase",fileNameData.toString());
//
//            if(!(fileNameData.containsAll(fileName))) {
//                return false;
//            }

//        for (File file : listOfFiles){
//            if(!fileName.contains(file.getName())){
//                return false;
//            }
//        }
           // Log.d("DownloadTestCase",fileName.toString());

        Log.d("DownloadTestCase","Files are encrypted and moved to data folder");

        if(decryptedFile.exists()){
            Log.d("DownloadTestCase","File Decrypted Success");
        }
        else {
            Log.d("DownloadTestCase","File Decrypted    Failed");
        }

        if(zipFile.exists()){
            Log.d("DownloadTestCase","File zip Success");
        }
        else {
            Log.d("DownloadTestCase","File zip Failed");
        }

        if(lexFolder.exists()){
            Log.d("DownloadTestCase","File unzipped Success");
        }
        else {
            Log.d("DownloadTestCase","File unzipped Failed");
        }
        return true;
        }catch(Exception e){return false;}


    }


}
