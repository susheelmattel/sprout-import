package com.sproutling.common.pojos.events

import com.fisherprice.smartconnect.api.constants.FPBLEConstants

class PermissionEvent(val isAddButtonClick: Boolean) {

     lateinit var peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE
    var actionType: Long = 0
    lateinit var deviceSerialID: String
}