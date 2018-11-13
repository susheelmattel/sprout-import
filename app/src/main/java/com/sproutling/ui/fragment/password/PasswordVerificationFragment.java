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
import com.sproutling.ui.dialogfragment.CodeResentDialogFragment;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by bradylin on 2/17/17.
 */

public class PasswordVerificationFragment extends BaseFragment {
    public static final String TAG = "PasswordVerificationFragment";

    public static final String EXTRA_PHONE = "phone";

    private OnPasswordVerifyListener mListener;

    private CustomShEditText mCodeEditText;
    private ShTextView mInstructionView, mResendView;

    private String mPhoneNumber;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mCodeEditText.showErrorMsg(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mListener != null) mListener.onActionButtonEnabled(TAG, s.length() == 5);
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
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CodeResentDialogFragment
                    .newInstance()
                    .show(getFragmentManager(), null);
        }
    };

    public PasswordVerificationFragment() {
    }

    public static PasswordVerificationFragment newInstance(String phoneNumber) {
        PasswordVerificationFragment fragment = new PasswordVerificationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_PHONE, phoneNumber);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPhoneNumber = arguments.getString(EXTRA_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password_verification, container, false);
        mCodeEditText =  view.findViewById(R.id.code);
        mInstructionView = (ShTextView) view.findViewById(R.id.instruction);
//        mErrorView = (ShTextView) view.findViewById(R.id.code_error);
        mResendView = (ShTextView) view.findViewById(R.id.resend);

        mCodeEditText.addTextChangedListener(mTextWatcher);
        mCodeEditText.setOnEditorActionListener(mOnEditorActionListener);

        mResendView.setOnClickListener(mOnClickListener);

        mInstructionView.setText(
                String.format(
                        getResources().getString(R.string.forgot_verification_instruction), mPhoneNumber)
        );

        return view;
    }

    public void sendCode() {
        final String pin = mCodeEditText.getText().toString();

        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return SKManagement.validatePin(Utils.formatPhone(mPhoneNumber, false), pin);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                showProgressBar(false);
                if (result != null && result) {
                    if (mListener != null) mListener.onCodeAccepted(true, pin);
                } else {
                    if (mListener != null) mListener.onCodeAccepted(false, pin);
                    mCodeEditText.showErrorMsg(true);
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
        initOnPasswordVerifyListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnPasswordVerifyListener();
    }

    private void initOnPasswordVerifyListener(){
        try {
            mListener = (OnPasswordVerifyListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnPasswordVerifyListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPasswordVerifyListener {
        void onActionButtonEnabled(String tag, boolean enabled);
        void onCodeAccepted(boolean accepted, String pin);
    }
}
