/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by bradylin on 3/21/17.
 */

public class SoftwareUpdateDialogFragment extends DialogFragment {

    public static final String EXTRA_URL = "url";

    OnSoftwareUpdateListener mListener;

    private String mUrl;

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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SoftwareUpdatePopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setImgAlert(R.mipmap.software_update);
        shAlertView.setTitle(getString(R.string.dialog_update_title));
        shAlertView.setMessage(getString(R.string.dialog_update_message));
        shAlertView.setButtonText(getString(R.string.dialog_update_ok));
        shAlertView.setOnClickListener(mOnClickListener);

        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;
    }

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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onUpdateClicked(mUrl);
            dismiss();
        }
    };

    public interface OnSoftwareUpdateListener {
        void onUpdateClicked(String url);
    }
}
