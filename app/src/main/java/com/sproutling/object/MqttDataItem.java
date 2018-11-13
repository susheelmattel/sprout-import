/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

/**
 * Created by Xylon on 2017/1/9.
 */

public class MqttDataItem {

    //The Data defined
    private int mIntSproutlingSerial, mIntTimeStamp, mIntBatteryValue, mIntHRValue;
    private String mStrChildId, mStrEventId;

    public MqttDataItem()
    {
        super();
    }

    public int getSproutlingSerial() {
        return mIntSproutlingSerial;
    }
    public void setSproutlingSerial(int serialNumber) {
        mIntSproutlingSerial = serialNumber;
    }

    public String getChildId() {
        return mStrChildId;
    }
    public void setChildId(String childId) {
        mStrChildId = childId;
    }

    public String getEventId() {
        return mStrEventId;
    }
    public void setEventId(String eventId) {
        mStrEventId = eventId;
    }

    public int getTimeStamp() {
        return mIntTimeStamp;
    }
    public void setTimeStamp(int timeStamp) {
        mIntTimeStamp = timeStamp;
    }

    public int getBatteryValue() {
        return mIntBatteryValue;
    }
    public void setBatteryValue(int batteryValue) {
        mIntBatteryValue = batteryValue;
    }

    public int getHRValue() {
        return mIntHRValue;
    }
    public void setHRValue(int hrValue) {
        mIntHRValue = hrValue;
    }
}
