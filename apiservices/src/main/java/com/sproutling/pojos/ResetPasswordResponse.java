package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 1/5/18.
 */

public class ResetPasswordResponse {
    @SerializedName("status")
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }
}
