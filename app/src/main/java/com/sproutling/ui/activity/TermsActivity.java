/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

public class TermsActivity extends BaseActivity {

    public static final String TAG = "TermsActivity";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_BODY = "body";
    private static final String EMPTY_TITLE = "No Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        ShTextView mTitleView = (ShTextView) findViewById(R.id.navigation_title);
        findViewById(R.id.navigation_back).setOnClickListener(mOnBackButtonClickListener);
        findViewById(R.id.navigation_action).setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mTitleView.setText(extras.getString(EXTRA_TITLE, EMPTY_TITLE));
        }
    }

    View.OnClickListener mOnBackButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
