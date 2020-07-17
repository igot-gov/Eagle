package com.infosysit.rainforest.services;

import android.util.Log;

import com.infosysit.sdk.UtilityJava;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by jithilprakash.pj on 10/8/2018.
 */
public class UtilityJavaTest {



    @Test
    public void getExtension_test1() throws Exception {
        boolean status = false;
        String extension = UtilityJava.getExtension("website-address:port/static-ilp/lex_5043407731031547000/lex_19036588158688145000/lex_13544714593934324000/quiz.json");
        Log.d("ArtificatUrl",extension);
        if(extension.length()>1){
            status = true;
        }
        assertTrue(status);

    }

    @Test
    public void getExtension_test2() throws Exception {
        boolean status = false;
        String extension = UtilityJava.getExtension("website-address:port/static-ilp/lex_5043407731031547000/lex_19036588158688145000/lex_13544714593934324000/quiz");
        Log.d("ArtificatUrl",extension);
        if(extension.length()>1){
            status = true;
        }
        assertTrue(status);

    }





//
//    @Test
//    public void showPermissionScreen() throws Exception {
//    }
//

//
//    @Test
//    public void unzipFile() throws Exception {
//    }
//

//
//    @Test
//    public void zipFileAtPath() throws Exception {
//    }
//
//    @Test
//    public void getStringFromFile() throws Exception {
//    }
//
//    @Test
//    public void getJsonFromFile() throws Exception {
//    }
//
//    @Test
//    public void showProgress() throws Exception {
//    }
//
//    @Test
//    public void hideProgress() throws Exception {
//    }
//
//    @Test
//    public void getStatus() throws Exception {
//        mContext = mActivityRule.getActivity().getBaseContext();
//        Thread.sleep(15000);
//        boolean status;
//        try{
//
//        DownloadStatusEntity downloadStatusEntity = AppDatabase.getDb(mContext).downloadStatusDao().getDownloadStatusByContentId(TestConstants.pdfLex);
//        if(downloadStatusEntity!=null) {
//            int downloadStatus = UtilityJava.getStatus(mContext, downloadStatusEntity.getDownloadId());
//            Log.d(getClass().getSimpleName(), downloadStatusEntity.getDownloadId() + " " + downloadStatus);
//            status = true;
//        }
//        else{
//            Log.d(getClass().getSimpleName(),"No data for test");
//            status=true;
//        }
//        }catch(Exception e){
//            status = false;
//        }
//        assertTrue(status);
//
//
//    }




}