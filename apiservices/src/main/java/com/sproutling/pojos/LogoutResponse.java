package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 3/6/17.
 */

public class LogoutResponse {
    @SerializedName("status")
    private String mStatus;

    public LogoutResponse(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }
}
