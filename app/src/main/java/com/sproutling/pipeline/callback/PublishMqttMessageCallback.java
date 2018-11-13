/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.pipeline.callback;

import android.util.Log;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.mqtt.MqttItem;

import sproutling.Hub;

public class PublishMqttMessageCallback implements IPipeCallback {
    private static final String TAG = PublishMqttMessageCallback.class.getSimpleName();

    /**
     * When pipeline is finished.
     *
     * @param responseObject output data
     */
    public void onResult(final Object responseObject) {
        Log.d(TAG, "onResult: " + responseObject.getClass().getSimpleName());

        if (responseObject instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) responseObject;
            for (String topic : mqttItem.getTopicList()) {
                Log.d(TAG, "Message Publish success: " + topic);
            }

            Log.d(TAG, "Action type: " + mqttItem.getActionType());
            try {
                Hub.HubControl hubControl = Hub.HubControl.newBuilder().mergeFrom(mqttItem.getPayload().clone()).build();
            } catch (Exception ex) {
                String str = ex.getMessage();
            }


        }
    }

    /**
     * This callback is invoked when there is a pipeline execution error.
     *
     * @param status   error
     * @param pipeItem errorItem
     */
    public void onError(final int status, final IPipeItem pipeItem) {
        Log.d(TAG, "Message Publish failed with status :" + String.valueOf(status));
        if (pipeItem != null) {
            Log.d(TAG, "Error: " + pipeItem.getErrorMessage());
        }
    }
}