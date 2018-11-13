/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubPresenceTopicEvent {
    private Hub.HubPresence mHubPresence;

    public HubPresenceTopicEvent(Hub.HubPresence hubPresence) {
        mHubPresence = hubPresence;
    }

    public Hub.HubPresence getHubPresence() {
        return mHubPresence;
    }
}
