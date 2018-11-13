package com.sproutling.common.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import com.fisherprice.api.models.file_transfer.FPFileTransferInfo.*
import com.sproutling.common.ui.presenter.interfaces.IBaseBLEFragmentPresenter
import com.sproutling.common.ui.view.interfaces.IBaseBLEFragmentView

abstract class BaseBLEFragmentView : BaseFragmentView(), IBaseBLEFragmentView {


    private inner class BLEActionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "BLEActionsReceiver : onReceive")
            if (TextUtils.isEmpty(intent.action)) {
                return
            }
            Log.d(TAG, "BLEActionsReceiver : onReceive" + intent.action)
            //pass the value to presenter
            mBLEFragmentPresenter.processBLEBroadcastMessage(intent.action, intent.extras)
        }
    }

    private inner class OTAActionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "OTAActionsReceiver : onReceive")
            if (TextUtils.isEmpty(intent.action)) {
                return
            }
            Log.d(TAG, "OTAActionsReceiver : onReceive" + intent.action)
            if (intent.extras != null) {
                Log.d(TAG, intent.extras.toString())
            }
            //pass the value to presenter
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity).registerReceiver(mBLEReceiver, getBLEFilters())
        LocalBroadcastManager.getInstance(activity).registerReceiver(mOTAReceiver, getOTAFilters())
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mBLEReceiver)
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mOTAReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBLEFragmentPresenter = getBaseBLEFragmentPresenter()
    }

    companion object {
        const val TAG = "BaseBLEFragmentView"
    }

    private fun getOTAFilters(): IntentFilter {
        val otaIntentFilter = IntentFilter()
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_TRANSFER_PROGRESS_NOTIF)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_TRANSFER_FINISHED_NOTIF)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_COMPLETE_NOTIF)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_CANCELLING)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_RETRY)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_COMPLETION)
        otaIntentFilter.addAction(FP_FIRMWARE_UPDATE_TRANSFER_DISCONNECTION)
        return otaIntentFilter
    }

    protected abstract fun getBaseBLEFragmentPresenter(): IBaseBLEFragmentPresenter
    private var mBLEReceiver = BLEActionsReceiver()
    private var mOTAReceiver = OTAActionsReceiver()
    private lateinit var mBLEFragmentPresenter: IBaseBLEFragmentPresenter
}
