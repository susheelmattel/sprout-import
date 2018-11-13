/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

/**
 * Created by subram13 on 3/21/17.
 */

public class PushNotificationChannelEvent {
    private String mChannelId;

    public PushNotificationChannelEvent(String channelId) {
        mChannelId = channelId;
    }

    public String getChannelId() {
        return mChannelId;
    }
}
