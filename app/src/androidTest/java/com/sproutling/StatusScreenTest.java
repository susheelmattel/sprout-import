package com.sproutling;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageButton;

import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.google.protobuf.Timestamp;
import com.sproutling.pipeline.callback.PublishMqttMessageCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.ui.activity.StatusActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import sproutling.EventOuterClass;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by xylonchen on 2017/2/22.
 */

@RunWith(AndroidJUnit4.class)
public class StatusScreenTest {


    ImageButton btnClose;

    private int UNKNOWN = 0;
    private int LEARNING_PERIOD = 1;
    private int AWAKE = 2;
    private int STIRRING = 3;
    private int ASLEEP = 4;
    private int HEART_RATE = 5;
    private int HEART_RATE_UNUSUAL = 6;
    private int ROLLED_OVER = 7;
    private int WEARABLE_READY = 8;
    private int WEARABLE_BATTERY = 9;
    private int WEARABLE_CHARGING = 10;
    private int WEARABLE_CHARGED = 11;
    private int WEARABLE_TOO_FAR_AWAY = 12;
    private int WEARABLE_NOT_FOUND = 13;
    private int WEARABLE_FELL_OFF = 14;
    private int HUB_OFFLINE = 15;
    private int NO_SERVICE = 16;

    private String serial = "device/hub/647HH00A00557/band/telemetry";

    private int duration = 2000;

    static final String TAG = "StatusScreenTest";

    @Rule
    public ActivityTestRule<StatusActivity> mStatusActivity = new ActivityTestRule(StatusActivity.class);

//    @Rule
//    public ActivityTestRule<SetSleepActivity> mSetSleepActivity = new ActivityTestRule(SetSleepActivity.class);

    @Before
    public void setUp() throws Exception {


//        // findViewById()找到我要测试的控件
//        btnClose = (ImageButton) mSetSleepActivity.getActivity().findViewById(R.id.btnClose);

        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }


    @Test
    public void checkStatusChange() {

        publishMqttMessage(LEARNING_PERIOD);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_learning_period))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_learning_period))).check(matches(isDisplayed()));
        onView(withId(R.id.imgInformation)).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(UNKNOWN);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        publishMqttMessage(AWAKE);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_awake))).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(STIRRING);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_stirring))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_stirring))).check(matches(isDisplayed()));
//        onView(withId(R.id.imgSmallIconBtn)).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(ASLEEP);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_asleep))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_asleep))).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(HEART_RATE);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        publishMqttMessage(HEART_RATE_UNUSUAL);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        publishMqttMessage(ROLLED_OVER);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_rolled_over))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_rollover_alert))).check(matches(isDisplayed()));
//        onView(withId(R.id.imgSmallIconBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.txtDismiss)).check(matches(isDisplayed()));

        publishMqttMessage(WEARABLE_READY);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_ready))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_ready))).check(matches(isDisplayed()));

        publishMqttMessage(WEARABLE_BATTERY);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        publishMqttMessage(WEARABLE_CHARGING);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_charging))).check(matches(isDisplayed()));
//        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_charging))).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(WEARABLE_CHARGED);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_charged))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_charged))).check(matches(isDisplayed()));
        if(!StatusActivity.sIsLearningPeriodCompleted)
        {
            onView(withId(R.id.linearFloatButton)).check(matches(isDisplayed()));
        }

        publishMqttMessage(WEARABLE_TOO_FAR_AWAY);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_too_far_away))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_too_far_away))).check(matches(isDisplayed()));

        publishMqttMessage(WEARABLE_NOT_FOUND);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_not_found))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_not_found))).check(matches(isDisplayed()));

        publishMqttMessage(WEARABLE_FELL_OFF);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_wearable_fell_off))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_wearable_fell_off))).check(matches(isDisplayed()));

        publishMqttMessage(HUB_OFFLINE);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_sprouting_offline))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_sprouting_offline))).check(matches(isDisplayed()));

        publishMqttMessage(NO_SERVICE);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}
        onView(allOf(withId(R.id.txtTitle), withText(R.string.status_title_no_service))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.txtContent), withText(R.string.status_detail_no_service))).check(matches(isDisplayed()));

    }


    @Test
    public void checkLearningPeriodStatus() {

        publishMqttMessage(LEARNING_PERIOD);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.statusView)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.statusView)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.frameFloatBtn)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(allOf(withId(R.id.btnStartStop), withText(R.string.set_sleep_dlg_start))).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

//        onView(allOf(withId(R.id.btnCloseLog))).perform(click());
//        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.frameFloatBtn)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(allOf(withId(R.id.btnStartStop), withText(R.string.set_sleep_dlg_stop))).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

//        onView(allOf(withId(R.id.btnCloseLog))).perform(click());
//        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(allOf(withId(R.id.btnDiscard))).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

    }


    @Test
    public void checkStirringStatus() {

        publishMqttMessage(STIRRING);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

//        onView(withId(R.id.imgSmallIconBtn)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.btnSecond)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

    }

    @Test
    public void checkRollOverStatus() {

        publishMqttMessage(ROLLED_OVER);
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

//        onView(withId(R.id.imgSmallIconBtn)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

        onView(withId(R.id.btnSecond)).perform(click());
        try {Thread.sleep(duration);} catch (InterruptedException e) {e.printStackTrace();}

    }


    /**
     * Publish a message to Mqtt broker.
     */
    private void publishMqttMessage(int currentEvent) {
        // Mock data
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis()/1000)
                .setNanos((int)System.nanoTime()).build();

        EventOuterClass.EventType eventType = EventOuterClass.EventType.forNumber(currentEvent);
        EventOuterClass.Event eosEvent = EventOuterClass.Event.newBuilder()
                .setEvent(eventType)
                .setEventValue(currentEvent)
                .setTimestamp(timestamp)
//                .setBatteryVoltage(239)
                .build();

        MqttItem mqttItem = new MqttItem.Builder(mStatusActivity.getActivity())
                .actionType(MqttAction.PUBLISH_MESSAGE)
                .topic(serial)
                .payload(eosEvent.toByteArray())
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new PublishMqttMessageCallback());
    }

}
