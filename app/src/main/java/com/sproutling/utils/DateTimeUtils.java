/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.sproutling.App;
import com.sproutling.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by apple on 2016/12/23.
 */

public class DateTimeUtils {

    public static final int MILLI_SEC = 1000;
    public static final int ONE_MINUTE_IN_MS = 60000;
    public static final int FIVE_MINUTE_IN_MS = 5 * ONE_MINUTE_IN_MS;
    public static final int FIFTEEN_HOURS_IN_MS = 15 * 60 * 60000;
    public static final int SEC_PER_MIN = 60;
    public static final int MIN_PER_HOUR = 60;
    public static final int MILLI_SEC_PER_MIN = MILLI_SEC * SEC_PER_MIN;
    public static final int MILLI_SEC_PER_HOUR = MILLI_SEC_PER_MIN * MIN_PER_HOUR;
    private static final String[] MONTH_FULL = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final List<String> MONTH_FULL_LIST = Arrays.asList(MONTH_FULL);
    private final static String l12 = "MM/dd - h:mm aa";
    private final static String l24 = "MM/dd - k:mm";
    private final static String m12 = "h:mm aa";
    private final static String m24 = "k:mm";
    private final static String s12 = "h:mmaa";
    private final static String s24 = "k:mm";
    private final static String s12h = "haa";
    private final static String s24h = "h";
    private static String mFormat;

    public static String getAMPM(Context context, int hourOfDay) {
        return hourOfDay < 12 ? context.getString(R.string.am) : context.getString(R.string.pm);
    }

    public static boolean isAM(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY) >= 0 && calendar.get(Calendar.HOUR_OF_DAY) < 7;
    }

    public static boolean isPM(Calendar start, Calendar end) {
        return start.get(Calendar.HOUR_OF_DAY) >= 19 || end.get(Calendar.HOUR_OF_DAY) >= 19;
    }

    public static Date parseSpellDate(String dateString) throws ParseException {
        ParseException exception;
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            return format.parse(dateString);
        } catch (ParseException ignored) {
        }
        try {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            return format.parse(dateString);
        } catch (ParseException e) {
            exception = e;
        }
        throw exception;
    }

    public static boolean get24HourMode(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    private static void setFormat(Context context) {
        mFormat = get24HourMode(context) ? m24 : m12;
    }

    public static String getFormat(Context context, Calendar calendar) {
        setFormat(context);
        return (DateFormat.format(mFormat, calendar) + "");
    }

    private static void setFormattedEventTime(Context context) {
        mFormat = get24HourMode(context) ? l24 : l12;
    }

    public static String getFormattedEventTime(Context context, Calendar calendar) {
        setFormattedEventTime(context);
        return (DateFormat.format(mFormat, calendar) + "");
    }

    private static void setFormatInChart(Context context) {
        mFormat = get24HourMode(context) ? s24 : s12;
    }

    private static void setFormatInChartHourOnly(Context context) {
        mFormat = get24HourMode(context) ? s24h : s12h;
    }

    public static String getFormatInChart(Context context, Calendar calendar) {
        int minutes = calendar.get(Calendar.MINUTE);
        if (minutes == 0)
            setFormatInChartHourOnly(context);
        else
            setFormatInChart(context);
        return (DateFormat.format(mFormat, calendar) + "");
    }

    public static String getFormattedDateTime(Date date, String timeDivider) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        stringBuilder.append(calendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append(", ");
        stringBuilder.append(calendar.get(Calendar.YEAR));
        stringBuilder.append(" " + timeDivider + " ");
        stringBuilder.append(new SimpleDateFormat("hh:mm a").format(calendar.getTime()));

        return stringBuilder.toString();
    }

    public static String getFormattedDateTime1(Date date) {
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd, yyyy");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(simpleDateFormat1.format(date));
        stringBuilder.append(" ");
        stringBuilder.append(App.getAppContext().getString(R.string.at));
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat2.format(date));
        return stringBuilder.toString();


    }

    public static String getCurrentTimeDifference(Calendar dateToCompare, Calendar dateToCompare1) {
        Calendar mCurrentCalender = dateToCompare1 == null ? Calendar.getInstance() : dateToCompare1;
        String retVal = null;
        if (dateToCompare != null) {
            long diffTime = mCurrentCalender.getTimeInMillis() - dateToCompare.getTimeInMillis();
            int hours = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_HOUR);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_HOUR;
            int mins = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_MIN);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_MIN;
            int secs = (int) (diffTime / DateTimeUtils.MILLI_SEC);
            retVal = String.format(App.getInstance().getString(R.string.sleep_dlg_edit_time_format), hours, mins, secs);
        }
        return retVal;
    }
}
