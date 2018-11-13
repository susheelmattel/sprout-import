/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import com.sproutling.services.SSManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by loren.hung on 2017/1/12.
 */

public class EventBean implements Serializable {

    private static final long serialVersionUID = 886227229901L;
    private String mId;
    private String mChildId = "";
    private String mCreatedAt = "";
    private String mData = "";
    private String mEndDate = "";
    private String mStartDate = "";
    private String mUpdatedAt = "";
    private String mEventType = "";

    private String mLanguage;
    private int mChildAge;
    private String mTitle;
    private String mUrl;
    private String mImageThumbnailUrl;
    private String mImageSmallUrl;
    private String mImageMediumUrl;
    private String mImageLargeUrl;
//    private TimeZone tz = TimeZone.getTimeZone("UTC");
//    private Calendar mStartTime = Calendar.getInstance(tz);
//    private Calendar mEndTime = Calendar.getInstance(tz);
    private Calendar mStartTime = Calendar.getInstance();
    private Calendar mEndTime = Calendar.getInstance();

    private int mSleepTime;
    private String mJsonString;

    private List<EventBean> mNightEvents;
    private List<SSManagement.SSNap.Spell> mSpells;

    public EventBean() {
    }

    public EventBean(JSONObject jsonObject) throws JSONException {
        setId(jsonObject.optString("mId"));
        setChildId(jsonObject.optString("mChildId"));
        setEndDate(jsonObject.optString("mEndDate"), true);
        setStartDate(jsonObject.optString("mStartDate"), true);
        setEventType(jsonObject.optString("mEventType"));
        setCreatedAt(jsonObject.optString("mCreatedAt"));
        setUpdatedAt(jsonObject.optString("mUpdatedAt"));
        mJsonString = jsonObject.toString();
    }

    public EventBean(SSManagement.SSEvent event) {
        setId(event.id);
        setChildId(event.childId);
        setEndDate(event.endDate, true);
        setStartDate(event.startDate, true);
        setEventType(event.eventType);
        setCreatedAt(event.createdAt);
        setUpdatedAt(event.updatedAt);
        mJsonString = event.jsonString;
        if (TimelineUtils.NAP.equals(event.eventType)) {
            mSpells = ((SSManagement.SSNap) event).getSpells();
        }
    }

    public EventBean(SSManagement.SSArticle article, Calendar calendar) {
        Calendar startTime = Calendar.getInstance();

        startTime.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 7, 0
        );

        setId(article.id);
        setEventType("Article");
        setCreatedAt(article.createdAt);
        setLanguage(article.language);
        setStartTime(startTime.getTimeInMillis());
        setEndTime(startTime.getTimeInMillis());
        setTitle(article.title);
        setUrl(article.url);
        setChildAge(article.childAge);
        setImageThumbnailUrl(article.imageThumbnailUrl);
        setImageSmallUrl(article.imageSmallUrl);
        setImageMediumUrl(article.imageMediumUrl);
        setImageLargeUrl(article.imageLargeUrl);

        mJsonString = article.jsonString;
    }

    public EventBean(String childId, String type, String startDate, String endDate) {
        setChildId(childId);
        setEventType(type);
        setStartDate(startDate, true);
        setEndDate(endDate, true);
        mNightEvents = new ArrayList<>();
        if (TimelineUtils.NAP.equals(type)) {
            mSpells = new ArrayList<>();
        }
    }

    public boolean hasNightEvents() {
        return mNightEvents != null && !mNightEvents.isEmpty();
    }

    public List<EventBean> getNightEvents() {
        if (mNightEvents == null) mNightEvents = new ArrayList<>();
        return mNightEvents;
    }

    public EventBean getFirstNightEvent() {
        return mNightEvents.get(0);
    }

    public EventBean getLastNightEvent() {
        return mNightEvents.get(mNightEvents.size() - 1);
    }

    public boolean hasSpells() {
        return mSpells != null && !mSpells.isEmpty();
    }

    public List<SSManagement.SSNap.Spell> getSpells() {
        if (mSpells == null) mSpells = new ArrayList<>();
        return mSpells;
    }

    public int getNumOfWakings() {
        int count = 0;
        for (SSManagement.SSNap.Spell spell : mSpells) {
            count = spell.isWake() ? ++count : count;
        }
        return count;
    }

    public long getTimeAwake() {
        long time = 0;
        for (SSManagement.SSNap.Spell spell : mSpells) {
            time = spell.isWake() ? time + spell.getDuration() : time;
        }
        return time;
    }

    public long getTimeAsleep() {
        long time = 0;
        for (SSManagement.SSNap.Spell spell : mSpells) {
            time = spell.isSleep() ? time + spell.getDuration() : time;
        }
        return time;
    }

    public long getTimeStirring() {
        long time = 0;
        for (SSManagement.SSNap.Spell spell : mSpells) {
            time = spell.isStirring() ? time + spell.getDuration() : time;
        }
        return time;
    }

    private void setLanguage(String language) {
        mLanguage = language;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    private void setChildAge(int childAge) {
        mChildAge = childAge;
    }

    public int getChildAge() {
        return mChildAge;
    }

    private void setImageThumbnailUrl(String imageThumbnailUrl) {
        mImageThumbnailUrl = imageThumbnailUrl;
    }

    public String getImageThumbnailUrl() {
        return mImageThumbnailUrl;
    }

    private void setImageSmallUrl(String imageSmallUrl) {
        mImageSmallUrl = imageSmallUrl;
    }

    public String getImageSmallUrl() {
        return mImageSmallUrl;
    }

    private void setImageMediumUrl(String imageMediumUrl) {
        mImageMediumUrl = imageMediumUrl;
    }

    public String getImageMediumUrl() {
        return mImageMediumUrl;
    }

    private void setImageLargeUrl(String imageLargeUrl) {
        mImageLargeUrl = imageLargeUrl;
    }

    public String getImageLargeUrl() {
        return mImageLargeUrl;
    }


    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    void setEventType(String eventType) {
        mEventType = eventType;
    }

    public String getEventType() {
        return mEventType;
    }

    public void setChildId(String childId) {
        mChildId = childId;
    }

    public String getChildId() {
        return mChildId;
    }

    public void setEndDate(String endDate, boolean withOffset) {
        mEndDate = endDate;
        setEndTime(endDate, withOffset);
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setStartDate(String startDate, boolean withOffset) {
        mStartDate = startDate;
        setStartTime(startDate, withOffset);
    }

    public String getStartDate() {
        return mStartDate;
    }

    private void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    private void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf;
        if (dateString.endsWith("Z")) {
            String[] dateFormat = dateString.split("\\.");

            if (!dateFormat[0].contains("Z")) {
                dateFormat[0] = dateFormat[0] + "Z";
            }
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
//        sdf.setTimeZone(tz);
            dateString = dateFormat[0];
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
        }
        return sdf.parse(dateString);
    }

    private void setStartTime(String startDate, boolean withOffset) {
        try {
            Date date = parseDate(startDate);
            mStartTime.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (startDate.endsWith("Z") && withOffset) {
            mStartTime.add(Calendar.MILLISECOND, (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()));
        }

        if (!mEndDate.equals("")) {
            setSleepTime();
        }
    }

    void setStartTime(long time) {
        mStartTime.setTimeInMillis(time);
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    private void setEndTime(String endDate, boolean withOffset) {
        try {
            Date date = parseDate(endDate);
            mEndTime.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (endDate.endsWith("Z") && withOffset) {
            mEndTime.add(Calendar.MILLISECOND, (TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()));
        }
        if (!mStartDate.equals("")) {
            setSleepTime();
        }
    }

    void setEndTime(long time) {
        mEndTime.setTimeInMillis(time);
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    private void setSleepTime() {
        mSleepTime = (int) (mEndTime.getTimeInMillis() / 1000 - mStartTime.getTimeInMillis() / 1000);
    }

    private int getSleepTime() {
        return mSleepTime / 60;
    }

    @Override
    public String toString() {
        return mJsonString;
    }


}
