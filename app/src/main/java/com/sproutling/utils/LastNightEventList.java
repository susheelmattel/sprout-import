/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class LastNightEventList implements Serializable {

    private static final long SERIAL_VERSION_UID = 886227229902L;

    private int mLastSleepSum;

    private List<EventBean> mLastNightEventList = new ArrayList<>();

    public void addLastNight(EventBean mEventBean) {
        mLastNightEventList.add(mEventBean);
    }

    public List<EventBean> getLastNightEventList() {
        return mLastNightEventList;
    }

    public void setLastNightEventList(List<EventBean> lastNightEventList) {
        mLastNightEventList = lastNightEventList;
    }

    public LastNightEventList() {
    }

    public void setLastSleepSum(int lastSleepSum) {
        mLastSleepSum = lastSleepSum;
    }

    public int getLastSleepSum() {
        return mLastSleepSum;
    }


}
