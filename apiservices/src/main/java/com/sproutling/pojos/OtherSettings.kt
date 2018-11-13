package com.sproutling.pojos

import com.google.gson.annotations.SerializedName

/**
 * Created by subram13 on 2/14/18.
 */
data class OtherSettings(@SerializedName("deviceSetting")var deviceSetting: PresetSettings?,@SerializedName("hub") var hubSettings: HubSettings?) {
}

