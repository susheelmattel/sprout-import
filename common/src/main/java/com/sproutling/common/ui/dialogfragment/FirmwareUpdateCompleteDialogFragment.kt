package com.sproutling.common.ui.dialogfragment

import com.sproutling.common.R

class FirmwareUpdateCompleteDialogFragment : BaseDialogFragment() {
    override fun getImage(): Int {
        return R.drawable.ic_firmware_update_modal
    }

    override fun getTitle(): String {
        return getString(R.string.firmware_update_complete_title)
    }

    override fun getMessage(): String {
        return getString(R.string.firmware_update_complete_message)
    }

    override fun getButtonText(): String {
        return getString(R.string.firmware_update_complete)
    }

    override fun getButton2Text(): String {
        return getString(R.string.empty)
    }

    override fun getButton2Visibility(): Boolean {
        return false
    }
}