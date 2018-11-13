package com.sproutling.pojos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 3/6/17.
 */

public class LoginResponse {
    @SerializedName("access_token")
    private String mAccessToken;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("expires_in")
    private String mExpiresIn;
    @SerializedName("refresh_token")
    private String mRefreshToken;
    @SerializedName("resource_owner_id")
    private String mResourceOwnerId;
    @SerializedName("scopes")
    private String mScopes;
    @SerializedName("token_Type")
    private String mTokenType;

    public LoginResponse(String accessToken, String createdAt, String expiresIn, String refreshToken, String resourceOwnerId, String scopes, String tokenType) {
        mAccessToken = accessToken;
        mCreatedAt = createdAt;
        mExpiresIn = expiresIn;
        mRefreshToken = refreshToken;
        mResourceOwnerId = resourceOwnerId;
        mScopes = scopes;
        mTokenType = tokenType;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public String getExpiresIn() {
        return mExpiresIn;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getResourceOwnerId() {
        return mResourceOwnerId;
    }

    public String getScopes() {
        return mScopes;
    }

    public String getTokenType() {
        return mTokenType;
    }
}
