/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sproutling.R;
import com.sproutling.services.SSError;
import com.sproutling.services.SSManagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by loren.hung on 2017/1/13.
 */

public class TimelineUtils {

    public static final String LEARNING_PERIOD = "learningPeriod";
    public static final String LEARNING_PERIOD_END = "learningPeriodEnd";
    public static final String NAP = "nap";
    public static final String HEART_RATE = "heartRate";
    public static final String SLEEP_TIP = "sleepTip";
    public static final String YESTERDAY_SLEEP_SUMMARY = "yesterdaySleepSummary";
    public static final String HUMIDITY = "humidity";
    public static final String LIGHT_LEVEL = "lightLevel";
    public static final String ROOM_TEMPERATURE = "roomTemperature";
    public static final String NOISE_LEVEL = "noiseLevel";

    public static List<EventBean> getListEvents(List<SSManagement.SSEvent> events, Calendar birthDate) {
        List<EventBean> listEvents = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            EventBean mEventBean = new EventBean(events.get(i));
            if (mEventBean.getStartTime().getTimeInMillis() > birthDate.getTimeInMillis()) {
                listEvents.add(mEventBean);
            }
        }
        return listEvents;
    }

    public static List<EventBean> getListArticles(List<SSManagement.SSArticle> events, Calendar birthDate) {
        List<EventBean> listEvents = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        for (int i = 0; i < events.size(); i++) {
            SSManagement.SSArticle article = events.get(i);
            calendar.setTimeInMillis(birthDate.getTimeInMillis() + (article.childAge * Const.TIME_MS_DAY));
            listEvents.add(new EventBean(article, calendar));
        }
        return listEvents;
    }


    public static List<EventListDay> getEventListDay(Context context, List<EventBean> listEvents, Calendar birthDate) {
        Map<Integer, EventListDay> map = new HashMap<>();

        List<EventBean> orderedEvents = getOrderedEvents(listEvents);
        for (int i = 0; i < orderedEvents.size(); i++) {
            EventBean eventBean = orderedEvents.get(i);

            insertEvent(map, birthDate, eventBean);

            if (TimelineUtils.LEARNING_PERIOD.equals(eventBean.getEventType()) &&
                    SharedPrefManager.isLearningPeriodDone(context)) {
                insertLPComplete(map, birthDate, eventBean.getEndTime());
            }
        }
        return getOrderedEventList(map);
    }

    private static void insertLPComplete(Map<Integer, EventListDay> map, Calendar birthDate, Calendar endTime) {
        EventBean eventBean = new EventBean();
        eventBean.setStartTime(endTime.getTimeInMillis());
        eventBean.setEndTime(endTime.getTimeInMillis());
        eventBean.setEventType(TimelineUtils.LEARNING_PERIOD_END);

        insertEvent(map, birthDate, eventBean);
    }

    private static void insertEvent(Map<Integer, EventListDay> map, Calendar birthDate, EventBean eventBean) {
        EventListDay eventListDay = getEventListDay(map, eventBean.getStartTime(), birthDate);

        if (NAP.equals(eventBean.getEventType()) && isNightSleep(eventBean)) {
            if (isEarlyNightSleep(eventBean)) { // check previous day first
                Calendar previousDay = Calendar.getInstance();
                previousDay.setTimeInMillis(eventBean.getStartTime().getTimeInMillis() - Const.TIME_MS_DAY);
                EventListDay previousEventListDay = getEventListDay(map, previousDay, birthDate);
                List<EventBean> eventList = previousEventListDay.getEventList();
                if (!eventList.isEmpty()) {
                    boolean added = combineNightSleepEvent(eventList, eventBean);
                    if (!added) {
                        addToEventListDay(eventListDay, eventBean);
                    }
                } else {
                    addToEventListDay(eventListDay, eventBean);
                }
            } else if (isLateNightSleep(eventBean)) {
                addToEventListDay(eventListDay, eventBean);
            }
        } else {
            eventListDay.add(eventBean);
        }
    }

    private static EventBean createNightEvent(EventBean eventBean) {
        EventBean event = new EventBean(eventBean.getChildId(), eventBean.getEventType(), eventBean.getStartDate(), eventBean.getEndDate());
        event.getNightEvents().add(eventBean);
        event.getSpells().addAll(eventBean.getSpells());
        return event;
    }

    private static void addToEventListDay(EventListDay eventListDay, EventBean eventBean) {
        List<EventBean> eventList = eventListDay.getEventList();
        if (!eventList.isEmpty()) {
            boolean added = combineNightSleepEvent(eventList, eventBean);
            if (!added) {
                eventListDay.add(createNightEvent(eventBean));
            }
        } else {
            eventListDay.add(createNightEvent(eventBean));
        }
    }

    private static boolean combineNightSleepEvent(List<EventBean> eventList, EventBean child) {
        for (EventBean parent : eventList) {
            if ((parent.getStartTime().get(Calendar.DAY_OF_MONTH) == child.getStartTime().get(Calendar.DAY_OF_MONTH) &&
                    (isEarlyNightSleep(parent) && isEarlyNightSleep(child)) || (isLateNightSleep(parent) && isLateNightSleep(child))) ||
                    (isLateNightSleep(parent) && isEarlyNightSleep(child))) {
                if (child.getStartTime().before(parent.getStartTime())) {
                    parent.setStartTime(child.getStartTime().getTimeInMillis());
                }
                if (child.getEndTime().after(parent.getEndTime())) {
                    parent.setEndTime(child.getEndTime().getTimeInMillis());
                }
                parent.getNightEvents().add(child);
                parent.getSpells().addAll(child.getSpells());
                return true;
            }
        }
        return false;
    }

    private static EventListDay getEventListDay(Map<Integer, EventListDay> map, Calendar calendar, Calendar birthDate) {
        Calendar copyCalendar = Calendar.getInstance();
        copyCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        if (!map.containsKey((int) (copyCalendar.getTimeInMillis() / Const.TIME_MS_SEC))) {
            EventListDay eventListDay = new EventListDay();
            eventListDay.setTimeInMillis(copyCalendar.getTimeInMillis());
            eventListDay.setChildAge(birthDate);
            map.put((int) (copyCalendar.getTimeInMillis() / Const.TIME_MS_SEC), eventListDay);
            return eventListDay;
        }
        return map.get((int) (copyCalendar.getTimeInMillis() / Const.TIME_MS_SEC));
    }

    private static List<EventBean> getOrderedEvents(List<EventBean> events) {
        List<EventBean> list = new ArrayList<>();
        Set<EventBean> evenQueue = new TreeSet<>(new Comparator<EventBean>() {
            @Override
            public int compare(EventBean lhs, EventBean rhs) {
                int result = 0;

                long time1 = lhs.getStartTime().getTimeInMillis();

                long time2 = rhs.getStartTime().getTimeInMillis();
                long diff = time1 - time2;

                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return result;
            }
        });
        evenQueue.addAll(events);
        list.addAll(evenQueue);
        return list;
    }

    private static List<EventListDay> getOrderedEventList(Map<Integer, EventListDay> map) {
        List<EventListDay> list = new ArrayList<>();
        Set<EventListDay> evenQueue = new TreeSet<>(new Comparator<EventListDay>() {
            @Override
            public int compare(EventListDay lhs, EventListDay rhs) {
                int result = 0;

                long time1 = lhs.getTimeInMillis();

                long time2 = rhs.getTimeInMillis();
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
        for (Integer key : map.keySet()) {
            EventListDay eventListDay = map.get(key);
            eventListDay.sort();
            if (!eventListDay.getEventList().isEmpty()) evenQueue.add(eventListDay);
        }
        list.addAll(evenQueue);
        return list;
    }

    public static final String[] month = {"January", "February", "March", "April", "May", "June"
            , "July", "August", "September", "October", "November", "December"};

    public static final String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
            , "Saturday"};

    public static String getDateString(Calendar calendar) {
        return month[calendar.get(Calendar.MONTH)] + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + ", " +
                calendar.get(Calendar.YEAR);
    }

    public static String getDateString3Word(Calendar calendar) {
        return month[calendar.get(Calendar.MONTH)].substring(0, 3) + " " +
                calendar.get(Calendar.DAY_OF_MONTH) + ", " +
                calendar.get(Calendar.YEAR);
    }

    public static String getDateWeekString(Context context, Calendar pastCalendar) {
        long trimmedPastTime = TimelineUtils.trimTimeToDay(pastCalendar).getTimeInMillis();
        long trimmedCurrentTime = TimelineUtils.trimTimeToDay(Calendar.getInstance()).getTimeInMillis();

        if (trimmedCurrentTime == trimmedPastTime) {
            return context.getString(R.string.today);
        } else if (trimmedCurrentTime - (1000 * 60 * 60 * 24) == trimmedPastTime) {
            return context.getString(R.string.yesterday);
        }
//        else if (currentCalendar.getTimeInMillis() - (1000 * 60 * 60 * 24 * 2) == pastCalendar.getTimeInMillis()) {
//            return mContext.getString(R.string.before_yesterday);
//        }
        return week[pastCalendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + month[pastCalendar.get(Calendar.MONTH)] + " " +
                pastCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDateWeekString(Calendar calendar) {
        return week[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + month[calendar.get(Calendar.MONTH)] + " " +
                calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDateWeekString(long time) {
        Calendar calendar = Calendar.getInstance();
//        Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        return week[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + month[calendar.get(Calendar.MONTH)] + " " +
                calendar.get(Calendar.DAY_OF_MONTH);
    }

//    public static int getHour(EventBean eventBean) {
//        return (int) ((eventBean.getEndTime().getTimeInMillis() - eventBean.getStartTime().getTimeInMillis()) / Const.TIME_MS_HOUR);
//    }
//
//    public static int getMin(EventBean eventBean) {
//        return (int) ((eventBean.getEndTime().getTimeInMillis() - eventBean.getStartTime().getTimeInMillis()) / Const.TIME_MS_MIN % 60);
//    }

//    public static int getHour(int time) {
//        return time / 60;
//    }
//
//    public static int getMin(int time) {
//        return time % 60;
//    }

    public static int getTotalMin(EventBean eventBean) {
        if (eventBean.hasNightEvents()) {
            int sum = 0;
            for (EventBean event : eventBean.getNightEvents()) {
                if (eventBean.hasSpells()) {
                    sum += (int) (eventBean.getTimeAsleep() / Const.TIME_MS_MIN);
                } else {
                    sum += (int) ((event.getEndTime().getTimeInMillis() - event.getStartTime().getTimeInMillis()) / Const.TIME_MS_MIN);
                }
            }
            return sum;
        } else {
            if (eventBean.hasSpells()) {
                return (int) (eventBean.getTimeAsleep() / Const.TIME_MS_MIN);
            } else {
                return (int) ((eventBean.getEndTime().getTimeInMillis() - eventBean.getStartTime().getTimeInMillis()) / Const.TIME_MS_MIN);
            }
        }
    }

    public static String getType(Context context, EventBean eventBean) {
        switch (eventBean.getEventType()) {
            case TimelineUtils.LEARNING_PERIOD:
                return context.getString(R.string.learning_period_started);
            case TimelineUtils.LEARNING_PERIOD_END:
                return context.getString(R.string.learning_period_end);
            case TimelineUtils.NAP:
                Calendar startTime = eventBean.getStartTime();
                if (startTime.get(Calendar.HOUR_OF_DAY) >= 7 && startTime.get(Calendar.HOUR_OF_DAY) < 12) {
                    return context.getString(R.string.morning_nap);
                } else if (startTime.get(Calendar.HOUR_OF_DAY) >= 12 && startTime.get(Calendar.HOUR_OF_DAY) < 19) {
                    return context.getString(R.string.afternoon_nap);
                } else if (startTime.get(Calendar.HOUR_OF_DAY) >= 19 || startTime.get(Calendar.HOUR_OF_DAY) < 7) {
                    return context.getString(R.string.night_sleep);
                }
                break;
            case TimelineUtils.HUMIDITY:
                return context.getString(R.string.humidity);
            case TimelineUtils.HEART_RATE:
                return context.getString(R.string.heartRate);
            case TimelineUtils.LIGHT_LEVEL:
                return context.getString(R.string.lightLevel);
            case TimelineUtils.ROOM_TEMPERATURE:
                return context.getString(R.string.roomTemperature);
            case TimelineUtils.NOISE_LEVEL:
                return context.getString(R.string.noiseLevel);
        }
        return "";
    }

    public static int getTypeIcon(EventBean eventBean) {
        switch (eventBean.getEventType()) {
            case TimelineUtils.LEARNING_PERIOD:
                return R.drawable.ic_learning_period_timeline;
            case TimelineUtils.LEARNING_PERIOD_END:
                return R.drawable.ic_learning_period_timeline;
            case TimelineUtils.NAP:
                Calendar startTime = eventBean.getStartTime();
                if (startTime.get(Calendar.HOUR_OF_DAY) >= 7 && startTime.get(Calendar.HOUR_OF_DAY) < 12) {
                    return R.drawable.ic_zzz_log_sleep;
                } else if (startTime.get(Calendar.HOUR_OF_DAY) >= 12 && startTime.get(Calendar.HOUR_OF_DAY) < 19) {
                    return R.drawable.ic_zzz_log_sleep;
                } else if (startTime.get(Calendar.HOUR_OF_DAY) >= 19 || startTime.get(Calendar.HOUR_OF_DAY) < 7) {
                    return R.drawable.ic_night_sleep;
                }
                break;
            case TimelineUtils.HUMIDITY:
                return R.mipmap.humidity;
            case TimelineUtils.HEART_RATE:
                return R.mipmap.heart_rate;
            case TimelineUtils.LIGHT_LEVEL:
                return R.drawable.light_level;
            case TimelineUtils.ROOM_TEMPERATURE:
                return R.drawable.ic_sc_tip_temperature;
            case TimelineUtils.NOISE_LEVEL:
                return R.drawable.noise_level;
        }
        return R.drawable.ic_zzz_log_sleep;
    }

    public static String getTypeDetail(Context mContext, EventBean eventBean, String childName, String gender) {
        switch (eventBean.getEventType()) {
            case TimelineUtils.LEARNING_PERIOD:
                return String.format(mContext.getString(R.string.learning_period_started_text), childName);
            case TimelineUtils.LEARNING_PERIOD_END:
                return String.format(mContext.getString(R.string.learning_period_end_text),
                        childName,
                        gender.equals("M") ? mContext.getString(R.string.he) : mContext.getString(R.string.she));
            case TimelineUtils.NAP:
                return String.format(mContext.getString(R.string.nap_text), childName,
                        TimelineUtils.formatHoursAndMinutes(mContext, getTotalMin(eventBean)),
                        gender.equals("M") ? mContext.getString(R.string.his) : mContext.getString(R.string.her));
            case TimelineUtils.HUMIDITY:
                return String.format(mContext.getString(R.string.humidity_txt), childName);
            case TimelineUtils.HEART_RATE:
                return String.format(mContext.getString(R.string.heartRate_txt), childName);
            case TimelineUtils.LIGHT_LEVEL:
                return String.format(mContext.getString(R.string.lightLevel_txt), childName);
            case TimelineUtils.ROOM_TEMPERATURE:
                return String.format(mContext.getString(R.string.roomTemperature_txt), childName);
            case TimelineUtils.NOISE_LEVEL:
                return String.format(mContext.getString(R.string.noiseLevel_txt), childName);
        }
        return "";
    }

    public static void handleError(Context mContext, SSError error) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.settings_baby_error_message_title)
                .setMessage(error.toString())
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static String getEventTitle(Context context, EventBean eventBean) {
        String type = getType(context, eventBean);
        if (isNightSleep(context, type)) {
            return isYesterday(eventBean.getStartTime()) ? context.getString(R.string.last_night_summary) : context.getString(R.string.night_sleep);
        }
        return type;
    }

    public static String getSummaryTitle(Context context, EventBean eventBean) {
        return isNightSleep(context, eventBean) ? (isYesterday(eventBean.getStartTime()) ? context.getString(R.string.last_night_summary) : context.getString(R.string.night_sleep)) : context.getString(R.string.Nap_Summary);
    }

    public static boolean isEarlyNightSleep(int hour) { // 0 - 7
        return hour >= 0 && hour < 7;
    }

    public static boolean isEarlyNightSleep(EventBean eventBean) {
        return isEarlyNightSleep(eventBean.getStartTime().get(Calendar.HOUR_OF_DAY));
    }

    public static boolean isLateNightSleep(int hour) { // 19 - 23
        return hour >= 19 && hour <= 23;
    }

    public static boolean isLateNightSleep(EventBean eventBean) {
        return isLateNightSleep(eventBean.getStartTime().get(Calendar.HOUR_OF_DAY));
    }

    public static boolean isNightSleep(int hour) { // 19 - 7
        return hour >= 19 || hour < 7;
    }

    public static boolean isNightSleep(EventBean eventBean) {
        return isNightSleep(eventBean.getStartTime().get(Calendar.HOUR_OF_DAY));
    }

    public static boolean isNightSleep(Context context, EventBean eventBean) {
        return getType(context, eventBean).equals(context.getString(R.string.night_sleep));
    }

    public static boolean isNightSleep(Context context, String type) {
        return type.equals(context.getString(R.string.night_sleep));
    }

    public static boolean isYesterday(Calendar calendar) {
        Calendar pastCalendar = (Calendar) calendar.clone();
        return TimelineUtils.trimTimeToDay(Calendar.getInstance()).getTimeInMillis() - (1000 * 60 * 60 * 24) == TimelineUtils.trimTimeToDay(pastCalendar).getTimeInMillis();
    }

//    public static boolean isLastNightSleep() {
//
//    }

    public static Calendar trimTimeToDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar trimTimeToMin(Calendar calendar) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar trimTimeToSec(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String formatHoursAndMinutesForPrediction(Context context, int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;

        String time;
        if (h > 1 && m > 1) {
            time = String.format(context.getString(R.string.hours_minutes), h, m);
        } else if (h > 1 && m == 1) {
            time = String.format(context.getString(R.string.hours_minute), h, m);
        } else if (h > 1 && m == 0) {
            time = String.format(context.getString(R.string.hours), h);
        } else if (h == 1 && m > 1) {
            time = String.format(context.getString(R.string.hour_minutes), h, m);
        } else if (h == 1 && m == 1) {
            time = String.format(context.getString(R.string.hour_minute), h, m);
        } else if (h == 1 && m == 0) {
            time = String.format(context.getString(R.string.hour), h);
        } else if (h == 0 && m > 1) {
            time = String.format(context.getString(R.string.minutes), m);
        } else {
            return context.getString(R.string.status_detail_asleep_wake_up_time_any);
        }
        return time;
    }

    public static String formatHoursAndMinutes(Context context, int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;

        String time;
        if (h > 1 && m > 1) {
            time = String.format(context.getString(R.string.hours_minutes), h, m);
        } else if (h > 1 && m == 1) {
            time = String.format(context.getString(R.string.hours_minute), h, m);
        } else if (h > 1 && m == 0) {
            time = String.format(context.getString(R.string.hours), h);
        } else if (h == 1 && m > 1) {
            time = String.format(context.getString(R.string.hour_minutes), h, m);
        } else if (h == 1 && m == 1) {
            time = String.format(context.getString(R.string.hour_minute), h, m);
        } else if (h == 1 && m == 0) {
            time = String.format(context.getString(R.string.hour), h);
        } else if (h == 0 && m > 1) {
            time = String.format(context.getString(R.string.minutes), m);
        } else if (h == 0 && m == 1) {
            time = String.format(context.getString(R.string.minute), m);
        } else {
            time = String.format(context.getString(R.string.minute), 0);
        }
        return time;
    }

    public static String formatHoursAndMinutesShort(Context context, int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;

        String time;
        if (h != 0 && m != 0) {
            time = String.format(context.getString(R.string.hour_minute_short), h, m);
        } else if (h != 0) {
            time = String.format(context.getString(R.string.hour_short), h);
        } else {
            time = String.format(context.getString(R.string.minute_short), m);
        }
        return time;
    }
}
