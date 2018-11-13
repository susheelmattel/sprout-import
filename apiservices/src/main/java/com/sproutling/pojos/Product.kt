package com.sproutling.pojos

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by subram13 on 2/14/18.
 */
data class Product(@SerializedName("id") val Id: String,
                   @SerializedName("updated_at") val updatedAt: Date,
                   @SerializedName("created_at") val createdAt: Date,
                   @SerializedName("name") val name: String) {
}