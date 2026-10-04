package com.ayan.youtubelegacy;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.KeyEvent;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(false);

        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(false);

        settings.setMediaPlaybackRequiresUserGesture(false);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);

        settings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 5.1.1) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/80.0.3987.149 Mobile Safari/537.36"
        );

        CookieManager cookieManager =
                CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(
                    webView,
                    true
            );
        }

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("https://www.youtube.com/");
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
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