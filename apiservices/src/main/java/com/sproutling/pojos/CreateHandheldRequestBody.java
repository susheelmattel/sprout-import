package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by subram13 on 3/15/17.
 */

public class CreateHandheldRequestBody {
    @SerializedName("uuid")
    private String mUuid;
    @SerializedName("token")
    private String mToken;
    @SerializedName("name")
    private String mName;
    @SerializedName("badge")
    private Integer mBadge;
    @SerializedName("locale")
    private String mLocale;
    @SerializedName("language")
    private String mLanguage;
    @SerializedName("timezone")
    private String mTimeZone;
    @SerializedName("ip_address")
    private String mIpAddress;
    @SerializedName("latitude")
    private double mLatitude;
    @SerializedName("longitude")
    private double mLongitude;
    @SerializedName("app_id")
    private String mAppID;

    public CreateHandheldRequestBody(String uuid, String token, Locale locale, String appID) {
        mUuid = uuid;
        mToken = token;
        mName = android.os.Build.MODEL;
        mLocale = locale.getLanguage() + "-" + locale.getCountry();
        mLanguage = mLocale;
        mTimeZone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        mAppID = appID;
    }

    public String getUuid() {
        return mUuid;
    }

    public String getToken() {
        return mToken;
    }

    public String getName() {
        return mName;
    }

    public int getBadge() {
        return mBadge;
    }

    public String getLocale() {
        return mLocale;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getAppID() {
        return mAppID;
    }
}
