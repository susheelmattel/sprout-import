package com.sproutling.common.pojos

import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.sproutling.common.utils.CommonConstant
import com.sproutling.pojos.Device
import com.sproutling.pojos.ProductSettings

data class DeviceParent(var isInRange: Boolean = false) {

    //var fpModel: FPModel<*>? = null
    var device: Device? = null
    lateinit var deviceSettings: ProductSettings
    lateinit var peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE
    var isManualDisconnectedID: Boolean = false
    var productImage: Int = 0
    var viewType: Int = CommonConstant.UNKOWN_TYPE
    var isFirmwareUpdateAvailable = false
    var isFwUpdateMandatory = false

}