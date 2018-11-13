/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.widget.ShCustomProgressBar;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.text.format.DateFormat.is24HourFormat;

public class AddSleepActivity extends BaseMqttActivity {

    private ShTextView mBeginTimeText, mEndTimeText, mBtnSave;
    private ImageView mCheckGreenStart, mCheckGreenEnd;
    private Context mContext;
    private Calendar mStartCalender, mEndCalender, mCurrentCalender;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute,
            mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private boolean isStartTimeSet, isEndTimeSet;
    private String mChildId;
    private ShCustomProgressBar mProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sleep);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContext = this;
        mProgressDlg = new ShCustomProgressBar(mContext);
        initViews();
        mChildId = getIntent().getStringExtra("ChildId");
        initCalenders();
    }

    private void initCalenders() {
        mStartCalender = Calendar.getInstance();
        mEndCalender = Calendar.getInstance();
        updateFields();
    }

    private void updateFields() {
        mStartYear = mStartCalender.get(Calendar.YEAR);
        mStartMonth = mStartCalender.get(Calendar.MONTH);
        mStartDay = mStartCalender.get(Calendar.DAY_OF_MONTH);
        mStartHour = mStartCalender.get(Calendar.HOUR_OF_DAY);
        mStartMinute = mStartCalender.get(Calendar.MINUTE);

        mEndYear = mEndCalender.get(Calendar.YEAR);
        mEndMonth = mEndCalender.get(Calendar.MONTH);
        mEndDay = mEndCalender.get(Calendar.DAY_OF_MONTH);
        mEndHour = mEndCalender.get(Calendar.HOUR_OF_DAY);
        mEndMinute = mEndCalender.get(Calendar.MINUTE);
    }

    private void initViews() {
        RelativeLayout beginTimeLayout = (RelativeLayout) findViewById(R.id.beginTimeLayout);
        RelativeLayout endTimeLayout = (RelativeLayout) findViewById(R.id.endTimeLayout);
        mBeginTimeText = (ShTextView) findViewById(R.id.beginTimeText);
        mEndTimeText = (ShTextView) findViewById(R.id.endTimeText);
        mCheckGreenStart = (ImageView) findViewById(R.id.checkGreenStart);
        mCheckGreenEnd = (ImageView) findViewById(R.id.checkGreenEnd);

        mBtnSave = (ShTextView) findViewById(R.id.btnSave);
        ShTextView btnCancel = (ShTextView) findViewById(R.id.btnCancel);

        beginTimeLayout.setOnClickListener(clickListener);
        endTimeLayout.setOnClickListener(clickListener);
        mBtnSave.setOnClickListener(clickListener);
        mBtnSave.setEnabled(false);
        btnCancel.setOnClickListener(clickListener);
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
                case R.id.btnSave:
                    runSaveTask();
                    break;
                case R.id.btnCancel:
                    finish();
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

    private void showStartTimePickerDlg() {
        new TimePickerDialog(mContext, mStartTimeSetListener, mStartHour, mStartMinute, is24HourFormat(mContext)).show();
    }

    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener
            = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStartHour = hourOfDay;
            mStartMinute = minute;
            mStartCalender.set(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute);
            //If start time is later then current, set it to current
            if (!isBeforeCurrentTime(mStartCalender)) {
                mStartCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
            }
            isStartTimeSet = true;

            // set end time to be 1 hour after start time
            mEndCalender.setTimeInMillis(mStartCalender.getTimeInMillis());
            mEndCalender.add(Calendar.HOUR, 1);
            updateFields();
            isEndTimeSet = true;
            setEndTimeText();

            //If end time was set, compare it with start time
//            if (isEndTimeSet && isCompareStartEndModified(true)) {
//                //Change End Time
//                setEndTimeText();
//            }
            setBeginTimeText();
            mBtnSave.setEnabled(isStartTimeSet && isEndTimeSet);
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

    private void showEndTimePickerDlg() {
        new TimePickerDialog(mContext, mEndTimeSetListener, mEndHour, mEndMinute, is24HourFormat(mContext)).show();
    }

    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener
            = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEndHour = hourOfDay;
            mEndMinute = minute;
            mEndCalender.set(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute);
            //If end time is later then current, set it to current
            if (!isBeforeCurrentTime(mEndCalender)) {
                mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
            }
            isEndTimeSet = true;
            //If start time was set, compare it with end time
            if (isStartTimeSet && isCompareStartEndModified(false)) {
                setBeginTimeText();
            }
            setEndTimeText();
            mBtnSave.setEnabled(isStartTimeSet && isEndTimeSet);
        }
    };

    private void setBeginTimeText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mBeginTimeText.setText(simpleDateFormat.format(mStartCalender.getTime()));
        mCheckGreenStart.setVisibility(View.VISIBLE);
    }

    private void setEndTimeText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mEndTimeText.setText(simpleDateFormat.format(mEndCalender.getTime()));
        mCheckGreenEnd.setVisibility(View.VISIBLE);
    }

    private boolean isCompareStartEndModified(boolean isAlignStart) {
        //StartTime equals or after EndTime or duration less than a minute
        if (!mStartCalender.before(mEndCalender) ||
                (mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis()) <= DateTimeUtils.MILLI_SEC_PER_MIN) {
            if (isAlignStart) {
                //Set EndTime to align StartTime
                mEndCalender.setTimeInMillis(mStartCalender.getTimeInMillis());
                //Set StartTime to a minute before EndTime
                mStartCalender.setTimeInMillis(mEndCalender.getTimeInMillis() - DateTimeUtils.MILLI_SEC_PER_MIN);
                return true;
            } else {
                //Set StartTime to align a minute before EndTime
                mStartCalender.setTimeInMillis(mEndCalender.getTimeInMillis() - DateTimeUtils.MILLI_SEC_PER_MIN);
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isBeforeCurrentTime(Calendar calendar) {
        mCurrentCalender = Calendar.getInstance();
        return calendar.before(mCurrentCalender);
    }

    private void runSaveTask() {
        new AsyncTask<Void, Void, Void>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mProgressDlg == null) {
                    mProgressDlg = new ShCustomProgressBar(mContext);
                }
                if (!mProgressDlg.isShowing()) {
                    mProgressDlg.show();
                }
            }

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
                if (mProgressDlg != null && mProgressDlg.isShowing()) {
                    mProgressDlg.dismiss();
                }
                if (mError != null) {
                    handleError(mError);
                } else {
                    Utils.logEvents(LogEvents.TIMELINE_ADDED_NAP);
                    setResult(NapSummaryActivity.NapTimeEditResultCode);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
        }
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

    private JSONObject getAddSleepEventJSON() throws JSONException {
        //{"child_id":"737bdb45-a37a-43a7-81af-8c5411376cd4",
        // "end_date":1483862500,
        // "event_type":"heartRate",
        // "start_date":1483862400}
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
}
