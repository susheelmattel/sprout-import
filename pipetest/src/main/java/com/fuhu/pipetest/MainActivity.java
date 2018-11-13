package com.fuhu.pipetest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeItem;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.mqtt.MqttQos;
import com.fuhu.pipetest.pipeline.TestAPI;
import com.fuhu.pipetest.pipeline.callback.ConnectMqttCallback;
import com.fuhu.pipetest.pipeline.callback.CreateUserCallback;
import com.fuhu.pipetest.pipeline.callback.DisconnectMqttCallback;
import com.fuhu.pipetest.pipeline.callback.LoginCallback;
import com.fuhu.pipetest.pipeline.callback.PublishMqttMessageCallback;
import com.fuhu.pipetest.pipeline.callback.SubscribeMqttCallback;
import com.fuhu.pipetest.pipeline.callback.UnsubscribeMqttCallback;
import com.fuhu.pipetest.pipeline.object.LoginItem;
import com.fuhu.pipetest.pipeline.object.LoginResponse;
import com.fuhu.pipetest.pipeline.object.SSUserItem;
import com.fuhu.pipetest.pipeline.taskList.BaseHttpTaskList;
import com.fuhu.pipetest.pipeline.taskList.BaseMqttTaskList;
import com.fuhu.pipetest.services.AccountManagement;
import com.google.protobuf.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import sproutling.EventOuterClass;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button mCreateUserButton, mLoginButton,  mUserDetailButton,
            mSubscribeButton, mUnsubscribeButton, mConnectMqttButton, mDisconnectMqttButton, mPublishMqttMessageButton;
    private EditText mEmailEditText, mPasswordEditText;
    private EditText mMqttTopicText;
    private Spinner mEventSpinner;
    private int currentEvent;
    private String [] events;

    private static final String MOCK_USER = "allan.shih+02@fuhu.com";
    private static final String MOCK_PASSWORD = "12345678";
    private static final String MOCK_SERIAL_NUMBER = "703HH00F00003";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailEditText = (EditText) findViewById(R.id.emailText);
        mEmailEditText.setText(MOCK_USER);
        mPasswordEditText = (EditText) findViewById(R.id.passwordText);
        mPasswordEditText.setText(MOCK_PASSWORD);

        mMqttTopicText = (EditText) findViewById(R.id.mqttTopicText);
        mMqttTopicText.setText(TestAPI.getEventTopic(MOCK_SERIAL_NUMBER));
//        mMqttMessageEditText = (ShEditText) findViewById(R.id.mqttMessageText);

        mEventSpinner = (Spinner) findViewById(R.id.eventSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mEventSpinner.setAdapter(adapter);
        mEventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEvent = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        events = getResources().getStringArray(R.array.event_array);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(mOnClickListener);

        // User
        mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mCreateUserButton.setOnClickListener(mOnClickListener);
        mUserDetailButton = (Button) findViewById(R.id.userDetailButton);
        mUserDetailButton.setOnClickListener(mOnClickListener);

        // MQTT
        mSubscribeButton = (Button) findViewById(R.id.subscribeButton);
        mSubscribeButton.setOnClickListener(mOnClickListener);
        mUnsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        mUnsubscribeButton.setOnClickListener(mOnClickListener);
        mConnectMqttButton = (Button) findViewById(R.id.connectMqttButton);
        mConnectMqttButton.setOnClickListener(mOnClickListener);
        mDisconnectMqttButton = (Button) findViewById(R.id.disconnectMqttButton);
        mDisconnectMqttButton.setOnClickListener(mOnClickListener);
        mPublishMqttMessageButton = (Button) findViewById(R.id.publishMessageButton);
        mPublishMqttMessageButton.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:
                    login();
                    break;
                case R.id.createUserButton:
                    createUser();
                    break;
                case R.id.userDetailButton:
                    userDetail();
                    break;
                case R.id.connectMqttButton:
                    connectMqtt();
                    break;
                case R.id.disconnectMqttButton:
                    disconnectMqtt();
                    break;
                case R.id.publishMessageButton:
                    publishMqttMessage();
                    break;
                case R.id.subscribeButton:
                    subscribeTopics();
                    break;
                case R.id.unsubscribeButton:
                    unsubscribeTopics();
                    break;
            }
        }
    };

    /**
     * Login into the application using Fuhu-Pipeline.
     */
    private void login() {
        if (mEmailEditText.getText() != null && mPasswordEditText.getText() != null) {
            LoginItem loginItem = new LoginItem();
            loginItem.setUsername(mEmailEditText.getText().toString());
            loginItem.setPassword(mPasswordEditText.getText().toString());
            loginItem.setGrantType("password");
            loginItem.setClientId("4677a7fc91373cbbb035ad4f43c1ebaddfaa8f95344fd37c9efe3ea7afaca08e");
            loginItem.setScope(null);

            HttpItem okHttpItem = new HttpItem.Builder(this)
                    .url(TestAPI.oauth())
                    .dataModel(LoginResponse.class) // for response
                    .timeout(5000L)
                    .post(loginItem);

            PipelineManager.getInstance().doPipeline(
                    new BaseHttpTaskList(),
                    okHttpItem,
                    new LoginCallback());
        }
    }

    /**
     * Create a new user.
     */
    private void createUser() {
        if (mEmailEditText.getText() != null && mPasswordEditText.getText() != null) {
            JSONObject requestJson = null;
            try {
                requestJson = new JSONObject()
                        .put("email", mEmailEditText.getText().toString())
                        .put("first_name", "Allan")
                        .put("last_name", "Shih")
                        .put("type", "Guardian")
                        .put("password", mPasswordEditText.getText().toString())
                        .put("phone_number", "18054037037")
                        .put("password_confirmation", mPasswordEditText.getText().toString());
            } catch (JSONException je) {
                je.printStackTrace();
            }

            HttpItem okHttpItem = new HttpItem.Builder(this)
                    .url(TestAPI.users())
                    .dataModel(SSUserItem.class) // for response
                    .post(requestJson);

            PipelineManager.getInstance().doPipeline(
                    new BaseHttpTaskList(),
                    okHttpItem,
                    new CreateUserCallback());
        }
    }

    /**
     * Get the user detail.
     */
    private void userDetail() {
//        HttpItem okHttpItem = new HttpItem.Builder(this)
//                .url(TestAPI.user(getUserId()))
//                .addHeader("Authorization", "Bearer " + getAccessToken())
//                .dataModel(SSUserItem.class)
//                .get();
//
//        PipelineManager.getInstance().doPipeline(
//                new BaseHttpTaskList(),
//                okHttpItem,
//                new UserDetailCallback());
        listEvents();
    }

    private void listEvents() {
        HttpItem okHttpItem = new HttpItem.Builder(this)
            .url("https://api-dev-us.sproutlingcloud.com/timeline/v1/children/c4a89fa8-ad63-4927-b377-f1e7e08ad250/events?event_type=learningPeriod")
            .dataModel(TestEventItem[].class) // for response
            .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InYwLjAuMSIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIzMzIzM2M0MS1lNmM0LTQxNTAtYmVjNi00MGMzYWU0MTUzNWEiLCJhaWQiOiJmYzgxYzM5Mi0xNzY5LTQxYmUtYWRiMi0zMGFiMjMxY2U1ZWIiLCJlbWFpbCI6ImFsbGFuLnNoaWgrMDJAZnVodS5jb20iLCJhcHBpZCI6IjYyYjljZjY4OGZmMWE1NmEzMjhlN2Q3OTZmZDNkYjQzN2JlM2E1ZWNjZGE5ZWE4M2QxNzc2MDljY2Y5NDY1ODciLCJleHAiOjE0OTI3NTgwMjQsImp0aSI6IjVlMDFhNGU2LTEzMDQtNDUzNy1iNzBjLWJkMjQ2MzUzMmRhMSIsImlhdCI6MTQ5MDE2NjAyNCwiaXNzIjoiU3Byb3V0bGluZyIsInN1YiI6IkFjY2VzcyBUb2tlbiJ9.n8uVUl4eVlL-fsTbaPyg34fY8D45HzGHNNpmQroPL1j6GOaKG1o8G0VNQElLa7EuNehAaFTRU-PQt742WQ7PXkklNN3XerGvbQ91M2a8jHkbec5u13FId1kYTFqyJtjj53KfmimucFnsepnjSGxQxb0V-WGdsn4_020J-ytYIKY")
            .addHeader("Accept-Language", "en-US")
            .addHeader("X-TIMEZONE", "PST")
            .get();

        PipelineManager.getInstance().doPipeline(
            new BaseHttpTaskList(),
            okHttpItem, new IPipeCallback() {
                @Override
                public void onResult(Object responseObject) {
                    Log.d(TAG, "on result: " + responseObject.getClass().getSimpleName());
                    if (responseObject != null && responseObject instanceof TestEventItem[]) {
                        TestEventItem [] items = (TestEventItem[]) responseObject;
                        Log.d(TAG, "items size: " + items.length);
                        Log.d(TAG, "id: " + items[0].id);
                    }
                }
                @Override
                public void onError(int status, IPipeItem pipeItem) {
                    Log.d(TAG, "on error: " + status);
                }
            });
    }

    private void getEvent() {
        HttpItem okHttpItem = new HttpItem.Builder(this)
            .url("https://api-dev-us.sproutlingcloud.com/timeline/v1/events/104c179e-f965-437c-a83f-0bafcf68ee32")
            .dataModel(TestEventItem.class) // for response
            .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InYwLjAuMSIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiIzMzIzM2M0MS1lNmM0LTQxNTAtYmVjNi00MGMzYWU0MTUzNWEiLCJhaWQiOiJmYzgxYzM5Mi0xNzY5LTQxYmUtYWRiMi0zMGFiMjMxY2U1ZWIiLCJlbWFpbCI6ImFsbGFuLnNoaWgrMDJAZnVodS5jb20iLCJhcHBpZCI6IjYyYjljZjY4OGZmMWE1NmEzMjhlN2Q3OTZmZDNkYjQzN2JlM2E1ZWNjZGE5ZWE4M2QxNzc2MDljY2Y5NDY1ODciLCJleHAiOjE0OTI3NTgwMjQsImp0aSI6IjVlMDFhNGU2LTEzMDQtNDUzNy1iNzBjLWJkMjQ2MzUzMmRhMSIsImlhdCI6MTQ5MDE2NjAyNCwiaXNzIjoiU3Byb3V0bGluZyIsInN1YiI6IkFjY2VzcyBUb2tlbiJ9.n8uVUl4eVlL-fsTbaPyg34fY8D45HzGHNNpmQroPL1j6GOaKG1o8G0VNQElLa7EuNehAaFTRU-PQt742WQ7PXkklNN3XerGvbQ91M2a8jHkbec5u13FId1kYTFqyJtjj53KfmimucFnsepnjSGxQxb0V-WGdsn4_020J-ytYIKY")
            .addHeader("Accept-Language", "en-US")
            .addHeader("X-TIMEZONE", "PST")
            .get();

        PipelineManager.getInstance().doPipeline(
            new BaseHttpTaskList(),
            okHttpItem, new IPipeCallback() {
                @Override
                public void onResult(Object responseObject) {
                    Log.d(TAG, "on result: " + responseObject.getClass().getSimpleName());
                    if (responseObject != null && responseObject instanceof TestEventItem) {
                        TestEventItem  item = (TestEventItem) responseObject;
                        Log.d(TAG, "event type: " + item.event_type);
                    }
                }
                @Override
                public void onError(int status, IPipeItem pipeItem) {
                    Log.d(TAG, "on error: " + status);
                }
            });
    }

    /**
     * Connect to MQTT broker.
     */
    private void connectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.CONNECT)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new ConnectMqttCallback());
    }

    /**
     * Disconnect from MQTT broker.
     */
    private void disconnectMqtt() {
        MqttItem mqttItem = new MqttItem.Builder(this)
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
    private void subscribeTopics() {
        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.SUBSCRIBE)
                .topic(mMqttTopicText.getText().toString(), MqttQos.AT_LEAST_ONCE)
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new SubscribeMqttCallback());
    }

    /**
     * Unsubscribe topics for device's events.
     */
    private void unsubscribeTopics() {
        if (mMqttTopicText.getText() != null) {
            MqttItem mqttItem = new MqttItem.Builder(this)
                    .actionType(MqttAction.UNSUBSCRIBE)
                    .topic(mMqttTopicText.getText().toString())
                    .build();

            PipelineManager.getInstance().doPipeline(
                    new BaseMqttTaskList(),
                    mqttItem,
                    new UnsubscribeMqttCallback());
        }
    }

    /**
     * Publish a message to Mqtt broker.
     */
    private void publishMqttMessage() {
        // Mock data
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis()/1000)
                .setNanos((int)System.nanoTime()).build();

        EventOuterClass.EventType eventType = EventOuterClass.EventType.forNumber(currentEvent);
        EventOuterClass.Event eosEvent = EventOuterClass.Event.newBuilder()
                .setEvent(eventType)
                .setEventValue(currentEvent)
                .setTimestamp(timestamp)
                .setBatteryPercentage(50)
                .build();

        MqttItem mqttItem = new MqttItem.Builder(this)
                .actionType(MqttAction.PUBLISH_MESSAGE)
                .topic(mMqttTopicText.getText().toString())
                .payload(eosEvent.toByteArray())
                .build();

        PipelineManager.getInstance().doPipeline(
                new BaseMqttTaskList(),
                mqttItem,
                new PublishMqttMessageCallback());
    }

    /**
     * Get user id.
     * @return
     */
    private String getUserId() {
        LoginResponse loginInfo = AccountManagement.getInstance(this).getLoginInfo();
        return loginInfo.getResourceOwnerId();
    }

//    /**
//     * Get Access Token.
//     * @return
//     */
    private String getAccessToken() {
        LoginResponse loginInfo = AccountManagement.getInstance(this).getLoginInfo();
        return loginInfo.getAccessToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PipelineManager.release();
    }

    private class TestEventItem extends APipeItem {
        String id, event_type, child_id, start_date, end_data, data, created_at, updated_at;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

    private String testJson = "[{\"id\":\"104c179e-f965-437c-a83f-0bafcf68ee32\"," +
            "\"event_type\":\"learningPeriod\"," +
            "\"child_id\":\"c4a89fa8-ad63-4927-b377-f1e7e08ad250\"," +
            "\"start_date\":\"2017-03-09T08:39:15Z\"," +
            "\"end_date\":\"2017-03-22T10:00:48.433791Z\"," +
            "\"data\":null," +
            "\"created_at\":\"2017-03-09T08:39:19.820129Z\"," +
            "\"updated_at\":\"2017-03-22T10:00:48.434476Z\"}] ";
}
