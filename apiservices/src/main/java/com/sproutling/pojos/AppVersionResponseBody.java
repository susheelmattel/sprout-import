package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/19/17.
 */

public class AppVersionResponseBody {
    @SerializedName("api")
    private Api mApi;
    @SerializedName("ios")
    private Api mIos;
    @SerializedName("android")
    private Api mAndroid;

    public Api getApi() {
        return mApi;
    }

    public Api getIos() {
        return mIos;
    }

    public Api getAndroid() {
        return mAndroid;
    }

    public class Api {
        @SerializedName("current_version")
        private String mCurrentVersion;
        @SerializedName("min_required_version")
        private String mMinReqVersion;
        @SerializedName("update_url")
        private String mUpdateUrl;

        public String getCurrentVersion() {
            return mCurrentVersion;
        }

        public String getMinReqVersion() {
            return mMinReqVersion;
        }

        public String getUpdateUrl() {
            return mUpdateUrl;
        }
    }
}
