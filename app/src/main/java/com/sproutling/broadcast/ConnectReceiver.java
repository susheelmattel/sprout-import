/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.states.Actions;
import com.sproutling.states.States;

import sproutling.EventOuterClass;

/**
 * Created by Xylon on 2017/1/20.
 */

public class ConnectReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectReceiver.class.getSimpleName();
    private NetworkInfo.State mWifiState = null;
    private NetworkInfo.State mMobileState = null;

    //when wifi is disconnect, the onReceive will do twice. So add this flag to check.
    public static boolean isAlreadyNoService = false;
    //When App First Launch, the onReceive will do once. So add this flag to check.
    public static boolean isFirstLaunch = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null)
                mWifiState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null)
                mMobileState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if (mWifiState != null && mMobileState != null && NetworkInfo.State.CONNECTED != mWifiState && NetworkInfo.State.CONNECTED == mMobileState) {
                Log.d(TAG, "mobile internet connected.");

                //Action the RECONNECT_MQTT to goto preStatus UI view.
                if (isFirstLaunch) {
                    isFirstLaunch = false;
                } else {
                    App.getInstance().dispatchAction(Actions.RECONNECT_MQTT, new Payload(), context);
                }
            } else if (mWifiState != null && mMobileState != null && NetworkInfo.State.CONNECTED == mWifiState && NetworkInfo.State.CONNECTED != mMobileState) {
                Log.d(TAG, "wifi connected.");

                //Action the RECONNECT_MQTT to goto preStatus UI view.
                if (isFirstLaunch) {
                    isFirstLaunch = false;
                } else {
                    App.getInstance().dispatchAction(Actions.RECONNECT_MQTT, new Payload(), context);
                }

            } else if (mWifiState != null && mMobileState != null && NetworkInfo.State.CONNECTED != mWifiState && NetworkInfo.State.CONNECTED != mMobileState && !isAlreadyNoService) {
//                Toast.makeText(context, "no service", Toast.LENGTH_SHORT).show();
                isAlreadyNoService = true;
                Log.d(TAG, "no service");

                //Action the STATUS_UPDATE to goto No Service UI view.
                EventOuterClass.Event noServiceEvent = EventOuterClass.Event.newBuilder(EventOuterClass.Event.getDefaultInstance()).setEvent(EventOuterClass.EventType.NO_SERVICE).build();
                App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, noServiceEvent), context);
            }
        }
    }
}
