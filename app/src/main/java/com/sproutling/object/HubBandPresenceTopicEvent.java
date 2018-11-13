/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubBandPresenceTopicEvent {
    private  Hub.BandPresence mBandPresence;

    public HubBandPresenceTopicEvent(Hub.BandPresence bandPresence) {
        mBandPresence = bandPresence;
    }

    public Hub.BandPresence getBandPresence() {
        return mBandPresence;
    }
}
