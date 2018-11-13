/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class LoginResponse {
    private String mScopes;
    private String mResourceOwnerId;
    private String mCreatedAt;
    private String mTokenType;
    private String mExpiresIn;
    private String mRefreshToken;
    private String mAccessToken;

    public String getScopes() {
        return mScopes;
    }

    public void setScopes(String scopes) {
        mScopes = scopes;
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

    public String getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(String expiresIn) {
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

    @Override
    public String toString() {
        return "[mScopes = " + mScopes + ", mResourceOwnerId = " + mResourceOwnerId + ", mCreatedAt = " + mCreatedAt + ", mTokenType = " + mTokenType + ", mExpiresIn = " + mExpiresIn + ", mRefreshToken = " + mRefreshToken + ", mAccessToken = " + mAccessToken + "]";
    }
}