/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import java.util.ArrayList;

/**
 * Created by subram13 on 1/16/17.
 */

public class WifiListEvent {
    private ArrayList<WifiItem> mWifiList;

    public WifiListEvent(ArrayList<WifiItem> wifiList) {
        mWifiList = new ArrayList<>(wifiList);
    }

    public ArrayList<WifiItem> getWifiList() {
        return mWifiList;
    }
}
