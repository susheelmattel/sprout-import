package com.fuhu.pipeline.mqtt;

import android.content.Context;
import android.util.Log;

import com.fuhu.pipeline.PipelineConfig;
import com.fuhu.pipeline.contract.APipeItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MqttItem extends APipeItem {
    private static final String TAG = MqttItem.class.getSimpleName();
    private int actionType;
    private String payloadString;

    private MqttAndroidClient mqttAndroidClient;
    private IMqttActionListener mqttActionListener;
    private IMqttToken mqttToken;
    private MqttConnectOptions mqttConnectOptions;
    private Map<String, Integer> mqttTopics;
    private String[] topicList;
    private int[] qosList;
    private byte[] payload;
    private boolean mRetained = false;

    /**
     * Default constructor.
     */
    public MqttItem() {
        super();
    }

    /**
     * Constructor with a type and payload.
     *
     * @param builder
     */
    public MqttItem(final Builder builder) {
        super(builder.context, builder.payloadJson, builder.payloadObject, builder.timeout);
        this.actionType = builder.actionType;
        this.payloadString = builder.payloadString;
        this.mqttConnectOptions = builder.mqttConnectOptions;
        this.mqttTopics = builder.mqttTopics;
        this.payload = builder.payload;
        this.mRetained = builder.mRetained;
        convertTopicMap(mqttTopics);
    }

    public boolean isRetained() {
        return mRetained;
    }

    /**
     * Get the MQTT action type of this MqttItem.
     */
    public int getActionType() {
        return actionType;
    }

    /**
     * Set the MQTT action type of this MqttItem.
     */
    public void setActionType(final int actionType) {
        this.actionType = actionType;
    }

    /**
     * Get the request string of this pipeItem.
     */
    public String getPayloadString() {
        return payloadString;
    }

    /**
     * Set the request string of this pipeItem.
     */
    public void setPayloadString(final String payloadString) {
        this.payloadString = payloadString;
    }

    /**
     * Get an MqttAndroidClient from this MqttItem.
     *
     * @return MqttAndroidClient
     */
    public MqttAndroidClient getMqttAndroidClient() {
        return mqttAndroidClient;
    }

    /**
     * Set an MqttAndroidClient into this MqttItem.
     *
     * @param mqttAndroidClient MqttAndroidClient
     */
    public void setMqttAndroidClient(final MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    /**
     * Get an IMqttToken from this MqttItem.
     *
     * @return mqtt token
     */
    public IMqttToken getMqttToken() {
        return mqttToken;
    }

    /**
     * Set an IMqttToken into this MqttItem.
     *
     * @param mqttToken mqtt token
     */
    public void setMqttToken(final IMqttToken mqttToken) {
        this.mqttToken = mqttToken;
    }

    /**
     * Get an MqttActionListener from this MqttItem.
     *
     * @return
     */
    public IMqttActionListener getMqttActionListener() {
        return mqttActionListener;
    }

    /**
     * Set an MqttActionListener into this MqttItem.
     *
     * @param mqttActionListener
     */
    public void setMqttActionListener(final IMqttActionListener mqttActionListener) {
        this.mqttActionListener = mqttActionListener;
    }

    /**
     * Get an MqttConnectOptions from this MqttItem.
     *
     * @return MqttConnectOptions
     */
    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    /**
     * Set an MqttConnectOptions into this MqttItem.
     *
     * @param mqttConnectOptions MqttConnectOptions
     */
    public void setMqttConnectOptions(final MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }

    /**
     * Get the topic from this MqttItem.
     *
     * @return topics
     */
    public Map<String, Integer> getMqttTopics() {
        return mqttTopics;
    }

    /**
     * Set the topic into this MqttItem.
     *
     * @param topics Mqtt topic
     */
    public void setMqttTopics(final Map<String, Integer> topics) {
        this.mqttTopics = topics;
        convertTopicMap(topics);
    }

    /**
     * Get the topic list from TopicMap.
     *
     * @return topicList
     */
    public String[] getTopicList() {
        return topicList;
    }

    /**
     * Get the qos list from TopicMap.
     *
     * @return qosList
     */
    public int[] getQosList() {
        return qosList;
    }

    /**
     * Get the payload from this MqttItem.
     *
     * @return payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Set the payload into this MqttItem.
     *
     * @param payload
     */
    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

    /**
     * Build MqttItem object.
     */
    public static class Builder {
        Context context;
        int actionType;
        String payloadString;
        JSONObject payloadJson;
        Object payloadObject;
        byte[] payload;
        MqttConnectOptions mqttConnectOptions;
        Map<String, Integer> mqttTopics;
        long timeout;
        boolean mRetained = false;

        public Builder(final Context context) {
            if (context == null) throw new IllegalArgumentException("context == null");
            this.context = context;
            this.timeout = PipelineConfig.TIMEOUT_PROCESS;
            this.mqttTopics = new HashMap<>();
        }

        public Builder setRetained(boolean retained) {
            this.mRetained = retained;
            return this;
        }

        public Builder actionType(final int actionType) {
            if (actionType < MqttAction.CONNECT || actionType > MqttAction.DISCONNECT) {
                throw new IllegalArgumentException("Please provide a valid mqtt action Type.");
            }
            this.actionType = actionType;
            return this;
        }

        public Builder topic(final String topic) {
            if (topic == null) throw new IllegalArgumentException("topic == null");
            this.mqttTopics.put(topic, MqttQos.AT_LEAST_ONCE);
            return this;
        }

        public Builder topic(final String topic, final int qos) {
            if (topic == null) throw new IllegalArgumentException("topic == null");
            this.mqttTopics.put(topic, qos);
            return this;
        }

        public Builder topics(final Map<String, Integer> mqttTopics) {
            if (mqttTopics == null) throw new IllegalArgumentException("mqttTopics == null");
            this.mqttTopics.putAll(mqttTopics);
            return this;
        }

        public Builder payload(final String payloadString) {
            if (payloadString == null) throw new IllegalArgumentException("payloadString == null");
            this.payloadString = payloadString;
            return this;
        }

        public Builder payload(final JSONObject payloadJson) {
            if (payloadJson == null) throw new IllegalArgumentException("payloadJson == null");
            this.payloadJson = payloadJson;
            return this;
        }

        public Builder payload(final Object payloadObject) {
            if (payloadObject == null) throw new IllegalArgumentException("payloadObject == null");
            this.payloadObject = payloadObject;
            return this;
        }

        public Builder payload(final byte[] payload) {
            if (payload == null) throw new IllegalArgumentException("payload == null");
            this.payload = payload;
            return this;
        }

        public Builder timeout(final long timeout) {
            this.timeout = timeout;
            return this;
        }

        public MqttItem build() {
            if (context == null) throw new IllegalStateException("context == null");
            if (actionType < MqttAction.CONNECT || actionType > MqttAction.DISCONNECT) {
                throw new IllegalArgumentException("Please provide a valid mqtt actionType.");
            }
            return new MqttItem(this);
        }
    }

    /**
     * Convert topicMap to topicList and qosList.
     *
     * @param topics topicMap
     */
    private void convertTopicMap(final Map<String, Integer> topics) {
        if (topics != null && topics.size() > 0) {
            topicList = new String[topics.size()];
            qosList = new int[topics.size()];

            int index = 0;
            for (Map.Entry<String, Integer> mapEntry : topics.entrySet()) {
                topicList[index] = mapEntry.getKey();
                qosList[index] = mapEntry.getValue();
                Log.d(TAG, "Topic: " + topicList[index] + " qos: " + qosList[index]);
                index++;
            }
        }
    }
}
