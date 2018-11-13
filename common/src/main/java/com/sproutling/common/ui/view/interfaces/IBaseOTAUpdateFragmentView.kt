package com.sproutling.common.ui.view.interfaces

import com.fisherprice.smartconnect.api.constants.FPBLEConstants

interface IBaseOTAUpdateFragmentView:IBaseOTAFragmentView {
    fun showPairingCompletedScreen(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, serialID: String)
    fun showUpdateCompleted(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE)
    fun showUpdateInprogress(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE,progress:Int)
    fun showUpdateFailed(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE)
}