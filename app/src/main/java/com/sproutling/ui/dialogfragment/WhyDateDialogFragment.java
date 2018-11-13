/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;

/**
 * Created by bradylin on 12/1/16.
 */

public class WhyDateDialogFragment extends DialogFragment {

    public static WhyDateDialogFragment newInstance() {
        return new WhyDateDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.WhyDatePopup);
        dialog.setContentView(R.layout.dialogfragment_setup_add_child_why);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.ok).setOnClickListener(mOnClickListener);

        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
