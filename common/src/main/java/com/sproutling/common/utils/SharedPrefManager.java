/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sproutling.common.pojos.DeviceParent;
import com.sproutling.common.pojos.DeviceUUID;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SharedPrefManager {
    //	public static final int DEFAULT_MODE = Context.MODE_MULTI_PROCESS;
    private static final int DEFAULT_MODE = Context.MODE_PRIVATE;
    //shared preference file name
    private static final String DEFAULT_SHARED_PREF = "default_shared_pref";
    private static final String DEVICE_OBJECT = "DEVICE_OBJECT";
    private static final String CHILD_PHOTO_OBJECT = "CHILD_PHOTO_OBJECT";
    private static final String UNIQUE_IDENTIFIER = "UNIQUE_IDENTIFIER";
    private static final String HANDHELD_ID = "HANDHELD_ID";
    private static final String PUSH_NOTIFICATION_TOKEN_ID = "PUSH_NOTIFICATION_TOKEN_ID";
    private static final String DEVICE_UUID = "DEVICE_UUID";

    private static final String PREF_KEY_CHILD_DEVICEPARENT_LIST = "child_devices_list";

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


    public static void clearDevice(Context context) {
        removeKey(context, DEVICE_OBJECT);
    }


    public static String getHandHeldId(Context context) {
        return getString(context, HANDHELD_ID, null);
    }


    public static String getPushNotificationTokenId(Context context) {
        return getString(context, PUSH_NOTIFICATION_TOKEN_ID, null);
    }

    public static String getUniqueIdentifier(Context context) {
        String uniqueID = getString(context, UNIQUE_IDENTIFIER, null);
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            put(context, UNIQUE_IDENTIFIER, uniqueID);
        }
        return uniqueID;
    }

    public static boolean saveNewDeviceUUID(Context context, DeviceUUID deviceUUID) {
        ArrayList<DeviceUUID> deviceUUIDS = getDeviceUUIDs(context);
        if (deviceUUIDS == null) {
            deviceUUIDS = new ArrayList<>();
        }
        deviceUUIDS.add(deviceUUID);
        String jsonString = Utils.toJsonString(deviceUUIDS);
        return put(context, DEVICE_UUID, jsonString);

    }

    public static ArrayList<DeviceUUID> getDeviceUUIDs(Context context) {
        String deviceJson = getString(context, DEVICE_UUID, null);
        if (deviceJson != null) {
            return toDevicesUUIDListFromJson(deviceJson);
        } else {
            return null;
        }
    }

    public static ArrayList<DeviceUUID> toDevicesUUIDListFromJson(String jsonString) {
        Gson gson = new Gson();
        Type token = new TypeToken<ArrayList<DeviceUUID>>() {
        }.getType();
        return gson.fromJson(jsonString, token);
    }
}