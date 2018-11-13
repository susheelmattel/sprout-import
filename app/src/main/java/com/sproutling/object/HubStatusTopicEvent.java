/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubStatusTopicEvent {
    private Hub.HubStatus mHubStatus;

    public HubStatusTopicEvent(Hub.HubStatus hubStatus) {
        mHubStatus = hubStatus;
    }

    public Hub.HubStatus getHubStatus() {
        return mHubStatus;
    }
}
