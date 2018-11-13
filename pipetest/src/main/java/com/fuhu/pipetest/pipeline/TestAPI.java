package com.fuhu.pipetest.pipeline;


public class TestAPI {
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String TCP_PREFIX = "tcp://";
    private static final String SSL_PREFIX = "ssl://";

//    private static final String ENDPOINT = HTTP_PREFIX + "35.163.179.218:8090";
    private static final String ENDPOINT = HTTPS_PREFIX + "api-dev.us.sproutlingcloud.com";
//    private static final String MQTT_BROKER = "ihut-219707358.us-west-2.elb.amazonaws.com:1883";
    private static final String MQTT_TLS_BROKER = "mqtt-us.sproutlingcloud.com:443";
//    private static final String MQTT_TLS_BROKER = "mqtt-cn.sproutlingcloud.com:443";
    private static final String VERSION = "v1";

    private static final String SERIAL_NUMBER = "703HH00F00003";//"647HH00A00557";
    public static String getSerialNumber() {
        return SERIAL_NUMBER;
    }

    //    Service names: identity, device, timeline, update
    private static final String USER = "users";
    private static final String CHILD = "child";
    private static final String CHILDREN = "children";
    private static final String PASSWORD = "passwords";

    private static final String STATUS = "status";
    private static final String DEVICE = "devices";
    private static final String EVENTS = "events";
    private static final String OAUTH = "oauth2/token";

    private static final String MQTT_EVENT_TOPIC = "device/hub/%s/events";
    private static final String MQTT_TELEMETRY = "device/hub/%s/band/telemetry"; // battery_voltage
    private static final String MQTT_STATUS = "device/hub/%s/status"; // heart_rate

//    /**
//     * Get an Mqtt url.
//     * @return mqtt url
//     */
//    public static String getMqttUrl() {
//        return TCP_PREFIX + MQTT_BROKER;
//    }

    /**
     * Get an Mqtt url with TLS.
     * @return mqtt tls url
     */
    public static String getMqttTlsUrl() {
        return SSL_PREFIX + MQTT_TLS_BROKER;
    }

    /**
     * Get the topic for device event.
     * @param serial the hub’s serial.
     * @return events topic name
     */
    public static String getEventTopic (final String serial) {
        return String.format(MQTT_EVENT_TOPIC, serial);
    }

    /**
     * Get the topic for Telemetry.
     * @param serial the hub’s serial.
     * @return Telemetry topic name
     */
    public static String getTelemetryTopic (final String serial) {
        return String.format(MQTT_TELEMETRY, serial);
    }

    /**
     * Get the topic for Status.
     * @param serial the hub’s serial.
     * @return Status topic name
     */
    public static String getStatusTopic (final String serial) {
        return String.format(MQTT_STATUS, serial);
    }

    /**
     * Resources related to users in the API.
     * @return user url
     */
    public static String users() {
        return ENDPOINT + "/"  + VERSION + "/" + USER;
    }

    /**
     * Resources related to user by id in the API.
     * @param id User id
     * @return user url with id
     */
    public static String user(final String id) {
        return ENDPOINT + "/"  + VERSION + "/" + USER + "/" + id;
    }

    /**
     * Resources related to passwords in the API.
     * @return password url
     */
    public static String passwords() {
        return ENDPOINT + "/"  + VERSION + "/" + PASSWORD;
    }

    /**
     * Resources related to password by token in the API.
     * @param token access token
     * @return password url with token
     */
    public static String password(final String token) {
        return ENDPOINT + "/"  + VERSION + "/" + PASSWORD + "/" + token;
    }

    /**
     * Resources related to Children in the API.
     * @return child url
     */
    public static String children() {
        return ENDPOINT + "/"  + VERSION + "/" + CHILDREN;
    }

    /**
     * Resources related to Children by id in the API.
     * @param id child id
     * @return child url with id
     */
    public static String child(final String id) {
        return ENDPOINT + "/"  + VERSION + "/" + CHILDREN + "/" + id;
    }

	/**
	 *
     * @param childId
     * @return
     */
    public static String learningPeriodEvent(final String childId) {
        return ENDPOINT+"/"+VERSION+"/"+EVENTS+"/"+CHILD+"?child_id="+ childId + "&event_type=learningPeriod";
    }

    /**
     *
     * @param childId
     * @return
     */
    public static String createTimelineEvent(final String childId) {
//        return ENDPOINT+"/"+VERSION+"/"+EVENTS+"/"+CHILD+"?child_id="+ childId;
        return ENDPOINT+"/"+VERSION+"/"+EVENTS;
    }

    /**
     * Resources related to device in the API.
     * @return device url
     */
    public static String devices() {
        return ENDPOINT + "/"  + VERSION + "/" + DEVICE;
    }

    /**
     * Resources related to device by token in the API.
     * @param serial serial number
     * @return device url with serial
     */
    public static String device(final String serial) {
        return ENDPOINT + "/"  + VERSION + "/" + DEVICE + "?serial=" + serial;
    }

    /**
     * Resources related to status in the API.
     * @return status url
     */
    public static String status() {
        return ENDPOINT + "/"  + VERSION + "/" + STATUS;
    }

    /**
      * Log into an application and to receive an access token from the ecosystem.
      * @return oauth url
      */
    public static String oauth() {
       return ENDPOINT + "/"  + VERSION + "/" + OAUTH;
    }
}
