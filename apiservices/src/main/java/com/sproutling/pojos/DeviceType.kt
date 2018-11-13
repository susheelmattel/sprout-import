package com.sproutling.pojos

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DeviceType(@SerializedName("id") val Id: String,
                      @SerializedName("updated_at") val updatedAt: Date,
                      @SerializedName("created_at") val createdAt: Date,
                      @SerializedName("name") val name: String) {
}