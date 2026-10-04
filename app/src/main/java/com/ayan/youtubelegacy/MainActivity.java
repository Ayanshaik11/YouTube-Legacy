package com.ayan.youtubelegacy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private WebView webView;

    private LinearLayout errorLayout;

    private Button retryButton;

    private View customView;

    private WebChromeClient.CustomViewCallback customViewCallback;

    private static final String YOUTUBE_URL =
            "https://www.youtube.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        errorLayout = findViewById(R.id.errorLayout);

        retryButton = findViewById(R.id.retryButton);

        setupWebView();

        retryButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        errorLayout.setVisibility(
                                View.GONE
                        );

                        webView.setVisibility(
                                View.VISIBLE
                        );

                        webView.loadUrl(YOUTUBE_URL);
                    }
                }
        );

        if (savedInstanceState == null) {

            webView.loadUrl(YOUTUBE_URL);

        } else {

            webView.restoreState(savedInstanceState);
        }
    }


    private void setupWebView() {

        WebSettings settings =
                webView.getSettings();


        // Enable JavaScript

        settings.setJavaScriptEnabled(true);


        // Enable website storage

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);


        // Cookies

        CookieManager
                .getInstance()
                .setAcceptCookie(true);


        if (Build.VERSION.SDK_INT >= 21) {

            CookieManager
                    .getInstance()
                    .setAcceptThirdPartyCookies(
                            webView,
                            true
                    );
        }


        // Viewport

        settings.setUseWideViewPort(false);

        settings.setLoadWithOverviewMode(false);


        // Disable zoom controls

        settings.setSupportZoom(false);

        settings.setBuiltInZoomControls(false);

        settings.setDisplayZoomControls(false);


        // Allow media playback

        settings.setMediaPlaybackRequiresUserGesture(false);


        // JavaScript windows

        settings.setJavaScriptCanOpenWindowsAutomatically(
                true
        );

        settings.setSupportMultipleWindows(false);


        /*
         * Chrome-like User Agent.
         *
         * This can improve compatibility with
         * websites that reject very old WebViews.
         */

        settings.setUserAgentString(
                "Mozilla/5.0 " +
                "(Linux; Android 5.1.1) " +
                "AppleWebKit/537.36 " +
                "(KHTML, like Gecko) " +
                "Chrome/80.0.3987.149 " +
                "Mobile Safari/537.36"
        );


        webView.setBackgroundColor(
                Color.WHITE
        );


        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView view,
                            String url) {

                        view.loadUrl(url);

                        return true;
                    }


                    @Override
                    public void onPageStarted(
                            WebView view,
                            String url,
                            Bitmap favicon) {

                        errorLayout.setVisibility(
                                View.GONE
                        );

                        webView.setVisibility(
                                View.VISIBLE
                        );
                    }


                    @Override
                    public void onReceivedError(
                            WebView view,
                            int errorCode,
                            String description,
                            String failingUrl) {

                        showError(description);
                    }


                    @Override
                    public void onReceivedError(
                            WebView view,
                            WebResourceRequest request,
                            WebResourceError error) {

                        if (Build.VERSION.SDK_INT >= 23) {

                            if (request.isForMainFrame()) {

                                showError(
                                        error.getDescription()
                                                .toString()
                                );
                            }
                        }
                    }
                }
        );


        webView.setWebChromeClient(
                new WebChromeClient() {

                    @Override
                    public void onShowCustomView(
                            View view,
                            CustomViewCallback callback) {

                        enterFullscreen(
                                view,
                                callback
                        );
                    }


                    @Override
                    public void onHideCustomView() {

                        exitFullscreen();
                    }
                }
        );


        webView.setDownloadListener(
                new DownloadListener() {

                    @Override
                    public void onDownloadStart(
                            String url,
                            String userAgent,
                            String contentDisposition,
                            String mimetype,
                            long contentLength) {

                        // Intentionally left empty.
                        //
                        // YouTube normally does not expose
                        // direct video downloads here.
                    }
                }
        );
    }


    private void showError(String message) {

        webView.setVisibility(
                View.GONE
        );

        errorLayout.setVisibility(
                View.VISIBLE
        );

        TextView errorText =
                findViewById(R.id.errorText);

        if (message == null) {

            errorText.setText(
                    "Unable to load YouTube."
            );

        } else {

            errorText.setText(message);
        }
    }


    private void enterFullscreen(
            View view,
            WebChromeClient.CustomViewCallback callback) {

        customView = view;

        customViewCallback = callback;


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );


        webView.setVisibility(
                View.GONE
        );


        ViewGroup decor =
                (ViewGroup) getWindow()
                        .getDecorView();


        decor.addView(
                customView,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
    }


    private void exitFullscreen() {

        if (customView == null) {

            return;
        }


        ViewGroup decor =
                (ViewGroup) getWindow()
                        .getDecorView();


        decor.removeView(customView);


        customView = null;


        if (customViewCallback != null) {

            customViewCallback.onCustomViewHidden();

            customViewCallback = null;
        }


        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );


        webView.setVisibility(
                View.VISIBLE
        );
    }


    @Override
    public void onBackPressed() {

        if (customView != null) {

            exitFullscreen();

            return;
        }


        if (webView.canGoBack()) {

            webView.goBack();

            return;
        }


        super.onBackPressed();
    }


    @Override
    protected void onSaveInstanceState(
            Bundle outState) {

        webView.saveState(outState);

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPause() {

        super.onPause();

        CookieManager
                .getInstance()
                .flush();
    }


    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.loadUrl(
                    "about:blank"
            );

            webView.stopLoading();

            webView.setWebChromeClient(null);

            webView.setWebViewClient(null);

            webView.destroy();
        }


        super.onDestroy();
    }
}