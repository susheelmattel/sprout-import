/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

/**
 * Created by bradylin on 9/26/17.
 */

public class SSServerException extends SSException {

    private static final String TAG = "SSServerException";

    public SSServerException(SSError error) {
        super(error);
    }

    public SSServerException(SSError error, int responseCode) {
        super(error, responseCode);
    }

    @Override
    public String toString() {
        return "[" + TAG + "] Code: " + getResponseCode() + ", Error: " + getError().toString();
    }
}
