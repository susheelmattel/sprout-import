/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by bradylin on 5/10/17.
 */

public class HubBandStateTopicEvent {
    private Hub.BandState mHubBandState;

    public HubBandStateTopicEvent(Hub.BandState hubPresence) {
        mHubBandState = hubPresence;
    }

    public Hub.BandState getHubPresence() {
        return mHubBandState;
    }
}
