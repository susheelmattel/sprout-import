package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

/**
 * Created by subram13 on 1/19/18.
 */
class WhyDateDialogFragment : BaseDialogFragment() {
    override fun getButton2Text(): String {
        return getString(R.string.empty)
    }

    override fun getButton2Visibility(): Boolean {
        return !TextUtils.isEmpty(getButton2Text())
    }

    override fun getImage(): Int {
        return R.drawable.ic_ask_date
    }

    override fun getTitle(): String {
        return getString(R.string.child_profile_dialog_fragment_title)
    }

    override fun getMessage(): String {
        return getString(R.string.child_profile_birthday_hint)
    }

    override fun getButtonText(): String {
        return getString(R.string.child_profile_got_it)
    }
}