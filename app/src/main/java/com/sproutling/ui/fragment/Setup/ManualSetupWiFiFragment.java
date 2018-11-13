/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.object.WifiConnectionEvent;
import com.sproutling.object.WifiNetworkTypeEvent;
import com.sproutling.object.WifiParams;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by subram13 on 1/20/17.
 */

public class ManualSetupWiFiFragment extends BaseFragment {
    public static final String TAG = "ManualSetupWiFiFragment";

    private CustomShEditText mEtNetworkName;
    private CustomShEditText mEtNetworkPwd;
    private ShTextView mTvSecurityType;
    private ShTextView mTvErrorMsg;
    private RelativeLayout mSecurityLayout;
    //initialize to default value WPA
    private String mNetworkType = ManualNetworkTypeFragment.WPA;
    private ManualSetupListener mNetworkTypeClickListener;


    public static ManualSetupWiFiFragment newInstance() {
        return new ManualSetupWiFiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_setup_wifi, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mEtNetworkName = view.findViewById(R.id.et_ssid);
        mEtNetworkName.addTextChangedListener(mTextWatcher);
        mEtNetworkName.setOnEditorActionListener(mOnEditorActionListener);
        mEtNetworkPwd = view.findViewById(R.id.et_pwd);
        mEtNetworkPwd.addTextChangedListener(mTextWatcher);
        mEtNetworkPwd.setOnEditorActionListener(mOnEditorActionListener);

        mTvErrorMsg = (ShTextView) view.findViewById(R.id.tv_error_msg);
        mTvSecurityType = (ShTextView) view.findViewById(R.id.tv_network_type);
        mSecurityLayout = (RelativeLayout) view.findViewById(R.id.security_layout);

        mSecurityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateControlValues();
                mNetworkTypeClickListener.onNetworkTypeClick(mNetworkType);
            }
        });
        setSelectedNetworkType(mNetworkType);
        validateControlValues();
        showError(false);
    }

    private void validateControlValues() {
        if (!TextUtils.isEmpty(mEtNetworkName.getText())) {
            if (ManualNetworkTypeFragment.NONE.equalsIgnoreCase(mNetworkType)) {
                mNetworkTypeClickListener.onActionButtonEnabled(true);
            } else {
                if (!TextUtils.isEmpty(mEtNetworkPwd.getText())) {
                    mNetworkTypeClickListener.onActionButtonEnabled(true);
                } else {
                    mNetworkTypeClickListener.onActionButtonEnabled(false);
                }
            }
        } else {
            mNetworkTypeClickListener.onActionButtonEnabled(false);
        }
        //when user starts to type, hide the error
        if (isErrorVisible()) {
            showError(false);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            validateControlValues();
        }
    };

    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            return false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WifiNetworkTypeEvent wifiNetworkTypeEvent) {
        setSelectedNetworkType(wifiNetworkTypeEvent.getNetworkType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WifiConnectionEvent wifiConnectionEvent) {
        if (wifiConnectionEvent.getConnectionStatus() == WifiConnectionEvent.CONNECTED) {
            mNetworkTypeClickListener.onHubWifiSetupSuccess();
            Log.d(TAG, "Hub wifi setup success");
        } else {
            Log.d(TAG, "Hub wifi setup failure");
            showProgressBar(false);
            showError(true);
        }
    }

    private void showError(boolean show) {
        mTvErrorMsg.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean isErrorVisible() {
        return mTvErrorMsg.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initNetworkTypeClickListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initNetworkTypeClickListener();
    }

    private void initNetworkTypeClickListener() {
        try {
            mNetworkTypeClickListener = (ManualSetupListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ManualSetupListener");
        }
    }

    private void setSelectedNetworkType(@ManualNetworkTypeFragment.NetworkSecurityType String networkType) {
        mNetworkType = networkType;
        mTvSecurityType.setText(mNetworkType);
        validateControlValues();
    }

    public WifiParams getWifiParams() {
        return new WifiParams(mEtNetworkName.getText().toString(), mEtNetworkPwd.getText().toString(), mNetworkType);
    }

    public interface ManualSetupListener {
        void onNetworkTypeClick(@ManualNetworkTypeFragment.NetworkSecurityType String networkType);

        void onActionButtonEnabled(boolean enabled);

        void onHubWifiSetupSuccess();
    }
}
