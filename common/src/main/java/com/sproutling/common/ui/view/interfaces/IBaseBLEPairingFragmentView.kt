package com.sproutling.common.ui.view.interfaces

import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.smartconnect.api.constants.FPBLEConstants

interface IBaseBLEPairingFragmentView:IBaseBLEFragmentView {
    fun showPairingCompletedScreen(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, serialID: String)
    fun showFirmwareUpdateDialog(fpModel: FPSmartModel<*>)
    fun goToFirmwareUpdateScreen(fpModel: FPSmartModel<*>)
}