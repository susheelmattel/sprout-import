/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsNotificationFragment extends Fragment {
    public static final String TAG = "SettingsNotificationFragment";

    private RelativeLayout mAllAlertsLayout;

    private Switch mAllowSwitch,
            mRedROSwitch,
//            mRedHRSwitch,
//            mRoomTempSwitch, mRoomHumSwitch, mRoomLightSwitch, mRoomNoiseSwitch,
            mSleepWakeSwitch, mSleepAsleepSwitch, //mSleepStirringSwitch,
//            mSystemFellSwitch,
        mSystemOfflineSwitch, mSystemMissingSwitch, mSystemFarSwitch, mSystemBatLowSwitch;
//    , mSystemBatOutSwitch;
    private ShTextView mAllowDesView;

    private OnNotificationListener mListener;

    private SSManagement.PushNotification mSettings;

    public SettingsNotificationFragment() {}

    public static SettingsNotificationFragment newInstance() {
        return new SettingsNotificationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_notification, container, false);
        mAllowSwitch = (Switch) view.findViewById(R.id.allow);
        mRedROSwitch = (Switch) view.findViewById(R.id.red_rollover);
//        mRedHRSwitch = (Switch) view.findViewById(R.id.red_heart_rate);
//        mRoomTempSwitch = (Switch) view.findViewById(R.id.room_temperature);
//        mRoomHumSwitch = (Switch) view.findViewById(R.id.room_humidity);
//        mRoomLightSwitch = (Switch) view.findViewById(R.id.room_light);
//        mRoomNoiseSwitch = (Switch) view.findViewById(R.id.room_noise);
        mSleepWakeSwitch = (Switch) view.findViewById(R.id.sleep_wake);
        mSleepAsleepSwitch = (Switch) view.findViewById(R.id.sleep_asleep);
//        mSleepStirringSwitch = (Switch) view.findViewById(R.id.sleep_stirring);
//        mSystemFellSwitch = (Switch) view.findViewById(R.id.system_wearable_fell);
        mSystemOfflineSwitch = (Switch) view.findViewById(R.id.system_offline);
        mSystemMissingSwitch = (Switch) view.findViewById(R.id.system_missing);
        mSystemFarSwitch = (Switch) view.findViewById(R.id.system_far);
        mSystemBatLowSwitch = (Switch) view.findViewById(R.id.system_battery_low);
//        mSystemBatOutSwitch = (Switch) view.findViewById(R.id.system_battery_out);

        mAllAlertsLayout = (RelativeLayout) view.findViewById(R.id.all_alerts_layout);

        mAllowDesView = (ShTextView) view.findViewById(R.id.allow_description);

        initViews();

        mAllowSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mRedROSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mRedHRSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mRoomTempSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mRoomHumSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mRoomLightSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mRoomNoiseSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSleepWakeSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSleepAsleepSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mSleepStirringSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mSystemFellSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSystemOfflineSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSystemMissingSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSystemFarSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
        mSystemBatLowSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);
//        mSystemBatOutSwitch.setOnCheckedChangeListener(mOnCheckChangedListener);

        setAllowChecked(isEnabled(mSettings.notificationsDisabled));
        return view;
    }

    private void initViews() {
        mSettings = AccountManagement.getInstance(getActivity()).getPushNotificationSettings();
        if (mSettings == null || mSettings.notificationsDisabled == null) {
            mSettings = SSManagement.PushNotification.getDefaultSettings();
            AccountManagement.getInstance(getActivity()).writePushNotificationSettings(mSettings);
        }
        mAllowSwitch.setChecked(isEnabled(mSettings.notificationsDisabled));
        mRedROSwitch.setChecked(isEnabled(mSettings.rolledOverDisabled));
//        mRedHRSwitch.setChecked(isEnabled(mSettings.unusualHeartbeatDisabled));
        mSleepWakeSwitch.setChecked(isEnabled(mSettings.awakeDisabled));
        mSleepAsleepSwitch.setChecked(isEnabled(mSettings.asleepDisabled));
//        mSleepStirringSwitch.setChecked(isEnabled(mSettings.stirringDisabled));
//        mSystemFellSwitch.setChecked(isEnabled(mSettings.wearableFellOffDisabled));
        mSystemOfflineSwitch.setChecked(isEnabled(mSettings.hubOfflineDisabled));
        mSystemMissingSwitch.setChecked(isEnabled(mSettings.wearableNotFoundDisabled));
        mSystemFarSwitch.setChecked(isEnabled(mSettings.wearableTooFarAwayDisabled));
        mSystemBatLowSwitch.setChecked(isEnabled(mSettings.batteryDisabled));
//        mSystemBatOutSwitch.setChecked(isEnabled(mSettings.outOfBatteryDisabled));
    }

    private boolean isEnabled(String enabled) {
        return SSManagement.PushNotification.ENABLED.equals(enabled);
    }

    private String getValue(boolean enabled) {
        return enabled ? SSManagement.PushNotification.ENABLED : SSManagement.PushNotification.DISABLED;
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.allow:
                    mSettings.notificationsDisabled = getValue(isChecked);
                    if (!isChecked){
                        Utils.logEvents(LogEvents.USER_REFUSED_NOTIFICATION_IN_APP);
                    }else {
                        Utils.logEvents(LogEvents.USER_SAID_OK_TO_NOTIFICATION_IN_APP);
                    }
                    setAllowChecked(isChecked);
                    break;
                case R.id.red_rollover:
                    mSettings.rolledOverDisabled = getValue(isChecked);
                    break;
//                case R.id.red_heart_rate:
//                    mSettings.unusualHeartbeatDisabled = getValue(isChecked);
//                    break;
//                case R.id.room_temperature:
//                    break;
//                case R.id.room_humidity:
//                    break;
//                case R.id.room_light:
//                    break;
//                case R.id.room_noise:
//                    break;
                case R.id.sleep_wake:
                    mSettings.awakeDisabled = getValue(isChecked);
                    break;
                case R.id.sleep_asleep:
                    mSettings.asleepDisabled = getValue(isChecked);
                    break;
//                case R.id.sleep_stirring:
//                    mSettings.stirringDisabled = getValue(isChecked);
//                    break;
//                case R.id.system_wearable_fell:
//                    mSettings.wearableFellOffDisabled = getValue(isChecked);
//                    break;
                case R.id.system_offline:
                    mSettings.hubOfflineDisabled = getValue(isChecked);
                    break;
                case R.id.system_missing:
                    mSettings.wearableNotFoundDisabled = getValue(isChecked);
                    break;
                case R.id.system_far:
                    mSettings.wearableTooFarAwayDisabled = getValue(isChecked);
                    break;
                case R.id.system_battery_low:
                    mSettings.batteryDisabled = getValue(isChecked);
                    break;
//                case R.id.system_battery_out:
//                    mSettings.outOfBatteryDisabled = getValue(isChecked);
//                    break;

            }
            if (mListener != null) mListener.onNotificationSwitched(mSettings);
        }
    };

    private void setAllowChecked(boolean isChecked) {
        int visibility = isChecked ? View.VISIBLE : View.GONE;

        mAllowDesView.setText(isChecked ? R.string.settings_notification_notification_allow_on_description : R.string.settings_notification_notification_allow_off_description);

        mAllAlertsLayout.setVisibility(visibility);
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnSettingsNotificationListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnSettingsNotificationListener();
    }

    private void initOnSettingsNotificationListener() {
        try {
            mListener = (OnNotificationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnNotificationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNotificationListener {
        void onNotificationSwitched(SSManagement.PushNotification settings);
    }
}