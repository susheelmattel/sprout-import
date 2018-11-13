package com.sproutling.common.ui.dialogfragment

import android.app.Dialog

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.view.View
import com.sproutling.common.R
import com.sproutling.common.ui.view.BaseView
import com.sproutling.common.ui.widget.NetworkAlertLayout
import com.sproutling.common.utils.Utils

/**
 * Created by subram13 on 1/8/18.
 */
class BaseNetworkDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity, R.style.DialogFragmentPopup)

       var  networkAlertView = NetworkAlertLayout(activity)
        networkAlertView.setImgAlert(R.drawable.ic_internet_offline)
        networkAlertView.setTitle(getString(R.string.internet_offline_title))
        networkAlertView.setMessage(getString(R.string.internet_offline_message))
        networkAlertView.setButtonText(getString(R.string.try_again))
        networkAlertView.setButton2Text(getString(R.string.internet_offline_button))
        val textColor = getButtonTextColor()
        if (textColor != null) {
            networkAlertView.setButtonTextColor(textColor)
        }
        networkAlertView.setButtonClickListener(View.OnClickListener {

            networkAlertView.setLayoutVisibility(true)

            Handler().postDelayed({
                run {
                    if (!Utils.isNetworkAvailable(activity)) {
                        networkAlertView.setLayoutVisibility(false)
                    }
                }
            }, BaseView.NETWORK_RETRY_TIME)
        })

        networkAlertView.setButton2ClickListener(View.OnClickListener {
            dismiss()
        })
        dialog.setContentView(networkAlertView)
        dialog.setCanceledOnTouchOutside(false)
        isCancelable = false

        return dialog
    }

    protected fun getButtonTextColor(): Int? {
        return null
    }
}