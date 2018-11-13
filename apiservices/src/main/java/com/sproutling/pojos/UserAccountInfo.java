package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/20/17.
 */

public class UserAccountInfo {
    @SerializedName("id")
    private String mId;
    @SerializedName("account_id")
    private String mAccountId;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone_number")
    private String mPhone;
    @SerializedName("invite_token")
    private String mInviteToken;
    @SerializedName("role")
    private String mType;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("push_notification_alert_settings")
    private PushNotification mAlert;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        mAccountId = accountId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getInviteToken() {
        return mInviteToken;
    }

    public void setInviteToken(String inviteToken) {
        mInviteToken = inviteToken;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public PushNotification getAlert() {
        return mAlert;
    }

    public void setAlert(PushNotification alert) {
        mAlert = alert;
    }
}
