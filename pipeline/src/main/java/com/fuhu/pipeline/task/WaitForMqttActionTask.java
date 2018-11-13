package com.fuhu.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class WaitForMqttActionTask extends APipeTask {
    private static final String TAG = WaitForMqttActionTask.class.getSimpleName();
    private boolean isDone;

    public WaitForMqttActionTask() {
        isDone = false;
    }

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(final IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;
            IMqttToken mqttToken = mqttItem.getMqttToken();

            // Checks if task has been completed.
            if (PipeStatus.isSuccess(mqttItem.getPipeStatus())) {
                PipeLog.d(TAG, "task has been completed");
                isDone = true;
                return;
            }

            if (mqttToken != null) {
                mqttToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        PipeLog.d(TAG, "MQTT action success.");

                        // Checks if this task is the last task.
                        if (getNextTask() == null) {
                            pipeItem.setPipeStatus(PipeStatus.SUCCESS);
                        }
                        isDone = true;
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        exception.printStackTrace();
                        PipeLog.d(TAG, "MQTT action faild." + exception.getMessage());
                        pipeItem.setPipeStatus(PipeStatus.MQTT_EXCEPTION);
                        pipeItem.setErrorMessage(exception.getMessage());
                        isDone = true;
                    }
                });
            } else {
                if (mqttItem.getPipeStatus() != PipeStatus.DEFAULT) {
                    mqttItem.setPipeStatus(PipeStatus.MQTT_TOKEN_NULL);
                    mqttItem.setErrorMessage("MQTT token is null.");
                }
                // Do next task.
                isDone = true;
            }
        }
    }

    /**
     * Returns true if this task completed. Completion may be due to normal termination,
     * an exception -- in all of these cases, this method will return true.
     * @return true if this task completed
     */
    public boolean isDone() {
        return isDone;
    }
}