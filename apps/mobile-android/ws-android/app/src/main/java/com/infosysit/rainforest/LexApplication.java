package com.infosysit.rainforest;

import android.app.Application;

import com.infosysit.rainforest.services.ConnectivityReceiver;
import com.infosysit.sdk.Constants;

/**
 * Created by akansha.goyal on 3/27/2018.
 */

public class LexApplication extends Application {

    private static LexApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Constants.baseUrl= BuildConfig.BASE_URL;
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")) {
            Constants.environmentType = "eagle";
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase("prod")) {
            Constants.environmentType = "igot";
        }

    }

    public static synchronized LexApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
