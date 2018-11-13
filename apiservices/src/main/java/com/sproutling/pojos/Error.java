package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 1/4/18.
 */

public class Error {
    @SerializedName("message")
    private String mMessage;
    @SerializedName("reason")
    private String mReason;
    @SerializedName("path")
    private String mPath;
    @SerializedName("logref")
    private int mLogref;

    public String getMessage() {
        return mMessage;
    }

    public String getReason() {
        return mReason;
    }

    public String getPath() {
        return mPath;
    }

    public int getLogref() {
        return mLogref;
    }


}
