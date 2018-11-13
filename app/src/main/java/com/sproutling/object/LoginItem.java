/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class LoginItem {
    private String mUsername;
    private String mGrantType;
    private String mClientId;
    private String mPassword;
    private String mScope;

    public String getUsername () {
        return mUsername;
    }

    public void setUsername (String username) {
        mUsername = username;
    }

    public String getGrantType () {
        return mGrantType;
    }

    public void setGrantType (String grantType) {
        mGrantType = grantType;
    }

    public String getClientId () {
        return mClientId;
    }

    public void setClientId (String clientId) {
        mClientId = clientId;
    }

    public String getPassword () {
        return mPassword;
    }

    public void setPassword (String password) {
        mPassword = password;
    }

    public String getScope () {
        return mScope;
    }

    public void setScope (String scope) {
        mScope = scope;
    }
}
