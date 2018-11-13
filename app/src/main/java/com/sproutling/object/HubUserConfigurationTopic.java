package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 5/30/17.
 */

public class HubUserConfigurationTopic {
    private Hub.HubUserConfiguration mHubUserConfiguration;

    public HubUserConfigurationTopic(Hub.HubUserConfiguration hubUserConfiguration) {
        mHubUserConfiguration = hubUserConfiguration;
    }

    public Hub.HubUserConfiguration getHubUserConfiguration() {
        return mHubUserConfiguration;
    }
}
