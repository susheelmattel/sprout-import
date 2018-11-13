/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

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
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsPasswordFragment extends BaseFragment {
    public static final String TAG = "SettingsPasswordFragment";

    private static final int PASSWORD_MAX = 72;
    private static final int PASSWORD_MIN = 8;
    private static final int STATE_PASS = 1;
    private static final int STATE_FAIL1 = 0;
    private static final int STATE_FAIL2 = -1;
    private static final int STATE_FAIL3 = -2;

    private OnChangePasswordListener mListener;

    private CustomShEditText mOldPasswordEditText, mNewPasswordEditText, mRetypePasswordEditText;
    //    ShTextView mOldPasswordErrorView, mNewPasswordErrorView, mRetypePasswordErrorView;
    private int mOldPasswordState, mPasswordState, mRePasswordState;

    public SettingsPasswordFragment() {
    }

    public static SettingsPasswordFragment newInstance() {
        return new SettingsPasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_password, container, false);

        mOldPasswordEditText = view.findViewById(R.id.old_password);
        mNewPasswordEditText = view.findViewById(R.id.new_password);
        mRetypePasswordEditText = view.findViewById(R.id.retype_password);

        mOldPasswordEditText.addTextChangedListener(mOldPasswordWatcher);
        mNewPasswordEditText.addTextChangedListener(mNewPasswordWatcher);
        mRetypePasswordEditText.addTextChangedListener(mRetypePasswordWatcher);

        mRetypePasswordEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        mOldPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);
        mNewPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);
        mRetypePasswordEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    private TextWatcher mOldPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() < PASSWORD_MIN) {
                mOldPasswordState = STATE_FAIL1;
            } else if (s.length() > PASSWORD_MAX) {
                mOldPasswordState = STATE_FAIL2;
            } else if (s.length() != 0 &&
                    s.toString().equals(AccountManagement.getInstance(getActivity()).readPassword())) {
                mOldPasswordState = STATE_PASS;
            }
            enableIfReady();
        }
    };

    private TextWatcher mNewPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() < PASSWORD_MIN) {
                mPasswordState = STATE_FAIL1;
                mNewPasswordEditText.setError(getString(R.string.settings_change_password_error_short));
                mNewPasswordEditText.showErrorMsg(true);
            } else if (s.length() > PASSWORD_MAX) {
                mPasswordState = STATE_FAIL2;
                mNewPasswordEditText.setError(getString(R.string.settings_change_password_error_long));
                mNewPasswordEditText.showErrorMsg(true);
            } else {
                mPasswordState = STATE_PASS;
                mNewPasswordEditText.showErrorMsg(false);
            }
            enableIfReady();
        }
    };

    private TextWatcher mRetypePasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String password = mNewPasswordEditText.getText().toString();
            String rePassword = s.toString();
            mRePasswordState = !rePassword.isEmpty() && rePassword.equals(password) ? STATE_PASS : STATE_FAIL1;
            enableIfReady();
        }
    };

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                switch (v.getId()) {
                    case R.id.retype_password:
                        if (mRePasswordState == STATE_PASS) {
                            mRetypePasswordEditText.showErrorMsg(false);
                        } else {
                            mRetypePasswordEditText.setError(getString(R.string.settings_change_password_error_not_match));
                            mRetypePasswordEditText.showErrorMsg(true);
                        }
                        break;
                }
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

    private void enableIfReady() {
        if (mListener != null)
            mListener.onActionButtonEnabled(TAG, mOldPasswordState == STATE_PASS &&
                    mPasswordState == STATE_PASS &&
                    mRePasswordState == STATE_PASS);
    }

    public void changePassword() {
        final String password = mNewPasswordEditText.getText().toString();
        final String passwordConfirmation = mRetypePasswordEditText.getText().toString();

        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                    SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(getActivity()).getUserAccountInfo();
                    return SKManagement.updateUserById(account.accessToken, userAccountInfo.id, getUpdatedUserJSON(userAccountInfo, password, passwordConfirmation));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            private JSONObject getUpdatedUserJSON(SSManagement.UserAccountInfo userAccountInfo, String password, String passwordConfirmation) throws JSONException {
                return new JSONObject().put("email", userAccountInfo.email)
                        .put("first_name", userAccountInfo.firstName)
                        .put("last_name", userAccountInfo.lastName)
                        .put("phone_number", userAccountInfo.phone)
                        .put("invite_token", userAccountInfo.inviteToken)
                        .put("password", password)
                        .put("password_confirmation", passwordConfirmation);
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
                showProgressBar(false);
                if (userAccountInfo != null) {
                    AccountManagement.getInstance(getActivity()).writeUserAccountInfo(userAccountInfo);
                    if (mListener != null) mListener.onPasswordChanged(true);
                } else {
                    if (mError != null) {
                        if (mListener != null) mListener.onPasswordChanged(false);
//                        handleError(mError, R.string.settings_baby_update_error_message_body);
                    }

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
        initOnChangePasswordListener();
    }

    private void initOnChangePasswordListener() {
        try {
            mListener = (OnChangePasswordListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnChangePasswordListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnChangePasswordListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnChangePasswordListener {
        void onActionButtonEnabled(String tag, boolean enabled);

        void onPasswordChanged(boolean changed);
    }
}


