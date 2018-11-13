package com.fuhu.pipetest.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.fuhu.pipetest.pipeline.object.LoginResponse;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bradylin on 11/23/16.
 */

public class AccountManagement {
    private static final String SHARED_PREFERENCES_FILE = "test_account";

    static final String PREF_KEY_SCOPE = "scopes";
    static final String PREF_KEY_ID = "resourceOwnerId";
    static final String PREF_KEY_CREATED = "createdAt";
    static final String PREF_KEY_TYPE = "tokenType";
    static final String PREF_KEY_EXPIRES = "expiresIn";
    static final String PREF_KEY_ACCESS = "accessToken";
    static final String PREF_KEY_REFRESH = "refreshToken";

    static AccountManagement sInstance;
    Context mContext;
    LoginResponse mLoginInfo;

    private AccountManagement(Context context) {
        mContext = context;
        mLoginInfo = readLoginInfo();
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

    public LoginResponse getLoginInfo() {
        mLoginInfo = readLoginInfo();
        return mLoginInfo;
    }

    public void writeLoginInfo(final LoginResponse loginInfo) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
//        editor.putString(PREF_KEY_SCOPE, userAccount.scope);
        editor.putString(PREF_KEY_ID, loginInfo.getResourceOwnerId());
//        editor.putString(PREF_KEY_CREATED, userAccount.createdAt);
//        editor.putString(PREF_KEY_TYPE, userAccount.tokenType);
//        editor.putInt(PREF_KEY_EXPIRES, userAccount.expiresIn);
        editor.putString(PREF_KEY_ACCESS, loginInfo.getAccessToken());
//        editor.putString(PREF_KEY_REFRESH, userAccount.refreshToken);
        editor.commit();
    }

    public LoginResponse readLoginInfo() {
        SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        LoginResponse loginInfo = new LoginResponse();

//        SSManagement.UserAccount userAccount = new SSManagement.UserAccount();
//        userAccount.scope = preferences.getString(PREF_KEY_SCOPE, null);
        loginInfo.setResourceOwnerId(preferences.getString(PREF_KEY_ID, null));
//        userAccount.createdAt = preferences.getString(PREF_KEY_CREATED, null);
//        userAccount.tokenType = preferences.getString(PREF_KEY_TYPE, null);
//        userAccount.expiresIn = preferences.getInt(PREF_KEY_EXPIRES, 0);
        loginInfo.setAccessToken(preferences.getString(PREF_KEY_ACCESS, "xxx"));
//        userAccount.refreshToken = preferences.getString(PREF_KEY_REFRESH, null);
        return loginInfo;
    }
}
