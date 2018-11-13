package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

/**
 * Created by subram13 on 1/4/18.
 */
class InvalidVerificationCodeDialogFragment : BaseDialogFragment() {
    override fun getButton2Text(): String {
        return getString(R.string.empty)
    }

    override fun getButton2Visibility(): Boolean {
        return !TextUtils.isEmpty(getButton2Text())
    }

    override fun getImage(): Int {
        return R.drawable.ic_error
    }

    override fun getTitle(): String {
        return getString(R.string.pin_validation_invalid_code)
    }

    override fun getMessage(): String {
        return getString(R.string.pin_validation_invalid_code_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.pin_validation_got_it)
    }
}