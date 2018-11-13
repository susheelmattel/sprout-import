package com.sproutling.pojos

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by subram13 on 11/7/17.
 */
data class CreateEventRequestBody(@SerializedName("child_id") val childId: String,
                                  @SerializedName("end_date") val endDate: Date,
                                  @SerializedName("start_date") val startDate: Date,
                                  @SerializedName("event_type") val eventType: String) {
}