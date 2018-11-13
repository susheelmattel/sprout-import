/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.dialogfragment;

import android.os.Bundle;
import android.view.View;

import com.sproutling.common.R;

import org.jetbrains.annotations.NotNull;


/**
 * Created by bradylin on 3/21/17.
 */

public class SoftwareUpdateDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_URL = "url";

    OnSoftwareUpdateListener mListener;

    private String mUrl;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onUpdateClicked(mUrl);
            dismiss();
        }
    };

    public static SoftwareUpdateDialogFragment newInstance(String url) {
        SoftwareUpdateDialogFragment fragment = new SoftwareUpdateDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_URL, url);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUrl = arguments.getString(EXTRA_URL);
        }
        setOnClickListener(mOnClickListener);
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);
//
//        ShAlertView shAlertView = new ShAlertView(getActivity());
//        shAlertView.setImgAlert(R.drawable.software_update);
//        shAlertView.setToolBarTitle(getString(R.string.dialog_update_title));
//        shAlertView.setMessage(getString(R.string.dialog_update_message));
//        shAlertView.setButtonText(getString(R.string.dialog_update_ok));
//        shAlertView.setButtonClickListener(mOnClickListener);
//
//        dialog.setContentView(shAlertView);
//        dialog.setCanceledOnTouchOutside(false);
//        setCancelable(false);
//
//        return dialog;
//    }

    @Override
    public void onStart() {
        super.onStart();
        mListener = (OnSoftwareUpdateListener) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    @Override
    public int getImage() {
        return R.drawable.software_update;
    }

    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.dialog_update_title);
    }

    @NotNull
    @Override
    public String getMessage() {
        return getString(R.string.dialog_update_message);
    }

    @NotNull
    @Override
    public String getButtonText() {
        return getString(R.string.dialog_update_ok);
    }

    @NotNull
    @Override
    public String getButton2Text() {
        return getString(R.string.empty);
    }

    @Override
    public boolean getButton2Visibility() {
        return false;
    }

    public interface OnSoftwareUpdateListener {
        void onUpdateClicked(String url);
    }
}
