package com.sproutling.common.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import com.sproutling.common.ui.presenter.interfaces.IBaseBLEFragmentPresenter
import com.sproutling.common.ui.presenter.interfaces.IBaseOTAFragmentPresenter
import com.sproutling.common.ui.view.BaseBLEFragmentView
import com.sproutling.common.ui.view.BaseFragmentView
import com.sproutling.common.ui.view.interfaces.IBaseOTAFragmentView

/**
 * Created by 322511 on 8/3/18.
 */
abstract class BaseOTAFragmentView : BaseBLEFragmentView() , IBaseOTAFragmentView {

    private inner class OTAActionsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "OTAActionsReceiver : onReceive")
            if (TextUtils.isEmpty(intent.action)) {
                return
            }
            Log.d(TAG, "OTAActionsReceiver : onReceive" + intent.action)
            //pass the value to presenter


            mFirmwareFragmentPresenter.processFirmwareBroadcastMessage(intent.action, intent.extras)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity).registerReceiver(mOTAReceiver, getFirmwareFilters())
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(mOTAReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirmwareFragmentPresenter = getBaseFirmwareFragmentPresenter()
    }

    companion object {
        const val TAG = "BaseOTAFragmentView"
    }


    protected abstract fun getBaseFirmwareFragmentPresenter(): IBaseOTAFragmentPresenter
    private var mOTAReceiver = OTAActionsReceiver()
    private lateinit var mFirmwareFragmentPresenter: IBaseOTAFragmentPresenter
}
