/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

import android.content.Context;
import android.os.AsyncTask;

import com.fuhu.states.State;
import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;
import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.utils.Const;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import sproutling.EventOuterClass;
import sproutling.EventOuterClass.Event;
import sproutling.EventOuterClass.EventType;
import sproutling.Hub;
import sproutling.Sleep;

/**
 * Created by Xylon on 2016/12/29.
 */
public class StatusReducer implements IStateReducer {

    @Override
    public IState reduce(Action action, final IState state) {
        Payload actPayload = (Payload) action.payload(); //action's payload
        final Payload statePayload = (Payload) state.data(); //state's paypoad
        String preStatus;
        String curStatus;
        Object dispatcher = action.getDispatcher();
        Payload resultPayload = new Payload();

        switch (action.getType()) { //switch current action
            case Actions.LIST_CHILD:
                if (dispatcher != null && dispatcher instanceof Context) {
                    final Context context = (Context) action.getDispatcher();
                    final SSManagement.Child[] mChild = new SSManagement.Child[1];

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                List<SSManagement.Child> children = SKManagement.listChildren(AccountManagement.getInstance(context).getAccessToken());
                                if (children != null && !children.isEmpty()) {
                                    mChild[0] = children.get(0); // TODO: multiple children?
                                }
                                ((StatusActivity) context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DATAITEM_CHILD, mChild[0]));
                                    }
                                });
                            } catch (IOException | JSONException | SSException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;

            case Actions.RECONNECT_MQTT:
                if (dispatcher != null && dispatcher instanceof Context) {
                    final Context context = (Context) action.getDispatcher();
                    preStatus = statePayload.getString(States.Key.PRE_STATUS);
                    curStatus = statePayload.getString(States.Key.CURRENT_STATUS);

                    resultPayload.put(States.Key.PRE_STATUS, curStatus);
                    resultPayload.put(States.Key.CURRENT_STATUS, preStatus);

                    if (context != null) {
                        if (context instanceof StatusActivity) {
                            ((StatusActivity) context).connectMqtt();
                        }
                    }

                    return new State(resultPayload);
                }
                break;

            case Actions.DISMISS_ROLLOVER:

                boolean disableRolloverNotification = actPayload.getBoolean(Actions.Key.DISABLE_ROLLOVER_NOTIFICATION, false);
                if (disableRolloverNotification) {
                    doDisableRolloverNotification();
                    Utils.logEvents(LogEvents.STATUS_CARD_TURN_OFF_ROLLOVER_ALERTS);
                } else {
                    Utils.logEvents(LogEvents.STATUS_CARD_DISMISS_ROLLOVER);
                }

                curStatus = statePayload.getString(States.Key.PRE_STATUS);//VALID_STATUS
                resultPayload.put(States.Key.CURRENT_STATUS, curStatus);

                return new State(resultPayload);

            case Actions.STATUS_UPDATE: {
                preStatus = statePayload.getString(States.Key.PRE_STATUS);
                curStatus = statePayload.getString(States.Key.CURRENT_STATUS);

                Event actMqttEvent = (Event) actPayload.get(States.Key.MQTT_EVENTS); //To get the new current status
                int eventTypeValue;
                String actPayloadCurStatus = actPayload.getString(States.Key.CURRENT_STATUS);
                if (actMqttEvent == null) {
                    if (States.StatusValue.NO_CONFIGURED_DEVICE.equals(actPayloadCurStatus)) {
                        eventTypeValue = States.EventTypeValue.NO_CONFIGURED_DEVICE;
                    } else if (States.StatusValue.FIRMWARE_UPDATING.equals(actPayloadCurStatus)) {
                        eventTypeValue = States.EventTypeValue.FIRMWARE_UPDATING;
                    } else if (States.StatusValue.DETECTING.equals(actPayloadCurStatus)) {
                        eventTypeValue = States.EventTypeValue.DETECTING;
                    } else {
                        eventTypeValue = States.EventTypeValue.INITIAL;
                    }
                } else {
                    eventTypeValue = actMqttEvent.getEventValue();
                }
                if (curStatus != null) {
                    // checks presence
                    if (States.EventTypeValue.NO_CONFIGURED_DEVICE != eventTypeValue &&
                            ((States.StatusValue.HUB_OFFLINE.equals(curStatus) && eventTypeValue != EventType.HUB_ONLINE_VALUE) ||
                            (!States.StatusValue.HUB_OFFLINE.equals(curStatus) && eventTypeValue == EventType.HUB_ONLINE_VALUE))) {
                        return null;
                    }

                    if (!States.StatusValue.HEART_RATE.equals(curStatus) && !States.StatusValue.WEARABLE_BATTERY.equals(curStatus)) {
                        if (States.StatusValue.ROLLED_OVER.equals(curStatus)) {
                            if (eventTypeValue != EventType.ROLLED_OVER_VALUE) {
                                preStatus = getStatusByEventType(eventTypeValue);
                            }
                        } else {
                            preStatus = curStatus; //previous status
                            curStatus = getStatusByEventType(eventTypeValue);
                        }
                    }
                } else {
                    preStatus = actPayloadCurStatus; //previous status
                    curStatus = getStatusByEventType(eventTypeValue);
                }
                return new State(statusSwitcher(resultPayload, preStatus, curStatus));
            }
            case Actions.SLEEP_PREDICTIONS:
                Sleep.SleepPrediction sleepPredictionData = (Sleep.SleepPrediction) actPayload.get(States.Key.MQTT_SLEEP_PREDICTION);
                resultPayload.put(States.Key.MQTT_SLEEP_PREDICTION, sleepPredictionData);
                return new State(resultPayload);

            case Actions.SLEEP_STATUS: {
                Hub.BandState bandState = (Hub.BandState) statePayload.get(States.Key.MQTT_BAND_STATE);
                Sleep.SleepStatus sleepStatus = (Sleep.SleepStatus) actPayload.get(States.Key.MQTT_SLEEP_STATUS);
                EventType eventType = null;
                Sleep.SleepStatus.State sleepState = sleepStatus.getSleepState();

                switch (sleepState) {
                    case SLEEPING:
                        eventType = EventType.ASLEEP;
                        break;
                    case STIRRING:
                        eventType = EventType.STIRRING;
                        break;
                    case AWAKE:
                        eventType = EventType.AWAKE;
                        break;
                }

                if (eventType != null) {
                    Event sleepStatusEvent =
                            Event.newBuilder(Event.getDefaultInstance())
                                    .setEvent(eventType)
                                    .build();

                    if (bandState != null &&
                            Hub.BandState.WearableState.DETECTING.equals(bandState.getState()) &&
                            bandState.hasTimestamp() && sleepStatus.hasTimestamp() &&
                            sleepStatus.getTimestamp().getSeconds() > bandState.getTimestamp().getSeconds()) {
                        App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, sleepStatusEvent));
                    }
//                    resultPayload.put(States.Key.MQTT_SLEEP_STATUS, sleepStatus);
//                    return new State(resultPayload);
                }
                break;
            }
            case Actions.HUB_PRESENCE:
                Hub.HubPresence hubPresence = (Hub.HubPresence) actPayload.get(States.Key.MQTT_HUB_PRESENCE);
                if (hubPresence != null) {
                    EventOuterClass.Event.Builder statusEventBuilder =
                            EventOuterClass.Event.newBuilder(EventOuterClass.Event.getDefaultInstance());
                    if (hubPresence.getConnectionState() == Hub.HubPresence.ConnectionStates.ONLINE) {
                        // hub is online
                        statusEventBuilder.setEvent(EventType.HUB_ONLINE);
                        App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, statusEventBuilder.build()));
                    } else if (hubPresence.getConnectionState() == Hub.HubPresence.ConnectionStates.OFFLINE) {
                        // hub is offline
                        statusEventBuilder.setEvent(EventType.HUB_OFFLINE);
                        App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, statusEventBuilder.build()));
                    } else if (hubPresence.getConnectionState() == Hub.HubPresence.ConnectionStates.REBOOT) {
                        App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.CURRENT_STATUS, States.StatusValue.FIRMWARE_UPDATING));
                    }
                }
                break;

            case Actions.BAND_ROLLOVER: {
                Hub.BandState bandState = (Hub.BandState) statePayload.get(States.Key.MQTT_BAND_STATE);
                Sleep.SleepStatus sleepStatus = (Sleep.SleepStatus) statePayload.get(States.Key.MQTT_SLEEP_STATUS);
//                Hub.BandRollover bandRollover = (Hub.BandRollover) actPayload.get(States.Key.MQTT_BAND_ROLLOVER);

                if (bandState != null && Hub.BandState.WearableState.DETECTING.equals(bandState.getState()) &&
                        bandState.hasTimestamp() && bandState.getTimestamp().getSeconds() > ((System.currentTimeMillis() + Const.TIME_MS_MIN) / Const.TIME_MS_SEC)) {
                    if (sleepStatus != null && sleepStatus.hasTimestamp() &&
                            sleepStatus.getTimestamp().getSeconds() > bandState.getTimestamp().getSeconds()) {
                        Event.Builder eventBuilder = Event.newBuilder(Event.getDefaultInstance());
                        eventBuilder.setEvent(EventType.ROLLED_OVER);
                        App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, eventBuilder.build()));
                    }
                }
                break;
            }
//			case Actions.BAND_PRESENCE:
//                Hub.BandPresence bandPresence = (Hub.BandPresence) actPayload.get(States.Key.BAND_PRESENCE_DATA);
//                resultPayload.put(States.Key.BAND_PRESENCE_DATA, bandPresence);
//                return new State(resultPayload);
//				break;
            case Actions.BAND_STATE:
                Hub.BandState bandState = (Hub.BandState) actPayload.get(States.Key.MQTT_BAND_STATE);
                Hub.BandState.WearableState wearableState = bandState.getState();
                Event.Builder statusEventBuilder = Event.newBuilder(Event.getDefaultInstance());
                boolean isChangeEvent = true;

                switch (wearableState) {
                    case CHARGED:
                        statusEventBuilder.setEvent(EventType.WEARABLE_CHARGED);
                        break;
                    case CHARGING:
                        statusEventBuilder.setEvent(EventType.WEARABLE_CHARGING);
                        break;
                    case READY:
                        statusEventBuilder.setEvent(EventType.WEARABLE_READY);
                        break;
                    case BATTERY_DEAD:
                        statusEventBuilder.setEvent(EventType.WEARABLE_BATTERY_DEAD);
                        break;
                    case NOT_FOUND:
                        statusEventBuilder.setEvent(EventType.WEARABLE_NOT_FOUND);
                        break;
                    case DETECTING:
                    default:
                        isChangeEvent = false;
                }
                if (isChangeEvent) {
                    App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.MQTT_EVENTS, statusEventBuilder.build()));
                } else if (bandState.getState() == Hub.BandState.WearableState.DETECTING && !States.StatusValue.HUB_OFFLINE.equals(statePayload.getString(States.Key.CURRENT_STATUS))) {
                    App.getInstance().dispatchAction(Actions.STATUS_UPDATE, new Payload().put(States.Key.CURRENT_STATUS, States.StatusValue.DETECTING));
                }
                resultPayload.put(States.Key.MQTT_BAND_STATE, bandState);
                return new State(resultPayload);
        }
        return null;
    }

    private void doDisableRolloverNotification() {
        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {

            AccountManagement account;
            SSManagement.User user;
            SSManagement.UserAccountInfo userAccountInfo;
            SSManagement.PushNotification settings;
            SSError mError;

            @Override
            protected void onPreExecute() {
                account = AccountManagement.getInstance(App.getAppContext());
                user = account.getUser();
                userAccountInfo = account.getUserAccountInfo();
                settings = account.getPushNotificationSettings();
                if (settings == null || settings.notificationsDisabled == null) {
                    settings = SSManagement.PushNotification.getDefaultSettings();
                    account.writePushNotificationSettings(settings);
                }
                settings.rolledOverDisabled = SSManagement.PushNotification.DISABLED; //disable rollover notification
            }

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    return SKManagement.updateAlert(user.accessToken, userAccountInfo.id, settings.getSettingsAPIJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
                if (userAccountInfo != null) {
                    AccountManagement.getInstance(App.getAppContext()).writeUserAccountInfo(userAccountInfo);
                }
            }
        }.execute();
    }

    private String getStatusByEventType(int eventType) {
        String status = States.StatusValue.INITIAL; //Default

        switch (eventType) {

            case States.EventTypeValue.DETECTING:
                status = States.StatusValue.DETECTING;
                break;
            case States.EventTypeValue.FIRMWARE_UPDATING:
                status = States.StatusValue.FIRMWARE_UPDATING;
                break;
            case States.EventTypeValue.NO_CONFIGURED_DEVICE:
                status = States.StatusValue.NO_CONFIGURED_DEVICE;
                break;
            case States.EventTypeValue.INITIAL:
                status = States.StatusValue.INITIAL;
                break;
            case EventType.UNKNOWN_VALUE:
                status = States.StatusValue.UNKNOWN;
                break;
            case EventType.LEARNING_PERIOD_VALUE:
                status = States.StatusValue.LEARNING_PERIOD;
                break;
            case EventType.AWAKE_VALUE:
                status = States.StatusValue.AWAKE;
                break;
            case EventType.STIRRING_VALUE:
                status = States.StatusValue.STIRRING;
                break;
            case EventType.ASLEEP_VALUE:
                status = States.StatusValue.ASLEEP;
                break;
            case EventType.HEART_RATE_VALUE:
                status = States.StatusValue.HEART_RATE;
                break;
            case EventType.HEART_RATE_UNUSUAL_VALUE:
                status = States.StatusValue.UNUSUAL_HEARTBEAT;
                break;
            case EventType.ROLLED_OVER_VALUE:
                status = States.StatusValue.ROLLED_OVER;
                break;
            case EventType.WEARABLE_READY_VALUE:
                status = States.StatusValue.WEARABLE_READY;
                break;
            case EventType.WEARABLE_CHARGING_VALUE:
                status = States.StatusValue.WEARABLE_CHARGING;
                break;
            case EventType.WEARABLE_BATTERY_VALUE:
                status = States.StatusValue.WEARABLE_BATTERY;
                break;
            case EventType.WEARABLE_CHARGED_VALUE:
                status = States.StatusValue.WEARABLE_CHARGED;
                break;
            case EventType.WEARABLE_TOO_FAR_AWAY_VALUE:
                status = States.StatusValue.WEARABLE_TOO_FAR_AWAY;
                break;
            case EventType.WEARABLE_NOT_FOUND_VALUE:
                status = States.StatusValue.WEARABLE_NOT_FOUND;
                break;
            case EventType.WEARABLE_FELL_OFF_VALUE:
                status = States.StatusValue.WEARABLE_FELL_OFF;
                break;
            case EventType.HUB_OFFLINE_VALUE:
                status = States.StatusValue.HUB_OFFLINE;
                break;
            case EventType.HUB_ONLINE_VALUE:
                status = States.StatusValue.HUB_ONLINE;
                break;
            case EventType.NO_SERVICE_VALUE:
                status = States.StatusValue.NO_SERVICE;
                break;
            case EventType.WEARABLE_BATTERY_DEAD_VALUE:
                status = States.StatusValue.WEARABLE_OUT_OF_BATTERY;
                break;
        }

        return status;
    }


    private Payload statusSwitcher(Payload resultPayload, String preStatus, String curStatus) {

        resultPayload.put(States.Key.PRE_STATUS, preStatus);
        resultPayload.put(States.Key.CURRENT_STATUS, curStatus);
        return resultPayload;
    }
}
