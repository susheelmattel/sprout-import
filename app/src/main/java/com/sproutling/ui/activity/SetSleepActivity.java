/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.text.format.DateFormat.is24HourFormat;


/**
 * Created by Xylon on 2017/2/6.
 */

public class SetSleepActivity extends BaseMqttActivity {

    private LinearLayout mTotalTimeContent;
    private RelativeLayout mBeginTimeLayout, mEndTimeLayout;
    private ShTextView mBeginTimeText, mEndTimeText,
            mSleepTimerText, mTotalTimeTitle, mTotalTimeText,
            mBtnResetTimer, mBtnStartStop, mBtnSave, mBtnCancel,
            mBtnEnterManually, mTxtBeginMessage, mTxtEndMessage;
    private ImageView mImgTimer, mCheckGreenStart, mCheckGreenEnd;
    private Context mContext;
    private Calendar mStartCalender, mEndCalender, mCurrentCalender;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mStartSecond,
            mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute, mEndSecond;

    private boolean isStartTimeSet, isEndTimeSet;
    private boolean isStartTimerRecording = false;

    private String mChildId;
    private static String sNowStatus;

    public static class Status {
        public static final String START = "0";
        public static final String RECORDING = "1";
        public static final String STOP = "2";
        public static final String MANUALLY = "3";

    }

    public static final int UPDATE_UI = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sleep);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContext = this;

        if (sNowStatus == null) {
            sNowStatus = Status.START;
        }

        mChildId = getIntent().getStringExtra("ChildId");
        initViews();
        initCalenders();
        setTotalTimeText();

        Timer timerRecording = new Timer();
        timerRecording.schedule(recordingTask, 0, 1000);

        //For test init
//        SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false);
//        SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (long)(-1));
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private void initViews() {
        mTotalTimeContent = (LinearLayout) findViewById(R.id.totalTimeContent);
        mBeginTimeLayout = (RelativeLayout) findViewById(R.id.beginTimeLayout);
        mEndTimeLayout = (RelativeLayout) findViewById(R.id.endTimeLayout);
        mBeginTimeText = (ShTextView) findViewById(R.id.beginTimeText);
        mEndTimeText = (ShTextView) findViewById(R.id.endTimeText);
        mTxtBeginMessage = (ShTextView) findViewById(R.id.txtBeginMessage);
        mTxtEndMessage = (ShTextView) findViewById(R.id.txtEndMessage);

        mSleepTimerText = (ShTextView) findViewById(R.id.sleepTimerText);
        mTotalTimeTitle = (ShTextView) findViewById(R.id.totalTimeTitle);
        mTotalTimeText = (ShTextView) findViewById(R.id.totalTimeText);

        mImgTimer = (ImageView) findViewById(R.id.imgTimer);
        mCheckGreenStart = (ImageView) findViewById(R.id.checkGreenStart);
        mCheckGreenEnd = (ImageView) findViewById(R.id.checkGreenEnd);

        mBtnResetTimer = (ShTextView) findViewById(R.id.btnResetTimer);
        mBtnStartStop = (ShTextView) findViewById(R.id.btnStartStop);
        mBtnEnterManually = (ShTextView) findViewById(R.id.btnEnterManually);
        mBtnSave = (ShTextView) findViewById(R.id.btnSave);
        mBtnCancel = (ShTextView) findViewById(R.id.btnCancel);

        mBeginTimeLayout.setOnClickListener(clickListener);
        mEndTimeLayout.setOnClickListener(clickListener);
        mBtnResetTimer.setOnClickListener(clickListener);
        mBtnStartStop.setOnClickListener(clickListener);
        mBtnEnterManually.setOnClickListener(clickListener);
        mBtnSave.setOnClickListener(clickListener);
        mBtnSave.setEnabled(false);
        mBtnCancel.setOnClickListener(clickListener);

        setViewByStatus(sNowStatus);
    }

    private void initCalenders() {
        if (mStartCalender == null) {
            mStartCalender = Calendar.getInstance();

            Long startTimeRecord = SharedPrefManager.getLong(this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (long) (-1));
            if (startTimeRecord != -1) {
                mStartCalender.setTimeInMillis(startTimeRecord);
                setBeginTimeText(false);
            }

        } else {
            isStartTimeSet = true;
            setBeginTimeText(false);
        }

        if (mEndCalender == null) {
            mEndCalender = Calendar.getInstance();
        } else {
            mCurrentCalender = Calendar.getInstance();
            mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
            isEndTimeSet = true;
            setEndTimeText();
        }
        updateFields();
    }

    private void updateFields() {
        mStartYear = mStartCalender.get(Calendar.YEAR);
        mStartMonth = mStartCalender.get(Calendar.MONTH);
        mStartDay = mStartCalender.get(Calendar.DAY_OF_MONTH);
        mStartHour = mStartCalender.get(Calendar.HOUR_OF_DAY);
        mStartMinute = mStartCalender.get(Calendar.MINUTE);
        mStartSecond = mStartCalender.get(Calendar.SECOND);

        mEndYear = mEndCalender.get(Calendar.YEAR);
        mEndMonth = mEndCalender.get(Calendar.MONTH);
        mEndDay = mEndCalender.get(Calendar.DAY_OF_MONTH);
        mEndHour = mEndCalender.get(Calendar.HOUR_OF_DAY);
        mEndMinute = mEndCalender.get(Calendar.MINUTE);
        mEndSecond = mEndCalender.get(Calendar.SECOND);
    }

    private void setTotalTimeText() {
        if (Status.START.equals(sNowStatus)) {
            mTotalTimeText.setText(
                    String.format(getString(R.string.sleep_dlg_edit_time_format), 0, 0, 0));
        } else {
            long diffTime = mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis();
            int hours = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_HOUR);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_HOUR;
            int mins = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_MIN);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_MIN;
            int secs = (int) (diffTime / DateTimeUtils.MILLI_SEC);

            if (Status.MANUALLY.equals(sNowStatus)) {
                secs = 0;

                if (isStartTimeSet && isEndTimeSet) {
                    mTotalTimeContent.setVisibility(View.VISIBLE);
                    mBtnSave.setEnabled(true);
                }
            }

            mTotalTimeText.setText(
                    String.format(getString(R.string.sleep_dlg_edit_time_format), hours, mins, secs));
        }
    }

    private void setCurrentTimeSpacing() {
        mCurrentCalender = Calendar.getInstance();
        if (mCurrentCalender != null && mStartCalender != null) {
            long diffTime = mCurrentCalender.getTimeInMillis() - mStartCalender.getTimeInMillis();
            int hours = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_HOUR);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_HOUR;
            int mins = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_MIN);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_MIN;
            int secs = (int) (diffTime / DateTimeUtils.MILLI_SEC);

            mTotalTimeText.setText(
                    String.format(getString(R.string.sleep_dlg_edit_time_format), hours, mins, secs));
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.beginTimeLayout:
                    showStartDatePickerDlg();
                    break;
                case R.id.endTimeLayout:
                    showEndDatePickerDlg();
                    break;

                case R.id.btnStartStop:

                    if (Status.START.equals(sNowStatus)) {
                        Utils.logEvents(LogEvents.NAP_STARTED);
                        SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, true);
                        sNowStatus = Status.RECORDING;
                        mCurrentCalender = Calendar.getInstance();
                        mStartCalender = Calendar.getInstance();
                        mStartCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
                        setBeginTimeText(true);
                        isStartTimeSet = true;
                    } else if (Status.RECORDING.equals(sNowStatus)) {
                        Utils.logEvents(LogEvents.NAP_ENDED);
                        SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false);
                        sNowStatus = Status.STOP;
                        mCurrentCalender = Calendar.getInstance();
                        mEndCalender = Calendar.getInstance();
                        mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
                        setEndTimeText();
                        isEndTimeSet = true;

                        mTxtBeginMessage.setVisibility(View.GONE);
                        mTxtEndMessage.setVisibility(View.GONE);
                        mCheckGreenStart.setVisibility(View.VISIBLE);
                        mCheckGreenEnd.setVisibility(View.VISIBLE);

                        if (mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis() < 60000) {
                            if (Status.MANUALLY.equals(sNowStatus)) {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message4);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            } else {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message3);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            }
                            mTxtEndMessage.setVisibility(View.VISIBLE);
                            mBtnSave.setEnabled(false);
                        } else {
                            mBtnSave.setEnabled(true);
                        }
                    }
                    setViewByStatus(sNowStatus);
                    break;

                case R.id.btnEnterManually:
                    if (Status.MANUALLY.equals(sNowStatus)) {
                        sNowStatus = Status.START;
                        mBtnSave.setEnabled(false);
                        setViewByStatus(sNowStatus);

                        mStartCalender = mEndCalender = null;
                        initCalenders();
                        mTxtBeginMessage.setVisibility(View.GONE);
                        mTxtEndMessage.setVisibility(View.GONE);
                        mCheckGreenStart.setVisibility(View.VISIBLE);
                        mCheckGreenEnd.setVisibility(View.VISIBLE);
                        mTotalTimeText.setText(String.format(getString(R.string.sleep_dlg_edit_time_format), 0, 0, 0));
                    } else {
                        sNowStatus = Status.MANUALLY;
//                        mBtnSave.setEnabled(true);
                        setViewByStatus(sNowStatus);

                        mStartCalender = Calendar.getInstance();
                        mEndCalender = Calendar.getInstance();
//                        isStartTimeSet = false;
//                        isEndTimeSet = false;
                        mBeginTimeText.setText(R.string.set_sleep_dlg_enter_time);
                        mEndTimeText.setText(R.string.set_sleep_dlg_enter_time);
                        mCheckGreenStart.setVisibility(View.GONE);
                        mCheckGreenEnd.setVisibility(View.GONE);
                    }

                    break;

                case R.id.btnSave:
                    if (Status.STOP.equals(sNowStatus) || Status.MANUALLY.equals(sNowStatus)) {
                        mTxtBeginMessage.setVisibility(View.GONE);
                        mTxtEndMessage.setVisibility(View.GONE);
                        mCheckGreenStart.setVisibility(View.VISIBLE);
                        mCheckGreenEnd.setVisibility(View.VISIBLE);

                        if (mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis() > 60000) {
                            sNowStatus = Status.START;
                            runSaveTask();
                        } else {
                            if (Status.MANUALLY.equals(sNowStatus)) {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message4);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            } else {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message3);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            }
                            mTxtEndMessage.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case R.id.btnCancel:

                    if (Status.START.equals(sNowStatus)) {
                        setResult(Integer.valueOf(sNowStatus));
                        mStartCalender = mEndCalender = null;
                        finish();
                    } else if (Status.RECORDING.equals(sNowStatus)) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putLong("StartCalenderMillis", mStartCalender.getTimeInMillis());
                        intent.putExtras(bundle);
                        setResult(Integer.valueOf(sNowStatus), intent);
                        finish();
                    } else if (Status.STOP.equals(sNowStatus) || Status.MANUALLY.equals(sNowStatus)) {
                        showOptions();
                    }
                    break;

                case R.id.btnResetTimer:
                    Utils.logEvents(LogEvents.NAP_RESET);

                    SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false);
                    sNowStatus = Status.START;
                    setViewByStatus(sNowStatus);
//                    mCurrentCalender = Calendar.getInstance();
//                    mStartCalender = Calendar.getInstance();
//                    mEndCalender = Calendar.getInstance();
//                    mStartCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
//                    mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
//                    setBeginTimeText(true);
//                    setEndTimeText();
                    setTotalTimeText();

                    break;
            }
        }
    };

    private void showStartDatePickerDlg() {
        new DatePickerDialog(mContext, mStartDateSetListener, mStartYear, mStartMonth, mStartDay).show();
    }

    private void showEndDatePickerDlg() {
        new DatePickerDialog(mContext, mEndDateSetListener, mEndYear, mEndMonth, mEndDay).show();
    }

    private void showStartTimePickerDlg() {
        new TimePickerDialog(mContext, mStartTimeSetListener, mStartHour, mStartMinute, is24HourFormat(mContext)).show();
    }

    private void showEndTimePickerDlg() {
        new TimePickerDialog(mContext, mEndTimeSetListener, mEndHour, mEndMinute, is24HourFormat(mContext)).show();
    }

    private DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            mStartYear = year;
            mStartMonth = monthOfYear;
            mStartDay = dayOfMonth;
            showStartTimePickerDlg();
        }
    };

    private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            mEndYear = year;
            mEndMonth = monthOfYear;
            mEndDay = dayOfMonth;
            showEndTimePickerDlg();
        }
    };

    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStartHour = hourOfDay;
            mStartMinute = minute;
            Calendar calenderSet = Calendar.getInstance();
            calenderSet.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mStartSecond - 1); // It contents 1 second risk. so sub 1 second to avoid it.

            mTxtBeginMessage.setVisibility(View.GONE);
            mTxtEndMessage.setVisibility(View.GONE);
            mCheckGreenStart.setVisibility(View.VISIBLE);
            mCheckGreenEnd.setVisibility(View.VISIBLE);

            //If start time is later then current
            if (!isBeforeCurrentTime(calenderSet)) {
                mTxtBeginMessage.setText(R.string.set_sleep_dlg_start_message);
                mTxtBeginMessage.setVisibility(View.VISIBLE);
//                mStartCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis()); //if need to set in current time.
                mCheckGreenStart.setVisibility(View.GONE);
                mCheckGreenEnd.setVisibility(View.GONE);

            } else {
                if (isEndTimeSet) { //若有設定 EndTime

                    if (!mEndCalender.after(calenderSet)) { //若 EndTime 不在 StartTime 之後
                        mTxtBeginMessage.setText(R.string.set_sleep_dlg_end_message2);
                        mTxtBeginMessage.setVisibility(View.VISIBLE);
                        mCheckGreenStart.setVisibility(View.GONE);
                        mCheckGreenEnd.setVisibility(View.GONE);

                    } else {
                        if (mEndCalender.getTimeInMillis() - calenderSet.getTimeInMillis() < 60000) {
                            if (Status.MANUALLY.equals(sNowStatus)) {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message4);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            } else {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message3);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            }
                            mTxtEndMessage.setVisibility(View.VISIBLE);
                        }
                        mStartCalender.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mStartSecond);
                        isStartTimeSet = true;
                    }
                } else {
                    mStartCalender.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mStartSecond);
                    isStartTimeSet = true;

                    if (Status.MANUALLY.equals(sNowStatus)) {
                        // set end time to be 1 hour after start time. only if start time is manually set
                        mEndCalender.setTimeInMillis(mStartCalender.getTimeInMillis());
                        mEndCalender.add(Calendar.HOUR, 1);
                        updateFields();
                        isEndTimeSet = true;
                        setEndTimeText();
                    }
                }
            }

            if (Status.MANUALLY.equals(sNowStatus)) {
                setBeginTimeText(false);
            } else {
                setBeginTimeText(true);
            }

            if (Status.RECORDING.equals(sNowStatus)) {
                setCurrentTimeSpacing();
            } else {
                setTotalTimeText();
            }
//            mBtnSave.setEnabled(isStartTimeSet && isEndTimeSet);
        }
    };

    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEndHour = hourOfDay;
            mEndMinute = minute;

            Calendar calenderSet = Calendar.getInstance();
            calenderSet.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute, mEndSecond + 1); // It contents 1 second risk. so add 1 second to avoid it.

            mTxtBeginMessage.setVisibility(View.GONE);
            mTxtEndMessage.setVisibility(View.GONE);

            mCheckGreenStart.setVisibility(View.VISIBLE);
            mCheckGreenEnd.setVisibility(View.VISIBLE);

            //If end time is later then current
            if (!isBeforeCurrentTime(calenderSet)) {
                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message1);
                mTxtEndMessage.setVisibility(View.VISIBLE);
//                mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis()); //if need to set in current time.
                mCheckGreenStart.setVisibility(View.GONE);
                mCheckGreenEnd.setVisibility(View.GONE);

            } else {
                if (isStartTimeSet) { //若有設定 StartTime

                    if (!mStartCalender.before(calenderSet)) { //若 StartTime 不在 EndTime 之前
                        mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message2);
                        mTxtEndMessage.setVisibility(View.VISIBLE);
                        mCheckGreenStart.setVisibility(View.GONE);
                        mCheckGreenEnd.setVisibility(View.GONE);
                    } else {
                        if (calenderSet.getTimeInMillis() - mStartCalender.getTimeInMillis() < 60000) {
                            if (Status.MANUALLY.equals(sNowStatus)) {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message4);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            } else {
                                mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message3);
                                mCheckGreenStart.setVisibility(View.GONE);
                                mCheckGreenEnd.setVisibility(View.GONE);
                            }
                            mTxtEndMessage.setVisibility(View.VISIBLE);
                        }
                        mEndCalender.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute, mEndSecond);
                        isEndTimeSet = true;
                    }
                } else {
                    mEndCalender.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute, mEndSecond);
                    isEndTimeSet = true;
                }
            }


            setEndTimeText();
            setTotalTimeText();
//            mBtnSave.setEnabled(isStartTimeSet && isEndTimeSet);
        }
    };

    private void setBeginTimeText(Boolean isRecordInSharePre) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mBeginTimeText.setText(simpleDateFormat.format(mStartCalender.getTime()));
        mCheckGreenStart.setVisibility(View.VISIBLE);

        if (isRecordInSharePre)
            SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, mStartCalender.getTimeInMillis());
    }

    private TimerTask recordingTask = new TimerTask() {
        @Override
        public void run() {
            if (isStartTimerRecording) {
                Message message = new Message();
                message.what = UPDATE_UI;
                handler.sendMessage(message);
            }
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_UI:
                    setCurrentTimeSpacing();
                    break;
            }
        }
    };

    private void setEndTimeText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mEndTimeText.setText(simpleDateFormat.format(mEndCalender.getTime()));
        mCheckGreenEnd.setVisibility(View.VISIBLE);
    }

    private boolean isCompareStartEndModified(boolean isAlignStart) {
        //StartTime equals or after EndTime
        if (!mStartCalender.before(mEndCalender)) {
            if (isAlignStart) {
                //Set EndTime to align StartTime
                mEndCalender.setTimeInMillis(mStartCalender.getTimeInMillis());
                return true;
            } else {
                //Set StartTime to align EndTime
                mStartCalender.setTimeInMillis(mEndCalender.getTimeInMillis());
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isBeforeCurrentTime(Calendar c) {
        mCurrentCalender = Calendar.getInstance();
        return c.before(mCurrentCalender);
    }

    private void runSaveTask() {
        new AsyncTask<Void, Void, Void>() {
            SSError mError;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(mContext).getUser();
                    SKManagement.createEvent(account.accessToken, getAddSleepEventJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mError != null) {
                    handleError(mError);
                } else {
                    Utils.logEvents(LogEvents.NAP_SAVED);
                    SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (long) (-1));
                    mStartCalender = mEndCalender = null;
                    setResult(Integer.valueOf(SetSleepActivity.Status.STOP)); //This result tell StatusActivity to update 12 hour alarm.
                    finish();
                }
            }
        }.execute();
    }

    private void handleError(SSError error) {
//        new AlertDialog.Builder(mContext)
//                .setTitle(R.string.settings_baby_error_message_title)
//                .setMessage(error.toString())
//                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }

    private JSONObject getAddSleepEventJSON() throws JSONException {
//        {
//            "event_type": "AWAKE",
//                "child_id": "bfc214e9-4245-414a-acb5-6139d95b689f",
//                "start_date": "2017-02-20T19:19:05.234410974Z",
//                "end_date": "2017-02-20T19:19:05.234410974Z",
//                "data": ""
//        }
        JSONObject body = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (mChildId != null && !mChildId.equals("")) {
            body.put("child_id", mChildId);
//            body.put("end_date", mEndCalender.getTimeInMillis()/1000);
//            body.put("start_date", mStartCalender.getTimeInMillis()/1000);
            body.put("end_date", sdf.format(mEndCalender.getTime()));
            body.put("start_date", sdf.format(mStartCalender.getTime()));
            body.put("event_type", "nap");
        }
        return body;
    }

    public void setViewByStatus(String status) {
        boolean isOnRecording = SharedPrefManager.getBoolean(this, SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false);

        if (Status.START.equals(status) && isOnRecording) {
            mImgTimer.setVisibility(View.GONE);
            mSleepTimerText.setVisibility(View.GONE);
            mBeginTimeLayout.setVisibility(View.VISIBLE);
            mEndTimeLayout.setVisibility(View.GONE);
            mTotalTimeTitle.setVisibility(View.VISIBLE);
            mBtnResetTimer.setVisibility(View.GONE);
            mBtnStartStop.setBackground(getDrawable(R.drawable.shape_oval_red));
            mBtnStartStop.setText(getString(R.string.set_sleep_dlg_stop));
            mBtnStartStop.setVisibility(View.VISIBLE);
            mBtnEnterManually.setVisibility(View.GONE);

            setCurrentTimeSpacing();
            isStartTimerRecording = true;

            sNowStatus = Status.RECORDING;
        } else {
            switch (status) {
                case Status.START:
                    mImgTimer.setVisibility(View.VISIBLE);
                    mSleepTimerText.setVisibility(View.VISIBLE);
                    mBeginTimeLayout.setVisibility(View.GONE);
                    mEndTimeLayout.setVisibility(View.GONE);
                    mTotalTimeTitle.setVisibility(View.INVISIBLE);
                    mBtnResetTimer.setVisibility(View.GONE);
                    mBtnStartStop.setVisibility(View.VISIBLE);
                    mBtnStartStop.setBackground(getDrawable(R.drawable.shape_oval_green));
                    mBtnStartStop.setText(getString(R.string.set_sleep_dlg_start));
                    mBtnEnterManually.setText(R.string.set_sleep_dlg_manually);
                    mBtnEnterManually.setVisibility(View.VISIBLE);
                    mTxtEndMessage.setVisibility(View.GONE);
                    mTotalTimeContent.setVisibility(View.VISIBLE);

                    isStartTimeSet = false;
                    isEndTimeSet = false;
                    isStartTimerRecording = false;
                    break;
                case Status.RECORDING:
                    mImgTimer.setVisibility(View.GONE);
                    mSleepTimerText.setVisibility(View.GONE);
                    mBeginTimeLayout.setVisibility(View.VISIBLE);
                    mEndTimeLayout.setVisibility(View.GONE);
                    mTotalTimeTitle.setVisibility(View.VISIBLE);
                    mBtnResetTimer.setVisibility(View.GONE);
                    mBtnStartStop.setBackground(getDrawable(R.drawable.shape_oval_red));
                    mBtnStartStop.setText(getString(R.string.set_sleep_dlg_stop));
                    mBtnStartStop.setVisibility(View.VISIBLE);
                    mBtnEnterManually.setVisibility(View.GONE);

                    setCurrentTimeSpacing();
                    isStartTimerRecording = true;
                    break;
                case Status.STOP:
                    mImgTimer.setVisibility(View.GONE);
                    mSleepTimerText.setVisibility(View.GONE);
                    mBeginTimeLayout.setVisibility(View.VISIBLE);
                    mEndTimeLayout.setVisibility(View.VISIBLE);
                    mTotalTimeTitle.setVisibility(View.VISIBLE);
                    mBtnResetTimer.setVisibility(View.VISIBLE);
                    mBtnStartStop.setVisibility(View.GONE);
                    mBtnStartStop.setBackground(getDrawable(R.drawable.shape_oval_green));
                    mBtnStartStop.setText(getString(R.string.set_sleep_dlg_start));
                    mBtnEnterManually.setVisibility(View.GONE);
                    isStartTimerRecording = false;
                    break;
                case Status.MANUALLY:
                    mImgTimer.setVisibility(View.GONE);
                    mSleepTimerText.setVisibility(View.GONE);
                    mBeginTimeLayout.setVisibility(View.VISIBLE);
                    mEndTimeLayout.setVisibility(View.VISIBLE);
                    mTotalTimeTitle.setVisibility(View.VISIBLE);
                    mBtnResetTimer.setVisibility(View.VISIBLE);
                    mBtnStartStop.setVisibility(View.GONE);
                    mBtnStartStop.setBackground(getDrawable(R.drawable.shape_oval_green));
                    mBtnStartStop.setText(getString(R.string.set_sleep_dlg_start));
                    mBtnEnterManually.setText(R.string.set_sleep_dlg_live);
                    mBtnEnterManually.setVisibility(View.VISIBLE);

                    mTotalTimeContent.setVisibility(View.INVISIBLE);

                    isStartTimerRecording = false;
                    break;
            }
        }
    }

    private void showOptions() {
        View view = getLayoutInflater().inflate(R.layout.dialog_sleeptimer, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        ShTextView tvSave = (ShTextView) view.findViewById(R.id.btnSave);
        ShTextView tvDiscard = (ShTextView) view.findViewById(R.id.btnDiscard);
        ShTextView tvCancel = (ShTextView) view.findViewById(R.id.btnCancel);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtBeginMessage.setVisibility(View.GONE);
                mTxtEndMessage.setVisibility(View.GONE);
                mCheckGreenStart.setVisibility(View.VISIBLE);
                mCheckGreenEnd.setVisibility(View.VISIBLE);

                if (mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis() > 60000) {
                    sNowStatus = Status.START;
                    runSaveTask();
                } else {
                    if (Status.MANUALLY.equals(sNowStatus)) {
                        mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message4);
                        mCheckGreenStart.setVisibility(View.GONE);
                        mCheckGreenEnd.setVisibility(View.GONE);
                    } else {
                        mTxtEndMessage.setText(R.string.set_sleep_dlg_end_message3);
                        mCheckGreenStart.setVisibility(View.GONE);
                        mCheckGreenEnd.setVisibility(View.GONE);
                    }
                    mTxtEndMessage.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });
        tvDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.logEvents(LogEvents.NAP_USER_TAPPED_DISCARD);
                sNowStatus = Status.START;

                SharedPrefManager.put(SetSleepActivity.this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (long) (-1));

                dialog.dismiss();
                setResult(Integer.valueOf(SetSleepActivity.Status.STOP));
                mStartCalender = mEndCalender = null;
                finish();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.logEvents(LogEvents.NAP_USER_TAPPED_CANCEL);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}
