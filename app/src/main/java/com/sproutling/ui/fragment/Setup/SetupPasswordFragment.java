/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.object.WifiConnectionEvent;
import com.sproutling.object.WifiItem;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.CustomShInputTextLayout;
import com.sproutling.ui.widget.ShTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by bradylin on 12/7/16.
 */

public class SetupPasswordFragment extends BaseFragment {
    public static final String TAG = "SetupPasswordFragment";

    private static final String ARG_WIFI = "wifi_item";

    private WifiItem mWifiItem;

    private SetPasswordListener mListener;

    private ShTextView mInstructionView, mManualSettingsView;
    private CustomShEditText mPasswordEditText;
    private CustomShInputTextLayout mTextInputLayout;


    public SetupPasswordFragment() {
    }

    public static SetupPasswordFragment newInstance(WifiItem param1) {
        SetupPasswordFragment fragment = new SetupPasswordFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_WIFI, param1);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWifiItem = getArguments().getParcelable(ARG_WIFI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_password, container, false);

        mInstructionView = (ShTextView) view.findViewById(R.id.instruction);
        mManualSettingsView = (ShTextView) view.findViewById(R.id.manual_settings);
        mPasswordEditText = (CustomShEditText) view.findViewById(R.id.password);
        mTextInputLayout = view.findViewById(R.id.passwordWrapper);

        mManualSettingsView.setOnClickListener(mOnManualSettingsClickListener);

        mPasswordEditText.addTextChangedListener(mTextWatcher);
        mPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mInstructionView.setText(String.format(getString(R.string.setup_password_instruction), mWifiItem.getSsid()));
    }

    private View.OnClickListener mOnManualSettingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onManualSetupClick();
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

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            validatePasswordField();
        }
    };

    private void validatePasswordField() {
        if (mPasswordEditText.getText().length() > 0) {
            if (mListener != null) mListener.onActionButtonEnabled(true);
            showError(false);
        } else if (mListener != null) mListener.onActionButtonEnabled(false);
    }

    private void showError(boolean show) {
        if (show) {
            mPasswordEditText.setText("");
            mTextInputLayout.setError(getString(R.string.setup_password_connection_error_msg));
        } else {
            mTextInputLayout.setError(null);
        }
    }

    public String getPassword() {
        String retVal = "";
        if (mPasswordEditText != null) {
            retVal = mPasswordEditText.getText().toString();
        }
        return retVal;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WifiConnectionEvent wifiConnectionEvent) {
        showProgressBar(false);
        if (wifiConnectionEvent.getConnectionStatus() == WifiConnectionEvent.CONNECTED) {
            //TODO:navigate to correct screen
            if (mListener != null) mListener.onHubWifiSetupSuccess();
            Log.d(TAG, "Hub wifi setup success");
        } else {
            Log.d(TAG, "Hub wifi setup failure");
            showError(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnSetPasswordListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnSetPasswordListener();
    }

    private void initOnSetPasswordListener() {
        try {
            mListener = (SetPasswordListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement SetPasswordListener");
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
    }

    public interface SetPasswordListener {
        void onActionButtonEnabled(boolean enabled);

        void onHubWifiSetupSuccess();

        void onManualSetupClick();
    }
}

