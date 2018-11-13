package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 4/30/18.
 */

public class UpdateUserRequestBody {
    @SerializedName("email")
    private String mEmail;
    @SerializedName("first_name")
    private String mFirstname;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("phone_number")
    private String mPhoneNumber;
    @SerializedName("invite_token")
    private String mInviteToken;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("password_confirmation")
    private String mPasswordConfirmation;

    public UpdateUserRequestBody(String firstName, String lastName, String email, String phoneNumber) {
        mEmail = email;
        mFirstname = firstName;
        mLastName = lastName;
        mPhoneNumber = phoneNumber;
    }

    public UpdateUserRequestBody(String firstname, String lastName, String email, String phoneNumber, String inviteToken, String password, String passwordConfirmation) {
        mEmail = email;
        mFirstname = firstname;
        mLastName = lastName;
        mPhoneNumber = phoneNumber;
        mInviteToken = inviteToken;
        mPassword = password;
        mPasswordConfirmation = passwordConfirmation;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public void setFirstname(String firstname) {
        mFirstname = firstname;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getInviteToken() {
        return mInviteToken;
    }

    public void setInviteToken(String inviteToken) {
        mInviteToken = inviteToken;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPasswordConfirmation() {
        return mPasswordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        mPasswordConfirmation = passwordConfirmation;
    }
}
