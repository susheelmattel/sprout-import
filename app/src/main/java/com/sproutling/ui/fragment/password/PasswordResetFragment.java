/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.password;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by bradylin on 2/17/17.
 */

public class PasswordResetFragment extends BaseFragment {
    public static final String TAG = "PasswordResetFragment";

    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_PIN = "pin";

    private static final int PASSWORD_MAX = 72;
    private static final int PASSWORD_MIN = 8;
    private static final int STATE_PASS = 1;
    private static final int STATE_FAIL1 = 0;
    private static final int STATE_FAIL2 = -1;
    private static final int STATE_FAIL3 = -2;

    private OnPasswordResetListener mListener;

    private CustomShEditText mPasswordEditText, mConfirmEditText;

    private int mPasswordState;

    private String mPhoneNumber, mPin;
    private TextWatcher mPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mPasswordEditText.showErrorMsg(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkPassword();
//            if (mListener != null) mListener.onActionButtonEnabled(TAG, s.length() > 0 ? true : false);

        }
    };
    private TextWatcher mConfirmTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mPasswordState == STATE_PASS) {
                if (mListener != null)
                    mListener.onActionButtonEnabled(TAG, s.length() != 0 && s.toString().equals(mPasswordEditText.getText().toString()));
            }

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

    public PasswordResetFragment() {
    }

    public static PasswordResetFragment newInstance(String phoneNumber, String pin) {
        PasswordResetFragment fragment = new PasswordResetFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_PHONE, phoneNumber);
        arguments.putString(EXTRA_PIN, pin);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPhoneNumber = arguments.getString(EXTRA_PHONE);
            mPin = arguments.getString(EXTRA_PIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_new_password, container, false);
        mPasswordEditText = view.findViewById(R.id.password);
        mConfirmEditText = view.findViewById(R.id.confirm_password);

        mPasswordEditText.addTextChangedListener(mPasswordTextWatcher);
        mConfirmEditText.addTextChangedListener(mConfirmTextWatcher);

        mPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);
        mConfirmEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    private void checkPassword() {
        int length = mPasswordEditText.getText().length();
        if (length < PASSWORD_MIN) {
            mPasswordState = STATE_FAIL2;
        } else if (length > PASSWORD_MAX) {
            mPasswordState = STATE_FAIL1;
        } else {
            mPasswordState = STATE_PASS;
        }
    }

    public void updatePassword() {
        final String password = mPasswordEditText.getText().toString();
        final String passwordConfirmation = mConfirmEditText.getText().toString();

        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return SKManagement.resetPassword(mPhoneNumber, mPin, password, passwordConfirmation);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                showProgressBar(false);
                if (result != null && result) {
                    if (mListener != null) mListener.onPasswordUpdated(true);
                } else {
                    if (mListener != null) mListener.onPasswordUpdated(false);
                    mPasswordEditText.showErrorMsg(true);
//                    if (mError != null) handleError(mError);
                }
            }
        }.execute();
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnPasswordResetListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnPasswordResetListener();
    }

    private void initOnPasswordResetListener(){
        try {
            mListener = (OnPasswordResetListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnPasswordResetListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPasswordResetListener {
        void onActionButtonEnabled(String tag, boolean enabled);
        void onPasswordUpdated(boolean updated);
    }
}
