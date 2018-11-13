/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSException;
import com.sproutling.ui.dialog.NightSleepDialog;
import com.sproutling.ui.dialogfragment.DeleteSummaryDialogFragment;
import com.sproutling.ui.widget.ChartView;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Const;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.TimelineUtils;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by loren.hung on 2016/12/29.
 */

public class NapSummaryActivity extends BaseMqttActivity implements DeleteSummaryDialogFragment.OnDeleteSummaryListener {

    public static final int NapTimeEditResultCode = 1000;
    public static final int REQUEST_CODE_DELETE_SUMMARY = 1;

    private static boolean sIsPortrait;
    private static boolean sIsEdit = false;

    private EventBean mEventBean;
    private ShTextView mTitle, mTimeAsleep, mTimeAwake, mTimeStirring, mWakings, mNightDlg;
    private ShTextView mEdit, mDelete;

    private NightSleepDialog mNightSleepDialog;
    private boolean isNight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_nap_summary);
        mNightSleepDialog = new NightSleepDialog(this);
        mEventBean = (EventBean) getIntent().getSerializableExtra("Event");
        isNight = TimelineUtils.isNightSleep(this, mEventBean);
        findViews();
    }

    private void findViews() {
        mTitle = (ShTextView) findViewById(R.id.navigation_title);
        mTitle.setText(TimelineUtils.getSummaryTitle(this, mEventBean));

        mTimeAsleep = (ShTextView) findViewById(R.id.time_asleep);
        mTimeAwake = (ShTextView) findViewById(R.id.time_awake);
        mTimeStirring = (ShTextView) findViewById(R.id.time_stirring);
        mWakings = (ShTextView) findViewById(R.id.wakings);
        mDelete = (ShTextView) findViewById(R.id.delete_summary);
        mEdit = (ShTextView) findViewById(R.id.edit_time);
        mNightDlg = (ShTextView) findViewById(R.id.night_dlg);
        mNightDlg.setVisibility(isNight && !sIsPortrait ? View.VISIBLE : View.GONE);
        mNightDlg.setOnClickListener(mOnClickListener);

        findViewById(R.id.navigation_action).setVisibility(View.GONE);
        findViewById(R.id.screen_change).setOnClickListener(mOnClickListener);
//        screenChange.setRotation(!sIsPortrait ? 90 : 0);
        findViewById(R.id.navigation_back).setOnClickListener(mOnClickListener);
        mEdit.setOnClickListener(mOnClickListener);
        mDelete.setOnClickListener(mOnClickListener);

        updateUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mNightDlg.setVisibility(isNight && Configuration.ORIENTATION_PORTRAIT == getScreenOrientation() ? View.VISIBLE : View.GONE);
    }

    public int getScreenOrientation() {
        Display display = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (display.getWidth() < display.getHeight()) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    private void updateUI() {
        if (mEventBean.hasSpells()) {
            mTimeAsleep.setText(TimelineUtils.formatHoursAndMinutesShort(this, (int) (mEventBean.getTimeAsleep() / Const.TIME_MS_MIN)));
            mTimeAwake.setText(TimelineUtils.formatHoursAndMinutesShort(this, (int) (mEventBean.getTimeAwake() / Const.TIME_MS_MIN)));
            mTimeStirring.setText(TimelineUtils.formatHoursAndMinutesShort(this, (int) (mEventBean.getTimeStirring() / Const.TIME_MS_MIN)));
            mWakings.setText(String.valueOf(mEventBean.getNumOfWakings()));
        } else {
            mTimeAsleep.setText(TimelineUtils.formatHoursAndMinutesShort(this, TimelineUtils.getTotalMin(mEventBean)));
            mTimeAwake.setText(TimelineUtils.formatHoursAndMinutesShort(this, 0));
            mTimeStirring.setText(TimelineUtils.formatHoursAndMinutesShort(this, 0));
            mWakings.setText("0");
        }
        mDelete.setText(String.format(getString(R.string.nap_summary_delete_event), isNight ? getString(R.string.nap_summary_delete_type_sleep) : getString(R.string.nap_summary_delete_type_nap)));
        ChartView chart = new ChartView(this, findViewById(R.id.last_night_barChart));
        chart.setEvent(mEventBean).setMode(ChartView.MODE_FULL_SCREEN).init();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.screen_change:
                    if (mTitle.getText().toString().equalsIgnoreCase(getString(R.string.night_sleep)) ||
                            mTitle.getText().toString().equalsIgnoreCase(getString(R.string.last_night_summary))) {
                        Utils.logEvents(LogEvents.TIMELINE_NIGHT_SLEEP_SUMMARY_SCREEN_ROTATE);
                    } else {
                        Utils.logEvents(LogEvents.TIMELINE_NAP_SUMMARY_SCREEN_ROTATE);
                    }
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
                    if (sIsEdit) {
                        setResult(NapSummaryActivity.NapTimeEditResultCode);
                    }
                    sIsEdit = false;
                    finish();
                    break;

                case R.id.night_dlg:
                    mNightSleepDialog.show();
                    break;
                case R.id.edit_time:
                    Intent intent = new Intent(NapSummaryActivity.this, EditSleepActivity.class);
                    intent.putExtra("Event", mEventBean);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.delete_summary:
                    String type = isNight ? getString(R.string.nap_summary_delete_type_sleep) : getString(R.string.nap_summary_delete_type_nap);
                    DeleteSummaryDialogFragment dialogFragment = DeleteSummaryDialogFragment.newInstance(type);
//                    dialogFragment.setTargetFragment(SettingsDeviceSettingsFragment.this, REQUEST_CODE_REMOVE_DEVICE);
                    dialogFragment.show(getFragmentManager(), null);
                    break;
                default:
            }
        }
    };

    @Override
    public void onBackPressed() {
        sIsPortrait = false;
        if (sIsEdit) {
            setResult(NapSummaryActivity.NapTimeEditResultCode);
        }
        sIsEdit = false;
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NapTimeEditResultCode && data != null) {
            mEventBean = (EventBean) data.getSerializableExtra("Event");
//            mEventBean.setStartDate(data.getStringExtra("StartDate"), false);
//            mEventBean.setEndDate(data.getStringExtra("EndDate"), false);
            sIsEdit = true;
//            if (!mEventBean.hasNightEvents() && !mEventBean.hasSpells()) {
//                finish();
//            }
            updateUI();
        }
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

    @Override
    public void onDeleteClicked() {
        deleteSummary();
    }

    public void deleteSummary() {
        new AsyncTask<Void, Void, Boolean>() {
            private SSException mException;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    AccountManagement accountManagement = AccountManagement.getInstance(NapSummaryActivity.this);
                    if (mEventBean.hasNightEvents()) {
                        for (EventBean event : mEventBean.getNightEvents()) {
                            SKManagement.deleteEventById(accountManagement.getAccessToken(), event.getId());
                        }
                        return true;
                    } else {
                        return SKManagement.deleteEventById(accountManagement.getAccessToken(), mEventBean.getId());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    setResult(REQUEST_CODE_DELETE_SUMMARY);
                    finish();
                } else {

                }
            }
        }.execute();
    }
}
