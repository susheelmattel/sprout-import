/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.fuhu.pipeline.manager.PipelineManager;
import com.fuhu.pipeline.mqtt.MqttAction;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.object.HubBandStatusTopicEvent;
import com.sproutling.object.HubStatusTopicEvent;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.pipeline.callback.PublishMqttMessageCallback;
import com.sproutling.pipeline.taskList.BaseMqttTaskList;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.RemoveDeviceDialogFragment;
import com.sproutling.ui.fragment.BaseMqttFragment;
import com.sproutling.ui.widget.BatteryDisplay;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import sproutling.Hub;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bradylin on 2/7/17.
 */

public class SettingsDeviceSettingsFragment extends BaseMqttFragment {
    public static final String TAG = "SettingsDveProfFgnt";

    private static final int REQUEST_CODE_REMOVE_DEVICE = 1;

    private CustomShEditText mDeviceEditText;

    private ShTextView mFirmwareChargerStatus, mFirmwareWearableStatus;
    private BatteryDisplay mBatteryView;
    //    private ShTextView mBatteryLevelStatus;
    private ImageView mImgBleStrength;
    private ImageView mImgWifiStrength;
    private ProgressBar mWearableFirmwarePBar;
    private ProgressBar mChargerFirmwarePBar;
    private OnDeviceSettingsListener mListener;
    private Switch mFarSwitch;
    private Switch mOfflineSwitch;

    private SSManagement.PushNotification mSettings;
    private SSManagement.DeviceResponse mDeviceResponse;
    private Hub.HubUserConfiguration mHubUserConfiguration;

    public SettingsDeviceSettingsFragment() {
    }

    public static SettingsDeviceSettingsFragment newInstance() {
        return new SettingsDeviceSettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_device_settings, container, false);

        mDeviceEditText = view.findViewById(R.id.device_name);
//        mDeviceNameErrorView = (ShTextView) view.findViewById(R.id.device_name_error);
        mFirmwareChargerStatus = (ShTextView) view.findViewById(R.id.firmware_charger_status);
        mFirmwareWearableStatus = (ShTextView) view.findViewById(R.id.firmware_wearable_status);
        mBatteryView = (BatteryDisplay) view.findViewById(R.id.battery_level_image);
        mChargerFirmwarePBar = (ProgressBar) view.findViewById(R.id.firmware_charger_progressbar);
        mWearableFirmwarePBar = (ProgressBar) view.findViewById(R.id.firmware_wearable_progressbar);
        mImgBleStrength = (ImageView) view.findViewById(R.id.img_ble_strength);
        mImgWifiStrength = (ImageView) view.findViewById(R.id.img_wifi_strength);
        view.findViewById(R.id.remove).setOnClickListener(mOnRemoveClickListener);
        view.findViewById(R.id.change_wifi_layout).setOnClickListener(mOnChangeWifiClickListener);
        mDeviceEditText.addTextChangedListener(mTextWatcher);
        mDeviceEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mDeviceEditText.setOnEditorActionListener(mOnEditorActionListener);

        mFarSwitch = (Switch) view.findViewById(R.id.light_alerts_far);
        mOfflineSwitch = (Switch) view.findViewById(R.id.light_alerts_offline);
        initSwitch();
        mFarSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mOfflineSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);

        view.findViewById(R.id.layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                updateHubName();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resetUIToDefault();
    }

    private void resetUIToDefault() {
        mDeviceResponse = SharedPrefManager.getDevice(getActivity());
        mDeviceEditText.setText(mDeviceResponse != null ? mDeviceResponse.getName() : "");
        mDeviceEditText.requestFocus();
        mBatteryView.setUnknownState();
        mImgBleStrength.setImageResource(R.drawable.ic_bluetooth_signal_unknown);
        mFirmwareWearableStatus.setText(getString(R.string.unknown));
        mImgWifiStrength.setImageResource(R.drawable.ic_wifi_signal_unknown);
        mFirmwareChargerStatus.setText(getString(R.string.unknown));
        showChargerFirmwareProgress(false);
        showWearableFirmwareProgress(false);
    }

    private void initSwitch() {
        mSettings = AccountManagement.getInstance(getActivity()).getPushNotificationSettings();
        if (mSettings == null || mSettings.notificationsDisabled == null) {
            mSettings = SSManagement.PushNotification.getDefaultSettings();
            AccountManagement.getInstance(getActivity()).writePushNotificationSettings(mSettings);
        }

        Hub.HubUserConfiguration hubUserConfiguration = App.getInstance().getHubUserConfiguration();
        if (hubUserConfiguration != null) {
            mHubUserConfiguration = hubUserConfiguration;
            mFarSwitch.setChecked(hubUserConfiguration.getBleAlert());
            mOfflineSwitch.setChecked(hubUserConfiguration.getWifiAlert());
        }
//        enableSwitches(false);
    }

    private void enableSwitches(boolean enable) {
        mFarSwitch.setEnabled(enable);
        mOfflineSwitch.setEnabled(enable);
    }

    private boolean isEnabled(String enabled) {
        return SSManagement.PushNotification.ENABLED.equals(enabled);
    }

    private String getValue(boolean enabled) {
        return enabled ? SSManagement.PushNotification.ENABLED : SSManagement.PushNotification.DISABLED;
    }

    private void showBattery(boolean show) {
        mBatteryView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showChargerFirmwareProgress(boolean show) {
        mChargerFirmwarePBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showWearableFirmwareProgress(boolean show) {
        mWearableFirmwarePBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        connectMqtt();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        disconnectMqtt();
    }

    @Override
    protected void onHubStatusTopicEvent(HubStatusTopicEvent hubStatusTopicEvent) {
        super.onHubStatusTopicEvent(hubStatusTopicEvent);
        updateHubStatus(hubStatusTopicEvent.getHubStatus());
    }

    @Override
    protected void onHubBandStatusTopicEvent(HubBandStatusTopicEvent hubBandStatusTopicEvent) {
        super.onHubBandStatusTopicEvent(hubBandStatusTopicEvent);
        updateWearableStatus(hubBandStatusTopicEvent.getBandStatus());
    }

    @Override
    protected void onHubUserConfiguration(Hub.HubUserConfiguration hubUserConfiguration) {
        super.onHubUserConfiguration(hubUserConfiguration);
        mHubUserConfiguration = hubUserConfiguration;
        App.getInstance().setHubUserConfiguration(hubUserConfiguration);
//        updateAlerts(hubUserConfiguration);
    }

    private void updateHubStatus(Hub.HubStatus hubStatus) {
        Drawable imgRes = hubStatus.getWifiConnection().getConnected() ? Utils.getWifiStrengthImgResource(hubStatus.getWifiConnection().getSignal())
                : ContextCompat.getDrawable(getActivity(), R.drawable.ic_wifi_signal_disconnected);
        mImgWifiStrength.setImageDrawable(imgRes);

        Hub.BLEConnection bleConnection = hubStatus.getBleActiveConnectionsCount() > 0 ? hubStatus.getBleActiveConnections(0) : null;
        if (bleConnection != null) {
            mImgBleStrength.setImageDrawable(Utils.getBleStrengthImgResource(bleConnection.getRssi()));
        } else {
            mImgBleStrength.setImageResource(R.drawable.ic_bluetooth_signal_disconnected);
        }

        if (hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.HUB_FLASH_START) {
            mFirmwareChargerStatus.setText(getString(R.string.settings_device_settings_firmware_updating));
            showChargerFirmwareProgress(true);
        } else {
            mFirmwareChargerStatus.setText(Utils.getVersion(hubStatus.getFirmwareVersion()));
            showChargerFirmwareProgress(false);
        }

        if (hubStatus.getFirmwareUpdateState() == Hub.FirmwareUpdateState.WEARABLE_FLASH_START) {
            mFirmwareWearableStatus.setText(getString(R.string.settings_device_settings_firmware_updating));
            showWearableFirmwareProgress(true);
        } else {
//            mFirmwareWearableStatus.setText(Utils.getVersion(hubStatus.getFirmwareVersion()));
            showWearableFirmwareProgress(false);
        }


    }

    private void updateWearableStatus(Hub.BandStatus bandStatus) {
        mBatteryView.setBatteryDisplay(Utils.getBatteryValue(bandStatus.getBatteryVoltage()));
        mFirmwareWearableStatus.setText(Utils.getVersion(bandStatus.getFirmwareVersion()));
    }

    private void updateAlerts(Hub.HubUserConfiguration hubUserConfiguration) {
        if (!mOfflineSwitch.isEnabled() && !mFarSwitch.isEnabled()) {
            enableSwitches(true);
        }
        mFarSwitch.setChecked(hubUserConfiguration.getBleAlert());
        Log.d(TAG, "FarSwitch : " + hubUserConfiguration.getBleAlert());
        mOfflineSwitch.setChecked(hubUserConfiguration.getWifiAlert());
        Log.d(TAG, "OfflineSwitch : " + hubUserConfiguration.getWifiAlert());

    }

    private CompoundButton.OnCheckedChangeListener mOnCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.light_alerts_far:
                    logBandTooFarSettings(isChecked);
                    break;
                case R.id.light_alerts_offline:
                    logHubOfflineSettings(isChecked);
                    break;
            }

        }
    };

    private void logBandTooFarSettings(boolean isChecked) {
        checkValueChanges();
        if (mHubUserConfiguration != null && mHubUserConfiguration.getBleAlert() != isChecked)
            Utils.logEvents(LogEvents.SETTINGS_BLE_ALERT_UPDATE);
    }

    private void logHubOfflineSettings(boolean isChecked) {
        checkValueChanges();
        if (mHubUserConfiguration != null && mHubUserConfiguration.getWifiAlert() != isChecked) {
            Utils.logEvents(LogEvents.SETTINGS_WIFI_ALERT_UPDATE);
        }
    }

    private void publishUserConfigToMqtt() {
        //publish userconfig only if values changed by the user
        if (mHubUserConfiguration != null && (mHubUserConfiguration.getWifiAlert() != mOfflineSwitch.isChecked()
                || mHubUserConfiguration.getBleAlert() != mFarSwitch.isChecked())) {
            Hub.HubUserConfiguration hubUserConfiguration = Hub.HubUserConfiguration.newBuilder()
                    .setBleAlert(mFarSwitch.isChecked())
                    .setWifiAlert(mOfflineSwitch.isChecked())
                    .setTimestamp(mHubUserConfiguration.getTimestamp())
                    .setTimezone(mHubUserConfiguration.getTimezone()).build();
            MqttItem mqttItem = new MqttItem.Builder(getActivity()).actionType(MqttAction.PUBLISH_MESSAGE)
                    .topic(MqttAPI.getMqttUserConfig(mDeviceResponse.getSerial()))
                    .payload(hubUserConfiguration.toByteArray())
                    .setRetained(true)
                    .build();
            PipelineManager.getInstance().doPipeline(new BaseMqttTaskList(), mqttItem, new PublishMqttMessageCallback());
        }
    }

    private View.OnClickListener mOnChangeWifiClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onChangeWifiSelected();
        }
    };

    private View.OnClickListener mOnRemoveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RemoveDeviceDialogFragment dialogFragment = RemoveDeviceDialogFragment.newInstance();
            dialogFragment.setTargetFragment(SettingsDeviceSettingsFragment.this, REQUEST_CODE_REMOVE_DEVICE);
            dialogFragment.show(getFragmentManager(), null);
        }
    };

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                updateHubName();
            }
        }
    };

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard();
                updateHubName();
            }
            return true;
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0 && !s.toString().equals(mDeviceResponse.getName())) {
                mDeviceEditText.showErrorMsg(false);
            } else if (s.length() == 0) {
                mDeviceEditText.showErrorMsg(true);
            } else {
                mDeviceEditText.showErrorMsg(false);
            }
//            checkValueChanges();
        }
    };

    private void checkValueChanges() {
        if (mHubUserConfiguration != null && (mHubUserConfiguration.getWifiAlert() != mOfflineSwitch.isChecked()
                || mHubUserConfiguration.getBleAlert() != mFarSwitch.isChecked())) {
//            if (mListener != null) mListener.onActionButtonEnabled(TAG, true);
            updateDeviceSettings();
        }
//        else {
//            if (mListener != null) mListener.onActionButtonEnabled(TAG, false);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_REMOVE_DEVICE:
                if (resultCode == RESULT_OK) {
                    removeDevice(AccountManagement.getInstance(getActivity()).getAccessToken());
                }
                break;
            default:
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initListener();
    }

    private void initListener() {
        try {
            mListener = (OnDeviceSettingsListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnDeviceSettingsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected ArrayList<String> getTopicsToSubscribe() {
        ArrayList<String> topics = new ArrayList<>();
        topics.add(MqttAPI.getStatusTopic(mDeviceResponse.getSerial()));
        topics.add(MqttAPI.getMqttBandStatusTopic(mDeviceResponse.getSerial()));
        topics.add(MqttAPI.getMqttUserConfig(mDeviceResponse.getSerial()));
        return topics;
    }

    public void updateDeviceSettings() {
//        mListener.onActionButtonEnabled(TAG, false);

        new AsyncTask<Boolean, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                showProgressBar(true);
            }

            @Override
            protected Void doInBackground(Boolean... params) {
                publishUserConfigToMqtt();
                //delay here because we don't want to disconnect mqtt before the message is sent
                SystemClock.sleep(2000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                showProgressBar(false);
//                if ((mDeviceEditText.getText().length() > 0 && !mDeviceEditText.getText().equals(mDeviceResponse.getName()))) {
//                    updateHubName();
//                } else {
//                    if (mListener != null) mListener.onHubSettingsUpdated();
//                }
            }
        }.execute();
    }

    private void updateHubName() {
        String deviceName = mDeviceEditText.getText().toString();
        if (!TextUtils.isEmpty(deviceName) && !mDeviceEditText.getText().toString().equals(mDeviceResponse.getName())) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", deviceName);
                jsonObject.put("owner_id", mDeviceResponse.getOwnerId());
                jsonObject.put("owner_type", "Child");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final Activity context = getActivity();
            final String accessToken = AccountManagement.getInstance(context).getAccessToken();
            Log.d(TAG, "Device Serial ID :" + mDeviceResponse.getSerial());

            new AsyncTask<Void, Void, SSManagement.DeviceResponse>() {
                private SSError mError;

                @Override
                protected SSManagement.DeviceResponse doInBackground(Void... voids) {
                    try {
                        return SKManagement.updateDevice(accessToken, mDeviceResponse.getSerial(), jsonObject);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } catch (SSException e) {
                        mError = e.getError();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    hideKeyboard();
                    showProgressBar(true);
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(SSManagement.DeviceResponse deviceResponse) {
                    showProgressBar(false);
                    if (deviceResponse != null) {
                        SharedPrefManager.saveDevice(context, deviceResponse);
                        mDeviceResponse = deviceResponse;
                        Log.d(TAG, "Device updated Successfully");
//                        if (mListener != null) mListener.onHubSettingsUpdated();
                    }
                    if (mError != null) {
                        Log.d(TAG, "Device update failed :" + mError.message);
                    }

                }
            }.execute();
        }
    }

    private void removeDevice(final String accessToken) {
        new AsyncTask<Void, Void, Boolean>() {
            private SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return SKManagement.deleteDeviceBySerial(accessToken, mDeviceResponse.getSerial());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mError = e.getError();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean removed) {
                showProgressBar(false);
                if (!removed) {
                    Log.d(TAG, "Failed to remove device");
                }
                if (mListener != null) mListener.onDeviceRemoved(removed);
            }
        }.execute();
    }

    public interface OnDeviceSettingsListener {
        void onActionButtonEnabled(String tag, boolean enabled);

        void onDeviceRemoved(boolean removed);

        void onHubSettingsUpdated();

//        void onAlertSwitched(SSManagement.PushNotification settings);

        void onChangeWifiSelected();
    }
}