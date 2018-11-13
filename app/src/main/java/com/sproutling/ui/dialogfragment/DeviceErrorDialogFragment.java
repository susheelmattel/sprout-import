/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by subram13 on 3/22/17.
 */

public class DeviceErrorDialogFragment extends DialogFragment {

    public static DeviceErrorDialogFragment newInstance() {
        return new DeviceErrorDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(getString(R.string.cant_set_up));
        shAlertView.setMessage(getString(R.string.device_create_err_msg));
        shAlertView.setButtonText(getString(R.string.contact_support));
        shAlertView.setImgAlert(R.drawable.ic_error);

        shAlertView.setOnClickListener(mOnClickListener);
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            App.getInstance().openHelp(getActivity());
        }
    };
}
