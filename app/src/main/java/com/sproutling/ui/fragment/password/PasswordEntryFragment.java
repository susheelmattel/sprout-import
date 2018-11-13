/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.password;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by bradylin on 2/17/17.
 */

public class PasswordEntryFragment extends BaseFragment {
    public static final String TAG = "PasswordEntryFragment";

    private OnPasswordEntryListener mListener;

    private CustomShEditText mPhoneEditText;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mListener != null) mListener.onActionButtonEnabled(TAG, Utils.isPhoneValid(s.toString()));
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

    public PasswordEntryFragment() {
    }

    public static PasswordEntryFragment newInstance() {
        return new PasswordEntryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_enter_phone_number, container, false);
        mPhoneEditText = view.findViewById(R.id.phone);

        mPhoneEditText.addTextChangedListener(mTextWatcher);

        String countryCode = Locale.getDefault().getCountry();
        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(countryCode));
        mPhoneEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    public void validatePhoneNumber() {
        if (Utils.isPhoneValid(mPhoneEditText.getText().toString())) {
            final String phone = Utils.formatPhone(mPhoneEditText.getText().toString(), false);
            new AsyncTask<Void, Void, Boolean>() {
                SSError mError;

                @Override
                protected void onPreExecute() {
                    showProgressBar(true);
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        return SKManagement.requestPin(phone);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SSException e) {
                        e.printStackTrace();
                        mError = e.getError();
                        return e.getResponseCode() == SSException.SUCCESS_CODE_202;
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    showProgressBar(false);
                    if (result != null && result) {
                        if (mListener != null)
                            mListener.onPhoneValidated(Utils.formatPhone(mPhoneEditText.getText().toString(), false));
                    } else {
                        mPhoneEditText.showErrorMsg(true);
                        if (mListener != null) mListener.onActionButtonEnabled(TAG, false);
                    }
                }
            }.execute();
        } else {
            mPhoneEditText.showErrorMsg(true);
            if (mListener != null) mListener.onActionButtonEnabled(TAG, false);
        }

    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnPasswordEntryListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnPasswordEntryListener();
    }

    private void initOnPasswordEntryListener(){
        try {
            mListener = (OnPasswordEntryListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnPasswordEntryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPasswordEntryListener {
        void onActionButtonEnabled(String tag, boolean enabled);
        void onPhoneValidated(String phoneNumber);
    }
}
