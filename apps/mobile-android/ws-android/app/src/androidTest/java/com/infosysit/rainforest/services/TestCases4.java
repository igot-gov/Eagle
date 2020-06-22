package com.infosysit.rainforest.services;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.infosysit.rainforest.HomeActivity;
import com.infosysit.sdk.persistence.SharedPrefrence;

import org.junit.Rule;
import org.junit.Test;

import static com.infosysit.sdk.Constants.continueLearningTelemetry;
import static com.infosysit.sdk.Constants.continueLearningTelemetryJson;
import static com.infosysit.sdk.Constants.telemetryTokenKey;
import static org.junit.Assert.assertTrue;

/**
 * Created by jithilprakash.pj on 10/17/2018.
 */

public class TestCases4 {


    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    public static Context mContext;
    public static WifiManager wifiManager;
    public static  String continueLearning="";
    public static  String continueLearningJson="";


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
}
