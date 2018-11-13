/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.states.Actions;
import com.sproutling.ui.widget.NotificationLayout;

/**
 * Created by xylonchen on 2017/3/3.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bData = intent.getExtras();
        if ("log_sleep_alarm".equals(bData.get("msg"))) {
            App.getInstance().dispatchAction(Actions.CALL_NOTIFICATION, new Payload().put(Actions.Key.NOTIFICATION, NotificationLayout.Type.ADVICE_LOG_SLEEP));
        } else if ("timeline_alarm".equals(bData.get("msg"))) {
            App.getInstance().dispatchAction(Actions.CALL_NOTIFICATION, new Payload().put(Actions.Key.NOTIFICATION, NotificationLayout.Type.TIMELINE));
        }
    }
}