/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubCLITopicEvent {
    private Hub.HubCLI mHubCLI;

    public HubCLITopicEvent(Hub.HubCLI hubCLI) {
        mHubCLI = hubCLI;
    }

    public Hub.HubCLI getHubCLI() {
        return mHubCLI;
    }
}
