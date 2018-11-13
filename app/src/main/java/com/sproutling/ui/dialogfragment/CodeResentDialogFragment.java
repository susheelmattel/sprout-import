/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;

/**
 * Created by bradylin on 2/17/17.
 */

public class CodeResentDialogFragment extends DialogFragment {

    public static CodeResentDialogFragment newInstance() {
        CodeResentDialogFragment fragment = new CodeResentDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.CodeResentPopup);
        dialog.setContentView(R.layout.dialogfragment_verification_code_resent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.ok).setOnClickListener(mOnOkClickListener);

        return dialog;
    }

    View.OnClickListener mOnOkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
