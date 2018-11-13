/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class SSErrorItem {
    private String mMessage;
    private int mLogRef;
    private String mReason;
    private String mPath;

    public String getMessage () {
        return mMessage;
    }

    public void setMessage (String message) {
        mMessage = message;
    }

    public int getLogRef() {
        return mLogRef;
    }

    public void setLogref (int logref) {
        mLogRef = logref;
    }

    public String getReason () {
        return mReason;
    }

    public void setReason (String reason) {
        mReason = reason;
    }

    public String getPath () {
        return mPath;
    }

    public void setPath (String path) {
        mPath = path;
    }
}