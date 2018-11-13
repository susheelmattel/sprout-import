/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 1/25/17.
 */

public class BleConnectionEvent {
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 0;

    @IntDef({CONNECTED, DISCONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BleConnectionStatus {

    }

    private @BleConnectionEvent.BleConnectionStatus int mConnectionStatus;
    private boolean mIsManualDisconnect;

    public BleConnectionEvent(@BleConnectionEvent.BleConnectionStatus int connectionStatus,boolean isManualDisconnect) {
        mConnectionStatus = connectionStatus;
        mIsManualDisconnect=isManualDisconnect;
    }

    public boolean isManualDisconnect() {
        return mIsManualDisconnect;
    }

    public int getConnectionStatus() {
        return mConnectionStatus;
    }
}
