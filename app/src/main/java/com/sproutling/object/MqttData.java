/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class MqttData {
    private int mHeartRate;
    private int mBattery;

    public int getHeartRate () {
        return mHeartRate;
    }

    public void setHeartRate (int heartRate) {
        mHeartRate = heartRate;
    }
    
    public int getBattery () {
        return mBattery;
    }

    public void setBattery (int battery) {
        mBattery = battery;
    }

}
