/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;

/**
 * Created by bradylin on 3/16/17.
 */

public class RemoveDeviceDialogFragment extends DialogFragment {

    public static RemoveDeviceDialogFragment newInstance() {
        RemoveDeviceDialogFragment fragment = new RemoveDeviceDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.RemoveDevicePopup);
        dialog.setContentView(R.layout.dialogfragment_settings_remove_device);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.yes).setOnClickListener(mOnClickListener);
        dialog.findViewById(R.id.cancel).setOnClickListener(mOnClickListener);

        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.yes:
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    };
}

