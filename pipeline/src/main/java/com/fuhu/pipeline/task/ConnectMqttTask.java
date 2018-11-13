package com.fuhu.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ConnectMqttTask extends APipeTask {
    private static final String TAG = ConnectMqttTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;
            MqttAndroidClient client = mqttItem.getMqttAndroidClient();
            MqttConnectOptions options = mqttItem.getMqttConnectOptions();
            options.setAutomaticReconnect(true);

            if (client != null) {
                try {
                    // Determines if this client is currently connected to the server.
                    if (client.isConnected()) {
                        PipeLog.d(TAG, "MQTT connected.");
                        mqttItem.setPipeStatus(PipeStatus.SUCCESS);
                    } else {
                        IMqttToken mqttToken;

                        PipeLog.d(TAG, "Connect to : " + client.getServerURI());
                        // Connects to an MQTT server using the provided connect options.
                        if (options != null) {
                            mqttToken = client.connect(options);
                        } else {
                            // Connects to an MQTT server using the default options.
                            mqttToken = client.connect();
                        }

                        // Update MqttItem.
                        mqttItem.setMqttToken(mqttToken);
                    }
                } catch (MqttException me) {
                    me.printStackTrace();
                    mqttItem.setPipeStatus(PipeStatus.MQTT_EXCEPTION);
                    mqttItem.setErrorMessage(me.getMessage());
                }
            } else {
                mqttItem.setPipeStatus(PipeStatus.MQTT_CLIENT_NULL);
                mqttItem.setErrorMessage("MQTT client is null.");
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