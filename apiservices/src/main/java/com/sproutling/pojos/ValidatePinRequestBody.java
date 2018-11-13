package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 1/4/18.
 */

public class ValidatePinRequestBody {

    @SerializedName("phone_number")
    private String mPhoneNumber;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("pin")
    private String mPin;

    public ValidatePinRequestBody(String phoneNumber, String email,  String pin) {
        mPhoneNumber = phoneNumber;
        mPin = pin;
        mEmail = email;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPin() {
        return mPin;
    }
}
