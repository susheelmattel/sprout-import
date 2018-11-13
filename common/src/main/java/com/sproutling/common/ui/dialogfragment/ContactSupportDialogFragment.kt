package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

/**
 * Created by Ankur Bhatt on 7/25/2018.
 */

class ContactSupportDialogFragment : BaseDialogFragment() {

    companion object {
        @JvmStatic
        val TAG: String = "SupportDialogFragment"
    }

    override fun getButton2Text(): String {
        return getString(R.string.child_profile_cancel)
    }

    override fun getButton2Visibility(): Boolean {
        return !TextUtils.isEmpty(getButton2Text())
    }

    override fun getImage(): Int {
        return R.drawable.ic_support_icon
    }

    override fun getTitle(): String {
        return getString(R.string.contact_support)
    }

    override fun getMessage(): String {
        return getString(R.string.contact_support_details)
    }

    override fun getButtonText(): String {
        return getString(R.string.call)
    }

}