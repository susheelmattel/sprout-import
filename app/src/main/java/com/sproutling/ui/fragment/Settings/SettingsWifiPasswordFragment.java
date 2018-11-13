/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.sproutling.R;
import com.sproutling.ui.widget.CustomShEditText;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsWifiPasswordFragment extends Fragment {
    public static final String TAG = "SettingsWifiPasswordFragment";

    private OnSetPasswordListener mListener;

    private CustomShEditText mPasswordEditText;

    public SettingsWifiPasswordFragment() {
    }

    public static SettingsWifiPasswordFragment newInstance() {
        return new SettingsWifiPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_wifi_password, container, false);

        mPasswordEditText = view.findViewById(R.id.password);
        mPasswordEditText.addTextChangedListener(mTextWatcher);
        mPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mPasswordEditText.getText().length() > 0)
                if (mListener != null) mListener.onActionButtonEnabled(true);
                else if (mListener != null) mListener.onActionButtonEnabled(false);
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

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//        }
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
            mListener = (OnSetPasswordListener) getActivity();
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

    public interface OnSetPasswordListener {
        void onActionButtonEnabled(boolean enabled);
    }

}
