/*
 * Copyright (c) 2017 Fuhu, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by brady.lin on 5/23/17.
 */

public class EmailRegisteredDialogFragment extends DialogFragment {

    public static EmailRegisteredDialogFragment newInstance() {
        return new EmailRegisteredDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(getString(R.string.setup_account_creation_error_email_title));
        shAlertView.setMessage(getString(R.string.setup_account_creation_error_email_body));
        shAlertView.setButtonText(getString(R.string.setup_account_creation_error_email_button));
        shAlertView.setImgAlert(R.drawable.ic_error);

        shAlertView.setOnClickListener(mOnClickListener);
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
            dismiss();
        }
    };
}
