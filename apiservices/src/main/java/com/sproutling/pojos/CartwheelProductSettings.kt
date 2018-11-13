package com.sproutling.pojos

import com.google.gson.annotations.SerializedName

/**
 * Created by subram13 on 2/15/18.
 */
open class CartwheelProductSettings(@SerializedName("name") var name: String = "my_settings",
                                    @SerializedName("user_id") var userID: String,
                                    @SerializedName("device_id") var deviceID: String,
                                    @SerializedName("product_id") var productID: String?,
                                    @SerializedName("push_notification_settings") var pushNotificationSettings: PushNotificationAlertSettings?,
                                    @SerializedName("other_settings") var devicePresetSettings: DevicePresetSettings?, @SerializedName("id") var ID: String? = null) {
}