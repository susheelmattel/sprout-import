package com.sproutling.pojos

import com.google.gson.annotations.SerializedName

/**
 * Created by 322511 on 07/23/18.
 */
data class DevicePresetSettings(@SerializedName("deviceSetting") var deviceSetting: PresetSettings) {
}

