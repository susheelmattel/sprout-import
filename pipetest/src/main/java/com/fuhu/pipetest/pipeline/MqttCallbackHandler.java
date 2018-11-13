package com.fuhu.pipetest.pipeline;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fuhu.pipetest.App;
import com.google.protobuf.ByteString;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import sproutling.EventOuterClass;
import sproutling.Hub;

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
    private static final String BAND_STATUS_URL = "band/system";
    private static final String BAND_TELEMETRY_URL = "/band/telemetry";
    private static final String BAND_ROLLOVER_URL = "/band/rollover";
    private static final String BAND_PRESENCE_URL = "/band/presence";


    /**
     * {@link Context} for the application used to format and import external strings
     **/
    private Context context;

    /**
     * Creates an <code>MqttCallbackHandler</code> object
     *
     * @param context The application's context
     */
    public MqttCallbackHandler(Context context) {
        this.context = context;
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
        if (message != null && message.getPayload() != null) {

            if (topic.toLowerCase().contains(HUB_PRESENCE_URL)) {
                Hub.HubPresence hubPresence = Hub.HubPresence.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(HUB_STATUS_URL)) {
                Hub.HubStatus hubStatus = Hub.HubStatus.parseFrom(message.getPayload());
                boolean wifiStatus = hubStatus.getWifiConnection().getConnected();
                String firmWareVersion = getVersion(hubStatus.getFirmwareVersion());
                Log.d(TAG, " HUb wifiStatus :" + wifiStatus);
                Log.d(TAG, "Hub firmWareVersion :" + firmWareVersion);

            } else if (topic.toLowerCase().contains(HUB_AMBIENT_URL)) {
                Hub.HubAmbient hubAmbient = Hub.HubAmbient.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(HUB_FIRMWARE_UPDATE_URL)) {
                Hub.FirmwareUpdate firmwareUpdate = Hub.FirmwareUpdate.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(HUB_CONTROL_URL)) {
                Hub.HubControl hubControl = Hub.HubControl.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(HUB_CLI_URL)) {
                Hub.HubCLI hubCLI = Hub.HubCLI.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(BAND_STATUS_URL)) {
                Hub.BandStatus bandStatus = Hub.BandStatus.parseFrom(message.getPayload());
                int batteryVoltage = bandStatus.getBatteryVoltage();
                String firmWareVersion = getVersion(bandStatus.getFirmwareVersion());
                Log.d(TAG, "Band batteryVoltage :" + batteryVoltage);
                Log.d(TAG, "Band firmWareVersion :" + firmWareVersion);

            } else if (topic.toLowerCase().contains(BAND_TELEMETRY_URL)) {
                Hub.BandTelemetry bandTelemetry = Hub.BandTelemetry.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(BAND_ROLLOVER_URL)) {
                Hub.BandRollover bandRollover = Hub.BandRollover.parseFrom(message.getPayload());

            } else if (topic.toLowerCase().contains(BAND_PRESENCE_URL)) {
                Hub.BandPresence bandPresence = Hub.BandPresence.parseFrom(message.getPayload());

            } else {
                EventOuterClass.Event event = EventOuterClass.Event.parseFrom(message.getPayload());

                if (event != null) {
                    Log.d(TAG, "content: " + event.toString());
                    Toast.makeText(App.getAppContext(), "Got a message on " + topic + " with content:\n"
                            + event.toString(), Toast.LENGTH_LONG).show();
                }
                //TODO Dispatches an action to store.
                Log.d(TAG, "childId: " + event.getChildId() + " timeStamp: " + event.getTimestamp().getSeconds());
                Log.v(TAG, "heartRate: " + event.getHeartRate() + " battery_Percentage: " + event.getBatteryPercentage());

                EventOuterClass.EventType eventType = event.getEvent();
                Log.d(TAG, "Serial: " + event.getSerial() + " event type: " + eventType.name());

//            switch (eventType)
//            {
//                case LEARNING_PERIOD:
//                case AWAKE:
//                case STIRRING:
//                case ASLEEP:
//                case ROLLED_OVER:
//                case HUB_OFFLINE:
//                case NO_SERVICE:
//                    break;
//
//                case HEART_RATE:
//                case HEART_RATE_UNUSUAL:
//                    break;
//
//                case WEARABLE_READY:
//                case WEARABLE_CHARGED:
//                case WEARABLE_TOO_FAR_AWAY:
//                case WEARABLE_NOT_FOUND:
//                case WEARABLE_FELL_OFF:
//                case WEARABLE_CHARGING:
//                case WEARABLE_BATTERY:
//                    break;
//
//                case UNKNOWN:
//                case UNRECOGNIZED:
//                    break;
//            }
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

    public static String getVersion(ByteString bytes) {
        int length = bytes.size();
        StringBuilder version = new StringBuilder();
        for (int i = 0; i < length; i++) {
            Byte byteVal = bytes.byteAt(i);
            version.append(byteVal.intValue());
            if (i < length - 1) {
                version.append(".");
            }
        }
        return version.toString();
    }
}