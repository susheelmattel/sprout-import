/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by subram13 on 4/10/17.
 */

public class PushNotificationData {
    @SerializedName("title")
    private String mTitle;
    @SerializedName("body")
    private String mBody;

    public PushNotificationData(String title, String body) {
        mTitle = title;
        mBody = body;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }
}
