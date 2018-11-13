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

public class FutureBirthdayDialogFragment extends DialogFragment {

    OnFutureBirthdayListener mListener;

    public static FutureBirthdayDialogFragment newInstance() {
        return new FutureBirthdayDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.FutureBirthdayPopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setImgAlert(R.drawable.ic_error);
        shAlertView.setTitle(getString(R.string.timeline_dialog_future_birthday_title));
        shAlertView.setMessage(getString(R.string.timeline_dialog_future_birthday_message));
        shAlertView.setButtonText(getString(R.string.got_it));
        shAlertView.setOnClickListener(mOnClickListener);

        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener = (OnFutureBirthdayListener) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onFutureBirthdayAcknowledged();
            dismiss();
        }
    };

    public interface OnFutureBirthdayListener {
        void onFutureBirthdayAcknowledged();
    }
}
