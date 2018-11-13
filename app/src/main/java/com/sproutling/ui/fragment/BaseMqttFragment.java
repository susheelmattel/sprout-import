/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.sproutling.App;
import com.sproutling.object.HubAmbientTopicEvent;
import com.sproutling.object.HubBandPresenceTopicEvent;
import com.sproutling.object.HubBandRolloverTopicEvent;
import com.sproutling.object.HubBandStatusTopicEvent;
import com.sproutling.object.HubBandTelemetryTopicEvent;
import com.sproutling.object.HubCLITopicEvent;
import com.sproutling.object.HubControlTopicEvent;
import com.sproutling.object.HubEventsTopicEvent;
import com.sproutling.object.HubFirmwareUpdateTopicEvent;
import com.sproutling.object.HubPresenceTopicEvent;
import com.sproutling.object.HubStatusTopicEvent;
import com.sproutling.object.HubUserConfigurationTopic;
import com.sproutling.pipeline.callback.ConnectMqttCallback;
import com.sproutling.pipeline.callback.DisconnectMqttCallback;
import com.sproutling.pipeline.callback.SubscribeMqttCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import sproutling.Hub;

import static com.sproutling.broadcast.ConnectReceiver.isAlreadyNoService;

/**
 * Created by subram13 on 2/14/17.
 */

public abstract class BaseMqttFragment extends BaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
    public void onEvent(HubEventsTopicEvent hubEventsTopicEvent) {
        onHubEventsTopicEvent(hubEventsTopicEvent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HubUserConfigurationTopic hubUserConfigurationTopic) {
        onHubUserConfiguration(hubUserConfigurationTopic.getHubUserConfiguration());

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
     * This method is called when HubEventsTopicEvent received
     * override this method in when its corresponding topic is subscribed
     *
     * @param hubEventsTopicEvent
     */
    protected void onHubEventsTopicEvent(HubEventsTopicEvent hubEventsTopicEvent) {

    }

    /**
     * This method is called when HubUserConfiguration received
     * override this method in when its corresponding topic is subscribed
     * @param hubUserConfiguration
     */
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {

    }

    /**
     * Connect to MQTT broker.
     */
    protected void connectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(App.getAppContext())
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
            if (Utils.isOnline(App.getAppContext())) {
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
//                disPatchAction(Actions.TYPE_STATUS_UPDATE, new Payload().put(States.Key.DATAITEM_MQTT, noServiceEvent));
//            }
        }
    };


    /**
     * Disconnect from MQTT broker.
     */
    protected void disconnectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(App.getAppContext())
                .actionType(MqttAction.DISCONNECT)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new DisconnectMqttCallback());
    }

    /**
     * Subscribe topics for device's events.
     */
    protected void subscribeTopics() {
        ArrayList<String> topics = getTopicsToSubscribe();
        if (topics != null && topics.size() > 0) {
            MqttItem.Builder mqttBuilder = new MqttItem.Builder(App.getAppContext())
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
