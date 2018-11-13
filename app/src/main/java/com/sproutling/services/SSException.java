/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

/**
 * Created by bradylin on 11/29/16.
 */

public class SSException extends Exception {

    public static final int SUCCESS_CODE_202 = 202;

    private SSError mError;
    private int mResponseCode;

    public SSException(SSError error) {
        mError = error;
    }

    public SSException(SSError error, int responseCode) {
        mError = error;
        mResponseCode = responseCode;
    }

    public SSError getError() {
        return mError;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    @Override
    public String toString() {
        return "Code: " + mResponseCode + ", Error: " + mError.toString();
    }
}
