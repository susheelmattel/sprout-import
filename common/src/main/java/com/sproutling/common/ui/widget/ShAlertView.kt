/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.sproutling.common.R


/**
 * Created by subram13 on 3/17/17.
 */

class ShAlertView : RelativeLayout {
    private var mTvTitle: TextView? = null
    private var mTvMsg: TextView? = null
    private var mBtn: TextView? = null
    private var mBtn2: TextView? = null
    private var mImgAlert: ImageView? = null
    private var divider2: View? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.dialog_alert_layout, this)
        mTvTitle = findViewById(R.id.title)
        mTvMsg = findViewById(R.id.message)
        mBtn = findViewById(R.id.ok)
        mBtn2 = findViewById(R.id.button2)
        mImgAlert = findViewById(R.id.image)
        divider2 = findViewById(R.id.divider2)
    }

    fun setTitle(title: String) {
        mTvTitle!!.text = title
    }

    fun setMessage(msg: String) {
        mTvMsg!!.text = msg
    }

    fun setMessage(msg: SpannableStringBuilder) {
        mTvMsg!!.text = msg
    }

    fun setButtonText(btnText: String) {
        mBtn!!.text = btnText
    }

    fun setButtonTextColor(color: Int) {
        mBtn!!.setTextColor(color)
    }

    fun setButton2Visibility(shouldDisplay: Boolean) {
        if (shouldDisplay) {
            mBtn2?.visibility = View.VISIBLE
            divider2?.visibility = View.VISIBLE
        }
    }

    fun setButton2Text(btn2Text: String?) {
        mBtn2!!.text = btn2Text
    }

    fun setImgAlert(resource: Int) {
        mImgAlert!!.setImageResource(resource)
    }

    fun setButtonClickListener(onClickListener: OnClickListener?) {
        mBtn!!.setOnClickListener(onClickListener)
    }

    fun setButton2ClickListener(onClickListener: OnClickListener?) {
        mBtn2!!.setOnClickListener(onClickListener)
    }
}
