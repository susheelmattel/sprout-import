package com.sproutling.common.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.sproutling.common.utils.Utils
import com.sproutling.events.ServiceNetworkEvent
import org.greenrobot.eventbus.EventBus

class NetworkChangeReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null && ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                EventBus.getDefault().post(ServiceNetworkEvent(Utils.isNetworkAvailable(context)))
        }
    }

}