package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 3/6/17.
 */

public class PushNotificationAlertSettings {
    @SerializedName("NotificationsDisabled")
    private String mNotificationsDisabled;

    @SerializedName("AsleepDisabled")
    private String mAsleepDisabled;

    @SerializedName("AwakeDisabled")
    private String mAwakeDisabled;

    @SerializedName("BatteryDisabled")
    private String mBatteryDisabled;

    @SerializedName("HeartRateDisabled")
    private String mHeartRateDisabled;

    @SerializedName("HubOfflineDisabled")
    private String mHubOfflineDisabled;

    @SerializedName("LearningPeriodDisabled")
    private String mLearningPeriodDisabled;

    @SerializedName("NoServiceDisabled")
    private String mNoServiceDisabled;

    @SerializedName("RolledoverDisabled")
    private String mRolledoverDisabled;

    @SerializedName("StirringDisabled")
    private String mStirringDisabled;

    @SerializedName("UnusualHeartbeatDisabled")
    private String mUnusualHeartbeatDisabled;

    @SerializedName("WearableHeartbeatDisabled")
    private String mWearableHeartbeatDisabled;

    @SerializedName("WearableChargedDisabled")
    private String mWearableChargedDisabled;

    @SerializedName("WearableChargingDisabled")
    private String mWearableChargingDisabled;

    @SerializedName("WearableFelloffDisabled")
    private String mWearableFelloffDisabled;

    @SerializedName("WearableNotFoundDisabled")
    private String mWearableNotFoundDisabled;

    @SerializedName("WearableReadyDisabled")
    private String mWearableReadyDisabled;

    @SerializedName("WearableTooFarAwayDisabled")
    private String mWearableTooFarAwayDisabled;

    @SerializedName("RoomTemperatureDisabled")
    private String mRoomTemperatureDisabled;

    @SerializedName("RoomHumidityDisabled")
    private String mRoomHumidityDisabled;

    @SerializedName("RoomNoiseLevelDisabled")
    private String mRoomNoiseLevelDisabled;

    @SerializedName("RoomLightLevelDisabled")
    private String mRoomLightLevelDisabled;

    public PushNotificationAlertSettings(String notificationsDisabled, String asleepDisabled, String awakeDisabled,
                                         String batteryDisabled, String heartRateDisabled, String hubOfflineDisabled,
                                         String learningPeriodDisabled, String noServiceDisabled, String rolledoverDisabled,
                                         String stirringDisabled, String unusualHeartbeatDisabled, String wearableHeartbeatDisabled,
                                         String wearableChargedDisabled, String wearableChargingDisabled, String wearableFelloffDisabled,
                                         String wearableNotFoundDisabled, String wearableReadyDisabled, String wearableTooFarAwayDisabled,
                                         String roomTemperatureDisabled, String roomHumidityDisabled, String roomNoiseLevelDisabled, String roomLightLevelDisabled) {
        mNotificationsDisabled = notificationsDisabled;
        mAsleepDisabled = asleepDisabled;
        mAwakeDisabled = awakeDisabled;
        mBatteryDisabled = batteryDisabled;
        mHeartRateDisabled = heartRateDisabled;
        mHubOfflineDisabled = hubOfflineDisabled;
        mLearningPeriodDisabled = learningPeriodDisabled;
        mNoServiceDisabled = noServiceDisabled;
        mRolledoverDisabled = rolledoverDisabled;
        mStirringDisabled = stirringDisabled;
        mUnusualHeartbeatDisabled = unusualHeartbeatDisabled;
        mWearableHeartbeatDisabled = wearableHeartbeatDisabled;
        mWearableChargedDisabled = wearableChargedDisabled;
        mWearableChargingDisabled = wearableChargingDisabled;
        mWearableFelloffDisabled = wearableFelloffDisabled;
        mWearableNotFoundDisabled = wearableNotFoundDisabled;
        mWearableReadyDisabled = wearableReadyDisabled;
        mWearableTooFarAwayDisabled = wearableTooFarAwayDisabled;
        mRoomTemperatureDisabled = roomTemperatureDisabled;
        mRoomHumidityDisabled = roomHumidityDisabled;
        mRoomNoiseLevelDisabled = roomNoiseLevelDisabled;
        mRoomLightLevelDisabled = roomLightLevelDisabled;
    }

    public String getNotificationsDisabled() {
        return mNotificationsDisabled;
    }

    public String getAsleepDisabled() {
        return mAsleepDisabled;
    }

    public String getAwakeDisabled() {
        return mAwakeDisabled;
    }

    public String getBatteryDisabled() {
        return mBatteryDisabled;
    }

    public String getHeartRateDisabled() {
        return mHeartRateDisabled;
    }

    public String getHubOfflineDisabled() {
        return mHubOfflineDisabled;
    }

    public String getLearningPeriodDisabled() {
        return mLearningPeriodDisabled;
    }

    public String getNoServiceDisabled() {
        return mNoServiceDisabled;
    }

    public String getRolledoverDisabled() {
        return mRolledoverDisabled;
    }

    public String getStirringDisabled() {
        return mStirringDisabled;
    }

    public String getUnusualHeartbeatDisabled() {
        return mUnusualHeartbeatDisabled;
    }

    public String getWearableHeartbeatDisabled() {
        return mWearableHeartbeatDisabled;
    }

    public String getWearableChargedDisabled() {
        return mWearableChargedDisabled;
    }

    public String getWearableChargingDisabled() {
        return mWearableChargingDisabled;
    }

    public String getWearableFelloffDisabled() {
        return mWearableFelloffDisabled;
    }

    public String getWearableNotFoundDisabled() {
        return mWearableNotFoundDisabled;
    }

    public String getWearableReadyDisabled() {
        return mWearableReadyDisabled;
    }

    public String getWearableTooFarAwayDisabled() {
        return mWearableTooFarAwayDisabled;
    }

    public String getRoomTemperatureDisabled() {
        return mRoomTemperatureDisabled;
    }

    public String getRoomHumidityDisabled() {
        return mRoomHumidityDisabled;
    }

    public String getRoomNoiseLevelDisabled() {
        return mRoomNoiseLevelDisabled;
    }

    public String getRoomLightLevelDisabled() {
        return mRoomLightLevelDisabled;
    }
}
