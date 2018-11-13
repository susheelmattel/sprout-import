package com.sproutling.common.managers

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import com.fisherprice.api.fw.FPFirmwareManager
import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.grpc.library.mcpp.mattel.mccp_grpc_android_sdk.update.ChannelGrpcClient
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.OTAProduct
import com.sproutling.common.utils.AccountData
import com.sproutling.common.utils.Utils
import mcpp.update.ChannelOuterClass
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object OTAManager {
    @JvmStatic
    fun initialize(grpcHost: String, grpcPort: Int, isSecuredPort: Boolean) {
        GrpcHost = grpcHost
        GrpcPort = grpcPort
        mIsSecuredPort = isSecuredPort
    }

    @JvmStatic
    fun getIsNewFirmWareVersionAvailable(currentVersion: Int, peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, deviceVersionCallback: DeviceVersionCallback, deviceSerialID: String? = null) {

        var channelGrpcClient = ChannelGrpcClient(GrpcHost, GrpcPort, mIsSecuredPort)
        val deviceChannel = getChannelFrom(peripheralType)
        if (!TextUtils.isEmpty(deviceChannel)) {
            object : AsyncTask<Void, Void, Int>() {
                override fun doInBackground(vararg params: Void?): Int {
                    var retVal = -1
                    try {
                        var response = channelGrpcClient.nextRelease(deviceChannel, currentVersion.toString(), ChannelOuterClass.Delivery.NONE_VALUE)
                        if (response != null) {
                            var respVersion = response.release.version
                            //if response version is empty then the current version is the latest version
                            if (TextUtils.isEmpty(respVersion)) {
                                retVal = currentVersion
                            } else {
                                if (TextUtils.isDigitsOnly(respVersion)) {
                                    retVal = respVersion.toInt()
                                }
                            }

                        }

                    } catch (exception: Exception) {
                        mException = exception
                    }

                    return retVal
                }

                override fun onPostExecute(result: Int?) {
                    channelGrpcClient.shutdownChannel()
                    if (mException == null) {
                        if (result != null) {
                            var isUpdateAvailable = result > currentVersion
                            var isMandatoryUpdate = currentVersion < Utils.getMinRequiredVersion(peripheralType)
                            if (!TextUtils.isEmpty(deviceSerialID)) AccountData.setFirmwareOTAFlags(updateAvailable = isUpdateAvailable, isMandatoryUpdate = isMandatoryUpdate, deviceID = deviceSerialID!!)

                            deviceVersionCallback.onResponse(isUpdateAvailable, isMandatoryUpdate)
                        }
                    } else {
                        deviceVersionCallback.onError(mException)
                    }
                }

                private var mException: Exception? = null
            }.execute()
        } else {
            Log.d(TAG, "getIsNewFirmWareVersionAvailable : deviceChannel is empty or null")
        }
    }

    @JvmStatic
    fun downloadNewFirmwareVersion(currentVersion: Int, peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, firmwareDownloadListener: FirmwareDownloadListener) {
        var channelGrpcClient = ChannelGrpcClient(GrpcHost, GrpcPort, mIsSecuredPort)
        val deviceChannel = getChannelFrom(peripheralType)
        if (!TextUtils.isEmpty(deviceChannel)) {
            object : AsyncTask<Void, Void, Boolean>() {
                override fun doInBackground(vararg params: Void?): Boolean {
                    var retVal = false
                    try {
                        var response = channelGrpcClient.nextRelease(deviceChannel, currentVersion.toString(), ChannelOuterClass.Delivery.INCLUDED_VALUE)
                        if (response != null) {
                            if (response.hasRelease()) {
                                Log.d(TAG, "downloadNewFirmwareVersion : Starting download..")
                                var firmwareData = response.release.payloadBytes.toByteArray()
                                Log.d(TAG, "downloadNewFirmwareVersion : Finished download..")
                                Log.d(TAG, "Firmware Data : $firmwareData")
                                retVal = saveFwData(firmwareData, peripheralType)
                            }
                        }

                    } catch (exception: Exception) {
                        mException = exception
                        Log.e(TAG, "Error in downloading and saving firmware : ${exception.localizedMessage}")
                        retVal = false
                    }

                    return retVal
                }

                override fun onPostExecute(result: Boolean?) {
                    channelGrpcClient.shutdownChannel()
                    if (mException == null) {
                        if (result != null) {
                            if (result) {
                                firmwareDownloadListener.onSuccess()
                            } else firmwareDownloadListener.onFailure()
                        }
                    } else {
                        firmwareDownloadListener.onFailure()
                    }
                }

                private var mException: Exception? = null
            }.execute()
        } else {
            Log.d(TAG, "downloadNewFirmwareVersion : deviceChannel is empty or null")
        }
    }

    @JvmStatic
    fun checkAppUpdate(currentVersion: Int, forceUpdateListener: ForceUpdateListener) {
        var channelGrpcClient = ChannelGrpcClient(GrpcHost, GrpcPort, mIsSecuredPort)
        object : AsyncTask<Void, Void, JSONObject>() {
            override fun doInBackground(vararg params: Void?): JSONObject? {
                try {
                    var response = channelGrpcClient.nextRelease(FORCE_UPDATE_CHANNEL, currentVersion.toString(), ChannelOuterClass.Delivery.INCLUDED_VALUE)
                    var newVersion = response.release.version
                    if (!TextUtils.isEmpty(newVersion)) {
                        var payLoad = response.release.payloadBytes.toByteArray()
                        var jsonObject = JSONObject(String(payLoad))
                        Log.d(TAG, "FORCE UPDATE RESPONSE jsonObject : {$jsonObject}")
                        return jsonObject
                    } else {
                        return null
                    }

                } catch (exception: Exception) {
                    mException = exception
                }

                return null
            }

            override fun onPostExecute(result: JSONObject?) {
                channelGrpcClient.shutdownChannel()
                if (mException == null) {
                    if (result != null) {
                        var minVersion = result.getInt(MIN_VERSION)
                        var updateUrl = result.getString(UPDATE_URL)
                        if (minVersion != null && !TextUtils.isEmpty(updateUrl)) {
                            forceUpdateListener.onResponse(minVersion, updateUrl)
                        } else {
                            forceUpdateListener.onError(Exception("Error parsing update json"))
                        }

                    } else {
                        forceUpdateListener.onError(Exception("UPDATE JSON IS NULL"))
                    }
                } else {
                    forceUpdateListener.onError(mException)
                }

            }

            private var mException: Exception? = null
        }.execute()

    }

    private fun getChannelFrom(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE): String {
        return when (peripheralType) {
            FPBLEConstants.CONNECT_PERIPHERAL_TYPE.DELUXE_SLEEPER -> PRNP_CHANNEL.toLowerCase()
            FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SLEEPER -> RNP_CHANNEL.toLowerCase()
            FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE -> SEAHORSE_CHANNEL.toLowerCase()
            FPBLEConstants.CONNECT_PERIPHERAL_TYPE.LAMP_SOOTHER -> DELUXE_SOOTHER_CHANNEL.toLowerCase()
            else -> {
                ""
            }
        }
    }

    private fun saveFwData(fwData: ByteArray, peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE): Boolean {
        var fileOutputStream = BaseApplication.sInstance?.openFileOutput(getZipFileName(peripheralType), Context.MODE_PRIVATE).use { it?.write(fwData) }
        return if (fileOutputStream != null) {
            extractFwData(peripheralType)
        } else {
            false
        }
    }

    private fun getZipFileName(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE): String {
        return peripheralType.name.plus(ZIP_FILE_FORMAT)
    }

    private fun extractFwData(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE): Boolean {
        val isUnzipSuccess = unZip(peripheralType)
        return if (isUnzipSuccess) {
            val peripheralDirectory = File(BaseApplication.sInstance?.filesDir, peripheralType.name)
            val productJsonFile = File(peripheralDirectory, PRODUCT_JSON_FILE_NAME)
            if (productJsonFile.exists()) {
                Log.d(TAG, "${productJsonFile.absolutePath} exists.")
                val jsonString = getStringFromFile(productJsonFile)
                Log.d(TAG, "Json Data : $jsonString")
                true
            } else {
                Log.d(TAG, "${productJsonFile.absolutePath} does not exists.")
                false
            }
        } else {
            false
        }
    }

    fun updateFirmware(fpModel: FPSmartModel<*>) {
        val peripheralDirectory = File(BaseApplication.sInstance?.filesDir, (fpModel.basePeripheralType as FPBLEConstants.CONNECT_PERIPHERAL_TYPE).name)
        val productJson = getProductJson(peripheralDirectory)
        if (!TextUtils.isEmpty(productJson)) {
            val firmwareInfo = FPFirmwareManager.getInstance().checkForAvailableUpdates(fpModel, JSONObject(productJson))
            val otaProduct = Utils.toObjectFromJson(OTAProduct::class.java, productJson)
            if (firmwareInfo.updateType == FPFirmwareManager.FirmwareUpdateType.FW_UPDATE_OPTIONAL || firmwareInfo.updateType == FPFirmwareManager.FirmwareUpdateType.FW_UPDATE_MANDATORY) {
                val firmwareFile = if (fpModel.currentFirmwareBank == 1) {
                    Log.d(TAG, "updateFirmware : firmware file A selected.")
                    File(peripheralDirectory, otaProduct.updates[0].filename_a)
                } else {
                    Log.d(TAG, "updateFirmware : firmware file B selected.")
                    File(peripheralDirectory, otaProduct.updates[0].filename_b)
                }
                if (firmwareFile.exists()) {
                    val firmwareContent = FPFirmwareManager.getInstance().prepareFirmwareUpdateForModel(fpModel, firmwareInfo, firmwareFile.readBytes())
                    fpModel.startFirmwareUpdate(firmwareContent)
                    Log.d(TAG, "updateFirmware : firmware update starting.")
                } else {
                    Log.e(TAG, "updateFirmware : firmware binary file missing.")
                }

            }
        } else {
            Log.d(TAG, "updateFirmware : productJson file missing.")
        }

    }

    fun deleteFirmwareBinary(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE?) {
        if (peripheralType != null) {
            val peripheralDirectory = File(BaseApplication.sInstance?.filesDir, peripheralType.name)
            deleteFile(peripheralDirectory)
            val zipFile = File(BaseApplication.sInstance?.filesDir, getZipFileName(peripheralType))
            deleteFile(zipFile)
        }
    }

    private fun deleteFile(file: File): Boolean {
        return if (file.exists()) {
            if (file.deleteRecursively()) {
                Log.d(TAG, "Deleted the directory : ${file.absolutePath}")
                true
            } else {
                Log.d(TAG, "Failed delete the directory : ${file.absolutePath}")
                false
            }
        } else {
            Log.d(TAG, "Failed delete the directory: File doesn't exists : ${file.absolutePath}")
            false
        }
    }

    private fun getProductJson(peripheralDirectory: File): String? {
        val productJsonFile = File(peripheralDirectory, PRODUCT_JSON_FILE_NAME)
        var jsonString: String? = null
        if (productJsonFile.exists()) {
            Log.d(TAG, "${productJsonFile.absolutePath} exists.")
            jsonString = getStringFromFile(productJsonFile)
            Log.d(TAG, "Json Data : $jsonString")

        } else {
            Log.d(TAG, "${productJsonFile.absolutePath} does not exists.")
        }
        return jsonString
    }

    private fun unZip(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE): Boolean {
        val directory = BaseApplication.sInstance?.filesDir
        val zipFile = File(directory, peripheralType.name + ZIP_FILE_FORMAT)
        val newDirectory = File(directory, peripheralType.name)
        checkDirectory(newDirectory)
        try {
            var fileInputStream = FileInputStream(zipFile)
            var zipInputStream = ZipInputStream(fileInputStream)
            var zipEntry: ZipEntry? = zipInputStream.nextEntry

            while (zipEntry != null) {
                //ignore folder with mocos name
                if (!zipEntry.name.contains(IGNORE_FOLDER_NAME, true)) {
                    Log.d(TAG, "UnZipping ${zipEntry.name}")
                    if (zipEntry.isDirectory) {
                        checkDirectory(File(directory, zipEntry.name))
                    } else {
                        var fileOutputStream = FileOutputStream(File(newDirectory, zipEntry.name))
                        var datum = zipInputStream.read()
                        while (datum != -1) {
                            fileOutputStream.write(datum)
                            datum = zipInputStream.read()
                        }
                        zipInputStream.closeEntry()
                        fileOutputStream.close()
                    }
                    zipEntry = zipInputStream.nextEntry
                } else {
                    Log.d(TAG, "Skipping UnZipping ${zipEntry.name}")
                    zipEntry = zipInputStream.nextEntry
                }
            }

            zipInputStream.close()
            Log.d(TAG, "Unzipping Success : ${zipFile.absolutePath}")
            return true
        } catch (exception: Exception) {
            Log.e(TAG, "Error unzipping : ${zipFile.absolutePath} : ${exception.localizedMessage}")
            return false
        }
    }

    private fun checkDirectory(file: File) {
        if (!file.isDirectory) {
            file.mkdirs()
        }

    }

    @Throws(Exception::class)
    private fun convertStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream) as Reader?)
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line).append("\n")
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    @Throws(Exception::class)
    private fun getStringFromFile(file: File): String {
        val fin = FileInputStream(file)
        val ret = convertStreamToString(fin)
        //Make sure you close all streams.
        fin.close()
        return ret
    }

    interface DeviceVersionCallback {
        fun onResponse(isNewVersionAvailable: Boolean, isMandatoryUpdate: Boolean)
        fun onError(exception: Exception?)
    }

    interface FirmwareDownloadListener {
        fun onSuccess()
        fun onFailure()
    }

    interface ForceUpdateListener {
        fun onResponse(minVersion: Int, updateUrl: String)
        fun onError(exception: Exception?)
    }

    var GrpcHost = "integration.platform.mattel"
    var GrpcPort = 5000
    var mIsSecuredPort = false
    const val TAG = "OTAManager"
    const val IGNORE_FOLDER_NAME = "__MACOSX/"
    const val ZIP_FILE_FORMAT = ".zip"
    const val PRODUCT_JSON_FILE_NAME = "product.json"
    const val MIN_VERSION = "min_required_version"
    const val UPDATE_URL = "update_url"
    const val PRNP_CHANNEL = "FPSmartConnect_PremiumRNPSleeper_DPV51"
    const val RNP_CHANNEL = "FPSmartConnect_RNPSleeper_CMP94"
    const val SEAHORSE_CHANNEL = "FPSmartConnect_Seahorse_FHC95"
    const val DELUXE_SOOTHER_CHANNEL = "FPSmartConnect_DeluxeTableTopSoother_DYW47"
    const val FORCE_UPDATE_CHANNEL = "fpsmartconnect_android_app"


}