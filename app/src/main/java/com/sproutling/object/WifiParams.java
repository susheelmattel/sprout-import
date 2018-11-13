/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;

/**
 * Created by subram13 on 1/23/17.
 */

public class WifiParams {
    private String mSsid;
    private String mPwd;
    private @ManualNetworkTypeFragment.NetworkSecurityType String mSecurity;

    public WifiParams(String ssid, String pwd,@ManualNetworkTypeFragment.NetworkSecurityType String security) {
        mSsid = ssid;
        mPwd = pwd;
        mSecurity = security;
    }

    public String getSsid() {
        return mSsid;
    }

    public String getPwd() {
        return mPwd;
    }

    public @ManualNetworkTypeFragment.NetworkSecurityType String getSecurity() {
        return mSecurity;
    }
}
