/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubBandStatusTopicEvent {
    private Hub.BandStatus mBandStatus;

    public HubBandStatusTopicEvent(Hub.BandStatus bandStatus) {
        mBandStatus = bandStatus;
    }

    public Hub.BandStatus getBandStatus() {
        return mBandStatus;
    }
}
