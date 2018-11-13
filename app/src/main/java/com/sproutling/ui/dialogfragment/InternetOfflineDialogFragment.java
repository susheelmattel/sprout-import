/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by bradylin on 12/1/16.
 */

public class InternetOfflineDialogFragment extends DialogFragment {

    public static InternetOfflineDialogFragment newInstance() {
        return new InternetOfflineDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.InternetOfflinePopup);
        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(getString(R.string.network_not_supported));
        shAlertView.setMessage(getString(R.string.setup_dialog_internet_offline_message));
        shAlertView.setButtonText(getString(R.string.got_it));
        shAlertView.setImgAlert(R.drawable.ic_internet_offline);
        shAlertView.setOnClickListener(mOnClickListener);
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
