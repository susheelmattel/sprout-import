/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sproutling.R;
import com.sproutling.ui.adapter.Settings.SettingsWifiFragmentListAdapter;
import com.sproutling.ui.widget.AnimationWifiView;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsWifiFragment extends Fragment {
    public static final String TAG = "SettingsWifiFragment";

    private OnChooseWiFiListener mListener;
    private ListView mWifiListView;
    private SettingsWifiFragmentListAdapter mWifiListAdapter;

    public SettingsWifiFragment() {}

    public static SettingsWifiFragment newInstance() {
        return new SettingsWifiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_wifi, container, false);

        mWifiListView = (ListView) view.findViewById(R.id.wifi_list);
        mWifiListView.setOnItemClickListener(mOnWifiListViewItemClickListener);

        mWifiListAdapter = new SettingsWifiFragmentListAdapter(getActivity());
        mWifiListView.setAdapter(mWifiListAdapter);

        AnimationWifiView wifiView = new AnimationWifiView(view);
        wifiView.start();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnChooseWiFiListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnChooseWiFiListener();
    }

    private void initOnChooseWiFiListener() {
        try {
            mListener = (OnChooseWiFiListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnChooseWiFiListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnChooseWiFiListener {
        void onWiFiSelected(ScanResult scanResult);
    }

    private AdapterView.OnItemClickListener mOnWifiListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScanResult result = (ScanResult) mWifiListAdapter.getItem(position);
            if (mListener != null) mListener.onWiFiSelected(result);
        }
    };
}

