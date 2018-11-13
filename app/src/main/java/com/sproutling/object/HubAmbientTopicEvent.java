/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubAmbientTopicEvent {
    private Hub.HubAmbient mHubAmbient;

    public HubAmbientTopicEvent(Hub.HubAmbient hubAmbient) {
        mHubAmbient = hubAmbient;
    }

    public Hub.HubAmbient getHubAmbient() {
        return mHubAmbient;
    }
}
