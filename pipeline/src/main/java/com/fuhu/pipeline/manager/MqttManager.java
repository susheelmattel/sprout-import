package com.fuhu.pipeline.manager;

import android.content.Context;

import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.Subscription;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.util.HashMap;
import java.util.Map;

public class MqttManager {
    private static final String TAG = MqttManager.class.getSimpleName();
    private static MqttManager INSTANCE;
    private MqttAndroidClient mqttAndroidClient;
    private MqttCallback mqttCallback;

    /** The list of the subscriptions **/
    private Map<String, Subscription> mSubscriptionMap;

    /**
     * Default constructor.
     */
    private MqttManager() {
        mSubscriptionMap = new HashMap<>();
    }

    /**
     * Get an instance of MqttManager.
     * @return MqttManager
     */
    public static MqttManager getInstance() {
        if (INSTANCE == null){
            synchronized(MqttManager.class) {
                if(INSTANCE == null) {
                    INSTANCE = new MqttManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * create an MqttAndroidClient that can be used to communicate with an MQTT server on android.
     * @param serverUri server url
     * @param clientId client id
     */
    public void init(final Context context, final String serverUri, final String clientId, final MqttCallback mqttCallback) {
        PipeLog.d(TAG, "init: " + serverUri + " client id: " + clientId);
        if (context != null && serverUri != null && clientId != null) {
            mqttAndroidClient = new MqttAndroidClient(context.getApplicationContext(), serverUri, clientId);

            // Sets a callback listener to use for events that happen asynchronously.
            this.mqttCallback = mqttCallback;
            mqttAndroidClient.setCallback(mqttCallback);
        }
    }

    /**
     * Get an MqttAndroidClient.
     * @return MqttAndroidClient
     */
    public MqttAndroidClient getMqttClient() {
        return mqttAndroidClient;
    }

    /**
     * Determines if this client is currently connected to the server.
     *
     * @return <code>true</code> if connected, <code>false</code> otherwise.
     */
    public boolean isConnected() {
        if (mqttAndroidClient != null) {
            return mqttAndroidClient.isConnected();
        }
        return false;
    }

    /**
     * Get the subscription map.
     * @return mqtt subscription map
     */
    public Map<String, Subscription> getSubscriptionMap() {
        return mSubscriptionMap;
    }

    /**
     * Clear the subscription map.
     */
    public void clearSubscriptionMap() {
        synchronized (mSubscriptionMap) {
            mSubscriptionMap.clear();
        }
    }

    /**
     * Add subscription to the subscription map.
     * @param topic mqtt topic
     * @param qos mqtt qos
     */
    public void addSubscription(final String topic, final int qos) {
        if (topic != null) {
            Subscription subscription = new Subscription(topic, qos);
            addSubscription(subscription);
        }
    }

    /**
     * Add subscription to the subscription map.
     * @param subscription mqtt subscription
     */
    public void addSubscription(final Subscription subscription) {
        if (subscription != null && subscription.getTopic() != null) {
            synchronized (mSubscriptionMap) {
                mSubscriptionMap.put(subscription.getTopic(), subscription);
            }
        }
    }

    /**
     * Remove subscription from the subscription map.
     * @param topic mqtt topic
     */
    public void removeSubscription(final String topic) {
        if (topic != null) {
            synchronized (mSubscriptionMap) {
                mSubscriptionMap.remove(topic);
            }
        }
    }

    /**
     * Remove subscription from the subscription map.
     * @param subscription mqtt subscription
     */
    public void removeSubscription(final Subscription subscription) {
        if (subscription != null && subscription.getTopic() != null) {
            removeSubscription(subscription.getTopic());
        }
    }

    /**
     * Close the mqtt client.
     */
    public void shutdown() {
        if (mSubscriptionMap != null) {
            mSubscriptionMap.clear();
            mSubscriptionMap = null;
        }

        if (mqttAndroidClient != null) {
            try {
                if (mqttAndroidClient.isConnected()) {
                    mqttAndroidClient.disconnect(null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            mqttAndroidClient.close();
                            mqttAndroidClient = null;
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            mqttAndroidClient.close();
                            mqttAndroidClient = null;
                        }
                    });
                } else {
                    mqttAndroidClient.close();
                    mqttAndroidClient = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mqttCallback = null;
    }

    /**
     * Destroy object.
     */
    public static void release() {
        if (INSTANCE != null) {
            INSTANCE.shutdown();
            INSTANCE = null;
        }
    }
}
