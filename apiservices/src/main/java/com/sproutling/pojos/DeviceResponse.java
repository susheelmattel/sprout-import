package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/20/17.
 */

public class DeviceResponse {
    @SerializedName("id")
    private String mId;
    @SerializedName("owner_id")
    private String mOwnerId;
    @SerializedName("account_id")
    private String mAccountId;
    @SerializedName("name")
    private String mName;
    @SerializedName("serial")
    private String mSerial;
    @SerializedName("firmware_version")
    private String mFirmwareVersion;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("owner_type")
    private String mOwnerType;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getOwnerId() {
        return mOwnerId;
    }

    public void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    public String getAccountId() {
        return mAccountId;
    }

    public void setAccountId(String accountId) {
        mAccountId = accountId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSerial() {
        return mSerial;
    }

    public void setSerial(String serial) {
        mSerial = serial;
    }

    public String getFirmwareVersion() {
        return mFirmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        mFirmwareVersion = firmwareVersion;
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

    public String getOwnerType() {
        return mOwnerType;
    }

    public void setOwnerType(String ownerType) {
        mOwnerType = ownerType;
    }
}
