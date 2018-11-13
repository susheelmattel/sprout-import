/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class EventListDataWeek {

    private List<EventListDay> mEventList = new ArrayList<>();
    private long mTimeInMillis;
    public boolean isExpand = false;
    public int id = -1;
    private SimpleDateFormat sdFormat = new SimpleDateFormat("MM/dd");

    private Calendar mCalendar = Calendar.getInstance();

    private long mSleepSun = 0;
    private long mSleepMon = 0;
    private long mSleepTue = 0;
    private long mSleepWed = 0;
    private long mSleepThu = 0;
    private long mSleepFri = 0;
    private long mSleepSat = 0;
    private long mSleepAll = 0;

    public void setSleepSun(long sleepSun) {
        mSleepSun += sleepSun;
    }

    public void setSleepMon(long sleepMon) {
        mSleepMon += sleepMon;
    }

    public void setSleepTue(long sleepTue) {
        mSleepTue += sleepTue;
    }

    public void setSleepWed(long sleepWed) {
        mSleepWed += sleepWed;
    }

    public void setSleepThu(long sleepThu) {
        mSleepThu += sleepThu;
    }

    public void setSleepFri(long sleepFri) {
        mSleepFri += sleepFri;
    }

    public void setSleepSat(long sleepSat) {
        mSleepSat += sleepSat;
    }

    public void setSleepAll(long sleepAll) {
        mSleepAll += sleepAll;
    }

    public long getSleepSun() {
        return mSleepSun;
    }

    public long getSleepMon() {
        return mSleepMon;
    }

    public long getSleepTue() {
        return mSleepTue;
    }

    public long getSleepWed() {
        return mSleepWed;
    }

    public long getSleepThu() {
        return mSleepThu;
    }

    public long getSleepFri() {
        return mSleepFri;
    }

    public long getSleepSat() {
        return mSleepSat;
    }

    public long getSleepAll() {
        return mSleepAll;
    }

    public void add(EventListDay mEventListData) {
        mEventList.add(mEventListData);
    }

    public void setTimeInMillis(long timeInMillis) {
        mTimeInMillis = timeInMillis;
        mCalendar.setTimeInMillis(timeInMillis);
    }

    public long getTimeInMillis() {
        return mTimeInMillis;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public List<EventListDay> getEventList() {
        return mEventList;
    }

    public String getDate() {
        return sdFormat.format(mCalendar.getTime());
    }

    public EventListDataWeek() {
    }
}
