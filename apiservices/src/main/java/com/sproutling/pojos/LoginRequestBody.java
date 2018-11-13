package com.sproutling.pojos;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 3/6/17.
 */

public class LoginRequestBody {

    private static final String CLIENT_ID = "4677a7fc91373cbbb035ad4f43c1ebaddfaa8f95344fd37c9efe3ea7afaca08e";
    private static final String GRANT_TYPE_PASSWORD = "password";

    @StringDef({GRANT_TYPE_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GrantType {

    }

    @SerializedName("username")
    private String mUserName;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("grant_type")
    private String mGrantType = GRANT_TYPE_PASSWORD;
    @SerializedName("refresh_token")
    private String mRefreshToken;
    @SerializedName("client_id")
    private String mClientId = CLIENT_ID;


    public LoginRequestBody(String userName, String password, String refreshToken) {
        mUserName = userName;
        mPassword = password;
        mRefreshToken = refreshToken;
    }

    public LoginRequestBody(String userName, String password, @GrantType String grantType, String refreshToken, String clientId) {
        mUserName = userName;
        mPassword = password;
        mGrantType = grantType;
        mRefreshToken = refreshToken;
        mClientId = clientId;
    }

    public LoginRequestBody(String userName, String password) {
        mUserName = userName;
        mPassword = password;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getGrantType() {
        return mGrantType;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getClientId() {
        return mClientId;
    }

    public void setGrantType(@GrantType String grantType) {
        mGrantType = grantType;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }
}
