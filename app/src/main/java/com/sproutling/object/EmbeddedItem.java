/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

public class EmbeddedItem {
    private SSErrorItem[] mErrors;

    public SSErrorItem[] getSSErrorItem () {
        return mErrors;
    }

    public void setSSErrorItem (SSErrorItem[] errors) {
        mErrors = errors;
    }
}
