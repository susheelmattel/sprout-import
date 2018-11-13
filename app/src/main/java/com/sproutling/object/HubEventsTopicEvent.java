/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.EventOuterClass;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubEventsTopicEvent {
    private EventOuterClass.Event mEvent;

    public HubEventsTopicEvent(EventOuterClass.Event event) {
        mEvent = event;
    }

    public EventOuterClass.Event getEvent() {
        return mEvent;
    }
}
