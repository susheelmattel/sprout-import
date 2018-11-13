/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.pipeline;


import com.sproutling.BuildConfig;

public class MqttAPI {
    private static final String TCP_PREFIX = "tcp://";
    private static final String SSL_PREFIX = "ssl://";

//    private static final String ENDPOINT = HTTP_PREFIX + "35.163.179.218:8090";
    //    private static final String ENDPOINT = HTTPS_PREFIX + "api-dev.us.sproutlingcloud.com";

    //    private static final String MQTT_BROKER = "ihut-219707358.us-west-2.elb.amazonaws.com:1883";
    public static String MQTT_TLS_BROKER = BuildConfig.MQTT_URL;

    public static final String TOPIC_DEVICE = "device/hub/";
    public static final String TOPIC_SLEEP_PREDICTION = "/sleep/predictions";
    public static final String TOPIC_SLEEP_STATUS = "/sleep/status";
    public static final String TOPIC_EVENT = "/events";
    public static final String TOPIC_TELEMETRY = "/band/telemetry";
    public static final String TOPIC_STATUS = "/status";
    public static final String TOPIC_BAND_STATUS = "/band/system";
    public static final String TOPIC_HUB_PRESENCE = "/presence";
    public static final String TOPIC_HUB_CONTROL = "/control";
    public static final String TOPIC_BAND_PRESENCE = "/band/presence";
    public static final String TOPIC_BAND_STATE = "/band/state";
    public static final String TOPIC_USER_CONFIG = "/user_config";
    public static final String TOPIC_BAND_ROLLOVER = "/band/rollover";

    private static final String MQTT_SLEEP_PREDICTION = TOPIC_DEVICE + "%s" + TOPIC_SLEEP_PREDICTION;
    private static final String MQTT_SLEEP_STATUS = TOPIC_DEVICE + "%s" + TOPIC_SLEEP_STATUS; // sleep, stirring, awake status
    private static final String MQTT_EVENT_TOPIC = TOPIC_DEVICE + "%s" + TOPIC_EVENT;
    private static final String MQTT_TELEMETRY = TOPIC_DEVICE + "%s" + TOPIC_TELEMETRY; // battery_voltage
    private static final String MQTT_STATUS = TOPIC_DEVICE + "%s" + TOPIC_STATUS; // heart_rate
    private static final String MQTT_BAND_STATUS = TOPIC_DEVICE + "%s" + TOPIC_BAND_STATUS; // band_status
    private static final String MQTT_HUB_CONTROL = TOPIC_DEVICE + "%s" + TOPIC_HUB_CONTROL; //for nightlight & color
    private static final String MQTT_HUB_PRESENCE = TOPIC_DEVICE + "%s" + TOPIC_HUB_PRESENCE; // online or offline
    private static final String MQTT_BAND_PRESENCE = TOPIC_DEVICE + "%s" + TOPIC_BAND_PRESENCE; //for detecting status
    private static final String MQTT_BAND_STATE = TOPIC_DEVICE + "%s" + TOPIC_BAND_STATE;
    private static final String MQTT_USER_CONFIG = TOPIC_DEVICE + "%s" + TOPIC_USER_CONFIG;
    private static final String MQTT_BAND_ROLLOVER = TOPIC_DEVICE + "%s" + TOPIC_BAND_ROLLOVER;

    /**
     * Get an Mqtt url with TLS.
     *
     * @return mqtt tls url
     */
    public static String getMqttTlsUrl() {
        return SSL_PREFIX + MQTT_TLS_BROKER;
    }


    public static String getSleepPredictionTopic(final String serial) {
        return String.format(MQTT_SLEEP_PREDICTION, serial);
    }

    public static String getSleepStatusTopic(final String serial) {
        return String.format(MQTT_SLEEP_STATUS, serial);
    }

    /**
     * Get the topic for device menu.
     *
     * @param serial the hub’s serial.
     * @return events topic name
     */
    public static String getHubControl(final String serial) {
        return String.format(MQTT_HUB_CONTROL, serial);
    }

    /**
     * Get the topic for device event.
     *
     * @param serial the hub’s serial.
     * @return events topic name
     */
    public static String getEventTopic(final String serial) {
        return String.format(MQTT_EVENT_TOPIC, serial);
    }

    /**
     * Get the topic for Telemetry.
     *
     * @param serial the hub’s serial.
     * @return Telemetry topic name
     */
    public static String getTelemetryTopic(final String serial) {
        return String.format(MQTT_TELEMETRY, serial);
    }

    /**
     * Get the topic for Status.
     *
     * @param serial the hub’s serial.
     * @return Status topic name
     */
    public static String getStatusTopic(final String serial) {
        return String.format(MQTT_STATUS, serial);
    }

    public static String getMqttBandStatusTopic(final String serial) {
        return String.format(MQTT_BAND_STATUS, serial);
    }

    public static String getHubPresence(String serial) {
        return String.format(MQTT_HUB_PRESENCE, serial);
    }

    public static String getBandPresence(String serial) {
        return String.format(MQTT_BAND_PRESENCE, serial);
    }

    public static String getBandState(String serial) {
        return String.format(MQTT_BAND_STATE, serial);
    }

    public static String getMqttUserConfig(String serial) {
        return String.format(MQTT_USER_CONFIG, serial);
    }

    public static String getBandRollover(String serial) {
        return String.format(MQTT_BAND_ROLLOVER, serial);
    }
}
