package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/20/17.
 */

public class PushNotification {
    public static final String ENABLED = "0";
    public static final String DISABLED = "1";

    public static final String DEFAULT_NOTIFICATIONS_DISABLED = "0";
    public static final String DEFAULT_ROLLEDOVER_DISABLED = "0";
    public static final String DEFAULT_UNUSUAL_HEARTBEAT_DISABLED = "0";
    public static final String DEFAULT_AWAKE_DISABLED = "0";
    public static final String DEFAULT_ASLEEP_DISABLED = "1";
    //        public static final String DEFAULT_STIRRING_DISABLED = "1";
    public static final String DEFAULT_WEARABLE_FELL_OFF_DISABLED = "0";
    public static final String DEFAULT_WEARABLE_NOT_FOUND_DISABLED = "1";
    public static final String DEFAULT_WEARABLE_TOO_FAR_AWAY_DISABLED = "1";
    public static final String DEFAULT_BATTERY_DISABLED = "1";
    public static final String DEFAULT_OUT_OF_BATTERY_DISABLED = "1";
    public static final String DEFAULT_HUB_OFFLINE_DISABLED = "1";
    @SerializedName("NotificationsDisabled")
    private String mNotificationsDisabled;
    @SerializedName("RolledoverDisabled")
    private String mRolledOverDisabled;
    @SerializedName("UnusualHeartbeatDisabled")
    private String mUnusualHeartbeatDisabled;
    @SerializedName("AwakeDisabled")
    private String mAwakeDisabled;
    @SerializedName("AsleepDisabled")
    private String mAsleepDisabled;
    //        public String stirringDisabled;
    @SerializedName("WearableFellOffDisabled")
    private String mWearableFellOffDisabled;
    @SerializedName("WearableNotFoundDisabled")
    private String mWearableNotFoundDisabled;
    @SerializedName("WearableTooFarAwayDisabled")
    private String mWearableTooFarAwayDisabled;
    @SerializedName("BatteryDisabled")
    private String mBatteryDisabled;
    @SerializedName("OutOfBatteryDisabled")
    private String mOutOfBatteryDisabled;
    @SerializedName("HubOfflineDisabled")
    private String mHubOfflineDisabled;
    @SerializedName("RoomTemperatureDisabled")
    private String mRoomTemperatureDisabled;
    @SerializedName("RoomHumidityDisabled")
    private String mRoomHumidityDisabled;
    @SerializedName("RoomNoiseLevelDisabled")
    private String mRoomNoiseLevelDisabled;
    @SerializedName("RoomLightLevelDisabled")
    private String mRoomLightLevelDisabled;

    public static PushNotification getDefaultSettings() {
        PushNotification settings = new PushNotification();
        settings.mNotificationsDisabled = DEFAULT_NOTIFICATIONS_DISABLED;
        settings.mRolledOverDisabled = DEFAULT_ROLLEDOVER_DISABLED;
        settings.mUnusualHeartbeatDisabled = DEFAULT_UNUSUAL_HEARTBEAT_DISABLED;
        settings.mAwakeDisabled = DEFAULT_AWAKE_DISABLED;
        settings.mAsleepDisabled = DEFAULT_ASLEEP_DISABLED;
//            settings.stirringDisabled = DEFAULT_STIRRING_DISABLED;
        settings.mWearableFellOffDisabled = DEFAULT_WEARABLE_FELL_OFF_DISABLED;
        settings.mWearableNotFoundDisabled = DEFAULT_WEARABLE_NOT_FOUND_DISABLED;
        settings.mWearableTooFarAwayDisabled = DEFAULT_WEARABLE_TOO_FAR_AWAY_DISABLED;
        settings.mBatteryDisabled = DEFAULT_BATTERY_DISABLED;
        settings.mOutOfBatteryDisabled = DEFAULT_OUT_OF_BATTERY_DISABLED;
        settings.mHubOfflineDisabled = DEFAULT_HUB_OFFLINE_DISABLED;
        return settings;
    }

    public String getNotificationsDisabled() {
        return mNotificationsDisabled;
    }

    public void setNotificationsDisabled(String notificationsDisabled) {
        mNotificationsDisabled = notificationsDisabled;
    }

    public String getRolledOverDisabled() {
        return mRolledOverDisabled;
    }

    public void setRolledOverDisabled(String rolledOverDisabled) {
        mRolledOverDisabled = rolledOverDisabled;
    }

    public String getUnusualHeartbeatDisabled() {
        return mUnusualHeartbeatDisabled;
    }

    public void setUnusualHeartbeatDisabled(String unusualHeartbeatDisabled) {
        mUnusualHeartbeatDisabled = unusualHeartbeatDisabled;
    }

    public String getAwakeDisabled() {
        return mAwakeDisabled;
    }

    public void setAwakeDisabled(String awakeDisabled) {
        mAwakeDisabled = awakeDisabled;
    }

    public String getAsleepDisabled() {
        return mAsleepDisabled;
    }

    public void setAsleepDisabled(String asleepDisabled) {
        mAsleepDisabled = asleepDisabled;
    }

    public String getWearableFellOffDisabled() {
        return mWearableFellOffDisabled;
    }

    public void setWearableFellOffDisabled(String wearableFellOffDisabled) {
        mWearableFellOffDisabled = wearableFellOffDisabled;
    }

    public String getWearableNotFoundDisabled() {
        return mWearableNotFoundDisabled;
    }

    public void setWearableNotFoundDisabled(String wearableNotFoundDisabled) {
        mWearableNotFoundDisabled = wearableNotFoundDisabled;
    }

    public String getWearableTooFarAwayDisabled() {
        return mWearableTooFarAwayDisabled;
    }

    public void setWearableTooFarAwayDisabled(String wearableTooFarAwayDisabled) {
        mWearableTooFarAwayDisabled = wearableTooFarAwayDisabled;
    }

    public String getBatteryDisabled() {
        return mBatteryDisabled;
    }

    public void setBatteryDisabled(String batteryDisabled) {
        mBatteryDisabled = batteryDisabled;
    }

    public String getOutOfBatteryDisabled() {
        return mOutOfBatteryDisabled;
    }

    public void setOutOfBatteryDisabled(String outOfBatteryDisabled) {
        mOutOfBatteryDisabled = outOfBatteryDisabled;
    }

    public String getHubOfflineDisabled() {
        return mHubOfflineDisabled;
    }

    public void setHubOfflineDisabled(String hubOfflineDisabled) {
        mHubOfflineDisabled = hubOfflineDisabled;
    }

    public String getRoomTemperatureDisabled() {
        return mRoomTemperatureDisabled;
    }

    public void setRoomTemperatureDisabled(String roomTemperatureDisabled) {
        mRoomTemperatureDisabled = roomTemperatureDisabled;
    }

    public String getRoomHumidityDisabled() {
        return mRoomHumidityDisabled;
    }

    public void setRoomHumidityDisabled(String roomHumidityDisabled) {
        mRoomHumidityDisabled = roomHumidityDisabled;
    }

    public String getRoomNoiseLevelDisabled() {
        return mRoomNoiseLevelDisabled;
    }

    public void setRoomNoiseLevelDisabled(String roomNoiseLevelDisabled) {
        mRoomNoiseLevelDisabled = roomNoiseLevelDisabled;
    }

    public String getRoomLightLevelDisabled() {
        return mRoomLightLevelDisabled;
    }

    public void setRoomLightLevelDisabled(String roomLightLevelDisabled) {
        mRoomLightLevelDisabled = roomLightLevelDisabled;
    }
}
