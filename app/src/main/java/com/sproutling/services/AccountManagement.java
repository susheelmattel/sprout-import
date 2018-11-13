/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fuhu.states.payloads.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sproutling.App;
import com.sproutling.pojos.Child;
import com.sproutling.pojos.CreateUserResponse;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bradylin on 11/23/16.
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
    private static final String PREF_KEY_CHILD_ID = "child_id";
    private static final String PREF_KEY_CHILD_ACCOUNT_ID = "child_accountId";
    private static final String PREF_KEY_CHILD_FIRST_NAME = "child_firstName";
    private static final String PREF_KEY_CHILD_LAST_NAME = "child_lastName";
    private static final String PREF_KEY_CHILD_GENDER = "child_gender";
    private static final String PREF_KEY_CHILD_BIRTH_DATE = "child_birthDate";
    private static final String PREF_KEY_CHILD_DUE_DATE = "child_dueDate";
    private static final String PREF_KEY_CHILD_CREATED_AT = "child_createdAt";
    private static final String PREF_KEY_CHILD_UPDATED_AT = "child_updatedAt";
    private static final String PREF_KEY_CHILD_TWIN_ID = "child_twinId";
    private static final String PREF_KEY_CHILD_PHOTO_URL = "child_photoUrl";

    private static AccountManagement sInstance;
    private Context mContext;

    private SSManagement.User mUser;
    private SSManagement.UserAccountInfo mUserAccountInfo;
    private SSManagement.PushNotification mPushNotificationSettings;
    private SSManagement.Child mChild;
    private SSManagement.EOLData mEOLData;

    private AccountManagement(Context context) {
        mContext = context;
        mUser = readFromPreferences();
        mUserAccountInfo = readUserAccountInfo();
        mPushNotificationSettings = readPushNotificationSettings();
        mChild = readChild();
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

    public SSManagement.User getUser() {
        return mUser;
    }

    public SSManagement.UserAccountInfo getUserAccountInfo() {
        return mUserAccountInfo;
    }

    public SSManagement.PushNotification getPushNotificationSettings() {
        return mPushNotificationSettings;
    }

    public SSManagement.Child getChild() {
        return mChild;
    }

    public String getAccessToken() {
        return mUser != null ? mUser.accessToken : null;
    }

    public String getRefreshToken() {
        return mUser != null ? mUser.refreshToken : null;
    }

//    public String getAuthToken() {
//        return getUserAccount().getAccessToken();
//    }

    public boolean isLoggedIn() {
        return mUser != null && mUser.accessToken != null;
    }

    public boolean isTypeCaregiver() {
        return mUserAccountInfo != null && SSManagement.TYPE_CAREGIVER.equals(mUserAccountInfo.type);
    }

    public void writePassword(String password) {
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_PASSWORD, password)
                .apply();
    }

    public String readPassword() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(PREF_KEY_PASSWORD, null);
    }

    public void writeToPreferences(SSManagement.User user) {
        mUser = user;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_SCOPE, user.scope)
                .putString(PREF_KEY_ID, user.resourceOwnerId)
                .putString(PREF_KEY_CREATED, user.createdAt)
                .putString(PREF_KEY_TYPE, user.tokenType)
                .putInt(PREF_KEY_EXPIRES, user.expiresIn)
                .putString(PREF_KEY_ACCESS, user.accessToken)
                .putString(PREF_KEY_REFRESH, user.refreshToken)
                .apply();
    }

    private SSManagement.User readFromPreferences() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        if (!preferences.contains(PREF_KEY_ID)) {
            return null;
        }
        SSManagement.User user = new SSManagement.User();
        user.scope = preferences.getString(PREF_KEY_SCOPE, null);
        user.resourceOwnerId = preferences.getString(PREF_KEY_ID, null);
        user.createdAt = preferences.getString(PREF_KEY_CREATED, null);
        user.tokenType = preferences.getString(PREF_KEY_TYPE, null);
        user.expiresIn = preferences.getInt(PREF_KEY_EXPIRES, 0);
        user.accessToken = preferences.getString(PREF_KEY_ACCESS, null);
        user.refreshToken = preferences.getString(PREF_KEY_REFRESH, null);
        return user;
    }

    public void writeUserAccountInfo(SSManagement.UserAccountInfo userAccountInfo) {
        mUserAccountInfo = userAccountInfo;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_USER_ID, userAccountInfo.id)
                .putString(PREF_KEY_USER_ACCOUNT_ID, userAccountInfo.accountId)
                .putString(PREF_KEY_USER_FIRSTNAME, userAccountInfo.firstName)
                .putString(PREF_KEY_USER_LASTNAME, userAccountInfo.lastName)
                .putString(PREF_KEY_USER_EMAIL, userAccountInfo.email)
                .putString(PREF_KEY_USER_PHONE, userAccountInfo.phone)
                .putString(PREF_KEY_USER_TYPE, userAccountInfo.type)
                .putString(PREF_KEY_USER_CREATED_AT, userAccountInfo.createdAt)
                .putString(PREF_KEY_USER_UPDATED_AT, userAccountInfo.updatedAt)
                .apply();
        writePushNotificationSettings(userAccountInfo.alert);
    }

    private SSManagement.UserAccountInfo readUserAccountInfo() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        if (!preferences.contains(PREF_KEY_USER_ID)) {
            return null;
        }
        SSManagement.UserAccountInfo userAccountInfo = new SSManagement.UserAccountInfo();
        userAccountInfo.id = preferences.getString(PREF_KEY_USER_ID, null);
        userAccountInfo.accountId = preferences.getString(PREF_KEY_USER_ACCOUNT_ID, null);
        userAccountInfo.firstName = preferences.getString(PREF_KEY_USER_FIRSTNAME, null);
        userAccountInfo.lastName = preferences.getString(PREF_KEY_USER_LASTNAME, null);
        userAccountInfo.email = preferences.getString(PREF_KEY_USER_EMAIL, null);
        userAccountInfo.phone = preferences.getString(PREF_KEY_USER_PHONE, null);
        userAccountInfo.type = preferences.getString(PREF_KEY_USER_TYPE, null);
        userAccountInfo.createdAt = preferences.getString(PREF_KEY_USER_CREATED_AT, null);
        userAccountInfo.updatedAt = preferences.getString(PREF_KEY_USER_UPDATED_AT, null);
        return userAccountInfo;
    }

    public void writePushNotificationSettings(SSManagement.PushNotification settings) {
        mPushNotificationSettings = settings;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_USER_PUSH_NOTIFICATION, settings != null ? settings.jsonString : null)
                .apply();
    }

    private SSManagement.PushNotification readPushNotificationSettings() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String settings = preferences.getString(PREF_KEY_USER_PUSH_NOTIFICATION, null);
        if (settings != null) {
            try {
                return new SSManagement.PushNotification(new JSONObject(settings));
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public void saveUserAccountInfo(CreateUserResponse createUserResponse) {
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(createUserResponse);
        SharedPrefManager.put(mContext, USER_ACCOUNT_INFO, jsonString);
    }

    public CreateUserResponse getUserAccountInformation() {
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

    public void writeChild(SSManagement.Child child) {
        mChild = child;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit()
                .putString(PREF_KEY_CHILD_ID, child.id)
                .putString(PREF_KEY_CHILD_ACCOUNT_ID, child.accountId)
                .putString(PREF_KEY_CHILD_FIRST_NAME, child.firstName)
                .putString(PREF_KEY_CHILD_LAST_NAME, child.lastName)
                .putString(PREF_KEY_CHILD_GENDER, child.gender)
                .putString(PREF_KEY_CHILD_BIRTH_DATE, child.birthDate)
                .putString(PREF_KEY_CHILD_DUE_DATE, child.dueDate)
                .putString(PREF_KEY_CHILD_CREATED_AT, child.createdAt)
                .putString(PREF_KEY_CHILD_UPDATED_AT, child.updatedAt)
                .putString(PREF_KEY_CHILD_TWIN_ID, child.twinId)
                .putString(PREF_KEY_CHILD_PHOTO_URL, child.photoUrl)
                .apply();
        App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DATAITEM_CHILD, mChild));
    }

    private SSManagement.Child readChild() {
        SharedPreferences preferences = mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        if (!preferences.contains(PREF_KEY_CHILD_ID)) {
            return null;
        }
        SSManagement.Child child = new SSManagement.Child();
        child.id = preferences.getString(PREF_KEY_CHILD_ID, null);
        child.accountId = preferences.getString(PREF_KEY_CHILD_ACCOUNT_ID, null);
        child.firstName = preferences.getString(PREF_KEY_CHILD_FIRST_NAME, null);
        child.lastName = preferences.getString(PREF_KEY_CHILD_LAST_NAME, null);
        child.gender = preferences.getString(PREF_KEY_CHILD_GENDER, null);
        child.birthDate = preferences.getString(PREF_KEY_CHILD_BIRTH_DATE, null);
        child.dueDate = preferences.getString(PREF_KEY_CHILD_DUE_DATE, null);
        child.createdAt = preferences.getString(PREF_KEY_CHILD_CREATED_AT, null);
        child.updatedAt = preferences.getString(PREF_KEY_CHILD_UPDATED_AT, null);
        child.twinId = preferences.getString(PREF_KEY_CHILD_TWIN_ID, null);
        child.photoUrl = preferences.getString(PREF_KEY_CHILD_PHOTO_URL, null);
        return child;
    }

    public void clear() {
        mUser = null;
        mUserAccountInfo = null;
        mChild = null;
        mPushNotificationSettings = null;
        mContext.getSharedPreferences(Utils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().clear().apply();
        deleteChildPhoto();
    }

    private void deleteChildPhoto() {
        File childPhotoFile = new File(App.getAppContext().getFilesDir(), Utils.getChildPhotoFileName());
        childPhotoFile.delete();
    }

    public void setEOLData(SSManagement.EOLData eolData) {
        mEOLData = eolData;
    }

    public SSManagement.EOLData readEOLData() {
        return mEOLData;
    }
}
