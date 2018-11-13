package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by subram13 on 3/15/17.
 */

public class CreateHandheldResponse extends CreateHandheldRequestBody {
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("id")
    private String mId;
    @SerializedName("user_id")
    private String mUserId;


    public CreateHandheldResponse(String uuid,String token, String createdAt, String updatedAt, String id, String userId,Locale locale,String appID) {
        super(uuid,token,locale,appID);
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mId = id;
        mUserId = userId;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getId() {
        return mId;
    }

    public String getUserId() {
        return mUserId;
    }
}
