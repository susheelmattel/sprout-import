/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.broadcast;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.sproutling.object.PushNotificationChannelEvent;
import com.sproutling.object.PushNotificationData;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by subram13 on 3/14/17.
 */

public class PushNotificationReceiver extends PushMessageReceiver {
    private static final String TAG = PushNotificationReceiver.class.getSimpleName();

    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        if (errorCode == 0) {
            Log.d(TAG, "appid" + appid);
            Log.d(TAG, "userId" + userId);
            Log.e(TAG, "channelId" + channelId);
            Log.d(TAG, "requestId" + requestId);
        }
        //TODO: set the channel ID to server

        EventBus.getDefault().post(new PushNotificationChannelEvent(channelId));
//        CreateHandheldRequestBody createHandheldRequestBody = new
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

        Log.d(TAG,  "onMessage: " + s + " : " + s1);
        if (!TextUtils.isEmpty(s)) {
            PushNotificationData pushNotificationData = Utils.toObjectFromJson(PushNotificationData.class, s);
            Utils.sendNotification(context,pushNotificationData.getTitle(),pushNotificationData.getBody());
        }

    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {

    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}
