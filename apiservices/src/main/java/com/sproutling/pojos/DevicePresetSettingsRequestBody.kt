package com.sproutling.pojos

/**
 * Created by 322511 on 07/23/18.
 */
class DevicePresetSettingsRequestBody(name: String, userID: String, deviceID: String, productID: String, pushNotificationSettings: PushNotificationAlertSettings?, devicePresetSettings: DevicePresetSettings, ID: String? = null) : CartwheelProductSettings(name, userID, deviceID, productID, pushNotificationSettings, devicePresetSettings, ID) {
}