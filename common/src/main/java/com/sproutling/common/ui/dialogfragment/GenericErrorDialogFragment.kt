package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

/**
 * Created by subram13 on 1/11/18.
 */
class GenericErrorDialogFragment : BaseDialogFragment() {
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
        return getString(R.string.generic_error_title)
    }

    override fun getMessage(): String {
        return getString(R.string.generic_error_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.generic_error_got_it)
    }
}