package com.infosysit.rainforest;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.infosysit.rainforest.services.ConnectivityReceiver;
import com.infosysit.rainforest.services.WebViewBridgeJava;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.persistence.SharedPrefrence;
import com.scottyab.rootbeer.RootBeer;

import java.util.ArrayList;
import java.util.List;


import static com.infosysit.sdk.Constants.PATH_PARAM;
import static com.infosysit.sdk.Constants.SEARCH_PARAM;
import static com.infosysit.sdk.Constants.baseUrl;
//import static com.infosysit.sdk.Constants.webViewPageStack;
import static com.infosysit.sdk.Constants.isAuthenticated;
import static com.infosysit.sdk.UtilityJava.isOnline;

public class HomeActivity extends Activity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static WebView loginPage = null;
    public static WebView splashPage = null;
    public static FloatingActionButton chatButton = null;
    static JsonObject telemetryJSON = new JsonObject();
    static boolean showSplashScreen = true;
    private static int retryLoggin = 0;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private HomeActivity.myWebChromeClient mWebChromeClient;
    private PermissionRequest myRequest;
    //    private Boolean refreshVisibility = false;
    public static final int MULTIPLE_PERMISSIONS = 10;
    public static String sCurrentUrl = "";
    //    private CustomSwipeToRefresh.OnRefreshListener refreshListener;
    private boolean lexExternalLink = false;
    private ConnectivityReceiver connectivityReceiver;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA};
    private Snackbar mSnackbarOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home);


//        checkAndroidVersion();
        RootBeer rootBeer = new RootBeer(this);
//        WebView.setWebContentsDebuggingEnabled(true);
        splashPage = (WebView) findViewById(R.id.splashScreen);
        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        chatButton = (FloatingActionButton) findViewById(R.id.floatingchatButton);

        Boolean onlineCheck = isOnline(this);
        Log.d("SplashHTMLLOg","REACHED2");

        //todo remove not
        if (rootBeer.isRootedWithoutBusyBoxCheck()) {
            loginPage = (WebView) findViewById(R.id.actMainWebLogIn);
            loginPage.getSettings().setJavaScriptEnabled(true);
            loginPage.loadUrl("file:///android_asset/rooted_device.html");
        } else {
            if (onlineCheck) {
                loadWebview();
            } else {
                goOffline();           }

            ConstraintLayout homeLayout = findViewById(R.id.home_constraint_layout);
            mSnackbarOffline = Snackbar.make(homeLayout, "Check your network.", Snackbar.LENGTH_INDEFINITE);
            mSnackbarOffline.setAction("VIEW DOWNLOADS", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    goOffline();
                }
            });
            mSnackbarOffline.setActionTextColor(Color.WHITE);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
//            Util.checkPermission(HomeActivity.this);

        }

    }

    public boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(HomeActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }




    //    TODo try catch exception
    @Override
    public void onBackPressed() {
        try {
            if (loginPage != null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (loginPage.getUrl() != null && loginPage.getUrl().endsWith("/home")) {
                            Log.d("LOGINPAGEURL", "URL " + loginPage.getUrl());
                            HomeActivity.super.onBackPressed();
                        } else if (loginPage.canGoBack()) {
                            if (lexExternalLink) {
                                Log.d("LOGINPAGEURL", "URL PREV " + loginPage.getUrl());
                                loginPage.goBack();
                                Log.d("LOGINPAGEURL", "URL AFTER " + loginPage.getUrl());
                                lexExternalLink = false;
                            }
                            Log.d("LOGINPAGEURL", "prevURL " + loginPage.getUrl());
                            loginPage.goBack();
                            Log.d("LOGINPAGEURL", "nextURL " + loginPage.getUrl());
                        } else {
                            Log.d(getClass().getSimpleName(), "URL ELSE " + loginPage.getUrl());
                        }
                    }
                });

            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            Log.e(getClass().getSimpleName(), "errorMsg " + ex.getMessage());
        }

    }

    public void goHome(View view) {
        Util.navigateToPage(this, "/home");
    }

    public void goBrand(View view) {
        Util.navigateToPage(this, "/catalog/marketing/Brand Assets");
    }

    public void goInfyTv(View view) {
        Util.navigateToPage(this, "/infytv");
    }

    public void goCatalog(View view) {
        Util.navigateToPage(this, "/catalog");
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("ACtivityFlow", "New Intent");
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // TOdo
    @Override
    protected void onResume() {

        if(getIntent().getData() != null){
            sCurrentUrl = getIntent().getData().toString();
        }
        if(getIntent().getExtras() != null && getIntent().getExtras().getString("INFY_ME_URL_OPEN") != null && getIntent().getExtras().getString("INFY_ME_URL_OPEN").contains(baseUrl)){
            sCurrentUrl = getIntent().getExtras().getString("INFY_ME_URL_OPEN");
        }
        else if(getIntent().getExtras() != null && getIntent().getExtras().getString("INFY_ME_ID_OPEN") != null){
            if(getIntent().getExtras().getString("INFY_ME_ID_OPEN").contains("id=")){
                sCurrentUrl = baseUrl+"/app/toc/"+getIntent().getExtras().getString("INFY_ME_ID_OPEN").replace("id=","")+"/overview";
            }
        }

        Log.d("VALUE_TO_TEST","URL: "+sCurrentUrl);


        connectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, intentFilter);
//        getToken();

        if (loginPage != null) {
            loginPage.resumeTimers();
            loginPage.onResume();
        }
        String redirectUrl = getIntent().getStringExtra(PATH_PARAM);
        String redirectsUrl = getIntent().getStringExtra(SEARCH_PARAM);
        Log.d("onResumeChat", "redirect " + redirectUrl);
        Log.d("onResumeChat", "redirects " + redirectsUrl);
        if (redirectUrl != null && !redirectUrl.equals("") && loginPage == null) {
            loadWebview();
        } else if (redirectsUrl != null && !redirectsUrl.equals("")) {
            loginPage.evaluateJavascript("navigateTo('/search'," + redirectsUrl + ")", null);
        } else if (redirectUrl != null && !redirectUrl.equals("")) {
            loginPage.evaluateJavascript("navigateTo('" + redirectUrl + "')", null);
        }
        super.onResume();
    }

    public void loadWebview() {
        if (!isOnline(this)) {
            goOffline();
            return;
        }
//        chatButton.setVisibility(View.VISIBLE);
        loginPage = (WebView) findViewById(R.id.actMainWebLogIn);


        loginPage.getSettings().setJavaScriptEnabled(true);
        loginPage.getSettings().setDomStorageEnabled(true);
//        loginPage.getSettings()
//                .setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0");
        loginPage.getSettings().setAllowFileAccessFromFileURLs(true);
        loginPage.getSettings().setAllowUniversalAccessFromFileURLs(true);
        loginPage.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        loginPage.getSettings().setBuiltInZoomControls(true);
        loginPage.getSettings().setDisplayZoomControls(false);
        loginPage.getSettings().setSupportMultipleWindows(true);
        loginPage.getSettings().setPluginState(WebSettings.PluginState.ON);


        mWebChromeClient = new myWebChromeClient();
        loginPage.setWebChromeClient(mWebChromeClient);


        //        loginPage.getSettings().setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(loginPage, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
//        if (sCurrentUrl.equals("")) {
//            loginPage.loadUrl(baseUrl);
//        } else {
//            loginPage.loadUrl(sCurrentUrl);
//        }

        if (sCurrentUrl.equals("")) {
            loginPage.loadUrl(baseUrl);
        } else {
            loginPage.loadUrl(sCurrentUrl);
        }


        final WebViewBridgeJava webViewBridge = new WebViewBridgeJava(this);
//        swipe down
//        webViewBridge.setRoutingChangeListener(this);
        loginPage.addJavascriptInterface(webViewBridge, "appRef");

        loginPage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        loginPage.setLongClickable(true);
        loginPage.setWebViewClient(new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;

            long last_page_start;
            long now;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (view.getUrl().equalsIgnoreCase("file:///android_asset/index.html")) {
                    if (url.equalsIgnoreCase(Constants.baseUrl)) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    } else if (url.equalsIgnoreCase("mailto:mail@domain.com")) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"mail@domain.com"});    //insert email address here
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (url.contains("close")) {
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    } else if (url.contains("play.google.com")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    }
                } else {
                    Log.d("onLoadUrl", "REACHED");
                    if (!loadingFinished) {
                        redirect = true;
                    }
                    loadingFinished = false;
                    if (url.startsWith("tel:")) {
                        Log.d("shouldOverride", url);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } else if (url.startsWith("http:") || url.startsWith("https:")) {
//                        view.loadUrl(url);
                        Log.d("CheckValuesUrl","URL: "+url);
                        Log.d("CheckValuesUrl","VIEW: "+view.getUrl());
                        if (url.contains("microsoftonline") || url.contains("stsakaash") || url.contains("pluralsight")
                                || url.contains("hmm12") || url.contains("skype.com")) {
                            Log.d("ExternalUrlObserve", "URL inside" + url);
                            Log.d("ExternalUrlObserves", "1");
                            view.evaluateJavascript("navigateTo(" + url + ")", null);
                            lexExternalLink = true;
                            return false;
                        } else if (url.contains(Constants.environmentType)) {
                            view.evaluateJavascript("navigateTo(" + url + ")", null);
                            lexExternalLink = false;
                            return false;
                        }
                        else {
                            Intent activityIntent = new Intent(HomeActivity.this, ExternalPlayerActivity.class);
                            activityIntent.putExtra(PATH_PARAM, url);
                            Log.d("ExternalUrlObserve", "2");
                            startActivity(activityIntent);
                            return true;
                        }
                    } else if (url.contains("mailto:")) {
                        Log.d("shouldOverrideLoading1", "Reached here send Mail: " + url);
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{url.split(":")[1]});
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(HomeActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }


                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("URL_TESTING: ","FINISHED_VIEW: "+view.getUrl());
                Log.d("URL_TESTING: ","FINISHED_URL: "+url);
                try {
                        Log.i("SESSION_OUTGOING", "HIde button " + SharedPrefrence.getItem(HomeActivity.this, Constants.last_loggedin_date, "undefined"));

//                    if(getIntent().getStringExtra(PATH_PARAM) != null  && !url.equals("")){
//                        Log.d("onPageFinished",getIntent().getStringExtra(PATH_PARAM));
//                        getIntent().putExtra(PATH_PARAM, "");
//                    }
                    if (!redirect) {
                        loadingFinished = true;
                    }
                    //call remove_splash in 500 miSec
                    if (loadingFinished && !redirect) {
                        now = System.nanoTime();
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        remove_splash();
                                    }
                                },
                                2000);
                    } else {
                        redirect = false;
                    }

                    // Author: Vaibhav Savla
                    // this code is required to check for the white screen
//                    if (url.contains("#state") && url.contains("code")) {
//                        Log.d("onPageFinished", "Bad State reached. Loading base url again.");
//                        Toast.makeText(HomeActivity.this, "Cache corrupted. Reloading App.", Toast.LENGTH_LONG).show();
//                        loginPage.clearCache(true);

//                    }
                } catch (Exception ex) {
                    Log.e("Settingconfig", ex.getMessage());
                }


            }

//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                Log.d("HomeError", "Msg1: " + error.getDescription().toString() + " Request " + request.getUrl() + error.getErrorCode());
//                showErrorWebView(HomeActivity.this);
//                super.onReceivedError(view, request, error);
//
//            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.i("SESSION_OUTGOING",errorResponse.getStatusCode()+" ");
                if ((errorResponse.getStatusCode() == 403 || errorResponse.getStatusCode() == 401) && retryLoggin < 3 &&
                        !request.getUrl().getPath().contains("/user/roles") && !isAuthenticated) {
                    Toast.makeText(HomeActivity.this, getString(R.string.loadingToast), Toast.LENGTH_SHORT).show();
                    retryLoggin += 1;
                    if (loginPage != null) {
                        loginPage.clearCache(true);
                    }
                    loadWebview();
                } else if (retryLoggin >= 3) {
                    loginPage.loadUrl("file:///android_asset/index.html");
                    splashPage.setVisibility(View.GONE);
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("URL_TESTING: ","STARTED_VIEW: "+view.getUrl());
                Log.d("URL_TESTING: ","STARTED_URL: "+url);
                Log.i("SESSION_OUTGOING", "pagestart");
                loadingFinished = false;
                last_page_start = System.nanoTime();

                show_splash();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i("SESSION_OUTGOING",view.getUrl());
                super.onLoadResource(view, url);
            }

            private void show_splash() {
                if (loginPage.getVisibility() == View.VISIBLE && showSplashScreen) {
                    chatButton.setVisibility(View.GONE);
                    splashPage.loadUrl("file:///android_asset/splashscreen.html");
                    loginPage.setVisibility(View.GONE);
                    splashPage.setVisibility(View.VISIBLE);
                }
            }

            //if a new "page start" was fired dont remove splash screen
            private void remove_splash() {
                if (last_page_start < now) {
                    loginPage.setVisibility(View.VISIBLE);
                    splashPage.setVisibility(View.GONE);
                    showSplashScreen = false;


//                    chatButton.setVisibility(View.VISIBLE);
                }
            }

        });

//        loginPage.setWebChromeClient(new WebChromeClient(){


    }


//    public static String getWifiName(Context context) {
//        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if (manager.isWifiEnabled()) {
//            WifiInfo wifiInfo = manager.getConnectionInfo();
//            if (wifiInfo != null) {
//                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
//                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
//                    Log.d("NameOfWIFI",wifiInfo.getSSID());
//                    return wifiInfo.getSSID().replaceAll("\"","");
//                }
//            }
//        }
//        return null;
//    }

    @Override
    protected void onPause() {
        Log.d("ACtivityFlow", "Pause");
        if (loginPage != null) {
            loginPage.onPause();
            loginPage.pauseTimers();
        }
        unregisterReceiver(connectivityReceiver);
        super.onPause();

//        Log.d("redirectUrl",loginPage.getUrl());
    }





    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void askForPermission(String origin, String permission, int requestCode) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        requestCode);
            }
        } else {
            try {
                myRequest.grant(myRequest.getResources());
            } catch (Exception ex) {
                Log.e("HomeActivityProctor", ex.getMessage());
            }
//
        }
    }





    @Override
    public void connectedToInternet() {
        if (mSnackbarOffline != null) {
            mSnackbarOffline.dismiss();
        }
    }

    @Override
    public void offline() {
        mSnackbarOffline.show();
    }

    private void goOffline() {
        try {
            String lastLoggedInString = SharedPrefrence.getItem(this, Constants.last_loggedin_date, "undefined");
            Log.d("HomeActivityError", "val " + lastLoggedInString);
            long lastLoggedInLong = 0L;
            if (lastLoggedInString.equalsIgnoreCase("undefined")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("TitleName");  //set suitable title here
                builder.setMessage("You have not logged in yet. Please login and check your network connectivity");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                lastLoggedInLong = Long.parseLong(lastLoggedInString);
                lastLoggedInLong = lastLoggedInLong + Constants.accessExpiry;
                if (lastLoggedInLong > System.currentTimeMillis()) {
                    Intent downloadPage = new Intent(HomeActivity.this, DownloadsWebView.class);
                    downloadPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(downloadPage);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Access Downloads");
                    builder.setMessage("You haven't logged-in in the past one week. Please login once again for accessing the offline content.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        } catch (Exception ex) {
            Log.d("Exception", "Message: " + ex.getMessage());
        }


    }






    class myWebChromeClient extends WebChromeClient {
        private Bitmap mDefaultVideoPoster;
        private View mVideoProgressView;

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            loginPage.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
//                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


            loginPage.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            Log.d("PermissionRequired", "onPermissionRequest");
            HomeActivity.this.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    Log.d("PermissionRequired", request.getOrigin().toString());
                    myRequest = request;
                    final String[] requestedResources = request.getResources();
                    for (String permission : requestedResources) {
                        switch (permission) {
//                                case "android.webkit.resource.AUDIO_CAPTURE": {
//                                    askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MULTIPLE_PERMISSIONS);
//                                    break;
//                                }
                            case "android.webkit.resource.VIDEO_CAPTURE": {
                                askForPermission(request.getOrigin().toString(), Manifest.permission.CAMERA, MULTIPLE_PERMISSIONS);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
}


