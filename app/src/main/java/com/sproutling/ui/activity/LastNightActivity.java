/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.fuhu.states.interfaces.IStatePayload;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.sproutling.R;
import com.sproutling.ui.dialog.NightSleepDialog;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Compatible;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.LastNightEventList;
import com.sproutling.utils.TimelineUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by loren.hung on 2016/12/29.
 */

public class LastNightActivity extends BaseMqttActivity {

    private Context mContext;

    private BarChart mChart;
    private static boolean sIsPortrait;
    private NightSleepDialog mNightSleepDialog;
    private LastNightEventList mLastNightEventList = new LastNightEventList();
    private ShTextView mTimeAsleep, mTitle, mEditTime;
    private Typeface mTfLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline_nap_summary);
        mContext = this;
        mLastNightEventList = (LastNightEventList) getIntent().getSerializableExtra("Event");
        findViews();
    }

    private void findViews() {
        mEditTime = (ShTextView) findViewById(R.id.edit_time);
        mEditTime.setText(getString(R.string.manually_adjust_nap));
        mTfLight = mEditTime.getTypeface();
        FrameLayout nightDlg = (FrameLayout) findViewById(R.id.night_dlg);
        findViewById(R.id.screen_change).setOnClickListener(mOnClickListener);
        findViewById(R.id.navigation_back).setOnClickListener(mOnClickListener);
        mTitle = (ShTextView) findViewById(R.id.navigation_title);
        mTitle.setText(getString(R.string.Night_Sleep_Summary));
        findViewById(R.id.navigation_action).setVisibility(View.GONE);
        mTimeAsleep = (ShTextView) findViewById(R.id.time_asleep);
        mTimeAsleep.setText(TimelineUtils.formatHoursAndMinutesShort(this, mLastNightEventList.getLastSleepSum()));

        mChart = (BarChart) findViewById(R.id.last_night_barChart);
        initChart(mChart);

        nightDlg.setVisibility(!sIsPortrait ? View.VISIBLE : View.GONE);

        setLastNightData(mChart);

        nightDlg.setOnClickListener(mOnClickListener);

        mNightSleepDialog = new NightSleepDialog(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sIsPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_timeline_nap_summary);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_timeline_nap_summary);
        }
        findViews();

    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.screen_change:
                    if (sIsPortrait) {
                        sIsPortrait = false;
                        onResume();
                    } else {
                        sIsPortrait = true;
                        onResume();
                    }
                    break;
                case R.id.night_dlg:
                    mNightSleepDialog.show();
                    break;
                case R.id.navigation_back:
                    sIsPortrait = false;
                    finish();
                    break;

                default:
            }
        }
    };

    @Override
    public void onBackPressed() {
        sIsPortrait = false;
        finish();
    }

    private void setLastNightData(BarChart mChart) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int num = 12 * 60;
        int offset = 0;
        int[] colorCode = new int[num];
        long[] startPoint = new long[mLastNightEventList.getLastNightEventList().size()];
        long[] endPoint = new long[mLastNightEventList.getLastNightEventList().size()];

        Calendar mCalendar = Calendar.getInstance();
//        Calendar mCalendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        for (int j = 0; j < num; j++) {

            colorCode[j] = Compatible.getColor(mContext, R.color.color_timeline_asleep);

            yVals1.add(new BarEntry(j, 0));

        }

        for (int i = 0; i < mLastNightEventList.getLastNightEventList().size(); i++) {

            EventBean mEventBean = mLastNightEventList.getLastNightEventList().get(i);

            mCalendar.setTimeInMillis(mLastNightEventList.getLastNightEventList().get(i).getStartTime().getTimeInMillis());

            if (mCalendar.get(Calendar.HOUR_OF_DAY) > 0 && mCalendar.get(Calendar.HOUR_OF_DAY) < 7) {
//                mCalendar.add(Calendar.DAY_OF_MONTH,-1);
                mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                offset = 5 * 60;
            } else {
                offset = 0;
                mCalendar.set(Calendar.HOUR_OF_DAY, 19);
            }


            startPoint[i] = ((mEventBean.getStartTime().getTimeInMillis() - mCalendar.getTimeInMillis()) / 1000 / 60) + offset;
            endPoint[i] = ((mEventBean.getEndTime().getTimeInMillis() - mEventBean.getStartTime().getTimeInMillis()) / 1000 / 60) + startPoint[i];

            if (startPoint[i] < 0) {
                startPoint[i] = 0;
            }

            if (endPoint[i] > num) {
                endPoint[i] = num;
            }

            for (int k = (int) startPoint[i]; k < endPoint[i]; k++) {

//                colorCode[k] = Compatible.getColor(mContext,R.color.color_timeline_asleep);
                yVals1.add(k, new BarEntry(k, 6));
            }

        }


        BarDataSet set1;

        set1 = new BarDataSet(yVals1, "");

        set1.setColors(colorCode);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(8f);
        data.setBarWidth(1);
        data.setDrawValues(false);
        mChart.setData(data);

    }


    @Override
    protected void onDestroy() {

        if (mNightSleepDialog.isShowing()) {
            mNightSleepDialog.dismiss();
        }

        super.onDestroy();

    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private void initChart(BarChart mChart) {
        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawBarShadow(false);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setAxisMinimum(0f);
        mChart.getXAxis().setTypeface(mTfLight);
        mChart.getXAxis().setTextColor(Compatible.getColor(mContext, R.color.dolphin));
        mChart.getXAxis().setTextSize(11.5f);

        mChart.getXAxis().setAxisMaximum(12 * 60f);
        mChart.getXAxis().setGranularity(1f);
        mChart.getXAxis().setLabelCount(720);
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                if (value == 0f) {
                    return "7pm";
                } else if (value == 120f) {
                    return "9pm";
                } else if (value == 240f) {
                    return "11pm";
                } else if (value == 360f) {
                    return "1am";
                } else if (value == 480f) {
                    return "3am";
                } else if (value == 600f) {
                    return "5am";
                } else if (value == 720f) {
                    return "7am";
                }

                return "";
            }
        });


        mChart.getAxisLeft().setDrawAxisLine(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setAxisMinimum(0f);
        mChart.getAxisLeft().setGranularity(0.25f);
        mChart.getAxisLeft().setLabelCount(24, false);
        mChart.getAxisLeft().setTypeface(mTfLight);
        mChart.getAxisLeft().setTextColor(Compatible.getColor(mContext, R.color.dolphin));
        mChart.getAxisLeft().setTextSize(11.5f);

        mChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                if (value == 0.3f) {
                    return getResources().getString(R.string.status_title_asleep);
                } else if (value == 3f) {
                    return getResources().getString(R.string.status_title_stirring);
                } else if (value == 6f) {
                    return getResources().getString(R.string.status_title_awake);
                }

                return "";
            }
        });


    }

}
