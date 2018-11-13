package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by subram13 on 3/6/17.
 */

public class CreateUserResponse {
    @SerializedName("id")
    private String mId;
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
    @SerializedName("account_id")
    private String mAccountID;
    @SerializedName("role")
    private String mType;
    @SerializedName("createdAt")
    private Date mCreatedAt;
    @SerializedName("updatedAt")
    private Date mUpdatedAt;
    @SerializedName("push_notification_alert_settings")
    private PushNotificationAlertSettings mPushNotificationAlertSettings;

    public CreateUserResponse(String id, String email, String firstname, String lastName, String phoneNumber, String inviteToken, String accountID, String type, Date createdAt, Date updatedAt, PushNotificationAlertSettings pushNotificationAlertSettings) {
        mId = id;
        mEmail = email;
        mFirstname = firstname;
        mLastName = lastName;
        mPhoneNumber = phoneNumber;
        mInviteToken = inviteToken;
        mAccountID = accountID;
        mType = type;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mPushNotificationAlertSettings = pushNotificationAlertSettings;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getFirstName() {
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

    public String getAccountID() {
        return mAccountID;
    }

    public String getType() {
        return mType;
    }

    public PushNotificationAlertSettings getPushNotificationAlertSettings() {
        return mPushNotificationAlertSettings;
    }
}
