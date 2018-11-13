/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */
package com.sproutling.ui.activity;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;
import com.google.protobuf.Timestamp;
import com.sproutling.App;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.broadcast.AlarmReceiver;
import com.sproutling.broadcast.ConnectReceiver;
import com.sproutling.databinding.ActivityStatusBinding;
import com.sproutling.object.HubBandStatusTopicEvent;
import com.sproutling.object.HubBandTelemetryTopicEvent;
import com.sproutling.object.HubMusicTimerEvent;
import com.sproutling.object.HubNightLightTimerEvent;
import com.sproutling.object.HubSleepStatusTopicEvent;
import com.sproutling.object.HubStatusTopicEvent;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.pipeline.callback.ConnectMqttCallback;
import com.sproutling.pipeline.callback.DisconnectMqttCallback;
import com.sproutling.pipeline.callback.PublishMqttMessageCallback;
import com.sproutling.pipeline.callback.SubscribeMqttCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.pojos.UserStatusFeedbackRequestBody;
import com.sproutling.pojos.UserStatusFeedbackResponseBody;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.activity.interfaces.ILogSleepView;
import com.sproutling.ui.dialog.IntegrationDialog;
import com.sproutling.ui.dialog.LearningPeriodDialog;
import com.sproutling.ui.dialog.LearningPeriodFinishDialog;
import com.sproutling.ui.dialog.WhatsNewDialog;
import com.sproutling.ui.dialogfragment.EOLUpdateDialogFragment;
import com.sproutling.ui.dialogfragment.WifiInterferenceDialogFragment;
import com.sproutling.ui.fragment.status.CoachmarkFragment;
import com.sproutling.ui.fragment.status.DrawerLayoutFragment;
import com.sproutling.ui.view.StatusScreen.StatusScreenCoachmarksBackground;
import com.sproutling.ui.widget.AnimationProgress;
import com.sproutling.ui.widget.NotificationLayout;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.ui.widget.StatusView;
import com.sproutling.utils.Const;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.EventBean;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.SharedPrefManager.SPKey;
import com.sproutling.utils.TimelineUtils;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sproutling.EventOuterClass;
import sproutling.Hub;
import sproutling.Sleep;

import static com.sproutling.broadcast.ConnectReceiver.isAlreadyNoService;
import static com.sproutling.ui.activity.interfaces.ILogSleepView.CHILD_ID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class StatusActivity extends BaseMqttActivity implements  EOLUpdateDialogFragment.OnLearnMoreListener{

    public static final String TAG = StatusActivity.class.getSimpleName();
    public static final int SCENE_STATUS = 10;
    public static final int SCENE_LEARNING_PERIOD = 20;
    public static final int UPDATE_CLOCK_UI = 1;
    public static final int UPDATE_HR_LOCATION = 2;
    private static final int START_COLOR = 1;
    private static final int END_COLOR = 2;
    private static final long DURATION_SCENE_TRANS = 300;
    private static final int ACTIVITY_RESULT_CODE_SET_SLEEP = 1000;
    private static final int ACTIVITY_RESULT_CODE_SETTING = 2000;
    private static final int ACTIVITY_RESULT_CODE_TIMELINE = 3000;
    private static final int HUB_WIFI_SIGNAL_MAX_SIGNAL_STRENGTH = 0;
    private static final int HUB_WIFI_SIGNAL_MIN_SIGNAL_STRENGTH = -120;
    private static final int HUB_WIFI_SIGNAL_CUTOFF_SIGNAL_STRENGTH = -30;
    private static final long WIFI_INTERFERENCE_DURATION_MS = MILLISECONDS.convert((24 * 60), MINUTES);
    private static final String FIRST_INSTALL_VERSION = "first_install_version";
    public static StatusActivity sInstance;
    public static boolean sIsLearningPeriodCompleted;
    public static int sBgColorStartCatch, sBgColorEndCatch;
    //onActivityResult()
    public static boolean sIsCalledByReplay = false;
    private static long rolloverSixMonthEndTime;
    public SSManagement.Child mCurrentChildItem;
    //    private ShCustomProgressBar mShCustomProgressBar;
    //To update UI by MqttEvent.
    public int mBatteryValue = 0;
    public int mHeartRateValue = 0;
    private ActivityStatusBinding mBinding;
    private GestureDetectorCompat myGestureDetectorCompat;
    //onCreate()
    private ConnectReceiver mConnectionReceiver;
    private IntegrationDialog mIntegrationDlg;
    //onStateChange()
    private String mSerialNumber;
    ConnectMqttCallback connectMqttCallback = new ConnectMqttCallback() {
        @Override
        public void onResultReceived(MqttItem mqttItem) {
            disPatchAction(Actions.GET_DEVICE_SERIAL, new Payload().put(Actions.Key.INDEX, 0));
            isAlreadyNoService = false;
        }

        @Override
        public void onFailed(int status, String message) {
            if (Utils.isOnline(StatusActivity.this)) {
                try {
                    Thread.sleep(3000);
                    connectMqtt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                EventOuterClass.Event noServiceEvent = EventOuterClass.Event.newBuilder(EventOuterClass.Event.getDefaultInstance()).setEvent(EventOuterClass.EventType.NO_SERVICE).build();
                disPatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, noServiceEvent));
            }
        }
    };
    private FrameLayout mTopBarLayout;
    private ImageView mImgMenu;
    private View.OnClickListener mStatusOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showStatusCardFeedback();
        }
    };
    private String mPreStatus, mCurStatus;
    //To init LEARNING_PERIOD.
    private Scene mSceneLearningPeriod, mSceneStatus;
    private TransitionSet mSceneTransitionSet;
    private StatusView mStatusViewLP;
    //To set the mAnimator about hr location and log sleep clock ui.
    private ObjectAnimator mAnimator;
    private long mStartTime;
    private Timer mClockTimerRecording;
    private Timer mHeartPositionRecording;
    private boolean isStartClockTimerRecording = false;
    private boolean isStartHeartLocationRecording = false;
    //To record the current scene status.
    private int mCurrentScene = SCENE_STATUS;
    //To save the predict sleep time
    private Sleep.SleepPrediction mPrediction;
    //To set correct sleep/wake label
    private Sleep.SleepStatus mSleepStatus;
    //To check sleep/wake status
    private Hub.BandState mBandState;
    private ImageView mImgFeedBack;
    private ShTextView mStatusTxt;
    private BottomSheetBehavior mBottomSheetBehavior;
    GestureDetector.OnGestureListener myOnGestureListener = new GestureDetector.OnGestureListener() {
        static final int actMovement = 200;

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            Log.d(TAG, "onSingleTapUp");
            onMenuClickOrSwipe();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
            Log.d(TAG, "onFling");
            if (e1.getY() > e2.getY()) {
                Log.d(TAG, "up swipe e1.getY() < e2.getY()");

            } else {
                Log.d(TAG, "down swipe e1.getY() > e2.getY()");

            }
            onMenuClickOrSwipe();
            return true;
        }
    };
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CLOCK_UI:
                    setCurrentTimeSpacing();
                    break;

                case UPDATE_HR_LOCATION:
                    Calendar c = Calendar.getInstance();
                    int nowHour = c.get(Calendar.HOUR_OF_DAY);
                    int nowMinute = c.get(Calendar.MINUTE);
                    if (mBinding != null && mBinding.statusView != null) {
                        mBinding.statusView.setIconCurrentPosition(nowHour, nowMinute); //Set StatusView
                    }
                    break;
            }
        }
    };
    private TimerTask mSleepTimerRecordingTask = new TimerTask() {
        @Override
        public void run() {
            if (isStartClockTimerRecording) {
                Message message = new Message();
                message.what = UPDATE_CLOCK_UI;
                mHandler.sendMessage(message);
            }
        }
    };
    private TimerTask mHeartPositionRefreshTask = new TimerTask() {
        @Override
        public void run() {
            if (isStartHeartLocationRecording) {
                Message message = new Message();
                message.what = UPDATE_HR_LOCATION;
                mHandler.sendMessage(message);
            }
        }
    };
    private boolean mUserOpenedHubControls = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //reset mSerialNumber to avoid "mqtt don't subscribe when re-launch from home" issue
//        mSerialNumber = null;

        SSManagement.EOLData eolData = AccountManagement.getInstance(StatusActivity.this).readEOLData();
        if(eolData != null){
            EOLUpdateDialogFragment.newInstance(eolData.settingsPopUpSupportWebsite, getString(R.string.dialog_important_notice),
                    eolData.popUpText,getString(R.string.dialog_eol_learn_more)).show(getFragmentManager(), null);
        }

        sInstance = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //To set View Components by databinding.
        setViewComponent();

        initState();

        //mShCustomProgressBar is a custom progress bar that display when data is loading.
//        mShCustomProgressBar = new ShCustomProgressBar(sInstance);

        // Setup refresh timers
        setupTimers();

        //To check if the sleep timer is recording.
        setSleepTimer();

        //mIntegrationDlg is a customize dialog. It contents Stirring, Roll over, learningPeriod Dialog.
        mIntegrationDlg = new IntegrationDialog(StatusActivity.this);

        //Gesture Detector
        myGestureDetectorCompat = new GestureDetectorCompat(this, myOnGestureListener);

        //mConnectionReceiver is a broadcast receiver to catch the wifi status.
        mConnectionReceiver = new ConnectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mConnectionReceiver, intentFilter);

        //To connect the MQTT
        if (SharedPrefManager.getDevice(this) != null) connectMqtt();

        //To check if it's already set the roll over six month end time, if didn't set yet, then set it.
        rolloverSixMonthEndTime = SharedPrefManager.getLong(this, SPKey.LONG_ROLLOVER_SIX_MONTH_ENDTIME, (long) (-1));
        if (rolloverSixMonthEndTime == -1) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 6); // Set the LogSleep Alarm wake up time.
            SharedPrefManager.put(this, SharedPrefManager.SPKey.LONG_ROLLOVER_SIX_MONTH_ENDTIME, cal.getTimeInMillis());
        }

        //To check if it's already set the log sleep alarm, if didn't set yet, then set it.
        long logSleepAlarmTime = SharedPrefManager.getLong(this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_ALARM_END_TIME, (long) (-1));
        if (logSleepAlarmTime == -1) {
            setLogSleepAlarm(-1);
        }

        //Initial the background color to green.
        sBgColorStartCatch = sBgColorEndCatch = R.color.statusgreen;

        //To check if the learning period duration is complete or not.
        sIsLearningPeriodCompleted = SharedPrefManager.isLearningPeriodDone(this);
        if (!sIsLearningPeriodCompleted) {
            initLearningPeriod();
        }

        //To get Child list (now we set child in this way.)
        SSManagement.Child child = AccountManagement.getInstance(StatusActivity.this).getChild();
        if (child == null) {
            disPatchAction(Actions.LIST_CHILD, null);
        } else {
            checkChild(child);
        }

        showNotification(NotificationLayout.Type.EXPECTATIONS);
        showWhatsNewDialog();
    }

    private void setViewComponent() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_status);
        mBinding.setBtnHandler(new BtnHandler());
        mBinding.childNameButton.setDrawablePadding();
        mImgFeedBack = findViewById(R.id.imgFeedBack);
        mStatusTxt = findViewById(R.id.txtTitle);
        mTopBarLayout = findViewById(R.id.topBarLayout);
        mImgMenu = findViewById(R.id.img_control);
        mTopBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mImgFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStatusCardFeedback();
            }
        });
        setUpBottomSheetBehavior();
    }

    private void setUpBottomSheetBehavior() {

        FrameLayout llBottomSheet = (FrameLayout) findViewById(R.id.frame);


        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.status_view_peek_height));

        mBottomSheetBehavior.setHideable(false);


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    if (States.StatusValue.HUB_OFFLINE.equalsIgnoreCase(mCurStatus) &&
                            States.StatusValue.FIRMWARE_UPDATING.equalsIgnoreCase(mCurStatus) &&
                            States.StatusValue.NO_SERVICE.equalsIgnoreCase(mCurStatus)) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //if user has opened the view already and trying to close now, then try saving the settings if any change.
                    if (mUserOpenedHubControls) {
                        DrawerLayoutFragment drawerLayoutFragment = (DrawerLayoutFragment) getFragmentManager().findFragmentById(R.id.drawerLayoutFragment);
                        drawerLayoutFragment.saveHubControlSettings();
                    }
                    mTopBarLayout.setBackgroundColor(getResources().getColor(R.color.white));
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mUserOpenedHubControls = true;
                    mTopBarLayout.setBackgroundColor(getResources().getColor(R.color.greyF8));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        llBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }

//    private void showView(View view, boolean isVisible) {
//        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
//    }

    private void initState() {
        mPreStatus = States.StatusValue.INITIAL;
        mCurStatus = States.StatusValue.INITIAL;

        SSManagement.DeviceResponse device = SharedPrefManager.getDevice(this);
        if (device == null) {
//            mCurStatus = States.StatusValue.NO_CONFIGURED_DEVICE;
            setNoDeviceConfigured();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPrefManager.getDevice(this) != null && mSerialNumber == null) connectMqtt();
        //To check if the alarm is dismiss by app close.
        checkLowBatteryAlarm();
        checkTimelineAndLogSleepAlarm();
    }

    @Override
    public void onStateChanged(IStatePayload iStatePayload) {
        // Get all data from payload...
        Payload payload = (Payload) iStatePayload;
//        EventOuterClass.Event mqttEvent = (EventOuterClass.Event) mPayload.get(States.Key.MQTT_EVENTS);
        SSManagement.Child child = (SSManagement.Child) payload.get(States.Key.DATAITEM_CHILD);
        String pStatus = payload.getString(States.Key.PRE_STATUS);
        String cStatus = payload.getString(States.Key.CURRENT_STATUS);

        //update mPreStatus and mCurStatus.
        if (pStatus != null) mPreStatus = pStatus;
        if (cStatus != null) {
//            if (States.StatusValue.DETECTING.equals(cStatus)) { // detecting does not overwrite states other than initial
//                if (States.StatusValue.INITIAL.equals(mCurStatus)) {
//                    mCurStatus = cStatus;
//                }
//            } else if (!mCurStatus.equals(cStatus)) {
            mCurStatus = cStatus;
//            }
        }

        String lpStatus = payload.getString(States.Key.LEARNING_PERIOD_STATUS);
        if (lpStatus != null) {
            sIsLearningPeriodCompleted = SharedPrefManager.isLearningPeriodDone(this);
        }

        //update Serial Number.
        String serialNumber = payload.getString(States.Key.DEVICE_SERIAL);
        if (serialNumber != null) {
            if (serialNumber.equals(States.Value.DEVICE_NULL)) {
//                mCurStatus = States.StatusValue.NO_CONFIGURED_DEVICE;
                setNoDeviceConfigured();
            } else if (!serialNumber.equals(mSerialNumber)) {
                mSerialNumber = serialNumber;
                subscribeTopics(serialNumber);
            }
        }

        //update Child Item.
        checkChild(child);

        Sleep.SleepPrediction sleepPredictionData = (Sleep.SleepPrediction) payload.get(States.Key.MQTT_SLEEP_PREDICTION);
        if (sleepPredictionData != null) {
            mPrediction = sleepPredictionData;
        }

        Sleep.SleepStatus sleepStatusData = (Sleep.SleepStatus) payload.get(States.Key.MQTT_SLEEP_STATUS);
        if (sleepStatusData != null) {
            mSleepStatus = sleepStatusData;
        }

        Hub.BandState bandState = (Hub.BandState) payload.get(States.Key.MQTT_BAND_STATE);
        if (bandState != null) {
            mBandState = bandState;
        }

        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTIVITY_RESULT_CODE_SET_SLEEP:
                switch (resultCode) {
                    case LogSleepView.Status.START:
                        isStartClockTimerRecording = false;
                        break;

                    case LogSleepView.Status.RECORDING:
                        mStartTime = data.getExtras().getLong(ILogSleepView.SLEEP_START_TIME_MS);
//                        mStartTime = data.getExtras().getLong("StartCalenderMillis");
                        setCurrentTimeSpacing();
                        mBinding.setBtnImgResource(R.drawable.ic_timer_red);
                        showView(mBinding.imgFloatBtnTick, View.VISIBLE);

                        if (mAnimator == null) {
                            mAnimator = ObjectAnimator.ofFloat(mBinding.imgFloatBtnTick, "rotation", 0, 360);
                            mAnimator.setDuration(6000);
                            mAnimator.setRepeatCount(-1);
                            mAnimator.setInterpolator(new LinearInterpolator());
                        }
                        mAnimator.start();
                        isStartClockTimerRecording = true;
                        break;

                    case LogSleepView.Status.STOP:
                        if (mAnimator != null)
                            mAnimator.cancel();
                        isStartClockTimerRecording = false;
                        mBinding.setBtnImgResource(R.drawable.ic_timer_white);
                        showView(mBinding.imgFloatBtnTick, View.GONE);
                        mBinding.setBtnTitle(getString(R.string.status_title_btn));

                        setLogSleepAlarm(-1);

                        break;
                }
                break;

            case ACTIVITY_RESULT_CODE_SETTING:

                switch (String.valueOf(resultCode)) {
                    case SettingsActivity.TOOLTIPS:
                        sIsCalledByReplay = true;
                        if (CoachmarkFragment.sInstance != null) {
                            CoachmarkFragment.sInstance.setTooltipsVisible(true);
                            CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.WELCOME_PAGE);
                        }
                        break;
                }
                break;

            case ACTIVITY_RESULT_CODE_TIMELINE:

                switch (String.valueOf(resultCode)) {
                    case TimelineActivity.TOOLTIPS:
                        if (CoachmarkFragment.sInstance != null) {
                            CoachmarkFragment.sInstance.setTooltipsVisible(true);
                            CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.PROGRESS_PAGE);
                        }
                        break;
                }
                break;
        }
    }

    private void showView(View view, int visibility) {
//        if (visible == View.VISIBLE || visible == View.GONE || visible == View.INVISIBLE) {
        view.setVisibility(visibility);
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        disconnectMqtt();

        if (mIntegrationDlg.isShowing()) {
            mIntegrationDlg.dismiss();
        }

        if (mConnectionReceiver != null) {
            unregisterReceiver(mConnectionReceiver);
        }

        if (mClockTimerRecording != null) {
            mClockTimerRecording.cancel();
        }

        if (mHeartPositionRecording != null) {
            mHeartPositionRecording.cancel();
        }

        sIsLearningPeriodCompleted = false;
        sInstance = null;
        App.getInstance().getStore().reset();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        myGestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void setNoDeviceConfigured() {
        Action action = new Action(Actions.STATUS_UPDATE, this, new Payload());
        ((Payload) action.payload()).put(States.Key.PRE_STATUS, mCurStatus);
        ((Payload) action.payload()).put(States.Key.CURRENT_STATUS, States.StatusValue.NO_CONFIGURED_DEVICE);
        App.getInstance().getStore().dispatch(action);
    }

    /*
     * User should see this dialog after every update and NOT on the first install
     */
    private void showWhatsNewDialog() {
        String new_version = BuildConfig.VERSION_NAME;
        String current_version = SharedPrefManager.getString(App.getInstance(), SPKey.APP_CURRENT_VERSION, FIRST_INSTALL_VERSION);

        if (current_version.equals(FIRST_INSTALL_VERSION)) {
            SharedPrefManager.put(App.getInstance(), SPKey.APP_CURRENT_VERSION, new_version);
            return;
        }

        if (!current_version.equals(new_version)) {
            SharedPrefManager.put(App.getInstance(), SPKey.APP_CURRENT_VERSION, new_version);

            WhatsNewDialog whatsNewDialog = new WhatsNewDialog(this);
            whatsNewDialog.show();
        }
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        //because the topics are already subscribed
        return null;
    }

    @Override
    protected void onHubSleepStatusTopicEvent(HubSleepStatusTopicEvent hubSleepStatusTopicEvent) {
        Sleep.SleepStatus sleepStatusData = hubSleepStatusTopicEvent.getSleepStatus();
        if (sleepStatusData != null) {
            mSleepStatus = sleepStatusData;
        }
    }

    @Override
    protected void onHubStatusTopicEvent(HubStatusTopicEvent hubStatusTopicEvent) {
        Hub.HubStatus hubStatus = hubStatusTopicEvent.getHubStatus();
        if (hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.HUB_FLASH_START ||
                hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.WEARABLE_FLASH_START) {
            Action action = new Action(Actions.STATUS_UPDATE, this, new Payload());
            ((Payload) action.payload()).put(States.Key.PRE_STATUS, mCurStatus);
            ((Payload) action.payload()).put(States.Key.CURRENT_STATUS, States.StatusValue.FIRMWARE_UPDATING);
            App.getInstance().getStore().dispatch(action);
        }
        //checkWifiInterference(hubStatus.getWifiConnection().getSignal());
    }

    private void checkWifiInterference(int signalStrength) {
        Log.d(TAG, "Wifi Signal Strength:" + String.valueOf(signalStrength));
        if (HUB_WIFI_SIGNAL_MAX_SIGNAL_STRENGTH > signalStrength && signalStrength >= HUB_WIFI_SIGNAL_CUTOFF_SIGNAL_STRENGTH) {
            Date wifiInterferenceDate = SharedPrefManager.getWifiInterferenceDate(this);
            if (wifiInterferenceDate != null) {
                Calendar todayCalendar = Calendar.getInstance(Locale.getDefault());
                if (todayCalendar.getTime().getTime() - wifiInterferenceDate.getTime() >= WIFI_INTERFERENCE_DURATION_MS) {
                    showWifiInterferenceDialog();
                }
            } else {
                showWifiInterferenceDialog();
            }

        }
    }

    private void showWifiInterferenceDialog() {
        SharedPrefManager.saveWifiInterferenceDate(this, Calendar.getInstance(Locale.getDefault()).getTime());
        WifiInterferenceDialogFragment wifiInterferenceDialogFragment = new WifiInterferenceDialogFragment();
        wifiInterferenceDialogFragment.show(getFragmentManager(), null);
    }

    @Override
    protected void onHubBandStatusTopicEvent(HubBandStatusTopicEvent hubBandStatusTopicEvent) {
        super.onHubBandStatusTopicEvent(hubBandStatusTopicEvent);
        mBatteryValue = Utils.getBatteryValue(hubBandStatusTopicEvent.getBandStatus().getBatteryVoltage());

        if (mBatteryValue <= 20) {
            if (!States.StatusValue.INITIAL.equals(mCurStatus) && !States.StatusValue.WEARABLE_CHARGING.equals(mCurStatus)
                    && !States.StatusValue.WEARABLE_OUT_OF_BATTERY.equals(mCurStatus)) {
                showNotification(NotificationLayout.Type.LOW_BATTERY);
            }
        } else if (NotificationLayout.Type.LOW_BATTERY.equals(mBinding.notificationLayout.getType())) {
            mBinding.notificationLayout.dismissLowBattery();
        }
        updateUI();
    }

    @Override
    protected void onHubBandTelemetryTopicEvent(HubBandTelemetryTopicEvent hubBandTelemetryTopicEvent) {
        super.onHubBandTelemetryTopicEvent(hubBandTelemetryTopicEvent);
        mHeartRateValue = hubBandTelemetryTopicEvent.getBandTelemetry().getHrBpm();
    }

    @Override
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {
        super.onHubUserConfiguration(hubUserConfiguration);
        App.getInstance().setHubUserConfiguration(hubUserConfiguration);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubMusicTimerEvent hubMusicTimerEvent) {
        Hub.HubControl hubControl = Hub.HubControl.newBuilder().setMusicTimer(hubMusicTimerEvent.getTimerSeconds()).build();
        publish(hubControl);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubNightLightTimerEvent hubNightLightTimerEvent) {
        Hub.HubControl hubControl = Hub.HubControl.newBuilder().setLedTimer(hubNightLightTimerEvent.getTimerSeconds()).build();
        publish(hubControl);
    }

    public void setLearningPeriodCompleted(boolean isCompleted) {
        sIsLearningPeriodCompleted = isCompleted;
        if (isCompleted && sInstance != null) {
            showView(mBinding.linearFloatButton, View.GONE);
        }
    }

    private void setupTimers() {
        //mClockTimerRecording is a Timer to refresh the clock ui when the sleep timer is recording.
        mClockTimerRecording = new Timer();
        mClockTimerRecording.schedule(mSleepTimerRecordingTask, 0, 1000); //1 sec

        //mHeartPositionRecording is a Timer to refresh the circle heart positon every 2 minutes.
        mHeartPositionRecording = new Timer();
        mHeartPositionRecording.schedule(mHeartPositionRefreshTask, 0, 120000); //2 mins
    }

    private void setSleepTimer() {
        boolean isSleepTimerOnRecording = SharedPrefManager.getBoolean(this, SPKey.BOOL_SLEEP_TIMER_RECORDING, false);
        if (isSleepTimerOnRecording) {
            mStartTime = SharedPrefManager.getLong(this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (long) (-1));
            setCurrentTimeSpacing();
            mBinding.setBtnImgResource(R.drawable.ic_timer_red);
            showView(mBinding.imgFloatBtnTick, View.VISIBLE);
            if (mAnimator == null) {
                mAnimator = ObjectAnimator.ofFloat(mBinding.imgFloatBtnTick, "rotation", 0, 360);
                mAnimator.setDuration(6000);
                mAnimator.setRepeatCount(-1);
                mAnimator.setInterpolator(new LinearInterpolator());
            }
            mAnimator.start();
            isStartClockTimerRecording = true;
        } else {
            mBinding.setBtnImgResource(R.drawable.ic_timer_white);
            showView(mBinding.imgFloatBtnTick, View.GONE);
            mBinding.setBtnTitle(getString(R.string.status_title_btn));
        }
    }

    private void initLearningPeriod() {
        //scene init
        mSceneTransitionSet = new TransitionSet();
        mSceneTransitionSet.setDuration(DURATION_SCENE_TRANS);
//        mSceneTransitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        mSceneTransitionSet.addTransition(new Fade());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSceneTransitionSet.addTransition(new ChangeImageTransform());
//            mSceneTransitionSet.addTransition(new ChangeBounds());
//        }
        ViewGroup sceneRoot = mBinding.sceneRoot;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSceneStatus = new Scene(sceneRoot, mBinding.containerStatus);
        }
        mSceneLearningPeriod = Scene.getSceneForLayout(sceneRoot, R.layout.scene_learning_period, this);

        initSceneStatus();
    }

    private void checkChild(SSManagement.Child child) {
        if (child != null) {
            // check if learning period is done
            disPatchAction(Actions.GET_LEARNING_PERIOD_TIMESTAMP, new Payload().put(Actions.Key.CHILD_ID, child.id));
            mCurrentChildItem = child;

            if (SharedPrefManager.getDevice(this) != null)
                //To get the last end time to set the 12 hour alarm.
                checkLastLogEndTime();
        }
    }

    private void checkLastLogEndTime() {
        Calendar mBirthDate = Calendar.getInstance();
        String[] date = mCurrentChildItem.birthDate.split("-");
        mBirthDate.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]), 0, 0, 0);
        getEvents(mBirthDate);
    }

    private void getEvents(final Calendar birthDate) {
        new AsyncTask<Void, Void, List<EventBean>>() {
            SSError mError;
            List<EventBean> mEvents = new ArrayList<>();

            @Override
            protected List<EventBean> doInBackground(Void... params) {
                try {
                    List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(AccountManagement.getInstance(sInstance).getAccessToken(), mCurrentChildItem.id);
                    return TimelineUtils.getListEvents(events, birthDate);
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
                if (list == null || list.size() == 0) {
//                    if (mError != null) {
//                        TimelineUtils.handleError(sInstance, mError);
//                    }
                    Utils.logEvents(LogEvents.TIMELINE_NO_DATA);
                    setLogSleepAlarm(-1);
                } else {
                    mEvents = list;
                    setLogSleepAlarm(getLastNapTimeInMillis(mEvents));
                }
            }
        }.execute();
    }

    private long getLastNapTimeInMillis(List<EventBean> events) {
        long lastNapEndTimeInMillis = -1;
        for (int i = 0; i < events.size(); i++) {
            EventBean eventBean = events.get(i);
            if (TimelineUtils.NAP.equals(eventBean.getEventType())) {
                Calendar endTime = eventBean.getEndTime();
                if (endTime.getTimeInMillis() > lastNapEndTimeInMillis)
                    lastNapEndTimeInMillis = endTime.getTimeInMillis();
            }
        }
        return lastNapEndTimeInMillis;
    }

    public void setLogSleepAlarm(long lastEndDateMillis) {
        Calendar cal = Calendar.getInstance();

        if (lastEndDateMillis != -1) {
            cal.setTimeInMillis(lastEndDateMillis); // Set the LogSleep Alarm wake up time by last end date time.(or set it by current time)
        }

        cal.add(Calendar.HOUR, 12);
        SharedPrefManager.put(this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_ALARM_END_TIME, cal.getTimeInMillis());
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("msg", "log_sleep_alarm");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pi != null) am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);

        if (lastEndDateMillis != -1) {
            checkTimelineAndLogSleepAlarm(); //if get new end time from server, need to check again.
        }
    }

    /**
     * To update UI by MQTT Event.
     */
    public void updateUI() {
//        mCurStatus = States.StatusValue.AWAKE;
        setBgShapeAndColor(mPreStatus, mCurStatus);
        setViewByStatus(mCurStatus);
    }

    private void setBgShapeAndColor(String preStatus, String curStatus) {
//        mBinding.setBgShape(R.drawable.shape_status_bg);
//        mBinding.setBgStartColor(getBgColor(START_COLOR, preStatus));
        mBinding.setBgStartColor(getBgColor(START_COLOR, curStatus));
        mBinding.setBgEndColor(getBgColor(END_COLOR, curStatus));
    }

    private int getBgColor(int type, String status) {

        int color = -1;

        if (status == null) {
            if (type == START_COLOR) return sBgColorStartCatch;
            else return sBgColorEndCatch;
        }

        switch (status) {
            case States.StatusValue.HEART_RATE:
            case States.StatusValue.WEARABLE_BATTERY:
            case States.StatusValue.UNKNOWN:
                if (type == START_COLOR) return sBgColorStartCatch;
                else return sBgColorEndCatch;

            case States.StatusValue.UNUSUAL_HEARTBEAT:
            case States.StatusValue.ROLLED_OVER:
                color = R.color.statusred;
                break;

            case States.StatusValue.WEARABLE_CHARGING:
            case States.StatusValue.WEARABLE_TOO_FAR_AWAY:
            case States.StatusValue.WEARABLE_NOT_FOUND:
            case States.StatusValue.WEARABLE_FELL_OFF:
            case States.StatusValue.HUB_OFFLINE:
            case States.StatusValue.WEARABLE_OUT_OF_BATTERY:
            case States.StatusValue.NO_SERVICE:
            case States.StatusValue.FIRMWARE_UPDATING:
                color = R.color.statusyellow;
                break;

            default:
                color = R.color.statusgreen;
                break;
        }

        if (type == START_COLOR) sBgColorStartCatch = color;
        else sBgColorEndCatch = color;

        return color;
    }

    private void setViewByStatus(String status) {
        if (mCurrentChildItem != null)
            mBinding.setChildName(mCurrentChildItem.firstName);

        if (status == null) { //recheck if the screen needs to update or not.
            return;
        }

        Log.d(TAG, "status: " + status);
        String logType = null;
        boolean skipViewUpdateByStatus = false;
        int faceMode = -1;
        int drawerLockMode = DrawerLayout.LOCK_MODE_UNLOCKED; //0
        mImgFeedBack.setVisibility(View.GONE);
        View.OnClickListener statusOnClickListener = null;
        switch (status) {
            case States.StatusValue.HUB_ONLINE:
            case States.StatusValue.INITIAL:
                mBinding.setStatusTitle(getString(R.string.status_title_initial));
                setStatusDetailDescription(States.StatusValue.INITIAL, R.string.status_detail_initial);
                isStartHeartLocationRecording = false;
                break;

            case States.StatusValue.DETECTING:
                mBinding.setStatusTitle(getString(R.string.status_title_detecting));
                setStatusDetailDescription(States.StatusValue.DETECTING, R.string.status_detail_detecting);
                drawerLockMode = -1;
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_DETECTING;
                break;

            case States.StatusValue.HEART_RATE:
            case States.StatusValue.WEARABLE_BATTERY:
                setVisibleByStatus(mPreStatus);
                skipViewUpdateByStatus = true;
                drawerLockMode = -1;
                logType = LogEvents.STATUS_CARD_BATTERY_LEVEL;
                break;

            case States.StatusValue.LEARNING_PERIOD:
                mBinding.setStatusTitle(getString(R.string.status_title_learning_period));
                setStatusDetailDescription(States.StatusValue.LEARNING_PERIOD, R.string.status_detail_learning_period);
//                mBinding.leftDrawer.setEnabled(true);
                isStartHeartLocationRecording = true;
                faceMode = StatusView.MODE_LEARNING_PERIOD;
                break;

            case States.StatusValue.AWAKE:
                mBinding.setStatusTitle(getString(R.string.status_title_awake));
                setStatusDetailDescription(States.StatusValue.AWAKE, R.string.status_detail_awake);
                isStartHeartLocationRecording = true;
                logType = LogEvents.STATUS_CARD_AWAKE_NOTIFICATION;
                faceMode = StatusView.MODE_AWAKE;
                mImgFeedBack.setVisibility(View.VISIBLE);
                statusOnClickListener = mStatusOnClickListener;
                break;

            case States.StatusValue.STIRRING:
                mBinding.setStatusTitle(getString(R.string.status_title_stirring));
                setStatusDetailDescription(States.StatusValue.STIRRING, R.string.status_detail_stirring);
                isStartHeartLocationRecording = true;
                logType = LogEvents.STATUS_CARD_STIRRING_NOTIFICATION;
                faceMode = StatusView.MODE_STIRRING;
                mImgFeedBack.setVisibility(View.VISIBLE);
                statusOnClickListener = mStatusOnClickListener;
                break;

            case States.StatusValue.ASLEEP:
                mBinding.setStatusTitle(getString(R.string.status_title_asleep));
                setStatusDetailDescription(States.StatusValue.ASLEEP, R.string.status_detail_asleep);
                isStartHeartLocationRecording = true;
                logType = LogEvents.STATUS_CARD_ASLEEP_NOTIFICATION;
                faceMode = StatusView.MODE_ASLEEP;
                mImgFeedBack.setVisibility(View.VISIBLE);
                statusOnClickListener = mStatusOnClickListener;
                break;

            case States.StatusValue.UNUSUAL_HEARTBEAT:
                mBinding.setStatusTitle(getString(R.string.status_title_unsual_haertbeat));
                setStatusDetailDescription(States.StatusValue.UNUSUAL_HEARTBEAT, R.string.status_detail_unsual_haertbeat);
                isStartHeartLocationRecording = true;
                logType = LogEvents.STATUS_CARD_UNUSUAL_HEART_RATE;
                faceMode = StatusView.MODE_UNUSUAL_HEARTBEAT;
                break;

            case States.StatusValue.ROLLED_OVER:
                mBinding.setStatusTitle(getString(R.string.status_title_rolled_over));
                setStatusDetailDescription(States.StatusValue.ROLLED_OVER, R.string.status_detail_rollover_alert);
                isStartHeartLocationRecording = true;
                logType = LogEvents.STATUS_CARD_ROLLED_OVER;
                faceMode = StatusView.MODE_ROLLOVER;
                break;

            case States.StatusValue.WEARABLE_READY:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_ready));
                setStatusDetailDescription(States.StatusValue.WEARABLE_READY, R.string.status_detail_wearable_ready);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_READY;
                break;

            case States.StatusValue.WEARABLE_CHARGING:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_charging));
                setStatusDetailDescription(States.StatusValue.WEARABLE_CHARGING, R.string.status_detail_wearable_charging);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_CHARGING;
                break;

            case States.StatusValue.WEARABLE_CHARGED:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_charged));
                setStatusDetailDescription(States.StatusValue.WEARABLE_CHARGED, R.string.status_detail_wearable_charged);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_CHARGED;
                break;

            case States.StatusValue.WEARABLE_TOO_FAR_AWAY:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_too_far_away));
                setStatusDetailDescription(States.StatusValue.WEARABLE_TOO_FAR_AWAY, R.string.status_detail_wearable_too_far_away);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_TOO_FAR_AWAY;
                break;

            case States.StatusValue.WEARABLE_NOT_FOUND:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_not_found));
                setStatusDetailDescription(States.StatusValue.WEARABLE_NOT_FOUND, R.string.status_detail_wearable_not_found);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_NOT_FOUND;
                break;

            case States.StatusValue.WEARABLE_FELL_OFF:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_fell_off));
                setStatusDetailDescription(States.StatusValue.WEARABLE_FELL_OFF, R.string.status_detail_wearable_fell_off);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_FELL_OFF;
                break;

            case States.StatusValue.HUB_OFFLINE:
                mBinding.setStatusTitle(getString(R.string.status_title_sprouting_offline));
                setStatusDetailDescription(States.StatusValue.HUB_OFFLINE, R.string.status_detail_sprouting_offline);
                drawerLockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_HUB_OFFLINE;
                break;

            case States.StatusValue.NO_SERVICE:
                mBinding.setStatusTitle(getString(R.string.status_title_no_service));
                setStatusDetailDescription(States.StatusValue.NO_SERVICE, R.string.status_detail_no_service);
                drawerLockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_NOTIF_NO_SERVICE;
                break;

            case States.StatusValue.WEARABLE_OUT_OF_BATTERY:
                mBinding.setStatusTitle(getString(R.string.status_title_wearable_out_of_battery));
                setStatusDetailDescription(States.StatusValue.WEARABLE_OUT_OF_BATTERY, R.string.status_detail_wearable_out_of_battery);
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_WEARABLE_OUT_OF_BATTERY;
                break;

            case States.StatusValue.NO_CONFIGURED_DEVICE:
                mBinding.setStatusTitle(getString(R.string.status_title_no_configured_device));
                setStatusDetailDescription(States.StatusValue.NO_CONFIGURED_DEVICE, R.string.status_detail_no_configured_device);
                drawerLockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_NO_CONFIGURED_DEVICE;
                break;

            case States.StatusValue.FIRMWARE_UPDATING:
                mBinding.setStatusTitle(getString(R.string.status_title_updating_firmware));
                setStatusDetailDescription(States.StatusValue.FIRMWARE_UPDATING, R.string.status_detail_updating_firmware);
                drawerLockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
                isStartHeartLocationRecording = false;
                logType = LogEvents.STATUS_CARD_FIRMWARE_UPDATING;
                break;

            case States.StatusValue.UNKNOWN:
            default:
                skipViewUpdateByStatus = true;
                drawerLockMode = -1;
                break;
        }
        mStatusTxt.setOnClickListener(statusOnClickListener);
//        if (drawerLockMode >= 0) {
//            mBinding.drawerLayout.setDrawerLockMode(drawerLockMode);
//        }
        if (logType != null) {
            Utils.logEvents(logType);
        }
        if (!skipViewUpdateByStatus) {
            statusViewSwitcher(status, faceMode);
        }

        checkSleepPredictionDisplay();
    }

    private void statusViewSwitcher(String status, int mode) {
        if (mode >= 0) {
            showView(mBinding.statusView, View.VISIBLE);
            showView(mBinding.wearableState, View.GONE);
            mBinding.statusView.setMode(mode); //set circleView
            updateStatusCircleProgress();
        } else {
            showView(mBinding.statusView, View.GONE);
            showView(mBinding.wearableState, View.VISIBLE);
            mBinding.wearableState.setStatus(status); //Set WearableView
        }
        setVisibleByStatus(status);
    }

    private void setStatusDetailDescription(String status, int stringId) {
        setStatusDetailDescription(status, getString(stringId));
    }

    private void setStatusDetailDescription(String status, String string) {
        String text;
        int visibility;
        if (States.StatusValue.AWAKE.equals(status) || States.StatusValue.ASLEEP.equals(status)) {
            text = sIsLearningPeriodCompleted ? string : getString(R.string.status_detail_lp_in_progress);
            visibility = sIsLearningPeriodCompleted ? View.GONE : View.VISIBLE;
        } else {
            text = string;
            visibility = View.GONE;
        }
        setStatusDescription(text);
        showView(mBinding.txtContentIcon, visibility);
    }

    public void updateStatusCircleProgress() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        mBinding.statusView.setIconCurrentPosition(hour, minute); //Set StatusView
        if (mStatusViewLP != null)
            mStatusViewLP.setIconCurrentPosition(hour, minute); //Set StatusView
    }

    private void checkSleepPredictionDisplay() {
        if (mSleepStatus == null || mBandState == null || !Hub.BandState.WearableState.DETECTING.equals(mBandState.getState()))
            return;
        if (mPrediction == null) {
            setNoSleepPredictionLabel(getString(R.string.status_detail_asleep_no_prediction));
            setPredictionArc(false);
            return;
        }
        String error = mPrediction.getError();
        if (error != null && !error.isEmpty()) {
            setNoSleepPredictionLabel(error);
            setPredictionArc(false);
            return;
        }
        if (mPrediction.hasTimestamp() && mBandState.hasTimestamp() &&
                mPrediction.getTimestamp().getSeconds() < mBandState.getTimestamp().getSeconds()) {
            setNoSleepPredictionLabel(null);
            setPredictionArc(false);
            return;
        }

        if (States.StatusValue.ASLEEP.equals(mCurStatus) ||
                States.StatusValue.AWAKE.equals(mCurStatus) ||
                States.StatusValue.STIRRING.equals(mCurStatus)) {
            setPredictionLabel();
            setPredictionArc(true);
        }
    }

    private void setPredictionArc(boolean hasPrediction) {
        Calendar startTime = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        Calendar predictTime = Calendar.getInstance();

        Timestamp napBeginTime = mSleepStatus.getNapBegan();
        startTime.setTimeInMillis(napBeginTime.getSeconds() * Const.TIME_MS_SEC);

        if (hasPrediction) {
            int predictionUpperBound = mPrediction.getPredictionUpperBound();
            predictTime.setTimeInMillis(napBeginTime.getSeconds() * Const.TIME_MS_SEC + predictionUpperBound * Const.TIME_MS_MIN);
        } else {
            predictTime = currentTime;
        }
        if (States.StatusValue.AWAKE.equals(mCurStatus) &&
                (predictTime.getTimeInMillis() - currentTime.getTimeInMillis() < 5 * Const.TIME_MS_MIN ||
                        mSleepStatus.getTimestamp().getSeconds() * Const.TIME_MS_SEC > 5 * Const.TIME_MS_MIN)) {
            return;
        }
        mBinding.statusView.setSleepPrediction(startTime, currentTime, predictTime);
    }

    private void setPredictionLabel() {
        if (States.StatusValue.ASLEEP.equals(mCurStatus) && sIsLearningPeriodCompleted) {
            setAsleepLabel();
        } else if (States.StatusValue.AWAKE.equals(mCurStatus) && sIsLearningPeriodCompleted) {
            setAwakeLabel();
        }
    }

    private void setNoSleepPredictionLabel(String error) {
        if (error != null && !error.isEmpty()) {
            // TODO: check error message when completed on server side
            if (States.StatusValue.ASLEEP.equals(mCurStatus)) {
                setStatusDetailDescription(mCurStatus, R.string.status_detail_asleep_no_prediction);
            }
        } else {
            if (States.StatusValue.ASLEEP.equals(mCurStatus)) {
                Timestamp napBeginTime = mSleepStatus.getNapBegan();
                Calendar currentTime = Calendar.getInstance();
                long difference = currentTime.getTimeInMillis() - napBeginTime.getSeconds() * Const.TIME_MS_SEC;

                // display "Predicting..." for the first 5 minutes
                if (difference < 5 * 60 * Const.TIME_MS_SEC) {
                    setStatusDetailDescription(mCurStatus, R.string.status_detail_asleep);
                } else { // display "will wake up any time"
                    String formattedTime = TimelineUtils.formatHoursAndMinutesForPrediction(StatusActivity.this, 0);
                    setStatusDetailDescription(mCurStatus, String.format(getResources().getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime));
                }
            } else if (States.StatusValue.AWAKE.equals(mCurStatus)) {
                setStatusDetailDescription(mCurStatus, "");
            }
        }
    }

    private void setAsleepLabel() {
        Calendar currentTime = Calendar.getInstance();
        Timestamp napBeginTime = mPrediction.getNapBegan();
        int predictionLowerBound = mPrediction.getPredictionLowerBound();
        int predictionUpperBound = mPrediction.getPredictionUpperBound();

        //  If a sleep prediction range is wider than 2 hours, do not show the prediction. Instead show "no prediction available."
        if (predictionUpperBound - predictionLowerBound > 120)
            setNoSleepPredictionLabel("no prediction available");

        Calendar lowerBoundTime = Calendar.getInstance();
        Calendar upperBoundTime = Calendar.getInstance();
        lowerBoundTime.setTimeInMillis(napBeginTime.getSeconds() * Const.TIME_MS_SEC + predictionLowerBound * Const.TIME_MS_MIN);
        upperBoundTime.setTimeInMillis(napBeginTime.getSeconds() * Const.TIME_MS_SEC + predictionUpperBound * Const.TIME_MS_MIN);

        Calendar napBegin = Calendar.getInstance();
        napBegin.setTimeInMillis(napBeginTime.getSeconds() * Const.TIME_MS_SEC);

        long remainingLowerBound = lowerBoundTime.getTimeInMillis() - currentTime.getTimeInMillis();
        long remainingUpperBound = upperBoundTime.getTimeInMillis() - currentTime.getTimeInMillis();

        String description;

        if (remainingLowerBound > remainingUpperBound) {
            description = getString(R.string.status_detail_asleep_no_prediction);

        } else if (remainingLowerBound > Const.TIME_MS_HOUR) { // display time for prediction range over 1 hour
            String formattedTime = String.format(getString(R.string.status_detail_asleep_wake_up_time_between),
                    DateTimeUtils.getFormat(StatusActivity.this, lowerBoundTime),
                    DateTimeUtils.getFormat(StatusActivity.this, upperBoundTime));
            description = String.format(getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime);

        } else if (remainingLowerBound > Const.TIME_MS_MIN) { // display minutes remaining for prediction range under 1 hour
            String formattedTime = String.format(getString(R.string.status_detail_asleep_wake_up_time_in),
                    TimelineUtils.formatHoursAndMinutesForPrediction(StatusActivity.this, (int) (remainingLowerBound / Const.TIME_MS_MIN)),
                    TimelineUtils.formatHoursAndMinutesForPrediction(StatusActivity.this, (int) (remainingUpperBound / Const.TIME_MS_MIN)));
            description = String.format(getResources().getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime);

        } else if (remainingUpperBound > Const.TIME_MS_HOUR) { // baby is sleeping beyond the lower bound
            // display remaining prediction greater than 1 hour
            String formattedTime = String.format(getResources().getString(R.string.status_detail_asleep_wake_up_time_at),
                    DateTimeUtils.getFormat(StatusActivity.this, upperBoundTime));
            description = String.format(getResources().getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime);

        } else if (remainingUpperBound > Const.TIME_MS_MIN) { // display remaining prediction greater than 1 minute
            String formattedTime = String.format(getString(R.string.status_detail_asleep_wake_up_time),
                    TimelineUtils.formatHoursAndMinutesForPrediction(StatusActivity.this, (int) (remainingUpperBound / Const.TIME_MS_MIN)));
            description = String.format(getResources().getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime);

        } else if (remainingUpperBound > 0) { // display "will wake up any time"
            String formattedTime = getString(R.string.status_detail_asleep_wake_up_time_any);
            description = String.format(getResources().getString(R.string.status_detail_asleep_wake_up), mCurrentChildItem.firstName, formattedTime);

        } else {
            description = getString(R.string.status_detail_asleep_no_prediction);
        }

        setStatusDescription(description);
    }

    private void setStatusDescription(String description) {
        if (description.equalsIgnoreCase(getString(R.string.status_detail_asleep_no_prediction))) {
            Utils.logEvents(LogEvents.NO_PREDICTION_AVAILABLE);
        }
        mBinding.setStatusDescription(description);
    }

    private void setAwakeLabel() {
        Timestamp napBeginTime = mSleepStatus.getNapBegan();
        Calendar currentTime = Calendar.getInstance();
        long awakeDuration = currentTime.getTimeInMillis() - napBeginTime.getSeconds() * Const.TIME_MS_SEC;

        int predictionLowerBound = mPrediction.getPredictionLowerBound();
        long remainingPrediction = napBeginTime.getSeconds() * Const.TIME_MS_SEC + predictionLowerBound * Const.TIME_MS_MIN - currentTime.getTimeInMillis();
        if (remainingPrediction >= 5 * Const.TIME_MS_MIN && awakeDuration < 5 * Const.TIME_MS_MIN) {
            String formattedTime = TimelineUtils.formatHoursAndMinutesForPrediction(StatusActivity.this, (int) (remainingPrediction / Const.TIME_MS_MIN));
            setStatusDescription(String.format(getResources().getString(R.string.status_detail_awake_back_to_sleep), mCurrentChildItem.firstName, formattedTime));
        } else {
            setStatusDescription("");
        }
    }

    private void setVisibleByStatus(String status) {

        if (status == null) return;

        int showTitle = View.VISIBLE,
                showContent = View.VISIBLE,
                showRollOverBtnAndDismiss = View.GONE,
                showInformation = View.GONE,
                showLogBtn = View.GONE,
                showStirringBtn = View.INVISIBLE,
                showControl = View.VISIBLE,
                showBattery = View.VISIBLE;
        int controlIcon = R.drawable.ic_control_icon;
        boolean batteryUnknown = false;

        switch (status) {
            case States.StatusValue.INITIAL:
                showContent = View.INVISIBLE;
                controlIcon = R.drawable.ic_control_icon_greyed;
                batteryUnknown = true;
                break;

            case States.StatusValue.DETECTING:
                showContent = View.INVISIBLE;
                break;

            case States.StatusValue.LEARNING_PERIOD:
                showInformation = View.VISIBLE;
                showLogBtn = !sIsLearningPeriodCompleted ? View.VISIBLE : View.GONE;
                break;

            case States.StatusValue.AWAKE:
                showLogBtn = !sIsLearningPeriodCompleted ? View.VISIBLE : View.GONE;
                break;

            case States.StatusValue.STIRRING:
                showLogBtn = !sIsLearningPeriodCompleted ? View.VISIBLE : View.GONE;
                showStirringBtn = View.VISIBLE;
                mBinding.setBtnImgSmallIconResource(R.drawable.ic_sc_bulb);
                break;

            case States.StatusValue.ASLEEP:
                showLogBtn = !sIsLearningPeriodCompleted ? View.VISIBLE : View.GONE;
                break;

            case States.StatusValue.ROLLED_OVER:
                showRollOverBtnAndDismiss = View.VISIBLE;
                mBinding.setBtnImgSmallIconResource(R.drawable.ic_sc_alert_off);

                boolean isNeedToShowDialog = SharedPrefManager.getBoolean(this, SPKey.BOOL_ROLLOVER_SIX_MONTH_SHOW_DIALOG, true);
                Calendar cal = Calendar.getInstance();
                if (cal.getTimeInMillis() - rolloverSixMonthEndTime > 0 && isNeedToShowDialog) {
                    SharedPrefManager.put(this, SharedPrefManager.SPKey.BOOL_ROLLOVER_SIX_MONTH_SHOW_DIALOG, false);
                    disPatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.ROLL_OVER_SIX_MONTH));
                }

                break;

            case States.StatusValue.WEARABLE_CHARGING:
                showContent = View.INVISIBLE;
                showLogBtn = !sIsLearningPeriodCompleted ? View.VISIBLE : View.GONE;
                mBinding.wearableState.setBatteryCharging(mBatteryValue);

                if (NotificationLayout.Type.LOW_BATTERY.equals(mBinding.notificationLayout.getType()) && mBinding.notificationLayout.isShowing())
                    mBinding.notificationLayout.dismiss();

                break;

            case States.StatusValue.WEARABLE_CHARGED:
                showLogBtn = View.GONE;
                break;

//            case States.StatusValue.WEARABLE_READY:
//            case States.StatusValue.WEARABLE_TOO_FAR_AWAY:
//            case States.StatusValue.WEARABLE_NOT_FOUND:
//            case States.StatusValue.WEARABLE_FELL_OFF:
            case States.StatusValue.NO_SERVICE:
            case States.StatusValue.FIRMWARE_UPDATING:
            case States.StatusValue.HUB_OFFLINE:
                controlIcon = R.drawable.ic_control_icon_greyed;
                batteryUnknown = true;
                break;
//            case States.StatusValue.NO_SERVICE:
//            case States.StatusValue.UNUSUAL_HEARTBEAT:
//            case States.StatusValue.WEARABLE_OUT_OF_BATTERY:
//                break;
            case States.StatusValue.NO_CONFIGURED_DEVICE:
                showControl = View.GONE;
                showBattery = View.GONE;
                mBinding.notificationLayout.setToDefault();
                break;
        }

        showView(mBinding.txtTitle, showTitle);
        showView(mBinding.txtContent, showContent);
        showView(mBinding.imgRollOverIconBtn, showRollOverBtnAndDismiss);
        showView(mBinding.linDismiss, showRollOverBtnAndDismiss);
        showView(mBinding.imgInformation, showInformation);
        showView(mBinding.linearFloatButton, showLogBtn);
        showView(mBinding.imgStirringIconBtn, showStirringBtn);
        showView(mBinding.imgControl, showControl);
        showView(mBinding.batteryDisplay, showBattery);

        if (batteryUnknown) mBinding.batteryDisplay.setUnknownState();
        else if (showBattery == View.VISIBLE) mBinding.setBatteryValue(mBatteryValue);
        mBinding.imgControl.setImageResource(controlIcon);
    }

    private void checkTimelineAndLogSleepAlarm() {
        if (!sIsLearningPeriodCompleted) {
            Boolean isAlreadyLaunchTimeline = !SharedPrefManager.getBoolean(this, SharedPrefManager.SPKey.BOOL_TIMELINE_FIRST_LAUNCH, true);
            long timelineAlarmTime = SharedPrefManager.getLong(this, SharedPrefManager.SPKey.LONG_TIMELINE_ALARM_TIME, (long) (-1));
            long logSleepAlarmTime = SharedPrefManager.getLong(this, SharedPrefManager.SPKey.LONG_SLEEP_TIMER_ALARM_END_TIME, (long) (-1));

            if (!isAlreadyLaunchTimeline) { //if user didn't launch timeline before.
                if (timelineAlarmTime == -1) { //if the timeline alarm didn't set yet.
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.HOUR, 12); // Set the Timeline Alarm wake up time.
                    SharedPrefManager.put(this, SharedPrefManager.SPKey.LONG_TIMELINE_ALARM_TIME, cal.getTimeInMillis());

                    Intent intent = new Intent(this, AlarmReceiver.class);
                    intent.putExtra("msg", "timeline_alarm");
                    PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                } else {
                    Calendar cal = Calendar.getInstance();
                    if (cal.getTimeInMillis() - timelineAlarmTime > 0) { //although we have an alarm, we needs to confirm that the notification is always show in this situation when launch the app.
                        showNotification(NotificationLayout.Type.TIMELINE);
                    }
                }
            } else { //cancel the alarm
                if (NotificationLayout.Type.TIMELINE.equals(mBinding.notificationLayout.getType())) {
                    mBinding.notificationLayout.setToDefault();
                }
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (pi != null) am.cancel(pi);
            }

            if (logSleepAlarmTime != -1) {
                Calendar cal = Calendar.getInstance();
                if (cal.getTimeInMillis() - logSleepAlarmTime > 0) { //although we have an alarm, we needs to confirm that the notification is always show in this situation when launch the app.
                    showNotification(NotificationLayout.Type.ADVICE_LOG_SLEEP);
                } else if (NotificationLayout.Type.ADVICE_LOG_SLEEP.equals(mBinding.notificationLayout.getType())) {
                    mBinding.notificationLayout.setToDefault();
                }
            }
        }
    }

    private void checkLowBatteryAlarm() {
        if (mBinding.notificationLayout.isNeedToShowLowBattery() && !States.StatusValue.WEARABLE_CHARGING.equals(mCurStatus)) {
            showNotification(NotificationLayout.Type.LOW_BATTERY);
        }
    }

    public void showAlarmDialog(int targetDialog) {
        switch (targetDialog) {
            case IntegrationDialog.LEARNING_PERIOD_FINISH:
                if (mCurrentChildItem != null) {
//                    mIntegrationDlg.setDialog(targetDialog, mCurrentChildItem.firstName, mCurrentChildItem.gender);
                    LearningPeriodFinishDialog learningPeriodFinishDialog = new LearningPeriodFinishDialog(this, mCurrentChildItem.firstName);
                    learningPeriodFinishDialog.show();
                } else
                    mIntegrationDlg.setDialog(targetDialog);
                break;

            default:
                mIntegrationDlg.setDialog(targetDialog);
                break;
        }
        if (targetDialog != IntegrationDialog.LEARNING_PERIOD_FINISH) {
            mIntegrationDlg.show();
        }
    }

    public void showNotification(String targetNotification) {
        if (sIsLearningPeriodCompleted &&
                (NotificationLayout.Type.ADVICE_LOG_SLEEP.equals(targetNotification) ||
                        NotificationLayout.Type.TIMELINE.equals(targetNotification)))
            return;
        mBinding.notificationLayout.show(targetNotification);
    }

    public void setNotificationVisible(boolean visible) {
        if (visible) {
            mBinding.notificationLayout.restore();
        } else if (mBinding.notificationLayout.isShowing()) {
            mBinding.notificationLayout.dismiss();
        }
    }

    private void setCurrentTimeSpacing() {
        Calendar currentCalender = Calendar.getInstance();
        if (currentCalender != null) {
            long diffTime = currentCalender.getTimeInMillis() - mStartTime;
            int hours = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_HOUR);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_HOUR;
            int mins = (int) (diffTime / DateTimeUtils.MILLI_SEC_PER_MIN);
            diffTime %= DateTimeUtils.MILLI_SEC_PER_MIN;
            int secs = (int) (diffTime / DateTimeUtils.MILLI_SEC);

            mBinding.setBtnTitle(String.format(getString(R.string.sleep_dlg_edit_time_format), hours, mins, secs));
        }
    }

    public SSManagement.Child getCurrentChild() {
        return mCurrentChildItem;
    }

    public void goToTimeline(boolean isForReplay) {
        if (mCurrentChildItem != null) {
            Intent intent = new Intent(StatusActivity.this, TimelineActivity.class);
            intent.putExtra(TimelineActivity.FIRSTNAME, mCurrentChildItem.firstName);
            intent.putExtra(TimelineActivity.ID, mCurrentChildItem.id);
            intent.putExtra(TimelineActivity.BIRTHDATE, mCurrentChildItem.birthDate);
            intent.putExtra(TimelineActivity.GENDER, mCurrentChildItem.gender);
            intent.putExtra(TimelineActivity.FOR_REPLAY, isForReplay);

            startActivityForResult(intent, ACTIVITY_RESULT_CODE_TIMELINE);
        }
    }

    /**
     * Disconnect from MQTT broker.
     */
    public void disconnectMqtt() {
        mSerialNumber = null;
        MqttItem mqttItem = new MqttItem.Builder(this).actionType(MqttAction.DISCONNECT).build();
        PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, new DisconnectMqttCallback());
    }

    /**
     * Connect to MQTT broker.
     */
    public void connectMqtt() {
        mSerialNumber = null;
        MqttItem mqttItem = new MqttItem.Builder(this).actionType(MqttAction.CONNECT).build();
        PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, connectMqttCallback);
    }

    /**
     * Subscribe topics for device's mEvents.
     */
    public void subscribeTopics(String serial) {
        if (serial != null) {
            MqttItem mqttItem = new MqttItem.Builder(this)
                    .actionType(MqttAction.SUBSCRIBE)
                    .topic(MqttAPI.getEventTopic(serial))
                    .topic(MqttAPI.getTelemetryTopic(serial))
                    .topic(MqttAPI.getMqttBandStatusTopic(serial))
                    .topic(MqttAPI.getSleepPredictionTopic(serial))
                    .topic(MqttAPI.getSleepStatusTopic(serial))
                    .topic(MqttAPI.getHubPresence(serial))
//                    .topic(MqttAPI.getBandPresence(serial))
                    .topic(MqttAPI.getStatusTopic(serial))
                    .topic(MqttAPI.getHubControl(serial))
                    .topic(MqttAPI.getBandState(serial))
                    .topic(MqttAPI.getMqttUserConfig(serial))
                    .topic(MqttAPI.getBandRollover(serial))
                    .build();

            PipelineManager.getInstance().doPipeline(
                    new BaseMqttTaskList(),
                    mqttItem,
                    new SubscribeMqttCallback());
        }
    }

    public void gotoScene(int targetScene) {
        if (targetScene == SCENE_STATUS) {
            TransitionManager.go(mSceneStatus, mSceneTransitionSet);
            initSceneStatus();
        } else if (targetScene == SCENE_LEARNING_PERIOD) {
            TransitionManager.go(mSceneLearningPeriod, mSceneTransitionSet);
            initSceneLearningPeriod();
        }
    }

    private void initSceneStatus() {
        mCurrentScene = SCENE_STATUS;
        View.OnClickListener goLearningPeriodListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurStatus != null && mBinding.statusView.getMode() == StatusView.MODE_LEARNING_PERIOD) {
                    disPatchAction(
                            Actions.SCENE_EVENT,
                            new Payload().put(Actions.Key.SCENE, SCENE_LEARNING_PERIOD).put(Actions.Key.VIEW, mBinding.statusView)
                    );
                }
            }
        };
        ImageView imgInfo = (ImageView) findViewById(R.id.imgInformation);
        if (imgInfo != null) {
            imgInfo.setOnClickListener(goLearningPeriodListener);
        }
        mBinding.statusView.setBabyFaceClickListener(goLearningPeriodListener);

        //fix issue when open fragFlower 2nd time.
        Fragment fragFlower = getFragmentManager().findFragmentById(R.id.fragFlower);
        if (fragFlower != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(fragFlower).commitAllowingStateLoss();
        }
    }

    private void initSceneLearningPeriod() {
        mCurrentScene = SCENE_LEARNING_PERIOD;
        mStatusViewLP = (StatusView) mSceneLearningPeriod.getSceneRoot().findViewById(R.id.statusView);
        updateStatusCircleProgress();
    }

    /**
     * Publish a message to Mqtt broker.
     */
    public void publishTurnOnLight(int color, float brightness) {
        Utils.logEvents(LogEvents.HUB_CONTROLS_LIGHTS_ON);
        Hub.LEDColor.LEDColors ledColors = Hub.LEDColor.LEDColors.forNumber(color); //UNKNOWN(0), BLUE(1), GREEN(2), RED(3), WHITE(4), YELLOW(5), PINK(6), AMBER(7)

        Hub.HubControl hubControl = Hub.HubControl.newBuilder()
                .setLedColor(ledColors)
                .setLedBrightness((int) brightness)
                .setLedMode(Hub.LEDMode.LEDModes.SET_COLOR)
                .build();
        publish(hubControl);
    }

    public void publishTurnOffLight() {
        Utils.logEvents(LogEvents.HUB_CONTROLS_LIGHTS_OFF);
        Hub.HubControl hubControl = Hub.HubControl.newBuilder().setLedMode(Hub.LEDMode.LEDModes.OFF)
                .build();
        publish(hubControl);
    }

    public void publishPlayMusic(ArrayList<String> songNames, float volume) {
        Utils.logEvents(LogEvents.HUB_CONTROLS_MUSIC_PLAYING);
        Hub.MusicState.MusicStates musicStates = Hub.MusicState.MusicStates.forNumber(2);    //UNKNOWN(0), STOP(1), PLAY(2), PAUSE(3), UNRECOGNIZED(-1)


        Hub.HubControl hubControl = Hub.HubControl.newBuilder()
                .setMusicState(musicStates)
                .setMusicPlayCount(0)
                .addAllMusicList(songNames)
                .setVolume((int) volume) //40 ~ 75
                .build();
        publish(hubControl);
    }

    public void publishStopMusic() {
        Utils.logEvents(LogEvents.HUB_CONTROLS_MUSIC_STOPPED);
        Hub.MusicState.MusicStates musicStates = Hub.MusicState.MusicStates.forNumber(1);    //UNKNOWN(0), STOP(1), PLAY(2), PAUSE(3), UNRECOGNIZED(-1)

        Hub.HubControl hubControl = Hub.HubControl.newBuilder().setMusicState(musicStates).build();
        publish(hubControl);
    }

    public void publishUpdateVolume(float volume) {
        Utils.logEvents(LogEvents.HUB_CONTROLS_MUSIC_VOLUME);
        Hub.HubControl hubControl = Hub.HubControl.newBuilder().setVolume((int) volume).build();
        publish(hubControl);
    }

    public void publish(Hub.HubControl hubControl) {
        if (mSerialNumber == null) return;
        MqttItem mqttItem = new MqttItem.Builder(this).actionType(MqttAction.PUBLISH_MESSAGE)
                .topic(MqttAPI.getHubControl(mSerialNumber))
                .payload(hubControl.toByteArray())
                .build();

        PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, new PublishMqttMessageCallback());
        App.getInstance().setUserData("hub_control_sent", hubControl.toString());
    }

    private void showHelpLayoutToolTip(View parentView) {
        final RelativeLayout helpLayout = parentView.findViewById(R.id.helpLayout);
        helpLayout.setVisibility(View.VISIBLE);
        ShTextView close = helpLayout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpLayout.setVisibility(View.GONE);
            }
        });
    }

    private void showStatusCardFeedback() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.FeedbackDialog);
        final View view1 = getLayoutInflater().inflate(R.layout.report_baby_status_1, null);

        View.OnClickListener imgHelpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpLayoutToolTip(view1);
            }
        };

        //View1
        TextView tvQuestion = view1.findViewById(R.id.question);
        TextView tvNo = view1.findViewById(R.id.btnNo);
        TextView tvYes = view1.findViewById(R.id.btnYes);
        ImageView imgHelp = view1.findViewById(R.id.imgFeedbackInfo);
        imgHelp.setOnClickListener(imgHelpClickListener);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackSaveView(bottomSheetDialog, new UserStatusFeedbackRequestBody(Utils.getFormattedStatus(mCurStatus), true));
            }
        });
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentStatusOptionView(bottomSheetDialog);
            }
        });

        String questionStr = String.format(getString(R.string.is_babyname_status), mCurrentChildItem.firstName, Utils.getStatus(mCurStatus));
        tvQuestion.setText(questionStr);

        bottomSheetDialog.setContentView(view1);
        bottomSheetDialog.show();
    }

    private void showCurrentStatusOptionView(final BottomSheetDialog bottomSheetDialog) {
        final View view2 = getLayoutInflater().inflate(R.layout.report_baby_status_2, null);

        View.OnClickListener imgHelpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpLayoutToolTip(view2);
            }
        };
        //View2
        TextView tvMsg = view2.findViewById(R.id.currentStatusMsg);
        final TextView tvOption1 = view2.findViewById(R.id.btnOne);
        final TextView tvOption2 = view2.findViewById(R.id.btnTwo);
        TextView tvOther = view2.findViewById(R.id.other);
        ImageView imgHelp = view2.findViewById(R.id.imgFeedbackInfo);
        imgHelp.setOnClickListener(imgHelpClickListener);
        String msgStr = String.format(getString(R.string.babyname_is_currently), mCurrentChildItem.firstName);
        tvMsg.setText(msgStr);
        ArrayList<String> options = getStatuses(mCurStatus);
        tvOption1.setText(options.get(0));
        tvOption1.setCompoundDrawablesRelativeWithIntrinsicBounds(getStatusIcon(options.get(0)), null, null, null);
        tvOption2.setText(options.get(1));
        tvOption2.setCompoundDrawablesRelativeWithIntrinsicBounds(getStatusIcon(options.get(1)), null, null, null);
        tvOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = tvOption1.getText().toString();
                showFeedbackSaveView(bottomSheetDialog, new UserStatusFeedbackRequestBody(Utils.getFormattedStatus(mCurStatus), false, Utils.getFormattedStatus(status), null));
            }
        });
        tvOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = tvOption2.getText().toString();
                showFeedbackSaveView(bottomSheetDialog, new UserStatusFeedbackRequestBody(Utils.getFormattedStatus(mCurStatus), false, Utils.getFormattedStatus(status), null));
            }
        });
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackView(bottomSheetDialog);
            }
        });
        bottomSheetDialog.setContentView(view2);
    }

    private void showFeedbackView(final BottomSheetDialog bottomSheetDialog) {
        final View view3 = getLayoutInflater().inflate(R.layout.report_baby_status_3, null);
        View.OnClickListener imgHelpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpLayoutToolTip(view3);
            }
        };
        //View3
        TextView tvMsg3 = view3.findViewById(R.id.currentStatusMsg);
        String msgStr = String.format(getString(R.string.babyname_is_currently), mCurrentChildItem.firstName);
        tvMsg3.setText(msgStr);
        final EditText feedback = view3.findViewById(R.id.feedback);
        final Button submitBtn = view3.findViewById(R.id.btnSubmit);

        ImageView imgHelp = view3.findViewById(R.id.imgFeedbackInfo);
        imgHelp.setOnClickListener(imgHelpClickListener);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackSaveView(bottomSheetDialog, new UserStatusFeedbackRequestBody(Utils.getFormattedStatus(mCurStatus),
                        false, null, feedback.getText().toString()));
            }
        });
        bottomSheetDialog.setContentView(view3);
    }

    private void showFeedbackSaveView(final BottomSheetDialog bottomSheetDialog, UserStatusFeedbackRequestBody userStatusFeedbackRequestBody) {
        final View view4 = getLayoutInflater().inflate(R.layout.report_baby_status_4, null);
        View.OnClickListener imgHelpClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpLayoutToolTip(view4);
            }
        };
        ImageView imgHelp = view4.findViewById(R.id.imgFeedbackInfo);
        imgHelp.setOnClickListener(imgHelpClickListener);
        final AnimationProgress animationProgress = view4.findViewById(R.id.animation);
        final RelativeLayout thanksLayout = view4.findViewById(R.id.thanksLayout);
        TextView gotIt = view4.findViewById(R.id.gotIt);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SystemClock.sleep(3800);
                return null;
            }

            @Override
            protected void onPreExecute() {
                animationProgress.setVisibility(View.VISIBLE);
                thanksLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                animationProgress.setVisibility(View.INVISIBLE);
                thanksLayout.setVisibility(View.VISIBLE);
            }
        };
        bottomSheetDialog.setContentView(view4);
        SproutlingApi.sendUserStatusFeedback(userStatusFeedbackRequestBody, new Callback<UserStatusFeedbackResponseBody>() {
            @Override
            public void onResponse(Call<UserStatusFeedbackResponseBody> call, Response<UserStatusFeedbackResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "User status feedback sent successful.");
                } else {
                    Log.e(TAG, "User status feedback sent failed.");
                }
            }

            @Override
            public void onFailure(Call<UserStatusFeedbackResponseBody> call, Throwable t) {

            }
        }, AccountManagement.getInstance(this).getAccessToken());

        asyncTask.execute();
    }

    private Drawable getStatusIcon(String statusValue) {
        switch (statusValue) {
            case States.StatusValue.AWAKE:
                return getDrawable(R.drawable.ic_awake);
            case States.StatusValue.STIRRING:
                return getDrawable(R.drawable.ic_stirring);
            case States.StatusValue.ASLEEP:
                return getDrawable(R.drawable.ic_asleep);
            default:
                return getDrawable(R.drawable.ic_awake);
        }
    }

    private ArrayList<String> getStatuses(String statusValue) {
        ArrayList<String> statuses = new ArrayList<>();
        switch (statusValue) {
            case States.StatusValue.AWAKE:
                statuses.add(Utils.getStatus(States.StatusValue.ASLEEP));
                statuses.add(Utils.getStatus(States.StatusValue.STIRRING));
                break;
            case States.StatusValue.STIRRING:
                statuses.add(Utils.getStatus(States.StatusValue.ASLEEP));
                statuses.add(Utils.getStatus(States.StatusValue.AWAKE));
                break;
            case States.StatusValue.ASLEEP:
                statuses.add(Utils.getStatus(States.StatusValue.AWAKE));
                statuses.add(Utils.getStatus(States.StatusValue.STIRRING));
                break;
            default:
                statuses.add(Utils.getStatus(States.StatusValue.ASLEEP));
                statuses.add(Utils.getStatus(States.StatusValue.STIRRING));
                break;
        }
        return statuses;
    }

    public void onMenuClickOrSwipe() {
        if (!States.StatusValue.HUB_OFFLINE.equalsIgnoreCase(mCurStatus) &&
                !States.StatusValue.FIRMWARE_UPDATING.equalsIgnoreCase(mCurStatus) &&
                !States.StatusValue.NO_SERVICE.equalsIgnoreCase(mCurStatus)) {

            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onUpdateClicked(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public class BtnHandler {
        public void onSettingClicked() {
            Log.v(TAG, "onSettingClicked.");
            Intent intent = new Intent(StatusActivity.this, SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_STATUS_SCREEN_AVAILABLE, true);
            startActivityForResult(intent, ACTIVITY_RESULT_CODE_SETTING);
        }

        public void onChildClicked() {
            Log.v(TAG, "onChildClicked.");
            goToTimeline(false);
        }

        public void onFloatingBtnClicked() {
            Log.v(TAG, "onFloatingBtnClicked.");
            if (mCurrentChildItem != null) {
//                Intent intentAddSleep = new Intent(StatusActivity.this, SetSleepActivity.class);
                Intent intentAddSleep = new Intent(StatusActivity.this, LogSleepView.class);
                intentAddSleep.putExtra(CHILD_ID, mCurrentChildItem.id);
//                intentAddSleep.putExtra("ChildId", mCurrentChildItem.id);
                startActivityForResult(intentAddSleep, ACTIVITY_RESULT_CODE_SET_SLEEP);
            }
        }

        public void onSmallIconClicked() {
            Log.v(TAG, "onSmallIconClicked.");

            if (States.StatusValue.ROLLED_OVER.equals(mCurStatus)) {
                disPatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.ROLL_OVER));
            } else if (States.StatusValue.ROLLED_OVER.equals(mCurStatus)) {
                disPatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.ROLL_OVER));
            } else if (States.StatusValue.STIRRING.equals(mCurStatus)) {
                disPatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.STIRRING));
            }
        }

        public void onDismissClicked() {
            Log.v(TAG, "onDismissClicked.");
            disPatchAction(Actions.DISMISS_ROLLOVER, new Payload());
        }

        public void onNotificationClicked() {
            Log.v(TAG, "onNotificationClicked.");
//            if (NotificationLayout.Type.ADVICE_LOG_SLEEP.equals(mBinding.notificationLayout.getType())) {
//                mBinding.notificationLayout.dismiss();
//                mBinding.notificationLayout.clear();
//            }
        }

        //Left DrawerLayout Menu control.
        public void onControlClicked() {
            Log.v(TAG, "onControlClicked.");
            onMenuClickOrSwipe();

        }

        public void onInfoClicked() {
            Log.v(TAG, "onInfoClicked.");
            LearningPeriodDialog learningPeriodDialog = new LearningPeriodDialog(StatusActivity.this, getCurrentChild().firstName);
            learningPeriodDialog.show();
            //
//            disPatchAction(Actions.SCENE_EVENT, new Payload().put(Actions.Key.SCENE, SCENE_LEARNING_PERIOD).put(Actions.Key.VIEW, mBinding.statusView));
            // Need to hide notification if it was displaying when presenting this scene
//            setNotificationVisible(false);
        }
    }
}
