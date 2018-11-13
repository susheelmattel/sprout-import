/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sproutling.R;

/**
 * Created by subram13 on 3/17/17.
 */

public class ShAlertView extends RelativeLayout {
    private ShTextView mTvTitle;
    private ShTextView mTvMsg;
    private ShTextView mBtn;
    private ShTextView mBtn2;
    private ShTextView mBtn3;
    private ImageView mImgAlert;
    private View mDivider2;
    private View mDivider3;

    public ShAlertView(Context context) {
        super(context);
        init(context);
    }

    public ShAlertView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ShAlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dialog_alert_layout, this);
        mTvTitle = (ShTextView) findViewById(R.id.title);
        mTvMsg = (ShTextView) findViewById(R.id.message);
        mBtn = (ShTextView) findViewById(R.id.ok);
        mBtn2 = (ShTextView) findViewById(R.id.ok2);
        mBtn3 = (ShTextView) findViewById(R.id.ok3);
        mDivider2 =  findViewById(R.id.divider2);
        mDivider3 = findViewById(R.id.divider3);
        mImgAlert = (ImageView) findViewById(R.id.image);

        mBtn2.setVisibility(GONE);
        mBtn3.setVisibility(GONE);
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        initView();
//    }
//
//    private void initView(){
//        mTvTitle = (ShTextView) findViewById(R.id.title);
//        mTvMsg = (ShTextView) findViewById(R.id.message);
//        mBtn = (ShTextView) findViewById(R.id.ok);
//        mImgAlert = (ImageView) findViewById(R.id.image);
//    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setMessage(String msg) {
        mTvMsg.setText(msg);
    }

    public void setMessage(SpannableStringBuilder msg) {
        mTvMsg.setText(msg);
    }

    public void setButtonText(String btnText) {
        mBtn.setText(btnText);
    }

    public void setButton2Text(String btnText) {
        mBtn2.setText(btnText);
    }

    public void setButton3Text(String btnText) {
        mBtn3.setText(btnText);
    }

    public void setButton2Visibility(boolean shouldShow) {
        if(shouldShow){
            mBtn2.setVisibility(VISIBLE);
            mDivider2.setVisibility(VISIBLE);
            mDivider3.setVisibility(VISIBLE);
        } else{
            mBtn2.setVisibility(GONE);
            mDivider2.setVisibility(GONE);
            mDivider3.setVisibility(GONE);
        }
    }

    public void setButton3Visibility(boolean shouldShow) {
        if(shouldShow){
            mBtn3.setVisibility(VISIBLE);
            mDivider3.setVisibility(VISIBLE);
        } else{
            mBtn3.setVisibility(GONE);
            mDivider3.setVisibility(GONE);
        }
    }

    public void setImgAlert(int resource) {
        mImgAlert.setImageResource(resource);
    }

    public void setButtonClickListener(View.OnClickListener onClickListener) {
        mBtn.setOnClickListener(onClickListener);
    }

    public void setButton2ClickListener(View.OnClickListener onClickListener) {
        mBtn2.setOnClickListener(onClickListener);
    }

    public void setButton3ClickListener(View.OnClickListener onClickListener) {
        mBtn3.setOnClickListener(onClickListener);
    }
}
