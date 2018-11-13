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
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.fuhu.states.interfaces.IStatePayload;
import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.GenericErrorDialogFragment;
import com.sproutling.ui.widget.ShCustomProgressBar;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.EventBean;
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
import static com.sproutling.utils.DateTimeUtils.FIFTEEN_HOURS_IN_MS;
import static com.sproutling.utils.DateTimeUtils.FIVE_MINUTE_IN_MS;

public class EditSleepActivity extends BaseMqttActivity {

    private ShTextView mBeginTimeText, mEndTimeText, mTotalTimeText;
    private Context mContext;
    private Calendar mStartCalender, mEndCalender, mCurrentCalender;
    private int mStartYear, mStartMonth, mStartDay, mStartHour, mStartMinute, mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private EventBean mEventBean;
    private ShCustomProgressBar mProgressDlg;
    private int mTaskCount;
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
            //Compare end time with start time
            if (isCompareStartEndModified(true)) {
                //Change End Time
                setEndTimeText();
            }
            setBeginTimeText();
            setTotalTimeText();
        }
    };
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
            //Compare start time with end time
            if (isCompareStartEndModified(false)) {
                setBeginTimeText();
            }
            setEndTimeText();
            setTotalTimeText();
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
                    checkUpdate();
                    break;
                case R.id.btnCancel:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sleep);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContext = this;
        mProgressDlg = new ShCustomProgressBar(mContext);
        initViews();
        mEventBean = (EventBean) getIntent().getSerializableExtra("Event");
        initCalenders();
    }

    private void initCalenders() {
        //Get Start/End Time & eventId from Extra
        mStartCalender = Calendar.getInstance();
        mEndCalender = Calendar.getInstance();
        mStartCalender.setTimeInMillis(mEventBean.getStartTime().getTimeInMillis());
        mEndCalender.setTimeInMillis(mEventBean.getEndTime().getTimeInMillis());

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

        setBeginTimeText();
        setEndTimeText();
        setTotalTimeText();
    }

    private void initViews() {
        RelativeLayout beginTimeLayout = (RelativeLayout) findViewById(R.id.beginTimeLayout);
        RelativeLayout endTimeLayout = (RelativeLayout) findViewById(R.id.endTimeLayout);
        mBeginTimeText = (ShTextView) findViewById(R.id.beginTimeText);
        mEndTimeText = (ShTextView) findViewById(R.id.endTimeText);
        ShTextView btnSave = (ShTextView) findViewById(R.id.btnSave);
        ShTextView btnCancel = (ShTextView) findViewById(R.id.btnCancel);
        mTotalTimeText = (ShTextView) findViewById(R.id.totalTimeText);

        beginTimeLayout.setOnClickListener(clickListener);
        endTimeLayout.setOnClickListener(clickListener);
        btnSave.setOnClickListener(clickListener);
        btnCancel.setOnClickListener(clickListener);
    }

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

    private void setBeginTimeText() {
        //If start time is later then current, set it to current
        if (!isBeforeCurrentTime(mStartCalender)) {
            mStartCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mBeginTimeText.setText(simpleDateFormat.format(mStartCalender.getTime()));
    }

    private void setEndTimeText() {
        //If end time is later then current, set it to current
        if (!isBeforeCurrentTime(mEndCalender)) {
            mEndCalender.setTimeInMillis(mCurrentCalender.getTimeInMillis());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy  hh:mm aa", Locale.US);
        mEndTimeText.setText(simpleDateFormat.format(mEndCalender.getTime()));
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

    private void setTotalTimeText() {
        long diffTime = mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis();
        int hours = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_HOUR);
        diffTime %= DateTimeUtils.MILLI_SEC_PER_HOUR;
        int mins = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_MIN);
        diffTime %= DateTimeUtils.MILLI_SEC_PER_MIN;
        int secs = (int) (diffTime / DateTimeUtils.MILLI_SEC);

        mTotalTimeText.setText(
                String.format(getString(R.string.sleep_dlg_edit_time_format), hours, mins, secs));
    }

    private void setStartDate(EventBean event, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
        event.setStartDate(sdf.format(calendar.getTime()), false);
    }

    private void setEndDate(EventBean event, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
        event.setEndDate(sdf.format(calendar.getTime()), false);
    }

    private void checkUpdate() {
        try {
            if (mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis() > (FIVE_MINUTE_IN_MS) &&
                    mEndCalender.getTimeInMillis() - mStartCalender.getTimeInMillis() < FIFTEEN_HOURS_IN_MS) {
                setStartDate(mEventBean, mStartCalender);
                setEndDate(mEventBean, mEndCalender);
                if (mEventBean.hasNightEvents()) {
                    mTaskCount = 0;
                    int eventSize = mEventBean.getNightEvents().size();
                    for (int i = eventSize - 1; i >= 0; i--) {
                        EventBean event = mEventBean.getNightEvents().get(i);
                        Calendar start = event.getStartTime();
                        Calendar end = event.getEndTime();
                        boolean hasChanged = false;
                        if (mStartCalender.after(start)) {
                            mTaskCount++;
                            deleteEvent(event);
                            mEventBean.getNightEvents().remove(i);
                            continue;
                        } else if (mStartCalender.before(start)) {
                            if (i == 0 || mStartCalender.after(mEventBean.getNightEvents().get(i - 1).getStartTime())) {
                                start = mStartCalender;
                                setStartDate(event, start);
                                hasChanged = true;
                            }
                        }
                        if (mEndCalender.before(event.getStartTime())) {
                            mTaskCount++;
                            deleteEvent(event);
                            mEventBean.getNightEvents().remove(i);
                            continue;
                        } else if (mEndCalender.after(end)) {
                            if (i == eventSize - 1 || mEndCalender.before(mEventBean.getNightEvents().get(i + 1).getStartTime())) {
                                end = mEndCalender;
                                setEndDate(event, end);
                                hasChanged = true;
                            }
                        } else if (mEndCalender.before(end) && i == eventSize - 1) {
                            end = mEndCalender;
                            setEndDate(event, end);
                            hasChanged = true;
                        }
                        if (hasChanged) {
                            mTaskCount++;
                            JSONObject body = getJSON(start, end);
                            runSaveTask(event, body);
                        }
                    }
                    if (mTaskCount == 0) {
                        finish();
                    }
                } else {
                    mTaskCount++;
                    runSaveTask(mEventBean, getJSON(mStartCalender, mEndCalender));
                }
            } else {
                GenericErrorDialogFragment genericErrorDialogFragment = GenericErrorDialogFragment.newInstance();
                genericErrorDialogFragment.setTitle(R.string.error);
                genericErrorDialogFragment.setButtonText(R.string.okay);
                genericErrorDialogFragment.setMessage(R.string.edit_sleep_activity_error);
                genericErrorDialogFragment.show(getFragmentManager(), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void runSaveTask(final EventBean event, final JSONObject body) {
        new AsyncTask<Void, Void, SSManagement.SSEvent>() {
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
            protected SSManagement.SSEvent doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(mContext).getUser();
                    return SKManagement.updateEventById(account.accessToken, event.getId(), body);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.SSEvent event) {
                super.onPostExecute(event);
                mTaskCount--;
                if (mTaskCount == 0) {
                    if (mProgressDlg != null && mProgressDlg.isShowing()) {
                        mProgressDlg.dismiss();
                    }
                    finishDialog();
                }
                Utils.logEvents(LogEvents.NAP_ADJUSTED);
            }
        }.execute();
    }

    public void deleteEvent(final EventBean event) {
        new AsyncTask<Void, Void, Boolean>() {
            private SSException mException;

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
            protected Boolean doInBackground(Void... params) {
                try {
                    AccountManagement accountManagement = AccountManagement.getInstance(mContext);
                    return SKManagement.deleteEventById(accountManagement.getAccessToken(), event.getId());
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
                mTaskCount--;
                if (mTaskCount == 0) {
                    if (mProgressDlg != null && mProgressDlg.isShowing()) {
                        mProgressDlg.dismiss();
                    }
                    finishDialog();
                }
            }
        }.execute();
    }

    private void finishDialog() {
        Intent intent = new Intent();
        intent.putExtra("Event", mEventBean);
        setResult(NapSummaryActivity.NapTimeEditResultCode, intent);
        finish();
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
        //{"end_date":1483862500,
        // "start_date":1483862400}
        JSONObject body = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        body.put("end_date", mEndCalender.getTimeInMillis()/1000);
//        body.put("start_date", mStartCalender.getTimeInMillis()/1000);
        body.put("end_date", sdf.format(mEndCalender.getTime()));
        body.put("start_date", sdf.format(mStartCalender.getTime()));
        return body;
    }

    private JSONObject getJSON(Calendar start, Calendar end) throws JSONException {
        JSONObject body = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US);
        body.put("start_date", sdf.format(start.getTime()));
        body.put("end_date", sdf.format(end.getTime()));
        return body;
    }
}
