/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.pojos.events

import com.google.gson.annotations.SerializedName

/**
 * Created by subram13 on 4/10/17.
 */

class PushNotificationData(@field:SerializedName("title")
                           val title: String, @field:SerializedName("body")
                           val body: String)
