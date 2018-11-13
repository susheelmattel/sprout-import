/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.object.BleConnectionEvent;
import com.sproutling.object.WifiItem;
import com.sproutling.object.WifiListEvent;
import com.sproutling.ui.adapter.Setup.SetupWifiFragmentListAdapter;
import com.sproutling.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SetupWiFiFragment extends BaseFragment {
    public static final String TAG = "SetupWiFiFragment";

    private static final String WIFI_LIST = "WIFI_LIST";


    private OnChooseWiFiListener mListener;
    private RecyclerView mWifiListView;

    private ArrayList<WifiItem> mWifiList;
    private SetupWifiFragmentListAdapter mWifiListAdapter;
//    private WifiRefreshFrameLayout mWifiRefreshFrameLayout;
//    private ShTextView mTvNoSsidError;

//    private boolean mRefreshing = false;

    public SetupWiFiFragment() {
    }

    public static SetupWiFiFragment newInstance() {
        return new SetupWiFiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_wi_fi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (savedInstanceState != null) {
//            mWifiList = savedInstanceState.getParcelableArrayList(WIFI_LIST);
//        }
        init(view);
    }

    private void init(View view) {
//        mWifiRefreshFrameLayout = (WifiRefreshFrameLayout) view.findViewById(R.id.refresh_frame);
//        mTvNoSsidError = (ShTextView) view.findViewById(R.id.tv_no_ssid_error);

        mWifiListView = (RecyclerView) view.findViewById(R.id.wifi_list);
        mWifiListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mWifiListAdapter = new SetupWifiFragmentListAdapter(getActivity());
        mWifiListAdapter.setSsidClickListener(new SetupWifiFragmentListAdapter.SsidClickListener() {
            @Override
            public void onClick(WifiItem wifiItem) {
                if (mListener != null) mListener.onWiFiSelected(wifiItem);
            }
        });
        if (mWifiList != null) {
            mWifiListAdapter.setItems(mWifiList);
        }
        mWifiListView.setAdapter(mWifiListAdapter);

//        mWifiRefreshFrameLayout.setPtrHandler(new PtrHandler() {
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mWifiListView, header);
//            }
//
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//
//                mRefreshing = true;
//                mListener.refreshWifiScanOnHub();
//            }
//        });
        //when we come back from password fragment, we don't want to get the wifi list again.
        // By default we will have other network as one item thats why we compare itemcount with 1
        if (mWifiList == null) {
            mListener.startWifiScanOnHub();

            showProgressBar(true);
//            showNoSsidError(false);
        }
//        else if (mWifiList.size() == 0) {
//            showNoSsidError(true);
//        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initChooseWiFiListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WifiListEvent wifiListEvent) {
//        if (mRefreshing) {
//            mWifiListAdapter.clearItems();
//            mWifiRefreshFrameLayout.refreshComplete();
//            mRefreshing = false;
//        }
        mWifiList = wifiListEvent.getWifiList();
        Log.i(TAG, "Wifi List count :" + mWifiList.size());
        mWifiListAdapter.setItems(mWifiList);
        mWifiListAdapter.notifyDataSetChanged();
//        if (wifiListEvent.getWifiList().size() == 0) {
//            showNoSsidError(true);
//        } else {
//            showNoSsidError(false);
//        }
        showProgressBar(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BleConnectionEvent bleConnectionEvent) {
        Log.i(TAG, "Ble Connection state :" + bleConnectionEvent.getConnectionStatus());
        if (bleConnectionEvent.getConnectionStatus() == BleConnectionEvent.DISCONNECTED) {
            showProgressBar(false);
//            if (mRefreshing) {
//                mWifiRefreshFrameLayout.refreshComplete();
//                mRefreshing = false;
//            }
        }
    }

//    private void showNoSsidError(boolean show) {
//        if (show) {
//            mTvNoSsidError.setVisibility(View.VISIBLE);
//        } else {
//            mTvNoSsidError.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initChooseWiFiListener();
    }

    private void initChooseWiFiListener() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public interface OnChooseWiFiListener {
        void startWifiScanOnHub();

        void refreshWifiScanOnHub();

        void onWiFiSelected(WifiItem wifiItem);
    }

}
