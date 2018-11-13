/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.sproutling.R;

/**
 * Created by subram13 on 1/20/17.
 */

public class ShCustomProgressBar extends ProgressDialog {
    public ShCustomProgressBar(Context context) {
        super(context);
    }

    public ShCustomProgressBar(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_bar);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setIndeterminate(true);
        setCancelable(false);
    }
}
