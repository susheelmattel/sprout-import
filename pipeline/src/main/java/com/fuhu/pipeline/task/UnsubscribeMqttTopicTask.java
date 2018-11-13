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

import java.util.Arrays;
import java.util.Set;

public class UnsubscribeMqttTopicTask extends APipeTask {
    private static final String TAG = UnsubscribeMqttTopicTask.class.getSimpleName();

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

                if (topicList != null) {
                    for (String topic : topicList) {
                        PipeLog.d(TAG, "unsubscribe: " + topic);
                    }
                } else {
                    PipeLog.d(TAG, "Get the cached topic list from MqttManager");
                    Set<String> topics = MqttManager.getInstance().getSubscriptionMap().keySet();
                    if (topics != null && topics.size() > 0) {
                        for (String topic : topics) {
                            PipeLog.d(TAG, "topic: " + topic);
                        }
                        topicList = Arrays.copyOf(topics.toArray(), topics.size(), String[].class);
                    }
                }

                if (topicList != null && topicList.length > 0) {
                    try {
                        // Requests the server to unsubscribe the client from one or more topics.
                        IMqttToken mqttToken = client.unsubscribe(topicList);

                        // Set an MqttToken to MqttItem.
                        mqttItem.setMqttToken(mqttToken);
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