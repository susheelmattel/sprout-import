package com.sproutling.common.managers

import android.util.Log
import com.fisherprice.api.models.FPModel
import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.api.utilities.FPUtilities
import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.sproutling.api.SproutlingApi
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.DeviceParent
import com.sproutling.common.pojos.DeviceUUID
import com.sproutling.common.pojos.events.RefreshDevicesEvent
import com.sproutling.common.utils.*
import com.sproutling.pojos.Device
import com.sproutling.pojos.DeviceRequestBody
import com.sproutling.pojos.ProductSettings
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class DeviceCreationManager {


    /**
     * Create device on the server
     * Create random unique ID if the unique ID on the device is empty or is "ffffff-ff..."
     * Create device settings on device creation success
     * @param fpModel: peripheral to which the user has connected successfully
     * */
    fun createDevice(fpModel: FPModel<*>, deviceCreationListener: DeviceCreationListener) {
        mDeviceCreationListener = deviceCreationListener
        var serialIDExists = false
        val serialID: ByteArray

        if (fpModel.uniqueIdentifier == null || fpModel.uniqueIdentifier.isEmpty()) {
            serialID = Utils.getNewDeviceSerialID()
        } else {
            val currentUniqueIdentifierByteArray = fpModel.uniqueIdentifier
            val currentUniqueIdentifierHex = FPUtilities.byteArrayToHex(currentUniqueIdentifierByteArray)
            if (currentUniqueIdentifierHex == CommonConstant.EMPTY_DEVICE_ID_CHECK) {
                serialID = Utils.getNewDeviceSerialID()
            } else {
                serialID = fpModel.uniqueIdentifier
                serialIDExists = true
            }
        }

        val accessToken = AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).userAccount!!.accessToken

        val hexSerialID = Utils.byteArrayToHexWithNoSpaces(serialID)

        val productName = Utils.getDefaultDeviceName(fpModel.peripheralType as FPBLEConstants.CONNECT_PERIPHERAL_TYPE?)
        var childName = ""
        if (AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child != null) {
            childName = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child.firstName + BaseApplication.sInstance!!.applicationContext.getString(R.string.apostrophe_s)
        }
        var deviceNickname = "$childName $productName"
        var samePeripheralDevice = checkPeripheralIsPresent(fpModel.peripheralType as FPBLEConstants.CONNECT_PERIPHERAL_TYPE?)

        if (samePeripheralDevice > 1) {
            val deviceCount = BaseApplication.sInstance!!.applicationContext.getString(R.string.hyphen) + Companion.SPACE + samePeripheralDevice
            deviceNickname = "$childName $productName $deviceCount"
        }

        Log.i(TAG, "deviceNickname " + deviceNickname);

        val device = DeviceRequestBody(AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).child.id, "Child", hexSerialID, Utils.getFWVersion(fpModel), deviceNickname)

        Log.d(TAG, "createDevice : Writing serial ID to API : HEX Serial ID : $hexSerialID")
        SproutlingApi.createDevice(accessToken, device, object : Callback, retrofit2.Callback<Device> {
            override fun onResponse(call: Call<Device>?, response: Response<Device>?) {
                if (response!!.isSuccessful) {
                    createDeviceSettings(accessToken, serialIDExists, response.body()!!, fpModel)
                } else {
                    if (response.code() == CommonConstant.ERROR_CODE_422) {
                        Log.e(TAG, "Device already added to the account. Navigating to pairing success screen.")
                        mDeviceCreationListener.onDeviceCreated(hexSerialID)
                    } else {
                        Log.e(TAG, "Unable to create child device on server")
                        fpModel.disconnect()
                        mDeviceCreationListener.onDeviceCreationFail()
                    }
                }
            }

            override fun onFailure(call: Call<Device>?, t: Throwable?) {
                Log.e(TAG, "Unable to create child device on server")
                mDeviceCreationListener.onDeviceCreationFail()
            }
        })
    }


    /**
     * Create device settings on the server
     * @param accessToken: token for API call
     * @param serialID: Unique ID of a device
     * @param device: response from device creation. Device to which the settings has to be created
     * @param fpModel: peripheral to which the user has connected successfully
     * */
    private fun createDeviceSettings(accessToken: String, serialIDExists: Boolean, device: Device, fpModel: FPModel<*>) {

        val productId = Utils.getIDFromPeripehralValue(fpModel.peripheralType.value)
        val productSettingsRequestBody = ProductSettings(device.name, AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount.resourceOwnerId, device.Id, productId, null, null)
        SproutlingApi.createDeviceSettings(productSettingsRequestBody, object : Callback, retrofit2.Callback<ProductSettings> {

            override fun onResponse(call: Call<ProductSettings>?, response: Response<ProductSettings>?) {

                if (response!!.isSuccessful) {
                    if (serialIDExists) {
                        saveDevicesLocal(device, response!!.body()!!, fpModel)
                    } else {
                        writeIDtoDevice(fpModel, accessToken, device, response!!.body()!!)
                    }
                } else {

                    Log.e(TAG, "Unable to create a device")
                    deleteProduct(device.serial, accessToken)
                    mDeviceCreationListener.onDeviceCreationFail()
                }
            }

            override fun onFailure(call: Call<ProductSettings>?, t: Throwable?) {
                Log.e(TAG, "Unable to create device settings")
                mDeviceCreationListener.onDeviceCreationFail()
            }

        }, accessToken)
    }

    private fun saveDevicesLocal(device: Device, deviceSettings: ProductSettings, fpModel: FPModel<*>) {

        val deviceTypes = AccountData.getDeviceTypes()

        val deviceParent = DeviceParent(false)
        deviceParent.device = device
        deviceParent.deviceSettings = deviceSettings
        deviceParent.peripheralType = Utils.getPeripheralTypeById(deviceSettings.productID, deviceTypes)
        deviceParent.productImage = Utils.getImageByPeripheralType(deviceParent.peripheralType!!)
        deviceParent.viewType = deviceParent.peripheralType!!.value

        AccountData.saveServerDevice(deviceParent)
        SharedPrefManager.saveNewDeviceUUID(BaseApplication.sInstance?.getAppContext(), DeviceUUID(device, fpModel.uuid))
        EventBus.getDefault().post(RefreshDevicesEvent())
        mDeviceCreationListener.onDeviceCreated(device.serial)
    }

    /**
     * Write an ID to the device
     * write ID to device after successfully creation of device and its settings on server
     * If writing device ID fails delete the same device from server on error response
     * @param fpModel: peripheral to which the user has connected successfully
     * @param serialID: Unique ID of a device
     * @param token: token for API call
     * */
    private fun writeIDtoDevice(fpModel: FPModel<*>, accessToken: String, device: Device, productSettings: ProductSettings) {
        Log.d(TAG, "writeIDtoDevice : hex Serial ID : ${device.serial}")
        val deviceSerialByteArray = Utils.hexStringToByteArray(device.serial)
        Log.d(TAG, "writeIDtoDevice : Byte array after conversion Serial ID : $deviceSerialByteArray")
        fpModel.sendUniqueIdentifier(deviceSerialByteArray, object : FPSmartModel.UniqueIdentifierCallback {
            override fun onSent() {
                Log.d(TAG, "Successfully wrote ID to the BLE device")
                saveDevicesLocal(device, productSettings, fpModel)
            }

            override fun onError(i: Int) {
                Log.e(TAG, "Unable to write ID to a device. Failure: $i")
                deleteProduct(device.serial, accessToken)
                mDeviceCreationListener.onDeviceCreationFail()

            }
        })
    }

    /**
     * Deleting device from server
     * @param serialID: Unique ID of a device
     * @param token: token for API call
     * */
    private fun deleteProduct(serialID: String, token: String) {
        Log.d(TAG, "deleteProduct : hex Serial ID : $serialID")
        SproutlingApi.deleteDevicebySerial(token, serialID, object : Callback, retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                Log.e(TAG, "Device successfully removed from server")
                mDeviceCreationListener.onDeviceCreationFail()
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e(TAG, "Unable to remove device from server")
                mDeviceCreationListener.onDeviceCreationFail()

            }
        })
    }

    private fun checkPeripheralIsPresent(connect_PERIPHERAL_TYPE: FPBLEConstants.CONNECT_PERIPHERAL_TYPE?): Int {
        var count = 1
        val deviceParents = AccountData.getServerDevices()
        for (deviceParent: DeviceParent in deviceParents) {
            Log.i(TAG, "List of peripheral " + deviceParent.peripheralType);
            if (deviceParent.peripheralType == connect_PERIPHERAL_TYPE) {
                count++
            }
        }
        return count
    }

    private lateinit var mDeviceCreationListener: DeviceCreationListener

    interface DeviceCreationListener {
        fun onDeviceCreated(deviceSerialID: String)
        fun onDeviceCreationFail()
    }

    companion object {
        private const val SPACE = " "
        private const val TAG = "DeviceCreationManager"
    }
}