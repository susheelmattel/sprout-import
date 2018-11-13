/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;

/**
 * Created by loren on 2016/6/22.
 */
public class ArticleWebViewActivity extends BaseMqttActivity {

    private static final String TAG = "WebViewActivity";

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_TITLE = "extra_title";

    private WebView mWebView;
    private String mTempUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_webview);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mTempUrl = url;

        ((ShTextView) findViewById(R.id.navigation_title)).setText(title);
        findViewById(R.id.navigation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.navigation_action).setVisibility(View.GONE);

        initWeb();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mWebView.resumeTimers();
        mWebView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private void initWeb() {
        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= 17) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        } else if (Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);
        }
        mWebView.loadUrl(mTempUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
