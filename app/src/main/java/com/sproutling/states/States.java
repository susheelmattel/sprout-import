/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

/**
 * Created by moi0312 on 2016/12/5.
 */

public class States {

    public static class Key {
        public static final String PRE_STATUS = "preStatusLog";
        public static final String CURRENT_STATUS = "currentStatusLog";
        public static final String DATAITEM_CHILD = "dataItemChild";

        public static final String ROLLOVER_SCREEN_FLAG = "rolloverscreenflag"; //TODO remove after re construct
        public static final String HR_ANOMALY_SCREEN_FLAG = "hrAnomalyScreenFlag"; //TODO remove after re construct

        public static final String MQTT_EVENTS = "MQTT_event";
        public static final String MQTT_HUB_PRESENCE = "MQTT_hubPresence";
        public static final String MQTT_HUB_USER_CONFIG = "MQTT_hubUserConfig";
        public static final String MQTT_HUB_STATUS = "MQTT_hubStatus";
        public static final String MQTT_HUB_CONTROL = "MQTT_hubControl";
        public static final String MQTT_BAND_STATE = "MQTT_bandState";
        public static final String MQTT_SLEEP_PREDICTION = "MQTT_sleepPrediction";
        public static final String MQTT_SLEEP_STATUS = "MQTT_sleepStatus";
        public static final String MQTT_BAND_ROLLOVER = "MQTT_bandRollover";

        public static final String DEVICE_SERIAL = "deviceSerial";
        public static final String LEARNING_PERIOD_STATUS = "learningPeriodStatus";
    }

    public static class Value {
        public static final String DEVICE_NULL = "deviceNull";
    }

    public static class StatusValue {
        public static final String INITIAL = "Initial";
        public static final String DETECTING = "Detecting";
        public static final String UNKNOWN = "Unknown";
        public static final String LEARNING_PERIOD = "LEARNING_PERIOD";
        public static final String AWAKE = "Awake";
        public static final String STIRRING = "Stirring";
        public static final String ASLEEP = "Asleep";
        public static final String HEART_RATE = "HEART_RATE";
        public static final String UNUSUAL_HEARTBEAT = "UnusualHeartbeat";
        public static final String ROLLED_OVER = "RolledOver";

        public static final String WEARABLE_READY = "WearableReady";
        public static final String WEARABLE_CHARGING = "WearableCharging";
        public static final String WEARABLE_CHARGED = "WearableCharged";
        public static final String WEARABLE_TOO_FAR_AWAY = "WearableTooFarAway";
        public static final String WEARABLE_NOT_FOUND = "WearableNotFound";
        public static final String WEARABLE_FELL_OFF = "WearableFellOff";
        public static final String WEARABLE_OUT_OF_BATTERY = "OutOfBattery";
        public static final String HUB_OFFLINE = "HubOffline";
        public static final String HUB_ONLINE = "HubOnline";

        public static final String NO_SERVICE = "NoService";
        public static final String WEARABLE_BATTERY = "Battery";

        public static final String NO_CONFIGURED_DEVICE = "noConfiguredDevice";
        public static final String FIRMWARE_UPDATING = "firmwareUpdating";
//		public static final String RETURN_TO_PRE_STATUS_FLAG = "ReturnToPreStatusFlag";
    }

    public static class EventTypeValue {
        public static final int INITIAL = -1;
        public static final int DETECTING = -2;
        public static final int NO_CONFIGURED_DEVICE = -3;
        public static final int FIRMWARE_UPDATING = -4;
    }

    public static class NightLightValue {
        public static final int UNKNOW = 0;
        public static final int BLUE = 1;
        public static final int GREEN = 2;
        public static final int RED = 3;
        public static final int WHITE = 4;
        public static final int YELLOW = 5;
        public static final int PINK = 6;
        public static final int AMBER = 7;

        //UNKNOWN(0), BLUE(1), GREEN(2), RED(3), WHITE(4), YELLOW(5), PINK(6), AMBER(7)
    }

    public static class SongValue {
        public static final String AIR_ON_G_STRING = "airongstring";
        public static final String AMBIENT_A = "ambienta";
        public static final String AMBIENT_B = "ambientb";
        public static final String AMBIENT_C = "ambientc";
        public static final String AMBIENT_D = "ambientd";
        public static final String AMBIENT_E = "ambiente";
        public static final String ARE_YOU_SLEEPING = "areyousleeping";
        public static final String BEAUTIFUL_DREAMER = "beautifuldreamer";
        public static final String BRAHMS_LULLABY = "brahmslullaby";
        public static final String CLAIR_DR_LUNE = "clairdelune";
        public static final String DANCE_OF_THE_SPIRITS = "danceofspirits";
        public static final String HUSH_LITTLE_BABY = "hushlittlebabay";
        public static final String LIEBESTRAUM = "liebestraum";
        public static final String SCHUBERTS_LULLABY = "schubertslullaby";
        public static final String TWINKLE_TWINKLE_LITTLE_STAR = "twinkletwinkle";
        public static final String WHITE = "white";
        public static final String BROWN = "brown";
        public static final String PINK = "pink";
        public static final String OCEAN = "ocean";
        public static final String RAIN = "rain";
        public static final String THUNDER = "thunder";
        public static final String HEART_SLOW = "heart_slow";
        public static final String HEART_FAST = "heart_fast";
    }
}
