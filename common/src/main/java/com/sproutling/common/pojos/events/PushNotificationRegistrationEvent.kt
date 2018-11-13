package com.sproutling.common.pojos.events

import com.sproutling.pojos.CreateHandheldResponse

/**
 * Created by subram13 on 8/1/17.
 */

class PushNotificationRegistrationEvent(val isSuccess: Boolean, val createHandheldResponse: CreateHandheldResponse?, val throwable: Throwable?)
