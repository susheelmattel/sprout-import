/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

/**
 * Created by bradylin on 12/13/16.
 */

public class FragmentItem {
    private String mTag;
    private String mName;

    public FragmentItem(String tag, String name) {
        mTag = tag;
        mName = name;
    }

    public String getTag() {
        return mTag;
    }

    public String getName() {
        return mName;
    }
}
