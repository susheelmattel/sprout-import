/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.states.app.support.AStateActivityCompat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sproutling.BuildConfig;
import com.sproutling.object.HubAmbientTopicEvent;
import com.sproutling.object.HubBandPresenceTopicEvent;
import com.sproutling.object.HubBandRolloverTopicEvent;
import com.sproutling.object.HubBandStateTopicEvent;
import com.sproutling.object.HubBandStatusTopicEvent;
import com.sproutling.object.HubBandTelemetryTopicEvent;
import com.sproutling.object.HubCLITopicEvent;
import com.sproutling.object.HubControlTopicEvent;
import com.sproutling.object.HubEventsTopicEvent;
import com.sproutling.object.HubFirmwareUpdateTopicEvent;
import com.sproutling.object.HubPresenceTopicEvent;
import com.sproutling.object.HubSleepPredictionTopicEvent;
import com.sproutling.object.HubSleepStatusTopicEvent;
import com.sproutling.object.HubStatusTopicEvent;
import com.sproutling.object.HubUserConfigurationTopic;
import com.sproutling.object.PushNotificationChannelEvent;
import com.sproutling.object.PushNotificationRegistrationEvent;
import com.sproutling.pipeline.callback.ConnectMqttCallback;
import com.sproutling.pipeline.callback.DisconnectMqttCallback;
import com.sproutling.pipeline.callback.SubscribeMqttCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.ui.dialogfragment.FirmwareUpdateDialogFragment;
import com.sproutling.ui.widget.ShCustomProgressBar;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import sproutling.Hub;

import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;
import static com.sproutling.broadcast.ConnectReceiver.isAlreadyNoService;

/**
 * Created by subram13 on 2/15/17.
 */

public abstract class BaseMqttActivity extends AStateActivityCompat {

    private static final String TAG = BaseMqttActivity.class.getSimpleName();
    public static final String FISHER_PRICE = "fisherPrice";
    private FirmwareUpdateDialogFragment mFirmwareUpdateDialogFragment;
    private ShCustomProgressBar mShCustomProgressBar;

    private boolean mIsActivityForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mShCustomProgressBar = new ShCustomProgressBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.logEvents(LogEvents.APP_IN_FOREGROUND);
        mIsActivityForeground = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected void showProgressBar(boolean show) {
        if (show) {
            hideKeyboard();
            if (mShCustomProgressBar != null) mShCustomProgressBar.show();
        } else {
            if (mShCustomProgressBar != null && mShCustomProgressBar.isShowing()) {
                mShCustomProgressBar.dismiss();
            }
        }
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setUpPushNotification() {
        if (BuildConfig.FLAVOR_app.equalsIgnoreCase(FISHER_PRICE_CHINA_FLAVOR)) {
            //Baidu Push notification
            PushSettings.enableDebugMode(this, true);
            String pushApiKey = Utils.getMetaValue(this, "api_key");
            //start push notification
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, pushApiKey);
            Log.v(TAG, "bdpush PushEnabled: " + PushManager.isPushEnabled(getApplicationContext()));
        } else {
//            FCM push notification
            // Get updated InstanceID token.
            String pushToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Push token: " + pushToken);
            if (!TextUtils.isEmpty(pushToken)) {
                Utils.sendRegistrationToServer(this, pushToken);
            } else {
                onPushNotificationRegistrationFailure(null);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushNotificationChannelEvent pushNotificationChannelEvent) {
        Utils.sendRegistrationToServer(this, pushNotificationChannelEvent.getChannelId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushNotificationRegistrationEvent pushNotificationRegistrationEvent) {
        if (pushNotificationRegistrationEvent.isSuccess()) {
            onPushNotificationRegistrationSuccess(pushNotificationRegistrationEvent.getCreateHandheldResponse());
        } else {
            onPushNotificationRegistrationFailure(pushNotificationRegistrationEvent.getThrowable());
        }
    }

    protected void onPushNotificationRegistrationSuccess(CreateHandheldResponse createHandheldResponse) {
        // TODO: null pointer exception
//        if (createHandheldResponse != null) {
//            SharedPrefManager.saveHandHeldId(this, createHandheldResponse.getId());
//        }

    }

    protected void onPushNotificationRegistrationFailure(Throwable t) {
        Log.e(TAG, "Error registering push notification");
    }


    protected void unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsActivityForeground = false;
        Utils.sendLogEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
        Utils.sendLogEvents();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubPresenceTopicEvent hubPresenceTopicEvent) {
        onHubPresenceTopicEvent(hubPresenceTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubStatusTopicEvent hubStatusTopicEvent) {
        onHubStatusTopicEvent(hubStatusTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubAmbientTopicEvent hubAmbientTopicEvent) {
        onHubAmbientTopicEvent(hubAmbientTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubFirmwareUpdateTopicEvent hubFirmwareUpdateTopicEvent) {
        onHubFirmwareUpdateTopicEvent(hubFirmwareUpdateTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubControlTopicEvent hubControlTopicEvent) {
        onHubControlTopicEvent(hubControlTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubCLITopicEvent hubCLITopicEvent) {
        onHubCLITopicEvent(hubCLITopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubBandStatusTopicEvent hubBandStatusTopicEvent) {
        onHubBandStatusTopicEvent(hubBandStatusTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubBandTelemetryTopicEvent hubBandTelemetryTopicEvent) {
        onHubBandTelemetryTopicEvent(hubBandTelemetryTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubBandRolloverTopicEvent hubBandRolloverTopicEvent) {
        onHubBandRolloverTopicEvent(hubBandRolloverTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubBandPresenceTopicEvent hubBandPresenceTopicEvent) {
        onHubBandPresenceTopicEvent(hubBandPresenceTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubBandStateTopicEvent hubBandStateTopicEvent) {
        onHubBandStateTopicEvent(hubBandStateTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubEventsTopicEvent hubEventsTopicEvent) {
        onHubEventsTopicEvent(hubEventsTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubSleepPredictionTopicEvent hubSleepPredictionTopicEvent) {
        onHubSleepPredictionTopicEvent(hubSleepPredictionTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubSleepStatusTopicEvent hubSleepStatusTopicEvent) {
        onHubSleepStatusTopicEvent(hubSleepStatusTopicEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubUserConfigurationTopic hubUserConfiguration) {
        onHubUserConfiguration(hubUserConfiguration.getHubUserConfiguration());

    }

    /**
     * This method is called when HubPresenceTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubPresenceTopicEvent
     */
    protected void onHubPresenceTopicEvent(HubPresenceTopicEvent hubPresenceTopicEvent) {
        //override this method in when its corresponding topic is subscribed
    }

    /**
     * This method is called when HubStatusTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubStatusTopicEvent
     */
    protected void onHubStatusTopicEvent(HubStatusTopicEvent hubStatusTopicEvent) {
        //override this method in when its corresponding topic is subscribed

        Hub.HubStatus hubStatus = hubStatusTopicEvent.getHubStatus();
//        if (hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.HUB_FLASH_START ||
//                hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.WEARABLE_FLASH_START) {
//            if (mIsActivityForeground) {
//                mFirmwareUpdateDialogFragment = (FirmwareUpdateDialogFragment) getFragmentManager().findFragmentByTag(FirmwareUpdateDialogFragment.TAG);
//                if (mFirmwareUpdateDialogFragment == null) {
//                    mFirmwareUpdateDialogFragment = FirmwareUpdateDialogFragment.newInstance(
//                            getString(R.string.updating___), getString(R.string.firmware_updating_message));
//                }
//                if (mFirmwareUpdateDialogFragment.getDialog() == null || !mFirmwareUpdateDialogFragment.getDialog().isShowing()) {
//                    mFirmwareUpdateDialogFragment.show(getFragmentManager(), FirmwareUpdateDialogFragment.TAG);
//                }
//            }
//
//        } else {
//            if (mFirmwareUpdateDialogFragment != null && mFirmwareUpdateDialogFragment.getDialog() != null
//                    && mFirmwareUpdateDialogFragment.getDialog().isShowing()) {
//
//                mFirmwareUpdateDialogFragment.setMessage(getString(R.string.firmware_updated_message));
//                mFirmwareUpdateDialogFragment.setButtonText(getString(R.string.ok));
//                mFirmwareUpdateDialogFragment.enableButton(true);
//            }
//        }

    }

    /**
     * This method is called when HubAmbientTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubAmbientTopicEvent
     */
    protected void onHubAmbientTopicEvent(HubAmbientTopicEvent hubAmbientTopicEvent) {

    }

    /**
     * This method is called when HubFirmwareUpdateTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubFirmwareUpdateTopicEvent
     */
    protected void onHubFirmwareUpdateTopicEvent(HubFirmwareUpdateTopicEvent hubFirmwareUpdateTopicEvent) {

    }

    /**
     * This method is called when HubControlTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubControlTopicEvent
     */
    protected void onHubControlTopicEvent(HubControlTopicEvent hubControlTopicEvent) {

    }

    /**
     * This method is called when HubCLITopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubCLITopicEvent
     */
    protected void onHubCLITopicEvent(HubCLITopicEvent hubCLITopicEvent) {

    }

    /**
     * This method is called when HubBandStatusTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubBandStatusTopicEvent
     */
    protected void onHubBandStatusTopicEvent(HubBandStatusTopicEvent hubBandStatusTopicEvent) {

    }

    /**
     * This method is called when HubBandTelemetryTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubBandTelemetryTopicEvent
     */
    protected void onHubBandTelemetryTopicEvent(HubBandTelemetryTopicEvent hubBandTelemetryTopicEvent) {

    }

    /**
     * This method is called when HubBandRolloverTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubBandRolloverTopicEvent
     */
    protected void onHubBandRolloverTopicEvent(HubBandRolloverTopicEvent hubBandRolloverTopicEvent) {

    }

    /**
     * This method is called when HubBandPresenceTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubBandPresenceTopicEvent
     */
    protected void onHubBandPresenceTopicEvent(HubBandPresenceTopicEvent hubBandPresenceTopicEvent) {

    }

    /**
     * This method is called when HubBandStateTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubBandStateTopicEvent
     */
    protected void onHubBandStateTopicEvent(HubBandStateTopicEvent hubBandStateTopicEvent) {

    }

    /**
     * This method is called when HubEventsTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubEventsTopicEvent
     */
    protected void onHubEventsTopicEvent(HubEventsTopicEvent hubEventsTopicEvent) {

    }

    /**
     * This method is called when HubSleepPredictionTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubSleepPredictionTopicEvent
     */
    protected void onHubSleepPredictionTopicEvent(HubSleepPredictionTopicEvent hubSleepPredictionTopicEvent) {

    }

    /**
     * This method is called when HubSleepStatusTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubSleepStatusTopicEvent
     */
    protected void onHubSleepStatusTopicEvent(HubSleepStatusTopicEvent hubSleepStatusTopicEvent) {

    }

    /**
     * This method is called when HubUserConfiguration received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubUserConfiguration
     */
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {

    }

    /**
     * Connect to MQTT broker.
     */
    protected void connectMqtt() {

        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.CONNECT)
                .build();
        PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, mConnectMqttCallback);
    }

    protected ConnectMqttCallback mConnectMqttCallback = new ConnectMqttCallback() {
        @Override
        public void onResultReceived(MqttItem mqttItem) {
            subscribeTopics();
            isAlreadyNoService = false;
        }

        @Override
        public void onFailed(int status, String message) {
            if (Utils.isOnline(BaseMqttActivity.this)) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        SystemClock.sleep(3000);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        connectMqtt();
                    }
                }.execute();
            }
//            else {
//                EventOuterClass.Event noServiceEvent = EventOuterClass.Event.newBuilder(EventOuterClass.Event.getDefaultInstance()).setEvent(EventOuterClass.EventType.NO_SERVICE).build();
//                disPatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.DATAITEM_MQTT, noServiceEvent));
//            }
        }
    };


    /**
     * Disconnect from MQTT broker.
     */
    protected void disconnectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.DISCONNECT)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new DisconnectMqttCallback());
    }

    /**
     * Subscribe topics for device's mEvents.
     */
    protected void subscribeTopics() {
        ArrayList<String> topics = getTopicsToSubscribe();
        if (topics != null && topics.size() > 0) {
            MqttItem.Builder mqttBuilder = new MqttItem.Builder(this)
                    .actionType(MqttAction.SUBSCRIBE);

            for (String topic : topics) {
                mqttBuilder.topic(topic);
            }
            MqttItem mqttItem = mqttBuilder.build();

            PipelineManager.getInstance().doPipeline(
                    new BaseMqttTaskList(),
                    mqttItem,
                    getSubscribeCallBack());
        }
    }

    protected abstract ArrayList<String> getTopicsToSubscribe();

    protected IPipeCallback getSubscribeCallBack() {
        return new SubscribeMqttCallback();
    }


}
