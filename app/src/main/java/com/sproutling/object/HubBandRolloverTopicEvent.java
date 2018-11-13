/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubBandRolloverTopicEvent {
    private Hub.BandRollover mBandRollover;

    public HubBandRolloverTopicEvent(Hub.BandRollover bandRollover) {
        mBandRollover = bandRollover;
    }

    public Hub.BandRollover getBandRollover() {
        return mBandRollover;
    }
}
