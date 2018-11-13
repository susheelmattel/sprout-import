package com.fuhu.pipetest.pipeline.callback;

import android.util.Log;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.mqtt.MqttItem;

public class UnsubscribeMqttCallback implements IPipeCallback {
    private static final String TAG = UnsubscribeMqttCallback.class.getSimpleName();
    /**
     * When pipeline is finished.
     * @param responseObject output data
     */
    public void onResult(final Object responseObject) {
        Log.d(TAG, "onResult: " + responseObject.getClass().getSimpleName());
        if (responseObject instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) responseObject;
            Log.d(TAG, "Action type: " + mqttItem.getActionType());
        }
    }

    /**
     * This callback is invoked when there is a pipeline execution error.
     * @param status error
     * @param pipeItem errorItem
     */
    public void onError(final int status, final IPipeItem pipeItem) {
        if (pipeItem != null) {
            Log.d(TAG, "Error: " + pipeItem.getErrorMessage());
        }
    }
}