package com.sproutling.pojos;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/20/17.
 */

public class Child {
    public static final String GENDER_GIRL = "F";
    public static final String GENDER_BOY = "M";
    public static final String GENDER_UNKNOWN = "U";
    @SerializedName("id")
    private String mId;
    @SerializedName("account_id")
    private String mAccountId;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("gender")
    private String mGender;
    @SerializedName("birth_date")
    private String mBirthDate;
    @SerializedName("due_date")
    private String mDueDate;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("twin_id")
    private String mTwinId;
    @SerializedName("photo_url")
    private String mPhotoUrl;
    private Bitmap mChildPhoto;

    public Bitmap getChildPhoto() {
        return mChildPhoto;
    }

    public void setChildPhoto(Bitmap childPhoto) {
        this.mChildPhoto = childPhoto;
    }

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

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(String birthDate) {
        mBirthDate = birthDate;
    }

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        mDueDate = dueDate;
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

    public String getTwinId() {
        return mTwinId;
    }

    public void setTwinId(String twinId) {
        mTwinId = twinId;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }
}
