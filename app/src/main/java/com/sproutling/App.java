/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.fuhu.pipeline.manager.MqttManager;
import com.fuhu.states.CombinedReducer;
import com.fuhu.states.Store;
import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IStateApplication;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;
import com.helpshift.All;
import com.helpshift.Core;
import com.helpshift.InstallConfig;
import com.helpshift.exceptions.InstallException;
import com.helpshift.support.ApiConfig;
import com.helpshift.support.Metadata;
import com.helpshift.support.Support;
import com.sproutling.api.SproutlingApi;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.pipeline.MqttCallbackHandler;
import com.sproutling.services.SSManagement;
import com.sproutling.states.Actions;
import com.sproutling.states.DialogReducer;
import com.sproutling.states.FlowReducer;
import com.sproutling.states.MusicAndLightReducer;
import com.sproutling.states.SproutlingStore;
import com.sproutling.states.States;
import com.sproutling.states.StatusReducer;
import com.sproutling.utils.SharedPrefManager;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.File;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import sproutling.Hub;


public class App extends Application implements IStateApplication, Support.Delegate {
    public static final String FISHER_PRICE_CHINA_FLAVOR = "fisherPriceChina";
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance;
    private static Hub.HubUserConfiguration mHubUserConfiguration;
    private SproutlingStore mStore;
    private String[] mTags;
    private HashMap<String, Object> mUserData;

    public static App getInstance() {
        return sInstance;
    }

    public static String getAppPackage() {
        return sInstance.getPackageName();
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        sInstance = this;
        if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
            Core.init(All.getInstance());
            InstallConfig installConfig = new InstallConfig.Builder()
                    .setNotificationIcon(R.drawable.ic_push_notif_status_bar)
                    .setLargeNotificationIcon(R.mipmap.ic_launcher)
                    .setEnableInAppNotification(true)
                    .build();
            try {
                Core.install(this, BuildConfig.HELP_SHIFT_API_KEY, BuildConfig.HELP_SHIFT_DOMAIN, BuildConfig.HELP_SHIFT_APP_ID,
                        installConfig);
            } catch (InstallException e) {
                android.util.Log.e("Helpshift", "install call : ", e);
            }

            android.util.Log.d("Helpshift", Support.libraryVersion + " - is the version for gradle");

            mTags = new String[]{}; //new String[]{"feedback", "paid user"};
        }


        // mStore init
        CombinedReducer reducer = new CombinedReducer(
                StatusReducer.class,
                FlowReducer.class,
                MusicAndLightReducer.class,
                DialogReducer.class
        );
        mStore = SproutlingStore.createStore(reducer);

        //dispatch init state
        if (mStore.isStateEmpty()) {
//			Action actInit = new Action(Actions.DATA_UPDATE, null, new Payload());
            Action actInit = new Action(Actions.STATUS_UPDATE, getAppContext(), new Payload());
            ((Payload) actInit.payload()).put(States.Key.PRE_STATUS, States.StatusValue.INITIAL);
            ((Payload) actInit.payload()).put(States.Key.CURRENT_STATUS, States.StatusValue.INITIAL);
            mStore.dispatch(actInit);
        }

        SSManagement.ENDPOINT = SharedPrefManager.getServerUrl(this);
        MqttAPI.MQTT_TLS_BROKER = SharedPrefManager.getMqttUrl(this);
        SproutlingApi.initialize(this, SSManagement.ENDPOINT);
//        initializeMqtt();
    }

    public void initializeMqtt() {
        // init MqttManager with client id.
        Log.d(TAG, "Initializing the MQTT client.");
        MqttCallback mqttCallback = new MqttCallbackHandler(this);
        String clientId = MqttClient.generateClientId();
        MqttManager.getInstance().init(this, MqttAPI.getMqttTlsUrl(), clientId, mqttCallback);
    }

    @Override
    public Store getStore() {
        return mStore;
    }

    @Override
    public void dispatchAction(int type, IStatePayload payload, Object dispatcher) {
        mStore.dispatch(new Action(type, dispatcher, payload));
    }

    @Override
    public void dispatchAction(int type, IStatePayload payload) {
        mStore.dispatch(new Action(type, null, payload));
    }

    public void setUserData(String key, Object data) {
        if (mUserData == null) mUserData = new HashMap<>();
        mUserData.put(key, data);
    }

    public void openHelp(Activity activity) {
        if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
            SSManagement.DeviceResponse device = SharedPrefManager.getDevice(getAppContext());
            if (device != null) setUserData("hub_serial_number", device.getSerial());

            Metadata metadata = new Metadata(mUserData, mTags);

            ApiConfig apiConfig = new ApiConfig.Builder()
                    .setRequireEmail(true)
                    .setCustomMetadata(metadata)
                    .build();
            Support.showFAQs(activity, apiConfig);
        }
    }

    public Hub.HubUserConfiguration getHubUserConfiguration() {
        return mHubUserConfiguration;
    }

    public void setHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {
        mHubUserConfiguration = hubUserConfiguration;
    }

    @Override
    public void sessionBegan() {

    }

    @Override
    public void sessionEnded() {

    }

    @Override
    public void newConversationStarted(String s) {

    }

    @Override
    public void conversationEnded() {

    }

    @Override
    public void userRepliedToConversation(String s) {

    }

    @Override
    public void userCompletedCustomerSatisfactionSurvey(int i, String s) {

    }

    @Override
    public void displayAttachmentFile(File file) {

    }

    @Override
    public void didReceiveNotification(int i) {

    }
}
