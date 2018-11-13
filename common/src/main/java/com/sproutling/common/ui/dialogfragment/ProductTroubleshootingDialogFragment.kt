package com.sproutling.common.ui.dialogfragment

import android.text.TextUtils
import com.sproutling.common.R

class ProductTroubleshootingDialogFragment : BaseDialogFragment() {

    companion object {
        @JvmStatic val TAG :String = "ProductTroubleshootingDialogFragment"
    }

    override fun getButton2Text(): String {
        return getString(R.string.device_troubleshoot_see_faq)
    }

    override fun getButton2Visibility(): Boolean {
        return !TextUtils.isEmpty(getButton2Text())
    }

    override fun getImage(): Int {
        return R.drawable.ic_power_dialog_icon
    }

    override fun getTitle(): String {
        return getString(R.string.device_troubleshoot_turn_product_on)
    }

    override fun getMessage(): String {
        return getString(R.string.device_troubleshoot_instructions)
    }

    override fun getButtonText(): String {
        return getString(R.string.device_troubleshoot_got_it)
    }

}