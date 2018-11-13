package com.fuhu.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.manager.MqttManager;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

public class SubscribeMqttTopicTask extends APipeTask {
    private static final String TAG = SubscribeMqttTopicTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;
            MqttAndroidClient client = mqttItem.getMqttAndroidClient();

            // Checks if the mqtt broker is connected.
            if (client != null && client.isConnected()) {
                String[] topicList = mqttItem.getTopicList();
                int[] qosList = mqttItem.getQosList();

                if (topicList != null && topicList.length > 0) {
                    try {
                        // Subscribe to multiple topics, each topic may include wildcards.
                        IMqttToken mqttToken = client.subscribe(topicList, qosList);

                        // Set an MqttToken to MqttItem.
                        mqttItem.setMqttToken(mqttToken);

                        // Add subscriptions to MqttManager.
                        if (topicList != null && qosList != null && topicList.length == qosList.length) {
                            for (int i = 0; i < topicList.length; i++) {
                                PipeLog.d(TAG, "Subscribe: " + topicList[i] + " qos: " + qosList[i]);
                                MqttManager.getInstance().addSubscription(topicList[i], qosList[i]);
                            }
                        }
                    } catch (MqttException me) {
                        me.printStackTrace();
                        mqttItem.setPipeStatus(PipeStatus.MQTT_EXCEPTION);
                        mqttItem.setErrorMessage(me.getMessage());
                    }
                } else {
                    mqttItem.setPipeStatus(PipeStatus.MQTT_TOPIC_NULL);
                    mqttItem.setErrorMessage("No mqtt topics.");
                }
            } else {
                mqttItem.setPipeStatus(PipeStatus.COULD_NOT_CONNECT_MQTT);
                mqttItem.setErrorMessage("Couldn't connect to MQTT broker.");
            }
        }
    }

    /**
     * Returns true if this task completed. Completion may be due to normal termination,
     * an exception -- in all of these cases, this method will return true.
     * @return true if this task completed
     */
    public boolean isDone() {
        return true;
    }
}