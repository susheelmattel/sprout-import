/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sproutling.BuildConfig;
import com.sproutling.object.CustomPlaylist;
import com.sproutling.pojos.CreatePhotoResponse;
import com.sproutling.pojos.ProductSettings;
import com.sproutling.services.SSManagement;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class SharedPrefManager {
    //	public static final int DEFAULT_MODE = Context.MODE_MULTI_PROCESS;
    private static final int DEFAULT_MODE = Context.MODE_PRIVATE;
    //shared preference file name
    private static final String DEFAULT_SHARED_PREF = "default_shared_pref";
    private static final String DEVICE_OBJECT = "DEVICE_OBJECT";
    private static final String HUB_CONTROL_SETTINGS = "HUB_CONTROL_SETTINGS";
    private static final String CHILD_PHOTO_OBJECT = "CHILD_PHOTO_OBJECT";
    private static final String UNIQUE_IDENTIFIER = "UNIQUE_IDENTIFIER";
    private static final String HANDHELD_ID = "HANDHELD_ID";
    private static final String PUSH_NOTIFICATION_TOKEN_ID = "PUSH_NOTIFICATION_TOKEN_ID";
    private static final String WIFI_INTERFERENCE_DATE = "WIFI_INTERFERENCE_DATE";
    private static final String SERVER_URL = "SERVER_URL";
    private static final String MQTT_URL = "MQTT_URL";
    private static final String CUSTOM_PLAYLIST = "CUSTOM_PLAYLIST";
    private static final String NIGHT_LIGHT_TIMER = "NIGHT_LIGHT_TIMER";
    private static final String MUSIC_TIMER = "MUSIC_TIMER";

    //get SharedPreferences
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(DEFAULT_SHARED_PREF, DEFAULT_MODE);
    }

    /**
     * @param context
     * @param sharedPreferenceName sharedPreferenceName
     * @return
     */
    public static SharedPreferences getSharedPreferences(Context context, String sharedPreferenceName) {
        return context.getSharedPreferences(sharedPreferenceName, DEFAULT_MODE);
    }

    /**
     * remove sharedPreference value by Key
     *
     * @param context
     * @param key     key
     */
    public static void removeKey(Context context, String key) {
        getSharedPreferences(context).edit().remove(key).apply();
    }

    /**
     * remove sharedPreference value by Key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     */
    public static void removeKey(String sharedPreferenceName, Context context, String key) {
        getSharedPreferences(context, sharedPreferenceName).edit().remove(key).apply();
    }
    //remove

    public static boolean isLearningPeriodDone(Context context) {
        String learningPeriodStatus = getString(context, SPKey.STR_LEARNING_PERIOD_STATUS, null);
        return (SharedPrefManager.SPValue.LEARNING_PERIOD_ENDED.equals(learningPeriodStatus));
    }

    public static boolean setLearningPeriodInProgress(Context context) {
        return put(context, SPKey.STR_LEARNING_PERIOD_STATUS, SPValue.LEARNING_PERIOD_IN_PROGRESS);
    }

    public static boolean setLearningPeriodDone(Context context) {
        return put(context, SPKey.STR_LEARNING_PERIOD_STATUS, SPValue.LEARNING_PERIOD_ENDED);
    }

    /**
     * put string value by key
     *
     * @param context
     * @param key     key
     * @param value   value
     */
    public static boolean put(Context context, String key, String value) {
//        getSharedPreferences(context).edit().putString(key, value).apply();
        return getSharedPreferences(context).edit().putString(key, value).commit();
    }

    /**
     * get string value by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    //---------------------- String --------------------------------------------

    /**
     * put string value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param value                value
     */
    public static boolean put(String sharedPreferenceName, Context context, String key, String value) {
        return getSharedPreferences(context, sharedPreferenceName).edit().putString(key, value).commit();
    }

    /**
     * get string value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         value
     * @return
     */
    public static String getString(String sharedPreferenceName, Context context, String key, String defaultValue) {
        return getSharedPreferences(context, sharedPreferenceName).getString(key, defaultValue);
    }

    /**
     * put boolean value by key
     *
     * @param context
     * @param key       key
     * @param boolValue
     */
    public static boolean put(Context context, String key, boolean boolValue) {
        return getSharedPreferences(context).edit().putBoolean(key, boolValue).commit();
    }

    /**
     * get boolean value by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    //---------------------- boolean --------------------------------------------

    /**
     * put boolean value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param boolValue            boolValue
     */
    public static boolean put(String sharedPreferenceName, Context context, String key, boolean boolValue) {
        return getSharedPreferences(context).edit().putBoolean(key, boolValue).commit();
    }

    /**
     * get boolean value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         defaultValue
     * @return
     */
    public static boolean getBoolean(String sharedPreferenceName, Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    /**
     * put int value by key
     *
     * @param context
     * @param key
     * @param intValue
     */
    public static boolean put(Context context, String key, int intValue) {
        return getSharedPreferences(context).edit().putInt(key, intValue).commit();
    }

    /**
     * get int value by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    //---------------------- int ----------------------

    /**
     * put int value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param intValue
     */
    public static boolean put(String sharedPreferenceName, Context context, String key, int intValue) {
        return getSharedPreferences(context, sharedPreferenceName).edit().putInt(key, intValue).commit();
    }

    /**
     * get int value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         defaultValue
     * @return
     */
    public static int getInt(String sharedPreferenceName, Context context, String key, int defaultValue) {
        return getSharedPreferences(context, sharedPreferenceName).getInt(key, defaultValue);
    }

    /**
     * put long value by key
     *
     * @param context
     * @param key       key
     * @param longValue longValue
     */
    public static boolean put(Context context, String key, long longValue) {
        return getSharedPreferences(context).edit().putLong(key, longValue).commit();
    }

    /**
     * get long value by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static long getLong(Context context, String key, long defaultValue) {
        return getSharedPreferences(context).getLong(key, defaultValue);
    }

    //---------------------- long ----------------------

    /**
     * put long value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param longValue            longValue
     */
    public static void put(String sharedPreferenceName, Context context, String key, long longValue) {
        getSharedPreferences(context, sharedPreferenceName).edit().putLong(key, longValue).apply();
    }

    /**
     * get long value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         defaultValue
     * @return
     */
    public static long getLong(String sharedPreferenceName, Context context, String key, long defaultValue) {
        return getSharedPreferences(context, sharedPreferenceName).getLong(key, defaultValue);
    }

    /**
     * put float value by key
     *
     * @param context
     * @param key        key
     * @param floatValue floatValue
     */
    public static boolean put(Context context, String key, float floatValue) {
        return getSharedPreferences(context).edit().putFloat(key, floatValue).commit();
    }

    /**
     * get float value by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        return getSharedPreferences(context).getFloat(key, defaultValue);
    }

    //---------------------- float ----------------------

    /**
     * put float value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param floatValue           floatValue
     */
    public static void put(String sharedPreferenceName, Context context, String key, float floatValue) {
        getSharedPreferences(context, sharedPreferenceName).edit().putFloat(key, floatValue).apply();
    }

    /**
     * get float value by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         defaultValue
     * @return
     */
    public static float getFloat(String sharedPreferenceName, Context context, String key, float defaultValue) {
        return getSharedPreferences(context, sharedPreferenceName).getFloat(key, defaultValue);
    }

    /**
     * put Set&lt;String&gt; by key
     *
     * @param context
     * @param key       key
     * @param stringSet stringSet
     */
    public static boolean put(Context context, String key, Set<String> stringSet) {
        return getSharedPreferences(context).edit().putStringSet(key, stringSet).commit();
    }

    /**
     * get Set&lt;String&gt; by key
     *
     * @param context
     * @param key          key
     * @param defaultValue defaultValue
     * @return
     */
    public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
        return getSharedPreferences(context).getStringSet(key, defaultValue);
    }

    //---------------------- Set<String> ----------------------

    /**
     * put Set&lt;String&gt; by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param stringSet            stringSet
     */
    public static void put(String sharedPreferenceName, Context context, String key, Set<String> stringSet) {
        getSharedPreferences(context, sharedPreferenceName).edit().putStringSet(key, stringSet).apply();
    }

    /**
     * get Set&lt;String&gt; by key
     *
     * @param sharedPreferenceName sharedPreferenceName
     * @param context
     * @param key                  key
     * @param defaultValue         defaultValue
     * @return
     */
    public static Set<String> getStringSet(String sharedPreferenceName, Context context, String key, Set<String> defaultValue) {
        return getSharedPreferences(context, sharedPreferenceName).getStringSet(key, defaultValue);
    }

    public static void clear(Context context) {
        getSharedPreferences(context).edit().clear().apply();
    }

    public static boolean saveDevice(Context context, SSManagement.DeviceResponse deviceResponse) {
        Gson gson = new GsonBuilder().create();
        String deviceJson = gson.toJson(deviceResponse);
        return put(context, DEVICE_OBJECT, deviceJson);
    }

    public static SSManagement.DeviceResponse getDevice(Context context) {
        String deviceJson = getString(context, DEVICE_OBJECT, null);
        if (deviceJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(deviceJson, SSManagement.DeviceResponse.class);
        }
        return null;
    }

    public static boolean saveHubControlSettings(Context context, ProductSettings productSettings) {
        Gson gson = new GsonBuilder().create();
        String deviceJson = gson.toJson(productSettings);
        return put(context, HUB_CONTROL_SETTINGS, deviceJson);
    }

    public static ProductSettings getHubControlSettings(Context context) {
        String deviceJson = getString(context, HUB_CONTROL_SETTINGS, null);
        if (deviceJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(deviceJson, ProductSettings.class);
        }
        return null;
    }

    public static void clearDevice(Context context) {
        removeKey(context, DEVICE_OBJECT);
    }

    public static boolean saveChildPhotoInfo(Context context, CreatePhotoResponse createPhotoResponse) {
        return put(context, CHILD_PHOTO_OBJECT, Utils.toJsonString(createPhotoResponse));
    }

    public static CreatePhotoResponse getChildPhotoInfo(Context context) {
        String jsonStr = getString(context, CHILD_PHOTO_OBJECT, null);
        if (jsonStr != null) {
            return Utils.toObjectFromJson(CreatePhotoResponse.class, jsonStr);
        }
        return null;
    }

    public static void saveHandHeldId(Context context, String id) {
        if (!TextUtils.isEmpty(id)) {
            put(context, HANDHELD_ID, id);
        }
    }

    public static void saveServerUrl(Context context, String serverUrl) {
        if (!TextUtils.isEmpty(serverUrl)) {
            put(context, SERVER_URL, serverUrl);
        }
    }

    public static String getServerUrl(Context context) {
        return getString(context, SERVER_URL, BuildConfig.SERVER_URL);
    }

    public static void saveMqttUrl(Context context, String mqttUrl) {
        if (!TextUtils.isEmpty(mqttUrl)) {
            put(context, MQTT_URL, mqttUrl);
        }
    }

    public static String getMqttUrl(Context context) {
        return getString(context, MQTT_URL, BuildConfig.MQTT_URL);
    }

    public static String getHandHeldId(Context context) {
        return getString(context, HANDHELD_ID, null);
    }

    public static void savePushNotificationTokenId(Context context, String id) {
        if (!TextUtils.isEmpty(id)) {
            put(context, PUSH_NOTIFICATION_TOKEN_ID, id);
        }
    }

    public static String getPushNotificationTokenId(Context context) {
        return getString(context, PUSH_NOTIFICATION_TOKEN_ID, null);
    }

    public static void saveNightLightTimerValue(Context context, long timeInMillisUtc) {
        put(context, NIGHT_LIGHT_TIMER, timeInMillisUtc);
    }

    public static long getNightLightTimerValue(Context context) {
        return getLong(context, NIGHT_LIGHT_TIMER, -1);
    }

    public static void saveMusicTimerValue(Context context, long timeInMillisUtc) {
        put(context, MUSIC_TIMER, timeInMillisUtc);
    }

    public static long getMusicTimerValue(Context context) {
        return getLong(context, MUSIC_TIMER, -1);
    }

    public static void saveCustomPlaylist(Context context, CustomPlaylist customPlaylist) {
        if (customPlaylist != null) {
            put(context, CUSTOM_PLAYLIST, Utils.toJsonString(customPlaylist));
        }
    }

    public static CustomPlaylist getCustomPlaylist(Context context) {
        String jsonStr = getString(context, CUSTOM_PLAYLIST, null);
        if (!TextUtils.isEmpty(jsonStr)) {
            return Utils.toObjectFromJson(CustomPlaylist.class, jsonStr);
        }
        return null;
    }

    public static void saveWifiInterferenceDate(Context context, Date date) {
        if (date != null) {
            put(context, WIFI_INTERFERENCE_DATE, date.getTime());
        }
    }

    public static Date getWifiInterferenceDate(Context context) {
        long dateVal = getLong(context, WIFI_INTERFERENCE_DATE, -1);
        Date wifiInterferenceDate = null;
        if (dateVal != -1) {
            wifiInterferenceDate = new Date(dateVal);
        }
        return wifiInterferenceDate;
    }

    public static String getUniqueIdentifier(Context context) {
        String uniqueID = getString(context, UNIQUE_IDENTIFIER, null);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            put(context, UNIQUE_IDENTIFIER, uniqueID);
        }
        return uniqueID;
    }

    //keys of SharedPreferenceManager
    public interface SPKey {
        String BOOL_TUTORIAL_SHOWN = "tutorialShown";
        String STR_LEARNING_PERIOD_STATUS = "learningPeriodStatus";
        String LONG_LEARNING_PERIOD_START_TIMESTAMP = "learningPeriodStartTimestamp";
        String LONG_LEARNING_PERIOD_DONE_TIMESTAMP = "learningPeriodDoneTimestamp";
        String BOOL_SLEEP_TIMER_RECORDING = "sleepTimerRecording";
        String LONG_SLEEP_TIMER_START_TIME = "sleepTimerStartTime";
        String BOOL_TIMELINE_FIRST_LAUNCH = "timelineFirstLaunch";
        String LONG_TIMELINE_ALARM_TIME = "timelineAlarmTime";
        String LONG_SLEEP_TIMER_ALARM_END_TIME = "sleepTimerAlarmTime";

        String LONG_ROLLOVER_SIX_MONTH_ENDTIME = "rolloverSixMonthEndTime";
        String BOOL_ROLLOVER_SIX_MONTH_SHOW_DIALOG = "rolloverSixMonthShowDialog";

        String INT_BRIGHTNESS = "intBrightness";
        String INT_VOLUME = "intVolume";
        String APP_CURRENT_VERSION = "app_current_version";
    }

    //value of SPKey
    public static class SPValue {
        //        public final int NONE = 0;
        public static final String LEARNING_PERIOD_IN_PROGRESS = "inProgress";
        public static final String LEARNING_PERIOD_ENDED = "ended";
    }


}