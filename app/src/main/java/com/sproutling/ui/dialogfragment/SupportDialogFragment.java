/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by bradylin on 3/21/17.
 */

public class SupportDialogFragment extends DialogFragment {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_BUTTON_TEXT = "button_text";
    public static final String EXTRA_BUTTON2_TEXT = "button2_text";
    public static final String EXTRA_BUTTON3_TEXT = "button3_text";
    public static final String EXTRA_EMAIL = "email";

    OnSupportDialogClickListener mListener;

    private String mUrl;
    private String mTitle;
    private String mMessage;
    private String mButtonTxt;
    private String mButton2Txt;
    private String mButton3Txt;
    private String mEmail;

    public static SupportDialogFragment newInstance(String url, String title, String message, String buttonTxt, String button2Txt, String button3Txt, String email) {
        SupportDialogFragment fragment = new SupportDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_URL, url);
        arguments.putString(EXTRA_TITLE, title);
        arguments.putString(EXTRA_MESSAGE, message);
        arguments.putString(EXTRA_BUTTON_TEXT, buttonTxt);
        arguments.putString(EXTRA_BUTTON2_TEXT, button2Txt);
        arguments.putString(EXTRA_BUTTON3_TEXT, button3Txt);
        arguments.putString(EXTRA_EMAIL, email);
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
            mButton2Txt = arguments.getString(EXTRA_BUTTON2_TEXT);
            mButton3Txt = arguments.getString(EXTRA_BUTTON3_TEXT);
            mEmail = arguments.getString(EXTRA_EMAIL);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.SoftwareUpdatePopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setImgAlert(R.drawable.ic_envelope);
        shAlertView.setTitle(mTitle);
        shAlertView.setMessage(mMessage);
        shAlertView.setButtonText(mButtonTxt);
        shAlertView.setButton2Text(mButton2Txt);
        shAlertView.setButton3Text(mButton3Txt);

        shAlertView.setButton2Visibility(true);
        shAlertView.setButton3Visibility(true);
        shAlertView.setButtonClickListener(mButton1OnClickListener);
        shAlertView.setButton2ClickListener(mButton2OnClickListener);
        shAlertView.setButton3ClickListener(mButton3OnClickListener);

        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener = (OnSupportDialogClickListener) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    private View.OnClickListener mButton1OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openEmailSupport();
            dismiss();
        }
    };

    private View.OnClickListener mButton2OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onVisitWebsiteClicked(mUrl);
            dismiss();
        }
    };

    private View.OnClickListener mButton3OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private void openEmailSupport(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mEmail});
        startActivity(Intent.createChooser(intent, null));
    }

    public interface OnSupportDialogClickListener {
        void onVisitWebsiteClicked(String url);
    }
}
