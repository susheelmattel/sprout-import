package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 1/3/18.
 */

public class ResetPinRequestBody {
    @SerializedName("phone_number")
    private String mPhoneNumber;

    @SerializedName("email")
    private String mEmail;

    public ResetPinRequestBody(String phoneNumber, String email) {
        mPhoneNumber = phoneNumber;
        mEmail = email;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getmEmail() {
        return mEmail;
    }
}
