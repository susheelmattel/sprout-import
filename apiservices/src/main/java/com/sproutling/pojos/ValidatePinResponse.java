package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by subram13 on 1/4/18.
 */

public class ValidatePinResponse {
    @SerializedName("status")
    private String mStatus;
    @SerializedName("errors")
    private ArrayList<Error> mErrors;

    public String getStatus() {
        return mStatus;
    }

    public boolean isInvalidPin() {
        if (mErrors != null) {
            return mErrors.get(0).getLogref() == 5034;
        } else return false;
    }
}
