package com.sproutling.common.ui.dialogfragment

import android.support.v4.content.ContextCompat
import com.sproutling.common.R

class LogoutDialogFragment : BaseDialogFragment() {
    override fun getImage(): Int {
        return R.drawable.ic_error
    }

    override fun getTitle(): String {
        return getString(R.string.logout_modal_title)
    }

    override fun getMessage(): String {
        return getString(R.string.logout_modal_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.logout_modal_logOutButton)
    }

    override fun getButton2Text(): String {
        return getString(R.string.logout_modal_cancelButton)
    }

    override fun getButton2Visibility(): Boolean {
        return true
    }

    protected override fun getButtonTextColor(): Int? {
        return ContextCompat.getColor(context, R.color.copper)
    }
}