package com.sproutling.pojos;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 3/6/17.
 */

public class CreateUserRequestBody {
    public static final String GUARDIAN = "Guardian";
    public static final String CARE_GIVER = "Caregiver";

    @StringDef({GUARDIAN, CARE_GIVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {

    }

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
    @SerializedName("type")
    private String mType;
    @SerializedName("language")
    private String mLanguage;
    @SerializedName("locale")
    private String mLocale;
    @SerializedName("source_app")
    private String mSourceApp;

    public CreateUserRequestBody(String email, String firstname, String lastName, String phoneNumber,
                                 String inviteToken, String password, String passwordConfirmation,
                                 @Type String type, String language, String locale, String sourceApp) {
        mEmail = email;
        mFirstname = firstname;
        mLastName = lastName;
        //add + to the phone number to match with the accepting criteria for server
        mPhoneNumber = phoneNumber;
        mInviteToken = inviteToken;
        mPassword = password;
        mPasswordConfirmation = passwordConfirmation;
        mType = type;
        mLanguage = language;
        mLocale = locale;
        mSourceApp = sourceApp;
    }


    public String getEmail() {
        return mEmail;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getInviteToken() {
        return mInviteToken;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getPasswordConfirmation() {
        return mPasswordConfirmation;
    }

    public String getType() {
        return mType;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public String getmLocale() {
        return mLocale;
    }

    public String getmSourceApp() {
        return mSourceApp;
    }
}
