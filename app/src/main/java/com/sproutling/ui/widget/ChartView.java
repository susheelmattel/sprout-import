/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.sproutling.R;
import com.sproutling.services.SSManagement;
import com.sproutling.utils.Compatible;
import com.sproutling.utils.Const;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.TimelineUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by bradylin on 6/15/17.
 */

public class ChartView {

    public static final int MODE_INLINE = 0;
    public static final int MODE_FULL_SCREEN = 1;
    private static final int MODE_DEFAULT = MODE_INLINE;

    private static final float BAR_HEIGHT_NA = 0;
    private static final float BAR_HEIGHT_AWAKE = 1.5f;
    private static final float BAR_HEIGHT_STIRRING = 3;
    private static final float BAR_HEIGHT_ASLEEP = 6;

    private Context mContext;
    private BarChart mChart;
    private EventBean mEventBean;
    private Typeface mTypeface;
    private Calendar mXAxisStartLabel, mXAxisEndLabel;
    private int mMode;

    private int intervals = 1;
    private int minutesInInterval;
    private long durationInMillis;

    public ChartView(Context context, View view) {
        this(context, view, null);
    }

    public ChartView(Context context, View view, EventBean eventBean) {
        mContext = context;
        mChart = (BarChart) view;
        mEventBean = eventBean;
        mTypeface = Typeface.createFromAsset(mContext.getAssets(),  "fonts/Chalet LondonNineteenSixty.otf");
        mMode = MODE_DEFAULT;
    }

    public ChartView setEvent(EventBean eventBean) {
        mEventBean = eventBean;
        return this;
    }

    public ChartView setMode(int mode) {
        mMode = mode;
        return this;
    }

    private boolean isInline() {
        return mMode == MODE_INLINE;
    }

    private boolean isFullScreen() {
        return mMode == MODE_FULL_SCREEN;
    }

    public void init() {
        initChart();

        Calendar eventStartTime = mEventBean.getStartTime();
        long eventStartTimeInMillis = eventStartTime.getTimeInMillis();
        Calendar eventEndTime = mEventBean.getEndTime();
        long eventEndTimeInMillis = eventEndTime.getTimeInMillis();
        durationInMillis = eventEndTimeInMillis - eventStartTimeInMillis;


        if (durationInMillis < Const.TIME_MS_HOUR * 2) {
            // Duration < 2 hours => incremental of 15 mins: 0, 15, 30, 45
            minutesInInterval = 15;
            mXAxisStartLabel = trimTimeByIncremental(eventStartTime, minutesInInterval);
        } else if (durationInMillis < Const.TIME_MS_HOUR * 6) {
            // Duration < 6 hours => incremental of 30 mins: 0, 30
            minutesInInterval = 30;
            mXAxisStartLabel = trimTimeByIncremental(eventStartTime, minutesInInterval);
        } else {
            // Duration >= 6 hours => incremental of 60 mins: 0, 30 or 0?
            minutesInInterval = 60;
            mXAxisStartLabel = trimTimeByIncremental(eventStartTime, minutesInInterval);
        }
        while (mXAxisStartLabel.getTimeInMillis() + Const.TIME_MS_MIN * minutesInInterval * intervals < eventEndTimeInMillis) {
            intervals++;
        }
        mXAxisEndLabel = Calendar.getInstance();
        mXAxisEndLabel.setTimeInMillis(mXAxisStartLabel.getTimeInMillis() + Const.TIME_MS_MIN * minutesInInterval * intervals);

        setXAxis();
        setYAxis();
        setSleepData(mChart);
    }

    private void initChart() {
        mChart.clear();
        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
    }

    private void setXAxis() {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setTypeface(mTypeface);
        xAxis.setTextColor(Compatible.getColor(mContext, R.color.dolphin));
        xAxis.setTextSize(getXAxisTextSize(durationInMillis));
        xAxis.setAxisMaximum(intervals * minutesInInterval);
        xAxis.setLabelCount(intervals * minutesInInterval, true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == mChart.getXChartMin()) {
                    return DateTimeUtils.getFormatInChart(mContext, mXAxisStartLabel);
                } else if (Math.ceil(value) == mChart.getXChartMax()) {
                    return DateTimeUtils.getFormatInChart(mContext, mXAxisEndLabel);
                } else if (value / minutesInInterval % 2 == 0) {
                    Calendar calendar = (Calendar) mXAxisStartLabel.clone();
                    calendar.add(Calendar.MINUTE, (int) value);
                    return DateTimeUtils.getFormatInChart(mContext, calendar);
                }
                return "";
            }
        });
    }

    private float getXAxisTextSize(long durationInMillis) {
        if (durationInMillis > Const.TIME_MS_HOUR * 4) {
            return isInline() ? 6f : 9f;
        } else if (durationInMillis > Const.TIME_MS_HOUR * 6) {
            return isInline() ? 5f : 7f;
        } else if (durationInMillis > Const.TIME_MS_HOUR * 8) {
            return isInline() ? 4f : 5f;
        }
        return isInline() ? 7f : 11f;
    }

    private void setYAxis() {
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setGranularity(0.3f);
        yAxis.setLabelCount(20, false);

        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == 0.3f) {
                    return mContext.getString(R.string.status_title_awake);
                } else if (value == 3f) {
                    return mContext.getString(R.string.status_title_stirring);
                } else if (value == 5.7000003f) {
                    return mContext.getString(R.string.status_title_asleep);
                }
                return "";
            }
        });
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(6f);
        yAxis.setTypeface(mTypeface);
        yAxis.setTextColor(Compatible.getColor(mContext, R.color.dolphin));
        yAxis.setTextSize(11f);
    }

    private void setSleepData(BarChart chart) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int xNum = (int) chart.getXChartMax();
        int[] colorCode = new int[xNum];

        List<SSManagement.SSNap.Spell> spells = getSpells();
        Calendar tempCalendar = (Calendar) mXAxisStartLabel.clone();

        for (int i = 0; i < xNum; i++) {
            BarEntry barEntry = new BarEntry(i, BAR_HEIGHT_NA);
            colorCode[i] = 0;

            for (SSManagement.SSNap.Spell spell : spells) {
                long timeInMillis = tempCalendar.getTimeInMillis();
                long startTimeInMillis = spell.getStartTime().getTimeInMillis();
                long endTimeInMillis = spell.getEndTime().getTimeInMillis();

                if ((timeInMillis <= startTimeInMillis && (timeInMillis + Const.TIME_MS_MIN) >= startTimeInMillis ||
                        timeInMillis <= endTimeInMillis && (timeInMillis + Const.TIME_MS_MIN) >= endTimeInMillis ||
                        timeInMillis >= startTimeInMillis && timeInMillis < endTimeInMillis) &&
                        (tempCalendar.after(mEventBean.getStartTime()) && tempCalendar.before(mEventBean.getEndTime()))) {
                    if (spell.isWake()) {
                        barEntry = new BarEntry(i, BAR_HEIGHT_AWAKE);
                        colorCode[i] = Compatible.getColor(mContext, R.color.color_timeline_awake);
                    } else if (spell.isSleep()) {
                        barEntry = new BarEntry(i, BAR_HEIGHT_ASLEEP);
                        colorCode[i] = Compatible.getColor(mContext, R.color.color_timeline_asleep);
                    } else {
                        barEntry = new BarEntry(i, BAR_HEIGHT_STIRRING);
                        colorCode[i] = Compatible.getColor(mContext, R.color.grey40);
                    }
                    break;
                }
            }

            if (colorCode[i] == 0 &&
                    ((tempCalendar.after(mEventBean.getStartTime()) && tempCalendar.before(spells.get(0).getStartTime())) ||
                            (tempCalendar.before(mEventBean.getEndTime()) && tempCalendar.after(spells.get(spells.size() - 1).getEndTime())))) {
                barEntry = new BarEntry(i, BAR_HEIGHT_ASLEEP);
                colorCode[i] = Compatible.getColor(mContext, R.color.color_timeline_asleep);
            }
            entries.add(barEntry);
            tempCalendar.add(Calendar.MINUTE, 1);
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(colorCode);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        data.setBarWidth(1);
        data.setDrawValues(false);
        chart.setData(data);
    }

    private List<SSManagement.SSNap.Spell> getSpells() {
        List<SSManagement.SSNap.Spell> spells = new ArrayList<>();
        if (mEventBean.hasSpells()) {
            spells = mEventBean.getSpells();
            for (SSManagement.SSNap.Spell spell : spells) {
                spell.setStartTime(TimelineUtils.trimTimeToSec(spell.getStartTime()));
                spell.setEndTime(TimelineUtils.trimTimeToSec(spell.getEndTime()));
            }
        } else {
            if (mEventBean.hasNightEvents()) {
                for (EventBean event : mEventBean.getNightEvents()) {
                    SSManagement.SSNap.Spell spell = new SSManagement.SSNap.Spell();
                    spell.setStartTime(event.getStartTime());
                    spell.setEndTime(event.getEndTime());
                    spell.setType(SSManagement.SSNap.Spell.TYPE_SLEEP);
                    spells.add(spell);
                }
            } else {
                SSManagement.SSNap.Spell spell = new SSManagement.SSNap.Spell();
                spell.setStartTime(mEventBean.getStartTime());
                spell.setEndTime(mEventBean.getEndTime());
                spell.setType(SSManagement.SSNap.Spell.TYPE_SLEEP);
                spells.add(spell);
            }
        }
        return spells;
    }

    private static Calendar trimTimeByIncremental(Calendar calendar, int incremental) {
        Calendar tempCalendar = (Calendar) calendar.clone();
        int minutes = tempCalendar.get(Calendar.MINUTE);
        tempCalendar.set(Calendar.MINUTE, 0);
        tempCalendar.set(Calendar.SECOND, 0);
        tempCalendar.set(Calendar.MILLISECOND, 0);

        int addition = minutes / incremental * incremental;
        tempCalendar.set(Calendar.MINUTE, addition);

        return tempCalendar;
    }
}
