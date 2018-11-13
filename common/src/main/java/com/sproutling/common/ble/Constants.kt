package com.sproutling.common.ble

import com.fisherprice.api.constants.FPBLEConstants.NEW_PERIPHERAL
import com.fisherprice.api.models.FPModel.*
import com.fisherprice.api.models.file_transfer.FPFileTransferInfo.*;

class Constants {
    class Filter {
        companion object {
            @JvmStatic  val BLE_CONNECTION_CHANGED: String
                get() = FP_CONNECTION_CHANGED_NOTIF_NOTIF
            @JvmStatic val BLE_MODEL_UPDATED: String
                get() = FP_MODEL_UPDATED_NOTIF
            @JvmStatic val BLE_NEW_PERIPHERAL: String
                get() = NEW_PERIPHERAL
        }
    }

    class OTAFilter {
        companion object {
            @JvmStatic  val OTA_UPDATE_COMPLETED: String
                get() = FP_FIRMWARE_UPDATE_COMPLETION
            @JvmStatic  val OTA_UPDATE_COMPLETE_NOTI: String
                get() = FP_FIRMWARE_UPDATE_COMPLETE_NOTIF
            @JvmStatic  val OTA_TRANSFER_FINISH_NOTI: String
                get() = FP_FIRMWARE_UPDATE_TRANSFER_FINISHED_NOTIF
            @JvmStatic  val OTA_TRANSFER_PROGRESS_NOTI: String
                get() = FP_FIRMWARE_UPDATE_TRANSFER_PROGRESS_NOTIF

            @JvmStatic  val OTA_UPDATE_CANCEL: String
                get() = FP_FIRMWARE_UPDATE_CANCELLING
            @JvmStatic  val OTA_RETRY: String
                get() = FP_FIRMWARE_UPDATE_RETRY
            @JvmStatic  val OTA_TRANSFER_DISCONNECT: String
                get() = FP_FIRMWARE_UPDATE_TRANSFER_DISCONNECTION

        }
    }

    class BLEPeripheralType {
        companion object {
            val DELUXE_SOOTHER: Int
                get() = 1
        }
    }
}