/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sproutling.R;

/**
 * Created by Xylon on 2016/12/28.
 */

public class ChildNameButton extends LinearLayout {

    private LinearLayout mBtnName;
    private ShTextView mChlidName;
    private ImageView mRedDot;

    public ChildNameButton(Context context) {
        super(context);
        init(context);
    }

    public ChildNameButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChildNameButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        LinearLayout mainContent = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.button_childname, this);
        mBtnName = (LinearLayout) mainContent.findViewById(R.id.btnName);
        mChlidName = (ShTextView) mainContent.findViewById(R.id.chlidName);
        mRedDot = (ImageView) mainContent.findViewById(R.id.redDot);
    }

    public void setBabyName(String name) {
        mChlidName.setText(name);
    }

    public void setRedDotAppear() {
        mRedDot.setVisibility(View.VISIBLE);
    }

    public void setRedDotDisappear() {
        mRedDot.setVisibility(View.GONE);
    }

    public void setDrawablePadding() {
        mChlidName.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.child_btn_drawable_padding));
    }
}
