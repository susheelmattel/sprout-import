/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import sproutling.Hub;

/**
 * Created by subram13 on 2/15/17.
 */

public class HubFirmwareUpdateTopicEvent {
    private Hub.FirmwareUpdate mFirmwareUpdate;

    public HubFirmwareUpdateTopicEvent(Hub.FirmwareUpdate firmwareUpdate) {
        mFirmwareUpdate = firmwareUpdate;
    }

    public Hub.FirmwareUpdate getFirmwareUpdate() {
        return mFirmwareUpdate;
    }
}
