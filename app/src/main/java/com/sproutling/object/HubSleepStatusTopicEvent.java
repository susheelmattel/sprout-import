/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Sleep;

/**
 * Created by bradylin on 5/10/17.
 */

public class HubSleepStatusTopicEvent {
    private Sleep.SleepStatus mSleepStatus;

    public HubSleepStatusTopicEvent(Sleep.SleepStatus sleepStatus) {
        mSleepStatus = sleepStatus;
    }

    public Sleep.SleepStatus getSleepStatus() {
        return mSleepStatus;
    }
}
