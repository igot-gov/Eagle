package com.infosysit.rainforest.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.infosysit.rainforest.HomeActivity;

import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.infosysit.sdk.services.SqlCrudService;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static com.infosysit.sdk.Constants.continueLearningTelemetry;
import static com.infosysit.sdk.Constants.continueLearningTelemetryJson;
import static com.infosysit.sdk.Constants.telemetryTokenKey;
import static org.junit.Assert.assertTrue;

/**
 * Created by jithilprakash.pj on 10/16/2018.
 */

public class TestCases3 {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    public static Context mContext;
    public static WifiManager wifiManager;
    public static  String continueLearning="";
    public static  String continueLearningJson="";





    @Test
    public void storetelemetry() throws Exception{
        mContext = mActivityRule.getActivity().getApplicationContext();
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Thread.sleep(15000);

        DownloadsBridge downloadsBridge = new DownloadsBridge(mContext);
        downloadsBridge.NAVIGATION_DATA_OUTGOING("{\"url\":\"/viewer/"+TestConstants.pdfLex+"\"}");
        Thread.sleep(60000);

        continueLearning = SharedPrefrence.getItem(mContext, continueLearningTelemetry,"");
        continueLearningJson = SharedPrefrence.getItem(mContext, continueLearningTelemetryJson,"");
        String telemetryToken = SharedPrefrence.getItem(mContext, telemetryTokenKey,"");
        Log.d("TestCases3 before",continueLearning);
        Log.d("TestCases3 before",continueLearningJson);
        wifiManager.setWifiEnabled(true);
        assertTrue(continueLearning.length()>0 && continueLearningJson.length()>0);

    }


    @Test
    public void testTelemetryLearningService() throws Exception{

        mContext = mActivityRule.getActivity().getApplicationContext();
        Thread.sleep(30000);
//        SharedPrefrence.setItem(mContext,continueLearningTelemetry,"[{\"contextPathId\":\"lex_9402513682357557066420\",\"resourceId\":\"lex_9402513682357557066420\",\"data\":null,\"percentComplete\":0}]");
        continueLearning = SharedPrefrence.getItem(mContext, continueLearningTelemetry,"");
        continueLearningJson = SharedPrefrence.getItem(mContext, continueLearningTelemetryJson,"");
        String telemetryToken = SharedPrefrence.getItem(mContext, telemetryTokenKey,"");
        Log.d("TestCases3","online "+SharedPrefrence.getItem(mContext, continueLearningTelemetry,""));
        Log.d("TestCases3","online "+ SharedPrefrence.getItem(mContext, continueLearningTelemetryJson,""));
        Log.d("TestCases3","online "+ SharedPrefrence.getItem(mContext, telemetryTokenKey,""));
        assertTrue(continueLearning.equalsIgnoreCase("") && continueLearningJson.equalsIgnoreCase("[]") && telemetryToken.equalsIgnoreCase(""));

    }


    //----------------------------------downloads the webmodule and check if download telemetry is happening
    //----------------------------------data should be on
    @Test
    public void downloadTelemetry()throws Exception{
        mContext = mActivityRule.getActivity().getApplicationContext();
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Thread.sleep(15000);
        SqlCrudService sqlCrudService = new SqlCrudService();
        sqlCrudService.handleActionDelete(TestConstants.webLex);
        Thread.sleep(2000);
        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);
        webViewBridgeJava.startDownload(TestConstants.webLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
        Thread.sleep(10000);
        String ApiCall = SharedPrefrence.getItem(mContext,"ApiCall","");
        SharedPrefrence.setItem(mContext,"ApiCall","");
        wifiManager.setWifiEnabled(true);
        assertTrue(ApiCall.length()>0);

    }


    //-------------------------------data will get on and download start if file not present
    //-------------------------------plays the resource offline
    //------------------------------- check if sharedpreference has some value

    @Test
    public void telemetryPlayerData()throws Exception{
        mContext = mActivityRule.getActivity().getBaseContext();
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        Thread.sleep(20000);
        DownloadsBridge downloadsBridge = new DownloadsBridge(mContext);
        WebViewBridgeJava webViewBridgeJava = new WebViewBridgeJava(mContext);

        File file = new File(Environment.getExternalStorageDirectory(),TestConstants.path+"/data/"+TestConstants.quizLex+".lex");
        if(!file.exists()){
            webViewBridgeJava.startDownload(TestConstants.quizLex, "LEX");//webmodule //lex_15314693713480422,lex_28368141434068914000
            Thread.sleep(15000);
            wifiManager.setWifiEnabled(true);

        }

        downloadsBridge.NAVIGATION_DATA_OUTGOING("{\"url\":\"/viewer/"+TestConstants.quizLex+"\"}");
        Thread.sleep(60000);
        String tokenKey = SharedPrefrence.getItem(mContext,telemetryTokenKey,"");
        Log.d("telemetryPlayerData","Test "+tokenKey);
        assertTrue(tokenKey.length()>0);
    }


    //------------------after the above method gets executed, this has to call
    //------------------ when app goes online, it makes api call
    @Test
    public void telemetryPlayerApiCall()throws Exception {
        mContext = mActivityRule.getActivity().getBaseContext();
        Thread.sleep(30000);
        wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        String tokenKey = SharedPrefrence.getItem(mContext,telemetryTokenKey,"");
        Log.d("telemetryPlayerData","Test "+tokenKey);
        assertTrue(tokenKey.length()==0);



    }

    //----- Please submit a quiz before this method

    @Test
    public void quizSubmitTelemetry()throws Exception{
            mContext = mActivityRule.getActivity().getBaseContext();
            Thread.sleep(15000);
            String apiCall = SharedPrefrence.getItem(mContext,"ApiCall","");
            String quizCall = SharedPrefrence.getItem(mContext, Constants.quizSubmitTelemetry ,"");
            SharedPrefrence.setItem(mContext,"ApiCall","");
            Log.d("quizSubmitTelemetry",apiCall);
            assertTrue(apiCall.length()>0 && quizCall.length()<1);
    }


    //---------------------------------data should be on. Playing a courseera resource and checking if apiCall is happening
    //---------------------------------Before running this method, play a non-iframe supported resource eg:neural networks and deep learning
    @Test
    public void viewHistoryCount()throws Exception{
        mContext = mActivityRule.getActivity().getBaseContext();
        Thread.sleep(15000);
        String apiCall = SharedPrefrence.getItem(mContext,"ApiCall","");
        Log.d("TestCases3",apiCall);
        SharedPrefrence.setItem(mContext,"ApiCall","");
        assertTrue(apiCall.length()>0);



    }

    }


