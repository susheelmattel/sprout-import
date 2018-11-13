/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Sleep;

/**
 * Created by bradylin on 5/10/17.
 */

public class HubSleepPredictionTopicEvent {
    private Sleep.SleepPrediction mSleepPrediction;

    public HubSleepPredictionTopicEvent(Sleep.SleepPrediction sleepPrediction) {
        mSleepPrediction = sleepPrediction;
    }

    public Sleep.SleepPrediction getSleepPrediction() {
        return mSleepPrediction;
    }
}
