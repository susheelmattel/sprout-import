package com.fuhu.pipetest.object;

/**
 * Created by bradylin on 12/13/16.
 */

public class FragmentItem {
    String mTag;
    String mName;

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
