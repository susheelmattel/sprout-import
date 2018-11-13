package com.sproutling.common.utils

import com.sproutling.common.pojos.DeviceParent
import com.sproutling.pojos.DeviceType
import com.sproutling.pojos.OtherSettings
import com.sproutling.pojos.ProductSettings

object AccountData {

    private var deviceParentList: ArrayList<DeviceParent> = ArrayList()
    private lateinit var deviceTypes: ArrayList<DeviceType>

    fun getServerDevices(): ArrayList<DeviceParent> {
        return deviceParentList
    }

    fun saveServerDevice(deviceParent: DeviceParent) {
        deviceParentList.add(deviceParent)
    }

    fun saveDeviceTypes(deviceTypes: ArrayList<DeviceType>) {
        AccountData.deviceTypes = deviceTypes
    }

    fun getDeviceTypes(): ArrayList<DeviceType> {
        return deviceTypes
    }

    fun clearDevices() {
        deviceParentList.clear()
    }

    fun clearAccountData() {
        deviceParentList.clear()
        deviceTypes.clear()
    }

    fun getDeviceSettings(deviceID: String): ProductSettings? {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID))
                return deviceParent.deviceSettings
        }
        return null
    }

    fun setDeviceSettingsPresets(otherSettings: OtherSettings, deviceID: String): Boolean {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID)) {
                deviceParent.deviceSettings.otherSettings = otherSettings
                return true
            }
        }
        return false
    }

    fun setFirmwareOTAFlags(deviceID: String, updateAvailable: Boolean, isMandatoryUpdate: Boolean) {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID)) {
                deviceParent.isFirmwareUpdateAvailable = updateAvailable
                deviceParent.isFwUpdateMandatory = isMandatoryUpdate
            }
        }
    }

    fun updateDeviceName(deviceID: String, name: String) {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID)) {
                deviceParent.device?.name = name
                break
            }
        }
    }

    fun getDeviceBySerial(deviceID: String): DeviceParent? {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID)) {
                return deviceParent
            }
        }
        return null
    }

    fun removeDeviceBySerial(serialID: String) {
        var deviceParent = getDeviceBySerial(serialID);
        if (deviceParent != null) {
            deviceParentList.remove(deviceParent)
        }
    }

    fun updatefirwarestatus(deviceID: String, name: String) {
        for (deviceParent in deviceParentList) {
            if (deviceParent.device?.serial.equals(deviceID)) {
                deviceParent.isFirmwareUpdateAvailable = false
                break
            }
        }
    }
}