/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import com.sproutling.ui.fragment.Setup.ManualNetworkTypeFragment;

/**
 * Created by subram13 on 1/23/17.
 */

public class WifiNetworkTypeEvent {

    private @ManualNetworkTypeFragment.NetworkSecurityType
    String mNetworkType;

    public WifiNetworkTypeEvent(@ManualNetworkTypeFragment.NetworkSecurityType String networkType) {
        mNetworkType = networkType;
    }

    public @ManualNetworkTypeFragment.NetworkSecurityType
    String getNetworkType() {
        return mNetworkType;
    }
}
