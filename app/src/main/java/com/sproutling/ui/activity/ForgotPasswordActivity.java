/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sproutling.R;
import com.sproutling.ui.dialogfragment.InternetOfflineDialogFragment;
import com.sproutling.ui.fragment.password.PasswordEntryFragment;
import com.sproutling.ui.fragment.password.PasswordEntryFragment.OnPasswordEntryListener;
import com.sproutling.ui.fragment.password.PasswordResetFragment;
import com.sproutling.ui.fragment.password.PasswordResetFragment.OnPasswordResetListener;
import com.sproutling.ui.fragment.password.PasswordVerificationFragment;
import com.sproutling.ui.fragment.password.PasswordVerificationFragment.OnPasswordVerifyListener;
import com.sproutling.ui.widget.ShTextView;

import java.util.Stack;

/**
 * Created by bradylin on 2/16/17.
 */

public class ForgotPasswordActivity extends BaseActivity implements OnPasswordEntryListener, OnPasswordResetListener, OnPasswordVerifyListener {

    private ShTextView mTitleView, mActionView;
    private ImageView mBackView;

    private Stack<String> mFragmentStack;
    private boolean isBack;
    private String mFragmentTag;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mFragmentStack = new Stack<>();

        mTitleView = (ShTextView) findViewById(R.id.navigation_title);
        mBackView = (ImageView) findViewById(R.id.navigation_back);
        mBackView.setOnClickListener(mOnBackClickListener);
        mActionView = (ShTextView) findViewById(R.id.navigation_action);
        mActionView.setOnClickListener(mOnActionClickListener);

        updateUI(PasswordEntryFragment.TAG, null);
    }

    View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            PasswordEntryFragment fragment = (PasswordEntryFragment)
//                    getFragmentManager().findFragmentByTag(PasswordEntryFragment.TAG);
//            if (fragment != null && fragment.isVisible()) {
                finish();
//            } else {
//                isBack = true;
//                getFragmentManager().popBackStack();
//                mFragmentStack.pop();
//                updateUI(mFragmentStack.peek(), null);
//            }
        }
    };

    View.OnClickListener mOnActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // continue
            } else {
                InternetOfflineDialogFragment
                        .newInstance()
                        .show(getFragmentManager(), null);
                return;
            }
            switch (mFragmentTag) {
                case PasswordEntryFragment.TAG:
                    validatePhoneNumber();
                    break;
                case PasswordVerificationFragment.TAG:
                    sendCode();
                    break;
                case PasswordResetFragment.TAG:
                    updatePassword();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        mBackView.performClick();
    }

    void updateUI(String tag, Object param) {
        Fragment fragment;
        switch (tag) {
            case PasswordEntryFragment.TAG:
                fragment = PasswordEntryFragment.newInstance();
                updateNavigationBar(R.string.forgot_enter_phone_number_navigation_title, R.string.forgot_navigation_send, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case PasswordVerificationFragment.TAG:
                fragment = PasswordVerificationFragment.newInstance((String) param);
                updateNavigationBar(R.string.forgot_verification_navigation_title, R.string.forgot_navigation_next, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            case PasswordResetFragment.TAG:
                fragment = PasswordResetFragment.newInstance(mPhoneNumber, (String) param);
                updateNavigationBar(R.string.forgot_new_password_navigation_title, R.string.forgot_navigation_save, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
                break;
            default:
                fragment = PasswordEntryFragment.newInstance();
                updateNavigationBar(R.string.forgot_enter_phone_number_navigation_title, R.string.forgot_navigation_send, false, View.VISIBLE);
                if (!isBack) updateFragment(fragment, tag);
        }
        isBack = false;
    }

    void updateNavigationBar(int titleId, int actionId, boolean enabled, int visibility) {
        mTitleView.setText(titleId);
        mActionView.setText(actionId);
        mActionView.setEnabled(enabled);
        mActionView.setVisibility(visibility);
    }

    void updateFragment(Fragment fragment, String tag) {
        mFragmentStack.push(tag);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void validatePhoneNumber() {
        PasswordEntryFragment fragment = (PasswordEntryFragment)
                getFragmentManager().findFragmentByTag(PasswordEntryFragment.TAG);

        if (fragment != null) {
            fragment.validatePhoneNumber();
        } else {
            fragment = new PasswordEntryFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }

    public void sendCode() {
        PasswordVerificationFragment fragment = (PasswordVerificationFragment)
                getFragmentManager().findFragmentByTag(PasswordVerificationFragment.TAG);

        if (fragment != null) {
            fragment.sendCode();
        } else {
            fragment = new PasswordVerificationFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }

    public void updatePassword() {
        PasswordResetFragment fragment = (PasswordResetFragment)
                getFragmentManager().findFragmentByTag(PasswordResetFragment.TAG);

        if (fragment != null) {
            fragment.updatePassword();
        } else {
            fragment = new PasswordResetFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }

    @Override
    public void onActionButtonEnabled(String tag, boolean enabled) {
        mFragmentTag = tag;
        mActionView.setEnabled(enabled);
    }

    @Override
    public void onPasswordUpdated(boolean updated) {
        if (updated) finish();
    }

    @Override
    public void onCodeAccepted(boolean accepted, String pin) {
        if (accepted) {
            updateUI(PasswordResetFragment.TAG, pin);
        }
    }

    @Override
    public void onPhoneValidated(String phoneNumber) {
        updateUI(PasswordVerificationFragment.TAG, mPhoneNumber = phoneNumber);
    }
}
