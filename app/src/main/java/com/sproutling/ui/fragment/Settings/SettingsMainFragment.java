/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.object.FragmentItem;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.activity.SetupActivity;
import com.sproutling.ui.adapter.Settings.SettingsListAdapter;
import com.sproutling.ui.fragment.BabyFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;

/**
 * Created by bradylin on 12/12/16.
 */

public class SettingsMainFragment extends Fragment {
    public static final String TAG = "SettingsMainFragment";

    private ShTextView mTvDeviceProf;
    private ShTextView mNoDeviceView, mDeviceName, mAddDeviceView, mVersionView, mTvServerUrl;
    private RelativeLayout mDeviceLayout;
    private SettingsListAdapter mListAdapter;

    private LinearLayout mSettingsOptionsLayout;

    private OnSettingsMainListener mListener;

    private SSManagement.DeviceResponse mDevice;

    public SettingsMainFragment() {
    }

    public static SettingsMainFragment newInstance() {
        return new SettingsMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_main, container, false);

        mTvDeviceProf = (ShTextView) view.findViewById(R.id.tv_device_profile);
        mNoDeviceView = (ShTextView) view.findViewById(R.id.no_device);
        mDeviceName = (ShTextView) view.findViewById(R.id.device_name);
        mAddDeviceView = (ShTextView) view.findViewById(R.id.add_device);
        mVersionView = (ShTextView) view.findViewById(R.id.version);
        mTvServerUrl = (ShTextView) view.findViewById(R.id.tv_server_url);
        mDeviceLayout = (RelativeLayout) view.findViewById(R.id.device);
        mSettingsOptionsLayout = (LinearLayout) view.findViewById(R.id.setting_options_layout);

        mListAdapter = new SettingsListAdapter(getActivity());
        mTvDeviceProf.setOnClickListener(mOnDeviceClickListener);
        mDeviceLayout.setOnClickListener(mOnDeviceClickListener);
        mAddDeviceView.setOnClickListener(mOnAddDeviceClickListener);

        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDevice();
    }

    void init() {
        initDevice();
        initList();
        initVersion();
    }

    private void initDevice() {
        mDevice = SharedPrefManager.getDevice(getActivity());
        if (mDevice != null) mDeviceName.setText(mDevice.getName());
        showDevice(mDevice != null);
    }

    private void initList() {
        List<FragmentItem> list = new ArrayList<>();
        if (hasDevice())
            list.add(new FragmentItem(SettingsNotificationFragment.TAG, getStringRes(R.string.settings_list_item_notification)));
        list.add(new FragmentItem(SettingsAccountFragment.TAG, getStringRes(R.string.settings_list_item_account)));
        list.add(new FragmentItem(BabyFragment.TAG, getStringRes(R.string.settings_list_item_baby)));
        if (hasDevice() && !AccountManagement.getInstance(getActivity()).isTypeCaregiver())
            list.add(new FragmentItem(SettingsCaregiversFragment.TAG, getStringRes(R.string.settings_list_item_caregivers)));
//        if (hasDevice()) list.add(new FragmentItem(SettingsWifiFragment.TAG, getStringRes(R.string.settings_list_item_wifi)));
        if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR))
            list.add(new FragmentItem(SettingsHelpFragment.TAG, getStringRes(R.string.settings_list_item_help)));
        if (hasDevice())
            list.add(new FragmentItem(SettingsWalkThroughFragment.TAG, getStringRes(R.string.settings_list_item_walkthrough)));

        int optionsSize = list.size();
        for (int i = 0; i < optionsSize; i++) {
            final FragmentItem option = list.get(i);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_activity_settings_main, null);
            ShTextView tvOption = (ShTextView) view.findViewById(R.id.name);
            LinearLayout divider = (LinearLayout) view.findViewById(R.id.divider);
            divider.setVisibility(i == optionsSize - 1 ? View.INVISIBLE : View.VISIBLE);
            tvOption.setText(option.getName());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.logEvents(LogEvents.SETTINGS_SELECTED);
                    if (mListener != null) mListener.onFragmentSelected(option);
                }
            });
            mSettingsOptionsLayout.addView(view);
        }
    }


    private boolean hasDevice() {
        return mDevice != null;
    }

    private String getStringRes(int id) {
        return getResources().getString(id);
    }

    private void initVersion() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = String.format(
                    getResources().getString(R.string.settings_version), pInfo.versionName);

            mVersionView.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            mTvServerUrl.setText(String.format(getString(R.string.server_url_mqtt_url),
                    SharedPrefManager.getServerUrl(getActivity()), SharedPrefManager.getMqttUrl(getActivity())));
        }
        mTvServerUrl.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.INVISIBLE);

    }

    public void refresh() {
        init();
    }

    private View.OnClickListener mOnDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onFragmentSelected(new FragmentItem(SettingsDeviceSettingsFragment.TAG, getStringRes(R.string.settings_device_settings_title)));
        }
    };

    private View.OnClickListener mOnAddDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SetupActivity.class);
            intent.putExtra(SetupActivity.EXTRA_SETUP_DEVICE, true);
            intent.putExtra(SetupActivity.EXTRA_SETUP_SKIP, true);
            intent.putExtra(SetupActivity.EXTRA_SETTING_SETUP_NEW_DEVICE, true);
//            intent.putExtra(SetupActivity.SETTING_TYPE, SetupActivity.SETTING_TYPE_CHANGE);
            startActivity(intent);
//            getActivity().finish();
        }
    };

    private void showDevice(boolean hasDevice) {
        if (hasDevice) {
            mDeviceLayout.setVisibility(View.VISIBLE);
            mNoDeviceView.setVisibility(View.GONE);
            mAddDeviceView.setVisibility(View.GONE);
        } else {
            mDeviceLayout.setVisibility(View.GONE);
            mNoDeviceView.setVisibility(View.VISIBLE);
            mAddDeviceView.setVisibility(View.VISIBLE);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnSettingsMainListener();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnSettingsMainListener();
    }

    private void initOnSettingsMainListener() {
        try {
            mListener = (OnSettingsMainListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnSettingsMainListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSettingsMainListener {
        void onFragmentSelected(FragmentItem item);
    }
}