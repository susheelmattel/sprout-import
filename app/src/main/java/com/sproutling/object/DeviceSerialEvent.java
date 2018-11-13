/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

/**
 * Created by subram13 on 2/9/17.
 */

public class DeviceSerialEvent {
    private String mDeviceSerial;

    public DeviceSerialEvent(String deviceSerial) {
        mDeviceSerial = deviceSerial;
    }

    public String getDeviceSerial() {
        return mDeviceSerial;
    }
}
