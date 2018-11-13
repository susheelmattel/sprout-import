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

public class CaregiverRemoveDialogFragment extends DialogFragment {

    public static final String EXTRA_CAREGIVER = "caregiver";
    public static final String EXTRA_CHILD = "child";

    private String mCaregiver;
    private String mChild;

    public static CaregiverRemoveDialogFragment newInstance(String caregiver, String child) {
        CaregiverRemoveDialogFragment fragment = new CaregiverRemoveDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CAREGIVER, caregiver);
        arguments.putString(EXTRA_CHILD, child);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCaregiver = arguments.getString(EXTRA_CAREGIVER);
            mChild = arguments.getString(EXTRA_CHILD);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.RemoveCaregiverPopup);
        dialog.setContentView(R.layout.dialogfragment_caregiver_remove);
        dialog.setCanceledOnTouchOutside(false);
        ShTextView messageView = (ShTextView) dialog.findViewById(R.id.message);
        dialog.findViewById(R.id.ok).setOnClickListener(mOnClickListener);
        dialog.findViewById(R.id.cancel).setOnClickListener(mOnClickListener);

        messageView.setText(
                String.format(
                        getResources().getString(R.string.caregiver_remove_popup_message), mCaregiver, mChild)
        );

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
