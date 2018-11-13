package com.sproutling.common.ui.presenter

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.fisherprice.api.ble.FPManager
import com.fisherprice.api.models.FPModel
import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ble.Constants
import com.sproutling.common.managers.DeviceCreationManager
import com.sproutling.common.managers.OTAManager.DeviceVersionCallback
import com.sproutling.common.managers.OTAManager.getIsNewFirmWareVersionAvailable
import com.sproutling.common.ui.presenter.interfaces.IBaseBLEPairingFragmentPresenter
import com.sproutling.common.ui.view.BaseBLEFragmentView
import com.sproutling.common.ui.view.interfaces.IBaseBLEPairingFragmentView
import com.sproutling.common.utils.Utils
import com.sproutling.events.ServiceNetworkEvent
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class BaseBLEPairingFragmentPresenterImpl(baseBLEPairingFragmentView: IBaseBLEPairingFragmentView) : BaseBLEFragmentPresenterImpl(baseBLEPairingFragmentView), IBaseBLEPairingFragmentPresenter {
    override fun onStartUpdateClicked(fpModel: FPSmartModel<*>) {
        mBaseBLEPairingFragmentView.goToFirmwareUpdateScreen(fpModel)
    }

    override fun setPeripheralType(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE) {
        mPeripheralType = peripheralType
    }

    override fun processBLEBroadcastMessage(action: String, data: Bundle) {
        if (!TextUtils.isEmpty(action)) {
            when (action) {
                Constants.Filter.BLE_NEW_PERIPHERAL -> {
                    Log.d(TAG, "New device found")
                    handleBLEModelChanges()
                }
                Constants.Filter.BLE_CONNECTION_CHANGED -> {
                    Log.d(TAG, "BLE Connection Changed")
                    handleBLEModelChanges()
                }
                Constants.Filter.BLE_MODEL_UPDATED -> {
                    Log.d(TAG, "BLE Model Updated")
                    handleBLEModelChanges()
                }
            }
        }
    }

    private fun handleBLEModelChanges() {
        if (isDeviceCreation) {
            return
        }
        val bleModels = FPManager.instance().modelsArray
        if (bleModels != null && bleModels.isNotEmpty()) {
            Log.d(TAG, "BLE Models count : ${bleModels.size}")
            val iterator = bleModels.iterator()
            iterator.forEach {
                Log.d(TAG, "Device Type : ${it.basePeripheralType.toString()}")
                if (if (mPeripheralType == FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER || mPeripheralType == FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER) {
                            (it.basePeripheralType == FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER || it.basePeripheralType == FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER)
                        } else {
                            it.basePeripheralType == mPeripheralType
                        }) {
                    if (!it.isConnected) {
                        if (mConnectingDeviceMacID.contains(it.uuid)) {
                            it.connect()
                            Log.d(TAG, "Device connect called again for  : " + it.basePeripheralType.toString())
                            Log.d(TAG, "Device connect called again for for MAC ID : " + it.uuid)
                        } else {
                            Log.d(TAG, "mConnectingDeviceMacID did not match for  : " + it.uuid)
                            it.connect()
                            mConnectingDeviceMacID.add(it.uuid)
                            Log.d(TAG, "Device connect call for first time : " + it.basePeripheralType.toString())
                            Log.d(TAG, "Device connect call for first time MAC ID : " + it.uuid)
                        }
                    } else {
                        if (mConnectingDeviceMacID.contains(it.uuid)) {
                            isDeviceCreation = true
                            Log.d(TAG, "Device connected : " + it.basePeripheralType.toString())
                            checkForUpdate(it as FPSmartModel<*>)
                            return@forEach
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "BLE Models empty")
        }
    }

    private fun checkForUpdate(fpModel: FPSmartModel<*>) {
        mBaseBLEPairingFragmentView.showProgressBar(true)

        if (fpModel != null) {
            getIsNewFirmWareVersionAvailable(fpModel.currentFirmwareVersion, fpModel.peripheralType as FPBLEConstants.CONNECT_PERIPHERAL_TYPE, object : DeviceVersionCallback {
                override fun onResponse(isNewVersionAvailable: Boolean, isMandatoryUpdate: Boolean) {
                    Log.d(TAG, "onResponse: isNewVersionAvailable : $isNewVersionAvailable")
                    Log.d(TAG, "onResponse: isMandatoryUpdate : $isMandatoryUpdate")
                    if (isMandatoryUpdate) {
                        mBaseBLEPairingFragmentView.showProgressBar(false)
                        mBaseBLEPairingFragmentView.showFirmwareUpdateDialog(fpModel)

                    } else {
                        createDevice(fpModel)
                    }
                }

                override fun onError(exception: Exception?) {
                    Log.e(BaseBLEFragmentView.TAG, "onResponse: Exception : " + exception!!.localizedMessage)
                    handleError(fpModel)
                }

            })
        }
    }

    private fun handleError(fpModel: FPModel<*>) {
        mBaseBLEPairingFragmentView.showProgressBar(false)
        FPManager.instance().removePairedPeripheral(fpModel.uuid)
        fpModel.disconnect()
        if (Utils.isNetworkAvailable(BaseApplication.sInstance?.getAppContext())) {
            mBaseBLEPairingFragmentView.showGenericErrorDialog()
        } else {
            EventBus.getDefault().post(ServiceNetworkEvent(true))
        }
    }

    private fun createDevice(fpModel: FPModel<*>) {
        var deviceCreationManager = DeviceCreationManager()
        deviceCreationManager.createDevice(fpModel, object : DeviceCreationManager.DeviceCreationListener {
            override fun onDeviceCreated(deviceSerialID: String) {
                mBaseBLEPairingFragmentView.showProgressBar(false)
                mBaseBLEPairingFragmentView.showPairingCompletedScreen(fpModel.basePeripheralType as FPBLEConstants.CONNECT_PERIPHERAL_TYPE, deviceSerialID)
            }

            override fun onDeviceCreationFail() {
                handleError(fpModel)
                isDeviceCreation = false
            }

        })
    }

    override fun startBLEScanning() {
        if (!FPManager.instance().isScanning) {
            FPManager.instance().startScanning()
        }
    }

    companion object {
        const val TAG = "BaseBLEPairingImpl"

    }

    private var mBaseBLEPairingFragmentView = baseBLEPairingFragmentView
    private var isDeviceCreation = false
    private lateinit var mPeripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE
    private var mConnectingDeviceMacID: ArrayList<String> = ArrayList()

}