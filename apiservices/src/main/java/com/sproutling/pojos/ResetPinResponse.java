package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by subram13 on 1/3/18.
 */

public class ResetPinResponse {
    @SerializedName("status")
    private String mStatus;
    @SerializedName("errors")
    private ArrayList<Error> mErrors;


    public String getStatus() {
        return mStatus;
    }

    public ArrayList<Error> getErrors() {
        return mErrors;
    }

    public boolean isUserIDWrong() {
        if (mErrors != null) {
            return mErrors.get(0).getLogref() == 5021;
        } else return false;
    }

    public boolean isPinAlreadySent() {
        if (mErrors != null) {
            return mErrors.get(0).getLogref() == 5044;
        } else return false;
    }
}
