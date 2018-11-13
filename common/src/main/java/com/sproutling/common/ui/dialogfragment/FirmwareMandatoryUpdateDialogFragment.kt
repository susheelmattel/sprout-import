package com.sproutling.common.ui.dialogfragment

import com.sproutling.common.R

class FirmwareMandatoryUpdateDialogFragment : BaseDialogFragment() {
    override fun getImage(): Int {
        return R.drawable.ic_firmware_update_modal
    }

    override fun getTitle(): String {
        return getString(R.string.firmware_update_required_title)
    }

    override fun getMessage(): String {
        return getString(R.string.firmware_update_required_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.firmware_update_availble)
    }

    override fun getButton2Text(): String {
        return ""
    }

    override fun getButton2Visibility(): Boolean {
        return false
    }

}