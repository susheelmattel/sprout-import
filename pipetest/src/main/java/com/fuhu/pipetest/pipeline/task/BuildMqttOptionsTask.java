package com.fuhu.pipetest.pipeline.task;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.task.BuildMqttClientTask;
import com.fuhu.pipeline.util.SSLUtil;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

/**
 * Created by allanshih on 2017/3/1.
 */

public class BuildMqttOptionsTask extends APipeTask {
    private static final String TAG = BuildMqttClientTask.class.getSimpleName();

    /**
     * The pipeline task processing function. Implement our processing here.
     * @param pipeItem PipeItem
     */
    @Override
    public void process(IPipeItem pipeItem) {
        if (pipeItem != null && pipeItem instanceof MqttItem) {
            MqttItem mqttItem = (MqttItem) pipeItem;

            // Get default MqttConnectOptions
            MqttConnectOptions mqttConnectOptions = mqttItem.getMqttConnectOptions();
            if (mqttConnectOptions == null) {
                mqttConnectOptions = new MqttConnectOptions();
            }

            // TODO Using trust certificates
            // Trust all certificates
            SSLContext sslContext = SSLUtil.getAllTrustSSLContext();

            if (sslContext != null) {
                // Get SocketFactory from SSLContext.
                SocketFactory socketFactory = sslContext.getSocketFactory();
                if (socketFactory != null) {

                    // Update new connect options.
                    mqttConnectOptions.setSocketFactory(socketFactory);
                    mqttItem.setMqttConnectOptions(mqttConnectOptions);
                } else {
                    PipeLog.w(TAG, "SocketFactory is null.");
                }
            } else {
                PipeLog.w(TAG, "SSLContext is null.");
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