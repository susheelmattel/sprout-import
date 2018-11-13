package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 12/20/17.
 */

public class User {
    @SerializedName("scope")
    private String mScope;
    @SerializedName("resource_owner_id")
    private String mResourceOwnerId;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("token_type")
    private String mTokenType;
    @SerializedName("expires_in")
    private int mExpiresIn;
    @SerializedName("refresh_token")
    private String mRefreshToken;
    @SerializedName("access_token")
    private String mAccessToken;

    public String getScope() {
        return mScope;
    }

    public void setScope(String scope) {
        mScope = scope;
    }

    public String getResourceOwnerId() {
        return mResourceOwnerId;
    }

    public void setResourceOwnerId(String resourceOwnerId) {
        mResourceOwnerId = resourceOwnerId;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String tokenType) {
        mTokenType = tokenType;
    }

    public int getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        mExpiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
