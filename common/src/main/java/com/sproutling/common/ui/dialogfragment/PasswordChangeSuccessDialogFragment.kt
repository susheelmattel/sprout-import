package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

/**
 * Created by subram13 on 1/8/18.
 */
class PasswordChangeSuccessDialogFragment : BaseDialogFragment() {
    override fun getButton2Text(): String {
        return getString(R.string.empty)
    }

    override fun getButton2Visibility(): Boolean {
        return !TextUtils.isEmpty(getButton2Text())
    }

    override fun getImage(): Int {
        return R.drawable.ic_success_dialog
    }

    override fun getTitle(): String {
        return getString(R.string.password_changed_title)
    }

    override fun getMessage(): String {
        return getString(R.string.password_changed_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.password_changed_got_it)
    }
}