/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import com.sproutling.ui.dialog.IntegrationDialog;
import com.sproutling.ui.fragment.status.CoachmarkFragment;
import com.sproutling.ui.view.StatusScreen.StatusScreenCoachmarksBackground;
import com.sproutling.ui.widget.StatusView;
import com.sproutling.utils.Const;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.SharedPrefManager.SPKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.sproutling.utils.SharedPrefManager.SPValue.LEARNING_PERIOD_ENDED;
import static com.sproutling.utils.SharedPrefManager.SPValue.LEARNING_PERIOD_IN_PROGRESS;

/**
 * Created by moi0312 on 2016/12/5.
 */

public class FlowReducer implements IStateReducer {
    private static final String TAG = FlowReducer.class.getSimpleName();

    @Override
    public IState reduce(Action action, IState state) {
        Payload payload = (Payload) action.payload();
        Object dispatcher = action.getDispatcher();
        switch (action.getType()) {
            case Actions.DATA_UPDATE:
                return new State(action.payload());

            case Actions.GET_DEVICE_SERIAL:
                if (dispatcher != null && dispatcher instanceof Context) {
                    Context context = (Context) dispatcher;
                    int index = payload.getInt(Actions.Key.INDEX, 0);
                    SSManagement.DeviceResponse device = SharedPrefManager.getDevice(context);
                    if (device != null) {
                        App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DEVICE_SERIAL, device.getSerial()));
                    } else {
//                        doGetDeviceSerial(context, index);
                        App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DEVICE_SERIAL, null));
                    }
                }
                break;
            case Actions.GET_LEARNING_PERIOD_TIMESTAMP:
                if (dispatcher != null && dispatcher instanceof Context) {
                    long startTimestamp = SharedPrefManager.getLong(App.getAppContext(), SPKey.LONG_LEARNING_PERIOD_START_TIMESTAMP, -1);
                    long endTimestamp = SharedPrefManager.getLong(App.getAppContext(), SPKey.LONG_LEARNING_PERIOD_DONE_TIMESTAMP, -1);
                    if (startTimestamp == -1 && endTimestamp == -1) {
                        final String childId = payload.getString(Actions.Key.CHILD_ID);
                        if (childId != null) {
                            new QueryLearningPeriodAsyncTask((Context) dispatcher).execute(childId);
                        }
                    } else {
                        checkLearningPeriod(endTimestamp);
                    }
                }
                break;

            case Actions.START_LEARNING_PERIOD:
                if (dispatcher != null && dispatcher instanceof Context) {
                    final Context context = (Context) dispatcher;
                    String childId = payload.getString(Actions.Key.CHILD_ID);
                    Date now = new Date();
                    //create learningPeriod event by timeline api
                    final JSONObject json = new JSONObject();
                    String startDate = Const.TIMELINE_DATE_FORMAT.format(now);
                    String endDate = Const.TIMELINE_DATE_FORMAT.format(new Date(now.getTime() + Const.TIME_MS_WEEK));
                    Log.v(TAG, "startDate: " + startDate + " , endDate: " + endDate);
                    try {
                        json.put("child_id", childId);
                        json.put("event_type", Const.TIMELINE_EVENT_LP);
                        json.put("start_date", startDate);
                        json.put("end_date", endDate);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new AddTimeLineEventAsyncTask(context).execute(json);
                }
                break;

            case Actions.SCENE_EVENT:
                int targetScene = payload.getInt(Actions.Key.SCENE, StatusActivity.SCENE_STATUS);
                StatusView statusView = (StatusView) payload.get(Actions.Key.VIEW);
                if (statusView != null) {
                    statusView.learningPeriodInfoAnim((targetScene == StatusActivity.SCENE_LEARNING_PERIOD));
                }
                break;

            case Actions.SWITCH_SCENE:
                if (dispatcher != null && dispatcher instanceof StatusActivity) {
                    int scene = payload.getInt(Actions.Key.SCENE, StatusActivity.SCENE_STATUS);
                    ((StatusActivity) dispatcher).gotoScene(scene);
                }
                break;
        }
        return null;
    }

    private void doGetDeviceSerial(final Context context, final int index) {
        new AsyncTask<Void, Void, SSManagement.DeviceResponse>() {
            SSError mError;

            @Override
            protected SSManagement.DeviceResponse doInBackground(Void... params) {
                try {
                    String accessToken = AccountManagement.getInstance(context).getAccessToken();
                    List<SSManagement.DeviceResponse> deviceList = SKManagement.listDevices(accessToken);
                    return deviceList.isEmpty() ? null : deviceList.get(index);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.DeviceResponse deviceResponse) {
                if (deviceResponse != null) {
                    SharedPrefManager.saveDevice(context, deviceResponse);
                    App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.DEVICE_SERIAL, deviceResponse.getSerial()));
                }
            }
        }.execute();
    }

    private void checkLearningPeriod(long learningPeriodEndTime) {
        if (StatusActivity.sInstance == null) return;
        boolean hasTutorialShown = SharedPrefManager.getBoolean(StatusActivity.sInstance, SPKey.BOOL_TUTORIAL_SHOWN, false);
        if (learningPeriodEndTime < 0 && !hasTutorialShown && CoachmarkFragment.sInstance != null) {
            CoachmarkFragment.sInstance.setTooltipsVisible(true);
            CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.WELCOME_PAGE);
            SharedPrefManager.put(StatusActivity.sInstance, SPKey.BOOL_TUTORIAL_SHOWN, true);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(learningPeriodEndTime);
            calendar.add(Calendar.MILLISECOND, TimeZone.getDefault().getOffset(calendar.getTimeInMillis()));
            Date now = new Date();
            String learningPeriodStatus = SharedPrefManager.getString(StatusActivity.sInstance, SPKey.STR_LEARNING_PERIOD_STATUS, null);
            if (now.after(calendar.getTime())) {
                if (LEARNING_PERIOD_IN_PROGRESS.equals(learningPeriodStatus) && CoachmarkFragment.sInstance != null) {
                    App.getInstance().dispatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.LEARNING_PERIOD_FINISH));

                    CoachmarkFragment.sInstance.setTooltipsVisible(true);
                    CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.PROGRESS_PAGE);
                } else {
                    StatusActivity.sInstance.setLearningPeriodCompleted(true);
                    SharedPrefManager.setLearningPeriodDone(StatusActivity.sInstance);
                    App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.LEARNING_PERIOD_STATUS, LEARNING_PERIOD_ENDED));
                }
            } else {
                SharedPrefManager.setLearningPeriodInProgress(StatusActivity.sInstance);
                App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.LEARNING_PERIOD_STATUS, LEARNING_PERIOD_IN_PROGRESS));
            }
        }

//        if (learningPeriodEndTime < 0) {
//                CoachmarkFragment.sInstance.setTooltipsVisible(true);
//                CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.WELCOME_PAGE);
//        } else {
//            StatusActivity.sIsLearningPeriodCompleted = SharedPrefManager.getBoolean(App.getAppContext(), SPKey.BOOL_LEARNING_PERIOD_DONE, false);
//            if (!StatusActivity.sIsLearningPeriodCompleted) {
//                Date learningPeriodEndDate = new Date(learningPeriodEndTime);
//                Date now = new Date();
//                if (now.after(learningPeriodEndDate)) {
//                    //show LEARNING_PERIOD complete dialog
//                    App.getInstance().dispatchAction(Actions.CALL_DIALOG, new Payload().put(Actions.Key.DIALOG, IntegrationDialog.LEARNING_PERIOD_FINISH));
//                    CoachmarkFragment.sInstance.setTooltipsVisible(true);
//                    CoachmarkFragment.sInstance.showTooltipsPage(StatusScreenCoachmarksBackground.PROGRESS_PAGE);
//                }
//            }
//        }
    }

    private class QueryLearningPeriodAsyncTask extends AsyncTask<String, Void, List<SSManagement.SSEvent>> {
        SSError mError;
        Context context;

        QueryLearningPeriodAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<SSManagement.SSEvent> doInBackground(String... params) {
            String childId = params[0];
            List<SSManagement.SSEvent> events = null;
            try {
                String accessToken = AccountManagement.getInstance(context).getAccessToken();
                Log.v(TAG, "accessToken: " + accessToken);
                events = SKManagement.listEventsByType(accessToken, childId, Const.TIMELINE_EVENT_LP);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (SSException e) {
                e.printStackTrace();
                mError = e.getError();
            }
            return events;
        }

        @Override
        protected void onPostExecute(List<SSManagement.SSEvent> events) {
            super.onPostExecute(events);
            if (mError != null) {
                //TODO error handle
                Log.v(TAG, "Error: QueryLearningPeriodAsyncTask: " + mError.message);
            } else if (events != null && events.size() > 0) {
                SSManagement.SSEvent event = events.get(0);
                setLearningPeriodStartEndTime(event);
            } else {
                //not yet start learning period
//                    App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.LEARNING_PERIOD_END_TIME, -1L));
                checkLearningPeriod(-1L);
            }
        }
    }

    private class AddTimeLineEventAsyncTask extends AsyncTask<JSONObject, Void, SSManagement.SSEvent> {
        SSError mError;
        Context context;

        AddTimeLineEventAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected SSManagement.SSEvent doInBackground(JSONObject... jsonObjs) {
            JSONObject jsonObj = jsonObjs[0];
            SSManagement.SSEvent result = null;
            try {
                String accessToken = AccountManagement.getInstance(context).getAccessToken();
                result = SKManagement.createEvent(accessToken, jsonObj);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (SSException e) {
                e.printStackTrace();
                mError = e.getError();
            }
            return result;
        }

        @Override
        protected void onPostExecute(SSManagement.SSEvent event) {
            super.onPostExecute(event);
            if (mError != null) {
                //TODO error handle
                Log.v(TAG, "Error: AddTimeLineEventAsyncTask: " + mError.message);
            } else {
                Log.v(TAG, "AddTimeLineEventAsyncTask: " + event.jsonString);
                setLearningPeriodStartEndTime(event);
            }
        }
    }

    public void setLearningPeriodStartEndTime(SSManagement.SSEvent event) {
        Log.v(TAG, "end_date: " + event.endDate);
        try {
            Date startDate = Const.TIMELINE_DATE_FORMAT.parse(event.startDate);
            SharedPrefManager.put(App.getAppContext(), SPKey.LONG_LEARNING_PERIOD_START_TIMESTAMP, startDate.getTime());

            Date endDate = Const.TIMELINE_DATE_FORMAT.parse(event.endDate);
            SharedPrefManager.put(App.getAppContext(), SPKey.LONG_LEARNING_PERIOD_DONE_TIMESTAMP, endDate.getTime());
//            App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.LEARNING_PERIOD_END_TIME, endDate.getTime()));
            checkLearningPeriod(endDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
