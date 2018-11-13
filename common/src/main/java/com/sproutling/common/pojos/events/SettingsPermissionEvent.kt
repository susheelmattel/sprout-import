package com.sproutling.common.pojos.events

import com.fisherprice.smartconnect.api.constants.FPBLEConstants

class SettingsPermissionEvent( var deviceSerialID: String, var peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE) {
}