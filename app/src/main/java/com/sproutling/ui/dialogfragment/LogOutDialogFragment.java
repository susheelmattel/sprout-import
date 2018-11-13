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
 * Created by bradylin on 3/23/17.
 */

public class LogOutDialogFragment extends DialogFragment {

    public static LogOutDialogFragment newInstance() {
        return new LogOutDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.LogOutPopup);
        dialog.setContentView(R.layout.dialogfragment_log_out);
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.ok).setOnClickListener(mOnClickListener);
        dialog.findViewById(R.id.cancel).setOnClickListener(mOnClickListener);

        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ok:
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                case R.id.cancel:
                    dismiss();
            }
        }
    };
}
