package com.ayan.youtubelegacy;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.view.View;
import android.view.WindowManager;

import android.webkit.CookieManager;
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

                        errorLayout.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);

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

        WebSettings settings = webView.getSettings();

        // JavaScript
        settings.setJavaScriptEnabled(true);

        // Website storage
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        // Cookies
        CookieManager cookieManager =
                CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(
                    webView,
                    true
            );
        }

        // Viewport
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Zoom
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        if (Build.VERSION.SDK_INT >= 11) {
            settings.setDisplayZoomControls(false);
        }

        // Media
        if (Build.VERSION.SDK_INT >= 17) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        // Windows
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        /*
         * IMPORTANT:
         *
         * Do NOT pretend to be Chrome 80.
         *
         * Let Android's WebView use its own normal
         * User-Agent.
         */
        settings.setUserAgentString(
                settings.getUserAgentString()
        );

        webView.setBackgroundColor(Color.WHITE);

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
                    public void onPageFinished(
                            WebView view,
                            String url) {

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
                new WebChromeClient()
        );
    }

    private void showError(String message) {

        webView.setVisibility(View.GONE);

        errorLayout.setVisibility(View.VISIBLE);

        TextView errorText =
                findViewById(R.id.errorText);

        if (message == null ||
                message.trim().length() == 0) {

            errorText.setText(
                    "YouTube could not be loaded."
            );

        } else {

            errorText.setText(message);
        }
    }

    @Override
    public void onBackPressed() {

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

            webView.loadUrl("about:blank");
            webView.stopLoading();

            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);

            webView.destroy();
        }

        super.onDestroy();
    }
}
