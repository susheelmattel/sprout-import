package com.sproutling.pojos

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by subram13 on 2/14/18.
 */
data class Device(@SerializedName("id") val Id: String,
                  @SerializedName("account_id") val accountId: String,
                  @SerializedName("owner_id") val ownerId: String,
                  @SerializedName("owner_type") val ownerType: String,
                  @SerializedName("serial") val serial: String,
                  @SerializedName("firmware_version") val firmwareVersion: String,
                  @SerializedName("updated_at") val updatedAt: Date,
                  @SerializedName("created_at") val createdAt: Date,
                  @SerializedName("name") var name: String) {
}