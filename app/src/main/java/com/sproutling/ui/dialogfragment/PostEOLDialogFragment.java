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

public class PostEOLDialogFragment extends DialogFragment {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_BUTTON_TEXT = "button_text";

    OnLearnMoreListener mListener;

    private String mUrl;
    private String mTitle;
    private String mMessage;
    private String mButtonTxt;

    public static PostEOLDialogFragment newInstance(String url, String title, String message, String buttonTxt) {
        PostEOLDialogFragment fragment = new PostEOLDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_URL, url);
        arguments.putString(EXTRA_TITLE, title);
        arguments.putString(EXTRA_MESSAGE, message);
        arguments.putString(EXTRA_BUTTON_TEXT, buttonTxt);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUrl = arguments.getString(EXTRA_URL);
            mTitle = arguments.getString(EXTRA_TITLE);
            mMessage = arguments.getString(EXTRA_MESSAGE);
            mButtonTxt = arguments.getString(EXTRA_BUTTON_TEXT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SoftwareUpdatePopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setImgAlert(R.drawable.ic_error);
        shAlertView.setTitle(mTitle);
        shAlertView.setMessage(mMessage);
        shAlertView.setButtonText(mButtonTxt);
        shAlertView.setButtonClickListener(mOnClickListener);

        shAlertView.setButton2Visibility(false);
        shAlertView.setButton3Visibility(false);


        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener = (OnLearnMoreListener) getActivity();
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
        }
    };

    public interface OnLearnMoreListener {
        void onUpdateClicked(String url);
    }
}
