package com.sproutling.common.ui.widget

import android.content.Context
import android.support.design.widget.TextInputEditText

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

import com.sproutling.common.R
import com.sproutling.common.utils.Utils


/**
 * Created by subram13 on 11/15/17.
 */
class CustomShEditText : TextInputEditText {

    private var mFocusChangeListener: View.OnFocusChangeListener? = null

    private lateinit var mInputTextLayout: CustomShInputTextLayout

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr)


    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    companion object {
        const val TAG = "CustomShEditText"
        const val TEXT_SIZE = 16F

    }

    private fun init() {
        setTextColor(Utils.getColor(context, R.color.dolphin))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE)
        setHintTextColor(Utils.getColor(context, R.color.fog))
        isFocusableInTouchMode = true
        super.setOnFocusChangeListener({ v, hasFocus ->
            var drawableLeft = if (compoundDrawables != null) compoundDrawables[0] else null
            if (drawableLeft != null) {
                if (hasFocus) {
                    drawableLeft.setTint(Utils.getColor(context, R.color.colorAccent))
                } else {
                    drawableLeft.setTint(Utils.getColor(context, R.color.dolphin))
                }
            }
            updateHintText(hasFocus)

            if (mFocusChangeListener != null) {
                mFocusChangeListener?.onFocusChange(v, hasFocus)
            }
        })
        super.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateHintText(isFocused)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

    }

    private fun updateHintText(hasFocus: Boolean) {
        if (mInputTextLayout != null) {
            val hintText = if (!TextUtils.isEmpty(mInputTextLayout.hint)) {
                if (mInputTextLayout.hint == "NULL")
                    ""
                else
                    mInputTextLayout.hint.toString()
            } else ""
            if (!TextUtils.isEmpty(hintText)) {
                if (hasFocus) {
                    mInputTextLayout.hint = hintText?.toUpperCase()
                } else {
                    if (TextUtils.isEmpty(text.toString()) && !mInputTextLayout.mIsErrorShowing)
                        mInputTextLayout.hint = Utils.toCamelCase(hintText)
                    else
                        mInputTextLayout.hint = hintText?.toUpperCase()
                }
            }
        }
    }

    override fun setOnFocusChangeListener(focusChangeListener: OnFocusChangeListener?) {
        mFocusChangeListener = focusChangeListener
    }

    public fun setInputTextLayout(inputTextLayout: CustomShInputTextLayout) {
        mInputTextLayout = inputTextLayout
    }

    public fun showErrorMsg(show: Boolean) {
        mInputTextLayout.showErrorMsg(show)
    }

    public fun setError(errorMsg: String) {
        mInputTextLayout.error = errorMsg
    }
}