/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.pipeline;


import android.content.Context;
import android.util.Log;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.object.HubAmbientTopicEvent;
import com.sproutling.object.HubBandRolloverTopicEvent;
import com.sproutling.object.HubBandStateTopicEvent;
import com.sproutling.object.HubBandStatusTopicEvent;
import com.sproutling.object.HubBandTelemetryTopicEvent;
import com.sproutling.object.HubCLITopicEvent;
import com.sproutling.object.HubControlTopicEvent;
import com.sproutling.object.HubEventsTopicEvent;
import com.sproutling.object.HubFirmwareUpdateTopicEvent;
import com.sproutling.object.HubPresenceTopicEvent;
import com.sproutling.object.HubSleepPredictionTopicEvent;
import com.sproutling.object.HubSleepStatusTopicEvent;
import com.sproutling.object.HubStatusTopicEvent;
import com.sproutling.object.HubUserConfigurationTopic;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.utils.Utils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import sproutling.EventOuterClass;
import sproutling.EventOuterClass.EventType;
import sproutling.Hub;
import sproutling.Sleep;

/**
 * Handles call backs from the MQTT Client
 */
public class MqttCallbackHandler implements MqttCallback {
    private static final String TAG = MqttCallbackHandler.class.getSimpleName();
    private static final String HUB_PRESENCE_URL = "/presence";
    private static final String HUB_STATUS_URL = "/status";
    private static final String HUB_AMBIENT_URL = "/ambient";
    private static final String HUB_FIRMWARE_UPDATE_URL = "/firmware";
    private static final String HUB_CONTROL_URL = "/control";
    private static final String HUB_CLI_URL = "/cli";
    private static final String BAND_STATUS_URL = "/band/system";
    private static final String BAND_TELEMETRY_URL = "/band/telemetry";
    private static final String BAND_ROLLOVER_URL = "/band/rollover";
    private static final String BAND_PRESENCE_URL = "/band/presence";
    private static final String BAND_STATE_URL = "/band/state";
    private static final String EVENTS = "/events";
    private static final String EVENTS_ALERTS = "/events/alerts";
    private static final String SLEEP_PREDICTIONS = "/sleep/predictions";
    private static final String SLEEP_STATUS = "/sleep/status";
    private static final String USER_CONFIG = "/user_config";

    /**
     * {@link Context} for the application used to format and import external strings
     **/
    private Context mContext;

    /**
     * Creates an <code>MqttCallbackHandler</code> object
     *
     * @param context The application's context
     */
    public MqttCallbackHandler(Context context) {
        mContext = context;
    }

    /**
     * This method is called when the connection to the server is lost.
     *
     * @param cause the reason behind the loss of connection.
     */
    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null) {
            Log.d(TAG, "The connection to the server is lost.");

            if (cause.getMessage() != null) {
                Log.d(TAG, "message: " + cause.getMessage());
            }

            if (cause.getCause() != null && cause.getCause().getMessage() != null) {
                Log.d(TAG, "Cause: " + cause.getCause().getMessage());
            }
            cause.printStackTrace();
        }
    }

    /**
     * This method is called when a message arrives from the server.
     *
     * @param topic   name of the topic on the message was published to
     * @param message the actual message.
     * @throws Exception if a terminal error has occurred, and the client should be
     *                   shut down.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "Got a message on " + topic);
        String[] strings = topic.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 3; i < strings.length; i++) { // remove /device/hub/{serial}
            builder.append("/").append(strings[i]);
        }
        if (message != null && message.getPayload() != null) {
            switch (builder.toString()) {
                case EVENTS:
                    EventOuterClass.Event eosEvent = EventOuterClass.Event.parseFrom(message.getPayload());
                    Log.v(TAG, "event: " + eosEvent.getEventValue() + " : " + eosEvent.getEvent());
                    EventOuterClass.EventType eventType = eosEvent.getEvent();
                    if (eventType == EventType.AWAKE || eventType == EventType.STIRRING || eventType == EventType.ASLEEP ||
                            eventType == EventType.WEARABLE_READY || eventType == EventType.WEARABLE_BATTERY ||
                            eventType == EventType.WEARABLE_CHARGING || eventType == EventType.WEARABLE_CHARGED ||
                            eventType == EventType.WEARABLE_NOT_FOUND || eventType == EventType.WEARABLE_BATTERY_DEAD ||
                            eventType == EventType.HUB_OFFLINE || eventType == EventType.HUB_ONLINE) {
                        Log.v(TAG, "event is blocked from this channel");
                        return;
                    }
                    EventBus.getDefault().post(new HubEventsTopicEvent(eosEvent));
                    //Dispatches an action to store.
                    App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, eosEvent));
                    break;
                case SLEEP_PREDICTIONS:
                    Sleep.SleepPrediction sleepPredictionData = Sleep.SleepPrediction.parseFrom(message.getPayload());
                    App.getInstance().setUserData("sleep_prediction", sleepPredictionData.toString());
                    Log.v(TAG, "sleepPredictionData.getPredictedSleepDuration(): " + sleepPredictionData.getPredictedSleepDuration());
                    EventBus.getDefault().post(new HubSleepPredictionTopicEvent(sleepPredictionData));
                    App.getInstance().dispatchAction(Actions.SLEEP_PREDICTIONS, new Payload().put(States.Key.MQTT_SLEEP_PREDICTION, sleepPredictionData));
                    break;
                case SLEEP_STATUS:
                    Sleep.SleepStatus sleepStatus = Sleep.SleepStatus.parseFrom(message.getPayload());
                    Log.v(TAG, "Sleep Status: " + sleepStatus.getSleepState());
                    App.getInstance().setUserData("sleep_status_received", sleepStatus.toString());
                    EventBus.getDefault().post(new HubSleepStatusTopicEvent(sleepStatus));
                    App.getInstance().dispatchAction(Actions.SLEEP_STATUS, new Payload().put(States.Key.MQTT_SLEEP_STATUS, sleepStatus));
                    break;
                case HUB_STATUS_URL:
                    Hub.HubStatus hubStatus = Hub.HubStatus.parseFrom(message.getPayload());
                    Log.d(TAG, "HUb wifiStatus :" + hubStatus.getWifiConnection().getConnected());
                    Log.d(TAG, "Hub firmWareVersion :" + Utils.getVersion(hubStatus.getFirmwareVersion()));
                    App.getInstance().setUserData("hub_firmware_number", Utils.getVersion(hubStatus.getFirmwareVersion()));
                    App.getInstance().setUserData("hub_status_received", hubStatus.toString());
                    EventBus.getDefault().post(new HubStatusTopicEvent(hubStatus));
                    App.getInstance().dispatchAction(Actions.TYPE_HUB_STATUS, new Payload().put(States.Key.MQTT_HUB_STATUS, hubStatus));
                    break;
                case HUB_AMBIENT_URL:
                    Hub.HubAmbient hubAmbient = Hub.HubAmbient.parseFrom(message.getPayload());
                    EventBus.getDefault().post(new HubAmbientTopicEvent(hubAmbient));
                    break;
                case HUB_FIRMWARE_UPDATE_URL:
                    Hub.FirmwareUpdate firmwareUpdate = Hub.FirmwareUpdate.parseFrom(message.getPayload());
                    EventBus.getDefault().post(new HubFirmwareUpdateTopicEvent(firmwareUpdate));
                    break;
                case HUB_CONTROL_URL:
                    Hub.HubControl hubControl = Hub.HubControl.parseFrom(message.getPayload());
                    App.getInstance().setUserData("hub_control_received", hubControl.toString());
                    EventBus.getDefault().post(new HubControlTopicEvent(hubControl));
                    App.getInstance().dispatchAction(Actions.HUB_CONTROL, new Payload().put(States.Key.MQTT_HUB_CONTROL, hubControl));
                    break;
                case HUB_CLI_URL:
                    Hub.HubCLI hubCLI = Hub.HubCLI.parseFrom(message.getPayload());
                    EventBus.getDefault().post(new HubCLITopicEvent(hubCLI));
                    break;
                case BAND_STATUS_URL:
                    Hub.BandStatus bandStatus = Hub.BandStatus.parseFrom(message.getPayload());
                    Log.d(TAG, "Band batteryVoltage :" + bandStatus.getBatteryVoltage());
                    Log.d(TAG, "Band firmWareVersion :" + Utils.getVersion(bandStatus.getFirmwareVersion()));
                    App.getInstance().setUserData("hub_firmware_number", Utils.getVersion(bandStatus.getFirmwareVersion()));
                    App.getInstance().setUserData("band_status_received", bandStatus.toString());
                    EventBus.getDefault().post(new HubBandStatusTopicEvent(bandStatus));
                    break;
                case BAND_TELEMETRY_URL:
                    Hub.BandTelemetry bandTelemetry = Hub.BandTelemetry.parseFrom(message.getPayload());
                    Log.v(TAG, "bandTelemetry: hrBpm: " + bandTelemetry.getHrBpm() + ", hrSqi: " + bandTelemetry.getHrSqi());
                    EventBus.getDefault().post(new HubBandTelemetryTopicEvent(bandTelemetry));
                    break;
                case BAND_ROLLOVER_URL:
                    Hub.BandRollover bandRollover = Hub.BandRollover.parseFrom(message.getPayload());
                    EventBus.getDefault().post(new HubBandRolloverTopicEvent(bandRollover));
                    App.getInstance().dispatchAction(Actions.BAND_ROLLOVER, new Payload().put(States.Key.MQTT_BAND_ROLLOVER, bandRollover));
                    break;
                case BAND_PRESENCE_URL:
//                Hub.BandPresence bandPresence = Hub.BandPresence.parseFrom(message.getPayload());
//                Log.v(TAG, "Band Presence: " + bandPresence.getPresenceDetected());
//                EventBus.getDefault().post(new HubBandPresenceTopicEvent(bandPresence));
//                App.getInstance().dispatchAction(Actions.BAND_PRESENCE, new Payload().put(States.Key.BAND_PRESENCE_DATA, bandPresence));
                    break;
                case BAND_STATE_URL:
                    Hub.BandState bandState = Hub.BandState.parseFrom(message.getPayload());
                    Log.v(TAG, "Band State: " + bandState.getState());
                    EventBus.getDefault().post(new HubBandStateTopicEvent(bandState));
                    App.getInstance().dispatchAction(Actions.BAND_STATE, new Payload().put(States.Key.MQTT_BAND_STATE, bandState));
                    break;
                case HUB_PRESENCE_URL:
                    Hub.HubPresence hubPresence = Hub.HubPresence.parseFrom(message.getPayload());
                    Log.v(TAG, "Hub: " + hubPresence.getConnectionState());
                    EventBus.getDefault().post(new HubPresenceTopicEvent(hubPresence));
                    App.getInstance().dispatchAction(Actions.HUB_PRESENCE, new Payload().put(States.Key.MQTT_HUB_PRESENCE, hubPresence));
                    break;
                case USER_CONFIG:
                    Hub.HubUserConfiguration hubUserConfiguration = Hub.HubUserConfiguration.parseFrom(message.getPayload());
                    Log.v(TAG, "HubUserConfiguration BLE_ALERT: " + hubUserConfiguration.getBleAlert());
                    Log.v(TAG, "HubUserConfiguration WIFI_ALERT: " + hubUserConfiguration.getWifiAlert());
                    EventBus.getDefault().post(new HubUserConfigurationTopic(hubUserConfiguration));
                    App.getInstance().dispatchAction(Actions.HUB_USER_CONFIG, new Payload().put(States.Key.MQTT_HUB_USER_CONFIG, hubUserConfiguration));
            }
        }
    }

    /**
     * Called when delivery for a message has been completed, and all
     * acknowledgments have been received.
     *
     * @param token the delivery token associated with the message.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Called when a outgoing publish is complete.
    }
}
