package com.sproutling.ui.widget

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View.OnFocusChangeListener
import com.sproutling.R
import com.sproutling.utils.Utils

/**
 * Created by subram13 on 11/15/17.
 */
class CustomShInputTextLayout : TextInputLayout {
    private var mErrorMsg: String? = null
    private var mInfoMsg: String? = null
    var mIsErrorShowing = false
    private lateinit var mCustomShEditText: CustomShEditText

    constructor(ctx: Context) : super(ctx) {
        initView(context)
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        initView(ctx, attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        initView(ctx, attrs)
    }

    companion object {
        const val TAG = "CustomShInputTextLayout"
    }

    private fun initView(ctx: Context, attrs: AttributeSet? = null) {
        if (attrs != null) {
            var styledAttributes = ctx.obtainStyledAttributes(attrs, R.styleable.CustomShInputTextLayout)
            mErrorMsg = styledAttributes.getString(R.styleable.CustomShInputTextLayout_errorMsg)
            mInfoMsg = styledAttributes.getString(R.styleable.CustomShInputTextLayout_infoMsg)
        }
    }

    public fun showErrorMsg(show: Boolean) {
        if (show) {
            if (!TextUtils.isEmpty(mErrorMsg)) {
                setError(mErrorMsg)
                mIsErrorShowing = true
            }
        } else {
            mIsErrorShowing = false
            super.setError(null)
        }
        updateHintTextCase()
    }

    public fun showInfoMsg(show: Boolean) {
        if (show) {
            if (!TextUtils.isEmpty(mInfoMsg)) {
                super.setError(mInfoMsg)
                setErrorTextAppearance(R.style.InfoAppearance)
            }
        } else {
            super.setError(null)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    private fun init() {
        setTypeface(ShTextView.TypeFaces.get(context, ShTextView.TypeFaces.LONDON))
        mCustomShEditText = editText as CustomShEditText
        if (mCustomShEditText != null) {
            Log.d(TAG, "mCustomShEditText is not null")
            mCustomShEditText.setInputTextLayout(this)
        } else {
            Log.d(TAG, "mCustomShEditText is null")
        }
        onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showInfoMsg(true)
            } else {
                if (!mIsErrorShowing) {
                    showInfoMsg(false)
                }
            }
//            val hintText = hint.toString()
//            if (!TextUtils.isEmpty(hintText)) {
//                if (hasFocus) {
//                    hint = hintText.toUpperCase()
//                } else {
//                    if (TextUtils.isEmpty(editText.toString()))
//                        hint = Utils.toCamelCase(hintText)
//                    else
//                        hint = hintText.toUpperCase()
//                }
//            }
        }
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        mErrorMsg = error.toString()
        mIsErrorShowing = !TextUtils.isEmpty(error)
        setErrorTextAppearance(R.style.ErrorAppearance_Msg)
        val editText = editText as CustomShEditText
        if (editText != null) {
            var drawableLeft = if (editText.compoundDrawables != null) editText.compoundDrawables[0] else null
            if (drawableLeft != null) {
                if (!TextUtils.isEmpty(error)) {
                    drawableLeft.setTint(Utils.getColor(context, R.color.red))
                } else {
                    if (editText.isFocused)
                        drawableLeft.setTint(Utils.getColor(context, R.color.colorAccent))
                    else
                        drawableLeft.setTint(Utils.getColor(context, R.color.dolphin))
                }
            }
        }
        updateHintTextCase()
    }

    private fun updateHintTextCase() {
        var hintText = hint.toString()
        var customShEditText = editText as CustomShEditText
        if (!TextUtils.isEmpty(hintText)) {
            hint = if (customShEditText.isFocused) {
                hintText.toUpperCase()
            } else {
                if (TextUtils.isEmpty(customShEditText.text.toString()) && !mIsErrorShowing)
                    Utils.toCamelCase(hintText)
                else
                    hintText.toUpperCase()
            }
        }
//        if (mIsErrorShowing) {
//            setHintTextAppearance(R.style.ErrorAppearance)
//        } else
//            setHintTextAppearance(R.style.NonErrorAppearance)
    }
}