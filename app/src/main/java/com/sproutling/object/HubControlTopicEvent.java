/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubControlTopicEvent {
    private Hub.HubControl mHubControl;

    public HubControlTopicEvent(Hub.HubControl hubControl) {
        mHubControl = hubControl;
    }

    public Hub.HubControl getHubControl() {
        return mHubControl;
    }
}
