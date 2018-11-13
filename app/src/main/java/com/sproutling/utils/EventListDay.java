/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class EventListDay {

    private List<EventBean> mEventList = new ArrayList<>();

    private List<EventBean> mLastNightEventList = new ArrayList<>();

    private long mTimeInMillis;
    private long mSleepNight = 0;
    private long mSleepDay = 0;
    private int mDayOfWeek = 0;
    private int mChildAge = 0;

    private Calendar mCalendar = Calendar.getInstance();

    public EventListDay() {
    }

    public void add(EventBean mEventBean) {
        mEventList.add(mEventBean);
    }

    public void addLastNight(EventBean mEventBean) {
        mLastNightEventList.add(mEventBean);
    }

    public void setTimeInMillis(long timeInMillis) {
        mTimeInMillis = timeInMillis;
        mCalendar.setTimeInMillis(timeInMillis);
        mDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
    }

    public void setChildAge(Calendar birthDate) {
        mChildAge = (int) ((getCalendar().getTimeInMillis() - birthDate.getTimeInMillis()) / 1000 / 60 / 60 / 24);
    }

    public int getChildAge() {
        return mChildAge;
    }

    public int getDayOfTheWeek() {
        return mDayOfWeek;
    }

    public long getSleepNight() {
        return mSleepNight;
    }

    public long getSleepDay() {
        return mSleepDay;
    }

    public void setSleepNight(long sleepNight) {
        mSleepNight += sleepNight;
    }

    public void setSleepDay(long sleepDay) {
        mSleepDay += sleepDay;
    }

    public long getTimeInMillis() {
        return mTimeInMillis;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public List<EventBean> getEventList() {
        return mEventList;
    }

    public List<EventBean> getLastNightEventList() {
        return mLastNightEventList;
    }

    private Set<EventBean> mEventQueue = new TreeSet<>(new Comparator<EventBean>() {
        @Override
        public int compare(EventBean lhs, EventBean rhs) {
            int result = 0;

            long time1 = lhs.getStartTime().getTimeInMillis();

            long time2 = rhs.getStartTime().getTimeInMillis();
            long diff = time1 - time2;

            if (diff > 0) {
                return -1;
            } else if (diff == 0) {
                return 1;
            } else if (diff < 0) {
                return 1;
            }
            return result;
        }
    });

    public void sort() {
        mEventQueue.clear();
        mEventQueue.addAll(mEventList);
//        for (EventBean bean : mEventList) {
//            mEventQueue.add(bean);
//        }
        mEventList.clear();
        mEventList.addAll(mEventQueue);
    }
}
