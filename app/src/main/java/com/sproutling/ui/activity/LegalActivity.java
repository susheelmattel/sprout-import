/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by bradylin on 4/10/17.
 */

public class LegalActivity extends Activity {

    ShTextView mTermsView;
        private TextView mTvContentView;
//    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        mTermsView = (ShTextView) findViewById(R.id.terms);
//        mWebView = (WebView) findViewById(R.id.wv_content);

        ((ShTextView) findViewById(R.id.navigation_title)).setText(R.string.legal_title);
        findViewById(R.id.navigation_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.LEGAL_BACK_BUTTON);
                finish();
            }
        });
        findViewById(R.id.navigation_action).setVisibility(View.GONE);
        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logEvents(LogEvents.LEGAL_I_ACCEPT);
                startActivity(new Intent(LegalActivity.this, SetupActivity.class));
                finish();
            }
        });
//        WebSettings webSettings = mWebView.getSettings();
//        Resources res = getResources();
//        float fontSize = res.getDimension(R.dimen.txtSize);
//        webSettings.setDefaultFontSize((int) fontSize);
//
//        mWebView.loadDataWithBaseURL(null, getString(R.string.legal_content), "text/html", "utf-8", null);


        setupTerms();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        if (intent != null && intent.getScheme() != null) {
            if (intent.getScheme().equals(getString(R.string.legal_tos_scheme))) {
                Utils.logEvents(LogEvents.TERMS_OF_SERVICE);
                Intent mIntent = new Intent(this, TermsActivity.class);
                mIntent.putExtra(TermsActivity.EXTRA_TITLE, getString(R.string.tos_title_terms));
                startActivity(mIntent);
            } else if (intent.getScheme().equals(getString(R.string.legal_pp_scheme))) {
                Utils.logEvents(LogEvents.PRIVACY_STATEMENT);
                Intent mIntent = new Intent(this, TermsActivity.class);
                mIntent.putExtra(TermsActivity.EXTRA_TITLE, getString(R.string.tos_title_pp));
                startActivity(mIntent);
            }
        }
    }

    void setupTerms() {
        mTermsView.setText(getText(R.string.legal_terms));
        mTermsView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
