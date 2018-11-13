/*
 * Copyright (c) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.sproutling.R;
import com.sproutling.ui.widget.ShAlertView;

/**
 * Created by brady.lin on 5/23/17.
 */

public class GenericErrorDialogFragment extends DialogFragment {

    private int mTitleId;
    private int mMessageId;
    private int mButtonTextId;

    public static GenericErrorDialogFragment newInstance() {
        return new GenericErrorDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.DialogFragmentPopup);

        ShAlertView shAlertView = new ShAlertView(getActivity());
        shAlertView.setTitle(getString(mTitleId == 0 ? R.string.server_error_message_title : mTitleId));
        shAlertView.setMessage(getString(mMessageId == 0 ? R.string.server_error_message_body : mMessageId));
        shAlertView.setButtonText(getString(mButtonTextId == 0 ? R.string.server_error_message_button : mButtonTextId));
        shAlertView.setImgAlert(R.drawable.ic_error);

        shAlertView.setOnClickListener(mOnClickListener);
        dialog.setContentView(shAlertView);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public GenericErrorDialogFragment setTitle(int id) {
        mTitleId = id;
        return this;
    }

    public GenericErrorDialogFragment setMessage(int id) {
        mMessageId = id;
        return this;
    }

    public GenericErrorDialogFragment setButtonText(int id) {
        mButtonTextId = id;
        return this;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
