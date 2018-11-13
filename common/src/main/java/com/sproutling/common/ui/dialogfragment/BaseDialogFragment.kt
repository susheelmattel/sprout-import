package com.sproutling.common.ui.dialogfragment

import android.app.Dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.sproutling.common.R
import com.sproutling.common.ui.widget.ShAlertView

/**
 * Created by subram13 on 1/8/18.
 */
abstract class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.DialogFragmentPopup)

        val shAlertView = ShAlertView(activity)
        shAlertView.setImgAlert(getImage())
        shAlertView.setTitle(getTitle())
        shAlertView.setMessage(getMessage())
        shAlertView.setButtonText(getButtonText())
        shAlertView.setButton2Text(getButton2Text())
        shAlertView.setButton2Visibility(getButton2Visibility())
        var textColor = getButtonTextColor()
        if (textColor != null) {
            shAlertView.setButtonTextColor(textColor)
        }
        shAlertView.setButtonClickListener(
                if (mOnClickListener != null) {
                    mOnClickListener
                } else {
                    View.OnClickListener { dismiss() }
                }
        )

        shAlertView.setButton2ClickListener(
                if (mOnNegativeClickListener != null) {
                    mOnNegativeClickListener
                } else {
                    View.OnClickListener { dismiss() }
                }
        )
        dialog.setContentView(shAlertView)
        dialog.setCanceledOnTouchOutside(false)
        isCancelable = false

        return dialog
    }

    private var mOnClickListener: View.OnClickListener? = null
    private var mOnNegativeClickListener: View.OnClickListener? = null
    abstract fun getImage(): Int
    abstract fun getTitle(): String
    abstract fun getMessage(): String
    abstract fun getButtonText(): String
    abstract fun getButton2Text(): String?
    abstract fun getButton2Visibility(): Boolean
    protected open fun getButtonTextColor(): Int? {
        return null
    }

    fun setOnClickListener(onClickListener: View.OnClickListener) {
        mOnClickListener = onClickListener
    }

    fun setOnNegativeClickListener(onClickListener: View.OnClickListener) {
        mOnNegativeClickListener = onClickListener
    }
}