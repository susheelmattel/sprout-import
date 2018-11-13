/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.ui.widget.ShTextView;

import java.util.ArrayList;

/**
 * Created by apple on 2017/1/9.
 */

public class AddMeasurementActivity extends BaseMqttActivity {
    private ImageView mBtnBack;
    private ShTextView mTitle, mBtnDone, mHeadSize, mWeightValue, mLengthSize;
    private LinearLayout mHeadLayout, mWeightLayout, mLengthLayout;
    private View mSpacerView1, mSpacerView2;
    private NumberPicker mHeadPicker, mWeightPicker, mLengthPicker;

    private static final int HEAD_MIN = 30;
    private static final int HEAD_MAX = 52;
    private static final int WEIGHT_MIN = 4;
    private static final int WEIGHT_MAX = 40;
    private static final int LENGTH_MIN = 40;
    private static final int LENGTH_MAX = 100;
    private int mHeadVal, mWeightVal, mLengthVal;
    private boolean isHeadSet, isWeightSet, isLengthSet;

    private static String[] sHeadRange, sWeightRange, sLengthRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_measurement);

        sHeadRange = new String[HEAD_MAX - HEAD_MIN + 1];
        for (int i = 0; i < (HEAD_MAX - HEAD_MIN + 1); i++) {
            sHeadRange[i] = (HEAD_MIN + i) + " " + getString(R.string.cm);
        }
        sWeightRange = new String[WEIGHT_MAX - WEIGHT_MIN + 1];
        for (int i = 0; i < (WEIGHT_MAX - WEIGHT_MIN + 1); i++) {
            sWeightRange[i] = (WEIGHT_MIN + i) + " " + getString(R.string.lbs);
        }
        sLengthRange = new String[LENGTH_MAX - LENGTH_MIN + 1];
        for (int i = 0; i < (LENGTH_MAX - LENGTH_MIN + 1); i++) {
            sLengthRange[i] = (LENGTH_MIN + i) + " " + getString(R.string.cm);
        }
        initViews();
        initStateAndListener();
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private void initViews() {
        mTitle = (ShTextView) findViewById(R.id.navigation_title);
        mBtnBack = (ImageView) findViewById(R.id.navigation_back);
        mBtnDone = (ShTextView) findViewById(R.id.navigation_action);
        mHeadSize = (ShTextView) findViewById(R.id.headSize);
        mWeightValue = (ShTextView) findViewById(R.id.weightValue);
        mLengthSize = (ShTextView) findViewById(R.id.lengthSize);
        mHeadLayout = (LinearLayout) findViewById(R.id.headLayout);
        mWeightLayout = (LinearLayout) findViewById(R.id.weightLayout);
        mLengthLayout = (LinearLayout) findViewById(R.id.lengthLayout);
        mSpacerView1 = findViewById(R.id.spacerView1);
        mSpacerView2 = findViewById(R.id.spacerView2);
        mHeadPicker = (NumberPicker) findViewById(R.id.headPicker);
        mWeightPicker = (NumberPicker) findViewById(R.id.weightPicker);
        mLengthPicker = (NumberPicker) findViewById(R.id.lengthPicker);
    }

    private void initStateAndListener() {
        mTitle.setText(R.string.input_measurement);
        mBtnBack.setOnClickListener(clickListener);
        mBtnDone.setOnClickListener(clickListener);
        mBtnDone.setText(R.string.done);
        mBtnDone.setEnabled(false);
        mHeadLayout.setOnClickListener(clickListener);
        mWeightLayout.setOnClickListener(clickListener);
        mLengthLayout.setOnClickListener(clickListener);
        mHeadPicker.setMinValue(HEAD_MIN);
        mHeadPicker.setMaxValue(HEAD_MAX);
        mHeadPicker.setDisplayedValues(sHeadRange);
        mHeadPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mHeadVal = picker.getValue();
                mHeadSize.setText(mHeadVal + " " + getString(R.string.cm));
                isHeadSet = true;
                mBtnDone.setEnabled(isHeadSet && isWeightSet && isLengthSet);
            }
        });
        mWeightPicker.setMinValue(WEIGHT_MIN);
        mWeightPicker.setMaxValue(WEIGHT_MAX);
        mWeightPicker.setDisplayedValues(sWeightRange);
        mWeightPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mWeightVal = picker.getValue();
                mWeightValue.setText(mWeightVal + " " + getString(R.string.lbs));
                isWeightSet = true;
                mBtnDone.setEnabled(isHeadSet && isWeightSet && isLengthSet);
            }
        });
        mLengthPicker.setMinValue(LENGTH_MIN);
        mLengthPicker.setMaxValue(LENGTH_MAX);
        mLengthPicker.setDisplayedValues(sLengthRange);
        mLengthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mLengthVal = picker.getValue();
                mLengthSize.setText(mLengthVal + " " + getString(R.string.cm));
                isLengthSet = true;
                mBtnDone.setEnabled(isHeadSet && isWeightSet && isLengthSet);
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_back:
                    finish();
                    break;
                case R.id.navigation_action:
                    //TODO Run Save Task
                    finish();
                    break;
                case R.id.headLayout:
                    if (mHeadPicker.getVisibility() == View.GONE) {
                        mHeadLayout.setBackgroundResource(R.drawable.measurement_head_bg_pop);
                        mWeightLayout.setBackgroundResource(R.drawable.measurement_weight_bg_normal);
                        mLengthLayout.setBackgroundResource(R.drawable.measurement_length_bg_normal);
                        mWeightPicker.setVisibility(View.GONE);
                        mLengthPicker.setVisibility(View.GONE);
                        mSpacerView1.setVisibility(View.GONE);
                        mSpacerView2.setVisibility(View.VISIBLE);
                        mHeadPicker.setVisibility(View.VISIBLE);
                    } else {
                        mHeadLayout.setBackgroundResource(R.drawable.measurement_head_bg_normal);
                        mHeadPicker.setVisibility(View.GONE);
                        mSpacerView1.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.weightLayout:
                    if (mWeightPicker.getVisibility() == View.GONE) {
                        mHeadLayout.setBackgroundResource(R.drawable.measurement_head_bg_normal);
                        mWeightLayout.setBackgroundResource(R.drawable.measurement_weight_bg_pop);
                        mLengthLayout.setBackgroundResource(R.drawable.measurement_length_bg_normal);
                        mHeadPicker.setVisibility(View.GONE);
                        mSpacerView2.setVisibility(View.GONE);
                        mLengthPicker.setVisibility(View.GONE);
                        mSpacerView1.setVisibility(View.VISIBLE);
                        mWeightPicker.setVisibility(View.VISIBLE);
                    } else {
                        mWeightLayout.setBackgroundResource(R.drawable.measurement_weight_bg_normal);
                        mWeightPicker.setVisibility(View.GONE);
                        mSpacerView2.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.lengthLayout:
                    if (mLengthPicker.getVisibility() == View.GONE) {
                        mHeadLayout.setBackgroundResource(R.drawable.measurement_head_bg_normal);
                        mWeightLayout.setBackgroundResource(R.drawable.measurement_weight_bg_normal);
                        mLengthLayout.setBackgroundResource(R.drawable.measurement_length_bg_pop);
                        mHeadPicker.setVisibility(View.GONE);
                        mWeightPicker.setVisibility(View.GONE);
                        mSpacerView1.setVisibility(View.VISIBLE);
                        mSpacerView2.setVisibility(View.VISIBLE);
                        mLengthPicker.setVisibility(View.VISIBLE);
                    } else {
                        mLengthLayout.setBackgroundResource(R.drawable.measurement_length_bg_normal);
                        mLengthPicker.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };

}
