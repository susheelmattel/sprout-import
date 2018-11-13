package com.sproutling.common.ui.dialogfragment

import android.support.v4.content.ContextCompat
import com.sproutling.common.R

class RemoveProductDialog : BaseDialogFragment() {

    override fun getImage(): Int {
        return R.drawable.ic_error
    }

    override fun getTitle(): String {
        return getString(R.string.remove_device_title)
    }

    override fun getMessage(): String {
        return getString(R.string.remove_device_body)
    }

    override fun getButtonText(): String {
        return getString(R.string.remove_device_yes)
    }

    override fun getButton2Text(): String {
        return getString(R.string.remove_device_cancel)
    }

    override fun getButton2Visibility(): Boolean {
        return true
    }

    protected override fun getButtonTextColor(): Int? {
        return ContextCompat.getColor(context, R.color.copper)
    }

}