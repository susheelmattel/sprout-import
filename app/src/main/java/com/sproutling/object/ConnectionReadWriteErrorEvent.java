/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

/**
 * Created by subram13 on 2/13/17.
 */

public class ConnectionReadWriteErrorEvent {
    private int mErrorEvent;

    public ConnectionReadWriteErrorEvent(int errorEvent) {
        mErrorEvent = errorEvent;
    }

    public int getErrorEvent() {
        return mErrorEvent;
    }
}
