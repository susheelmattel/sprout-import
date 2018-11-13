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
import com.sproutling.ui.widget.ShTextView;

/**
 * Created by bradylin on 1/26/17.
 */

public class CaregiverAcceptInvitationDialogFragment extends DialogFragment {

    public static final String EXTRA_GUARDIAN = "guardian";

    private String mGuardian;

    public static CaregiverAcceptInvitationDialogFragment newInstance(String guardian) {
        CaregiverAcceptInvitationDialogFragment fragment = new CaregiverAcceptInvitationDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_GUARDIAN, guardian);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mGuardian = arguments.getString(EXTRA_GUARDIAN);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.AcceptInvitationPopup);
        dialog.setContentView(R.layout.dialogfragment_caregiver_accept_invitation);
        dialog.setCanceledOnTouchOutside(false);
        ShTextView titleView = (ShTextView) dialog.findViewById(R.id.title);
        ShTextView messageView = (ShTextView) dialog.findViewById(R.id.message);
        dialog.findViewById(R.id.ok).setOnClickListener(mOnOkClickListener);
        dialog.findViewById(R.id.cancel).setOnClickListener(mOnCancelClickListener);

        titleView.setText(
                String.format(
                        getResources().getString(R.string.caregiver_accept_invitation_popup_title), mGuardian)
        );

        messageView.setText(
                String.format(
                        getResources().getString(R.string.caregiver_accept_invitation_popup_message), mGuardian)
        );

        return dialog;
    }

    View.OnClickListener mOnOkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
            dismiss();
        }
    };

    View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
            dismiss();
        }
    };
}
