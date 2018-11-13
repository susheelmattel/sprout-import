/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

/**
 * Created by bradylin on 9/26/17.
 */

public class SSRequestException extends SSException {

    private static final String TAG = "SSRequestException";

    public SSRequestException(SSError error) {
        super(error);
    }

    public SSRequestException(SSError error, int responseCode) {
        super(error, responseCode);
    }

    @Override
    public String toString() {
        return "[" + TAG + "] Code: " + getResponseCode() + ", Error: " + getError().toString();
    }
}
