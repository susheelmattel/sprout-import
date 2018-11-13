/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.fuhu.states.interfaces.IStatePayload;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.adapter.TimeLine.LearningAdapter;
import com.sproutling.ui.adapter.TimeLine.LearningWeekAdapter;
import com.sproutling.ui.adapter.TimeLine.TimeLineAdapter;
import com.sproutling.ui.adapter.TimeLine.TimeLineWeekAdapter;
import com.sproutling.ui.dialog.TimelineDialog;
import com.sproutling.ui.dialogfragment.FutureBirthdayDialogFragment;
import com.sproutling.ui.view.TimeLine.TimelineCoachMarksBackground;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Const;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.EventListDay;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.TimelineUtils;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.sproutling.ui.activity.NapSummaryActivity.NapTimeEditResultCode;
import static com.sproutling.ui.activity.NapSummaryActivity.REQUEST_CODE_DELETE_SUMMARY;
import static com.sproutling.ui.activity.interfaces.ILogSleepView.CHILD_ID;

/**
 * Created by loren.hung on 2016/12/20.
 */

public class TimelineActivity extends BaseMqttActivity implements FutureBirthdayDialogFragment.OnFutureBirthdayListener {

    public static final String TOOLTIPS = "10000";

    public static final String FIRSTNAME = "name";
    public static final String ID = "id";
    public static final String BIRTHDATE = "birthdate";
    public static final String GENDER = "gender";
    public static final String FOR_REPLAY = "replay";

    private Context mContext;
    private FloatingActionMenu mFabMenu;
    private ShTextView mTxtDateView;
    private ShTextView mInformationTitleView;
    private ShTextView mInformationTxtView;
    private RecyclerView mWeekSummary;
    private boolean isLearningDone = false;

    private LearningAdapter mLearningAdapter;
    private TimeLineAdapter mTimeLineAdapter;
    private FrameLayout mCoachMarks;
    private static FrameLayout sFlDate;

    private static int sHeaderHeight;

    private TimelineCoachMarksBackground mTimelineCoachMarksBackground;

    private String mChildFirstName = "";
    private String mChildId = "";
    private String mChildBirthDate = "";
    private String mChildGender = "";
    private boolean isForReplay = false;
    private Calendar mBirthDate = Calendar.getInstance();
    //    Calendar mBirthDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    public static boolean sIsHeaderShow = false;

    private boolean mHasNapEvents;
    private boolean mGetEventsOK;
    private boolean mGetArticlesOK;

    private List<EventBean> mArticles;
    private List<EventBean> mEvents;
    private List<EventListDay> mDayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mContext = this;
        sHeaderHeight = (int) getResources().getDimension(R.dimen.margin3);

        mChildFirstName = getIntent().getStringExtra(FIRSTNAME);
        mChildId = getIntent().getStringExtra(ID);
        mChildBirthDate = getIntent().getStringExtra(BIRTHDATE);
        mChildGender = getIntent().getStringExtra(GENDER);
        isForReplay = getIntent().getBooleanExtra(FOR_REPLAY, false);

        String[] date = mChildBirthDate.split("-");
        mBirthDate.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]), 0, 0, 0);

        isLearningDone = SharedPrefManager.isLearningPeriodDone(this);

        boolean isTimelineFirstLaunch = SharedPrefManager.getBoolean(this, SharedPrefManager.SPKey.BOOL_TIMELINE_FIRST_LAUNCH, true);

        findViews();

        if (isForReplay) {
            mHomeHandler.sendEmptyMessage(1);
        } else if (isTimelineFirstLaunch) {
            SharedPrefManager.put(this, SharedPrefManager.SPKey.BOOL_TIMELINE_FIRST_LAUNCH, false);
            new TimelineDialog(mContext, mHomeHandler).show();
        } else {
            showProgressBar(true);
        }

        if (mBirthDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            FutureBirthdayDialogFragment.newInstance().show(getFragmentManager(), null);
        }

        mEvents = new ArrayList<>();
        mArticles = new ArrayList<>();

        getEvents();
        getArticles();
        if (!isLearningDone) {
            mLearningAdapter = new LearningAdapter(mContext, mChildFirstName, mChildBirthDate);
            mWeekSummary.setAdapter(mLearningAdapter);
//            mGetArticlesOK = true;
        } else {
            mTimeLineAdapter = new TimeLineAdapter(mContext, mChildFirstName, mChildBirthDate);
            mWeekSummary.setAdapter(mTimeLineAdapter);
//            getArticles();
        }
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        return null;
    }

    @Override
    public void onStateChanged(IStatePayload payload) {

    }

//    @Override
//    public void onStateChanged(IStatePayload payload) {
//        //TODO: Update new time date after add/edit sleep time
//        Log.e("onStateChanged", "Received");
//    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        sFlDate.setTranslationY(-getResources().getDimension(R.dimen.margin3));
    }

    private void findViews() {
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        sFlDate = (FrameLayout) findViewById(R.id.fl_date);
        mCoachMarks = (FrameLayout) findViewById(R.id.Coachmarks);
        mFabMenu.setClosedOnTouchOutside(true);
        FloatingActionButton addSleep = (FloatingActionButton) findViewById(R.id.fab_add_sleep);
        FloatingActionButton addMeasurement = (FloatingActionButton) findViewById(R.id.fab_add_measurement);
        findViewById(R.id.navigation_back).setOnClickListener(mOnClickListener);
        ShTextView titleView = (ShTextView) findViewById(R.id.navigation_title);
        mTxtDateView = (ShTextView) findViewById(R.id.txt_date);
        titleView.setText(mChildFirstName);
        mInformationTitleView = (ShTextView) findViewById(R.id.information_title);
        mInformationTxtView = (ShTextView) findViewById(R.id.information_txt);
        mInformationTxtView.setText(String.format(getString(R.string.enter_information_txt), mChildFirstName));

        ShTextView gotItView = (ShTextView) findViewById(R.id.Txt_GotIt);

        mTimelineCoachMarksBackground = (TimelineCoachMarksBackground) findViewById(R.id.CoachmarksBackground);

        mWeekSummary = (RecyclerView) findViewById(R.id.rv_timeline);
        mWeekSummary.setLayoutManager(new LinearLayoutManager(this));

        titleView.setOnClickListener(mOnClickListener);
        addSleep.setOnClickListener(mOnClickListener);
        addMeasurement.setOnClickListener(mOnClickListener);
        gotItView.setOnClickListener(mOnClickListener);

        mWeekSummary.setOnScrollListener(mOnScrollListener);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (recyclerView != null && recyclerView.getChildCount() > 0) {
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItemPosition);

                if (isLearningDone) {
                    TimeLineAdapter.ItemHolder itemHolder = (TimeLineAdapter.ItemHolder) viewHolder;

                    if (itemHolder != null) {
                        int[] recyclerViewLocationInWindow = new int[2];
                        recyclerView.getLocationInWindow(recyclerViewLocationInWindow);
                        int dim = recyclerViewLocationInWindow[1];
                        int[] itemLocationInWindow = new int[2];

                        itemHolder.weekSummaryExpandLayout.getLocationInWindow(itemLocationInWindow);

                        if (((itemLocationInWindow[1] - dim) < getResources().getDimension(R.dimen.paddingS)
                                && (itemLocationInWindow[1] - dim) > -getResources().getDimension(R.dimen.margin)) ||
                                (recyclerView.getChildAt(0).getTop() >= 0)) {
                            if (sIsHeaderShow) {
                                animateHeaderUp();
                                mTxtDateView.setText("");
                            }

                        }

                        if (itemHolder.eventListDataWeek.isExpand) {
                            if (itemHolder.getRecyclerView() != null && itemHolder.getRecyclerView().getChildCount() > 0) {
                                for (int j = 0; j < itemHolder.getRecyclerView().getChildCount(); j++) {
                                    RecyclerView.ViewHolder viewHolderWeek = itemHolder.getRecyclerView().findViewHolderForAdapterPosition(j);
                                    TimeLineWeekAdapter.TopItemHolder itemHolderWeek = (TimeLineWeekAdapter.TopItemHolder) viewHolderWeek;

                                    if (itemHolderWeek != null) {
                                        int[] dimTxt = new int[2];
                                        itemHolderWeek.textDay.getLocationInWindow(dimTxt);

                                        if ((dimTxt[1] - dim) < getResources().getDimension(R.dimen.paddingS)
                                                && (dimTxt[1] - dim) > -getResources().getDimension(R.dimen.margin)) {
                                            mTxtDateView.setText(TimelineUtils.getDateWeekString(TimelineActivity.this, itemHolderWeek.date));
                                            if (!sIsHeaderShow) {
                                                animateHeaderDown();
                                                sIsHeaderShow = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LearningAdapter.ItemHolder itemHolder = (LearningAdapter.ItemHolder) viewHolder;

                    if (itemHolder != null) {
                        int[] recyclerViewLocationInWindow = new int[2];
                        recyclerView.getLocationInWindow(recyclerViewLocationInWindow);
                        int dim = recyclerViewLocationInWindow[1];

                        if (recyclerView.getChildAt(0).getTop() <= 0
                                && recyclerView.getChildAt(0).getTop() > -(getResources().getDimension(R.dimen.paddingS) * 2)) {
                            mTxtDateView.setText("");
                            if (sIsHeaderShow) {
                                animateHeaderUp();
                            }
                        }
                        if (itemHolder.getRecyclerView() != null && itemHolder.getRecyclerView().getChildCount() > 0) {
                            for (int j = 0; j < itemHolder.getRecyclerView().getChildCount(); j++) {
                                RecyclerView.ViewHolder viewHolderWeek = itemHolder.getRecyclerView().findViewHolderForAdapterPosition(j);
                                LearningWeekAdapter.TopItemHolder itemHolderWeek = (LearningWeekAdapter.TopItemHolder) viewHolderWeek;

                                if (itemHolderWeek != null && recyclerView.getChildAt(firstVisibleItemPosition) != null) {
                                    int[] dimTxt = new int[2];
                                    itemHolderWeek.text_Day.getLocationInWindow(dimTxt);

                                    if ((dimTxt[1] - dim) < getResources().getDimension(R.dimen.paddingS) && (dimTxt[1] - dim) > -getResources().getDimension(R.dimen.margin)) {
                                        mTxtDateView.setText(TimelineUtils.getDateWeekString(TimelineActivity.this, itemHolderWeek.date));
                                        if (!sIsHeaderShow) {
                                            animateHeaderDown();
                                            sIsHeaderShow = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    private void animateHeaderDown() {
        sFlDate.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator());
    }

    public static void animateHeaderUp() {
        sFlDate.animate().translationY(-sHeaderHeight).setDuration(300).setInterpolator(new DecelerateInterpolator());
        sIsHeaderShow = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Back to Launch screen if the user is not logged in
        if (!AccountManagement.getInstance(this).isLoggedIn()) {
            finish();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_title:
                    mWeekSummary.getLayoutManager().scrollToPosition(0);
                    animateHeaderUp();
                    break;
                case R.id.fab_add_sleep:
                    Intent intentAddSleep = new Intent(TimelineActivity.this, LogSleepView.class);
                    intentAddSleep.putExtra(CHILD_ID, mChildId);
                    startActivityForResult(intentAddSleep, 0);
                    mFabMenu.close(false);
//                    Test Code to launch AddSleepActivity by Store
//                    HashMap map = new HashMap<String, Object>();
//                    map.put("CTX", mContext);
//                    store.dispatch(new Action(0, new Payload(map)));

//                    Test Code to launch NightSleepDialog
//                    new NightSleepDialog(mContext).show();
                    break;
                case R.id.fab_add_measurement:
                    Intent intentAddMeasurement = new Intent(mContext, AddMeasurementActivity.class);
                    mContext.startActivity(intentAddMeasurement);
                    mFabMenu.close(false);
                    break;
                case R.id.navigation_back:
                    finish();
                    break;
                case R.id.Txt_GotIt:
                    mCoachMarks.setVisibility(View.GONE);

                    if (isForReplay) {
                        setResult(Integer.valueOf(TOOLTIPS));
                        finish();
                    }
                    break;
                default:
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_CODE_DELETE_SUMMARY || resultCode == NapTimeEditResultCode || resultCode == LogSleepView.Status.STOP) {
            showProgressBar(true);
            getEvents();
            if (isLearningDone) {
                getArticles();
            } else {
                mGetArticlesOK = true;
            }
        }
    }

    private Handler mHomeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (msg.what == 0) {
                mCoachMarks.setVisibility(View.VISIBLE);

                mInformationTitleView.setText(getString(R.string.enter_information));
                mInformationTxtView.setText(String.format(getString(R.string.enter_information_txt), mChildFirstName));
                mTimelineCoachMarksBackground.setCircle((int) mFabMenu.getMenuIconView().getX() + mFabMenu.getMenuIconView().getHeight() / 2,
                        (int) mFabMenu.getMenuIconView().getY() + mFabMenu.getMenuIconView().getHeight() / 2);
            } else if (msg.what == 1) {
                mCoachMarks.setVisibility(View.VISIBLE);
                mInformationTitleView.setText(getString(R.string.add_a_missed_nap));
                mInformationTxtView.setText(getString(R.string.add_a_missed_nap_txt));
                mTimelineCoachMarksBackground.setCircle((int) mFabMenu.getMenuIconView().getX() + mFabMenu.getMenuIconView().getHeight() / 2,
                        (int) mFabMenu.getMenuIconView().getY() + mFabMenu.getMenuIconView().getHeight() / 2);

            }
        }
    };

    private void getEvents() {
        mGetEventsOK = false;
        new AsyncTask<Void, Void, List<EventBean>>() {
            SSError mError;

            @Override
            protected List<EventBean> doInBackground(Void... params) {
                try {
                    List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(AccountManagement.getInstance(mContext).getAccessToken(), mChildId);
                    return TimelineUtils.getListEvents(events, mBirthDate);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<EventBean> list) {
                mHasNapEvents = hasNapEvent(list);
                if (list == null || list.isEmpty()) {
                    Utils.logEvents(LogEvents.TIMELINE_NO_DATA);
                }
                if (list != null) {
                    mEvents = list;
                } else if (mError != null) {
                    TimelineUtils.handleError(mContext, mError);
                    showProgressBar(false);
                }
                if (mGetArticlesOK) {
                    eventAddArticle();
                }
                mGetEventsOK = true;
            }
        }.execute();
    }

    private void getArticles() {
        mGetArticlesOK = false;
        new AsyncTask<Void, Void, List<EventBean>>() {
            SSError mError;

            @Override
            protected List<EventBean> doInBackground(Void... params) {
                long startTimeInMillis = SharedPrefManager.getLong(mContext, SharedPrefManager.SPKey.LONG_LEARNING_PERIOD_START_TIMESTAMP, -1L);
                if (startTimeInMillis < 0) {
                    return Collections.emptyList();
                }
                int startDay = (int) ((startTimeInMillis - mBirthDate.getTimeInMillis()) / Const.TIME_MS_DAY);

                Calendar today = Calendar.getInstance();
//                Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                int endAgeDay = (int) ((today.getTimeInMillis() - mBirthDate.getTimeInMillis()) / Const.TIME_MS_DAY);
                if (endAgeDay == 0) endAgeDay = 1;

                try {
                    List<SSManagement.SSArticle> articles = SKManagement.listArticles(AccountManagement.getInstance(mContext).getAccessToken(), startDay, endAgeDay);
                    return TimelineUtils.getListArticles(articles, mBirthDate);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<EventBean> list) {
                if (list != null) {
                    mArticles = list;
                } else if (mError != null) {
                    TimelineUtils.handleError(mContext, mError);
                }
                if (mGetEventsOK) {
                    eventAddArticle();
                }
                mGetArticlesOK = true;
            }
        }.execute();
    }

    private void eventAddArticle() {
        List<EventBean> allList = new ArrayList<>();

        allList.addAll(mEvents);
        allList.addAll(mArticles);
        mDayList = TimelineUtils.getEventListDay(this, allList, mBirthDate);

        if (!isLearningDone) {
            mLearningAdapter.setChildDate(mChildGender, mDayList);
        } else {
            mTimeLineAdapter.setChildDate(mChildFirstName, mChildBirthDate, mChildGender, mDayList);
        }
        showProgressBar(false);

//        if (mBirthDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
//            FutureBirthdayDialogFragment.newInstance().show(getFragmentManager(), null);
//        }
    }

    private boolean hasNapEvent(List<EventBean> events) {
        if (events == null || events.isEmpty()) return false;
        for (EventBean event : events) {
            if (TimelineUtils.NAP.equalsIgnoreCase(event.getEventType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFutureBirthdayAcknowledged() {
        finish();
    }
}