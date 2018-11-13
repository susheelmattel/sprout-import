package com.sproutling.common.ui.dialogfragment

import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.widget.TextView
import com.sproutling.common.R

class FirmwareUpdateProgressDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.DialogFragmentPopup)
        dialog.setContentView(R.layout.dialog_firmware_updating_layout)
        mTvProgressUpdate = dialog.findViewById(R.id.updateProgress)
        mTvProgressUpdate?.text = getString(R.string.firmware_update_firmware_status, INITIAL_PROGRESS.toString() + PERCENT)
        dialog.setCanceledOnTouchOutside(false)
        isCancelable = false
        return dialog
    }

    fun setFirmwareUpdateProgress(progress: String) {
        mTvProgressUpdate?.text = progress
    }

    private var mTvProgressUpdate: TextView? = null

    companion object {
        private const val INITIAL_PROGRESS = 0
        private const val PERCENT = "%"
    }
}