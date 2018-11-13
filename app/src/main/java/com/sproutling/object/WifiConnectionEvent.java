/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 1/23/17.
 */

public class WifiConnectionEvent {
    public static final int CONNECTED = 1;
    public static final int NOT_CONNECTED = 0;

    @IntDef({CONNECTED, NOT_CONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HubWifiConnectionStatus {

    }

    private @WifiConnectionEvent.HubWifiConnectionStatus int mConnectionStatus;

    public WifiConnectionEvent(@HubWifiConnectionStatus int connectionStatus) {
        mConnectionStatus = connectionStatus;
    }

    public int getConnectionStatus() {
        return mConnectionStatus;
    }
}
