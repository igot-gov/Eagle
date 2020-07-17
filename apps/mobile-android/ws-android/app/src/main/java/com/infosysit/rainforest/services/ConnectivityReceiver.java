package com.infosysit.rainforest.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.infosysit.rainforest.LexApplication;
import com.infosysit.rainforest.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by akansha.goyal on 3/27/2018.
 */

public class ConnectivityReceiver
        extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver(ConnectivityReceiverListener listener) {
        super();
        connectivityReceiverListener = listener;
    }

    @Override
    public void onReceive(final Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();



//        Util.checkIfInternet().enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if(response.code() ==  200){
//                    connectivityReceiverListener.connectedToInternet();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.d("TestingOpenrap","Ex: "+t.getMessage());
//                Util.checkIfOpenRap().enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        if(response.code() ==  200){
//                            connectivityReceiverListener.connectedToOpenRap();
//                        }
////                Toast.makeText(HomeActivity.this, "Openrap Api Status " + response.code(), Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        connectivityReceiverListener.offline();
//                    }
//                });
//            }
//        });
    }


    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) LexApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }



    public static boolean isWifi(){
        ConnectivityManager manager = (ConnectivityManager) LexApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting());
    }


    public interface ConnectivityReceiverListener {
        void connectedToInternet();
        void offline();
    }
}
