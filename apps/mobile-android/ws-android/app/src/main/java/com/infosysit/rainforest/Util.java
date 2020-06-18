package com.infosysit.rainforest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.UtilityJava;
import com.infosysit.sdk.services.ApiInterfaceJava;
import com.infosysit.sdk.services.InstallAPK;
import com.infosysit.sdk.services.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.infosysit.sdk.Constants.PATH_PARAM;
import static com.infosysit.sdk.Constants.baseUrl;
import static com.infosysit.sdk.Constants.connectivityMessage;
import static com.infosysit.sdk.UtilityJava.isOnline;

/**
 * Created by akansha.goyal on 4/19/2018.
 */

public class Util {


    public static boolean checkWriteExternalPermission(Context context)
    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }




    public static void navigateToPage(final Context context, final String url) {
        if (isOnline(context)) {
            Util.navigationWebView(context, url);
        } else {
            Toast.makeText(context, connectivityMessage, Toast.LENGTH_SHORT).show();
        }
    }
    public static void navigationWebView(Context context, String navigateTo) {
        Intent homeActivity = new Intent(context, HomeActivity.class);
        homeActivity.putExtra(PATH_PARAM,navigateTo);
        homeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        context.startActivity(homeActivity);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkPermission(final Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  ) {


            if (ActivityCompat.shouldShowRequestPermissionRationale
                    ((Activity)context, Manifest.permission.WRITE_EXTERNAL_STORAGE )) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Permission denied");
                builder.setMessage(R.string.permissionDenied);

                builder.setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity) context).requestPermissions(
                                new String[]{Manifest.permission
                                        .WRITE_EXTERNAL_STORAGE},
                                Constants.PERMISSION_REQUEST_CODE);
                    }
                });

                builder.setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ((Activity)context).requestPermissions(
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE},
                        Constants.PERMISSION_REQUEST_CODE);
            }
        } else {
            // write your logic code if permission already granted
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void getUserData (){
        String apiUrl = baseUrl;
        ApiInterfaceJava apiInterface = RetrofitSingleton.getInstance(apiUrl);
        Call<JsonObject> response = apiInterface.getUserDetails(UtilityJava.tokenValue);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200 && response.body() != null) {
                    UtilityJava.widValue = response.body().get("user").getAsJsonObject().get("wid").getAsString();
                    Log.d("USERDATA", UtilityJava.widValue);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("USERDATA: ","ERROR: "+t.getMessage());
            }
        });
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void openApp(Context context, String appPackageName) {
        if (context == null) {
            Log.e("UtilClass","Context is null");
            return;
        }
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appPackageName);
        if (intent != null) {
            ApplicationInfo applicationInfo = null;
            try {
//                String apkName = "app-dev28.apk";
//                String fullPath = Environment.getExternalStorageDirectory() + "/Download/" + apkName;
                applicationInfo = pm.getApplicationInfo(appPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo != null) {
//                Log.d("UtilClass","SDK VERION applicationInfo exists");
//                Log.d("UtilClass","SDK VERION "+applicationInfo.targetSdkVersion);
//                Log.d("UtilClass","MIN VERION "+applicationInfo.minSdkVersion);
            }
            //Update Apk if possible

            //Play the APK
//            context.startActivity(intent);

        }else{
            // Download Apk
            InstallAPK downloadAndInstall = new InstallAPK(context);
            downloadAndInstall.execute();
        }

            //Open file and install it

            //Play the APK
//            File toInstallApk = new File(Environment.getExternalStorageDirectory(),"/Download/app-dev28.apk");
//            if(toInstallApk.exists()){
//                Log.d("UtilClass","it exists");
//
//                final PackageManager pms = context.getPackageManager();
//                String apkName = "app-dev28.apk";
//                String fullPath = Environment.getExternalStorageDirectory() + "/Download/" + apkName;
//                PackageInfo info = pms.getPackageArchiveInfo(fullPath, 0);
//                Log.d("UtilClass","PackageName "+info.packageName+"PackageVersion "+info.versionName);



//
//                Intent promptInstall = new Intent(android.content.Intent.ACTION_VIEW);
//                promptInstall.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/" + "app-dev28.apk")), "application/vnd.android.package-archive");
//
//                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                promptInstall.setDataAndType(Uri.parse(toInstallApk.getAbsolutePath()), "application/vnd.android.package-archive" );
//                context.startActivity(promptInstall);
//            }

//            else
//                Log.d("UtilClass","not exists");
////            File path = new File(Environment.getExternalStorageDirectory(),"");
//
//            Log.e("UtilClass", "Cannot start app, appPackageName:'" + appPackageName + "'");
//        }
    }






//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//                    if(readExternalFile)
//                    {
//                        // write your logic here
//                    } else {
//
////                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
////                        builder.setTitle("Permission denied");
////                        builder.setMessage("Without this permission the app is unable to download the content. Are you sure " +
////                                "you want to deny this permission?");
////
////                        builder.setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {
////
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.dismiss();
////                            }
////                        });
////
////                        builder.setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
////
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                            dialog.dismiss();
////                            }
////                        });
////
////                        AlertDialog alert = builder.create();
////                        alert.show();
//
//                        Snackbar.make(HomeActivity.this.findViewById(android.R.id.content),
//                                "File Access required for Downloading the content.",
//                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
//                                new View.OnClickListener() {
//                                    @RequiresApi(api = Build.VERSION_CODES.M)
//                                    @Override
//                                    public void onClick(View v) {
//                                        requestPermissions(
//                                                new String[]{Manifest.permission
//                                                        .WRITE_EXTERNAL_STORAGE},
//                                                PERMISSION_REQUEST_CODE);
//                                    }
//                                }).show();
//                    }
//                }
//                break;
//        }
    }

//    public static boolean isHomeAppRunning(final Context context) {
//        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        final List<ActivityManager.RunningTaskInfo> procInfos = activityManager.getRunningTasks(10);
//        if (procInfos != null)
//        {
//            String name = context.getApplicationContext().getPackageName();
//            for (int i = 0 ; i < procInfos.size() ; i++) {
//                Log.d("onBackPressedValue",procInfos.get(i).topActivity.getClassName());
//                if (procInfos.get(i).topActivity.getClassName().equals(context.getApplicationContext().getPackageName()+homeActivityName)) {
//                    Log.d("onBackPressedValue","Value is true");
//                    return true;
//                }
//            }
//        }
//        return false;
//    }



