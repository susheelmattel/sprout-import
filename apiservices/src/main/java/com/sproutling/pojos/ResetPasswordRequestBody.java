package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 1/5/18.
 */

public class ResetPasswordRequestBody {
    @SerializedName("phone_number")
    private String mPhoneNumber;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("pin")
    private String mPin;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("password_confirmation")
    private String mPasswordConfirmation;

    public ResetPasswordRequestBody(String email, String phoneNumber, String pin, String password, String passwordConfirmation) {
        mEmail = email;
        mPhoneNumber = phoneNumber;
        mPin = pin;
        mPassword = password;
        mPasswordConfirmation = passwordConfirmation;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getPin() {
        return mPin;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getPasswordConfirmation() {
        return mPasswordConfirmation;
    }

    public String getmEmail() {
        return mEmail;
    }
}
