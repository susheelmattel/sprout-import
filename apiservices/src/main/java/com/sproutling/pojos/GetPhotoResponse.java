package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 3/10/17.
 */

public class GetPhotoResponse {
    @SerializedName("id")
    private String mId;
    @SerializedName("mine_type")
    private String mMineType;
    @SerializedName("usage")
    private String mUsage;
    @SerializedName("ChildID")
    private String mChildId;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;

    public String getId() {
        return mId;
    }

    public String getMineType() {
        return mMineType;
    }

    public String getUsage() {
        return mUsage;
    }

    public String getChildId() {
        return mChildId;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }
}
