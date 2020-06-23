package com.infosysit.rainforest;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.infosysit.sdk.Constants;
import com.infosysit.sdk.services.ExternalPlayerService;
import com.infosysit.sdk.services.TelemetryServices;

import java.net.URL;

import static com.infosysit.rainforest.ColorUtil.changeWindowColor;
import static com.infosysit.sdk.Constants.EXTERNAL_OPEN;
import static com.infosysit.sdk.Constants.PATH_PARAM;

public class ExternalPlayerActivity extends AppCompatActivity {

    public WebView playerwWebView = null;
    public TextView resourceName1 = null;
    public TextView resourceName2 = null;
    public ImageButton copyUrl = null;
    private LinearLayout externalPlayerToolbar = null;
    private ConstraintLayout toolBar = null;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private ImageButton goBackBtn;
    private ExternalPlayerActivity.myWebChromeClient mWebChromeClient;
    //    private DrawerLayout mDrawerLayout;
    private boolean playerLoaded = false;
    private boolean running = false;
    private String externalUrl = "";
    //    private ImageView lockIcon;
    // one time url chnage allowed for coursera
    private boolean changedExternalUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        Log.d(getClass().getSimpleName(), "OnCreateCalled");
        setContentView(R.layout.activity_external_player);
        playerwWebView = (WebView) findViewById(R.id.externalWebView);
        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainerPlayer);
        toolBar = (ConstraintLayout) findViewById(R.id.toolbar);
        resourceName1 = (TextView) findViewById(R.id.resource_name1);
        resourceName2 = (TextView) findViewById(R.id.resource_name2);
        copyUrl = (ImageButton) findViewById(R.id.copyLink);
        goBackBtn = (ImageButton) findViewById(R.id.goBack);
        externalPlayerToolbar = (LinearLayout) findViewById(R.id.external_player_toolbar);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        changeWindowColor(this);
//        lockIcon = (ImageView) findViewById(R.id.lock_webview);
//        mDrawerLayout = findViewById(R.id.drawer_layout);
        runTelemetry();
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        // set item as selected to persist highlight
//                        menuItem.setChecked(true);
//                        // close drawer when item is tapped
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });

        final ProgressBar loader = findViewById(R.id.progressBar);
        mWebChromeClient = new myWebChromeClient();
        playerwWebView.setWebChromeClient(mWebChromeClient);
        playerwWebView.getSettings().setJavaScriptEnabled(true);
        playerwWebView.getSettings().setDomStorageEnabled(true);
        playerwWebView.getSettings().setBuiltInZoomControls(true);
        playerwWebView.getSettings().setDisplayZoomControls(false);
        playerwWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        playerwWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        playerwWebView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return (event.getAction() == MotionEvent.ACTION_MOVE);
//            }
//        });
        externalUrl = getIntent().getStringExtra(PATH_PARAM);
        //externalUrl = "https://ieeexplore.ieee.org/servlet/wayf.jsp?entityId=https://sts.windows.net/63ce7d59-2f3e-42cd-a8cc-be764cff5eb6/&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpl%2Febooks%2FbookPdfWithBanner.jsp%3FfileName%3D6282363.pdf%26bkn%3D6267326%26pdfType%3Dchapter";
        playerwWebView.loadUrl(externalUrl);
//        playerwWebView.loadUrl("https://infosys.myhbp.org/hmm12/content/innovation_and_creativity/shape_a_creative_team.html?anchor=get_comfortable_with_team_conflict#!#get_comfortable_with_team_conflict");
//        playerwWebView.addJavascriptInterface(new WebViewBridgeJava(this),"appRef");
//
        playerwWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        playerwWebView.setLongClickable(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(playerwWebView, true);
        playerwWebView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                hideSystemUI(); // setup fullscreen
            }
        });


        playerwWebView.setWebViewClient(new WebViewClient() {


            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return  false;
//                return handleUri(uri);
            }

            private boolean handleUri(final Uri uri) {
                final String url = uri.toString();
                if(getIntent().getStringExtra(EXTERNAL_OPEN) == null){
                    try {
                        URL domain = new URL(url);
                        String path = domain.getFile().substring(0, domain.getFile().lastIndexOf('/'));
                        String base = domain.getProtocol() + "://" + domain.getHost() + path;
                        Log.d("ExternalPlayerUrl", "artifact: " + externalUrl);
                        Log.d("ExternalPlayerUrl", "base: " + base);
                        Log.d("ExternalPlayerUrl", "urltoplay: " + url);
                        if (playerwWebView.getVisibility() == View.VISIBLE) {
                            if ((externalUrl.split("\\?")[0]).contains(url) || url.contains(base) || url.contains("microsoftonline") || url.contains("stsakaash") ||
                                    url.contains(Constants.environmentType) || url.contains("skype.com") || (externalUrl.contains(base) && base.contains("lessons"))) {
                                return false;
                            } else if (url.contains("coursera.org") && url.contains("/learn/") && !changedExternalUrl) {
                                String[] tempUrl = url.split("/learn/");
                                externalUrl = tempUrl[0] + "/learn/" + tempUrl[1].split("/")[0];
                                changedExternalUrl = true;
                                Log.d("ExternalPlayerUrl", "newUrl " + externalUrl);
                                return false;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ExternalPlayerActivity.this);
                                builder.setTitle("External Link");
                                builder.setMessage("This is an external link. Do you want to open it in a new window?");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("ExternalPlayerUrl", "ok clicked");
                                        dialog.dismiss();
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        startActivity(browserIntent);
                                    }
                                });

                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("ExternalPlayerUrl", "cancel clicked");
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();

                                return true;
                            }
                        } else {
                            return false;
                        }
                    } catch (Exception ex) {
                        Log.e("ExceptionPlayer", ex.getMessage());
                        return false;
                    }
                } else {
                    Log.d("testValue","Value: "+getIntent().getStringExtra(EXTERNAL_OPEN));
                    return false;
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("externalUrl", "page started");
                Log.d("URL_TESTING: ","STARTED_VIEW: "+view.getUrl());
                Log.d("URL_TESTING: ","STARTED_URL: "+url);
                loader.setVisibility(View.VISIBLE);
                copyUrl.setVisibility(View.GONE);
                playerwWebView.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("externalUrl", "page finished");
                Log.d("URL_TESTING: ","FINISHED_VIEW: "+view.getUrl());
                Log.d("URL_TESTING: ","FINISHED_URL: "+url);
                loader.setVisibility(View.GONE);
                playerwWebView.setVisibility(View.VISIBLE);
                resourceName1.setText(view.getTitle());
                resourceName2.setText(view.getUrl());
                copyUrl.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("PlayerError", "Error1" + " request " + request.getUrl() + " error: " + error.getDescription() + " code " + error.getErrorCode());
                super.onReceivedError(view, request, error);
                //auth error
                if (error.getErrorCode() == -4 || error.getErrorCode() == -12 || error.getErrorCode() == -13 || error.getErrorCode() == -14 || error.getErrorCode() == -2) {
                    view.loadUrl("about:blank");
                    showErrorWebView(ExternalPlayerActivity.this, error.getErrorCode());
                }
            }
        });

    }

    private void showErrorWebView(final Context context, int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // auth error
        if (errorCode == -4)
            builder.setMessage(R.string.error_auth_failed);
        // file error
        if (errorCode == -12 || errorCode == -13 || errorCode == -14)
            builder.setMessage(R.string.error_in_file);
        // url error
        if (errorCode == -2)
            builder.setMessage(R.string.error_in_webView_url);

        builder.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                goBack();
            }
        });

        builder.setNegativeButton(R.string.report, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.navigateToPage(ExternalPlayerActivity.this, "/contact-us");
            }
        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        playerwWebView.resumeTimers();
        Log.d(getClass().getSimpleName(), "OnStartCalled");
        changedExternalUrl = false;
        running = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerwWebView.onResume();
        Log.d(getClass().getSimpleName(), "OnResumeCalled");
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerwWebView.onPause();
        Log.d("TelemetryExternalPlayer", "Pause called");
        Intent telemetryService = new Intent(ExternalPlayerActivity.this, ExternalPlayerService.class);
        telemetryService.putExtra(Constants.playerData, Constants.externalPlayerTelemetryJson.toString());
        Constants.externalPlayerTelemetryJson = new JsonObject();
        startService(telemetryService);
        running = false;
    }


    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
    }

    public void goBack() {
        super.onBackPressed();
    }


    public void goHome(View view) {
        Util.navigateToPage(this, "/home");
    }

    private void sendTelemetry() {
        JsonObject telemetryPlayer = new JsonObject();
        telemetryPlayer.addProperty("type", "running");
        telemetryPlayer.addProperty("plugin", "iframe");
        JsonObject telemetryData = new JsonObject();
        telemetryData.addProperty("isideal", false);
        telemetryData.addProperty("lostFocus", false);
        telemetryPlayer.add("data", telemetryData);
        TelemetryServices telemetryServices = new TelemetryServices();
        try {
            telemetryServices.telemetryPlayer(ExternalPlayerActivity.this, telemetryPlayer, "online");
            if (Constants.externalPlayerTelemetryJson.getAsJsonArray("events").size() == 10) {
                Intent telemetryService = new Intent(ExternalPlayerActivity.this, ExternalPlayerService.class);
                telemetryService.putExtra(Constants.playerData, Constants.externalPlayerTelemetryJson.toString());
                Constants.externalPlayerTelemetryJson = new JsonObject();
                startService(telemetryService);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runTelemetry() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    sendTelemetry();
                }
                handler.postDelayed(this, 30000);

            }
        });

    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (inCustomView()) {
                hideCustomView();
                return true;
            }

            if ((mCustomView == null) && playerwWebView.canGoBack()) {
                playerwWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void copyLink(View view) {
        ClipboardManager cm = (ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE);
        cm.setText(resourceName2.getText());
        Toast.makeText(this, "Link Copied", Toast.LENGTH_SHORT).show();
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
            playerwWebView.getRootView().setVisibility(View.GONE);

//            ((View) mWebView.getRootView()).setVisibility(View.GONE);

            customViewContainer.setVisibility(View.VISIBLE);
            externalPlayerToolbar.setVisibility(View.GONE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(ExternalPlayerActivity.this);
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


            playerwWebView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);
            externalPlayerToolbar.setVisibility(View.VISIBLE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }
}
