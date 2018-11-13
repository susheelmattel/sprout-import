package com.sproutling.common.ui.dialogfragment

import com.sproutling.common.R

class FirmwareUpdateRetryDialogFragment : BaseDialogFragment() {
    override fun getImage(): Int {
        return R.drawable.ic_firmware_update_modal_red
    }

    override fun getTitle(): String {
        return getString(R.string.firmware_update_failed_title)
    }

    override fun getMessage(): String {
        return getString(R.string.firmware_update_failed_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.firmware_update_failed)
    }

    override fun getButton2Text(): String {
        return getString(R.string.cancel_firmware)
    }

    override fun getButton2Visibility(): Boolean {
        return true
    }
}