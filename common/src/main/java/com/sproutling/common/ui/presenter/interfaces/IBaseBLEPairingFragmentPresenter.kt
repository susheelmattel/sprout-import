package com.sproutling.common.ui.presenter.interfaces

import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.smartconnect.api.constants.FPBLEConstants

interface IBaseBLEPairingFragmentPresenter:IBaseBLEFragmentPresenter {
    fun setPeripheralType(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE)
    fun startBLEScanning()
    fun onStartUpdateClicked(fpModel: FPSmartModel<*>)
}