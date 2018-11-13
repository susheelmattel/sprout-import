package com.sproutling.pojos

import com.google.gson.annotations.SerializedName

/**
 * Created by subram13 on 2/20/18.
 */
data class HubSettings(@SerializedName("ledBrightness") var nightLightBrightness: Int,
                       @SerializedName("ledColor") var nightLightColor: Int,
                       @SerializedName("volume") var musicVolumeLevel: Int,
                       @SerializedName("songList") var musicSongs: ArrayList<String>) {
}