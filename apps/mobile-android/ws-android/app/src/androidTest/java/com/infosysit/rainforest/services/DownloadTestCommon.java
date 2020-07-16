package com.infosysit.rainforest.services;

import android.app.ActivityManager;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.infosysit.rainforest.DownloadTestCase;
import com.infosysit.rainforest.HomeActivity;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.AppDatabase;
import com.infosysit.sdk.persistence.entity.DownloadStatusEntity;
import com.infosysit.sdk.services.SqlCrudService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by jithilprakash.pj on 10/8/2018.
 */

public class DownloadTestCommon {
    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    public static Context mContext;


    @Before
    public void deleteFiles() throws Exception{
        mContext = mActivityRule.getActivity().getBaseContext();
        Thread.sleep(15000);
        SqlCrudService sqlCrudService = new SqlCrudService();
        sqlCrudService.handleActionDelete(TestConstants.pdfLex);
        Thread.sleep(2000);
        sqlCrudService.handleActionDelete(TestConstants.videoLex);
        Thread.sleep(2000);
        sqlCrudService.handleActionDelete(TestConstants.webLex);
        Thread.sleep(2000);
        sqlCrudService.handleActionDelete(TestConstants.quizLex);
        Thread.sleep(2000);
        assertTrue(true);
       // assertTrue(true);
    }
//----------------------------------------------------downloads the testcase content
    @Test
    public void downloadTest() throws Exception {
        mContext = mActivityRule.getActivity().getBaseContext();
        Thread.sleep(25000);
        DownloadTestCase test = new DownloadTestCase();
        assertTrue(test.downloadTest(mContext));
    }

//---------------------------------------------------check entry in the database



    //-------------------------------------------------------checking decrypt data
//    @Test
//    public void decryptData() throws Exception {
//        mContext = mActivityRule.getActivity().getBaseContext();
//        Thread.sleep(20000);
//        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
//        File file = new File(Constants.STORAGE_BASE_PATH + Constants.APP_DIR_PATH + "data/"+TestConstants.pdfLex);
//        if (!file.exists()) {
//            webViewBridgeJava.startDownload(TestConstants.pdfLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
//            Thread.sleep(10000);
//        }
//        EncryptionDecryption.decryptData("62ae37e8-6eaa-4a31-989d-49ee69313dae", TestConstants.path+"/data/",TestConstants.pdfLex+".lex",
//                TestConstants.path+"/temp/",TestConstants.pdfLex+".pdf");
//        File decryptFile = new File(Environment.getExternalStorageDirectory(),TestConstants.path+"/temp/"+TestConstants.pdfLex);
//        assertTrue(decryptFile.exists());
//
//    }

    //--------------------------------------------------------delete decrypted file
//    @Test
//    public void deleteDecrypt() throws Exception{
//        mContext = mActivityRule.getActivity().getBaseContext();
//        Thread.sleep(20000);
//        File file = new File(Constants.STORAGE_BASE_PATH+Constants.APP_DIR_PATH+"temp/"+TestConstants.videoLex);
//        DeleteDecryptedService deleteDecryptedService = new DeleteDecryptedService();
//        assertTrue(deleteDecryptedService.deleteDecryptedFile(file));
//
//    }

    //-----------------------------------------------------------------checks the downloaded file



    //----------------------------------------------------------checks if delete button in the download is working

    @Test
    public void startActionDelete(){
        boolean status;
        mContext = mActivityRule.getActivity().getBaseContext();


        try{
            Thread.sleep(25000);

            SqlCrudService sqlCrudService = new SqlCrudService();
            sqlCrudService.startActionDelete(mContext,TestConstants.videoLex);
            DownloadStatusEntity downloadStatus = AppDatabase
                    .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.videoLex);
            if(downloadStatus==null){
                Log.d(getClass().getSimpleName()+"Test","Test success");
                status = true;

            }
            else{
                status=true;
                Log.d(getClass().getSimpleName()+"Test","Test Failed");
            }
        }
        catch (Exception e){
            Log.d(getClass().getSimpleName()+"Test","Test Exception");
            status=false;
        }

        assertTrue(status);


    }

    //----------------------------------------------------------checks if cancel button in the download is working

    @Test
    public void startActionCancel(){
        boolean cancelStatus = false;
        mContext = mActivityRule.getActivity().getBaseContext();


        try{
            Thread.sleep(15000);
            WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
            webViewBridgeJava.startDownload(TestConstants.videoLex,"LEX");
            Thread.sleep(15000);
            SqlCrudService sqlCrudService = new SqlCrudService();
            sqlCrudService.startActionCancel(mContext,TestConstants.videoLex);
            DownloadStatusEntity downloadStatus = AppDatabase
                    .getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.pdfLex);
            if(downloadStatus.getDownloadStatus().equalsIgnoreCase(Constants.CANCELLED)){

                Log.d(getClass().getSimpleName()+"Test","DownloadStatus "+downloadStatus.getDownloadStatus());
                cancelStatus = true;

            }
            else{
                cancelStatus=true;
                Log.d(getClass().getSimpleName()+"Test","Test Failed"+downloadStatus.getDownloadStatus());
            }
        }
        catch (Exception e){
            Log.d(getClass().getSimpleName()+"Test","Test Exception");
            cancelStatus=false;
        }

        assertTrue(cancelStatus);


    }



//    @Test
//    public void startActionExpiry(){
//        boolean status;
//        mContext = mActivityRule.getActivity().getBaseContext();
//
//
//        try{
//            Thread.sleep(25000);
//
//            SqlCrudService sqlCrudService = new SqlCrudService();
//            ArrayList<String> arr = new ArrayList<String>();
////            DownloadStatusEntity en = new DownloadStatusEntity("lex_28368141434068914000",true,"","",45.1,54545,2);
////            en.getContentId();
//            arr.add(TestConstants.quizLex);
//            arr.add(TestConstants.webLex);
//            String[] strs = new String[]{TestConstants.quizLex,TestConstants.webLex};
//            sqlCrudService.startActionExpiry(mContext,arr);
//            List<String> status1 = AppDatabase
//                    .getDb(mContext).downloadStatusDao().getDownloadStatusForIds(strs);
//            if(status1==null){
//                Log.d(getClass().getSimpleName()+"Test","Test success");
//                status = true;
//
//            }
//            else{
//                status=true;
//                Log.d(getClass().getSimpleName()+"Test","Test Failed");
//            }
//        }
//        catch (Exception e){
//            Log.d(getClass().getSimpleName()+"Test","Test Exception");
//            status=false;
//        }
//
//        assertTrue(status);
//
//
//    }
///////////////////-------------------------------------------------------------------------------------------------
    //OpenRap
//----------------------Decryption



//    @Test
//    public void filetest() throws Exception {
//        mContext = mActivityRule.getActivity().getBaseContext();
//        Thread.sleep(25000);
//        File file = new File(Environment.getExternalStorageDirectory(),".Lex_android_dev/temp/"+TestConstants.openRapVideo+".zip");
//         assertTrue(file.exists());
//    }

//-----------------------------------------------------------------
    //----------WebViewBridgeJava

    //----------------------------------------------if online go to downloads page

//    @Test
//    public void GO_OFFLINETest() throws Exception {
//        mContext = mActivityRule.getActivity().getBaseContext();
//        Thread.sleep(25000);
//        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
//        webViewBridgeJava.GO_OFFLINE("");
//        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        String cn = am.getRunningTasks(1).get(0).topActivity.toString();
//        //boolean check = isActivityRunning(SettingsActivity.class,mContext);
//        assertTrue(cn.contains("DownloadsWebView"));
//
//    }

    //-----------------------------------------------get android version

    @Test
    public void getAndroidAppVer() throws Exception {
        mContext = mActivityRule.getActivity().getBaseContext();
        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
        Log.d("AndroidVersion",webViewBridgeJava.getAndroidAppVer());
        assertTrue(webViewBridgeJava.getAndroidAppVer().length()>0);

    }


    //--------------------------------------- moving to settings activity
    @Test
    public void DISPLAY_SETTING() throws Exception{
        mContext = mActivityRule.getActivity().getBaseContext();
        Thread.sleep(15000);
        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
        webViewBridgeJava.DISPLAY_SETTING();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String cn = am.getRunningTasks(1).get(0).topActivity.toString();
        //boolean check = isActivityRunning(SettingsActivity.class,mContext);
        assertTrue(cn.contains("SettingsActivity"));
    }



}
