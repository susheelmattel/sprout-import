/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.fuhu.states.interfaces.IStatePayload;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.GrowthUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by loren.hung on 2016/12/29.
 */

public class GrowthChartActivity extends BaseMqttActivity {

    private Context mContext;

    private static boolean sIsPortrait;
    private FrameLayout mNightDlg;

    private LineChart mChart;
    private ArrayList<ILineDataSet> mDataSetsLength = new ArrayList<>();
    private Calendar mBirthday = Calendar.getInstance();
    private Calendar mToday = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline_growth_chart);
        mContext = this;
        findViews();

        mBirthday.add(Calendar.MONTH, -3);
        addLengthReferenceLine();
    }

    private void findViews() {
        findViewById(R.id.screen_change).setOnClickListener(mOnClickListener);
        findViewById(R.id.navigation_back).setOnClickListener(mOnClickListener);
        findViewById(R.id.navigation_action).setVisibility(View.GONE);
        ((ShTextView) findViewById(R.id.navigation_title)).setText("Growth Chart"); // TODO: move to string.xml
        mChart = (LineChart) findViewById(R.id.growth_lineChart);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.getLegend().setEnabled(false);
        mChart.getDescription().setEnabled(false);
//
//        mChart.setMarkerView(new LineChartMarkerView(this,R.layout.baby_tracking_marker_chart,LineChartMarkerView.MARKER_LENGTH));

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
//        mChart.getXAxis().setLabelsToSkip(0);

        mChart.getAxisRight().setEnabled(false);

//        mChart.getAxisLeft().setValueFormatter(new YAxisLengthFormat());
//        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMaximum(70f);
        mChart.getAxisLeft().setAxisMinimum(40f);
        mChart.getAxisLeft().setDrawAxisLine(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sIsPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
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

    private void setData(LineChart mChart) {

    }

    class NapAxisYValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public NapAxisYValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            switch ((int) value) {
                case 0:
                    return "Asleep";

                case 3:
                    return "Stirring";

                case 6:
                    return "Awake";

            }
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private void addLengthReferenceLine() {
        int duration = GrowthUtil.getDuration(mBirthday, mToday);
        if (duration == GrowthUtil.DURATION_INVALID) {
            //don't draw shadow area
            return;
        }

        int count = GrowthUtil.getCount(mBirthday, mToday);
        if (count <= 0) {
            //don't draw shadow area
            return;
        }

        float[] raw2 = GrowthUtil.getData(GrowthUtil.TYPE_LENGTH, GrowthUtil.GENDER_BOY, count, GrowthUtil.PERCENTILE_2ND);
        float[] raw25 = GrowthUtil.getData(GrowthUtil.TYPE_LENGTH, GrowthUtil.GENDER_BOY, count, GrowthUtil.PERCENTILE_25TH);
        float[] raw50 = GrowthUtil.getData(GrowthUtil.TYPE_LENGTH, GrowthUtil.GENDER_BOY, count, GrowthUtil.PERCENTILE_50TH);
        float[] raw75 = GrowthUtil.getData(GrowthUtil.TYPE_LENGTH, GrowthUtil.GENDER_BOY, count, GrowthUtil.PERCENTILE_75TH);
        float[] raw98 = GrowthUtil.getData(GrowthUtil.TYPE_LENGTH, GrowthUtil.GENDER_BOY, count, GrowthUtil.PERCENTILE_98TH);


        List<Entry> entry2 = new ArrayList<Entry>();
        List<Entry> entry25 = new ArrayList<Entry>();
        List<Entry> entry50 = new ArrayList<Entry>();
        List<Entry> entry75 = new ArrayList<Entry>();
        List<Entry> entry98 = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            entry2.add(new Entry(i, raw2[i]));
            entry25.add(new Entry(i, raw25[i]));
            entry50.add(new Entry(i, raw50[i]));
            entry75.add(new Entry(i, raw75[i]));
            entry98.add(new Entry(i, raw98[i]));
        }

//        mDataSetsLength.add(dataSetLength3rd); // add the datasets
//        mDataSetsLength.add(dataSetLength97th); // add the datasets

        mDataSetsLength.add(getLineDataSet(entry2, "Length2rd", reference2nd)); // add the datasets
        mDataSetsLength.add(getLineDataSet(entry25, "Length2rd", reference25th)); // add the datasets
        mDataSetsLength.add(getLineDataSet(entry50, "Length2rd", reference50th)); // add the datasets
        mDataSetsLength.add(getLineDataSet(entry75, "Length2rd", reference25th)); // add the datasets
        mDataSetsLength.add(getLineDataSet(entry98, "Length98th", kidData)); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(mDataSetsLength);
        mChart.setData(data);
    }

    private int reference2nd = 1;
    private int reference25th = 2;
    private int reference50th = 3;
    private int kidData = 4;

    private LineDataSet getLineDataSet(List<Entry> entry, String title, int type) {
        LineDataSet dataSetLength = new LineDataSet(entry, title);
        dataSetLength.enableDashedLine(10f, 5f, 0f);
        dataSetLength.enableDashedHighlightLine(10f, 5f, 0f);
        dataSetLength.setDrawValues(false);
        dataSetLength.setDrawHorizontalHighlightIndicator(false);
        dataSetLength.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if (type == reference2nd) {
            dataSetLength.setColor(getResources().getColor(R.color.color_growth_2nd));
            dataSetLength.setDrawCircles(false);
            dataSetLength.setDrawCircleHole(false);
        } else if (type == reference25th) {
            dataSetLength.setColor(getResources().getColor(R.color.color_growth_25th));
            dataSetLength.setDrawCircles(false);
            dataSetLength.setDrawCircleHole(false);
        } else if (type == reference50th) {
            dataSetLength.setColor(getResources().getColor(R.color.color_growth_50th));
            dataSetLength.setDrawCircles(false);
            dataSetLength.setDrawCircleHole(false);
            dataSetLength.disableDashedLine();
            dataSetLength.setLineWidth(3);
        } else if (type == kidData) {
            dataSetLength.setColor(getResources().getColor(R.color.tealish2));
            dataSetLength.disableDashedLine();
//            dataSetLength.setCircleColor(Color.WHITE);
            dataSetLength.setLineWidth(1f);
            dataSetLength.setCircleRadius(3f);

        }
        return dataSetLength;
    }
}
