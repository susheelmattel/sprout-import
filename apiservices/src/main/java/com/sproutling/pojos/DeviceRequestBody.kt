package com.sproutling.pojos

import com.google.gson.annotations.SerializedName

data class DeviceRequestBody(@SerializedName("owner_id") val ownerId: String,
                             @SerializedName("owner_type") val ownerType: String,
                             @SerializedName("serial") val serial: String,
                             @SerializedName("firmware_version") val firmwareVersion: String,
                             @SerializedName("name") val name: String) {
}