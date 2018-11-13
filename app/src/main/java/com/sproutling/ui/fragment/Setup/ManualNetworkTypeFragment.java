/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.ui.adapter.Setup.WifiNetworkTypeAdapter;
import com.sproutling.ui.fragment.BaseFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by subram13 on 1/23/17.
 */

public class ManualNetworkTypeFragment extends BaseFragment {

    public static final String NONE = "None";
    public static final String WEP = "WEP";
    public static final String WPA = "WPA";
    public static final String WPA2 = "WPA2";
    public static final String WPA_ENTERPRISE = "WPA Enterprise";
    public static final String WPA2_ENTERPRISE = "WPA2 Enterprise";
    private static final String SELECTED_NETWORK_TYPE = "SELECTED_NETWORK_TYPE";

    @StringDef({NONE, WPA, WPA2, WPA_ENTERPRISE, WPA2_ENTERPRISE, WEP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NetworkSecurityType {

    }

    private RecyclerView mRecyclerView;
    private String mSelectedNetworkType;
    private WifiNetworkTypeAdapter mWifiNetworkTypeAdapter;
    private NetworkTypeSelectListener mNetworkTypeSelectListener;

    public static ManualNetworkTypeFragment newInstance(@NetworkSecurityType String selectedNetworkType) {
        ManualNetworkTypeFragment fragment = new ManualNetworkTypeFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SELECTED_NETWORK_TYPE, selectedNetworkType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSelectedNetworkType = getArguments().getString(SELECTED_NETWORK_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_vw_network_type);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWifiNetworkTypeAdapter = new WifiNetworkTypeAdapter(getActivity(), getNetworkTypes());
        mWifiNetworkTypeAdapter.setSelectedNetworkType(mSelectedNetworkType);
        mWifiNetworkTypeAdapter.setNetworkTypeClickListener(new WifiNetworkTypeAdapter.NetworkTypeClickListener() {
            @Override
            public void onClick(@NetworkSecurityType String networkType) {
                mNetworkTypeSelectListener.onNetworkTypeSelect(networkType);
            }
        });

        mRecyclerView.setAdapter(mWifiNetworkTypeAdapter);
    }

    private ArrayList<String> getNetworkTypes() {
        ArrayList<String> networkTypes = new ArrayList<>();
        networkTypes.add(NONE);
//        networkTypes.add(WEP);
        networkTypes.add(WPA);
        networkTypes.add(WPA2);
        networkTypes.add(WPA_ENTERPRISE);
        networkTypes.add(WPA2_ENTERPRISE);
        return networkTypes;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initNetworkTypeSelectListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initNetworkTypeSelectListener();
    }

    private void initNetworkTypeSelectListener() {
        try {
            mNetworkTypeSelectListener = (NetworkTypeSelectListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement NetworkTypeSelectListener");
        }
    }

    public interface NetworkTypeSelectListener {
        void onNetworkTypeSelect(@NetworkSecurityType String networkType);
    }
}
