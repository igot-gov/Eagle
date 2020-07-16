package com.infosysit.rainforest.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

public class GetVersionCode extends AsyncTask<Void, String, String> {
    private Context context;

    public GetVersionCode(Context context) { this.context = context; }

    @Override
    protected String doInBackground(Void... voids) {

        String newVersion = null;

        try {
            Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName()  + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://www.google.com")
                    .get();
            if (document != null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;

    }


    @Override

    protected void onPostExecute(String onlineVersion) {

        super.onPostExecute(onlineVersion);
        String currentVersion = null;

        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            try{
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                currentVersion = pInfo.versionName;
            } catch (Exception ex){

            }

//            if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion)) {
//                //show anything
//            }

        }

        Log.d("VERSION_UPDATE", "Current version " + currentVersion + "playstore version " + onlineVersion);

    }
}
