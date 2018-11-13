/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubBandTelemetryTopicEvent {
    private Hub.BandTelemetry mBandTelemetry;

    public HubBandTelemetryTopicEvent(Hub.BandTelemetry bandTelemetry) {
        mBandTelemetry = bandTelemetry;
    }

    public Hub.BandTelemetry getBandTelemetry() {
        return mBandTelemetry;
    }
}
