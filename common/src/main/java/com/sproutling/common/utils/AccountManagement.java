/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sproutling.common.app.BaseApplication;
import com.sproutling.pojos.Child;
import com.sproutling.pojos.CreateUserResponse;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.pojos.PushNotification;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Raj on 12/20/16.
 */

public class AccountManagement {

    private static final String USER_ACCOUNT_INFO = "USER_ACCOUNT_INFO";
    private static final String USER_ACCOUNT = "USER_ACCOUNT";

    private static final String PREF_KEY_PASSWORD = "password";

    private static final String PREF_KEY_SCOPE = "scope";
    private static final String PREF_KEY_ID = "resourceOwnerId";
    private static final String PREF_KEY_CREATED = "createdAt";
    private static final String PREF_KEY_TYPE = "tokenType";
    private static final String PREF_KEY_EXPIRES = "expiresIn";
    private static final String PREF_KEY_ACCESS = "accessToken";
    private static final String PREF_KEY_REFRESH = "refreshToken";

    // UserAccountInfo
    private static final String PREF_KEY_USER_ID = "user_id";
    private static final String PREF_KEY_USER_ACCOUNT_ID = "user_accountId";
    private static final String PREF_KEY_USER_FIRSTNAME = "user_firstName";
    private static final String PREF_KEY_USER_LASTNAME = "user_lastName";
    private static final String PREF_KEY_USER_EMAIL = "user_email";
    private static final String PREF_KEY_USER_PHONE = "phone";
    private static final String PREF_KEY_USER_TYPE = "type";
    private static final String PREF_KEY_USER_CREATED_AT = "user_createdAt";
    private static final String PREF_KEY_USER_UPDATED_AT = "user_updatedAt";
    private static final String PREF_KEY_USER_PUSH_NOTIFICATION = "user_push_notification";

    // Child
    private static final String PREF_KEY_CHILD = "child";
    private static final String PREF_KEY_CHILD_DEVICE_TYPES = "child_devices_types";
    private static final String PREF_KEY_CHILD_DEVICES = "child_devices";
    private static final String PREF_KEY_CHILD_DEVICES_SETTINGS = "child_devices_settings";
    //    private static final String PREF_KEY_CHILD_ACCOUNT_ID = "child_accountId";
//    private static final String PREF_KEY_CHILD_FIRST_NAME = "child_firstName";
//    private static final String PREF_KEY_CHILD_LAST_NAME = "child_lastName";
//    private static final String PREF_KEY_CHILD_GENDER = "child_gender";
//    private static final String PREF_KEY_CHILD_BIRTH_DATE = "child_birthDate";
//    private static final String PREF_KEY_CHILD_DUE_DATE = "child_dueDate";
//    private static final String PREF_KEY_CHILD_CREATED_AT = "child_createdAt";
//    private static final String PREF_KEY_CHILD_UPDATED_AT = "child_updatedAt";
//    private static final String PREF_KEY_CHILD_TWIN_ID = "child_twinId";
//    private static final String PREF_KEY_CHILD_PHOTO_URL = "child_photoUrl";
    private static final String PREF_KEY_FIRST_TIME_INSTALLATION = "first_time_installation";
    private static final String APP_INSTALLATION = "app_installation";

    private static AccountManagement sInstance;
    private Context mContext;

    //    private User mUser;
    private LoginResponse mLoginResponse;
    //    private UserAccountInfo mUserAccountInfo;
    private PushNotification mPushNotificationSettings;
    private Child mChild;

    private AccountManagement(Context context) {
        mContext = context;
//        mUser = readFromPreferences();
        mLoginResponse = getUserAccount();
//        mUserAccountInfo = readUserAccountInfo();
        mPushNotificationSettings = readPushNotificationSettings();
    }

    public static AccountManagement getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AccountManagement.class) {
                if (sInstance == null) {
                    sInstance = new AccountManagement(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }


//    public UserAccountInfo getUserAccountInfo() {
//        return mUserAccountInfo;
//    }

    public PushNotification getPushNotificationSettings() {
        return mPushNotificationSettings;
    }

    public Child getChild() {
        return mChild;
    }

    public String getAccessToken() {
        return mLoginResponse != null ? mLoginResponse.getAccessToken() : null;
    }

    public String getRefreshToken() {
        return mLoginResponse != null ? mLoginResponse.getRefreshToken() : null;
    }

//    public String getAuthToken() {
//        return getUserAccount().getAccessToken();
//    }

    public boolean isLoggedIn() {
        return mLoginResponse != null && mLoginResponse.getAccessToken() != null;
    }

//    public boolean isTypeCaregiver() {
//        return mUserAccountInfo != null && SSManagement.TYPE_CAREGIVER.equals(mUserAccountInfo.type);
//    }

    public void writePassword(String password) {
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_PASSWORD, password)
                .apply();
    }

    public String readPassword() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(PREF_KEY_PASSWORD, null);
    }


    public void writePushNotificationSettings(PushNotification settings) {
        mPushNotificationSettings = settings;
        String settingsJson = null;
        if (settings != null) {
            Gson gson = new GsonBuilder().create();
            settingsJson = gson.toJson(settings);
        }
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_USER_PUSH_NOTIFICATION, settingsJson)
                .apply();
    }

    private PushNotification readPushNotificationSettings() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String settings = preferences.getString(PREF_KEY_USER_PUSH_NOTIFICATION, null);
        if (settings != null) {
            Gson gson = new Gson();
            return gson.fromJson(settings, PushNotification.class);
        }
        return null;
    }

    public void saveUserAccountInfo(CreateUserResponse createUserResponse) {
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(createUserResponse);
        SharedPrefManager.put(mContext, USER_ACCOUNT_INFO, jsonString);
    }

    public CreateUserResponse getUserAccountInfo() {
        String jsonString = SharedPrefManager.getString(mContext, USER_ACCOUNT_INFO, null);
        if (!TextUtils.isEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, CreateUserResponse.class);
        }
        return null;
    }

    public void saveUserAccount(LoginResponse loginResponse) {

        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(loginResponse);
        SharedPrefManager.put(mContext, USER_ACCOUNT, jsonString);
    }

    public LoginResponse getUserAccount() {
        String jsonString = SharedPrefManager.getString(mContext, USER_ACCOUNT, null);
        if (!TextUtils.isEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, LoginResponse.class);
        }
        return null;
    }

    public void setChild(Child child) {
        mChild = child;
    }

    private Child readChild() {
        return mChild;
    }

    public void clear() {
//        mUser = null;
//        mUserAccountInfo = null;
        mLoginResponse = null;
        mChild = null;
        mPushNotificationSettings = null;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().clear().apply();
        deleteChildPhoto();
    }

    private void deleteChildPhoto() {
        File childPhotoFile = new File(
                BaseApplication.Companion.getSInstance().getAppContext().getFilesDir(), Utils.getChildPhotoFileName());
        childPhotoFile.delete();
    }

    public void saveIsFirstTimeInstallation(Boolean status) {
        mContext.getSharedPreferences(APP_INSTALLATION,MODE_PRIVATE).edit().putBoolean(PREF_KEY_FIRST_TIME_INSTALLATION, status).commit();
    }

    public Boolean getIsFirstTimeInstallation() {
        return mContext.getSharedPreferences(APP_INSTALLATION,MODE_PRIVATE).getBoolean(PREF_KEY_FIRST_TIME_INSTALLATION, true);
    }
}
