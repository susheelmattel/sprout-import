package com.fuhu.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Map;

public class PublishMqttMessageTask extends APipeTask {
    private static final String TAG = PublishMqttMessageTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     *
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;
            MqttAndroidClient client = mqttItem.getMqttAndroidClient();

            // Checks if the mqtt broker is connected.
            if (client != null && client.isConnected()) {
                Map<String, Integer> topicMap = mqttItem.getMqttTopics();

                // Checks if the topicMap is exist.
                if (topicMap != null && topicMap.size() > 0) {
                    String topic = mqttItem.getTopicList()[0];
                    int qos = mqttItem.getQosList()[0];
                    byte[] payload = mqttItem.getPayload();

                    // Checks if the request json object is exist.
                    if (payload != null) {
                        try {
                            // Publishes a message to a topic on the server.
                            IMqttToken mqttToken = client.publish(
                                    topic,
                                    payload,
                                    qos,
                                    mqttItem.isRetained()
                            );

                            // Set an MqttToken to MqttItem.
                            mqttItem.setMqttToken(mqttToken);
                        } catch (MqttException me) {
                            me.printStackTrace();
                            mqttItem.setPipeStatus(PipeStatus.MQTT_EXCEPTION);
                            mqttItem.setErrorMessage(me.getMessage());
                        }
                    } else {
                        mqttItem.setPipeStatus(PipeStatus.MQTT_MESSAGE_NULL);
                        mqttItem.setErrorMessage("MQTT message is null.");
                    }
                } else {
                    mqttItem.setPipeStatus(PipeStatus.MQTT_TOPIC_NULL);
                    mqttItem.setErrorMessage("MQTT topic is null.");
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
     *
     * @return true if this task completed
     */
    public boolean isDone() {
        return true;
    }
}