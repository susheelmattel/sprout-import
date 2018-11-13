package com.sproutling.pojos

/**
 * Created by subram13 on 2/14/18.
 */
class ProductSettingsRequestBody(name: String, userID: String, deviceID: String, productID: String, pushNotificationSettings: PushNotificationAlertSettings?, otherSettings: OtherSettings, ID: String? = null) : ProductSettings(name, userID, deviceID, productID, pushNotificationSettings, otherSettings, ID) {
}